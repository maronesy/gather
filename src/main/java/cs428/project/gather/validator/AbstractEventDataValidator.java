package cs428.project.gather.validator;

import cs428.project.gather.data.*;
import cs428.project.gather.data.form.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.repo.*;

import java.util.List;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public abstract class AbstractEventDataValidator extends AbstractValidator {
	@Autowired
    EventRepository eventRepo;

	@Autowired
	CategoryRepository categoryRepo;
	
	protected void validateEventName(NewEventData newEventData, Errors errors) {
		String eventName = newEventData.getEventName();
		if(eventName == null)
		{
			String message = "Field required-" + NewEventData.EVENT_NAME_FIELD_NAME;
			errors.reject("-1", message+":Event name is a required field.");
		}
		else if(eventName.length() < 2 || eventName.length() > 128)
		{
			String message = "Field invalid-" + NewEventData.EVENT_NAME_FIELD_NAME;
			errors.reject("-2", message+":The event name must be between 2 and 128 characters.");
		}
	}

	protected void validateEventTime(NewEventData newEventData, Errors errors) {
		long eventTime = newEventData.getEventTime();
		if(eventTime < DateTime.now().getMillis()){
			String message = "Field invalid-" + NewEventData.EVENT_TIME_FIELD_NAME;
			errors.reject("-3", message+":Event time must be a valid time in the future.");
		}
	}

	protected void validateEventCategory(NewEventData newEventData, Errors errors) {
		String categoryStr = newEventData.getEventCategory();
		if(categoryStr == null)
		{
			String message = "Field required-" + NewEventData.EVENT_CATEGORY_FIELD_NAME;
			errors.reject("-1", message+":Event category is a required field.");
		}
		else{
			List<Category> results  = this.categoryRepo.findByName(categoryStr);
			if(results.size()!=1){
				String message = "Field invalid-" + NewEventData.EVENT_CATEGORY_FIELD_NAME;
				errors.reject("-5", message+":The input category was not recognized as a valid category.");
			}
		}

	}

	protected void validateEventDescription(NewEventData newEventData, Errors errors) {
		String eventDescription = newEventData.getEventDescription();
		if(eventDescription == null)
		{
			String message = "Field required-" + NewEventData.EVENT_DESCRIPTION_FIELD_NAME;
			errors.reject("-1", message+":Event description is a required field.");
		}
		else if(eventDescription.length() < 2 || eventDescription.length() > 500)
		{
			String message = "Field invalid-" + NewEventData.EVENT_DESCRIPTION_FIELD_NAME;
			errors.reject("-2", message+":The event description must be between 2 and 500 characters.");
		}
	}

	protected void validateEventCoords(NewEventData newEventData, Errors errors) {
		Coordinates eventCoords = newEventData.getEventCoodinates();
		if(eventCoords == null)
		{
			String message = "Field required-" + NewEventData.EVENT_COORDS_FIELD_NAME;
			errors.reject("-1", message+":Event coordinates is a required field.");
		}
		else if(eventCoords.getLatitude() < -90 || eventCoords.getLatitude() > 90)
		{
			String message = "Field invalid-" + NewEventData.EVENT_COORDS_FIELD_NAME;
			errors.reject("-3", message+":The latitude value is out of range.");
		}
		else if(eventCoords.getLongitude() < -180 || eventCoords.getLongitude() > 180)
		{
			String message = "Field invalid-" + NewEventData.EVENT_COORDS_FIELD_NAME;
			errors.reject("-3", message+":The longitude value is out of range.");
		}
	}

	protected void validateCallerCoordinates(NewEventData newEventData, Errors errors) {
		Coordinates callerCoords = newEventData.getCallerCoodinates();
		if(callerCoords == null)
		{
			String message = "Field required-" + NewEventData.CALLER_COORDS_FIELD_NAME;
			errors.reject("-1", message+":User coordinates is a required field.");
		}
		else if(callerCoords.getLatitude() < -90 || callerCoords.getLatitude() > 90)
		{
			String message = "Field invalid-" + NewEventData.CALLER_COORDS_FIELD_NAME;
			errors.reject("-3", message+":The latitude value is out of range.");
		}
		else if(callerCoords.getLongitude() < -180 || callerCoords.getLongitude() > 180)
		{
			String message = "Field invalid-" + NewEventData.CALLER_COORDS_FIELD_NAME;
			errors.reject("-3", message+":The longitude value is out of range.");
		}
	}

}
