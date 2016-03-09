package cs428.project.gather.data;

public class RESTSignInResponseData extends RESTResponseData{

	private String displayName;
	
	public RESTSignInResponseData(int status, String message, String displayName){
		super(status,message);
		this.setDisplayName(displayName);
	}
	
	public RESTSignInResponseData(int status, String message){
		super(status,message);
		this.setDisplayName("");
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
		
}
