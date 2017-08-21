package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.SampleSheet;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

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

    /**
     * @param mainController
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setMainApp(this.mainController.getMainApp());
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

    @Override
    public void show(Parent root) throws IOException {

    }

    public void tableEdit() {
        standardDataGridPane.getChildren().clear();
        standardDataGridPane.setPrefHeight(0);

        int row = 0;

        for(Sample sample : sampleArrayList) {
            SampleSheet item = sample.getSampleSheet();
            standardDataGridPane.setPrefHeight(standardDataGridPane.getPrefHeight() + 26);

            TextField sampleName = new TextField();
            sampleName.setStyle("-fx-text-inner-color: black;");
            sampleName.setText(!StringUtils.isEmpty(item.getSampleName()) ? item.getSampleName() : item.getSampleId());

            TextField select = new TextField();
            select.setStyle("-fx-text-inner-color: black;");

            TextField paitentid = new TextField();
            paitentid.setStyle("-fx-text-inner-color: black;");

            TextField disease = new TextField();
            disease.setStyle("-fx-text-inner-color: black;");

            TextField panel = new TextField();
            panel.setStyle("-fx-text-inner-color: black;");

            TextField source = new TextField();
            source.setStyle("-fx-text-inner-color: black;");

            standardDataGridPane.addRow(row, sampleName, select, paitentid, disease, panel, source);

            row++;
        }

    }

    @FXML
    public void submit() {
        if(sampleArrayList != null) {
            for (int i = 0; i < standardDataGridPane.getChildren().size(); i+=6) {
                int rowNum = i / 6;
                Sample sample = sampleArrayList.get(rowNum);

                //TextField sampleName = (TextField) standardDataGridPane.getChildren().get(i);

                
            }
        }

    }

    @FXML
    public void back() throws IOException { sampleUploadController.pageSetting(2); }

    @FXML
    public void closeDialog() { sampleUploadController.closeDialog(); }
}
