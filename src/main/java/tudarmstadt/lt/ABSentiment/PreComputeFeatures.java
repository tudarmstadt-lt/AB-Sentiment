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

package tudarmstadt.lt.ABSentiment;

import tudarmstadt.lt.ABSentiment.featureExtractor.precomputation.ComputeCorpusIdfScores;
import tudarmstadt.lt.ABSentiment.featureExtractor.precomputation.ComputeMaxDocumentLength;

/**
 * Precomputation of IDF map and other data used in features.
 */
public class PreComputeFeatures {

    /**
     * Extracts different data for feature extractors.
     * @param args
     */
    public static void main(String[] args) {

        String idfFile = "data/en/feature/idfmap.tsv.gz";
        String corpusFile = "data/en/corpus/corpus_en.tsv";
        String maxLengthFile = "data/en/feature/max_length";
        String configurationFile = "configuration.txt";

        ComputeCorpusIdfScores.computeIdfScores(corpusFile, idfFile, 100);System.out.println("1******************");
        ComputeMaxDocumentLength.computeMaxDocumentLength(corpusFile, maxLengthFile);System.out.println("2******************");

//        String trainingFile = "data/train.tsv";
//        String relTrainingFile = "data/en/relevance_train.tsv";
//        String relIdfTermsFile = "data/en/feature/relevance_idfterms.tsv";
//
//        String sentTrainingFile = "data/en/sentiment_train.tsv";
//        String sentIdfTermsFile = "data/en/feature/sentiment_idfterms.tsv";
//
//        String aspectTrainingFile = "data/en/aspect_train.tsv";
//        String aspectIdfTermsFile = "data/en/feature/aspect_idfterms.tsv";
//        String aspecCoarsetIdfTermsFile = "data/en/feature/aspect_coarse_idfterms.tsv";
//
//        computeIdfScores(configurationFile, trainingFile, relIdfTermsFile, false, "relevance");System.out.println("3******************");
//        computeIdfScores(configurationFile, trainingFile, sentIdfTermsFile, false, "sentiment");System.out.println("4******************");
//        computeIdfScores(configurationFile, trainingFile, aspectIdfTermsFile, false, "aspect");System.out.println("5******************");
//        computeIdfScores(configurationFile, trainingFile, aspecCoarsetIdfTermsFile, true, "aspect");System.out.println("6******************");
    }

}