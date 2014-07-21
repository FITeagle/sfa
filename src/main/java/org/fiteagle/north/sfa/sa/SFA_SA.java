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

	public SFA_SA(final ISFA_SA_Delegate delegate) {
	}

	@Override
	public Object handle(final String methodName, final List<?> parameter,
			final String path, final X509Certificate cert) {
		Object result;

		SFA_SA.LOGGER.log(Level.INFO, "Working on method: " + methodName);
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
	public Object getVersion(final List<?> parameter) {

		final Map<String, Object> result = new HashMap<>();

		// todo: generate result here based on internal ontology
		this.createDummyAnswer(result);

		return result;
	}

	private void createDummyAnswer(final Map<String, Object> result) {

	}

}
