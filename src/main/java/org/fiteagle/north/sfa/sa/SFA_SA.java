package org.fiteagle.north.sfa.sa;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.north.sfa.aaa.CertificateAuthority;
import org.fiteagle.north.sfa.aaa.CredentialFactory;
import org.fiteagle.north.sfa.aaa.X509Util;
import org.fiteagle.north.sfa.aaa.jaxbClasses.Credential;
import org.fiteagle.north.sfa.aaa.jaxbClasses.SignedCredential;
import org.fiteagle.north.sfa.am.GENI_CodeEnum;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.util.URN;

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
                default:
                    result = "Unimplemented method '" + methodName + "'";
                    break;
            }
        }catch(CertificateParsingException e){
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
    public Object getCredential(X509Certificate certificate) throws CertificateParsingException {
        HashMap<String,Object> result = new HashMap<>();
        //TODO get urn from properties
        URN sliceAuthorityURN = new URN("urn:publicid:IDN+localhost+authority+SA");
        Credential credential =  CredentialFactory.newCredential(certificate, sliceAuthorityURN);
        String signedCredential = CredentialFactory.signCredential(credential);
        String output = "";
        int code = 0;
        result.put("value",signedCredential);
        result.put("code",code);
        result.put("output",output);
        return result;
    }

    @Override
    public Object register(List<?> parameter) {
        if(parameter.size()> 1 || parameter.size() < 1){
            throw new RuntimeException();
        }


        Map<String, String> inputMap = (Map<String, String>) parameter.get(0);

        URN desiredURN =  new URN(inputMap.get("urn"));


        CertificateAuthority ca = CertificateAuthority.getInstance();
        X509Certificate saCert = ca.getSliceAuthorityCertificate();


        HashMap<String,Object> result = new HashMap<>();
        String dummyCred  ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<signed-credential xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://www.protogeni.net/resources/credential/credential.xsd\" xsi:schemaLocation=\"http://www.protogeni.net/resources/credential/ext/policy/1 http://www.protogeni.net/resources/credential/ext/policy/1/policy.xsd\">\n" +
                "  <credential xml:id=\"ref89B5DD46A9185644\">\n" +
                " <type>privilege</type>\n" +
                " <serial>133127</serial>\n" +
                " <owner_gid>\n" +
                "MIIECDCCA3GgAwIBAgIDAKO+MA0GCSqGSIb3DQEBBAUAMIG1MQswCQYDVQQGEwJC\n" +
                "RTELMAkGA1UECBMCT1YxDjAMBgNVBAcTBUdoZW50MRgwFgYDVQQKEw9pTWluZHMg\n" +
                "LSBpbGFiLnQxHjAcBgNVBAsTFUNlcnRpZmljYXRlIEF1dGhvcml0eTEjMCEGA1UE\n" +
                "AxMaYm9zcy53YWxsMi5pbGFidC5pbWluZHMuYmUxKjAoBgkqhkiG9w0BCQEWG3Z3\n" +
                "YWxsLW9wc0BhdGxhbnRpcy51Z2VudC5iZTAeFw0xNDA2MjAxNDAyMTlaFw0xNTA2\n" +
                "MjAxNDAyMTlaMIGwMQswCQYDVQQGEwJCRTELMAkGA1UECBMCT1YxGDAWBgNVBAoT\n" +
                "D2lNaW5kcyAtIGlsYWIudDEdMBsGA1UECxMUaW1pbmRzLXdhbGwyLndpbGxuZXIx\n" +
                "LTArBgNVBAMTJGViMTM0NTM1LWY2ZDYtMTFlMy1iNDA3LTAwMTUxN2JlY2RjMTEs\n" +
                "MCoGCSqGSIb3DQEJARYdd2lsbG5lckB3YWxsMi5pbGFidC5pbWluZHMuYmUwgZ8w\n" +
                "DQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAMRIxqO8x/NuB3kIx1o/9Xc7kWjtMfV9\n" +
                "MN0latj8QaXHQrdGVftp3wMV9aHd13vULGHdvpZIR0KeKX0sft4E1GgB9FY4rKeX\n" +
                "BqEgi9W1FSQMVDMnRkXkSmfh8bBOpMH6xvaGQLnFUUH8KKnjQOcuaUEue1Zv0YX7\n" +
                "jcL/ylonA8EpAgMBAAGjggEnMIIBIzAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBSc\n" +
                "4sTpQozlRSfqovjJEulYPJyxKzCBjgYDVR0RBIGGMIGDhjN1cm46cHVibGljaWQ6\n" +
                "SUROK3dhbGwyLmlsYWJ0LmltaW5kcy5iZSt1c2VyK3dpbGxuZXKBHXdpbGxuZXJA\n" +
                "d2FsbDIuaWxhYnQuaW1pbmRzLmJlhi11cm46dXVpZDplYjEzNDUzNS1mNmQ2LTEx\n" +
                "ZTMtYjQwNy0wMDE1MTdiZWNkYzEwYwYIKwYBBQUHAQEEVzBVMFMGFGmDzJOAqJjM\n" +
                "qMe9saeAgKqu14obhjtodHRwczovL3d3dy53YWxsMi5pbGFidC5pbWluZHMuYmU6\n" +
                "MTIzNjkvcHJvdG9nZW5pL3htbHJwYy9zYTANBgkqhkiG9w0BAQQFAAOBgQATj7Q3\n" +
                "VdRqe5OWA2E0SpOc5twzA6qkzIHeFxAmyTwtM60b2cv37SSYeUoo6tXzEp7ZGB0Z\n" +
                "Dd61d4A6/vKYojvvqyC5V3pIHQ9EsiElFZhWkZ07FKn3GHBaAd1QMhIojkeX7u+e\n" +
                "3GDTe4DL19vMlNLUlL+ie/9iZCR/LroaH5eB4A==\n" +
                "</owner_gid>\n" +
                " <owner_urn>urn:publicid:IDN+wall2.ilabt.iminds.be+user+willner</owner_urn>\n" +
                " <target_gid>\n" +
                "MIIDeTCCAuKgAwIBAgIDAMBjMA0GCSqGSIb3DQEBBAUAMIG1MQswCQYDVQQGEwJC\n" +
                "RTELMAkGA1UECBMCT1YxDjAMBgNVBAcTBUdoZW50MRgwFgYDVQQKEw9pTWluZHMg\n" +
                "LSBpbGFiLnQxHjAcBgNVBAsTFUNlcnRpZmljYXRlIEF1dGhvcml0eTEjMCEGA1UE\n" +
                "AxMaYm9zcy53YWxsMi5pbGFidC5pbWluZHMuYmUxKjAoBgkqhkiG9w0BCQEWG3Z3\n" +
                "YWxsLW9wc0BhdGxhbnRpcy51Z2VudC5iZTAeFw0xNDA3MTEwNTM5NDBaFw0yMDAx\n" +
                "MDEwNjM5NDBaMIGtMQswCQYDVQQGEwJCRTELMAkGA1UECBMCT1YxGDAWBgNVBAoT\n" +
                "D2lNaW5kcyAtIGlsYWIudDEZMBcGA1UECxMQaW1pbmRzLXdhbGwyLmZvbzEtMCsG\n" +
                "A1UEAxMkMjI5ZGUwZGItMDhjNi0xMWU0LWI0MDctMDAxNTE3YmVjZGMxMS0wKwYJ\n" +
                "KoZIhvcNAQkBFh5hbGV4YW5kZXIud2lsbG5lckB0dS1iZXJsaW4uZGUwgZ8wDQYJ\n" +
                "KoZIhvcNAQEBBQADgY0AMIGJAoGBAKxs6H8qJBIWA+kqZP0ttYb6Hav2IDnfAuxc\n" +
                "0etNNfryjFnAq3IXxeXyDHR1Zf9SF5ZE9fDIXEWLdHX47m2ZpWbw6d9gmROKweWV\n" +
                "5NtPb8DhnTyykHe3yWabJeykSXVSjaxcTnigvFSkYngjS0xbs2BX7bhLs9WJ+0Di\n" +
                "WYM3D825AgMBAAGjgZwwgZkwHQYDVR0OBBYEFOIf71bwjk7fppBNc5qBTxYBk6TN\n" +
                "MGoGA1UdEQRjMGGGMHVybjpwdWJsaWNpZDpJRE4rd2FsbDIuaWxhYnQuaW1pbmRz\n" +
                "LmJlK3NsaWNlK2Zvb4YtdXJuOnV1aWQ6MjI5ZGUwZGItMDhjNi0xMWU0LWI0MDct\n" +
                "MDAxNTE3YmVjZGMxMAwGA1UdEwEB/wQCMAAwDQYJKoZIhvcNAQEEBQADgYEAhgGs\n" +
                "EEJNqxEtwoeT16POMsaoS/BWoF1hxsd/1xSeTh9kg+TppJC2VAcJs75MaubPf/PX\n" +
                "Q6bAtDzNRExEciuHnu68eftGP2M16I1cqXn9x4F96mnVmaBmjxxRkgBspd5dhlpV\n" +
                "NJg6fohix1ZzkLFxYPZT2ogRJILZVz2qCUzVV1A=\n" +
                "</target_gid>\n" +
                " <target_urn>urn:publicid:IDN+wall2.ilabt.iminds.be+slice+foo</target_urn>\n" +
                " <uuid>22d1de1f-08c6-11e4-b407-001517becdc1</uuid>\n" +
                " <expires>2014-07-11T08:39:40Z</expires>\n" +
                "  <privileges>\n" +
                "<privilege><name>*</name><can_delegate>1</can_delegate></privilege>\n" +
                "</privileges></credential>\n" +
                "  <signatures>\n" +
                "    <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\" xml:id=\"Sig_ref89B5DD46A9185644\">\n" +
                " <SignedInfo>\n" +
                "  <CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/>\n" +
                "  <SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>\n" +
                "  <Reference URI=\"#ref89B5DD46A9185644\">\n" +
                "    <Transforms>\n" +
                "      <Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>\n" +
                "    </Transforms>\n" +
                "    <DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>\n" +
                "    <DigestValue>2HXqxYbZzX3YtLLoMDbg6ZeZ/VI=</DigestValue>\n" +
                "    </Reference>\n" +
                " </SignedInfo>\n" +
                " <SignatureValue>hhw4ZwbcCJ8TwaGr3G+PxQQVm3Lgkku7wraDt2jj0WE0o0RhTPfh/EYR8n4+5Jc+\n" +
                "+zOitGXz5/NvCLjkJyJNxM0qJ4SJtITZRV0Ny6CwMD0FMH4kddlmKSJsrOtqdk9b\n" +
                "hV0enHeLjOLqp7BW+5iEvHV+oUn7BIp6FsNlFaC9reE=</SignatureValue>\n" +
                " <KeyInfo>\n" +
                "  <X509Data>\n" +
                "<X509Certificate>MIIDsDCCAxmgAwIBAgICA/QwDQYJKoZIhvcNAQEEBQAwgbUxCzAJBgNVBAYTAkJF\n" +
                "MQswCQYDVQQIEwJPVjEOMAwGA1UEBxMFR2hlbnQxGDAWBgNVBAoTD2lNaW5kcyAt\n" +
                "IGlsYWIudDEeMBwGA1UECxMVQ2VydGlmaWNhdGUgQXV0aG9yaXR5MSMwIQYDVQQD\n" +
                "Expib3NzLndhbGwyLmlsYWJ0LmltaW5kcy5iZTEqMCgGCSqGSIb3DQEJARYbdndh\n" +
                "bGwtb3BzQGF0bGFudGlzLnVnZW50LmJlMB4XDTEzMDkwMjA2NTkyNVoXDTE5MDIy\n" +
                "MzA3NTkyNVowgakxCzAJBgNVBAYTAkJFMQswCQYDVQQIEwJPVjEYMBYGA1UEChMP\n" +
                "aU1pbmRzIC0gaWxhYi50MRgwFgYDVQQLEw9pbWluZHMtd2FsbDIuc2ExLTArBgNV\n" +
                "BAMTJDk1YWM1Njk2LTEzYTUtMTFlMy05NjZhLTAwMTUxN2JlY2RjMTEqMCgGCSqG\n" +
                "SIb3DQEJARYbdndhbGwtb3BzQGF0bGFudGlzLnVnZW50LmJlMIGfMA0GCSqGSIb3\n" +
                "DQEBAQUAA4GNADCBiQKBgQCgyCFyx3jrEwbsoXJrlpcyuP4oCNmWGjL0hlYZFoxD\n" +
                "1sCgTxVMWIAlDtfuIdNgU98jaoKDJjXsKOZGDBBWo/4IWqISmIB/LmVxY58b5r6e\n" +
                "89i4yfuYGvAqm8zpbsnZ958GT6TnhdkWwz90MI2fZXo1Ce1DaR7l0pQhkuTu9li3\n" +
                "+wIDAQABo4HYMIHVMB0GA1UdDgQWBBR+oou+gYJEndilQR9BUEkCbU8tgTA+BgNV\n" +
                "HREENzA1hjN1cm46cHVibGljaWQ6SUROK3dhbGwyLmlsYWJ0LmltaW5kcy5iZSth\n" +
                "dXRob3JpdHkrc2EwDwYDVR0TAQH/BAUwAwEB/zBjBggrBgEFBQcBAQRXMFUwUwYU\n" +
                "aYPMk4ComMyox72xp4CAqq7XihuGO2h0dHBzOi8vd3d3LndhbGwyLmlsYWJ0Lmlt\n" +
                "aW5kcy5iZToxMjM2OS9wcm90b2dlbmkveG1scnBjL3NhMA0GCSqGSIb3DQEBBAUA\n" +
                "A4GBAI3lK1OEeDOlSTW1haUYkWTCk06xeM4YXUCOQghUQe2lxuNAYYU46BQPn2+4\n" +
                "FX6ohz16e/e1y+wcoYn0USo6G79xB6jItUL2dQviE1cLRic/BXzAp5hJxBdvGUAu\n" +
                "SEa8hHwPVB57wmU8xpDnIRML9lfVyZPWs+FdscUfOTMTilw4</X509Certificate>\n" +
                "<X509SubjectName>emailAddress=vwall-ops@atlantis.ugent.be,CN=95ac5696-13a5-11e3-966a-001517becdc1,OU=iminds-wall2.sa,O=iMinds - ilab.t,ST=OV,C=BE</X509SubjectName>\n" +
                "<X509IssuerSerial>\n" +
                "<X509IssuerName>emailAddress=vwall-ops@atlantis.ugent.be,CN=boss.wall2.ilabt.iminds.be,OU=Certificate Authority,O=iMinds - ilab.t,L=Ghent,ST=OV,C=BE</X509IssuerName>\n" +
                "<X509SerialNumber>1012</X509SerialNumber>\n" +
                "</X509IssuerSerial>\n" +
                "</X509Data>\n" +
                "  <KeyValue>\n" +
                "<RSAKeyValue>\n" +
                "<Modulus>\n" +
                "oMghcsd46xMG7KFya5aXMrj+KAjZlhoy9IZWGRaMQ9bAoE8VTFiAJQ7X7iHTYFPf\n" +
                "I2qCgyY17CjmRgwQVqP+CFqiEpiAfy5lcWOfG+a+nvPYuMn7mBrwKpvM6W7J2fef\n" +
                "Bk+k54XZFsM/dDCNn2V6NQntQ2ke5dKUIZLk7vZYt/s=\n" +
                "</Modulus>\n" +
                "<Exponent>\n" +
                "AQAB\n" +
                "</Exponent>\n" +
                "</RSAKeyValue>\n" +
                "</KeyValue>\n" +
                " </KeyInfo>\n" +
                "</Signature>\n" +
                "  </signatures>\n" +
                "</signed-credential>";
        String output ="";
        int code = 0;

        result.put("output",output);
        result.put("code", code);
        result.put("value",  dummyCred);
        return result;

    }


    private void createDummyAnswer(final Map<String, Object> result) {

    }




}
