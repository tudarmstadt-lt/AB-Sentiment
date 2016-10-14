package tudarmstadt.lt.ABSentiment.training.sentiment;

import tudarmstadt.lt.ABSentiment.training.precomputation.ComputeIdfScores;


public class SentimentTrainTest {

    @org.junit.Test
    public void Train() {
        String trainingFile = "data/sentiment_train.tsv";
        String idfFile = "data/features/idfmap.tsv";

        ComputeIdfScores.computeIdfScores(trainingFile, idfFile);
        Train.main(new String[0]);
    }

}