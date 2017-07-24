package tudarmstadt.lt.ABSentiment.featureExtractor.precomputation;

import org.jobimtext.api.struct.IThesaurusDatastructure;
import org.jobimtext.api.struct.Order2;
import org.jobimtext.api.struct.WebThesaurusDatastructure;
import tudarmstadt.lt.ABSentiment.featureExtractor.util.TfidfHelper;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * Created by abhishek on 10/7/17.
 */
public class LexicalExpansion extends ProblemBuilder{

    private IThesaurusDatastructure<String, String> dt;
    private HashMap<String, Integer> posExpansions = null;
    private HashMap<String, Integer> negExpansions = null;
    private HashMap<String, Integer> neuExpansions = null;
    private HashMap<String, Integer> candidateTerms = new HashMap<>();
    private HashMap<String, Float[]> expandedLexicon = new HashMap<>();
    private static List<Order2> similarWords;

    public HashMap expandLexicon(String posFile, String negFile, String neuFile, String DTConfigurationFile, int numberOfSimilarWords, int minimumSeedTermOccurrence, int minimumCorpusFrequencyTerms, String TfIdfFileName, String outputFile){
        dt = new WebThesaurusDatastructure(DTConfigurationFile);
        dt.connect();
        posExpansions = getDTExpansion(posFile, numberOfSimilarWords);
        negExpansions = getDTExpansion(negFile, numberOfSimilarWords);
        neuExpansions = getDTExpansion(neuFile, numberOfSimilarWords);
        getCandidateTerms(posExpansions);
        getCandidateTerms(negExpansions);
        getCandidateTerms(neuExpansions);
        getMinimumCorpusFrequencyTerms(minimumCorpusFrequencyTerms, TfIdfFileName);
        getMinimumSeedTermOccurrences(minimumSeedTermOccurrence);
        getExpandedLexicon(outputFile);
        return expandedLexicon;
    }

    public HashMap expandLexicon(String posFile, String negFile, String neuFile, int numberOfSimilarWords, int minimumSeedTermOccurrence, int minimumCorpusFrequencyTerms, String TfIdfFileName){
        dt = new WebThesaurusDatastructure(DTConfigurationFile);
        dt.connect();
        posExpansions = getDTExpansion(posFile, numberOfSimilarWords);
        negExpansions = getDTExpansion(negFile, numberOfSimilarWords);
        neuExpansions = getDTExpansion(neuFile, numberOfSimilarWords);
        getCandidateTerms(posExpansions);
        getCandidateTerms(negExpansions);
        getCandidateTerms(neuExpansions);
        getMinimumCorpusFrequencyTerms(minimumCorpusFrequencyTerms, TfIdfFileName);
        getMinimumSeedTermOccurrences(minimumSeedTermOccurrence);
        getExpandedLexicon(polarityLexiconFile);
        return expandedLexicon;
    }

    private HashMap<String, Integer> getDTExpansion(String inputFile, int numberOfSimilarWords){
        HashMap<String, Integer> hashMap = null;
        if(inputFile != null) {
            hashMap = new HashMap<>();
            BufferedReader bufferedReader;
            try {
                String word;
                bufferedReader = new BufferedReader(new FileReader(inputFile));
                while ((word = bufferedReader.readLine()) != null) {
                    similarWords = dt.getSimilarTerms(word, numberOfSimilarWords + 1);
                    for (Order2 element : similarWords) {
                        if (hashMap.containsKey(element.key)) {
                            hashMap.put(element.key, hashMap.get(element.key) + 1);
                        } else {
                            hashMap.put(element.key, 1);
                        }
                    }
                }
                bufferedReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hashMap;
    }

    private void getCandidateTerms(HashMap<String, Integer> hashMap){
        if(hashMap != null){
            for(Map.Entry<String, Integer> entry:hashMap.entrySet()){
                if(candidateTerms.containsKey(entry.getKey())){
                    candidateTerms.put(entry.getKey(), candidateTerms.get(entry.getKey())+entry.getValue());
                }else{
                    candidateTerms.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void getMinimumSeedTermOccurrences(int minimumSeedTermOccurrence){
        ArrayList<String> removeList = new ArrayList<>();
        for(Map.Entry<String, Integer> entry: candidateTerms.entrySet()){
            if(entry.getValue() < minimumSeedTermOccurrence){
                removeList.add(entry.getKey());
            }
        }
        for(String element:removeList){
            candidateTerms.remove(element);
        }
    }

    private void getMinimumCorpusFrequencyTerms(int minimumCorpusFrequencyTerms, String fileName){
        TfidfHelper tfidfHelper = new TfidfHelper();
        tfidfHelper.loadIdfList(fileName);
        HashMap<String, Integer> termCorpusFrequency = tfidfHelper.getTokenCorpusFrequency();
        ArrayList<String> removeList = new ArrayList<>();
        for(Map.Entry<String, Integer> entry: candidateTerms.entrySet()){
            if(!termCorpusFrequency.containsKey(entry.getKey())){
                removeList.add(entry.getKey());
            }else if(entry.getValue()<minimumCorpusFrequencyTerms){
                removeList.add(entry.getKey());
            }
        }
        for(String element:removeList){
            candidateTerms.remove(element);
        }
    }

    private void getExpandedLexicon(String outputFile){
        int size = 0, index;
        if(posExpansions != null){size++;}
        if(negExpansions != null){size++;}
        if(neuExpansions != null){size++;}
        Float score[];
        for(Map.Entry<String, Integer> entry:candidateTerms.entrySet()) {
            index = -1;
            score = new Float[size];
            for (int i = 0; i < size; i++) {
                score[i] = 0.0f;
            }
            if ((posExpansions != null)) {
                index++;
                if ((posExpansions.containsKey(entry.getKey()))) {
                    score[index] = ((float) posExpansions.get(entry.getKey())) / entry.getValue();
                }
            }
            if ((negExpansions != null)){
                index++;
                if ((negExpansions.containsKey(entry.getKey()))) {
                    score[index] = ((float) negExpansions.get(entry.getKey())) / entry.getValue();
                }
            }if ((neuExpansions != null)) {
                index++;
                if ((neuExpansions.containsKey(entry.getKey()))) {
                    score[index] = ((float) neuExpansions.get(entry.getKey())) / entry.getValue();
                }
            }
            expandedLexicon.put(entry.getKey(), score);
        }
        try {
            Writer out;
            if (outputFile.endsWith(".gz")) {
                out = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFile)), "UTF-8");
            } else {
                out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(outputFile), "UTF-8"));
            }
            for (Map.Entry<String, Float[]> entry:expandedLexicon.entrySet()) {
                out.write(entry.getKey());
                for(Float item:entry.getValue()){
                    out.write("\t"+item);
                }
                out.write("\n");
            }
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
