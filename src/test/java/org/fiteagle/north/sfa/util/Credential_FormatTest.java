package org.fiteagle.north.sfa.util;

import java.io.IOException;
import java.io.StringReader;

import javax.ws.rs.ForbiddenException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Credential_FormatTest {
  
  String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
      + "<credential>"
      + "<type>privilege</type>"
      + "<owner_gid>dummy value</owner_gid>"
      + "<owner_urn>dummy vlaue</owner_urn>"
      + "<target_urn>dummmy vlaue</target_urn>"
      + "<target_gid>dummy value</target_gid>"
      + "<expires>dummy value</expires>"
      + "<privileges>"
      + "<privilege>"
      + "<name>*</name>"
      + "<can_delegate>false</can_delegate>"
      + "</privilege>"
      + "</privileges>"
      + "</credential>";
  
 @Test (expected = org.fiteagle.north.sfa.exceptions.ForbiddenException.class)
 public void testEmptyCredentialValue(){
   Credential_Format credential_Format = new Credential_Format("");
 }
  
 
 @Test 
 public void testPrivilegeType(){
   Credential_Format credential_Format = new Credential_Format(geni_value);
   Assert.assertEquals(true, credential_Format.checkPrivilegeType());
 }
 
 @Test (expected = org.fiteagle.north.sfa.exceptions.ForbiddenException.class)
 public void testMissingOwnerURN(){
   String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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
       + "</credential>";
   Credential_Format credential_Format = new Credential_Format(geni_value);
   
 }
 
}

