package cs428.project.gather.data;

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
        HttpStatus result = HttpStatus.BAD_REQUEST;
        switch(errorCode){
        case 0:
            result = HttpStatus.OK;
        case -1:
            result = HttpStatus.UNPROCESSABLE_ENTITY;
        case -2:
            result = HttpStatus.LENGTH_REQUIRED;
        case -3:
            result = HttpStatus.BAD_REQUEST;
        case -4:
            result = HttpStatus.CONFLICT;
        case -5:
            result = HttpStatus.NOT_FOUND;
        case -6:
            result = HttpStatus.UNAUTHORIZED;
        case -7:
            result = HttpStatus.BAD_REQUEST;

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
