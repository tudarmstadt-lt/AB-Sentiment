package tudarmstadt.lt.ABSentiment.featureExtractor.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for generic word space. Specifies common methods.
 */
public abstract class GenericWordSpace<T>{

    /*
     * Store vectors & vocab in memory:
     */
    public Map<String, T> store = new HashMap<String, T>();
    /**
     * Checks if the word representation of a word exists
     * @param word the word whose representation is to be checked
     * @return a boolean value indicating the presence or absence of the word
     */
    public boolean contains(String word) {return store.containsKey(word);}
    /**
     * Returns the vector representation of the word
     * @param word the word whose representation is to be returned
     * @return the vector representation of the word
     */
    public T vector(String word) {return store.get(word);}
    /**
     * Returns the cosine similarity of two words after fetching their word representation
     * @param w1 the first word
     * @param w2 the second word
     * @return the cosine similarity of two words
     */
    public double cosineSimilarity(String w1, String w2) {
        return cosineSimilarity(vector(w1), vector(w2));
    }
    /**
     * Returns the cosine similarity of two vectors
     * @param vec1 the first vector
     * @param vec2 the second vector
     * @return the cosine similarity of two vectors
     */
    public abstract double cosineSimilarity(T vec1, T vec2);
    /**
     * Returns the length of the word vector of the word embedding chosen
     * @return the length of the word vector
     */
    public abstract int getVectorLength();

}