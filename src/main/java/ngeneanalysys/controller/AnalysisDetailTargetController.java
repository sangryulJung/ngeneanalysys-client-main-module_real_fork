package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-08-28
 */
public class AnalysisDetailTargetController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

    }

    /**
     * SNP/Indels Dialog 창 출력
     */
    @FXML
    public void showSnpIndels() {
        try {
            // Load the fxml file and create a new stage for the popup dialog
            FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_LAYOUT);
            BorderPane page = loader.load();
            AnalysisDetailSNPsINDELsController controller = loader.getController();
            controller.setParamMap(getParamMap());
            controller.setMainController(this.mainController);

            controller.show(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fusion Dialog 창 출력
     */
    @FXML
    public void showFusion() {
        try {
            // Load the fxml file and create a new stage for the popup dialog
            FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_FUSION);
            BorderPane page = loader.load();
            AnalysisDetailFusionController controller = loader.getController();
            controller.setParamMap(getParamMap());
            controller.setMainController(this.mainController);

            controller.show(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
