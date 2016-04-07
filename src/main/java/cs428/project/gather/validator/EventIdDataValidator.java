package cs428.project.gather.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import cs428.project.gather.data.EventsQueryData;
import cs428.project.gather.data.EventIdData;

@Component
public class EventIdDataValidator extends AbstractValidator {

	@Override
	public boolean supports(Class<?> targetClass)
	{
		boolean supported = false;

		if(EventsQueryData.class.equals(targetClass))
		{
			supported = true;
		}

		return supported;
	}

	@Override
	public void validate(Object target, Errors errors)
	{
		
		EventIdData eventData = (EventIdData)target;

		if(eventData == null)
		{
			throw new IllegalArgumentException("The user registration data cannot be null.");
		}
		else
		{
			validateEventId(eventData, errors);
		}
	}

	private void validateEventId(EventIdData joinData, Errors errors)
	{
		Long id = joinData.getEventId();
		if(id < 0 || id == null){
			String message = "Field invalid-" + "id";
			errors.reject("-3",message+":The id must be number greater than 0.");
		} 
	}

}
