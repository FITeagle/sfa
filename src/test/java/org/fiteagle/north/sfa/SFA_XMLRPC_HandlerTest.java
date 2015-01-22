package org.fiteagle.north.sfa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.fiteagle.north.sfa.am.SFA_AM;
import org.fiteagle.north.sfa.am.dm.SFA_AM_Delegate_Default;
import org.fiteagle.north.sfa.dm.SFA_XMLRPC_Handler;
import org.junit.Assert;
import org.junit.Before;

import org.junit.Test;
import redstone.xmlrpc.XmlRpcException;

public class SFA_XMLRPC_HandlerTest {

	private SFA_XMLRPC_Handler handler;
	private ByteArrayOutputStream outputStream;
	private InputStream inputStream;

	@Before
	public void setup() {
		this.handler = new SFA_XMLRPC_Handler(new SFA_AM());
		this.outputStream = new ByteArrayOutputStream();
	}

//	@Test
	public void testGetVersionViaAM() throws XmlRpcException, IOException {
		this.inputStream = this.getClass().getResourceAsStream(
				"/dummy-request-getversion-xmlrpc.xml");
		this.handler.handle(this.inputStream, this.outputStream, null, null);
		final String result = this.outputStream.toString();
		System.out.println("Get Version Result: " + result);
		Assert.assertTrue(result.contains("geni_api"));
	}

	//@Test
	public void testListResourcesViaAM() throws XmlRpcException, IOException {
		this.inputStream = this.getClass().getResourceAsStream(
				"/dummy-request-listresources-xmlrpc.xml");
		this.handler.handle(this.inputStream, this.outputStream, null, null);
		final String result = this.outputStream.toString();
		System.out.println("List Resource Result: " + result);
		Assert.assertTrue(result.contains("geni_code"));
	}

//	@Test
	public void testListResourcesBadCredential() throws XmlRpcException,
			IOException {
		this.inputStream = this.getClass().getResourceAsStream(
				"/testListResourcesBadCredential.xml");
		this.handler.handle(this.inputStream, this.outputStream, null, null);
		String result = this.outputStream.toString();
		System.out.println(result);
		Assert.assertTrue(result.contains("rspec"));
	}

}
