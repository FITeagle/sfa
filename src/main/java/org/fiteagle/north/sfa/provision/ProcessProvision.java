package org.fiteagle.north.sfa.provision;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ProcessProvision {
  
  private final static Logger LOGGER = Logger.getLogger(ProcessProvision.class.getName());
  
  @SuppressWarnings("unchecked")
  public static void parseProvsionParameter(final List<?> parameter, Map<String, Object> provisionParameters) {
    
    LOGGER.log(Level.INFO, " parsing provision parameter ");
    System.out.println(parameter.size());
    
    for (final Object param : parameter) {
      if (param instanceof Map<?, ?>) {
        LOGGER.log(Level.INFO, "parameter is a map");
        final Map<String, ?> param2 = (Map<String, ?>) param;
        if (!param2.isEmpty()) {
          for (Map.Entry<String, ?> parameters : param2.entrySet()) {
            provisionParameters.put(parameters.getKey(), parameters.getValue().toString());
          }
        }
      }
      if (param instanceof List<?>) {
        final List<String> param2 = (List<String>) param;
        if (!param2.isEmpty()) {
          provisionParameters.put(ISFA_AM.URN, param2);
          for (String parametersString : (List<String>) provisionParameters.get(ISFA_AM.URN)) {
            System.out.println("provision urns are " + parametersString);
          }
        }
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  public static void provisionInstances(Map<String, Object> provisionParameters) {
    Model requestModel = ModelFactory.createDefaultModel();
    
    for (final String urn : (List<String>) provisionParameters.get(ISFA_AM.URN)) {
      Resource slice = requestModel.createResource(urn);
      slice.addProperty(RDF.type, ISFA_AM.OMN + ISFA_AM.SLICE);
    }
    
    String serializedModel = MessageUtil.serializeModel(requestModel);
    LOGGER.log(Level.INFO, "send provision request ...");
    Model resultModel = SFA_AM_MDBSender.getInstance().sendRDFRequest(serializedModel, IMessageBus.TYPE_CONFIGURE, IMessageBus.TARGET_ORCHESTRATOR);
    LOGGER.log(Level.INFO, "provision reply received.");
    
  }
  
}
