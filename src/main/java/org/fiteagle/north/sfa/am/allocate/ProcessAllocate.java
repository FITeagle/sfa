package org.fiteagle.north.sfa.am.allocate;

import com.hp.hpl.jena.rdf.model.*;

import info.openmultinet.ontology.exceptions.InvalidModelException;
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
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;

import com.hp.hpl.jena.vocabulary.RDF;


public class ProcessAllocate extends AbstractMethodProcessor{
  
  private final static Logger LOGGER = Logger.getLogger(ProcessAllocate.class.getName());
  
  private Map<String, Object> allocateParameters = new HashMap<>(); 
  
  public ProcessAllocate(List<?> parameter) {
    this.parameter = parameter;
  }
  
  public void parseAllocateParameter() throws JAXBException, InvalidModelException {
    ProcessAllocate.LOGGER.log(Level.INFO, "parsing allocate parameter");
    System.out.println(this.parameter);
    System.out.println(this.parameter.size());
    for (final Object param : this.parameter) {
      if (param instanceof String) {
        String allocateParameter = (String) param;
        if (allocateParameter.startsWith(ISFA_AM.URN)) {
          allocateParameters.put(ISFA_AM.URN, new URN(allocateParameter));
          ProcessAllocate.LOGGER.log(Level.INFO, allocateParameters.get(ISFA_AM.URN).toString());
        } else if (allocateParameter.contains(ISFA_AM.REQUEST)) {
          allocateParameters.put(ISFA_AM.REQUEST, allocateParameter);
          ProcessAllocate.LOGGER.log(Level.INFO, allocateParameters.get(ISFA_AM.REQUEST).toString());

          Model model = parseRSpec(allocateParameter);
          allocateParameters.put(ISFA_AM.RequiredResources, model);
        }
      }
      if (param instanceof Map<?, ?>) {
        @SuppressWarnings("unchecked")
        final Map<String, ?> param2 = (Map<String, ?>) param;
        if (!param2.isEmpty()) {
          for (Map.Entry<String, ?> parameters : param2.entrySet()) {
            if (parameters.getKey().toString().equals(IGeni.GENI_END_TIME)) {
              allocateParameters.put(ISFA_AM.EndTime, parameters.getValue().toString());
              ProcessAllocate.LOGGER.log(Level.INFO, allocateParameters.get(ISFA_AM.EndTime).toString());
            }
          }
        }
      }
    }

  }
  
  @SuppressWarnings("unchecked")
  public Model reserveInstances() {
    Model incoming = (Model) allocateParameters.get(ISFA_AM.RequiredResources);
    Model requestModel = ModelFactory.createDefaultModel();
      URN sliceURN = (URN) allocateParameters.get(ISFA_AM.URN);

      Resource topology = requestModel.createResource(IConfig.TOPOLOGY_NAMESPACE_VALUE+ sliceURN.getSubject());
      topology.addProperty(RDF.type, Omn.Topology);
      Model requestedResources = getRequestedResources(topology, incoming);
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

            }
            topology.addProperty(Omn.hasResource, newResource);
            requestedResourcesModel.add(topology.getProperty(Omn.hasResource));
        }

       return requestedResourcesModel;
    }



  private Model parseRSpec(String request) throws JAXBException, InvalidModelException {

      InputStream is = new ByteArrayInputStream( request.getBytes(Charset.defaultCharset()) );
      Model model = RequestConverter.getModel(is);
      return  model;
  }
  
  public void createResponse(final HashMap<String, Object> result, Model allocateResponse) throws UnsupportedEncodingException {
    final Map<String, Object> value = new HashMap<>();
    

    try {

      value.put(IGeni.GENI_RSPEC, ManifestConverter.getRSpec(allocateResponse, IConfig.DEFAULT_HOSTNAME));
    } catch (JAXBException | InvalidModelException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    final List<Map<String, Object>> geniSlivers = new LinkedList<>();
    
    ResIterator iterator = allocateResponse.listResourcesWithProperty(RDF.type,Omn.Reservation);
    while(iterator.hasNext()){

      final Map<String, Object> sliverMap = new HashMap<>();

      Resource reservation = iterator.nextResource();
      
      sliverMap.put(IGeni.GENI_SLIVER_URN, ManifestConverter.generateSliverID(IConfig.DEFAULT_HOSTNAME,reservation.getProperty(Omn.isReservationOf).getResource().getURI()));
      sliverMap.put(IGeni.GENI_EXPIRES, reservation.getProperty(MessageBusOntologyModel.endTime).getLiteral().getString());
      sliverMap.put(IGeni.GENI_ALLOCATION_STATUS, ReservationStateEnum.valueOf(reservation.getProperty(Omn_lifecycle.hasReservationState).getResource().getLocalName()).getGeniState());
      geniSlivers.add(sliverMap);
    }

    value.put(IGeni.GENI_SLIVERS, geniSlivers);
    result.put(ISFA_AM.VALUE, value);
    this.addCode(result);
    this.addOutput(result);
  }



    private String setSliverURN(String SliceURN, int i){
	  String sliverURN = "";
	  URN urn = new URN(SliceURN);
	  urn.setType(ISFA_AM.Sliver);
	  urn.setSubject(Integer.toString(i));
	  sliverURN = urn.toString();
	  return sliverURN;
  }
  }

