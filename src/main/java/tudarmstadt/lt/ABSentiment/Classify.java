package tudarmstadt.lt.ABSentiment;


import org.w3c.dom.Element;
import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.reader.XMLReader;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.type.AspectExpression;
import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.type.Result;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class Classify extends ProblemBuilder{

    static boolean xmlData = false;

    static String outputFile;

    static Writer out = null;
    static DocumentBuilder docBuilder;
    static org.w3c.dom.Document results;
    static Element documents;


    public static void main(String[] args) {

        String configurationFile = "configuration.txt";
        if(args.length == 1 ){
            configurationFile = args[0];
        }

        initialise(configurationFile);

        String inputFile = testFile;

        if (inputFile.endsWith("xml")) {
            xmlData = true;
        }

        outputFile = inputFile.substring(0, inputFile.lastIndexOf(".")) + "_classified" +
                inputFile.substring(inputFile.lastIndexOf("."));

        AbSentiment classifier = new AbSentiment(configurationFile);


        InputReader fr;

        if (xmlData){
            fr = new XMLReader(inputFile);
        } else {
            fr = new TsvReader(inputFile);
        }

        // initialize Writers
        initializeOutput();

        Result res;
        for (Document d : fr) {
            res = classifier.analyzeText(d.getDocumentText());
            addResult(d, res);

        }

        // terminate writers
        writeDocuments();

    }

    private static void initializeOutput() {
        if (xmlData) {

            DocumentBuilderFactory docFactory;
            docFactory = DocumentBuilderFactory.newInstance();
            try {
                docBuilder = docFactory.newDocumentBuilder();
                results = docBuilder.newDocument();

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            documents = results.createElement("Documents");
            results.appendChild(documents);
        } else {
            try {
                OutputStream predStream = new FileOutputStream(outputFile);
                out = new OutputStreamWriter(predStream, "UTF-8");
            } catch (FileNotFoundException | UnsupportedEncodingException e1) {
                e1.printStackTrace();
                System.exit(1);
            }
        }
    }

    private static void writeDocuments() {
        if (xmlData) {
            Transformer transformer = null;
            try {
                transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

                DOMSource source = new DOMSource(results);
                StreamResult result = new StreamResult(new File(outputFile));

                transformer.transform(source, result);
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }

        } else {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void addResult(Document d, Result res) {
        if (xmlData) {
            Element doc = results.createElement("Document");
            doc.setAttribute("id", d.getDocumentId());
            documents.appendChild(doc);

            Element text = results.createElement("text");
            text.setTextContent(d.getDocumentText());
            doc.appendChild(text);

            Element relevance = results.createElement("relevance");
            relevance.setTextContent(res.getRelevance());
            doc.appendChild(relevance);

            Element sentiment = results.createElement("sentiment");
            sentiment.setTextContent(res.getSentiment());
            doc.appendChild(sentiment);

            Element opinions = results.createElement("Opinions");
            doc.appendChild(opinions);
            if (res.getAspectExpressions().size() == 0) {
                Element opinion = results.createElement("Opinion");
                opinion.setAttribute("from", "0");
                opinion.setAttribute("to", "0");
                opinion.setAttribute("target", "NULL");
                opinion.setAttribute("polarity", res.getSentiment());
                opinion.setAttribute("category", res.getAspect());
                opinions.appendChild(opinion);
            } else {
                for (AspectExpression aspectExpression : res.getAspectExpressions()) {
                    Element opinion = results.createElement("Opinion");
                    opinion.setAttribute("from", aspectExpression.getBegin()+"");
                    opinion.setAttribute("to", aspectExpression.getEnd() +"");
                    opinion.setAttribute("target", aspectExpression.getAspectExpression());
                    opinion.setAttribute("polarity", res.getSentiment());
                    opinion.setAttribute("category", res.getAspect());
                    opinions.appendChild(opinion);
                }
            }

        } else {
            try {
                out.write(d.getDocumentId() + "\t" + d.getDocumentText() + "\t");
                out.write(res.getRelevance() + "\t" + res.getSentiment() + "\t" + res.getAspect() + ":" + res.getSentiment()+ "\n");
               } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}