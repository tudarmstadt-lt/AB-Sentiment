package tudarmstadt.lt.ABSentiment.training.aspectclass;

import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.training.LSTMTesting;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

import java.util.Vector;

/**
 * Aspect Model Tester (fine-grained)
 */
public class Test extends ProblemBuilder {

    /**
     * Classifies an input file, given a model
     * @param args optional: input file, model file and the output file
     */
    public static void main(String[] args) {

        loadLabelMappings("data/models/sentiment_label_mappings.tsv");

        modelFile = "data/models/sentiment_model";
        testFile = "data/new_financial_test.tsv";

        featureOutputFile = "data/sentiment_test.svm";
        predictionFile = "sentiment_test_predictions.tsv";
        idfGazeteerFile = "data/features/sentiment_idfterms.tsv";
        positiveGazeteerFile = "data/dictionaries/positive";
        negativeGazeteerFile = "data/dictionaries/negative";
        gloveFile = "data/wordEmbedding/glove_50_dimension.txt";
        w2vFile = "data/wordEmbedding/w2v_50_dimension.bin";

        String modelType = "linear";

        if (args.length == 3) {
            testFile = args[0];
            modelFile = args[1];
            predictionFile = args[2];
        }

        Vector<FeatureExtractor> features = loadFeatureExtractors();

        if(modelType.equals("linear")){
            LinearTesting linearTesting = new LinearTesting();
            Model model = linearTesting.loadModel(modelFile);
            classifyTestSet(testFile, model, features, predictionFile);
        }else if(modelType.equals("lstm")){
            LSTMTesting lstmTesting = new LSTMTesting();
            Problem problem = buildProblem(testFile, features);
            MultiLayerNetwork model = lstmTesting.loadModel(modelFile);
            classifyTestSet(model, problem);
        }

    }

}
