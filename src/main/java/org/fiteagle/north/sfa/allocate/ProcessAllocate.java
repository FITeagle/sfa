package org.fiteagle.north.sfa.allocate;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
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
            if (parameters.getKey().toString().equals(ISFA_AM.GENI_END_TIME)) {
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
  public static void reserveInstances(final Map<String, Object> allocateParameter, Map<String, String> sliverMap) {
    
    Model requestModel = ModelFactory.createDefaultModel();
    Resource slice = requestModel.createResource(allocateParameter.get(ISFA_AM.URN).toString());
    slice.addProperty(RDF.type, MessageBusOntologyModel.classGroup);
    if(allocateParameter.containsKey(ISFA_AM.EndTime)){
      slice.addProperty(MessageBusOntologyModel.endTime, allocateParameter.get(ISFA_AM.EndTime).toString());
    }
    int counter = 1;
    for (final Object requiredReserouces : (List<String>) allocateParameter.get(ISFA_AM.RequiredResources)){
      Resource sliver = requestModel.createResource(setSliverURN(allocateParameter.get(ISFA_AM.URN).toString(), counter));
      sliver.addProperty(RDF.type, MessageBusOntologyModel.classReservation);
      sliver.addProperty(MessageBusOntologyModel.partOf, slice.getURI());
      sliver.addProperty(MessageBusOntologyModel.reserveInstanceFrom, requiredReserouces.toString());
      counter = counter + 1;
    }
    
    String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
    LOGGER.log(Level.INFO, "send reservation request ...");
    Model resultModel = SFA_AM_MDBSender.getInstance().sendRDFRequest(serializedModel, IMessageBus.TYPE_CREATE, IMessageBus.TARGET_RESERVATION);
    LOGGER.log(Level.INFO, "reservation reply received.");
    
    StmtIterator iter = resultModel.listStatements();
    while (iter.hasNext()) {
      Statement st = iter.next();
      Resource r = st.getSubject();
      //sliverMap.put(r.getURI().toString(), "geni_allocated");
      sliverMap.put(r.getURI().toString(), st.getObject().toString());
      //sliverMap.put(st.getSubject().getURI(),"geni_allocated");
      //LOGGER.log(Level.INFO, "created sliver " + st.getSubject().getURI());
      LOGGER.log(Level.INFO, "created sliver " + r.getURI());
    }
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
  
  public static void addAllocateValue(final HashMap<String, Object> result, final Map<String,String> slivers, final Map<String, Object> allocateParameters) {
    final Map<String, Object> value = new HashMap<>();
    
    value.put(ISFA_AM.GENI_RSPEC, "should be the geni.rspec manifest"); // to be continued
    
    final List<Map<String, Object>> geniSlivers = new LinkedList<>();
    
    /**
     * defines a loop depending on the slivers number.
     * In the loop, Map is created for each sliver containing 
     * sliver urn, experires and status.
     * The created maps should be added to geni_slivers list.
     */    
    for (Map.Entry<String, String> sliver : slivers.entrySet()) {
      LOGGER.log(Level.INFO, "sliver in the list " + sliver.getKey());
      final Map<String, Object> sliverMap = new HashMap<>();
      sliverMap.put(ISFA_AM.GENI_SLIVER_URN, sliver.getKey());
      LOGGER.log(Level.INFO, "sliver in the map is " + sliverMap.get(ISFA_AM.GENI_SLIVER_URN));
      if(allocateParameters.containsKey(ISFA_AM.EndTime)){
        sliverMap.put(ISFA_AM.GENI_EXPIRES, allocateParameters.get(ISFA_AM.EndTime));
        LOGGER.log(Level.INFO, "end time is " + sliverMap.get(ISFA_AM.GENI_EXPIRES));
      } else {
        sliverMap.put(ISFA_AM.GENI_EXPIRES, "");
      }
      sliverMap.put(ISFA_AM.GENI_ALLOCATION_STATUS, sliver.getValue());
      LOGGER.log(Level.INFO, "geni allocation status is " + sliverMap.get(ISFA_AM.GENI_ALLOCATION_STATUS));
      geniSlivers.add(sliverMap);
    }
    value.put(ISFA_AM.GENI_SLIVERS, geniSlivers);
    result.put(ISFA_AM.VALUE, value);
  }
  
  private static String setSliverURN(String SliceURN, int i){
	  String sliverURN = "";
	  String sliver = "sliver";
	  URN urn = new URN(SliceURN);
	  urn.setType(sliver);
	  urn.setSubject(Integer.toString(i));
	  sliverURN = urn.toString();
	  return sliverURN;
  }
  }

