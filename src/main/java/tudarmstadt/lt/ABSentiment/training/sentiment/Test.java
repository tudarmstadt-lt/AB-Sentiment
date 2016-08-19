package tudarmstadt.lt.ABSentiment.training.sentiment;

import de.bwaldvogel.liblinear.Model;
import org.apache.uima.UIMAException;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

import java.io.IOException;
import java.util.Vector;

/**
 * Sentiment Model Tester
 */
public class Test extends LinearTesting {

    public static void main(String[] args) throws UIMAException, InterruptedException, IOException {

        loadLabelMappings("sentiment-label-mappings.tsv");

        String modelFile = "sentiment-model.svm";
        String inputFile = "/sentiment-test.tsv";
        String predictionFile = "sentiment-test_predictions.tsv";

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

