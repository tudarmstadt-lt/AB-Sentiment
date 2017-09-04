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

import static org.junit.Assert.assertEquals;


public class TsvReaderTest {
    @org.junit.Test
    public void ReadFormat() {
        InputReader in = new TsvReader("/input_test.tsv");

        // file has 2 documents
        int i = 2;
        for (Document d: in) {
            i--;
        }
        assertEquals(0, i);
    }



}