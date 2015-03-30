package org.fiteagle.north.sfa.am;

import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;


//import info.openmultinet.ontology.exceptions.InvalidModelException;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.translators.geni.AdvertisementConverter;
import info.openmultinet.ontology.translators.geni.ManifestConverter;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import org.apache.commons.codec.binary.Base64;
import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.allocate.ProcessAllocate;
import org.fiteagle.north.sfa.am.listResources.ListResourcesProcessor;
import org.fiteagle.north.sfa.am.performOperationalAction.PerformOperationalActionHandler;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.exceptions.BadVersionException;
import org.fiteagle.north.sfa.exceptions.ForbiddenException;
import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.fiteagle.north.sfa.am.status.StatusProcessor;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.am.dm.SFA_AM_Delegate_Default;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.exceptions.EmptyReplyException;
import org.fiteagle.north.sfa.am.delete.ProcessDelete;
import org.fiteagle.north.sfa.am.describe.DescribeProcessor;
import org.fiteagle.north.sfa.am.getVersion.ProcessGetVersion;
import org.fiteagle.north.sfa.am.provision.ProcessProvision;
import org.fiteagle.north.sfa.util.GENI_Credential;
import org.fiteagle.north.sfa.util.URN;

import com.hp.hpl.jena.rdf.model.Model;

public class SFA_AM implements ISFA_AM {
    private final static Logger LOGGER = Logger.getLogger(SFA_AM.class.getName());
    private ISFA_AM_Delegate delegate;

    public SFA_AM() {

    }

    private String query = "";

    @Override
    public Object handle(final String methodName, final List<?> parameter, final String path, final X509Certificate cert) {
        Object result;

        this.delegate = new SFA_AM_Delegate_Default();
        SFA_AM.LOGGER.log(Level.INFO, System.getProperty("jboss.server.config.dir") + System.getProperty("file.separator") + "jetty-ssl.keystore");
        SFA_AM.LOGGER.log(Level.INFO, "Working on method: " + methodName);
        try {


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
        } catch (BadArgumentsException e) {
          result = handleException(e, GENI_CodeEnum.BADARGS);
          
        } catch (JMSException e) {
          result = handleException(e, GENI_CodeEnum.SERVERERROR);
          
        } catch (EmptyReplyException e) {
          result = handleException(e, GENI_CodeEnum.SEARCHFAILED);
          
        } catch (MessageUtil.TimeoutException e) {
          result = handleException(e, GENI_CodeEnum.TIMEDOUT);
          
        } catch (ForbiddenException e){
          result = handleException(e, GENI_CodeEnum.FORBIDDEN);
          
        } catch (SearchFailedException e){
        result = handleException(e, GENI_CodeEnum.SEARCHFAILED);
        
        } catch(BadVersionException e){
          result = handleException(e, GENI_CodeEnum.BADVERSION);
          
        } catch (RuntimeException e) {
          result = handleException(e, GENI_CodeEnum.ERROR);
          
        } catch (UnsupportedEncodingException e) {
          result = handleException(e, GENI_CodeEnum.ERROR);
          
//    } catch (InvalidModelException e) {
//      HashMap<String, Object> exceptionBody = new HashMap<>();
//      handleException(exceptionBody, e.getMessage(), GENI_CodeEnum.ERROR);
//      result = exceptionBody;
        } catch (JAXBException e) {
          result = handleException(e, GENI_CodeEnum.ERROR);

        } catch (InvalidModelException e) {
          result = handleException(e, GENI_CodeEnum.ERROR);
        } 

        return result;
    }

    @Override
    public Object allocate(final List<?> parameter) throws JAXBException, InvalidModelException, UnsupportedEncodingException {
        SFA_AM.LOGGER.log(Level.INFO, "allocate...");
        final HashMap<String, Object> result = new HashMap<>();

        final Map<String, Object> allocateParameters = new HashMap<>();
        ProcessAllocate.parseAllocateParameter(parameter, allocateParameters);

//        final Map<String, String> sliverMap = new HashMap<>();
        Model allocateResponse = ProcessAllocate.reserveInstances(allocateParameters);
        ProcessAllocate.addAllocateValue(result, allocateParameters, allocateResponse);

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
    public Object provision(final List<?> parameter) throws UnsupportedEncodingException {
        SFA_AM.LOGGER.log(Level.INFO, "provision...");
        final HashMap<String, Object> result = new HashMap<>();
        List<URN> urns = parseURNList(parameter.get(0));
        ProcessProvision processProvision = new ProcessProvision(urns);
        processProvision.handleCredentials(parameter.get(1));
        final HashMap<String, Object> provisionParameters = (HashMap<String, Object>) parameter.get(2);
        SFA_AM.LOGGER.log(Level.INFO, "provision parameters are parsed");
        processProvision.setSender(SFA_AM_MDBSender.getInstance());
        Model provisionResponse = processProvision.provisionInstances();
        processProvision.createResponse(result, provisionResponse);
        return result;
    }

    @Override
    public Object status(final List<?> parameter) throws UnsupportedEncodingException {
        SFA_AM.LOGGER.log(Level.INFO, "status...");
        final HashMap<String, Object> result = new HashMap<>();
        List<URN> urns = parseURNList(parameter.get(0));
        StatusProcessor statusProcessor = new StatusProcessor(urns);
        statusProcessor.handleCredentials(parameter.get(1));
        final HashMap<String, Object> statusParameters = (HashMap<String, Object>) parameter.get(2);
        statusProcessor.setSender(SFA_AM_MDBSender.getInstance());
        Model statusResponse = statusProcessor.getStates();
        statusProcessor.createResponse(result, statusResponse);
        return result;
    }

    @Override
    public Object performOperationalAction(final List<?> parameter) throws UnsupportedEncodingException {
        SFA_AM.LOGGER.log(Level.INFO, "performOperationalAction...");
        final HashMap<String, Object> result = new HashMap<>();
        List<URN> urns = parseURNList(parameter.get(0));
        PerformOperationalActionHandler performOperationalActionHandler = new PerformOperationalActionHandler(urns);
        performOperationalActionHandler.handleCredentials(parameter.get(1));
        String action = (String) parameter.get(2);
        //TODO ignore options for now

        performOperationalActionHandler.setSender(SFA_AM_MDBSender.getInstance());
        Model performResponse = performOperationalActionHandler.performAction(action);
        performOperationalActionHandler.createResponse(result, performResponse);
        return result;
    }

    @Override
    public Object delete(final List<?> parameter) throws UnsupportedEncodingException {
        
        SFA_AM.LOGGER.log(Level.INFO, "delete...");
        final HashMap<String, Object> result = new HashMap<>();
        List<URN> urns = parseURNList(parameter.get(0));
        ProcessDelete processDelete = new ProcessDelete(urns);
        processDelete.handleCredentials(parameter.get(1));
        final HashMap<String, Object> deleteParameters = (HashMap<String, Object>) parameter.get(2);
        SFA_AM.LOGGER.log(Level.INFO, "delete parameters are parsed");
        processDelete.setSender(SFA_AM_MDBSender.getInstance());
        Model deleteResponse = processDelete.deleteInstances();
        processDelete.createResponse(result, deleteResponse);
        return result;
    }

    @Override
    public Object shutdown(final List<?> parameter) {
        final HashMap<String, Object> result = new HashMap<>();
        return result;
    }

  @Override
  public Object listResources(final List<?> parameter) throws JMSException, UnsupportedEncodingException, JAXBException, InvalidModelException {
    SFA_AM.LOGGER.log(Level.INFO, "listResources...");
    HashMap<String, Object> result = new HashMap<>();
    ListResourcesProcessor listResourcesProcessor = new ListResourcesProcessor(parameter);
    listResourcesProcessor.handleCredentials();
    listResourcesProcessor.parseOptionsParameters();
    if (listResourcesProcessor.checkSupportedVersions()) {
      listResourcesProcessor.setSender(SFA_AM_MDBSender.getInstance());
      Model topologyModel = listResourcesProcessor.listResources();
      listResourcesProcessor.createResponse(result, topologyModel);
      return result;
      } else {
        throw new BadVersionException(GENI_CodeEnum.BADVERSION.getDescription());
        }
  }



    //TODO remove after refactoring all methods
    public static String compress(String toCompress) throws UnsupportedEncodingException {
        byte[] output = null;
        String outputString = "";


        byte[] input = toCompress.getBytes(ISFA_AM.UTF_8);
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

    //TODO needs way more logic, needs refactoring
    private List<GENI_Credential> parseCredentialsParameters(final Object param) {

        @SuppressWarnings("unchecked")
        final List<Map<String, ?>> param2 = (List<Map<String, ?>>) param;
        List<GENI_Credential> credentialList = new ArrayList<>(param2.size());
        if (param2.size() > 0) {
            for (Map<String, ?> map : param2) {
                GENI_Credential credential = new GENI_Credential(map);
                credentialList.add(credential);

            }
        } else {
            throw new BadArgumentsException("Invalid Credentials");
        }
        return credentialList;
    }

    @Override
    public Object getVersion(final List<?> parameter) {
        final HashMap<String, Object> result = new HashMap<>();
        ProcessGetVersion processGetVersion = new ProcessGetVersion();
        processGetVersion.setSender(SFA_AM_MDBSender.getInstance());
        
        Model testbedDescriptionModel = processGetVersion.getTestbedDescription();
        String testbedDescription = processGetVersion.parseTestbedDescription(testbedDescriptionModel);
        processGetVersion.createResponse(result, testbedDescription);
        return result;
    }

    @Override
    public Object describe(List<?> parameter) throws UnsupportedEncodingException, JAXBException, InvalidModelException {
        LOGGER.log(Level.ALL, "Describe called");
        final HashMap<String, Object> result = new HashMap<>();
        Object URNList = parameter.get(0);
        Object credList = parameter.get(1);
        Object options = parameter.get(2);
        List<URN> URNS = parseURNList(URNList);

        List<GENI_Credential> credentialList = parseCredentialsParameters(credList);
        checkCredentials(credentialList);

        parseDescribeOptions(options);

        DescribeProcessor describeProcessor = new DescribeProcessor();
        Model descriptions = describeProcessor.getDescriptions(URNS);
        HashMap<String, Object> value = new HashMap<>();
        StmtIterator stmtIterator = descriptions.listStatements(null, RDF.type, Omn.Reservation);
        if(stmtIterator.hasNext() || !(URNS.size() ==1 && ISFA_AM.SLICE.equalsIgnoreCase(URNS.get(0).getType()) ))
            describeProcessor.addSliverInformation(value,descriptions);

        value.put(IGeni.GENI_RSPEC, ManifestConverter.getRSpec(descriptions, IConfig.DEFAULT_HOSTNAME));
        if (this.delegate.getCompressed())
            value.put(IGeni.GENI_RSPEC, compress((String) value.get(IGeni.GENI_RSPEC)));
        this.addCode(result);
        this.addOutput(result);
        result.put(ISFA_AM.VALUE, value);
        return result;
    }

    // TODO remove after refactoring all methods
    private void checkCredentials(List<GENI_Credential> credentialList) {

        for (GENI_Credential credential : credentialList) {
            if(credential.get_geni_type() == null || credential.get_geni_version() ==null || credential.get_geni_value() == null){
                throw new ForbiddenException("Operation forbidden, Credentials not valid");
            }
            this.delegate.setGeniType((String) credential.get_geni_type());
            this.delegate.setGeinVersion((String) credential.get_geni_version());
            this.delegate.setGeniValue((String) credential.get_geni_value());
        }

    }

    private void parseDescribeOptions(Object options) {
        HashMap<String, Object> optionsMap = (HashMap<String, Object>) options;

        boolean compressed = (boolean) optionsMap.get(IGeni.GENI_COMPRESSED);
        if (compressed)
            this.delegate.setCompressed(true);

        HashMap<String, Object> geni_rspec_version = (HashMap<String, Object>) optionsMap.get(IGeni.GENI_RSPEC_VERSION);
        String geni_rspec_version_type = (String) geni_rspec_version.get(ISFA_AM.TYPE);
        String geni_rspec_version_version = (String) geni_rspec_version.get(ISFA_AM.VERSION);

    }

    private List<URN> parseURNList(Object urnList) {
        List<String> URNS = (ArrayList<String>) urnList;
        if (URNS == null || URNS.size() == 0) {
            throw new BadArgumentsException("URN must not be null");
        }
        List<URN> returnList = new ArrayList<>();
        for (String s : URNS) {
            URN u = new URN(s);
            returnList.add(u);
        }
        return returnList;
    }

    private HashMap<String, Object> handleException(Exception e, GENI_CodeEnum errorCode) {
        LOGGER.log(Level.WARNING, e.getMessage(),e);
        HashMap<String, Object> result = new HashMap<>();
        AbstractMethodProcessor abstractMethodProcessor = new AbstractMethodProcessor();
        abstractMethodProcessor.delegate.setGeniCode(errorCode.getValue());
        abstractMethodProcessor.delegate.setOutput(e.getMessage());
        abstractMethodProcessor.addCode(result);
        abstractMethodProcessor.addOutput(result);
        result.put(ISFA_AM.VALUE, new HashMap<String, Object>());
        return result;
    }

    //TODO remove after refactoring all methods
    private void addOutput(final HashMap<String, Object> result) {
        result.put(ISFA_AM.OUTPUT, this.delegate.getOutput());
    }

    //TODO remove after refactoring all methods
    private void addCode(final HashMap<String, Object> result) {
        final Map<String, Integer> code = new HashMap<>();
        code.put(IGeni.GENI_CODE, this.delegate.getGeniCode());
        code.put(ISFA_AM.AM_CODE, this.delegate.getAMCode());
        result.put(ISFA_AM.CODE, code);
    }

}
