package org.fiteagle.north.sfa.util;


import java.util.logging.Logger;

import javax.xml.bind.annotation.*;


@XmlRootElement(name="signed-credential")
@XmlAccessorType(XmlAccessType.FIELD)
public class Signed_Credential{
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private final static Logger LOGGER = Logger.getLogger(Signed_Credential.class.getName());
  
  final String CREDENTIAL = "credential";
  final String SIGNATURES = "signatures";
  
  private Credential credentials;

  @XmlElement(name=CREDENTIAL)
  private void setCredential(Credential credential){
    this.credentials = credential;
  }
  
  public Credential getCredential(){
    return this.credentials;
  }
  
  
}
