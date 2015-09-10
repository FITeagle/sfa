package org.fiteagle.north.sfa.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.fiteagle.north.sfa.exceptions.ForbiddenException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author AlaaAlloush
 *
 */
public class Privilege extends HashMap<String, String>{
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private final static Logger LOGGER = Logger.getLogger(Privilege.class.getName());
  
  final String NAME = "name";
  final String CAN_DELEGATE = "can_delegate";
  final String PRIVILEGE = "privilege";
  
  public Privilege(String geni_value){
    parse_credential_privileges(geni_value);
  }
  
  private void setNAME(String name){
    this.put(NAME, name);
  }
  
  public String getNAME(){
    return this.get(NAME);
  }
  
  private void setCanDelegate(String can_delegate){
    this.put(CAN_DELEGATE, can_delegate);
  }
  
  public String getCanDelegate(){
    return this.get(CAN_DELEGATE);
  }
  
  private void parse_credential_privileges(String geni_value){
    DocumentBuilder builder;
    try {
      builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
     
    StringReader stringReader = new StringReader(geni_value);
    org.xml.sax.InputSource is = new org.xml.sax.InputSource(stringReader);
    Document document;
  
    document = builder.parse(is);
    
    NodeList nodeList = document.getElementsByTagName(PRIVILEGE);
    if(nodeList.getLength() == 0){
      throw new ForbiddenException("Credentials don't have privileges");
    }
    else {
      for (int i = 0; i < nodeList.getLength(); i++) {
        Element element = (Element) nodeList.item(i);

          setNAME(parse_privilege(element, NAME));
          setCanDelegate(parse_privilege(element, CAN_DELEGATE));

      }
    }
    
    }catch (ParserConfigurationException e) {
      LOGGER.log(Level.SEVERE, "", e);
    }catch (SAXException e) {
      LOGGER.log(Level.SEVERE, "", e);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "", e);
    }
      
  }
  
  private String parse_privilege(Element element, String node){
    NodeList name = element.getElementsByTagName(node);
    if(name.getLength() != 0){
      Element line = (Element) name.item(0);
      return line.getTextContent();
    } 
    else {
      throw new ForbiddenException("privileges don't have " + node);
    }
  }
  
}
