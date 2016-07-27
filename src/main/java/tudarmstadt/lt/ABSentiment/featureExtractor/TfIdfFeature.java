package tudarmstadt.lt.ABSentiment.featureExtractor;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.uimahelper.Tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by eugen on 7/27/16.
 */
public class TfIdfFeature implements FeatureExtractor {

    private int maxTokenId;

    private HashMap<Integer, Double> termIdf = new HashMap<>();

    private HashMap<String, Integer> tokenIds = new HashMap<>();

    private HashMap<Integer, String> tokenStrings = new HashMap<>();

    private Tokenizer tokenizer = new Tokenizer();

    public TfIdfFeature(String idfFile) {
        loadIdfList(idfFile);

    }

    /**
     * Loads a word list with TF*IDF scores
     *
     * @param fileName the path and filename of the wordlist
     */
    private void loadIdfList(String fileName) {
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
                tokenStrings.put(tokenId, tokenLine[0]);
                termIdf.put(tokenId, Double.parseDouble(tokenLine[2]));
            }
            br.close();
        } catch (IOException e) {
            //logger.log(Level.SEVERE, "Could not load word list " + fileName + "!");
            e.printStackTrace();
        }
    }

    @Override
    public Feature[] extractFeature(Document document) {
        tokenizer.tokenizeString(document.getDocumentText());
        Collection<String> documentText = tokenizer.getTokens();


        HashMap<Integer, Integer> tokenCounts = getTokenCounts(documentText);
        return getTfIdfScores(tokenCounts);
    }

    private HashMap<Integer, Integer> getTokenCounts(Collection<String> documentText) {
        HashMap<Integer, Integer> tokenCounts = new HashMap<>();
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
        return tokenCounts;
    }

    public int getFeatureCount() {
        return maxTokenId;
    }



    private Feature[] getTfIdfScores(HashMap<Integer, Integer> tokenCounts) {
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

        Feature[] instance = new Feature[termWeights.size()];
        ArrayList<Integer> list = new ArrayList<>(termWeights.keySet());
        Collections.sort(list);
        Double w;
        int i =0;
        for (int tokenId: list) {

            w = termWeights.get(tokenId);
            if (w == null) {
                w = 0.0;
            }
            normalizedWeight = w / norm;
            instance[i++] = new FeatureNode(tokenId, normalizedWeight);
        }
        return instance;
    }
}
