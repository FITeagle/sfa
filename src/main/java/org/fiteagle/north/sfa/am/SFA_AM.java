package org.fiteagle.north.sfa.am;

import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

import javax.jms.JMSException;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.fiteagle.north.sfa.allocate.AllocateParameter;
import org.fiteagle.north.sfa.allocate.ProcessAllocate;
import org.fiteagle.north.sfa.allocate.UsersAllocateParameters;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender.EmptyReplyException;
import org.fiteagle.north.sfa.provision.ProcessProvision;

public class SFA_AM implements ISFA_AM {
  private static final int API_VERSION = 3;
  private final static Logger LOGGER = Logger.getLogger(SFA_AM.class.getName());
  private final ISFA_AM_Delegate delegate;
  
  public SFA_AM(final ISFA_AM_Delegate delegate) {
    this.delegate = delegate;
  }
  
  private String query = "";
  
  @Override
  public Object handle(final String methodName, final List<?> parameter, final String path, final X509Certificate cert) {
    Object result;
    
    SFA_AM.LOGGER.log(Level.INFO, "Working on method: " + methodName);
    switch (methodName.toUpperCase()) {
      case ISFA_AM.METHOD_GET_VERSION:
        result = this.getVersion(parameter);
        break;
      case ISFA_AM.METHOD_LIST_RESOURCES:
        result = this.listResources(parameter);
        break;
      case ISFA_AM.METHOD_ALLOCATE:
        result = this.allocate(parameter);
        break;
      case ISFA_AM.METHOD_RENEW:
        result = this.renew(parameter);
        break;
      case ISFA_AM.METHOD_PROVISION:
        result = this.provision(parameter);
        break;
      case ISFA_AM.METHOD_STATUS:
        result = this.status(parameter);
        break;
      case ISFA_AM.METHOD_PERFORMOPERATIONALACTION:
        result = this.performOperationalAction(parameter);
        break;
      case ISFA_AM.METHOD_DELETE:
        result = this.delete(parameter);
        break;
      case ISFA_AM.METHOD_SHUTDOWN:
        result = this.shutdown(parameter);
        break;
      default:
        result = "Unimplemented method '" + methodName + "'";
        break;
    }
    
    return result;
  }
  
  @Override
  public Object allocate(final List<?> parameter) {
    SFA_AM.LOGGER.log(Level.INFO, "allocate...");
    final HashMap<String, Object> result = new HashMap<>();
    
    Map<String, Object> allocateParameters = new HashMap<>();
    ProcessAllocate.parseAllocateParameter(parameter, allocateParameters);
    
    final Map<String, String> sliverMap = new HashMap<>();
    ProcessAllocate.reserveInstances(allocateParameters, sliverMap);
    ProcessAllocate.addAllocateValue(result, sliverMap, allocateParameters);
    
    this.addCode(result);
    this.addOutput(result);
    return result;
  }
  
  @Override
  public Object renew(final List<?> parameter) {
    final HashMap<String, Object> result = new HashMap<>();
    return result;
  }
  
  @Override
  public Object provision(final List<?> parameter) {
    SFA_AM.LOGGER.log(Level.INFO, "provision...");
    final HashMap<String, Object> result = new HashMap<>();
    
    Map<String, Object> provisionParameters = new HashMap<>();
    ProcessProvision.parseProvsionParameter(parameter, provisionParameters);
    ProcessProvision.provisionInstnces(provisionParameters);
    
    return result;
  }
  
  @Override
  public Object status(final List<?> parameter) {
    final HashMap<String, Object> result = new HashMap<>();
    return result;
  }
  
  @Override
  public Object performOperationalAction(final List<?> parameter) {
    final HashMap<String, Object> result = new HashMap<>();
    return result;
  }
  
  @Override
  public Object delete(final List<?> parameter) {
    final HashMap<String, Object> result = new HashMap<>();
    return result;
  }
  
  @Override
  public Object shutdown(final List<?> parameter) {
    final HashMap<String, Object> result = new HashMap<>();
    return result;
  }
  
  @Override
  public Object listResources(final List<?> parameter) {
    SFA_AM.LOGGER.log(Level.INFO, "listResources...");
    final HashMap<String, Object> result = new HashMap<>();
    this.parseListResourcesParameter(parameter);
    try {
      addRessources(result);
    } catch (JMSException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    this.addCode(result);
    this.addOutput(result);
    
    return result;
  }
  
  private void addRessources(final HashMap<String, Object> result) throws JMSException {
    
    try {
      String testbedRessources = SFA_AM_MDBSender.getInstance().getListRessources(this.query);
      
      if (this.delegate.getCompressed()) {
        result.put(ISFA_AM.VALUE, compress(testbedRessources));
      } else {
        result.put(ISFA_AM.VALUE, testbedRessources);
      }
      
    } catch (EmptyReplyException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
      this.delegate.setGeniCode(GENI_CodeEnum.UNAVAILABLE.getValue());
      this.delegate.setOutput(GENI_CodeEnum.UNAVAILABLE.getDescription() + e.getMessage());
      return;
    } catch (RuntimeException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
      if (e.getMessage().equals(Response.Status.REQUEST_TIMEOUT.name())) {
        this.delegate.setGeniCode(GENI_CodeEnum.TIMEDOUT.getValue());
        this.delegate.setOutput(GENI_CodeEnum.TIMEDOUT.getDescription());
      } else {
        this.delegate.setGeniCode(GENI_CodeEnum.ERROR.getValue());
        this.delegate.setOutput(e.getMessage());
      }
      return;
    }
  }
  
  public static String compress(String toCompress) {
    byte[] output = null;
    String outputString = "";
    
    try {
      byte[] input = toCompress.getBytes("UTF-8");
      // Compress the bytes
      output = new byte[input.length];
      Deflater compresser = new Deflater();
      compresser.setInput(input);
      compresser.finish();
      compresser.deflate(output);
      compresser.end();
      outputString = Base64.encodeBase64String(output);
      
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return outputString;
    
  }
  
  private void parseListResourcesParameter(final List<?> parameter) {
    for (final Object param : parameter) {
      if (param instanceof Map<?, ?>) {
        this.parseOptionsParameters(param);
      }
      /*
       * else if (param instanceof List<?>) { // tood: parse more //this.parseCredentialsParameters(param); }
       */
    }
  }
  
  private void parseOptionsParameters(final Object param) {
    
    @SuppressWarnings("unchecked")
    final Map<String, ?> param2 = (Map<String, ?>) param;
    
    if (param2.containsKey("geni_query")) {
      this.query = param2.get("geni_query").toString();
    } else {
      this.query = "";
    }
    
    if (param2.containsKey("geni_compressed")) {
      this.delegate.setCompressed((Boolean) param2.get("geni_compressed"));
    }
    if (param2.containsKey("geni_available")) {
      this.delegate.setAvailable((Boolean) param2.get("geni_available"));
    }
    
    // added for later use.
    /*
     * if(param2.get("geni_rspec_version") instanceof Map<?, ?>){ final Map<String, ?> geniRSpecVersion = (Map<String,
     * ?>) param2.get("geni_rspec_version"); String type = (String) geniRSpecVersion.get("type"); String version =
     * (String) geniRSpecVersion.get("version"); }
     */
  }
  
  private void parseCredentialsParameters(final Object param) {
    
    @SuppressWarnings("unchecked")
    final List<Map<String, ?>> param2 = (List<Map<String, ?>>) param;
    for (Map<String, ?> credential : param2) {
      if (credential.containsKey("geni_type")) {
        this.delegate.setGeniType((String) credential.get("geni_type"));
      }
      if (credential.containsKey("geni_version")) {
        this.delegate.setGeinVersion((String) credential.get("geni_version"));
      }
      if (credential.containsKey("geni_value")) {
        this.delegate.setGeniValue((String) credential.get("geni_value"));
      }
    }
  }
  
  @Override
  public Object getVersion(final List<?> parameter) {
    final HashMap<String, Object> result = new HashMap<>();
    this.addAPIVersion(result);
    this.addValue(result);
    this.addCode(result);
    this.addOutput(result);
    return result;
  }
  
  private void addAPIVersion(final HashMap<String, Object> result) {
    result.put(ISFA_AM.GENI_API, SFA_AM.API_VERSION);
  }
  
  private void addValue(final HashMap<String, Object> result) {
    
    final Map<String, Object> value = new HashMap<>();
    value.put(ISFA_AM.GENI_API, SFA_AM.API_VERSION);
    
    String testbedDescription;
    
    try {
      testbedDescription = (String) SFA_AM_MDBSender.getInstance().getTestbedDescription();
      value.put(ISFA_AM.OMN_TESTBED, testbedDescription);
      System.out.println("omn_testbed " + value.get(ISFA_AM.OMN_TESTBED));
      this.delegate.setGeniCode(0);
      this.delegate.setOutput("SUCCESS");
      
    } catch (EmptyReplyException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
      this.delegate.setGeniCode(GENI_CodeEnum.UNAVAILABLE.getValue());
      this.delegate.setOutput(GENI_CodeEnum.UNAVAILABLE.getDescription() + e.getMessage());
      return;
    } catch (RuntimeException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
      if (e.getMessage().equals(Response.Status.REQUEST_TIMEOUT.name())) {
        this.delegate.setGeniCode(GENI_CodeEnum.TIMEDOUT.getValue());
        this.delegate.setOutput(GENI_CodeEnum.TIMEDOUT.getDescription());
      } else {
        this.delegate.setGeniCode(GENI_CodeEnum.ERROR.getValue());
        this.delegate.setOutput(e.getMessage());
      }
      return;
    }
    
    final Map<String, String> apiVersions = new HashMap<>();
    apiVersions.put(ISFA_AM.VERSION_3, ISFA_AM.API_VERSION);
    value.put(ISFA_AM.GENI_API_VERSION, apiVersions);
    
    final List<Map<String, Object>> reqRSpecs = new LinkedList<>();
    final Map<String, Object> typeA = new HashMap<>();
    typeA.put(ISFA_AM.TYPE, ISFA_AM.OPEN_MULTINET);
    typeA.put(ISFA_AM.VERSION, ISFA_AM.VERSION_1);
    typeA.put(ISFA_AM.GENI_NAMESPACE, ISFA_AM.NAMESPACE);
    
    List<String> extensionsMap = SFA_AM_MDBSender.getInstance().getExtensions();
    final String[] extensions = new String[extensionsMap.size()];
    
    int i = 0;
    for (String namespace : extensionsMap) {
      extensions[i] = namespace;
      i++;
    }
    typeA.put(ISFA_AM.GENI_EXTENSIONS, extensions);
    
    reqRSpecs.add(typeA);
    value.put(ISFA_AM.GENI_REQUEST_VERSION, reqRSpecs);
    
    final List<Map<String, Object>> adRSpecs = new LinkedList<>();
    final Map<String, Object> adTypeA = new HashMap<>();
    adTypeA.put(ISFA_AM.TYPE, ISFA_AM.OPEN_MULTINET);
    adTypeA.put(ISFA_AM.VERSION, ISFA_AM.VERSION_1);
    adTypeA.put(ISFA_AM.GENI_NAMESPACE, ISFA_AM.NAMESPACE);
    adTypeA.put(ISFA_AM.GENI_EXTENSIONS, extensions);
    adRSpecs.add(adTypeA);
    value.put(ISFA_AM.GENI_AD_VERSION, adRSpecs);
    
    final List<Map<String, Object>> credTypes = new LinkedList<>();
    final Map<String, Object> credTypeA = new HashMap<>();
    credTypeA.put(ISFA_AM.GENI_TYPE, ISFA_AM.GENI_SFA);
    credTypeA.put(ISFA_AM.GENI_VERSION, "1"); // should be 3 ?
    credTypes.add(credTypeA);
    value.put(ISFA_AM.GENI_CREDENTIAL_TYPES, credTypes);
    
    result.put(ISFA_AM.VALUE, value);
  }
  
  private void addOutput(final HashMap<String, Object> result) {
    result.put(ISFA_AM.OUTPUT, this.delegate.getOutput());
  }
  
  private void addCode(final HashMap<String, Object> result) {
    final Map<String, Integer> code = new HashMap<>();
    code.put(ISFA_AM.GENI_CODE, this.delegate.getGeniCode());
    code.put(ISFA_AM.AM_CODE, this.delegate.getAMCode());
    result.put(ISFA_AM.CODE, code);
  }
  
}
