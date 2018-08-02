package ngeneanalysys.model;

import org.joda.time.DateTime;

import java.util.List;

public class SampleView {
    private Integer id;
    private String name;
    private Run run;
    private String memberName;
    private String memberGroupName;
    private String patientId;
    private Panel panel;
    private String diseaseName;
    private String analysisType;
    private String sampleSource;
    private String qcResult;
    private String inputFType;
    private SampleStatus sampleStatus;
    private PipelineVersion pipelineVersion;
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

    public String getName() {
        return name;
    }

    public Run getRun() {
        return run;
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

    public Panel getPanel() {
        return panel;
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

    public PipelineVersion getPipelineVersion() { return pipelineVersion; }

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

    public void setAnalysisResultSummary(AnalysisResultSummary analysisResultSummary) {
        this.analysisResultSummary = analysisResultSummary;
    }

    @Override
    public String toString() {
        return "SampleView{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", run='" + run + '\'' +
                ", memberName='" + memberName + '\'' +
                ", memberGroupName='" + memberGroupName + '\'' +
                ", patientId='" + patientId + '\'' +
                ", panel='" + panel + '\'' +
                ", diseaseName='" + diseaseName + '\'' +
                ", analysisType='" + analysisType + '\'' +
                ", sampleSource='" + sampleSource + '\'' +
                ", qcResult='" + qcResult + '\'' +
                ", inputFType='" + inputFType + '\'' +
                ", sampleStatus=" + sampleStatus +
                ", pipelineVersion='" + pipelineVersion + '\'' +
                ", analysisResultSummary=" + analysisResultSummary +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", deleted=" + deleted +
                ", sampleQCs=" + sampleQCs +
                '}';
    }
}
