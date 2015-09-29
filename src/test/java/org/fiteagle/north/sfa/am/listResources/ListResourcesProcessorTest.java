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
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.common.CommonTestMethods;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.am.provision.ProcessProvision;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.util.GENI_Credential;
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
    listResourcesProcessor.handleCredentials(0, ISFA_AM.METHOD_LIST_RESOURCES);
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
  
  
  @Test (expected = org.fiteagle.north.sfa.exceptions.ForbiddenException.class)
  public void testWrongPrivilege(){
    String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<signed-credential>"
        + "<credential>"
        + "<type>privilege</type>"
        + "<owner_gid></owner_gid>"
        + "<owner_urn></owner_urn>"
        + "<target_urn></target_urn>"
        + "<target_gid></target_gid>"
        + "<expires></expires>"
        + "<privileges>"
        + "<privilege>"
        + "<name>control</name>"
        + "<can_delegate>false</can_delegate>"
        + "</privilege>"
        + "</privileges>"
        + "</credential>"
        + "<signed-credential>";
    
    callAuthorizationTest(geni_value);
   
  }
  
  @Test (expected = org.fiteagle.north.sfa.exceptions.ForbiddenException.class)
  public void testMultiWrongPrivileges(){
    String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<signed-credential>"
        + "<credential>"
        + "<type>privilege</type>"
        + "<owner_gid>dummyvalue</owner_gid>"
        + "<owner_urn>dummyvlaue</owner_urn>"
        + "<target_urn>dummmyvlaue</target_urn>"
        + "<target_gid>dummyvalue</target_gid>"
        + "<expires>dummy value</expires>"
        + "<privileges>"
        + "<privilege>"
        + "<name>resolve</name>"
        + "<can_delegate>false</can_delegate>"
        + "</privilege>"
        + "<privilege>"
        + "<name>refresh</name>"
        + "<can_delegate>false</can_delegate>"
        + "</privilege>"
        + "</privileges>"
        + "</credential>"
        + "</signed-credential>"; 
    
    callAuthorizationTest(geni_value);
   
  }
  
  @Test
  public void testMultiPrivileges(){
    String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<signed-credential>"
        + "<credential>"
        + "<type>privilege</type>"
        + "<owner_gid>dummyvalue</owner_gid>"
        + "<owner_urn>dummyvlaue</owner_urn>"
        + "<target_urn>dummmyvlaue</target_urn>"
        + "<target_gid>dummyvalue</target_gid>"
        + "<expires>dummy value</expires>"
        + "<privileges>"
        + "<privilege>"
        + "<name>resolve</name>"
        + "<can_delegate>false</can_delegate>"
        + "</privilege>"
        + "<privilege>"
        + "<name>info</name>"
        + "<can_delegate>false</can_delegate>"
        + "</privilege>"
        + "</privileges>"
        + "</credential>"
        + "</signed-credential>"; 
    
    callAuthorizationTest(geni_value);
   
  }
  
  @Test
  public void testDelegatedCredentials(){
    String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<signed-credential><credential><type>privilege</type><owner_gid>dummyvalue</owner_gid><owner_urn>dummyvlaue</owner_urn>"
        + "<target_urn>urn:public:IDN+test</target_urn><target_gid>dummyvalue</target_gid><expires>dummy value</expires>"
        + "<privileges>"
        + "<privilege><name>*</name><can_delegate>false</can_delegate></privilege>"
        + "<privilege><name>info</name><can_delegate>false</can_delegate></privilege>"
        + "</privileges>"
        + "<parent>"
        + "<credential><type>privilege</type><owner_gid>dummyvalue</owner_gid><owner_urn>dummyvlaue</owner_urn>"
        + "<target_urn>urn:public:IDN+test</target_urn><target_gid>dummyvalue</target_gid><expires>dummy value</expires>"
        + "<privileges>"
        + "<privilege><name>*</name><can_delegate>true</can_delegate></privilege>"
        + "<privilege><name>info</name><can_delegate>true</can_delegate></privilege>"
        + "</privileges>"
        + "</credential>"
        + "</parent>"
        + "</credential>"
        + "</signed-credential>"; 
    
    callAuthorizationTest(geni_value);
  }
  
  @Test (expected = org.fiteagle.north.sfa.exceptions.ForbiddenException.class)
  public void testBadDelegatedCredentials(){
    String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<signed-credential><credential><type>privilege</type><owner_gid>dummyvalue</owner_gid><owner_urn>dummyvlaue</owner_urn>"
        + "<target_urn>urn:public:IDN+test</target_urn><target_gid>dummyvalue</target_gid><expires>dummy value</expires>"
        + "<privileges>"
        + "<privilege><name>*</name><can_delegate>false</can_delegate></privilege>"
        + "</privileges>"
        + "<parent>"
        + "<credential><type>privilege</type><owner_gid>dummyvalue</owner_gid><owner_urn>dummyvlaue</owner_urn>"
        + "<target_urn>urn:public:IDN+test</target_urn><target_gid>dummyvalue</target_gid><expires>dummy value</expires>"
        + "<privileges>"
        + "<privilege><name>*</name><can_delegate>false</can_delegate></privilege>"
        + "</privileges>"
        + "</credential>"
        + "</parent>"
        + "</credential>"
        + "</signed-credential>"; 
    
    callAuthorizationTest(geni_value);
  }
  
  
  private void callAuthorizationTest(String geni_value){
    
    Map<String, String> credentialsMap = new HashMap<String, String>();
    credentialsMap.put("geni_type", "geni_sfa");
    credentialsMap.put("geni_version", "2");
    credentialsMap.put("geni_value", geni_value);
    
    GENI_Credential geni_Credential = new GENI_Credential(credentialsMap);
    List<GENI_Credential> credential_list = new LinkedList<>();
    credential_list.add(geni_Credential);
    this.prepareTest();
    this.prepareParameters();
    listResourcesProcessor = new ListResourcesProcessor(parameter);
    listResourcesProcessor.checkCredentials(credential_list, ISFA_AM.METHOD_LIST_RESOURCES);
    
  }
  
}
