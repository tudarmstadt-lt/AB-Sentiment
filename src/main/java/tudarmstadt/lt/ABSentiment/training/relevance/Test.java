package tudarmstadt.lt.ABSentiment.training.relevance;

import de.bwaldvogel.liblinear.Model;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

import java.util.Vector;

/**
 * Relevance Model Tester
 */
public class Test extends LinearTesting {

    /**
     * Classifies an input file, given a model
     * @param args optional: input file, model file and the output file
     */
    public static void main(String[] args) {

        loadLabelMappings("data/models/relevance_label_mappings.tsv");

        modelFile = "data/models/relevance_model.svm";
        testFile = "dev.xml";
        featureOutputFile = "data/relevance_test.svm";
        predictionFile = "relevance_test_predictions.tsv";
        idfGazeteerFile = "data/features/relevance_idfterms.tsv";
        //documentLengthFile = "data/features/max_length";

        if (args.length == 3) {
            testFile = args[0];
            modelFile = args[1];
            predictionFile = args[2];
        }

        Vector<FeatureExtractor> features = loadFeatureExtractors();

        Model model = loadModel(modelFile);

        classifyTestSet(testFile, model, features, predictionFile, "relevance");
    }

}

