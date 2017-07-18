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
 * Aspect Model Tester (course-grained)
 */
public class TestCoarse extends ProblemBuilder {

    /**
     * Classifies an input file, given a model
     * @param args optional: input file, model file and the output file
     */
    public static void main(String[] args) {

        initialise("configuration.txt");

        loadLabelMappings(labelMappingsFile);

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
            classifyTestSet(testFile, model, features, predictionFile, "aspect", true);
        }else if(modelType.equals("lstm")){
            LSTMTesting lstmTesting = new LSTMTesting();
            Problem problem = buildProblem(testFile, features, false);
            MultiLayerNetwork model = lstmTesting.loadModel(modelFile);
            classifyTestSet(model, problem, true);
        }

    }

}

