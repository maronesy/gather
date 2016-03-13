package cs428.project.gather.utilities;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticMeasurement;
import org.gavaghan.geodesy.GlobalPosition;

import cs428.project.gather.data.Coordinates;



public final class GeodeticHelper
{
	private static final double ONE_METER_IN_MILES = 0.000621371;

	private GeodeticHelper()
	{
	}

	public static double getDistanceBetweenCoordinates(Coordinates originCoordinates, Coordinates destinationCoordinates)
	{
		if(originCoordinates == null)
		{
			throw new IllegalArgumentException("The origin coordinates cannot be null.");
		}

		double originLatitude = originCoordinates.getLatitude();
		if(originLatitude < -90.0  || originLatitude > 90.0)
		{
			throw new IllegalArgumentException("The origin latitude value is out of range.");
		}

		double originLongitude = originCoordinates.getLongitude();
		if(originLongitude < -180.0 || originLongitude > 180.0)
		{
			throw new IllegalArgumentException("The origin longitude value is out of range.");
		}

		if(destinationCoordinates == null)
		{
			throw new IllegalArgumentException("The destination coordinates cannot be null.");
		}

		double destinationLatitude = destinationCoordinates.getLatitude();
		if(destinationLatitude < -90.0  || destinationLatitude > 90.0)
		{
			throw new IllegalArgumentException("The destination latitude value is out of range.");
		}

		double destinationLongitude = destinationCoordinates.getLongitude();
		if(destinationLongitude < -180.0 || destinationLongitude > 180.0)
		{
			throw new IllegalArgumentException("The destination longitude value is out of range.");
		}

		GlobalPosition originGlobalPosition = new GlobalPosition(originLatitude, originLongitude, 0.0);
		GlobalPosition destinationGlobalPosition = new GlobalPosition(destinationLatitude, destinationLongitude, 0.0);

		GeodeticCalculator geodeticCalculator = new GeodeticCalculator();

		GeodeticMeasurement geodeticMeasurement = geodeticCalculator.calculateGeodeticMeasurement(Ellipsoid.WGS84, originGlobalPosition, destinationGlobalPosition);

		double distanceInMeters = geodeticMeasurement.getEllipsoidalDistance();

		double distanceInMiles = distanceInMeters * ONE_METER_IN_MILES;

		return distanceInMiles;
	}
}
