package tudarmstadt.lt.ABSentiment.training.sentiment;

import tudarmstadt.lt.ABSentiment.training.ComputeIdfScores;

import static org.junit.Assert.*;


public class SentimentTrainTest {

    @org.junit.Test
    public void Train() {
        String trainingFile = "/relevance-train.tsv";
        String idfFile = "idfmap.tsv";

        ComputeIdfScores.computeIdfScores(trainingFile, idfFile);
        Train.main(new String[0]);
    }

}