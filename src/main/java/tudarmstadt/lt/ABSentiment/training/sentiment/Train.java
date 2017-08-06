package tudarmstadt.lt.ABSentiment.training.sentiment;

import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.training.DNNTraining;
import tudarmstadt.lt.ABSentiment.training.LinearTraining;

import java.util.Vector;

/**
 * Sentiment Model Trainer
 */
public class Train extends ProblemBuilder {

    public static void main(String[] args) {

        String modelType = "linear";
        if (args.length == 1) {
            configurationfile = args[0];
        }
        initialise(configurationfile);

        Vector<FeatureExtractor> features = loadFeatureExtractors();
        Problem problem = buildProblem(trainFile, features, "sentiment", true);

        if(modelType.equals("linear")){
            LinearTraining linearTraining = new LinearTraining();
            Model model = linearTraining.trainModel(problem);
            linearTraining.saveModel(model, sentimentModel);
            saveLabelMappings(labelMappingsFileSentiment);
        }else if(modelType.equals("lstm")){
            DNNTraining dnnTraining = new DNNTraining();
            MultiLayerNetwork model = dnnTraining.trainModel(problem);
            dnnTraining.saveModel(model, sentimentModel, true);
            saveLabelMappings(labelMappingsFileSentiment);
        }
    }

}