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

package tudarmstadt.lt.ABSentiment;


import org.w3c.dom.Element;
import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.reader.XMLReader;
import tudarmstadt.lt.ABSentiment.reader.XMLReaderSemEval;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
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

/**
 * File-based classifier. Reads the file with multiple documents and classifies it. Supports TSV and XML file formats.
 */
public class Classify extends ProblemBuilder{

    private static boolean xmlData = false;

    private static String outputFile;

    private static Writer out;
    private static DocumentBuilder docBuilder;
    private static org.w3c.dom.Document results;
    private static Element documents;

    private static AbSentiment classifier;

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

        classifier = new AbSentiment(configurationFile);


        InputReader fr;

        if (xmlData){
            if (semeval16) {
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
            if (semeval16) {
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
            Transformer transformer;
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
            if (semeval16) {
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