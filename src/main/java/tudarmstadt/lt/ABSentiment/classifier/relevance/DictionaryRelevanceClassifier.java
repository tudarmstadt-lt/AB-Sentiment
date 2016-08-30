package tudarmstadt.lt.ABSentiment.classifier.relevance;

import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.classifier.Classifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * Baseline relevance classifier using a dictionary terms that indicate irrelevance.
 */
public class DictionaryRelevanceClassifier implements Classifier {

    private HashSet<String> wordList;

    public DictionaryRelevanceClassifier() {

        String filename = "/dictionaries/non-relevant";

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

    @Override
    public String getLabel(JCas cas) {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public double getScore() {
        return 0;
    }

}