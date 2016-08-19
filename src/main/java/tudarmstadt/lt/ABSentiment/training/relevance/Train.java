package tudarmstadt.lt.ABSentiment.training.relevance;

import de.bwaldvogel.liblinear.*;
import org.apache.uima.UIMAException;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.ComputeIdfScores;
import tudarmstadt.lt.ABSentiment.training.LinearTraining;

import java.util.Vector;

/**
 * Relevance Model Trainer
 */
public class Train extends LinearTraining {

    public static void main(String[] args) throws UIMAException, InterruptedException {

        String trainingFile = "/relevance-train.tsv";
        String modelFile = "relevance-model.svm";
        String labelMappingsFile  = "relevance-label-mappings.tsv";

        if (args.length == 2) {
            trainingFile = args[0];
            modelFile = args[1];
        }

        // IDF scores need to be computed only once per training corpus
        ComputeIdfScores.computeIdfScores(trainingFile, idfFile);

        Vector<FeatureExtractor> features = loadFeatureExtractors();

        Problem problem = buildProblem(trainingFile, features);
        Model model = trainModel(problem);
        saveModel(model, modelFile);

        saveLabelMappings(labelMappingsFile);
    }

}