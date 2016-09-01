package tudarmstadt.lt.ABSentiment;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tudarmstadt.lt.ABSentiment.type.AspectExpression;
import tudarmstadt.lt.ABSentiment.type.Result;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@RestController
@EnableAutoConfiguration
public class ApplicationController {

    /**
     * Output StringBuilder
     */
    private StringBuilder output;
    /**
     * Formatter for confidence scores
     */
    private NumberFormat formatter = new DecimalFormat("#0.000");

    private AbSentiment analyzer = new AbSentiment();
    /**
     * Processes input text and outputs the classification results.
     * @param text the input text
     * @return returns a HTML document with the analysis of the input
     */
    @RequestMapping("/")
    String home(@RequestParam(value = "text", defaultValue = "") String text) {

        Result result = analyzer.analyzeText(text);

        output = new StringBuilder();

        addHeader();

        addHeading("Input");
        openInput(result.getSentiment());
        addText(result);
        closeInput();

        addHeading("Relevance");
        addResult(result.getRelevance(), result.getRelevanceScore());

        addHeading("Sentiment");
        addResult(result.getSentiment(), result.getSentimentScore());

        addHeading("Aspect");
        addResult(result.getAspect(), result.getAspectScore());

        addHeading("Coarse Aspect");
        addResult(result.getAspectCoarse(), result.getAspectCoarseScore());

        return output.toString();
    }

    /**
     * Runs the RESTful server.
     * @param args execution arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ApplicationController.class, args);
    }

    /**
     * Adds a Heading to the output.
     * @param text the heading
     */
    private void addHeading(String text) {
        output.append("<h3>").append(text).append("</h3>");
    }

    /**
     * Adds a label with a confidence score to the output.
     * @param label the label
     * @param score the confidence score for the label
     */
    private void addResult(String label, double score) {
        output.append("Label: ").append(label);
        output.append("&emsp; Confidence: ").append(formatter.format(score));
    }

    /**
     * Adds a paragraph tag with a provided class (e.g. sentiment).
     * @param cssClass the CSS class of the paragraph
     */
    private void openInput(String cssClass) {
        output.append("<p class='").append(cssClass).append("'>");
    }

    /**
     * Closes the paragraph tag.
     */
    private void closeInput() {
        output.append("</p>");
    }

    /**
     * Adds text from a given {@link Result}. Annotates {@AspectExpression}s
     * @param result the {@link Result} object
     */
    private void addText(Result result) {
        String text = result.getText();
        for (AspectExpression e: result.getAspectExpressions()) {
            text.replaceFirst(e.getAspectExpression(), "<b>"+e.getAspectExpression()+"</b>");
        }
        output.append(text);
    }

    private void addHeader() {
        output.append("<html><header><title>Analysis</title>");
        output.append("<style>margin-bottom:0;.pos {background:green;}.neg {background:red;}</style>");
        output.append("</header>");
    }

}