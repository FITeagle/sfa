package org.fiteagle.north.sfa.am.dm;

import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
//import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.ws.rs.core.Response;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusMsgFactory;

import com.hp.hpl.jena.rdf.model.Model;

@Startup
@Singleton
public class SFA_AM_MDBSender {

	@Inject
	private JMSContext context;
	@Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
	// @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
	private Topic topic;
	
	private static SFA_AM_MDBSender instance;
	
	
	public SFA_AM_MDBSender(){
		instance = this;
	}
	
	public static SFA_AM_MDBSender getInstance() {
		return instance;
	}
	

	public String getTestbedDescription() throws JMSException{
		String query = "DESCRIBE ?testbed WHERE {?testbed <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://fiteagle.org/ontology#Testbed>. }";
	    String requestModel = MessageBusMsgFactory.createSerializedSPARQLQueryModel(query);
	    final Message request = createRDFMessage(requestModel, IMessageBus.TYPE_REQUEST);
	    sendRequest(request);
	    
	    Message rcvMessage = waitForResult(request);
	    String resultString = getResult(rcvMessage);
	    String result = MessageBusMsgFactory.getTTLResultModelFromSerializedModel(resultString);
	    
	    Model resultModel = MessageBusMsgFactory.parseSerializedModel(result);
	    MessageBusMsgFactory.setCommonPrefixes(resultModel);
	    result = MessageBusMsgFactory.serializeModel(resultModel);
	    
	    System.out.println("result is " + result);
	    return result;
	}
	
	private Message createRDFMessage(final String rdfInput, final String methodType) {
        final Message message = this.context.createMessage();

        try {
          message.setStringProperty(IMessageBus.METHOD_TYPE, methodType);
          message.setStringProperty(IMessageBus.SERIALIZATION, IMessageBus.SERIALIZATION_DEFAULT);
          message.setStringProperty(IMessageBus.RDF, rdfInput);
          message.setJMSCorrelationID(UUID.randomUUID().toString());
        } catch (JMSException e) {
          System.out.println(e.getMessage());
        }

        return message;
    }
	
	 private void sendRequest(final Message message) {
	     this.context.createProducer().send(this.topic, message);
	 }
	 
	   private Message waitForResult(final Message message) throws JMSException {
	        final String filter = "JMSCorrelationID='" + message.getJMSCorrelationID() + "'";
	        final Message rcvMessage = this.context.createConsumer(this.topic, filter).receive(IMessageBus.TIMEOUT);
	        return rcvMessage;
	    }
	   
	    private String getResult(final Message rcvMessage) throws JMSException {
	        String resources = Response.Status.REQUEST_TIMEOUT.name();
	        if (null != rcvMessage) {
	            resources = rcvMessage.getStringProperty(IMessageBus.RDF);
	        }
	        return resources;
	    }

	/**
	 * curl -v http://localhost:8080/sfa/sfarest/listResources
	 */
	//@Produces("text/turtle")
//	@GET
//	@Path("listResources")
	public void listResources() {
		final int TIMEOUT = 5000;
		final Message message = this.context.createMessage();
		 try {
			//message.setStringProperty(IMessageBus.TYPE_REQUEST, SFAsender.LIST_RESOURCES);
			message.setStringProperty(IMessageBus.SERIALIZATION, "TURTLE");
			 message.setStringProperty(IMessageBus.QUERY, "SELECT * {?s ?p ?o} LIMIT 100");
			 message.setJMSCorrelationID(UUID.randomUUID().toString());
			 System.out.println("sending a request from SFA to resource repository");
			 this.context.createProducer().send(this.topic, message);
			 
			 final String filter = "JMSCorrelationID='"+ message.getJMSCorrelationID() + "'";
			 Message rcvMessage = this.context.createConsumer(this.topic, filter).receive(TIMEOUT);
			
			 String result = "";
			 if (null != rcvMessage){
				 result = rcvMessage.getStringProperty(IMessageBus.RESULT); 
				 System.out.println("the received result for listResources method is " + result);
			 }
			 else
				 System.out.println("the received result for listResources method is empty !");
 
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
