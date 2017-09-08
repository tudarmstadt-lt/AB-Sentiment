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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * TfidfHelper class helps to load the tf-idf file whenever required
 * Created by abhishek on 1/6/17.
 */
public class TfidfHelper {

    protected static HashMap<Integer, Double> termIdf = new HashMap<>();
    protected static HashMap<String, Integer> tokenIds = new HashMap<>();
    protected static HashMap<Integer, String> tokenStrings = new HashMap<>();
    protected static HashMap<String, Integer> tokenCorpusFrequency = new HashMap<>();
    protected int maxTokenId = 0;

    /**
     * Loads the tf-idf file into four HashMaps
     * @param fileName path to the tf-idf file
     */
    public void loadIdfList(String fileName) {
        try {
            BufferedReader br;
            if (fileName.endsWith(".gz")) {
                br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(fileName)), "UTF-8"));
            } else {
                br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            }
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokenLine = line.split("\\t");
                int tokenId = ++maxTokenId;
                tokenIds.put(tokenLine[0], tokenId);
                tokenStrings.put(tokenId, tokenLine[0]);
                termIdf.put(tokenId, Double.parseDouble(tokenLine[2]));
                tokenCorpusFrequency.put(tokenLine[0], Integer.parseInt(tokenLine[3]));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Returns a HashMap containing the word and it's corpus frequency
     * @return a HashMap containing the word and it's corpus frequency
     */
    public HashMap<String, Integer> getTokenCorpusFrequency(){
        return tokenCorpusFrequency;
    }

}