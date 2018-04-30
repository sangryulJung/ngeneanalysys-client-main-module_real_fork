package ngeneanalysys.service.fileanalyse;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class PrimerCoverageInfo {
    // 패널 식별 코드
    private String panelCode = null;
    // 읽은 Reads의 수
    private Integer totalReadCount = 0;
    // primer coverage
    private Double coverage = 0.0;
    // total match primer count
    private Integer totalMatchPrimerCount = 0;
    // primer match percentage
    private Double matchPercentage = 0.0;

    public Double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(Double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public String getPanelCode() {
        return panelCode;
    }

    public void setPanelCode(String panelCode) {
        this.panelCode = panelCode;
    }

    public Integer getTotalReadCount() {
        return totalReadCount;
    }

    public void setTotalReadCount(Integer totalReadCount) {
        this.totalReadCount = totalReadCount;
    }

    public Double getCoverage() {
        return coverage;
    }

    public void setCoverage(Double coverage) {
        this.coverage = coverage;
    }

    public Integer getTotalMatchPrimerCount() {
        return totalMatchPrimerCount;
    }

    public void setTotalMatchPrimerCount(Integer totalMatchPrimerCount) {
        this.totalMatchPrimerCount = totalMatchPrimerCount;
    }
}
