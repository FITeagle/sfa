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
    private URN ownerURN ;

    private URN targetURN;
    private X509Certificate targetCertificate;

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private KeyStoreManagement keyStoreManagement;

    public CredentialFactoryWorker(
            X509Certificate credentialCertificate,URN owner, X509Certificate targetCertificate, URN target) {

        this.keyStoreManagement = KeyStoreManagement.getInstance();
        this.userCertificate = credentialCertificate;
        this.ownerURN = owner;
        this.targetURN = target;
        this.targetCertificate = targetCertificate;

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




    private void setTargetGID() {
        try {
            credential.setTargetGid(X509Util
                    .getCertificateBodyEncoded(targetCertificate));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

    }



    private void setExpirationDate() {

        GregorianCalendar gregCalendar = new GregorianCalendar();
        gregCalendar
                .setTimeInMillis(java.lang.System.currentTimeMillis() + 10000000);
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

    public Credential getCredential() throws Exception {
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

    private void setTargetURN() {
        credential.setTargetURN(targetURN.toString());
    }

    private void setOwnerURN() {
        credential.setOwnerURN(ownerURN.toString());
    }

    private void setTargetCertificate() throws Exception {
        credential.setTargetGid(X509Util.getCertificateBodyEncoded(targetCertificate));
    }

}