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

package tudarmstadt.lt.ABSentiment.reader;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import tudarmstadt.lt.ABSentiment.type.uima.GoldAspectTarget;

/**
 * Reader for CoNLL format, creates a CAS;
 * Tokens are separated by newlines, sentences by two newlines.
 * A line contains a token and a label.
 */
public class ConllReader extends JCasAnnotator_ImplBase {
    public static final String CONLL_VIEW = "ConnlView";

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
    }

    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {
        JCas docView;
        String tbText;
        try {
            docView = jcas.getView(CAS.NAME_DEFAULT_SOFA);
            tbText = jcas.getView(CONLL_VIEW).getDocumentText();
        }
        catch (CASException e) {
            throw new AnalysisEngineProcessException(e);
        }
        // a new sentence always starts with a new line
        if (tbText.charAt(0) != '\n') {
            tbText = "\n" + tbText;
        }

        String[] tokens = tbText.split("(\r\n|\n)");
        Sentence sentence = null;
        int idx = 0;
        Token token = null;
        GoldAspectTarget aspectTargetTag;
        String aspectTarget;
        boolean initSentence = false;
        StringBuilder docText = new StringBuilder();

        StringBuilder sentenceSb = new StringBuilder();

        for (String line : tokens) {
            // new sentence if there's a new line
            if (line.equals("")) {
                if (sentence != null && token != null) {
                    terminateSentence(sentence, token);
                    docText.append("\n");
                    idx++;
                }
                // init new sentence with the next recognized token
                initSentence = true;
                sentenceSb = new StringBuilder();
            } else {
                String[] tag = line.split("\\t");
                String word = tag[0];

                aspectTarget = tag[tag.length - 1];

                docText.append(word);
                sentenceSb.append(word).append(" ");

                token = new Token(docView, idx, idx + word.length());
                aspectTargetTag = new GoldAspectTarget(docView, idx, idx + word.length());
                docText.append(" ");
                idx++;

                // start new sentence
                if (initSentence) {
                    sentence = new Sentence(docView);
                    sentence.setBegin(token.getBegin());
                    initSentence = false;
                }
                // increment index of text
                idx += word.length();
                aspectTargetTag.setAspectTargetType(aspectTarget);

                aspectTargetTag.addToIndexes();
                token.addToIndexes();

            }
        }

        if (sentence != null && token != null) {
            terminateSentence(sentence, token);
        }
        docView.setSofaDataString(docText.toString(), "text/plain");
    }

    /**
     * Creates {@link Sentence} Annotation, sets the sentence end.
     * @param sentence the current {@link Sentence} annotation
     * @param token the last {@link Token} in the sentence
     */
    private void terminateSentence(Sentence sentence, Token token) {
        sentence.setEnd(token.getEnd());
        sentence.addToIndexes();
    }


}