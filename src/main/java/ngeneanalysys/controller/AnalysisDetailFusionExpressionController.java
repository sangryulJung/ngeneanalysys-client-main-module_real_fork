package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.GeneExpression;
import ngeneanalysys.service.APIService;
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

    /** API 서버 통신 서비스 */
    private APIService apiService;

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

        apiService = APIService.getInstance();

        geneIdTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGeneId()));
        geneNameTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGeneName()));
        chrTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getChromosome()));
        strandTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getStrand()));
        startPositionTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getStartPosition()));
        endPositionTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getEndPosition()));
        coverageTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getCoverage()));
        fpkmTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getFpkm()));
        tpmTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getTpm()));

        analysisDetailFusionMainController.subTabFusionExpression.setContent(root);
    }
}
