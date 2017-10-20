package com.tcs.weather.predictor.support;


import com.tcs.weather.predictor.constants.Constants;

import com.tcs.weather.predictor.dto.Geocode;
import com.tcs.weather.predictor.dto.WeatherDTO;
import com.tcs.weather.predictor.exception.WeatherPredictionException;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.spark.sql.functions.lit;


/**
 * This class consists collection of static methods for spark based operations
 *
 * @author Vishnu
 * @version 1.0.0
 * @since 1.0.0
 */

public class SparkUtils {

    private static final Logger logger = LoggerFactory.getLogger(SparkUtils.class);

    /**
     * This method constructs the spark master from provided config
     *
     * @param sparkconfig
     * @return
     */

    public static SparkSession createSparkContext ( ServiceConfig.Spark sparkconfig ) {
        logger.info("Setting spark master as {} and app name as {}", sparkconfig.master, sparkconfig.appName);
        return SparkSession
                .builder().master(sparkconfig.master)
                .appName(sparkconfig.appName)
                .getOrCreate();
    }


    /**
     * This method retrieves the list of files under input directory
     *
     * @param sparkSession
     * @param path
     * @return
     * @throws WeatherPredictionException
     */
    public static List <String> getInputFiles ( SparkSession sparkSession, String path ) throws WeatherPredictionException {
        try {
            JavaRDD <Tuple2 <String, String>> stationData = sparkSession.sparkContext().
                    wholeTextFiles(path, 1).toJavaRDD();
            JavaRDD <String> filesRDD = stationData.map((Function <Tuple2 <String, String>, String>) tuple2 -> tuple2._1);
            return filesRDD.collect();

        } catch (Exception e) {
            throw new WeatherPredictionException(e.getCause(), String.format("Failed to locate files under the path %s", path));
        }

    }


    /**
     * This method returns a struct schema object
     *
     * @return
     */
    protected static StructType getStructType () {
        return DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField(Constants.STATION, DataTypes.StringType, false),
                DataTypes.createStructField(Constants.DATE, DataTypes.TimestampType, false),
                DataTypes.createStructField(Constants.TEMPERATURE, DataTypes.DoubleType, true)
                , DataTypes.createStructField(Constants.HUMIDITY, DataTypes.DoubleType, true),
                DataTypes.createStructField(Constants.PRESSURE, DataTypes.DoubleType, true),
                DataTypes.createStructField(Constants.CONDITION, DataTypes.StringType, true)
        ));
    }


    /**
     * This mthod returns a dataset object from an input file path
     *
     * @param spark
     * @param path
     * @return
     */
    public static Dataset <Row> loadDataSet ( SparkSession spark, String path ) {

        // Generate the schema based on the string of schema
        StructType schema = getStructType();
        return spark.read()
                .option(Constants.HEADER, true)
                .schema(schema)
                .csv(path);

    }


    /**
     * This method saves the output dataset to a file
     *
     * @param ds
     * @param filePath
     * @return
     * @throws WeatherPredictionException
     */

    public static boolean saveDataSet ( Dataset ds, String filePath ) throws WeatherPredictionException{
        try {
            ds.coalesce(1).write().mode(SaveMode.Overwrite).csv(filePath);
            logger.info("Dataset output successfully written to folder {}", filePath);
        }
        catch (Exception e){
            throw new WeatherPredictionException(String.format("Failed to save final datset under {}",filePath));
        }

        return true;
    }


    /**
     * This method creates an empty dataset with pre defined schema
     *
     * @param sparkSession
     * @return
     */
    public static Dataset <Row> createEmptyDataSet ( SparkSession sparkSession ) {
        // Generate the schema based on the string of schemas
        StructType schema = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField(Constants.STATION, DataTypes.StringType, false),
                DataTypes.createStructField(Constants.DATE, DataTypes.TimestampType, false),
                DataTypes.createStructField(Constants.TEMPERATURE, DataTypes.DoubleType, true)
                , DataTypes.createStructField(Constants.HUMIDITY, DataTypes.DoubleType, true),
                DataTypes.createStructField(Constants.PRESSURE, DataTypes.DoubleType, true),
                DataTypes.createStructField(Constants.CONDITION, DataTypes.StringType, true),
                DataTypes.createStructField(Constants.LATITUDE, DataTypes.DoubleType, true),
                DataTypes.createStructField(Constants.LONGITUDE, DataTypes.DoubleType, true),
                DataTypes.createStructField(Constants.ALTITUDE, DataTypes.DoubleType, true)));

        List <Row> rows = new ArrayList <>();
        return sparkSession.createDataFrame(rows, schema);
    }


    /**
     * Returns java spark context from spark session object
     *
     * @param sparkSession
     * @return
     */
    public static JavaSparkContext getJavaSparkContext ( SparkSession sparkSession ) {
        return JavaSparkContext.fromSparkContext(sparkSession.sparkContext());
    }


    /**
     * This method enriches the dataset with location
     *
     * @param ds
     * @param geocode
     * @return
     */
    public static Dataset <Row> enrichWithGeoPoints ( Dataset <Row> ds, Geocode geocode ) {
        return ds.
                withColumn(Constants.LATITUDE, lit(geocode.getLatitude()))
                .withColumn(Constants.LONGITUDE, lit(geocode.getLongitude()))
                .withColumn(Constants.ALTITUDE, lit(geocode.getAltitude()));

    }


    /**
     * Coverts the row dataset to string dataset by parsing to WeatherDTO
     *
     * @param ds
     * @return
     */
    public static Dataset <String> convertToStringDataset ( Dataset <Row> ds ) {
        return ds.as(Encoders.bean(WeatherDTO.class)).orderBy(Constants.DATE)
                .map(
                        (MapFunction <WeatherDTO, String>) WeatherDTO::toString,
                        Encoders.STRING());

    }


}
