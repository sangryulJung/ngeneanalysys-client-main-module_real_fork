package ngeneanalysys.model;

public class ClinicalEvidence {
    private String levelA;
    private String levelB;
    private String levelC;
    private String levelD;

    public String getLevelA() {
        return levelA;
    }

    public void setLevelA(String levelA) {
        this.levelA = levelA;
    }

    public String getLevelB() {
        return levelB;
    }

    public void setLevelB(String levelB) {
        this.levelB = levelB;
    }

    public String getLevelC() {
        return levelC;
    }

    public void setLevelC(String levelC) {
        this.levelC = levelC;
    }

    public String getLevelD() {
        return levelD;
    }

    public void setLevelD(String levelD) {
        this.levelD = levelD;
    }

    @Override
    public String toString() {
        return "ClinicalEvidence{" +
                "levelA='" + levelA + '\'' +
                ", levelB='" + levelB + '\'' +
                ", levelC='" + levelC + '\'' +
                ", levelD='" + levelD + '\'' +
                '}';
    }
}
