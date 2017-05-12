package tudarmstadt.lt.ABSentiment.featureExtractor.util;

/**
 * Created by abhishek on 10/5/17.
 */
import java.util.HashMap;
import java.util.Map;

public abstract class GenericWordSpace<T>{

    /*
     * Store vectors & vocab in memory:
     */
    public Map<String, T> store = new HashMap<String, T>();

    public boolean contains(String word) {
        return store.containsKey(word);
    }

    public T vector(String word) {
        return store.get(word);
    }

    public double cosineSimilarity(String w1, String w2) {
        return cosineSimilarity(vector(w1), vector(w2));
    }

    public abstract double cosineSimilarity(T vec1, T vec2);

    public double distanceSimilarity(String w1, String w2) {
        return distanceSimilarity(vector(w1), vector(w2));
    }

    public abstract double distanceSimilarity(T vec1, T vec2);

    public abstract int getVectorLength();

}