package com.tcs.weather.predictor.constants;

/**
 * This class contains constants for machine learning algorithms
 *
 * @author Vishnu
 * @version 1.0.0
 * @since 1.0.0
 */

public class MLConstants {

    public static final String LINEAR = "linear";
    public static final String NEXT = "next";

    private MLConstants () {
    }


    //ARIMA Constants
    public static final String CSS_CGD = "css-cgd";

    public static final int P_HUMIDITY = 1;
    public static final int D_HUMIDITY = 0;
    public static final int Q_HUMIDITY = 2;

    public static final int P_PRESSURE = 1;
    public static final int D_PRESSURE = 0;
    public static final int Q_PRESSURE = 2;

    public static final int P_TEMPERATURE = 1;
    public static final int D_TEMPERATURE = 0;
    public static final int Q_TEMPERATURE = 1;

    //RANDOM FOREST CONSTANTS
    public static final int MAX_DEPTH = 3;
    public static final int NUM_TREES = 20;
    public static final String FEATURE_SUBSET_STRATEGY = "auto";
    public static final int SEED = 5043;
    public static final String FEATURES = "features";
    public static final String IMPURITY = "gini";
}
