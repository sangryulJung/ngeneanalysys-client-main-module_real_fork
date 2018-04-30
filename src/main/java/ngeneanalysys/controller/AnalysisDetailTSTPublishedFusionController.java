package ngeneanalysys.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.PublishedFusion;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.paged.PagedPublishedFusion;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-04-30
 */
public class AnalysisDetailTSTPublishedFusionController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private TableView<PublishedFusion> publishedFusionTableView;

    @FXML
    private TableColumn<PublishedFusion, String> fusionGeneTableColumn;

    @FXML
    private TableColumn<PublishedFusion, String> mitelmanIdsTableColumn;

    @FXML
    private TableColumn<PublishedFusion, String> observedTableColumn;

    @FXML
    private TableColumn<PublishedFusion, String> breakpoint1TableColumn;

    @FXML
    private TableColumn<PublishedFusion, String> breakpoint2TableColumn;

    @FXML
    private TableColumn<PublishedFusion, String> fusionSupportingReadsTableColumn;

    @FXML
    private TableColumn<PublishedFusion, String> gene1ReferenceReadsTableColumn;

    @FXML
    private TableColumn<PublishedFusion, String> gene2ReferenceReadsTableColumn;

    @FXML
    private TableColumn<PublishedFusion, String> scoreTableColumn;

    @FXML
    private TableColumn<PublishedFusion, String> filterTableColumn;

    private APIService apiService;

    private Sample sample;

    private AnalysisDetailVariantsController variantsController;

    /**
     * @param variantsController AnalysisDetailVariantsController
     */
    public void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("published fusion view");

        this.apiService = APIService.getInstance();

        this.sample = (Sample)paramMap.get("sample");

        fusionGeneTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFusionGene()));
        mitelmanIdsTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMitelmanIds()));
        observedTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getObserved()));
        breakpoint1TableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBreakpoint1()));
        breakpoint2TableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBreakpoint2()));
        fusionSupportingReadsTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFusionSupportingReads()));
        gene1ReferenceReadsTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGene1ReferenceReads()));
        gene2ReferenceReadsTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGene2ReferenceReads()));
        scoreTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getScore()));
        filterTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFilter()));

        try {
            HttpClientResponse response = apiService
                    .get("TST170RnaResults/samples/" + sample.getId() + "/publishedFusions", null, null, null);

            PagedPublishedFusion publishedFusion = response.getObjectBeforeConvertResponseToJSON(PagedPublishedFusion.class);

            publishedFusionTableView.setItems(FXCollections.observableArrayList(publishedFusion.getResult()));

        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
        variantsController.getDetailContents().setCenter(root);

    }
}
