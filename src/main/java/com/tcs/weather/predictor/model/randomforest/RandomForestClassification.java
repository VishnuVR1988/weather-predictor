package com.tcs.weather.predictor.model.randomforest;

import com.tcs.weather.predictor.model.ClassificationModel;
import com.tcs.weather.predictor.constants.Constants;
import com.tcs.weather.predictor.constants.MLConstants;

import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.feature.IndexToString;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the implementation of RandomForest based classification algorithm
 *
 * @author Vishnu
 * @version 1.0.0
 * @since 1.0.0
 */

public class RandomForestClassification implements ClassificationModel {


    private static final Logger logger = LoggerFactory.getLogger(RandomForestClassification.class);


    private String impurity;

    private int maxDepth;

    private int numTrees;

    private String featureSubsetStrategy;

    private int seed;

    private String variable;

    private String station;

    public String getStation () {
        return station;
    }

    public void setStation ( String station ) {
        this.station = station;
    }

    public String getVariable () {
        return variable;
    }

    public void setVariable ( String variable ) {
        this.variable = variable;
    }

    public String getImpurity () {
        return impurity;
    }

    public void setImpurity ( String impurity ) {
        this.impurity = impurity;
    }

    public int getMaxDepth () {
        return maxDepth;
    }

    public void setMaxDepth ( int maxDepth ) {
        this.maxDepth = maxDepth;
    }

    public int getNumTrees () {
        return numTrees;
    }

    public void setNumTrees ( int numTrees ) {
        this.numTrees = numTrees;
    }

    public String getFeatureSubsetStrategy () {
        return featureSubsetStrategy;
    }

    public void setFeatureSubsetStrategy ( String featureSubsetStrategy ) {
        this.featureSubsetStrategy = featureSubsetStrategy;
    }

    public int getSeed () {
        return seed;
    }

    public void setSeed ( int seed ) {
        this.seed = seed;
    }


    /**
     * @param inputDataSet
     * @param predictionDataSet
     * @return
     */

    @Override
    public Dataset <Row> applyClassification ( Dataset <Row> inputDataSet, Dataset <Row> predictionDataSet ) {
        //First, we create a feature column of all the predictor value
        //The predictors are assumed as temperature, pressure and humidity.
        logger.info("Creating the feature column of all the predictor values.");

        //VectorAssembler constructs Vector from raw feature columns
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[]{Constants.TEMPERATURE, Constants.PRESSURE, Constants.HUMIDITY})
                .setOutputCol(MLConstants.FEATURES);

        logger.info("Features are merged using VectorAssembler.");

        Dataset <Row> transformedDS = assembler.transform(inputDataSet);

        logger.info("Feature column is fitted to model successfully.");

        //StringIndexer converts String values that are part of a look-up into categorical indices, which could be used by
        // machine learning algorithms in ml library
        StringIndexerModel stringIndexerModel = new StringIndexer().
                setInputCol(variable).
                setOutputCol(Constants.LABEL).fit(transformedDS);

        Dataset <Row> indexedDf = stringIndexerModel.transform(transformedDS);
        logger.info("String indexer is applied on the training dataset.");

        //Create the random forest classifier object
        RandomForestClassifier randomForestClassifier = new RandomForestClassifier()
                .setImpurity(impurity)
                .setMaxDepth(maxDepth)
                .setNumTrees(numTrees)
                .setFeatureSubsetStrategy(featureSubsetStrategy)
                .setSeed(seed);
        RandomForestClassificationModel randomForestClassificationModel = randomForestClassifier.fit(indexedDf);

        logger.info("Random Forest model is created successfully.");

        //apply the model on predicted dataset to derive condition.
        Dataset <Row> classificationDS = randomForestClassificationModel.
                transform(stringIndexerModel.transform(assembler.transform(predictionDataSet)));

        logger.info("Classification is applied successfully.");

        // Convert indexed labels back to original labels.
        IndexToString converter = new IndexToString()
                .setInputCol(Constants.PREDICTION)
                .setOutputCol(Constants.PREDICTED + variable).setLabels(stringIndexerModel.labels());
        Dataset <Row> converted = converter.transform(classificationDS);

        return converted.select(Constants.STATION, Constants.DATE, Constants.TEMPERATURE, Constants.PRESSURE,
                Constants.HUMIDITY, Constants.PREDICTED + variable).
                withColumnRenamed(Constants.PREDICTED + variable, Constants.CONDITION);

    }

}

