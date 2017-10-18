package com.tcs.weather.predictor.support;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;


/**
 * Created by vishnuvr on 18/10/2017.
 */
public class SparkUtilsTest {

    private transient SparkSession spark;
    private ServiceConfig config;


    @Before
    public void setUp () throws Exception {
         config = ServiceConfig.getConfig();
         spark = SparkUtils.createSparkContext(config.spark);

    }

    @Test
    public void testCreateSparkContext(){
        assertNotNull(spark);
    }

    @Test
    public void testCreateSparkContextMaster(){
        assertEquals("local[4]",config.spark.master);
    }

    @Test
    public void testCreateSparkContextAppName(){
        assertEquals("Toy Weather Predictor",config.spark.appName);
    }


    @Test
    public void testLoadDataSet(){
    assertNotNull(SparkUtils.loadDataSet(spark,config.input));
    }


    @Test
    public void testLoadDataSetSchema(){
        Dataset <Row> rowDataset = SparkUtils.loadDataSet(spark, config.input);
        StructType expectedSchema = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("station", DataTypes.StringType, true),
                DataTypes.createStructField("date", DataTypes.TimestampType, true),
                DataTypes.createStructField("temperature", DataTypes.DoubleType, true)
                , DataTypes.createStructField("humidity", DataTypes.DoubleType, true),
                DataTypes.createStructField("pressure", DataTypes.DoubleType, true),
                DataTypes.createStructField("condition", DataTypes.StringType, true)
        ));
        assertEquals("Schema is not matching",expectedSchema,rowDataset.schema());
    }




}
