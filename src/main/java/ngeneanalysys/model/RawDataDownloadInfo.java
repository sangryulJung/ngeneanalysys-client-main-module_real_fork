package ngeneanalysys.model;

import java.io.File;

/**
 * @author Jang
 * @since 2018-06-26
 */
public class RawDataDownloadInfo {
    private File folder;
    private RunSampleView runSampleView;
    private String type;

    public RawDataDownloadInfo(File folder, RunSampleView runSampleView, String type) {
        this.folder = folder;
        this.runSampleView = runSampleView;
        this.type = type;
    }

    /**
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return folder
     */
    public File getFolder() {
        return folder;
    }

    /**
     * @param folder
     */
    public void setFolder(File folder) {
        this.folder = folder;
    }

    /**
     * @return runSampleView
     */
    public RunSampleView getRunSampleView() {
        return runSampleView;
    }

    /**
     * @param runSampleView
     */
    public void setRunSampleView(RunSampleView runSampleView) {
        this.runSampleView = runSampleView;
    }
}
