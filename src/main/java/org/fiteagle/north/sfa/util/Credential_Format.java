package org.fiteagle.north.sfa.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.model.InputSource;
import org.apache.xerces.parsers.DOMParser;
import org.fiteagle.north.sfa.exceptions.ForbiddenException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author AlaaAlloush
 *
 */
public class Credential_Format extends HashMap<String, Object> {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private final static Logger LOGGER = Logger.getLogger(Credential_Format.class.getName());
  
  final String ID = "id";
  final String TYPE = "type";
  final String SERIAL = "serial";
  final String OWNER_GID = "owner_gid";
  final String OWNER_URN = "owner_urn";
  final String TARGET_URN = "target_urn";
  final String TARGET_GID = "target_gid";
  final String EXPIRES = "expires";
  final String PRIVILEGE = "privilege";
  final String SIGNATURES = "signatures";
  final String CREDENTIAL = "credential";
  
  final String GENI_VALUE = "geni_value";
  
  public Credential_Format(String geni_value){
    this.put(GENI_VALUE, geni_value);
    this.parseType();
    this.parseOwnerGid();
    this.parseOwnerURN();
    this.parseTargetURN();
    this.parseTargetGID();
    this.parseExpires();
    this.parsePrivileges();
  }
  
  
  private void parseID(){
    
  }
  
  public String getID(){
    return this.get(ID).toString();
  }
  
  private void parseType(){
    parse_credential_value(TYPE);
  }
  
  public String getType(){
    return this.get(TYPE).toString();
  }
  
  private void parseSerial(String serial){
    parse_credential_value(serial);
  }
  
  public String getSerial(){
    return this.get(SERIAL).toString();
  }
  
  private void parseOwnerGid(){
    parse_credential_value(OWNER_GID);
  }
  
  public String getOwnerGid(){
    return this.get(OWNER_GID).toString();
  }
  
  private void parseOwnerURN(){
    parse_credential_value(OWNER_URN);
  }
  
  public String getOwnerURN(){
    return this.get(OWNER_URN).toString();
  }
  
  private void parseTargetURN(){
    parse_credential_value(TARGET_GID);
  }
  
  public String getTargetURN(){
    return this.get(TARGET_URN).toString();
  }
  
  private void parseTargetGID(){
    parse_credential_value(TARGET_GID);
  }
  
  public String getTargetGID(){
    return this.get(TARGET_GID).toString();
  }
  
  private void parseExpires(){
    parse_credential_value(EXPIRES);
  }
  
  public String getExpires(){
    return this.get(EXPIRES).toString();
  }
  
  private void parsePrivileges(){
    Privilege privilege = new Privilege(this.get(GENI_VALUE).toString());
    
    this.put(PRIVILEGE, privilege);
    
  }
  
  public Privilege getPrivileges(){
    return (Privilege) this.get(PRIVILEGE);
  }
  
  private void parseSignatures(){
    parse_credential_value(SIGNATURES);
  }
  
  public String getSignatures(){
    return this.get(SIGNATURES).toString();
  }
  
  public boolean checkPrivilegeType(){
    if(PRIVILEGE.equals(this.get(TYPE).toString()))
        return true;
    else return false;
  }
  
  private void parse_credential_value(String node){
    DocumentBuilder builder;
    try {
      builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      
      if(this.get(GENI_VALUE) == null || this.get(GENI_VALUE).toString().isEmpty()){
        throw new ForbiddenException("Credential don't have geni_value");
      }
      String value = this.get(GENI_VALUE).toString();
      
      StringReader stringReader = new StringReader(value);
      org.xml.sax.InputSource is = new org.xml.sax.InputSource(stringReader);
      Document document;
  
      document = builder.parse(is);
    
      NodeList nodeList = document.getElementsByTagName(node);
      if(nodeList.getLength() != 0){
        Element element = (Element) nodeList.item(0);
      
        this.put(node, element.getTextContent());
        }
      else {
        throw new ForbiddenException("Credentials don't contain " + node);
        }
    
    
    }catch (ParserConfigurationException e) {
      LOGGER.log(Level.SEVERE, "", e);
    }catch (SAXException e) {
      LOGGER.log(Level.SEVERE, "", e);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "", e);
    }
      
  }
  
  
}
