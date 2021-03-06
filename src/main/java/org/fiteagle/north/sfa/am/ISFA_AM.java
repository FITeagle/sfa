package org.fiteagle.north.sfa.am;

import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;



//import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.exceptions.InvalidRspecValueException;
import info.openmultinet.ontology.exceptions.MissingRspecElementException;

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
	String StartTime ="startTime";
	String node = "node";
	String componentManagerId = "component_manager_id";
	String RequiredResources = "requiredResources";
	String Sliver = "sliver";
	String SLICE = "slice";
	String GENI= "geni";
	String UTF_8 = "UTF-8";
	String LOCALHOST = "localhost";
	String READY = "Ready";
	String STARTED = "Started";
	String UNCOMPLETED = "Uncompleted";
	String NO_ERROR = "NO ERROR";
	String SUCCESS = "SUCCESS";
	String KEYS = "keys";
	
	String GENI_RSPEC_VERSION = "geni_rspec_version";
  String GENI_BEST_EFFORT = "geni_best_effort";
  String GENI_END_TIME = "geni_end_time";

  String GENI_USERS = "geni_users";
  
  final String PRIVILEGE_DEFAULT = "*";
  final String PRIVILEGE_REFRESH = "refresh";
  final String PRIVILEGE_RESOLVE = "resolve";
  final String PRIVILEGE_INFO = "info";
  final String PRIVILEGE_AUTHORITY= "authority";
  final String PRIVILEGE_PI = "pi";
  final String PRIVILEGE_BIND = "bind";
  final String PRIVILEGE_CONTROL = "control";
  final String PRIVILEGE_INSTANTIATE = "instantiate";



	public abstract Object listResources(List<?> parameter,X509Certificate cert) throws JMSException, UnsupportedEncodingException, JAXBException, InvalidModelException;

	public abstract Object getVersion(List<?> parameter);
	
	public abstract Object describe(List<?> parameter) throws UnsupportedEncodingException, JAXBException, InvalidModelException;// , InvalidModelException;
	
	public abstract Object allocate(List<?> parameter) throws JAXBException, InvalidModelException, UnsupportedEncodingException, MissingRspecElementException, InvalidRspecValueException;
	
	public abstract Object renew(List<?> parameter) throws UnsupportedEncodingException;
	
	public abstract Object provision(List<?> parameter) throws UnsupportedEncodingException;
	
	public abstract Object status(List<?> parameter) throws UnsupportedEncodingException;
	
	public abstract Object performOperationalAction(List<?> parameter) throws UnsupportedEncodingException;
	
	public abstract Object delete(List<?> parameter) throws UnsupportedEncodingException;
	
	public abstract Object shutdown(List<?> parameter);
}