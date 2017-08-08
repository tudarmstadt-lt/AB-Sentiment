package tudarmstadt.lt.ABSentiment.classifier.relevance;

import tudarmstadt.lt.ABSentiment.classifier.LinearClassifier;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

/**
 * The LinearRelevanceClassifier classifies the relevance of a document.
 */
public class LinearRelevanceClassifier extends LinearClassifier  {

    public LinearRelevanceClassifier(String configurationFile) {
        initialise(configurationFile);
        LinearTesting linearTesting = new LinearTesting();
        model = linearTesting.loadModel(relevanceModel);
        features = loadFeatureExtractors();
        labelMappings = loadLabelMapping(labelMappingsFileRelevance);
    }
}
