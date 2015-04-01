package org.fiteagle.north.sfa.aaa;


        import java.io.ByteArrayOutputStream;
        import java.io.StringReader;
        import java.io.StringWriter;
        import java.security.cert.X509Certificate;

        import javax.xml.bind.JAXBContext;
        import javax.xml.bind.JAXBException;
        import javax.xml.bind.Marshaller;
        import javax.xml.bind.Unmarshaller;


        import org.fiteagle.north.sfa.aaa.jaxbClasses.Credential;
        import org.fiteagle.north.sfa.aaa.jaxbClasses.Signatures;
        import org.fiteagle.north.sfa.aaa.jaxbClasses.SignedCredential;
        import org.fiteagle.north.sfa.util.URN;
        import org.xml.sax.InputSource;

public class CredentialFactory {


    public static Credential newCredential(X509Certificate userCert, URN user, X509Certificate targetCert, URN target) throws Exception {

        CredentialFactoryWorker worker = new CredentialFactoryWorker(userCert,user,targetCert, target);

        return worker.getCredential();
    }





    public static String signCredential(Credential credential){
        SignedCredential signedCredential = new SignedCredential();
        signedCredential.setCredential(credential);

        Signatures signatures = new Signatures();
        signedCredential.setSignatures(signatures);
        SignatureCreator signer = new SignatureCreator();
        String signedCredentialString = "";
        try {
            String tmpsignedcredentialString = getJAXBString(signedCredential);
            InputSource is = new InputSource(new StringReader(tmpsignedcredentialString));
            ByteArrayOutputStream bout = signer.signContent(is, credential.getId());
            tmpsignedcredentialString = new String(bout.toByteArray());
            signedCredentialString = SFIFix.removeNewlinesFromCertificateInsideSignature(tmpsignedcredentialString);

        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        signedCredentialString =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + signedCredentialString;
        return signedCredentialString;
    }

    private static String getJAXBString(Object jaxbObject) throws JAXBException {
        JAXBContext context = JAXBContext
                .newInstance("org.fiteagle.north.sfa.aaa.jaxbClasses");
        Marshaller marshaller = context.createMarshaller();
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(jaxbObject, stringWriter);

        return stringWriter.toString();

    }

    public static SignedCredential buildCredential(String credential) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance("org.fiteagle.north.sfa.aaa.jaxbClasses");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(credential);
        SignedCredential sc = (SignedCredential) unmarshaller.unmarshal(reader);
        return sc;
    }

    private static class SFIFix{

        public static String removeNewlinesFromCertificateInsideSignature(String certificateString){
            String begin = "<X509Certificate>";
            String end = "</X509Certificate>";
            certificateString = certificateString.replaceAll(begin+"\\n", begin);
            certificateString = certificateString.replaceAll("\\n" +end, end);
            return certificateString;


        }
    }


}