package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-11-27
 */
public class AnalysisDetailOverviewHeredAmcController extends AnalysisDetailCommonController {

    @FXML
    private GridPane mainGridPane;

    private AnalysisDetailGermlineAmcCNVReportController controller;

    void setContents() {
        controller.setCompositeCmtCnvResults();
    }

    @Override
    public void show(Parent root) throws IOException {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_HERED_AMC_CNV_REPORT);
            Node node = loader.load();
            AnalysisDetailGermlineAmcCNVReportController controller = loader.getController();
            this.controller = controller;
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.show((Parent) node);
            mainGridPane.add(node, 0, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
