package ngeneanalysys.controller;

import javafx.scene.Parent;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-08-31
 */
public class AnalysisDetailSNPsINDELsMemoController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private AnalysisDetailSNPsINDELsController analysisDetailSNPsINDELsController;

    /**
     * @return the analysisDetailSNPsINDELsController
     */
    public AnalysisDetailSNPsINDELsController getAnalysisDetailSNPsINDELsController() {
        return analysisDetailSNPsINDELsController;
    }
    /**
     * @param analysisDetailSNPsINDELsController the analysisDetailSNPsINDELsController to set
     */
    public void setAnalysisDetailSNPsINDELsController(
            AnalysisDetailSNPsINDELsController analysisDetailSNPsINDELsController) {
        this.analysisDetailSNPsINDELsController = analysisDetailSNPsINDELsController;
    }

    @Override
    public void show(Parent root) throws IOException {


        analysisDetailSNPsINDELsController.subTabMemo.setContent(root);
    }
}
