package org.fiteagle.north.sfa.sa;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
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
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.fiteagle.api.core.*;
import org.fiteagle.north.sfa.aaa.CertificateAuthority;
import org.fiteagle.north.sfa.aaa.CredentialFactory;
import org.fiteagle.north.sfa.aaa.KeyStoreManagement;
import org.fiteagle.north.sfa.aaa.X509Util;
import org.fiteagle.north.sfa.aaa.jaxbClasses.Credential;
import org.fiteagle.north.sfa.aaa.jaxbClasses.SignedCredential;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;
import redstone.xmlrpc.XmlRpcArray;

import javax.security.auth.x500.X500Principal;
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
                    result = this.getCredential(cert, parameter);
                    break;
                case ISFA_SA.METHOD_REGISTER:
                    result = this.register(parameter);
                    break;
                case ISFA_SA.METHOD_RENEW_SLICE:
                    result = this.renewSlice(parameter);
                    break;
                case ISFA_SA.METHOD_RESOLVE:
                    result = this.resolve(parameter);
                    break;
                case ISFA_SA.METHOD_GET_KEYS:
                    result = this.getKeys(parameter);
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


        return result;
    }

    @Override
    public Object getCredential(X509Certificate userCertificate, List<?> parameter) throws Exception {
        HashMap<String,Object> result = new HashMap<>();

        //TODO get urn from properties
        Config config  = new Config();
        URN sliceAuthorityURN = new URN("urn:publicid:IDN+"+config.getProperty(IConfig.KEY_HOSTNAME)+"+authority+SA");
        KeyStoreManagement keyStoreManagement =  KeyStoreManagement.getInstance();
        X509Certificate sliceAuthorityCert = keyStoreManagement.getSliceAuthorityCert();
        URN ownerURN = X509Util.getURN(userCertificate);
        Credential credential = null;
        if(!parameter.isEmpty()){
            LOGGER.log(Level.SEVERE, "found parameter list");
            String urnString = "";
            Map<String, String> inputMap = (Map<String, String>) parameter.get(0);
            URN target = new URN(inputMap.get("urn"));

                LOGGER.log(Level.SEVERE, target.toString());
                PrivateKey caPrivateKey = keyStoreManagement.getSAPrivateKey();
                X509Certificate sliceCert = createSliceCert(sliceAuthorityCert,caPrivateKey,target);
                credential =  CredentialFactory.newCredential(userCertificate,ownerURN ,sliceCert,target);


            }else {
            credential = CredentialFactory.newCredential(userCertificate, ownerURN, sliceAuthorityCert, sliceAuthorityURN);
        }
        String signedCredential = CredentialFactory.signCredential(credential);
        String output = "";
        int code = 0;
        result.put("value",signedCredential);
        result.put("code",code);
        result.put("output",output);
        LOGGER.log(Level.SEVERE, signedCredential);
        return result;
    }

    private X509Certificate createSliceCert(X509Certificate caCert,PrivateKey caPrivateKey, URN target) throws CertificateException, OperatorCreationException, IOException {


            X500Name issuer = new JcaX509CertificateHolder(caCert).getSubject();

            ContentSigner contentsigner = new JcaContentSignerBuilder(
                    "SHA1WithRSAEncryption").build(caPrivateKey);

            X500Name subject = createX500Name(target.getSubject());
            PublicKey aPublic = createSlicePublicKey();
            SubjectPublicKeyInfo subjectsPublicKeyInfo = getSubjectPublicKey(aPublic);
            X509v3CertificateBuilder ca_gen = new X509v3CertificateBuilder(issuer,
                    new BigInteger(new SecureRandom().generateSeed(256)),
                    new Date(),
                    new Date(System.currentTimeMillis() + 31500000000L), subject,
                    subjectsPublicKeyInfo);
            BasicConstraints ca_constraint = new BasicConstraints(false);
            ca_gen.addExtension(X509Extension.basicConstraints, true, ca_constraint);
            GeneralNames subjectAltName = new GeneralNames(new GeneralName(
                    GeneralName.uniformResourceIdentifier, target.toString()));

            X509Extension extension = new X509Extension(false, new DEROctetString(
                    subjectAltName));
            ca_gen.addExtension(X509Extension.subjectAlternativeName, false,
                    extension.getParsedValue());
            X509CertificateHolder holder = (X509CertificateHolder) ca_gen
                    .build(contentsigner);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf
                    .generateCertificate(new ByteArrayInputStream(holder
                            .getEncoded()));
        }

    private SubjectPublicKeyInfo getSubjectPublicKey(PublicKey aPublic) throws IOException {
        SubjectPublicKeyInfo subPubInfo = new SubjectPublicKeyInfo(
                (ASN1Sequence) ASN1Sequence.fromByteArray(aPublic.getEncoded()));
        return subPubInfo;
    }

    private PublicKey createSlicePublicKey() {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair().getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private X500Name createX500Name(String username) {
        X500Principal prince = new X500Principal("CN=" + username);
        X500Name x500Name = new X500Name(prince.getName());
        return x500Name;
    }
    @Override
    public Object register(List<?> parameter) throws Exception {
        if(parameter.size()> 1 || parameter.size() < 1){
            throw new RuntimeException();
        }


        Map<String, Object> inputMap = (Map<String, Object>) parameter.get(0);

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

    @Override
    public Object resolve(List<?> parameter) {
        LOGGER.log(Level.INFO, "Logging Resolve ");
        for(Object o: parameter){
            LOGGER.log(Level.INFO,parameter.toString());
        }

        HashMap<String,Object> result = new HashMap<>();
        String output = "";
        int code = 0;

        result.put("output", output);
        result.put("code", code);
        result.put("value", "");
        return result;
    }

    @Override
    public Object getKeys(List<?> parameter) {
        LOGGER.log(Level.INFO, "Logging getKeys ");
        for(Object o: parameter){
            LOGGER.log(Level.INFO,parameter.toString());
        }

        HashMap<String,Object> result = new HashMap<>();
        String output = "";
        int code = 0;

        result.put("output", output);
        result.put("code", code);
        result.put("value", "");
        return result;
    }

    private Model createGroupModel(URN sliceURN, X509Certificate sliceCert) throws Exception {
        Model groupModel = ModelFactory.createDefaultModel();
        //TODO change this to URN.toURI()
        Resource resource = groupModel.createResource("http://"+ sliceURN.getDomain() + "/topology/"+sliceURN.getSubject());
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



    private class RegisterDelegate {
        private Map<String, Object> inputMap;
        private URN sliceURN;
        private URN ownerURN;
        private X509Certificate ownerCert;
        private X509Certificate sliceCert;

        public RegisterDelegate(Map<String, Object> inputMap) {
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
            sliceURN = new URN((String)inputMap.get("urn"));
            String type =(String) inputMap.get((String)"type");
            String credentialString = (String)inputMap.get("credential");
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

/*
        public RegisterDelegate invoke() throws Exception {
            sliceURN = new URN(()inputMap.get("urn"));
            String type = inputMap.get("type");
            XmlRpcArray credentialArray = inputMap.get("credentials");
            String credentialString = credentialArray.getString(0);
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
        */
    }
}
