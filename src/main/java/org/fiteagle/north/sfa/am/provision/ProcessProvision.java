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
import org.fiteagle.north.sfa.util.GENI_Credential;
import org.fiteagle.north.sfa.util.URN;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class ProcessProvision extends AbstractMethodProcessor {
  
  private final static Logger LOGGER = Logger.getLogger(ProcessProvision.class.getName());
  
  public ProcessProvision(final List<?> parameter) {
    this.parameter = parameter;
  }
  
  public  Model provisionInstances() throws UnsupportedEncodingException {

		LOGGER.log(Level.INFO, "create provision model ");
		Model requestModel = ModelFactory.createDefaultModel();
		for (URN urn : this.urns) {

			if (ISFA_AM.SLICE.equals(urn.getType())) {
                Individual topology = Omn.Topology.createIndividual(IConfig.TOPOLOGY_NAMESPACE_VALUE+urn.getSubject());
                requestModel.add(topology.listProperties());

			}else{
				if (ISFA_AM.Sliver.equals(urn.getType())) {
					Individual resource = Omn.Resource.createIndividual(URLDecoder.decode(urn.getSubject(),ISFA_AM.UTF_8));
                    requestModel.add(resource.listProperties());
				}
			}

		}

		String serializedModel = MessageUtil.serializeModel(requestModel,
				IMessageBus.SERIALIZATION_TURTLE);
		LOGGER.log(Level.INFO, "send provision request ...");
		Model provisionResponse = getSender().sendRDFRequest(serializedModel, IMessageBus.TYPE_CREATE,
						IMessageBus.TARGET_ORCHESTRATOR);
		LOGGER.log(Level.INFO,
				"provision reply is received.");
		return provisionResponse;
	}
  
  public void createResponse(final HashMap<String, Object> result, Model provisionResponse){
	  
	  final Map<String, Object> value = new HashMap<>();

	  try {
		value.put(IGeni.GENI_RSPEC, ManifestConverter.getRSpec(provisionResponse, ISFA_AM.LOCALHOST));
	} catch (JAXBException | InvalidModelException e) {
		// TODO Auto-generated catch block
		LOGGER.log(Level.SEVERE,e.toString());
	}
      this.addSliverInformation(value,provisionResponse);
	    result.put(ISFA_AM.VALUE, value);
	    this.addCode(result);
      this.addOutput(result);
  }
  
}
