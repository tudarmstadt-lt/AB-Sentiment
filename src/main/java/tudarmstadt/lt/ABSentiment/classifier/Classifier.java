package tudarmstadt.lt.ABSentiment.classifier;

import org.apache.uima.jcas.JCas;

/**
 * Interface for classifier classes.
 */
public interface Classifier {

    String getLabel(JCas cas);
    double getScore();
    double getScore(int i);
    String[] getLabels();
    double[] getScores();

}
