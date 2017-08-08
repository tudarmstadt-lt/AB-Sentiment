package tudarmstadt.lt.ABSentiment.type;

import tudarmstadt.lt.ABSentiment.featureExtractor.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Document {

    private ArrayList<Sentence> sentences;
    private ArrayList<Opinion> opinions = null;
    private String documentId;

    public Document() {
        this.sentences = new ArrayList<>();
    }

    public Document(String id) {
        this();
        this.documentId = id;
    }

    public void setDocumentId(String id) {
        this.documentId = id;
    }

    public void addSentence(Sentence s) {
        sentences.add(s);
    }

    public void addOpinions(Opinion opinion){
        if ( opinions == null){
            opinions = new ArrayList<>();
        }
        opinions.add(opinion);
    }

    public List<Opinion> getOpinions(){ return this.opinions;}

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

//    public String getLabelsCoarseString() {
//        StringBuilder sb = new StringBuilder();
//        if (labels == null && sentences.get(0).getAspectCategories().length == 0) {return "0";}
//        if (labels != null) {
//
//            for (String l: labels) {
//                if (l != null) {
//                    sb.append(extractCoarseCategory(l)).append(" ");
//                }
//            }
//        } else {
//            for (Sentence sen : sentences) {
//                for (String s : sen.getAspectCategoriesCoarse()) {
//
//                    sb.append(extractCoarseCategory(s)).append(" ");
//                }
//            }
//        }
//        return sb.toString().trim();
//    }
//
//    private String extractCoarseCategory(String categoryFine) {
//        if (categoryFine.indexOf('#') == -1) {
//            return categoryFine;
//        }
//        return categoryFine.substring(0, categoryFine.indexOf('#'));
//    }

    public List<Sentence> getSentences() {
        return this.sentences;
    }

    public String[] getDocumentSentiment() {
        ArrayList<String> sentiments = new ArrayList<>();
        for(Sentence sentence:sentences){
            try {
                for(String sentiment: sentence.getSentiment()){
                    sentiments.add(sentiment);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return sentiments.toArray(new String[sentiments.size()]);
    }

    public List<Pair<Integer, Integer>> getTargetOffsets() {
        List<Pair<Integer, Integer>> ret = new ArrayList<>();
        for (Sentence s : sentences) {
            ret.addAll(s.getTargetOffsets());
        }
        return ret;
    }

}