package org.fiteagle.north.sfa.am.status;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import info.openmultinet.ontology.translators.geni.ManifestConverter;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.ReservationStateEnum;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.fiteagle.north.sfa.util.URN;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dne on 11.02.15.
 */
public class StatusProcessor {
    private final static Logger LOGGER = Logger.getLogger(StatusProcessor.class.getName());
    public Model getStates(List<URN> urns) throws UnsupportedEncodingException {
        LOGGER.log(Level.INFO, "create status model ");
        Model requestModel = ModelFactory.createDefaultModel();
        for (URN urn : urns) {
            if(urn.getType().equals("sliver")){
                Individual resource = Omn.Resource.createIndividual(URLDecoder.decode(urn.getSubject(), "UTF-8"));
                requestModel.add(resource.listProperties());
            }else {
                Individual topology = Omn.Topology.createIndividual(Omn.Topology.getURI()+"/"+urn.getSubject());
                requestModel.add(topology.listProperties());

            }


        }

        String serializedModel = MessageUtil.serializeModel(requestModel,
                IMessageBus.SERIALIZATION_TURTLE);
        LOGGER.log(Level.INFO, "send provision request ...");
        Model statusResponse = SFA_AM_MDBSender.getInstance()
                .sendRDFRequest(serializedModel, IMessageBus.TYPE_GET,
                        IMessageBus.TARGET_ORCHESTRATOR);
        LOGGER.log(Level.INFO,
                "status reply received.");
        return statusResponse;
    }

    public void addStatusValue(HashMap<String, Object> result, Model statusResponse) {
        final Map<String, Object> value = new HashMap<>();




        final List<Map<String, Object>> geniSlivers = new LinkedList<>();

        StmtIterator stmtIterator = statusResponse.listStatements(null, RDF.type, Omn.Reservation);
        if(!stmtIterator.hasNext()){
            throw new SearchFailedException("Resource not found");
        }
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();
            Resource reservation = statement.getSubject();

            final Map<String, Object> sliverMap = new HashMap<>();
            Resource resource = statusResponse.getResource(reservation.getProperty(Omn.isReservationOf).getObject().asResource().getURI());
            sliverMap.put(IGeni.GENI_SLIVER_URN, ManifestConverter.generateSliverID("localhost",resource.getURI()));
            sliverMap.put(IGeni.GENI_EXPIRES, reservation.getProperty(MessageBusOntologyModel.endTime).getLiteral().getString());
            sliverMap.put(IGeni.GENI_ALLOCATION_STATUS, ReservationStateEnum.valueOf(reservation.getProperty(Omn_lifecycle.hasReservationState).getResource().getLocalName()).getGeniState());
            sliverMap.put(IGeni.GENI_OPERATIONAL_STATUS, getGENI_OperationalState(resource.getProperty(Omn_lifecycle.hasState).getObject()));
            sliverMap.put(IGeni.GENI_ERROR, "NO ERROR");

            geniSlivers.add(sliverMap);
        }
        value.put(IGeni.GENI_SLIVERS, geniSlivers);

        value.put(IGeni.GENI_URN, getSliceURN(statusResponse));
        result.put(ISFA_AM.VALUE, value);
    }

    private String getSliceURN(Model statusResponse) {
        StmtIterator stmtIterator = statusResponse.listStatements(new SimpleSelector(null,Omn.hasResource, (Object)null));
        Resource topology = stmtIterator.nextStatement().getSubject();
        String localname = topology.getLocalName();
        URN urn =  new URN("urn:publicid:IDN+localhost+slice+"+localname);
        return urn.toString();

    }

    private String getGENI_OperationalState(RDFNode object) {
        switch (object.asResource().getLocalName()){
            case "Ready":
                LOGGER.log(Level.INFO, "ready");
                return "geni_ready";
            default:
                return "";
        }
    }
}
