package tudarmstadt.lt.ABSentiment.util;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import tudarmstadt.lt.ABSentiment.featureExtractor.util.Pair;
import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.XMLReader;
import tudarmstadt.lt.ABSentiment.training.aspecttarget.AspectTermWriter;
import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.type.Sentence;
import tudarmstadt.lt.ABSentiment.type.uima.GoldAspectTarget;
import tudarmstadt.lt.ABSentiment.uimahelper.Preprocessor;

import java.io.*;
import java.util.Set;

public class XMLExtractorTarget {

    public static void main(String[] args) {

        String inputFile = args[0];
        String outputFile = inputFile.replace(".xml", "") + ".conll";
        if (outputFile.startsWith("/")) {
            outputFile = "." + outputFile;
        }

        Writer out = null;

        try {
            OutputStream predStream = new FileOutputStream(outputFile);
            out = new OutputStreamWriter(predStream, "UTF-8");

        } catch (FileNotFoundException | UnsupportedEncodingException e1) {
            e1.printStackTrace();
            System.exit(1);
        }


        InputReader in = new XMLReader(inputFile);

        Preprocessor preprocessor = new Preprocessor();

        for (Document d: in) {
            preprocessor.processText(d.getDocumentText());
            JCas cas = preprocessor.getCas();
            for (Pair<Integer, Integer> o : d.getTargetOffsets()) {
                GoldAspectTarget t = new GoldAspectTarget(cas, o.getFirst(), o.getSecond());
                t.addToIndexes();
            }
            boolean inTarget = false;
            for (Token t : JCasUtil.selectCovered(cas, Token.class, 0, cas.getDocumentText().length())) {
                try {
                    out.append(t.getCoveredText() + "\t");
                    if (JCasUtil.selectCovered(GoldAspectTarget.class, t).size() > 0) {
                        if (inTarget) {
                            out.append("I\n");
                        } else {
                            out.append("B\n");
                            inTarget = true;
                        }
                    } else {
                        out.append("O\n");
                        inTarget = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            try {
                out.append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }

