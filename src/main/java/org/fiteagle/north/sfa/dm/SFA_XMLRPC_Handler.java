package org.fiteagle.north.sfa.dm;

import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.north.sfa.ISFA;

import redstone.xmlrpc.XmlRpcDispatcher;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcServer;

public class SFA_XMLRPC_Handler implements ISFA_XMLRPC_InvocationHandler {

	private final static Logger LOGGER = Logger
			.getLogger(SFA_XMLRPC_Handler.class.getName());
	private final static String DUMMY_RESPONSE_FILE_LIST_RESOURCES = "/dummy-listresources.xml";
	private final static String DUMMY_RESPONSE_FILE_GET_CRED = "/dummy-getcred.xml";
	private static final String DUMMY_RESPONSE_FILE_SLICE_BAD = "/dummy-badslice.xml";
	private static final String DUMMY_RESPONSE_FILE_REGISTER = "/dummy-register.xml";
	private static final String DUMMY_RESPONSE_FILE_DESCRIBE = "/dummy-describe.xml";
	private static final String DUMMY_RESPONSE_FILE_ALLOCATE = "/dummy-allocate.xml";
	private static final String DUMMY_RESPONSE_FILE_DELETE = "/dummy-delete.xml";
	private static final String DUMMY_RESPONSE_FILE_PROVISION = "/dummy-provision.xml";

	private XmlRpcServer xmlrpcServer;
	private XmlRpcDispatcher dispatcher;
	private PrintWriter writer;
	private ISFA manager;
	private ISFA_XMLRPC_InvocationHandler handler;
	private String path;
	private X509Certificate cert;

	public SFA_XMLRPC_Handler(ISFA manager) {
		this.manager = manager;
		this.handler = this;
		this.xmlrpcServer = new XmlRpcServer();
		this.xmlrpcServer.setSerializer(new SFA_XMLRPC_Serializer());
		xmlrpcServer.addInvocationHandler("__default__", handler);
		// todo: xmlrpcServer.addInvocationInterceptor(securityModule);
		this.dispatcher = new XmlRpcDispatcher(xmlrpcServer, "");
	}

	public void handle(InputStream inputStream, OutputStream outputStream,
			String path, X509Certificate cert) throws XmlRpcException,
			IOException {
		this.writer = new PrintWriter(outputStream);
		try {
			this.handler.setPath(path);
			this.handler.setCert(cert);
			// todo: forward path and certificate here for AuthN/AuthZ
			dispatcher.dispatch(inputStream, writer);
		} catch (XmlRpcException e) {
			String message = e.getMessage() + ": " + e.getCause().getMessage();
			xmlrpcServer.getSerializer().writeError(1, message, writer);
			LOGGER.log(Level.WARNING, message);
		}
		writer.close();
	}

	@SuppressWarnings("rawtypes")
	public Object invoke(String methodName, List parameter) throws Throwable {
		LOGGER.log(Level.INFO, "Working on method: " + methodName);
		LOGGER.log(Level.INFO, "Working on path: " + this.path);
		String certInfo = null == this.cert ? null : this.cert.getSubjectX500Principal().toString();
		LOGGER.log(Level.INFO, "Working with cert: " + certInfo);

		// todo: move this hack to the manager (i.e. construct dummy answers)
		if ("ListResources".equals(methodName)) {
			return returnDummyValue(DUMMY_RESPONSE_FILE_LIST_RESOURCES);
		} else if ("GetCredential".equals(methodName)) {
			return returnDummyValue(DUMMY_RESPONSE_FILE_GET_CRED);
		} else if ("Status".equals(methodName)) {
			return returnDummyValue(DUMMY_RESPONSE_FILE_SLICE_BAD);
		} else if ("Register".equals(methodName)) {
			return returnDummyValue(DUMMY_RESPONSE_FILE_REGISTER);
		} else if ("Describe".equals(methodName)) {
			return returnDummyValue(DUMMY_RESPONSE_FILE_DESCRIBE);
		} else if ("Allocate".equals(methodName)) {
			return returnDummyValue(DUMMY_RESPONSE_FILE_ALLOCATE);
		} else if ("Provision".equals(methodName)) {
			return returnDummyValue(DUMMY_RESPONSE_FILE_PROVISION);
		} else if ("Delete".equals(methodName)) {
			return returnDummyValue(DUMMY_RESPONSE_FILE_DELETE);
		}

		return this.manager.handle(methodName, parameter, this.path, this.cert);
	}

	private String returnDummyValue(String filename)
			throws MalformedURLException {
		InputStream filestream = this.getClass().getResourceAsStream(filename);
		writer.write(convertStreamToString(filestream));
		writer.flush();
		return null;
	}

	private String convertStreamToString(InputStream is) {
		@SuppressWarnings("resource")
		Scanner s = new Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public void setCert(X509Certificate cert) {
		this.cert = cert;
	}

}
