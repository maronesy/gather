package cs428.project.gather.data.response;

import java.util.Date;
import org.springframework.http.*;
import org.springframework.validation.*;

public class RESTResponseData {
    protected int status = -1;
    protected String message = "";
    protected long timestamp;

    public RESTResponseData() {}

    public RESTResponseData(int status) {
        this.status = status;
        Date now = new Date();
        this.timestamp = now.getTime();
    }

    public RESTResponseData(int status, String message) {
        this.status = status;
        Date now = new Date();
        this.timestamp = now.getTime();
        this.message = message;
    }

    public RESTResponseData(int status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static ResponseEntity<RESTResponseData> OKResponse(String message) {
        return new ResponseEntity<RESTResponseData>(new RESTResponseData(0, message), HttpStatus.OK);
    }

    public static ResponseEntity<RESTResponseData> response(int status, String message, HttpStatus httpStatus) {
        return new ResponseEntity<RESTResponseData>(new RESTResponseData(status, message), httpStatus);
    }

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

	public static ResponseEntity<RESTResponseData> buildResponse(String errorCodeStr, String errorMessage) {
		int errorCode = -1;
		errorCode = Integer.parseInt(errorCodeStr);
		HttpStatus httpStatus = convertErrorCodeToHttpStatus(errorCode);
		return new ResponseEntity<RESTResponseData>(new RESTResponseData(errorCode,errorMessage),httpStatus);
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
        	//TODO: If we wish to change this back to UNAUTHORIZED, we need to fix SignInControllerTest.testSignInUserWrongPassword()
        	//HttpStatus.UNAUTHORIZED causes test to fail since it seems the RestTemplate automatically attempts to retry when this status 
        	//is received and the retry throws an exception
//            result = HttpStatus.UNAUTHORIZED;
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
