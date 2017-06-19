package tudarmstadt.lt.ABSentiment.classifier.aspectclass;

import tudarmstadt.lt.ABSentiment.classifier.LinearClassifier;

/**
 * The LinearAspectClassifier classifies the aspects found in a document.
 */
public class LinearAspectClassifier extends LinearClassifier {

    /**
     * Constructor for the aspect classifier; expects the path to the model file.
     * @param modelFile path to the SVM model file
     */
    public LinearAspectClassifier(String modelFile) {
        this(modelFile, "data/models/aspect_label_mappings.tsv");
    }

    /**
     * Constructor for the aspect classifier; expects the path to the model file, as well as the label mapping file.
     * @param modelFile path to the SVM model file
     * @param labelMappingsFile path to the file containing label ids and their String representation
     */
    public LinearAspectClassifier(String modelFile, String labelMappingsFile) {
        model = loadModel(modelFile);
        features = loadFeatureExtractors();

        //idfGazeteerFile = "data/features/aspect_idfterms.tsv";
        labelMappings = loadLabelMapping(labelMappingsFile);
    }

}
