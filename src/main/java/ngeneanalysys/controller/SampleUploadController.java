package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.model.Sample;
import ngeneanalysys.util.FXMLLoadUtil;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * @author Jang
 * @since 2017-08-17
 */
public class SampleUploadController extends BaseStageController{
    private static Logger logger = LoggerUtil.getLogger();

    /** 메인 화면 컨트롤러 객체 */
    private MainController mainController;

    /** 분석자 진행현황 화면 컨트롤러 객체 */
    private ExperimenterHomeController experimentHomeController;

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    private List<Sample>  samples;

    @FXML
    private Pane tableRegion;

    private SampleUploadScreenFirstController sampleUploadScreenFirstController;

    private SampleUploadScreenSecondController sampleUploadScreenSecondController;

    private SampleUploadScreenThirdController sampleUploadScreenThirdController;

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

        // Scene Init
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.showAndWait();

    }

    public void pageSetting(int scene) throws IOException {
        tableRegion.getChildren().clear();
        FXMLLoader loader = null;
        VBox box = null;

        switch (scene) {
            case 1 :
                loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_FIRST);
                box = loader.load();
                sampleUploadScreenFirstController = loader.getController();
                sampleUploadScreenFirstController.setMainController(mainController);
                sampleUploadScreenFirstController.setSampleUploadController(this);

                tableRegion.getChildren().add(box);
                break;
            case 2 :
                loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_SECOND);
                box = loader.load();

                sampleUploadScreenSecondController = loader.getController();
                sampleUploadScreenSecondController.setMainController(mainController);
                sampleUploadScreenSecondController.setSampleUploadController(this);

                tableRegion.getChildren().add(box);
                break;
            case 3 :
                loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_THIRD);
                box = loader.load();
                sampleUploadScreenThirdController = loader.getController();
                sampleUploadScreenThirdController.setMainController(mainController);
                sampleUploadScreenThirdController.setSampleUploadController(this);

                tableRegion.getChildren().add(box);
                break;
            default:
                break;
        }



    }

    public void closeDialog() { currentStage.close(); }
}
