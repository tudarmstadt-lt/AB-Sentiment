package tudarmstadt.lt.ABSentiment.training.util;

import org.datavec.api.split.InputSplit;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ListDoubleSplit implements InputSplit{
    private List<List<Double>> data;

    public ListDoubleSplit(List<List<Double>> data) {
        this.data = data;
    }

    public long length() {
        return (long)this.data.size();
    }

    public URI[] locations() {
        return new URI[0];
    }

    public Iterator<URI> locationsIterator() {
        return Collections.emptyIterator();
    }

    public Iterator<String> locationsPathIterator() {
        return Collections.emptyIterator();
    }

    public void reset() {
    }

    public void write(DataOutput out) throws IOException {
    }

    public void readFields(DataInput in) throws IOException {
    }

    @Override
    public void writeType(DataOutput dataOutput) throws IOException {

    }

    public double toDouble() {
        throw new UnsupportedOperationException();
    }

    public float toFloat() {
        throw new UnsupportedOperationException();
    }

    public int toInt() {
        throw new UnsupportedOperationException();
    }

    public long toLong() {
        throw new UnsupportedOperationException();
    }

    public List<List<Double>> getData() {
        return this.data;
    }
}
