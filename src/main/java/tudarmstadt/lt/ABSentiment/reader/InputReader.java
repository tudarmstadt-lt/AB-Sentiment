package tudarmstadt.lt.ABSentiment.reader;

import tudarmstadt.lt.ABSentiment.type.Document;

import java.util.Iterator;

/**
 * Interface for input readers.
 */
public interface InputReader extends Iterable<Document>, Iterator<Document> {

    @Override
    boolean hasNext();

    @Override
    Document next();

}
