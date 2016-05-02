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

/**
 * 
 * @author Team Gather
 * This class represents the data object to create new events 
 * 
 */
public class NewEventData {
	/**
	 * Public static names for validator getting the field names
	 */
	public static final String EVENT_NAME_FIELD_NAME = "eventName";
	public static final String EVENT_COORDS_FIELD_NAME = "eventCoordinates";
	public static final String EVENT_DESCRIPTION_FIELD_NAME = "eventDescription";
	public static final String EVENT_CATEGORY_FIELD_NAME = "eventCategory";
	public static final String EVENT_TIME_FIELD_NAME = "eventTime";

	private String eventName;
	private Coordinates eventCoordinates;
	private String eventDescription;
	private String eventCategory;
	private List<Long> eventOccurrences = null;

	/**
	 * Parse the raw JSON data in String and validate the data, then set the 
	 * Error code accordingly.
	 * 
	 * @param rawData: The raw JSON data in String
	 * @param validator: The validator object to validate the input data
	 * @param errors: The error object to pass to the validator for different error code 
	 * @return: A paginated bad request response based on the binding result.
	 * 
	 */
	public static NewEventData parseIn(String rawData, AbstractValidator validator, Errors errors) {
		System.out.println("rawData: " + rawData);
		NewEventData eventData = (new Gson()).fromJson(rawData, NewEventData.class);
		eventData.validate(validator, errors);
		return eventData;
	}

	/**
	 * Validate this object and save the Error status
	 * 
	 * @param validator: The validator object to validate the input data
	 * @param errors: The error object to pass to the validator for different error code 
	 * 
	 */
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
