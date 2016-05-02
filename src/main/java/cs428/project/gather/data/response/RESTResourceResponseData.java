package cs428.project.gather.data.response;

import org.springframework.http.*;
import org.springframework.validation.BindingResult;

/**
 * 
 * @author Team Gather
 * This class takes a response Data Type, "T", and makes the response with the single result
 * with data type, "T"
 * 
 */
public class RESTResourceResponseData<T> extends RESTResponseData {
    private T result;

	/**
	 * Create a bad request based on the binding result.
	 * 
	 * @param bindingResult: The binding result that contains the error information form the validator  
	 * @return: A bad request response based on the binding result.
	 * 
	 */
    public static <T> ResponseEntity<RESTResourceResponseData<T>> badResponse(BindingResult bindingResult) {
        /*
            Given that Bar<T> is a subclass of Foo, Java is unable to convert from Foo to Bar<T> (which makes no sense.)
            This hack works around that problem without introducing code duplication
        */

        // Build the original errorResponse
        ResponseEntity<RESTResponseData> errorResponse = RESTResponseData.buildResponse(bindingResult);
        // Extract the stuff out and rebuild the errorResponse
        return new ResponseEntity<RESTResourceResponseData<T>>(new RESTResourceResponseData<T>(errorResponse.getBody()), errorResponse.getStatusCode());
    }

	/**
	 * Create a response data based on the list of results.
	 * 
	 * @param results: The result of the data type, "T"  
	 * @param status: The HTTP Status for this request
	 * @return: A response data based on the list of results.
	 * 
	 */
    public static <T> ResponseEntity<RESTResourceResponseData<T>> createResponse(T result, HttpStatus status) {
        return new ResponseEntity<RESTResourceResponseData<T>>(new RESTResourceResponseData(0, result), status);
    }

	/**
	 * Constructor.
	 * Create a response data based on a regular response data.
	 * 
	 * @param data: The regular response data   
	 * 
	 */
    public RESTResourceResponseData(RESTResponseData data) {
        super(data.getSTATUS(), data.getMessage());
    }

	/**
	 * Constructor.
	 * Create a response data based on return status and actual result.
	 * 
	 * @param status: The return status   
	 * @param result: The result of type "T"   
	 * 
	 */
    public RESTResourceResponseData(int status, T result) {
        super(status, "");
        this.result = result;
    }

	/**
	 * Constructor.
	 * Create a response data based on return status, return message and actual result.
	 * 
	 * @param status: The return status   
	 * @param message: The return message   
	 * @param result: The result of type "T"   
	 * 
	 */
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
