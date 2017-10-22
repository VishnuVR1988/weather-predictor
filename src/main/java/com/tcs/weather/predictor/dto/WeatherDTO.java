package com.tcs.weather.predictor.dto;

import com.tcs.weather.predictor.constants.Constants;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static java.lang.String.join;

/**
 * This class correspons to weather prediction output fields
 * @author Vishnu
 * @since 1.0.0
 * @version 1.0.0
 */

public class WeatherDTO implements Serializable {

    private double altitude;

    private String condition;

    private Timestamp date;

    private double humidity;

    private double latitude;

    private double longitude;

    private double pressure;

    private String station;


    private double temperature;


    public String getStation () {
        return station;
    }

    public void setStation ( String station ) {
        this.station = station;
    }

    public Timestamp getDate () {
        return date;
    }

    public void setDate ( Timestamp date ) {
        this.date = date;
    }

    public String getCondition () {
        return condition;
    }

    public void setCondition ( String condition ) {
        this.condition = condition;
    }

    public double getTemperature () {
        return temperature;
    }

    public void setTemperature ( double temperature ) {
        this.temperature = temperature;
    }

    public double getPressure () {
        return pressure;
    }

    public void setPressure ( double pressure ) {
        this.pressure = pressure;
    }

    public double getHumidity () {
        return humidity;
    }

    public void setHumidity ( double humidity ) {
        this.humidity = humidity;
    }

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

        return join("|", station, joinLatLngAlt(), formatDate(), condition, format(temperature),
                format(pressure), format(humidity));
    }


    private String format(double value){
        return String.format("%.2f", value);
    }

    private String joinLatLngAlt(){
        return String.format("%.2f,%.2f,%.2f",latitude,longitude,altitude);
    }


    private String formatDate(){
        String dateTimeFormatPattern = Constants.OUT_DATE_FORMAT;
        final DateFormat format = new SimpleDateFormat(dateTimeFormatPattern);
        return format.format(date);

    }




}
