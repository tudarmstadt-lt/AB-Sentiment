package tudarmstadt.lt.ABSentiment.featureExtractor.util;

import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.TsvReader;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;
import tudarmstadt.lt.ABSentiment.type.Document;

import java.io.*;
import java.util.*;

/**
 * Created by abhishek on 28/5/17.
 */
public class DTHelper extends ProblemBuilder{

    protected static W2vSpace w2vSpace = null;

    public static void main(String args[]){
        initialise("configuration.txt");
        if(w2vFile != null){
            w2vSpace = W2vSpace.load(w2vFile, true);
        }
        analyseInputFile(trainFile, testFile, missingWordsFile);

    }

    public static void analyseInputFile(String inputTrainFile, String inputTestFile, String outputFile){
        Set<String> set = new HashSet<>();
        InputReader inputReader;
        if(inputTrainFile != null){
            inputReader = new TsvReader(inputTrainFile);
            for(Document document: inputReader){
                for(String term:document.getDocumentText().split(" ")){
                    if(w2vSpace != null){
                        if((term != null) && (!w2vSpace.contains(term))){
                            set.add(term);
                        }
                    }
                }
            }
        }
        if(inputTestFile != null){
            inputReader = new TsvReader(inputTestFile);
            for(Document document: inputReader){
                for(String term:document.getDocumentText().split(" ")){
                    if(w2vSpace != null){
                        if((term != null) && (!w2vSpace.contains(term))){
                            set.add(term);
                        }
                    }
                }
            }
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"))) {
            for(String item: set){
                writer.write(item+"\n");
            }
            writer.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
