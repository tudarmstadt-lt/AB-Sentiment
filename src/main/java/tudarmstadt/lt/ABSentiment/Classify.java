package tudarmstadt.lt.ABSentiment;


import org.w3c.dom.Element;
import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.reader.XMLReader;
import tudarmstadt.lt.ABSentiment.reader.XMLReaderSemEval;
import tudarmstadt.lt.ABSentiment.type.AspectExpression;
import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.type.Result;
import tudarmstadt.lt.ABSentiment.type.Sentence;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class Classify {

    static boolean xmlData = false;
    static boolean semEval2017 = false;

    static String outputFile;

    static Writer out = null;
    static DocumentBuilder docBuilder;
    static org.w3c.dom.Document results;
    static Element documents;

    static AbSentiment classifier;

    public static void main(String[] args) {



        String inputFile = "dev.tsv";

        semEval2017 = true;

        if (args.length > 0) {
            inputFile = args[0];
        }
        if (inputFile.endsWith("xml")) {
            xmlData = true;
        }

        outputFile = inputFile.substring(0, inputFile.lastIndexOf(".")) + "_classified" +
                inputFile.substring(inputFile.lastIndexOf("."));

         classifier = new AbSentiment();


        InputReader fr;

        if (xmlData){
            if (semEval2017) {
                fr = new XMLReaderSemEval(inputFile);
            } else {
                fr = new XMLReader(inputFile);
            }
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
            if (semEval2017) {
                documents = results.createElement("Reviews");
                results.appendChild(documents);
            } else {
                documents = results.createElement("Documents");
                results.appendChild(documents);
            }
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
            if (semEval2017) {
                Element rev = results.createElement("Review");
                rev.setAttribute("rid", d.getDocumentId());
                documents.appendChild(rev);

                Element sentences = results.createElement("sentences");
                rev.appendChild(sentences);

                // global opinions
                Element opinions = results.createElement("Opinions");
                rev.appendChild(opinions);
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
                        opinion.setAttribute("from", aspectExpression.getBegin() + "");
                        opinion.setAttribute("to", aspectExpression.getEnd() + "");
                        opinion.setAttribute("target", aspectExpression.getAspectExpression());
                        opinion.setAttribute("polarity", res.getSentiment());
                        opinion.setAttribute("category", res.getAspect());
                        opinions.appendChild(opinion);
                    }
                }

                // sentence-wise classification
                for (Sentence s : d.getSentences()) {
                    Element sent = results.createElement("sentence");
                    sent.setAttribute("id", s.getId());
                    sentences.appendChild(sent);

                    Element text = results.createElement("text");
                    text.setTextContent(s.getText());
                    sent.appendChild(text);

                    Element opinionsSent = results.createElement("Opinions");
                    sent.appendChild(opinionsSent);
                    Result resSent = classifier.analyzeText(s.getText());
                    if (resSent.getAspectExpressions().size() == 0) {
                        Element opinion = results.createElement("Opinion");
                        opinion.setAttribute("from", "0");
                        opinion.setAttribute("to", "0");
                        opinion.setAttribute("target", "NULL");
                        opinion.setAttribute("polarity", resSent.getSentiment());
                        opinion.setAttribute("category", resSent.getAspect());
                        opinionsSent.appendChild(opinion);
                    } else {
                        for (AspectExpression aspectExpression : resSent.getAspectExpressions()) {
                            Element opinion = results.createElement("Opinion");
                            opinion.setAttribute("from", aspectExpression.getBegin() + "");
                            opinion.setAttribute("to", aspectExpression.getEnd() + "");
                            opinion.setAttribute("target", aspectExpression.getAspectExpression());
                            opinion.setAttribute("polarity", resSent.getSentiment());
                            opinion.setAttribute("category", resSent.getAspect());
                            opinionsSent.appendChild(opinion);
                        }
                    }
                }
            } else {
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
                        opinion.setAttribute("from", aspectExpression.getBegin() + "");
                        opinion.setAttribute("to", aspectExpression.getEnd() + "");
                        opinion.setAttribute("target", aspectExpression.getAspectExpression());
                        opinion.setAttribute("polarity", res.getSentiment());
                        opinion.setAttribute("category", res.getAspect());
                        opinions.appendChild(opinion);
                    }
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