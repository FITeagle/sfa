package org.fiteagle.north.sfa;

import java.util.List;

public interface SFAManager {

	@SuppressWarnings("rawtypes")
	String manage(String methodName, List parameter);
}
