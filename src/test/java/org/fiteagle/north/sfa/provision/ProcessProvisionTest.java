package org.fiteagle.north.sfa.provision;

import info.openmultinet.ontology.vocabulary.Omn;
import static org.junit.Assert.assertFalse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.easymock.EasyMock;
import org.fiteagle.north.sfa.am.common.CommonTestMethods;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.am.provision.ProcessProvision;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.fiteagle.north.sfa.util.URN;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ProcessProvisionTest extends CommonTestMethods{
  
  ProcessProvision processProvision;
  
  @Test (expected = BadArgumentsException.class)
  public void handleCredentialsTest(){
    
    this.prepareParameters();
    processProvision = new ProcessProvision(parameter);
    processProvision.handleCredentials(1);
  }
  
  @Test 
  public void provisionInstancesTest() throws UnsupportedEncodingException{

    this.prepareTest();
    this.prepareParameters(); 
    processProvision = new ProcessProvision(parameter);
    processProvision.parseURNList();
    processProvision.setSender(sender);
    Model model = processProvision.provisionInstances();
    assertFalse(model.isEmpty());
  }
  
  @Test (expected = BadArgumentsException.class)
  public void provisionEmptyList(){
    test_urn = null;
    parameter.add(test_urn);
    processProvision = new ProcessProvision(parameter);
    processProvision.parseURNList();
  }
  
  @Test (expected = SearchFailedException.class)
  public void createResponseTest() throws UnsupportedEncodingException{
    this.prepareTest();
    this.prepareParameters();
    processProvision = new ProcessProvision(parameter);
    processProvision.parseURNList();
    processProvision.setSender(sender);
    Model model = processProvision.provisionInstances();
    final HashMap<String, Object> result = new HashMap<>();
    processProvision.createResponse(result, model);
  }
  
}
