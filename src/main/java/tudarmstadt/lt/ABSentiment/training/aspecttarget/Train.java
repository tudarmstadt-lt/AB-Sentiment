package tudarmstadt.lt.ABSentiment.training.aspecttarget;

import org.apache.uima.UIMAException;
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
import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

/**
 * Aspect Target Identification Model Trainer
 */
public class Train {

    public static void main(String[] args) throws AnalysisEngineProcessException {

        File modelDirectory = new File("");
        File trainingFile = new File("src/main/resources/targets-train.connl");

        if (args.length == 2) {
            trainingFile = new File(args[0]);
            modelDirectory = new File(args[1]);
        }

        try {
            runPipeline(
                    FilesCollectionReader.getCollectionReaderWithSuffixes(trainingFile.getAbsolutePath(),
                            ConllReader.CONLL_VIEW, trainingFile.getName()),
                    createEngine(ConllReader.class),
                    AnalysisEngineFactory.createEngine(AspectAnnotator.class,
                            CleartkSequenceAnnotator.PARAM_IS_TRAINING, true,
                            DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
                            modelDirectory.getAbsolutePath(),
                            DefaultSequenceDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
                            CrfSuiteStringOutcomeDataWriter.class));
            org.cleartk.ml.jar.Train.main(modelDirectory.getAbsolutePath());
        } catch (UIMAException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
