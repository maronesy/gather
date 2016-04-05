package cs428.project.gather.data;

public class EventsQueryData {

	public static final String LONGITUDE_FIELD_NAME = "longitude";
	public static final String LATITUDE_FIELD_NAME = "latitude";
	public static final String RADIUS_MI_FIELD_NAME = "radiusMi";
	public static final String TIME_WINDOW_FIELD_NAME = "hour";
	
	public static final float MAX_RADIUS = 50f;

	private float longitude;
	private float latitude;
	private float radiusMi;
	private int hour = -1;
	
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

	public float getRadiusMi()
	{
		return radiusMi;
	}

	public void setRadiusMi(float radiusKm)
	{
		this.radiusMi = radiusKm;
	}
	
	public int getHour(){
		return hour;
	}
	
	public void setHour(int hour){
		this.hour = hour;		
	}
	
}

