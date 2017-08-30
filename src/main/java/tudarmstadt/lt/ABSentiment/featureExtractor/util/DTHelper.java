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

package tudarmstadt.lt.ABSentiment.featureExtractor.util;

import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.type.Document;

import java.io.*;
import java.util.*;

/**
 * DTHelper class takes in a train and/or test file and stores a list of words from these files whose vector representation is absent in the embedding file
 * Created by abhishek on 28/5/17.
 */
public class DTHelper extends ProblemBuilder{

    protected static GenericWordSpace genericWordSpace = null;

    public static void main(String args[]){
        initialise("configuration.txt");
        if(w2vFile != null){
            genericWordSpace = W2vSpace.load(w2vFile, true);
        }else if(gloveFile != null){
            genericWordSpace = GloVeSpace.load(gloveFile, true,true);
        }
        analyseInputFile(trainFile, testFile, missingWordsFile);

    }

    /**
     * Analyses the train and/or test file, builds the list of words whose vector representation does not exists and write them to a file
     * @param inputTrainFile path to the train file
     * @param inputTestFile path to the test file
     * @param outputFile path to the output file containing the list of words
     */
    public static void analyseInputFile(String inputTrainFile, String inputTestFile, String outputFile){
        Set<String> set = new HashSet<>();
        InputReader inputReader;
        if(inputTrainFile != null){
            inputReader = new TsvReader(inputTrainFile);
            for(Document document: inputReader){
                for(String term:document.getDocumentText().split(" ")){
                    if(genericWordSpace != null){
                        if((term != null) && (!genericWordSpace.contains(term))){
                            set.add(term);
                        }
                    }
                }
            }
        }
        if(inputTestFile != null){
            inputReader = new TsvReader(inputTestFile);
            for(Document document: inputReader){
                for(String term:document.getDocumentText().split(" ")){
                    if(genericWordSpace != null){
                        if((term != null) && (!genericWordSpace.contains(term))){
                            set.add(term);
                        }
                    }
                }
            }
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"))) {
            for(String item: set){
                writer.write(item+"\n");
            }
            writer.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
