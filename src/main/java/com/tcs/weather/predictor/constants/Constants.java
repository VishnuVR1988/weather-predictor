package com.tcs.weather.predictor.constants;

/**
 * @author Vishnu
 */
public class Constants {

    private Constants(){}

    public static final String ARIMA = "arima";
    public static final String APPLICATION_PROPERTY_FILE="src/main/resources/predictor.properties";
    public static final String INPUT_DIRECTORY = "inputDirectory";
    public static final String SPARK_MASTER = "sparkMaster";
    public static final String APP_NAME = "appName";
    public static final String HEADER = "header";
    public static final String INFER_SCHEMA = "inferSchema";
    public static final String MODEL_TYPE = "modelType";
    public static final String MODEL_COEFFICIENT = "modelCoefficient";

    public static final String DATE = "date";
    public static final String STATION = "station";
    public static final String CSS_CGD = "css-cgd";
    public static final String RAIN = "rain";
    public static final String SUNNY = "sunny";
    public static final String CONDITION = "condition";
    public static final String TEMPERATURE = "temperature";
    public static final String PRESSURE = "pressure";
    public static final String HUMIDITY = "humidity";


    public static final String GEO_CODE_URL = "http://maps.googleapis.com/maps/api/geocode/json?address=%s";
    public static final String COMMA = ",";

    public static final String GEO_ELEVATION_URL = "https://maps.googleapis.com/maps/api/elevation/json?locations=%s,%s";
    public static final String API_KEY = "AIzaSyCKG0jFzonJ9uXR8rmE32Jm_eT2KScdKQ4";


    public static final String PREDICTED = "predicted";
    public static final String PREDICTION = "prediction";
    public static final String LABEL = "label";
}
