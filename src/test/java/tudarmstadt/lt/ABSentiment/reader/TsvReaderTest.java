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