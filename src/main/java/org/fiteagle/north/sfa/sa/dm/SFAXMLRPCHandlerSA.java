package org.fiteagle.north.sfa.sa.dm;

import javax.ejb.Stateless;

import org.fiteagle.north.sfa.SFAXMLRPCHandler;
import org.fiteagle.north.sfa.sa.SFASliceAuthority;

@Stateless
public class SFAXMLRPCHandlerSA extends SFAXMLRPCHandler {

	public SFAXMLRPCHandlerSA() {
		super();
		this.manager = new SFASliceAuthority();
	}
}
