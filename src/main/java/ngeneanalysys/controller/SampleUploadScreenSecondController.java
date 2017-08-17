package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.model.SampleSheet;
import ngeneanalysys.util.FXMLLoadUtil;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * @author Jang
 * @since 2017-08-10
 */
public class SampleUploadScreenSecondController extends BaseStageController {
    private static Logger logger = LoggerUtil.getLogger();

    /** 메인 화면 컨트롤러 객체 */
    private MainController mainController;

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    private List<SampleSheet> sampleSheetArrayList;

    @FXML
    private GridPane qcGridPane;

    /**
     * @param sampleSheetArrayList
     */
    public void setSampleSheetArrayList(List<SampleSheet> sampleSheetArrayList) {
        this.sampleSheetArrayList = sampleSheetArrayList;
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

        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.showAndWait();
    }

    @FXML
    public void closeDialog() { currentStage.close(); }

    @FXML
    public void back() throws IOException {
        if(currentStage != null) {
            currentStage.close();
        }

        FXMLLoader loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_FIRST);
        VBox box = loader.load();
        SampleUploadScreenFirstController controller = loader.getController();
        controller.setMainController(mainController);
        controller.setSampleSheetArrayList(sampleSheetArrayList);
        controller.show((Parent) box);
    }

    @FXML
    public void next() {

    }


}
