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
import java.util.ArrayList;
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
        qcGridPane.getChildren().clear();
        qcGridPane.setPrefHeight(0);

        for(int row  = 0 ; row < 23 ; row++) {
            qcGridPane.setPrefHeight(qcGridPane.getPrefHeight() + 26);

            TextField sampleName = new TextField();
            sampleName.setStyle("-fx-text-inner-color: black;");

            ComboBox<ComboBoxItem>  dnaQc = new ComboBox<>();
            qcSetting(dnaQc);
            //if(sample.getQcData() != null && "Fail".equals(sample.getQcData().getDnaQC()))

            ComboBox<ComboBoxItem>  libraryQc = new ComboBox<>();
            qcSetting(libraryQc);
            //if(sample.getQcData() != null && "Fail".equals(sample.getQcData().getLibraryQC()))

            ComboBox<ComboBoxItem>  seqClusterDensity = new ComboBox<>();
            qcSetting(seqClusterDensity);

            ComboBox<ComboBoxItem>  seqQ30 = new ComboBox<>();
            qcSetting(seqQ30);
            //if(sample.getQcData() != null && "Fail".equals(sample.getQcData().getSeqQ30()))

            ComboBox<ComboBoxItem>  seqClusterPF = new ComboBox<>();
            qcSetting(seqClusterPF);
            //if(sample.getQcData() != null && "Fail".equals(sample.getQcData().getSeqClusterPF()))

            ComboBox<ComboBoxItem>  seqIndexingPFCV = new ComboBox<>();
            qcSetting(seqIndexingPFCV);
            //if(sample.getQcData() != null && "Fail".equals(sample.getQcData().getSeqIndexingPFCV()))

            qcGridPane.addRow(row, sampleName, dnaQc, libraryQc, seqClusterDensity, seqQ30, seqClusterPF, seqIndexingPFCV);
        }

        if(sampleUploadController.getSamples() != null) {
            sampleArrayList = sampleUploadController.getSamples();
            tableEdit();
        }
    }

    public void tableEdit() {
        int rowIndex = 0;
        int totalIndex = 0;
        for(Sample sample : sampleArrayList) {
            //입력가능한 샘플의 총 양은 23개
            if (22 < rowIndex) break;

            QcData qcData = sample.getQcData();

            if(qcData == null) {
                sample.setQcData(new QcData());
                qcData = sample.getQcData();
            }

            TextField sampleName = (TextField) qcGridPane.getChildren().get(totalIndex);
            sampleName.setText(!StringUtils.isEmpty(sample.getSampleSheet().getSampleName())
                    ?  sample.getSampleSheet().getSampleName() : sample.getSampleSheet().getSampleId());

            ComboBox<ComboBoxItem> dnaQc = (ComboBox<ComboBoxItem>) qcGridPane.getChildren().get(totalIndex + 1);
            dnaQc.getSelectionModel().select((qcData.getDnaQC() != null && "F".equals(qcData.getDnaQC())) ?
            1 : 0);

            ComboBox<ComboBoxItem> libraryQc = (ComboBox<ComboBoxItem>) qcGridPane.getChildren().get(totalIndex + 2);
            libraryQc.getSelectionModel().select((qcData.getLibraryQC() != null && "F".equals(qcData.getLibraryQC())) ?
                    1 : 0);

            ComboBox<ComboBoxItem> seqClusterDensity = (ComboBox<ComboBoxItem>) qcGridPane.getChildren().get(totalIndex + 3);
            seqClusterDensity.getSelectionModel().select((qcData.getSeqClusterDensity() != null && "F".equals(qcData.getSeqClusterDensity())) ?
                    1 : 0);

            ComboBox<ComboBoxItem> seqQ30 = (ComboBox<ComboBoxItem>) qcGridPane.getChildren().get(totalIndex + 4);
            seqQ30.getSelectionModel().select((qcData.getSeqQ30() != null && "F".equals(qcData.getSeqQ30())) ?
                    1 : 0);

            ComboBox<ComboBoxItem> seqClusterPF = (ComboBox<ComboBoxItem>) qcGridPane.getChildren().get(totalIndex + 5);
            seqClusterPF.getSelectionModel().select((qcData.getSeqClusterPF() != null && "F".equals(qcData.getSeqClusterPF())) ?
                    1 : 0);

            ComboBox<ComboBoxItem> seqIndexingPFCV = (ComboBox<ComboBoxItem>) qcGridPane.getChildren().get(totalIndex + 6);
            seqIndexingPFCV.getSelectionModel().select((qcData.getSeqIndexingPFCV() != null && "F".equals(qcData.getSeqIndexingPFCV())) ?
                    1 : 0);

            totalIndex += 7;
            rowIndex++;
        }
    }

    public void qcSetting(ComboBox<ComboBoxItem> qc) {
        qc.setConverter(new ComboBoxConverter());
        qc.getItems().add(new ComboBoxItem("P", "P"));
        qc.getItems().add(new ComboBoxItem("F", "F"));
        qc.getSelectionModel().selectFirst();
    }

    public void saveQCData() {
        if(sampleArrayList == null) sampleArrayList = new ArrayList<>();

        for (int i = 0; i < qcGridPane.getChildren().size(); i += 7) {
            boolean isNewItem = false;
            TextField sampleNameTextField = (TextField) qcGridPane.getChildren().get(i);
            String sampleName = sampleNameTextField.getText();

            if(sampleName.isEmpty()) continue;

            int indexNumber = i / 7;

            Sample sample = null;
            if(indexNumber > sampleArrayList.size() - 1) {
                isNewItem = true;
                sample = new Sample();
                SampleSheet sampleSheet = new SampleSheet();
                sample.setSampleSheet(sampleSheet);
                sampleArrayList.add(sample);
            } else {
                sample = sampleArrayList.get(indexNumber);
            }

            QcData qcData = sample.getQcData();

            if(qcData == null) {
                qcData = new QcData();
                sample.setQcData(qcData);
            }

            if(isNewItem || !sample.getSampleSheet().getSampleName().isEmpty()) {
                sample.getSampleSheet().setSampleName(sampleName);
            } else {
                sample.getSampleSheet().setSampleId(sampleName);
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
