package org.fiteagle.north.sfa.am;

import java.util.List;

import org.fiteagle.north.sfa.ISFA;

public interface ISFA_AM extends ISFA {

	String METHOD_LIST_RESOURCES = "LISTRESOURCES";
	String METHOD_GET_VERSION = "GETVERSION";

	public abstract Object listResources(List<?> parameter);

	public abstract Object getVersion(List<?> parameter);
}