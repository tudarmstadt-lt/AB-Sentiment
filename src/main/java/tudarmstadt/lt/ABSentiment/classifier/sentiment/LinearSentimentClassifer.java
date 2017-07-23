package tudarmstadt.lt.ABSentiment.classifier.sentiment;

import tudarmstadt.lt.ABSentiment.classifier.LinearClassifier;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;
import tudarmstadt.lt.ABSentiment.training.LinearTraining;

/**
 * The LinearSentimentClassifer analyzes the sentiment of a document.
 */
public class LinearSentimentClassifer extends LinearClassifier {

    public LinearSentimentClassifer(String configurationFile) {
        initialise(configurationFile);
        LinearTesting linearTesting = new LinearTesting();
        model = linearTesting.loadModel(sentimentModel);
        features = loadFeatureExtractors();

        labelMappings = loadLabelMapping(labelMappingsFileSentiment);
    }
}
