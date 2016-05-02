package cs428.project.gather.validator;

import cs428.project.gather.data.form.EventsQueryData;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/***
 * 
 * @author Team Gather
 *
 * This class validates that queries for events have valid time windows, coordinates, and radius fields.
 */
@Component
public class EventsQueryDataValidator extends AbstractValidator {
	@Override
	public boolean supports(Class<?> targetClass) {
		return EventsQueryData.class.equals(targetClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		EventsQueryData queryData = (EventsQueryData)target;
		if (queryData == null) {
			throw new IllegalArgumentException("The user registration data cannot be null.");
		} else {
			validateTimeWindow(queryData, errors);
			validateLatitude(queryData, errors);
			validateLongitude(queryData, errors);
			validateRadius(queryData, errors);
		}
	}

	private void validateTimeWindow(EventsQueryData queryData, Errors errors) {
		int timeWindow = queryData.getHour();
		if(timeWindow < -1 || timeWindow == 0){
			String message = "Field invalid-" + EventsQueryData.TIME_WINDOW_FIELD_NAME;
			errors.reject("-3",message+":The time window must be greater than 0 hours, or it can be -1 to indicate returning all events in the next year.");
		}
	}

	private void validateLatitude(EventsQueryData queryData, Errors errors) {
		float latitude = queryData.getLatitude();
		if(latitude < -90 || latitude > 90) {
			String message = "Field invalid-" + EventsQueryData.LATITUDE_FIELD_NAME;
			errors.reject("-3", message+":The latitude value is out of range.");
		}
	}

	private void validateLongitude(EventsQueryData queryData, Errors errors) {
		float longitude = queryData.getLongitude();
		if(longitude < -180 || longitude > 180) {
			String message = "Field invalid-" + EventsQueryData.LONGITUDE_FIELD_NAME;
			errors.reject("-3", message+":The longitude value is out of range.");
		}
	}

	private void validateRadius(EventsQueryData queryData, Errors errors) {
		float radius = queryData.getRadiusMi();
		if(radius <= 0 || radius > EventsQueryData.MAX_RADIUS ) {
			String message = "Field invalid-" + EventsQueryData.RADIUS_MI_FIELD_NAME;
			errors.reject("-3", message+":The radius value must be greater than zero but less than " + EventsQueryData.MAX_RADIUS + ".");
		}
	}
}
