package ngeneanalysys.controller;

import com.opencsv.CSVReader;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.model.Sample;
import ngeneanalysys.util.FXMLLoadUtil;
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
public class SampleUploadScreenFirst extends BaseStageController{
    private static Logger logger = LoggerUtil.getLogger();

    /** 메인 화면 컨트롤러 객체 */
    private MainController mainController;

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    /** 분석자 진행현황 화면 컨트롤러 객체 */
    private ExperimenterHomeController experimentHomeController;

    @FXML
    private TextField textFieldRunName;

    @FXML
    private TableView<Sample> tableViewSampleSheetForm;

    @FXML
    private Button buttonNext;

    @FXML
    private Button buttonCancel;

    @FXML
    private TableColumn<Sample, String> tableColumnSample;

    @FXML
    private TableColumn<Sample, String> tableColumnSamplePlate;

    @FXML
    private TableColumn<Sample, String> tableColumnSampleWell;

    @FXML
    private TableColumn<Sample, String> tableColumnI7IndexID;

    @FXML
    private TableColumn<Sample, String> tableColumnIndex;

    @FXML
    private TableColumn<Sample, String> tableColumnSampleProject;

    @FXML
    private TableColumn<Sample, String> tableColumnDescription;

    /** 분석 샘플 정보 목록 객체 */
    private ObservableList<Sample> sampleList;

    @FXML
    private GridPane sampleSheetGridPane;

    @FXML
    private ScrollPane sampleSheetScrollPane;


    /**
     * @param mainController
     *            the mainController to set
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        setMainApp(this.mainController.getMainApp());
    }

    /**
     * @param experimentHomeController
     *            the experimentHomeController to set
     */
    public void setExperimentHomeController(ExperimenterHomeController experimentHomeController) {
        this.experimentHomeController = experimentHomeController;
    }

    @Override
    public void show(Parent root) throws IOException {

        // Create the dialog Stage
        currentStage = new Stage();
        currentStage.initStyle(StageStyle.DECORATED);
        currentStage.initModality(Modality.APPLICATION_MODAL);
        currentStage.setTitle(CommonConstants.SYSTEM_NAME + " > New Analysis Request");
        // OS가 Window인 경우 아이콘 출력.
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            currentStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        currentStage.initOwner(getMainApp().getPrimaryStage());

       /* contentWrapper.getChildren().add(maskerPane);
        maskerPane.setPrefWidth(698);
        maskerPane.setPrefHeight(668);
        maskerPane.setVisible(false);*/

/*
        tableColumnSample.setCellValueFactory(cellData -> {
            if(cellData.getValue().getSampleID() == null) {
                return new SimpleStringProperty(cellData.getValue().getSampleName());
            } else {
                return new SimpleStringProperty(cellData.getValue().getSampleID());
            }
        });
        tableColumnDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        tableColumnI7IndexID.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getI7IndexID()));
        tableColumnIndex.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIndex()));
        tableColumnSampleWell.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSampleWell()));
        tableColumnSampleProject.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSampleProject()));
        tableColumnSamplePlate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSamplePlate()));
*/

        sampleSheetScrollPane.setFitToHeight(true);

        // Schen Init
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.showAndWait();
    }

    /**
     * [Next] 버튼 활성/비활성 토글
     */
    public void toggleNextBtnActivation() {
        boolean isActivationNextBtn = true;
        if (sampleList == null || sampleList.size() < 1) {
            isActivationNextBtn = false;
        }
        buttonNext.setDisable(!isActivationNextBtn);
    }

    @FXML
    public void showFileFindWindow() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose FASTQ Sequence Files");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("csv", "*.csv"));
        File file = fileChooser.showOpenDialog(currentStage);

        if(file != null && file.getName().equalsIgnoreCase("samplesheet.csv")) {

            try(CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(file)))) {
                String[] s;
                boolean tableData = false;
                List<Sample> list = new ArrayList<>();
                while((s = csvReader.readNext()) != null) {
                    if (tableData) {
                        Sample sample = new Sample(s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7]);
                        list.add(sample);
                    } else if(s[0].equalsIgnoreCase("Sample_ID")) {
                        tableData = true;
                    }
                }

                sampleList = FXCollections.observableList(list);
                //refreshSampleListTableView();
                tableEdit();
                toggleNextBtnActivation();

            } catch (IOException e) {

            }




        }

    }

    public void convertSampleSheet(File sampleSheets) {

    }

    @FXML
    public void next() throws IOException{
        if(currentStage != null) {
            currentStage.close();
        }

        FXMLLoader loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_SECOND);
        VBox box = loader.load();
        SampleUploadScreenSecond controller = loader.getController();
        controller.setMainApp(mainApp);
        controller.show((Parent) box);

    }

    @FXML
    public void closeDialog() {
        currentStage.close();
    }

    /**
     * 샘플 목록 출력 새로고침
     */
    public void refreshSampleListTableView() {
        // 목록 데이터 새로 삽입
        tableViewSampleSheetForm.setItems(null);
        tableViewSampleSheetForm.setItems((ObservableList<Sample>) this.sampleList);
        // 목록 화면 갱신
        tableViewSampleSheetForm.getProperties().put(TableViewSkinBase.RECREATE, Boolean.TRUE);
    }

    //선택된 sampleSheet 내용 화면 출력
    public void tableEdit() {
        sampleSheetGridPane.getChildren().clear();

        int row = 0;

        for(Sample item : sampleList) {

            TextField sampleName = new TextField();
            sampleName.setStyle("-fx-text-inner-color: black;");
            sampleName.setText(!StringUtils.isEmpty(item.getSampleName()) ?  item.getSampleName() : item.getSampleID());

            TextField samplePlate = new TextField();
            samplePlate.setStyle("-fx-text-inner-color: black;");
            samplePlate.setText(item.getSamplePlate());

            TextField sampleWell = new TextField();
            sampleWell.setStyle("-fx-text-inner-color: black;");
            sampleWell.setText(item.getSampleWell());

            TextField i7IndexId = new TextField();
            i7IndexId.setStyle("-fx-text-inner-color: black;");
            i7IndexId.setText(item.getI7IndexID());

            TextField index = new TextField();
            index.setStyle("-fx-text-inner-color: black;");
            index.setText(item.getIndex());

            TextField sampleProject = new TextField();
            sampleProject.setStyle("-fx-text-inner-color: black;");
            sampleProject.setText(item.getSampleProject());

            TextField description = new TextField();
            description.setStyle("-fx-text-inner-color: black;");
            description.setText(item.getDescription());

            sampleSheetGridPane.addRow(row, sampleName, i7IndexId, index, samplePlate, sampleWell, sampleProject, description);

            row++;
        }
    }

}
