package tudarmstadt.lt.ABSentiment.classifier.aspectclass;

import tudarmstadt.lt.ABSentiment.classifier.LinearClassifier;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

/**
 * The LinearAspectClassifier classifies the aspects found in a document.
 */
public class LinearAspectClassifier extends LinearClassifier {

    public LinearAspectClassifier(String configurationFile) {
        initialise(configurationFile);
        LinearTesting linearTesting = new LinearTesting();
        model = linearTesting.loadModel(aspectModel);
        features = loadFeatureExtractors();

        //idfGazeteerFile = "data/features/aspect_idfterms.tsv";
        labelMappings = loadLabelMapping(labelMappingsFileAspect);
    }

}
