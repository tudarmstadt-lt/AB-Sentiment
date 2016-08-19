package tudarmstadt.lt.ABSentiment;


import org.apache.uima.jcas.JCas;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tudarmstadt.lt.ABSentiment.aspecttermextraction.CrfAspectExtractor;
import tudarmstadt.lt.ABSentiment.classifier.aspectclass.LinearAspectClassifier;
import tudarmstadt.lt.ABSentiment.classifier.aspectclass.LinearCoarseAspectClassifier;
import tudarmstadt.lt.ABSentiment.aspecttermextraction.DictionaryAspectExtractor;
import tudarmstadt.lt.ABSentiment.classifier.relevance.LinearRelevanceClassifier;
import tudarmstadt.lt.ABSentiment.classifier.sentiment.LinearSentimentClassifer;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Vector;

@RestController
@EnableAutoConfiguration
public class ApplicationController {

    @RequestMapping("/")
    String home(@RequestParam(value = "text", defaultValue = "") String text) {

        LinearRelevanceClassifier rel = new LinearRelevanceClassifier();


        DictionaryAspectExtractor aspectExtractor = new DictionaryAspectExtractor();
        LinearAspectClassifier aspectClassifier = new LinearAspectClassifier("aspect-model.svm");
        LinearCoarseAspectClassifier coarseAspectClassifier = new LinearCoarseAspectClassifier();
        LinearSentimentClassifer sentimentClassifier = new LinearSentimentClassifer();

        NumberFormat formatter = new DecimalFormat("#0.000");


        Preprocessor nlpPipeline = new Preprocessor(text);
        JCas cas = nlpPipeline.getCas();


        List<String> aspectTerms = aspectExtractor.getTerms(text);

        String ret = "<style>margin-bottom:0;</style>" +
                "<h3>Input</h3>\t" + text;
        ret += "<h3>Relevance</h3>\tLabel: " + rel.getCategory(text) + ",\tConfidence: " + formatter.format(rel.getScore());

       // ret += "<h3>Fine-grained Category:</h3> " + aspectClassifier.getLabel(text) ;
        ret += "<p />options: <ul>";
        Vector<Double> predictions = aspectClassifier.getPredictions();
        if (predictions != null) {
            for (int i = 0; i < predictions.size(); i++) {
                if (predictions.get(i) != null) {
                    ret += "<li>" + formatter.format(aspectClassifier.getScore(i)) + " " + aspectClassifier.getAspectLabel(i);
                }
            }
        }
        ret += " </ul>";


        ret += "<h3>Coarse-grained Category:</h3>" + coarseAspectClassifier.getCategory(text);
        ret += "<p />options: <ul>";
        predictions = coarseAspectClassifier.getPredictions();
        for (int i = 0; i<predictions.size(); i++) {
            if (predictions.get(i) != null) {
                ret += "<li>" +  formatter.format(coarseAspectClassifier.getScore(i)) + " "+  coarseAspectClassifier.getAspectLabel(i) ;
            }
        }
        ret += " </ul>";


        ret += "<h3>Sentiment</h3>" + sentimentClassifier.getSentiment(text) + " " + formatter.format(sentimentClassifier.getScore());

        ret += "<h3>Aspect Terms</h3><ul>";
        for (String term : aspectTerms) {

            ret += "<li>" + term;

        }

        ret += " </ul>";
        return ret;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ApplicationController.class, args);
    }

}