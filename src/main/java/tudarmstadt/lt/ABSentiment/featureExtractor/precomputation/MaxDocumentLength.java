package tudarmstadt.lt.ABSentiment.featureExtractor.precomputation;

import tudarmstadt.lt.ABSentiment.featureExtractor.DocumentLengthFeature;
import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.GZIPOutputStream;

/**
 * Computes the maximal document length from a collection of {@link Document}s.
 * This length can be used by the {@link DocumentLengthFeature}
 */
public class MaxDocumentLength {

    private int maxLength = 1;

    private Preprocessor preprocessor;

    /**
     * Constructor
     */
    public MaxDocumentLength() {
        preprocessor = new Preprocessor(true);
    }



    /**
     * Processes a {@link Document}, extracts tokens and increases their document frequency
     * @param d the Document that is added to the collection
     */
    public void addDocument(Document d) {
        preprocessor.processText(d.getDocumentText());

        int documentLength = preprocessor.getTokenStrings().size();
        if (documentLength > maxLength) {
            maxLength = documentLength;
        }

    }


    /**
     * Saves the IDF scores in a tab-separated format:<br>
     * TOKEN  &emsp; TOKEN_ID &emsp; IDF-SCORE &emsp; FREQUENCY
     * @param idfFile path to the output file
     */
    protected void saveMaxLength(String idfFile) {
        try {
            Writer out;
            if (idfFile.endsWith(".gz")) {
                out = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(idfFile)), "UTF-8");
            } else {
                out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(idfFile), "UTF-8"));
            }
            out.write(maxLength + "\n");
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }
}
