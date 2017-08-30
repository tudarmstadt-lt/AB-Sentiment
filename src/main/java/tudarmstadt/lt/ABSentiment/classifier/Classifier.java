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

package tudarmstadt.lt.ABSentiment.classifier;

import org.apache.uima.jcas.JCas;

/**
 * Interface for classifier classes.
 */
public interface Classifier {

    /**
     * Classifies a CAS and returns a label.
     * @param cas the CAS that is analyzed
     * @return a String label assigned by the classifier
     */
    String getLabel(JCas cas);

    /**
     * Returns the last label.
     * @return a String label assigned by the classifier
     */
    String getLabel();

    /**
     * Returns the confidence score for the most probable label.
     * @return the confidence score for the label
     */
    double getScore();

    //double getScore(int i);
    //String[] getLabels();
    //double[] getScores();

}
