package org.fiteagle.north.sfa.sa;

import java.security.cert.X509Certificate;
import java.util.List;

import org.fiteagle.north.sfa.ISFA;

public interface ISFA_SA extends ISFA {

	String METHOD_GET_CREDENTIAL = "GETCREDENTIAL";
	String METHOD_REGISTER = "REGISTER";
	String METHOD_GET_VERSION = "GETVERSION";
	Object getVersion(List<?> parameter);
	Object getCredential(X509Certificate certificate);
	Object register(List<?> parameter);

}
