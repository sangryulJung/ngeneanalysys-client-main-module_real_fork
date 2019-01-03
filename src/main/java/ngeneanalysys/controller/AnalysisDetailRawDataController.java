package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.model.Panel;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.model.AnalysisFile;
import ngeneanalysys.model.AnalysisFileList;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.service.APIService;
import ngeneanalysys.task.AnalysisResultFileDownloadTask;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-08-29
 */
public class AnalysisDetailRawDataController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    private Panel panel;

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    /** 현재 샘플 정보 객체 */
    private SampleView sample;

    @FXML
    private HBox filterTitleArea;

    @FXML
    private HBox filterList;

    /** 목록 Table View */
    @FXML
    private TableView<AnalysisFile> rawListTableView;

    /** 파일 타입 컬럼 */
    @FXML
    private TableColumn<AnalysisFile,String> typeColumn;

    /** 파일명 컬럼 */
    @FXML
    private TableColumn<AnalysisFile,String> filenameColumn;

    /** 파일 용량 컬럼 */
    @FXML
    private TableColumn<AnalysisFile,String> sizeColumn;

    /** 파일 생성일시 컬럼 */
    @FXML
    private TableColumn<AnalysisFile,String> createdColumn;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        sample = (SampleView)paramMap.get("sampleView");
        panel = (Panel)paramMap.get("panel");

        createCheckBox();
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileType()));
        filenameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(ConvertUtil.convertFileSizeFormat(cellData.getValue().getSize())));
        createdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DateFormatUtils.format(cellData.getValue().getCreatedAt().toDate(), "yyyy/MM/dd")));
        sizeColumn.setComparator((o1, o2) -> {

            String[] item1 = o1.replaceAll(",", "").split(" ");
            String[] item2 = o2.replaceAll(",", "").split(" ");
            BigDecimal value1 = returnData(item1[0] ,item1[1]);
            BigDecimal value2 = returnData(item2[0] ,item2[1]);
            return value1.compareTo(value2);
        });

        getAnalysisFiles();

        currentStage = new Stage();
        currentStage.setResizable(false);
        currentStage.initStyle(StageStyle.DECORATED);
        currentStage.initModality(Modality.APPLICATION_MODAL);
        currentStage.setTitle(CommonConstants.SYSTEM_NAME + " > Data file download");
        // OS가 Window인 경우 아이콘 출력.
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            currentStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        currentStage.initOwner(getMainApp().getPrimaryStage());

        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.showAndWait();
    }

    private void createTableHeader(TableColumn<AnalysisFile, ?> column) {
        HBox hBox = new HBox();
        hBox.setPrefHeight(Double.MAX_VALUE);
        hBox.setAlignment(Pos.CENTER);
        CheckBox box = new CheckBox();
        hBox.getChildren().add(box);
        column.setStyle("-fx-alignment : center");
        column.setSortable(false);
        column.setGraphic(box);

        box.selectedProperty().addListener((list, ov, nv) -> {
            if(rawListTableView.getItems() != null) {
                rawListTableView.getItems().forEach(item -> item.setCheckItem(nv));
                rawListTableView.refresh();
            }
        });

        column.setPrefWidth(50);

        rawListTableView.getColumns().add(0, column);
    }

    private void createCheckBox() {
        TableColumn<AnalysisFile, Boolean> checkBoxColumn = new TableColumn<>("");
        createTableHeader(checkBoxColumn);
        checkBoxColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue() != null ));
        checkBoxColumn.setCellFactory(param -> new BooleanCell());
    }

    private void getAnalysisFiles() {
        Task<Void> task = new Task<Void>() {
            List<AnalysisFile> totalList;
            @Override
            protected Void call() throws Exception {
                Map<String,Object> paramMap = new HashMap<>();
                paramMap.put("sampleId", sample.getId());

                HttpClientResponse response = apiService.get("/analysisFiles", paramMap, null, false);
                totalList = null;
                AnalysisFileList analysisFileList = response.getObjectBeforeConvertResponseToJSON(AnalysisFileList.class);
                totalList = analysisFileList.getResult();
                if(PipelineCode.BRCA_ACCUTEST_PLUS_CMC_DNA.getCode().equals(panel.getCode()) ||
                        PipelineCode.BRCA_ACCUTEST_PLUS_MLPA_DNA.getCode().equals(panel.getCode())) {
                    List<AnalysisFile> files = totalList.stream().filter(item -> item.getName().contains("cnv") ||
                            item.getName().contains("BRCA_exon")).collect(Collectors.toList());
                    totalList.removeAll(files);
                } else if(PipelineCode.HERED_ACCUTEST_AMC_CNV_DNA.getCode().equals(panel.getCode())) {
                    List<AnalysisFile> files = totalList.stream().filter(item -> item.getName().contains("snp_vaf") ||
                            item.getName().contains("gene_coverage")).collect(Collectors.toList());
                    totalList.removeAll(files);
                }
                List<AnalysisFile> files = totalList.stream().filter(item -> item.getName().contains("png") ||
                        item.getName().contains("txt") || item.getName().contains("json")).collect(Collectors.toList());
                totalList.removeAll(files);
                totalList = totalList.stream().sorted(Comparator.comparing(AnalysisFile::getName)).collect(Collectors.toList());

                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                filterTitleArea.setVisible(false);
                filterTitleArea.setPrefWidth(0);
                filterList.getChildren().add(new Label("Download data files of QC report, uploaded fastq, BED files (BAM or VCF formats), and statistics. "));
                ObservableList<AnalysisFile> displayList = null;
                if(totalList != null && !totalList.isEmpty()) {
                    displayList = FXCollections.observableArrayList(totalList);
                }
                // 목록 초기화
                if(rawListTableView.getItems() != null && rawListTableView.getItems().size() > 0) {
                    rawListTableView.getItems().clear();
                }
                rawListTableView.setItems(displayList);
            }

            @Override
            protected void failed() {
                super.failed();
                DialogUtil.showWebApiException(getException(), currentStage);
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private BigDecimal returnData(String value, String unit) {
        BigDecimal returnValue = new BigDecimal(value);
        final BigDecimal unitValue = new BigDecimal(1024);
        switch (unit) {
            case "KB":
                returnValue = returnValue.multiply(unitValue);
                break;
            case "MB":
                returnValue = returnValue.multiply(unitValue)
                        .multiply(unitValue);
                break;
            case "GB":
                returnValue = returnValue.multiply(unitValue)
                        .multiply(unitValue)
                        .multiply(unitValue);
                break;
            case "TB":
                returnValue = returnValue.multiply(unitValue)
                        .multiply(unitValue)
                        .multiply(unitValue)
                        .multiply(unitValue);
                break;
            default:
        }

        return returnValue;
    }

    /**
     * 파일 다운로드
     * @param downloadFiles List<AnalysisFile>
     */
    @SuppressWarnings("static-access")
    public void download(List<AnalysisFile> downloadFiles) {
        // Show save bedFile dialog
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if(mainController.getBasicInformationMap().containsKey("path")) {
            directoryChooser.setInitialDirectory(new File((String) mainController.getBasicInformationMap().get("path")));
        } else {
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        File file = directoryChooser.showDialog(this.getMainApp().getPrimaryStage());

        try {
            if(file != null) {
                mainController.getBasicInformationMap().put("path", file.getAbsolutePath());
                logger.debug(String.format("start download..[%s]", file.getName()));
                String taskID = "DOWN_" + new Random().nextInt();
                Task<Void> task = new AnalysisResultFileDownloadTask(this, downloadFiles, new File(file.getAbsolutePath()), taskID);
                final Thread downloadThread = new Thread(task);
                HBox mainProgressTaskPane = getMainController().getProgressTaskContentArea();

                HBox box = new HBox();
                box.setId(taskID);
                box.getStyleClass().add("general_progress");

                if(mainProgressTaskPane.getChildren().size() > 0) {
                    Label separatorLabel = new Label("|");
                    box.getChildren().add(separatorLabel);
                    box.setMargin(separatorLabel, new Insets(0, 0, 0, 5));
                }

                Label titleLabel = new Label("Download Files");
                ProgressBar progressBar = new ProgressBar();
                progressBar.getStyleClass().add("progress-bar");
                progressBar.progressProperty().bind(task.progressProperty());
                Label messageLabel = new Label();
                messageLabel.textProperty().bind(task.messageProperty());
                Button cancelButton = new Button("cancel");
                cancelButton.getStyleClass().add("btn_cancel_normal");
                cancelButton.setOnAction(event -> {
                    try {
                        logger.error(String.format("download cancel..[%s]", file.getName()));
                        Thread.sleep(100);
                        Platform.runLater(() -> {
                            downloadThread.interrupt();
                            task.cancel();
                            getMainController().removeProgressTaskItemById(taskID);
                        });
                    } catch (Exception e) {
                        logger.error("download cancel failed!!");
                        DialogUtil.error("Failed File Download Cancel.",
                                "An error occurred during the canceling bedFile download.",
                                getMainController().getPrimaryStage(), false);
                    }
                });

                box.getChildren().add(titleLabel);
                box.setMargin(titleLabel, new Insets(0, 0, 0, 5));
                box.getChildren().add(progressBar);
                box.setMargin(progressBar, new Insets(0, 0, 0, 5));
                box.getChildren().add(messageLabel);
                box.setMargin(messageLabel, new Insets(0, 0, 0, 5));
                box.getChildren().add(cancelButton);
                box.setMargin(cancelButton, new Insets(0, 0, 0, 5));

                // 메인 화면 Progress Task 영역에 화면 출력
                mainProgressTaskPane.getChildren().add(box);

                // Thread 실행
                downloadThread.setDaemon(true);
                downloadThread.start();
                currentStage.close();
            }
        } catch (Exception e) {
            DialogUtil.error("Save Fail.", "An error occurred during the download.",
                    getMainController().getPrimaryStage(), false);
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteSelectedFiles() {
        List<AnalysisFile> files = rawListTableView.getItems().stream().filter(AnalysisFile::getCheckItem)
                .collect(Collectors.toList());

        if(!files.isEmpty()) {
            Task<Void> task = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    for(AnalysisFile analysisFile : files) {
                        apiService.delete("analysisFiles/" + analysisFile.getId());
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    getAnalysisFiles();
                }

                @Override
                protected void failed() {
                    super.failed();
                    getException().printStackTrace();
                    DialogUtil.showWebApiException(getException(), currentStage);
                }
            };
            Thread thread = new Thread(task);
            thread.start();
        } else {
            DialogUtil.alert("Selected Files Deletion", "Please select the files to delete.",
                    getMainController().getPrimaryStage(), false);
        }
    }

    @FXML
    public void downloadSelectedFiles() {
        List<AnalysisFile> files = rawListTableView.getItems().stream().filter(AnalysisFile::getCheckItem)
                .collect(Collectors.toList());

        if(!files.isEmpty()) {
            download(files);
        } else {
            DialogUtil.alert("Selected Files Download", "Please select the files to download.",
                    getMainController().getPrimaryStage(), false);
        }

    }

    class BooleanCell extends TableCell<AnalysisFile, Boolean> {
        private CheckBox checkBox = new CheckBox();

        private BooleanCell() {
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                AnalysisFile analysisFile = BooleanCell.this.getTableView().getItems().get(
                        BooleanCell.this.getIndex());
                analysisFile.setCheckItem(newValue);
                checkBox.setSelected(newValue);
            });
        }

        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(empty) {
                setGraphic(null);
                return;
            }

            AnalysisFile analysisFile = BooleanCell.this.getTableView().getItems().get(
                    BooleanCell.this.getIndex());
            checkBox.setSelected(analysisFile.getCheckItem());

            setGraphic(checkBox);
        }
    }

}
