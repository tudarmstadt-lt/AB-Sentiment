package tudarmstadt.lt.ABSentiment.training.aspecttarget;

import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.cleartk.ml.CleartkSequenceAnnotator;
import org.cleartk.ml.crfsuite.CrfSuiteStringOutcomeDataWriter;
import org.cleartk.ml.jar.DefaultSequenceDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.util.cr.FilesCollectionReader;
import tudarmstadt.lt.ABSentiment.classifier.aspecttarget.AspectAnnotator;
import tudarmstadt.lt.ABSentiment.reader.ConllReader;

import java.io.File;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

/**
 * Aspect Target Identification Model Trainer
 */
public class Train {

    /**
     * Trains the model from an input file
     * @param args optional: input file and directory for model
     */
    public static void main(String[] args) {

        File modelDirectory = new File("data/models/");
        File trainingFile = new File("data/targets_train.connl");

        if (args.length == 2) {
            trainingFile = new File(args[0]);
            modelDirectory = new File(args[1]);
        }

        try {
            runPipeline(
                    FilesCollectionReader.getCollectionReaderWithSuffixes(trainingFile.getAbsolutePath(),
                            ConllReader.CONLL_VIEW, trainingFile.getName()),
                    createEngine(ConllReader.class),
                    AnalysisEngineFactory.createEngine(OpenNlpPosTagger.class,
                            OpenNlpPosTagger.PARAM_MODEL_LOCATION, "data/models/opennlp-de-pos-maxent.bin"),

                    AnalysisEngineFactory.createEngine(ClearNlpLemmatizer.class),
                    AnalysisEngineFactory.createEngine(AspectAnnotator.class,
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
