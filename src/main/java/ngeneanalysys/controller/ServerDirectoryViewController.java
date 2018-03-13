package ngeneanalysys.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.ServerFile;
import ngeneanalysys.model.ServerFileInfo;
import ngeneanalysys.service.APIService;
import ngeneanalysys.task.SampleSheetDownloadTask;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
    private TreeView<ServerFile> serverItemTreeView;

    private SampleUploadController sampleUploadController;

    private SampleUploadScreenFirstController sampleUploadScreenFirstController;

    private boolean isRun = false;

    private final Image folderImage = resourceUtil.getImage("/layout/images/P6_3.png", 18, 18);
    private final Image fastqImage = resourceUtil.getImage("/layout/images/P6_4.png", 18, 18);

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
            HttpClientResponse response = apiService.get("/runDir", null, null, false);
            logger.info(response.getContentString());
            ServerFileInfo serverFileInfo = response.getObjectBeforeConvertResponseToJSON(ServerFileInfo.class);
            logger.info(serverFileInfo.toString());

            TreeItem<ServerFile> root = new TreeItem(serverFileInfo.getParent(), new ImageView(folderImage));

            createLeaf(serverFileInfo.getChild(), root);

            serverItemTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue.getValue().getIsFile().equalsIgnoreCase("true")) return;
                try {
                    if(!isRun && (newValue.getChildren() == null || newValue.getChildren().isEmpty())) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("subPath", getPathRemoveRoot(newValue));
                        HttpClientResponse response2 = apiService.get("/runDir", params, null, false);
                        logger.info(response2.getContentString());
                        ServerFileInfo serverFileInfo2 = response2.getObjectBeforeConvertResponseToJSON(ServerFileInfo.class);
                        logger.info(serverFileInfo2.toString());

                        createLeaf(serverFileInfo2.getChild(), newValue);
                    }
                } catch (WebAPIException e) {
                    e.printStackTrace();
                }
            });

            serverItemTreeView.setRoot(root);
            serverItemTreeView.setShowRoot(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createLeaf(List<ServerFile> child, TreeItem<ServerFile> root) {
        if(child == null) return;

        root.setExpanded(true);
        for(ServerFile file : child) {
            TreeItem<ServerFile> leaf = null;
            if(file.getIsFile().equalsIgnoreCase("false")) {
                leaf = new TreeItem(file, new ImageView(folderImage));
            } else if(!isRun){
                leaf = new TreeItem(file, new ImageView(fastqImage));
            }
            if(leaf != null) root.getChildren().add(leaf);
        }

    }

    @FXML
    public void closeDialog() { currentStage.close(); }

    @FXML
    private void submit() {
        if(serverItemTreeView.getSelectionModel().getSelectedItem() != null) {
            String path = "";

            if(isRun) {
                TreeItem<ServerFile> current = serverItemTreeView.getSelectionModel().getSelectedItem();
                sampleUploadScreenFirstController.setServerItem(getPathRemoveRoot(current));
            } else {
                //select fastq files

            }

            closeDialog();
        } else {
            logger.info("directory item is not selected");
        }
    }

    private String getPathRemoveRoot(TreeItem<ServerFile> item) {
        String path = "";

        TreeItem<ServerFile> current = item;
        while (current != null && current != serverItemTreeView.getRoot()) {
            if (path.equalsIgnoreCase("")) {
                path = current.getValue().getName();
            } else {
                path = current.getValue().getName() + "/" + path;
            }
            current = current.getParent();
        }

        return path;
    }

}
