package cs428.project.gather.data;

/**
 * 
 * @author Team Gather
 * 
 * This is a coordinates class that can be used when a new coordinates object needs to be constructed for new users
 * or events
 *
 */

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
}