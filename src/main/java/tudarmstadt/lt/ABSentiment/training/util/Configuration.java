package tudarmstadt.lt.ABSentiment.training.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by abhishek on 22/5/17.
 */
public class Configuration {

    private BufferedReader reader = null;

    public HashMap<String, String> readConfigurationFile(String fileName){
        HashMap<String, String> config = new HashMap<>();
        String line;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream(fileName), "UTF-8"));
        } catch (Exception e) {
            System.err.println("Stream could not be opened: " + fileName + "\nTrying filename...");
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(1);
            }

        }
        try {
            while ((line = reader.readLine()) != null) {
                String[] catLine = line.split("\\t+");
                if (catLine.length > 1) {
                    config.put(catLine[0].trim(), catLine[1].trim());
                }
            }
            reader.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return config;
    }
}
