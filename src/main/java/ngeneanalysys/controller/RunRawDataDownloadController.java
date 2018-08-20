package ngeneanalysys.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.RunSampleView;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jang
 * @since 2018-06-25
 */
public class RunRawDataDownloadController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private Stage dialogStage;

    private RunSampleView runSampleView;

    @FXML
    private HBox hBox;

    private CheckComboBox<String> checkComboBox;

    /**
     * @param runSampleView RunSampleView
     */
    void setRunSampleView(RunSampleView runSampleView) {
        this.runSampleView = runSampleView;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("init raw data download");
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Raw Data Download");

        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());

        createCheckComboBox();

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }

    private void createCheckComboBox() {
        checkComboBox = new CheckComboBox<>();
        checkComboBox.getItems().addAll("bai", "bam", "vcf", "fastq.gz");
        hBox.getChildren().add(checkComboBox);
    }

    @FXML
    public void rawDataDownload() {
        ObservableList<String> checkItem = checkComboBox.getCheckModel().getCheckedItems();
        if(!checkItem.isEmpty()) {
            List<String> list = new ArrayList<>(checkItem);
            downloadTask(list);
        } else {
            DialogUtil.warning("", "Please select file to delete.", mainController.getPrimaryStage(), true);
        }
    }

    private void downloadTask(List<String> fileType) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File folder = directoryChooser.showDialog(this.getMainApp().getPrimaryStage());

        if(folder != null) {
            mainController.runningRawDataDownload(folder, runSampleView, fileType);
            dialogStage.close();
        }
    }
}
