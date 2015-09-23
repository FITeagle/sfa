package org.fiteagle.north.sfa.util;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.fiteagle.north.sfa.exceptions.ForbiddenException;

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
      if(geni_value == null || geni_value.isEmpty()){
        throw new ForbiddenException("bad credentials "); 
      }
      JAXBContext jc;
      try {
        jc = JAXBContext.newInstance(Signed_Credential.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        
        StringReader stringReader = new StringReader(geni_value);
        org.xml.sax.InputSource is = new org.xml.sax.InputSource(stringReader);
        
        Signed_Credential signed_credential = (Signed_Credential) unmarshaller.unmarshal(is);
        
        this.put(CREDENTIAL_FORMAT, signed_credential);
        
      } catch (JAXBException e) {
        throw new ForbiddenException("credentials value can't be read "); 
      }
      
    }
    
    public Signed_Credential get_signed_credential(){
      return (Signed_Credential) this.get(CREDENTIAL_FORMAT);
    }


}
