package org.fiteagle.north.sfa.sa;

import java.io.StringReader;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.aaa.CertificateAuthority;
import org.fiteagle.north.sfa.aaa.CredentialFactory;
import org.fiteagle.north.sfa.aaa.KeyStoreManagement;
import org.fiteagle.north.sfa.aaa.X509Util;
import org.fiteagle.north.sfa.aaa.jaxbClasses.Credential;
import org.fiteagle.north.sfa.aaa.jaxbClasses.SignedCredential;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class SFA_SA implements ISFA_SA {

    protected static Logger LOGGER = Logger.getLogger(SFA_SA.class.getName());

    ISFA_SA_Delegate delegate;

    public SFA_SA() {
    }

    @Override
    public Object handle(final String methodName, final List<?> parameter,
                         final String path, final X509Certificate cert) {
        Object result;
        this.delegate = null;
        SFA_SA.LOGGER.log(Level.INFO, "Working on method: " + methodName);
        try {
            switch (methodName.toUpperCase()) {
                case ISFA_AM.METHOD_GET_VERSION:
                    result = this.getVersion(parameter);
                    break;
                case ISFA_SA.METHOD_GET_CREDENTIAL:
                    result = this.getCredential(cert);
                    break;
                case ISFA_SA.METHOD_REGISTER:
                    result = this.register(parameter);
                    break;
                case ISFA_SA.METHOD_RENEW_SLICE:
                    result = this.renewSlice(parameter);
                    break;
                default:
                    result = "Unimplemented method '" + methodName + "'";
                    break;
            }
        }catch(CertificateParsingException e){
            HashMap<String, Object> exceptionBody = new HashMap<>();
            //handleException(exceptionBody, e, GENI_CodeEnum.BADARGS);
            LOGGER.log(Level.WARNING,e.getMessage(),e);
            result = exceptionBody;
        } catch (Exception e){
            HashMap<String, Object> exceptionBody = new HashMap<>();
            //handleException(exceptionBody, e, GENI_CodeEnum.BADARGS);
            LOGGER.log(Level.WARNING,e.getMessage(),e);
            result = exceptionBody;
        }
        return result;

    }

    @Override
    public Object getVersion(final List<?> parameter) {

        final Map<String, Object> result = new HashMap<>();

        // todo: generate result here based on internal ontology
        this.createDummyAnswer(result);

        return result;
    }

    @Override
    public Object getCredential(X509Certificate userCertificate) throws Exception {
        HashMap<String,Object> result = new HashMap<>();
        //TODO get urn from properties
        URN sliceAuthorityURN = new URN("urn:publicid:IDN+localhost+authority+SA");
        KeyStoreManagement keyStoreManagement =  KeyStoreManagement.getInstance();
        X509Certificate sliceAuthorityCert = keyStoreManagement.getSliceAuthorityCert();
        URN ownerURN = X509Util.getURN(userCertificate);
        Credential credential =  CredentialFactory.newCredential(userCertificate,ownerURN ,sliceAuthorityCert,sliceAuthorityURN);
        String signedCredential = CredentialFactory.signCredential(credential);
        String output = "";
        int code = 0;
        result.put("value",signedCredential);
        result.put("code",code);
        result.put("output",output);
        return result;
    }

    @Override
    public Object register(List<?> parameter) throws Exception {
        if(parameter.size()> 1 || parameter.size() < 1){
            throw new RuntimeException();
        }


        Map<String, String> inputMap = (Map<String, String>) parameter.get(0);

        RegisterDelegate registerDelegate = new RegisterDelegate(inputMap).invoke();
        X509Certificate ownerCert = registerDelegate.getOwnerCert();
        URN ownerURN = registerDelegate.getOwnerURN();
        X509Certificate sliceCert = registerDelegate.getSliceCert();
        URN sliceURN = registerDelegate.getSliceURN();

        Credential sliceCredential  = CredentialFactory.newCredential(ownerCert,ownerURN, sliceCert,sliceURN);


        String sliceCertString = CredentialFactory.signCredential(sliceCredential);

        Model groupModel = createGroupModel(sliceURN, sliceCert);

        Model resultModel = sendGroup(groupModel);
        HashMap<String,Object> result = new HashMap<>();
        if(resultModel != null) {


            String output = "";
            int code = 0;

            result.put("output", output);
            result.put("code", code);
            result.put("value", sliceCertString);
        }
        return result;

    }

    @Override
    public Object renewSlice(List<?> parameter) throws JAXBException, DatatypeConfigurationException {
        HashMap<String,Object> result = new HashMap<>();
        Map<String, String> inputMap = (Map<String, String>) parameter.get(0);
        String credentialString = inputMap.get("credential");
        SignedCredential signedCredential = CredentialFactory.buildCredential(credentialString);
        URN sliceURN = new URN(signedCredential.getCredential().getTargetURN());

        Credential newCredential = signedCredential.getCredential();
        String expirationValue = inputMap.get("expiration");
        ParsePosition parsePos = new ParsePosition(0);
        Date newDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(expirationValue,parsePos);
        GregorianCalendar gregorianCalendar =
                (GregorianCalendar)GregorianCalendar.getInstance();
        gregorianCalendar.setTime(newDate);
        XMLGregorianCalendar xmlval = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);

        newCredential.setExpires(xmlval);

        String newCredentialString = CredentialFactory.signCredential(newCredential);

        if(newCredentialString != null) {


            String output = "";
            int code = 0;

            result.put("output", output);
            result.put("code", code);
            result.put("value", newCredentialString);
        }
        return result;


    }

    private Model createGroupModel(URN sliceURN, X509Certificate sliceCert) throws Exception {
        Model groupModel = ModelFactory.createDefaultModel();
        //TODO change this to URN.toURI()
        Resource resource = groupModel.createResource(IConfig.TOPOLOGY_NAMESPACE_VALUE+ sliceURN.getSubject());
        resource.addProperty(RDF.type, Omn.Topology );
        resource.addProperty(RDFS.label, resource.getURI());
        Property authInfo = groupModel.createProperty(Omn_lifecycle.hasAuthenticationInformation.getNameSpace(),Omn_lifecycle.hasAuthenticationInformation.getLocalName());
        authInfo.addProperty(RDF.type, OWL.FunctionalProperty);
        resource.addProperty(authInfo, X509Util.getCertificateBodyEncoded(sliceCert));

        return groupModel;
    }
    private Model sendGroup(Model groupModel) {
        String serializedModel = MessageUtil.serializeModel(groupModel,IMessageBus.SERIALIZATION_TURTLE);
        Model resultModel = SFA_AM_MDBSender.getInstance().sendRDFRequest(serializedModel, IMessageBus.TYPE_CREATE, IMessageBus.TARGET_RESERVATION);
        return resultModel;
    }




    private void createDummyAnswer(final Map<String, Object> result) {

    }


    private class RegisterDelegate {
        private Map<String, String> inputMap;
        private URN sliceURN;
        private URN ownerURN;
        private X509Certificate ownerCert;
        private X509Certificate sliceCert;

        public RegisterDelegate(Map<String, String> inputMap) {
            this.inputMap = inputMap;
        }

        public URN getSliceURN() {
            return sliceURN;
        }

        public URN getOwnerURN() {
            return ownerURN;
        }

        public X509Certificate getOwnerCert() {
            return ownerCert;
        }

        public X509Certificate getSliceCert() {
            return sliceCert;
        }

        public RegisterDelegate invoke() throws Exception {
            sliceURN = new URN(inputMap.get("urn"));
            String type = inputMap.get("type");
            String credentialString = inputMap.get("credential");
            JAXBContext context = JAXBContext.newInstance("org.fiteagle.north.sfa.aaa.jaxbClasses");
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(credentialString);
            SignedCredential sc = (SignedCredential) unmarshaller.unmarshal(reader);

            ownerURN = new URN(sc.getCredential().getOwnerURN());

            ownerCert = X509Util.buildX509Certificate(sc.getCredential().getOwnerGid());

            CertificateAuthority ca = CertificateAuthority.getInstance();
            sliceCert = ca.createSliceCertificate(sliceURN);
            return this;
        }
    }
}
