package ngeneanalysys.model;

import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2017-08-17
 */
public class SampleStatus {

    private String step;

    private String status;

    private String statusMsg;

    private Integer progressPercentage;

    private DateTime uploadStartedAt;

    private DateTime uploadFinishedAt;

    private DateTime pipelineStartedAt;

    private DateTime pipelineFinishedAt;

    private DateTime reportStartedAt;

    private DateTime reportFinishedAt;

    private DateTime jobStartedAt;

    private DateTime jobFinishedAt;

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return statusMsg
     */
    public String getStatusMsg() {
        return statusMsg;
    }

    /**
     * @param statusMsg
     */
    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    /**
     * @return progressPercentage
     */
    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    /**
     * @param progressPercentage
     */
    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    /**
     * @return uploadStartedAt
     */
    public DateTime getUploadStartedAt() {
        return uploadStartedAt;
    }

    /**
     * @param uploadStartedAt
     */
    public void setUploadStartedAt(DateTime uploadStartedAt) {
        this.uploadStartedAt = uploadStartedAt;
    }

    /**
     * @return uploadFinishedAt
     */
    public DateTime getUploadFinishedAt() {
        return uploadFinishedAt;
    }

    /**
     * @param uploadFinishedAt
     */
    public void setUploadFinishedAt(DateTime uploadFinishedAt) {
        this.uploadFinishedAt = uploadFinishedAt;
    }

    /**
     * @return pipelineStartedAt
     */
    public DateTime getPipelineStartedAt() {
        return pipelineStartedAt;
    }

    /**
     * @param pipelineStartedAt
     */
    public void setPipelineStartedAt(DateTime pipelineStartedAt) {
        this.pipelineStartedAt = pipelineStartedAt;
    }

    /**
     * @return pipelineFinishedAt
     */
    public DateTime getPipelineFinishedAt() {
        return pipelineFinishedAt;
    }

    /**
     * @param pipelineFinishedAt
     */
    public void setPipelineFinishedAt(DateTime pipelineFinishedAt) {
        this.pipelineFinishedAt = pipelineFinishedAt;
    }

    /**
     * @return reportStartedAt
     */
    public DateTime getReportStartedAt() {
        return reportStartedAt;
    }

    /**
     * @param reportStartedAt
     */
    public void setReportStartedAt(DateTime reportStartedAt) {
        this.reportStartedAt = reportStartedAt;
    }

    /**
     * @return reportFinishedAt
     */
    public DateTime getReportFinishedAt() {
        return reportFinishedAt;
    }

    /**
     * @param reportFinishedAt
     */
    public void setReportFinishedAt(DateTime reportFinishedAt) {
        this.reportFinishedAt = reportFinishedAt;
    }

    /**
     * @return jobStartedAt
     */
    public DateTime getJobStartedAt() {
        return jobStartedAt;
    }

    /**
     * @param jobStartedAt
     */
    public void setJobStartedAt(DateTime jobStartedAt) {
        this.jobStartedAt = jobStartedAt;
    }

    /**
     * @return jobFinishedAt
     */
    public DateTime getJobFinishedAt() {
        return jobFinishedAt;
    }

    /**
     * @param jobFinishedAt
     */
    public void setJobFinishedAt(DateTime jobFinishedAt) {
        this.jobFinishedAt = jobFinishedAt;
    }

    @Override
    public String toString() {
        return "SampleStatus{" +
                "step='" + step + '\'' +
                ", status='" + status + '\'' +
                ", statusMsg='" + statusMsg + '\'' +
                ", uploadStartedAt=" + uploadStartedAt +
                ", uploadFinishedAt=" + uploadFinishedAt +
                ", pipelineStartedAt=" + pipelineStartedAt +
                ", pipelineFinishedAt=" + pipelineFinishedAt +
                ", reportStartedAt=" + reportStartedAt +
                ", reportFinishedAt=" + reportFinishedAt +
                ", jobStartedAt=" + jobStartedAt +
                ", jobFinishedAt=" + jobFinishedAt +
                '}';
    }
}
