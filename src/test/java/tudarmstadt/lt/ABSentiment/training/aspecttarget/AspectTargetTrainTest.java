package tudarmstadt.lt.ABSentiment.training.aspecttarget;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import tudarmstadt.lt.ABSentiment.training.ComputeIdfScores;

public class AspectTargetTrainTest {
    @org.junit.Test
    public void Train() throws AnalysisEngineProcessException {
        Train.main(new String[0]);
    }

}