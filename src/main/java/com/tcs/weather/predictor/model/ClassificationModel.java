package com.tcs.weather.predictor.model;

import com.tcs.weather.predictor.exception.WeatherPredictionException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Abstract ClassificationModel For machine learning predictor.
 * @author Vishnu
 */
public interface ClassificationModel {

    /**
     *
     * @param trainDataSet
     * @param predictionDataSet
     * @return
     * @throws WeatherPredictionException
     */
    Dataset<Row> applyClassification ( Dataset <Row> trainDataSet, Dataset <Row> predictionDataSet ) throws WeatherPredictionException;

}
