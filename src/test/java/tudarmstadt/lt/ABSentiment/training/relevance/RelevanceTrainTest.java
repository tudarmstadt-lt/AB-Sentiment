package tudarmstadt.lt.ABSentiment.training.relevance;

import tudarmstadt.lt.ABSentiment.training.precomputation.ComputeIdfScores;

public class RelevanceTrainTest {

    @org.junit.Test
    public void Train() {
        String trainingFile = "data/relevance_train.tsv";
        String idfFile = "data/features/idfmap.tsv";
        ComputeIdfScores.computeIdfScores(trainingFile, idfFile);

        Train.main(new String[0]);
    }

}