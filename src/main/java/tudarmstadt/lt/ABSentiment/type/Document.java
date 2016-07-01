package tudarmstadt.lt.ABSentiment.type;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by eugen on 7/1/16.
 */
public class Document {

    List<Sentence> sentences;
    String documentId;

    public Document() {
        this.sentences = new LinkedList<>();
    }

    public Document(String id) {
        this.documentId = id;
    }

    public void addSentence(Sentence s) {
        sentences.add(s);
    }

    public String getDocumentText() {
        StringBuilder sb = new StringBuilder();
        for (Sentence s : sentences) {
            sb.append(s.getText());
            sb.append(" ");
        }
        return sb.toString();
    }
}
