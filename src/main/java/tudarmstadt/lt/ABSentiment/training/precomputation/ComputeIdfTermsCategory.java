package tudarmstadt.lt.ABSentiment.training.precomputation;

import tudarmstadt.lt.ABSentiment.featureExtractor.TfIdfFeature;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.type.Document;
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
    private static TfIdfFeature idfScores = new TfIdfFeature(idfFile);

    private static Preprocessor preprocessor = new Preprocessor();

    private static int maxEntries = 20;


    public static void computeIdfScores(String inputFile, String outFile) {
       computeIdfScores(inputFile, outFile, false);
    }

    public static void computeIdfScores(String inputFile, String outFile, boolean useCoarseLabels) {
        termFrequencyLabel = new TreeMap<>();

        InputReader in = new TsvReader(inputFile);

        for (Document d: in) {
            preprocessor.processText(d.getDocumentText());
            Collection<String> documentText = preprocessor.getTokenStrings(preprocessor.getCas());
            HashMap<Integer, Integer> tokenCounts = idfScores.getTokenCounts(documentText);

            String[] labelIds = d.getLabels();
            if (useCoarseLabels) {
                d.getLabelsCoarse();
            }
            for (String label : labelIds) {
                addDocument(tokenCounts, label);
            }

        }

        saveIdfTerms(outFile);

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
                for (Map.Entry<Integer, Double> e : sorted_map.entrySet()) {
                    if (i++ < maxEntries) {
                        out.write(idfScores.getWordString(e.getKey()) + "\t" + e.getKey() + "\t" + e.getValue() +
                                "\t" +labelId + "\t" + getLabelString(labelId) +"\n");
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

    public ValueComparator(Map<Integer, Double> base) {
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