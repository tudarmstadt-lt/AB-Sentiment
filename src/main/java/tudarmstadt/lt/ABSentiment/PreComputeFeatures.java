package tudarmstadt.lt.ABSentiment;

import tudarmstadt.lt.ABSentiment.featureExtractor.precomputation.ComputeCorpusIdfScores;
import tudarmstadt.lt.ABSentiment.featureExtractor.precomputation.ComputeMaxDocumentLength;

import static tudarmstadt.lt.ABSentiment.featureExtractor.precomputation.ComputeIdfTermsCategory.computeIdfScores;

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
        String maxLengthFile = "data/features/max_length";

        ComputeCorpusIdfScores.computeIdfScores(corpusFile, idfFile, 100);
        ComputeMaxDocumentLength.computeMaxDocumentLength(corpusFile, maxLengthFile);

        String trainingFile = "train.tsv";
        String relTrainingFile = "data/relevance_train.tsv";
        String relIdfTermsFile = "data/features/relevance_idfterms.tsv";

        String sentTrainingFile = "data/sentiment_train.tsv";
        String sentIdfTermsFile = "data/features/sentiment_idfterms.tsv";

        String aspectTrainingFile = "data/aspect_train.tsv";
        String aspectIdfTermsFile = "data/features/aspect_idfterms.tsv";
        String aspecCoarsetIdfTermsFile = "data/features/aspect_coarse_idfterms.tsv";

        computeIdfScores(trainingFile, relIdfTermsFile, false, "relevance");
        computeIdfScores(trainingFile, sentIdfTermsFile, false, "sentiment");
        computeIdfScores(trainingFile, aspectIdfTermsFile, false, "aspect");
        // coarse labels
        computeIdfScores(trainingFile, aspecCoarsetIdfTermsFile, true, "aspect");
    }

}