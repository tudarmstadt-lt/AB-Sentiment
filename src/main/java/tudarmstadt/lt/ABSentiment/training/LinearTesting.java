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

package tudarmstadt.lt.ABSentiment.training;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import tudarmstadt.lt.ABSentiment.featureExtractor.util.ConfusionMatrix;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.type.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * Testing Class that is extended by all linear testers.
 * Offers methods to test {@link Model}s by classifying documents and storing the labels.
 */
public class LinearTesting{

    public Model loadModel(String modelFile) {
        if (modelFile.endsWith(".svm.gz")) {
            try {
                System.err.println("Loading model from: " + modelFile);
                return Linear.loadModel(new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(modelFile)), "UTF-8")));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        try {
            return Linear.loadModel(new File(modelFile));
        } catch (IOException e) {
            System.err.println("Model file not found, trying to load a gzipped file...");
            // if no model found, try to get a gzipped version
            return loadModel(modelFile+".svm.gz");
        }
    }
}
