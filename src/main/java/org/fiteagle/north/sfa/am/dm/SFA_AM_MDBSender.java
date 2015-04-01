package org.fiteagle.north.sfa.am.dm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Topic;
import javax.ws.rs.core.Response;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.Reasoner;
import info.openmultinet.ontology.Parser;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import org.fiteagle.north.sfa.exceptions.EmptyReplyException;;

@Startup
@Singleton
public class SFA_AM_MDBSender {
  
  @Inject
  private JMSContext context;
  @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
  private Topic topic;
  
  private static Logger LOGGER = Logger.getLogger(SFA_AM_MDBSender.class.toString());
  
  private final static String TRIPLET_STORE_URL = "<http://localhost:3030/fiteagle/query> ";
  
  private static SFA_AM_MDBSender instance;

   Reasoner reasoner;
  public SFA_AM_MDBSender() {
    instance = this;
      reasoner  = Parser.createReasoner();

  }
  
  public static SFA_AM_MDBSender getInstance() {
    return instance;
  }
  
  public Model sendSPARQLQueryRequest(String query, String methodTarget) {
    Message request = MessageUtil.createSPARQLQueryMessage(query, methodTarget, IMessageBus.SERIALIZATION_TURTLE, context);
    return sendRequest(request, IMessageBus.TYPE_GET, methodTarget);
  }
  
  public Model sendRDFRequest(String model, String methodType, String methodTarget) {
    final Message request = MessageUtil.createRDFMessage(model, methodType, methodTarget, IMessageBus.SERIALIZATION_TURTLE, null, context);
    return sendRequest(request, methodType, methodTarget);
  }
  
  public Model sendRequest(Message request, String methodType, String methodTarget) {
    context.createProducer().send(topic, request);
    Message rcvMessage = MessageUtil.waitForResult(request, context, topic);
    String resultString = MessageUtil.getStringBody(rcvMessage);
    
    if(MessageUtil.getMessageType(rcvMessage).equals(IMessageBus.TYPE_ERROR)){
      if(resultString.equals(Response.Status.REQUEST_TIMEOUT.name())){
        throw new MessageUtil.TimeoutException("Sent message ("+ methodType + ") (Target: "+methodTarget+"): "+MessageUtil.getStringBody(request));
      }
      throw new RuntimeException(resultString);
    }
    else{
      LOGGER.log(Level.INFO, "Received reply");
      return MessageUtil.parseSerializedModel(resultString, IMessageBus.SERIALIZATION_TURTLE);
    }
  }
  
  public List<String> getExtensions() {
    String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
        + "PREFIX omn: <http://open-multinet.info/ontology/omn#> "
        + "PREFIX omnFederation: <http://open-multinet.info/ontology/omn-federation#> ."
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + "CONSTRUCT { "
        + "?resource omnFederation:partOfFederation ?testbed. " + "?resource rdf:type ?type. }" + "FROM " + TRIPLET_STORE_URL
        + "WHERE {" + "?resource omnFederation:partOfFederation ?testbed. " + "?testbed a omnFederation:Federation. "
        + "OPTIONAL {?resource rdf:type ?type. } }";
    Model resultModel = sendSPARQLQueryRequest(query, IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER);
    
    List<String> namespaces = new ArrayList<>();
    StmtIterator iter = resultModel.listStatements();
    while (iter.hasNext()) {
      Statement currentStatement = iter.next();
      Resource subject = currentStatement.getSubject();
      if (!namespaces.contains(subject.getNameSpace())) {
        namespaces.add(subject.getNameSpace());
      }
      RDFNode objectNode = currentStatement.getObject();
      if (objectNode.canAs(Resource.class)) {
        Resource object = objectNode.asResource();
        if (!namespaces.contains(object.getNameSpace())) {
          namespaces.add(object.getNameSpace());
        }
      }
    }
    for (Map.Entry<String, String> entry : resultModel.getNsPrefixMap().entrySet()) {
      if (!namespaces.contains(entry.getValue())) {
        namespaces.add(entry.getValue());
      }
    }
    return namespaces;
  }
  
  
}
