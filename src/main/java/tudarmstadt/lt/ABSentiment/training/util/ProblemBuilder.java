package tudarmstadt.lt.ABSentiment.training.util;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import org.apache.uima.jcas.JCas;
import org.datavec.api.records.reader.RecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import tudarmstadt.lt.ABSentiment.featureExtractor.*;
import tudarmstadt.lt.ABSentiment.featureExtractor.util.ConfusionMatrix;
import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.training.util.ListDoubleRecordReader;
import tudarmstadt.lt.ABSentiment.training.util.ListDoubleSplit;
import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by abhishek on 19/5/17.
 */
public class ProblemBuilder {

    protected static InputReader fr;
    protected static Preprocessor preprocessor = new Preprocessor();

    private static Integer maxLabelId = -1;
    private static int featureCount = 0;
    protected static boolean useCoarseLabels = false;

    protected static String trainFile;
    protected static String testFile;
    protected static String predictionFile;
    protected static String modelFile;
    protected static String labelMappingsFile;
    protected static String featureOutputFile;
    protected static String featureStatisticsFile;
    protected static String idfGazeteerFile;
    protected static String idfFile;
    protected static String relevanceModel;
    protected static String aspectModel;
    protected static String aspectCoarseModel;
    protected static String sentimentModel;

    protected static String positiveGazeteerFile;
    protected static String negativeGazeteerFile;

    protected static String missingWordsFile;
    protected static String DTExpansionFile;

    protected static String gloveFile;
    protected static String w2vFile;

    protected static String weightedIdfFile;
    protected static String weightedW2vFile;
    protected static String weightedGloveFile;

    protected static HashMap<String, Integer> labelMappings = new HashMap<>();
    protected static HashMap<Integer, String> labelLookup = new HashMap<>();
    protected static String goldLabel;
    protected static String predictedLabel;
    protected static ConfusionMatrix confusionMatrix;
    protected static ArrayList<String> allLabels = new ArrayList<>();

    protected static void initialise(String configurationFile){

        modelFile = null;
        idfFile = null;
        idfGazeteerFile = null;
        positiveGazeteerFile = null;
        negativeGazeteerFile = null;
        gloveFile = null;
        w2vFile = null;
        trainFile = null;
        testFile = null;
        featureOutputFile = null;
        predictionFile = null;
        labelMappingsFile = null;
        relevanceModel = null;
        aspectModel = null;
        aspectCoarseModel = null;
        sentimentModel = null;
        missingWordsFile = null;
        DTExpansionFile = null;
        weightedW2vFile = null;
        weightedGloveFile = null;
        weightedIdfFile = null;

        Configuration config = new Configuration();
        HashMap<String, String> fileLocation;
        fileLocation = config.readConfigurationFile(configurationFile);

        for(HashMap.Entry<String, String> entry: fileLocation.entrySet()){
            if(entry.getKey().equals("modelFile")){
                modelFile = entry.getValue();
            }else if(entry.getKey().equals("idfFile")){
                idfFile = entry.getValue();
            }else if(entry.getKey().equals("idfGazeteerFile")){
                idfGazeteerFile = entry.getValue();
            }else if(entry.getKey().equals("positiveGazeteerFile")){
                positiveGazeteerFile = entry.getValue();
            }else if(entry.getKey().equals("negativeGazeteerFile")){
                negativeGazeteerFile = entry.getValue();
            }else if(entry.getKey().equals("gloveFile")){
                gloveFile = entry.getValue();
            }else if(entry.getKey().equals("w2vFile")){
                w2vFile = entry.getValue();
            }else if(entry.getKey().equals("trainFile")){
                trainFile = entry.getValue();
            }else if(entry.getKey().equals("testFile")){
                testFile = entry.getValue();
            }else if(entry.getKey().equals("featureOutputFile")){
                featureOutputFile = entry.getValue();
            }else if(entry.getKey().equals("predictionFile")){
                predictionFile = entry.getValue();
            }else if(entry.getKey().equals("labelMappingsFile")){
                labelMappingsFile = entry.getValue();
            }else if(entry.getKey().equals("relevanceModel")){
                relevanceModel = entry.getValue();
            }else if(entry.getKey().equals("aspectModel")){
                aspectModel = entry.getValue();
            }else if(entry.getKey().equals("aspectCoarseModel")){
                aspectCoarseModel = entry.getValue();
            }else if(entry.getKey().equals("sentimentModel")) {
                sentimentModel = entry.getValue();
            }else if(entry.getKey().equals("missingWordsFile")) {
                missingWordsFile = entry.getValue();
            }else if(entry.getKey().equals("DTExpansionFile")) {
                DTExpansionFile = entry.getValue();
            }else if(entry.getKey().equals("weightedW2vFile")) {
                weightedW2vFile = entry.getValue();
            }else if(entry.getKey().equals("weightedGloveFile")) {
                weightedGloveFile = entry.getValue();
            }else if(entry.getKey().equals("weightedIdfFile")) {
                weightedIdfFile = entry.getValue();
            }
        }
    }

    protected static Vector<FeatureExtractor> loadFeatureExtractors() {
        int offset = 1;
        Vector<FeatureExtractor> features = new Vector<>();

        if(idfFile!=null){
            FeatureExtractor tfidf = new TfIdfFeature(idfFile, offset);
            offset += tfidf.getFeatureCount();
            features.add(tfidf);
        }
        if (idfGazeteerFile != null) {
            FeatureExtractor gazeteerIdf = new GazeteerFeature(idfGazeteerFile, offset);
            offset += gazeteerIdf.getFeatureCount();
            features.add(gazeteerIdf);
        }
        if (positiveGazeteerFile!= null) {
            FeatureExtractor posDict = new GazeteerFeature(positiveGazeteerFile, offset);
            offset += posDict.getFeatureCount();
            features.add(posDict);
        }
        if (negativeGazeteerFile!= null) {
            FeatureExtractor negDict = new GazeteerFeature(negativeGazeteerFile, offset);
            offset += negDict.getFeatureCount();
            features.add(negDict);
        }
        if (gloveFile!=null){
            FeatureExtractor glove = new WordEmbeddingFeature(gloveFile, null, 1, DTExpansionFile, offset);
            offset+=glove.getFeatureCount();
            features.add(glove);
        }
        if(w2vFile!=null){
            FeatureExtractor word2vec = new WordEmbeddingFeature(w2vFile, null, 2, DTExpansionFile,  offset);
            offset+=word2vec.getFeatureCount();
            features.add(word2vec);
        }
        if(weightedGloveFile!=null && weightedIdfFile!=null){
            FeatureExtractor word2vec = new WordEmbeddingFeature(weightedGloveFile, weightedIdfFile, 1, DTExpansionFile,  offset);
            offset+=word2vec.getFeatureCount();
            features.add(word2vec);
        }
        if(weightedW2vFile!=null && weightedIdfFile!=null){
            FeatureExtractor word2vec = new WordEmbeddingFeature(weightedW2vFile, weightedIdfFile, 2, DTExpansionFile,  offset);
            offset+=word2vec.getFeatureCount();
            features.add(word2vec);
        }
        return features;
    }

    protected static Problem buildProblem(String trainingFile, Vector<FeatureExtractor> features, String featureVectorFile) {
        fr = new TsvReader(trainingFile);

        int documentCount = 0;
        Vector<Double> labels = new Vector<>();
        Vector<Feature[]> featureVector = new Vector<>();
        Vector<Feature[]> instanceFeatures;
        for (Document d: fr) {
            preprocessor.processText(d.getDocumentText());
            instanceFeatures = applyFeatures(preprocessor.getCas(), features);

            // creates a training instance for each document label (multi-label training)
            String[] documentLabels;
            if (useCoarseLabels) {
                documentLabels = d.getLabelsCoarse();
            } else {
                documentLabels = d.getLabels();
            }
            for (String l : documentLabels) {
                if (l.isEmpty()) {continue;}
                Double label = getLabelId(l);
                labels.add(label);
                // combine feature vectors for one instance
                featureVector.add(combineInstanceFeatures(instanceFeatures));
                documentCount++;
            }
        }

        if (featureVectorFile != null) {
            saveFeatureVectors(featureVectorFile, featureVector, labels);
        }

        Problem problem = new Problem();
        problem.l = documentCount;
        problem.n = featureCount;
        problem.x = new Feature[documentCount][];
        problem.y = new double[documentCount];

        for (int i = 0; i<labels.size(); i++) {
            problem.y[i] = labels.get(i);
            problem.x[i] = featureVector.get(i);
        }

        return problem;
    }

    protected static Problem buildProblem(String trainingFile, Vector<FeatureExtractor> features) {
        resetLabelMappings();
        maxLabelId = -1;
        printFeatureStatistics(features);
        return buildProblem(trainingFile, features, featureOutputFile);
    }

    protected static Vector<Feature[]> applyFeatures(JCas cas, Vector<FeatureExtractor> features) {
        Vector<Feature[]> instanceFeatures = new Vector<>();
        for (FeatureExtractor feature : features) {
            instanceFeatures.add(feature.extractFeature(cas));
            // update the featureCount, the maximal Feature id
            featureCount = feature.getFeatureCount() + feature.getOffset();
        }
        return instanceFeatures;
    }

    protected static Feature[] combineInstanceFeatures(Vector<Feature[]> instanceFeatures) {
        int length = 0;
        for (Feature[] f : instanceFeatures) {
            length += f.length;
        }
        Feature[] instance = new Feature[length];
        int i=0;
        for (Feature[] fa : instanceFeatures) {
            for (Feature value : fa) {
                instance[i++] = value;
            }
        }
        return instance;
    }

    protected static void saveLabelMappings(String mappingFile) {
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(mappingFile), "UTF-8"));
            for (String label : labelMappings.keySet()) {
                out.write(labelMappings.get(label) + "\t" + label + "\n");
            }
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    protected static Double getLabelId(String label) {
        if (labelMappings.containsKey(label)) {
            return labelMappings.get(label).doubleValue();
        } else {
            labelMappings.put(label, ++maxLabelId);
            labelLookup.put(maxLabelId, label);
            return maxLabelId.doubleValue();
        }
    }

    protected static String getLabelString(Double labelId) {
        return labelLookup.get(labelId.intValue());
    }

    protected static void saveFeatureVectors(String featureVectorFile, Vector<Feature[]> featureVector, Vector<Double> labels) {
        if (featureVectorFile == null) {return;}
        try {
            Writer featureOut = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(featureVectorFile), "UTF-8"));
            for (int i = 0; i< labels.size(); i++) {
                featureOut.write(labels.get(i).toString());
                Feature[] features = featureVector.get(i);
                for (Feature f : features) {
                    featureOut.write(" " + f.getIndex() + ":" + f.getValue());
                }
                featureOut.write("\n");
            }
            featureOut.close();
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void printFeatureStatistics(Vector<FeatureExtractor> features) {
        if (featureStatisticsFile != null) {
            try {
                Writer statisticsOut = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(featureStatisticsFile), "UTF-8"));
                statisticsOut.write("training set: "+ trainFile + "\n");
                if (featureStatisticsFile != null) {
                    int start;
                    int end;
                    for (FeatureExtractor feature : features) {
                        start = feature.getOffset();
                        end = feature.getOffset() + feature.getFeatureCount();
                        statisticsOut.append(feature.getClass().getCanonicalName() + "\t" + start + "\t" + end + "\n");
                    }
                }
                statisticsOut.close();
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void resetLabelMappings() {
        labelMappings = new HashMap<>();
        labelLookup = new HashMap<>();
        maxLabelId = -1;
    }

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

        recall = confusionMatrix.getRecallForAllLabels();
        precision = confusionMatrix.getPrecisionForAllLabels();
        fMeasure = confusionMatrix.getFMeasureForAllLabels();

        System.out.println("Label"+"\t"+"Recall"+"\t"+"Precision"+"\t"+"F Score");
        for(String itemLabel: allLabels){
            System.out.println(itemLabel+"\t"+recall.get(itemLabel)+"\t"+precision.get(itemLabel)+"\t"+fMeasure.get(itemLabel));
        }

        printFeatureStatistics(features);
        confusionMatrix.printConfusionMatrix();
        System.out.println("\n");
        System.out.println("True positive     : " + confusionMatrix.getTruePositive());
        System.out.println("Accuracy          : " + confusionMatrix.getOverallAccuracy());
        System.out.println("Overall Precision : " + confusionMatrix.getOverallPrecision());
        System.out.println("Overall Recall    : " + confusionMatrix.getOverallRecall());
        System.out.println("Overall FMeasure  : " + confusionMatrix.getOverallFMeasure());
    }


    protected static void classifyTestSet(MultiLayerNetwork model, Problem problem){
        int batchSize = 40;
        int labelIndex = 0;
        int numClasses = 3;

        List<List<Double>> inputFeature = new ArrayList<>();
        for(int i=0;i<problem.l;i++){
            Feature[] array = problem.x[i];
            Double y = problem.y[i];
            ArrayList<Double> newArray = new ArrayList<>();
            newArray.add(y);
            System.out.println(y);
            int k = 0;
            for(int j=0;j<problem.n;j++){
                if(k<array.length){
                    if(array[k].getIndex()==j){
                        newArray.add(array[k++].getValue());
                    }else{
                        newArray.add(0.0);
                    }
                }
            }
            inputFeature.add(newArray);
        }

        RecordReader recordReader = new ListDoubleRecordReader();
        try {
            recordReader.initialize(new ListDoubleSplit(inputFeature));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, numClasses);

        Evaluation eval = new Evaluation(numClasses);
        DataSet ds = null;
        while(iterator.hasNext()){
            ds = iterator.next();
            INDArray output = model.output(ds.getFeatureMatrix());
            eval.eval(ds.getLabels(),output);
        }
        System.out.println(eval.stats());
    }
}