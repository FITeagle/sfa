package org.fiteagle.north.sfa.sa.dm;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import org.fiteagle.north.sfa.dm.AbstractSFA_Servlet;
import org.fiteagle.north.sfa.dm.SFA_XMLRPC_Handler;
import org.fiteagle.north.sfa.sa.SFA_SA;
import org.fiteagle.north.sfa.util.FixedSerializer;

@SuppressWarnings("serial")
@WebServlet(name = "SfaSAServlet", description = "Servlet to handle SFA slice authority requests", urlPatterns = {
		"/api/sa/v1", "/api/sa/v2" })
public class SFA_SA_Servlet extends AbstractSFA_Servlet {

	protected static Logger LOGGER = Logger.getLogger(SFA_SA_Servlet.class
			.getName());
	private Fixed_XMLRPC_Server server;

	public SFA_SA_Servlet() {
		super();
		this.server = new Fixed_XMLRPC_Server();
		server.setSerializer(new FixedSerializer());
		this.handler = new SFA_XMLRPC_Handler(new SFA_SA());
	}
}
