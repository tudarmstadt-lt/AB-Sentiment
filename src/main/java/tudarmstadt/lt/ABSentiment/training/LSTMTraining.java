package tudarmstadt.lt.ABSentiment.training;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import org.apache.uima.resource.metadata.Import;
import org.datavec.api.conf.Configuration;
import org.datavec.api.records.Record;
import org.datavec.api.records.listener.RecordListener;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.collection.ListStringRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.CollectionInputSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.api.split.ListStringSplit;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tudarmstadt.lt.ABSentiment.training.util.ListDoubleRecordReader;
import tudarmstadt.lt.ABSentiment.training.util.ListDoubleSplit;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LSTMTraining{

    public MultiLayerNetwork trainModel(Problem problem){

        int numEpochs = 20;
        int batchSize = 200;
        int labelIndex = 0;
        int numClasses = 3;

        List<List<Double>> inputFeature = new ArrayList<>();
        for(int i=0;i<problem.l;i++){
            Feature[] array = problem.x[i];
            Double y = problem.y[i];
            ArrayList<Double> newArray = new ArrayList<>();
            newArray.add(y);
            int k = 0;
            for(int j=0;j<problem.n;j++){
                if(k<array.length){
                    if(array[k].getIndex()==j){
                        newArray.add(array[k++].getValue());
                    }else{
                        newArray.add(0.0);
                    }
                }
            }
            inputFeature.add(newArray);
        }
        RecordReader recordReader = new ListDoubleRecordReader();
        try {
            recordReader.initialize(new ListDoubleSplit(inputFeature));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, numClasses);

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1)
                .learningRate(0.1)
                .rmsDecay(0.95)
                .seed(12345)
                .regularization(true)
                .l2(0.001)
                .weightInit(WeightInit.XAVIER)
                .updater(Updater.ADAM)
                .list()
                .layer(0, new GravesLSTM.Builder().nIn(problem.n).nOut(600).forgetGateBiasInit(1.0)
                        .activation(Activation.LEAKYRELU).build())
                .layer(1, new GravesLSTM.Builder().nIn(600).nOut(600).forgetGateBiasInit(1.0)
                        .activation(Activation.LEAKYRELU).build())
                .layer(2, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT).activation(Activation.SOFTMAX)
                        .nIn(600).nOut(numClasses).build())
                .backpropType(BackpropType.Standard)
                .pretrain(false).backprop(true)
                .build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(1));
        DataSet ds = null;
        for( int i=0; i<numEpochs; i++ ){
            while(iterator.hasNext()){
                ds = iterator.next();
                model.fit(ds);
            }
            iterator.reset();
        }

        Evaluation eval = new Evaluation(numClasses);
        INDArray output = model.output(ds.getFeatureMatrix());
        eval.eval(ds.getLabels(), output);
        System.out.println(eval.stats());
        return model;
    }

    public void saveModel(MultiLayerNetwork model, String modelFile, boolean saveUpdater) {
        File locationToSave = new File(modelFile+".zip");
        try {
            ModelSerializer.writeModel(model, locationToSave, saveUpdater);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
