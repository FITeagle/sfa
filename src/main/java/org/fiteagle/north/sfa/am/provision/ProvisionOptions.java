package org.fiteagle.north.sfa.am.provision;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;

public class ProvisionOptions {
  
  private final static Logger LOGGER = Logger.getLogger(ProvisionOptions.class.getName());
  
  private Map<String, ?> options;
  private Map<String, String> geni_rspec_version;
  private List<Map<String, ?>> geni_users;
  
  private String geni_best_effort;
  private String geni_end_time;
  private String user;
  
  private List<String> keys;
  
  public ProvisionOptions(final Map<String, ?> options){
    this.options = options;
    set_geni_rspec_version();
    set_geni_best_effort();
    set_geni_end_time();
    set_geni_users();
  }
  
  @SuppressWarnings("unchecked")
  private void set_geni_rspec_version() {
    this.geni_rspec_version = (Map<String, String>) this.options.get(ISFA_AM.GENI_RSPEC_VERSION);
  }
  
  public Map<String, String> get_geni_rspec_version(){
    return this.geni_rspec_version;
  }
  
  private void set_geni_best_effort(){
    this.geni_best_effort = (String) this.options.get(ISFA_AM.GENI_BEST_EFFORT);
  }
  
  public String get_geni_best_effort(){
    return this.geni_best_effort;
  }
  
  private void set_geni_end_time(){
    this.geni_end_time = (String) this.options.get(ISFA_AM.GENI_END_TIME);
  }
  
  public String get_geni_end_time(){
    return this.geni_end_time;
  }
  
  @SuppressWarnings("unchecked")
  private void set_geni_users(){
    this.geni_users = (List<Map<String, ?>>) options.get(ISFA_AM.GENI_USERS);
  }
  
  public List<Map<String, ?>> get_geni_users(){
    return this.geni_users;
  }
  
  private void setUser(String urn){
    String[] URN = urn.split("\\+");
    if(URN.length != 4) { throw new BadArgumentsException("urn " + urn + " in option parameter should contain user name"); }
    this.user = URN[3];
  }
  
  public String getUser(){
    return this.user;
  }
  
  private void setKeys(List<String> keys){
    this.keys = keys;
  }
  
  public List<String> getKeys(){
    return this.keys;
  }
  
  public void parse_geni_users(){
    
    for(Map<String, ?> users: get_geni_users()){
      setUser((String) users.get(ISFA_AM.URN));
      LOGGER.log(Level.INFO, "user name is " + getUser());
      
      @SuppressWarnings("unchecked")
      List<String> keys = (List<String>) users.get(ISFA_AM.KEYS);
      if(keys.isEmpty()) {
          throw new BadArgumentsException("geni_users field doesn't contain keys !!");
        }
      LOGGER.log(Level.INFO, "user's public key is " + keys.get(0));
      setKeys(keys);
      }
  }
  
}
