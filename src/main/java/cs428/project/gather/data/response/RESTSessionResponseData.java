package cs428.project.gather.data.response;

import org.springframework.http.*;

/**
 * 
 * @author Team Gather
 * Response data for session login and logout 
 * 
 */
public class RESTSessionResponseData extends RESTResponseData {
	private String displayName;

	/**
	 * Create a session response data with basic information.
	 * 
	 * @param status: The return status
	 * @param message: The return message
	 * @param httpStatus: The HTTP status
	 * @return: A session response data with return status, a message, and HTTP Status.
	 * 
	 */
    public static ResponseEntity<RESTSessionResponseData> sessionResponse(int status, String message, HttpStatus httpStatus) {
        return new ResponseEntity<RESTSessionResponseData>(new RESTSessionResponseData(status, message), httpStatus);
    }

	/**
	 * Create a session response data with basic information and user display name.
	 * 
	 * @param status: The return status
	 * @param message: The return message
	 * @param displayName: The user's display name
	 * @param httpStatus: The HTTP status
	 * @return: A session response data with return status, a message, and HTTP Status.
	 * 
	 */
    public static ResponseEntity<RESTSessionResponseData> sessionResponse(int status, String message, String displayName, HttpStatus httpStatus) {
        return new ResponseEntity<RESTSessionResponseData>(new RESTSessionResponseData(status, message, displayName), httpStatus);
    }

	/**
	 * Constructor.
	 * Create a session response data based on basic information and display name.
	 * 
	 * @param status: The return status
	 * @param message: The return message
	 * @param displayName: The user's display name
	 * 
	 */
	public RESTSessionResponseData(int status, String message, String displayName) {
		super(status,message);
		this.setDisplayName(displayName);
	}

	/**
	 * Constructor.
	 * Create a session response data based on basic information.
	 * 
	 * @param status: The return status
	 * @param message: The return message
	 * 
	 */
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
