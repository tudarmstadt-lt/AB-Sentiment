package tudarmstadt.lt.ABSentiment.type;

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
        opinions.addAll(opinions);
    }
}
