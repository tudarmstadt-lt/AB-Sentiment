/*
 * ******************************************************************************
 *  Copyright 2016
 *  Copyright (c) 2016 Technische Universität Darmstadt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ****************************************************************************
 */

package tudarmstadt.lt.ABSentiment.classifier;

import de.bwaldvogel.liblinear.Feature;
import org.apache.uima.jcas.JCas;
import org.datavec.api.records.reader.RecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.json.JSONException;
import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor;
import tudarmstadt.lt.ABSentiment.training.util.ListDoubleRecordReader;
import tudarmstadt.lt.ABSentiment.training.util.ListDoubleSplit;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


 /**
 * The DNN Classifier provides common methods for classification using neural networks
 * Created by abhishek on 20/5/17.
 */
public class DNNClassifier extends ProblemBuilder implements Classifier{

    protected MultiLayerNetwork model;

    protected Vector<FeatureExtractor> features;

    protected String label;
    protected int labelIndex;
    protected double score;

    @Override
    public String getLabel(JCas cas) {
        int inputNode=0, outputNode=0;
        Vector<Feature[]> instanceFeatures = applyFeatures(cas, features);
        Feature[] instance = combineInstanceFeatures(instanceFeatures);
        JSONObject jsonInputLayer = null;
        try {
            jsonInputLayer = new JSONObject(model.conf().toJson());
            inputNode = Integer.parseInt(jsonInputLayer.getJSONObject("layer").getJSONObject("gravesLSTM").get("nin").toString());
            JSONObject jsonOutputLayer = new JSONObject(model.getOutputLayer().conf().toJson());
            outputNode = Integer.parseInt(jsonOutputLayer.getJSONObject("layer").getJSONObject("rnnoutput").get("nout").toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }List<List<Double>> inputFeature = new ArrayList<>();
            Feature[] array = instance;
            Double y = 0.0;
            ArrayList<Double> newArray = new ArrayList<>();
            newArray.add(y);
            int k = 0;
            for(int j=0;j<inputNode;j++){
                if(k<array.length){
                    if(array[k].getIndex()==j){
                        newArray.add(array[k++].getValue());
                    }else{
                        newArray.add(0.0);
                    }
                }
            }
            inputFeature.add(newArray);

        RecordReader recordReader = new ListDoubleRecordReader();
        try {
            recordReader.initialize(new ListDoubleSplit(inputFeature));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, 1, 0, outputNode);
        DataSet ds = iterator.next();
        INDArray output = model.output(ds.getFeatureMatrix());
        score = Double.parseDouble(output.maxNumber().toString());
        labelIndex = (int)Double.parseDouble(Nd4j.argMax(output).toString());
        HashMap<Integer, String> labelHashMap;
        labelHashMap = loadLabelMapping(labelMappingsFileSentiment);
        label = labelHashMap.get(labelIndex);
        return label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public double getScore() {
        return score;
    }

    protected HashMap<Integer, String> loadLabelMapping(String fileName) {
        HashMap<Integer, String> lMap = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

            String line;
            while ((line = br.readLine()) != null) {
                String[] catLine = line.split("\\t");
                int labelId = Integer.parseInt(catLine[0]);
                lMap.put(labelId, catLine[1]);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return lMap;
    }
}