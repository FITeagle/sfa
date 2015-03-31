package org.fiteagle.north.sfa.am.listResources;

import static org.junit.Assert.assertFalse;
import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.vocabulary.Omn;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;

import org.easymock.EasyMock;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ListResourcesProcessorTest {
  
  ListResourcesProcessor listResourcesProcessor;
  SFA_AM_MDBSender sender;
  
  @Before
  public void initialize() {
    sender = EasyMock.createMock(SFA_AM_MDBSender.class);
  }
  
  @Test (expected = BadArgumentsException.class)
  public void handleCredentialsTest() {
    List<Object> parameter = new LinkedList<>();
    List<Map<String, ?>> dummyCredentials = new LinkedList<>();
    parameter.add(dummyCredentials);
    listResourcesProcessor = new ListResourcesProcessor(parameter);
    listResourcesProcessor.handleCredentials();
  }
  

  @Test
  public void parseOptionsParametersTest() {
    List<Object> parameter = new LinkedList<>();
    Map<String, ?> options = new HashMap<>();
    parameter.add("asd");
    parameter.add(options);
    listResourcesProcessor = new ListResourcesProcessor(parameter);
    listResourcesProcessor.parseOptionsParameters();;
  }
  
  @Test
  public void listResourcesTest() throws UnsupportedEncodingException, JMSException {
    List<Object> parameter = new LinkedList<>();
    Model returnModel = createTestModel();
    EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(returnModel);
    EasyMock.replay(sender);
    listResourcesProcessor = new ListResourcesProcessor(parameter);
    listResourcesProcessor.setSender(sender);
    Model model = listResourcesProcessor.listResources();
    assertFalse(model.isEmpty());
    
  }
  
  @Test
  public void createResponseTest() throws UnsupportedEncodingException, JAXBException, InvalidModelException{
    List<Object> parameter = new LinkedList<>();
    Model returnModel = createTestModel();
    listResourcesProcessor = new ListResourcesProcessor(parameter);
    HashMap<String, Object> result = new HashMap<>();
    listResourcesProcessor.createResponse(result, returnModel);
    
  }
  
  private Model createTestModel() {
    Model returnModel = ModelFactory.createDefaultModel();
    Resource resource = returnModel.createResource("http://test");
    resource.addProperty(RDF.type, Omn.Resource);
    return returnModel;
  }
}
