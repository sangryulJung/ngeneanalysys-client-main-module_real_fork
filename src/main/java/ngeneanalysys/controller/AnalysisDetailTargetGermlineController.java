package ngeneanalysys.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.VariantCountByGene;
import ngeneanalysys.model.VariantCountByGeneForGermlineDNA;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-08-28
 */
public class AnalysisDetailTargetGermlineController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    @FXML
    private ComboBox<ComboBoxItem> virtualPanelComboBox;

    @FXML
    private ComboBox<ComboBoxItem> tierComboBox;

    @FXML
    private TextField geneListTextField;

    @FXML
    private TableView<VariantCountByGeneForGermlineDNA> geneTable;

    @FXML
    private TableColumn<VariantCountByGeneForGermlineDNA, String> geneColumn;

    @FXML
    private TableColumn<VariantCountByGeneForGermlineDNA, Integer> pathogenicSnvColumn;

    @FXML
    private TableColumn<VariantCountByGeneForGermlineDNA, Integer> pathogenicIndelsColumn;

    @FXML
    private TableColumn<VariantCountByGeneForGermlineDNA, Integer> likelyPathogenicSnvColumn;

    @FXML
    private TableColumn<VariantCountByGeneForGermlineDNA, Integer> likelyPathogenicIndelsColumn;

    @FXML
    private TableColumn<VariantCountByGeneForGermlineDNA, Integer> uncertainSignificanceSnvColumn;

    @FXML
    private TableColumn<VariantCountByGeneForGermlineDNA, Integer> uncertainSignificanceIndelsColumn;

    @FXML
    private TableColumn<VariantCountByGeneForGermlineDNA, Integer> likelyBenignSnvColumn;

    @FXML
    private TableColumn<VariantCountByGeneForGermlineDNA, Integer> likelyBenignIndelsColumn;

    @FXML
    private TableColumn<VariantCountByGeneForGermlineDNA, Integer> benignSnvColumn;

    @FXML
    private TableColumn<VariantCountByGeneForGermlineDNA, Integer> benignIndelsColumn;

    @FXML
    private Button fusionButton;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        fusionButton.setDisable(true);
        fusionButton.setVisible(false);

        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGeneSymbol()));
        pathogenicSnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPathogenicSnpCount()).asObject());
        pathogenicIndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPathogenicInDelCount()).asObject());
        likelyPathogenicSnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLikelyPathogenicSnpCount()).asObject());
        likelyPathogenicIndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLikelyPathogenicInDelCount()).asObject());
        uncertainSignificanceSnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUncertainSignificanceSnpCount()).asObject());
        uncertainSignificanceIndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUncertainSignificanceInDelCount()).asObject());
        likelyBenignSnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLikelyBenignSnpCount()).asObject());
        likelyBenignIndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLikelyBenignInDelCount()).asObject());
        benignSnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBenignSnpCount()).asObject());
        benignIndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBenignInDelCount()).asObject());

        showVariantCountByGeneData();

    }

    /**
     * SNP/Indels Dialog 창 출력
     */
    @FXML
    public void showSnpIndels() {
        try {
            // Load the fxml file and create a new stage for the popup dialog
            FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_LAYOUT);
            BorderPane page = loader.load();
            AnalysisDetailSNPsINDELsController controller = loader.getController();
            controller.setParamMap(getParamMap());
            controller.setMainController(this.mainController);
            controller.show(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fusion Dialog 창 출력
     */
    @FXML
    public void showFusion() {
        try {

            FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_FUSION_MAIN);
            GridPane page = loader.load();
            AnalysisDetailFusionMainController controller = loader.getController();
            controller.setParamMap(getParamMap());
            controller.setMainController(this.mainController);

            controller.show(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class OpenDetailButton extends TableCell<VariantCountByGene, Boolean> {
        final Button button = new Button("open");

        public OpenDetailButton() {

        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(item == null) {
                setGraphic(null);
                return;
            }
            setGraphic(button);
        }
    }

    @FXML
    public void searchGene() {
        Sample sample = (Sample)getParamMap().get("sample");
        ObservableList<VariantCountByGeneForGermlineDNA> VariantCountByGene = getVariantcountByGeneData(sample.getId(),
                    geneListTextField.getText());
        geneTable.setItems(VariantCountByGene);
    }

    @FXML
    public void cellSelectEvent(MouseEvent event){
        if(event.getClickCount() == 2) {
            String obj = geneTable.getSelectionModel().getSelectedCells().get(0).getTableColumn().getText();
            VariantCountByGeneForGermlineDNA gene = geneTable.getSelectionModel().getSelectedItem();
            logger.debug(obj + " gene : " + gene.getGeneSymbol());
        }
    }


    private void showVariantCountByGeneData() {
        Sample sample = (Sample)getParamMap().get("sample");
        ObservableList<VariantCountByGeneForGermlineDNA> VariantCountByGene = getVariantcountByGeneData(sample.getId(), null);
        geneTable.setItems(VariantCountByGene);
    }

    private ObservableList<VariantCountByGeneForGermlineDNA> getVariantcountByGeneData(int sampleId, String geneList) {
        Map<String, Object> param = new HashMap<>();
        ObservableList<VariantCountByGeneForGermlineDNA> variantCountByGenes = null;
        if (geneList != null && geneList.trim().length() > 0) {
            param.put("geneSymbols", geneList);
        }
        try {
            HttpClientResponse response = null;

            response = apiService.get("/analysisResults/variantCountByGeneForGermlineDNA/" + sampleId,
                    param, null, false);

            if (response != null) {
                variantCountByGenes = (ObservableList<VariantCountByGeneForGermlineDNA>) response
                        .getMultiObjectBeforeConvertResponseToJSON(VariantCountByGeneForGermlineDNA.class,
                                true);
            }
            if(variantCountByGenes == null) return null;
        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(),
                    true);
        }
        return variantCountByGenes
                .stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        items -> FXCollections.observableArrayList(items)));
    }
}
