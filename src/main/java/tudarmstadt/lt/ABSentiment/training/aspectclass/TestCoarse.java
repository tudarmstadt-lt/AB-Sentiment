package tudarmstadt.lt.ABSentiment.training.aspectclass;

import de.bwaldvogel.liblinear.Model;
import org.apache.uima.UIMAException;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

import java.io.IOException;
import java.util.Vector;

/**
 * Aspect Model Tester (course-grained)
 */
public class TestCoarse extends LinearTesting {

    /**
     * Classifies an input file, given a model
     * @param args optional: input file, model file and the output file
     */
    public static void main(String[] args) throws UIMAException, InterruptedException, IOException {

        loadLabelMappings("data/models/aspect_coarse_label_mappings.tsv");

        testFile = "data/aspect_test.tsv";
        modelFile = "data/models/aspect_coarse_model.svm";
        predictionFile = "aspect-coarse_test_predictions.tsv";

        if (args.length == 3) {
            testFile = args[0];
            modelFile = args[1];
            predictionFile = args[2];
        }

        Vector<FeatureExtractor> features = loadFeatureExtractors();

        Model model = loadModel(modelFile);

        useCoarseLabels = true;
        classifyTestSet(testFile, model, features, predictionFile);
    }

}

