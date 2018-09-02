package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-04-27
 */
public class SpliceVariant {
    private Integer sampleId;
    private String chromosome;
    private Integer startPosition;
    private Integer endPosition;
    private String ref;
    private String alt;
    private Double quality;
    private String filter;
    private Integer altDedup;
    private String svType;
    private Integer altDup;
    private Integer refDedup;
    private Integer refDup;
    private String intergenic;
    private String ant;

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return chromosome
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * @return startPosition
     */
    public Integer getStartPosition() {
        return startPosition;
    }

    /**
     * @return endPosition
     */
    public Integer getEndPosition() {
        return endPosition;
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
     * @return quality
     */
    public Double getQuality() {
        return quality;
    }

    /**
     * @return filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @return altDedup
     */
    public Integer getAltDedup() {
        return altDedup;
    }

    /**
     * @return svType
     */
    public String getSvType() {
        return svType;
    }

    /**
     * @return altDup
     */
    public Integer getAltDup() {
        return altDup;
    }

    /**
     * @return refDedup
     */
    public Integer getRefDedup() {
        return refDedup;
    }

    /**
     * @return refDup
     */
    public Integer getRefDup() {
        return refDup;
    }

    /**
     * @return intergenic
     */
    public String getIntergenic() {
        return intergenic;
    }

    /**
     * @return ant
     */
    public String getAnt() {
        return ant;
    }
}
