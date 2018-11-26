package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-10-31
 */
public class CnvConfigBrcaAccuTest {
    private String ampliconCopyNumberPredictionAlgorithm;
    private Double simpleCutoffDuplicationValue;
    private Double simpleCutoffDeletionValue;
    private Integer exonCopyNumberPredictionThreshold;

    /**
     * @return ampliconCopyNumberPredictionAlgorithm
     */
    public String getAmpliconCopyNumberPredictionAlgorithm() {
        return ampliconCopyNumberPredictionAlgorithm;
    }

    /**
     * @param ampliconCopyNumberPredictionAlgorithm
     */
    public void setAmpliconCopyNumberPredictionAlgorithm(String ampliconCopyNumberPredictionAlgorithm) {
        this.ampliconCopyNumberPredictionAlgorithm = ampliconCopyNumberPredictionAlgorithm;
    }

    /**
     * @return simpleCutoffDuplicationValue
     */
    public Double getSimpleCutoffDuplicationValue() {
        return simpleCutoffDuplicationValue;
    }

    /**
     * @param simpleCutoffDuplicationValue
     */
    public void setSimpleCutoffDuplicationValue(Double simpleCutoffDuplicationValue) {
        this.simpleCutoffDuplicationValue = simpleCutoffDuplicationValue;
    }

    /**
     * @return simpleCutoffDeletionValue
     */
    public Double getSimpleCutoffDeletionValue() {
        return simpleCutoffDeletionValue;
    }

    /**
     * @param simpleCutoffDeletionValue
     */
    public void setSimpleCutoffDeletionValue(Double simpleCutoffDeletionValue) {
        this.simpleCutoffDeletionValue = simpleCutoffDeletionValue;
    }

    /**
     * @return exonCopyNumberPredictionThreshold
     */
    public Integer getExonCopyNumberPredictionThreshold() {
        return exonCopyNumberPredictionThreshold;
    }

    /**
     * @param exonCopyNumberPredictionThreshold
     */
    public void setExonCopyNumberPredictionThreshold(Integer exonCopyNumberPredictionThreshold) {
        this.exonCopyNumberPredictionThreshold = exonCopyNumberPredictionThreshold;
    }
}
