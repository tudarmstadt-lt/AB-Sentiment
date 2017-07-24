package tudarmstadt.lt.ABSentiment.training.relevance;

import tudarmstadt.lt.ABSentiment.featureExtractor.precomputation.ComputeCorpusIdfScores;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;

public class RelevanceTrainTest extends ProblemBuilder{

    @org.junit.Test
    public void Train() {

        initialise("/configurationTest.txt");
        String idfFile = "data/features/idfmap.tsv.gz";
        ComputeCorpusIdfScores.computeIdfScores(trainFile, idfFile);

        String[] args = new String[1];
        args[0] = "/configurationTest.txt";
        Train.main(args);
    }

}