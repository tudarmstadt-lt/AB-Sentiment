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


import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.jar.GenericJarClassifierFactory;
import tudarmstadt.lt.ABSentiment.classifier.aspecttarget.AspectAnnotator;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.type.uima.AspectTarget;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

/**
 * The CRF classifier is used for aspect target detection.
 */
public class CrfClassifier extends ProblemBuilder{
    private AnalysisEngine classifier;

    /**
     * Constructor, creates an AnalysisEngine with a CRF classifier.
     */
    public CrfClassifier(String configurationFile) {
        try {
            initialise(configurationFile);
            classifier = createEngine(AspectAnnotator.class,
                    GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                    crfModel + "model.jar");

        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies the CRF classifier that annotates {@link AspectTarget} in a CAS
     * @param cas the input CAS
     * @return a processed CAS with {@link AspectTarget} annotations
     */
    public JCas processCas(JCas cas) {
        try {
            runPipeline(cas, classifier);
            return cas;
        } catch (AnalysisEngineProcessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
