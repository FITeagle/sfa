package org.fiteagle.north.sfa.am.dm;

import javax.ejb.Stateless;

import org.fiteagle.north.sfa.SFAXMLRPCHandler;
import org.fiteagle.north.sfa.am.SFAAggregateManager;

@Stateless
public class SFAXMLRPCHandlerAM extends SFAXMLRPCHandler {

	public SFAXMLRPCHandlerAM() {
		super();
		this.manager = new SFAAggregateManager();
	}

}
