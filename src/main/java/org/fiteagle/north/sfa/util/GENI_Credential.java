package org.fiteagle.north.sfa.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dne on 20.01.15.
 */
public class GENI_Credential extends HashMap<String, Object>{

      final String GENI_TYPE = "geni_type";
      final String GENI_VERSION = "geni_version";
      final String GENI_VALUE = "geni_value";
      
      final String CREDENTIAL_FORMAT = "credential_format";

    public GENI_Credential(Map<String, ?> credMap){
        set_geni_type((String) credMap.get(GENI_TYPE));
        set_geni_version((String) credMap.get(GENI_VERSION));
        set_geni_value((String) credMap.get(GENI_VALUE));
        parse_geni_value((String) credMap.get(GENI_VALUE));
    }

    public String get_geni_type(){
        return this.get(GENI_TYPE).toString();
    }
    private void set_geni_type(String geni_type){
        this.put(GENI_TYPE, geni_type);
    }

    public String get_geni_version(){
        return this.get(GENI_VERSION).toString();
    }

    private void set_geni_version(String geni_version){
        this.put(GENI_VERSION, geni_version);
    }

    public String get_geni_value(){
        return this.get(GENI_VALUE).toString();
    }

    private void set_geni_value(String geni_value){
        this.put(GENI_VALUE, geni_value);
    }
    
    private void parse_geni_value(String geni_value){
      Credential_Format credential_Format = new Credential_Format(geni_value);
      this.put(CREDENTIAL_FORMAT, credential_Format);
    }
    
    public Credential_Format get_credential_format(){
      return (Credential_Format) this.get(CREDENTIAL_FORMAT);
    }


}
