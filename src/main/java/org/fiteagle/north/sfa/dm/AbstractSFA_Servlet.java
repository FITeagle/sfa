package org.fiteagle.north.sfa.dm;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public abstract class AbstractSFA_Servlet extends HttpServlet {

	protected SFA_XMLRPC_Handler handler;

	protected static Logger LOGGER = Logger.getLogger(AbstractSFA_Servlet.class
			.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "Working on: GET");
		handler.handle(req.getInputStream(), resp.getOutputStream(), req.getPathInfo(), getCert(req));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "Working on: POST");
		handler.handle(req.getInputStream(), resp.getOutputStream(), req.getPathInfo(), getCert(req));
	}
	
	private X509Certificate getCert(HttpServletRequest req) throws IOException {
	    X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
	    if (null != certs && certs.length > 0) {
	        return certs[0];
	    }
	    LOGGER.log(Level.INFO, "No X.509 client certificate found in request");
	    return null;
	  }
}
