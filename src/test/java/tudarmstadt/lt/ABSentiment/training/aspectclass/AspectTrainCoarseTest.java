package tudarmstadt.lt.ABSentiment.training.aspectclass;

import tudarmstadt.lt.ABSentiment.featureExtractor.precomputation.ComputeCorpusIdfScores;

public class AspectTrainCoarseTest {


    @org.junit.Test
    public void Train() {
        String trainingFile = "data/aspect_train.tsv";
        String idfFile = "data/features/idfmap.tsv.gz";
        ComputeCorpusIdfScores.computeIdfScores(trainingFile, idfFile);

        String[] args = new String[1];
        args[0] = trainingFile;
        Train.main(args);
    }

}