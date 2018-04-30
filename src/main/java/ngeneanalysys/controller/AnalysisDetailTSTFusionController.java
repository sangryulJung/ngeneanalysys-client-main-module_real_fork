package ngeneanalysys.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.TSTFusion;
import ngeneanalysys.model.paged.PagedTSTFusion;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-04-27
 */
public class AnalysisDetailTSTFusionController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private TableView<TSTFusion> fusionTableView;

    @FXML
    private TableColumn<TSTFusion, String> callerTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> geneATableColumn;

    @FXML
    private TableColumn<TSTFusion, String> geneBTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> geneABreakpointTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> geneBBreakpointTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> scoreTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> filterTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> preciseImpreciseTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> intragenicCallTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> refASplitTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> refAPairTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> refBSplitTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> refBPairTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> altSplitTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> altPairTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> candidateAltTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> contigTableColumn;

    @FXML
    private TableColumn<TSTFusion, String> contigAlign1TableColumn;

    @FXML
    private TableColumn<TSTFusion, String> contigAlign2TableColumn;

    @FXML
    private TableColumn<TSTFusion, String> keepFusionTableColumn;

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
        logger.debug("tst Fusion view");

        callerTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCaller()));
        geneATableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGeneA()));
        geneBTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGeneB()));
        geneABreakpointTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGeneABreakpoint()));
        geneBBreakpointTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGeneBBreakpoint()));
        scoreTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getScore()));
        filterTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFilter()));
        preciseImpreciseTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPreciseImprecise()));
        intragenicCallTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIntragenicCall()));
        refASplitTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRefASplit()));
        refAPairTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRefAPair()));
        refBSplitTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRefBSplit()));
        refBPairTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRefBPair()));
        altSplitTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAltSplit()));
        altPairTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAltPair()));
        candidateAltTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCandidateAlt()));
        contigTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContig()));
        contigAlign1TableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContigAlign1()));
        contigAlign2TableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContigAlign2()));
        keepFusionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKeepFusion()));


        apiService = APIService.getInstance();
        this.sample = (Sample)paramMap.get("sample");
        try {
            HttpClientResponse response = apiService
                    .get("TST170RnaResults/samples/" + sample.getId() + "/fusions", null, null, null);

            PagedTSTFusion fusion = response.getObjectBeforeConvertResponseToJSON(PagedTSTFusion.class);

            fusionTableView.setItems(FXCollections.observableArrayList(fusion.getResult()));


        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
        variantsController.getDetailContents().setCenter(root);
    }
}
