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

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import java.io.File;
import java.io.IOException;

/**
 * Testing class for DNN classifier
 * Created by abhishek on 19/5/17.
 */
public class DNNTesting {
    public MultiLayerNetwork loadModel(String modelFile) {
        MultiLayerNetwork model = null;
        File locationToSave = new File(modelFile+".zip");
        try {
            model = ModelSerializer.restoreMultiLayerNetwork(locationToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }
}
