package tudarmstadt.lt.ABSentiment.classifier.relevance;

import tudarmstadt.lt.ABSentiment.classifier.LinearClassifier;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

/**
 * The LinearRelevanceClassifier classifies the relevance of a document.
 */
public class LinearRelevanceClassifier extends LinearClassifier  {

    public LinearRelevanceClassifier(String configurationFile) {
        this(configurationFile, "data/models/relevance_label_mappings.tsv");
    }

    public LinearRelevanceClassifier(String configurationFile, String labelMappingsFile) {
        initialise(configurationFile);
        LinearTesting linearTesting = new LinearTesting();
        model = linearTesting.loadModel(modelFile);
        features = loadFeatureExtractors();

        labelMappings = loadLabelMapping(labelMappingsFile);
    }
}
