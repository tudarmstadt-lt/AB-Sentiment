package tudarmstadt.lt.ABSentiment.util;

import tudarmstadt.lt.ABSentiment.reader.InputReader;
import tudarmstadt.lt.ABSentiment.reader.XMLReader;
import tudarmstadt.lt.ABSentiment.type.Document;
import tudarmstadt.lt.ABSentiment.type.Sentence;

/**
 * Created by eugen on 12/7/16.
 */
public class XMLExtractor {

    public static void main(String[] args) {
        InputReader in = new XMLReader("data/ABSA16_Laptops_Train_SB1_v2.xml");

        //InputReader in = new XMLReader("data/ABSA16_Restaurants_Train_SB1_v2.xml");
        //InputReader in = new XMLReader("data/EN_LAPT_SB1_TEST_.xml.gold");

        //InputReader in = new XMLReader("data/EN_REST_SB1_TEST.xml.gold");

        for (Document d: in) {
            for (Sentence s : d.getSentences()) {
                String label = null;
                try {
                    label = s.getSentiment();
                    //label = s.getAspectCategories();
                } catch (NoSuchFieldException e) {
                    label = null;
                }
                if (label != null) {
                    System.out.println(s.getId() + "\t" + s.getText() + "\t" + label);
                }

            }
        }
    }
}
