package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.SequencerCode;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.model.AnalysisFile;
import ngeneanalysys.model.paged.PagedSample;
import ngeneanalysys.model.Run;
import ngeneanalysys.model.Sample;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.FXMLLoadUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-08-17
 */
public class SampleUploadController extends BaseStageController{
    private static Logger logger = LoggerUtil.getLogger();

    private Run run = null;

    /** 메인 화면 컨트롤러 객체 */
    private MainController mainController;

    /** 분석자 진행현황 화면 컨트롤러 객체 */
    private HomeController homeController;

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    private List<Sample> samples = new ArrayList<>(23);

    @FXML
    private TextField textFieldRunName;

    public ToggleGroup getSequencerType() {
        return sequencerType;
    }
    @FXML
    private RadioButton sequencerMiSeqDXRadioButton;

    @FXML
    private RadioButton sequencerMiSeqRadioButton;

    @FXML
    private ToggleGroup sequencerType;

    @FXML
    private Pane tableRegion;

    @FXML
    private VBox contentWrapper;

    private SampleUploadScreenFirstController sampleUploadScreenFirstController;

    private SampleUploadScreenSecondController sampleUploadScreenSecondController;

    private SampleUploadScreenThirdController sampleUploadScreenThirdController;

    private Map<String, Map<String, Object>> fileMap = new HashMap<>();

    private List<File> uploadFileList = new ArrayList<>();

    private List<AnalysisFile> uploadFileData = new ArrayList<>();

    /**
     * @return run
     */
    public Run getRun() {
        return run;
    }

    /**
     * @param run
     */
    public void setRun(Run run) {
        this.run = run;
        sampleLoad();
    }

    /**
     * @return fileMap
     */
    public Map<String, Map<String, Object>> getFileMap() {
        return fileMap;
    }

    /**
     * @return uploadFileList
     */
    public List<File> getUploadFileList() {
        return uploadFileList;
    }

    /**
     * @return uploadFileData
     */
    public List<AnalysisFile> getUploadFileData() {
        return uploadFileData;
    }

    /**
     * @return homeController
     */
    public HomeController getHomeController() {
        return homeController;
    }

    /**
     * @param homeController
     */
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * @return currentStage
     */
    public Stage getCurrentStage() {
        return currentStage;
    }

    /**
     * @return samples
     */
    public List<Sample> getSamples() {
        return samples;
    }

    /**
     * @param samples
     */
    public void setSamples(List<Sample> samples) {
        this.samples = samples;
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
        logger.info("init upload Controller");
        // Create the dialog Stage
        currentStage = new Stage();
        currentStage.setResizable(false);
        currentStage.initStyle(StageStyle.DECORATED);
        currentStage.initModality(Modality.APPLICATION_MODAL);
        currentStage.setTitle(CommonConstants.SYSTEM_NAME + " > New Analysis Request");
        // OS가 Window인 경우 아이콘 출력.
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            currentStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        currentStage.initOwner(getMainApp().getPrimaryStage());
        /*currentStage.setMinHeight(510);
        currentStage.setMinWidth(900);
        currentStage.setMaxHeight(510);
        currentStage.setMaxWidth(900);*/
        pageSetting(1);

        textFieldRunName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
                textFieldRunName.setText(oldValue);
                /*Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("text error");
                alert.setContentText("");

                alert.showAndWait();*/
            }
        });

        contentWrapper.getChildren().add(maskerPane);
        maskerPane.setPrefHeight(contentWrapper.getPrefHeight());
        maskerPane.setPrefWidth(contentWrapper.getPrefWidth());
        maskerPane.setVisible(false);

        // Scene Init
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.showAndWait();

    }

    public void pageSetting(int scene) throws IOException {
        tableRegion.getChildren().clear();
        FXMLLoader loader = null;
        VBox box = null;
        sequencerType.setUserData(SequencerCode.MISEQ_DX.getDescription());
        sequencerMiSeqDXRadioButton.setOnAction(ev -> sequencerType.setUserData(SequencerCode.MISEQ_DX.getDescription()));

        sequencerMiSeqRadioButton.setOnAction(ev -> sequencerType.setUserData(SequencerCode.MISEQ.getDescription()));

        switch (scene) {
            case 1 :
                loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_FIRST);
                box = loader.load();
                sampleUploadScreenFirstController = loader.getController();
                sampleUploadScreenFirstController.setMainController(mainController);
                sampleUploadScreenFirstController.setSampleUploadController(this);
                sampleUploadScreenFirstController.show(box);
                tableRegion.getChildren().add(box);
                break;
            case 2 :
                loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_SECOND);
                box = loader.load();
                sampleUploadScreenSecondController = loader.getController();
                sampleUploadScreenSecondController.setMainController(mainController);
                sampleUploadScreenSecondController.setSampleUploadController(this);
                sampleUploadScreenSecondController.show(box);

                tableRegion.getChildren().add(box);
                break;
            case 3 :
                loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_THIRD);
                box = loader.load();
                sampleUploadScreenThirdController = loader.getController();
                sampleUploadScreenThirdController.setMainController(mainController);
                sampleUploadScreenThirdController.setSampleUploadController(this);
                sampleUploadScreenThirdController.show(box);
                tableRegion.getChildren().add(box);
                break;
            default:
                break;
        }

    }

    public String getRunName() {
        return textFieldRunName.getText();
    }

    public void closeDialog() { currentStage.close(); }

    public void sampleLoad() {
        HttpClientResponse response = null;
        Map<String, Object> params = new HashMap<>();
        try {
            APIService apiService = APIService.getInstance();
            params.clear();
            params.put("runId", run.getId());
            params.put("limit", 23);
            params.put("offset", 0);
            response = apiService.get("/samples", params, null, false);

            PagedSample pagedSample = response.getObjectBeforeConvertResponseToJSON(PagedSample.class);
            samples = pagedSample.getResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
