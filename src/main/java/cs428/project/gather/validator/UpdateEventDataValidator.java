package cs428.project.gather.validator;

import cs428.project.gather.data.Coordinates;
import cs428.project.gather.data.form.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.repo.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class UpdateEventDataValidator extends AbstractEventDataValidator {
	@Autowired
    EventRepository eventRepo;

	@Autowired
	CategoryRepository categoryRepo;
	
	@Autowired
	RegistrantRepository registrantRepo;

	@Override
	public boolean supports(Class<?> targetClass) {
		return UpdateEventData.class.equals(targetClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UpdateEventData updateEventData = (UpdateEventData)target;

		if(updateEventData == null)
		{
			throw new IllegalArgumentException("The new event data cannot be null.");
		}
		else
		{
			validateEventExists(updateEventData, errors);
			validateEventName(updateEventData, errors);
			validateEventCoords(updateEventData, errors);
			validateEventDescription(updateEventData, errors);
			validateEventCategory(updateEventData, errors);
			validateParticipantsExists(updateEventData, errors);
			validateOwnersExists(updateEventData, errors);
		}

	}

	private void validateEventExists(UpdateEventData updateEventData, Errors errors) {
		if(eventRepo.findOne(updateEventData.getEventId())== null){
			String message = "Cannot update event. Event doesn't exist.";
			errors.reject("-5", message);
		}
		
	}

	private void validateParticipantsExists(UpdateEventData updateEventData, Errors errors) {
		if(!errors.hasErrors()){

			List<String> participants= updateEventData.getParticipants();

			if(participants!=null){
				for(int i=0; i<participants.size();i++){
					if(registrantRepo.findByDisplayName(participants.get(i))==null){
						String message = "Cannot update event. Some participants are not existed";
						errors.reject("-7", message);
					}
				}
			}
		}
	}
	
	private void validateOwnersExists(UpdateEventData updateEventData, Errors errors) {
		if(!errors.hasErrors()){

			List<String> owners= updateEventData.getOwners();

			if(owners!=null){
				for(int i=0; i<owners.size();i++){
					if(registrantRepo.findByDisplayName(owners.get(i))==null){
						String message = "Cannot update event. Some owners are not existed";
						errors.reject("-7", message);
					}
				}
				
			}
		}
	}

	@Override
	boolean nullNameCheck(String eventName, Errors errors) {
		if(eventName==null) return true;
		return false;
	}

	@Override
	boolean nullOccurrencesCheck(List<Long> eventOccurrences, Errors errors) {
		if(eventOccurrences==null) return true;
		return false;
	}

	@Override
	boolean nullCategoryCheck(String category, Errors errors) {
		if(category==null) return true;
		return false;
	}

	@Override
	boolean nullDescriptionCheck(String description, Errors errors) {
		if(description==null) return true;
		return false;
	}

	@Override
	boolean nullEventCoordinatesCheck(Coordinates eventCoords, Errors errors) {
		if(eventCoords==null) return true;
		return false;
	}
	
}
