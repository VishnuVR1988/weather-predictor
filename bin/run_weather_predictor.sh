#!/bin/bash
################################################################################
# Script to launch weather predictor jar and generate the prediction values.

# application.conf can be placed inside conf folder to override default values. Relative path is provided in
# default config. Please change path if running from a different directory other than weather-predictor
#log4.properties override the default logging

################################################################################
#MAIN SCRIPT

get_abs_script_path() {
  pushd . >/dev/null
  cd "$(dirname "$0")"
  SCRIPT_DIR=$(pwd)
  popd  >/dev/null
}


display_usage(){
echo "Usage: $0 <numDays(OPTIONAL)>"
echo " "
exit 1
}

if [ "$1" == "-h" ] || [ "$1" == "-help" ] ; then
  display_usage
  exit 0
fi


get_abs_script_path

echo "SCRIPT_DIR is "${SCRIPT_DIR}

MS_UBER_BIN_JAR=${SCRIPT_DIR}/../target/uber.jar

MAIN="com.tcs.weather.Main"

MS_PARAMS=" "

CONFIG_FILE="$SCRIPT_DIR/../conf/application.conf"

LOG4J_FILE="$SCRIPT_DIR/../conf/log4j.properties"

PARAMS=$1

if [ -f "$CONFIG_FILE" ];
then
    MS_PARAMS="$MS_PARAMS -Dconfig.file=$CONFIG_FILE"
fi


if [ -f "$LOG4J_FILE" ];
then
    MS_PARAMS="$MS_PARAMS -Dlog4j.configuration=file://$LOG4J_FILE"
fi


cmd='spark-submit --class $MAIN
  --conf "spark.executor.extraJavaOptions=LOG4J_FILE"
  --driver-java-options "$MS_PARAMS" $MS_UBER_BIN_JAR $PARAMS'

eval ${cmd} ${PARAMS}

exit 0


