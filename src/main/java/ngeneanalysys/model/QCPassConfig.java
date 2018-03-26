package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-03-26
 */
public class QCPassConfig {
    private Integer totalBasePair;
    private Double q30TrimmedBasePercentage;
    private Double mappedBasePercentage;
    private Double onTargetPercentage;
    private Double onTargetCoverage;
    private Double duplicatedReadsPercentage;
    private Double roiCoveragePercentage;

    /**
     * @return totalBasePair
     */
    public Integer getTotalBasePair() {
        return totalBasePair;
    }

    /**
     * @return q30TrimmedBasePercentage
     */
    public Double getQ30TrimmedBasePercentage() {
        return q30TrimmedBasePercentage;
    }

    /**
     * @return mappedBasePercentage
     */
    public Double getMappedBasePercentage() {
        return mappedBasePercentage;
    }

    /**
     * @return onTargetPercentage
     */
    public Double getOnTargetPercentage() {
        return onTargetPercentage;
    }

    /**
     * @return onTargetCoverage
     */
    public Double getOnTargetCoverage() {
        return onTargetCoverage;
    }

    /**
     * @return duplicatedReadsPercentage
     */
    public Double getDuplicatedReadsPercentage() {
        return duplicatedReadsPercentage;
    }

    /**
     * @return roiCoveragePercentage
     */
    public Double getRoiCoveragePercentage() {
        return roiCoveragePercentage;
    }
}
