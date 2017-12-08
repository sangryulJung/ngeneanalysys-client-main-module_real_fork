package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.GeneExpression;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class AnalysisDetailFusionExpressionController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private AnalysisDetailFusionMainController analysisDetailFusionMainController;

    @FXML
    private TableView<GeneExpression> GeneExpressionTableView;

    @FXML
    private TableColumn<GeneExpression, String> geneIdTableColumn;

    @FXML
    private TableColumn<GeneExpression, String> geneNameTableColumn;

    @FXML
    private TableColumn<GeneExpression, String> chrTableColumn;

    @FXML
    private TableColumn<GeneExpression, String> strandTableColumn;

    @FXML
    private TableColumn<GeneExpression, Integer> startPositionTableColumn;

    @FXML
    private TableColumn<GeneExpression, Integer> endPositionTableColumn;

    @FXML
    private TableColumn<GeneExpression, BigDecimal> coverageTableColumn;

    @FXML
    private TableColumn<GeneExpression, BigDecimal> fpkmTableColumn;

    @FXML
    private TableColumn<GeneExpression, BigDecimal> tpmTableColumn;


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
