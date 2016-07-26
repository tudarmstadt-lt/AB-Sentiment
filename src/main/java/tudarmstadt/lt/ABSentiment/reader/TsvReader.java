package tudarmstadt.lt.ABSentiment.reader;

import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.type.Sentence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * Created by eugen on 7/1/16.
 */
public class TsvReader implements InputReader {

    private BufferedReader reader = null;
    private boolean checkedNext = false;
    private boolean hasNext = false;

    private Document currentDoc;
    private String line;


    public TsvReader(String filename) {
        try {
            reader = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream(filename)));

        } catch(Exception e) {
            System.err.println("File could not be opened: " +filename );
            e.printStackTrace();
            System.exit(1);
        }

    }



    @Override
    public Document next() {
        if (!checkedNext) {
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        checkedNext = false;
        currentDoc = buildDocument(line);
        return currentDoc;
    }

    @Override
    public boolean hasNext() {
        if (!checkedNext) {
            checkedNext = true;
            try {
                line = reader.readLine();
                hasNext = (line != null && !line.isEmpty());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hasNext;
    }

    private Document buildDocument(String line) {
        Document doc = new Document();

        String[] documentFields = line.split("\\t");
        if (documentFields.length < 2 || documentFields.length > 3) {
            throw new IllegalArgumentException("The document should at least have 2 fields, with an optional label in the 3rd field!0");
        }
        doc.setDocumentId(documentFields[0]);
        doc.addSentence(new Sentence(documentFields[1]));

        if (documentFields.length == 3) {
            doc.setLabel(documentFields[2]);
        }
        return doc;


    }

    @Override
    public Iterator<Document> iterator() {
        return this;
    }
}
