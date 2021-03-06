package org.fiteagle.north.sfa.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ForbiddenException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Signed_Credential_Test {
  
  
  @Test
  public void testPrivilegeType() throws JAXBException{
      
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
          + "<name>*</name>"
          + "<can_delegate>false</can_delegate>"
          + "</privilege>"
          + "</privileges>"
          + "</credential>"
          + "</signed-credential>"; 
      
      
      Assert.assertEquals("privilege", parseXML(geni_value).getCredentials().getType());
  
  }
  
 @Test (expected = org.fiteagle.north.sfa.exceptions.ForbiddenException.class)
 public void testEmptyCredentialValue(){
   Map<String, ?> credential = new HashMap<>();
   GENI_Credential credential_Format = new GENI_Credential(credential);
 }
  
 
 
 @Test
 public void testMissingOwnerURN() throws JAXBException{
   
   String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
       + "<signed-credential>"
       + "<credential>"
       + "<type>privilege</type>"
       + "<owner_gid>dummy value</owner_gid>"
       + "<target_urn>dummmy vlaue</target_urn>"
       + "<target_gid>dummy value</target_gid>"
       + "<expires>dummy value</expires>"
       + "<privileges>"
       + "<privilege>"
       + "<name>*</name>"
       + "<can_delegate>false</can_delegate>"
       + "</privilege>"
       + "</privileges>"
       + "</credential>"
       + "</signed-credential>"; 
   

   Assert.assertNull(parseXML(geni_value).getCredentials().getOwnerURN());
   
 }
 
 @Test
 public void testMultiPrivilege() throws JAXBException{
     
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
         + "<name>*</name>"
         + "<can_delegate>false</can_delegate>"
         + "</privilege>"
         + "<privilege>"
         + "<name>info</name>"
         + "<can_delegate>false</can_delegate>"
         + "</privilege>"
         + "</privileges>"
         + "</credential>"
         + "</signed-credential>"; 
     
     
     Assert.assertEquals("privilege", parseXML(geni_value).getCredentials().getType());
     
 }
 
 @Test
 public void testDelegatedCredentails() throws JAXBException{
     
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
         + "<name>*</name>"
         + "<can_delegate>false</can_delegate>"
         + "</privilege>"
         + "<privilege>"
         + "<name>info</name>"
         + "<can_delegate>false</can_delegate>"
         + "</privilege>"
         + "</privileges>"
         + "<parent>"
         + "<credential>"
         + "<type>privilege</type>"
         + "<owner_gid>dummyvalue</owner_gid>"
         + "<owner_urn>dummyvlaue</owner_urn>"
         + "<target_urn>dummyvalue</target_urn>"
         + "<target_gid>dummyvalue</target_gid>"
         + "<expires>dummy value</expires>"
         + "<privileges>"
         + "<privilege>"
         + "<name>*</name>"
         + "<can_delegate>false</can_delegate>"
         + "</privilege>"
         + "<privilege>"
         + "<name>info</name>"
         + "<can_delegate>false</can_delegate>"
         + "</privilege>"
         + "</privileges>"
         + "</credential>"
         + "</parent>"
         + "</credential>"
         + "</signed-credential>"; 
     
     
     Assert.assertEquals("privilege", parseXML(geni_value).getCredentials().getParent().getParent_credential().getType());
     
 }
 
   private Signed_Credential parseXML(String geni_value) throws JAXBException{
    
       JAXBContext jc = JAXBContext.newInstance(Signed_Credential.class);
       Unmarshaller unmarshaller = jc.createUnmarshaller();
       
       StringReader stringReader = new StringReader(geni_value);
       org.xml.sax.InputSource is = new org.xml.sax.InputSource(stringReader);
       
       Signed_Credential signed_credential = (Signed_Credential) unmarshaller.unmarshal(is);
       
     return signed_credential;
     
   }
}

