package org.fiteagle.north.sfa.am.dm;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.IResourceRepository;
import org.fiteagle.api.core.usermanagement.Node;
import org.fiteagle.api.core.usermanagement.User;
import org.fiteagle.api.core.usermanagement.UserPublicKey;



@Path("/sfarest")
public class SFArest {

	@Inject
	private JMSContext context;
	@Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
	private Topic topic;
	
	public SFArest(){
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * curl -v http://localhost:8080/sfa/sfarest/listResources
	 */
	//@Produces("text/turtle")
	@GET
	@Path("listResources")
	public void listResources() {
		final int TIMEOUT = 5000;
		final Message message = this.context.createMessage();
		 try {
			message.setStringProperty(IMessageBus.TYPE_REQUEST, IResourceRepository.LIST_RESOURCES);
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
