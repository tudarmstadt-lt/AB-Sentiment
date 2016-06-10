package tudarmstadt.lt.ABSentiment.sentimentclassification;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * Created by eugen on 4/12/16.
 */
public class DictonarySentimentClassifier {

    private HashSet<String> wordList_p;
    private HashSet<String> wordList_n;


    public DictonarySentimentClassifier() {

        String filename_p = "/positive";
        String filename_n = "/negative";

        wordList_p = loadWordList(filename_p);
        wordList_n = loadWordList(filename_n);

    }

    public String getSentiment(String text) {
        int pos = 0;
        int neg = 0;

        for (String w : wordList_p) {
            if (text.contains(w)) {
                pos++;
            }
        }
        for (String w : wordList_n) {
            if (text.contains(w)) {
                neg++;
            }
        }

        if (pos > neg) {
            return "positive";
        }
        if (neg > pos) {
            return "negative";
        }

        return "neutral";

    }

    /**
     * Loads a word list
     *
     * @param fileName the path and filename of the wordlist
     * @return HashSet containing the words
     */
    private HashSet<String> loadWordList(String fileName) {
        HashSet<String> set = new HashSet<String>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream(fileName)));

            String line;
            while ((line = br.readLine()) != null) {
                set.add(line);
            }
            br.close();
        } catch (IOException e) {
            //logger.log(Level.SEVERE, "Could not load word list " + fileName + "!");
            e.printStackTrace();
            return null;
        }
        return set;
    }
}
