package org.fiteagle.north.sfa.am.common;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.translators.geni.ManifestConverter;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import org.apache.commons.codec.binary.Base64;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.ISFA_AM_Delegate;
import org.fiteagle.north.sfa.am.ReservationStateEnum;

import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;

import org.fiteagle.north.sfa.am.dm.SFA_AM_Delegate_Default;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.exceptions.ForbiddenException;

import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.fiteagle.north.sfa.util.GENI_Credential;
import org.fiteagle.north.sfa.util.URN;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

/**
 * Created by dne on 16.03.15.
 */
public class AbstractMethodProcessor {
    private final static Logger LOGGER = Logger.getLogger(AbstractMethodProcessor.class.getName());


    public SFA_AM_MDBSender getSender() {
        return sender;
    }


    private SFA_AM_MDBSender sender;
    
    public ISFA_AM_Delegate delegate = new SFA_AM_Delegate_Default();
    
    protected List<?> parameter;
    
    protected List<URN> urns;
    
    public AbstractMethodProcessor() {
      
    }
    

    public void addSliverInformation(Map<String, Object> value, Model response){

        final List<Map<String, Object>> geniSlivers = getSlivers(response);
        value.put(IGeni.GENI_SLIVERS, geniSlivers);

        value.put(IGeni.GENI_URN, getSliceURN(response));

    }

    public List<Map<String, Object>> getSlivers(Model response) {
        final List<Map<String, Object>> geniSlivers = new LinkedList<>();

        StmtIterator stmtIterator = response.listStatements(null, RDF.type, Omn.Reservation);
        if(!stmtIterator.hasNext()){
            throw new SearchFailedException("Resource not found");
        }
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();
            Resource reservation = statement.getSubject();

            final Map<String, Object> sliverMap = new HashMap<>();
            Config config = new Config();
            Resource resource = response.getResource(reservation.getProperty(Omn.isReservationOf).getObject().asResource().getURI());
            sliverMap.put(IGeni.GENI_SLIVER_URN, ManifestConverter.generateSliverID(config.getProperty(IConfig.KEY_HOSTNAME), resource.getURI()));
            sliverMap.put(IGeni.GENI_EXPIRES, reservation.getProperty(MessageBusOntologyModel.endTime).getLiteral().getString());
            sliverMap.put(IGeni.GENI_ALLOCATION_STATUS, ReservationStateEnum.valueOf(reservation.getProperty(Omn_lifecycle.hasReservationState).getResource().getLocalName()).getGeniState());
            sliverMap.put(IGeni.GENI_OPERATIONAL_STATUS, getGENI_OperationalState(resource.getProperty(Omn_lifecycle.hasState).getObject()));
            sliverMap.put(IGeni.GENI_ERROR, ISFA_AM.NO_ERROR);

            geniSlivers.add(sliverMap);
        } return geniSlivers;
    }

    public String getSliceURN(Model statusResponse) {
        StmtIterator stmtIterator = statusResponse.listStatements(new SimpleSelector(null,Omn.hasResource, (Object)null));
        Resource topology = stmtIterator.nextStatement().getSubject();

        String uri = topology.getURI();
        String localname = "";
        String hostname = "";
        try {
            URL url = new URL(uri);
            localname = url.getPath().substring( url.getPath().lastIndexOf('/') + 1 );
            hostname = url.getHost();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //String localname =  uri.substring( uri.lastIndexOf('/') + 1 );
        //String localname = topology.getLocalName();
        //URN urn =  new URN("urn:publicid:IDN+"+topology.getNameSpace().replace("http://","")+"+slice+"+localname);
        URN urn = new URN("urn:publicid:IDN+"+hostname+"+slice+"+localname);
        return urn.toString();

    }

    private String getGENI_OperationalState(RDFNode object) {
        switch (object.asResource().getLocalName()){
            case ISFA_AM.READY:
                LOGGER.log(Level.INFO, ISFA_AM.READY);
                return IGeni.GENI_READY;
            case ISFA_AM.UNCOMPLETED:
                return IGeni.GENI_PENDING_ALLOCATION;
            case ISFA_AM.STARTED:
                    return  IGeni.GENI_READY;
            default:
                return "";
        }
    }



    public void setSender(SFA_AM_MDBSender sender) {
        this.sender = sender;
    }

    public String compress(String toCompress) throws UnsupportedEncodingException {
      byte[] output;
      String outputString;

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
    
    public void addOutput(final HashMap<String, Object> result) {
        result.put(ISFA_AM.OUTPUT, this.delegate.getOutput());
    }

    public void addCode(final HashMap<String, Object> result) {
        final Map<String, Integer> code = new HashMap<>();
        code.put(IGeni.GENI_CODE, this.delegate.getGeniCode());
        code.put(ISFA_AM.AM_CODE, this.delegate.getAMCode());
        result.put(ISFA_AM.CODE, code);
    }
    
    public void handleCredentials(int index, String methodName) {
      List<GENI_Credential> credentialList = this.parseCredentialsParameters(this.parameter.get(index));
      this.checkCredentials(credentialList, methodName);
    }
    
    public List<GENI_Credential> parseCredentialsParameters(final Object param) {

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
    
  public void checkCredentials(List<GENI_Credential> credentialList, String methodName) {
    
    for (GENI_Credential credential : credentialList) {
      checkCredentialParts(credential);
//      if (credential.get_geni_type() == null || credential.get_geni_version() == null
//          || credential.get_geni_value() == null
//          || credential.get_credential_format().getPrivileges().getNAME() == null) {
//        throw new ForbiddenException("Operation forbidden, Credentials not valid");
//      }
      
      String privilege = credential.get_credential_format().getPrivileges().getNAME();
      
      switch (methodName){
        case ISFA_AM.METHOD_LIST_RESOURCES:
          if(!ISFA_AM.PRIVILEGE_DEFAULT.equals(privilege) && !ISFA_AM.PRIVILEGE_INFO.equals(privilege)){
            handleNotValidPrivileges();
          }
          break;
        
        case ISFA_AM.METHOD_ALLOCATE:
          if(!ISFA_AM.PRIVILEGE_DEFAULT.equals(privilege)){
            handleNotValidPrivileges();
          }
          break;
          
        case ISFA_AM.METHOD_DESCRIBE:
          if(!ISFA_AM.PRIVILEGE_DEFAULT.equals(privilege) && !ISFA_AM.PRIVILEGE_INFO.equals(privilege)){
            handleNotValidPrivileges();
          }
          break;
          
        case ISFA_AM.METHOD_RENEW:
          if(!ISFA_AM.PRIVILEGE_DEFAULT.equals(privilege) && !ISFA_AM.PRIVILEGE_REFRESH.equals(privilege)){
            handleNotValidPrivileges();
          }
          break;
          
        case ISFA_AM.METHOD_PROVISION:
          if(!ISFA_AM.PRIVILEGE_DEFAULT.equals(privilege) && !ISFA_AM.PRIVILEGE_CONTROL.equals(privilege)){
            handleNotValidPrivileges();
          }
          break;
          
        case ISFA_AM.METHOD_STATUS:
          if(!ISFA_AM.PRIVILEGE_DEFAULT.equals(privilege) && !ISFA_AM.PRIVILEGE_INFO.equals(privilege) && !ISFA_AM.PRIVILEGE_PI.equals(privilege)){
            handleNotValidPrivileges();
          }
          break;
          
        case ISFA_AM.METHOD_PERFORMOPERATIONALACTION:
          if(!ISFA_AM.PRIVILEGE_DEFAULT.equals(privilege) && !ISFA_AM.PRIVILEGE_CONTROL.equals(privilege)){
            handleNotValidPrivileges();
          }
          break;
          
        case ISFA_AM.METHOD_DELETE:
          if(!ISFA_AM.PRIVILEGE_DEFAULT.equals(privilege) && !ISFA_AM.PRIVILEGE_CONTROL.equals(privilege) && !ISFA_AM.PRIVILEGE_RESOLVE.equals(privilege) && !ISFA_AM.PRIVILEGE_INSTANTIATE.equals(privilege)){
            handleNotValidPrivileges();
          }
          break;
          
        case ISFA_AM.METHOD_SHUTDOWN:
          if(!ISFA_AM.PRIVILEGE_DEFAULT.equals(privilege) && !ISFA_AM.PRIVILEGE_CONTROL.equals(privilege) && !ISFA_AM.PRIVILEGE_PI.equals(privilege)){
            handleNotValidPrivileges();
          }
          break;
          
      }
      this.delegate.setGeniType(credential.get_geni_type());
      this.delegate.setGeinVersion(credential.get_geni_version());
      this.delegate.setGeniValue(credential.get_geni_value());
      
    }
  }
  
    private void checkCredentialParts(GENI_Credential credential){
    if (credential.get_geni_type() == null || credential.get_geni_version() == null
        || credential.get_geni_value() == null || credential.get_credential_format().getPrivileges().getNAME() == null
        || !credential.get_credential_format().checkPrivilegeType()) {
      throw new ForbiddenException("Operation forbidden, Credentials not valid");
    }
    }
  
    private void handleNotValidPrivileges(){
      throw new ForbiddenException("Operation forbidden, privileges are not valid for this method");
    }
    
    public List<?> getParameter(){
      return this.parameter;
    }


    public void parseURNList() {
      List<String> URNS = (ArrayList<String>) parameter.get(0);
      if (URNS == null || URNS.size() == 0) {
          throw new BadArgumentsException("URN must not be null");
      }
      List<URN> returnList = new ArrayList<>();
      for (String s : URNS) {
          URN u = new URN(s);
          returnList.add(u);
      }
      setURNlist(returnList);
  }
    
    private void setURNlist(List<URN> returnList) {
      this.urns = returnList;
    }
}
