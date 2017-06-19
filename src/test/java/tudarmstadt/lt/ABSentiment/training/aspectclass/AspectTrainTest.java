package tudarmstadt.lt.ABSentiment.training.aspectclass;

import tudarmstadt.lt.ABSentiment.featureExtractor.precomputation.ComputeCorpusIdfScores;

public class AspectTrainTest {

    @org.junit.Test
    public void Train() {
        String trainingFile = "data/aspect_train.tsv";
        String idfFile = "data/features/idfmap.tsv";
        ComputeCorpusIdfScores.computeIdfScores(trainingFile, idfFile);

        Train.main(new String[0]);
    }

}