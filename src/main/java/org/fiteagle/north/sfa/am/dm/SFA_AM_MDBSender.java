package org.fiteagle.north.sfa.am.dm;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.ws.rs.core.Response;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;

import com.hp.hpl.jena.rdf.model.Model;;

@Startup
@Singleton
public class SFA_AM_MDBSender {

    private final static Logger LOGGER = Logger.getLogger(SFA_AM_MDBSender.class.getName());

    @Inject
    private JMSContext context;
    @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    private static SFA_AM_MDBSender instance;

    
    public SFA_AM_MDBSender() {
	instance = this;
    }

    public static SFA_AM_MDBSender getInstance() {
	return instance;
    }

    public Model sendSPARQLQueryRequest(String query, String methodTarget) {
	Message request = MessageUtil.createSPARQLQueryMessage(query, methodTarget, IMessageBus.SERIALIZATION_TURTLE,
		context);
	Model response = sendRequest(request, IMessageBus.TYPE_GET, methodTarget);

	return response;
    }

    public Model sendRDFRequest(String model, String methodType, String methodTarget) {
	LOGGER.log(Level.INFO, "PREPARE: Sending RDF " + methodType + " to " + methodTarget);
	final Message request = MessageUtil.createRDFMessage(model, methodType, methodTarget,
		IMessageBus.SERIALIZATION_TURTLE, null, context);
	Model response = sendRequest(request, methodType, methodTarget);
	return response;
    }

    public Model sendRequest(Message request, String methodType, String methodTarget) {
	LOGGER.log(Level.INFO, "START: Sending " + methodType + " to " + methodTarget);
	try {
	    LOGGER.log(Level.INFO, "CONTENT: " + ((TextMessage) request).getText());
	} catch (JMSException e) {
	    LOGGER.log(Level.INFO, "CONTENT: " + e.getMessage());
	}
	context.createProducer().send(topic, request);
	Message rcvMessage = MessageUtil.waitForResult(request, context, topic);
	String resultString = MessageUtil.getStringBody(rcvMessage);
	LOGGER.log(Level.INFO, "END: Sending " + methodType + " to " + methodTarget);
	LOGGER.log(Level.INFO, "CONTENT: " + resultString);

	if (MessageUtil.getMessageType(rcvMessage).equals(IMessageBus.TYPE_ERROR)) {
	    if (resultString.equals(Response.Status.REQUEST_TIMEOUT.name())) {
		throw new MessageUtil.TimeoutException("Sent message (" + methodType + ") (Target: " + methodTarget
			+ "): " + MessageUtil.getStringBody(request));
	    }
	    throw new BadArgumentsException(resultString);
	} else {
	    LOGGER.log(Level.INFO, "Received reply");
	    return MessageUtil.parseSerializedModel(resultString, IMessageBus.SERIALIZATION_TURTLE);
	}
    }
}
