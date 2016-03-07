package cs428.project.gather.data;


import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class RESTResponseData {

	private int status = -1;
	private String message = "";
	private long timestamp;
	
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
	
	public RESTResponseData(BindingResult error){
		String message="";
		int errorCode=-1;
		int count=0;
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
		    count++;
		}
		if(count>1){
			this.status=-100;
		}else{
			this.status = errorCode;
		}
		Date now = new Date();
		this.timestamp = now.getTime();
		this.message = message;
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
