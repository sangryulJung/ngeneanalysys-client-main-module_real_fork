package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.AnalysisFile;
import ngeneanalysys.model.AnalysisFileList;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.service.APIService;
import ngeneanalysys.task.AnalysisResultFileDownloadTask;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-08-29
 */
public class AnalysisDetailRawDataController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    /** 현재 샘플 정보 객체 */
    private SampleView sample;

    /** 전체 파일 목록 객체 */
    private List<AnalysisFile> totalList;

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

    /** 파일 다운로드 버튼 컬럼 */
    @FXML
    private TableColumn<AnalysisFile,Object> downloadColumn;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        sample = (SampleView)paramMap.get("sampleView");

        createCheckBox();
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileType()));
        filenameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(ConvertUtil.convertFileSizeFormat(cellData.getValue().getSize())));
        createdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DateFormatUtils.format(cellData.getValue().getCreatedAt().toDate(), "yyyy/MM/dd")));
        downloadColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        downloadColumn.setCellFactory(param -> new TableCell<AnalysisFile, Object>() {
            @Override
            public void updateItem(Object item, boolean empty) {
                if (item != null) {
                    Button button = new Button("Download");
                    button.getStyleClass().add("btn_raw_data_download");
                    button.setOnAction(event -> download((AnalysisFile) item));
                    setGraphic(button);
                } else {
                    setGraphic(null);
                }
            }
        });
        downloadColumn.setSortable(false);

        sizeColumn.setComparator((o1, o2) -> {

            String[] item1 = o1.replaceAll(",", "").split(" ");
            String[] item2 = o2.replaceAll(",", "").split(" ");
            BigDecimal value1 = returnData(item1[0] ,item1[1]);
            BigDecimal value2 = returnData(item2[0] ,item2[1]);

            if(value1.longValue() > value2.longValue()) {
                return 1;
            } else if(value1.longValue() < value2.longValue()) {
                return -1;
            }

            return 0;
        });

        getAnalysisFiles();

        currentStage = new Stage();
        currentStage.setResizable(false);
        currentStage.initStyle(StageStyle.DECORATED);
        currentStage.initModality(Modality.APPLICATION_MODAL);
        currentStage.setTitle(CommonConstants.SYSTEM_NAME + " > Analysis Result Data Download");
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
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("sampleId", sample.getId());
        try {
            HttpClientResponse response = apiService.get("/analysisFiles", paramMap, null, false);
            totalList = null;
            AnalysisFileList analysisFileList = response.getObjectBeforeConvertResponseToJSON(AnalysisFileList.class);
            totalList = analysisFileList.getResult();
            totalList = totalList.stream().sorted(Comparator.comparing(AnalysisFile::getName)).collect(Collectors.toList());

            filterTitleArea.setVisible(false);
            filterTitleArea.setPrefWidth(0);
            filterList.getChildren().add(new Label("Analysis Result Data Download: Uploaded Fastq files, Mapped bedFile(BAM format), Variant bedFile(VCF format), Data QC Report(PDF) "));
            setList("ALL");

        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
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
     * 목록 화면 출력
     * @param tag String
     */
    public void setList(String tag) {
        ObservableList<AnalysisFile> displayList = null;

        if(StringUtils.isEmpty(tag) || "ALL".equals(tag)) {
            if(totalList != null && !totalList.isEmpty()) {
                displayList = FXCollections.observableArrayList(totalList);
            }
        } else {
            if(totalList != null && !totalList.isEmpty()) {
                displayList = FXCollections.observableArrayList();
                for (AnalysisFile item : totalList) {
                    if(tag.equals(item.getFileType())) {
                        displayList.add(item);
                    }
                }
            }
        }

        // 목록 초기화
        if(rawListTableView.getItems() != null && rawListTableView.getItems().size() > 0) {
            rawListTableView.getItems().clear();
        }
        rawListTableView.setItems(displayList);
    }

    /**
     * 파일 다운로드
     * @param resultFile AnalysisFile
     */
    @SuppressWarnings("static-access")
    public void download(AnalysisFile resultFile) {
        logger.debug(resultFile.getName());
        String fileExtension = FilenameUtils.getExtension(resultFile.getName());
        String extensionFilterTypeName = String.format("%s (*.%s)", fileExtension.toUpperCase(), fileExtension);
        String extensionFilterName = String.format("*.%s", fileExtension);

        // Show save bedFile dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(resultFile.getName());
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(extensionFilterTypeName, extensionFilterName));
        File file = fileChooser.showSaveDialog(this.getMainApp().getPrimaryStage());

        try {
            if(file != null) {
                logger.debug(String.format("start download..[%s]", file.getName()));

                Task<Void> task = new AnalysisResultFileDownloadTask(this, resultFile, file);
                final Thread downloadThread = new Thread(task);
                HBox mainProgressTaskPane = getMainController().getProgressTaskContentArea();
                String progressBoxId = "DOWN_" + resultFile.getSampleId() + "_" + resultFile.getId();

                HBox box = new HBox();
                box.setId(progressBoxId);
                box.getStyleClass().add("general_progress");

                if(mainProgressTaskPane.getChildren().size() > 0) {
                    Label separatorLabel = new Label("|");
                    box.getChildren().add(separatorLabel);
                    box.setMargin(separatorLabel, new Insets(0, 0, 0, 5));
                }

                Label titleLabel = new Label("Download File : " + resultFile.getName());
                ProgressBar progressBar = new ProgressBar();
                progressBar.getStyleClass().add("progress-bar");
                progressBar.progressProperty().bind(task.progressProperty());
                Label messageLabel = new Label();
                messageLabel.textProperty().bind(task.messageProperty());
                Button cancelButton = new Button("cancel");
                cancelButton.getStyleClass().add("btn_cancel_normal");
                cancelButton.setOnAction(event -> {
                    if(downloadThread != null) {
                        try {
                            logger.error(String.format("download cancel..[%s]", file.getName()));
                            Thread.sleep(100);
                            Platform.runLater(() -> {
                                downloadThread.interrupt();
                                task.cancel();
                                getMainController().removeProgressTaskItemById(progressBoxId);
                            });
                        } catch (Exception e) {
                            logger.error("download cancel failed!!");
                            DialogUtil.error("Failed File Download Cancel.", "An error occurred during the canceling bedFile download.", getMainController().getPrimaryStage(), false);
                        }
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
            }
        } catch (Exception e) {
            DialogUtil.error("Save Fail.", "An error occurred during the download.", getMainController().getPrimaryStage(), false);
            e.printStackTrace();
        }
    }

    @FXML
    public void fileDelete() {
        List<AnalysisFile> files = rawListTableView.getItems().stream().filter(AnalysisFile::getCheckItem).collect(Collectors.toList());

        if(!files.isEmpty()) {
            for(AnalysisFile analysisFile : files) {
                try {
                    apiService.delete("analysisFiles/" + analysisFile.getId());
                } catch (WebAPIException wae) {
                    logger.debug(wae.getMessage());
                }
            }
            getAnalysisFiles();
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
