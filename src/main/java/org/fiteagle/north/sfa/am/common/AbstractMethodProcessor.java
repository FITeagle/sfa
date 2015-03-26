package org.fiteagle.north.sfa.am.common;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.translators.geni.ManifestConverter;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.north.sfa.ISFA;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.ISFA_AM_Delegate;
import org.fiteagle.north.sfa.am.ReservationStateEnum;
import org.fiteagle.north.sfa.am.dm.SFA_AM_Delegate_Default;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.fiteagle.north.sfa.util.URN;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dne on 16.03.15.
 */
public abstract class AbstractMethodProcessor {
    private final static Logger LOGGER = Logger.getLogger(AbstractMethodProcessor.class.getName());
    
    private SFA_AM_MDBSender sender;
    
    protected ISFA_AM_Delegate delegate = new SFA_AM_Delegate_Default();;

    public void addSliverInformation(Map<String, Object> value, Model response){

        final List<Map<String, Object>> geniSlivers = new LinkedList<>();

        StmtIterator stmtIterator = response.listStatements(null, RDF.type, Omn.Reservation);
        if(!stmtIterator.hasNext()){
            throw new SearchFailedException("Resource not found");
        }
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();
            Resource reservation = statement.getSubject();

            final Map<String, Object> sliverMap = new HashMap<>();
            Resource resource = response.getResource(reservation.getProperty(Omn.isReservationOf).getObject().asResource().getURI());
            sliverMap.put(IGeni.GENI_SLIVER_URN, ManifestConverter.generateSliverID(ISFA_AM.LOCALHOST, resource.getURI()));
            sliverMap.put(IGeni.GENI_EXPIRES, reservation.getProperty(MessageBusOntologyModel.endTime).getLiteral().getString());
            sliverMap.put(IGeni.GENI_ALLOCATION_STATUS, ReservationStateEnum.valueOf(reservation.getProperty(Omn_lifecycle.hasReservationState).getResource().getLocalName()).getGeniState());
            sliverMap.put(IGeni.GENI_OPERATIONAL_STATUS, getGENI_OperationalState(resource.getProperty(Omn_lifecycle.hasState).getObject()));
            sliverMap.put(IGeni.GENI_ERROR, ISFA_AM.NO_ERROR);

            geniSlivers.add(sliverMap);
        }
        value.put(IGeni.GENI_SLIVERS, geniSlivers);

        value.put(IGeni.GENI_URN, getSliceURN(response));

    }

    private String getSliceURN(Model statusResponse) {
        StmtIterator stmtIterator = statusResponse.listStatements(new SimpleSelector(null,Omn.hasResource, (Object)null));
        Resource topology = stmtIterator.nextStatement().getSubject();

        String uri = topology.getURI();
        String localname =  uri.substring( uri.lastIndexOf('/') + 1 );
        URN urn =  new URN("urn:publicid:IDN+localhost+slice+"+localname);
        return urn.toString();

    }

    private String getGENI_OperationalState(RDFNode object) {
        switch (object.asResource().getLocalName()){
            case ISFA_AM.READY:
                LOGGER.log(Level.INFO, ISFA_AM.READY);
                return IGeni.GENI_READY;
            case ISFA_AM.UNCOMPLETED:
                return IGeni.GENI_PENDING_ALLOCATION;
            default:
                return "";
        }
    }
    

    public SFA_AM_MDBSender getSender() {
        return sender;
    }

    public void setSender(SFA_AM_MDBSender sender) {
        this.sender = sender;
    }
    
}
