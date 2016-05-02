package cs428.project.gather.data.form;

import cs428.project.gather.validator.*;
import cs428.project.gather.utilities.*;
import cs428.project.gather.data.*;
import cs428.project.gather.data.model.Occurrence;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.*;
import com.google.gson.*;
import org.springframework.validation.Errors;

public class NewEventData {
	public static final String EVENT_NAME_FIELD_NAME = "eventName";
	public static final String EVENT_COORDS_FIELD_NAME = "eventCoordinates";
	public static final String EVENT_DESCRIPTION_FIELD_NAME = "eventDescription";
	public static final String EVENT_CATEGORY_FIELD_NAME = "eventCategory";
	public static final String EVENT_TIME_FIELD_NAME = "eventTime";

	private String eventName;
	private Coordinates eventCoordinates;
	private String eventDescription;
	private String eventCategory;
	//private long eventTime;
	private List<Long> eventOccurrences = null;

	public static NewEventData parseIn(String rawData, AbstractValidator validator, Errors errors) {
		System.out.println("rawData: " + rawData);
		NewEventData eventData = (new Gson()).fromJson(rawData, NewEventData.class);
		eventData.validate(validator, errors);
		return eventData;
	}

	public void validate(AbstractValidator validator, Errors errors) {
		validator.validate(this, errors);
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Coordinates getEventCoodinates() {
		return eventCoordinates;
	}

	public void setEventCoodinates(Coordinates eventCoodinates) {
		this.eventCoordinates = eventCoodinates;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public String getEventCategory() {
		return eventCategory;
	}

	public void setEventCategory(String eventCategory) {
		this.eventCategory = eventCategory;
	}

	public List<Long> getOccurrences() {
		return eventOccurrences;
	}

}
