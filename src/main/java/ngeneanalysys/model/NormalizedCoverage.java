package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-11-15
 */
public class NormalizedCoverage {
    private Integer sampleId;
    private String gene;
    private String warning;
    private BigDecimal minReferenceRange;
    private BigDecimal maxReferenceRange;
    private BigDecimal sampleRatio;
    private BigDecimal sampleDepth;
    private String prediction;

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
     * @return warning
     */
    public String getWarning() {
        return warning;
    }

    /**
     * @return minReferenceRange
     */
    public BigDecimal getMinReferenceRange() {
        return minReferenceRange;
    }

    /**
     * @return maxReferenceRange
     */
    public BigDecimal getMaxReferenceRange() {
        return maxReferenceRange;
    }

    /**
     * @return sampleRatio
     */
    public BigDecimal getSampleRatio() {
        return sampleRatio;
    }

    /**
     * @return sampleDepth
     */
    public BigDecimal getSampleDepth() {
        return sampleDepth;
    }

    /**
     * @return prediction
     */
    public String getPrediction() {
        return prediction;
    }
}
