package tudarmstadt.lt.ABSentiment.training.sentiment;

import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.LinearTraining;

import java.util.Vector;

/**
 * Sentiment Model Trainer
 */
public class Train extends LinearTraining {

    /**
     * Trains the model from an input file
     * @param args optional: input file and optional model file
     */
    public static void main(String[] args) {

        trainingFile = "train.xml";
        modelFile = "data/models/sentiment_model.svm";
        featureOutputFile = "data/sentiment_train.svm";
        featureStatisticsFile = "data/sentiment_feature_stats.tsv";
        labelMappingsFile  = "data/models/sentiment_label_mappings.tsv";
        idfGazeteerFile = "data/features/sentiment_idfterms.tsv";
        positiveGazeteerFile = "data/dictionaries/positive";
        negativeGazeteerFile = "data/dictionaries/negative";
        gloveFile = null;
        w2vFile = null;

        if (args.length == 2) {
            trainingFile = args[0];
            modelFile = args[1];
        } else if (args.length == 1) {
            trainingFile = args[0];
        }

        Vector<FeatureExtractor> features = loadFeatureExtractors();

        Problem problem = buildProblem(trainingFile, features, "sentiment");
        Model model = trainModel(problem);
        saveModel(model, modelFile);

        saveLabelMappings(labelMappingsFile);

    }

}