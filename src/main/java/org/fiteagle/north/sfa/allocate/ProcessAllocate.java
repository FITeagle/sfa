package org.fiteagle.north.sfa.allocate;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import javax.xml.parsers.*;

import org.xml.sax.InputSource;
import org.w3c.dom.*;

import java.io.*;

public class ProcessAllocate {
  
  private final static Logger LOGGER = Logger.getLogger(ProcessAllocate.class.getName());
  private static String prefix = "http://open-multinet.info/ontology/omn#";
  private static String URN = "urn";
  private static String Request = "request";
  private static String EndTime = "endTime";
  private static String node = "node";
  private static String componentManagerId = "component_manager_id";
  
  public static void parseAllocateParameter(final List<?> parameter, Map<String, String> allocateParameters) {
    ProcessAllocate.LOGGER.log(Level.INFO, "parsing allocate parameter");
    System.out.println(parameter);
    System.out.println(parameter.size());
    for (final Object param : parameter) {
      if (param instanceof String) {
        String allocateParameter = (String) param;
        if (allocateParameter.startsWith(URN)) {
          allocateParameters.put(URN, allocateParameter);
          ProcessAllocate.LOGGER.log(Level.INFO, allocateParameters.get(URN));
        } else if (allocateParameter.contains(Request)) {
          allocateParameters.put(Request, allocateParameter);
          ProcessAllocate.LOGGER.log(Level.INFO, allocateParameters.get(Request));
        }
      }
      
      if (param instanceof Map<?, ?>) {
        @SuppressWarnings("unchecked")
        final Map<String, ?> param2 = (Map<String, ?>) param;
        if (!param2.isEmpty()) {
          for (Map.Entry<String, ?> parameters : param2.entrySet()) {
            if (parameters.getKey().toString().equals(ISFA_AM.GENI_END_TIME)) {
              allocateParameters.put(EndTime, parameters.getValue().toString());
              ProcessAllocate.LOGGER.log(Level.INFO, allocateParameters.get(EndTime));
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
  
  public static String reserveInstances(Map<String, String> allocateParameter) {
    String result = "";
    Model requestModel = ModelFactory.createDefaultModel();
    Resource reservationRequest = requestModel.createResource(MessageBusOntologyModel.internalMessage.getURI());
    reservationRequest.addProperty(MessageBusOntologyModel.requestType, IMessageBus.REQUEST_TYPE_RESERVE);
    
    for (Map.Entry<String, String> parameter : allocateParameter.entrySet()) {
      if (parameter.getKey().equals(Request)) {
        reservationRequest.addProperty(requestModel.createProperty(prefix + componentManagerId),
            parseRSpec(parameter.getValue(),componentManagerId));
      } else {
        reservationRequest.addProperty(requestModel.createProperty(prefix + parameter.getKey()), parameter.getValue()
            .toString());
      }
    }
    String serializedModel = MessageUtil.serializeModel(requestModel);
    Model resultModel = SFA_AM_MDBSender.getInstance().sendRequest(serializedModel);
    return result;
  }
  
  private static String parseRSpec(String request, String requiredAttribute) {
    String attribute = "";
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db;
      db = dbf.newDocumentBuilder();
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(request));
      
      Document doc = db.parse(is);
      NodeList nodes = doc.getElementsByTagName(node);
      
      for (int i = 0; i < nodes.getLength(); i++) {
        Element element = (Element) nodes.item(i);
        attribute = element.getAttribute(requiredAttribute);
        System.out.println("parsed element component manager id " + element.getAttribute(requiredAttribute));
      }
    } catch (ParserConfigurationException | SAXException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return attribute;
  }
  
  /*
   * private static String noMaxInstances = "UNLIMITED"; public void allocateResources() throws JMSException { String
   * maxInstances = getMaxInstances(); if(!maxInstances.equals(noMaxInstances)){ int instances = getInstances(); } }
   *//**
   * 
   * @return the maximum number of instances which resource adapter can instantiate
   * @throws JMSException
   * @throws TimeoutException
   */
  /*
   * public static String getMaxInstances() { String maxInstances = noMaxInstances; String query =
   * "PREFIX av: <http://federation.av.tu-berlin.de/about#> " + "RREFIX omn: <http://open-multinet.info/ontology/omn#> "
   * + "SELECT ?amount " + "WHERE {av:MotorGarage-1 omn:maxInstances ?amount } "; Model resultModel =
   * SFA_AM_MDBSender.getInstance().sendRequest(query); StmtIterator iter = resultModel.listStatements();
   * while(iter.hasNext()){ int result = iter.next().getInt(); return Integer.toString(result); } return maxInstances; }
   * public static int getInstances() { int instances = 0; String query =
   * " PREFIX av: <http://federation.av.tu-berlin.de/about#> " +
   * " RREFIX omn: <http://open-multinet.info/ontology/omn#> " + "SELECT ?instance " +
   * "WHERE {?instance a ?resourceType . " + "av:MotorGarage-1 a ?adapterType . " +
   * "?adapterType omn:implements ?resourceType .} "; Model resultModel =
   * SFA_AM_MDBSender.getInstance().sendRequest(query); StmtIterator iter = resultModel.listStatements();
   * while(iter.hasNext()){ instances++; iter.next(); } return instances; }
   */
}
