package ngeneanalysys.controller;

import com.opencsv.CSVReader;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.model.SampleSheet;
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
public class SampleUploadScreenFirstController extends BaseStageController{
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
    private TableView<SampleSheet> tableViewSampleSheetForm;

    @FXML
    private Button buttonNext;

    @FXML
    private Button buttonCancel;

    /** 분석 샘플 정보 목록 객체 */
    private ObservableList<SampleSheet> sampleSheetList;

    private List<SampleSheet> sampleSheetArrayList;

    @FXML
    private GridPane sampleSheetGridPane;

    @FXML
    private ScrollPane sampleSheetScrollPane;

    /**
     * @param sampleSheetArrayList
     */
    public void setSampleSheetArrayList(List<SampleSheet> sampleSheetArrayList) {
        this.sampleSheetArrayList = sampleSheetArrayList;
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

        sampleSheetScrollPane.setFitToHeight(true);

        // Schen Init
        //Scene scene = new Scene(root);
        //currentStage.setScene(scene);
        //currentStage.showAndWait();
    }

    /**
     * [Next] 버튼 활성/비활성 토글
     */
    public void toggleNextBtnActivation() {
        boolean isActivationNextBtn = true;
        if (sampleSheetList == null || sampleSheetList.size() < 1) {
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
                List<SampleSheet> list = new ArrayList<>();
                while((s = csvReader.readNext()) != null) {
                    if (tableData) {
                        SampleSheet sampleSheet = new SampleSheet(s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7]);
                        list.add(sampleSheet);
                    } else if(s[0].equalsIgnoreCase("Sample_ID")) {
                        tableData = true;
                    }
                }

                sampleSheetList = FXCollections.observableList(list);
                sampleSheetArrayList = list;
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
        /*if(currentStage != null) {
            currentStage.close();
        }

        FXMLLoader loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_SECOND);
        VBox box = loader.load();
        Scene scene = new Scene(box);
        currentStage.setScene(scene);
        SampleUploadScreenSecondController controller = loader.getController();
        controller.setMainController(mainController);
        controller.setSampleSheetArrayList(sampleSheetArrayList);
        controller.show((Parent) box);*/

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
        tableViewSampleSheetForm.setItems((ObservableList<SampleSheet>) this.sampleSheetList);
        // 목록 화면 갱신
        tableViewSampleSheetForm.getProperties().put(TableViewSkinBase.RECREATE, Boolean.TRUE);
    }

    //선택된 sampleSheet 내용 화면 출력
    public void tableEdit() {
        sampleSheetGridPane.getChildren().clear();
        sampleSheetGridPane.setPrefHeight(0);

        int row = 0;

        for(SampleSheet item : sampleSheetArrayList) {
            sampleSheetGridPane.setPrefHeight(sampleSheetGridPane.getPrefHeight() + 26);

            TextField sampleName = new TextField();
            sampleName.setStyle("-fx-text-inner-color: black;");
            sampleName.setText(!StringUtils.isEmpty(item.getSampleName()) ?  item.getSampleName() : item.getSampleId());
            sampleName.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            });

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
