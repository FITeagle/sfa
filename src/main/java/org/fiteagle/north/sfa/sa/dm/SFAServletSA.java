package org.fiteagle.north.sfa.sa.dm;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import org.fiteagle.north.sfa.SFAServlet;

@SuppressWarnings("serial")
@WebServlet(name = "SfaSAServlet", description = "Servlet to handle SFA slice authority requests", urlPatterns = {
		"/api/sa/v1", "/api/sa/v2" })
public class SFAServletSA extends SFAServlet {

	protected static Logger LOGGER = Logger
			.getLogger(SFAServletSA.class.getName());
	
	public SFAServletSA() {
		super();
		this.handler = new SFAXMLRPCHandlerSA();
	}
}
