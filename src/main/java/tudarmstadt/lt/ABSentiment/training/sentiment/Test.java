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

package tudarmstadt.lt.ABSentiment.training.sentiment;

import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.training.DNNTesting;
import tudarmstadt.lt.ABSentiment.training.LinearTesting;

import java.util.Vector;

/**
 * Sentiment Model Tester
 */
public class Test extends ProblemBuilder {

    /**
     * Classifies an input file, given a model
     * @param args optional: input file, model file and the output file
     */
    public static void main(String[] args) {

        String modelType = "linear";
        String type = "sentiment";
        if (args.length == 1) {
            configurationfile = args[0];
        }
        initialise(configurationfile);
        loadLabelMappings(labelMappingsFileSentiment);

        Vector<FeatureExtractor> features = loadFeatureExtractors(type);

        if(modelType.equals("linear")){
            LinearTesting linearTesting = new LinearTesting();
            Model model = linearTesting.loadModel(sentimentModel);
            classifyTestSet(testFile, model, features, predictionFile, type, true);
        }else if(modelType.equals("dnn")){
            DNNTesting dnnTesting = new DNNTesting();
            Problem problem = buildProblem(testFile, features, type, false);
            MultiLayerNetwork model = dnnTesting.loadModel(sentimentModel);
            classifyTestSet(model, problem, true);
        }

    }

}