package tudarmstadt.lt.ABSentiment.training.relevance;

import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.training.LSTMTesting;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

import java.util.Vector;

/**
 * Relevance Model Tester
 */
public class Test extends ProblemBuilder {

    /**
     * Classifies an input file, given a model
     * @param args optional: input file, model file and the output file
     */
    public static void main(String[] args) {

        String modelType = "linear";
        if (args.length == 1) {
            configurationfile = args[0];
        }
        initialise(configurationfile);

        loadLabelMappings(labelMappingsFileRelevance);
        Vector<FeatureExtractor> features = loadFeatureExtractors();

        if(modelType.equals("linear")){
            LinearTesting linearTesting = new LinearTesting();
            Model model = linearTesting.loadModel(relevanceModel);
            classifyTestSet(testFile, model, features, predictionFile, "relevance", true);
        }else if(modelType.equals("lstm")){
            LSTMTesting lstmTesting = new LSTMTesting();
            Problem problem = buildProblem(testFile, features, false);
            MultiLayerNetwork model = lstmTesting.loadModel(relevanceModel);
            classifyTestSet(model, problem, true);
        }

    }

}