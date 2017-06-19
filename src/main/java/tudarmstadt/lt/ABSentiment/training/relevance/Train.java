package tudarmstadt.lt.ABSentiment.training.relevance;

import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.training.LSTMTraining;
import tudarmstadt.lt.ABSentiment.training.LinearTraining;

import java.util.Vector;

/**
 * Relevance Model Trainer
 */
public class Train extends ProblemBuilder {

    public static void main(String[] args) {

        trainFile = "data/new_financial_train.tsv";
        modelFile = "data/models/sentiment_model";
        featureOutputFile = "data/sentiment_train.svm";
        featureStatisticsFile = "data/sentiment_feature_stats.tsv";
        labelMappingsFile  = "data/models/sentiment_label_mappings.tsv";
        idfGazeteerFile = "data/features/sentiment_idfterms.tsv";
        positiveGazeteerFile = "data/dictionaries/positive";
        negativeGazeteerFile = "data/dictionaries/negative";
        gloveFile = "data/wordEmbedding/glove_50_dimension.txt";
        w2vFile = "data/wordEmbedding/w2v_50_dimension.bin";

        String modelType = "linear";

        if (args.length == 2) {
            trainFile = args[0];
            modelFile = args[1];
        } else if (args.length == 1) {
            trainFile = args[0];
        }

        Vector<FeatureExtractor> features = loadFeatureExtractors();
        Problem problem = buildProblem(trainFile, features);

        if(modelType.equals("linear")){
            LinearTraining linearTraining = new LinearTraining();
            Model model = linearTraining.trainModel(problem);
            linearTraining.saveModel(model, modelFile);
            saveLabelMappings(labelMappingsFile);
        }else if(modelType.equals("lstm")){
            LSTMTraining lstmTraining = new LSTMTraining();
            MultiLayerNetwork model = lstmTraining.trainModel(problem);
            lstmTraining.saveModel(model, modelFile, true);
        }
    }

}