package tudarmstadt.lt.ABSentiment.featureExtractor.util;

import java.util.HashSet;
import java.util.Set;

public class WordSpaceUtils {

	/*
	 * Remove vectors from model:
	 */
	public static <T> GenericWordSpace<T> reduceModelVocab(GenericWordSpace<T> model, Set<String> keyVectors) {
		Set<String> vocab = new HashSet<String>(model.store.keySet());
		for (String word : vocab) {
			if (!keyVectors.contains(word)) {
				model.store.remove(word);
			}
		}
		return model;
	}

}
