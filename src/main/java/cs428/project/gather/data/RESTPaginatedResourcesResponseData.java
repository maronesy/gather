package cs428.project.gather.data;

import org.springframework.http.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import java.util.List;

public class RESTPaginatedResourcesResponseData<T> extends RESTResponseData {
    protected int count             = 0;
    protected String previous       = null;
    protected String next           = null;
    protected List<T> results       = null;

    public static <T> ResponseEntity<RESTPaginatedResourcesResponseData<T>> badResponse(BindingResult bindingResult) {
        // Build the original errorResponse
        ResponseEntity<RESTResponseData> errorResponse = RESTResponseData.responseBuilder(bindingResult);
        // Extract the stuff out and rebuild the errorResponse
        return new ResponseEntity<RESTPaginatedResourcesResponseData<T>>(new RESTPaginatedResourcesResponseData<T>(errorResponse.getBody()), errorResponse.getStatusCode());
    }

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

    public RESTPaginatedResourcesResponseData(RESTResponseData data) {
        super(data.getSTATUS(), data.getMessage());
    }

    public RESTPaginatedResourcesResponseData(int status, int count, String previous, String next, List<T> results) {
        super(status, "");
        this.count      = count;
        this.previous   = previous;
        this.next       = next;
        this.results    = results;
    }

    public RESTPaginatedResourcesResponseData(String error_message) {
        this.message = error_message;
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
