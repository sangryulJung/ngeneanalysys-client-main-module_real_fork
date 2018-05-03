package ngeneanalysys.controller;

import com.opencsv.CSVReader;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ngeneanalysys.code.AnalysisJobStatusCode;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.LibraryTypeCode;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedAnalysisFile;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.task.SampleSheetDownloadTask;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.FileUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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

    private List<Sample> sampleArrayList = null;

    private List<AnalysisFile> failedAnalysisFileList = new ArrayList<>();

    @FXML
    private GridPane standardDataGridPane;

    @FXML
    private ToggleGroup selectFile;

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

    private List<ComboBox<ComboBoxItem>> panelComboBoxList = new ArrayList<>();

    private List<ComboBox<String>> sampleSourceComboBoxList = new ArrayList<>();

    private boolean isServerItem = false;

    private boolean isServerFastq = false;

    private String runPath = "";

    //화면에 표시될 row 수
    private int totalRow = 0;

    public void setServerFASTQ(String path) {
        isServerItem = true;
        isServerFastq = true;
        if(!sampleArrayList.isEmpty()) sampleArrayList.removeAll(sampleArrayList);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("subPath", path);

            runPath = path;
            HttpClientResponse response = apiService.get("/runDir", params, null, false);
            ServerFileInfo serverFileInfo = response.getObjectBeforeConvertResponseToJSON(ServerFileInfo.class);

            List<ServerFile> serverFiles = serverFileInfo.getChild().stream()
                    .filter(serverFile -> serverFile.getName().toLowerCase().endsWith("fastq.gz"))
                    .collect(Collectors.toList());
            if(serverFiles.isEmpty()) DialogUtil.alert("Empty Fastq File Directory", "Can not find fastq files in the directory.", sampleUploadController.getCurrentStage(), true);
            setServerFastqList(serverFiles);
            sampleUploadController.setTextFieldRunName(path);

        } catch (WebAPIException e) {
                DialogUtil.error(e.getHeaderText(), e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    public void setServerRun(String path) {
        isServerItem = true;
        isServerFastq = false;
        logger.debug(path);
        if(sampleArrayList.isEmpty()) sampleArrayList.removeAll(sampleArrayList);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("runDir", path);
            runPath = path;
            Task<Void> task = new SampleSheetDownloadTask(this.sampleUploadController, this, path);
            final Thread downloadThread = new Thread(task);

            downloadThread.setDaemon(true);
            downloadThread.start();

            sampleUploadController.setTextFieldRunName(path);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean searchSameSample(String fairName) {
        if(sampleArrayList == null || sampleArrayList.isEmpty()) return false;

        return sampleArrayList.stream().anyMatch(item -> item.getName().equalsIgnoreCase(fairName));
    }


    public void setServerFastqList(List<ServerFile> serverFiles) {
        List<ServerFile> undeterminedFile = new ArrayList<>();
        serverFiles.stream().forEach(file -> {
            if(file.getName().toUpperCase().startsWith("UNDETERMINED_"))
                undeterminedFile.add(file);
        });

        if(!undeterminedFile.isEmpty()) serverFiles.removeAll(undeterminedFile);

        while (!serverFiles.isEmpty()) {
            ServerFile serverFile = serverFiles.get(0);
            String fastqFilePairName = FileUtil.getFASTQFilePairName(serverFile.getName());

            List<ServerFile> pairFileList = serverFiles.stream().filter(file ->
                    file.getName().startsWith(fastqFilePairName)).collect(Collectors.toList());

            //fastq 파일이 짝을 이루고 올리는데 실패한 파일인 경우
           if (pairFileList.size() == 2 && checkSameSample(fastqFilePairName)) {
                Sample sample = new Sample();
                sample.setName(fastqFilePairName);
                sampleArrayList.add(sample);
            }

            if(sampleArrayList.size() == 23) break;

            serverFiles.removeAll(pairFileList);
        }

        tableEdit();
    }

    public void setSampleSheet(String path) {
        if(!sampleArrayList.isEmpty()) sampleArrayList.removeAll(sampleArrayList);
        try(CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(path)))) {
            String[] s;
            boolean tableData = false;
            while((s = csvReader.readNext()) != null) {
                if (tableData && sampleArrayList.size() < 23) {
                    final String sampleName = s[0];
                    logger.debug(s[0]);
                    Sample sample = new Sample();
                    sample.setName(sampleName);
                    if(s[8] != null && s[8].contains("DNA")) {
                        Optional<Panel> panel = panels.stream().filter(item -> item.getName().contains("Tumor 170 DNA")).findFirst();
                        if(panel.isPresent()) {
                            sample.setPanelId(panel.get().getId());
                            sample.setSampleSource(panel.get().getDefaultSampleSource());
                            if(panel.get().getDefaultDiseaseId() != null)
                                sample.setDiseaseId(panel.get().getDefaultDiseaseId());
                        }
                    } else if(s[8] != null && s[8].contains("RNA")) {
                        Optional<Panel> panel = panels.stream().filter(item -> item.getName().contains("Tumor 170 RNA")).findFirst();
                        if(panel.isPresent()) {
                            sample.setPanelId(panel.get().getId());
                            sample.setSampleSource(panel.get().getDefaultSampleSource());
                            if(panel.get().getDefaultDiseaseId() != null)
                                sample.setDiseaseId(panel.get().getDefaultDiseaseId());
                        }
                    }
                    sampleArrayList.add(sample);
                } else if(s[0].equalsIgnoreCase("Sample_ID")) {
                    tableData = true;
                }
            }
            tableEdit();

        } catch (IOException e) {
            DialogUtil.alert("not found", "file not found", this.getMainApp().getPrimaryStage(), true);
        }

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
    public void setSampleUploadController(SampleUploadController sampleUploadController) {
        this.sampleUploadController = sampleUploadController;
    }

    @Override
    public void show(Parent root) throws IOException {
        standardDataGridPane.getChildren().clear();
        standardDataGridPane.setPrefHeight(0);
        fileMap = sampleUploadController.getFileMap();
        uploadFileList = sampleUploadController.getUploadFileList();
        uploadFileData= sampleUploadController.getUploadFileData();

        panels = (List<Panel>)mainController.getBasicInformationMap().get("panels");
        diseases = (List<Diseases>)mainController.getBasicInformationMap().get("diseases");

        if(sampleUploadController.getSamples() != null) {
            sampleArrayList = sampleUploadController.getSamples();
            tableEdit();
        }
    }

    @FXML
    private void localFastqFilesAction() {
        maskerPane.setVisible(true);

        if(isServerItem && !sampleArrayList.isEmpty()) {
            isServerItem = false;
            isServerFastq = false;
            sampleArrayList.removeAll(sampleArrayList);
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Directory");
        if(mainController.getBasicInformationMap().containsKey("path")) {
            directoryChooser.setInitialDirectory(new File((String) mainController.getBasicInformationMap().get("path")));
        } else {
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        File folder = directoryChooser.showDialog(this.sampleUploadController.getCurrentStage());

        if(folder != null) {
            File[] fileArray = folder.listFiles();
            List<File> fileList = new ArrayList<>(Arrays.asList(fileArray));
            if (System.getProperty("os.name").toLowerCase().contains("window")) {
                fileList = fileList.stream().filter(file -> file.getName().endsWith(".fastq.gz")).collect(Collectors.toList());
            } else {
                fileList = fileList.stream().filter(file -> file.getName().endsWith(".fastq") ||
                        file.getName().endsWith(".fastq.gz")).collect(Collectors.toList());
            }

            if(fileList.isEmpty()) DialogUtil.alert("not found", "not found fastq file", sampleUploadController.getCurrentStage(), true);
            List<File> undeterminedFile = new ArrayList<>();
            fileList.stream().forEach(file -> {
                if(file.getName().toUpperCase().startsWith("UNDETERMINED_"))
                    undeterminedFile.add(file);
            });

            if(!undeterminedFile.isEmpty()) fileList.removeAll(undeterminedFile);

            if(!fileList.isEmpty()) sampleUploadController.setTextFieldRunName(folder.getName());

            while (!fileList.isEmpty()) {

                mainController.getBasicInformationMap().put("path", folder.getAbsolutePath());
                File fastqFile = fileList.get(0);
                String fastqFilePairName = FileUtil.getFASTQFilePairName(fastqFile.getName());

                List<File> pairFileList = fileList.stream().filter(file ->
                        file.getName().startsWith(fastqFilePairName + "_")).collect(Collectors.toList());

                Optional<AnalysisFile> optionalFile = uploadFileData.stream().filter(item ->
                        item.getName().contains(fastqFilePairName + "_")).findFirst();
                Sample sample = null;
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
            }

        }
        tableEdit();
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
        } catch (IOException e) {
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
        } catch (IOException e) {
            DialogUtil.error("FXML Load Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    public void tableEdit() {
        int row = 0;
        standardDataGridPane.getChildren().removeAll(standardDataGridPane.getChildren());
        standardDataGridPane.setPrefHeight(0);
        //sample 수만큼 row를 생성
        for(int i = 0; i < sampleArrayList.size() ; i++) {
            createRow(i);
        }

        for(Sample sample : sampleArrayList) {
            if(row > 22) break;

            sampleNameTextFieldList.get(row).setText(sample.getName());

            if(sample.getDiseaseId() != null) {
                ComboBox<ComboBoxItem> disease = diseaseComboBoxList.get(row);
                disease.getItems().forEach(diseaseItem ->{
                    if(diseaseItem.getValue().equals(sample.getDiseaseId().toString())) {
                        disease.getSelectionModel().select(diseaseItem);
                        return;
                    }
                });
            } else {
                ComboBox<ComboBoxItem> disease = diseaseComboBoxList.get(row);
                disease.getSelectionModel().clearSelection();
            }

            ComboBox<ComboBoxItem> panel = panelComboBoxList.get(row);
            ComboBox<String> sampleSource = sampleSourceComboBoxList.get(row);

            if(sample.getPanelId() == null && panel.getSelectionModel().getSelectedItem() == null) {
                panel.getSelectionModel().select(0);
            } else if (sample.getPanelId() != null) {
                Optional<ComboBoxItem> optionalPanel = panel.getItems().stream()
                        .filter(item -> item.getValue().equalsIgnoreCase(sample.getPanelId().toString())).findFirst();
                if(optionalPanel.isPresent()) {
                    panel.getSelectionModel().select(optionalPanel.get());
                    settingDiseaseComboBox(sample.getPanelId(), row);
                }
            }

            if(StringUtils.isEmpty(sample.getSampleSource()) && sampleSource.getSelectionModel().getSelectedItem() != null) {
                sampleSource.getSelectionModel().select(0);
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
                            Map<String, Object> file = new HashMap<>();
                            AnalysisFile failedAnalysisFile = new AnalysisFile();

                            if(analysisFile.getName().contains("_R1_0"))
                                failedAnalysisFile.setName(analysisFile.getName().replaceAll("_R1_0", "_R2_0"));
                            if(analysisFile.getName().contains("_R2_0"))
                                failedAnalysisFile.setName(analysisFile.getName().replaceAll("_R2_0", "_R1_0"));
                            failedAnalysisFile.setStatus("NOT_FOUND");
                            failedAnalysisFileList.add(failedAnalysisFile);
                        }
                        for(AnalysisFile analysisFile : allFile) {
                            if(analysisFile.getStatus().equals(AnalysisJobStatusCode.SAMPLE_FILE_META_ACTIVE)) activeFile.add(analysisFile);
                        }
                        if(!activeFile.isEmpty()) allFile.removeAll(activeFile);
                        failedAnalysisFileList.addAll(allFile);
                    } catch (WebAPIException wae) {
                        DialogUtil.error(wae.getHeaderText(), wae.getContents(), getMainApp().getPrimaryStage(), true);
                    }
                }

                sampleNameTextFieldList.get(row).setDisable(true);
                panelComboBoxList.get(row).setDisable(true);
                diseaseComboBoxList.get(row).setDisable(true);
                sampleSourceComboBoxList.get(row).setDisable(true);

            }

            row++;
        }

    }

    public void createRow(int row) {
        standardDataGridPane.setPrefHeight(standardDataGridPane.getPrefHeight() + 28);
        if(!sampleNameTextFieldList.isEmpty() && sampleNameTextFieldList.size() > row) {
            panelSetting(panelComboBoxList.get(row));
            standardDataGridPane.addRow(row, sampleNameTextFieldList.get(row), panelComboBoxList.get(row)
                    , sampleSourceComboBoxList.get(row), diseaseComboBoxList.get(row));
            return;
        }
        TextField sampleName = new TextField();
        sampleName.setStyle("-fx-text-inner-color: black; -fx-border-width: 0;");
        sampleName.setMaxWidth(Double.MAX_VALUE);
        sampleName.setEditable(false);
        sampleName.setAlignment(Pos.CENTER);
        sampleName.setCursor(Cursor.DEFAULT);
        sampleNameTextFieldList.add(sampleName);
        sampleName.textProperty().addListener((observable, oldValue, newValue) -> {
            Set<String> fileName = fileMap.keySet();
            fileName.stream().forEach(file -> {
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
        //settingDiseaseComboBox(panels.get(0).getId(), row);
        //diseasesSetting(disease);

        ComboBox<ComboBoxItem> panel  = new ComboBox<>();
        panel.setMaxWidth(200);
        panelComboBoxList.add(panel);
        panelSetting(panel);
        panel.setCursor(Cursor.HAND);
        panel.setStyle("-fx-text-inner-color: black; -fx-border-width: 0;");
        panel.setOnAction(event -> {
            ComboBox<ComboBoxItem> obj = (ComboBox<ComboBoxItem>) event.getSource();
            if(panelComboBoxList.contains(obj)) {
                int index  = panelComboBoxList.indexOf(obj);
                ComboBoxItem item = obj.getSelectionModel().getSelectedItem();
                logger.debug(item.getText());
                if(!StringUtils.isEmpty(item.getValue())) {

                    if (index == 0 && sampleArrayList.size() > 1) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Panel Setting");
                        alert.setHeaderText("Panel Setting");
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
        source.getItems().addAll("Blood","Bone marrow", "FFPE","DNA");
        source.setMaxWidth(200);
        sampleSourceComboBoxList.add(source);
        source.setStyle("-fx-text-inner-color: black; -fx-border-width: 0;");

        standardDataGridPane.addRow(row, sampleName, panel, source, disease);
        GridPane.setHalignment(sampleName, HPos.CENTER);
        GridPane.setHalignment(panel, HPos.CENTER);
        GridPane.setHalignment(source, HPos.CENTER);
        GridPane.setHalignment(disease, HPos.CENTER);
        panel.getSelectionModel().select(0);
    }

    public void panelBatchApplication(ComboBoxItem item) {
        for(int index = 1 ; index < panelComboBoxList.size(); index++) {
            ComboBox<ComboBoxItem> panel = panelComboBoxList.get(index);
            panel.getSelectionModel().select(item);
            settingDiseaseComboBox(Integer.parseInt(item.getValue()), index);
        }
    }

    public void settingDiseaseComboBox(int panelId, int index) {
        //질병명 추가
        HttpClientResponse response = null;
        try {
            response = apiService.get("panels/" + panelId, null, null, false);
            PanelView panelDetail = response.getObjectBeforeConvertResponseToJSON(PanelView.class);

            ComboBox<ComboBoxItem> diseaseComboBox = diseaseComboBoxList.get(index);

            diseaseComboBox.getItems().clear();

            for(Diseases diseases : diseases) {
                List<Integer> diseaseIds = panelDetail.getDiseaseIds();
                if(diseaseIds != null && !diseaseIds.isEmpty() &&
                        //diseaseIds.stream().filter(diseaseId -> diseaseId.equals(diseases.getId())).findFirst().isPresent())
                        diseaseIds.stream().anyMatch(diseaseId -> diseaseId.equals(diseases.getId())))
                    diseaseComboBox.getItems().add(new ComboBoxItem(diseases.getId().toString(), diseases.getName()));
            }
            // 질병명이 없는 패널일 경우 샘플의 질병을 N/A로 설정되도록 함.
            if (diseaseComboBox.getItems().size() == 0) {
                diseaseComboBox.getItems().add(new ComboBoxItem("0", "N/A"));
            }
            diseaseComboBox.getSelectionModel().selectFirst();

            if(panelDetail.getDefaultSampleSource() != null) {
                sampleSourceComboBoxList.get(index).getSelectionModel().select(panelDetail.getDefaultSampleSource());
            } else {
                sampleSourceComboBoxList.get(index).getSelectionModel().clearSelection();
            }

        } catch (WebAPIException wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getMessage(), getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    public boolean saveSampleData() {
        boolean ok = true;
        int rowCount = standardDataGridPane.getChildren().size() / 4;

        for (int i = 0; i < rowCount; i++) {
            Sample sample = sampleArrayList.get(i);
            if(sample.getId() != null) continue;
            if(sample.getRunId() == null) sample.setRunId(-1);

            TextField sampleName = sampleNameTextFieldList.get(i);
            if(sampleName.getText().isEmpty()) continue;
            sample.setName(sampleName.getText());

            ComboBox<ComboBoxItem> panelIdComboBox = panelComboBoxList.get(i);
            if(panelIdComboBox.getSelectionModel().getSelectedItem().getValue() == null) {
                ok = false;
                break;

            }
            Integer panelId = Integer.parseInt(panelIdComboBox.getSelectionModel().getSelectedItem().getValue());
            sample.setPanelId(panelId);

            ComboBox<ComboBoxItem> diseaseId = diseaseComboBoxList.get(i);
            if(diseaseId.getSelectionModel().getSelectedItem().getValue() == null) {
                ok = false;
                break;
            }
            sample.setDiseaseId(Integer.parseInt(diseaseId.getSelectionModel().getSelectedItem().getValue()));

            ComboBox<String> sampleSource  = sampleSourceComboBoxList.get(i);
            if(sampleSource.getSelectionModel().getSelectedItem() == null) {
                ok = false;
                break;
            }
            sample.setSampleSource(sampleSource.getSelectionModel().getSelectedItem());
        }

        return ok;
    }

    public void checkAmplicon() {
        for(ComboBox<ComboBoxItem> panelComboBox : panelComboBoxList) {
            if(panelComboBox.isDisabled()) continue;

            Integer panelId = Integer.parseInt(panelComboBox.getSelectionModel().getSelectedItem().getValue());
            HttpClientResponse response;
            try {
                response = apiService.get("panels/" + panelId, null, null, false);
                Panel panelDetail = response.getObjectBeforeConvertResponseToJSON(Panel.class);

                if(LibraryTypeCode.AMPLICON_BASED.getDescription().equals(panelDetail.getLibraryType())) {

                }

            } catch (WebAPIException wae) {
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), sampleUploadController.getCurrentStage(), true);
            }
        }
    }

    @FXML
    public void submit() {

        for(ComboBox<ComboBoxItem> panelComboBox : panelComboBoxList) {
            ComboBoxItem comboBoxItem = panelComboBox.getSelectionModel().getSelectedItem();

            if("".equals(comboBoxItem.getValue())) {
                DialogUtil.alert("warning", "Panel not selected. please select a panel.",
                        this.sampleUploadController.getCurrentStage(), true);
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
            if (sampleArrayList != null && !sampleArrayList.isEmpty()) {

                Map<String, Object> params = new HashMap<>();
                HttpClientResponse response = null;
                RunWithSamples run = null;

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

                if(!saveSampleData()) {
                    DialogUtil.alert("check item", "check item", sampleUploadController.getCurrentStage(), true);
                    return;
                }
                List<Map<String, Object>> list = returnSampleMap();

                params.put("sampleCreateRequests", list);

                response = apiService.post("/runs", params, null, true);
                run = response.getObjectBeforeConvertResponseToJSON(RunWithSamples.class);
                mainController.getBasicInformationMap().put("runId", run.getRun().getId());
                logger.debug(run.toString());

                List<Sample> samples = run.getSamples();

                for(Sample sample : samples) {
                    postAnalysisFilesData(sample);
                }
                /*for (Sample sample : sampleArrayList) {
                    sample.setRunId(run.getId());
                    sampleUpload(sample);
                }*/

                if((uploadFileData != null && !uploadFileData.isEmpty()) &&
                        (uploadFileList != null && !uploadFileList.isEmpty())) {
                    assert run != null;
                    this.mainController.runningAnalysisRequestUpload(uploadFileData, uploadFileList, run.getRun());
                }
                logger.debug("submit");
                closeDialog();
            }
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    private void newSampleAdded() {
        for(Sample sample : sampleArrayList) {
            if(sample.getId() == null) {

                Optional<TextField> optionalTextField = sampleNameTextFieldList.stream().filter(item ->
                        (!StringUtils.isEmpty(item.getText()) && sample.getName().equals(item.getText()))).findFirst();

                if(optionalTextField.isPresent()) {
                    int row = sampleNameTextFieldList.indexOf(optionalTextField.get());

                    ComboBox<ComboBoxItem> panelIdComboBox = panelComboBoxList.get(row);
                    Integer panelId = Integer.parseInt(panelIdComboBox.getSelectionModel().getSelectedItem().getValue());
                    sample.setPanelId(panelId);

                    ComboBox<ComboBoxItem> diseaseId = diseaseComboBoxList.get(row);
                    sample.setDiseaseId(Integer.parseInt(diseaseId.getSelectionModel().getSelectedItem().getValue()));
                }

                try {
                    sample.setRunId(sampleUploadController.getRun().getId());
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

    private List<Map<String, Object>> returnSampleMap() {
        List<Map<String, Object>> list = new ArrayList<>();
        for(Sample sample : sampleArrayList) {
            Map<String, Object> params = new HashMap<>();

            params.put("runId", sample.getRunId());
            params.put("name", sample.getName());
            params.put("panelId", sample.getPanelId());
            params.put("diseaseId", sample.getDiseaseId());
            params.put("sampleSource", sample.getSampleSource());
            params.put("inputFType", "FASTQ.GZ");
            list.add(params);
        }
        return list;
    }

    private void sampleUpload(Sample sample) throws Exception {

        Map<String, Object> params = new HashMap<>();
        HttpClientResponse response = null;

        params.put("runId", sample.getRunId());
        params.put("name", sample.getName());
        params.put("diseaseId", sample.getDiseaseId());
        params.put("inputFType", "FASTQ.GZ");
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
                HttpClientResponse fileResponse = null;
                try {
                    fileResponse = apiService.post("/analysisFiles", fileInfo, null, true);
                    AnalysisFile fileData = fileResponse.getObjectBeforeConvertResponseToJSON(AnalysisFile.class);
                    logger.debug(fileData.getName());
                    uploadFileData.add(fileData);
                } catch (WebAPIException e) {
                    DialogUtil.error(e.getHeaderText(), e.getMessage(), getMainApp().getPrimaryStage(), true);
                } catch (IOException e) {
                    logger.error("Unknown Error", e);
                    DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
                }

            }
        });
    }

    private void panelSetting(ComboBox<ComboBoxItem> panelBox) {
        if(panelBox.getItems().isEmpty()) panelBox.getItems().removeAll(panelBox.getItems());
        List<Panel> panelList = new ArrayList<>();
        if(isServerItem && !isServerFastq) {
            panelList.addAll(panels.stream().filter(panel -> panel.getName().startsWith("TruSight Tumor 170")).collect(Collectors.toList()));
        } else {
            panelList.addAll(panels);
        }
        panelBox.setConverter(new ComboBoxConverter());
        panelBox.getItems().add(new ComboBoxItem());
        for(Panel panel :  panelList) {
            panelBox.getItems().add(new ComboBoxItem(panel.getId().toString(), panel.getName()));
        }
        panelBox.getSelectionModel().selectFirst();
        panelBox.valueProperty().addListener((ov, oldValue, newValue) -> {
        });
    }

    @FXML
    public void next() throws IOException {
        saveSampleData();
        sampleUploadController.pageSetting(2); }

    @FXML
    public void closeDialog() { sampleUploadController.closeDialog(); }

    @FXML
    public void showFileFindWindow() {
        maskerPane.setVisible(true);

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Directory");
        if(mainController.getBasicInformationMap().containsKey("path")) {
            directoryChooser.setInitialDirectory(new File((String) mainController.getBasicInformationMap().get("path")));
        } else {
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        File folder = directoryChooser.showDialog(this.sampleUploadController.getCurrentStage());

        if(folder != null) {
            File[] fileArray = folder.listFiles();
            assert fileArray != null;
            List<File> fileList = new ArrayList<>(Arrays.asList(fileArray));
            fileList = fileList.stream().filter(file -> file.getName().endsWith(".fastq.gz")).collect(Collectors.toList());
            if(fileList.isEmpty()) DialogUtil.alert("not found", "not found fastq file", sampleUploadController.getCurrentStage(), true);
            while (!fileList.isEmpty()) {
                mainController.getBasicInformationMap().put("path", folder.getAbsolutePath());
                File fastqFile = fileList.get(0);
                String fastqFilePairName = FileUtil.getFASTQFilePairName(fastqFile.getName());

                List<File> pairFileList = fileList.stream().filter(file ->
                        file.getName().startsWith(fastqFilePairName)).collect(Collectors.toList());

                Optional<AnalysisFile> optionalFile = uploadFileData.stream().filter(item ->
                        item.getName().contains(fastqFilePairName)).findFirst();
                Sample sample = null;
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
            }

        }
        tableEdit();

        maskerPane.setVisible(false);
    }

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
    private Sample getSameSample(Integer sampleId) {
        Optional<Sample> optionalSample = sampleArrayList.stream().filter(item -> (item.getId() != null
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
            Sample sample = new Sample();
            sample.setName(fastqFilePairName);
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

    @FXML
    private void fastqAdd() {
        maskerPane.setVisible(true);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        if(mainController.getBasicInformationMap().containsKey("path")) {
            fileChooser.setInitialDirectory(new File((String)mainController.getBasicInformationMap().get("path")));
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "fastq", "*.fastq", "*.fastq.gz");
        fileChooser.getExtensionFilters().add(fileExtensions);
        File file = fileChooser.showOpenDialog(this.sampleUploadController.getCurrentStage());

        if(file != null) {
            String fastqFilePairName = FileUtil.getFASTQFilePairName(file.getName());

            String chooseDirectoryPath = FilenameUtils.getFullPath(file.getAbsolutePath());
            logger.debug(String.format("directory path of choose bedFile : %s", chooseDirectoryPath));
            File directory = new File(chooseDirectoryPath);
            //선택한 파일의 폴더 내 모든 FASTQ 파일 검색
            List<File> fastqFilesInFolder = (List<File>) FileUtils.listFiles(directory, new String[]{"fastq.gz"}, false);

            List<File> pairFileList = fastqFilesInFolder.stream().filter(item ->
                    item.getName().startsWith(fastqFilePairName)).collect(Collectors.toList());

            //fastq 파일은 짝을 이루어야 함
            if(pairFileList.size() == 2 && checkSameSample(fastqFilePairName)) {
                addUploadFile(pairFileList, fastqFilePairName, true);
            }
            mainController.getBasicInformationMap().put("path", chooseDirectoryPath);
        }
        tableEdit();

        maskerPane.setVisible(false);
    }
}