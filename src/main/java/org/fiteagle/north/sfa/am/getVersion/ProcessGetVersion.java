package org.fiteagle.north.sfa.am.getVersion;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.exceptions.EmptyReplyException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ProcessGetVersion extends AbstractMethodProcessor {

	private static final Logger LOGGER = Logger
			.getLogger(ProcessGetVersion.class.getName());

	private static final int API_VERSION = 3;

	private static Config config;

	public ProcessGetVersion(List<?> parameter) {
		this.parameter = parameter;
	}

	public Model getTestbedDescription() {
		Model testbedDescriptionModel = getSender().sendSPARQLQueryRequest("",
				IMessageBus.TARGET_FEDERATION_MANAGER);
		return testbedDescriptionModel;
	}

	public String parseTestbedDescription(Model testbedDescriptionModel)
			throws EmptyReplyException {
		StmtIterator iterator = testbedDescriptionModel.listStatements();
		if (iterator.hasNext() == false) {
			throw new EmptyReplyException("No testbed could be found");
		}
		String testbedDescription = MessageUtil.serializeModel(
				testbedDescriptionModel, IMessageBus.SERIALIZATION_RDFJSON);
		LOGGER.log(Level.INFO, "result is " + testbedDescription);
		return testbedDescription;
	}

	public void createResponse(final HashMap<String, Object> result,
			String testbedDescription) {
		result.put(IGeni.GENI_API, API_VERSION);

		final Map<String, Object> value = new HashMap<>();

		value.put(IGeni.GENI_API, API_VERSION);
		value.put(ISFA_AM.OMN_TESTBED, testbedDescription);
		LOGGER.log(Level.INFO, "omn_testbed " + value.get(ISFA_AM.OMN_TESTBED));

		final String[] extensions = getSupportedExtensions();

		addSupportedRequestRspecInfo(value, extensions);

		addAdvertisementRspecInfo(value, extensions);

		addSupportedCredentialTypes(value);

		this.delegate.setGeniCode(0);
		this.delegate.setOutput(ISFA_AM.SUCCESS);

		result.put(ISFA_AM.VALUE, value);
		this.addCode(result);
		this.addOutput(result);
	}

	public String[] getSupportedExtensions() {
		List<String> extensionsMap = new LinkedList<>();

		// extensionsMap = SFA_AM_MDBSender.getInstance().getExtensions();
		final String[] extensions = new String[extensionsMap.size()];

		int i = 0;
		for (String namespace : extensionsMap) {
			// extensions[i] = namespace;
			i++;
		}
		return extensions;
	}

	private void addSupportedRequestRspecInfo(Map<String, Object> value,
			String[] extensions) {
		final Map<String, String> apiVersions = new HashMap<>();
		apiVersions.put(ISFA_AM.VERSION_3, getURL());
		value.put(IGeni.GENI_API_VERSION, apiVersions);

		final List<Map<String, Object>> reqRSpecs = new LinkedList<>();
		final Map<String, Object> typeA = new HashMap<>();
		typeA.put(ISFA_AM.TYPE, ISFA_AM.OPEN_MULTINET);
		typeA.put(ISFA_AM.VERSION, ISFA_AM.VERSION_1);
		typeA.put(IGeni.GENI_NAMESPACE, ISFA_AM.NAMESPACE);
		// TODO wrong schema
		typeA.put(ISFA_AM.SCHEMA, IGeni.GENI_REQUEST_RSPEC_SCHEMA);
		typeA.put(IGeni.GENI_EXTENSIONS, extensions);
		reqRSpecs.add(typeA);

		final Map<String, Object> typeB = new HashMap<>();
		typeB.put(ISFA_AM.TYPE, ISFA_AM.GENI.toUpperCase());
		typeB.put(ISFA_AM.VERSION, ISFA_AM.VERSION_3);
		typeB.put(IGeni.GENI_NAMESPACE, "http://www.geni.net/resources/rspec/3");
		typeB.put(ISFA_AM.SCHEMA, IGeni.GENI_REQUEST_RSPEC_SCHEMA);
		typeB.put(IGeni.GENI_EXTENSIONS, extensions);
		reqRSpecs.add(typeB);

		value.put(IGeni.GENI_REQUEST_VERSION, reqRSpecs);
	}

	private void addAdvertisementRspecInfo(Map<String, Object> value,
			String[] extensions) {
		final List<Map<String, Object>> adRSpecs = new LinkedList<>();
		final Map<String, Object> adTypeA = new HashMap<>();
		adTypeA.put(ISFA_AM.TYPE, ISFA_AM.OPEN_MULTINET);
		adTypeA.put(ISFA_AM.VERSION, ISFA_AM.VERSION_1);
		adTypeA.put(ISFA_AM.SCHEMA, IGeni.GENI_AD_RSPEC_SCHEMA);
		adTypeA.put(IGeni.GENI_NAMESPACE, ISFA_AM.NAMESPACE);
		adTypeA.put(IGeni.GENI_EXTENSIONS, extensions);
		adRSpecs.add(adTypeA);

		final Map<String, Object> adTypeB = new HashMap<>();
		adTypeB.put(ISFA_AM.TYPE, ISFA_AM.GENI.toUpperCase());
		adTypeB.put(ISFA_AM.VERSION, ISFA_AM.VERSION_3);
		adTypeB.put(ISFA_AM.SCHEMA, IGeni.GENI_AD_RSPEC_SCHEMA);
		adTypeB.put(IGeni.GENI_NAMESPACE,
				"http://www.geni.net/resources/rspec/3");
		adTypeB.put(IGeni.GENI_EXTENSIONS, extensions);
		adRSpecs.add(adTypeB);
		value.put(IGeni.GENI_AD_VERSION, adRSpecs);
	}

	private void addSupportedCredentialTypes(Map<String, Object> value) {
		final List<Map<String, Object>> credTypes = new LinkedList<>();
		final Map<String, Object> credTypeA = new HashMap<>();
		credTypeA.put(IGeni.GENI_TYPE, IGeni.GENI_SFA);
		credTypeA.put(IGeni.GENI_VERSION, "1");
		credTypes.add(credTypeA);
		value.put(IGeni.GENI_CREDENTIAL_TYPES, credTypes);
	}

	private String getURL() {
		String urlString = "";
		try {
			if (config == null) {
				config = new Config("sfa");
			}
		} catch (Exception e) {
			Log.fatal("SFA",
					"Please add the Host-URL to the sfa.properties file");
			Config config = new Config("sfa");
			config.createPropertiesFile();
		}
		urlString = config.getProperty("url");
		urlString += "/sfa/api/am/v3";

		return urlString;
	}

}
