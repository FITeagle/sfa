package org.fiteagle.north.sfa.am.dm;

import javax.servlet.annotation.WebServlet;

import org.fiteagle.north.sfa.am.SFA_AM;
import org.fiteagle.north.sfa.dm.AbstractSFA_Servlet;
import org.fiteagle.north.sfa.dm.SFA_XMLRPC_Handler;

@SuppressWarnings("serial")
@WebServlet(name = "SfaServlet", description = "Servlet to handle SFA requests", urlPatterns = {
		"/api/am/v3", "/api/am/v4" })
public class SFA_AM_Servlet extends AbstractSFA_Servlet {

	public SFA_AM_Servlet() {
		super();
		this.handler = new SFA_XMLRPC_Handler(new SFA_AM(
				new SFA_AM_Delegate_Default()));
	}
}
