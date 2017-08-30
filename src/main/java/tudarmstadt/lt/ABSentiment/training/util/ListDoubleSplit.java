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

import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.WritableType;

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

    @Override
    public WritableType getType() {
        return null;
    }

    public List<List<Double>> getData() {
        return this.data;
    }
}
