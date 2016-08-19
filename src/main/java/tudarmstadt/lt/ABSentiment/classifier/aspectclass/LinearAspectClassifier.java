package tudarmstadt.lt.ABSentiment.classifier.aspectclass;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.classifier.Classifier;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.io.*;
import java.util.*;

public class LinearAspectClassifier implements Classifier {


    private HashMap<Integer, Double> termIdf = new HashMap<>();
    private HashMap<String, Integer> tokenIds = new HashMap<>();

    private HashMap<Integer, String> categoryMappings = new HashMap<>();

    private int maxTokenId;

    private Model model;
    private Preprocessor preprocessor;

    private double[] probEstimates;
    private Vector<Double> predictions;
    private double score;

    public LinearAspectClassifier(String modelFile) {
        preprocessor = new Preprocessor();

        String tfIdfMappingFile = "idfmap.tsv";
        String categoryMappingFile = "aspect-label-mappings.tsv";

        try {
            model = Linear.loadModel(new File(modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadWordList(tfIdfMappingFile);
        loadCategoryMappings(categoryMappingFile);

    }

    public String getLabel(JCas cas) {
        predictions = new Vector<>();

        HashMap<Integer, Integer> tokenCounts = new HashMap<>();

        Collection<String> documentText = preprocessor.getTokenStrings(cas);
        for (String token : documentText) {
            if (token == null) {
                continue;
            }

            Integer tokenId = tokenIds.get(token);
            if (tokenId == null) {
                continue;
            }

            if (tokenCounts.get(tokenId) != null) {
                tokenCounts.put(tokenId, tokenCounts.get(tokenId) + 1);
            } else {
                tokenCounts.put(tokenId, 1);
            }
        }
        // create new document
        int count;
        double idf;
        double weight;
        double normalizedWeight;
        double norm = 0;

        HashMap<Integer, Double> termWeights = new HashMap<>();
        for (int tokenID : tokenCounts.keySet()) {
            count = tokenCounts.get(tokenID);
            idf = termIdf.get(tokenID);
            weight = count * idf;

            if (weight > 0.0) {
                norm += Math.pow(weight, 2);
                termWeights.put(tokenID, weight);
            }
        }
        norm = Math.sqrt(norm);

        Feature[] instance = new Feature[maxTokenId];
        ArrayList<Integer> list = new ArrayList<>(termWeights.keySet());
        Collections.sort(list);
        Double w;
        for (int i = 0; i < maxTokenId; i++) {

            w = termWeights.get(i);
            if (w == null) {
                w = 0.0;
            }
            normalizedWeight = w / norm;
            instance[i] = new FeatureNode(i + 1, normalizedWeight);
        }
        probEstimates = new double[model.getNrClass()];
        double prediction = Linear.predictProbability(model, instance, probEstimates);

        predictions.setSize(model.getNrClass());
        for (int j = 0; j < model.getNrClass(); j++) {
            if (probEstimates[j]*model.getNrClass() > 1.0) {
                predictions.add(j, probEstimates[j]);
            }
        }
        score = probEstimates[Double.valueOf(prediction).intValue()];

        return categoryMappings.get(Double.valueOf(prediction).intValue());
    }

    public double getScore() {
        return score;
    }

    public String getAspectLabel(int i) {
        return categoryMappings.get(i);
    }

    public Vector<Double> getPredictions() {
        return predictions;
    }

    /**
     * Loads a word list
     *
     * @param fileName the path and filename of the
     */
    private void loadWordList(String fileName) {
        maxTokenId = 0;
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

            String line;
            while ((line = br.readLine()) != null) {
                String[] tokenLine = line.split("\\t");
                int tokenId = Integer.parseInt(tokenLine[1]);
                if (tokenId > maxTokenId) {
                    maxTokenId = tokenId;
                }
                tokenIds.put(tokenLine[0], tokenId);
                termIdf.put(tokenId, Double.parseDouble(tokenLine[2]));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public double getScore(int i) {
        return probEstimates[i];
    }

    @Override
    public String[] getLabels() {
        return new String[0];
    }

    @Override
    public double[] getScores() {
        return probEstimates;
    }

    /**
     * Loads a word list with category mappings
     *
     * @param fileName the path and filename of the cat
     */
    private void loadCategoryMappings(String fileName) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] catLine = line.split("\\t");
                int categoryId = Integer.parseInt(catLine[0]);
                categoryMappings.put(categoryId, catLine[1]);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
