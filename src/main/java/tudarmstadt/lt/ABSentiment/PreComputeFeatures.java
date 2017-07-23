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

        String idfFile = "data/en/feature/idfmap.tsv.gz";
        String corpusFile = "data/en/corpus/corpus_en.tsv";
        String maxLengthFile = "data/en/feature/max_length";

        ComputeCorpusIdfScores.computeIdfScores(corpusFile, idfFile, 100);System.out.println("1******************");
        ComputeMaxDocumentLength.computeMaxDocumentLength(corpusFile, maxLengthFile);System.out.println("2******************");

//        String trainingFile = "data/train.tsv";
//        String relTrainingFile = "data/en/relevance_train.tsv";
//        String relIdfTermsFile = "data/en/feature/relevance_idfterms.tsv";
//
//        String sentTrainingFile = "data/en/sentiment_train.tsv";
//        String sentIdfTermsFile = "data/en/feature/sentiment_idfterms.tsv";
//
//        String aspectTrainingFile = "data/en/aspect_train.tsv";
//        String aspectIdfTermsFile = "data/en/feature/aspect_idfterms.tsv";
//        String aspecCoarsetIdfTermsFile = "data/en/feature/aspect_coarse_idfterms.tsv";
//
//        computeIdfScores(trainingFile, relIdfTermsFile, false, "relevance");System.out.println("3******************");
//        computeIdfScores(trainingFile, sentIdfTermsFile, false, "sentiment");System.out.println("4******************");
//        computeIdfScores(trainingFile, aspectIdfTermsFile, false, "aspect");System.out.println("5******************");
//        computeIdfScores(trainingFile, aspecCoarsetIdfTermsFile, true, "aspect");System.out.println("6******************");
    }

}