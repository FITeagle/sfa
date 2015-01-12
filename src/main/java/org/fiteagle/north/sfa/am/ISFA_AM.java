package org.fiteagle.north.sfa.am;

import java.util.List;

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

	
	String GENI_API_VERSION = "geni_api_versions";
	String GENI_API = "geni_api";
	String GENI_TYPE = "geni_type";
	String GENI_SFA = "geni_sfa";
	String GENI_NAMESPACE = "namespace";
	String GENI_EXTENSIONS = "extensions";
	String GENI_REQUEST_VERSION = "geni_request_rspec_versions";
	String GENI_REQUEST_RSPEC_SCHEMA ="http://www.geni.net/resources/rspec/3/request.xsd";
	String GENI_AD_VERSION = "geni_ad_rspec_versions";
	String GENI_AD_RSPEC_SCHEMA ="http://www.geni.net/resources/rspec/3/ad.xsd";
	String GENI_VERSION = "geni_version";
	String GENI_CREDENTIAL_TYPES = "geni_credential_types";
	String GENI_CODE = "geni_code";
	String GENI_END_TIME = "geni_end_time";
	String GENI_SLIVER_URN = "geni_sliver_urn";
	String GENI_EXPIRES = "geni_expires";
	String GENI_ALLOCATION_STATUS = "geni_allocation_status";
	String GENI_ALLOCATED = "geni_allocated";
	String GENI_RSPEC = "geni_rspec";
	String GENI_SLIVERS = "geni_slivers";
	
	String OMN_TESTBED = "omn_testbed";
	String OMN_RESOURCE = "omn_resource";
	String API_VERSION = "https://federation.av.tu-berlin.de:8443/sfa/api/am/v3";
	String TYPE = "type";
	String NAMESPACE = "http://open-multinet.info/ontology/omn-resource";
	String VERSION = "version";
	String SCHEMA = "schema";
	String VALUE = "value";
	String OUTPUT = "output";
	String VERSION_1 = "1";
	String VERSION_3 = "3";
	String AM_CODE = "am_code";
	String CODE = "code";
	String OPEN_MULTINET = "open-multinet";
	String OMN = "http://open-multinet.info/ontology/omn#";
	String URN = "urn";
	String REQUEST = "request";
	String EndTime = "endTime";
	String node = "node";
	String componentManagerId = "component_manager_id";
	String RequiredResources = "requiredResources";
	String Sliver = "sliver";

	
	public abstract Object listResources(List<?> parameter);

	public abstract Object getVersion(List<?> parameter);
	
	public abstract Object describe(List<?> parameter);
	
	public abstract Object allocate(List<?> parameter);
	
	public abstract Object renew(List<?> parameter);
	
	public abstract Object provision(List<?> parameter);
	
	public abstract Object status(List<?> parameter);
	
	public abstract Object performOperationalAction(List<?> parameter);
	
	public abstract Object delete(List<?> parameter);
	
	public abstract Object shutdown(List<?> parameter);
}