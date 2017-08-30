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

package tudarmstadt.lt.ABSentiment.classifier.relevance;

import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.classifier.Classifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * Baseline relevance classifier using a dictionary terms that indicate irrelevance.
 */
public class DictionaryRelevanceClassifier implements Classifier {

    private HashSet<String> wordList;
    private String label;

    /**
     * Constructor, loads a list of words that indicate non-relevance.
     */
    public DictionaryRelevanceClassifier() {
        String filename = "/data/dictionaries/non-relevant";

        wordList = loadWordList(filename);
    }


    @Override
    public String getLabel(JCas cas) {
        String text = cas.getDocumentText();
        label = "1.0";

        for (String w : wordList) {
            if (text.contains(w)) {
                label = "-1.0";
            }
        }
        return label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public double getScore() {
        return 0;
    }


    /**
     * Loads a word list.
     *
     * @param fileName the path and filename of the wordlist
     * @return HashSet containing the words
     */
    private HashSet<String> loadWordList(String fileName) {
        HashSet<String> set = new HashSet<>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream(fileName), "UTF-8"));

            String line;
            while ((line = br.readLine()) != null) {
                set.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return set;
    }

}
