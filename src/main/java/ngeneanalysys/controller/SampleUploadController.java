package ngeneanalysys.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    private VBox tableArea;

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

        pageSetting(1);

        // Scene Init
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.showAndWait();

    }

    public void pageSetting(int scene) throws IOException {
        

    }
}
