package com.tcs.weather.predictor.model.randomforest;

import com.tcs.weather.predictor.ModelLoader;
import com.tcs.weather.predictor.model.ClassificationModel;
import com.tcs.weather.predictor.support.ServiceConfig;
import com.tcs.weather.predictor.support.SparkUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class consists of test methods for Random Forest Model Class
 *
 * @author Vishnu
 * @version 1.0.0
 * @since 1.0.0
 */

public class RandomForestClassificationTest {

    private transient SparkSession spark;
    private ServiceConfig config;
    private ClassificationModel conditionModel;

    @Before
    public void setUp () throws Exception {
        config = ServiceConfig.getConfig();
        spark = SparkUtils.createSparkContext(config.spark);
        conditionModel = ModelLoader.loadModel(new RandomForestClassification(), "condition");

    }

    @After
    public void tearDown () throws Exception {
        spark.stop();
    }

    @Test
    public void testPointForecast () throws Exception {
        Dataset <Row> rowDataset = SparkUtils.loadDataSet(spark, config.input.dataPath);
        Dataset[] splits = rowDataset.randomSplit(new double[] { 0.7, 0.3 });
        Dataset<Row> trainingData = splits[0];
        Dataset<Row> testData = splits[1];
        assertNotNull(conditionModel.applyClassification(trainingData,testData));
    }
}