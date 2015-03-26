package org.fiteagle.north.sfa.am.getVersion;

import static org.junit.Assert.assertFalse;

import java.util.HashMap;

import info.openmultinet.ontology.vocabulary.Omn;

import org.easymock.EasyMock;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.exceptions.EmptyReplyException;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ProcessGetVersionTest {
  
  ProcessGetVersion processGetVersion;
  SFA_AM_MDBSender sender;
  
  @Before
  public void initialize() {
    sender = EasyMock.createMock(SFA_AM_MDBSender.class);
  }
  
  @Test
  public void setTestbedDescriptionTest() {
    Model returnModel = createTestModel();
    EasyMock.expect(sender.sendSPARQLQueryRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
        .andReturn(returnModel);
    EasyMock.replay(sender);
    processGetVersion = new ProcessGetVersion();
    processGetVersion.setSender(sender);
    Model model = processGetVersion.getTestbedDescription();
    assertFalse(model.isEmpty());
  }
  
  @Test(expected = EmptyReplyException.class)
  public void parseTestbedDescriptionTest() {
    Model testModel = ModelFactory.createDefaultModel();
    processGetVersion = new ProcessGetVersion();
    String testbedDescription = processGetVersion.parseTestbedDescription(testModel);
  }
  
  @Test
  public void addValueTest() {
    final HashMap<String, Object> result = new HashMap<>();
    String testbedDescription = "testbed description";
    processGetVersion = new ProcessGetVersion();
    processGetVersion.addValue(result, testbedDescription);
  }
  
  private Model createTestModel() {
    Model returnModel = ModelFactory.createDefaultModel();
    Resource resource = returnModel.createResource("http://test");
    resource.addProperty(RDF.type, Omn.Resource);
    return returnModel;
  }
  
}
