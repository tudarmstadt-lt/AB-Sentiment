package tudarmstadt.lt.ABSentiment.training.aspectclass;

import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import org.apache.uima.UIMAException;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.LinearTraining;

import java.util.Vector;

/**
 * Aspect Model Trainer (coarse-grained)
 */
public class TrainCoarse extends LinearTraining {

    /**
     * Trains the model from an input file
     * @param args optional: input file and optional model file
     */
    public static void main(String[] args) throws UIMAException, InterruptedException {


        trainingFile = "data/aspect_train.tsv";
        modelFile = "data/models/aspect_coarse_model.svm";
        labelMappingsFile = "data/models/aspect_coarse_label_mappings.tsv";
        idfGazeteerFile = "data/features/aspect_coarse_idfterms.tsv";

        if (args.length == 2) {
            trainingFile = args[0];
            modelFile = args[1];
        } else if (args.length == 1) {
            trainingFile = args[0];
        }

        Vector<FeatureExtractor> features = loadFeatureExtractors();

        // enable coarse document labels
        useCoarseLabels = true;

        Problem problem = buildProblem(trainingFile, features);
        Model model = trainModel(problem);
        saveModel(model, modelFile);

        saveLabelMappings(labelMappingsFile);
    }

}