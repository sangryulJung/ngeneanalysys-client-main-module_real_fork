package ngeneanalysys.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.*;
import org.controlsfx.tools.Borders;
import org.slf4j.Logger;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.RunSampleView;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;

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

    private List<CheckBox> checkBoxes;

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
        checkBoxes = new ArrayList<>();
        checkBoxes.add(new CheckBox("bai"));
        checkBoxes.add(new CheckBox("bam"));
        checkBoxes.add(new CheckBox("vcf"));
        checkBoxes.add(new CheckBox("fastq.gz"));
        GridPane grid = new GridPane();
        grid.setVgap(10.0);
        grid.setHgap(10.0);
        grid.getRowConstraints().add(new RowConstraints(30));
        grid.getRowConstraints().add(new RowConstraints(30));
        grid.getColumnConstraints().add(new ColumnConstraints(100));
        grid.getColumnConstraints().add(new ColumnConstraints(100));
        grid.add(checkBoxes.get(0), 0, 0);
        grid.add(checkBoxes.get(1), 0, 1);
        grid.add(checkBoxes.get(2), 1, 0);
        grid.add(checkBoxes.get(3), 1, 1);
        grid.setPrefSize(210, 70);
        Node wrappedCheckBox = Borders.wrap(grid)
                .lineBorder().title("File Types").thickness(1).radius(0, 5, 5, 0).build()
                .emptyBorder().padding(10).buildAll();
        hBox.getChildren().add(wrappedCheckBox);
    }

    @FXML
    public void rawDataDownload() {
        List<String> selectedFileTypes = checkBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList());

        if(!selectedFileTypes.isEmpty()) {
            downloadTask(selectedFileTypes);
        } else {
            DialogUtil.warning("", "Please select file types to download.", mainController.getPrimaryStage(), true);
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
