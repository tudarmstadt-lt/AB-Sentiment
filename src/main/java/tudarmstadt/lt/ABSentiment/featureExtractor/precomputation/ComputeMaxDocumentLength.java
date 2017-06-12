package tudarmstadt.lt.ABSentiment.featureExtractor.precomputation;

import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.type.Document;

/**
 * Computes maximum document length for a corpus in TSV format
 */
public class ComputeMaxDocumentLength {

    /**
     * Computes the maximal document length for an input file and stores the result in a file.
     * @param inputFile file containing the input corpus
     * @param outputFile path to the output file which will contain the integer number
     */
    public static void computeMaxDocumentLength(String inputFile, String outputFile) {
        MaxDocumentLength ml = new MaxDocumentLength();
        InputReader fr = new TsvReader(inputFile);

        for (Document d: fr) {
            ml.addDocument(d);
        }
        ml.saveMaxLength(outputFile);
    }

}
