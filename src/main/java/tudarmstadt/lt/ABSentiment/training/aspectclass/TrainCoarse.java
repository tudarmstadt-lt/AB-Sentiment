package tudarmstadt.lt.ABSentiment.training.aspectclass;

import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.training.LSTMTraining;
import tudarmstadt.lt.ABSentiment.training.LinearTraining;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Aspect Model Trainer (coarse-grained)
 */
public class TrainCoarse extends ProblemBuilder {

    public static void main(String[] args) {


        String modelType = "linear";
        if (args.length == 1) {
            configurationfile = args[0];
        }
        initialise(configurationfile);

        Vector<FeatureExtractor> features = loadFeatureExtractors();
        Problem problem = buildProblem(trainFile, features, true);

        if(modelType.equals("linear")){
            LinearTraining linearTraining = new LinearTraining();
            Model model = linearTraining.trainModel(problem);
            linearTraining.saveModel(model, aspectCoarseModel);
            saveLabelMappings(labelMappingsFileAspectCoarse);
        }else if(modelType.equals("lstm")){
            LSTMTraining lstmTraining = new LSTMTraining();
            MultiLayerNetwork model = lstmTraining.trainModel(problem);
            lstmTraining.saveModel(model, aspectCoarseModel, true);
        }
    }

}