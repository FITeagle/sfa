package org.fiteagle.north.sfa.util;

import javax.ws.rs.ForbiddenException;

import org.junit.Assert;
import org.junit.Test;

public class PrivilegeTest {
  
  String privilege_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
      + "<credential>"
      + "<type>privilege</type>"
      + "<owner_gid></owner_gid>"
      + "<owner_urn></owner_urn>"
      + "<target_urn></target_urn>"
      + "<target_gid></target_gid>"
      + "<expires></expires>"
      + "<privileges>"
      + "<privilege>"
      + "<name>*</name>"
      + "<can_delegate>false</can_delegate>"
      + "</privilege>"
      + "</privileges>"
      + "</credential>";
  
  @Test (expected = org.fiteagle.north.sfa.exceptions.ForbiddenException.class)
  public void testMissingPrivileges(){
    String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<credential>"
        + "<type>privilege</type>"
        + "<owner_gid></owner_gid>"
        + "<owner_urn></owner_urn>"
        + "<target_urn></target_urn>"
        + "<target_gid></target_gid>"
        + "<expires></expires>"
        + "<privileges></privileges>"
        + "</credential>";
    
    Privilege privilege = new Privilege(geni_value);
  }
  
  @Test (expected = org.fiteagle.north.sfa.exceptions.ForbiddenException.class)
  public void testMissingPrivilege(){
    String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<credential>"
        + "<type>privilege</type>"
        + "<owner_gid></owner_gid>"
        + "<owner_urn></owner_urn>"
        + "<target_urn></target_urn>"
        + "<target_gid></target_gid>"
        + "<expires></expires>"
        + "<privileges><privilege></privilege></privileges>"
        + "</credential>";
    
    Privilege privilege = new Privilege(geni_value);
    
  }
  
  @Test
  public void testParsePrivilegeName(){
    Privilege privilege = new Privilege(privilege_value);
    Assert.assertEquals("*", privilege.get("name"));
    
  }
  
  @Test (expected = org.fiteagle.north.sfa.exceptions.ForbiddenException.class)
  public void testMissingPrivilegeName(){
    String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<credential>"
        + "<type>privilege</type>"
        + "<owner_gid></owner_gid>"
        + "<owner_urn></owner_urn>"
        + "<target_urn></target_urn>"
        + "<target_gid></target_gid>"
        + "<expires></expires>"
        + "<privileges>"
        + "<privilege>"
        + "<can_delegate>false</can_delegate>"
        + "</privilege>"
        + "</privileges>"
        + "</credential>";
    
    Privilege privilege = new Privilege(geni_value);
  }
  
  @Test
  public void testParsePrivilegeCanDelegate(){
    Privilege privilege = new Privilege(privilege_value);
    Assert.assertEquals("false", privilege.get("can_delegate"));
    
  }
  
  @Test (expected = org.fiteagle.north.sfa.exceptions.ForbiddenException.class)
  public void testMissingPrivilegeCanDelegate(){
    String geni_value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<credential>"
        + "<type>privilege</type>"
        + "<owner_gid></owner_gid>"
        + "<owner_urn></owner_urn>"
        + "<target_urn></target_urn>"
        + "<target_gid></target_gid>"
        + "<expires></expires>"
        + "<privileges>"
        + "<privilege>"
        + "<name>*</name>"
        + "</privilege>"
        + "</privileges>"
        + "</credential>";
    
    Privilege privilege = new Privilege(geni_value);
  }
  
}
