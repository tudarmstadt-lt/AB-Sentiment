package tudarmstadt.lt.ABSentiment.type;

import org.apache.uima.jcas.tcas.Annotation;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Document {

    List<Sentence> sentences;
    String documentId;
    String[] labels;

    public Document() {
        this.sentences = new LinkedList<>();
    }

    public Document(String id) {
        this.documentId = id;
        this.sentences = new LinkedList<>();
    }

    public void setDocumentId(String id) {
        this.documentId = id;
    }

    public void setLabels(String label) {
        labels = label.split(" ");
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
        return sb.toString().trim();
    }

    public String[] getLabels() {
        return labels;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getLabelsString() {
        StringBuilder sb = new StringBuilder();
        if (labels == null) {return "0";}
        for (String l: labels) {
            if (l != null) {
                sb.append(l).append(" ");
            }
        }
        return sb.toString().trim();
    }

    public String[] getLabelsCoarse() {
        String[] ret = new String[labels.length];

        for (int i=0; i<labels.length; i++) {
            ret[i] = extractCoarseCategory(labels[i]);
        }
        return ret;
    }

    public String getLabelsCoarseString() {
        StringBuilder sb = new StringBuilder();
        if (labels == null) {return "0";}
        for (String l: labels) {
            if (l != null) {
                sb.append(extractCoarseCategory(l)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private String extractCoarseCategory(String categoryFine) {
        if (categoryFine.indexOf('#') == -1) {
            return categoryFine;
        }
        return categoryFine.substring(0, categoryFine.indexOf('#'));
    }
}
