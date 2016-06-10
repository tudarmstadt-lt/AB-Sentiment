package tudarmstadt.lt.ABSentiment.aspecttermclassification.train;

import org.apache.uima.UIMAException;

/**
 * Created by eugen on 5/16/16.
 */


public class Train {


    public static void main(String[] args) throws UIMAException {

        FileReader fr = new FileReader();
        fr.processFile("/db-categories-train.tsv");

        fr.testFile("/db-categories-test.tsv");

    }

}