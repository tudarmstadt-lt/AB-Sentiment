package tudarmstadt.lt.ABSentiment.aspecttermextraction;


import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.classifier.Classifier;

public class CrfAspectExtractor implements Classifier {

    public CrfAspectExtractor() {

    }
    @Override
    public String getLabel(JCas cas) {
        return null;
    }

    @Override
    public double getScore() {
        return 0;
    }

    @Override
    public double getScore(int i) {
        return 0;
    }

    @Override
    public String[] getLabels() {
        return new String[0];
    }

    @Override
    public double[] getScores() {
        return new double[0];
    }
}
