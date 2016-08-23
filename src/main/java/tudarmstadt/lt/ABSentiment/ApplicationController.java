package tudarmstadt.lt.ABSentiment;


import org.apache.uima.jcas.JCas;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tudarmstadt.lt.ABSentiment.classifier.LinearClassifier;
import tudarmstadt.lt.ABSentiment.classifier.aspectclass.LinearAspectClassifier;
import tudarmstadt.lt.ABSentiment.classifier.relevance.LinearRelevanceClassifier;
import tudarmstadt.lt.ABSentiment.classifier.sentiment.LinearSentimentClassifer;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@RestController
@EnableAutoConfiguration
public class ApplicationController {

    private StringBuilder sb;
    private NumberFormat formatter = new DecimalFormat("#0.000");

    @RequestMapping("/")
    String home(@RequestParam(value = "text", defaultValue = "") String text) {

        // initialize classifiers
        LinearClassifier relevanceClassifier = new LinearRelevanceClassifier("relevance-model.svm");
        LinearClassifier aspectClassifier = new LinearAspectClassifier("aspect-model.svm");
        LinearClassifier coarseAspectClassifier = new LinearAspectClassifier("aspect-coarse-model.svm", "aspect-coarse-label-mappings.tsv");
        LinearClassifier sentimentClassifier = new LinearSentimentClassifer("sentiment-model.svm");

        // process input
        Preprocessor nlpPipeline = new Preprocessor(text);
        JCas cas = nlpPipeline.getCas();

        // classify input
        String sentimentLabel = sentimentClassifier.getLabel(cas);
        double sentimentScore = sentimentClassifier.getScore();
        String relevanceLabel = relevanceClassifier.getLabel(cas);
        double relevanceScore = relevanceClassifier.getScore();
        String aspectLabel = aspectClassifier.getLabel(cas);
        double aspectScore = aspectClassifier.getScore();
        String aspectCoarseLabel = coarseAspectClassifier.getLabel(cas);
        double aspectCoarseScore = coarseAspectClassifier.getScore();

        sb = new StringBuilder();

        sb.append("<style>margin-bottom:0;.pos {background:green;}.neg {background:red;}</style>");

        addHeading("Input");
        addText(text, sentimentLabel);

        addHeading("Relevance");
        addResult(relevanceLabel, relevanceScore);

        addHeading("Sentiment");
        addResult(sentimentLabel, sentimentScore);

        addHeading("Aspect");
        addResult(aspectLabel, aspectScore);

        addHeading("Coarse Aspect");
        addResult(aspectCoarseLabel, aspectCoarseScore);

        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ApplicationController.class, args);
    }

    private void addHeading(String text) {
        sb.append("<h3>").append(text).append("</h3>");
    }

    private void addResult(String label, double score) {
        sb.append("Label: ").append(label);
        sb.append("&emsp; Confidence: ").append(formatter.format(score));
    }

    private void addText(String text, String cssClass) {
        sb.append("<p class='").append(cssClass).append("'>");
        sb.append(text);
        sb.append("</p>");
    }

}