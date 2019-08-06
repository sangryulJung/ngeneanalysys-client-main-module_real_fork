package ngeneanalysys.model;

import java.math.BigDecimal;

public class DBNSFP {
    private String siftPrediction;
    private String mutationTasterPrediction;
    private String gerpNrScore;
    private String gerpRsScore;
    private String fathmmPrediction;
    private String lrtPrediction;
    private String mutationAssessorPrediction;
    private String metaSvmPrediction;
    private BigDecimal dbscSnvRfScore;
    private BigDecimal dbscSnvAdaScore;

    /**
     * @return metaSvmPrediction
     */
    public String getMetaSvmPrediction() {
        return metaSvmPrediction;
    }

    /**
     * @return dbscSnvRfScore
     */
    public BigDecimal getDbscSnvRfScore() {
        return dbscSnvRfScore;
    }

    /**
     * @return dbscSnvAdaScore
     */
    public BigDecimal getDbscSnvAdaScore() {
        return dbscSnvAdaScore;
    }

    public String getSiftPrediction() {
        return siftPrediction;
    }

    public String getMutationTasterPrediction() {
        return mutationTasterPrediction;
    }

    public String getGerpNrScore() {
        return gerpNrScore;
    }

    public String getGerpRsScore() {
        return gerpRsScore;
    }

    public String getFathmmPrediction() {
        return fathmmPrediction;
    }

    public String getLrtPrediction() {
        return lrtPrediction;
    }

    public String getMutationAssessorPrediction() {
        return mutationAssessorPrediction;
    }

    @Override
    public String toString() {
        return "DBNSFP{" +
                "siftPrediction='" + siftPrediction + '\'' +
                ", mutationTasterPrediction='" + mutationTasterPrediction + '\'' +
                ", gerpNrScore='" + gerpNrScore + '\'' +
                ", gerpRsScore='" + gerpRsScore + '\'' +
                ", fathmmPrediction='" + fathmmPrediction + '\'' +
                ", lrtPrediction='" + lrtPrediction + '\'' +
                ", mutationAssessorPrediction='" + mutationAssessorPrediction + '\'' +
                ", metaSvmPrediction='" + metaSvmPrediction + '\'' +
                ", dbscSnvRfScore=" + dbscSnvRfScore +
                ", dbscSnvAdaScore=" + dbscSnvAdaScore +
                '}';
    }
}
