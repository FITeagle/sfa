package org.fiteagle.north.sfa.allocate;

import javax.jms.JMSException;

import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ProcessAllocate {
	
	private static String noMaxInstances = "UNLIMITED";
	
	public void allocateResources() throws JMSException {
		String maxInstances = getMaxInstances();
		if(!maxInstances.equals(noMaxInstances)){
			int instances = getInstances();
		}
	}
	
	/**
	 * 
	 * @return the maximum number of instances which resource adapter can instantiate  
	 * @throws JMSException
	 * @throws TimeoutException
	 */
	public static String getMaxInstances() {
		String maxInstances = noMaxInstances;
		String query = "PREFIX av: <http://federation.av.tu-berlin.de/about#> "
				+ "RREFIX omn: <http://open-multinet.info/ontology/omn#> "
				+ "SELECT ?amount "
				+ "WHERE {av:MotorGarage-1 omn:maxInstances ?amount } ";
		Model resultModel = SFA_AM_MDBSender.getInstance().sendRequest(query);
		StmtIterator iter = resultModel.listStatements();
		while(iter.hasNext()){
			int result = iter.next().getInt();
			return Integer.toString(result);
  	  }
		return maxInstances;
	}
	
	public static int getInstances() {
		int instances = 0;
		String query = " PREFIX av: <http://federation.av.tu-berlin.de/about#> "
				+ " RREFIX omn: <http://open-multinet.info/ontology/omn#> "
				+ "SELECT ?instance "
				+ "WHERE {?instance a ?resourceType . "
				+ "av:MotorGarage-1 a ?adapterType . "
				+ "?adapterType omn:implements ?resourceType .} ";
		Model resultModel = SFA_AM_MDBSender.getInstance().sendRequest(query);
		StmtIterator iter = resultModel.listStatements();
   	  while(iter.hasNext()){
   		  instances++;
   		  iter.next();
   	  }
   	  return instances;
	}
}
