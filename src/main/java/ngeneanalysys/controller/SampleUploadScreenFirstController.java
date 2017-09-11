package ngeneanalysys.controller;

import com.opencsv.CSVReader;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.SampleSheet;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jang
 * @since 2017-08-10
 */
public class SampleUploadScreenFirstController extends BaseStageController{
    private static Logger logger = LoggerUtil.getLogger();

    /** 메인 화면 컨트롤러 객체 */
    private MainController mainController;

    private SampleUploadController sampleUploadController;

    /** 분석자 진행현황 화면 컨트롤러 객체 */
    private HomeController homeController;

    @FXML
    private TextField textFieldRunName;

    @FXML
    private TableView<SampleSheet> tableViewSampleSheetForm;

    @FXML
    private Button buttonNext;

    @FXML
    private Button buttonCancel;

    private List<Sample> sampleArrayList = null;

    @FXML
    private GridPane sampleSheetGridPane;


    /**
     * @param homeController
     */
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * @param sampleUploadController
     */
    public void setSampleUploadController(SampleUploadController sampleUploadController) {
        this.sampleUploadController = sampleUploadController;
        /*if(sampleUploadController.getSamples() != null) {
            sampleArrayList = sampleUploadController.getSamples();
            tableEdit();
        }*/
    }

    /**
     * @param sampleArrayList
     */
    public void setSampleArrayList(List<Sample> sampleArrayList) {
        this.sampleArrayList = sampleArrayList;
        tableEdit();
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
        sampleSheetGridPane.getChildren().clear();
        sampleSheetGridPane.setPrefHeight(0);

        for(int row  = 0 ; row < 23 ; row++) {
            sampleSheetGridPane.setPrefHeight(sampleSheetGridPane.getPrefHeight() + 26);

            TextField sampleName = new TextField();
            sampleName.setStyle("-fx-text-inner-color: black;");
            sampleName.getStyleClass().add("font_size_9");

            TextField samplePlate = new TextField();
            samplePlate.setStyle("-fx-text-inner-color: black;");

            TextField sampleWell = new TextField();
            sampleWell.setStyle("-fx-text-inner-color: black;");

            TextField i7IndexId = new TextField();
            i7IndexId.setStyle("-fx-text-inner-color: black;");

            TextField index = new TextField();
            index.setStyle("-fx-text-inner-color: black;");

            TextField sampleProject = new TextField();
            sampleProject.setStyle("-fx-text-inner-color: black;");

            TextField description = new TextField();
            description.setStyle("-fx-text-inner-color: black;");

            sampleSheetGridPane.addRow(row, sampleName, i7IndexId, index, samplePlate, sampleWell, sampleProject, description);

        }
        if(sampleUploadController.getSamples() != null) {
            sampleArrayList = sampleUploadController.getSamples();
            tableEdit();
        }
    }

    /**
     * [Next] 버튼 활성/비활성 토글
     */
    public void toggleNextBtnActivation() {
        boolean isActivationNextBtn = true;
        if (sampleArrayList == null || sampleArrayList.size() < 1) {
            isActivationNextBtn = false;
        }
        buttonNext.setDisable(!isActivationNextBtn);
    }

    @FXML
    public void showFileFindWindow() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose SampleSheet File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("csv", "*.csv"));
        File file = fileChooser.showOpenDialog(sampleUploadController.getCurrentStage());

        if(file != null && file.getName().equalsIgnoreCase("samplesheet.csv")) {

            try(CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(file)))) {
                String[] s;
                boolean tableData = false;
                List<Sample> list = new ArrayList<>();
                while((s = csvReader.readNext()) != null) {
                    if (tableData && list.size() < 23) {
                        Sample sample = new Sample();
                        SampleSheet sampleSheet = new SampleSheet(s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7]);
                        sample.setSampleSheet(sampleSheet);
                        list.add(sample);
                    } else if(s[0].equalsIgnoreCase("Sample_ID")) {
                        tableData = true;
                    }
                }

                sampleArrayList = list;
                tableEdit();

            } catch (IOException e) {

            }

        }

    }

    @FXML
    public void next() throws IOException{
        saveSampleSheetData();
        sampleUploadController.pageSetting(2);

    }

    public void saveSampleSheetData() {
        if(sampleArrayList == null) sampleArrayList = new ArrayList<>();

        for(int i = 0; i < sampleSheetGridPane.getChildren().size() ; i += 7) {

            TextField sampleTextField = (TextField) sampleSheetGridPane.getChildren().get(i);
            String sampleName = sampleTextField.getText();
            if(!StringUtils.isEmpty(sampleName)) {
                //파일에는 없으나 추가하는 정보이거나 파일 없이 직접 입력한 정보
                int rowIndex = i / 7;
                SampleSheet sampleSheet = null;
                boolean newItem = false;
                if(sampleArrayList.size() < rowIndex + 1) {
                    Sample sample = new Sample();
                    sampleSheet = new SampleSheet();
                    sample.setSampleSheet(sampleSheet);
                    sampleArrayList.add(sample);
                    newItem = true;
                } else {
                    //파일에서 읽어온 정보를 갱신할 때
                    Sample sample = sampleArrayList.get(rowIndex);
                    sampleSheet = sample.getSampleSheet();
                }
                //샘플시트 수정, 추가 사항 ArrayList에 저장
                if(!StringUtils.isEmpty(sampleSheet.getSampleName()) || newItem) {
                    sampleSheet.setSampleName(sampleName);
                } else {
                    sampleSheet.setSampleId(sampleName);
                }

                TextField i7IndexIdTextField = (TextField) sampleSheetGridPane.getChildren().get(i + 1);
                sampleSheet.setI7IndexId(i7IndexIdTextField.getText());

                TextField indexTextField = (TextField) sampleSheetGridPane.getChildren().get(i + 2);
                sampleSheet.setSampleIndex(indexTextField.getText());

                TextField samplePlateTextField = (TextField) sampleSheetGridPane.getChildren().get(i + 3);
                sampleSheet.setSamplePlate(samplePlateTextField.getText());

                TextField sampleWellTextField = (TextField) sampleSheetGridPane.getChildren().get(i + 4);
                sampleSheet.setSampleWell(sampleWellTextField.getText());

                TextField sampleProjectTextField = (TextField) sampleSheetGridPane.getChildren().get(i + 5);
                sampleSheet.setSampleProject(sampleProjectTextField.getText());

                TextField descriptionTextField = (TextField) sampleSheetGridPane.getChildren().get(i + 6);
                sampleSheet.setDescription(descriptionTextField.getText());

            }
        }

        sampleUploadController.setSamples(sampleArrayList);
    }

    @FXML
    public void closeDialog() {
        if(sampleUploadController != null) sampleUploadController.closeDialog();
    }

    //선택된 sampleSheet 내용 화면 출력
    public void tableEdit() {

        int rowIndex = 0;
        int totalIndex = 0;
        for(Sample sample : sampleArrayList) {
            //입력가능한 샘플의 총 양은 23개
            if(22 < rowIndex) break;

            SampleSheet item = sample.getSampleSheet();
            TextField sampleName = (TextField) sampleSheetGridPane.getChildren().get(totalIndex);
            sampleName.setText(!StringUtils.isEmpty(item.getSampleName()) ?  item.getSampleName() : item.getSampleId());

            TextField i7IndexId = (TextField) sampleSheetGridPane.getChildren().get(totalIndex + 1);
            i7IndexId.setText(item.getI7IndexId());

            TextField index = (TextField) sampleSheetGridPane.getChildren().get(totalIndex + 2);
            index.setText(item.getSampleIndex());

            TextField samplePlate = (TextField) sampleSheetGridPane.getChildren().get(totalIndex + 3);
            samplePlate.setText(item.getSamplePlate());

            TextField sampleWell = (TextField) sampleSheetGridPane.getChildren().get(totalIndex + 4);
            sampleWell.setText(item.getSampleWell());

            TextField sampleProject = (TextField) sampleSheetGridPane.getChildren().get(totalIndex + 5);
            sampleProject.setText(item.getSampleProject());

            TextField description = (TextField) sampleSheetGridPane.getChildren().get(totalIndex + 6);
            description.setText(item.getDescription());

            totalIndex += 7;
            rowIndex++;
        }

        /*sampleSheetGridPane.getChildren().clear();
        sampleSheetGridPane.setPrefHeight(0);

        int row = 0;

        for(Sample sample : sampleArrayList) {
            SampleSheet item = sample.getSampleSheet();
            sampleSheetGridPane.setPrefHeight(sampleSheetGridPane.getPrefHeight() + 26);

            TextField sampleName = new TextField();
            sampleName.setStyle("-fx-text-inner-color: black;");
            sampleName.getStyleClass().add("font_size_9");
            sampleName.setText(!StringUtils.isEmpty(item.getSampleName()) ?  item.getSampleName() : item.getSampleId());

            TextField samplePlate = new TextField();
            samplePlate.setStyle("-fx-text-inner-color: black;");
            samplePlate.setText(item.getSamplePlate());

            TextField sampleWell = new TextField();
            sampleWell.setStyle("-fx-text-inner-color: black;");
            sampleWell.setText(item.getSampleWell());

            TextField i7IndexId = new TextField();
            i7IndexId.setStyle("-fx-text-inner-color: black;");
            i7IndexId.setText(item.getI7IndexId());

            TextField index = new TextField();
            index.setStyle("-fx-text-inner-color: black;");
            index.setText(item.getSampleIndex());

            TextField sampleProject = new TextField();
            sampleProject.setStyle("-fx-text-inner-color: black;");
            sampleProject.setText(item.getSampleProject());

            TextField description = new TextField();
            description.setStyle("-fx-text-inner-color: black;");
            description.setText(item.getDescription());

            sampleSheetGridPane.addRow(row, sampleName, i7IndexId, index, samplePlate, sampleWell, sampleProject, description);

            row++;
        }*/
    }

}
