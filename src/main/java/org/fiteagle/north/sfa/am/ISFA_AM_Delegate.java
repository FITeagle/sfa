package org.fiteagle.north.sfa.am;

import java.security.cert.X509Certificate;

public interface ISFA_AM_Delegate {

	Integer getGeniCode();

	Integer getAMCode();

	String getOutput();

	String getListResourcesValue();
	

	void setCompressed(boolean equalsIgnoreCase);
	boolean getCompressed();
	
	void setAvailable(boolean available);
	boolean getAvailable();
	
	void setSliceURN(String slice_urn);
	String getSliceURN();
	
	// methods for Credentials
	void setGeniType(String geni_type);
	String getGeniType();
	
	void setGeinVersion(String geni_version);
	String getGeniVersion();
	
	void setGeniValue(String geni_value);
	String getGeniValue();
}
