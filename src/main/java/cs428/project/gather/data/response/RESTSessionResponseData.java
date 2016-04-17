package cs428.project.gather.data.response;

import org.springframework.http.*;

public class RESTSessionResponseData extends RESTResponseData {
	private String displayName;

    public static ResponseEntity<RESTSessionResponseData> sessionResponse(int status, String message, HttpStatus httpStatus) {
        return new ResponseEntity<RESTSessionResponseData>(new RESTSessionResponseData(status, message), httpStatus);
    }

    public static ResponseEntity<RESTSessionResponseData> sessionResponse(int status, String message, String displayName, HttpStatus httpStatus) {
        return new ResponseEntity<RESTSessionResponseData>(new RESTSessionResponseData(status, message, displayName), httpStatus);
    }

	public RESTSessionResponseData(int status, String message, String displayName) {
		super(status,message);
		this.setDisplayName(displayName);
	}

	public RESTSessionResponseData(int status, String message) {
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
