package org.fiteagle.north.sfa.sa;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.north.sfa.am.ISFA_AM;

public class SFA_SA implements ISFA_SA {

	protected static Logger LOGGER = Logger.getLogger(SFA_SA.class.getName());
	private ISFA_SA_Delegate delegate;

	public SFA_SA(ISFA_SA_Delegate delegate) {
		this.delegate = delegate;
	}

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
		
	}

}
