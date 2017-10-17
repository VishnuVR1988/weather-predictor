package com.tcs.weather.predictor.dto;

/**
 * Created by vishnuvr on 14/10/2017.
 */
public class Geocode {

    private double latitude;

    private double longitude;

    private double altitude;

    public double getLatitude () {
        return latitude;
    }

    public void setLatitude ( double latitude ) {
        this.latitude = latitude;
    }

    public double getLongitude () {
        return longitude;
    }

    public void setLongitude ( double longitude ) {
        this.longitude = longitude;
    }

    public double getAltitude () {
        return altitude;
    }

    public void setAltitude ( double altitude ) {
        this.altitude = altitude;
    }

    @Override
    public String toString () {
        return "Geocode{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                '}';
    }
}
