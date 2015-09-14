package org.fiteagle.north.sfa.util;


import java.util.logging.Logger;

import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class Privilege{
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private final static Logger LOGGER = Logger.getLogger(Privilege.class.getName());
  
  private String privilege_name;
  private String privilege_can_delegate;
  
  final String NAME = "name";
  final String CAN_DELEGATE = "can_delegate";
  

  @XmlElement(name=NAME)
  private void setName(String name){
    this.privilege_name = name;
  }
  
  public String getName(){
    return this.privilege_name;
  }
  
  @XmlElement(name=CAN_DELEGATE)
  private void setCanDelegate(String can_delegate){
    this.privilege_can_delegate = can_delegate;
  }
  
  public String getCanDelegate(){
    return this.privilege_can_delegate;
  }
  
  
}
