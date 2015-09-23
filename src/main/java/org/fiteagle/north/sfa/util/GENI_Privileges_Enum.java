package org.fiteagle.north.sfa.util;

public enum GENI_Privileges_Enum {
  
  LISTRESOURCES("*,info"),
  ALLOCATE("*"),
  DESCRIBE("*,info"),
  RENEW("*,refresh"),
  PROVISION("*,control"),
  STATUS("*,info,pi"),
  PERFORMOPERATIONALACTION("*,control"),
  DELETE("*,control,resolve,instantiate"),
  SHUTDOWN("*,control,pi");
  
  String privileges;
  
  private GENI_Privileges_Enum(String privileges){
    this.privileges = privileges;
  }
  
  public String getPrivileges(){
    return this.privileges;
  }
  
  
}
