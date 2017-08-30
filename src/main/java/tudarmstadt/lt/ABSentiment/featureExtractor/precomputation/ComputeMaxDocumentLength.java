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

package tudarmstadt.lt.ABSentiment.featureExtractor.precomputation;

import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.type.Document;

/**
 * Computes maximum document length for a corpus in TSV format
 */
public class ComputeMaxDocumentLength {

    /**
     * Computes the maximal document length for an input file and stores the result in a file.
     * @param inputFile file containing the input corpus
     * @param outputFile path to the output file which will contain the integer number
     */
    public static void computeMaxDocumentLength(String inputFile, String outputFile) {
        MaxDocumentLength ml = new MaxDocumentLength();
        InputReader fr = new TsvReader(inputFile);

        for (Document d: fr) {
            ml.addDocument(d);
        }
        ml.saveMaxLength(outputFile);
    }

}
