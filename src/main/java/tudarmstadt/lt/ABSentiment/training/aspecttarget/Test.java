package tudarmstadt.lt.ABSentiment.training.aspecttarget;

import org.apache.uima.UIMAException;
import org.cleartk.ml.jar.GenericJarClassifierFactory;
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
public class Test {

    /**
     * Classifies an input file from a model
     * @param args optional: input file and model directory
     */
    public static void main(String[] args) {
        File testFile = new File("data/targets_test.connl");
        File modelDirectory = new File("data/models/");

        if (args.length == 2) {
            testFile = new File(args[0]);
            modelDirectory = new File(args[1]);
        }

        try {
            runPipeline(
                    FilesCollectionReader.getCollectionReaderWithSuffixes(testFile.getAbsolutePath(),
                            ConllReader.CONLL_VIEW, testFile.getName()),
                    createEngine(ConllReader.class),
                    createEngine(AspectAnnotator.class,
                            GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                            modelDirectory.getAbsolutePath() + "/model.jar"),
                    createEngine(AspectTermWriter.class, AspectTermWriter.OUTPUT_FILE, testFile+"_out",
                            AspectTermWriter.IS_GOLD, true));
        } catch (UIMAException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
