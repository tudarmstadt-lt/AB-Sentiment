package tudarmstadt.lt.ABSentiment.type;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Document {

    private List<Sentence> sentences;
    private List<Opinion> opinions;
    private String documentId;
    private String[] labels = new String[0];
    private String relevance;
    private String sentiment;

    public Document() {
        this.sentences = new LinkedList<>();
        this.opinions = new LinkedList<>();
    }

    public Document(String id) {
        this();
        this.documentId = id;
    }

    public void setDocumentId(String id) {
        this.documentId = id;
    }

    public void setLabels(String label) {
        label = label.replaceAll("  ", " ").trim();
        labels = label.split(" ");
    }

    public void setDocumentAspects(String label) {
        label = label.replaceAll("  ", " ").trim();
        String[] labelsU= label.split(" ");
        labels = new String[labelsU.length];
        int i = 0;
        for (String l: labelsU) {
            labels[i++] = l.substring(0, l.lastIndexOf(":"));
        }
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

    public String getDocumentId() {
        return documentId;
    }

    public String[] getLabels() {
        return labels;
    }

    public String getLabelsString() {
        StringBuilder sb = new StringBuilder();
        if (labels == null || labels.length == 0) {return "0";}
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

    public List<Sentence> getSentences() {
        return this.sentences;
    }


    public void setRelevance(String relevance) {
        this.relevance = relevance;
    }

    public String[] getRelevance() {
        String[] ret = new String[1];
        ret[0] = relevance;
        return ret;
    }

    public void setSentiments(String docSent) {
        Opinion op;
        if (opinions.isEmpty()) {
            op = new Opinion(null, docSent);
        } else {
            op = opinions.get(0);
            op.setPolarity(docSent);
        }
        opinions.add(0, op);
    }

    public void setDocumentSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String[] getDocumentSentiment() {
        String[] sentiments = new String[1];
        if (sentiment != null && !sentiment.isEmpty()) {
            sentiments[0] = sentiment;
        } else {
            sentiments = new String[opinions.size()];
            int i = 0;
            for (Opinion op: opinions) {
                try {
                    sentiments[i++] = op.getPolarity();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        return sentiments;
    }


}
