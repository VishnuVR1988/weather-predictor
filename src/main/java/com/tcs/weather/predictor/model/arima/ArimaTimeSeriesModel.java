package com.tcs.weather.predictor.model.arima;

import com.cloudera.sparkts.DayFrequency;
import com.cloudera.sparkts.models.ARIMA;
import com.cloudera.sparkts.DateTimeIndex;
import com.cloudera.sparkts.api.java.DateTimeIndexFactory;
import com.cloudera.sparkts.api.java.JavaTimeSeriesRDD;
import com.cloudera.sparkts.api.java.JavaTimeSeriesRDDFactory;
import com.cloudera.sparkts.UniformDateTimeIndex;

import com.tcs.weather.predictor.constants.MLConstants;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.sql.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcs.weather.predictor.model.TimeSeriesModel;
import com.tcs.weather.predictor.exception.WeatherPredictionException;
import com.tcs.weather.predictor.constants.Constants;


/**
 * This class is the implementation of arima based forecast algorithm
 * @author Vishnu
 * @since 1.0.0
 * @version 1.0.0
 */

public class ArimaTimeSeriesModel implements TimeSeriesModel,Serializable {


    private static final Logger logger = LoggerFactory.getLogger(ArimaTimeSeriesModel.class);


    /**
     * p ARIMA parameter, the order (number of time lags) of the autoregressive model
     */
    private int p;

    /**
     * d ARIMA parameter, the degree of differencing
     */

    private int d;

    /**
     * q ARIMA parameter, the order of the moving-average model
     */
    private int q;


    /**
     * forecasting variable
     */
    private String variable;


    public static final double ALPHA = 1 - Math.exp(-0.30103 / 4.0);


    public String getVariable () {
        return variable;
    }


    public void setVariable ( String variable ) {
        this.variable = variable;
    }


    public void setP ( int p ) {
        this.p = p;
    }

    public void setD ( int d ) {
        this.d = d;
    }

    public void setQ ( int q ) {
        this.q = q;
    }


    /**
     * Retrieve the last n elements of a vector
     *
     * @param v Input vector
     * @param n elements to be retrieved
     * @return
     */
    private static Vector getLast ( Vector v, int n ) {
        double[] data = new double[n];

        for (int i = 0; i < n; ++i)
            data[i] = v.apply(v.size() - n + i - 1);

        return Vectors.dense(data);
    }


    /**
     * The method returns the exponentially weighted moving average
     *
     * @param v Input vector
     * @return
     */
    private static Vector ewma ( Vector v ) {
        double[] data = new double[v.size()];

        data[0] = v.apply(0);

        for (int i = 1; i < v.size(); ++i)
            data[i] = (1 - ALPHA) * data[i - 1] + ALPHA * v.apply(i);

        return Vectors.dense(data);
    }


    /**
     * Calculates the difference between the two vectors
     *
     * @param v1 First vector
     * @param v2 second vector
     * @return
     */
    private static Vector diff ( Vector v1, Vector v2 ) {
        double[] data = new double[v1.size()];

        for (int i = 0; i < v1.size(); ++i)
            data[i] = v1.apply(i) - v2.apply(i);

        return Vectors.dense(data);
    }


    /**
     * Returns the value of previous row based on time
     *
     * @param r
     * @param diff
     * @return
     */
    public static Row getRowWithPrevTime ( Row r, long diff ) {
        Timestamp t = r.getAs(0);
        t.setTime(t.getTime() + diff);
        return RowFactory.create(t, r.getAs(1), r.getAs(2));
    }

    /**
     * This method forecast the variables using ARIMA
     *
     * @param inputDataSet : Timeseries input data
     * @param spark        : Spark session object
     * @param steps        : Number of immediate predictions
     * @return forecasted Dataset
     * @throws WeatherPredictionException
     */

    @Override
    public Dataset <Row> pointForecast ( Dataset <Row> inputDataSet, SparkSession spark, int steps ) throws WeatherPredictionException {
        if (inputDataSet.count() == 0) return null;

        //Create DateTimeIndices
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime start = ZonedDateTime.of(inputDataSet.selectExpr("min(date)").
                as(Encoders.TIMESTAMP()).collectAsList().get(0).toLocalDateTime(), zoneId);
        ZonedDateTime end = ZonedDateTime.of(inputDataSet.selectExpr("max(date)")
                .as(Encoders.TIMESTAMP()).collectAsList().get(0).toLocalDateTime(), zoneId);
        UniformDateTimeIndex dtIndexInput = DateTimeIndexFactory.uniformFromInterval(start, end, new DayFrequency(1));
        ZonedDateTime startDateForecast = end.plusDays(1);
        ZonedDateTime latestDateForecast = end.plusDays(steps);
        DateTimeIndex dtIndexForecast = DateTimeIndexFactory.uniformFromInterval(
                startDateForecast, latestDateForecast, new DayFrequency(1));

        //Align data on the DateTimeIndex to create a TimeSeriesRDD
        JavaTimeSeriesRDD tsRDD = JavaTimeSeriesRDDFactory.timeSeriesRDDFromObservations(
                dtIndexInput, inputDataSet, Constants.DATE, Constants.STATION, variable);

        // Cache it in memory
        tsRDD.cache();

        // Impute missing values using linear interpolation
        JavaTimeSeriesRDD <String> filledTsRDD = tsRDD.fill(MLConstants.LINEAR).fill(MLConstants.NEXT);

        //ARIMA forecast
        JavaTimeSeriesRDD <String> tsRDDForecast;

        tsRDDForecast = filledTsRDD.mapSeries(
                ( Vector ts ) -> {
                    Vector tsEwma = ewma(ts);
                    Vector tsEwmaDiff = diff(ts, tsEwma);
                    Vector tsDiff = getLast(ARIMA.fitModel(p, d, q,
                            tsEwmaDiff, true, MLConstants.CSS_CGD, null)
                                    .forecast(tsEwmaDiff, steps),
                            steps);
                    double[] data = new double[steps];
                    data[0] = tsEwma.apply(tsEwma.size() - 1)
                            + tsDiff.apply(0) / (1 - ALPHA);
                    for (int i = 1; i < steps; ++i)
                        data[i] = data[i - 1] + tsDiff.apply(i) / (1 - ALPHA);

                    return Vectors.dense(data);
                }, dtIndexForecast
        );

        return tsRDDForecast.toObservationsDataFrame(spark.sqlContext(),
                Constants.DATE, Constants.STATION, variable);
    }
}
