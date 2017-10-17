package com.tcs.weather.predictor.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import static java.lang.String.join;

/**
 * @author Vishnu
 */

public class WeatherDTO implements Serializable {


    private String station;

    private Timestamp date;

    private String condition;

    private double temperature;

    private double pressure;

    private double humidity;

    private double latitude;

    private double longitude;

    private double altitude;

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

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

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    @Override
    public String toString() {

        return join("||", station, latitude + "," + longitude + "," + altitude, date.toString(), condition, Double.toString(temperature),
                Double.toString(pressure), Double.toString(humidity));
    }



}
