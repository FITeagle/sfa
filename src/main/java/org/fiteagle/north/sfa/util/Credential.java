package org.fiteagle.north.sfa.util;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Credential {
  
final String TYPE = "type";
final String OWNER_GID = "owner_gid";
final String OWNER_URN = "owner_urn";
final String TARGET_URN = "target_urn";
final String TARGET_GID = "target_gid";
final String EXPIRES = "expires";
final String PRIVILEGE = "privilege";
final String PRIVILEGES = "privileges";
final String PARENT = "parent";
  
  private String credential_type;
  private String owner_gid;
  private String owner_urn;
  private String target_urn;
  private String target_gid;
  private String credential_expiration;
  private List<Privilege> credential_privileges;
  private Credentials parent_;
  
  @XmlElement(name=TYPE)
  private void setType(String type){
    this.credential_type = type;
  }
  
  public String getType(){
    return this.credential_type;
  }
  
  @XmlElement(name=OWNER_GID)
  private void setOwnerGid(String owner_gid){
    this.owner_gid = owner_gid;
  }
  
  public String getOwnerGid(){
    return this.owner_gid;
  }
  
  @XmlElement(name=OWNER_URN)
  private void setOwnerURN(String owner_urn){
    this.owner_urn = owner_urn;
  }
  
  public String getOwnerURN(){
    return this.owner_urn;
  }
  
  @XmlElement(name=TARGET_URN)
  private void setTargetURN(String target_urn){
    this.target_urn = target_urn;
  }
  
  public String getTargetURN(){
    return this.target_urn;
  }
  
  @XmlElement(name=TARGET_GID)
  private void setTargetGID(String target_gid){
    this.target_gid = target_gid;
  }
  
  public String getTargetGID(){
    return this.target_gid;
  }
  
  @XmlElement(name=EXPIRES)
  private void setExpires(String expires){
    this.credential_expiration = expires;
  }
  
  public String getExpires(){
    return this.credential_expiration;
  }
  
  @XmlElementWrapper(name=PRIVILEGES)
  @XmlElement(name=PRIVILEGE)
  private void setPrivilege(List<Privilege> credential_privilege){
    this.credential_privileges = credential_privilege;
  }
  
  public List<Privilege> getPrivilege(){
    return this.credential_privileges;
  }
  
  
  @XmlElement(name=PARENT)
  private void setParent(Credentials parent_credentials){
    this.parent_ = parent_credentials;
  }
  
  public Credentials getParent(){
    return this.parent_;
  }
  
}
