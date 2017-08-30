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

package tudarmstadt.lt.ABSentiment.featureExtractor.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jblas.FloatMatrix;

/**
 * A Java wrapper for GloVe - Only Reads a pre trained model!
 * The original code comes from https://github.com/igorbrigadir/word2vec-java
 */
public class GloVeSpace extends GenericWordSpace<FloatMatrix> {

    private static int vectorLength;

    /**
     * Reads a binary model
     * @param vocabFile vocabulary file, containing all the words in the binary file
     * @param gloVeModel path containing the binary model
     * @param norm specifies if the word representation is to be normalised
     * @return a model containing the word representation of all the words in the vocabulary
     */
    public static GloVeSpace load(String vocabFile, String gloVeModel, boolean norm) {
        GloVeSpace model = new GloVeSpace();
        try {
            FileInputStream in = new FileInputStream(gloVeModel);
            DataInputStream ds = new DataInputStream(new BufferedInputStream(in, 131072));
            List<String> vocab = FileUtils.readLines(new File(vocabFile));
            long numWords = vocab.size();
            // Vector Size = num of bytes in total / 16 / vocab
            int vecSize = (int) (in.getChannel().size() / 16 / numWords) - 1;
            // Word Vectors:
            for (String aVocab : vocab) {
                String word = StringUtils.split(aVocab, ' ')[0];
                float[] vector = readFloatVector(ds, vecSize);
                model.store.put(word, new FloatMatrix(vector));
            }
            // Unit Vectors:
            if (norm) {
                for (Entry<String, FloatMatrix> e : model.store.entrySet()) {
                    model.store.put(e.getKey(), VectorMath.normalize(e.getValue()));
                }
            }
            vectorLength = vecSize;
            System.out.println(String.format("Loaded %s words, vector size %s", numWords, vecSize));
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load model: " + gloVeModel);
            e.printStackTrace();
        }
        return model;
    }

    /**
     * Reads a .txt or .txt.gz model
     * @param gloVeModel path containing the model
     * @param norm specifies if the word representation is to be normalised
     * @param header specifies if the first word and it's representation is to be printed
     * @return a model containing the word representation of all the words
     */
    public static GloVeSpace load(String gloVeModel, boolean norm, boolean header) {
        GloVeSpace model = new GloVeSpace();
        try {
            Reader decoder;
            if (gloVeModel.endsWith("gz")) {
                decoder = new InputStreamReader(new GZIPInputStream(new FileInputStream(gloVeModel)), "UTF-8");
            } else {
                decoder = new InputStreamReader(new FileInputStream(gloVeModel), "UTF-8");
            }
            BufferedReader r = new BufferedReader(decoder);

            long numWords = 0;
            String line;

            if (header) {
                String h = r.readLine();
                System.out.println(h);
            }

            while ((line = r.readLine()) != null) {
                // Split into words:
                String[] wordvec = StringUtils.split(line, ' ');
                if (wordvec.length < 2) {
                    break;
                }
                float[] vec = readFloatVector(wordvec);
                if (norm) {
                    model.store.put(wordvec[0], VectorMath.normalize(new FloatMatrix(vec)));
                } else {
                    model.store.put(wordvec[0], new FloatMatrix(vec));
                }
                numWords++;
            }
            decoder.close();
            r.close();
            int vecSize = model.store.entrySet().iterator().next().getValue().length;
            vectorLength =vecSize;
            System.out.println(String.format("Loaded %s words, vector size %s", numWords, vecSize));

        } catch (IOException e) {
            System.err.println("ERROR: Failed to load model: " + gloVeModel);
            e.printStackTrace();
        }
        return model;
    }

    /**
     * Read a Vector - Array from binary file
     * @param ds input data stream
     * @param vectorSize length of each word vector
     * @return an array of float containing the word vector representation
     */
    private static float[] readFloatVector(DataInputStream ds, int vectorSize) throws IOException {
        float[] vector = new float[vectorSize];
        for (int j = 0; j < vectorSize; j++) {
            long l = ds.readLong();
            float d = (float)(Long.reverseBytes(l));
            vector[j] = d;
        }
        return vector;
    }

    /**
     * Read a Vector - Array from text file
     * @param line a single input line containing the word and it's representation
     * @return an array of float containing the word vector representation
     */
    private static float[] readFloatVector(String[] line) throws IOException {
        int vectorSize = line.length;
        float[] vector = new float[vectorSize - 1];
        for (int j = 1; j < vectorSize; j++) {
            try {
                Float d = Float.parseFloat(line[j]);
                vector[j - 1] = d;
            } catch (NumberFormatException e) {
                System.err.println("ERROR Parsing: " + line + " " + e.getMessage());
                vector[j - 1] = 0.0F;
            }
        }
        return vector;
    }

    @Override
    public int getVectorLength(){
        return vectorLength;
    }

    @Override
    public double cosineSimilarity(FloatMatrix vec1, FloatMatrix vec2) {
        return VectorMath.cosineSimilarity(vec1, vec2);
    }
}