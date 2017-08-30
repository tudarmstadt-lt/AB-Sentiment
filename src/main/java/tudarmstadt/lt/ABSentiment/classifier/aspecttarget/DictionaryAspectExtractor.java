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

package tudarmstadt.lt.ABSentiment.classifier.aspecttarget;

import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.classifier.Classifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Baseline aspect target extractor, uses dictionaries of aspect terms.
 */
public class DictionaryAspectExtractor implements Classifier {

    private HashSet<String> wordList;
    private String label;

    /**
     * Constructor, loads a list of aspect terms
     */
    public DictionaryAspectExtractor() {
        String filename = "/data/dictionaries/aspects";

        wordList = loadWordList(filename);
    }

    @Override
    public String getLabel(JCas cas) {
        String text = cas.getDocumentText();
        for (String t : wordList) {
            if (text.contains(t)) {
               label = t;
            }
        }
        return label;
    }

    public List<String> getLabels(JCas cas) {
        String text = cas.getDocumentText();
        List<String> ret = new ArrayList<>();

        for (String t : wordList) {
            if (text.contains(t)) {
                ret.add(t);
            }
        }
        return ret;
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
     * Loads a word list
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