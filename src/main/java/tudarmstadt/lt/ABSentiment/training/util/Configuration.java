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

package tudarmstadt.lt.ABSentiment.training.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Configuration class helps to read the configuration file and build a HashMap
 * Created by abhishek on 22/5/17.
 */
public class Configuration {

    private BufferedReader reader = null;

    /**
     * Reads a configuration file and builds a HashMap containing variable and it's initialization
     * @param fileName path to the configuration file
     * @return a HashMap containing variable and it's initialization
     */
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
