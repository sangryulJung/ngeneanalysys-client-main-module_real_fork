package ngeneanalysys.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.SpliceVariant;
import ngeneanalysys.model.paged.PagedSpliceVariant;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-04-27
 */
public class AnalysisDetailTSTSpliceVariantController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private TableView<SpliceVariant> spliceVariantTableView;

    @FXML
    private TableColumn<SpliceVariant, String> chrTableColumn;

    @FXML
    private TableColumn<SpliceVariant, Integer> startPositionTableColumn;

    @FXML
    private TableColumn<SpliceVariant, Integer> endPositionTableColumn;

    @FXML
    private TableColumn<SpliceVariant, String> refTableColumn;

    @FXML
    private TableColumn<SpliceVariant, String> altTableColumn;

    @FXML
    private TableColumn<SpliceVariant, Double> qualityTableColumn;

    @FXML
    private TableColumn<SpliceVariant, String> filterTableColumn;

    @FXML
    private TableColumn<SpliceVariant, Integer> altDedupTableColumn;

    @FXML
    private TableColumn<SpliceVariant, String> svTypeTableColumn;

    @FXML
    private TableColumn<SpliceVariant, Integer> altDupTableColumn;

    @FXML
    private TableColumn<SpliceVariant, Integer> refDedupTableColumn;

    @FXML
    private TableColumn<SpliceVariant, Integer> refDupTableColumn;

    @FXML
    private TableColumn<SpliceVariant, String> intergenicTableColumn;

    @FXML
    private TableColumn<SpliceVariant, String> antTableColumn;


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

        chrTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChromosome()));
        startPositionTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStartPosition()));
        endPositionTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEndPosition()));
        refTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRef()));
        altTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlt()));
        qualityTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuality()));
        filterTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFilter()));
        altDedupTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAltDedup()));
        svTypeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSvType()));
        altDupTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAltDup()));
        refDedupTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRefDedup()));
        refDupTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRefDup()));
        intergenicTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIntergenic()));
        antTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAnt()));

        apiService = APIService.getInstance();
        this.sample = (Sample)paramMap.get("sample");
        try {
            HttpClientResponse response = apiService
                    .get("TST170RnaResults/samples/" + sample.getId() + "/spliceVariants", null, null, null);

            PagedSpliceVariant spliceVariant = response.getObjectBeforeConvertResponseToJSON(PagedSpliceVariant.class);

            spliceVariantTableView.setItems(FXCollections.observableArrayList(spliceVariant.getResult()));

        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
        variantsController.getDetailContents().setCenter(root);
    }
}
