package org.fiteagle.north.sfa;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.InflaterInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.fiteagle.north.sfa.allocate.ProcessAllocate;
import org.fiteagle.north.sfa.am.SFA_AM;
import org.junit.Assert;
import org.junit.Test;

public class SFA_AMTest {

	@Test
	public void testCompress() throws UnsupportedEncodingException {
		String input = "<rdf:RDF    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"    xmlns:dc=\"http://purl.org/dc/elements/1.1/\"    xmlns:wgs=\"http://www.w3.org/2003/01/geo/wgs84_pos#\"    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"    xmlns:omn=\"http://open-multinet.info/ontology/omn#\"    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" >   <rdf:Description rdf:about=\"http://federation.av.tu-berlin.de/about#MotorGarage-1\">    <wgs:long>13.323732</wgs:long>    <wgs:lat>52.516377</wgs:lat>    <omn:partOfGroup rdf:resource=\"http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed\"/>    <rdfs:label xml:lang=\"en\">A deployed motor garage adapter named: MotorGarage-1</rdfs:label>    <rdfs:comment xml:lang=\"en\">A motor garage adapter that can simulate different dynamic motor resources.</rdfs:comment>    <rdf:type rdf:resource=\"http://open-multinet.info/ontology/resource/motorgarage#MotorGarage\"/>  </rdf:Description></rdf:RDF>";
		String outputString = SFA_AM.compress(input);

		Assert.assertEquals(input, decompress(outputString));
	}

	private String decompress(String toDecompress) {
		String res = "";
		try {
			byte[] b = Base64.decodeBase64(StringUtils.getBytesUtf8(toDecompress));
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new InflaterInputStream(new ByteArrayInputStream(b))));
			String line = br.readLine();
			while (line != null) {
				res += line;
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;

	}


}
