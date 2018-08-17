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
    private Double uniformity02Percentage;
    private Double mappingQuality60Percentage;

    /**
     * @param totalBasePair
     */
    public void setTotalBasePair(Integer totalBasePair) {
        this.totalBasePair = totalBasePair;
    }

    /**
     * @param q30TrimmedBasePercentage
     */
    public void setQ30TrimmedBasePercentage(Double q30TrimmedBasePercentage) {
        this.q30TrimmedBasePercentage = q30TrimmedBasePercentage;
    }

    /**
     * @param mappedBasePercentage
     */
    public void setMappedBasePercentage(Double mappedBasePercentage) {
        this.mappedBasePercentage = mappedBasePercentage;
    }

    /**
     * @param onTargetPercentage
     */
    public void setOnTargetPercentage(Double onTargetPercentage) {
        this.onTargetPercentage = onTargetPercentage;
    }

    /**
     * @param onTargetCoverage
     */
    public void setOnTargetCoverage(Double onTargetCoverage) {
        this.onTargetCoverage = onTargetCoverage;
    }

    /**
     * @param duplicatedReadsPercentage
     */
    public void setDuplicatedReadsPercentage(Double duplicatedReadsPercentage) {
        this.duplicatedReadsPercentage = duplicatedReadsPercentage;
    }

    /**
     * @param roiCoveragePercentage
     */
    public void setRoiCoveragePercentage(Double roiCoveragePercentage) {
        this.roiCoveragePercentage = roiCoveragePercentage;
    }

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

    /**
     * @return uniformity02Percentage
     */
    public Double getUniformity02Percentage() {
        return uniformity02Percentage;
    }

    /**
     * @param uniformity02Percentage
     */
    public void setUniformity02Percentage(Double uniformity02Percentage) {
        this.uniformity02Percentage = uniformity02Percentage;
    }

    /**
     * @return mappingQuality60Percentage
     */
    public Double getMappingQuality60Percentage() {
        return mappingQuality60Percentage;
    }

    /**
     * @param mappingQuality60Percentage
     */
    public void setMappingQuality60Percentage(Double mappingQuality60Percentage) {
        this.mappingQuality60Percentage = mappingQuality60Percentage;
    }
}
