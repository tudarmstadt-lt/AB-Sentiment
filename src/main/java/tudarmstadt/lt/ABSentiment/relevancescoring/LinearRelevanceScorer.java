package tudarmstadt.lt.ABSentiment.relevancescoring;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import tudarmstadt.lt.ABSentiment.uimahelper.Tokenizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by eugen on 4/10/16.
 */
public class LinearRelevanceScorer {


    HashMap<Integer, Double> termIdf = new HashMap<>();

    HashMap<String, Integer> tokenIds = new HashMap<>();

    Tokenizer tokenizer = new Tokenizer();
    int maxTokenId;

    Model model;

    public LinearRelevanceScorer() {

        String modelFile = "relevance-model.svm";
        String tfIdfMapping = "/relevance-tf-idf.tsv";

        try {
            model = Linear.loadModel(new File(modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadWordList(tfIdfMapping);

    }

    public double getScore(String text) {

        HashMap<Integer, Integer> tokenCounts = new HashMap<>();

        tokenizer.tokenizeString(text);
        Collection<String> documentText = tokenizer.getTokens();
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

        Linear.enableDebugOutput();
        double prediction = Linear.predict(model, instance);
        return prediction;
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
                    new InputStreamReader(this.getClass().getResourceAsStream(fileName)));

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
}
