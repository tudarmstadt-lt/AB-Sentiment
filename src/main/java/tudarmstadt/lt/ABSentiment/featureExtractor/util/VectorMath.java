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

  public static float sum(FloatMatrix f){return f.sum();}
}
