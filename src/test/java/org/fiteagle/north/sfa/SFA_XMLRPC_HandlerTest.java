package org.fiteagle.north.sfa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.fiteagle.north.sfa.dm.SFA_XMLRPC_Handler;
import org.fiteagle.north.sfa.sa.SFA_SA;
import org.junit.Assert;
import org.junit.Test;

import redstone.xmlrpc.XmlRpcException;

public class SFA_XMLRPC_HandlerTest {

	@Test
	public void testGetVersionViaSA() throws XmlRpcException, IOException {
		SFA_XMLRPC_Handler handler = new SFA_XMLRPC_Handler(new SFA_SA());
		OutputStream outputStream = new ByteArrayOutputStream();
		InputStream inputStream = this.getClass().getResourceAsStream("/dummy-request-getversion-xmlrpc.xml");
		handler.handle(inputStream, outputStream, null, null);
		Assert.assertTrue(outputStream.toString().contains("geni_api"));
	}
}
