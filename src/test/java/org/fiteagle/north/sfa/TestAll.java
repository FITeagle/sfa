package org.fiteagle.north.sfa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.fiteagle.north.sfa.am.dm.SFAXMLRPCHandlerAM;
import org.fiteagle.north.sfa.sa.dm.SFAXMLRPCHandlerSA;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TestAll {

	@Test
	public void testNull() throws SAXException, IOException {
		String inputString = "";
		SFAXMLRPCHandler handler = new SFAXMLRPCHandlerAM();
		String expected = "A problem occured during parsing";
		
		testMethodCall(handler, inputString, expected);
	}

	@Test
	public void testReadDummy() {
		String filename = "/dummy-getcred.xml";
		InputStream filestream = this.getClass().getResourceAsStream(filename);
		Assert.assertNotNull(filestream);
	}
	
	@Test
	public void testGetVersion() throws SAXException, IOException {
		String inputString = "<?xml version='1.0'?><methodCall><methodName>GetVersion</methodName><params></params></methodCall>";
		SFAXMLRPCHandler handler = new SFAXMLRPCHandlerAM();
		String expected = "geni_api_versions";
		
		testMethodCall(handler, inputString, expected);
	}

	@Test
	public void testListResources() throws SAXException, IOException {
		String inputString = "<?xml version='1.0'?><methodCall><methodName>ListResources</methodName><params></params></methodCall>";
		SFAXMLRPCHandler handler = new SFAXMLRPCHandlerAM();
		String expected = "issvrqawv3";
		
		testMethodCall(handler, inputString, expected);
	}

	@Test
	public void testListProvision() throws SAXException, IOException {
		String inputString = "<?xml version='1.0'?><methodCall><methodName>Provision</methodName><params></params></methodCall>";
		SFAXMLRPCHandler handler = new SFAXMLRPCHandlerAM();
		String expected = "geni_rspec";
		
		testMethodCall(handler, inputString, expected);
	}

	@Test
	public void testGetCredentials() throws SAXException, IOException {
		String inputString = "<?xml version='1.0'?><methodCall><methodName>GetCredential</methodName><params></params></methodCall>";
		SFAXMLRPCHandler handler = new SFAXMLRPCHandlerSA();
		String expected = "Exponent";
		
		testMethodCall(handler, inputString, expected);
	}

	@Test
	public void testGetRegister() throws SAXException, IOException {
		String inputString = "<?xml version='1.0'?><methodCall><methodName>Register</methodName><params></params></methodCall>";
		SFAXMLRPCHandler handler = new SFAXMLRPCHandlerSA();
		String expected = "Exponent";
		
		testMethodCall(handler, inputString, expected);
	}

	private void testMethodCall(SFAXMLRPCHandler handler, String input,
			String expected) throws IOException {
		InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Assert.assertFalse(outputStream.toString().contains(expected));
		handler.handle(inputStream, outputStream);
		System.out.println(outputStream.toString());
		Assert.assertTrue(outputStream.toString().contains(expected));
	}
	
	
}
