package org.fiteagle.north.sfa.am;

import java.util.List;
import java.security.cert.X509Certificate;
import org.fiteagle.north.sfa.ISFA;

public interface ISFA_AM extends ISFA {

	String METHOD_LIST_RESOURCES = "LISTRESOURCES";
	String METHOD_GET_VERSION = "GETVERSION";
	String METHOD_DESCRIBE = "DESCRIBE";
	String METHOD_ALLOCATE = "ALLOCATE";
	String METHOD_RENEW = "RENEW";
	String METHOD_PROVISION = "PROVISION";
	String METHOD_STATUS = "STATUS";
	String METHOD_PERFORMOPERATIONALACTION = "PERFORMOPERATIONALACTION";
	String METHOD_DELETE = "DELETE";
	String METHOD_SHUTDOWN = "SHUTDOWN";

	public abstract Object listResources(List<?> parameter);

	public abstract Object getVersion(List<?> parameter);
	
//	public abstract Object describe(List<?> parameter);
	
	public abstract Object allocate(List<?> parameter);
	
	public abstract Object renew(List<?> parameter);
	
	public abstract Object provision(List<?> parameter);
	
	public abstract Object status(List<?> parameter);
	
	public abstract Object performOperationalAction(List<?> parameter);
	
	public abstract Object delete(List<?> parameter);
	
	public abstract Object shutdown(List<?> parameter);
}