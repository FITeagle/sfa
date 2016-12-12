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


	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException {

		this.handler.handle(req.getInputStream(), resp.getOutputStream(),
				req.getPathInfo(), this.getCert(req));
	}

	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException {

		this.handler.handle(req.getInputStream(), resp.getOutputStream(),
				req.getPathInfo(), this.getCert(req));
	}

	private X509Certificate getCert(final HttpServletRequest req)
			throws IOException {
		final X509Certificate[] certs = (X509Certificate[]) req
				.getAttribute("javax.servlet.request.X509Certificate");
		if ((null != certs) && (certs.length > 0)) {
			return certs[0];
		}

		return null;
	}
}
