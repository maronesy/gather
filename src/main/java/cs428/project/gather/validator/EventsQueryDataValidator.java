package cs428.project.gather.validator;

import cs428.project.gather.data.EventsQueryData;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventsQueryDataValidator extends AbstractValidator
{

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
		
		EventsQueryData eventData = (EventsQueryData)target;

		if(eventData == null)
		{
			throw new IllegalArgumentException("The user registration data cannot be null.");
		}
		else
		{
			validateTimeWindow(eventData, errors);
			validateLatitude(eventData, errors);
			validateLongitude(eventData, errors);
			validateRadius(eventData, errors);

		}
	}

	private void validateTimeWindow(EventsQueryData eventData, Errors errors)
	{
		int timeWindow = eventData.getHour();
		if(timeWindow < -1 || timeWindow == 0){
			String message = "Field invalid-" + EventsQueryData.TIME_WINDOW_FIELD_NAME;
			errors.reject("-3",message+":The time window must be greater than 0 hours, or it can be -1 to indicate returning all events in the next year.");
		}
	}

	private void validateLatitude(EventsQueryData eventData, Errors errors)
	{
		float latitude = eventData.getLatitude();
		if(latitude < -90 || latitude > 90)
		{
			String message = "Field invalid-" + EventsQueryData.LATITUDE_FIELD_NAME;
			errors.reject("-3", message+":The latitude value is out of range.");
		}
	}

	private void validateLongitude(EventsQueryData eventData, Errors errors)
	{
		float longitude = eventData.getLongitude();
		if(longitude < -180 || longitude > 180)
		{
			String message = "Field invalid-" + EventsQueryData.LONGITUDE_FIELD_NAME;
			errors.reject("-3", message+":The longitude value is out of range.");
		}
	}
	
	private void validateRadius(EventsQueryData eventData, Errors errors)
	{
		float radius = eventData.getRadiusMi();
		if(radius <= 0 || radius > EventsQueryData.MAX_RADIUS )
		{
			String message = "Field invalid-" + EventsQueryData.RADIUS_MI_FIELD_NAME;
			errors.reject("-3", message+":The radius value must be greater than zero but less than " + EventsQueryData.MAX_RADIUS + ".");
		}
	}
}
