package org.fiteagle.north.sfa.am.dm;

import java.io.InputStream;
import java.util.logging.Logger;

import org.fiteagle.north.sfa.am.ISFA_AM_Delegate;
import org.fiteagle.north.sfa.dm.SFA_XMLRPC_Handler;

public class SFA_AM_Delegate_Default implements ISFA_AM_Delegate {

	protected static Logger LOGGER = Logger
			.getLogger(SFA_AM_Delegate_Default.class.getName());

	private boolean geni_compressed;
	private boolean geni_available;
	
	private String slice_urn;
	
	// for the credentials
	private String geni_type;
	private String geni_version;
	private String geni_value;
	private String output = "";
	private int geni_code= 0;
    private String rspecType;
    private String rspecVersion;


    @Override
	public void setOutput (String outputString){
		this.output = outputString;
	}
	
	@Override
	public void setGeniCode(int geniCode){
		this.geni_code = geniCode;
	}
	
	@Override
	public Integer getGeniCode() {
		return geni_code;
	}

	@Override
	public Integer getAMCode() {
		return 0;
	}

	@Override
	public String getOutput() {
		return output;
	}

	@Override
	public String getListResourcesValue() {
		final InputStream filestream = this.getClass().getResourceAsStream(
				"/dummy-listresources-semantic.xml");
		String rspec = SFA_XMLRPC_Handler.convertStreamToString(filestream);

		return rspec;
	}


	@Override
	public void setCompressed(final boolean compressed) {
		this.geni_compressed = compressed;
	}
	
	@Override
	public boolean getCompressed() {
		return this.geni_compressed;
	}

	@Override
	public void setAvailable(final boolean available){
		this.geni_available = available;
	}
	
	@Override
	public boolean getAvailable(){
		return this.geni_available;
	}
	
	@Override
	public void setSliceURN(String slice_urn){
		this.slice_urn = slice_urn;
	}
	
	@Override
	public String getSliceURN(){
		return this.slice_urn;
	}
	
	@Override
	public void setGeniType(String geni_type){
		this.geni_type = geni_type;
	}
	
	@Override
	public String getGeniType(){
		return this.geni_type;
	}
	
	@Override
	public void setGeinVersion(String geni_version){
		this.geni_version = geni_version;
	}
	
	@Override
	public String getGeniVersion(){
		return this.geni_version;
	}
	
	@Override
	public void setGeniValue(String geni_value){
		this.geni_value = geni_value;
	}
	
	@Override
	public String getGeniValue(){
		return this.geni_value;
	}

    @Override
    public void setRspecType(String rspecType) {
        this.rspecType = rspecType;
    }

    @Override
    public void setRspecVersion(String rspecVersion) {
        this.rspecVersion = rspecVersion;

    }
    @Override
    public String getRspecType() {
        return rspecType;
    }

    @Override
    public String getRspecVersion() {
        return rspecVersion;
    }
}
