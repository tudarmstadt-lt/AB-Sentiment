package tudarmstadt.lt.ABSentiment.training.relevance;

import tudarmstadt.lt.ABSentiment.featureExtractor.precomputation.ComputeCorpusIdfScores;

public class RelevanceTrainTest {

    @org.junit.Test
    public void Train() {
        String trainingFile = "data/relevance_train.tsv";
        String idfFile = "data/features/idfmap.tsv";
        ComputeCorpusIdfScores.computeIdfScores(trainingFile, idfFile);

        Train.main(new String[0]);
    }

}