package com.tcs.weather.predictor.model;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Abstract TimeSeriesModel For machine learning predictor.
 *
 * @author Vishnu
 * @version 1.0.0
 * @since 1.0.0
 */

public interface TimeSeriesModel {

    /**
     * @param inputDataSet - Input training data
     * @param spark        - Spark Session Object
     * @param steps        - future steps for timeseries forecast
     * @return the dataset with timeseries forecast
     */

    Dataset <Row> pointForecast ( Dataset <Row> inputDataSet, SparkSession spark, int steps );

}
