package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-04-27
 */
public class TSTCNV {
    private Integer sampleId;
    private String chromosome;
    private Integer startPosition;
    private Integer endPosition;
    private String ref;
    private String alt;
    private String svType;
    private String gene;
    private Double foldChange;

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
     * @return svType
     */
    public String getSvType() {
        return svType;
    }

    /**
     * @return gene
     */
    public String getGene() {
        return gene;
    }

    /**
     * @return foldChange
     */
    public Double getFoldChange() {
        return foldChange;
    }
}
