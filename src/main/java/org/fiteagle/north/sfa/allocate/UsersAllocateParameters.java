package org.fiteagle.north.sfa.allocate;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UsersAllocateParameters {
	
	private final static Logger LOGGER = Logger.getLogger(UsersAllocateParameters.class
			.getName());
	
	private static UsersAllocateParameters instance;
	
	public UsersAllocateParameters(){
	instance = this;
	}
	
	public static UsersAllocateParameters getInstance(){
		return instance;
	}
	
	private List<AllocateParameter> allocateParam = new LinkedList<AllocateParameter>();
	
	public void addAllocateParameter (AllocateParameter param){
		this.allocateParam.add(param);
		System.out.println("user parameter added successfully");
	}
	
	public void getAllParm(){
		System.out.println("all users parameters ");
		for(final AllocateParameter param : allocateParam){
			UsersAllocateParameters.LOGGER.log(Level.INFO, param.getRequest());
		}
	}

}
