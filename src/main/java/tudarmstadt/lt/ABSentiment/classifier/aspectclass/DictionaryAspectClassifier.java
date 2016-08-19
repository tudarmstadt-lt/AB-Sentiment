package tudarmstadt.lt.ABSentiment.classifier.aspectclass;

import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.classifier.Classifier;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Baseline aspect classifier using a dictionary of aspect expressions.
 */
public class DictionaryAspectClassifier implements Classifier {

    HashMap<String, String> wordList;

    public DictionaryAspectClassifier() {
        String filename = "/dictionaries/aspect_categories";

        wordList = loadWordList(filename);
    }

    public String getLabel(String text) {

        return wordList.get(text);
    }

    /**
     * Loads a word list
     *
     * @param fileName the path and filename of the wordlist
     * @return HashSet containing the words
     */
    private HashMap<String, String> loadWordList(String fileName) {
        HashMap<String, String> set = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] split = line.split("\\t");
                    if (split.length >= 1) {
                        set.put(split[0], split[1]);
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            br.close();
        } catch (IOException e) {
            //logger.log(Level.SEVERE, "Could not load word list " + fileName + "!");
            e.printStackTrace();
        }
        return set;
    }

    @Override
    public String getLabel(JCas cas) {
        return null;
    }

    @Override
    public double getScore() {
        return 1.0;
    }

    @Override
    public double getScore(int i) {
        return 1.0;
    }

    @Override
    public String[] getLabels() {
        return new String[0];
    }

    @Override
    public double[] getScores() {
        return new double[0];
    }
}
