package ngeneanalysys.model;

public class SampleAnalysisJobCount {
    private Integer runCount;
    private Integer runningSampleCount;
    private Integer queuedSampleCount;
    private Integer failedSampleCount;
    private Integer completedSampleCount;

    public Integer getRunCount() {
        return runCount;
    }

    public void setRunCount(Integer runCount) {
        this.runCount = runCount;
    }

    public Integer getRunningSampleCount() {
        return runningSampleCount;
    }

    public void setRunningSampleCount(Integer runningSampleCount) {
        this.runningSampleCount = runningSampleCount;
    }

    public Integer getQueuedSampleCount() {
        return queuedSampleCount;
    }

    public void setQueuedSampleCount(Integer queuedSampleCount) {
        this.queuedSampleCount = queuedSampleCount;
    }

    public Integer getFailedSampleCount() {
        return failedSampleCount;
    }

    public void setFailedSampleCount(Integer failedSampleCount) {
        this.failedSampleCount = failedSampleCount;
    }

    public Integer getCompletedSampleCount() {
        return completedSampleCount;
    }

    public void setCompletedSampleCount(Integer completedSampleCount) {
        this.completedSampleCount = completedSampleCount;
    }
}
