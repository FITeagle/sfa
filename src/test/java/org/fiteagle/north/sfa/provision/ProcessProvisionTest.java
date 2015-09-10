package org.fiteagle.north.sfa.provision;

import info.openmultinet.ontology.vocabulary.Omn;
import static org.junit.Assert.assertFalse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.SFA_AM;
import org.fiteagle.north.sfa.am.common.CommonTestMethods;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.am.provision.ProcessProvision;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.fiteagle.north.sfa.util.GENI_Credential;
import org.fiteagle.north.sfa.util.URN;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ProcessProvisionTest extends CommonTestMethods{
  
  ProcessProvision processProvision;
  Map<String, Object> options = new HashMap<>();
  
  @Test (expected = BadArgumentsException.class)
  public void handleCredentialsTest(){
    
    this.prepareParameters();
    processProvision = new ProcessProvision(parameter);
    processProvision.handleCredentials(1, ISFA_AM.METHOD_PROVISION);
  }
  
  @Test 
  public void provisionInstancesTest() throws UnsupportedEncodingException{

    this.prepareTest();
    this.prepareParameters(); 
    createOptions();
    parameter.add(options);
    processProvision = new ProcessProvision(parameter);
    processProvision.parseURNList();
    processProvision.handleOptions();
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
    createOptions();
    parameter.add(options);
    processProvision = new ProcessProvision(parameter);
    processProvision.parseURNList();
    processProvision.handleOptions();
    processProvision.setSender(sender);
    Model model = processProvision.provisionInstances();
    final HashMap<String, Object> result = new HashMap<>();
    processProvision.createResponse(result, model);
  }
  
  @Test
  public void authorizationTest(){
    Map<String, String> credentialsMap = new HashMap<String, String>();
    
  }
  
  @Test (expected = org.fiteagle.north.sfa.exceptions.ForbiddenException.class)
  public void testProvisionAuthorization(){
    String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<credential>"
        + "<type>privilege</type>"
        + "<owner_gid></owner_gid>"
        + "<owner_urn></owner_urn>"
        + "<target_urn></target_urn>"
        + "<target_gid></target_gid>"
        + "<expires></expires>"
        + "<privileges>"
        + "<privilege>"
        + "<name>info</name>"
        + "<can_delegate>false</can_delegate>"
        + "</privilege>"
        + "</privileges>"
        + "</credential>";
    Map<String, String> credentialsMap = new HashMap<String, String>();
    credentialsMap.put("geni_type", "geni_sfa");
    credentialsMap.put("geni_version", "2");
    credentialsMap.put("geni_value", geni_value);
    
    GENI_Credential geni_Credential = new GENI_Credential(credentialsMap);
    List<GENI_Credential> credential_list = new LinkedList<>();
    credential_list.add(geni_Credential);
    this.prepareTest();
    this.prepareParameters();
    createOptions();
    parameter.add(options);
    processProvision = new ProcessProvision(parameter);
    processProvision.checkCredentials(credential_list, ISFA_AM.METHOD_PROVISION);
   
  }
  
  
  private void createOptions(){
    List<Map<String, Object>> geni_users = new LinkedList<Map<String, Object>>();
    Map<String, Object> users = new HashMap<>();
    users.put(ISFA_AM.URN, "urn:publicid:IDN+geni.net:gcf+user+testing");
    List<String> keys = new LinkedList<>();
    keys.add("ssh-rsa hsdhsiudthsdkjfghsdjhoiuthsdkjgd");
    users.put(ISFA_AM.KEYS, keys);
    geni_users.add(users);
    this.options.put(ISFA_AM.GENI_USERS, geni_users);
  }
  
}
