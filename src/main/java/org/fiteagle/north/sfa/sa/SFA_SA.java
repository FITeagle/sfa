package org.fiteagle.north.sfa.sa;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.north.sfa.am.ISFA_AM;

public class SFA_SA implements ISFA_SA {

	protected static Logger LOGGER = Logger.getLogger(SFA_SA.class.getName());

	@Override
	public Object handle(String methodName, List<?> parameter, String path,
			X509Certificate cert) {
		Object result;

		LOGGER.log(Level.INFO, "Working on method: " + methodName);
		switch (methodName.toUpperCase()) {
		case ISFA_AM.METHOD_GET_VERSION:
			result = this.getVersion(parameter);
			break;
		default:
			result = "Unimplemented method '" + methodName + "'";
			break;
		}

		return result;

	}

	@Override
	public Object getVersion(List<?> parameter) {

		Map<String, Object> result = new HashMap<>();

		// todo: generate result here based on internal ontology
		createDummyAnswer(result);

		return result;
	}

	private void createDummyAnswer(Map<String, Object> result) {
		result.put("geni_api", 3);

		Map<String, Integer> code = new HashMap<>();
		code.put("geni_code", 0);
		code.put("am_code", 0);
		result.put("code", code);

		Map<String, Object> value = new HashMap<>();
		value.put("geni_api", 3);

		Map<String, String> apiVersions = new HashMap<>();
		apiVersions.put("3", "https://path_to_this_server_from_ontology");
		value.put("geni_api_versions", apiVersions);

		List<Map<String, Object>> reqRSpecs = new LinkedList<>();
		Map<String, Object> typeA = new HashMap<>();
		typeA.put("type", "GENI");
		typeA.put("version", "3");
		typeA.put("schema", "foo");
		typeA.put("namespace", "bar");
		String[] extensions = new String[0];
		typeA.put("extensions", extensions);
		reqRSpecs.add(typeA);
		value.put("geni_request_rspec_versions", reqRSpecs);

		List<Map<String, Object>> adRSpecs = new LinkedList<>();
		Map<String, Object> adTypeA = new HashMap<>();
		adTypeA.put("type", "GENI");
		adTypeA.put("version", "3");
		adTypeA.put("schema", "foo");
		adTypeA.put("namespace", "bar");
		adTypeA.put("extensions", extensions);
		adRSpecs.add(adTypeA);
		value.put("geni_ad_rspec_versions", adRSpecs);

		List<Map<String, Object>> credTypes = new LinkedList<>();
		Map<String, Object> credTypeA = new HashMap<>();
		credTypeA.put("geni_type", "geni_sfa");
		credTypeA.put("geni_version", "1");
		credTypes.add(credTypeA);
		value.put("geni_credential_types", credTypes);

		result.put("value", value);

		result.put("output", "");
	}

}
