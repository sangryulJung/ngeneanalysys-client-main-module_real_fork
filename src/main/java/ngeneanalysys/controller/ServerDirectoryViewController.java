package ngeneanalysys.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.ServerFile;
import ngeneanalysys.model.ServerFileInfo;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.FileUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
     * @param run if true then TST170 Run Mode
     */
    public void setRun(boolean run) {
        isRun = run;
    }

    /**
     * @param sampleUploadScreenFirstController SampleUploadScreenFirstController
     */
    void setSampleUploadScreenFirstController(SampleUploadScreenFirstController sampleUploadScreenFirstController) {
        this.sampleUploadScreenFirstController = sampleUploadScreenFirstController;
    }

    /**
     * @param sampleUploadController SampleUploadController
     */
    void setSampleUploadController(SampleUploadController sampleUploadController) {
        this.sampleUploadController = sampleUploadController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("init serverDirectory Controller");

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
        if (isRun) {
            serverItemTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        } else {
            serverItemTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
        try {
            HttpClientResponse response = apiService.get("/runDir", null, null, false);
            logger.debug(response.getContentString());
            ServerFileInfo serverFileInfo = response.getObjectBeforeConvertResponseToJSON(ServerFileInfo.class);
            logger.debug(serverFileInfo.toString());

            TreeItem<ServerFile> root = new TreeItem<>(serverFileInfo.getParent(), new ImageView(folderImage));

            createLeaf(serverFileInfo.getChild(), root);

            serverItemTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue != null && newValue.getValue() != null && newValue.getValue().getIsFile() != null &&
                        !newValue.getValue().getIsFile()) {
                    try {
                        if(/*!isRun && */(newValue.getChildren() == null || newValue.getChildren().isEmpty())) {
                            Map<String, Object> params = new HashMap<>();
                            params.put("subPath", getPathRemoveRoot(newValue));
                            HttpClientResponse response2 = apiService.get("/runDir", params, null, false);
                            logger.debug(response2.getContentString());
                            ServerFileInfo serverFileInfo2 = response2.getObjectBeforeConvertResponseToJSON(ServerFileInfo.class);
                            logger.debug(serverFileInfo2.toString());

                            createLeaf(serverFileInfo2.getChild(), newValue);
                        }
                    } catch (WebAPIException e) {
                        e.printStackTrace();
                    }
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
        List<ServerFile> sortedServerFiles = child.stream().sorted(Comparator.comparing(ServerFile::getName))
                .collect(Collectors.toList());
        for(ServerFile file : sortedServerFiles) {
            TreeItem<ServerFile> leaf = null;
            if(!file.getIsFile()) {
                leaf = new TreeItem<>(file, new ImageView(folderImage));
            } else if(!isRun && file.getName().toLowerCase().endsWith(".fastq.gz")){
                leaf = new TreeItem<>(file, new ImageView(fastqImage));
            }
            if(leaf != null) root.getChildren().add(leaf);
        }
    }

    @FXML
    public void closeDialog() { currentStage.close(); }

    @FXML
    private void submit() {
        if(serverItemTreeView.getSelectionModel().getSelectedItems() != null &&
                serverItemTreeView.getSelectionModel().getSelectedItems().size() > 0) {

            if(isRun) {
                TreeItem<ServerFile> current = serverItemTreeView.getSelectionModel().getSelectedItem();
                sampleUploadScreenFirstController.setServerRun(getPathRemoveRoot(current));
                closeDialog();
            } else {
                //select FASTQ files
                List<TreeItem<ServerFile>> selectedFiles = serverItemTreeView.getSelectionModel().getSelectedItems();
                if (selectedFiles != null && !selectedFiles.isEmpty()) {
                    List<TreeItem<ServerFile>> filteredFiles = ((ObservableList<TreeItem<ServerFile>>) selectedFiles).filtered(item -> item.getValue().getIsFile());
                    if (filteredFiles != null && !filteredFiles.isEmpty()) {
                        TreeItem<ServerFile> parent = filteredFiles.get(0).getParent();
                        List<TreeItem<ServerFile>> filteredFiles2 = ((ObservableList<TreeItem<ServerFile>>) filteredFiles).filtered(item -> item.getParent() == parent);
                        Optional<TreeItem<ServerFile>> otherFile = filteredFiles.stream().filter(item -> item.getParent() != parent).findFirst();
                        if(filteredFiles2 != null && !filteredFiles2.isEmpty() && !otherFile.isPresent()) {
                            Boolean isValidPair = FileUtil.isValidPairedFastqFiles(
                                    filteredFiles2.stream().map(item -> item.getValue().getName()).collect(Collectors.toList()));
                            if(isValidPair) {
                                sampleUploadScreenFirstController.setServerFASTQ(getPathRemoveRoot(parent),
                                        filteredFiles2.stream().map(TreeItem::getValue).collect(Collectors.toList()));
                                closeDialog();
                            } else {
                                DialogUtil.warning("Server Fastq Files Selection", "Please select valid fastq file pairs.", this.currentStage, true);
                            }
                        } else if (otherFile.isPresent()) {
                            DialogUtil.warning("Server Fastq Files Selection", "Please select only one directory.", this.currentStage, true);
                        } else {
                            DialogUtil.warning("Server Fastq Files Selection", "Please select at least one fastq files pair.", this.currentStage, true);
                        }
                    } else {
                        DialogUtil.warning("Server Fastq Files Selection", "Please select at least one paired fastq files.", this.currentStage, true);
                    }
                } else {
                    DialogUtil.warning("Server Fastq Files Selection", "Please select at least one paired fastq files.", this.currentStage, true);
                }
            }
        } else {
            logger.debug("directory item is not selected");
        }
    }

    private String getPathRemoveRoot(TreeItem<ServerFile> item) {
        StringBuffer path = new StringBuffer();

        TreeItem<ServerFile> current = item;
        while (current != null && current != serverItemTreeView.getRoot()) {
            if (path.toString().equalsIgnoreCase("")) {
                path = new StringBuffer(current.getValue().getName());
            } else {
                path.insert(0, current.getValue().getName() + "/");
            }
            current = current.getParent();
        }

        return path.toString();
    }

}
