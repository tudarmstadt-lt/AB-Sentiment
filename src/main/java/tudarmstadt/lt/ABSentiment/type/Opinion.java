package tudarmstadt.lt.ABSentiment.type;


public class Opinion {

    private char categorySep = '#';
    private String categoryCoarse;
    private String categoryFine;

    public void setPolarity(String polarity) {
        this.polarity = polarity;
    }

    private String polarity;
    private String target;

    public Opinion(String category) {
        this.categoryFine = category;
        this.categoryCoarse = extractCoarseCategory(category);
    }

    public Opinion(String categoryFine, String polarity) {
        this.categoryFine = categoryFine;
        this.polarity = polarity;

        if (categoryFine != null) {
            this.categoryCoarse = extractCoarseCategory(categoryFine);
        }

    }

    public Opinion(String category, String polarity, String target) {

        this.categoryFine = category;
        this.polarity = polarity;
        this.target = target;

        this.categoryCoarse = extractCoarseCategory(categoryFine);
    }

    private String extractCoarseCategory(String categoryFine) {
        if (categoryFine.indexOf(categorySep) == -1) {
            return categoryFine;
        }
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

    public String getPolarity() throws NoSuchFieldException {
        if (polarity != null) {
            return polarity;
        } else {
            throw new NoSuchFieldException("The polarity is not set");
        }
    }

    public void setTarget(String t) {
        this.target = t;
    }

    public String getTarget() {
        return target;
    }

    public String toString() {
        return "coarse: " + categoryCoarse + "\tfine: "+ categoryFine + "\tpolarity: " + polarity;
    }

    public void setCategorySeparator(char separator) {
        this.categorySep = separator;
    }
}
