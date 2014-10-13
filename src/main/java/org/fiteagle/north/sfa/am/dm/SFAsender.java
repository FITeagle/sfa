package org.fiteagle.north.sfa.am.dm;

import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import org.fiteagle.api.core.IMessageBus;

public class SFAsender {

	@Inject
	private JMSContext context;
	@Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
	private Topic topic;

	public static String LIST_RESOURCES = "listResources";
	
	public SFAsender() {
		
	}
	
	public String getListResourcesValue() {

		final int TIMEOUT = 5000;
		String result = "";
		System.out.println("in listResources() method");
		final Message message = this.context.createMessage();
		try {
			message.setStringProperty(IMessageBus.TYPE_REQUEST,
					LIST_RESOURCES);
			message.setStringProperty(IMessageBus.SERIALIZATION, "TURTLE");
			message.setStringProperty(IMessageBus.QUERY,
					"SELECT * {?s ?p ?o} LIMIT 100");
			message.setJMSCorrelationID(UUID.randomUUID().toString());
			System.out
					.println("sending a request from SFA to resource repository");
			
			this.context.createProducer().send(this.topic, message);

			final String filter = "JMSCorrelationID='"
					+ message.getJMSCorrelationID() + "'";
			Message rcvMessage = this.context
					.createConsumer(this.topic, filter).receive(TIMEOUT);
			
			if (null != rcvMessage) {
				result = rcvMessage.getStringProperty(IMessageBus.RESULT);
				System.out
						.println("the received result for listResources method is "
								+ result);
			} else
				System.out
						.println("the received result for listResources method is empty !");

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}
}
