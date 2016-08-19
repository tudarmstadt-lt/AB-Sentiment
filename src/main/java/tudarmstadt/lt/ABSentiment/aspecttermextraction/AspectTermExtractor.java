package tudarmstadt.lt.ABSentiment.aspecttermextraction;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.CleartkSequenceAnnotator;
import org.cleartk.ml.jar.DefaultSequenceDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.ml.crfsuite.CrfSuiteStringOutcomeDataWriter;
import org.cleartk.ml.jar.GenericJarClassifierFactory;
import org.cleartk.util.cr.FilesCollectionReader;
import tudarmstadt.lt.ABSentiment.reader.ConllReader;

import java.io.File;
import java.io.IOException;

public class AspectTermExtractor {


    public static void main(String[] args) throws AnalysisEngineProcessException {

        File modelDirectory = new File("");
        File trainingFile = new File("targets-train.tsv");

        try {

            runPipeline(
                    FilesCollectionReader.getCollectionReaderWithSuffixes(trainingFile.getAbsolutePath(),
                            ConllReader.CONLL_VIEW, trainingFile.getName()),
                    createEngine(ConllReader.class),
                    createEngine(AspectAnnotator.class,
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

        trainingFile = new File("targets-test.tsv");
        try {
            runPipeline(
                    FilesCollectionReader.getCollectionReaderWithSuffixes(trainingFile.getAbsolutePath(),
                            ConllReader.CONLL_VIEW, trainingFile.getName()),
                    createEngine(ConllReader.class),
                    createEngine(AspectAnnotator.class,
                            GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                            modelDirectory.getAbsolutePath() + "/model.jar"),

                    createEngine(AspectTermWriter.class, AspectTermWriter.OUTPUT_FILE, trainingFile+"out",
                            AspectTermWriter.IS_GOLD, true));
        } catch (ResourceInitializationException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (UIMAException e1) {
            e1.printStackTrace();
        }
    }
}
