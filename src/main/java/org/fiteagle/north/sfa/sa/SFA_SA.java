package org.fiteagle.north.sfa.sa;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public Object getCredential(X509Certificate certificate) {
        HashMap<String,Object> result = new HashMap<>();
        String dummyCert = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<signed-credential xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"><credential\n" +
                "id=\"0bca5e6b-c885-48d9-9655-0d3a2242ed29\"\n" +
                "xml:id=\"0bca5e6b-c885-48d9-9655-0d3a2242ed29\"><type>privilege</type><owner_gid>MIIELTCCAxWgAwIBAgKCAQDwlt4ioYoyJbXy9/YtXolvNWyJvXo44eBovP/xywjCYJbrNIW0RUTequCItZvmDH/UX6upGHQCDncGtMzdCAesPQW4afk9u/nq+lqqTgCFfjRFB/nSlWjiTDqWQOqQdU++MwTEIeOUCJWN/2joYd18+9mCCdhJNnSVugrPd2PNzQGzk5Cv5FvJMIxs3qjasFfKeCm9w5qer6sSDDGQ8TV/NFHEtBOZL/VPQ1h+CE792yBbHWz1vAX2KTWAtwlDimx8uN6RD2Jv2en3hzFrrOtMGfJUmMiluhbsq0XgaQWT5+NP/YHcaGbcSb2PiNa3MMc6cKqOaeQYW2VuKFidb4dUMA0GCSqGSIb3DQEBBQUAMFsxCzAJBgNVBAYTAkRFMQ8wDQYDVQQIDAZCZXJsaW4xDzANBgNVBAcMBkJlcmxpbjEMMAoGA1UECgwDVFVCMQswCQYDVQQLDAJBVjEPMA0GA1UEAwwGdGVzdENBMB4XDTE0MDYyNjE5MTkyMloXDTE1MDYyNjA5MTkyMlowFzEVMBMGA1UEAwwMdGVzdGluZzFAbWJwMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsMo1u+YN4B4BwuvHACQAUwweAzo0tSi604m4qnA7A52PP0+4bo6ivKedQyVzPHot3h8+1FRzCgc4b+GnlIP4XHO0uAmZORNAze+lEKtj4wrbW1edVByEqDopNNybBHyE+bm7KBEL4LGyMqy9yCtXp8bWLSu2R4R9fsPpmYHNzONSFxNuIqDmA0hsqpqkJATuEMaruM1vqtV+oJiyMBLZz+EZLLdonoeMcGNGIG3VhzibGSCFnT684f7DKS0xnesB07yPE87qsSxoSg6RyzbuBKVxk1AWS7rD2rdRoxMURQl5Et5lZXZmo7R6Yn1dVl8PwaLl75aC60R+mKRpC2U8kwIDAQABoz8wPTAMBgNVHRMBAf8EAjAAMC0GA1UdEQQmMCSGInVybjpwdWJsaWNpZDpJRE4rbWJwK3VzZXIrdGVzdGluZzEwDQYJKoZIhvcNAQEFBQADggEBAKl2YmloEh5T2Y+a2RR/6eq+rIKGpEchjG5lnmrVM//Nb3AgBacchwMCbJHKAoua5h8DaXabbginEHet5lAF9J2fBVTka73Tbj32k6Zpgv3/gbzWTDrfJFD3ckWKGY7PwTN/j6QoZIY6ZeAQbeOBQJrHwNdrQmFcphSgmjGagT6ZHcZlt6A2zNCg8DRCDBe7YswnM2ADIqa3wOeI0gD7ycehEpanmXxTxQCcx8LPsJbGaadzAZ7OmpCoupR6Pv8pPoSoFnBsTNUKRFPgdSlP+AhIx37xVbrX7724LDctpfwZzddNHBMnxCdEe576w3tjfLzAf9gJQHFb+PQhHQNA8IE=</owner_gid><owner_urn>urn:publicid:IDN+mbp+user+testing1</owner_urn><target_urn>urn:publicid:IDN+mbp+authority+sa</target_urn><target_gid>MIID5TCCA06gAwIBAgIBEDANBgkqhkiG9w0BAQUFADCBijELMAkGA1UEBhMCREUxDzANBgNVBAgMBkJlcmxpbjEPMA0GA1UEBwwGQmVybGluMRkwFwYDVQQKDBBGcmF1bmhvZmVyIEZPS1VTMQ0wCwYDVQQLDAROR05JMS8wLQYDVQQDDCZjYS5maXRlYWdsZS1mdXNlY28uZm9rdXMuZnJhdW5ob2Zlci5kZTAeFw0xMzA2MDUxMzU2MTVaFw0xNDA2MDUxMzU2MTVaMHkxCzAJBgNVBAYTAkRFMQ8wDQYDVQQIDAZCZXJsaW4xGTAXBgNVBAoMEEZyYXVuaG9mZXIgRk9LVVMxDTALBgNVBAsMBE5HTkkxLzAtBgNVBAMMJnNhLmZpdGVhZ2xlLWZ1c2Vjby5mb2t1cy5mcmF1bmhvZmVyLmRlMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDK/pW+TituO/99j7/QAtvkumjBv5WgB47WasPlscy+RjLvbclgfNSjfvJ4affzS3aEhyVhxJCxqF5N12dy6E1m/75hKccPgBFHe2C6UcSILN1UK6w3u4gmohlLIeiAGR8HcSduoJ1rnS9H6MUHtX6leVzwsjjxj5Kth8iv1ZJ3VQIDAQABo4IBaTCCAWUwDwYDVR0TAQH/BAUwAwEB/zAsBglghkgBhvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYEFOcb4ZyRC57cXrlYtX2kzQ2evFBCMIGpBgNVHSMEgaEwgZ6hgZCkgY0wgYoxCzAJBgNVBAYTAkRFMQ8wDQYDVQQIDAZCZXJsaW4xDzANBgNVBAcMBkJlcmxpbjEZMBcGA1UECgwQRnJhdW5ob2ZlciBGT0tVUzENMAsGA1UECwwETkdOSTEvMC0GA1UEAwwmY2EuZml0ZWFnbGUtZnVzZWNvLmZva3VzLmZyYXVuaG9mZXIuZGWCCQCbHKdifYh3XzBMBgNVHREERTBDhkF1cm46cHVibGljaWQ6SUROK2ZpdGVhZ2xlLWZ1c2Vjby5mb2t1cy5mcmF1bmhvZmVyLmRlK2F1dGhvcml0eStzYTALBgNVHQ8EBAMCBeAwDQYJKoZIhvcNAQEFBQADgYEAh+ToI9ce0dfEOCrWV4Ak6rE2rL71DN5vCbWi9N96x1KgUa5P2/bieWe3YlCXE4X0ilIWPaubKiYKkm5axcfA9K3YJ3v/2o9JO1y2xM41PJ523vtiRRUTNeSbNho8T8sI3bNe60n+7XGvrmfuazNJP3wJHoeGkZubff+rDMrzT5s=</target_gid><expires>2014-07-11T07:46:48.717+02:00</expires><privileges><privilege><name>*</name><can_delegate>false</can_delegate></privilege></privileges></credential><signatures><Signature\n" +
                "xmlns=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
                "<SignedInfo>\n" +
                "<CanonicalizationMethod\n" +
                "Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"></CanonicalizationMethod>\n" +
                "<SignatureMethod\n" +
                "Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></SignatureMethod>\n" +
                "<Reference URI=\"#0bca5e6b-c885-48d9-9655-0d3a2242ed29\">\n" +
                "<Transforms>\n" +
                "<Transform\n" +
                "Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"></Transform>\n" +
                "</Transforms>\n" +
                "<DigestMethod\n" +
                "Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></DigestMethod>\n" +
                "<DigestValue>m6MrZn9atEtrYsWxbFRMeewOSr0=</DigestValue>\n" +
                "</Reference>\n" +
                "</SignedInfo>\n" +
                "<SignatureValue>\n" +
                "gQBoekx8EDcX+Uonf93jJIpokdUD45Bl7fspT4oKVhs/Kl5kCR3RDeUfB82cths9W+ygHRCfrGoa\n" +
                "DZtjyWLU8/xqfORLxsfD1W8CLe1ffu6NP0U61U7spDvTLrXS3jqRK/z6vP3gLotzM+OZjA8h1JAv\n" +
                "FWaRcvf/79R9//lpoaA=\n" +
                "</SignatureValue>\n" +
                "<KeyInfo>\n" +
                "<X509Data>\n" +
                "<X509Certificate>MIID5TCCA06gAwIBAgIBEDANBgkqhkiG9w0BAQUFADCBijELMAkGA1UEBhMCREUxDzANBgNVBAgM\n" +
                "BkJlcmxpbjEPMA0GA1UEBwwGQmVybGluMRkwFwYDVQQKDBBGcmF1bmhvZmVyIEZPS1VTMQ0wCwYD\n" +
                "VQQLDAROR05JMS8wLQYDVQQDDCZjYS5maXRlYWdsZS1mdXNlY28uZm9rdXMuZnJhdW5ob2Zlci5k\n" +
                "ZTAeFw0xMzA2MDUxMzU2MTVaFw0xNDA2MDUxMzU2MTVaMHkxCzAJBgNVBAYTAkRFMQ8wDQYDVQQI\n" +
                "DAZCZXJsaW4xGTAXBgNVBAoMEEZyYXVuaG9mZXIgRk9LVVMxDTALBgNVBAsMBE5HTkkxLzAtBgNV\n" +
                "BAMMJnNhLmZpdGVhZ2xlLWZ1c2Vjby5mb2t1cy5mcmF1bmhvZmVyLmRlMIGfMA0GCSqGSIb3DQEB\n" +
                "AQUAA4GNADCBiQKBgQDK/pW+TituO/99j7/QAtvkumjBv5WgB47WasPlscy+RjLvbclgfNSjfvJ4\n" +
                "affzS3aEhyVhxJCxqF5N12dy6E1m/75hKccPgBFHe2C6UcSILN1UK6w3u4gmohlLIeiAGR8HcSdu\n" +
                "oJ1rnS9H6MUHtX6leVzwsjjxj5Kth8iv1ZJ3VQIDAQABo4IBaTCCAWUwDwYDVR0TAQH/BAUwAwEB\n" +
                "/zAsBglghkgBhvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE\n" +
                "FOcb4ZyRC57cXrlYtX2kzQ2evFBCMIGpBgNVHSMEgaEwgZ6hgZCkgY0wgYoxCzAJBgNVBAYTAkRF\n" +
                "MQ8wDQYDVQQIDAZCZXJsaW4xDzANBgNVBAcMBkJlcmxpbjEZMBcGA1UECgwQRnJhdW5ob2ZlciBG\n" +
                "T0tVUzENMAsGA1UECwwETkdOSTEvMC0GA1UEAwwmY2EuZml0ZWFnbGUtZnVzZWNvLmZva3VzLmZy\n" +
                "YXVuaG9mZXIuZGWCCQCbHKdifYh3XzBMBgNVHREERTBDhkF1cm46cHVibGljaWQ6SUROK2ZpdGVh\n" +
                "Z2xlLWZ1c2Vjby5mb2t1cy5mcmF1bmhvZmVyLmRlK2F1dGhvcml0eStzYTALBgNVHQ8EBAMCBeAw\n" +
                "DQYJKoZIhvcNAQEFBQADgYEAh+ToI9ce0dfEOCrWV4Ak6rE2rL71DN5vCbWi9N96x1KgUa5P2/bi\n" +
                "eWe3YlCXE4X0ilIWPaubKiYKkm5axcfA9K3YJ3v/2o9JO1y2xM41PJ523vtiRRUTNeSbNho8T8sI\n" +
                "3bNe60n+7XGvrmfuazNJP3wJHoeGkZubff+rDMrzT5s=</X509Certificate>\n" +
                "</X509Data>\n" +
                "<KeyValue>\n" +
                "<RSAKeyValue>\n" +
                "<Modulus>\n" +
                "yv6Vvk4rbjv/fY+/0ALb5Lpowb+VoAeO1mrD5bHMvkYy723JYHzUo37yeGn380t2hIclYcSQsahe\n" +
                "TddncuhNZv++YSnHD4ARR3tgulHEiCzdVCusN7uIJqIZSyHogBkfB3EnbqCda50vR+jFB7V+pXlc\n" +
                "8LI48Y+SrYfIr9WSd1U=\n" +
                "</Modulus>\n" +
                "<Exponent>AQAB</Exponent>\n" +
                "</RSAKeyValue>\n" +
                "</KeyValue>\n" +
                "</KeyInfo>\n" +
                "</Signature></signatures></signed-credential>";
        String output = "";
        int code = 0;
        result.put("value",dummyCert);
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
