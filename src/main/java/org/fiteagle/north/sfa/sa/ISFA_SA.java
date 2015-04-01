package org.fiteagle.north.sfa.sa;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.List;

import org.fiteagle.north.sfa.ISFA;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

public interface ISFA_SA extends ISFA {

	String METHOD_GET_CREDENTIAL = "GETCREDENTIAL";
	String METHOD_REGISTER = "REGISTER";
	String METHOD_GET_VERSION = "GETVERSION";
    String METHOD_RENEW_SLICE = "RENEWSLICE" ;

    Object getVersion(List<?> parameter);
	Object getCredential(X509Certificate certificate) throws Exception;
	Object register(List<?> parameter) throws Exception;

    Object renewSlice(List<?> parameter) throws JAXBException, DatatypeConfigurationException;
}
