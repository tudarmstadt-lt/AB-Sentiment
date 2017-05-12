package tudarmstadt.lt.ABSentiment.featureExtractor;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import org.apache.uima.jcas.JCas;
import org.jblas.FloatMatrix;
import tudarmstadt.lt.ABSentiment.featureExtractor.util.GenericWordSpace;
import tudarmstadt.lt.ABSentiment.featureExtractor.util.GloVeSpace;
import tudarmstadt.lt.ABSentiment.featureExtractor.util.W2vSpace;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.util.Collection;

/**
 * Created by abhishek on 10/5/17.
 */
public class WordEmbeddingFeature implements FeatureExtractor {

    private int offset = 0;
    private int featureCount = 0;
    private String embeddingFile;
    int wordRepresentation;
    GenericWordSpace<FloatMatrix> model;


    public WordEmbeddingFeature(String embeddingFile, int wordRepresentation){
        this.embeddingFile = embeddingFile;
        this.wordRepresentation = wordRepresentation;
        if(wordRepresentation == 1){
            model = GloVeSpace.load(embeddingFile, true, true);
        }else{
            model = W2vSpace.load(embeddingFile, true);
        }
        featureCount = model.getVectorLength();
    }

    public WordEmbeddingFeature(String embeddingFile, int wordRepresentation, int offset){
        this(embeddingFile, wordRepresentation);
        this.offset = offset;
    }

    private Preprocessor preprocessor = new Preprocessor();

    @Override
    public Feature[] extractFeature(JCas cas) {
        Collection<String> documentText = preprocessor.getTokenStrings(cas);
        FloatMatrix wordVector = new FloatMatrix(featureCount);
        int num = 0;
        for (String token : documentText) {
            if(model.vector(token) != null){
                wordVector = wordVector.add(model.vector(token));
                num++;
            }
        }
//        wordVector = VectorMath.normalize(wordVector);
//        wordVector = wordVector.div(num);
        Feature[] instance = new Feature[featureCount];
        for(int i=0;i<featureCount;i++){
            instance[i] = new FeatureNode(i+offset, wordVector.get(i));
        }
        return instance;
    }

    @Override
    public int getFeatureCount() {
        return featureCount;
    }

    @Override
    public int getOffset() {
        return offset;
    }
}
