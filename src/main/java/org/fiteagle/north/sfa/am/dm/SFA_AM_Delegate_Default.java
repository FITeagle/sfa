package org.fiteagle.north.sfa.am.dm;

import java.util.logging.Logger;

import org.fiteagle.north.sfa.am.ISFA_AM_Delegate;

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
		return "<rspec/>";
	}

	@Override
	public void setCompressed(final boolean compressed) {
	}

}
