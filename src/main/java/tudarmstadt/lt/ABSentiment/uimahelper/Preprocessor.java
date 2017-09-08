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

package tudarmstadt.lt.ABSentiment.uimahelper;


import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import lt_hamburg.segmenter.annotator.TokenAnnotator;
import lt_hamburg.segmenter.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.uima.fit.util.JCasUtil.select;

/**
 * Preprocessor class that performs NLP operations using UIMA AnalysisEngines.
 */
public class Preprocessor extends ProblemBuilder{

    private JCas cas;
    private AnalysisEngine tokenizer;
    private AnalysisEngine postagger;
    private boolean lightAnalysis = false;

    /**
     * Constructor; initializes the UIMA pipeline and the CAS.
     */
    public Preprocessor() {

        // build annotation engine
        try {
            tokenizer = AnalysisEngineFactory.createEngine(TokenAnnotator.class);
            //tokenizer = AnalysisEngineFactory.createEngine(BreakIteratorSegmenter.class);
            postagger = AnalysisEngineFactory.createEngine(OpenNlpPosTagger.class,
                            OpenNlpPosTagger.PARAM_MODEL_LOCATION, crfModelFolder +"opennlp-"+language+"-pos-maxent.bin");
        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        }
        // build cas
        try {
            cas = JCasFactory.createJCas();
        } catch (UIMAException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor; initializes the UIMA pipeline and the CAS, then processes an input text
     * @param lightAnalysis flag to indicate light analysis, only tokenization is applied
     */
    public Preprocessor(boolean lightAnalysis) {
        this();
        this.lightAnalysis = lightAnalysis;
    }

    /**
     * Constructor; initializes the UIMA pipeline and the CAS, then processes an input text
     * @param input input text that is analyzed in the CAS
     */
    public Preprocessor(String input) {
        this();
        processText(input);
    }

    /**
     * Processes a new text by the NLP pipeline. Resets the CAS for fast processing.
     * @param input input text
     */
    public void processText(String input) {
        createCas(input);
        try {
            tokenizer.process(cas);
            if (!lightAnalysis) {
                postagger.process(cas);
            }
        } catch (AnalysisEngineProcessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the CAS from an input text
     * @param input input text
     */
    private void createCas(String input) {
        cas.reset();
        cas.setDocumentText(input);
        cas.setDocumentLanguage(language);
    }

    /**
     * Retrieves the CAS, e.g. for {@link tudarmstadt.lt.ABSentiment.featureExtractor.FeatureExtractor}s
     * @return the CAS object
     */
    public JCas getCas() {
        return cas;
    }

    /**
     * Retrieves a list of tokens as Strings from a provided CAS.
     * @param cas the CAS, from which the tokens are extracted
     * @return a list of Strings
     */
    public List<String> getTokenStrings(JCas cas) {
        List<String> tokenStrings = new ArrayList<>();
        Collection<Token> tokens = select(cas, Token.class);
        for (Annotation token : tokens) {
            tokenStrings.add(token.getCoveredText());
        }
        return tokenStrings;
    }


    /**
     * Retrieves a list of tokens as Strings from the current CAS.
     * @return a list of Strings
     */
    public List<String> getTokenStrings() {
        return getTokenStrings(this.cas);
    }

    /**
     * Retrieves a list of tokens as @link{Token}s from a provided CAS.
     * @param cas the CAS, from which the tokens are extracted
     * @return a list of @link{Token} Annotations
     */
    public List<Token> getTokens(JCas cas) {
        List<Token> tokenList = new ArrayList<>();
        Collection<Token> tokens = select(cas, Token.class);
        for (Annotation token : tokens) {
            tokenList.add((Token) token);
        }
        return tokenList;
    }


    /**
     * Retrieves a list of tokens as @link{Token}s from the current CAS.
     * @return a list of @link{Token} Annotations
     */
    public List<Token> getTokens() {
        return getTokens(this.getCas());
    }

}
