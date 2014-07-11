package org.fiteagle.north.sfa.am.dm;

import javax.servlet.annotation.WebServlet;

import org.fiteagle.north.sfa.SFAServlet;

@SuppressWarnings("serial")
@WebServlet(name = "SfaServlet", description = "Servlet to handle SFA requests", urlPatterns = {
		"/api/am/v3", "/api/am/v4" })
public class SFAServletAM extends SFAServlet {

	public SFAServletAM() {
		super();
		this.handler = new SFAXMLRPCHandlerAM();
	}
}
