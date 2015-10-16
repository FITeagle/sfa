package org.fiteagle.north.sfa.am.listResources;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.translators.geni.AdvertisementConverter;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import org.fiteagle.api.core.*;
import org.fiteagle.north.sfa.am.GENI_CodeEnum;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.exceptions.BadVersionException;
import org.fiteagle.north.sfa.util.GENI_Credential;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dne on 03.03.15.
 */
public class ListResourcesProcessor extends AbstractMethodProcessor{
  
    public ListResourcesProcessor(final List<?> parameter) {
        this.parameter = parameter;
    }
        
    public Model listResources() throws UnsupportedEncodingException, JMSException {
        Model resourcesResult = getResources();
        Model topologyModel = ModelFactory.createDefaultModel();
        topologyModel.setNsPrefixes(resourcesResult.getNsPrefixMap());
        
        Resource topology = topologyModel.createResource(AnonId.create());
        topology.addProperty(RDF.type, Omn_lifecycle.Offering);
        topology.addProperty(RDF.type, Omn.Topology);
        ResIterator resIterator = resourcesResult.listSubjectsWithProperty(Omn_lifecycle.canImplement);
        while(resIterator.hasNext()){
            Resource resource = resIterator.nextResource();
            topology.addProperty(Omn.hasResource, resource);
            resource.addProperty(Omn.isResourceOf, topology);
            topologyModel.add(resource.getModel());
        }
        this.addManagerId(topologyModel);
        return topologyModel;
    }

    private Model getResources() throws JMSException, UnsupportedEncodingException {
        Model requestModel = ModelFactory.createDefaultModel();
        String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
        Model model = getSender().sendRDFRequest(serializedModel, IMessageBus.TYPE_GET, IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER);
       return  model;
    }

    public void addManagerId(Model topologyModel) {
      ResIterator resIterator = topologyModel.listResourcesWithProperty(Omn_lifecycle.canImplement);
        Config config = new Config();
      Resource aggregateManager = topologyModel.createResource("urn:publicid:IDN+"+config.getProperty(IConfig.KEY_HOSTNAME)+"+authority+cm");
      while(resIterator.hasNext()){
          resIterator.nextResource().addProperty(Omn_lifecycle.parentOf,aggregateManager);
          }
      }

    public void createResponse(final HashMap<String, Object> result, Model topologyModel) throws JAXBException, InvalidModelException, UnsupportedEncodingException {
      String testbedResources = "";
      
      if(ISFA_AM.VERSION_3.equals(this.delegate.getRspecVersion()) && ISFA_AM.GENI.equals(this.delegate.getRspecType())){
        AdvertisementConverter converter = new AdvertisementConverter();
          converter.setVerbose(true);
        testbedResources = converter.getRSpec(topologyModel);
    }else if("omn".equals(this.delegate.getRspecType())){
         testbedResources = MessageUtil.serializeModel(topologyModel, IMessageBus.SERIALIZATION_RDFXML);
    }
   
    if (this.delegate.getCompressed()) {
        result.put(ISFA_AM.VALUE, compress(testbedResources));
    } else {
        result.put(ISFA_AM.VALUE, testbedResources);
    }
    this.addCode(result);
    this.addOutput(result);
    }
        
        
    public boolean checkSupportedVersions() {
      if(!(ISFA_AM.GENI.equals(this.delegate.getRspecType()) && ISFA_AM.VERSION_3.equals(this.delegate.getRspecVersion())) && !"omn".equals(this.delegate.getRspecType()))
        return false;
      else 
        return true;
      
    }
        
    public void parseOptionsParameters() {
      String query = "";
      
      @SuppressWarnings("unchecked")
      final Map<String, ?> param2 = (Map<String, ?>) parameter.get(1);

      if (param2.containsKey(IGeni.GENI_QUERY)) {
          query = param2.get(IGeni.GENI_QUERY).toString();
      } else {
          query = "";
      }

      if (param2.containsKey(IGeni.GENI_COMPRESSED)) {
          this.delegate.setCompressed((Boolean) param2.get(IGeni.GENI_COMPRESSED));
      }
      if (param2.containsKey(IGeni.GENI_AVAILABLE)) {
          this.delegate.setAvailable((Boolean) param2.get(IGeni.GENI_AVAILABLE));
      }

      if(param2.containsKey(IGeni.GENI_RSPEC_VERSION)){
        if(param2.get(IGeni.GENI_RSPEC_VERSION) instanceof Map<?, ?>){
          final Map<String, ?> geniRSpecVersion = (Map<String, ?>) param2.get(IGeni.GENI_RSPEC_VERSION);
          this.delegate.setRspecType((String) geniRSpecVersion.get(ISFA_AM.TYPE));
          this.delegate.setRspecVersion( (String) geniRSpecVersion.get(ISFA_AM.VERSION));
          }
        }
      }

}
