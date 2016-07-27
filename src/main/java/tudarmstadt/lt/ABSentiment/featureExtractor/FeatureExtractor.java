package tudarmstadt.lt.ABSentiment.featureExtractor;

import de.bwaldvogel.liblinear.Feature;
import tudarmstadt.lt.ABSentiment.type.Document;

/**
 * Created by eugen on 7/22/16.
 */
public interface FeatureExtractor {

    Feature[] extractFeature(Document document);
    int getFeatureCount();
}
