package tudarmstadt.lt.ABSentiment.type;

import tudarmstadt.lt.ABSentiment.featureExtractor.util.Pair;

import java.util.*;


public class Sentence {

    private String text = null;
    private String id = null;
    private String sentiment  = null;
    private String relevance = null;
    private ArrayList<Opinion> opinions;

    public Sentence() {
        opinions = new ArrayList<>();
    }

    public Sentence (String sentence) {
        this.text = sentence.trim();
        opinions = new ArrayList<>();
    }

    public String getText() {
        return text;
    }

    public void setText(String sentence) {
        this.text = sentence.trim();
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public void setRelevance(String relevance) {
        this.relevance = relevance;
    }

    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id.trim();
    }

    public void addOpinions(ArrayList<Opinion> opinions) {
        this.opinions.addAll(opinions);
    }

    public String[] getRelevance() {
        String[] ret = new String[1];
        ret[0] = relevance;
        return ret;
    }

    public String[] getAspectCategories() {
        String[] aspects = new String[opinions.size()];
        int i = 0;
        for (Opinion o: opinions) {
            try {
                aspects[i++] = o.getFineCategory();
            } catch (NoSuchFieldException e) {
            }
        }
        if(i == 0){
            aspects = new String[]{"0"};
        }
        return aspects;
    }


    public String[] getAspectCategoriesCoarse() {
        String[] aspects = new String[opinions.size()];
        int i = 0;
        for (Opinion o: opinions) {
            try {
                aspects[i++] = o.getCoarseCategory();
            } catch (NoSuchFieldException e) {
            }
        }
        if(i == 0){
            aspects = new String[]{"0"};
        }
        return aspects;
    }

    public Set<String> getTargets() {
        Set<String> targets = new HashSet<>();
        for (Opinion o: opinions) {
            targets.add(o.getTarget());
        }
        return targets;
    }


    public String[] getSentiment() throws NoSuchFieldException {
        ArrayList<String> sentiments = new ArrayList<>();
        if (sentiment != null && !sentiment.isEmpty()) {
            sentiments.add(sentiment);
        } else {
            for(Opinion opinion:opinions){
                try {
                    sentiments.add(opinion.getPolarity());
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        if(sentiments.size() == 0){
            throw new NoSuchFieldException("No Sentiment present");
        }
        return sentiments.toArray(new String[sentiments.size()]);
    }

    public Collection<? extends Pair<Integer,Integer>> getTargetOffsets() {

        List<Pair<Integer, Integer>> offsets = new ArrayList<>();
        for (Opinion opinion : opinions) {
            offsets.addAll(opinion.getTargets());
        }
        return offsets;
    }
}