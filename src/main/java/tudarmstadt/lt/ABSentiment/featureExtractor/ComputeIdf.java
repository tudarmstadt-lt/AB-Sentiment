package tudarmstadt.lt.ABSentiment.featureExtractor;

import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Computes the IDF Scores from a collection of {@link Document}s.
 * Scores can be stored in a file, e.g. to be used by the TF-IDF Feature {@link TfIdfFeature}
 */
public class ComputeIdf {

    private int documentCount;
    private int maxTokenId;

    private HashMap<Integer, Integer> documentFrequency;
    private Preprocessor preprocessor;
    private HashMap<String, Integer> tokenIds;

    /**
     * Constructor
     */
    public ComputeIdf() {
        documentFrequency  = new HashMap<>();
        preprocessor = new Preprocessor();
        tokenIds = new HashMap<>();
    }

    /**
     * Processes a {@link Document}, extracts tokens and increases their document frequency
     * @param d the Document that is added to the collection
     */
    public void addDocument(Document d) {
        documentCount++;
        preprocessor.processText(d.getDocumentText());
        List<String> documentTokens = preprocessor.getTokenStrings();
        HashSet<Integer> containedTokens = new HashSet<>();

        for (String token : documentTokens) {
            Integer tokenId = tokenIds.get(token);
            if (tokenId == null) {
                tokenId = ++maxTokenId;
                tokenIds.put(token, tokenId);
            }
            if (!containedTokens.contains(tokenId)) {
                containedTokens.add(tokenId);
                increaseDocumentCount(tokenId);
            }
        }
    }

    /**
     * Increases the document frequency for a token, identified by tokenID.
     * @param tokenID the Integer tokenId
     */
    private void increaseDocumentCount(Integer tokenID) {
        if (documentFrequency.containsKey(tokenID)) {
            documentFrequency.put(tokenID, documentFrequency.get(tokenID) + 1);
        } else {
            documentFrequency.put(tokenID, 1);
        }
    }

    /**
     * Saves the IDF scores in a tab-separated format:<br>
     * TOKEN  &emsp; TOKEN_ID &emsp; IDF-SCORE
     * @param idfFile path to the output file
     */
    public void saveIdfScores(String idfFile) {
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(idfFile), "UTF-8"));
            for (String token : tokenIds.keySet()) {
                int tokenId = tokenIds.get(token);
                double idfScore = Math.log(documentCount / documentFrequency.get(tokenId));
                out.write(token + "\t" + tokenId + "\t" + idfScore + "\n");
            }
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }
}
