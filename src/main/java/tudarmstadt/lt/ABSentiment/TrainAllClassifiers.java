package tudarmstadt.lt.ABSentiment;


import tudarmstadt.lt.ABSentiment.training.aspectclass.TrainCoarse;

import java.io.File;

public class TrainAllClassifiers {

    public static void main(String [] args) {

        tudarmstadt.lt.ABSentiment.training.relevance.Train.main(new String[0]);
        tudarmstadt.lt.ABSentiment.training.aspectclass.Train.main(new String[0]);
        TrainCoarse.main(new String[0]);
        tudarmstadt.lt.ABSentiment.training.sentiment.Train.main(new String[0]);

        tudarmstadt.lt.ABSentiment.training.aspecttarget.Train.main(new String[0]);

        // remove temporary CRF data
        File model = new File("data/models/crfsuite.model");
        model.deleteOnExit();
        File encoders = new File("data/models/encoders.ser");
        encoders.deleteOnExit();
        File manifest = new File("data/models/MANIFEST.MF");
        manifest.deleteOnExit();

    }
}
