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
	private final static Logger LOGGER = Logger.getLogger(SFA_AM.class
			.getName());
	private final ISFA_AM_Delegate delegate;

	public SFA_AM(final ISFA_AM_Delegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public Object handle(final String methodName, final List<?> parameter,
			final String path, final X509Certificate cert) {
		Object result;

		SFA_AM.LOGGER.log(Level.INFO, "Working on method: " + methodName);
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
	public Object listResources(final List<?> parameter) {
		SFA_AM.LOGGER.log(Level.INFO, "listResources...");
		final HashMap<String, Object> result = new HashMap<>();
		this.parseListResourcesParameter(parameter);
		result.put("value", this.delegate.getListResourcesValue());

		this.addCode(result);
		this.addOutput(result);
		return result;
	}

	private void parseListResourcesParameter(final List<?> parameter) {
		for (final Object param : parameter) {
			if (param instanceof Map<?, ?>) {
				@SuppressWarnings("unchecked")
				final Map<String, ?> param2 = (Map<String, ?>) param;
				this.delegate.setCompressed((Boolean) param2
						.get("geni_compressed"));
			} else if (param instanceof List<?>) {
				// tood: parse more
			}
		}
	}

	@Override
	public Object getVersion(final List<?> parameter) {
		SFA_AM.LOGGER.log(Level.INFO, "getVersion...");
		final HashMap<String, Object> result = new HashMap<>();

		this.addAPIVersion(result);
		this.addValue(result);
		this.addCode(result);
		this.addOutput(result);
		return result;
	}

	private void addAPIVersion(final HashMap<String, Object> result) {
		result.put("geni_api", SFA_AM.API_VERSION);
	}

	private void addValue(final HashMap<String, Object> result) {
		// todo: use delegate for this
		final Map<String, Object> value = new HashMap<>();
		value.put("geni_api", SFA_AM.API_VERSION);

		final Map<String, String> apiVersions = new HashMap<>();
		apiVersions.put("3", "https://path_to_this_server_from_ontology");
		value.put("geni_api_versions", apiVersions);

		final List<Map<String, Object>> reqRSpecs = new LinkedList<>();
		final Map<String, Object> typeA = new HashMap<>();
		typeA.put("type", "GENI");
		typeA.put("version", "3");
		typeA.put("schema", "foo");
		typeA.put("namespace", "bar");
		final String[] extensions = new String[0];
		typeA.put("extensions", extensions);
		reqRSpecs.add(typeA);
		value.put("geni_request_rspec_versions", reqRSpecs);

		final List<Map<String, Object>> adRSpecs = new LinkedList<>();
		final Map<String, Object> adTypeA = new HashMap<>();
		adTypeA.put("type", "GENI");
		adTypeA.put("version", "3");
		adTypeA.put("schema", "foo");
		adTypeA.put("namespace", "bar");
		adTypeA.put("extensions", extensions);
		adRSpecs.add(adTypeA);
		value.put("geni_ad_rspec_versions", adRSpecs);

		final List<Map<String, Object>> credTypes = new LinkedList<>();
		final Map<String, Object> credTypeA = new HashMap<>();
		credTypeA.put("geni_type", "geni_sfa");
		credTypeA.put("geni_version", "1");
		credTypes.add(credTypeA);
		value.put("geni_credential_types", credTypes);

		result.put("value", value);
	}

	private void addOutput(final HashMap<String, Object> result) {
		result.put("output", this.delegate.getOutput());
	}

	private void addCode(final HashMap<String, Object> result) {
		final Map<String, Integer> code = new HashMap<>();
		code.put("geni_code", this.delegate.getGeniCode());
		code.put("am_code", this.delegate.getAMCode());
		result.put("code", code);
	}
}
