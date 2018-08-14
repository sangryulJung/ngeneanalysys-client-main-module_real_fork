package ngeneanalysys.model;

public class DBNSFP {
    private String siftPrediction;
    private String mutationTasterPrediction;
    private String gerpNrScore;
    private String gerpRsScore;
    private String fathmmPrediction;
    private String lrtPrediction;
    private String mutationAssessorPrediction;

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
                '}';
    }
}
