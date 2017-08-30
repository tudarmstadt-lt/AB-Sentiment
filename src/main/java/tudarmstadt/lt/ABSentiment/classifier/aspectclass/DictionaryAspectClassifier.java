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

package tudarmstadt.lt.ABSentiment.classifier.aspectclass;

import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.classifier.Classifier;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Baseline aspect classifier using a dictionary of aspect expressions.
 */
public class DictionaryAspectClassifier implements Classifier {

    private HashMap<String, String> wordList;
    private String label;

    /**
     * Constructor, loads a map of aspect terms and their aspect classes
     */
    public DictionaryAspectClassifier() {
        String filename = "/data/dictionaries/aspect_classes";

        wordList = loadWordList(filename);
    }

    @Override
    public String getLabel(JCas cas) {
        String text = cas.getDocumentText();
        wordList.keySet().stream().filter(text::contains).forEach(term -> {
            label = wordList.get(term);
        });
        return label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public double getScore() {
        return 1.0;
    }

    /**
     * Loads a word list.
     *
     * @param fileName the path and filename of the wordlist
     * @return HashSet containing the words
     */
    private HashMap<String, String> loadWordList(String fileName) {
        HashMap<String, String> set = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] split = line.split("\\t");
                    if (split.length >= 1) {
                        set.put(split[0], split[1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }

}
