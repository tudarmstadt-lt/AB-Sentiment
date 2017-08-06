package tudarmstadt.lt.ABSentiment.training.sentiment;

import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.training.DNNTesting;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

import java.util.Vector;

/**
 * Sentiment Model Tester
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
        loadLabelMappings(labelMappingsFileSentiment);

        Vector<FeatureExtractor> features = loadFeatureExtractors();

        if(modelType.equals("linear")){
            LinearTesting linearTesting = new LinearTesting();
            Model model = linearTesting.loadModel(sentimentModel);
            classifyTestSet(testFile, model, features, predictionFile, "sentiment", true);
        }else if(modelType.equals("dnn")){
            DNNTesting dnnTesting = new DNNTesting();
            Problem problem = buildProblem(testFile, features, "sentiment", false);
              MultiLayerNetwork model = dnnTesting.loadModel(sentimentModel);
            classifyTestSet(model, problem, true);
        }

    }

}