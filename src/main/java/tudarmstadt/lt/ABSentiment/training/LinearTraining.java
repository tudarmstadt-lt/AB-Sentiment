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


import de.bwaldvogel.liblinear.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

/**
 * General training class for the linear SVM classifier
 */
public class LinearTraining {

    public Model trainModel(Problem problem){
        SolverType solver = SolverType.L2R_LR;
        double C = 1.0;
        double eps = 0.01;
        Parameter parameter = new Parameter(solver, C, eps);

        return Linear.train(problem, parameter);
    }

    public void saveModel(Model model, String modelFile, boolean saveGzipped) {
        try {
            if (saveGzipped) {
                model.save(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(modelFile + ".svm.gz")), "UTF-8"));
            } else {
                model.save(new File(modelFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveModel(Model model, String modelFile) {
       saveModel(model, modelFile, true);
    }

}
