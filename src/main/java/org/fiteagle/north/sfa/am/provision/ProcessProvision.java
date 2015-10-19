package org.fiteagle.north.sfa.am.provision;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.api.core.OntologyModelUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.util.URN;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.translators.geni.ManifestConverter;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_service;

public class ProcessProvision extends AbstractMethodProcessor {
  
  private final static Logger LOGGER = Logger.getLogger(ProcessProvision.class.getName());
  
  private ProvisionOptions provisionOptions; 
  private String hostname = new Config().getProperty(IConfig.KEY_HOSTNAME);
  
  public ProcessProvision(final List<?> parameter) {
    this.parameter = parameter;
  }
  
  public  Model provisionInstances() throws UnsupportedEncodingException {

		LOGGER.log(Level.INFO, "create provision model ");
		Model requestModel = ModelFactory.createDefaultModel();
		
		for (URN urn : this.urns) {

			if (ISFA_AM.SLICE.equals(urn.getType())) {

                Resource topology = requestModel.createResource("http://" + urn.getDomain() + "/topology/" + urn.getSubject());
				topology.addProperty(RDF.type, Omn.Topology);
				addUsers(topology);
                requestModel.add(topology.listProperties());

			}else{
				if (ISFA_AM.Sliver.equals(urn.getType())) {
					Resource resource = requestModel.createResource(URLDecoder.decode(urn.getSubject(), ISFA_AM.UTF_8));
					resource.addProperty(RDF.type,Omn.Resource);
					addUsers(resource);
					requestModel.add(resource.listProperties());
				}
			}

		}

		String serializedModel = MessageUtil.serializeModel(requestModel,
				IMessageBus.SERIALIZATION_TURTLE);
		LOGGER.log(Level.INFO, "START: Provision model: " + serializedModel);
		Model provisionResponse = getSender().sendRDFRequest(serializedModel, IMessageBus.TYPE_CREATE,
						IMessageBus.TARGET_ORCHESTRATOR);
		LOGGER.log(Level.INFO, "END: Provisioned model: " + OntologyModelUtil.toString(provisionResponse));
		return provisionResponse;
	}

	private void addUsers(Resource topology) {
		if(provisionOptions.getUser()!= null){

			topology.addProperty(Omn_service.username, provisionOptions.getUser());
			for(String key: provisionOptions.getKeys()){
				topology.addProperty(Omn_service.publickey, key);
			}
		}

	}

	public void createResponse(final HashMap<String, Object> result, Model provisionResponse){
	  
	  final Map<String, Object> value = new HashMap<>();

	  try {
		  value.put(IGeni.GENI_RSPEC, ManifestConverter.getRSpec(provisionResponse, hostname));
	} catch (JAXBException | InvalidModelException e) {
		LOGGER.log(Level.SEVERE,e.toString());
	}
      this.addSliverInformation(value,provisionResponse);
	    result.put(ISFA_AM.VALUE, value);
	    this.addCode(result);
      this.addOutput(result);
  }
  
  @SuppressWarnings("unchecked")
  public void handleOptions(){
    
    final Map<String, ?> param2 = (Map<String, ?>) this.parameter.get(2);
    provisionOptions = new ProvisionOptions(param2);
    provisionOptions.parse_geni_users();
    
    }
  
}
