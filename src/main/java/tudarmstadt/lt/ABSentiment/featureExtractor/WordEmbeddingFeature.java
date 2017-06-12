package tudarmstadt.lt.ABSentiment.featureExtractor;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import org.apache.uima.jcas.JCas;
import org.jblas.FloatMatrix;
import tudarmstadt.lt.ABSentiment.featureExtractor.util.GenericWordSpace;
import tudarmstadt.lt.ABSentiment.featureExtractor.util.GloVeSpace;
import tudarmstadt.lt.ABSentiment.featureExtractor.util.VectorMath;
import tudarmstadt.lt.ABSentiment.featureExtractor.util.W2vSpace;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.util.Collection;

/**
 * Word Embedding {@link FeatureExtractor}, extracts the averaged word representation for an instance using a word embedding file.
 */
public class WordEmbeddingFeature implements FeatureExtractor {

    private int offset = 0;
    private int featureCount = 0;
    private String embeddingFile;
    private int wordRepresentation;
    private GenericWordSpace<FloatMatrix> model;

    private Preprocessor preprocessor = new Preprocessor();

    /**
     * Constructor; specifies the word embedding file. The type of word embedding. Feature offset is set to '0' by default.
     * @param embeddingFile path to the file containing word embeddings
     * @param wordRepresentation specifies the type of word embedding to be used
     */
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

    /**
     * Constructor; specifies the word embedding file. The type of word embedding. Feature offset is specified.
     * @param embeddingFile path to the file containing word embeddings
     * @param wordRepresentation specifies the type of word embedding to be used
     * @param offset the feature offset, all features start from this offset
     */
    public WordEmbeddingFeature(String embeddingFile, int wordRepresentation, int offset){
        this(embeddingFile, wordRepresentation);
        this.offset = offset;
    }

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
        Feature[] instance = new Feature[featureCount];
        if(num!=0){
            wordVector = VectorMath.normalize(wordVector);
        }
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
