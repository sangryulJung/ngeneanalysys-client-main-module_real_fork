package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.model.TSTCNV;
import ngeneanalysys.model.paged.PagedTSTCNV;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-04-27
 */
public class AnalysisDetailTSTCNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private TableView<TSTCNV> cnvTableView;

    @FXML
    private TableColumn<TSTCNV, String> chrTableColumn;

    @FXML
    private TableColumn<TSTCNV, Integer> startPositionTableColumn;

    @FXML
    private TableColumn<TSTCNV, Integer> endPositionTableColumn;

    @FXML
    private TableColumn<TSTCNV, String> refTableColumn;

    @FXML
    private TableColumn<TSTCNV, String> altTableColumn;

    @FXML
    private TableColumn<TSTCNV, String> svTypeTableColumn;

    @FXML
    private TableColumn<TSTCNV, String> geneTableColumn;

    @FXML
    private TableColumn<TSTCNV, Double> foldChangeTableColumn;


    private APIService apiService;

    private SampleView sample;

    private AnalysisDetailVariantsController variantsController;

    /**
     * @param variantsController AnalysisDetailVariantsController
     */
    public void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show cnv");

        chrTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChromosome()));
        startPositionTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStartPosition()));
        endPositionTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEndPosition()));
        refTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRef()));
        altTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlt()));
        svTypeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSvType()));
        geneTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGene()));
        foldChangeTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFoldChange()));

        apiService = APIService.getInstance();
        this.sample = (SampleView)paramMap.get("sampleView");

        showCnv();

        variantsController.getDetailContents().setCenter(root);
    }

    public void showCnv() {
        if(cnvTableView.getItems() != null && !cnvTableView.getItems().isEmpty()) {
            cnvTableView.getItems().removeAll(cnvTableView.getItems());
            cnvTableView.refresh();
        }

        try {
            HttpClientResponse response = apiService
                    .get("TST170DnaResults/samples/" + sample.getId() + "/cnvs", null, null, null);

            PagedTSTCNV pagedTSTCNV = response.getObjectBeforeConvertResponseToJSON(PagedTSTCNV.class);

            cnvTableView.setItems(FXCollections.observableArrayList(pagedTSTCNV.getResult()));

        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
    }
}
