package org.fiteagle.north.sfa.am.dm;

import java.util.HashMap;
import java.util.Map;
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
import com.hp.hpl.jena.rdf.model.StmtIterator;

@Startup
@Singleton
public class SFA_AM_MDBSender {

	@Inject
	private JMSContext context;
	@Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
	private Topic topic;
	
	
	private static SFA_AM_MDBSender instance;
	
	public SFA_AM_MDBSender(){
		instance = this;
	}
	
	public static SFA_AM_MDBSender getInstance() {
		return instance;
	}
	
	public Map<String, String> getExtensions() throws JMSException, TIMEOUTException {
		Map<String, String> extensionsMap = new HashMap<>();
		
		//String query = "DESCRIBE ?resource WHERE {?resource <http://open-multinet.info/ontology/omn#partOfGroup> <http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed>. }";
		String query = "PREFIX omn: <http://open-multinet.info/ontology/omn#> "
		          + "PREFIX wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> "
		          + "PREFIX av: <http://federation.av.tu-berlin.de/about#> "
		          + "CONSTRUCT { ?resource omn:partOfGroup av:AV_Smart_Communication_Testbed."
		          + "?resource wgs:lat ?lat. ?resource wgs:long ?long. "
		          + "} "
		          + "FROM <http://localhost:3030/ds/query> "
		          + "WHERE {?resource omn:partOfGroup av:AV_Smart_Communication_Testbed. "
		          + "}";
		String requestModel = MessageBusMsgFactory.createSerializedSPARQLQueryModel(query);
	    final Message request = createRDFMessage(requestModel, IMessageBus.TYPE_REQUEST);
	    sendRequest(request);
	    
	    Message rcvMessage = waitForResult(request);
	    String resultString = getResult(rcvMessage);
	    String result = MessageBusMsgFactory.getTTLResultModelFromSerializedModel(resultString);
	    
	    Model resultModel = MessageBusMsgFactory.parseSerializedModel(result);
	    result = MessageBusMsgFactory.serializeModel(resultModel);
	    
	    Model extensionsModel = MessageBusMsgFactory.parseSerializedModel(result);
	    extensionsMap = extensionsModel.getNsPrefixMap();
		return extensionsMap;
	}

	public String getTestbedDescription() throws JMSException, TIMEOUTException, EmptyException{
		//String query = "DESCRIBE ?testbed WHERE {?testbed <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://fiteagle.org/ontology#Testbed>. }";
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX omn: <http://open-multinet.info/ontology/omn#> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> "
				+ "PREFIX av: <http://federation.av.tu-berlin.de/about#> "
				+ "CONSTRUCT { ?testbed rdf:type omn:Testbed. ?testbed rdfs:label ?label. "
				+ "?testbed rdfs:seeAlso ?seeAlso. ?testbed wgs:long ?long. ?testbed wgs:lat ?lat. } "
				+ "FROM <http://localhost:3030/ds/query> "
				+ "WHERE {?testbed rdf:type omn:Testbed. "
				+ "OPTIONAL {?testbed rdfs:label ?label. ?testbed rdfs:seeAlso ?seeAlso. ?testbed wgs:long ?long. ?testbed wgs:lat ?lat. } }";

		String requestModel = MessageBusMsgFactory.createSerializedSPARQLQueryModel(query);
	    final Message request = createRDFMessage(requestModel, IMessageBus.TYPE_REQUEST);
	    sendRequest(request);
	    
	    Message rcvMessage = waitForResult(request);
	    String resultString = getResult(rcvMessage);
	    System.out.println("resultString is " + resultString);
	    String result = MessageBusMsgFactory.getTTLResultModelFromSerializedModel(resultString);
	    
	    Model resultModel = MessageBusMsgFactory.parseSerializedModel(result);
	    StmtIterator iterator = resultModel.listStatements();
	    if(iterator.hasNext() == false){
	    	throw new EmptyException();
		    }
	   
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
	        final Message rcvMessage = this.context.createConsumer(this.topic, filter).receive(IMessageBus.SHORT_TIMEOUT);
	        return rcvMessage;
	    }
	   
	    private String getResult(final Message rcvMessage) throws JMSException, TIMEOUTException {
	        String resources = Response.Status.REQUEST_TIMEOUT.name();
	        if (rcvMessage == null){
	        	throw new TIMEOUTException();
	        }
	        else resources = rcvMessage.getStringProperty(IMessageBus.RDF);
	     
	        return resources;
	    }

	/**
	 * curl -v http://localhost:8080/sfa/sfarest/listResources
	 * @throws TIMEOUTException 
	 */
	    
	    
	    public String getListRessources(String geni_query) throws JMSException, TIMEOUTException{
	    	String requestModel;
	    	if (!geni_query.isEmpty()){
	    		 requestModel = MessageBusMsgFactory.createSerializedSPARQLQueryModel(geni_query);
	    		 System.out.println("we are using the query from the user ");
	    	}
	    	else {
	    		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	    		          + "PREFIX omn: <http://open-multinet.info/ontology/omn#> "
	    		          + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
	    		          + "PREFIX wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> "
	    		          + "CONSTRUCT { "
	    		          + "?resource omn:partOfGroup ?testbed."
	    		          + "?resource rdfs:label ?label. "
	    		          + "?resource rdfs:comment ?comment."
	    		          + "?resource rdf:type ?type. "
	    		          + "?resource wgs:lat ?lat. "
	    		          + "?resource wgs:long ?long. } "
	    		          + "FROM <http://localhost:3030/ds/query> "
	    		          + "WHERE {"
	    		          + "?resource omn:partOfGroup ?testbed. "
	    		          + "?testbed a omn:Testbed. "
	    		          + "OPTIONAL {?resource rdfs:label ?label. }"
	    		          + "OPTIONAL {?resource rdfs:comment ?comment. }"
	    		          + "OPTIONAL {?resource rdf:type ?type. }"
	    		          + "OPTIONAL {?resource wgs:lat ?lat. }"
	    		          + "OPTIONAL {?resource wgs:long ?long. } }";
	    		requestModel = MessageBusMsgFactory.createSerializedSPARQLQueryModel(query);
	    		System.out.println("we are using the default query");
	    	}
	    	
			final Message request = createRDFMessage(requestModel, IMessageBus.TYPE_REQUEST);
		    sendRequest(request);
		
		    Message rcvMessage = waitForResult(request);
		    if (rcvMessage != null){
		    	 String resultString = getResult(rcvMessage);
				 String result = MessageBusMsgFactory.getTTLResultModelFromSerializedModel(resultString);
				 System.out.println("result is " + result);
				 return result;
		    }
		    else{
		    	System.out.println("Recieved Message is empty");
		    	return "error";
		    }

	    }
	    
	    public class TIMEOUTException extends Exception{
	    	/**
			 * 
			 */
			private static final long serialVersionUID = -927138508647382475L;

	    	public TIMEOUTException(){
	    		super("REQUEST TIMEOUT");
	    	}
	    	}
	    
	    
	    
	    public class EmptyException extends Exception{
	    	/**
			 * 
			 */
			private static final long serialVersionUID = 3084952835284992423L;

			/**
	    		 * 
	    		 */
	    	public EmptyException(){
	    		super("EMPTY ANSWER");
	    	}
	    	}

}





