package tudarmstadt.lt.ABSentiment.classifier.relevance;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by eugen on 4/10/16.
 */
public class LinearRelevanceClassifier {


    HashMap<Integer, Double> termIdf = new HashMap<>();

    HashMap<String, Integer> tokenIds = new HashMap<>();


    HashMap<Integer, String> categoryMappings = new HashMap<>();

    Preprocessor preprocessor;
    int maxTokenId;

    double score;

    Model model;

    public LinearRelevanceClassifier() {

        String modelFile = "relevance-model.svm";
        String tfIdfMapping = "idfmap.tsv";

        String categoryMappingFile = "relevance-label-mappings.tsv";

        try {
            model = Linear.loadModel(new File(modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadWordList(tfIdfMapping);

        loadCategoryMappings(categoryMappingFile);

    }

    public String getCategory(String text) {

        HashMap<Integer, Integer> tokenCounts = new HashMap<>();

        preprocessor = new Preprocessor(text);
        Collection<String> documentText = preprocessor.getTokenStrings();
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
        Double w = 0.0;
        for (int i = 0; i < maxTokenId; i++) {

            w = termWeights.get(i);
            if (w == null) {
                w = 0.0;
            }
            normalizedWeight = w / norm;
            instance[i] = new FeatureNode(i + 1, normalizedWeight);
            //System.out.println(i + 1 + "\t" + normalizedWeight);
        }
        double[] prob_estimates = new double[model.getNrClass()];
        double prediction = Linear.predictProbability(model, instance, prob_estimates);

        for (int j = 0; j < model.getNrClass(); j++) {
            if (prob_estimates[j]*model.getNrClass() > 1.0) {

               // System.out.println(categoryMappings.get(Double.parseDouble(model.getLabels()[j] + "")) + "\t" + (prob_estimates[j]*model.getLabels().length));
                // System.out.println(j + "\t" + (prob_estimates[j] * model.getNrClass()));

            }
        }
        score = prob_estimates[Double.valueOf(prediction).intValue()];

        return categoryMappings.get(Double.valueOf(prediction).intValue());
    }

    public double getScore() {
        return score;
    }

    /**
     * Loads a word list with TF*IDF scores
     *
     * @param fileName the path and filename of the wordlist
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
            //logger.log(Level.SEVERE, "Could not load word list " + fileName + "!");
            e.printStackTrace();
        }
    }

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
            //logger.log(Level.SEVERE, "Could not load word list " + fileName + "!");
            e.printStackTrace();
        }
    }
}
