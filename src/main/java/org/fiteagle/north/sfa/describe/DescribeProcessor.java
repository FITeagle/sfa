package org.fiteagle.north.sfa.describe;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;

import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;

import javax.jms.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dne on 12.01.15.
 */
public class DescribeProcessor {
    private final static Logger LOGGER = Logger.getLogger(DescribeProcessor.class.getName());

    private Model getDescriptions(List<URN> urns){
        Model requestModel = ModelFactory.createDefaultModel();
        for(URN u : urns){
            Resource resource = requestModel.createResource(u.toString());
            if(ISFA_AM.SLICE.equals(u.getType())){
                resource.addProperty(RDF.type, MessageBusOntologyModel.classGroup);
            }
            if(ISFA_AM.Sliver.equals(u.getType())){
                resource.addProperty(RDF.type, MessageBusOntologyModel.classReservation);
            }
        }
        String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
        LOGGER.log(Level.INFO, "send getValue request ...");
        Model resultModel = SFA_AM_MDBSender.getInstance().sendRDFRequest(serializedModel, IMessageBus.TYPE_GET, IMessageBus.TARGET_RESERVATION);

        return resultModel;
    }

    //TODO remove ugly RSPEC string!
    public HashMap<String, Object> getValue(Object credList, Object options, List<URN> urns) {
        HashMap<String, Object> value = new HashMap<>();
        Model descriptions = getDescriptions(urns);
        List<HashMap<String,Object>> slivers = new ArrayList<>();
        value.put(IGeni.GENI_RSPEC, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<rspec type=\"manifest\" xmlns=\"http://www.geni.net/resources/rspec/3\"/>");
        value.put(IGeni.GENI_SLIVERS, slivers);
        // value.put(IGeni.GENI_RSPEC, OMN2Manifest.getRSpec(descriptions));
        value.put(IGeni.GENI_URN, urns.get(0).toString());
        return value;
    }
}
