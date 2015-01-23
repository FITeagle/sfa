package org.fiteagle.north.sfa.aaa;


        import java.security.cert.CertificateParsingException;
        import java.security.cert.X509Certificate;
        import java.util.GregorianCalendar;
        import java.util.UUID;

        import javax.xml.datatype.DatatypeConfigurationException;
        import javax.xml.datatype.DatatypeFactory;
        import javax.xml.datatype.XMLGregorianCalendar;

        import org.fiteagle.north.sfa.aaa.jaxbClasses.Credential;
        import org.fiteagle.north.sfa.aaa.jaxbClasses.Privilege;
        import org.fiteagle.north.sfa.aaa.jaxbClasses.Privileges;
        import org.fiteagle.north.sfa.util.URN;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;

public class CredentialFactoryWorker {

    private Credential credential;
    private X509Certificate userCertificate;
    URN target;
    private X509Certificate targetCertificate;

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private KeyStoreManagement keyStoreManagement;

    public CredentialFactoryWorker(
            X509Certificate credentialCertificate, URN target) {

        this.keyStoreManagement = KeyStoreManagement.getInstance();
        this.userCertificate = credentialCertificate;
        this.target = target;

    }

    private void setTargetCertificate() {
        try {
            targetCertificate = getTargetCertificate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private X509Certificate getTargetCertificate() throws Exception {

        if (target.getType().equalsIgnoreCase("slice")) {


            try {

                return keyStoreManagement.getResourceCertificate(target
                        .getSubjectAtDomain());
            } catch (CertificateAuthority.CertificateNotFoundException e) {
                X509Certificate groupCertificate = CertificateAuthority
                        .getInstance().createCertificate(target,null);
                keyStoreManagement.storeResourceCertificate(groupCertificate);
                return groupCertificate;
            }

        }
        if (target.getType().equalsIgnoreCase("authority")) {

            return keyStoreManagement.getSliceAuthorityCert();
        }
        throw new RuntimeException();
    }

    private void setId() {
        credential.setId(UUID.randomUUID().toString());

    }

    private void setType() {
        credential.setType("privilege");

    }

    private void setOwnerGID() {
        X509Certificate returnCert = userCertificate;
        CertificateAuthority ca = CertificateAuthority.getInstance();
        if (X509Util.isSelfSigned(userCertificate)) {
            try {
                returnCert = ca.createCertificate(userCertificate);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage());
            }
        }
        String returnString;
        try {
            returnString = X509Util.getCertificateBodyEncoded(returnCert);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
        userCertificate = returnCert;
        credential.setOwnerGid(returnString);

    }

    private void setOwnerURN() {
        URN urn = getSubjectUrn();
        credential.setOwnerURN(urn.toString());

    }

    private URN getSubjectUrn() {
        try {
            return X509Util.getURN(userCertificate);
        } catch (CertificateParsingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    private void setTargetGID() {
        try {
            credential.setTargetGid(X509Util
                    .getCertificateBodyEncoded(targetCertificate));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    private void setTargetURN() {
        credential.setTargetURN(target.toString());
    }

    private void setExpirationDate() {

        GregorianCalendar gregCalendar = new GregorianCalendar();
        gregCalendar
                .setTimeInMillis(java.lang.System.currentTimeMillis() + 100000);
        XMLGregorianCalendar expirationDate = null;
        try {
            expirationDate = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(gregCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        credential.setExpires(expirationDate);

    }

    private void setPrivleges() {
        Privileges privileges = new Privileges();
        Privilege userPriv = new Privilege();
        userPriv.setCanDelegate(false);
        userPriv.setName("*");
        privileges.getPrivilege().add(userPriv);
        credential.setPrivileges(privileges);

    }





    public class UnsupportedTarget extends RuntimeException {

        private static final long serialVersionUID = -7821229625163019933L;

    }



    public void setKeyStoreManager(KeyStoreManagement keyStoreManagement){
        this.keyStoreManagement = keyStoreManagement;
    }

    public Credential getCredential() {
        credential = new Credential();
        setTargetCertificate();
        setId();
        setType();
        setOwnerGID();
        setOwnerURN();
        setTargetGID();
        setTargetURN();
        setExpirationDate();
        setPrivleges();
        return credential;
    }

}