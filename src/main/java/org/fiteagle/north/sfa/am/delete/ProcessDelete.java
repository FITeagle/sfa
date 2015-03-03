package org.fiteagle.north.sfa.am.delete;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

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
import org.fiteagle.north.sfa.am.ReservationStateEnum;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;


public class ProcessDelete {
	
	private final static Logger LOGGER = Logger.getLogger(ProcessDelete.class.getName());
	
	public static Model deleteInstances(List<URN> urns) {
		
		LOGGER.log(Level.INFO, "create delete model ");
		Model requestModel = ModelFactory.createDefaultModel();
		for (URN urn : urns) {
			Resource reservation = requestModel.createResource(urn.toString());

			if (ISFA_AM.SLICE.equals(urn.getType())) {
				reservation.addProperty(RDF.type, Omn.Group);
			} else if (ISFA_AM.Sliver.equals(urn.getType())) {
				reservation.addProperty(RDF.type, Omn.Reservation);
					}
			}

		String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
		LOGGER.log(Level.INFO, "send delete request ...");
		Model deleteResponse = SFA_AM_MDBSender.getInstance().sendRDFRequest(serializedModel, IMessageBus.TYPE_DELETE,IMessageBus.TARGET_ORCHESTRATOR);
		LOGGER.log(Level.INFO,"delete reply is received.");
		return deleteResponse;
		
	}
	
	public static void addDeleteValue(final HashMap<String, Object> result, Model deleteResponse){
		
		  final List<Map<String, Object>> value = new LinkedList<>();
		  
		  StmtIterator stmtIterator = deleteResponse.listStatements(null, RDF.type, Omn.Reservation);
		    while (stmtIterator.hasNext()) {
		      Statement statement = stmtIterator.next();
		      Resource reservation = statement.getSubject();
		      
		      /**
		       * defines a loop depending on the slivers number.
		       * In the loop, Map is created for each sliver containing 
		       * sliver urn, experires and allocateion_status.
		       * The created maps should be added to geniSlivers list.
		       */ 
		      final Map<String, Object> sliverMap = new HashMap<>();
		      sliverMap.put(IGeni.GENI_SLIVER_URN, reservation.getURI());
		    	sliverMap.put(IGeni.GENI_EXPIRES, reservation.getProperty(MessageBusOntologyModel.endTime).getLiteral().getString());
		      sliverMap.put(IGeni.GENI_ALLOCATION_STATUS, ReservationStateEnum.valueOf(reservation.getProperty(Omn_lifecycle.hasReservationState).getResource().getLocalName()));
		      
		      value.add(sliverMap);
		    }
		    
		    result.put(ISFA_AM.VALUE, value);
	}

}
