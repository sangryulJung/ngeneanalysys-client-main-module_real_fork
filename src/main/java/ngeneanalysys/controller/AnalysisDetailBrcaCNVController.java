package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-10-15
 */
public class AnalysisDetailBrcaCNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private HBox brca1ExonBox;

    @FXML
    private HBox brca2ExonBox;

    private APIService apiService;

    private AnalysisDetailVariantsController variantsController;

    /**
     * @param variantsController AnalysisDetailVariantsController
     */
    void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    @Override
    public void show(Parent root) throws IOException {

        apiService = APIService.getInstance();

        Panel panel = (Panel)paramMap.get("panel");
        SampleView sample = (SampleView)paramMap.get("sampleView");

        variantsController.getDetailContents().setCenter(root);
    }

    public void setList() {
        logger.debug("get CNV list");
    }
}
