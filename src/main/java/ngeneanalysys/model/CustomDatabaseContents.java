package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2019-08-08
 */
public class CustomDatabaseContents {
    private String chr;
    private String startPosition;
    private String ref;
    private String alt;
    private String annotation;

    public CustomDatabaseContents(String chr, String startPosition, String ref, String alt, String annotation) {
        this.chr = chr;
        this.startPosition = startPosition;
        this.ref = ref;
        this.alt = alt;
        this.annotation = annotation;
    }

    /**
     * @return chr
     */
    public String getChr() {
        return chr;
    }

    /**
     * @return startPosition
     */
    public String getStartPosition() {
        return startPosition;
    }

    /**
     * @return ref
     */
    public String getRef() {
        return ref;
    }

    /**
     * @return alt
     */
    public String getAlt() {
        return alt;
    }

    /**
     * @return annotation
     */
    public String getAnnotation() {
        return annotation;
    }
}
