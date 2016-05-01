package cs428.project.gather.data.response;

import java.util.Date;
import org.springframework.http.*;
import org.springframework.validation.*;

/**
 * 
 * @author Team Gather
 * Basic REST interface response data contains the return status, a message, 
 * and a timestamp. Based on the validator check, this class will provide the 
 * proper return status.
 * 
 */
public class RESTResponseData {
    protected int status = -1;
    protected String message = "";
    protected long timestamp;

	/**
	 * Constructor.
	 * Create an empty response data.  
	 * 
	 */
    public RESTResponseData() {}

	/**
	 * Constructor.
	 * Create a response data based on return status and message.
	 * 
	 * @param status: The return status   
	 * @param message: The return message
	 * 
	 */
    public RESTResponseData(int status, String message) {
        this.status = status;
        Date now = new Date();
        this.timestamp = now.getTime();
        this.message = message;
    }

	/**
	 * Create a OK response data with a message.
	 * 
	 * @param message: The return message
	 * @return: A OK response data with the given message.
	 * 
	 */
    public static ResponseEntity<RESTResponseData> OKResponse(String message) {
        return new ResponseEntity<RESTResponseData>(new RESTResponseData(0, message), HttpStatus.OK);
    }

	/**
	 * Create a basic response data with return status, a message, and HTTP Status.
	 * 
	 * @param status: The return status
	 * @param message: The return message
	 * @param httpStatus: The HTTP status
	 * @return: A response data with return status, a message, and HTTP Status.
	 * 
	 */
    public static ResponseEntity<RESTResponseData> response(int status, String message, HttpStatus httpStatus) {
        return new ResponseEntity<RESTResponseData>(new RESTResponseData(status, message), httpStatus);
    }

	/**
	 * Create a bad request with a proper return status, based on the binding result.
	 * 
	 * @param bindingResult: The binding result that contains the error information form the validator  
	 * @return: A bad request response with a proper return status, based on the binding result.
	 * 
	 */
    public static ResponseEntity<RESTResponseData> buildResponse(BindingResult error) {
        String message="";
        int errorCode=-1;
        for (Object object : error.getAllErrors()) {
            if(object instanceof FieldError) {
                FieldError fieldError = (FieldError) object;
                message+=fieldError.getDefaultMessage()+" ";
                errorCode=Integer.parseInt(fieldError.getCode());
            }

            if(object instanceof ObjectError) {
                ObjectError objectError = (ObjectError) object;
                message+=objectError.getDefaultMessage()+" ";
                errorCode=Integer.parseInt(objectError.getCode());
            }
            break;
        }

        HttpStatus httpStatus=convertErrorCodeToHttpStatus(errorCode);
        return new ResponseEntity<RESTResponseData>(new RESTResponseData(errorCode,message),httpStatus);
    }

    private static HttpStatus convertErrorCodeToHttpStatus(int errorCode) {
        HttpStatus result;
        switch(errorCode){
        case 0:
            result = HttpStatus.OK;
            break;
        case -1:
            result = HttpStatus.UNPROCESSABLE_ENTITY;
            break;
        case -2:
            result = HttpStatus.LENGTH_REQUIRED;
            break;
        case -3:
            result = HttpStatus.BAD_REQUEST;
            break;
        case -4:
            result = HttpStatus.CONFLICT;
            break;
        case -5:
            result = HttpStatus.NOT_FOUND;
            break;
        case -6:
        	result = HttpStatus.BAD_REQUEST;
            break;
        case -7:
            result = HttpStatus.BAD_REQUEST;
            break;
        case -8:
        	result = HttpStatus.INTERNAL_SERVER_ERROR;
        	break;
        default:
        	result = HttpStatus.BAD_REQUEST;
            break;
        }
        return result;
    }

    public int getSTATUS() {
        return status;
    }

    public void setSTATUS(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
