package org.fiteagle.north.sfa;

import java.security.cert.X509Certificate;
import java.util.List;

public interface ISFA {
	public Object handle(String methodName, List<?> parameter, String path,
			X509Certificate cert);
}
