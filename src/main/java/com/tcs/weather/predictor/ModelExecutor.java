package com.tcs.weather.predictor;

import com.tcs.weather.predictor.constants.Constants;
import com.tcs.weather.predictor.dto.Geocode;
import com.tcs.weather.predictor.model.ClassificationModel;
import com.tcs.weather.predictor.model.TimeSeriesModel;
import com.tcs.weather.predictor.support.GeoUtils;
import com.tcs.weather.predictor.support.SparkUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vishnu
 */

public class ModelExecutor {


    private static final Logger logger = LoggerFactory.getLogger(ModelExecutor.class);

    public static Dataset <Row> getRowDataset ( int limit, SparkSession sparkSession, TimeSeriesModel tempTimeSeriesModel,
                                                TimeSeriesModel pressureTimeSeriesModel, TimeSeriesModel humidityTimeSeriesModel,
                                                ClassificationModel conditionClassificationModel, Dataset <Row> inputData,
                                                String station ) {
        try {
            //Do the auto arima regression for temperature
            Dataset <Row> tempForecast = tempTimeSeriesModel.pointForecast(inputData, sparkSession, limit);

            //Do the auto arima regression for pressure
            Dataset <Row> pressureForecast = pressureTimeSeriesModel.pointForecast(inputData, sparkSession, limit);

            //Do the auto arima regression for humidity
            Dataset <Row> humidityForecast = humidityTimeSeriesModel.pointForecast(inputData, sparkSession, limit);

            //Join the datasets based on date column
            logger.info("Joining the datasets based on date column on station {}",station);
            Dataset <Row> joinedDS = tempForecast.join(pressureForecast.drop(Constants.STATION), Constants.DATE)
                    .join(humidityForecast.drop(Constants.STATION), Constants.DATE);

            //Perform classification for condition
            logger.info("TimeSeries prediction completed for {].Starting regression ....",station);
            Dataset <Row> predictedDSWithCondition = conditionClassificationModel.applyClassification(inputData, joinedDS);

            //Enrich the datasets by adding location information and sort by date.
            //This is loaded to WeatherDTO class.
            Geocode geocode = GeoUtils.getLatLongAlt(station);

            return SparkUtils.enrichWithGeoPoints(predictedDSWithCondition, geocode);

        } catch (Exception e) {
            logger.error("Weather prediction failed for {}.\n {}",station,e.getMessage(),e);
        }
        return null;
    }
}
