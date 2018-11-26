package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-11-15
 */
public class SnpVariantAlleleFraction {
    private Integer sampleId;
    private Integer number;
    private String gene;
    private String dbSnpId;
    private BigDecimal minReferenceHeteroRange;
    private BigDecimal maxReferenceHeteroRange;
    private BigDecimal vaf;
    private Integer depth;
    private String zygosity;
    private String prediction;

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * @return gene
     */
    public String getGene() {
        return gene;
    }

    /**
     * @return dbSnpId
     */
    public String getDbSnpId() {
        return dbSnpId;
    }

    /**
     * @return minReferenceHeteroRange
     */
    public BigDecimal getMinReferenceHeteroRange() {
        return minReferenceHeteroRange;
    }

    /**
     * @return maxReferenceHeteroRange
     */
    public BigDecimal getMaxReferenceHeteroRange() {
        return maxReferenceHeteroRange;
    }

    /**
     * @return vaf
     */
    public BigDecimal getVaf() {
        return vaf;
    }

    /**
     * @return depth
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     * @return zygosity
     */
    public String getZygosity() {
        return zygosity;
    }

    /**
     * @return prediction
     */
    public String getPrediction() {
        return prediction;
    }
}
