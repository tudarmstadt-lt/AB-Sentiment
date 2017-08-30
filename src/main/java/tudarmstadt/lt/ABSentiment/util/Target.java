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

package tudarmstadt.lt.ABSentiment.util;

import com.clearnlp.util.pair.Pair;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.tweet.INT;

import java.util.*;

/**
 * Aspect Target type class used in Document conversion.
 */
public class Target {
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getPolarity() {
        return polarity;
    }

    public void setPolarity(String polarity) {
        this.polarity = polarity;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    String target;
    String polarity;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    String category;
    int begin;
    int end;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    int position;

    public int getOrigPosition() {
        return origPosition;
    }

    public void setOrigPosition(int origPosition) {
        this.origPosition = origPosition;
    }

    int origPosition = -1;

    public Vector<Integer> getRelations() {
        return relations;
    }

    public void setRelations(Vector<Integer> relations) {
        this.relations = relations;
        Set<Integer> toremove = new HashSet<>();
        for (int rId : relations) {
            if (rId == position) {
                toremove.add(rId);
            }
        }
        for (int rId : toremove) {

            this.relations.removeElement(rId);
        }
    }

    Vector<Integer> relations;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    boolean active = true;

    public List<Pair<Integer, Integer>> getPositions() {
        return positions;
    }

    public void setPositions(List<Pair<Integer, Integer>> positions) {
        this.positions = positions;
    }

    public void addPosition(int from, int to) {
        positions.add(new Pair<>(from, to));
    }

    public void addPositions(List<Pair<Integer, Integer>> newPositions) {
        if (!newPositions.isEmpty()) {
            positions.addAll(newPositions);
        }
    }

    List<Pair<Integer, Integer>> positions = new ArrayList<>();

    public boolean isContained(Target previous) {
        for (Pair<Integer, Integer> p : this.getPositions()){
            if (p.o1 == previous.getBegin()|| this.getBegin() == previous.getBegin()) {
                return true;
            }
        }
        return false;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    Integer endPosition;
    public void setEndPosition(int position) {
        endPosition = position;
    }
}
