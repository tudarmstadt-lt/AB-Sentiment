package tudarmstadt.lt.ABSentiment.training.aspectclass;

import tudarmstadt.lt.ABSentiment.featureExtractor.precomputation.ComputeCorpusIdfScores;

public class AspectTrainCoarseTest {


    @org.junit.Test
    public void Train() {
        String trainingFile = "/train.xml";
        String idfFile = "data/features/idfmap.tsv";
        ComputeCorpusIdfScores.computeIdfScores(trainingFile, idfFile);

        String[] args = new String[1];
        args[0] = trainingFile;
        Train.main(args);
    }

}