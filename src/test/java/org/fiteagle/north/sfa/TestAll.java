package org.fiteagle.north.sfa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.fiteagle.north.sfa.am.SFA_AM;
import org.fiteagle.north.sfa.am.dm.SFA_AM_Delegate_Default;
import org.fiteagle.north.sfa.dm.SFA_XMLRPC_Handler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TestAll {

	private SFA_XMLRPC_Handler handler;

	@Before
	public void setup() {
		this.handler = new SFA_XMLRPC_Handler(new SFA_AM(
				new SFA_AM_Delegate_Default()));
	}

	@Test
	public void testNull() throws SAXException, IOException {
		final String inputString = "";

		final String expected = "A problem occured during parsing";

		this.testMethodCall(this.handler, inputString, expected);
	}

	@Test
	public void testReadDummy() {
		final String filename = "/dummy-getcred.xml";
		final InputStream filestream = this.getClass().getResourceAsStream(
				filename);
		Assert.assertNotNull(filestream);
	}

	@Test
	public void testGetVersion() throws SAXException, IOException {
		final String inputString = "<?xml version='1.0'?><methodCall><methodName>GetVersion</methodName><params></params></methodCall>";

		final String expected = "omn_testbed";

		this.testMethodCall(this.handler, inputString, expected);
	}

	@Test
	public void testListResources() throws SAXException, IOException {
		final String inputString = "<?xml version='1.0'?><methodCall><methodName>ListResources</methodName><params></params></methodCall>";

		// String expected = "issvrqawv3";
		final String expected = "rspec";

		this.testMethodCall(this.handler, inputString, expected);
	}

	@Test
	public void testListProvision() throws SAXException, IOException {
		final String inputString = "<?xml version='1.0'?><methodCall><methodName>Provision</methodName><params></params></methodCall>";

		final String expected = "geni_rspec";

		this.testMethodCall(this.handler, inputString, expected);
	}

	@Test
	public void testGetCredentials() throws SAXException, IOException {
		final String inputString = "<?xml version='1.0'?><methodCall><methodName>GetCredential</methodName><params></params></methodCall>";

		final String expected = "Exponent";

		this.testMethodCall(this.handler, inputString, expected);
	}

	@Test
	public void testGetRegister() throws SAXException, IOException {
		final String inputString = "<?xml version='1.0'?><methodCall><methodName>Register</methodName><params></params></methodCall>";

		final String expected = "Exponent";

		this.testMethodCall(this.handler, inputString, expected);
	}

	private void testMethodCall(final SFA_XMLRPC_Handler handler,
			final String input, final String expected) throws IOException {
		final InputStream inputStream = new ByteArrayInputStream(
				input.getBytes(StandardCharsets.UTF_8));
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Assert.assertFalse(outputStream.toString().contains(expected));
		handler.handle(inputStream, outputStream, null, null);
		System.out.println("================================");
		System.out.println(outputStream.toString());
		System.out.println("================================");
		Assert.assertTrue(outputStream.toString().contains(expected));
	}

}
