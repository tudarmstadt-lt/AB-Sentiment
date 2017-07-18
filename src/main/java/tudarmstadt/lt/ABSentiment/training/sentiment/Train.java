package tudarmstadt.lt.ABSentiment.training.sentiment;

import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.training.LSTMTraining;
import tudarmstadt.lt.ABSentiment.training.LinearTraining;

import java.util.Vector;

/**
 * Sentiment Model Trainer
 */
public class Train extends ProblemBuilder {

    public static void main(String[] args) {

        initialise("configuration.txt");

        String modelType = "linear";

        if (args.length == 2) {
            trainFile = args[0];
            modelFile = args[1];
        } else if (args.length == 1) {
            trainFile = args[0];
        }
        Vector<FeatureExtractor> features = loadFeatureExtractors();
        Problem problem = buildProblem(trainFile, features, "sentiment", true);

        if(modelType.equals("linear")){
            LinearTraining linearTraining = new LinearTraining();
            Model model = linearTraining.trainModel(problem);
            linearTraining.saveModel(model, modelFile);
            saveLabelMappings(labelMappingsFile);
        }else if(modelType.equals("lstm")){
            LSTMTraining lstmTraining = new LSTMTraining();
            MultiLayerNetwork model = lstmTraining.trainModel(problem);
            lstmTraining.saveModel(model, modelFile, true);
            saveLabelMappings(labelMappingsFile);
        }
    }

}