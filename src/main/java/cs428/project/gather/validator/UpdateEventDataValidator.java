package cs428.project.gather.validator;

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
			validateEventName(updateEventData, errors);
			validateEventCoords(updateEventData, errors);
			validateEventDescription(updateEventData, errors);
			validateEventCategory(updateEventData, errors);
			validateCallerCoordinates(updateEventData, errors);
			validateNonNullRemoveAndAddLists(updateEventData, errors);
		}

	}

	private void validateNonNullRemoveAndAddLists(UpdateEventData updateEventData, Errors errors) {
		if(!errors.hasErrors()){
			List<Occurrence> occurrencesToAdd = updateEventData.getOccurrencesToAdd();
			List<Occurrence> occurrencesToRemove =  updateEventData.getOccurrencesToRemove();
			List<String> ownersToAdd = updateEventData.getOwnersToAdd();
			List<String> ownersToRemove = updateEventData.getOwnersToRemove();
			List<String> participantsToAdd = updateEventData.getParticipantsToAdd();
			List<String> participantsToRemove = updateEventData.getOwnersToRemove();

			if(occurrencesToAdd==null||occurrencesToRemove==null ||
					ownersToAdd==null||ownersToRemove==null ||
					participantsToAdd==null||participantsToRemove==null){
				String message = "Cannot update event. Some remove and add lists for occurrence, owners or particpants are not defined in JSON";
				errors.reject("-7", message);
			}
		}
	}
}
