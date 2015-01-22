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
  
  
  public static Model provisionInstances(
			List<URN> urns) {

		LOGGER.log(Level.INFO, "create provision model ");
		Model requestModel = ModelFactory.createDefaultModel();
		for (URN urn : urns) {
			Resource reservation = requestModel.createResource(urn.toString());

			if (ISFA_AM.SLICE.equals(urn.getType())) {
				reservation.addProperty(RDF.type,
						MessageBusOntologyModel.classGroup);
			}else{
				if (ISFA_AM.Sliver.equals(urn.getType())) {
					reservation.addProperty(RDF.type,
							MessageBusOntologyModel.classReservation);
				}
			}

		}

		String serializedModel = MessageUtil.serializeModel(requestModel,
				IMessageBus.SERIALIZATION_TURTLE);
		LOGGER.log(Level.INFO, "send provision request ...");
		Model provisionResponse = SFA_AM_MDBSender.getInstance()
				.sendRDFRequest(serializedModel, IMessageBus.TYPE_CONFIGURE,
						IMessageBus.TARGET_ORCHESTRATOR);
		LOGGER.log(Level.INFO,
				"provision is reply received.");
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
