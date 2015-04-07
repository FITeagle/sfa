package org.fiteagle.north.sfa.am.status;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.*;

import info.openmultinet.ontology.vocabulary.Omn;

import org.fiteagle.api.core.*;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.GENI_Credential;
import org.fiteagle.north.sfa.util.URN;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dne on 11.02.15.
 */
public class StatusProcessor extends AbstractMethodProcessor {
  
  
  public StatusProcessor(final List<?> parameter) {
    this.parameter = parameter;
  }
  
    private final static Logger LOGGER = Logger.getLogger(StatusProcessor.class.getName());
    
    public Model getStates() throws UnsupportedEncodingException {
        LOGGER.log(Level.INFO, "create status model ");
        Model requestModel = ModelFactory.createDefaultModel();
        for (URN urn : this.urns) {
            if(ISFA_AM.Sliver.equals(urn.getType())){
                Individual resource = Omn.Resource.createIndividual(URLDecoder.decode(urn.getSubject(), ISFA_AM.UTF_8));
                requestModel.add(resource.listProperties());
            }else {
                Individual topology = Omn.Topology.createIndividual(IConfig.TOPOLOGY_NAMESPACE_VALUE+urn.getSubject());
                requestModel.add(topology.listProperties());
            }
        }

        String serializedModel = MessageUtil.serializeModel(requestModel,
                IMessageBus.SERIALIZATION_TURTLE);
        LOGGER.log(Level.INFO, "send status request ...");
        Model statusResponse = getSender().sendRDFRequest(serializedModel, IMessageBus.TYPE_GET,IMessageBus.TARGET_ORCHESTRATOR);
        LOGGER.log(Level.INFO,"status reply received.");
        return statusResponse;
    }

    public void createResponse(final HashMap<String, Object> result, Model statusResponse) {
      HashMap<String, Object> value = new HashMap<>();
      addSliverInformation(value, statusResponse);
      result.put(ISFA_AM.VALUE, value);
      this.addCode(result);
      this.addOutput(result);
    }
    
}
