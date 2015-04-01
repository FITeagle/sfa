package org.fiteagle.north.sfa.am.getVersion;

import static org.junit.Assert.assertFalse;

import java.util.HashMap;

import info.openmultinet.ontology.vocabulary.Omn;

import org.easymock.EasyMock;
import org.fiteagle.north.sfa.am.common.CommonTestMethods;
import org.fiteagle.north.sfa.exceptions.EmptyReplyException;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ProcessGetVersionTest extends CommonTestMethods{
  
  ProcessGetVersion processGetVersion;
  
  @Test
  public void setTestbedDescriptionTest() {
    Model returnModel = createTestModel();
    EasyMock.expect(sender.sendSPARQLQueryRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
        .andReturn(returnModel);
    EasyMock.replay(sender);
    processGetVersion = new ProcessGetVersion(parameter);
    processGetVersion.setSender(sender);
    Model model = processGetVersion.getTestbedDescription();
    assertFalse(model.isEmpty());
  }
  
  @Test(expected = EmptyReplyException.class)
  public void parseTestbedDescriptionTest() {
    Model testModel = ModelFactory.createDefaultModel();
    this.prepareParameters();
    processGetVersion = new ProcessGetVersion(parameter);
    assertFalse(processGetVersion.parseTestbedDescription(testModel).isEmpty());
  }
  
  @Test
  public void createResponseTest() {
    final HashMap<String, Object> result = new HashMap<>();
    String testbedDescription = "testbed description";
    this.prepareParameters();
    processGetVersion = new ProcessGetVersion(parameter);
    processGetVersion.createResponse(result, testbedDescription);
  }
  
  
}
