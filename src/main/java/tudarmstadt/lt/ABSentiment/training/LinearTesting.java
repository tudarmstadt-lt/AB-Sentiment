package tudarmstadt.lt.ABSentiment.training;


import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;

/**
 * Testing Class that is extended by all linear testers.
 * Offers methods to test {@link Model}s by classifying documents and storing the labels.
 */
public class LinearTesting extends LinearTraining {

    protected static Model model;

    private static HashMap<Double, String> labelMappings = new HashMap<>();

    /**
     * Loads the {@link Model} from a file.
     * @param modelFile path to the model file
     * @return the {@link Model} stored in the file
     */
    protected static Model loadModel(String modelFile) {
        try {
            return Linear.loadModel(new File(modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Classifies a file containing the test set using a {@link Model}. Stores the original labels and the predictions in a file for evaluation.
     * @param inputFile corpus containing the test set
     * @param model linear {@link Model}
     * @param features Vector of {@link FeatureExtractor}s
     * @param predictionFile path to the output file to store the predictions
     */
    protected static void classifyTestSet(String inputFile, Model model, Vector<FeatureExtractor> features, String predictionFile) {
        InputReader fr = new TsvReader(inputFile);
        Writer out = null;

        try {
            OutputStream predStream = new FileOutputStream(predictionFile);
            out = new OutputStreamWriter(predStream);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        Feature[] instance;
        Vector<Feature[]> instanceFeatures;
        for (Document d : fr) {
            preprocessor = new Preprocessor(d.getDocumentText());
            instanceFeatures = applyFeatures(preprocessor.getCas(), features);

            Double prediction;

            instance = combineInstanceFeatures(instanceFeatures);
            double[] prob_estimates = new double[model.getNrClass()];
            prediction = Linear.predictProbability(model, instance, prob_estimates);

            for (int j = 0; j < model.getNrClass(); j++) {
                System.out.println(labelMappings.get(Double.parseDouble(model.getLabels()[j]+"")) +"\t" +(prob_estimates[j]));
            }

            System.out.println(d.getLabelsString() + "\t" + labelMappings.get(prediction));
            try {
                out.append(d.getLabelsString());
                out.append("\t").append(labelMappings.get(prediction)).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the label--identifier mappings to retrieve the correct String label for the predicted label.
     * @param fileName path to the mapping file
     */
    protected static void loadLabelMappings(String fileName) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

            String line;
            while ((line = br.readLine()) != null) {
                String[] catLine = line.split("\\t");
                Double labelId = Double.parseDouble(catLine[0]);
                labelMappings.put(labelId, catLine[1]);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
