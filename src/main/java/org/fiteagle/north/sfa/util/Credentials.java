package org.fiteagle.north.sfa.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Credentials {
  
 protected Credential parent_cred;
 
 @XmlElement(name="credential")
 private void setParent_credential(Credential parent){
   this.parent_cred = parent;
 }
 
 public Credential getParent_credential(){
   return this.parent_cred;
 }
 
}
