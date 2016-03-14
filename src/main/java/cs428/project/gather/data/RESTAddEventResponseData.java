package cs428.project.gather.data;

import java.sql.Timestamp;

import cs428.project.gather.data.model.Event;

public class RESTAddEventResponseData extends RESTResponseData{
	private String eventName;
	private String eventCategory;
	private String eventDescription;
	private long eventTime;
	private double distanceFromCaller;
	private Coordinates coordinates;
	
	public RESTAddEventResponseData(int status, String message, Event savedEventResult, double distance) {
		super(status, message);
		this.eventName = savedEventResult.getName();
		//TODO: Handle category properly
		this.eventCategory = "category";
		this.eventDescription = savedEventResult.getDescription();
		this.eventTime = savedEventResult.getOccurrences().get(0).getTimestamp().getTime();
		this.distanceFromCaller = distance;
		this.coordinates = savedEventResult.getLocation().getCoordinates();
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventCategory() {
		return eventCategory;
	}
	public void setEventCategory(String eventCategory) {
		this.eventCategory = eventCategory;
	}
	public String getEventDescription() {
		return eventDescription;
	}
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}
	public long getEventTime() {
		return eventTime;
	}
	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}
	public double getDistanceFromCaller() {
		return distanceFromCaller;
	}
	public void setDistanceFromCaller(double distanceFromCaller) {
		this.distanceFromCaller = distanceFromCaller;
	}
	public Coordinates getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	
	
}
