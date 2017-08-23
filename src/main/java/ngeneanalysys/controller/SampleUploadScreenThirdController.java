package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.model.*;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.JsonUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-08-10
 */
public class SampleUploadScreenThirdController extends BaseStageController{
    private static Logger logger = LoggerUtil.getLogger();

    /** 메인 화면 컨트롤러 객체 */
    private MainController mainController;

    private SampleUploadController sampleUploadController;

    private List<Sample> sampleArrayList = null;

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    @FXML
    private GridPane standardDataGridPane;

    private APIService apiService;

    List<Panel> panels = null;

    List<Diseases> diseases = null;

    Map<String, Map<String, Object>> fileMap = new HashMap<>();

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
        if(sampleUploadController.getSamples() != null) {
            sampleArrayList = sampleUploadController.getSamples();
            tableEdit();
        }
    }

    /**
     * FASTQ 파일 Pair명 추출
     *
     * @param fileName
     * @return
     */
    public String getFASTQFilePairName(String fileName) {
        String pairName = "";
        if (fileName.contains("_")) {
            String[] arr = fileName.split("_");
            int idx = 0;
            for (String name : arr) {
                if (idx < arr.length - 2) {
                    if (idx > 0)
                        pairName += "_";
                    pairName += name;
                }
                idx++;
            }
        }
        return pairName;
    }

    @Override
    public void show(Parent root) throws IOException {
    }

    public void tableEdit() {
        standardDataGridPane.getChildren().clear();
        standardDataGridPane.setPrefHeight(0);

        int row = 0;
        HttpClientResponse response = null;
        try {
            response = apiService.get("/panels", null, null, false);
            panels = (List<Panel>)response.getMultiObjectBeforeConvertResponseToJSON(Panel.class, false);
            response = null;
            response = apiService.get("/diseases", null, null, false);
            diseases = (List<Diseases>)response.getMultiObjectBeforeConvertResponseToJSON(Diseases.class, false);
        } catch (Exception e) {
            e.printStackTrace();
        }


        for(Sample sample : sampleArrayList) {
            SampleSheet item = sample.getSampleSheet();
            standardDataGridPane.setPrefHeight(standardDataGridPane.getPrefHeight() + 26);

            TextField sampleName = new TextField();
            sampleName.setStyle("-fx-text-inner-color: black;");
            sampleName.setText(!StringUtils.isEmpty(item.getSampleName()) ? item.getSampleName() : item.getSampleId());

            /*TextField select = new TextField();
            select.setStyle("-fx-text-inner-color: black;");*/
            Button select = new Button();
            select.setText("SELECT");
            select.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose FASTQ File");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                fileChooser.getExtensionFilters()
                        .addAll(new FileChooser.ExtensionFilter("fastq", "*.fastq", "*.fastq.gz"));
                List<File> files = fileChooser.showOpenMultipleDialog(currentStage);

                Map<String, Object> fileMap = new HashMap<>();
                String name = null;
                if(sampleName.getText() != null && !"".equals(sampleName.getText())) {
                    name = sampleName.getText();
                }
                Long fileSize = 0L;
                for(File file : files) {
                    fileSize +=  file.length();
                }

                String fileName = getFASTQFilePairName(files.get(0).getName());

                fileMap.put("name", fileName);
                fileMap.put("fileSize", fileSize);
                fileMap.put("fileType", "FASTQ.GZ");
                this.fileMap.put(name, fileMap);


            });

            TextField paitentId = new TextField();
            paitentId.setStyle("-fx-text-inner-color: black;");

            /*TextField disease = new TextField();
            disease.setStyle("-fx-text-inner-color: black;");*/

            ComboBox<ComboBoxItem> disease  = new ComboBox<>();
            diseasesSetting(disease);

            ComboBox<ComboBoxItem> panel  = new ComboBox<>();
            panelSetting(panel);

            TextField source = new TextField();
            source.setStyle("-fx-text-inner-color: black;");
            source.setText("FFPE");

            standardDataGridPane.addRow(row, sampleName, select, paitentId, disease, panel, source);

            row++;
        }

    }

    @FXML
    public void submit() {
        try {
            if (sampleArrayList != null) {

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
                    params.put("sequencingPlatform", "MISEQ");
                    response = apiService.post("/runs", params, null, true);
                    run = response.getObjectBeforeConvertResponseToJSON(Run.class);
                    logger.info(run.toString());
                } catch (Exception e) {

                }


                for (int i = 0; i < standardDataGridPane.getChildren().size(); i += 6) {
                    int rowNum = i / 6;
                    Sample sample = sampleArrayList.get(rowNum);
                    sample.setRunId(run.getId());

                    TextField sampleName = (TextField) standardDataGridPane.getChildren().get(i);
                    sample.setName(sampleName.getText() + "RUN_" + rowNum);

                    Button fileName = (Button) standardDataGridPane.getChildren().get(i + 1);

                    TextField patientId = (TextField) standardDataGridPane.getChildren().get(i + 2);
                    sample.setPaitentId(patientId.getText());

                    ComboBox<ComboBoxItem> diseaseId = (ComboBox<ComboBoxItem>) standardDataGridPane.getChildren().get(i + 3);
                    sample.setDiseaseId(Integer.parseInt(diseaseId.getSelectionModel().getSelectedItem().getValue()));

                    ComboBox<ComboBoxItem> panelId = (ComboBox<ComboBoxItem>) standardDataGridPane.getChildren().get(i + 4);
                    sample.setPanelId(Integer.parseInt(panelId.getSelectionModel().getSelectedItem().getValue()));

                    TextField sampleSource = (TextField) standardDataGridPane.getChildren().get(i + 5);
                    sample.setSampleSource((sampleSource.getText() == null || sampleSource.getText().equals(""))
                            ? "FFPE" : sampleSource.getText());

                    //sampleUpload(sample);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        logger.info("submit");

        closeDialog();
    }

    public void sampleUpload(Sample sample) throws  Exception {
        Map<String, Object> params = new HashMap<>();
        HttpClientResponse response = null;

        params.put("runId", sample.getRunId());
        params.put("name", sample.getName());
        params.put("patientId", sample.getPaitentId());
        params.put("panelId", sample.getPanelId());
        params.put("diseaseId", sample.getDiseaseId());
        params.put("analysisType", "SOMATIC");
        params.put("sampleSource", sample.getSampleSource());
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
        QCData qcData1 = sample.getQcData();
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

        String name = (!StringUtils.isEmpty(sample.getSampleSheet().getSampleName()))
                ? sample.getSampleSheet().getSampleName() : sample.getSampleSheet().getSampleId();

        Map<String, Object> fileData = fileMap.get(name);

        if(fileData != null) {
            fileData.put("sampleId", sampleData.getId());
            response = apiService.post("/analysisFiles", fileData, null, true);
            AnalysisFile file = response.getObjectBeforeConvertResponseToJSON(AnalysisFile.class);
            logger.info(file.getName());
        }

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
    }

    @FXML
    public void back() throws IOException { sampleUploadController.pageSetting(2); }

    @FXML
    public void closeDialog() { sampleUploadController.closeDialog(); }
}
