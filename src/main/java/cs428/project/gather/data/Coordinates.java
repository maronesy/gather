package cs428.project.gather.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Coordinates {
	private double latitude;
	private double longitude;

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(latitude);
		builder.append(longitude);
		int hashCode = builder.toHashCode();
		return hashCode;
	}

	public boolean equals(Object anotherObject) {
		boolean equal = false;

		if(anotherObject == this) {
			equal = true;

		} else if(anotherObject != null && anotherObject.getClass().equals(this.getClass())) {
			Coordinates anotherCoordinates = (Coordinates)anotherObject;
			EqualsBuilder equalsBuilder = new EqualsBuilder();

			equalsBuilder.append(latitude, anotherCoordinates.latitude);
			equalsBuilder.append(longitude, anotherCoordinates.longitude);
			equal = equalsBuilder.isEquals();
		}

		return equal;
	}
}

