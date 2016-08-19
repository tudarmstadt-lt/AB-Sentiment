package tudarmstadt.lt.ABSentiment.training;

import tudarmstadt.lt.ABSentiment.featureExtractor.ComputeIdf;
import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.type.Document;

/**
 * Computes global IDF scores for a corpus
 */
public class ComputeIdfScores {

    /**
     * Computes global IDF scores from an input file and saves them in a file.
     * @param inputFile file containing the input corpus
     * @param outputFile path to the ouput file
     */
    public static void computeIdfScores(String inputFile, String outputFile) {
        ComputeIdf idf = new ComputeIdf();
        InputReader fr = new TsvReader(inputFile);

        for (Document d: fr) {
            idf.addDocument(d);
        }
        idf.saveIdfScores(outputFile);
    }
}
