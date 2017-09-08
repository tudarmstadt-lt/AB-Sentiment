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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringUtils;
import org.jblas.FloatMatrix;

/*
 * A Java wrapper for W2v - Only Reads a pre trained model!
 * The original code comes from https://github.com/igorbrigadir/word2vec-java
 */
public class W2vSpace extends GenericWordSpace<FloatMatrix> {

    private static int vectorLength;

    /**
     * Reads a .txt or .txt.gz model. Norm is set to 'true' and header is set to 'false' by default.
     * @param word2vecModel path containing the model
     * @return a model containing the word representation of all the words
     */
    public W2vSpace loadText(String word2vecModel) {
        return loadText(word2vecModel, true, false);
    }

    /**
     * Reads a .txt or .txt.gz model
     * @param word2vecModel path containing the model
     * @param norm specifies if the word representation is to be normalised
     * @param header specifies if the first word and it's representation is to be printed
     * @return a model containing the word representation of all the words
     */
    public W2vSpace loadText(String word2vecModel, boolean norm, boolean header) {
        W2vSpace model = new W2vSpace();
        try {
            Reader decoder;
            if (word2vecModel.endsWith("gz")) {
                decoder = new InputStreamReader(new GZIPInputStream(new FileInputStream(word2vecModel)), "UTF-8");
            } else {
                decoder = new InputStreamReader(new FileInputStream(word2vecModel), "UTF-8");
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
            int vecSize = model.store.entrySet().iterator().next().getValue().length;
            vectorLength = vecSize;
            System.out.println(String.format(word2vecModel + " Loaded %s words, vector size %s", numWords, vecSize));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return model;
    }

    /**
     * Reads a binary model. Norm is set to 'true' by default.
     * @param word2vecModel path containing the binary model
     * @return a model containing the word representation of all the words in the vocabulary
     */
    public static W2vSpace load(String word2vecModel) {
        return load(word2vecModel, true);
    }

    /**
     * Reads a binary model. Norm is specified.
     * @param word2vecModel path containing the binary model
     * @param norm specifies if the word representation is to be normalised
     * @return a model containing the word representation of all the words in the vocabulary
     */
    public static W2vSpace load(String word2vecModel, boolean norm) {
        W2vSpace model = new W2vSpace();
        DataInputStream ds;
        try {
            if (word2vecModel.endsWith("bin.gz")) {
                ds = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(word2vecModel)), 131072));

            } else {
                ds = new DataInputStream(new BufferedInputStream(new FileInputStream(word2vecModel), 131072));
            }
            // Read header:
            int numWords = Integer.parseInt(readString(ds));
            int vecSize = Integer.parseInt(readString(ds));
            for (int i = 0; i < numWords; i++) {
                // Word:
                String word = readString(ds);
                // Unit Vector
                FloatMatrix f = new FloatMatrix(readFloatVector(ds, vecSize));
                if (norm) {
                    f = VectorMath.normalize(f);
                }
                model.store.put(word, f);
            }
            vectorLength = vecSize;
            System.out.println(String.format("Loaded %s words, vector size %s", numWords, vecSize));
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load model: " + word2vecModel);
            e.printStackTrace();
        }
        return model;
    }

    /**
     * Read a string from the binary model (System default should be UTF-8)
     * @param ds input data stream
     * @return a String from the model
     */
    public static String readString(DataInputStream ds) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        while (true) {
            byte byteValue = ds.readByte();
            if ((byteValue != 32) && (byteValue != 10)) {
                byteBuffer.write(byteValue);
            } else if (byteBuffer.size() > 0) {
                break;
            }
        }
        String word = byteBuffer.toString();
        byteBuffer.close();
        return word;
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
                float d = Float.parseFloat(line[j]);
                vector[j - 1] = d;
            } catch (NumberFormatException e) {
                System.err.println("ERROR Parsing: " + line + " " + e.getMessage());
                vector[j - 1] = 0.0f;
            }
        }
        return vector;
    }

    /**
     * Read a Vector - Array of Floats from the binary model
     * @param ds input data stream
     * @param vectorSize length of each word vector
     * @return an array of float containing the word vector representation
     */
    public static float[] readFloatVector(DataInputStream ds, int vectorSize) throws IOException {
        // Vector is an Array of Floats...
        float[] vector = new float[vectorSize];
        // Floats stored as 4 bytes
        byte[] vectorBuffer = new byte[4 * vectorSize];
        // Read the full vector in a single chunk:
        ds.read(vectorBuffer);
        // Parse bytes into floats
        for (int i = 0; i < vectorSize; i++) {
            // & with 0xFF to get unsigned byte value as int
            int byte1 = (vectorBuffer[(i * 4) + 0] & 0xFF) << 0;
            int byte2 = (vectorBuffer[(i * 4) + 1] & 0xFF) << 8;
            int byte3 = (vectorBuffer[(i * 4) + 2] & 0xFF) << 16;
            int byte4 = (vectorBuffer[(i * 4) + 3] & 0xFF) << 24;
            // Encode the 4 byte values (0-255) above into a single int
            // Reverse bytes for endian compatibility
            int reverseBytes = (byte1 | byte2 | byte3 | byte4);
            vector[i] = Float.intBitsToFloat(reverseBytes);
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