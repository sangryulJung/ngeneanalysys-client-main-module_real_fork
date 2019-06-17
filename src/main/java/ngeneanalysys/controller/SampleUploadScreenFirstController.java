package ngeneanalysys.controller;

import com.opencsv.CSVReader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import ngeneanalysys.code.AnalysisJobStatusCode;
import ngeneanalysys.code.UserTypeCode;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.code.enums.SampleSourceCode;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedAnalysisFile;
import ngeneanalysys.model.paged.PagedPanel;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.model.render.PanelComboBoxConverter;
import ngeneanalysys.model.render.PanelComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.task.SampleSheetDownloadTask;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-08-10
 */
public class SampleUploadScreenFirstController extends BaseStageController{
    private static Logger logger = LoggerUtil.getLogger();

    /** 메인 화면 컨트롤러 객체 */
    private MainController mainController;

    private SampleUploadController sampleUploadController;

    private List<SampleView> sampleArrayList = null;

    private List<AnalysisFile> failedAnalysisFileList = new ArrayList<>();

    @FXML
    private Label uploadToolTip;

    @FXML
    private Label controlLabel;

    @FXML
    private Button buttonSubmit;

    @FXML
    private GridPane standardDataGridPane;

    @FXML
    protected RadioButton localFastqFilesRadioButton;

    @FXML
    protected RadioButton serverFastqFilesRadioButton;

    @FXML
    protected RadioButton serverRunFolderRadioButton;

    private APIService apiService;

    private List<Panel> panels = null;

    private List<Diseases> diseases = null;

    private Map<String, Map<String, Object>> fileMap = null;

    private List<File> uploadFileList = null;

    private List<AnalysisFile> uploadFileData = null;

    private List<TextField> sampleNameTextFieldList = new ArrayList<>();

    private List<ComboBox<ComboBoxItem>> diseaseComboBoxList = new ArrayList<>();

    private List<ComboBox<PanelComboBoxItem>> panelComboBoxList = new ArrayList<>();

    private List<ComboBox<String>> sampleSourceComboBoxList = new ArrayList<>();

    private List<RadioButton> sampleIsControlList = new ArrayList<>();

    private ToggleGroup isControlGroup = new ToggleGroup();

    private boolean isServerItem = false;

    private boolean isServerFastq = false;

    private String runPath = "";

    private List<PanelView> panelViewList;

    void setServerFASTQ(String path, List<ServerFile> serverFiles) {
        isServerItem = true;
        isServerFastq = true;
        if(!sampleArrayList.isEmpty()) sampleArrayList.clear();
        if(!fileMap.isEmpty()) {
            fileMap.clear();
            uploadFileList.clear();
        }

        runPath = path;

        if(serverFiles.isEmpty()) DialogUtil.alert("Empty Fastq File Directory", "Can not find fastq files in the directory.", sampleUploadController.getCurrentStage(), true);
        setServerFastqList(serverFiles);
        sampleUploadController.setTextFieldRunName(path);

    }

    void setServerRun(String path) {
        isServerItem = true;
        isServerFastq = false;
        logger.debug(path);
        if(sampleArrayList.isEmpty()) sampleArrayList.clear();
        if(!fileMap.isEmpty()) {
            fileMap.clear();
            uploadFileList.clear();
        }
        try {
            runPath = path;
            SampleSheetDownloadTask task = new SampleSheetDownloadTask(this.sampleUploadController, this, path);
            final Thread downloadThread = new Thread(task);

            downloadThread.setDaemon(true);
            downloadThread.start();

            sampleUploadController.setTextFieldRunName(path);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setServerFastqList(List<ServerFile> serverFiles) {
        List<ServerFile> undeterminedFile = new ArrayList<>();
        serverFiles.forEach(file -> {
            if(file.getName().toUpperCase().startsWith("UNDETERMINED_"))
                undeterminedFile.add(file);
        });

        if(!undeterminedFile.isEmpty()) serverFiles.removeAll(undeterminedFile);

        while (!serverFiles.isEmpty()) {
            ServerFile serverFile = serverFiles.get(0);
            String fastqFilePairName = FileUtil.getFASTQFilePairName(serverFile.getName());

            if(StringUtils.isEmpty(fastqFilePairName)) {
                serverFiles.remove(serverFile);
                continue;
            }

            List<ServerFile> pairFileList = serverFiles.stream().filter(file ->
                    file.getName().startsWith(fastqFilePairName + "_")).collect(Collectors.toList());

            //fastq 파일이 짝을 이루고 올리는데 실패한 파일인 경우
           if (pairFileList.size() == 2 && checkSameSample(fastqFilePairName)) {
                sampleArrayList.add(createSampleView(fastqFilePairName));
            }

            if(sampleArrayList.size() == 25) break;

            serverFiles.removeAll(pairFileList);
        }

        tableEdit();
    }

    private SampleView createSampleView(String name) {
        SampleView sampleView = new SampleView();
        if(StringUtils.isNotEmpty(name)) {
            sampleView.setName(name);
        }
        sampleView.setIsControl(false);
        sampleView.setRun(new Run());
        sampleView.setPanel(new Panel());

        return sampleView;
    }

    public void setSampleSheet(String path) {
        if(!sampleArrayList.isEmpty()) sampleArrayList.clear();
        try(CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(path), CommonConstants.ENCODING_TYPE_UTF))) {
            String[] s;
            boolean tableData = false;
            while((s = csvReader.readNext()) != null) {
                if (tableData && sampleArrayList.size() < 25) {
                    final String sampleName = s[0];
                    logger.debug(s[0]);
                    SampleView sample = createSampleView(sampleName);
                    if(s[8] != null && s[8].contains("DNA")) {
                        Optional<Panel> optionalPanel = panels.stream().filter(item -> item.getName().contains("Tumor 170 DNA")).findFirst();
                        optionalPanel.ifPresent(panel -> setPanelInfo(sample, panel));
                    } else if(s[8] != null && s[8].contains("RNA")) {
                        Optional<Panel> optionalPanel = panels.stream().filter(item -> item.getName().contains("Tumor 170 RNA")).findFirst();
                        optionalPanel.ifPresent(panel -> setPanelInfo(sample, panel));
                    }
                    sampleArrayList.add(sample);
                } else if(s[0].equalsIgnoreCase("Sample_ID")) {
                    tableData = true;
                }
            }
            tableEdit();

        } catch (IOException e) {
            DialogUtil.alert("The sample sheet file could not be read.", e.getMessage(), this.getMainApp().getPrimaryStage(), true);
        }

    }

    private void setPanelInfo(SampleView sample, Panel panel) {
        sample.getPanel().setId(panel.getId());
        sample.setSampleSource(panel.getDefaultSampleSource());
        sample.getPanel().setDefaultDiseaseId(panel.getDefaultDiseaseId());
        if(panel.getDefaultDiseaseId() != null)
            sample.getPanel().setDefaultDiseaseId(panel.getDefaultDiseaseId());
    }

    /**
     * @param mainController MainController
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setMainApp(this.mainController.getMainApp());
        apiService = APIService.getInstance();
    }

    /**
     * @param sampleUploadController SampleUploadController
     */
    void setSampleUploadController(SampleUploadController sampleUploadController) {
        this.sampleUploadController = sampleUploadController;
    }

    @Override
    public void show(Parent root) throws IOException {
        runInfoEdit();
        standardDataGridPane.getChildren().clear();
        standardDataGridPane.setPrefHeight(0);
        fileMap = sampleUploadController.getFileMap();
        uploadFileList = sampleUploadController.getUploadFileList();
        uploadFileData= sampleUploadController.getUploadFileData();
        panelViewList = new ArrayList<>();

        settingPanelAndDiseases();

        PopOverUtil.openToolTipPopOver(uploadToolTip,
                "Choose FASTQ sequencing data file");

        if(sampleUploadController.getSamples() != null) {
            sampleArrayList = sampleUploadController.getSamples();
            Platform.runLater(this::tableEdit);
        }
    }

    @SuppressWarnings("unchecked")
    private void settingPanelAndDiseases() {
        // 기본 정보 로드
        HttpClientResponse response;

        LoginSession loginSession = LoginSessionUtil.getCurrentLoginSession();

        try {
            Map<String,Object> params = new HashMap<>();
            if(loginSession.getRole().equalsIgnoreCase(UserTypeCode.USER_TYPE_ADMIN)) {
                params.put("skipOtherGroup", "false");
            } else {
                params.put("skipOtherGroup", "true");
            }
            response = apiService.get("/panels", params, null, false);
            final PagedPanel pagedPanel = response.getObjectBeforeConvertResponseToJSON(PagedPanel.class);
            this.panels = pagedPanel.getResult().stream().sorted(Comparator.comparing(Panel::getName))
                    .collect(Collectors.toList());

            response = apiService.get("/diseases", null, null, false);
            List<Diseases> diseasesList = (List<Diseases>)response.getMultiObjectBeforeConvertResponseToJSON(Diseases.class, false);
            this.diseases = diseasesList.stream().sorted(Comparator.comparing(Diseases::getName))
                    .collect(Collectors.toList());

        } catch (WebAPIException e) {
            DialogUtil.error(e.getHeaderText(), e.getMessage(), getMainApp().getPrimaryStage(),
                    false);
        }
    }

    private void runInfoEdit() {
        Run run = sampleUploadController.getRun();
        if(run != null) {
            serverFastqFilesRadioButton.setDisable(true);
            serverRunFolderRadioButton.setDisable(true);
            if(!StringUtils.isEmpty(run.getServerRunDir())) {
                localFastqFilesRadioButton.setDisable(true);
                buttonSubmit.setDisable(true);
            }
        }
    }

    @FXML
    private void localFastqFilesAction() {
        maskerPane.setVisible(true);

        if(isServerItem && !sampleArrayList.isEmpty()) {
            isServerItem = false;
            isServerFastq = false;
            sampleArrayList.clear();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Fastq Files");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("fastq Files", "*.fastq", "*.fastq.gz")
        );
        if(mainController.getBasicInformationMap().containsKey("path")) {
            fileChooser.setInitialDirectory(new File((String) mainController.getBasicInformationMap().get("path")));
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(this.sampleUploadController.getCurrentStage());

        if(selectedFiles != null) {
            Boolean isValidPair = FileUtil.isValidPairedFastqFiles(
                    selectedFiles.stream().map(File::getName).collect(Collectors.toList()));
            if (isValidPair) {
                File[] fileArray = selectedFiles.toArray(new File[0]);
                List<File> fileList = new ArrayList<>(Arrays.asList(fileArray));

                if(fileList.isEmpty()) DialogUtil.alert("", "Can not find fastq file.", sampleUploadController.getCurrentStage(), true);
                List<File> undeterminedFile = new ArrayList<>();
                fileList.forEach(file -> {
                    if(file.getName().toUpperCase().startsWith("UNDETERMINED_"))
                        undeterminedFile.add(file);
                });

                if(!undeterminedFile.isEmpty()) {
                    fileList.removeAll(undeterminedFile);
                }

                //if(fileList != null && !fileList.isEmpty()) sampleUploadController.setTextFieldRunName(folder.getName());

                while (!fileList.isEmpty()) {

                    //mainController.getBasicInformationMap().put("path", folder.getAbsolutePath());
                    File fastqFile = fileList.get(0);
                    String fastqFilePairName = FileUtil.getFASTQFilePairName(fastqFile.getName());

                    if(StringUtils.isEmpty(fastqFilePairName)) {
                        fileList.remove(fastqFile);
                        continue;
                    }


                    List<File> pairFileList = fileList.stream().filter(file ->
                            file.getName().startsWith(fastqFilePairName + "_")).collect(Collectors.toList());

                    Optional<AnalysisFile> optionalFile = failedAnalysisFileList.stream().filter(item ->
                            item.getName().contains(fastqFilePairName + "_")).findFirst();
                    SampleView sample = null;
                    if(optionalFile.isPresent()) sample = getSameSample(optionalFile.get().getSampleId());
                    //fastq 파일이 짝을 이루고 올리는데 실패한 파일인 경우
                    if(pairFileList.size() == 2 && sample != null) {
                        List<File> failedFileList = new ArrayList<>();
                        List<AnalysisFile> selectedAnalysisFileList = new ArrayList<>();
                        for (File selectedFile : pairFileList) {
                            Optional<AnalysisFile> fileOptional = failedAnalysisFileList.stream().filter(item ->
                                    selectedFile.getName().equals(item.getName())).findFirst();
                            if (fileOptional.isPresent()) {
                                failedFileList.add(selectedFile);

                                //meta data 정보가 하나만 입력이 되어있는 경우
                                if("NOT_FOUND".equals(fileOptional.get().getStatus())) {
                                    failedAnalysisFileList.remove(fileOptional.get());
                                    addUploadFile(selectedFile,fastqFilePairName);
                                } else {
                                    //메타 데이터 정보가 온전히 존재하고 파일 업로드에 실패한 경우
                                    selectedAnalysisFileList.add(fileOptional.get());
                                }

                                //meta data 정보가 없는 경우
                            } else if(sample.getSampleStatus() != null && sample.getSampleStatus().getStep().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STEP_UPLOAD)
                                    && sample.getSampleStatus().getStatus().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_QUEUED)) {
                                failedFileList.add(selectedFile);
                            }
                        }
                        if(!failedFileList.isEmpty()) addUploadFile(failedFileList, fastqFilePairName, false);

                        if(!selectedAnalysisFileList.isEmpty()) uploadFileData.addAll(selectedAnalysisFileList);
                    } else if (pairFileList.size() == 2 && checkSameSample(fastqFilePairName)) {
                        addUploadFile(pairFileList, fastqFilePairName, true);
                    }

                    fileList.removeAll(pairFileList);
                    if(sampleArrayList.size() > 24) break;
                }
                tableEdit();
            } else {
                DialogUtil.warning("", "Please select valid fastq file pairs.",
                        sampleUploadController.getCurrentStage(), true);
            }
        }

        maskerPane.setVisible(false);
    }

    @FXML
    private void serverFastqFilesAction() {
        try {
            // Load the fxml file and create a new stage for the popup dialog
            FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.SERVER_DIRECTORY_VIEW);
            Pane page = loader.load();
            ServerDirectoryViewController controller = loader.getController();
            controller.setSampleUploadController(this.sampleUploadController);
            controller.setSampleUploadScreenFirstController(this);
            controller.show(page);
        } catch (Exception e) {
            DialogUtil.error("FXML Load Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    @FXML
    private void serverRunFolderAction() {
        try {
            // Load the fxml file and create a new stage for the popup dialog
            FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.SERVER_DIRECTORY_VIEW);
            Pane page = loader.load();
            ServerDirectoryViewController controller = loader.getController();
            controller.setSampleUploadController(this.sampleUploadController);
            controller.setSampleUploadScreenFirstController(this);
            controller.setRun(true);
            controller.show(page);
        } catch (Exception e) {
            DialogUtil.error("FXML Load Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    private void tableEdit() {
        int row = 0;
        standardDataGridPane.getChildren().removeAll(standardDataGridPane.getChildren());
        standardDataGridPane.setPrefHeight(0);
        //sample 수만큼 row를 생성
        for(int i = 0; i < sampleArrayList.size() ; i++) {
            createRow(i);
        }

        for(SampleView sample : sampleArrayList) {
            if(row > 24) break;
            if(sampleNameTextFieldList.get(row).isDisable()) {
                row++;
                continue;
            }
            sampleNameTextFieldList.get(row).setText(sample.getName());

            if(sample.getPanel().getDefaultDiseaseId() != null) {
                ComboBox<ComboBoxItem> disease = diseaseComboBoxList.get(row);
                disease.getItems().forEach(diseaseItem ->{
                    if(diseaseItem.getValue().equals(sample.getPanel().getDefaultDiseaseId().toString())) {
                        disease.getSelectionModel().select(diseaseItem);
                    }
                });
            } else {
                ComboBox<ComboBoxItem> disease = diseaseComboBoxList.get(row);
                disease.getSelectionModel().clearSelection();
            }

            ComboBox<PanelComboBoxItem> panel = panelComboBoxList.get(row);
            ComboBox<String> sampleSource = sampleSourceComboBoxList.get(row);

            if(sample.getPanel().getId() == null && panel.getSelectionModel().getSelectedItem() == null) {
                panel.getSelectionModel().select(0);
            } else if (sample.getPanel().getId() != null) {
                Optional<PanelComboBoxItem> optionalPanel = panel.getItems().stream()
                        .filter(item -> item.getValue().equalsIgnoreCase(sample.getPanel().getId().toString())).findFirst();
                if(optionalPanel.isPresent()) {
                    panel.getSelectionModel().select(optionalPanel.get());
                    settingDiseaseComboBox(sample.getPanel().getId(), row);
                } else if(StringUtils.isNotEmpty(sample.getPanel().getName())) {
                    PanelComboBoxItem comboBoxItem = new PanelComboBoxItem(sample.getPanel().getId().toString(),
                            sample.getPanel().getName(), sample.getPanel().getCode());
                    panel.getItems().add(comboBoxItem);
                    panel.getSelectionModel().select(comboBoxItem);

                    ComboBox<ComboBoxItem> disease = diseaseComboBoxList.get(row);
                    ComboBoxItem comboBoxItem1 = new ComboBoxItem(sample.getDiseaseName(), sample.getDiseaseName());
                    disease.getItems().add(comboBoxItem1);
                    disease.getSelectionModel().select(comboBoxItem1);

                }
            }

            if(sample.getIsControl() != null && sample.getIsControl()) {
                sampleIsControlList.get(row).setSelected(true);
            }

            if(StringUtils.isEmpty(sample.getSampleSource()) && sampleSource.getSelectionModel().getSelectedItem() != null) {
                sampleSource.getSelectionModel().clearSelection();
            } else {
                sampleSource.getSelectionModel().select(sample.getSampleSource());
            }

            //sample Status 가 존재한다면 그에 따른 추가기능의 설정
            if(sample.getSampleStatus() != null) {
                if(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STEP_UPLOAD.equalsIgnoreCase(sample.getSampleStatus().getStep())
                        && !AnalysisJobStatusCode.SAMPLE_FILE_META_ACTIVE.equalsIgnoreCase(sample.getSampleStatus().getStatus())) {
                    try {
                        Map<String, Object> params = new HashMap<>();
                        params.put("sampleId", sample.getId());

                        HttpClientResponse response = apiService.get("analysisFiles", params, null, false);

                        PagedAnalysisFile pagedAnalysisFile = response.getObjectBeforeConvertResponseToJSON(PagedAnalysisFile.class);

                        List<AnalysisFile> allFile = pagedAnalysisFile.getResult();

                        List<AnalysisFile> activeFile = new ArrayList<>();

                        if(allFile.size() == 1) {
                            AnalysisFile analysisFile = allFile.get(0);
                            AnalysisFile failedAnalysisFile = new AnalysisFile();
                            String r1 = "_R1_0";
                            String r2 = "_R2_0";
                            if(analysisFile.getName().contains(r1))
                                failedAnalysisFile.setName(analysisFile.getName().replaceAll(r1, r2));
                            if(analysisFile.getName().contains(r2))
                                failedAnalysisFile.setName(analysisFile.getName().replaceAll(r2, r1));
                            failedAnalysisFile.setStatus("NOT_FOUND");
                            failedAnalysisFileList.add(failedAnalysisFile);
                            setTextColor(row);
                        }
                        for(AnalysisFile analysisFile : allFile) {
                            if(analysisFile.getStatus().equals(AnalysisJobStatusCode.SAMPLE_FILE_META_ACTIVE)) activeFile.add(analysisFile);
                        }
                        if(!activeFile.isEmpty()) allFile.removeAll(activeFile);
                        failedAnalysisFileList.addAll(allFile);
                        if(!allFile.isEmpty()) {
                            setTextColor(row);
                        }
                    } catch (WebAPIException wae) {
                        DialogUtil.error(wae.getHeaderText(), wae.getContents(), getMainApp().getPrimaryStage(), true);
                    }
                }

                sampleNameTextFieldList.get(row).setDisable(true);
                panelComboBoxList.get(row).setDisable(true);
                diseaseComboBoxList.get(row).setDisable(true);
                sampleSourceComboBoxList.get(row).setDisable(true);
                sampleIsControlList.get(row).setDisable(true);

            }

            row++;
        }

    }

    private void setTextColor(int row) {
        String textColor = "red-text-combo-box";
        sampleNameTextFieldList.get(row).setStyle(sampleNameTextFieldList.get(row).getStyle() +
                "-fx-text-fill : #FF0000;");
        panelComboBoxList.get(row).getStyleClass().add(textColor);
        diseaseComboBoxList.get(row).getStyleClass().add(textColor);
        sampleSourceComboBoxList.get(row).getStyleClass().add(textColor);
    }

    @SuppressWarnings("unchecked")
    private void createRow(int row) {
        standardDataGridPane.setPrefHeight(standardDataGridPane.getPrefHeight() + 30);
        if(!sampleNameTextFieldList.isEmpty() && sampleNameTextFieldList.size() > row) {
            if(!panelComboBoxList.get(row).isDisable()) panelSetting(panelComboBoxList.get(row));
            standardDataGridPane.addRow(row, sampleNameTextFieldList.get(row), panelComboBoxList.get(row)
                    , sampleSourceComboBoxList.get(row), diseaseComboBoxList.get(row), sampleIsControlList.get(row));
            return;
        }
        String textStyle = "-fx-text-inner-color: black; -fx-border-width: 0;";
        TextField sampleName = new TextField();
        sampleName.setStyle(textStyle);
        sampleName.setMaxWidth(Double.MAX_VALUE);
        sampleName.setEditable(false);
        sampleName.setAlignment(Pos.CENTER);
        sampleName.setCursor(Cursor.DEFAULT);
        sampleNameTextFieldList.add(sampleName);
        sampleName.textProperty().addListener((observable, oldValue, newValue) -> {
            Set<String> fileName = fileMap.keySet();
            fileName.forEach(file -> {
                Map<String, Object> fileInfo = fileMap.get(file);

                if (fileInfo.get("sampleName") != null && fileInfo.get("sampleName").toString().equals(oldValue)) {
                    fileInfo.put("sampleName", newValue);
                }
            });
        });

        ComboBox<ComboBoxItem> disease  = new ComboBox<>();
        disease.setConverter(new ComboBoxConverter());
        disease.setMaxWidth(200);
        disease.setStyle("-fx-border-width: 0;");
        disease.setCursor(Cursor.HAND);
        diseaseComboBoxList.add(disease);

        ComboBox<PanelComboBoxItem> panel  = new ComboBox<>();
        panel.setMaxWidth(200);
        panelComboBoxList.add(panel);
        panelSetting(panel);
        panel.setCursor(Cursor.HAND);
        panel.setStyle(textStyle);
        panel.setOnAction(event -> {
            ComboBox<PanelComboBoxItem> obj = (ComboBox<PanelComboBoxItem>) event.getSource();
            if(panelComboBoxList.contains(obj)) {
                int index  = panelComboBoxList.indexOf(obj);
                PanelComboBoxItem item = obj.getSelectionModel().getSelectedItem();

                if(item != null && !StringUtils.isEmpty(item.getValue())) {
                    logger.debug(item.getText());
                    if (index == 0 && sampleArrayList.size() > 1) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        DialogUtil.setIcon(alert);
                        alert.setTitle("Panel Setting");
                        alert.setHeaderText("");
                        alert.setContentText("Do you want to apply them all at once?");
                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            panelBatchApplication(item);
                        }
                    }

                    settingDiseaseComboBox(Integer.parseInt(item.getValue()), index);
                }
            }
        });

        ComboBox<String> source = new ComboBox<>();
        source.setMaxWidth(200);
        sampleSourceComboBoxList.add(source);
        source.setStyle(textStyle);

        RadioButton radioButton = new RadioButton();
        radioButton.setToggleGroup(isControlGroup);
        sampleIsControlList.add(radioButton);
        radioButton.setVisible(false);

        standardDataGridPane.addRow(row, sampleName, panel, source, disease, radioButton);
        GridPane.setHalignment(sampleName, HPos.CENTER);
        GridPane.setHalignment(panel, HPos.CENTER);
        GridPane.setHalignment(source, HPos.CENTER);
        GridPane.setHalignment(disease, HPos.CENTER);
        GridPane.setHalignment(radioButton, HPos.CENTER);
        GridPane.setValignment(radioButton, VPos.CENTER);
        panel.getSelectionModel().select(0);
    }

    private void panelBatchApplication(PanelComboBoxItem item) {
        for(int index = 1 ; index < panelComboBoxList.size(); index++) {
            ComboBox<PanelComboBoxItem> panel = panelComboBoxList.get(index);
            panel.getSelectionModel().select(item);
            settingDiseaseComboBox(Integer.parseInt(item.getValue()), index);
        }
    }

    private void settingDiseaseComboBox(int panelId, int index) {
        //질병명 추가
        HttpClientResponse response;
        PanelView panelDetail = null;
        try {
            if(panelViewList == null || panelViewList.isEmpty() || panelViewList.stream().noneMatch(panel -> panel.getId() == panelId)) {
                response = apiService.get("panels/" + panelId, null, null, false);
                panelDetail = response.getObjectBeforeConvertResponseToJSON(PanelView.class);
                if(panelViewList == null) panelViewList = new ArrayList<>();
                panelViewList.add(panelDetail);
            } else {
                Optional<PanelView> optionalPanelView = panelViewList.stream()
                        .filter(panel -> panel.getId() == panelId).findFirst();
                if(optionalPanelView.isPresent()) panelDetail = optionalPanelView.get();
            }

            ComboBox<ComboBoxItem> diseaseComboBox = diseaseComboBoxList.get(index);

            diseaseComboBox.getItems().clear();

            for(Diseases disease : diseases) {
                List<Integer> diseaseIds = panelDetail.getDiseaseIds();
                if(diseaseIds != null && !diseaseIds.isEmpty() &&
                        diseaseIds.stream().anyMatch(diseaseId -> diseaseId.equals(disease.getId())))
                    diseaseComboBox.getItems().add(new ComboBoxItem(disease.getId().toString(), disease.getName()));
            }
            // 질병명이 없는 패널일 경우 샘플의 질병을 N/A로 설정되도록 함.
            if (diseaseComboBox.getItems().size() == 0) {
                diseaseComboBox.getItems().add(new ComboBoxItem("0", "N/A"));
            }
            List<SampleSourceCode> sampleSourceCodes = PipelineCode.getSampleSource(panelDetail.getCode());

            if(!sampleSourceComboBoxList.get(index).getItems().isEmpty()) {
                sampleSourceComboBoxList.get(index).getItems().removeAll(sampleSourceComboBoxList.get(index).getItems());
            }

            for(SampleSourceCode sampleSourceCode : sampleSourceCodes) {
                sampleSourceComboBoxList.get(index).getItems().add(sampleSourceCode.getDescription());
            }

            sampleIsControlList.get(index).setVisible(panelDetail.getCode().equals(PipelineCode.HERED_ACCUTEST_CNV_DNA.getCode()) ||
                    panelDetail.getCode().equals(PipelineCode.HERED_ACCUTEST_AMC_CNV_DNA.getCode()));


            if(panelDetail.getDefaultSampleSource() != null) {
                if(sampleSourceComboBoxList.get(index).getItems().contains(panelDetail.getDefaultSampleSource()))
                    sampleSourceComboBoxList.get(index).getSelectionModel().select(panelDetail.getDefaultSampleSource());
            } else {
                sampleSourceComboBoxList.get(index).getSelectionModel().clearSelection();
            }

            controlLabel.setVisible(sampleIsControlList.stream().anyMatch(RadioButton::isVisible));

            if(panelDetail.getDefaultDiseaseId() != null) {
                Integer defaultDiseaseId = panelDetail.getDefaultDiseaseId();
                Optional<ComboBoxItem> defaultDiseaseItem = diseaseComboBox.getItems().stream().filter(
                        item -> item.getValue().equals(defaultDiseaseId.toString())).findFirst();
                if (defaultDiseaseItem.isPresent()) {
                    diseaseComboBox.getSelectionModel().select(defaultDiseaseItem.get());
                } else {
                    diseaseComboBox.getSelectionModel().selectFirst();
                }
            } else {
                diseaseComboBox.getSelectionModel().selectFirst();
            }

        } catch (WebAPIException wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getMessage(), getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
            DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    private boolean saveSampleData() {
        boolean ok = true;
        int rowCount = standardDataGridPane.getChildren().size() / 5;

        for (int i = 0; i < rowCount; i++) {
            SampleView sample = sampleArrayList.get(i);
            if(sample.getId() != null) continue;
            if(sample.getRun().getId() == null) sample.getRun().setId(-1);

            TextField sampleName = sampleNameTextFieldList.get(i);
            if(sampleName.getText().isEmpty()) continue;
            sample.setName(sampleName.getText());

            ComboBox<PanelComboBoxItem> panelIdComboBox = panelComboBoxList.get(i);
            if(panelIdComboBox.getSelectionModel().getSelectedItem().getValue() == null) {
                ok = false;
                break;

            }
            Integer panelId = Integer.parseInt(panelIdComboBox.getSelectionModel().getSelectedItem().getValue());
            sample.getPanel().setId(panelId);

            sample.getPanel().setCode(panelIdComboBox.getSelectionModel().getSelectedItem().getCode());

            ComboBox<ComboBoxItem> diseaseId = diseaseComboBoxList.get(i);
            if(diseaseId.getSelectionModel().getSelectedItem().getValue() == null) {
                ok = false;
                break;
            }
            sample.getPanel().setDefaultDiseaseId(Integer.parseInt(diseaseId.getSelectionModel().getSelectedItem().getValue()));

            ComboBox<String> sampleSource  = sampleSourceComboBoxList.get(i);
            if(sampleSource.getSelectionModel().getSelectedItem() == null) {
                ok = false;
                break;
            }
            sample.setSampleSource(sampleSource.getSelectionModel().getSelectedItem());

            if(sampleIsControlList.get(i).isVisible()) sample.setIsControl(sampleIsControlList.get(i).isSelected());
        }

        return ok;
    }

    public void checkAmplicon() {
        for(ComboBox<PanelComboBoxItem> panelComboBox : panelComboBoxList) {
            if(panelComboBox.isDisabled()) continue;

            int panelId = Integer.parseInt(panelComboBox.getSelectionModel().getSelectedItem().getValue());
            HttpClientResponse response;
            try {
                response = apiService.get("panels/" + panelId, null, null, false);
                Panel panelDetail = response.getObjectBeforeConvertResponseToJSON(Panel.class);

//                if(LibraryTypeCode.AMPLICON_BASED.getDescription().equals(panelDetail.getLibraryType())) {
//
//                }

            } catch (WebAPIException wae) {
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), sampleUploadController.getCurrentStage(), true);
            }
        }
    }

    @FXML
    public void submit() {
        if (sampleArrayList == null || sampleArrayList.isEmpty()) return;

        for(ComboBox<PanelComboBoxItem> panelComboBox : panelComboBoxList) {
            ComboBoxItem comboBoxItem = panelComboBox.getSelectionModel().getSelectedItem();

            if("".equals(comboBoxItem.getValue())) {
                DialogUtil.alert("warning", "Panel not selected. please select a panel.",
                        this.sampleUploadController.getCurrentStage(), true);
                return;
            }
        }

        List<RadioButton> isControlVisibleList = sampleIsControlList.stream().filter(RadioButton::isVisible)
                .collect(Collectors.toList());

        if(!saveSampleData()) {
            DialogUtil.alert("", "Check the analysis settings of the samples.", sampleUploadController.getCurrentStage(), true);
            return;
        }

        // 선택 된 패널의 개수 => 만약 heme, hered, solid 중 하나가 선택된 상태라면 선택된 패널의 개수는 1이여야함
        long size = sampleArrayList.stream().map(sample -> sample.getPanel().getId()).distinct().count();

        boolean isNotCnvPanel = sampleArrayList.stream().allMatch(sample ->
                PipelineCode.isNotCnvPanel(sample.getPanel().getCode()));

        boolean isBrcaCnvPanel = sampleArrayList.stream().allMatch(sample ->
                PipelineCode.isBRCACNVPipeline(sample.getPanel().getCode()));

        //heme_cnv, hered_cnv, solid_cnv 중 하나가 존재한다면 해당 Run은 동일한 패널을 사용해야함
        if(!(isNotCnvPanel || size == 1)) {
            DialogUtil.warning("", "CNV analysis must select all samples in the same panel.",
                    sampleUploadController.getCurrentStage(), true);
            return;
        }

        if(!isNotCnvPanel && sampleArrayList.size() == 1 && !isBrcaCnvPanel) {
            DialogUtil.warning("Warning", "CNV panels require two or more samples.",
                    sampleUploadController.getCurrentStage(), true);
            return;
        }

        if(!isControlVisibleList.isEmpty() && isControlVisibleList.stream().noneMatch(RadioButton::isSelected)) {
            DialogUtil.alert("", "The control sample must be selected.", sampleUploadController.getCurrentStage(), true);
            return;
        }

        if(isBrcaCnvPanel && sampleArrayList.size() < 6) {
            String alertContentText = "More than 6 samples are required for running appropriate CNV analysis. Would you like to continue to proceed?";

            Alert alert = new Alert(Alert.AlertType.WARNING);
            DialogUtil.setIcon(alert);
            alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            alert.initOwner(this.mainController.getPrimaryStage());
            alert.setTitle("CNV Warning");
            alert.setHeaderText("");
            alert.setContentText(alertContentText);

            Optional<ButtonType> result = alert.showAndWait();

            if(result.isPresent() && result.get() == ButtonType.CANCEL) {
                return;
            }
        }

        if(sampleUploadController.getRun() != null) {
            newSampleAdded();
            if((uploadFileData != null && !uploadFileData.isEmpty()) &&
                    (uploadFileList != null && !uploadFileList.isEmpty()))
                this.mainController.runningAnalysisRequestUpload(uploadFileData, uploadFileList, sampleUploadController.getRun());

            logger.debug("submit");
            closeDialog();
            return;
        }

        try {
            Map<String, Object> params = new HashMap<>();
            HttpClientResponse response;
            RunWithSamples run;

            if(sampleUploadController.getRunName() == null || "".equals(sampleUploadController.getRunName())) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
                params.put("name", sdf.format(date));
            } else {
                params.put("name", sampleUploadController.getRunName());
            }
            params.put("sequencingPlatform", sampleUploadController.getSequencerType().getUserData());

            if(isServerItem) {
                params.put("serverRunDir", runPath);
            }

            List<Map<String, Object>> list = returnSampleMap();

            params.put("sampleCreateRequests", list);

            response = apiService.post("/runs", params, null, true);
            run = response.getObjectBeforeConvertResponseToJSON(RunWithSamples.class);
            if (run != null) {
                mainController.getBasicInformationMap().put("runId", run.getRun().getId());
                logger.debug(run.toString());
                List<Sample> samples = run.getSamples();

                for(Sample sample : samples) {
                    postAnalysisFilesData(sample);
                }

                if((uploadFileData != null && !uploadFileData.isEmpty()) &&
                        (uploadFileList != null && !uploadFileList.isEmpty())) {
                    this.mainController.runningAnalysisRequestUpload(uploadFileData, uploadFileList, run.getRun());
                }
                logger.debug("submit");
            }
            closeDialog();

        } catch (WebAPIException e) {
            DialogUtil.warning(e.getHeaderText(), e.getMessage(), getMainApp().getPrimaryStage(), true);
            logger.warn("Analysis request warning : ", e.getMessage());
        } catch (Exception e) {
            logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
            DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    private void newSampleAdded() {
        for(SampleView sample : sampleArrayList) {
            if(sample.getId() == null) {
                try {
                    sample.getRun().setId(sampleUploadController.getRun().getId());
                    sampleUpload(sample);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if ((sample.getSampleStatus() != null &&
                    sample.getSampleStatus().getStep().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STEP_UPLOAD)
                    && sample.getSampleStatus().getStatus().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_QUEUED))) {
                postAnalysisFilesData(sample);
            }
        }
    }

    private Map<String, Object> createSampleMap(SampleView sample) {
        Map<String, Object> params = new HashMap<>();

        params.put("runId", sample.getRun().getId());
        params.put("name", sample.getName());
        params.put("panelId", sample.getPanel().getId());
        params.put("isControl", sample.getIsControl());
        params.put("code", sample.getPanel().getCode());
        params.put("diseaseId", sample.getPanel().getDefaultDiseaseId());
        params.put("sampleSource", sample.getSampleSource());
        params.put("inputFType", "FASTQ.GZ");

        return params;
    }

    private List<Map<String, Object>> returnSampleMap() {
        List<Map<String, Object>> list = new ArrayList<>();
        for(SampleView sample : sampleArrayList) {
            Map<String, Object> params = createSampleMap(sample);
            list.add(params);
        }
        return list;
    }

    private void sampleUpload(SampleView sample) throws Exception {

        Map<String, Object> params = createSampleMap(sample);
        HttpClientResponse response;

        response = apiService.post("/samples", params, null, true);
        Sample sampleData = response.getObjectBeforeConvertResponseToJSON(Sample.class);

        postAnalysisFilesData(sampleData);
    }

    private void postAnalysisFilesData(Sample sample) {
        Set<String> fileName = fileMap.keySet();

        fileName.forEach(file -> {
            Map<String, Object> fileInfo = fileMap.get(file);

            if(fileInfo.get("sampleName") != null && fileInfo.get("sampleName").toString().equals(sample.getName())) {
                fileInfo.put("sampleId", sample.getId());
                fileInfo.put("sampleName", null);
                fileInfo.remove("sampleName");
                HttpClientResponse fileResponse;
                try {
                    fileResponse = apiService.post("/analysisFiles", fileInfo, null, true);
                    AnalysisFile fileData = fileResponse.getObjectBeforeConvertResponseToJSON(AnalysisFile.class);
                    logger.debug(fileData.getName());
                    uploadFileData.add(fileData);
                } catch (WebAPIException e) {
                    DialogUtil.error(e.getHeaderText(), e.getMessage(), getMainApp().getPrimaryStage(), true);
                } catch (IOException e) {
                    logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
                    DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), true);
                }

            }
        });
    }

    private void postAnalysisFilesData(SampleView sample) {
        Set<String> fileName = fileMap.keySet();

        fileName.forEach(file -> {
            Map<String, Object> fileInfo = fileMap.get(file);

            if(fileInfo.get("sampleName") != null && fileInfo.get("sampleName").toString().equals(sample.getName())) {
                fileInfo.put("sampleId", sample.getId());
                fileInfo.put("sampleName", null);
                fileInfo.remove("sampleName");
                HttpClientResponse fileResponse;
                try {
                    fileResponse = apiService.post("/analysisFiles", fileInfo, null, true);
                    AnalysisFile fileData = fileResponse.getObjectBeforeConvertResponseToJSON(AnalysisFile.class);
                    logger.debug(fileData.getName());
                    uploadFileData.add(fileData);
                } catch (WebAPIException e) {
                    DialogUtil.error(e.getHeaderText(), e.getMessage(), getMainApp().getPrimaryStage(), true);
                } catch (IOException e) {
                    logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
                    DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), true);
                }

            }
        });
    }

    private void panelSetting(ComboBox<PanelComboBoxItem> panelBox) {
        if(!panelBox.getItems().isEmpty()) panelBox.getItems().removeAll(panelBox.getItems());
        List<Panel> panelList = new ArrayList<>();
        if(isServerItem && !isServerFastq) {
            panelList.addAll(panels.stream().filter(panel -> panel.getName().startsWith("TruSight Tumor 170")).collect(Collectors.toList()));
        } else {
            panelList.addAll(panels);
        }
        panelBox.setConverter(new PanelComboBoxConverter());
        panelBox.getItems().add(new PanelComboBoxItem());
        for(Panel panel :  panelList) {
            panelBox.getItems().add(new PanelComboBoxItem(panel.getId().toString(), panel.getName(), panel.getCode()));
        }

        panelBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void closeDialog() { sampleUploadController.closeDialog(); }

    /**
     * 동일한 샘플을 선택한것이 있는지 확인 있다면 true
     * @param name String
     * @return boolean
     */
    private boolean checkSameSample(String name) {
        return sampleArrayList.stream().noneMatch(item -> name.equals(item.getName()));
    }

    /**
     * 동일한 샘플을 검색하여 해당 sample의 id 값을 리턴
     * @param sampleId Integer
     * @return Sample
     */
    private SampleView getSameSample(Integer sampleId) {
        Optional<SampleView> optionalSample = sampleArrayList.stream().filter(item -> (item.getId() != null
                        && sampleId.equals(item.getId()))).findFirst();
        return optionalSample.orElse(null);
    }

    private void addUploadFile(List<File> fileList, String fastqFilePairName, boolean newFileCheck) {
        for (File fastqFile : fileList) {
            Map<String, Object> file = new HashMap<>();
            file.put("sampleName", fastqFilePairName);
            file.put("name", fastqFile.getName());
            file.put("fileSize", fastqFile.length());
            file.put("isInput", true);
            file.put("fileType", "FASTQ.GZ");
            this.fileMap.put(fastqFile.getName(), file);
        }
        uploadFileList.addAll(fileList);
        if(newFileCheck) {
            SampleView sample = createSampleView(fastqFilePairName);
            sampleArrayList.add(sample);
        }
    }

    private void addUploadFile(File selectedFile, String fastqFilePairName) {
        Map<String, Object> file = new HashMap<>();
        file.put("sampleName", fastqFilePairName);
        file.put("name", selectedFile.getName());
        file.put("fileSize", selectedFile.length());
        file.put("isInput", true);
        file.put("fileType", "FASTQ.GZ");
        this.fileMap.put(selectedFile.getName(), file);

        uploadFileList.add(selectedFile);
    }
}