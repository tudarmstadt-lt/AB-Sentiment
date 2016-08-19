package tudarmstadt.lt.ABSentiment.aspecttermextraction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DictionaryAspectExtractor {

    HashSet<String> wordList;

    public DictionaryAspectExtractor() {


        String filename = "/dictionaries/aspects";

        wordList = loadWordList(filename);


    }

    public List<String> getTerms(String text) {
        List<String> ret = new ArrayList<String>();

        for (String t : wordList) {
            if (text.contains(t)) {
                ret.add(t);
            }
        }
        return ret;
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
