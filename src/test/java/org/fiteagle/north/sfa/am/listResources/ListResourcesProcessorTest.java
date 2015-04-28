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
import org.fiteagle.north.sfa.am.common.CommonTestMethods;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ListResourcesProcessorTest extends CommonTestMethods{
  
  ListResourcesProcessor listResourcesProcessor;
 
  
  @Test (expected = BadArgumentsException.class)
  public void handleCredentialsTest() {
    parameter.add(credentials);
    listResourcesProcessor = new ListResourcesProcessor(parameter);
    listResourcesProcessor.handleCredentials(0);
  }
  

  @Test
  public void parseOptionsParametersTest() {
    Map<String, ?> options = new HashMap<>();
    parameter.add(credentials);
    parameter.add(options);
    listResourcesProcessor = new ListResourcesProcessor(parameter);
    listResourcesProcessor.parseOptionsParameters();
  }
  
  @Test
  public void listResourcesTest() throws UnsupportedEncodingException, JMSException {
    
    this.prepareTest();
    listResourcesProcessor = new ListResourcesProcessor(parameter);
    listResourcesProcessor.setSender(sender);
    Model model = listResourcesProcessor.listResources();
    assertFalse(model.isEmpty());
    
  }
  
  @Test
  public void createResponseTest() throws UnsupportedEncodingException, JAXBException, InvalidModelException{
    Model returnModel = createTestModel();
    listResourcesProcessor = new ListResourcesProcessor(parameter);
    HashMap<String, Object> result = new HashMap<>();
    listResourcesProcessor.createResponse(result, returnModel);
    
  }
  
}