package org.fiteagle.north.sfa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.fiteagle.north.sfa.am.SFA_AM;
import org.fiteagle.north.sfa.am.dm.SFA_AM_Delegate_Default;
import org.fiteagle.north.sfa.dm.SFA_XMLRPC_Handler;
import org.junit.Assert;
import org.junit.Test;

import redstone.xmlrpc.XmlRpcException;

public class SFA_XMLRPC_HandlerTest {

	@Test
	public void testGetVersionViaAM() throws XmlRpcException, IOException {
		SFA_XMLRPC_Handler handler = new SFA_XMLRPC_Handler(new SFA_AM(new SFA_AM_Delegate_Default()));
		OutputStream outputStream = new ByteArrayOutputStream();
		InputStream inputStream = this.getClass().getResourceAsStream("/dummy-request-getversion-xmlrpc.xml");
		handler.handle(inputStream, outputStream, null, null);
		String result = outputStream.toString();
		System.out.println("Get Version Result: " + result);
		Assert.assertTrue(result.contains("geni_api"));
	}
	
	@Test
	public void testListResourcesViaAM() throws XmlRpcException, IOException {
		SFA_XMLRPC_Handler handler = new SFA_XMLRPC_Handler(new SFA_AM(new SFA_AM_Delegate_Default()));
		OutputStream outputStream = new ByteArrayOutputStream();
		InputStream inputStream = this.getClass().getResourceAsStream("/dummy-request-listresources-xmlrpc.xml");
		handler.handle(inputStream, outputStream, null, null);
		String result = outputStream.toString();
		System.out.println("List Resource Result: " + result);
		Assert.assertTrue(result.contains("geni_code"));
	}

}
