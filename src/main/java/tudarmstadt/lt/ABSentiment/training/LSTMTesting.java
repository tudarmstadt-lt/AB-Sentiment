package tudarmstadt.lt.ABSentiment.training;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import java.io.File;
import java.io.IOException;

/**
 * Created by abhishek on 19/5/17.
 */
public class LSTMTesting {
    public MultiLayerNetwork loadModel(String modelFile) {
        MultiLayerNetwork model = null;
        File locationToSave = new File(modelFile+".zip");
        try {
            model = ModelSerializer.restoreMultiLayerNetwork(locationToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }
}
