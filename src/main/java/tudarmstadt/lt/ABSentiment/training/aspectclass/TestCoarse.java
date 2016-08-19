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

    public static void main(String[] args) throws UIMAException, InterruptedException, IOException {

        loadLabelMappings("aspect-coarse-label-mappings.tsv");

        String inputFile = "/aspect-test.tsv";
        String modelFile = "aspect-coarse-model.svm";
        String predictionFile = "aspect-coarse-test_predictions.tsv";

        if (args.length == 3) {
            inputFile = args[0];
            modelFile = args[1];
            predictionFile = args[2];
        }

        Vector<FeatureExtractor> features = loadFeatureExtractors();

        Model model = loadModel(modelFile);

        useCoarseLabels = true;
        classifyTestSet(inputFile, model, features, predictionFile);
    }

}

