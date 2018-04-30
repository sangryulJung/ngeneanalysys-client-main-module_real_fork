package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.GeneExpression;
import ngeneanalysys.model.paged.PagedGeneExpression;
import ngeneanalysys.model.Sample;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class AnalysisDetailFusionExpressionController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    private Sample sample;

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
        logger.debug("fusion expression init");

        apiService = APIService.getInstance();

        sample = (Sample)paramMap.get("sample");

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

        showFusionExpressionList();
    }

    private void showFusionExpressionList() {
        HttpClientResponse response = null;
        try {
            response = apiService.get("analysisResults/sampleGeneExpression/" + sample.getId(), null, null, false);

            PagedGeneExpression fusionGene = response.getObjectBeforeConvertResponseToJSON(PagedGeneExpression.class);
            List<GeneExpression> list = fusionGene.getResult();
            ObservableList<GeneExpression> displayList = null;

            if(list != null && !list.isEmpty()) {
                displayList = FXCollections.observableArrayList(list);
                logger.debug(displayList.size() + "");
            }

            if (GeneExpressionTableView.getItems() != null && !GeneExpressionTableView.getItems().isEmpty()) {
                GeneExpressionTableView.getItems().clear();
            }
            GeneExpressionTableView.setItems(displayList);

        } catch (WebAPIException wae) {
            GeneExpressionTableView.setItems(null);
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            GeneExpressionTableView.setItems(null);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }
}
