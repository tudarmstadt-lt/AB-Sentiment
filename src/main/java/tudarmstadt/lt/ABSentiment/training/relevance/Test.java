package tudarmstadt.lt.ABSentiment.training.relevance;

import de.bwaldvogel.liblinear.Model;
import org.apache.uima.UIMAException;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

import java.io.IOException;
import java.util.Vector;

/**
 * Relevance Model Tester
 */
public class Test extends LinearTesting {

    public static void main(String[] args) throws UIMAException, InterruptedException, IOException {

        loadLabelMappings("relevance-label-mappings.tsv");

        String modelFile = "relevance-model.svm";
        String inputFile = "/relevance-test.tsv";
        String predictionFile = "relevance-test_predictions.tsv";

        Vector<FeatureExtractor> features = loadFeatureExtractors();

        Model model = loadModel(modelFile);

        classifyTestSet(inputFile, model, features, predictionFile);
    }

}

