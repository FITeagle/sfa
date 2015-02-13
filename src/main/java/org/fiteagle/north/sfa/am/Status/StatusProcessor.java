package org.fiteagle.north.sfa.am.Status;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import info.openmultinet.ontology.vocabulary.Omn;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
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

            Individual resource = Omn.Resource.createIndividual(URLDecoder.decode(urn.getSubject(), "UTF-8"));
            requestModel.add(resource.listProperties());

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
}
