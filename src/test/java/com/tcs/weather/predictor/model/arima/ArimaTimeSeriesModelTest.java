package com.tcs.weather.predictor.model.arima;

import com.tcs.weather.predictor.ModelLoader;
import com.tcs.weather.predictor.model.TimeSeriesModel;
import com.tcs.weather.predictor.support.ServiceConfig;
import com.tcs.weather.predictor.support.SparkUtils;
import org.apache.spark.sql.SparkSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * This class consists of test methods for Arima TimeSeries Model Class
 *
 * @author Vishnu
 * @version 1.0.0
 * @since 1.0.0
 */

public class ArimaTimeSeriesModelTest {

    private transient SparkSession spark;
    private ServiceConfig config;
    private TimeSeriesModel pressureModel;

    @After
    public void tearDown () throws Exception {
        spark.stop();

    }

    @Before
    public void setUp () throws Exception {
        config = ServiceConfig.getConfig();
        spark = SparkUtils.createSparkContext(config.spark);
        pressureModel = ModelLoader.loadModel(new ArimaTimeSeriesModel(), "pressure");

    }

    @Test
    public void testPointForecast () throws Exception {
        assertNotNull(pressureModel.pointForecast(SparkUtils.loadDataSet(spark, config.input.dataPath),spark,10));
    }

    @Test
    public void testPointForecastStepSize () throws Exception {
        assertEquals(10,pressureModel.pointForecast(SparkUtils.loadDataSet(spark, config.input.dataPath ),spark,10).count());
    }



}