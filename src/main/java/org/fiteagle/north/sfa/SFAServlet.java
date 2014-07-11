package org.fiteagle.north.sfa;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public abstract class SFAServlet extends HttpServlet {

	protected SFAXMLRPCHandler handler;

	protected static Logger LOGGER = Logger
			.getLogger(SFAServlet.class.getName());

	public SFAServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "Working on: GET");
		handler.handle(req.getInputStream(), resp.getOutputStream());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "Working on: POST");
		handler.handle(req.getInputStream(), resp.getOutputStream());
	}
}
