package org.fiteagle.north.sfa.am;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SFA_AM implements ISFA_AM {
	private static final int API_VERSION = 3;
	private final static Logger LOGGER = Logger
			.getLogger(SFA_AM.class.getName());
	private ISFA_AM_Delegate delegate;

	public SFA_AM(ISFA_AM_Delegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public Object handle(String methodName, List<?> parameter, String path, X509Certificate cert) {
		Object result;

		LOGGER.log(Level.INFO, "Working on method: " + methodName);
		switch (methodName.toUpperCase()) {
		case ISFA_AM.METHOD_GET_VERSION:
			result = this.getVersion(parameter);
			break;
		case ISFA_AM.METHOD_LIST_RESOURCES:
			result = this.listResources(parameter);
			break;
		default:
			result = "Unimplemented method '" + methodName + "'";
			break;
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object listResources(List<?> parameter) {
		LOGGER.log(Level.INFO, "listResources...");
		HashMap<String, Object> result = new HashMap<>();
		parseListResourcesParameter(parameter);
		result.put("value", this.delegate.getListResourcesValue());

		addCode(result);		
		addOutput(result);
		return result;
	}

	private void parseListResourcesParameter(List<?> parameter) {
		for (Object param : parameter) {
			if (param instanceof Map<?, ?>) {
				Map<String, ?> param2 = (Map<String, ?>) param;
				this.delegate.setCompressed((Boolean) param2.get("geni_compressed"));	
			} else if (param instanceof List<?>) {
				//tood: parse more
			}
		}
	}

	@Override
	public Object getVersion(List<?> parameter) {
		LOGGER.log(Level.INFO, "getVersion...");
		HashMap<String, Object> result = new HashMap<>();

		addAPIVersion(result);
		addValue(result);		
		addCode(result);
		addOutput(result);				
		return result;
	}

	private void addAPIVersion(HashMap<String, Object> result) {
		result.put("geni_api", API_VERSION);
	}

	private void addValue(HashMap<String, Object> result) {
		//todo: use delegate for this
		Map<String, Object> value = new HashMap<>();
		value.put("geni_api", API_VERSION);
				
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
	}

	private void addOutput(HashMap<String, Object> result) {
		result.put("output", this.delegate.getOutput());
	}

	private void addCode(HashMap<String, Object> result) {
		Map<String, Integer> code = new HashMap<>();
		code.put("geni_code", this.delegate.getGeniCode());
		code.put("am_code", this.delegate.getAMCode());
		result.put("code", code);
	}
}
