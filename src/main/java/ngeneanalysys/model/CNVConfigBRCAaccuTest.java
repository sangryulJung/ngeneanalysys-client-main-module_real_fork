package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-10-31
 */
public class CNVConfigBRCAaccuTest {
    private String ampliconCopyNumberPredictionAlgorithm;
    private Double simpleCutoffDulplicationValue;
    private Double simpleCutoffDeletionValue;
    private Integer exonCopyNumberPredictionThreshold;

    /**
     * @return ampliconCopyNumberPredictionAlgorithm
     */
    public String getAmpliconCopyNumberPredictionAlgorithm() {
        return ampliconCopyNumberPredictionAlgorithm;
    }

    public Double getSimpleCutoffDulplicationValue() { return simpleCutoffDulplicationValue; }

    public Double getSimpleCutoffDeletionValue() { return simpleCutoffDeletionValue; }

    /**
     * @return exonCopyNumberPredictionThreshold
     */
    public Integer getExonCopyNumberPredictionThreshold() {
        return exonCopyNumberPredictionThreshold;
    }

    public void setAmpliconCopyNumberPredictionAlgorithm(String ampliconCopyNumberPredictionAlgorithm) {
        this.ampliconCopyNumberPredictionAlgorithm = ampliconCopyNumberPredictionAlgorithm;
    }

    public void setSimpleCutoffDulplicationValue(Double simpleCutoffDulplicationValue) {
        this.simpleCutoffDulplicationValue = simpleCutoffDulplicationValue;
    }

    public void setSimpleCutoffDeletionValue(Double simpleCutoffDeletionValue) {
        this.simpleCutoffDeletionValue = simpleCutoffDeletionValue;
    }

    public void setExonCopyNumberPredictionThreshold(Integer exonCopyNumberPredictionThreshold) {
        this.exonCopyNumberPredictionThreshold = exonCopyNumberPredictionThreshold;
    }
}
