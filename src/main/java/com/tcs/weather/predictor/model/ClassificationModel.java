package com.tcs.weather.predictor.model;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Abstract ClassificationModel For machine learning predictor.
 * @author Vishnu
 */
public interface ClassificationModel {

    /**
     *
     * @param trainDataSet - Input training data
     * @param predictionDataSet - Actula data on which prediction to be applied.
     * @return predicted datset with classfication applied
     */

    Dataset<Row> applyClassification ( Dataset <Row> trainDataSet, Dataset <Row> predictionDataSet );

}
