package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jang
 * @since 2018-03-07
 */
public class ServerDirectoryViewController extends BaseStageController {
    private static Logger logger = LoggerUtil.getLogger();

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    private APIService apiService;

    @FXML
    private TreeView<String> serverItemTreeView;

    private SampleUploadController sampleUploadController;

    private SampleUploadScreenFirstController sampleUploadScreenFirstController;

    private boolean isRun = false;

    private final Image folderImage = resourceUtil.getImage("/layout/images/delete.png", 18, 18);
    private final Image fastqImage = resourceUtil.getImage("/layout/images/modify.png", 18, 18);

    /**
     * @param run
     */
    public void setRun(boolean run) {
        isRun = run;
    }

    /**
     * @param sampleUploadScreenFirstController
     */
    public void setSampleUploadScreenFirstController(SampleUploadScreenFirstController sampleUploadScreenFirstController) {
        this.sampleUploadScreenFirstController = sampleUploadScreenFirstController;
    }

    /**
     * @param sampleUploadController
     */
    public void setSampleUploadController(SampleUploadController sampleUploadController) {
        this.sampleUploadController = sampleUploadController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("init serverDirectory Controller");

        apiService = APIService.getInstance();

        // Create the dialog Stage
        currentStage = new Stage();
        currentStage.setResizable(false);
        //currentStage.initStyle(StageStyle.DECORATED);
        currentStage.initStyle(StageStyle.TRANSPARENT);
        currentStage.initModality(Modality.APPLICATION_MODAL);
        currentStage.setTitle(CommonConstants.SYSTEM_NAME + " > server folder");
        // OS가 Window인 경우 아이콘 출력.
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            currentStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        currentStage.initOwner(sampleUploadController.getCurrentStage());

        setDirectory();

        // Scene Init
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.showAndWait();
    }

    private void setDirectory() {

        try {
            Map<String, Object> params = new HashMap<>();

            HttpClientResponse response = apiService.get("/serverFileInfo", params, null, false);

            logger.info(response.getContentString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        String rootString = "root";
        TreeItem<String> root = new TreeItem(rootString, new ImageView(folderImage));
        root.setExpanded(true);


        serverItemTreeView.setRoot(root);
        serverItemTreeView.setShowRoot(true);
    }

    @FXML
    public void closeDialog() { currentStage.close(); }

    @FXML
    private void submit() {
        if(serverItemTreeView.getSelectionModel().getSelectedItem() != null) {
            String path = "";
            path = serverItemTreeView.getSelectionModel().getSelectedItem().getValue();
            TreeItem<String> parent = serverItemTreeView.getSelectionModel().getSelectedItem().getParent();
            while (parent != null) {
                path = parent.getValue() +   "/" + path;
                parent = parent.getParent();
            }

            sampleUploadScreenFirstController.setServerItem(path);
            closeDialog();
        } else {
            logger.info("directory item is not selected");
        }
    }

}
