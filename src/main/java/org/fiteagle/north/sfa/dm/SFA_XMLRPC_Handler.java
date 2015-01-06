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
	private final static String DUMMY_RESPONSE_FILE_GET_CRED = "/dummy-getcred.xml";
	private static final String DUMMY_RESPONSE_FILE_SLICE_BAD = "/dummy-badslice.xml";
	private static final String DUMMY_RESPONSE_FILE_REGISTER = "/dummy-register.xml";
	private static final String DUMMY_RESPONSE_FILE_DESCRIBE = "/dummy-describe.xml";
	private static final String DUMMY_RESPONSE_FILE_ALLOCATE = "/dummy-allocate.xml";
	private static final String DUMMY_RESPONSE_FILE_DELETE = "/dummy-delete.xml";
	private static final String DUMMY_RESPONSE_FILE_PROVISION = "/dummy-provision.xml";

	private final XmlRpcServer xmlrpcServer;
	private final XmlRpcDispatcher dispatcher;
	private PrintWriter writer;
	private final ISFA manager;
	private final ISFA_XMLRPC_InvocationHandler handler;
	private String path;
	private X509Certificate cert;

	public SFA_XMLRPC_Handler(final ISFA manager) {
		this.manager = manager;
		this.handler = this;
		this.xmlrpcServer = new XmlRpcServer();
		this.xmlrpcServer.setSerializer(new SFA_XMLRPC_Serializer());
		this.xmlrpcServer.addInvocationHandler("__default__", this.handler);
		// todo: xmlrpcServer.addInvocationInterceptor(securityModule);
		this.dispatcher = new XmlRpcDispatcher(this.xmlrpcServer, "");
	}

	public void handle(final InputStream inputStream,
			final OutputStream outputStream, final String path,
			final X509Certificate cert) throws XmlRpcException, IOException {
		this.writer = new PrintWriter(outputStream);
		try {
			this.handler.setPath(path);
			this.handler.setCert(cert);
			// todo: forward path and certificate here for AuthN/AuthZ
			this.dispatcher.dispatch(inputStream, this.writer);
		} catch (XmlRpcException | NullPointerException e) {
			final String message = e.getMessage() + ": "
					+ e.getCause().getMessage();
			SFA_XMLRPC_Handler.LOGGER.log(Level.WARNING, message, e.getCause());
			this.xmlrpcServer.getSerializer().writeError(1, message,
					this.writer);
		}
		this.writer.close();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object invoke(final String methodName, final List parameter)
			throws Throwable {
		
		System.out.println("            parameter:"+parameter);
		
		SFA_XMLRPC_Handler.LOGGER.log(Level.INFO, "Working on method: "
				+ methodName);
		SFA_XMLRPC_Handler.LOGGER.log(Level.INFO, "Working on path: "
				+ this.path);
		final String certInfo = null == this.cert ? null : this.cert
				.getSubjectX500Principal().toString();
		SFA_XMLRPC_Handler.LOGGER.log(Level.INFO, "Working with cert: "
				+ certInfo);

		// todo: move this hack to the manager (i.e. construct dummy answers)
		if ("GetCredential".equals(methodName)) {
			return this
					.returnDummyValue(SFA_XMLRPC_Handler.DUMMY_RESPONSE_FILE_GET_CRED);
		} else if ("Status".equals(methodName)) {
			return this
					.returnDummyValue(SFA_XMLRPC_Handler.DUMMY_RESPONSE_FILE_SLICE_BAD);
		} else if ("Register".equals(methodName)) {
			return this
					.returnDummyValue(SFA_XMLRPC_Handler.DUMMY_RESPONSE_FILE_REGISTER);
		} else if ("Describe".equals(methodName)) {
			return this
					.returnDummyValue(SFA_XMLRPC_Handler.DUMMY_RESPONSE_FILE_DESCRIBE);
		} 
		else if ("Delete".equals(methodName)) {
			return this
					.returnDummyValue(SFA_XMLRPC_Handler.DUMMY_RESPONSE_FILE_DELETE);
		}
		Object result = this.manager.handle(methodName, parameter, this.path, this.cert);
		return result;
	}

	private String returnDummyValue(final String filename)
			throws MalformedURLException {
		final InputStream filestream = this.getClass().getResourceAsStream(
				filename);
		this.writer.write(SFA_XMLRPC_Handler.convertStreamToString(filestream));
		this.writer.flush();
		return null;
	}

	public static String convertStreamToString(final InputStream is) {
		@SuppressWarnings("resource")
		final Scanner s = new Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	@Override
	public void setPath(final String path) {
		this.path = path;
	}

	@Override
	public void setCert(final X509Certificate cert) {
		this.cert = cert;
	}

}
