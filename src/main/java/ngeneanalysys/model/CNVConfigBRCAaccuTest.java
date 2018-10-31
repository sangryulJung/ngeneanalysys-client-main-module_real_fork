package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-10-31
 */
public class CNVConfigBRCAaccuTest {
    private String ampliconCopyNumberPredictionAlgorithm;
    private Double simpleCutoffValue;
    private Integer exonCopyNumberPredictionThreshold;

    /**
     * @return ampliconCopyNumberPredictionAlgorithm
     */
    public String getAmpliconCopyNumberPredictionAlgorithm() {
        return ampliconCopyNumberPredictionAlgorithm;
    }

    /**
     * @return simpleCutoffValue
     */
    public Double getSimpleCutoffValue() {
        return simpleCutoffValue;
    }

    /**
     * @return exonCopyNumberPredictionThreshold
     */
    public Integer getExonCopyNumberPredictionThreshold() {
        return exonCopyNumberPredictionThreshold;
    }
}
