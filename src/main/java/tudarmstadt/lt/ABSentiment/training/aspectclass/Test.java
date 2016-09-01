package tudarmstadt.lt.ABSentiment.training.aspectclass;

import de.bwaldvogel.liblinear.Model;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

import java.util.Vector;

/**
 * Aspect Model Tester (fine-grained)
 */
public class Test extends LinearTesting {

    /**
     * Classifies an input file, given a model
     * @param args optional: input file, model file and the output file
     */
    public static void main(String[] args) {

        loadLabelMappings("aspect-label-mappings.tsv");

        String modelFile = "aspect-model.svm";
        String inputFile = "/aspect-test.tsv";
        String predictionFile = "aspect-test_predictions.tsv";

        if (args.length == 3) {
            inputFile = args[0];
            modelFile = args[1];
            predictionFile = args[2];
        }

        Vector<FeatureExtractor> features = loadFeatureExtractors();

        Model model = loadModel(modelFile);

        classifyTestSet(inputFile, model, features, predictionFile);
    }

}

