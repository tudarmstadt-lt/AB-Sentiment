package tudarmstadt.lt.ABSentiment;


import tudarmstadt.lt.ABSentiment.training.aspectclass.TestCoarse;

public class TestAllClassifiers {

    public static void main(String [] args) {
        tudarmstadt.lt.ABSentiment.training.relevance.Test.main(new String[0]);
        tudarmstadt.lt.ABSentiment.training.aspectclass.Test.main(new String[0]);
        TestCoarse.main(new String[0]);
        tudarmstadt.lt.ABSentiment.training.sentiment.Test.main(new String[0]);

        tudarmstadt.lt.ABSentiment.training.aspecttarget.Test.main(new String[0]);
    }
}
