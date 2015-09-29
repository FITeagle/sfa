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
  
  private Credential credential;
  
//  private Credentials geni_credentials;

  @XmlElement(name=CREDENTIAL)
  private void setCredentials(Credential geni_credentials){
    this.credential = geni_credentials;
  }
  
  public Credential getCredentials(){
    return this.credential;
  }
  
  
  // TODO: implement signatures
  
}
