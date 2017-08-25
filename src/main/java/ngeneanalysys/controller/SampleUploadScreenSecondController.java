package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.exceptions.ExcelParseException;
import ngeneanalysys.model.QcData;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.SampleSheet;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.util.*;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-08-10
 */
public class SampleUploadScreenSecondController extends BaseStageController {
    private static Logger logger = LoggerUtil.getLogger();

    /** 메인 화면 컨트롤러 객체 */
    private MainController mainController;

    private SampleUploadController sampleUploadController;

    private List<Sample> sampleArrayList;

    @FXML
    private GridPane qcGridPane;

    /**
     * @param sampleUploadController
     */
    public void setSampleUploadController(SampleUploadController sampleUploadController) {
        this.sampleUploadController = sampleUploadController;
        if(sampleUploadController.getSamples() != null &&!sampleUploadController.getSamples().isEmpty()) {
            sampleArrayList = sampleUploadController.getSamples();
            tableEdit();
        }
    }


    /**
     * @param mainController
     *            the mainController to set
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setMainApp(this.mainController.getMainApp());
    }

    @Override
    public void show(Parent root) throws IOException {

    }

    public void tableEdit() {
        qcGridPane.getChildren().clear();
        qcGridPane.setPrefHeight(0);

        int row = 0;

        for(Sample sample : sampleArrayList) {
            SampleSheet item = sample.getSampleSheet();
            qcGridPane.setPrefHeight(qcGridPane.getPrefHeight() + 26);

            TextField sampleName = new TextField();
            sampleName.setStyle("-fx-text-inner-color: black;");
            sampleName.setText(!StringUtils.isEmpty(item.getSampleName()) ?  item.getSampleName() : item.getSampleId());

            ComboBox<ComboBoxItem>  dnaQc = new ComboBox<>();
            qcSetting(dnaQc);
            //if(sample.getQcData() != null && "Fail".equals(sample.getQcData().getDnaQC()))
            if(sample.getQcData() != null && sample.getQcData().getDnaQC().startsWith("F"))
                dnaQc.getSelectionModel().select(1);

            ComboBox<ComboBoxItem>  libraryQc = new ComboBox<>();
            qcSetting(libraryQc);
            //if(sample.getQcData() != null && "Fail".equals(sample.getQcData().getLibraryQC()))
            if(sample.getQcData() != null && sample.getQcData().getLibraryQC().startsWith("F"))
                libraryQc.getSelectionModel().select(1);

            ComboBox<ComboBoxItem>  seqClusterDensity = new ComboBox<>();
            qcSetting(seqClusterDensity);
            if(sample.getQcData() != null && sample.getQcData().getSeqClusterDensity().startsWith("F"))
                seqClusterDensity.getSelectionModel().select(1);

            ComboBox<ComboBoxItem>  seqQ30 = new ComboBox<>();
            qcSetting(seqQ30);
            //if(sample.getQcData() != null && "Fail".equals(sample.getQcData().getSeqQ30()))
            if(sample.getQcData() != null && sample.getQcData().getSeqQ30().startsWith("F"))
                seqQ30.getSelectionModel().select(1);

            ComboBox<ComboBoxItem>  seqClusterPF = new ComboBox<>();
            qcSetting(seqClusterPF);
            //if(sample.getQcData() != null && "Fail".equals(sample.getQcData().getSeqClusterPF()))
            if(sample.getQcData() != null && sample.getQcData().getSeqClusterPF().startsWith("F"))
                seqClusterPF.getSelectionModel().select(1);

            ComboBox<ComboBoxItem>  seqIndexingPFCV = new ComboBox<>();
            qcSetting(seqIndexingPFCV);
            //if(sample.getQcData() != null && "Fail".equals(sample.getQcData().getSeqIndexingPFCV()))
            if(sample.getQcData() != null && sample.getQcData().getSeqIndexingPFCV().startsWith("F"))
                seqIndexingPFCV.getSelectionModel().select(1);

            qcGridPane.addRow(row, sampleName, dnaQc, libraryQc, seqClusterDensity, seqQ30, seqClusterPF, seqIndexingPFCV);

            row++;
        }

    }

    public void qcSetting(ComboBox<ComboBoxItem> qc) {
        qc.setConverter(new ComboBoxConverter());
        qc.getItems().add(new ComboBoxItem("P", "P"));
        qc.getItems().add(new ComboBoxItem("F", "F"));
        qc.getSelectionModel().selectFirst();
    }

    public void saveQCData() {
        for (int i = 0; i < qcGridPane.getChildren().size(); i += 7) {
            int indexNumber = i / 7;
            Sample sample = sampleArrayList.get(indexNumber);

            QcData qcData = sample.getQcData();

            if(qcData == null) {
                qcData = new QcData();
                sample.setQcData(qcData);
            }

            ComboBox<ComboBoxItem> dnaQC = (ComboBox<ComboBoxItem>) qcGridPane.getChildren().get(i + 1);
            qcData.setDnaQC(dnaQC.getSelectionModel().getSelectedItem().getValue());

            ComboBox<ComboBoxItem> libraryQc = (ComboBox<ComboBoxItem>) qcGridPane.getChildren().get(i + 2);
            qcData.setLibraryQC(libraryQc.getSelectionModel().getSelectedItem().getValue());

            ComboBox<ComboBoxItem> seqClusterDensity = (ComboBox<ComboBoxItem>) qcGridPane.getChildren().get(i + 3);
            qcData.setSeqClusterDensity(seqClusterDensity.getSelectionModel().getSelectedItem().getValue());

            ComboBox<ComboBoxItem> seqQ30 = (ComboBox<ComboBoxItem>) qcGridPane.getChildren().get(i + 4);
            qcData.setSeqQ30(seqQ30.getSelectionModel().getSelectedItem().getValue());

            ComboBox<ComboBoxItem> seqClusterPF = (ComboBox<ComboBoxItem>) qcGridPane.getChildren().get(i + 5);
            qcData.setSeqClusterPF(seqClusterPF.getSelectionModel().getSelectedItem().getValue());

            ComboBox<ComboBoxItem> seqIndexingPFCV = (ComboBox<ComboBoxItem>) qcGridPane.getChildren().get(i + 6);
            qcData.setSeqIndexingPFCV(seqIndexingPFCV.getSelectionModel().getSelectedItem().getValue());

        }
    }

    @FXML
    public void closeDialog() { if(sampleUploadController != null) sampleUploadController.closeDialog(); }

    @FXML
    public void back() throws IOException {
        if(sampleArrayList != null && sampleArrayList.size() > 0) {
            saveQCData();
        }
        sampleUploadController.pageSetting(1);
    }

    @FXML
    public void next() throws IOException {

        if(sampleArrayList != null && sampleArrayList.size() > 0) {
            saveQCData();
        }

        sampleUploadController.pageSetting(3);
    }

    @FXML
    public void showFileFindWindow() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose qcData File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("xlsx", "*.xlsx"));
        File file = fileChooser.showOpenDialog(sampleUploadController.getCurrentStage());

        if(file != null && file.getName().equalsIgnoreCase("qcInfo.xlsx") && sampleArrayList != null) {
            try {
                Map<String, QcData> qcList = WorksheetUtil.readQCDataExcelSheet(file);

                for(Sample sample : sampleArrayList) {
                    String name = (!StringUtils.isEmpty(sample.getSampleSheet().getSampleName()))
                            ? sample.getSampleSheet().getSampleName() : sample.getSampleSheet().getSampleId();

                    QcData excelQCData = qcList.get(name);

                    if(qcList.get(name) != null) {
                        logger.info(name);
                        sample.setQcData(excelQCData);
                    } else {
                        DialogUtil.alert("찾을 수 없음", name + " 샘플에 대한 QC 정보를 찾을 수 없음", sampleUploadController.getCurrentStage(), true);
                    }
                }

                tableEdit();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExcelParseException e) {
                e.printStackTrace();
            }

        }

    }

}
