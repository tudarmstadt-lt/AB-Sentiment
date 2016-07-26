package tudarmstadt.lt.ABSentiment.reader;

import tudarmstadt.lt.ABSentiment.type.Document;

import java.util.Iterator;

/**
 * Created by eugen on 7/1/16.
 */
public interface InputReader extends Iterable<Document>, Iterator<Document> {

    boolean hasNext();

    Document next();



}
