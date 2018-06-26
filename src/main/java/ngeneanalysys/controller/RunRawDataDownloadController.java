package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.*;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.RunSampleView;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author Jang
 * @since 2018-06-25
 */
public class RunRawDataDownloadController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private Stage dialogStage;

    private RunSampleView runSampleView;

    /**
     * @param runSampleView
     */
    public void setRunSampleView(RunSampleView runSampleView) {
        this.runSampleView = runSampleView;
    }

    @Override
    public void show(Parent root) throws IOException {
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Raw Data Download");

        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }

    @FXML
    public void baiFileDownload() {
        downloadTask("bai");
    }

    @FXML
    public void bamFileDownload() {
        downloadTask("bam");
    }

    @FXML
    public void vcfFileDownload() {
        downloadTask("vcf");
    }

    @FXML
    public void fastqFileDownload() {
        downloadTask("fastq.gz");
    }

    public void downloadTask(String fileType) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File folder = directoryChooser.showDialog(this.getMainApp().getPrimaryStage());

        if(folder != null) {
            mainController.runningRawDataDownload(folder, runSampleView, fileType);
            dialogStage.close();
        }
    }
}
