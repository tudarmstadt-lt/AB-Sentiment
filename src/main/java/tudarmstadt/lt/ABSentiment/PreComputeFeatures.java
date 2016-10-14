package tudarmstadt.lt.ABSentiment;

import tudarmstadt.lt.ABSentiment.training.precomputation.ComputeIdfScores;
import tudarmstadt.lt.ABSentiment.training.precomputation.ComputeIdfTermsCategory;

/**
 * Precomputation of IDF map and other data used in features.
 */
public class PreComputeFeatures {

    /**
     * Extracts different data for feature extractors.
     * @param args
     */
    public static void main(String[] args) {

        String idfFile = "data/features/idfmap.tsv.gz";
        String corpusFile = "data/corpus/corpus_de.tsv";

        ComputeIdfScores.computeIdfScores(corpusFile, idfFile, 3);

        String relTrainingFile = "data/relevance_train.tsv";
        String relIdfTermsFile = "data/features/relevance_idfterms.tsv";

        String sentTrainingFile = "data/sentiment_train.tsv";
        String sentIdfTermsFile = "data/features/sentiment_idfterms.tsv";

        String aspectTrainingFile = "data/aspect_train.tsv";
        String aspectIdfTermsFile = "data/features/aspect_idfterms.tsv";
        String aspecCoarsetIdfTermsFile = "data/features/aspect_coarse_idfterms.tsv";

        ComputeIdfTermsCategory.computeIdfScores(relTrainingFile, relIdfTermsFile);
        ComputeIdfTermsCategory.computeIdfScores(sentTrainingFile, sentIdfTermsFile);
        ComputeIdfTermsCategory.computeIdfScores(aspectTrainingFile, aspectIdfTermsFile);
        // coarse labels
        ComputeIdfTermsCategory.computeIdfScores(aspectTrainingFile, aspecCoarsetIdfTermsFile, true);
    }

}