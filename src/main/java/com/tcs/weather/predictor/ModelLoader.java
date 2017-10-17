package com.tcs.weather.predictor;

import com.tcs.weather.predictor.model.ClassificationModel;
import com.tcs.weather.predictor.model.TimeSeriesModel;
import com.tcs.weather.predictor.model.arima.ArimaTimeSeriesModel;
import com.tcs.weather.predictor.model.randomforest.RandomForestTimeSeriesModel;
import com.tcs.weather.predictor.constants.Constants;
import com.tcs.weather.predictor.constants.MLConstants;

/**
 * @author Vishnu
 *
 */
public class ModelLoader {

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
        }
        return arimaModel;
    }

    public static ClassificationModel loadModel ( RandomForestTimeSeriesModel randomForestModel, String variable) {
        switch (variable) {
            case (Constants.CONDITION):
                applyClassificationParams(randomForestModel, variable);
                break;
        }
        return randomForestModel;
    }

    private static void applyClassificationParams ( RandomForestTimeSeriesModel randomForestModel, String variable ) {
        randomForestModel.setImpurity(MLConstants.IMPURITY);
        randomForestModel.setFeatureSubsetStrategy(MLConstants.FEATURE_SUBSET_STRATEGY);
        randomForestModel.setMaxDepth(MLConstants.MAX_DEPTH);
        randomForestModel.setNumTrees(MLConstants.NUM_TREES);
        randomForestModel.setVariable(variable);
        return;
    }


}
