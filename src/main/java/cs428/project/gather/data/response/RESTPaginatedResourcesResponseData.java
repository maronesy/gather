package cs428.project.gather.data.response;

import java.util.List;
import org.springframework.http.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

/**
 * 
 * @author Team Gather
 * This class takes a response Data Type, "T", and makes the response into pages to avoid 
 * a single REST call return too much data at once. If multiple pages of data are returned,
 * the information of how to get the next page of data is included.
 * 
 */
public class RESTPaginatedResourcesResponseData<T> extends RESTResponseData {
    protected int count             = 0;
    protected String previous       = null;
    protected String next           = null;
    protected List<T> results       = null;

	/**
	 * Create a bad request based on the binding result.
	 * 
	 * @param bindingResult: The binding result that contains the error information form the validator  
	 * @return: A paginated bad request response based on the binding result.
	 * 
	 */
    public static <T> ResponseEntity<RESTPaginatedResourcesResponseData<T>> badResponse(BindingResult bindingResult) {
        // Build the original errorResponse
        ResponseEntity<RESTResponseData> errorResponse = RESTResponseData.buildResponse(bindingResult);
        // Extract the stuff out and rebuild the errorResponse
        return new ResponseEntity<RESTPaginatedResourcesResponseData<T>>(new RESTPaginatedResourcesResponseData<T>(errorResponse.getBody()), errorResponse.getStatusCode());
    }

	/**
	 * Create a paginated response based on the list of results.
	 * 
	 * @param request: The HTTP Servlet Request  
	 * @param results: The list of results of the data type, "T"  
	 * @return: A paginated response based on the list of results.
	 * 
	 */
    public static <T> ResponseEntity<RESTPaginatedResourcesResponseData<T>> createResponse(HttpServletRequest request, List<T> results) {
        int total_num_results   = results.size();
        int results_per_page    = 20;
        int page_num            = 1;

        // GET PAGE SIZE
        String maybe_page_size = request.getParameter("size");
        if (maybe_page_size != null) {
            try {
                results_per_page = Integer.parseInt(maybe_page_size);
            } catch (Exception e) {
                return new ResponseEntity<RESTPaginatedResourcesResponseData<T>>(new RESTPaginatedResourcesResponseData("Invalid page size '" + maybe_page_size + "'"), HttpStatus.BAD_REQUEST);
            }

        }
        if (results_per_page <= 0) {
            return new ResponseEntity<RESTPaginatedResourcesResponseData<T>>(new RESTPaginatedResourcesResponseData("Invalid page size '" + Integer.toString(results_per_page) + "': must be greater than 0."), HttpStatus.BAD_REQUEST);
        }

        // CALCULATE TOTAL PAGES
        int total_pages = ((total_num_results-1) / results_per_page) + 1;

        // GET PAGE NUMBER
        String maybe_page_num = request.getParameter("page");
        if (maybe_page_num != null) {
            try {
                page_num = Integer.parseInt(maybe_page_num);
            } catch (Exception e) {
                return new ResponseEntity<RESTPaginatedResourcesResponseData<T>>(new RESTPaginatedResourcesResponseData("Invalid page number '" + maybe_page_num + "'"), HttpStatus.BAD_REQUEST);
            }

        }
        if (page_num < 1) {
            return new ResponseEntity<RESTPaginatedResourcesResponseData<T>>(new RESTPaginatedResourcesResponseData("Invalid page '" + Integer.toString(page_num) + "': That page number is less than 1."), HttpStatus.BAD_REQUEST);

        } else if (page_num > total_pages) {
            return new ResponseEntity<RESTPaginatedResourcesResponseData<T>>(new RESTPaginatedResourcesResponseData("Invalid page '" + Integer.toString(page_num) + "': That page contains no results."), HttpStatus.BAD_REQUEST);
        }

        // CALCULATE OTHER FIELDS
        String previous = (page_num <= 1) ? null : (request.getRequestURL().toString() + "?page=" + (page_num-1) + (maybe_page_size == null ? "" : "&size=" + maybe_page_size));
        String next     = (page_num >= total_pages) ? null : (request.getRequestURL().toString() + "?page=" + (page_num+1) +  (maybe_page_size == null ? "" : "&size=" + maybe_page_size));
        int start       = (page_num-1)*results_per_page;
        int end         = (page_num*results_per_page > results.size()) ? results.size() : page_num*results_per_page;

        // RETURN PAGINATED RESULTS
        return new ResponseEntity<RESTPaginatedResourcesResponseData<T>>(new RESTPaginatedResourcesResponseData(0, results.size(), previous, next, results.subList(start, end)), HttpStatus.OK);
    }

	/**
	 * Constructor.
	 * Create a paginated response based on a regular response data.
	 * 
	 * @param data: The regular response data   
	 * 
	 */
    public RESTPaginatedResourcesResponseData(RESTResponseData data) {
        super(data.getSTATUS(), data.getMessage());
    }

	/**
	 * Constructor.
	 * Create a paginated response based on parameters.
	 * 
	 * @param status: return status  
	 * @param count: total count of the paginated response
	 * @param previous: URL for the previous page
	 * @param next: URL for the next page
	 * @param results: The list of the data results 
	 * 
	 */
    public RESTPaginatedResourcesResponseData(int status, int count, String previous, String next, List<T> results) {
        super(status, "");
        this.count      = count;
        this.previous   = previous;
        this.next       = next;
        this.results    = results;
    }

	/**
	 * Constructor.
	 * Create a paginated response based on a error message.
	 * 
	 * @param errorMessage: error message to set.   
	 * 
	 */
    public RESTPaginatedResourcesResponseData(String errorMessage) {
        this.message = errorMessage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int providedCount) {
        count = providedCount;
    }

    public String getPrevious(){
        return previous;
    }

    public void setPrevious(String previousURL) {
        previous = previousURL;
    }

    public String getNext(){
        return next;
    }

    public void setNext(String nextURL){
        next = nextURL;
    }

    public List<T> getResults(){
        return results;
    }

    public void setResults(List<T> listResults){
        results = listResults;
    }
}
