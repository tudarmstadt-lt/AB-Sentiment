package tudarmstadt.lt.ABSentiment.classifier.aspectclass;

import tudarmstadt.lt.ABSentiment.classifier.LinearClassifier;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

/**
 * The LinearAspectClassifier classifies the aspects found in a document.
 */
public class LinearAspectClassifier extends LinearClassifier {

    public LinearAspectClassifier(String configurationFile) {
        this(configurationFile, "data/models/aspect_label_mappings.tsv");
    }

    public LinearAspectClassifier(String configurationFile, String labelMappingsFile) {
        initialise(configurationFile);
        LinearTesting linearTesting = new LinearTesting();
        model = linearTesting.loadModel(modelFile);
        features = loadFeatureExtractors();

        labelMappings = loadLabelMapping(labelMappingsFile);
    }

}
