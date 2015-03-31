package org.fiteagle.north.sfa.am.status;

import static org.junit.Assert.assertFalse;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.fiteagle.north.sfa.am.common.CommonTestMethods;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;


public class StatusProcessorTest extends CommonTestMethods{
  
  StatusProcessor statusProcessor;
  
  @Test (expected = BadArgumentsException.class)
  public void handleCredentialsTest(){

    this.prepareParameters();
    statusProcessor = new StatusProcessor(parameter);
    statusProcessor.handleCredentials(1);
  }
  
  @Test
  public void testGetStates() throws UnsupportedEncodingException{

    this.prepareTest();
    this.prepareParameters();
    statusProcessor = new StatusProcessor(parameter);
    statusProcessor.parseURNList();
    statusProcessor.setSender(sender);
    Model model = statusProcessor.getStates();
    assertFalse(model.isEmpty());
  }
  
  @Test (expected = SearchFailedException.class)
  public void createResponseTest() throws UnsupportedEncodingException{
    
    this.prepareTest();
    this.prepareParameters();
    statusProcessor = new StatusProcessor(parameter);
    statusProcessor.parseURNList();
    statusProcessor.setSender(sender);
    Model model = statusProcessor.getStates();
    final HashMap<String, Object> result = new HashMap<>();
    statusProcessor.createResponse(result, model);
  }
  
}
