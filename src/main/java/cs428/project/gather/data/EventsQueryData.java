package cs428.project.gather.data;

public class EventsQueryData {

//	public static final String LONGITUDE = "longitude";
//	public static final String LATITUDE = "latitude";
//	public static final String RADIUS_KM = "radiusMi";

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

