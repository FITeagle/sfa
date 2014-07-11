package org.fiteagle.north.sfa;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import redstone.xmlrpc.XmlRpcDispatcher;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcInvocationHandler;
import redstone.xmlrpc.XmlRpcServer;

public abstract class SFAXMLRPCHandler implements XmlRpcInvocationHandler {

	private final static Logger LOGGER = Logger
			.getLogger(SFAXMLRPCHandler.class.getName());
	private final static String DUMMY_RESPONSE_FILE_GET_VERSION = "/dummy-getversion.xml";
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
	protected SFAManager manager;

	public SFAXMLRPCHandler() {
		this.xmlrpcServer = new XmlRpcServer();
		xmlrpcServer.addInvocationHandler("__default__", this);
		this.dispatcher = new XmlRpcDispatcher(xmlrpcServer, "");
	}

	public void handle(InputStream inputStream, OutputStream outputStream)
			throws XmlRpcException, IOException {
		this.writer = new PrintWriter(outputStream);
		try {
			dispatcher.dispatch(inputStream, writer);
		} catch (XmlRpcException e) {
			xmlrpcServer.getSerializer().writeError(1, e.getMessage(), writer);
		}
		writer.close();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public String invoke(String methodName, List parameter) throws Throwable {
		LOGGER.log(Level.INFO, "Working on method: " + methodName);

		// todo: move this hack to the manager (i.e. construct dummy answers)
		if ("GetVersion".equals(methodName)) {
			return returnDummyValue(DUMMY_RESPONSE_FILE_GET_VERSION);
		} else if ("ListResources".equals(methodName)) {
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

		return this.manager.manage(methodName, parameter);
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

}
