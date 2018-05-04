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
import ngeneanalysys.model.CNV;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.paged.PagedCNV;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-04-27
 */
public class AnalysisDetailCNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private TableView<CNV> cnvTableView;

    @FXML
    private TableColumn<CNV, String> chrTableColumn;

    @FXML
    private TableColumn<CNV, Integer> startPositionTableColumn;

    @FXML
    private TableColumn<CNV, Integer> endPositionTableColumn;

    @FXML
    private TableColumn<CNV, String> refTableColumn;

    @FXML
    private TableColumn<CNV, String> altTableColumn;

    @FXML
    private TableColumn<CNV, String> svTypeTableColumn;

    @FXML
    private TableColumn<CNV, String> geneTableColumn;

    @FXML
    private TableColumn<CNV, Double> foldChangeTableColumn;


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
        this.sample = (Sample)paramMap.get("sample");

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

            PagedCNV pagedCNV = response.getObjectBeforeConvertResponseToJSON(PagedCNV.class);

            cnvTableView.setItems(FXCollections.observableArrayList(pagedCNV.getResult()));

        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
    }
}
