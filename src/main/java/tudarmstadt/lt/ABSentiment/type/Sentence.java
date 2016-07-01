package tudarmstadt.lt.ABSentiment.type;

import java.util.Vector;

/**
 * Created by eugen on 7/1/16.
 */
public class Sentence {

    String text;
    Vector<Opinion> opinions;

    public Sentence() {

    }

    public Sentence (String sentence) {
        this.text = sentence.trim();
    }

    public String getText() {
        return text;
    }
}
