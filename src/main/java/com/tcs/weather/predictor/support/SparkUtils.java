package com.tcs.weather.predictor.support;


import com.tcs.weather.predictor.constants.Constants;

import org.apache.spark.sql.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


/**
 * @author Vishnu
 */
public class SparkUtils {

    private static final Logger logger = LoggerFactory.getLogger(SparkUtils.class);

    public static SparkSession createSparkContext ( ServiceConfig.Spark sparkconfig ) {
        logger.info("Setting spark master as {} and app name as {}", sparkconfig.master, sparkconfig.appName);
        return SparkSession
                .builder().master(sparkconfig.master)
                .appName(sparkconfig.appName)
                .getOrCreate();
    }

    public static Dataset <Row> loadDataSet ( SparkSession spark, ServiceConfig.Input input ) {

        // Generate the schema based on the string of schema

        StructType schema = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("station", DataTypes.StringType, true),
                DataTypes.createStructField("date", DataTypes.TimestampType, true),
                DataTypes.createStructField("temperature", DataTypes.DoubleType, true)
                , DataTypes.createStructField("humidity", DataTypes.DoubleType, true),
                DataTypes.createStructField("pressure", DataTypes.DoubleType, true),
                DataTypes.createStructField("condition", DataTypes.StringType, true)
        ));


        //TODO:Validation
        return spark.read()
                .option(Constants.HEADER, true)
                .schema(schema)
                .csv(input.dataPath);

    }


    public static boolean saveDataFrameAsCSV ( Dataset ds, String filePath ) {
        ds.coalesce(1).write().format("text")
                    .save(filePath);
        return true;
    }


}
