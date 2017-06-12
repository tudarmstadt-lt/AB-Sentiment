package tudarmstadt.lt.ABSentiment.type;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


public class Sentence {

    private String text;
    private String id;
    private Vector<Opinion> opinions;

    public Sentence() {
        opinions = new Vector<>();
    }

    public Sentence (String sentence) {
        this.text = sentence.trim();
        opinions = new Vector<>();
    }

    public String getText() {
        return text;
    }

    public void setText(String sentence) {
        this.text = sentence.trim();
    }

    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id.trim();
    }

    public void addOpinions(Vector<Opinion> opinions) {
        this.opinions.addAll(opinions);
    }

    public String getSentiment() throws NoSuchFieldException {
        Set<String> polarities = new HashSet<>();
        for (Opinion o: opinions) {
            try {
                polarities.add(o.getPolarity());
            } catch (NoSuchFieldException e) {
            }
        }
        String r = new String();
        for (String p : polarities) {
            r += p + " ";
        }
        if (r.compareTo("") == 0) {
            throw new NoSuchFieldException("No Sentiment present");
        }
        return r.trim();
    }

    public String getAspectCategories() throws NoSuchFieldException {
        Set<String> aspects = new HashSet<>();
        for (Opinion o: opinions) {
            try {
                aspects.add(o.getFineCategory());
            } catch (NoSuchFieldException e) {
            }
        }
        String r = new String();
        for (String p : aspects) {
            r += p + " ";
        }
        if (r.compareTo("") == 0) {
            throw new NoSuchFieldException("No Aspect Category specified");
        }
        return r.trim();
    }

    public Set<String> getTargets() {
        Set<String> targets = new HashSet<>();
        for (Opinion o: opinions) {
           targets.add(o.getTarget());
        }
        return targets;
    }
}
