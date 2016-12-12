package org.fiteagle.north.sfa.am.delete;

import com.hp.hpl.jena.rdf.model.*;
import info.openmultinet.ontology.translators.geni.ManifestConverter;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.api.core.*;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.ReservationStateEnum;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.GENI_Credential;
import org.fiteagle.north.sfa.util.URN;

import com.hp.hpl.jena.vocabulary.RDF;


public class ProcessDelete extends AbstractMethodProcessor {
	
//private final static Logger LOGGER = Logger.getLogger(ProcessDelete.class.getName());
	
	public ProcessDelete(final List<?> parameter) {
    this.parameter = parameter;
  }
	
	public  Model deleteInstances() throws UnsupportedEncodingException {
		

		Model requestModel = ModelFactory.createDefaultModel();
		for (URN urn : this.urns) {


			if (ISFA_AM.SLICE.equals(urn.getType())) {
                Resource resource = requestModel.createResource("http://"+urn.getDomain()+"/topology/"+ urn.getSubject());
                resource.addProperty(RDF.type, Omn.Topology);
			} else if (ISFA_AM.Sliver.equals(urn.getType())) {
                Resource resource =  requestModel.createResource(URLDecoder.decode(urn.getSubject(), ISFA_AM.UTF_8));
                resource.addProperty(RDF.type, Omn.Resource);
            }
		}

		String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);

		Model deleteResponse = getSender().sendRDFRequest(serializedModel, IMessageBus.TYPE_DELETE,IMessageBus.TARGET_ORCHESTRATOR);

		return deleteResponse;
		
	}
	
	public void createResponse(final HashMap<String, Object> result, Model deleteResponse){
		
		  final List<Map<String, Object>> value = new LinkedList<>();
		  
		  ResIterator resIterator = deleteResponse.listResourcesWithProperty(Omn.hasReservation);
		    while (resIterator.hasNext()) {

		      Resource resource = resIterator.nextResource();
		      
		      /**
		       * defines a loop depending on the slivers number.
		       * In the loop, Map is created for each sliver containing 
		       * sliver urn, experires and allocateion_status.
		       * The created maps should be added to geniSlivers list.
		       */ 
		      final Map<String, Object> sliverMap = new HashMap<>();
				Config config =  new Config();
				Resource reservation  = resource.getProperty(Omn.hasReservation).getObject().asResource();
		      sliverMap.put(IGeni.GENI_SLIVER_URN,  ManifestConverter.generateSliverID(config.getProperty(IConfig.KEY_HOSTNAME), resource.getURI()));
		    	sliverMap.put(IGeni.GENI_EXPIRES, reservation.getProperty(MessageBusOntologyModel.endTime).getLiteral().getString());
		      sliverMap.put(IGeni.GENI_ALLOCATION_STATUS, ReservationStateEnum.valueOf(reservation.getProperty(Omn_lifecycle.hasReservationState).getResource().getLocalName()).getGeniState());
		      
		      value.add(sliverMap);
		    }
		    
		    result.put(ISFA_AM.VALUE, value);
        this.addCode(result);
        this.addOutput(result);
	}
	

}
