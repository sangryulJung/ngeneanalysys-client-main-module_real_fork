package ngeneanalysys.controller;

import javafx.scene.Parent;
import ngeneanalysys.controller.extend.SubPaneController;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class AnalysisDetailFusionGeneController extends SubPaneController {

    private AnalysisDetailFusionMainController analysisDetailFusionMainController;

    /**
     * @param analysisDetailFusionMainController
     */
    public void setAnalysisDetailFusionMainController(AnalysisDetailFusionMainController analysisDetailFusionMainController) {
        this.analysisDetailFusionMainController = analysisDetailFusionMainController;
    }

    @Override
    public void show(Parent root) throws IOException {


        analysisDetailFusionMainController.subTabFusionGene.setContent(root);
    }
}
