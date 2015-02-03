package org.fiteagle.north.sfa.allocate;

import com.hp.hpl.jena.rdf.model.*;
import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.translators.geni.ManifestConverter;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.ReservationStateEnum;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.vocabulary.RDF;


public class ProcessAllocate {
  
  private final static Logger LOGGER = Logger.getLogger(ProcessAllocate.class.getName());
  
  public static void parseAllocateParameter(final List<?> parameter, final Map<String,  Object> allocateParameters) {
    ProcessAllocate.LOGGER.log(Level.INFO, "parsing allocate parameter");
    System.out.println(parameter);
    System.out.println(parameter.size());
    for (final Object param : parameter) {
      if (param instanceof String) {
        String allocateParameter = (String) param;
        if (allocateParameter.startsWith(ISFA_AM.URN)) {
          allocateParameters.put(ISFA_AM.URN, allocateParameter);
          ProcessAllocate.LOGGER.log(Level.INFO, allocateParameters.get(ISFA_AM.URN).toString());
        } else if (allocateParameter.contains(ISFA_AM.REQUEST)) {
          allocateParameters.put(ISFA_AM.REQUEST, allocateParameter);
          ProcessAllocate.LOGGER.log(Level.INFO, allocateParameters.get(ISFA_AM.REQUEST).toString());
          final List<String> requiredResources = new LinkedList<>();
          parseRSpec(allocateParameter, ISFA_AM.componentManagerId, requiredResources);
          allocateParameters.put(ISFA_AM.RequiredResources, requiredResources);
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
    
    /*
     * for (final Object param : parameter) { if (param instanceof String) { // considered to be slice_urn String param2
     * = (String) param; this.delegate.setSliceURN(param2); } else if (param instanceof Map<?, ?>) { // considered to be
     * options parameters. this.parseOptionsParameters(param); } else if(param instanceof List<?>){ // considered to be
     * credentials parameters. this.parseCredentialsParameters(param); } }
     */
  }
  
  @SuppressWarnings("unchecked")
  public static Model reserveInstances(final Map<String, Object> allocateParameter) {
    
    Model requestModel = ModelFactory.createDefaultModel();
    Resource slice = requestModel.createResource(allocateParameter.get(ISFA_AM.URN).toString());
    slice.addProperty(RDF.type, Omn.Topology);

    int counter = 1;
    for (final Object requiredResources : (List<String>) allocateParameter.get(ISFA_AM.RequiredResources)){
      Resource sliver = requestModel.createResource(setSliverURN(allocateParameter.get(ISFA_AM.URN).toString(), counter));
      sliver.addProperty(RDF.type, Omn.Resource);
      Resource reservation = requestModel.createResource(Omn.Reservation.getURI()+UUID.randomUUID().toString());
        reservation.addProperty(RDF.type, Omn.Reservation);
        reservation.addProperty(Omn.isReservationOf, sliver);
        reservation.addProperty(Omn_lifecycle.hasReservationState, Omn_lifecycle.Allocated);

      sliver.addProperty(Omn.isResourceOf, requiredResources.toString());
        slice.addProperty(Omn.hasResource, sliver);
      if(allocateParameter.containsKey(ISFA_AM.EndTime)){
        reservation.addProperty(MessageBusOntologyModel.endTime, allocateParameter.get(ISFA_AM.EndTime).toString());
      }else{
        Date afterAdding2h = getDefaultExpirationTime();
        reservation.addProperty(MessageBusOntologyModel.endTime, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(afterAdding2h));
      }
      counter = counter + 1;
    }
    
    String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
    LOGGER.log(Level.INFO, "send reservation request ...");
    Model resultModel = SFA_AM_MDBSender.getInstance().sendRDFRequest(serializedModel, IMessageBus.TYPE_CREATE, IMessageBus.TARGET_RESERVATION);
    LOGGER.log(Level.INFO, "reservation reply received.");
    
//    StmtIterator iter = resultModel.listStatements();
//    while (iter.hasNext()) {
//      Statement st = iter.next();
//      Resource r = st.getSubject();
//      sliverMap.put(r.getURI().toString(), st.getObject().toString());
//      LOGGER.log(Level.INFO, "created sliver " + r.getURI());
//    }
    return resultModel;
  }

  private static Date getDefaultExpirationTime() {
    Date date = new Date();
    long t=date.getTime();
    return new Date(t + (120 * 60000));
  }

  private static void parseRSpec(String request, String requiredAttribute, List<String> requiredResources) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db;
      db = dbf.newDocumentBuilder();
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(request));
      
      Document doc = db.parse(is);
      NodeList nodes = doc.getElementsByTagName(ISFA_AM.node);
      
      for (int i = 0; i < nodes.getLength(); i++) {
        Element element = (Element) nodes.item(i);
        requiredResources.add(element.getAttribute(requiredAttribute));
        System.out.println("parsed element component manager id " + element.getAttribute(requiredAttribute));
      }
    } catch (ParserConfigurationException | SAXException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public static void addAllocateValue(final HashMap<String, Object> result, final Map<String, Object> allocateParameters, Model allocateResponse) {
    final Map<String, Object> value = new HashMap<>();
    

    try {
      value.put(IGeni.GENI_RSPEC, ManifestConverter.getRSpec(allocateResponse));
    } catch (JAXBException | InvalidModelException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    final List<Map<String, Object>> geniSlivers = new LinkedList<>();
    
    ResIterator iterator = allocateResponse.listResourcesWithProperty(RDF.type,Omn.Reservation);
    while(iterator.hasNext()){
      /**
       * defines a loop depending on the slivers number.
       * In the loop, Map is created for each sliver containing 
       * sliver urn, experires and status.
       * The created maps should be added to geni_slivers list.
       */    
      final Map<String, Object> sliverMap = new HashMap<>();

      Resource reservation = iterator.nextResource();
      
      sliverMap.put(IGeni.GENI_SLIVER_URN, reservation.getProperty(Omn.isReservationOf).getResource().getURI());
      sliverMap.put(IGeni.GENI_EXPIRES, reservation.getProperty(MessageBusOntologyModel.endTime).getLiteral().getString());
      sliverMap.put(IGeni.GENI_ALLOCATION_STATUS, ReservationStateEnum.valueOf(reservation.getProperty(Omn_lifecycle.hasReservationState).getResource().getLocalName()).getGeniState());
      geniSlivers.add(sliverMap);
    }

    value.put(IGeni.GENI_SLIVERS, geniSlivers);
    result.put(ISFA_AM.VALUE, value);
  }
  
  private static String setSliverURN(String SliceURN, int i){
	  String sliverURN = "";
	  URN urn = new URN(SliceURN);
	  urn.setType(ISFA_AM.Sliver);
	  urn.setSubject(Integer.toString(i));
	  sliverURN = urn.toString();
	  return sliverURN;
  }
  }

