package tudarmstadt.lt.ABSentiment.classifier;

import de.bwaldvogel.liblinear.Problem;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.LSTMTesting;
import tudarmstadt.lt.ABSentiment.training.util.Configuration;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by abhishek on 21/5/17.
 */
public class TestLSTMClassifier extends LSTMClassifier{

    public static void main(String args[]){

        initialise("configuration.txt");

        LSTMClassifier lstmClassifier = new LSTMClassifier();

        LSTMTesting lstmTesting = new LSTMTesting();

        lstmClassifier.model = lstmTesting.loadModel(modelFile);
        lstmClassifier.features = loadFeatureExtractors();

        Preprocessor preprocessor = new Preprocessor();
        preprocessor.processText("good great epic");

        System.out.println(lstmClassifier.getLabel(preprocessor.getCas()));
        System.out.println(lstmClassifier.getScore());
    }
}
