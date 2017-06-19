package tudarmstadt.lt.ABSentiment.classifier.aspecttarget;


import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.CleartkSequenceAnnotator;
import org.cleartk.ml.Instance;
import org.cleartk.ml.feature.extractor.CleartkExtractor;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Following;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Preceding;
import org.cleartk.ml.feature.extractor.CoveredTextExtractor;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import org.cleartk.ml.feature.extractor.TypePathExtractor;
import org.cleartk.ml.feature.function.*;
import tudarmstadt.lt.ABSentiment.type.uima.AspectTarget;
import tudarmstadt.lt.ABSentiment.type.uima.GoldAspectTarget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

/**
 * UIMA ClearTk Annotator for CRF Training and Testing. Extracts Features and trains/classifies an instance.
 */
public class AspectAnnotator extends CleartkSequenceAnnotator<String> {
    //lists for features
    private List<FeatureExtractor1<Token>> featureExtractors;

    @Override
    public void initialize(UimaContext context)
            throws ResourceInitializationException {
        super.initialize(context);

        featureExtractors = new ArrayList<>();

        featureExtractors.add(new FeatureFunctionExtractor(new TypePathExtractor(Token.class, "pos/PosValue")));


        //featureExtractors.add(new FeatureFunctionExtractor(new TypePathExtractor(Token.class, "lemma")));

        //featureExtractors.add(new CleartkExtractor(Token.class,
          //      new FeatureFunctionExtractor(new TypePathExtractor(Token.class, "pos/PosValue")),
            //    new Preceding(2), new Following(2)));

        //featureExtractors.add(new CleartkExtractor(Token.class,
        //        new FeatureFunctionExtractor(new TypePathExtractor(Token.class, "lemma")),
        //        new Preceding(2), new Following(2)));

        //featureExtractors.add(new CleartkExtractor(Token.class,
          //      new FeatureFunctionExtractor(new TypePathExtractor(Token.class, "lemma")),
            //    new Preceding(3), new Following(3)));

        featureExtractors.add(new FeatureFunctionExtractor(
                new CoveredTextExtractor(), new LowerCaseFeatureFunction(), new CapitalTypeFeatureFunction(),
                new NumericTypeFeatureFunction(), new CharacterCategoryPatternFunction<Token>(), new ContainsHyphenFeatureFunction(),
                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.RIGHT_TO_LEFT, 0, 2),
                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.RIGHT_TO_LEFT, 0, 3),
                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.RIGHT_TO_LEFT, 0, 4),
                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.RIGHT_TO_LEFT, 0, 5),
                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.LEFT_TO_RIGHT, 0, 2),
                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.LEFT_TO_RIGHT, 0, 3),
                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.LEFT_TO_RIGHT, 0, 4),
                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.LEFT_TO_RIGHT, 0, 5)
        ));


        featureExtractors.add(
                new CleartkExtractor(Token.class,
                        new FeatureFunctionExtractor(new CoveredTextExtractor(), new LowerCaseFeatureFunction(), new CapitalTypeFeatureFunction(),
                                new NumericTypeFeatureFunction(), new CharacterCategoryPatternFunction<Token>(), new ContainsHyphenFeatureFunction(),
                                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.RIGHT_TO_LEFT, 0, 2),
                                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.RIGHT_TO_LEFT, 0, 3),
                                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.RIGHT_TO_LEFT, 0, 4),
                                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.RIGHT_TO_LEFT, 0, 5),
                                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.LEFT_TO_RIGHT, 0, 2),
                                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.LEFT_TO_RIGHT, 0, 3),
                                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.LEFT_TO_RIGHT, 0, 4),
                                new CharacterNgramFeatureFunction(CharacterNgramFeatureFunction.Orientation.LEFT_TO_RIGHT, 0, 5)),
                        new Preceding(2),
                        new Following(2)));


    }

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        for (Sentence sentence : select(jCas, Sentence.class)) {
            List<Instance<String>> instances = new ArrayList<>();
            List<Token> tokens = selectCovered(jCas, Token.class, sentence);
            for (Token token : tokens) {
                Instance<String> instance = new Instance<>();
                for (FeatureExtractor1<Token> extractor : this.featureExtractors) {
                    if (extractor instanceof CleartkExtractor) {
                        instance.addAll((((CleartkExtractor) extractor).extractWithin(jCas, token, sentence)));
                    }
                    else {
                        instance.addAll(extractor.extract(jCas, token));
                    }
                }
                try {
                    instance.setOutcome(selectCovered(jCas, GoldAspectTarget.class, token).get(0).getAspectTargetType());
                } catch (IndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }
                instances.add(instance);
            }
            if (this.isTraining()) {
                this.dataWriter.write(instances);
            } else {
                List<String> labels = this.classify(instances);
                Iterator<Token> tokensIter = tokens.iterator();
                for (String label : labels) {
                    Token t = tokensIter.next();
                    AspectTarget target = new AspectTarget(jCas, t.getBegin(), t.getEnd());
                    target.setAspectTargetType(label);
                    target.addToIndexes();
                }
            }
        }
    }
}

