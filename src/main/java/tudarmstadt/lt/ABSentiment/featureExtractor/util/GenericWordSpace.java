/*
 * ******************************************************************************
 *  Copyright 2016
 *  Copyright (c) 2016 Technische Universität Darmstadt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ****************************************************************************
 */

package tudarmstadt.lt.ABSentiment.featureExtractor.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for generic word space. Specifies common methods.
 * The original code comes from https://github.com/igorbrigadir/word2vec-java
 */
public abstract class GenericWordSpace<T>{

    /*
     * Store vectors & vocab in memory:
     */
    public Map<String, T> store = new HashMap<>();
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