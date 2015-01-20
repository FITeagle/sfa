package org.fiteagle.north.sfa.am;

import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;


//import info.openmultinet.ontology.exceptions.InvalidModelException;
import org.apache.commons.codec.binary.Base64;
import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.allocate.ProcessAllocate;
import org.fiteagle.north.sfa.am.dm.SFA_AM_Delegate_Default;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender.EmptyReplyException;
import org.fiteagle.north.sfa.describe.DescribeProcessor;
import org.fiteagle.north.sfa.provision.ProcessProvision;
import org.fiteagle.north.sfa.util.URN;

import com.hp.hpl.jena.rdf.model.Model;

public class SFA_AM implements ISFA_AM {
  private static final int API_VERSION = 3;
  private final static Logger LOGGER = Logger.getLogger(SFA_AM.class.getName());
  private  ISFA_AM_Delegate delegate;
  
  public SFA_AM() {

  }
  
  private String query = "";
  
  @Override
  public Object handle(final String methodName,final List<?> parameter, final String path, final X509Certificate cert) {
    Object result;
    this.delegate =  new SFA_AM_Delegate_Default();;
    SFA_AM.LOGGER.log(Level.INFO, "Working on method: " + methodName);
    try{


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
      case ISFA_AM.METHOD_DESCRIBE:
        result = this.describe(parameter);
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
    }catch(BadArgumentsException e){
        HashMap<String, Object> exceptionBody = new HashMap<>();
        handleException(exceptionBody, e.getMessage(), GENI_CodeEnum.BADARGS);
        result = exceptionBody;
    }catch (JMSException e){
        HashMap<String, Object> exceptionBody = new HashMap<>();
        handleException(exceptionBody,e.getMessage(),GENI_CodeEnum.SERVERERROR);
        result =  exceptionBody;
    }catch (EmptyReplyException e) {
      HashMap<String, Object> exceptionBody = new HashMap<>();
      handleException(exceptionBody,e.getMessage(),GENI_CodeEnum.SEARCHFAILED);
      result =  exceptionBody;
    }catch(MessageUtil.TimeoutException e){
      HashMap<String, Object> exceptionBody = new HashMap<>();
      handleException(exceptionBody,e.getMessage(),GENI_CodeEnum.TIMEDOUT);
      result = exceptionBody;
    } catch (RuntimeException e) {
      HashMap<String, Object> exceptionBody = new HashMap<>();
      handleException(exceptionBody,e.getMessage(),GENI_CodeEnum.ERROR);
      result = exceptionBody;
    }catch (UnsupportedEncodingException e) {
      HashMap<String, Object> exceptionBody = new HashMap<>();
      handleException(exceptionBody, e.getMessage(), GENI_CodeEnum.ERROR);
      result = exceptionBody;
//    } catch (InvalidModelException e) {
//      HashMap<String, Object> exceptionBody = new HashMap<>();
//      handleException(exceptionBody, e.getMessage(), GENI_CodeEnum.ERROR);
//      result = exceptionBody;
    } catch (JAXBException e) {
      HashMap<String, Object> exceptionBody = new HashMap<>();
      handleException(exceptionBody, e.getMessage(), GENI_CodeEnum.ERROR);
      result = exceptionBody;
    }

    return result;
  }
  
  @Override
  public Object allocate(final List<?> parameter) {
    SFA_AM.LOGGER.log(Level.INFO, "allocate...");
    final HashMap<String, Object> result = new HashMap<>();
    
    final Map<String, Object> allocateParameters = new HashMap<>();
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
    
    final HashMap<String, Object> provisionParameters = new HashMap<>();
    ProcessProvision.parseProvsionParameter(parameter, provisionParameters);
    SFA_AM.LOGGER.log(Level.INFO, "provision parameters are parsed");
    Model provisionResponse = ProcessProvision.provisionInstances(provisionParameters);
    ProcessProvision.addProvisionValue(result, provisionResponse);
    this.addCode(result);
    this.addOutput(result);
    
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
  public Object listResources(final List<?> parameter) throws JMSException, UnsupportedEncodingException {
    SFA_AM.LOGGER.log(Level.INFO, "listResources...");
    final HashMap<String, Object> result = new HashMap<>();


    this.parseListResourcesParameter(parameter);
    addResources(result);

    
    this.addCode(result);
    this.addOutput(result);
    
    return result;
  }
  

  private void addResources(final HashMap<String, Object> result) throws JMSException, UnsupportedEncodingException {
    

      String testbedResources = SFA_AM_MDBSender.getInstance().getListRessources(this.query);
      
      if (this.delegate.getCompressed()) {
        result.put(ISFA_AM.VALUE, compress(testbedResources));
      } else {
        result.put(ISFA_AM.VALUE, testbedResources);
      }
      

  }
  
  public static String compress(String toCompress) throws UnsupportedEncodingException {
    byte[] output = null;
    String outputString = "";
    

    byte[] input = toCompress.getBytes("UTF-8");
     // Compress the bytes
    output = new byte[input.length];
    Deflater compresser = new Deflater();
    compresser.setInput(input);
    compresser.finish();
    compresser.deflate(output);
    compresser.end();
    outputString = Base64.encodeBase64String(output);
      

    
    return outputString;
    
  }
  
  private void parseListResourcesParameter(final List<?> parameter) {

    //First Parameter is always credential
    Object credential  = parameter.get(0);
    Object options = parameter.get(1);

    if(credential instanceof List<?>){
      parseCredentialsParameters(credential);
    }else{
      throw new BadArgumentsException("Invalid Credentials");
    }

      if (options instanceof Map<?, ?>) {
        this.parseOptionsParameters(options);
      }
      /*
       * else if (param instanceof List<?>) { // tood: parse more //this.parseCredentialsParameters(param); }
       */

  }
  
  private void parseOptionsParameters(final Object param) {
    
    @SuppressWarnings("unchecked")
    final Map<String, ?> param2 = (Map<String, ?>) param;
    
    if (param2.containsKey(IGeni.GENI_QUERY)) {
      this.query = param2.get(IGeni.GENI_QUERY).toString();
    } else {
      this.query = "";
    }
    
    if (param2.containsKey(IGeni.GENI_COMPRESSED)) {
      this.delegate.setCompressed((Boolean) param2.get(IGeni.GENI_COMPRESSED));
    }
    if (param2.containsKey(IGeni.GENI_AVAILABLE)) {
      this.delegate.setAvailable((Boolean) param2.get(IGeni.GENI_AVAILABLE));
    }
    
    // added for later use.
    /*
     * if(param2.get("geni_rspec_version") instanceof Map<?, ?>){ final Map<String, ?> geniRSpecVersion = (Map<String,
     * ?>) param2.get("geni_rspec_version"); String type = (String) geniRSpecVersion.get("type"); String version =
     * (String) geniRSpecVersion.get("version"); }
     */
  }
  //TODO needs way more logic, needs refactoring
  private void parseCredentialsParameters(final Object param) {
    
    @SuppressWarnings("unchecked")
    final List<Map<String, ?>> param2 = (List<Map<String, ?>>) param;
    if(param2.size() > 0){
    for (Map<String, ?> credential : param2) {
      if (credential.containsKey(IGeni.GENI_TYPE)) {
        this.delegate.setGeniType((String) credential.get(IGeni.GENI_TYPE));
      }
      if (credential.containsKey(IGeni.GENI_VERSION)) {
        this.delegate.setGeinVersion((String) credential.get(IGeni.GENI_VERSION));
      }
      if (credential.containsKey(IGeni.GENI_VALUE)) {
        this.delegate.setGeniValue((String) credential.get(IGeni.GENI_VALUE));
      }
    }
    }else{
      throw new BadArgumentsException("Invalid Credentials");
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

  @Override
  public Object describe(List<?> parameter) throws UnsupportedEncodingException, JAXBException { // , InvalidModelException {
    LOGGER.log(Level.ALL,"Describe called");
    final HashMap<String, Object> result = new HashMap<>();
    Object URNList = parameter.get(0);
    Object credList = parameter.get(1);
    Object options  = parameter.get(2);
    List<URN> URNS =parseURNList(URNList);

    parseCredentialsParameters(credList);
    parseDescribeOptions(options);

    DescribeProcessor describeProcessor = new DescribeProcessor();
    HashMap<String, Object> value = describeProcessor.getValue(credList, options, URNS);

    if(this.delegate.getCompressed())
      value.put(IGeni.GENI_RSPEC,compress((String)value.get(IGeni.GENI_RSPEC)));
    this.addCode(result);
    this.addOutput(result);
    result.put(ISFA_AM.VALUE,value);
    return result;
  }

  private void parseDescribeOptions(Object options) {
    HashMap<String,Object> optionsMap  = (HashMap<String,Object>) options;

    boolean compressed = (boolean) optionsMap.get(IGeni.GENI_COMPRESSED);
    if(compressed)
      this.delegate.setCompressed(true);

    HashMap<String, Object> geni_rspec_version = (HashMap<String, Object>) optionsMap.get(IGeni.GENI_RSPEC_VERSION);
    String geni_rspec_version_type = (String) geni_rspec_version.get(ISFA_AM.TYPE);
    String geni_rspec_version_version = (String) geni_rspec_version.get(ISFA_AM.VERSION);

  }

  private List<URN> parseURNList(Object urnList) {
    List<String> URNS = (ArrayList<String>) urnList;
    if(URNS == null || URNS.size() == 0){
      throw new BadArgumentsException("URN must not be null");
    }
    List<URN> returnList = new ArrayList<>();
    for(String s: URNS){
      URN u =  new URN(s);
      returnList.add(u);
    }
    return returnList;
  }

  private void handleException(HashMap<String, Object> result, String mes, GENI_CodeEnum errorCode) {
    LOGGER.log(Level.WARNING,mes);

    this.delegate.setGeniCode(errorCode.getValue());
    this.delegate.setOutput(mes);
    this.addCode(result);
    this.addOutput(result);


    result.put(ISFA_AM.VALUE, new HashMap<String, Object>());
  }

  private void addAPIVersion(final HashMap<String, Object> result) {
    result.put(IGeni.GENI_API, SFA_AM.API_VERSION);
  }
  
  private void addValue(final HashMap<String, Object> result) {
    
    final Map<String, Object> value = new HashMap<>();
    value.put(IGeni.GENI_API, SFA_AM.API_VERSION);
    
    String testbedDescription;

    testbedDescription = (String) SFA_AM_MDBSender.getInstance().getTestbedDescription();
    value.put(ISFA_AM.OMN_TESTBED, testbedDescription);
    System.out.println("omn_testbed " + value.get(ISFA_AM.OMN_TESTBED));

    final String[] extensions = getSupportedExtensions();

    addSupportedRequestRspecInfo(value, extensions);

    addAdvertisementRspecInfo(value, extensions);

    addSupportedCredentialTypes(value);
    this.delegate.setGeniCode(0);
    this.delegate.setOutput("SUCCESS");
    result.put(ISFA_AM.VALUE, value);
  }

  private void addSupportedRequestRspecInfo(Map<String, Object> value, String[] extensions) {
    final Map<String, String> apiVersions = new HashMap<>();
    apiVersions.put(ISFA_AM.VERSION_3, ISFA_AM.API_VERSION);
    value.put(IGeni.GENI_API_VERSION, apiVersions);

    final List<Map<String, Object>> reqRSpecs = new LinkedList<>();
    final Map<String, Object> typeA = new HashMap<>();
    typeA.put(ISFA_AM.TYPE, ISFA_AM.OPEN_MULTINET);
    typeA.put(ISFA_AM.VERSION, ISFA_AM.VERSION_1);
    typeA.put(IGeni.GENI_NAMESPACE, ISFA_AM.NAMESPACE);
    typeA.put(ISFA_AM.SCHEMA, IGeni.GENI_REQUEST_RSPEC_SCHEMA);

    typeA.put(IGeni.GENI_EXTENSIONS, extensions);

    reqRSpecs.add(typeA);
    value.put(IGeni.GENI_REQUEST_VERSION, reqRSpecs);
  }

  private String[] getSupportedExtensions() {
    List<String> extensionsMap = null;

    extensionsMap = SFA_AM_MDBSender.getInstance().getExtensions();
    final String[] extensions = new String[extensionsMap.size()];

    int i = 0;
    for (String namespace : extensionsMap) {
      extensions[i] = namespace;
      i++;
    }
    return extensions;
  }

  private void addAdvertisementRspecInfo(Map<String, Object> value, String[] extensions) {
    final List<Map<String, Object>> adRSpecs = new LinkedList<>();
    final Map<String, Object> adTypeA = new HashMap<>();
    adTypeA.put(ISFA_AM.TYPE, ISFA_AM.OPEN_MULTINET);
    adTypeA.put(ISFA_AM.VERSION, ISFA_AM.VERSION_1);
    adTypeA.put(ISFA_AM.SCHEMA, IGeni.GENI_AD_RSPEC_SCHEMA);
    adTypeA.put(IGeni.GENI_NAMESPACE, ISFA_AM.NAMESPACE);
    adTypeA.put(IGeni.GENI_EXTENSIONS, extensions);
    adRSpecs.add(adTypeA);
    value.put(IGeni.GENI_AD_VERSION, adRSpecs);
  }

  private void addSupportedCredentialTypes(Map<String, Object> value) {
    final List<Map<String, Object>> credTypes = new LinkedList<>();
    final Map<String, Object> credTypeA = new HashMap<>();
    credTypeA.put(IGeni.GENI_TYPE, IGeni.GENI_SFA);
    credTypeA.put(IGeni.GENI_VERSION, "1"); // should be 3 ?
    credTypes.add(credTypeA);
    value.put(IGeni.GENI_CREDENTIAL_TYPES, credTypes);
  }

  private void addOutput(final HashMap<String, Object> result) {
    result.put(ISFA_AM.OUTPUT, this.delegate.getOutput());
  }
  
  private void addCode(final HashMap<String, Object> result) {
    final Map<String, Integer> code = new HashMap<>();
    code.put(IGeni.GENI_CODE, this.delegate.getGeniCode());
    code.put(ISFA_AM.AM_CODE, this.delegate.getAMCode());
    result.put(ISFA_AM.CODE, code);
  }



  private class BadArgumentsException extends RuntimeException{
    private String message;
    public BadArgumentsException(String message){
      this.message = message;
    }
    @Override
    public String getMessage(){
      return message;
    }
  }
}
