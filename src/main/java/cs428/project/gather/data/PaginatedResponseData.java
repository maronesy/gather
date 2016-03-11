package cs428.project.gather.data;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.*;

public class PaginatedResponseData<T> {
    protected int count             = 0;
    protected String previous       = null;
    protected String next           = null;
    protected List<T> results       = null;
    protected String errorMessage   = null;

    /*
    public static <T> PaginatedResponseData<T> create(HttpServletRequest request, List<T> results) {
        int results_per_page = 20;
        String maybe_page_num = request.getParameter("page");

        int page_num = 1;
        if (maybe_page_num != null) {
            try {
                page_num = Integer.parseInt(maybe_page_num);
            } catch (Exception e) {
                page_num = 1;
            }
        }

        int total_num_results   = results.size();
        int total_pages         = (total_num_results / results_per_page) + 1;

        String previous = (page_num <= 1) ? null : request.getRequestURL().toString() + "?page=" + (page_num-1);
        String next     = (page_num >= total_pages) ? null : request.getRequestURL().toString() + "?page=" + (page_num+1);

        int start   = (page_num-1)*results_per_page;
        int end     = (page_num*results_per_page > results.size()) ? results.size() : page_num*results_per_page;
        return new PaginatedResponseData<T>(results.size(), previous, next, results.subList(start, end));
    }
    */

    public static <T> ResponseEntity<PaginatedResponseData<T>> createResponse(HttpServletRequest request, List<T> results) {
        int results_per_page    = 20;
        int total_num_results   = results.size();
        int total_pages         = (total_num_results / results_per_page) + 1;
        int page_num            = 1;

        String maybe_page_num = request.getParameter("page");
        if (maybe_page_num != null) {
            try {
                page_num = Integer.parseInt(maybe_page_num);
            } catch (Exception e) {
                return new ResponseEntity<PaginatedResponseData<T>>(new PaginatedResponseData("Invalid page number '" + maybe_page_num + "'"), HttpStatus.BAD_REQUEST);
            }
        }

        if (page_num < 1) {
            return new ResponseEntity<PaginatedResponseData<T>>(new PaginatedResponseData("Invalid page '" + Integer.toString(page_num) + "': That page number is less than 1."), HttpStatus.BAD_REQUEST);
        } else if (page_num > total_pages) {
            return new ResponseEntity<PaginatedResponseData<T>>(new PaginatedResponseData("Invalid page '" + Integer.toString(page_num) + "': That page contains no results."), HttpStatus.BAD_REQUEST);
        }

        String previous = (page_num <= 1) ? null : request.getRequestURL().toString() + "?page=" + (page_num-1);
        String next     = (page_num >= total_pages) ? null : request.getRequestURL().toString() + "?page=" + (page_num+1);

        int start   = (page_num-1)*results_per_page;
        int end     = (page_num*results_per_page > results.size()) ? results.size() : page_num*results_per_page;
        return new ResponseEntity<PaginatedResponseData<T>>(new PaginatedResponseData(results.size(), previous, next, results.subList(start, end)), HttpStatus.OK);
    }

    public PaginatedResponseData(int count, String previous, String next, List<T> results) {
        this.count      = count;
        this.previous   = previous;
        this.next       = next;
        this.results    = results;
    }


    public PaginatedResponseData(String error_message) {
        this.errorMessage      = error_message;
    }

    public int getCount(){
        return count;
    }

    public void setCount(int providedCount){
        count = providedCount;
    }

    public String getPrevious(){
        return previous;
    }

    public void setPrevious(String previousURL){
        previous = previousURL;
    }

    public String getNext(){
        return next;
    }

    public void setErrorMessage(String msg) {
        errorMessage = msg;
    }

    public String getErrorMessage() {
        return errorMessage;
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
