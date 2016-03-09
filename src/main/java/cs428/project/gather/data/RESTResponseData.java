package cs428.project.gather.data;


import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class RESTResponseData {

	protected int status = -1;
	protected String message = "";
	protected long timestamp;
	
	public RESTResponseData(){
	}
	
	public RESTResponseData(int status){
		this.status = status;
		Date now = new Date();
		this.timestamp = now.getTime();
	}
	
	public RESTResponseData(int status, String message){
		this.status = status;
		Date now = new Date();
		this.timestamp = now.getTime();
		this.message = message;
	}
	
	public RESTResponseData(int status, String message, long timestamp){
		this.status = status;
		this.message = message;
		this.timestamp = timestamp;
	}
	
	public static ResponseEntity<RESTResponseData> responseBuilder(BindingResult error){
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

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(this.status);
		builder.append(this.message);

		int hashCode = builder.toHashCode();

		return hashCode;
	}

	@Override
	public boolean equals(Object anotherObject) {
		boolean equal = false;

		if (anotherObject == this) {
			equal = true;
		} else if (anotherObject != null && anotherObject.getClass().equals(this.getClass())) {
			RESTResponseData anotherSignInData = (RESTResponseData) anotherObject;

			EqualsBuilder equalsBuilder = new EqualsBuilder();

			equalsBuilder.append(this.status, anotherSignInData.status);
			equalsBuilder.append(this.message, anotherSignInData.message);
			

			equal = equalsBuilder.isEquals();
		}
		return equal;
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
