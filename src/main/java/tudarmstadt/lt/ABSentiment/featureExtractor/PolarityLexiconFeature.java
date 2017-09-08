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

package tudarmstadt.lt.ABSentiment.featureExtractor;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * PolarityLexiconFeature class helps to extract polarity lexicon feature based on the polarity score of each token
 * Created by abhishek on 11/7/17.
 */
public class PolarityLexiconFeature implements FeatureExtractor{

    private int offset = 0;

    private HashMap<String, Float[]> termPolarityMap = new HashMap<>();
    private int totalNumberOfLabels;
    private int featureCount;
    private int numberOfTokensFound;

    private Preprocessor preprocessor = new Preprocessor(true);

    /**
     * Constructor; specifies the polarity lexicon file. Feature offset is set to '0' by default.
     * @param termPolarityFile path to a file containing word with their polarity vector
     */
    public PolarityLexiconFeature(String termPolarityFile) {
        System.out.println("Polarity Lexicon: loading " + termPolarityFile + "...");
        loadPolarityFile(termPolarityFile);
    }

    /**
     * Constructor; specifies the polarity lexicon file. Feature offset is specified.
     * @param termPolarityFile path to a file containing word with their polarity vector
     */
    public PolarityLexiconFeature(String termPolarityFile, int offset) {
        this(termPolarityFile);
        this.offset = offset;
    }

    @Override
    public Feature[] extractFeature(JCas cas) {
        Collection<String> documentText = preprocessor.getTokenStrings(cas);
        float featureVector[] = new float[featureCount];
        Feature[] features = new Feature[featureCount];
        for(int i = 0;i<featureVector.length;i++){
            featureVector[i] = 0.0f;
        }
        numberOfTokensFound = 0;
        for(String token:documentText){
            if(termPolarityMap.containsKey(token)){
                Float polarity[] = termPolarityMap.get(token);
                for(int i=0;i<totalNumberOfLabels;i++){
                    featureVector[i]+=polarity[i];
                }
            }
            numberOfTokensFound++;
        }
        if(numberOfTokensFound != 0) {
            for(int i = 0;i<featureVector.length;i++){
                featureVector[i] /= numberOfTokensFound;
            }
        }
        for(int i=0;i<featureCount;i++){
            features[i] = new FeatureNode(i + offset + 1, featureVector[i]);
        }
        return features;
    }

    @Override
    public int getFeatureCount() {
        return featureCount;
    }

    @Override
    public int getOffset() {
        return offset;
    }


    /**
     * Loads a word list with words with their polarity vector.
     * @param fileName path to a file containing word with their polarity vector
     */
    public void loadPolarityFile(String fileName) {
        try {
            BufferedReader br;
            if (fileName.endsWith(".gz")) {
                br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(fileName)), "UTF-8"));
            } else {
                br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            }
            String line;
            int size=0;
            if((line = br.readLine()) != null){
                size = line.split("\t").length-1;
            }
            Float polarity[] = new Float[size];
            while(line != null){
                String term[] = line.split("\t");
                for(int i=0;i<size;i++){
                    polarity[i] = Float.parseFloat(term[i+1]);
                }
                termPolarityMap.put(term[0], polarity);
                line = br.readLine();
            }
            totalNumberOfLabels = size;
            featureCount = totalNumberOfLabels;
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}