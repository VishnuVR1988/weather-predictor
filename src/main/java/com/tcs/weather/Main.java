package com.tcs.weather;

import com.tcs.weather.predictor.ModelExecutor;
import com.tcs.weather.predictor.model.ClassificationModel;
import com.tcs.weather.predictor.exception.WeatherPredictionException;
import com.tcs.weather.predictor.model.arima.ArimaTimeSeriesModel;
import com.tcs.weather.predictor.model.randomforest.RandomForestClassification;
import org.apache.commons.io.FilenameUtils;
import org.apache.spark.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcs.weather.predictor.model.TimeSeriesModel;
import com.tcs.weather.predictor.ModelLoader;
import com.tcs.weather.predictor.constants.Constants;
import com.tcs.weather.predictor.support.ServiceConfig;
import com.tcs.weather.predictor.support.SparkUtils;

import java.util.List;


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
 * same file under the conf folder and run using run_weather_predictor.sh.
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
 * @see ArimaTimeSeriesModel
 * </p>
 * <p>
 * <p>
 * <p>
 * Weather condition is classified according to predicted temperature, pressure and humidity values.
 * @see RandomForestClassification
 * </p>
 * @author Vishnu
 * @version 1.0.0
 * @since 1.0.0
 */

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main ( String[] args ) {

        boolean status = true;
        logger.info("Starting weather predictor main program execution.");
        int limit = args.length > 1 ? Integer.parseInt(args[0]) : Constants.DEFAULT_LIMIT;
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


    /**
     * Executes the prediction for given limit
     * @param limit
     * @throws WeatherPredictionException
     */

    private static void run ( int limit ) throws WeatherPredictionException {

        logger.info("Parsing application.conf file.");
        //Load the application config file from resources
        final ServiceConfig config = ServiceConfig.getConfig();
        logger.info("Instantiating spark session object.");
        //Instantiate the spark session object
        SparkSession sparkSession = SparkUtils.createSparkContext(config.spark);
        //Get the input files. File names correspond to cities.
        logger.info("Loading input file paths.");
        List <String> inputFiles = SparkUtils.getInputFiles(sparkSession, config.input.dataPath);
        //Create an initial empty dataset
        logger.info("Creating empty dataset.");
        Dataset <Row> initialDataSet = SparkUtils.createEmptyDataSet(sparkSession);

        //Intializing model classes
        TimeSeriesModel tempTimeSeriesModel = ModelLoader.loadModel(new ArimaTimeSeriesModel(), Constants.TEMPERATURE);
        TimeSeriesModel pressureTimeSeriesModel = ModelLoader.loadModel(new ArimaTimeSeriesModel(), Constants.PRESSURE);
        TimeSeriesModel humidityTimeSeriesModel = ModelLoader.loadModel(new ArimaTimeSeriesModel(), Constants.HUMIDITY);
        ClassificationModel conditionClassificationModel = ModelLoader.loadModel(new RandomForestClassification(), Constants.CONDITION);


        //Perform the prediction for each cities
        for (String file : inputFiles) {
            logger.info("Loading input dataset from the file  {}", file);
            Dataset <Row> inputData = SparkUtils.loadDataSet(sparkSession, file);
            logger.info("Data loaded successfully from {}", file);
            String station = FilenameUtils.getBaseName(file);
            Dataset <Row> finalDS = ModelExecutor.getPredictedDataset(limit, sparkSession, tempTimeSeriesModel,
                    pressureTimeSeriesModel, humidityTimeSeriesModel, conditionClassificationModel, inputData, station);
            //Union to the initial dataset
            if (finalDS != null) initialDataSet = initialDataSet.union(finalDS);

        }

        //convert to String dataset.
        Dataset <String> finalDSStr = SparkUtils.convertToStringDataset(initialDataSet);
        finalDSStr.show(false);

        //Save to output folder
        SparkUtils.saveDataSet(finalDSStr, config.output.path);

        sparkSession.stop();

    }


}
