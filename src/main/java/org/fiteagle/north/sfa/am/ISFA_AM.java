package org.fiteagle.north.sfa.am;

import java.io.UnsupportedEncodingException;
import java.util.List;

//import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.exceptions.InvalidModelException;
import org.fiteagle.north.sfa.ISFA;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;

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
	String SLICE = "slice";
	String GENI= "geni";

	
	public abstract Object listResources(List<?> parameter) throws JMSException, UnsupportedEncodingException, JAXBException, InvalidModelException;

	public abstract Object getVersion(List<?> parameter);
	
	public abstract Object describe(List<?> parameter) throws UnsupportedEncodingException, JAXBException;// , InvalidModelException;
	
	public abstract Object allocate(List<?> parameter) throws JAXBException, InvalidModelException, UnsupportedEncodingException;
	
	public abstract Object renew(List<?> parameter);
	
	public abstract Object provision(List<?> parameter) throws UnsupportedEncodingException;
	
	public abstract Object status(List<?> parameter) throws UnsupportedEncodingException;
	
	public abstract Object performOperationalAction(List<?> parameter);
	
	public abstract Object delete(List<?> parameter);
	
	public abstract Object shutdown(List<?> parameter);
}