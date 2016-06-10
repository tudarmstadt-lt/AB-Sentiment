package tudarmstadt.lt.ABSentiment.relevancescoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * Created by eugen on 4/10/16.
 */
public class DictionaryRelevanceScorer {

    private HashSet<String> wordList;

    public DictionaryRelevanceScorer() {

        String filename = "/non-relevant";

        wordList = loadWordList(filename);

    }

    public double getScore(String text) {

        for (String w : wordList) {
            if (text.contains(w)) {
                return -1.0;
            }
        }

        return 1.0;

    }

    /**
     * Loads a word list
     *
     * @param fileName the path and filename of the wordlist
     * @return HashSet containing the words
     */
    private HashSet<String> loadWordList(String fileName) {
        HashSet<String> set = new HashSet<>();
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
