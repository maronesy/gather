package cs428.project.gather.data;

import java.util.List;

import cs428.project.gather.data.model.Event;

public class EventsResponseData {
	
	
	protected int count;
	protected String previous = null;
	protected String next = null;
	protected List<Event> results;
	
	public EventsResponseData(String previous, String next, List<Event> results){
		this.count = results.size();
		this.previous = previous;
		this.next = next;
		this.results = results;
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
	
	public void setNext(String nextURL){
		next = nextURL;
	}
	
	public List<Event> getEvents(){
		return results;
	}
	
	public void setEvents(List<Event> listEvents){
		results = listEvents; 
	}
	
}
