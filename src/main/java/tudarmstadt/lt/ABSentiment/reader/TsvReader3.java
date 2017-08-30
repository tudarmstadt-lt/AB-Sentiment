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

package tudarmstadt.lt.ABSentiment.reader;

import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.type.Opinion;
import tudarmstadt.lt.ABSentiment.type.Sentence;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * TSV input reader for tab separated input files.<br>
 * The input format is: ID &emsp; text &emsp; optional label
 */
public class TsvReader3 implements InputReader {

    private BufferedReader reader = null;
    private boolean checkedNext = false;
    private boolean hasNext = false;
    private String type;

    private String line;


    /**
     * Creates a Reader using a file name
     * @param filename the path and filename of the input file
     */
    public TsvReader3(String filename, String type) {
        this.type = type;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream(filename), "UTF-8"));
        } catch (Exception e) {
            System.err.println("Stream could not be opened: " + filename + "\nTrying filename...");
            try {
                reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(filename), "UTF-8"));
            } catch (FileNotFoundException e1) {
                System.err.println("File could not be opened: " + filename);
                try {
                    System.err.println("Trying gzipped file: " + filename + ".gz ...");
                    reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename + ".gz")), "UTF-8"));
                } catch (IOException e2) {
                    e2.printStackTrace();
                    System.exit(1);
                }
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            System.err.println("... success.");
        }
    }

    @Override
    public Document next() {
        if (!checkedNext) {
            try {
                line = reader.readLine();
                while (line.isEmpty()) {
                    line = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        checkedNext = false;
        Document currentDoc = buildDocument(line);
        return currentDoc;
    }

    @Override
    public boolean hasNext() {
        if (!checkedNext) {
            checkedNext = true;
            line = "";
            try {
                // skip empty lines for robustness
                while (line.isEmpty()) {
                    line = reader.readLine();
                    if (line == null) {
                        hasNext = false;
                        return false;
                    }
                    hasNext = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                hasNext = false;
            }
        }
        return hasNext;
    }

    /**
     * Creats a {@link Document} from an input line.
     * @param line the input line
     * @return a {@link Document} with text, identifier and label
     */
    private Document buildDocument(String line) {
        Document doc = new Document();

        String[] documentFields = line.split("\\t");
        if (documentFields.length < 2) {
            throw new IllegalArgumentException("The document should at least have 2 fields, with an optional label in the 3rd field!");
        }
        doc.setDocumentId(documentFields[0]);
        Sentence sentence = new Sentence(documentFields[1]);
        sentence.setId(documentFields[0]);
        if(documentFields.length >= 3){
            if(type.equals("sentiment")) {
                sentence.setSentiment(documentFields[2]);
            }else if(type.equals("relevance")){
                sentence.setRelevance(documentFields[2]);
            }else if(type.equals("aspect")){
                ArrayList<Opinion> opinions = new ArrayList<>();
                Opinion opinion = new Opinion(documentFields[2]);
                opinions.add(opinion);
                sentence.addOpinions(opinions);

            }
        }
        doc.addSentence(sentence);
        return doc;
    }

    @Override
    public Iterator<Document> iterator() {
        return this;
    }
}
