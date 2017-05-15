package tudarmstadt.lt.ABSentiment.featureExtractor.util;

import org.jblas.FloatMatrix;

/**
 * A Java Class for some vector math utilities
 * The original code comes from https://github.com/igorbrigadir/word2vec-java
 */
public class VectorMath {

    /**
     * Computes and returns the cosine similarity of two word vectors
     * @param vec1 the first float vector
     * @param vec2 the second float vector
     * @return the cosine similarity of the two word vectors
     */
  public static double cosineSimilarity(FloatMatrix vec1, FloatMatrix vec2) {
    return vec1.dot(vec2);
  }

    /**
     * Normalizes the word vector
     * @param f the input float matrix for normalization
     * @return a float matrix after normalization
     */
  public static FloatMatrix normalize(FloatMatrix f) {
    return f.divi(f.norm2());
  }
}
