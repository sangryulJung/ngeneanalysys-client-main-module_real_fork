package ngeneanalysys.model;

import org.joda.time.DateTime;

import java.util.List;

public class SampleView {
    private Integer id;
    private String name;
    private Integer runId;
    private String runName;
    private String memberName;
    private String memberGroupName;
    private String patientId;
    private String panelName;
    private String diseaseName;
    private String analysisType;
    private String sampleSource;
    private String qcResult;
    private String inputFType;
    private SampleStatus sampleStatus;
    private SampleSheet sampleSheet;
    private QcData qcData;
    private AnalysisResultSummary analysisResultSummary;
    private DateTime createdAt;
    private DateTime updatedAt;
    private DateTime deletedAt;
    private Integer deleted;
    private List<SampleQC> sampleQCs;

    /**
     * @return sampleQCs
     */
    public List<SampleQC> getSampleQCs() {
        return sampleQCs;
    }

    public Integer getId() {
        return id;
    }

    public Integer getRunId() {
        return runId;
    }

    public String getName() {
        return name;
    }

    public String getRunName() {
        return runName;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberGroupName() {
        return memberGroupName;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPanelName() {
        return panelName;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public String getSampleSource() {
        return sampleSource;
    }

    public String getQcResult() {
        return qcResult;
    }

    public String getInputFType() {
        return inputFType;
    }

    public SampleStatus getSampleStatus() {
        return sampleStatus;
    }

    public SampleSheet getSampleSheet() {
        return sampleSheet;
    }

    public QcData getQcData() {
        return qcData;
    }

    public AnalysisResultSummary getAnalysisResultSummary() {
        return analysisResultSummary;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public DateTime getDeletedAt() {
        return deletedAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    @Override
    public String toString() {
        return "SampleView{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", runId=" + runId +
                ", runName='" + runName + '\'' +
                ", memberName='" + memberName + '\'' +
                ", memberGroupName='" + memberGroupName + '\'' +
                ", patientId='" + patientId + '\'' +
                ", panelName='" + panelName + '\'' +
                ", diseaseName='" + diseaseName + '\'' +
                ", analysisType='" + analysisType + '\'' +
                ", sampleSource='" + sampleSource + '\'' +
                ", qcResult='" + qcResult + '\'' +
                ", inputFType='" + inputFType + '\'' +
                ", sampleStatus=" + sampleStatus +
                ", sampleSheet=" + sampleSheet +
                ", qcData=" + qcData +
                ", analysisResultSummary=" + analysisResultSummary +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", deleted=" + deleted +
                '}';
    }
}
