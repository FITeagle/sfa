package org.fiteagle.north.sfa.util;

import com.sun.corba.se.spi.ior.ObjectKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dne on 20.01.15.
 */
public class GENI_Credential extends HashMap<String, String>{

      final String GENI_TYPE = "geni_type";
      final String GENI_VERSION = "geni_version";
      final String GENI_VALUE = "geni_value";

    public GENI_Credential(Map<String, ?> credMap){
        set_geni_type((String) credMap.get(GENI_TYPE));
        set_geni_version((String) credMap.get(GENI_VERSION));
        set_geni_value((String) credMap.get(GENI_VALUE));
    }

    public String get_geni_type(){
        return this.get(GENI_TYPE);
    }
    private void set_geni_type(String geni_type){
        this.put(GENI_TYPE, geni_type);
    }

    public String get_geni_version(){
        return this.get(GENI_VERSION);
    }

    private void set_geni_version(String geni_version){
        this.put(GENI_VERSION, geni_version);
    }

    public String get_geni_value(){
        return this.get(GENI_VALUE);
    }

    private void set_geni_value(String geni_value){
        this.put(GENI_VALUE, geni_value);
    }


}
