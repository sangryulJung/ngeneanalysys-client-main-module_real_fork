package ngeneanalysys.controller;

import javafx.scene.Parent;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class AnalysisDetailFusionExpressionController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private AnalysisDetailFusionMainController analysisDetailFusionMainController;

    /**
     * @param analysisDetailFusionMainController
     */
    public void setAnalysisDetailFusionMainController(AnalysisDetailFusionMainController analysisDetailFusionMainController) {
        this.analysisDetailFusionMainController = analysisDetailFusionMainController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("fusion expression init");

        analysisDetailFusionMainController.subTabFusionExpression.setContent(root);
    }
}
