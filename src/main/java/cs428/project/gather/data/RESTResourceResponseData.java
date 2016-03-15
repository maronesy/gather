package cs428.project.gather.data;

import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import java.sql.Timestamp;

public class RESTResourceResponseData<T> extends RESTResponseData {
	private T result;

	public static <T> ResponseEntity<RESTResourceResponseData<T>> badResponse(BindingResult bindingResult) {
		/*
			Given that Bar<T> is a subclass of Foo, Java is unable to convert from Foo to Bar<T>, which makes no sense.
			This hack works around that problem without introduction code duplication
		*/

		// Build the original errorResponse
		ResponseEntity<RESTResponseData> errorResponse = RESTResponseData.responseBuilder(bindingResult);
		// Extract the stuff out and rebuild the errorResponse ()
		return new ResponseEntity<RESTResourceResponseData<T>>(new RESTResourceResponseData<T>(errorResponse.getBody()), errorResponse.getStatusCode());
	}

	public RESTResourceResponseData(RESTResponseData data) {
		super(data.getSTATUS(), data.getMessage());
	}

	public RESTResourceResponseData(int status, T result) {
		super(status, "");
		this.result = result;
	}

	public RESTResourceResponseData(int status, String message, T result) {
		super(status, message);
		this.result = result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public T getResult() {
		return this.result;
	}
}
