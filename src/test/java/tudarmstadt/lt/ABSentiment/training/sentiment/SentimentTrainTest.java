package tudarmstadt.lt.ABSentiment.training.sentiment;

import tudarmstadt.lt.ABSentiment.featureExtractor.precomputation.ComputeCorpusIdfScores;


public class SentimentTrainTest {

    @org.junit.Test
    public void Train() {
        String trainingFile = "data/sentiment_train.tsv";
        String idfFile = "data/features/idfmap.tsv";

        ComputeCorpusIdfScores.computeIdfScores(trainingFile, idfFile);

        String[] args = new String[1];
        args[0] = trainingFile;
        Train.main(args);
    }

}