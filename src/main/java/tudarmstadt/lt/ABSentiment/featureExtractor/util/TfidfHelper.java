package tudarmstadt.lt.ABSentiment.featureExtractor.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * Created by abhishek on 1/6/17.
 */
public class TfidfHelper {

    protected static HashMap<Integer, Double> termIdf = new HashMap<>();
    protected static HashMap<String, Integer> tokenIds = new HashMap<>();
    protected static HashMap<Integer, String> tokenStrings = new HashMap<>();
    protected int maxTokenId = 0;

    public void loadIdfList(String fileName) {
        try {
            BufferedReader br;
            if (fileName.endsWith(".gz")) {
                br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(fileName)), "UTF-8"));
            } else {
                br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            }
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokenLine = line.split("\\t");
                int tokenId = ++maxTokenId;
                tokenIds.put(tokenLine[0], tokenId);
                tokenStrings.put(tokenId, tokenLine[0]);
                termIdf.put(tokenId, Double.parseDouble(tokenLine[2]));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
