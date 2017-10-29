# Weather predictor - A tool to forecast the weather using machine learning and distributed computing

## Introduction

Weather forecast systems are among the most complex equations that computer has to solve. Bigdata has the potential to
transform the way we can understand and predict w   eather systems using distributed computing capabilities. This project aims to solve
this using machine learning algorithms and big data frameworks like spark.

## Problem Statement

Create a toy model of the environment (taking into account things like atmosphere, topography, geography, oceanography, or similar) that evolves over time. Then take measurements at various locations (ie weather stations), and then have the program emit that data, as in the following:

SYDNEY|-33.86,151.21,39|2017-09-23T00:02:12Z|RAINY|+12.5|1004.3|97

## Pre-requisites

Apache maven

Java 1.8 or above

Spark 2.2

sparkts(spark cloudera time series library)


## Forecasting 

The model is built upon historic data to forecast temperature , humidity , pressure and weather condition.As the 
weather observations are over a period of time, time-series 
analysis using ARIMA is used to forecast weather parameters like temperature ,pressure and humidity. These have been modelled as univariate variables.
.


ARIMA stands for auto-regressive integrated moving average and is specified by these three order parameters: (p, d, q). The process of fitting an ARIMA model is sometimes referred to as the Box-Jenkins method.

An auto regressive (AR(p)) component is referring to the use of past values in the regression equation for the series Y. The auto-regressive parameter p specifies the number of lags used in the model. For example, AR(2) or, equivalently, ARIMA(2,0,0), is represented as

Y_t = c + φ1y_{t-1} + φ12 y_{t-2}+ e_t

where φ1, φ2 are parameters for the model.

Once the temperature, pressure ,humidity are forecasted, 
the overall weather condition(SNOW/RAIN/SUNNY) is evaluated using a random forest classification 
model.

ARIMA methodology does have its limitations. These models directly rely on past values, and therefore work best on long and stable series.
Also it is best suited for short term predictions.


## Project

The application is driven by a config file in the resources folder 
application.conf. It accepts the following parameters.


spark.master - Spark master URL.

spark.appName - Spark application name.

input.dataPath- Path to load the input.

output.path- Path to save the output.



The historical weather details are downloaded from Bureau of Meteorology, Australia website.
 
The input data for two stations, Sydney and Melbourne (2 months data) are available under src/main/resources
More number for stations can be added by providing historical values in the below format 
under input directory.

Input Format:
```
<station>,<date>,<temperature>,<pressure>,<humidity>,<condition>
```
Eg:
```
station,date,temperature,pressure,humidity,condition
sydney,2017-09-01,13.7,1024.2,48,SUNNY
```
FileName: Should be same as station name

## Build

This project is built using Apache Maven. To build this run:

    mvn -DskipTests clean package

## Execution

Once build is completed, jar is generated under target folder.

This can be run using the script under bin folder.

    ./bin/run_weather-predictor.sh
    Usage: bin/run_weather-predictor.sh <numDays(OPTIONAL)>

The application.conf and log4.properties are available under conf folder.
This can be overrided accordingly.Relative path is provided in
default config. Please change path if running from a different
directory other than weather-predictor.


## Files and Folders

 ###Input files

  - [sydney.csv](https://github.com/VishnuVR1988/weather-predictor/tree/master/src/main/java/resources/input/sydney.csv)
 
  - [melbourne.csv](https://github.com/VishnuVR1988/weather-predictor/tree/master/src/main/java/resources/input/melbourne.csv)

 ###Other files

  - [application.conf](https://github.com/VishnuVR1988/weather-predictor/tree/master/src/conf/application.conf)

  - [run_weather-predictor.sh](https://github.com/VishnuVR1988/weather-predictor/tree/master/bin/run_weather-predictor.sh)





