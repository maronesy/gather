package cs428.project.gather.validator;

import cs428.project.gather.data.form.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.repo.*;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class NewEventDataValidator extends AbstractEventDataValidator {
	@Autowired
    EventRepository eventRepo;

	@Autowired
	CategoryRepository categoryRepo;

	@Override
	public boolean supports(Class<?> targetClass) {
		return NewEventData.class.equals(targetClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		NewEventData newEventData = (NewEventData)target;

		if(newEventData == null)
		{
			throw new IllegalArgumentException("The new event data cannot be null.");
		}
		else
		{
			validateEventName(newEventData, errors);
			validateEventCoords(newEventData, errors);
			validateEventDescription(newEventData, errors);
			validateEventCategory(newEventData, errors);
			validateEventTime(newEventData, errors);
			validateCallerCoordinates(newEventData, errors);
			validateNotDuplicateEvent(newEventData, errors);
		}

	}

	private void validateNotDuplicateEvent(NewEventData newEventData, Errors errors) {
		if(!errors.hasErrors()){
			String name = newEventData.getEventName();
			double latitude = newEventData.getEventCoodinates().getLatitude();
			double longitude = newEventData.getEventCoodinates().getLongitude();
			Timestamp time = new Timestamp(newEventData.getEventTime());

			List<Event> foundEvents = this.eventRepo.findByNameAndLocationAndTime(name, latitude, longitude, time);
			if(!foundEvents.isEmpty()){
				String message = "Cannot create event. An existing event with the same name, location, and time already exists!";
				errors.reject("-4", message);
			}
		}
	}
}
