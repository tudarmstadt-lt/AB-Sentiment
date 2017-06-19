package tudarmstadt.lt.ABSentiment.training;


import de.bwaldvogel.liblinear.*;
import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.featureExtractor.GazeteerFeature;
import tudarmstadt.lt.ABSentiment.featureExtractor.TfIdfFeature;
import tudarmstadt.lt.ABSentiment.featureExtractor.WordEmbeddingFeature;
import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

public class LinearTraining {

    public Model trainModel(Problem problem){
        SolverType solver = SolverType.L2R_LR;
        double C = 5.0;
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
