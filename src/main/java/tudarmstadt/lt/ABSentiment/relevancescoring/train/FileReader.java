package tudarmstadt.lt.ABSentiment.relevancescoring.train;

import de.bwaldvogel.liblinear.*;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import tudarmstadt.lt.ABSentiment.uimahelper.Tokenizer;

import java.io.*;
import java.util.*;

/**
 * Created by eugen on 5/20/16.
 */
public class FileReader {

    private Tokenizer tokenizer = new Tokenizer();


    HashMap<Integer, Integer> documentFrequency = new HashMap<>();
    HashMap<Integer, Double> termIdf = new HashMap<>();

    HashMap<String, Integer> tokenIds = new HashMap<>();
    int documentCount = 0;


    int maxTokenId = 0;


    Model model;

    public void processFile(String fname) throws UIMAException {


        computeTfIdf(fname);


        Problem problem = new Problem();
        problem.l = documentCount;

        problem.n = maxTokenId;
        problem.x = new Feature[documentCount][];
        problem.y = new double[documentCount];

        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream(fname)));


            String line;
            int docId = 0;
            HashMap<Integer, Integer> tokenCounts = new HashMap<>();
            while ((line = br.readLine()) != null) {
                tokenCounts.clear();
                String[] document = line.split("\\t");

                tokenizer.tokenizeString(document[1]);
                Collection<String> documentText = tokenizer.getTokens();
                for (String token : documentText) {
                    if (token == null) {
                        continue;
                    }

                    int tokenId = tokenIds.get(token);

                    if (tokenCounts.get(tokenId) != null) {
                        tokenCounts.put(tokenId, tokenCounts.get(tokenId) + 1);
                    } else {
                        tokenCounts.put(tokenId, 1);
                    }
                }
                // create new document
                int count;
                double idf;
                double weight;
                double normalizedWeight;
                double norm = 0;

                HashMap<Integer, Double> termWeights = new HashMap<>();
                for (int tokenID : tokenCounts.keySet()) {
                    count = tokenCounts.get(tokenID);
                    idf = termIdf.get(tokenID);
                    weight = count * idf;


                    if (weight > 0.0) {
                        norm += Math.pow(weight, 2);
                        termWeights.put(tokenID, weight);
                    }
                }
                norm = Math.sqrt(norm);

                Feature[] instance = new Feature[maxTokenId];
                ArrayList<Integer> list = new ArrayList<>(termWeights.keySet());
                Collections.sort(list);
                Double w = 0.0;
                for (int i = 0; i < maxTokenId; i++) {

                    w = termWeights.get(i);
                    if (w == null) {
                        w = 0.0;
                    }
                    normalizedWeight = w / norm;
                    instance[i] = new FeatureNode(i + 1, normalizedWeight);
                    // System.out.println(i + 1 + "\t" + normalizedWeight);
                }
                problem.x[docId] = instance;

                problem.y[docId] = Double.parseDouble(document[2]);
                //System.out.println(docId + "\t" + document[2]);
                docId++;
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("training");
        SolverType solver = SolverType.L2R_LR;
        //solver = SolverType.L2R_L2LOSS_SVC_DUAL;
        //solver = SolverType.L1R_L2LOSS_SVC;
        double C = 1.0;
        double eps = 0.01;

        Parameter parameter = new Parameter(solver, C, eps);

        model = Linear.train(problem, parameter);
        File modelFile = new File("src/relevance-model.svm");
        try {
            model.save(modelFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void computeTfIdf(String fname) throws UIMAException {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream(fname)));


            String line;
            HashSet<Integer> documentTokens = new HashSet<>();
            while ((line = br.readLine()) != null) {
                documentCount++;
                documentTokens.clear();
                String[] document = line.split("\\t");
                tokenizer.tokenizeString(document[1]);
                Collection<String> documentText = tokenizer.getTokens();
                for (String token : documentText) {
                    Integer tokenId = tokenIds.get(token);
                    if (tokenId == null) {
                        tokenId = ++maxTokenId;
                        tokenIds.put(token, tokenId);
                    }
                    if (!documentTokens.contains(tokenId)) {
                        documentTokens.add(tokenId);
                        addCounts(tokenId);
                    }
                }
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            // Create file
            FileWriter fstream = new FileWriter("relevance-tf-idf.tsv");
            BufferedWriter out = new BufferedWriter(fstream);
            for (String token : tokenIds.keySet()) {
                int tokenId = tokenIds.get(token);
                double idfScore = Math.log(documentCount / documentFrequency.get(tokenId));
                termIdf.put(tokenId, idfScore);
                out.write(token + "\t" + tokenId + "\t" + idfScore + "\n");
            }
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }


    public FileReader() throws UIMAException {

    }

    private void addCounts(Integer tokenID) {
        if (documentFrequency.containsKey(tokenID)) {
            documentFrequency.put(tokenID, documentFrequency.get(tokenID) + 1);
        } else {
            documentFrequency.put(tokenID, 1);
        }
    }


    private static TypeSystemDescription createTypeSystemDescription() {
        return null;
    }


    public Collection<Token> tokenizeString(String input) {
        return null;
    }


    public void testFile(String fname) throws UIMAException {


        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream(fname)));

            File predictionFile = new File(fname.substring(1) + "_predictions");
            OutputStream predStream = new FileOutputStream(predictionFile);
            Writer out = new OutputStreamWriter(predStream);


            String line;
            HashMap<Integer, Integer> tokenCounts = new HashMap<>();
            while ((line = br.readLine()) != null) {
                line.trim();
                out.append(line);
                tokenCounts.clear();
                String[] document = line.split("\\t");
                //String[] documentText = document[1].split("\\s+");

                tokenizer.tokenizeString(document[1]);
                Collection<String> documentText = tokenizer.getTokens();
                for (String token : documentText) {
                    if (token == null) {
                        continue;
                    }

                    Integer tokenId = tokenIds.get(token);
                    if (tokenId == null) {
                        continue;
                    }

                    if (tokenCounts.get(tokenId) != null) {
                        tokenCounts.put(tokenId, tokenCounts.get(tokenId) + 1);
                    } else {
                        tokenCounts.put(tokenId, 1);
                    }
                }
                // create new document
                int count;
                double idf;
                double weight;
                double normalizedWeight;
                double norm = 0;

                HashMap<Integer, Double> termWeights = new HashMap<>();
                for (int tokenID : tokenCounts.keySet()) {
                    count = tokenCounts.get(tokenID);
                    idf = termIdf.get(tokenID);
                    weight = count * idf;


                    if (weight > 0.0) {
                        norm += Math.pow(weight, 2);
                        termWeights.put(tokenID, weight);
                    }
                }
                norm = Math.sqrt(norm);

                Feature[] instance = new Feature[maxTokenId];
                ArrayList<Integer> list = new ArrayList<>(termWeights.keySet());
                Collections.sort(list);
                Double w = 0.0;
                for (int i = 0; i < maxTokenId; i++) {

                    w = termWeights.get(i);
                    if (w == null) {
                        w = 0.0;
                    }
                    normalizedWeight = w / norm;
                    instance[i] = new FeatureNode(i + 1, normalizedWeight);
                    //System.out.println(i + 1 + "\t" + normalizedWeight);
                }

                Linear.enableDebugOutput();
                double prediction = Linear.predict(model, instance);
                //System.out.println(prediction);
                out.append("\t" + prediction + "\n");
            }
            br.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}