package org.fiteagle.north.sfa.am.dm;

import org.fiteagle.north.sfa.am.ISFA_AM_Delegate;

public class SFA_AM_Delegate_Default implements ISFA_AM_Delegate {

	private boolean compressed;

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
	public void setCompressed(boolean compressed) {
		System.out.println("Compressed: " + compressed);
		this.compressed = compressed;
	}

}
