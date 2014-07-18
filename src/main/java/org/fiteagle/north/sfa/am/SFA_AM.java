package org.fiteagle.north.sfa.am;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SFA_AM implements ISFA_AM {
	private final static Logger LOGGER = Logger
			.getLogger(SFA_AM.class.getName());

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

	@Override
	public Object listResources(List<?> parameter) {
		//todo: generate result here based on internal ontology
		return "list resources";
	}

	@Override
	public Object getVersion(List<?> parameter) {
		//todo: generate result here based on internal ontology
		//      in particular parameters like "f4f_describe_testbed"

		HashMap<String, Object> result = new HashMap<>();
		result.put("output", "");
		result.put("geni_api", 3);
		
		HashMap<String, Integer> code = new HashMap<>();
		code.put("geni_code", 0);
		code.put("am_code", 0);
		result.put("code", code);
		
		HashMap<String, HashMap<String, String>> value = new HashMap<>();
		HashMap<String, String> apiVersions = new HashMap<>();
		apiVersions.put("3", "https://path_to_this_server_from_ontology");
		value.put("geni_api_versions", apiVersions);
		result.put("value", value);

		return result;
	}
}
