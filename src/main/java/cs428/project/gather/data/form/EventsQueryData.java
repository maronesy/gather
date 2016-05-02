package cs428.project.gather.data.form;

import cs428.project.gather.validator.*;

import java.util.*;
import com.google.gson.*;
import org.springframework.validation.Errors;

/**
 * 
 * @author Team Gather
 * This class represents the data object to query events from backend
 * 
 */
public class EventsQueryData {
	/**
	 * Public static names for validator getting the field names
	 */
	public static final String LONGITUDE_FIELD_NAME = "longitude";
	public static final String LATITUDE_FIELD_NAME = "latitude";
	public static final String RADIUS_MI_FIELD_NAME = "radiusMi";
	public static final String TIME_WINDOW_FIELD_NAME = "hour";
	public static final float MAX_RADIUS = 50f;

	private Long eventId;
	private float longitude;
	private float latitude;
	private float radiusMi;
	private int hour = -1;
	private Set<String> categories = new HashSet<String>();
	private boolean useRegistrantProfile;

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
	public static EventsQueryData parseIn(String rawData, AbstractValidator validator, Errors errors) {
		System.out.println("rawData: " + rawData);
		EventsQueryData eventsQuery = (new Gson()).fromJson(rawData, EventsQueryData.class);
		eventsQuery.validate(validator, errors);
		return eventsQuery;
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

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long id) {
		this.eventId = id;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getRadiusMi() {
		return radiusMi;
	}

	public void setRadiusMi(float radiusKm) {
		this.radiusMi = radiusKm;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public Set<String> getCategories() {
		return categories;
	}

	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}

	public boolean getUseRegistrantProfile() {
		return useRegistrantProfile;
	}

	public void setUseRegistrantProfile(boolean useRegistrantProfile) {
		this.useRegistrantProfile = useRegistrantProfile;
	}
}

