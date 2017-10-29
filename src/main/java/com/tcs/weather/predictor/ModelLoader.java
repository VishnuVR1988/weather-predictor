package com.tcs.weather.predictor;

import com.tcs.weather.predictor.constants.Constants;
import com.tcs.weather.predictor.model.ClassificationModel;
import com.tcs.weather.predictor.model.TimeSeriesModel;
import com.tcs.weather.predictor.model.arima.ArimaTimeSeriesModel;
import com.tcs.weather.predictor.model.randomforest.RandomForestClassification;
import com.tcs.weather.predictor.constants.MLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vishnu
 * @version 1.0.0
 * @since 1.0.0
 */

public class ModelLoader {

    private static final Logger logger = LoggerFactory.getLogger(ModelLoader.class);


    /**
     *
     * @param arimaModel
     * @param variable
     * @return the timeseries model object
     */
    public static TimeSeriesModel loadModel ( ArimaTimeSeriesModel arimaModel, String variable) {

        switch (variable) {
            case (Constants.HUMIDITY): {
                arimaModel.setP(MLConstants.P_HUMIDITY);
                arimaModel.setQ(MLConstants.Q_HUMIDITY);
                arimaModel.setD(MLConstants.D_HUMIDITY);
                arimaModel.setVariable(variable);
                break;
            }

            case (Constants.PRESSURE): {
                arimaModel.setP(MLConstants.P_PRESSURE);
                arimaModel.setQ(MLConstants.Q_PRESSURE);
                arimaModel.setD(MLConstants.D_PRESSURE);
                arimaModel.setVariable(variable);
                break;
            }

            case (Constants.TEMPERATURE): {
                arimaModel.setP(MLConstants.P_TEMPERATURE);
                arimaModel.setQ(MLConstants.Q_TEMPERATURE);
                arimaModel.setD(MLConstants.D_TEMPERATURE);
                arimaModel.setVariable(variable);
                break;
            }
            default:
                logger.error("Invalid parameter");
                break;

        }
        return arimaModel;
    }

    /**
     *
     * @param randomForestModel
     * @param variable
     * @return the classification model object
     */
    public static ClassificationModel loadModel ( RandomForestClassification randomForestModel, String variable) {
        switch (variable) {
            case (Constants.CONDITION):
                applyClassificationParams(randomForestModel, variable);
                break;
            default:
                logger.error("Invalid parameter");
                break;

        }
        return randomForestModel;
    }

    /**
     *
     * @param randomForestModel
     * @param variable
     */
    private static void applyClassificationParams ( RandomForestClassification randomForestModel, String variable) {
        randomForestModel.setImpurity(MLConstants.IMPURITY);
        randomForestModel.setFeatureSubsetStrategy(MLConstants.FEATURE_SUBSET_STRATEGY);
        randomForestModel.setMaxDepth(MLConstants.MAX_DEPTH);
        randomForestModel.setNumTrees(MLConstants.NUM_TREES);
        randomForestModel.setSeed(MLConstants.SEED);
        randomForestModel.setVariable(variable);
    }


}
