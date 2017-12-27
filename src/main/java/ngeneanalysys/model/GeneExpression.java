package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class GeneExpression {
    private Integer id;
    private Integer sampleId;
    private String geneId;
    private String geneName;
    private String chromosome;
    private String strand;
    private Integer startPosition;
    private Integer endPosition;
    private BigDecimal coverage;
    private BigDecimal fpkm;
    private BigDecimal tpm;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return geneId
     */
    public String getGeneId() {
        return geneId;
    }

    /**
     * @return geneName
     */
    public String getGeneName() {
        return geneName;
    }

    /**
     * @return chromosome
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * @return strand
     */
    public String getStrand() {
        return strand;
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
     * @return coverage
     */
    public BigDecimal getCoverage() {
        return coverage;
    }

    /**
     * @return fpkm
     */
    public BigDecimal getFpkm() {
        return fpkm;
    }

    /**
     * @return tpm
     */
    public BigDecimal getTpm() {
        return tpm;
    }
}
