package tudarmstadt.lt.ABSentiment;

/**
 * Created by eugen on 4/10s/16.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tudarmstadt.lt.ABSentiment.aspecttermclassification.LinearAspectClassifier;
import tudarmstadt.lt.ABSentiment.aspecttermextraction.DictionaryAspectExtractor;
import tudarmstadt.lt.ABSentiment.relevancescoring.LinearRelevanceScorer;
import tudarmstadt.lt.ABSentiment.sentimentclassification.DictonarySentimentClassifier;

import java.util.List;

@RestController
@EnableAutoConfiguration
public class ApplicationController {

    @RequestMapping("/")
    String home(@RequestParam(value = "text", defaultValue = "") String text) {

        LinearRelevanceScorer rel = new LinearRelevanceScorer();


        DictionaryAspectExtractor aspectExtractor = new DictionaryAspectExtractor();
        LinearAspectClassifier aspectClassifier = new LinearAspectClassifier();
        DictonarySentimentClassifier sentimentClassifier = new DictonarySentimentClassifier();

        List<String> aspectTerms = aspectExtractor.getTerms(text);

        String ret = "Input:\t" + text;
        ret += "<br />Relevance:\t" + rel.getScore(text);
        ret += "<br />Category: " + aspectClassifier.getCategory(text);

        ret += "<hr />";
        for (String term : aspectTerms) {

            ret += "Aspect term: " + term;


            ret += "<hr />";

            ret += "Sentiment: " + sentimentClassifier.getSentiment(text);

        }

        return ret;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ApplicationController.class, args);
    }

}