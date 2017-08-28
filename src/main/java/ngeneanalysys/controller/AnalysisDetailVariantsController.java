package ngeneanalysys.controller;

import javafx.scene.Parent;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-08-28
 */
public class AnalysisDetailVariantsController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        this.mainController.getMainFrame().setCenter(root);
    }
}
