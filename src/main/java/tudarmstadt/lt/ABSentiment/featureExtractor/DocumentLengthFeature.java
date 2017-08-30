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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * Document length {@link FeatureExtractor}, extracts normalized document length based on the precomputed maximal length.
 */
public class DocumentLengthFeature implements FeatureExtractor {

    private int maxDocumentLength = Integer.MAX_VALUE;
    private int offset = 0;
    private final int featureCount = 1;

    private Preprocessor preprocessor = new Preprocessor();

    /**
     * Constructor; specifies the "max-document-length" file. Feature offset is set to '0' by default.
     * @param maxLengthFile path to the file containing the maximal document length
     */
    public DocumentLengthFeature(String maxLengthFile, int offset) {
        loadMaxLength(maxLengthFile);
        this.offset = offset;
    }



    @Override
    public Feature[] extractFeature(JCas cas) {

        Collection<String> documentText = preprocessor.getTokenStrings(cas);

        Feature[] ret = new Feature[1];
        Double value = Math.max(1.0, documentText.size() / maxDocumentLength);
        ret[0] = new FeatureNode(offset, value);
        return ret;
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
     * Loads a word list with an integer value, specifying the maximal document length.
     * @param fileName the path to the input file
     */
    private void loadMaxLength(String fileName) {
        try {
            BufferedReader br;
            if (fileName.endsWith(".gz")) {
                br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(fileName)), "UTF-8"));
            } else {
                br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            }
            String line;
            if ((line = br.readLine()) != null) {
                maxDocumentLength = Integer.parseInt(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
