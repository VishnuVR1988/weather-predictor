package com.tcs.weather.predictor.dto;

/**
 * This class is a wrapper of geo co ordinate fields
 * @author Vishnu
 * @since 1.0.0
 * @version 1.0.0
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
    public boolean equals ( Object o ) {
        if (this == o) return true;
        if (!(o instanceof Geocode)) return false;

        Geocode geocode = (Geocode) o;

        return Double.compare(geocode.latitude, latitude) == 0 && Double.compare(geocode.longitude, longitude) == 0 && Double.compare(geocode.altitude, altitude) == 0;
    }

    @Override
    public int hashCode () {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(altitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
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
