package org.fiteagle.north.sfa.am;

import java.security.cert.X509Certificate;

public interface ISFA_AM_Delegate {

	Integer getGeniCode();

	Integer getAMCode();

	String getOutput();

	String getListResourcesValue();
	

	void setCompressed(boolean equalsIgnoreCase);
	
	void setAvailable(boolean available);

}
