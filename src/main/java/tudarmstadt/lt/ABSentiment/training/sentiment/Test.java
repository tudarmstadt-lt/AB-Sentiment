package tudarmstadt.lt.ABSentiment.training.sentiment;

import de.bwaldvogel.liblinear.Model;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

import java.util.Vector;

/**
 * Sentiment Model Tester
 */
public class Test extends LinearTesting {

    /**
     * Classifies an input file, given a model
     * @param args optional: input file, model file and the output file
     */
    public static void main(String[] args) {

        loadLabelMappings("data/models/sentiment_label_mappings.tsv");

        modelFile = "data/models/sentiment_model.svm";
        testFile = "data/new_financial_test.tsv";

        featureOutputFile = "data/sentiment_test.svm";
        predictionFile = "sentiment_test_predictions.tsv";
        idfGazeteerFile = "data/features/sentiment_idfterms.tsv";
        positiveGazeteerFile = "data/dictionaries/positive";
        negativeGazeteerFile = "data/dictionaries/negative";
        gloveFile = "data/wordEmbedding/glove_50_dimension.txt";
        w2vFile = "data/wordEmbedding/w2v_50_dimension.bin";

        if (args.length == 3) {
            testFile = args[0];
            modelFile = args[1];
            predictionFile = args[2];
        }

        Vector<FeatureExtractor> features = loadFeatureExtractors();

        Model model = loadModel(modelFile);

        classifyTestSet(testFile, model, features, predictionFile);

        printConfusionMatrix();
        System.out.println("\n");
        System.out.println("True positive     : " + getTruePositive());
        System.out.println("Accuracy          : " + getOverallAccuracy());
        System.out.println("Overall Precision : " + getOverallPrecision());
        System.out.println("Overall Recall    : " + getOverallRecall());
        System.out.println("Overall FMeasure  : " + getOverallFMeasure());
    }

}

