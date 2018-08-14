package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-08-14
 */
public class DBNSFP {
    private String siftPrediction;
    private String mutationTasterPrediction;
    private BigDecimal gerpNrScore;
    private BigDecimal gerpRsScore;
    private String fathmmPrediction;
    private String lrtPrediction;
    private String mutationAssessorPrediction;

    /**
     * @return siftPrediction
     */
    public String getSiftPrediction() {
        return siftPrediction;
    }

    /**
     * @return mutationTasterPrediction
     */
    public String getMutationTasterPrediction() {
        return mutationTasterPrediction;
    }

    /**
     * @return gerpNrScore
     */
    public BigDecimal getGerpNrScore() {
        return gerpNrScore;
    }

    /**
     * @return gerpRsScore
     */
    public BigDecimal getGerpRsScore() {
        return gerpRsScore;
    }

    /**
     * @return fathmmPrediction
     */
    public String getFathmmPrediction() {
        return fathmmPrediction;
    }

    /**
     * @return lrtPrediction
     */
    public String getLrtPrediction() {
        return lrtPrediction;
    }

    /**
     * @return mutationAssessorPrediction
     */
    public String getMutationAssessorPrediction() {
        return mutationAssessorPrediction;
    }
}
