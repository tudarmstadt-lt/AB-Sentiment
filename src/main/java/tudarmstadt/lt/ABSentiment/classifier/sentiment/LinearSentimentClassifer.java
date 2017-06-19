package tudarmstadt.lt.ABSentiment.classifier.sentiment;

import tudarmstadt.lt.ABSentiment.classifier.LinearClassifier;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;
import tudarmstadt.lt.ABSentiment.training.LinearTraining;

/**
 * The LinearSentimentClassifer analyzes the sentiment of a document.
 */
public class LinearSentimentClassifer extends LinearClassifier {

    public LinearSentimentClassifer(String configurationFile) {
        this(configurationFile, "data/models/sentiment_label_mappings.tsv");
    }

    public LinearSentimentClassifer(String configurationFile, String labelMappingsFile) {
        initialise(configurationFile);
        LinearTesting linearTesting = new LinearTesting();
        model = linearTesting.loadModel(modelFile);
        features = loadFeatureExtractors();

        labelMappings = loadLabelMapping(labelMappingsFile);
    }
}
