package org.fiteagle.north.sfa.am.allocate;

import com.hp.hpl.jena.rdf.model.*;


import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.exceptions.MissingRspecElementException;
import info.openmultinet.ontology.translators.geni.ManifestConverter;
import info.openmultinet.ontology.translators.geni.RequestConverter;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.fiteagle.api.core.*;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.ReservationStateEnum;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.util.URN;

import com.hp.hpl.jena.vocabulary.RDF;


public class ProcessAllocate extends AbstractMethodProcessor{
  
  private final static Logger LOGGER = Logger.getLogger(ProcessAllocate.class.getName());
  
  private Map<String, Object> allocateOptions = new HashMap<>(); 
  
  private URN urn;
  private String request;
  
  public ProcessAllocate(List<?> parameter) {
    this.parameter = parameter;
  }
  
  public void parseAllocateParameter() throws JAXBException, InvalidModelException {
    ProcessAllocate.LOGGER.log(Level.INFO, "parsing allocate parameter");
    LOGGER.log(Level.INFO, "allocate parameters " + this.parameter);
    LOGGER.log(Level.INFO, "number of allocate parameters " + this.parameter.size());
    
    if(parameter.get(0) == null || parameter.get(2) == null){
      throw new BadArgumentsException("sliceUrn and rspec fileds must not be null");
    }
    String slice_urn = (String) parameter.get(0);
    this.urn = new URN(slice_urn);
    LOGGER.log(Level.INFO, "urn " + this.urn);
    this.request = (String) parameter.get(2);
    LOGGER.log(Level.INFO, "request " + this.request);
    
    
    @SuppressWarnings("unchecked")
    final Map<String, ?> param2 = (Map<String, ?>) parameter.get(3);
    if (!param2.isEmpty()) {
          for (Map.Entry<String, ?> parameters : param2.entrySet()) {
            if (parameters.getKey().toString().equals(IGeni.GENI_END_TIME)) {
              allocateOptions.put(ISFA_AM.EndTime, parameters.getValue().toString());
              ProcessAllocate.LOGGER.log(Level.INFO, allocateOptions.get(ISFA_AM.EndTime).toString());
            }
          }
        }
      
  }
  
  @SuppressWarnings("unchecked")
  public Model reserveInstances() throws JAXBException, InvalidModelException, MissingRspecElementException {
    

    Model incoming  = parseRSpec(request);
    Model requestModel = ModelFactory.createDefaultModel();
 
    Resource topology = requestModel.createResource("http://"+this.urn.getDomain()+"/topology/"+ this.urn.getSubject());
    topology.addProperty(RDF.type, Omn.Topology);
    Model requestedResources = getRequestedResources(topology, incoming);
    LOGGER.log(Level.INFO, "allocate model " + requestedResources);
    requestModel.add(requestedResources);
      

    String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
    LOGGER.log(Level.INFO, "send reservation request ...");
    Model resultModel = getSender().sendRDFRequest(serializedModel, IMessageBus.TYPE_CREATE, IMessageBus.TARGET_RESERVATION);
    LOGGER.log(Level.INFO, "reservation reply received.");

    return resultModel;
  }

    private Model getRequestedResources(Resource topology, Model requestedModel) {
        ResIterator resIterator = requestedModel.listResourcesWithProperty(Omn.isResourceOf);
        Model requestedResourcesModel = ModelFactory.createDefaultModel();
        while(resIterator.hasNext()){
            Resource oldResource = resIterator.nextResource();
            Resource oldBase = oldResource.getProperty(Omn_lifecycle.implementedBy).getObject().asResource();

            Resource newResource = requestedResourcesModel.createResource(oldBase.getURI() + "/" + oldResource.getLocalName());

            StmtIterator stmtIterator = oldResource.listProperties();
            while(stmtIterator.hasNext()){
                Statement statement = stmtIterator.nextStatement();
                if(statement.getPredicate().equals(Omn.isResourceOf)){
                    newResource.addProperty(statement.getPredicate(),topology);
                }else{
                    newResource.addProperty(statement.getPredicate(),statement.getObject());
                }
                if(statement.getPredicate().equals(Omn_lifecycle.usesService)){
                    Resource service = requestedModel.getResource(statement.getObject().asResource().getURI());
                    StmtIterator serviceProperties = service.listProperties();
                    while (serviceProperties.hasNext()){
                        requestedResourcesModel.add(serviceProperties.nextStatement());
                    }

                }

            }
            topology.addProperty(Omn.hasResource, newResource);
            requestedResourcesModel.add(topology.getProperty(Omn.hasResource));
        }

       return requestedResourcesModel;
    }



  private Model parseRSpec(String request) throws JAXBException, InvalidModelException, MissingRspecElementException {
      Model model = null ;
      InputStream is = new ByteArrayInputStream( request.getBytes(Charset.defaultCharset()) );
      if(request.contains(RDF.getURI())){
         model = ModelFactory.createDefaultModel();
          model.read(is,null,IMessageBus.SERIALIZATION_RDFXML);
      }else{
          model = RequestConverter.getModel(is);
      }


      return  model;
  }
  
  public void createResponse(final HashMap<String, Object> result, Model allocateResponse) throws UnsupportedEncodingException {
    
    
    final Map<String, Object> value = new HashMap<>();
    

    try {

        Config config = new Config();
      value.put(IGeni.GENI_RSPEC, ManifestConverter.getRSpec(allocateResponse,config.getProperty(IConfig.KEY_HOSTNAME) ));
    } catch (JAXBException | InvalidModelException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    final List<Map<String, Object>> geniSlivers = new LinkedList<>();
    
    ResIterator iterator = allocateResponse.listResourcesWithProperty(RDF.type,Omn.Reservation);
    while(iterator.hasNext()){

      final Map<String, Object> sliverMap = new HashMap<>();

      Resource reservation = iterator.nextResource();
      Config config =  new Config();
      sliverMap.put(IGeni.GENI_SLIVER_URN, ManifestConverter.generateSliverID(config.getProperty(IConfig.KEY_HOSTNAME), reservation.getProperty(Omn.isReservationOf).getResource().getURI()));
      sliverMap.put(IGeni.GENI_EXPIRES, reservation.getProperty(MessageBusOntologyModel.endTime).getLiteral().getString());
      sliverMap.put(IGeni.GENI_ALLOCATION_STATUS, ReservationStateEnum.valueOf(reservation.getProperty(Omn_lifecycle.hasReservationState).getResource().getLocalName()).getGeniState());
      geniSlivers.add(sliverMap);
    }

    value.put(IGeni.GENI_SLIVERS, geniSlivers);
    result.put(ISFA_AM.VALUE, value);
    this.addCode(result);
    this.addOutput(result);
//  }
  }
  }

