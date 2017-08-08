package tudarmstadt.lt.ABSentiment;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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

    private AbSentiment analyzer = new AbSentiment("configuration.txt");
    /**
     * Processes input text and outputs the classification results.
     * @param text the input text
     * @return returns a HTML document with the analysis of the input
     */
    @RequestMapping("/")
    String home(@RequestParam(value = "text", defaultValue = "") String text, @RequestParam(value="format", defaultValue ="html") String format) {

        Result result = analyzer.analyzeText(text);

       if (format.compareTo("json") == 0) {
            return generateJSONResponse(result);
       } else {
           return generateHTMLResponse(result);
       }
    }

    private String generateJSONResponse(Result result) {
        JSONObject out = new JSONObject();
        out.put("input", result.getText());

        JSONObject sent = new JSONObject();
        sent.put("label", result.getSentiment());
        sent.put("score", result.getSentimentScore());
        out.put("sentiment", sent);

        JSONObject rel = new JSONObject();
        rel.put("label", result.getRelevance());
        rel.put("score", result.getRelevanceScore());
        out.put("relevance", rel);

        JSONObject asp = new JSONObject();
        asp.put("label", result.getAspect());
        asp.put("score", result.getAspectScore());
        out.put("aspect", asp);


        JSONObject aspCoarse = new JSONObject();
        aspCoarse.put("label", result.getAspectCoarse());
        aspCoarse.put("score", result.getAspectCoarseScore());
        out.put("aspect_coarse", aspCoarse);

        JSONArray targets = new JSONArray();
        for (AspectExpression a : result.getAspectExpressions()) {
            targets.add(a.getAspectExpression());
        }
        out.put("targets", targets);

        return out.toString();
    }

    private String generateHTMLResponse(Result result) {
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

        addHeading("Aspect Targets");
        addTargets(result);

        addExamples();
        addFooter();
        return output.toString();
    }

    private void addTargets(Result result) {
        output.append("<ul>");
        for (AspectExpression a : result.getAspectExpressions()) {
            output.append("<li>").append(a.getAspectExpression()).append("</li>");
        }
        output.append("</ul>");
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
     * Adds text from a given {@link Result}. Annotates {@link AspectExpression}s
     * @param result the {@link Result} object
     */
    private void addText(Result result) {
        String text = result.getText();
        for (AspectExpression e: result.getAspectExpressions()) {
            text = text.replaceFirst(e.getAspectExpression(), "<b>"+e.getAspectExpression()+"</b>");
        }
        output.append(text);
    }

    /**
     * Adds a HTML header with CSS
     */
    private void addHeader() {
        output.append("<html><header><title>Analysis</title>");
        output.append("<style>* {margin-bottom:0;}.pos {background:green;}.neg {background:red;}</style>");
        output.append("</header><body>");
    }

    /**
     * Closes the HTML page
     */
    private void addFooter() {
        output.append("</body></html>");
    }


    /**
     * Adds a list of example items that are active for testing.
     */
    private void addExamples() {
        addHeading("Examples");
        output.append("<div><ul>");
        addExample("Hast du schon Deutsche Bahn &#35;Jobs in &#35;Augsburg auf unserer Homepage gesehen ? http://t.co/lAnyuN6WUq http://t.co/IvnaO2OAh7 ");
        addExample("\" Ja geil .. 1 Stunde später zuhause weil sich \"\" \"\" Unbefugte \"\" \"\" in der Bahn aufhielten . :/ \"");
        addExample("Re : MDR Sachsen-Anhalt Zum Glück fahr ich nicht mit der bahn bin immer 100 Prozent pünktlich und das seit Jahren . Komisch das es nie bei der Bahn geht obwohl die fette Kohle bekommen ");
        addExample("Re : Rheinbahn Das kann doch nicht wahr sein . Diese Woche ist die Bahn jedesmal ein paar minuten zu früh dran , weshalb ich sie dann verpasse und meinen Anschlusszug nur mit , Hetzen und Leute beiseite schieben , schaffe . Das Nervt gewaltig . ");
        addExample("Geld für den Nahverkehr wird knapp, und schon steht eine Bahnverbindung auf der Kippe");
        addExample("schlechte Luft am Bahnsteig");
        addExample("Manche Fahrgäste fühlen sich vom Qualm der Mitreisenden belästigt");
        addExample("RT @phornic : Nette Mitarbeiter der Bahn sind nett");
        addExample("Die App funktioniert nicht , sieht aber gut aus");
        addExample("Der neue ICE sieht schön aus und hat ein gutes Design");
        output.append("</ul></div>");
    }

    /**
     * Adds an clickable example item for convenient testing.
     * @param text the input string
     */
    private void addExample(String text) {
        output.append("<li>");
        output.append("<a href='?text=").append(text).append("'>").append(text).append("</a>");
        output.append("</li>");
    }
}