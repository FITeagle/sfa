package org.fiteagle.north.sfa.am.provision;

import com.hp.hpl.jena.ontology.Individual;
import info.openmultinet.ontology.exceptions.InvalidModelException;
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

import javax.xml.bind.JAXBException;

import org.fiteagle.api.core.*;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.ReservationStateEnum;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class ProcessProvision extends AbstractMethodProcessor {
  
  private final static Logger LOGGER = Logger.getLogger(ProcessProvision.class.getName());
  
  
  public  Model provisionInstances(
			List<URN> urns) throws UnsupportedEncodingException {

		LOGGER.log(Level.INFO, "create provision model ");
		Model requestModel = ModelFactory.createDefaultModel();
		for (URN urn : urns) {


			if (ISFA_AM.SLICE.equals(urn.getType())) {
                Individual topology = Omn.Topology.createIndividual(IConfig.TOPOLOGY_NAMESPACE_VALUE+urn.getSubject());
                requestModel.add(topology.listProperties());

			}else{
				if (ISFA_AM.Sliver.equals(urn.getType())) {
					Individual resource = Omn.Resource.createIndividual(URLDecoder.decode(urn.getSubject(),"UTF-8"));
                    requestModel.add(resource.listProperties());
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
				"provision reply is received.");
		return provisionResponse;
	}
  
  public  void addProvisionValue(final HashMap<String, Object> result, Model provisionResponse){
	  
	  final Map<String, Object> value = new HashMap<>();

	  try {
		value.put(IGeni.GENI_RSPEC, ManifestConverter.getRSpec(provisionResponse, "localhost"));
	} catch (JAXBException | InvalidModelException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
//	  value.put(IGeni.GENI_RSPEC, "RSPEC manifest");

      this.addSliverInformation(value,provisionResponse);
	  
//	  final List<Map<String, Object>> geniSlivers = new LinkedList<>();
//
//	  StmtIterator stmtIterator = provisionResponse.listStatements(null, RDF.type, Omn.Reservation);
//	    while (stmtIterator.hasNext()) {
//	      Statement statement = stmtIterator.next();
//	      Resource reservation = statement.getSubject();
//
//	      /**
//	       * defines a loop depending on the slivers number.
//	       * In the loop, Map is created for each sliver containing
//	       * sliver urn, experires, allocateion_status and operation_status.
//	       * The created maps should be added to geniSlivers list.
//	       */
//	      final Map<String, Object> sliverMap = new HashMap<>();
//            Resource resource = provisionResponse.getResource(reservation.getProperty(Omn.isReservationOf).getObject().asResource().getURI());
//	      sliverMap.put(IGeni.GENI_SLIVER_URN, ManifestConverter.generateSliverID("localhost",resource.getURI()));
//	      sliverMap.put(IGeni.GENI_EXPIRES, reservation.getProperty(MessageBusOntologyModel.endTime).getLiteral().getString());
//	      sliverMap.put(IGeni.GENI_ALLOCATION_STATUS, ReservationStateEnum.valueOf(reservation.getProperty(Omn_lifecycle.hasReservationState).getResource().getLocalName()).getGeniState());
//	      sliverMap.put(IGeni.GENI_OPERATIONAL_STATUS, "");
//	      sliverMap.put(IGeni.GENI_ERROR, "NO ERROR");
//
//	      geniSlivers.add(sliverMap);
//	    }
	    //value.put(IGeni.GENI_SLIVERS, geniSlivers);
	    result.put(ISFA_AM.VALUE, value);
  }
  
}
