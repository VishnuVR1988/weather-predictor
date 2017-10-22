package com.tcs.weather.predictor.constants;

/**
 * This class contains commonly used constants
 * @author Vishnu
 * @since 1.0.0
 * @version 1.0.0
 */
public class Constants {


    public static final String OUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private Constants(){}

    public static final String HEADER = "header";

    //INPUT COLUMNS

    public static final String DATE = "date";
    public static final String STATION = "station";
    public static final String PRESSURE = "pressure";
    public static final String HUMIDITY = "humidity";
    public static final String TEMPERATURE = "temperature";
    public static final String CONDITION = "condition";

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ALTITUDE = "altitude";


    public static final String GEO_ELEVATION_URL = "https://maps.googleapis.com/maps/api/elevation/json?locations=%s,%s";
    public static final String API_KEY = "AIzaSyCKG0jFzonJ9uXR8rmE32Jm_eT2KScdKQ4";

    public static final String PREDICTED = "predicted";
    public static final String PREDICTION = "prediction";
    public static final String LABEL = "label";
}
