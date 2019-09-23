package ngeneanalysys.task;

import javafx.concurrent.Task;

public abstract class FileUploadTask<T> extends Task<T> {
    private int numberOfWork = 0;
    private int completeWorkCount = 0;

    public FileUploadTask(int numberOfWork) {
        this.numberOfWork = numberOfWork;
    }

    /**
     * @return numberOfWork
     */
    public int getNumberOfWork() {
        return numberOfWork;
    }

    /**
     * @return completeWorkCount
     */
    public int getCompleteWorkCount() {
        return completeWorkCount;
    }

    /**
     * @param completeWorkCount
     */
    public void setCompleteWorkCount(int completeWorkCount) {
        this.completeWorkCount = completeWorkCount;
    }

    @Override
    public void updateProgress(long workDone, long max) {
        super.updateProgress(workDone, max);
    }

    @Override
    public void updateProgress(double workDone, double max) {
        super.updateProgress(workDone, max);
    }
}
