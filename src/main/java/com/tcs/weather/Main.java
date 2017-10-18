package com.tcs.weather;

import com.tcs.weather.predictor.dto.Geocode;
import com.tcs.weather.predictor.model.ClassificationModel;
import com.tcs.weather.predictor.exception.WeatherPredictionException;
import com.tcs.weather.predictor.model.arima.ArimaTimeSeriesModel;
import com.tcs.weather.predictor.model.randomforest.RandomForestClassification;
import com.tcs.weather.predictor.support.GeoUtils;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;

import static org.apache.spark.sql.functions.lit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcs.weather.predictor.model.TimeSeriesModel;
import com.tcs.weather.predictor.ModelLoader;
import com.tcs.weather.predictor.dto.WeatherDTO;
import com.tcs.weather.predictor.constants.Constants;
import com.tcs.weather.predictor.support.ServiceConfig;
import com.tcs.weather.predictor.support.SparkUtils;

import java.io.IOException;


/**
 * This is the main entry point for weather predictor class.
 * <p>
 * <p>
 * The model is built upon historic data to forecast temperature , humidity , pressure and weather condition.
 * As the weather observations are over a period of time, time-series analysis using ARIMA is used to
 * forecast weather parameters like temperature ,pressure and humidity. These have been assumed as univariate variables.
 * </p>
 * <p>
 * <p>
 * <p>
 * The predictor is driven by <b>application.conf</b> placed under the resources. This can be overriden by placing
 * same file under the conf folder and run using weather-predictor.sh.
 * <p>
 * The application loads the properties from the above file and accepts the number of future forecast steps
 * as a command line argument.
 * It defaults to 10 future predictions if this is not supplied by the user.
 * <p>
 * </p>
 * <p>
 * <p>
 * Once dataset is loaded it uses spark-timeseries library for predicting temperature, pressure and humidity.
 * See {@linktourl https://github.com/sryza/spark-timeseries}
 *
 * @see ArimaTimeSeriesModel
 * </p>
 * <p>
 * <p>
 * <p>
 * Weather condition is classified according to predicted temperature, pressure and humidity values.
 * @see RandomForestClassification
 * </p>
 *
 * @author Vishnu
 * @version 1.0.0
 * @since 1.0.0
 */

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main ( String[] args ) {

        boolean status = true;
        logger.info("Starting weather predictor main program execution.");
        int limit = args.length > 1 ? Integer.parseInt(args[0]) : 10;
        logger.info("Setting limit for forecast size as :{}", limit);
        try {
            run(limit);
        } catch (Exception e) {
            status = false;
            logger.error(e.getLocalizedMessage());
        } finally {
            logger.info("Execution completed. Completion status:{} ", status);
        }
    }

    private static void run ( int limit ) throws WeatherPredictionException,IOException {

        logger.info("Parsing application.conf file.");
        //Load the application config file from resources
        final ServiceConfig config = ServiceConfig.getConfig();
        logger.info("Instantiating spark session object");
        //Instantiate the spark session object
        final SparkSession spark = SparkUtils.createSparkContext(config.spark);

        //Load the dataset

        logger.info("Loading input dataset from the path {}", config.input.dataPath);
        Dataset <Row> inputData = SparkUtils.loadDataSet(spark, config.input);
        logger.info("Data loaded successfully from {}", config.input.dataPath);

        //Displays the schema of input dataset
        inputData.printSchema();

        //root
        //       |-- station: string (nullable = true)
        //       |-- date: timestamp (nullable = true)
        //       |-- temperature: double (nullable = true)
        //       |-- humidity: double (nullable = true)
        //       |-- pressure: double (nullable = true)
        //       |-- condition: string (nullable = true)

        // Displays the content of the DataFrame to stdout
        inputData.show();

        // --------+-------------------+-----------+--------+--------+---------+
        // | station|               date|temperature|humidity|pressure|condition|
        // +--------+-------------------+-----------+--------+--------+---------+
        // |Adelaide|2017-08-01 00:00:00|        8.6|   57.56|  1023.9|    SUNNY|
        // |Adelaide|2017-08-02 00:00:00|        7.2|    62.4|  1025.6|    RAINY|
        // |Adelaide|2017-08-03 00:00:00|        8.9|    85.6|  1020.3|    RAINY|
        // --------+-------------------+-----------+--------+--------+---------+

        //Create the model objects
        logger.info("Loading  the model objects for predicting values.");

        TimeSeriesModel tempTimeSeriesModel = ModelLoader.loadModel(new ArimaTimeSeriesModel(), Constants.TEMPERATURE);
        TimeSeriesModel pressureTimeSeriesModel = ModelLoader.loadModel(new ArimaTimeSeriesModel(), Constants.PRESSURE);
        TimeSeriesModel humidityTimeSeriesModel = ModelLoader.loadModel(new ArimaTimeSeriesModel(), Constants.HUMIDITY);
        ClassificationModel conditionClassificationModel = ModelLoader.loadModel(new RandomForestClassification(), Constants.CONDITION);

        logger.info("TimeSeriesModel creation completed.Starting regression ....");

        //Do the auto arima regression for temperature
        Dataset <Row> tempForecast = tempTimeSeriesModel.pointForecast(inputData, spark, limit);

        //Do the auto arima regression for pressure
        Dataset <Row> pressureForecast = pressureTimeSeriesModel.pointForecast(inputData, spark, limit);

        //Do the auto arima regression for humidity
        Dataset <Row> humidityForecast = humidityTimeSeriesModel.pointForecast(inputData, spark, limit);

        //Join the datasets based on date column
        logger.info("Joining the datasets based on date column.");
        Dataset <Row> joinedDS = tempForecast.join(pressureForecast.drop(Constants.STATION), Constants.DATE)
                .join(humidityForecast.drop(Constants.STATION), Constants.DATE);

        //Perform classification for condition
        Dataset <Row> predictedDSWithCondition = conditionClassificationModel.pointForecast(inputData,joinedDS);

        //Enrich the datasets by adding location information and sort by date.
        //This is loaded to WeatherDTO class.

        Geocode geocode = GeoUtils.getLatLongAlt("sydney");

        logger.debug("latitude is "+geocode.getLatitude());
        logger.debug("longitude is "+geocode.getLongitude());
        logger.debug("altitude is "+geocode.getAltitude());


        Dataset <WeatherDTO> finalDS = predictedDSWithCondition.
                withColumn("latitude", lit(geocode.getLatitude()))
                .withColumn("longitude", lit(geocode.getLongitude()))
                .withColumn("altitude", lit(geocode.getAltitude())).orderBy("date")
                .as(Encoders.bean(WeatherDTO.class));

        //Preview of output is displayed.
        finalDS.show();

        //convert to String dataset.
        Dataset <String> finalDSStr = finalDS.map(
                (MapFunction <WeatherDTO, String>) WeatherDTO::toString,
                Encoders.STRING());

        //Save to output folder
        final boolean isSaved = SparkUtils.saveDataSet(finalDSStr, config.output.path);

        if (isSaved) {
            logger.info("Dataset output successfully written to folder {}", config.output.path);
        }

        spark.stop();

    }


}
