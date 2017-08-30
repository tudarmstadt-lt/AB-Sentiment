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

package tudarmstadt.lt.ABSentiment.training.aspecttarget;

import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import org.cleartk.ml.CleartkSequenceAnnotator;
import org.cleartk.ml.crfsuite.CrfSuiteStringOutcomeDataWriter;
import org.cleartk.ml.jar.DefaultSequenceDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.util.cr.FilesCollectionReader;
import tudarmstadt.lt.ABSentiment.classifier.aspecttarget.AspectAnnotator;
import tudarmstadt.lt.ABSentiment.reader.ConllReader;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.util.XMLExtractorTarget;

import java.io.File;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

/**
 * Aspect Target Identification Model Trainer
 */
public class Train extends ProblemBuilder {

    /**
     * Trains the model from an input file
     *
     * @param args optional: input file and directory for model
     */
    public static void main(String[] args) {

        if (args.length == 1) {
            configurationfile = args[0];
        }
        initialise(configurationfile);

        File modelDirectory = new File(crfModel);
        String inputFile = trainFile;



        if (inputFile.endsWith("xml")) {
            String[] xArgs = new String[2];
            xArgs[0] = inputFile;
            inputFile = inputFile.replace(".xml", "") + ".conll";
            if (inputFile.startsWith("/")) {
                inputFile = "." + inputFile;
            }
            xArgs[1] = inputFile;
            XMLExtractorTarget.main(xArgs);
        }

        String modelLocation = crfModel.concat("opennlp-de-pos-maxent.bin");

        File trainingFile = new File(inputFile);
        trainingFile.deleteOnExit();
        try {
            runPipeline(
                    FilesCollectionReader.getCollectionReaderWithSuffixes(trainingFile.getAbsolutePath(),
                            ConllReader.CONLL_VIEW, inputFile),
                    createEngine(ConllReader.class),
                    createEngine(OpenNlpPosTagger.class,
                            OpenNlpPosTagger.PARAM_MODEL_LOCATION, modelLocation),

                    createEngine(ClearNlpLemmatizer.class),
                    createEngine(AspectAnnotator.class,
                            CleartkSequenceAnnotator.PARAM_IS_TRAINING, true,
                            DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
                            modelDirectory.getAbsolutePath(),
                            DefaultSequenceDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
                            CrfSuiteStringOutcomeDataWriter.class));
            org.cleartk.ml.jar.Train.main(modelDirectory.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
