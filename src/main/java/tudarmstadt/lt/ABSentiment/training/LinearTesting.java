package tudarmstadt.lt.ABSentiment.training;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import tudarmstadt.lt.ABSentiment.featureExtractor.util.ConfusionMatrix;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.type.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * Testing Class that is extended by all linear testers.
 * Offers methods to test {@link Model}s by classifying documents and storing the labels.
 */
public class LinearTesting extends LinearTraining {

    private static HashMap<String, Integer> labelMappings = new HashMap<>();
    private static HashMap<Integer, String> labelLookup = new HashMap<>();

    protected static String testFile;
    protected static String predictionFile;

    private static String goldLabel;
    private static String predictedLabel;

    private static ConfusionMatrix confusionMatrix;

    private static ArrayList<String> allLabels = new ArrayList<>();

    /**
     * Loads the {@link Model} from a file.
     * @param modelFile path to the model file, can be gzipped
     * @return the {@link Model} stored in the file
     */
    protected static Model loadModel(String modelFile) {
        if (modelFile.endsWith(".gz")) {
            try {
                System.err.println("Loading model from: " + modelFile);
                return Linear.loadModel(new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(modelFile)), "UTF-8")));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        try {
            return Linear.loadModel(new File(modelFile));
        } catch (IOException e) {
            System.err.println("Model file not found, trying to load a gzipped file...");
            // if no model found, try to get a gzipped version
            return loadModel(modelFile+".gz");
        }
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
        Writer featureOut = null;

        try {
            OutputStream predStream = new FileOutputStream(predictionFile);
            out = new OutputStreamWriter(predStream, "UTF-8");
            if (featureOutputFile != null) {
                OutputStream vectorStream = new FileOutputStream(featureOutputFile);
                featureOut = new OutputStreamWriter(vectorStream, "UTF-8");
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e1) {
            e1.printStackTrace();
            System.exit(1);
        }

        Feature[] instance;
        Vector<Feature[]> instanceFeatures;

        confusionMatrix = new ConfusionMatrix();
        String item;
        for (int j = 0; j < model.getNrClass(); j++) {
            item = labelLookup.get(Integer.parseInt(model.getLabels()[j]+""));
            confusionMatrix.addLabel(item);
            allLabels.add(item);
        }

        confusionMatrix.createMatrix();
        for (Document d : fr) {
            preprocessor.processText(d.getDocumentText());
            instanceFeatures = applyFeatures(preprocessor.getCas(), features);

            Double prediction;

            instance = combineInstanceFeatures(instanceFeatures);
            double[] prob_estimates = new double[model.getNrClass()];
            prediction = Linear.predictProbability(model, instance, prob_estimates);
            System.out.println("-------------\n" + d.getDocumentId());
            for (int j = 0; j < model.getNrClass(); j++) {
                System.out.println(labelLookup.get(Integer.parseInt(model.getLabels()[j]+"")) +"\t" +(prob_estimates[j]));
            }

            try {
                out.write(d.getDocumentId() + "\t" + d.getDocumentText() + "\t");
                if (useCoarseLabels) {
                    out.append(d.getLabelsCoarseString());
                    goldLabel = d.getLabelsCoarseString();
                    predictedLabel = labelLookup.get(prediction.intValue());
                    System.out.println(goldLabel + "\t" + predictedLabel);
                } else {
                    out.append(d.getLabelsString());
                    goldLabel = d.getLabelsString();
                    predictedLabel= labelLookup.get(prediction.intValue());
                    System.out.println(goldLabel + "\t" + predictedLabel);
                }
                confusionMatrix.updateMatrix(predictedLabel, goldLabel);
                out.append("\t").append(labelLookup.get(prediction.intValue())).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // vector output
            if (featureOutputFile != null) {
                String[] labels = d.getLabels();
                if (useCoarseLabels) { labels = d.getLabelsCoarse(); }
                for (String label : labels) {
                    try {
                        assert featureOut != null;
                        featureOut.write(Double.parseDouble(labelMappings.get(label).toString()) + "");
                        for (Feature f : instance) {
                            featureOut.write(" " + f.getIndex() + ":" + f.getValue());
                        }
                        featureOut.write("\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("---------------------------------------");
        HashMap<String, Float> recall;
        HashMap<String, Float> precision;
        HashMap<String, Float> fMeasure;

        recall = getRecallForAll();
        precision = getPrecisionForAll();
        fMeasure = getFMeasureForAll();

        System.out.println("Label"+"\t"+"Recall"+"\t"+"Precision"+"\t"+"F Score");
        for(String itemLabel: allLabels){
            System.out.println(itemLabel+"\t"+recall.get(itemLabel)+"\t"+precision.get(itemLabel)+"\t"+fMeasure.get(itemLabel));
        }

        printFeatureStatistics(features);
        printConfusionMatrix();
        System.out.println("\n");
        System.out.println("True positive     : " + getTruePositive());
        System.out.println("Accuracy          : " + getOverallAccuracy());
        System.out.println("Overall Precision : " + getOverallPrecision());
        System.out.println("Overall Recall    : " + getOverallRecall());
        System.out.println("Overall FMeasure  : " + getOverallFMeasure());
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
                Integer labelId = Integer.parseInt(catLine[0]);
                labelLookup.put(labelId, catLine[1]);

                labelMappings.put(catLine[1], labelId);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected static void printConfusionMatrix(){
        confusionMatrix.printConfusionMatrix();
    }

    protected static double getRecallForLabel(String label){
        return confusionMatrix.getRecallForLabel(label);
    }

    protected static double getPrecisionForLabel(String label){
        return confusionMatrix.getPrecisionForLabel(label);
    }

    protected static HashMap<String, Float> getRecallForAll(){
        return confusionMatrix.getRecallForAllLabels();
    }

    protected static HashMap<String, Float> getPrecisionForAll(){
        return confusionMatrix.getPrecisionForAllLabels();
    }

    protected static HashMap<String, Float> getFMeasureForAll(){
        return confusionMatrix.getFMeasureForAllLabels();
    }

    protected static int getTruePositive(){
        return confusionMatrix.getTruePositive();
    }

    protected static float getOverallAccuracy(){return confusionMatrix.getOverallAccuracy(); }

    protected static float getOverallRecall(){
        return confusionMatrix.getOverallRecall();
    }

    protected static float getOverallPrecision(){
        return confusionMatrix.getOverallPrecision();
    }

    protected static float getOverallFMeasure(){
        return confusionMatrix.getOverallFMeasure()   ;
    }
}
