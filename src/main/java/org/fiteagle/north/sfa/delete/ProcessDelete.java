package org.fiteagle.north.sfa.delete;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;


public class ProcessDelete {
	
	private final static Logger LOGGER = Logger.getLogger(ProcessDelete.class.getName());
	
	public static Model deleteInstances(List<URN> urns) {
		
		LOGGER.log(Level.INFO, "create delete model ");
		Model requestModel = ModelFactory.createDefaultModel();
		for (URN urn : urns) {
			Resource reservation = requestModel.createResource(urn.toString());

			if (ISFA_AM.SLICE.equals(urn.getType())) {
				reservation.addProperty(RDF.type, MessageBusOntologyModel.classGroup);
			} else if (ISFA_AM.Sliver.equals(urn.getType())) {
				reservation.addProperty(RDF.type, MessageBusOntologyModel.classReservation);
					}
			}

		String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
		LOGGER.log(Level.INFO, "send delete request ...");
		Model deleteResponse = SFA_AM_MDBSender.getInstance().sendRDFRequest(serializedModel, IMessageBus.TYPE_DELETE,IMessageBus.TARGET_ORCHESTRATOR);
		LOGGER.log(Level.INFO,"delete reply is received.");
		return deleteResponse;
		
	}
	
	public static void addDeleteValue(final HashMap<String, Object> result, Model provisionResponse){
		
	}

}
