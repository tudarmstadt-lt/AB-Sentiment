package tudarmstadt.lt.ABSentiment.type;

/**
 * Created by eugen on 7/1/16.
 */
public class Opinion {

    private char categorySep = '#';
    private String categoryCoarse;
    private String categoryFine;
    private String polarity;

    public Opinion(String category) {
        this.categoryFine = category;
        this.categoryCoarse = extractCoarseCategory(category);

    }

    public Opinion(String categoryFine, String polarity) {
        this.categoryFine = categoryFine;
        this.polarity = polarity;

        this.categoryCoarse = extractCoarseCategory(categoryFine);
    }

    private String extractCoarseCategory(String categoryFine) {
        return categoryFine.substring(0, categoryFine.indexOf(categorySep));
    }

    public String getCoarseCategory() throws NoSuchFieldException {
        if (categoryCoarse != null) {
            return categoryCoarse;
        } else {
            throw new NoSuchFieldException("The coarse category is not set");
        }
    }

    public String getFineCategory() throws NoSuchFieldException {
        if (categoryFine != null) {
            return categoryFine;
        } else {
            throw new NoSuchFieldException("The fine category is not set");
        }
    }

    public String toString() {
        return "coarse: " + categoryCoarse + "\tfine: "+ categoryFine + "\tpolarity: " + polarity;
    }

    public void setCategorySeparator(char separator) {
        this.categorySep = separator;
    }
}
