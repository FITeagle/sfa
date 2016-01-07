package org.fiteagle.north.sfa.am.allocate;

public class AllocateParameter {
	
	private String URN;
	private String request;
	private String endTime;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	private String startTime;
	
	public void setURN(String urn){
		this.URN = urn;
	}
	public String getURN(){
		return this.URN;
	}
	
	public void setRequest(String request){
		this.request = request;
	}
	public String getRequest(){
		return this.request;
	}
	
	public void setEndTime(String endTime){
		this.endTime = endTime;
	}
	public String getEndTime(){
		return this.endTime;
	}

}
