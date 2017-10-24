package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.FileUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
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

    @FXML
    private GridPane standardDataGridPane;

    private APIService apiService;

    List<Panel> panels = null;

    List<Diseases> diseases = null;

    Map<String, Map<String, Object>> fileMap = null;

    List<File> uploadFileList = null;

    List<AnalysisFile> uploadFileData = null;

    List<TextField> sampleNameTextFieldList = new ArrayList<>();

    List<Button> fileSelectButtonList = new ArrayList<>();

    List<TextField> patientIdTextFieldList = new ArrayList<>();

    List<ComboBox<ComboBoxItem>> diseaseComboBoxList = new ArrayList<>();

    List<ComboBox<ComboBoxItem>> panelComboBoxList = new ArrayList<>();

    List<TextField> sampleSourceTextFieldList = new ArrayList<>();

    //화면에 표시될 row 수
    private int totalRow = 0;

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

    public void tableEdit() {
        int row = 0;

        //sample 수만큼 row를 생성
        while(sampleArrayList.size() > totalRow) {
            createRow(totalRow++);
        }

        for(Sample sample : sampleArrayList) {
            if(row > 22) break;

            sampleNameTextFieldList.get(row).setText(sample.getName());
            patientIdTextFieldList.get(row).setText((sample.getPaitentId() != null) ? sample.getPaitentId().toString() : "");

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

            row++;
        }

    }

    public void createRow(int row) {
        standardDataGridPane.setPrefHeight(standardDataGridPane.getPrefHeight() + 27);
        TextField sampleName = new TextField();
        sampleName.setStyle("-fx-text-inner-color: black;");
        sampleName.setMaxWidth(Double.MAX_VALUE);
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
        paitentId.setStyle("-fx-text-inner-color: black;");
        paitentId.setMaxWidth(Double.MAX_VALUE);
        patientIdTextFieldList.add(paitentId);

        ComboBox<ComboBoxItem> disease  = new ComboBox<>();
        disease.setMaxWidth(Double.MAX_VALUE);
        diseaseComboBoxList.add(disease);
        diseasesSetting(disease);

        ComboBox<ComboBoxItem> panel  = new ComboBox<>();
        panel.setMaxWidth(Double.MAX_VALUE);
        panelComboBoxList.add(panel);
        panelSetting(panel);
        panel.setOnAction(event -> {
            ComboBox<ComboBoxItem> obj = (ComboBox<ComboBoxItem>) event.getSource();
            if(panelComboBoxList.contains(obj)) {
                int index  = panelComboBoxList.indexOf(obj);
                ComboBoxItem item = obj.getSelectionModel().getSelectedItem();
                logger.info(item.getText());
                panels.stream().forEach(panelItem -> {
                    if(panelItem.getId() == Integer.parseInt(item.getValue())) {
                        sampleSourceTextFieldList.get(index).setText(panelItem.getSampleSource());
                    }
                });
            }
        });

        TextField source = new TextField();
        source.setMaxWidth(Double.MAX_VALUE);
        sampleSourceTextFieldList.add(source);
        source.setEditable(false);
        source.setStyle("-fx-text-inner-color: black;");
        source.setText(panels.get(0).getSampleSource());

        //standardDataGridPane.addRow(row, sampleName, select, panel, source, disease, paitentId);
        standardDataGridPane.addRow(row, sampleName, panel, source, disease, paitentId);
    }

    public void saveSampleData() {
        for (int i = 0; i < standardDataGridPane.getChildren().size(); i += 5) {
            int rowNum = i / 6;
            Sample sample = null;
            if(sampleArrayList.size() > rowNum) {
                sample = sampleArrayList.get(rowNum);
            } else {
                sample = new Sample();
                sample.setQcData(new QcData());
                sample.setSampleSheet(new SampleSheet());
            }
            TextField sampleName = (TextField) standardDataGridPane.getChildren().get(i);
            if(sampleName.getText().isEmpty()) continue;
            sample.setName(sampleName.getText());

            ComboBox<ComboBoxItem> panelIdComboBox = (ComboBox<ComboBoxItem>) standardDataGridPane.getChildren().get(i + 1);
            Integer panelId = Integer.parseInt(panelIdComboBox.getSelectionModel().getSelectedItem().getValue());
            sample.setPanelId(panelId);

            ComboBox<ComboBoxItem> diseaseId = (ComboBox<ComboBoxItem>) standardDataGridPane.getChildren().get(i + 3);
            sample.setDiseaseId(Integer.parseInt(diseaseId.getSelectionModel().getSelectedItem().getValue()));

            TextField patientId = (TextField) standardDataGridPane.getChildren().get(i + 4);
            sample.setPaitentId(patientId.getText());

            Optional<Panel> panel = panels.stream().filter(item -> item.getId().equals(panelId)).findFirst();
            if(panel.isPresent()) {
                Panel p = panel.get();
                //sample.setAnalysisType(p.getAnalysisType());
                TextField sampleSource = (TextField) standardDataGridPane.getChildren().get(i + 2);
                //sample.setSampleSource((sampleSource.getText() == null || sampleSource.getText().equals(""))
                //        ? "FFPE" : sampleSource.getText());
                //sample.setSampleSource(p.getSampleSource());
            }
        }
    }

    @FXML
    public void submit() {

        if(sampleUploadController.getRunId() != -1) {
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
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.hh:mm:ss");
                        params.put("name", "RUN NAME_" + sdf.format(date));
                    } else {
                        params.put("name", sampleUploadController.getRunName());
                    }
                    params.put("sequencingPlatform", sampleUploadController.getSequencerType().getUserData());
                    response = apiService.post("/runs", params, null, true);
                    run = response.getObjectBeforeConvertResponseToJSON(Run.class);
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
        logger.info(sampleData.toString());

        /*String name = (!StringUtils.isEmpty(sample.getSampleSheet().getSampleName()))
                ? sample.getSampleSheet().getSampleName() : sample.getSampleSheet().getSampleId();*/
        String name = sample.getName();

        Set<String> fileName = fileMap.keySet();

        fileName.stream().forEach(file -> {
            Map<String, Object> fileInfo = fileMap.get(file);

            if(fileInfo.get("sampleName") != null && fileInfo.get("sampleName").toString().equals(sample.getName())) {
                fileInfo.put("sampleId", sampleData.getId());
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

    public void diseasesSetting(ComboBox<ComboBoxItem> diseaseBox) {
        diseaseBox.setConverter(new ComboBoxConverter());
        for(Diseases disease :  diseases) {
            diseaseBox.getItems().add(new ComboBoxItem(disease.getId().toString(), disease.getName()));
        }
        diseaseBox.getSelectionModel().selectFirst();
    }

    public void panelSetting(ComboBox<ComboBoxItem> panelBox) {
        panelBox.setConverter(new ComboBoxConverter());
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
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File folder = directoryChooser.showDialog(this.sampleUploadController.getCurrentStage());

        if(folder != null) {
            File[] fileArray = folder.listFiles();
            List<File> fileList = new ArrayList<>(Arrays.asList(fileArray));
            fileList = fileList.stream().filter(file -> file.getName().endsWith(".fastq.gz")).collect(Collectors.toList());

            while(!fileList.isEmpty()) {
                File fastqFile = fileList.get(0);
                String fastqFilePairName = FileUtil.getFASTQFilePairName(fastqFile.getName());

                List<File> pairFileList = fileList.stream().filter(file ->
                    file.getName().startsWith(fastqFilePairName)).collect(Collectors.toList());

                if(pairFileList.size() == 2 && !checkSameSample(fastqFilePairName)) {
                    addUploadFile(pairFileList, fastqFilePairName);
                } else {
                    fileList.removeAll(pairFileList);
                }
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
        boolean overlapCheck = false;
        for(Sample sample : sampleArrayList) {
            if(sample.getName().equals(name)) {
                overlapCheck = true;
                break;
            }
        }
        return overlapCheck;
    }

    private void addUploadFile(List<File> fileList, String fastqFilePairName) {
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
        Sample sample = new Sample();
        sample.setName(fastqFilePairName);
        sample.setSampleSheet(new SampleSheet());
        sample.setQcData(new QcData());
        sampleArrayList.add(sample);
    }

    @FXML
    private void fastqAdd() {
        maskerPane.setVisible(true);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Directory");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "fastq", "*.fastq", "*.fastq.gz");
        fileChooser.getExtensionFilters().add(fileExtensions);
        File file = fileChooser.showOpenDialog(this.sampleUploadController.getCurrentStage());

        if(file != null) {
            String fastqFilePairName = FileUtil.getFASTQFilePairName(file.getName());

            String chooseDirectoryPath = FilenameUtils.getFullPath(file.getAbsolutePath());
            logger.info(String.format("directory path of choose file : %s", chooseDirectoryPath));
            File directory = new File(chooseDirectoryPath);
            //선택한 파일의 폴더 내 모든 파일의 수
            List<File> fastqFilesInFolder = (List<File>) FileUtils.listFiles(directory, new String[]{"fastq.gz"}, false);

            List<File> pairFileList = fastqFilesInFolder.stream().filter(item ->
                    item.getName().startsWith(fastqFilePairName)).collect(Collectors.toList());

            //fastq 파일은 짝을 이루어야 함
            if(pairFileList.size() == 2 && !checkSameSample(fastqFilePairName)) {
                addUploadFile(pairFileList, fastqFilePairName);
            }
        }
        tableEdit();

        maskerPane.setVisible(false);
    }
}