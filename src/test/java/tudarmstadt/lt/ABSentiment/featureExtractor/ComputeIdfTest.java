package tudarmstadt.lt.ABSentiment.featureExtractor;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Problem;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.util.Vector;


public class ComputeIdfTest extends ProblemBuilder{
    @org.junit.Test
    public void addDocument() throws Exception {

    }

    @org.junit.Test
    public void saveIdfScores() throws Exception {

    }

    @org.junit.Test
    public void checkNodeIDOrder() throws Exception {
//        initialise("configuration.txt");
//        Vector<FeatureExtractor> features = loadFeatureExtractors();
//        Problem problem = buildProblem(trainFile, features, true);
//        Feature[][] inputFeature = problem.x;
//        int n = inputFeature.length, i, len;
//        for (i = 0; i < n; ++i) {
//            Feature[] nodes = inputFeature[i];
//            int indexBefore = 0;
//            len = nodes.length;
//            for (int j = 0; j < len; ++j) {
//                Feature node = nodes[j];
//                if (node.getIndex() <= indexBefore) {
//                    throw new IllegalArgumentException("Feature nodes must be sorted by index in ascending order");
//                }
//                indexBefore = node.getIndex();
//            }
//        }
    }

    @org.junit.Test
    public void checkFeatureOffset() {
//        initialise("configuration.txt");
//        long offset = Math.round(Math.random()*100);
//
//        FeatureExtractor tfidf = new TfIdfFeature(idfFile, (int) offset);
//
//        long minFeatureIndex = tfidf.getOffset();
//        long maxFeatureIndex = offset + tfidf.getFeatureCount();
//
//        Preprocessor preprocessor
//                = new Preprocessor();
//        preprocessor.processText("RT @BuntesDresden : Deutsche Bahn @db_bahn kappt int. Zugverbindungen nach #Budapest , Begründung : » Flüchtlingsströme « #Keleti #Migration htt …");
//        Feature feature[] = tfidf.extractFeature(preprocessor.getCas());
//        for(Feature f:feature){
//            int index = f.getIndex();
//            assert(index>= minFeatureIndex && index<=maxFeatureIndex);
//        }

    }

}