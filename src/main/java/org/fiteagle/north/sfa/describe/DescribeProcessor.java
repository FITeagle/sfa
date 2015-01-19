package org.fiteagle.north.sfa.describe;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
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

    private Map<String, String> getDescriptions(List<URN> urns){
        Model requestModel = ModelFactory.createDefaultModel();
        for(URN u : urns){
            Resource resource = requestModel.createResource(u.toString());
            if(u.getType().equals("slice")){
                resource.addProperty(RDF.type, MessageBusOntologyModel.classGroup);
            }
            if(u.getType().equals("sliver")){
                resource.addProperty(RDF.type, MessageBusOntologyModel.classReservation);
            }
        }
        String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
        LOGGER.log(Level.INFO, "send getValue request ...");
        Model resultModel = SFA_AM_MDBSender.getInstance().sendRDFRequest(serializedModel, IMessageBus.TYPE_GET, IMessageBus.TARGET_RESERVATION);
        LOGGER.log(Level.INFO, "getValue reply received.");
        StmtIterator iter = resultModel.listStatements();
        final Map<String, String> sliverMap = new HashMap<>();
        while (iter.hasNext()) {
            Statement st = iter.next();
            Resource r = st.getSubject();

            sliverMap.put(r.getURI().toString(), st.getObject().toString());

            LOGGER.log(Level.INFO, "created sliver " + r.getURI());
        }
        return sliverMap;
    }

    //TODO remove ugly RSPEC string!
    public HashMap<String, Object> getValue(Object credList, Object options, List<URN> urns) {
        HashMap<String, Object> value = new HashMap<>();
        Map<String, String> descriptions = getDescriptions(urns);
        List<HashMap<String,Object>> slivers = new ArrayList<>();
        value.put(ISFA_AM.GENI_RSPEC, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<rspec type=\"manifest\" xmlns=\"http://www.geni.net/resources/rspec/3\"/>");
        value.put(ISFA_AM.GENI_SLIVERS, slivers);
        value.put(ISFA_AM.GENI_URN, urns.get(0).toString());
        return value;
    }
}
