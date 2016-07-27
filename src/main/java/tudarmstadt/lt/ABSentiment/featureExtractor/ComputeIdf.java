package tudarmstadt.lt.ABSentiment.featureExtractor;

import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.uimahelper.Tokenizer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by eugen on 7/27/16.
 */
public class ComputeIdf {

    private int documentCount;
    private int maxTokenId;

    private Tokenizer tokenizer;

    private HashMap<Integer, Integer> documentFrequency;
    HashMap<String, Integer> tokenIds = new HashMap<>();


    public ComputeIdf() {
        tokenizer = new Tokenizer();
        documentFrequency  = new HashMap<>();
    }


    public void addDocument(Document d) {
        documentCount++;
        tokenizer.tokenizeString(d.getDocumentText());
        List<String> documentTokens = tokenizer.getTokens();
        HashSet<Integer> containedTokens = new HashSet<>();

        for (String token : documentTokens) {
            Integer tokenId = tokenIds.get(token);
            if (tokenId == null) {
                tokenId = ++maxTokenId;
                tokenIds.put(token, tokenId);
            }
            if (!containedTokens.contains(tokenId)) {
                containedTokens.add(tokenId);
                addCounts(tokenId);
            }
        }

    }

    private void addCounts(Integer tokenID) {
        if (documentFrequency.containsKey(tokenID)) {
            documentFrequency.put(tokenID, documentFrequency.get(tokenID) + 1);
        } else {
            documentFrequency.put(tokenID, 1);
        }
    }

    public void storeIdfScores(String idfFile) {
        try {
            FileWriter fstream = new FileWriter(idfFile);
            BufferedWriter out = new BufferedWriter(fstream);
            for (String token : tokenIds.keySet()) {
                int tokenId = tokenIds.get(token);
                double idfScore = Math.log(documentCount / documentFrequency.get(tokenId));
                out.write(token + "\t" + tokenId + "\t" + idfScore + "\n");
            }
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

    }
}
