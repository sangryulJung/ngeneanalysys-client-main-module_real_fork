package ngeneanalysys.controller;

import com.opencsv.CSVReader;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    private APIService apiService;

    List<Panel> panels = null;

    List<Diseases> diseases = null;

    Map<String, Map<String, Object>> fileMap = null;

    List<File> uploadFileList = null;

    List<AnalysisFile> uploadFileData = null;

    List<TextField> sampleNameTextFieldList = new ArrayList<>();

    List<TextField> patientIdTextFieldList = new ArrayList<>();

    List<ComboBox<ComboBoxItem>> diseaseComboBoxList = new ArrayList<>();

    List<ComboBox<ComboBoxItem>> panelComboBoxList = new ArrayList<>();

    List<TextField> sampleSourceTextFieldList = new ArrayList<>();

    private boolean isServerItem = false;

    //화면에 표시될 row 수
    private int totalRow = 0;

    public void setServerItem(String path) {
        isServerItem = true;
        logger.info(path);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("runDir", path);

            Task<Void> task = new SampleSheetDownloadTask(this.sampleUploadController, this, path);
            final Thread downloadThread = new Thread(task);

            downloadThread.setDaemon(true);
            downloadThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSampleSheet(String path) {
        try(CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(path)))) {
            String[] s;
            boolean tableData = false;
            while((s = csvReader.readNext()) != null) {
                if (tableData && sampleArrayList.size() < 23) {
                    final String sampleName = s[1];
                    logger.info(s[0]);
                    Sample sample = new Sample();
                    sample.setName(sampleName);
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
     * @param mainController
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setMainApp(this.mainController.getMainApp());
        apiService = APIService.getInstance();
    }

    /**
     * @param sampleUploadController
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
            fileList = fileList.stream().filter(file -> file.getName().endsWith(".fastq.gz")).collect(Collectors.toList());

            while (!fileList.isEmpty()) {
                List<File> undeterminedFile = new ArrayList<>();
                fileList.stream().forEach(file -> {
                    if(file.getName().toUpperCase().startsWith("UNDETERMINED_"))
                        undeterminedFile.add(file);
                });

                if(!undeterminedFile.isEmpty()) fileList.removeAll(undeterminedFile);

                if(fileList.isEmpty()) return;

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
                } else if (pairFileList.size() == 2 && !checkSameSample(fastqFilePairName)) {
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public void tableEdit() {
        int row = 0;

        //sample 수만큼 row를 생성
        while(sampleArrayList.size() > totalRow) {
            createRow(totalRow++);
        }

        for(Sample sample : sampleArrayList) {
            if(row > 22) break;

            sampleNameTextFieldList.get(row).setText(sample.getName());

            patientIdTextFieldList.get(row).setText((sample.getPaitentId() != null) ? sample.getPaitentId() : "");

            if(sample.getDiseaseId() != null) {
                ComboBox<ComboBoxItem> disease = diseaseComboBoxList.get(row);
                disease.getItems().forEach(diseaseItem ->{
                    if(diseaseItem.getValue().equals(sample.getDiseaseId().toString())) {
                        disease.getSelectionModel().select(diseaseItem);
                        return;
                    }
                });
            }

            if(sample.getPanelId() != null) {
                ComboBox<ComboBoxItem> panel = panelComboBoxList.get(row);
                TextField sampleSource = sampleSourceTextFieldList.get(row);
                panel.getItems().forEach(panelItem ->{
                    if(panelItem.getValue().equals(sample.getPanelId().toString())) {
                        panel.getSelectionModel().select(panelItem);
                        if(panels != null && !panels.isEmpty()) {
                            final Optional<Panel> selectPanel = panels.stream().filter(item -> item.getId().equals(sample.getPanelId())).findFirst();
                            if(selectPanel.isPresent())
                                sampleSource.setText(selectPanel.get().getSampleSource());
                        }
                        return;
                    }
                });
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
                patientIdTextFieldList.get(row).setDisable(true);
                panelComboBoxList.get(row).setDisable(true);
                diseaseComboBoxList.get(row).setDisable(true);
                sampleSourceTextFieldList.get(row).setDisable(true);

            }

            row++;
        }

    }

    public void createRow(int row) {
        standardDataGridPane.setPrefHeight(standardDataGridPane.getPrefHeight() + 28);
        TextField sampleName = new TextField();
        sampleName.setStyle("-fx-text-inner-color: black; -fx-border-width: 0;");
        sampleName.setMaxWidth(Double.MAX_VALUE);
        sampleName.setEditable(false);
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

        TextField paitentId = new TextField();
        paitentId.setStyle("-fx-text-inner-color: black; -fx-border-width: 0;");
        paitentId.setMaxWidth(Double.MAX_VALUE);
        patientIdTextFieldList.add(paitentId);

        ComboBox<ComboBoxItem> disease  = new ComboBox<>();
        disease.setConverter(new ComboBoxConverter());
        disease.setMaxWidth(Double.MAX_VALUE);
        disease.setStyle("-fx-border-width: 0;");
        disease.setCursor(Cursor.HAND);
        diseaseComboBoxList.add(disease);
        //settingDiseaseComboBox(panels.get(0).getId(), row);
        //diseasesSetting(disease);

        ComboBox<ComboBoxItem> panel  = new ComboBox<>();
        panel.setMaxWidth(Double.MAX_VALUE);
        panelComboBoxList.add(panel);
        panelSetting(panel);
        panel.setCursor(Cursor.HAND);
        panel.setStyle("-fx-text-inner-color: black; -fx-border-width: 0;");
        panel.setOnAction(event -> {
            ComboBox<ComboBoxItem> obj = (ComboBox<ComboBoxItem>) event.getSource();
            if(panelComboBoxList.contains(obj)) {
                int index  = panelComboBoxList.indexOf(obj);
                ComboBoxItem item = obj.getSelectionModel().getSelectedItem();
                logger.info(item.getText());
                if(!StringUtils.isEmpty(item.getValue())) {
                    panels.stream().forEach(panelItem -> {
                        if (panelItem.getId() == Integer.parseInt(item.getValue())) {
                            sampleSourceTextFieldList.get(index).setText(panelItem.getSampleSource());
                        }
                    });

                    if (index == 0) {
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

        TextField source = new TextField();
        source.setMaxWidth(Double.MAX_VALUE);
        sampleSourceTextFieldList.add(source);
        source.setEditable(false);
        source.setStyle("-fx-text-inner-color: black; -fx-border-width: 0;");
        //source.setText(panels.get(0).getSampleSource());

        //standardDataGridPane.addRow(row, sampleName, select, panel, source, disease, paitentId);
        standardDataGridPane.addRow(row, sampleName, panel, source, disease, paitentId);
        panel.getSelectionModel().select(0);
    }

    public void panelBatchApplication(ComboBoxItem item) {
        for(int index = 1 ; index < panelComboBoxList.size(); index++) {
            ComboBox<ComboBoxItem> panel = panelComboBoxList.get(index);
            panel.getSelectionModel().select(item);
            final int itemIndex = index;
            panels.stream().forEach(panelItem -> {
                if (panelItem.getId() == Integer.parseInt(item.getValue())) {
                    sampleSourceTextFieldList.get(itemIndex).setText(panelItem.getSampleSource());
                }
            });

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

        } catch (WebAPIException wae) {
            wae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSampleData() {
        int rowCount = standardDataGridPane.getChildren().size() / 5;

        for (int i = 0; i < rowCount; i++) {
            Sample sample = null;
            sample = sampleArrayList.get(i);
            if(sample.getId() != null) continue;

            TextField sampleName = sampleNameTextFieldList.get(i);
            if(sampleName.getText().isEmpty()) continue;
            sample.setName(sampleName.getText());

            ComboBox<ComboBoxItem> panelIdComboBox = panelComboBoxList.get(i);
            Integer panelId = Integer.parseInt(panelIdComboBox.getSelectionModel().getSelectedItem().getValue());
            sample.setPanelId(panelId);

            ComboBox<ComboBoxItem> diseaseId = diseaseComboBoxList.get(i);
            sample.setDiseaseId(Integer.parseInt(diseaseId.getSelectionModel().getSelectedItem().getValue()));

            TextField patientId = patientIdTextFieldList.get(i);
            sample.setPaitentId(patientId.getText());
        }
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

            logger.info("submit");
            closeDialog();
            return;
        }

        try {
            if (sampleArrayList != null && !sampleArrayList.isEmpty()) {

                Map<String, Object> params = new HashMap<>();
                HttpClientResponse response = null;
                Run run = null;
                try {
                    if(sampleUploadController.getRunName() == null || "".equals(sampleUploadController.getRunName())) {
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
                        params.put("name", sdf.format(date));
                    } else {
                        params.put("name", sampleUploadController.getRunName());
                    }
                    params.put("sequencingPlatform", sampleUploadController.getSequencerType().getUserData());
                    response = apiService.post("/runs", params, null, true);
                    run = response.getObjectBeforeConvertResponseToJSON(Run.class);
                    mainController.getBasicInformationMap().put("runId", run.getId());
                    logger.info(run.toString());

                    saveSampleData();

                    for (Sample sample : sampleArrayList) {
                        sample.setRunId(run.getId());
                        sampleUpload(sample);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if((uploadFileData != null && !uploadFileData.isEmpty()) &&
                        (uploadFileList != null && !uploadFileList.isEmpty()))
                    this.mainController.runningAnalysisRequestUpload(uploadFileData, uploadFileList, run);
                logger.info("submit");
                closeDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

                    TextField patientId = patientIdTextFieldList.get(row);
                    sample.setPaitentId(patientId.getText());
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

    public void sampleUpload(Sample sample) throws  Exception {

        Map<String, Object> params = new HashMap<>();
        HttpClientResponse response = null;

        params.put("runId", sample.getRunId());
        params.put("name", sample.getName());
        params.put("patientId", sample.getPaitentId());
        params.put("panelId", sample.getPanelId());
        params.put("diseaseId", sample.getDiseaseId());
//        params.put("analysisType", sample.getAnalysisType());
//        params.put("sampleSource", sample.getSampleSource());
        params.put("inputFType", "FASTQ.GZ");
        Map<String, String> sampleSheet = new HashMap<>();
        SampleSheet sampleSheet1 = sample.getSampleSheet();
        sampleSheet.put("sampleId", sampleSheet1.getSampleId());
        sampleSheet.put("sampleName", sampleSheet1.getSampleName());
        sampleSheet.put("samplePlate", sampleSheet1.getSamplePlate());
        sampleSheet.put("sampleWell", sampleSheet1.getSampleWell());
        sampleSheet.put("i7IndexId", sampleSheet1.getI7IndexId());
        sampleSheet.put("sampleIndex", sampleSheet1.getSampleIndex());
        sampleSheet.put("sampleProject", sampleSheet1.getSampleProject());
        sampleSheet.put("description", sampleSheet1.getDescription());
        params.put("sampleSheet", sampleSheet);
        Map<String, String> qcData = new HashMap<>();
        QcData qcData1 = sample.getQcData();
        qcData.put("dnaQC", qcData1.getDnaQC());
        qcData.put("libraryQC", qcData1.getLibraryQC());
        qcData.put("seqClusterDensity", qcData1.getSeqClusterDensity());
        qcData.put("seqQ30", qcData1.getSeqQ30());
        qcData.put("seqClusterPF", qcData1.getSeqClusterPF());
        qcData.put("seqIndexingPFCV", qcData1.getSeqIndexingPFCV());
        params.put("qcData", qcData);
        response = apiService.post("/samples", params, null, true);
        Sample sampleData = response.getObjectBeforeConvertResponseToJSON(Sample.class);

        postAnalysisFilesData(sampleData);

    }

    public void postAnalysisFilesData(Sample sample) {
        Set<String> fileName = fileMap.keySet();

        fileName.stream().forEach(file -> {
            Map<String, Object> fileInfo = fileMap.get(file);

            if(fileInfo.get("sampleName") != null && fileInfo.get("sampleName").toString().equals(sample.getName())) {
                fileInfo.put("sampleId", sample.getId());
                fileInfo.put("sampleName", null);
                fileInfo.remove("sampleName");
                HttpClientResponse fileResponse = null;
                try {
                    fileResponse = apiService.post("/analysisFiles", fileInfo, null, true);
                    AnalysisFile fileData = fileResponse.getObjectBeforeConvertResponseToJSON(AnalysisFile.class);
                    logger.info(fileData.getName());
                    uploadFileData.add(fileData);
                } catch (WebAPIException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void panelSetting(ComboBox<ComboBoxItem> panelBox) {
        panelBox.setConverter(new ComboBoxConverter());
        panelBox.getItems().add(new ComboBoxItem());
        for(Panel panel :  panels) {
            panelBox.getItems().add(new ComboBoxItem(panel.getId().toString(), panel.getName()));
        }
        panelBox.getSelectionModel().selectFirst();
        panelBox.valueProperty().addListener((ov, oldValue, newValue) -> {
            ComboBoxItem item = newValue;
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
            List<File> fileList = new ArrayList<>(Arrays.asList(fileArray));
            fileList = fileList.stream().filter(file -> file.getName().endsWith(".fastq.gz")).collect(Collectors.toList());

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
                } else if (pairFileList.size() == 2 && !checkSameSample(fastqFilePairName)) {
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
     * @param name
     * @return
     */
    private boolean checkSameSample(String name) {
        return sampleArrayList.stream().anyMatch(item -> name.equals(item.getName()));
    }

    /**
     * 동일한 샘플을 검색하여 해당 sample의 id 값을 리턴
     * @param sampleId
     * @return
     */
    private Sample getSameSample(Integer sampleId) {
        Optional<Sample> optionalSample = sampleArrayList.stream().filter(item -> (item.getId() != null
                        && sampleId.equals(item.getId()))).findFirst();
        if(optionalSample.isPresent()) return optionalSample.get();
        return null;
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
            sample.setSampleSheet(new SampleSheet());
            sample.setQcData(new QcData());
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
            logger.info(String.format("directory path of choose bedFile : %s", chooseDirectoryPath));
            File directory = new File(chooseDirectoryPath);
            //선택한 파일의 폴더 내 모든 FASTQ 파일 검색
            List<File> fastqFilesInFolder = (List<File>) FileUtils.listFiles(directory, new String[]{"fastq.gz"}, false);

            List<File> pairFileList = fastqFilesInFolder.stream().filter(item ->
                    item.getName().startsWith(fastqFilePairName)).collect(Collectors.toList());

            //fastq 파일은 짝을 이루어야 함
            if(pairFileList.size() == 2 && !checkSameSample(fastqFilePairName)) {
                addUploadFile(pairFileList, fastqFilePairName, true);
            }
            mainController.getBasicInformationMap().put("path", chooseDirectoryPath);
        }
        tableEdit();

        maskerPane.setVisible(false);
    }


    @FXML
    public void showFileFindWindow2() {
        maskerPane.setVisible(true);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "fastq", "*.fastq", "*.fastq.gz");
        fileChooser.getExtensionFilters().add(fileExtensions);
        List<File> fileList = fileChooser.showOpenMultipleDialog(this.sampleUploadController.getCurrentStage());
        List<File> addFileList = new ArrayList<>();

        if(fileList != null && !fileList.isEmpty()) {
            String chooseDirectoryPath = FilenameUtils.getFullPath(fileList.get(0).getAbsolutePath());
            logger.info(String.format("directory path of choose bedFile : %s", chooseDirectoryPath));
            File directory = new File(chooseDirectoryPath);
            //선택한 파일의 폴더 내 모든 FASTQ 파일 검색
            List<File> fastqFilesInFolder = (List<File>) FileUtils.listFiles(directory, new String[]{"fastq.gz"}, false);

            fileList = fileList.stream().filter(file -> file.getName().endsWith(".fastq.gz")).collect(Collectors.toList());

            while (!fileList.isEmpty()) {
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
                } else if (pairFileList.size() == 2 && !checkSameSample(fastqFilePairName)) {
                    addUploadFile(pairFileList, fastqFilePairName, true);
                }

                fileList.removeAll(pairFileList);
            }

        }
        tableEdit();

        maskerPane.setVisible(false);
    }
}