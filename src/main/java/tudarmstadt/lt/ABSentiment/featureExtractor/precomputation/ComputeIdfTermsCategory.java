package tudarmstadt.lt.ABSentiment.featureExtractor.precomputation;

import tudarmstadt.lt.ABSentiment.featureExtractor.TfIdfFeature;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.type.Sentence;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

/**
 * Relevance Model Trainer
 */
public class ComputeIdfTermsCategory extends ProblemBuilder {


    private static TreeMap<Double, HashMap<Integer, Integer>> termFrequencyLabel;
    private static TfIdfFeature idfScores;

    private static Preprocessor preprocessor = new Preprocessor(true);

    private static int maxEntries = 20;
    private static int minLength = 2;
    private static int minCount = 2;

    public static void computeIdfScores(String configurationFile, String inputFile, String outFile) {
        computeIdfScores(configurationFile ,inputFile, outFile, false);
    }

    public static void computeIdfScores(String configurationFile, String inputFile, String outFile, boolean useCoarseLabels) {
        computeIdfScores(configurationFile, inputFile, outFile, useCoarseLabels, null);
    }

    public static void computeIdfScores(String configurationFile,String inputFile, String outFile, boolean useCoarseLabels, String type) {
        initialise(configurationFile);
        if(idfScores!=null){
            idfScores = new TfIdfFeature(idfFile);
        }else{
            idfScores = new TfIdfFeature("data/de/feature/idfmap.tsv.gz");
        }
        termFrequencyLabel = new TreeMap<>();
        resetLabelMappings();

        InputReader in = new TsvReader(inputFile);
        String[] labels = null;
        HashMap<Integer, Integer> tokenCounts = null;
        for (Document doc: in) {
            for(Sentence sentence:doc.getSentences()){
                preprocessor.processText(sentence.getText());
                Collection<String> documentText = preprocessor.getTokenStrings(preprocessor.getCas());
                tokenCounts = idfScores.getTokenCounts(documentText);
                if (type == null){
                    labels = sentence.getAspectCategories();
                    if (useCoarseLabels) {
                        labels = sentence.getAspectCategoriesCoarse();
                    }
                } else if (type.compareTo("relevance") == 0) {
                    labels = sentence.getRelevance();
                } else if (type.compareTo("sentiment") == 0) {
                    try {
                        labels = sentence.getSentiment();
                    } catch (NoSuchFieldException e) {
                    }
                } else if (type.compareTo("aspect") == 0){
                    labels = sentence.getAspectCategories();
                    if (useCoarseLabels) {
                        labels = sentence.getAspectCategoriesCoarse();
                    }
                }

            }
            for (String label : labels) {
                addDocument(tokenCounts, label);
            }
        }
        saveIdfTerms(outFile);
    }

    /**
     * Setter Method for the minimum token length to be considered as candidates, default 2.
     * @param length the new minimum  token length
     */
    public void setMinLength(int length) {
        if (length >= 1){
            this.minLength = length;
        }
    }


    private static void saveIdfTerms(String fileName) {
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), "UTF-8"));

            for (Double labelId : termFrequencyLabel.keySet()) {

                HashMap<Integer, Double> results = new HashMap<>();
                HashMap<Integer, Integer> counts = termFrequencyLabel.get(labelId);

                for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
                    double idfscore = idfScores.getIdfScore(e.getKey());
                    results.put(e.getKey(),idfscore * e.getValue());
                }

                ValueComparator bvc = new ValueComparator(results);
                TreeMap<Integer, Double> sorted_map = new TreeMap<>(bvc);
                sorted_map.putAll(results);

                int i = 0;
                String word;
                for (Map.Entry<Integer, Double> e : sorted_map.entrySet()) {

                    word = idfScores.getWordString(e.getKey());

                    if (i < maxEntries && e.getValue() > 0.5 && !word.isEmpty() && word.length() > minLength && counts.get(e.getKey())>= minCount) {
                        out.write(idfScores.getWordString(e.getKey()) + "\t" + e.getKey() + "\t" + e.getValue() +
                                "\t" +labelId + "\t" + getLabelString(labelId) +"\n");
                        i++;
                    }
                }
            }
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    private static void addDocument(HashMap<Integer, Integer> tokenCounts, String label) {
        Double labelId = getLabelId(label);
        HashMap<Integer, Integer> counts = termFrequencyLabel.get(labelId);
        if (counts == null) {
            termFrequencyLabel.put(labelId, tokenCounts);
        } else {
            for (Map.Entry<Integer, Integer> e : tokenCounts.entrySet()) {
                if (counts.get(e.getKey()) != null) {
                    counts.put(e.getKey(), e.getValue()+tokenCounts.get(e.getKey()));
                } else {
                    counts.put(e.getKey(), e.getValue());
                }
                termFrequencyLabel.put(labelId, counts);
            }
        }
    }

}

class ValueComparator implements Comparator<Integer> {
    private Map<Integer, Double> base;

    ValueComparator(Map<Integer, Double> base) {
        this.base = base;
    }

    public int compare(Integer a, Integer b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}