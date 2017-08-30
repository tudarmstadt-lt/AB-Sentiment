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

import org.datavec.api.conf.Configuration;
import org.datavec.api.records.Record;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.reader.BaseRecordReader;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListDoubleRecordReader extends BaseRecordReader {
    private List<List<Double>> delimitedData;
    private Iterator<List<Double>> dataIter;
    private Configuration conf;

    public ListDoubleRecordReader() {
    }

    public void initialize(InputSplit split) throws IOException, InterruptedException {
        if(split instanceof ListDoubleSplit) {
            ListDoubleSplit listDoubleSplit = (ListDoubleSplit)split;
            this.delimitedData = listDoubleSplit.getData();
            this.dataIter = this.delimitedData.iterator();
        } else {
            throw new IllegalArgumentException("Illegal type of input split " + split.getClass().getName());
        }
    }

    public void initialize(Configuration conf, InputSplit split) throws IOException, InterruptedException {
        this.initialize(split);
    }

    public List<Writable> next() {
        List<Double> next = (List)this.dataIter.next();
        this.invokeListeners(next);
        List<Writable> ret = new ArrayList();
        Iterator var3 = next.iterator();

        while(var3.hasNext()) {
            Double s = (Double)var3.next();
            ret.add(new DoubleWritable(s));
        }

        return ret;
    }

    public boolean hasNext() {
        return this.dataIter.hasNext();
    }

    public List<String> getLabels() {
        return null;
    }

    public void reset() {
        this.dataIter = this.delimitedData.iterator();
    }

    public List<Writable> record(URI uri, DataInputStream dataInputStream) throws IOException {
        return null;
    }

    public Record nextRecord() {
        return new org.datavec.api.records.impl.Record(this.next(), (RecordMetaData)null);
    }

    public Record loadFromMetaData(RecordMetaData recordMetaData) throws IOException {
        throw new UnsupportedOperationException("Loading from metadata not yet implemented");
    }

    public List<Record> loadFromMetaData(List<RecordMetaData> recordMetaDatas) throws IOException {
        throw new UnsupportedOperationException("Loading from metadata not yet implemented");
    }

    public void close() throws IOException {
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public Configuration getConf() {
        return this.conf;
    }
}
