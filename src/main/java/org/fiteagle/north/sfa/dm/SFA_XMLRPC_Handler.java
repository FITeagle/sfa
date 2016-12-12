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
import org.fiteagle.north.sfa.sa.dm.FixedXmlRpcDispatcher;
import org.fiteagle.north.sfa.sa.dm.Fixed_XMLRPC_Server;

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

	private final Fixed_XMLRPC_Server xmlrpcServer;
	private  FixedXmlRpcDispatcher dispatcher;
	private PrintWriter writer;
	private final ISFA manager;
	private final ISFA_XMLRPC_InvocationHandler handler;
	private String path;
	private X509Certificate cert;

	public SFA_XMLRPC_Handler(final ISFA manager) {
		this.manager = manager;
		this.handler = this;
		this.xmlrpcServer = new Fixed_XMLRPC_Server();
		this.xmlrpcServer.setSerializer(new SFA_XMLRPC_Serializer());
		this.xmlrpcServer.addInvocationHandler("", this.handler);
		// todo: xmlrpcServer.addInvocationInterceptor(securityModule);
		
		

	}

	public void handle(final InputStream inputStream,
			final OutputStream outputStream, final String path,
			final X509Certificate cert) throws XmlRpcException, IOException {
		this.writer = new PrintWriter(outputStream);
		
		LOGGER.log(Level.INFO, "START: Dispatching input stream");
		try {
			this.handler.setPath(path);
			this.handler.setCert(cert);
			this.dispatcher = new FixedXmlRpcDispatcher(this.xmlrpcServer, "");
			// todo: forward path and certificate here for AuthN/AuthZ
			this.dispatcher.dispatch(inputStream, this.writer);
		} catch (XmlRpcException | NullPointerException e) {
			final String message = e.getMessage() + ": "
					+ e.getCause().getMessage();
			SFA_XMLRPC_Handler.LOGGER.log(Level.WARNING, message, e.getCause());
			this.xmlrpcServer.getSerializer().writeError(1, message,
					this.writer);
		}
		LOGGER.log(Level.INFO, "END: Dispatching input stream");
		this.writer.close();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object invoke(final String methodName, final List parameter)
			throws Throwable {
	


		final String certInfo = null == this.cert ? null : this.cert
				.getSubjectX500Principal().toString();


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

	  /*
	  Parts (or the complete Code) of the following method(s) were inspired or copied
	  from StackOverFlow/StackExchange.
	  Because of the Licensing of StackOverFlow under the CC-BY-SA [*¹] and MIT[*²] License this comment
	  has to link the original code:
	  
	  Link: https://stackoverflow.com/a/5445161
	  */
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

/*
[1] https://creativecommons.org/licenses/by-sa/3.0/


[2] https://opensource.org/licenses/MIT
	The MIT License (MIT)
	Copyright (c) 2016 StackExchange

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files 
	(the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, 
	publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
	subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
	MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
	FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
	WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
