package tudarmstadt.lt.ABSentiment.classifier;


import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.jar.GenericJarClassifierFactory;
import tudarmstadt.lt.ABSentiment.classifier.aspecttarget.AspectAnnotator;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

public class CrfClassifier {
    AnalysisEngine classifier;

    public CrfClassifier(String modelDirectory) {
        try {
            classifier = createEngine(AspectAnnotator.class,
                    GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
                    modelDirectory + "/model.jar");
        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        }
    }

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
