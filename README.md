#Weather predictor - A tool to forecast the weather using machine learning and distributed computing

##Introduction

Weather forecast systems are among the most complex equations that computer has to solve. Bigdata has the potential to
transform the way we can understand and predict weather systems using distributed computing capabilities. This project aims to solve
this using machine learning algorithms and big data frameworks like spark.

##Problem Statement

Create a toy model of the environment (taking into account things like atmosphere, topography, geography, oceanography, or similar) that evolves over time. Then take measurements at various locations (ie weather stations), and then have the program emit that data, as in the following:

SYD|-33.86,151.21,39|2015-12-23T05:02:12Z|Rain|+12.5|1004.3|97

##Pre-requisites

Apache maven

Java 1.8 or above

Spark 2.2

sparkts(spark cloudera time series library)


##Forecasting 

The model is built upon historic data to forecast temperature , humidity , pressure and weather condition.As the 
weather observations are over a period of time, time-series 
analysis using ARIMA is used to forecast weather parameters like temperature ,pressure and humidity. These have been modelled as Univariate variables
.


ARIMA stands for auto-regressive integrated moving average and is specified by these three order parameters: (p, d, q). The process of fitting an ARIMA model is sometimes referred to as the Box-Jenkins method.

An auto regressive (AR(p)) component is referring to the use of past values in the regression equation for the series Y. The auto-regressive parameter p specifies the number of lags used in the model. For example, AR(2) or, equivalently, ARIMA(2,0,0), is represented as

Y_t = c + φ1y_{t-1} + φ12 y_{t-2}+ e_t

where φ1, φ2 are parameters for the model.

Once the temperature, pressure ,humidity are forecasted, 
the overall weather condition(SNOW/RAIN/SUNNY) is evaluated using a random forest classification 
model.

<TODO: Random forest>

ARIMA methodology does have its limitations. These models directly rely on past values, and therefore work best on long and stable series.
Also it is best suited for short term predictions.


##Project

The application is driven by a config file in the resources folder 
application.conf. It accepts the following parameters.


spark.master -


spark.appName -


input.dataPath


output.path-



##Build

This project is built using Apache Maven. To build this run:

build/mvn -DskipTests clean package

##Execution




##Files and Folders