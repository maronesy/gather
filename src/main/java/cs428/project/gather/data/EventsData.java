package cs428.project.gather.data;

public class EventsData {

	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";
	public static final String RADIUS_KM = "radiusKm";

	private float longitude;
	private float latitude;
	private float radiusKm;
	
	public float getLongitude()
	{
		return longitude;
	}

	public void setLongitude(float longitude)
	{
		this.longitude = longitude;
	}

	public float getLatitude()
	{
		return latitude;
	}

	public void setLatitude(float latitude)
	{
		this.latitude = latitude;
	}

	public float getRadiusKm()
	{
		return radiusKm;
	}

	public void setRadiusKm(float radiusKm)
	{
		this.radiusKm = radiusKm;
	}

}

