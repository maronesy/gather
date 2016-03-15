package cs428.project.gather.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class NewEventData {
//	public static final String EMAIL_FIELD_NAME = "email";
//	public static final String PASSWORD_FIELD_NAME = "password";

	private String eventName;
	private Coordinates eventCoordinates;
	private String eventDescription;
	private String eventCategory;
	private long eventTime;
	private Coordinates callerCoordinates;

	
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(eventName);
		builder.append(eventCoordinates);
		builder.append(eventDescription);
		builder.append(eventCategory);
		builder.append(eventTime);
		builder.append(callerCoordinates);
		

		int hashCode = builder.toHashCode();

		return hashCode;
	}

	@Override
	public boolean equals(Object anotherObject) {
		boolean equal = false;

		if (anotherObject == this) {
			equal = true;
		} else if (anotherObject != null && anotherObject.getClass().equals(this.getClass())) {
			NewEventData anotherEventData = (NewEventData) anotherObject;

			EqualsBuilder equalsBuilder = new EqualsBuilder();

			equalsBuilder.append(this.eventName, anotherEventData.eventName);
			equalsBuilder.append(this.eventCategory, anotherEventData.eventCoordinates);
			equalsBuilder.append(this.eventDescription, anotherEventData.eventDescription);
			equalsBuilder.append(this.callerCoordinates, anotherEventData.callerCoordinates);
			equalsBuilder.append(this.eventCoordinates, anotherEventData.eventCoordinates);
			equalsBuilder.append(this.eventTime, anotherEventData.eventTime);

			equal = equalsBuilder.isEquals();
		}
		return equal;
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

	public long getEventTime() {
		return eventTime;
	}

	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}

	public Coordinates getCallerCoodinates() {
		return callerCoordinates;
	}

	public void setCallerCoodinates(Coordinates callerCoodinates) {
		this.callerCoordinates = callerCoodinates;
	}
}
