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

package tudarmstadt.lt.ABSentiment.training.aspectclass;

import tudarmstadt.lt.ABSentiment.featureExtractor.precomputation.ComputeCorpusIdfScores;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;

public class AspectTrainTest extends ProblemBuilder{

    @org.junit.Test
    public void Train() {
        initialise("/configurationTest.txt");
        String idfFile = "data/features/idfmap.tsv.gz";
        ComputeCorpusIdfScores.computeIdfScores(trainFile, idfFile);

        String[] args = new String[1];
        args[0] = "/configurationTest.txt";
        Train.main(args);

    }

}