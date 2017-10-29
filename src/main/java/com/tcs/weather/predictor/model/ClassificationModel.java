package com.tcs.weather.predictor.model;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Abstract ClassificationModel For machine learning predictor.
 *
 * @author Vishnu
 * @version 1.0.0
 * @since 1.0.0
 */

public interface ClassificationModel {

    /**
     * The abstract method performs the classification on predictionDataSet based on trainDataSet
     * @param trainDataSet      - Input training data
     * @param predictionDataSet - Actual data on which prediction to be applied.
     * @return the predicted dataset with classification applied
     */

    Dataset <Row> applyClassification ( Dataset <Row> trainDataSet, Dataset <Row> predictionDataSet );

}
