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

package tudarmstadt.lt.ABSentiment.featureExtractor;

import de.bwaldvogel.liblinear.Feature;
import org.apache.uima.jcas.JCas;

/**
 * Interface for feature extractors. Specifies common methods.
 */
public interface FeatureExtractor {

    /**
     * Extracts the feature using a {@link JCas}
     * @param cas the provided {@link JCas}
     * @return an array of {@link Feature}s, a training instance
     */
    Feature[] extractFeature(JCas cas);

    /**
     * Returns the maximal number of features that the extractor can produce.
     * @return the number of features in the {@link FeatureExtractor}
     */
    int getFeatureCount();

    /**
     * Returns the feature offset of the {@link FeatureExtractor}. All feature ids start from this offset to prevent overlaps.
     * @return the feature offset of the {@link FeatureExtractor}
     */
    int getOffset();
}
