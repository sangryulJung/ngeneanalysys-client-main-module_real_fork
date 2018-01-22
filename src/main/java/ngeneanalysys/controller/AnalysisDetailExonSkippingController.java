package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.ExonSkip;
import ngeneanalysys.model.paged.PagedExonSkip;
import ngeneanalysys.model.Sample;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Jang
 * @since 2018-01-12
 */
public class AnalysisDetailExonSkippingController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    private Sample sample;

    private AnalysisDetailFusionMainController analysisDetailFusionMainController;

    @FXML
    private TableView<ExonSkip> exonSkipTableView;

    @FXML
    private TableColumn<ExonSkip, Integer> geneIdTableColumn;

    @FXML
    private TableColumn<ExonSkip, Integer> exonNumberTableColumn;

    @FXML
    private TableColumn<ExonSkip, Integer> totalReadCountTableColumn;

    @FXML
    private TableColumn<ExonSkip, Integer> skippedReadCountTableColumn;

    @FXML
    private TableColumn<ExonSkip, BigDecimal> skippedRateTableColumn;

    /**
     * @param analysisDetailFusionMainController
     */
    public void setAnalysisDetailFusionMainController(AnalysisDetailFusionMainController analysisDetailFusionMainController) {
        this.analysisDetailFusionMainController = analysisDetailFusionMainController;
    }

    public void setList() {
        try {
            HttpClientResponse response = apiService.get("analysisResults/sampleExonSkip/" + sample.getId(), null, null, false);

            PagedExonSkip pagedExonSkip = response.getObjectBeforeConvertResponseToJSON(PagedExonSkip.class);

            List<ExonSkip> exonSkipList = pagedExonSkip.getResult();
            ObservableList<ExonSkip> displayList = null;

            if(!exonSkipList.isEmpty()) {
                displayList = FXCollections.observableArrayList(exonSkipList);
                logger.info(displayList.size() + "");
            }

            if (exonSkipTableView.getItems() != null && !exonSkipTableView.getItems().isEmpty()) {
                exonSkipTableView.getItems().clear();
            }
            exonSkipTableView.setItems(displayList);

        } catch (WebAPIException wae) {

        }
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show Fusion");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        sample = (Sample)paramMap.get("sample");

        setList();

        geneIdTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSampleId()));
        exonNumberTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getExonNumber()));
        totalReadCountTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getTotalReadCount()));
        skippedReadCountTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSkippedReadCount()));
        skippedRateTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSkippedRate()));

        analysisDetailFusionMainController.subTabExonSkipping.setContent(root);
    }
}
