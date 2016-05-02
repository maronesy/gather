package cs428.project.gather.data.form;

import cs428.project.gather.data.model.*;
import cs428.project.gather.utilities.GsonHelper;
import cs428.project.gather.validator.*;

import java.util.*;
import com.google.gson.*;
import org.springframework.validation.Errors;

/**
 * 
 * @author Team Gather
 * This class represents the data object to update existing events 
 * 
 */
public class UpdateEventData extends NewEventData {
	private Long eventId;
	private List<String> owners = null;
	private List<String> participants= null;

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
	public static UpdateEventData parseIn(String rawData, AbstractValidator validator, Errors errors) {
		System.out.println("rawData: " + rawData);
		Gson gson = GsonHelper.getGson();
		UpdateEventData updateEventData = gson.fromJson(rawData, UpdateEventData.class);
		updateEventData.validate(validator, errors);
		return updateEventData;
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

	public List<String> getOwners() {
		return owners;
	}

	public List<String> getParticipants() {
		return participants;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long id) {
		this.eventId = id;
	}
}
