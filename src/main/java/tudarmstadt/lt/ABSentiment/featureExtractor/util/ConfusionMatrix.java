package tudarmstadt.lt.ABSentiment.featureExtractor.util;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by abhishek on 12/5/17.
 */
public class ConfusionMatrix {

    ArrayList<String> labels = new ArrayList<>();
    HashMap<Pair<String, String>, Integer> matrix = new HashMap<>();

    public void addLabel(String label){
        labels.add(label);
    }

    public void createMatrix(){
        for(String predictedLabel:labels){
            for(String goldLabel:labels){
                matrix.put(new Pair<>(predictedLabel, goldLabel), 0);
            }
        }
    }

    public void updateMatrix(String predictedLabel, String goldLabel){
        matrix.put(new Pair<>(predictedLabel, goldLabel), matrix.get(new Pair<>(predictedLabel, goldLabel))+1);
    }

    public void printConfusionMatrix(){
        System.out.println("Gold labels      : Left to Right");
        System.out.println("Predicted labels : Top to bottom");
        for(String label:labels){
            System.out.print("\t"+label);
        }
        for(String predictedLabel:labels){
            System.out.print("\n"+predictedLabel);
            for(String goldLabel:labels){
                System.out.print("\t"+matrix.get(new Pair<>(predictedLabel, goldLabel)));
            }
        }
    }

    public int getGoldSumForLabel(String target){
        int result = 0;
        for(String label:labels){
            result+=matrix.get(new Pair<>(label, target));
        }
        return result;
    }

    public int getPredictedSumForLabel(String target){
        int result = 0;
        for(String label:labels){
            result+=matrix.get(new Pair<>(target, label));
        }
        return result;
    }

    public float getRecallForLabel(String label){
        float recall = 0;
        if(matrix.containsKey(new Pair<>(label, label)) && getGoldSumForLabel(label)>0){
            recall = (float)matrix.get(new Pair<>(label, label))/ (float) (getGoldSumForLabel(label));
        }
        return recall;
    }

    public float getPrecisionForLabel(String label){
        float precision = 0;
        if(matrix.containsKey(new Pair<>(label, label)) && getPredictedSumForLabel(label)>0){
            precision = (float) matrix.get(new Pair<>(label, label))/ (float) (getPredictedSumForLabel(label));
        }
        return precision;
    }

    public HashMap<String, Float> getRecallForAllLabels(){
        HashMap<String, Float> recall = new HashMap<>();
        for(String label:labels){
            recall.put(label, getRecallForLabel(label));
        }
        return recall;
    }

    public HashMap<String, Float> getPrecisionForAllLabels(){
        HashMap<String, Float> precision = new HashMap<>();
        for(String label:labels){
            precision.put(label, getPrecisionForLabel(label));
        }
        return precision;
    }

    public int getTruePositive(){
        int result = 0;
        for(String label:labels){
            result+=matrix.get(new Pair<>(label, label));
        }
        return result;
    }


    public HashMap<String, Float> getFMeasureForAllLabels(){
        HashMap<String, Float> fMeasure = new HashMap<>();
        for(String label:labels){
            if((getPrecisionForLabel(label)+getRecallForLabel(label))>0){
                fMeasure.put(label, (2*getPrecisionForLabel(label)*getRecallForLabel(label))/ (getPrecisionForLabel(label)+getRecallForLabel(label)));
            }
        }
        return fMeasure;
    }

    public float getOverallRecall(){
        int num = 0;
        float sum = 0;
        for(String label:labels){
            sum+=getRecallForLabel(label);
            num++;
        }
        return sum/num;
    }


    public float getOverallPrecision(){
        int num = 0;
        float sum = 0;
        for(String label:labels){
            sum+=getPrecisionForLabel(label);
            num++;
        }
        return sum/num;
    }

    public float getOverallFMeasure(){
        return (2*getOverallPrecision()*getOverallRecall())/(getOverallPrecision()+getOverallRecall());
    }

    public float getOverallAccuracy(){
        int truePositive = getTruePositive(), allPrediction = 0;
        for(String label1:labels){
            for(String label2:labels){
                allPrediction+=matrix.get(new Pair<>(label1, label2));
            }
        }
        return ((float) truePositive / (float) allPrediction)*100;
    }
}