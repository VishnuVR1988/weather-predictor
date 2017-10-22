package com.tcs.weather.predictor.model;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Abstract TimeSeriesModel For machine learning predictor.
 * @author Vishnu
 */
public interface TimeSeriesModel {

    /**
     *
     * @param inputDataSet
     * @param spark
     * @param steps
     * @return
     */
    Dataset<Row> pointForecast ( Dataset <Row> inputDataSet, SparkSession spark, int steps );

}
