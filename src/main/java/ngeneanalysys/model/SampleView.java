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
    private String sampleSource;
    private String qcResult;
    private String inputFType;
    private SampleStatus sampleStatus;
    private PipelineVersion pipelineVersion;
    private Boolean isControl;
    private AnalysisResultSummary analysisResultSummary;
    private DateTime createdAt;
    private DateTime updatedAt;
    private DateTime deletedAt;
    private Integer deleted;
    private List<SampleQC> sampleQCs;

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param run
     */
    public void setRun(Run run) {
        this.run = run;
    }

    /**
     * @param memberName
     */
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    /**
     * @param memberGroupName
     */
    public void setMemberGroupName(String memberGroupName) {
        this.memberGroupName = memberGroupName;
    }

    /**
     * @param patientId
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * @param panel
     */
    public void setPanel(Panel panel) {
        this.panel = panel;
    }

    /**
     * @param diseaseName
     */
    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    /**
     * @param sampleSource
     */
    public void setSampleSource(String sampleSource) {
        this.sampleSource = sampleSource;
    }

    /**
     * @param qcResult
     */
    public void setQcResult(String qcResult) {
        this.qcResult = qcResult;
    }

    /**
     * @param inputFType
     */
    public void setInputFType(String inputFType) {
        this.inputFType = inputFType;
    }

    /**
     * @param sampleStatus
     */
    public void setSampleStatus(SampleStatus sampleStatus) {
        this.sampleStatus = sampleStatus;
    }

    /**
     * @param pipelineVersion
     */
    public void setPipelineVersion(PipelineVersion pipelineVersion) {
        this.pipelineVersion = pipelineVersion;
    }

    /**
     * @param createdAt
     */
    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @param updatedAt
     */
    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @param deletedAt
     */
    public void setDeletedAt(DateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    /**
     * @param deleted
     */
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    /**
     * @param sampleQCs
     */
    public void setSampleQCs(List<SampleQC> sampleQCs) {
        this.sampleQCs = sampleQCs;
    }

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

    /**
     * @return isControl
     */
    public Boolean getIsControl() {
        return isControl;
    }

    /**
     * @param control
     */
    public void setIsControl(Boolean control) {
        isControl = control;
    }

    @Override
    public String toString() {
        return "SampleView{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", run=" + run +
                ", memberName='" + memberName + '\'' +
                ", memberGroupName='" + memberGroupName + '\'' +
                ", patientId='" + patientId + '\'' +
                ", panel=" + panel +
                ", diseaseName='" + diseaseName + '\'' +
                ", sampleSource='" + sampleSource + '\'' +
                ", qcResult='" + qcResult + '\'' +
                ", inputFType='" + inputFType + '\'' +
                ", sampleStatus=" + sampleStatus +
                ", pipelineVersion=" + pipelineVersion +
                ", isControl=" + isControl +
                ", analysisResultSummary=" + analysisResultSummary +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", deleted=" + deleted +
                ", sampleQCs=" + sampleQCs +
                '}';
    }
}
