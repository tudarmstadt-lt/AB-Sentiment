package tudarmstadt.lt.ABSentiment;

import java.io.File;

public class TrainAllClassifiers {

    public static void main(String [] args) {

        tudarmstadt.lt.ABSentiment.training.relevance.Train.main(args);
        tudarmstadt.lt.ABSentiment.training.aspectclass.Train.main(args);
        tudarmstadt.lt.ABSentiment.training.aspectclass.TrainCoarse.main(args);
        tudarmstadt.lt.ABSentiment.training.sentiment.Train.main(args);

        if (args.length > 0 && args[0].endsWith(".xml")) {
            tudarmstadt.lt.ABSentiment.training.aspecttarget.Train.main(args);
        }

        // remove temporary CRF data
        File model = new File("data/models/crfsuite.model");
        model.deleteOnExit();
        File trainData = new File("data/models/crfsuite.training");
        trainData.deleteOnExit();
        File encoders = new File("data/models/encoders.ser");
        encoders.deleteOnExit();
        File manifest = new File("data/models/MANIFEST.MF");
        manifest.deleteOnExit();
    }
}
