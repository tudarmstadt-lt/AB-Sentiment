package tudarmstadt.lt.ABSentiment.featureExtractor.precomputation;

import org.jobimtext.api.struct.IThesaurusDatastructure;
import org.jobimtext.api.struct.Order2;
import org.jobimtext.api.struct.WebThesaurusDatastructure;
import tudarmstadt.lt.ABSentiment.training.util.ProblemBuilder;

import java.io.*;
import java.util.*;

/**
 * Created by abhishek on 3/7/17.
 */
public class ExpandIdfTerms extends ProblemBuilder{

    private String inputFile;
    private String outputFile;
    private int numberOfSimilarWords;
    private int numberOfTermsPerCategory;
    private HashMap<String, ArrayList<String>> categoryTermMapping;
    private HashMap<String, Double> termScoreMapping;
    private HashMap<String, Double> sortedTermScoreMapping;
    private IThesaurusDatastructure<String, String> dt;
    private static List<Order2> similarWords;

    public ExpandIdfTerms(String inputFile, int numberOfSimilarWords, int numberOfTermsPerCategory, String outputFile, String DTConfigurationFile){
        this.inputFile = inputFile;
        this.numberOfSimilarWords = numberOfSimilarWords;
        this.numberOfTermsPerCategory = numberOfTermsPerCategory;
        this.outputFile = outputFile;
        dt = new WebThesaurusDatastructure(DTConfigurationFile);
        dt.connect();
        categoryTermMapping = new HashMap<>();
    }

    public ExpandIdfTerms(String inputFile, int numberOfSimilarWords, int numberOfTermsPerCategory, String outputFile){
        this.inputFile = inputFile;
        this.numberOfSimilarWords = numberOfSimilarWords;
        this.numberOfTermsPerCategory = numberOfTermsPerCategory;
        this.outputFile = outputFile;
        dt = new WebThesaurusDatastructure(DTConfigurationFile);
        dt.connect();
        categoryTermMapping = new HashMap<>();
    }

    public void setTermsPerCategory(){
        String word = "";
        ArrayList<String> terms;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
            word = bufferedReader.readLine();
            int length = 0;
            if(word != null){
                length = word.split("\t").length;
            }
            while(word != null){
                String splitLine[] = word.split("\t");
                if( !categoryTermMapping.containsKey(splitLine[length-1])){
                    terms = new ArrayList<>();
                }else{
                    terms = categoryTermMapping.get(splitLine[length-1]);
                }
                if(terms.size()<numberOfTermsPerCategory){
                    terms.add(splitLine[0]);
                    categoryTermMapping.put(splitLine[length-1], terms);
                }
                word = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getDTExpansions(){
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"));
            for(HashMap.Entry<String, ArrayList<String>> entry: categoryTermMapping.entrySet()){
                termScoreMapping = new HashMap<>();
                for(String term: entry.getValue()){
                    similarWords = dt.getSimilarTerms(term, numberOfSimilarWords+1);
                    int flag = 0;
                    for(Order2 element:similarWords){
                        if(flag == 0){
                            flag = 1;
                        }else{
                            if(termScoreMapping.containsKey(element.key)){
                                termScoreMapping.put(element.key, termScoreMapping.get(element.key)+element.score);
                            }else{
                                termScoreMapping.put(element.key, element.score);
                            }
                        }
                    }
                }

                sortedTermScoreMapping = sortByComparator(termScoreMapping, false);
                for(HashMap.Entry<String, Double> item:sortedTermScoreMapping.entrySet()){
                    writer.write(item.getKey()+"\t"+item.getValue()+"\t"+entry.getKey()+"\n");
                }
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

    private static HashMap<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order) {
        List<HashMap.Entry<String, Double>> list = new LinkedList<>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>()
        {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2){
                if (order){
                    return o1.getValue().compareTo(o2.getValue());
                }else{
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });
        HashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
