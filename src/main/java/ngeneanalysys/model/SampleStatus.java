package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

/**
 * @author Jang
 * @since 2017-08-17
 */
public class SampleStatus {

    @JsonProperty("uploadStatus")
    private String uploadStatus;

    @JsonProperty("pipelineStatus")
    private String pipelineStatus;

    @JsonProperty("reportStatus")
    private String reportStatus;

    private String statusMsg;

    private DateTime uploadStartedAt;

    private DateTime uploadFinishedAt;

    private DateTime pipelineStartedAt;

    private DateTime pipelineFinishedAt;

    private DateTime reportStartedAt;

    private DateTime reportFinishedAt;

    private DateTime jobStartedAt;

    private DateTime jobFinishedAt;

    /**
     * @return uploadStatus
     */
    public String getUploadStatus() {
        return uploadStatus;
    }

    /**
     * @param uploadStatus
     */
    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    /**
     * @return pipelineStatus
     */
    public String getPipelineStatus() {
        return pipelineStatus;
    }

    /**
     * @param pipelineStatus
     */
    public void setPipelineStatus(String pipelineStatus) {
        this.pipelineStatus = pipelineStatus;
    }

    /**
     * @return reportStatus
     */
    public String getReportStatus() {
        return reportStatus;
    }

    /**
     * @param reportStatus
     */
    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
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
}
