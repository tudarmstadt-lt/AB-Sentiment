package tudarmstadt.lt.ABSentiment.uimahelper;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.uima.fit.util.JCasUtil.select;

/**
 * Created by eugen on 6/2/16.
 */
public class Tokenizer {

    JCas cas;
    AnalysisEngine tokenizer;

    public Tokenizer() {
        try {
            tokenizer = AnalysisEngineFactory.createEngine(BreakIteratorSegmenter.class);
        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        }
        try {
            cas = JCasFactory.createJCas();
        } catch (UIMAException e) {
            e.printStackTrace();
        }
    }

    public void tokenizeString(String input) {
        cas.reset();
        cas.setDocumentText(input);
        cas.setDocumentLanguage("de");
        try {
            tokenizer.process(cas);
        } catch (AnalysisEngineProcessException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTokens() {
        List<String> tokenStrings = new ArrayList<>();
        Collection<Token> tokens = select(cas, Token.class);
        for (Annotation token : tokens) {
            tokenStrings.add(token.getCoveredText());
        }
        return tokenStrings;
    }
}
