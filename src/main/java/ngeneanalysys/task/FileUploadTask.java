package ngeneanalysys.task;

import javafx.concurrent.Task;

public abstract class FileUploadTask<T> extends Task<T> {

    public void updateProgress(long workDone, long max) {
        super.updateProgress(workDone, max);
    }

    public void updateProgress(double workDone, double max) {
        super.updateProgress(workDone, max);
    }
}
