package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2017-08-17
 */
public class Sample {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("runId")
    private Integer runId;

    @JsonProperty("memberId")
    private Integer memberId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("panelId")
    private Integer panelId;

    @JsonProperty("diseaseId")
    private Integer diseaseId;

    @JsonProperty("sampleSource")
    private String sampleSource;

    @JsonProperty("qcResult")
    private String qcResult;

    @JsonProperty("inputFType")
    private String inputFType;

    @JsonProperty("sampleStatus")
    private SampleStatus sampleStatus;

    @JsonProperty("createdAt")
    private DateTime createdAt;

    @JsonProperty("updatedAt")
    private DateTime updatedAt;

    @JsonProperty("deletedAt")
    private DateTime deletedAt;

    @JsonProperty("deleted")
    private Integer deleted;

    @JsonProperty("sampleSheet")
    private SampleSheet sampleSheet;

    private QcData qcData;

    private AnalysisResultSummary analysisResultSummary;

    /**
     * @return analysisResultSummary
     */
    public AnalysisResultSummary getAnalysisResultSummary() {
        return analysisResultSummary;
    }

    /**
     * @param analysisResultSummary
     */
    public void setAnalysisResultSummary(AnalysisResultSummary analysisResultSummary) {
        this.analysisResultSummary = analysisResultSummary;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return runId
     */
    public Integer getRunId() {
        return runId;
    }

    /**
     * @param runId
     */
    public void setRunId(Integer runId) {
        this.runId = runId;
    }

    /**
     * @return memberId
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * @param memberId
     */
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return panelId
     */
    public Integer getPanelId() {
        return panelId;
    }

    /**
     * @param panelId
     */
    public void setPanelId(Integer panelId) {
        this.panelId = panelId;
    }

    /**
     * @return diseaseId
     */
    public Integer getDiseaseId() {
        return diseaseId;
    }

    /**
     * @param diseaseId
     */
    public void setDiseaseId(Integer diseaseId) {
        this.diseaseId = diseaseId;
    }


    public String getSampleSource() {
        return sampleSource;
    }

    public void setSampleSource(String sampleSource) {
        this.sampleSource = sampleSource;
    }

    /**
     * @return qcResult
     */
    public String getQcResult() {
        return qcResult;
    }

    /**
     * @param qcResult
     */
    public void setQcResult(String qcResult) {
        this.qcResult = qcResult;
    }

    /**
     * @return inputFType
     */
    public String getInputFType() {
        return inputFType;
    }

    /**
     * @param inputFType
     */
    public void setInputFType(String inputFType) {
        this.inputFType = inputFType;
    }

    /**
     * @return sampleStatus
     */
    public SampleStatus getSampleStatus() {
        return sampleStatus;
    }

    /**
     * @param sampleStatus
     */
    public void setSampleStatus(SampleStatus sampleStatus) {
        this.sampleStatus = sampleStatus;
    }

    /**
     * @return createdAt
     */
    public DateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt
     */
    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return updatedAt
     */
    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt
     */
    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return deletedAt
     */
    public DateTime getDeletedAt() {
        return deletedAt;
    }

    /**
     * @param deletedAt
     */
    public void setDeletedAt(DateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    /**
     * @return deleted
     */
    public Integer getDeleted() {
        return deleted;
    }

    /**
     * @param deleted
     */
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    /**
     * @return sampleSheet
     */
    public SampleSheet getSampleSheet() {
        return sampleSheet;
    }

    /**
     * @param sampleSheet
     */
    public void setSampleSheet(SampleSheet sampleSheet) {
        this.sampleSheet = sampleSheet;
    }

    /**
     * @return qcData
     */
    public QcData getQcData() {
        return qcData;
    }

    /**
     * @param qcData
     */
    public void setQcData(QcData qcData) {
        this.qcData = qcData;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "id=" + id +
                ", runId=" + runId +
                ", memberId=" + memberId +
                ", name='" + name + '\'' +
                ", panelId=" + panelId +
                ", diseaseId=" + diseaseId +
                ", sampleSource='" + sampleSource + '\'' +
                ", qcResult='" + qcResult + '\'' +
                ", inputFType='" + inputFType + '\'' +
                ", sampleStatus=" + sampleStatus +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", deleted=" + deleted +
                ", sampleSheet=" + sampleSheet +
                ", qcData=" + qcData +
                ", analysisResultSummary=" + analysisResultSummary +
                '}';
    }
}
