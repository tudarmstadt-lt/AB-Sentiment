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
        initialise("/configurationTest.txt");
        Vector<FeatureExtractor> features = loadFeatureExtractors();
        Problem problem = buildProblem(trainFile, features, true);
        Feature[][] inputFeature = problem.x;
        int n = inputFeature.length, i, len;
        for (i = 0; i < n; ++i) {
            Feature[] nodes = inputFeature[i];
            int indexBefore = 0;
            len = nodes.length;
            for (int j = 0; j < len; ++j) {
                Feature node = nodes[j];
                if (node.getIndex() <= indexBefore) {
                    throw new IllegalArgumentException("Feature nodes must be sorted by index in ascending order");
                }
                indexBefore = node.getIndex();
            }
        }
    }

    @org.junit.Test
    public void checkFeatureOffset() {
        initialise("/configurationTest.txt");
        long offset = Math.round(Math.random()*100);

        FeatureExtractor tfidf = new TfIdfFeature(idfFile, (int) offset);

        long minFeatureIndex = tfidf.getOffset();
        long maxFeatureIndex = offset + tfidf.getFeatureCount();

        Preprocessor preprocessor = new Preprocessor(true);
        preprocessor.processText("RT @BuntesDresden : Deutsche Bahn @db_bahn kappt int. Zugverbindungen nach #Budapest , Begründung : » Flüchtlingsströme « #Keleti #Migration htt …");
        Feature feature[] = tfidf.extractFeature(preprocessor.getCas());
        for(Feature f:feature){
            int index = f.getIndex();
            assert(index>= minFeatureIndex && index<=maxFeatureIndex);
        }

    }

}