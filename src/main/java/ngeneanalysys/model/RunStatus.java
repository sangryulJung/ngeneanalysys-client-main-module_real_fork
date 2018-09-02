package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-04-05
 */
public class RunStatus {
    private String status;
    private String statusMsg;
    private Integer sampleCount;
    private Integer completeCount;
    private Integer runningCount;
    private Integer queuedCount;
    private Integer failedCount;
    private Integer progressPercentage;

    /**
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return statusMsg
     */
    public String getStatusMsg() {
        return statusMsg;
    }

    /**
     * @return sampleCount
     */
    public Integer getSampleCount() {
        return sampleCount;
    }

    /**
     * @return completeCount
     */
    public Integer getCompleteCount() {
        return completeCount;
    }

    /**
     * @return runningCount
     */
    public Integer getRunningCount() {
        return runningCount;
    }

    /**
     * @return queuedCount
     */
    public Integer getQueuedCount() {
        return queuedCount;
    }

    /**
     * @return failedCount
     */
    public Integer getFailedCount() {
        return failedCount;
    }

    /**
     * @return progressPercentage
     */
    public Integer getProgressPercentage() {
        return progressPercentage;
    }
}
