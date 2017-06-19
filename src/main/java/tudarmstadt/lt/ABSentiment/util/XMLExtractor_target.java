package tudarmstadt.lt.ABSentiment.util;

import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.XMLReader;
import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.type.Sentence;

import java.util.Set;

public class XMLExtractor_target {

    public static void main(String[] args) {

        String inputFile = args[0];

        InputReader in = new XMLReader(inputFile);

        for (Document d: in) {
            for (Sentence s : d.getSentences()) {
                Set<String> targets;
                    targets = s.getTargets();


                String[] sentence = s.getText().split(" ");
                for (String w: sentence) {

                    System.out.print(w + "\t");
                    if (targets.contains(w)) {
                        System.out.println("B");
                    } else {
                        System.out.println("O");
                    }
                }
System.out.println();

            }
        }
    }
}
