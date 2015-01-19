package org.fiteagle.north.sfa.provision;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
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
  public static Model provisionInstances(Map<String, Object> provisionParameters) {
    Model requestModel = ModelFactory.createDefaultModel();
    
    for (final String urn : (List<String>) provisionParameters.get(ISFA_AM.URN)) {
      Resource reservation = requestModel.createResource(urn);
      URN checkURN = new URN(urn);
      if(checkURN.getType().equals(ISFA_AM.SLICE)){
    	  reservation.addProperty(RDF.type, MessageBusOntologyModel.classGroup);
      }
      if(checkURN.getType().equals(ISFA_AM.Sliver)){
    	  reservation.addProperty(RDF.type, MessageBusOntologyModel.classReservation);
      }
    }
    
    String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
    LOGGER.log(Level.INFO, "send provision request ...");
    Model provisionResponse = SFA_AM_MDBSender.getInstance().sendRDFRequest(serializedModel, IMessageBus.TYPE_CONFIGURE, IMessageBus.TARGET_ORCHESTRATOR);
    LOGGER.log(Level.INFO, "provision reply received." + provisionResponse.getGraph());
    return provisionResponse;
  }
  
  public static void addProvisionValue(final HashMap<String, Object> result, Model provisionResponse){
	  
	  final Map<String, Object> value = new HashMap<>();
	  value.put(IGeni.GENI_RSPEC,"should be geni_manifest");
	  final List<Map<String, Object>> geniSlivers = new LinkedList<>();
	  
	  StmtIterator stmtIterator = provisionResponse.listStatements(null, RDF.type, MessageBusOntologyModel.classReservation);
	    while (stmtIterator.hasNext()) {
	      Statement statement = stmtIterator.next();
	      Resource reservation = statement.getSubject();
	      
	      /**
	       * defines a loop depending on the slivers number.
	       * In the loop, Map is created for each sliver containing 
	       * sliver urn, experires, allocateion_status and operation_status.
	       * The created maps should be added to geniSlivers list.
	       */ 
	      final Map<String, Object> sliverMap = new HashMap<>();
	      sliverMap.put(IGeni.GENI_SLIVER_URN, reservation.getURI());
	      if(reservation.hasProperty(MessageBusOntologyModel.endTime)){
	    	  sliverMap.put(IGeni.GENI_EXPIRES, reservation.getProperty(MessageBusOntologyModel.endTime).getLiteral().getString());
	      } else {
	    	  sliverMap.put(IGeni.GENI_EXPIRES, "");
	      }
	      sliverMap.put(IGeni.GENI_ALLOCATION_STATUS, reservation.getProperty(MessageBusOntologyModel.hasState).getLiteral().getString());
	      sliverMap.put(IGeni.GENI_OPERATIONAL_STATUS, "");
	      sliverMap.put(IGeni.GENI_ERROR, "NO ERROR");
	      
	      geniSlivers.add(sliverMap);
	    }
	    value.put(IGeni.GENI_SLIVERS, geniSlivers);
	    result.put(ISFA_AM.VALUE, value);
  }
  
}
