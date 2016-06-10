package tudarmstadt.lt.ABSentiment.aspecttermclassification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by eugen on 4/13/16.
 */
public class DictionaryAspectClassifier {

    HashMap<String, String> wordList;

    public DictionaryAspectClassifier() {


        String filename = "/aspect_categories";

        wordList = loadWordList(filename);


    }

    public String getCategory(String text) {

        return wordList.get(text);
    }

    /**
     * Loads a word list
     *
     * @param fileName the path and filename of the wordlist
     * @return HashSet containing the words
     */
    private HashMap<String, String> loadWordList(String fileName) {
        HashMap<String, String> set = new HashMap<String, String>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream(fileName)));

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
            //return null;
        }
        return set;
    }
}
