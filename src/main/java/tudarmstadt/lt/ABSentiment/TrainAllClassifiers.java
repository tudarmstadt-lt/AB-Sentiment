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

package tudarmstadt.lt.ABSentiment;

/**
 * General training class. Calls all model trainers to train on the file specified in a @link{Configuration}.
 */
public class TrainAllClassifiers {

    public static void main(String [] args) {

        tudarmstadt.lt.ABSentiment.training.relevance.Train.main(args);
        tudarmstadt.lt.ABSentiment.training.aspectclass.Train.main(args);
        tudarmstadt.lt.ABSentiment.training.aspectclass.TrainCoarse.main(args);
        tudarmstadt.lt.ABSentiment.training.sentiment.Train.main(args);
        tudarmstadt.lt.ABSentiment.training.aspecttarget.Train.main(args);
    }
}
