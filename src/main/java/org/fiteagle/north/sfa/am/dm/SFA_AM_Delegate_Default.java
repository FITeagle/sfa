package org.fiteagle.north.sfa.am.dm;

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.jms.JMSException;

import org.fiteagle.north.sfa.am.ISFA_AM_Delegate;
import org.fiteagle.north.sfa.dm.SFA_XMLRPC_Handler;

public class SFA_AM_Delegate_Default implements ISFA_AM_Delegate {

	protected static Logger LOGGER = Logger
			.getLogger(SFA_AM_Delegate_Default.class.getName());

	@Override
	public Integer getGeniCode() {
		return 0;
	}

	@Override
	public Integer getAMCode() {
		return 0;
	}

	@Override
	public String getOutput() {
		return "";
	}

	@Override
	public String getListResourcesValue() {
		final InputStream filestream = this.getClass().getResourceAsStream(
				"/dummy-listresources-semantic.xml");
		String rspec = SFA_XMLRPC_Handler.convertStreamToString(filestream);

		return rspec;
	}


	@Override
	public void setCompressed(final boolean compressed) {
	}

	@Override
	public void setAvailable(final boolean available){
	}
}
