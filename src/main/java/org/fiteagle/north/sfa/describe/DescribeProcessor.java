package org.fiteagle.north.sfa.describe;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;

import javax.jms.Message;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dne on 12.01.15.
 */
public class DescribeProcessor {
    private final static Logger LOGGER = Logger.getLogger(DescribeProcessor.class.getName());

    public Model getDescription(List<URN> urns){
        Model requestModel = ModelFactory.createDefaultModel();
        for(URN u : urns){
            requestModel.createResource(u.toString());
        }
        String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
        LOGGER.log(Level.INFO, "send describe request ...");
        Model resultModel = SFA_AM_MDBSender.getInstance().sendRDFRequest(serializedModel, IMessageBus.TYPE_GET, IMessageBus.TARGET_RESERVATION);
        LOGGER.log(Level.INFO, "describe reply received.");
        return resultModel;
    }
}
