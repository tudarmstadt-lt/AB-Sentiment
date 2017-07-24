package tudarmstadt.lt.ABSentiment.training.aspecttarget;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

public class AspectTargetTrainTest {
    @org.junit.Test
    public void Train() throws AnalysisEngineProcessException {
        String[] args = new String[1];
        args[0] = "/configurationTest.txt";
        Train.main(args);
    }

}