package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-10-22
 */
public class BrcaCnvAmplicon {
    private Integer sampleId;
    private String gene;
    private String exon;
    private String amplicon;
    private BigDecimal rawRangeMin;
    private BigDecimal rawRangeMax;
    private BigDecimal distributionRangeMin;
    private BigDecimal distributionRangeMax;
    private Integer referenceMeanDepth;
    private Integer referenceMedianDepth;
    private Integer sampleDepth;
    private BigDecimal sampleRatio;
    private Integer distributionPrediction;
    private Integer rawPrediction;

    /**
     * @return rawPrediction
     */
    public Integer getRawPrediction() {
        return rawPrediction;
    }

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return gene
     */
    public String getGene() {
        return gene;
    }

    /**
     * @return exon
     */
    public String getExon() {
        return exon;
    }

    /**
     * @return amplicon
     */
    public String getAmplicon() {
        return amplicon;
    }

    /**
     * @return rawRangeMin
     */
    public BigDecimal getRawRangeMin() {
        return rawRangeMin;
    }

    /**
     * @return rawRangeMax
     */
    public BigDecimal getRawRangeMax() {
        return rawRangeMax;
    }

    /**
     * @return distributionRangeMin
     */
    public BigDecimal getDistributionRangeMin() {
        return distributionRangeMin;
    }

    /**
     * @return distributionRangeMax
     */
    public BigDecimal getDistributionRangeMax() {
        return distributionRangeMax;
    }

    /**
     * @return referenceMeanDepth
     */
    public Integer getReferenceMeanDepth() {
        return referenceMeanDepth;
    }

    /**
     * @return referenceMedianDepth
     */
    public Integer getReferenceMedianDepth() {
        return referenceMedianDepth;
    }

    /**
     * @return sampleDepth
     */
    public Integer getSampleDepth() {
        return sampleDepth;
    }

    /**
     * @return sampleRatio
     */
    public BigDecimal getSampleRatio() {
        return sampleRatio;
    }

    /**
     * @return distributionPrediction
     */
    public Integer getDistributionPrediction() {
        return distributionPrediction;
    }
}

