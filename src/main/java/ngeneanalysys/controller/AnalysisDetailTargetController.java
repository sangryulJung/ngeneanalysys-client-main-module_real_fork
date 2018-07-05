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
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.VariantCountByGene;
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
public class AnalysisDetailTargetController extends AnalysisDetailCommonController {
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
    private TableView<VariantCountByGene> geneTable;

    @FXML
    private TableColumn<VariantCountByGene, String> geneColumn;

    @FXML
    private TableColumn<VariantCountByGene, Integer> tier1SnvColumn;

    @FXML
    private TableColumn<VariantCountByGene, Integer> tier1IndelsColumn;

    @FXML
    private TableColumn<VariantCountByGene, Integer> tier2SnvColumn;

    @FXML
    private TableColumn<VariantCountByGene, Integer> tier2IndelsColumn;

    @FXML
    private TableColumn<VariantCountByGene, Integer> tier3SnvColumn;

    @FXML
    private TableColumn<VariantCountByGene, Integer> tier3IndelsColumn;

    @FXML
    private TableColumn<VariantCountByGene, Integer> tier4SnvColumn;

    @FXML
    private TableColumn<VariantCountByGene, Integer> tier4IndelsColumn;

    @FXML
    private TableColumn<VariantCountByGene, Integer> tierNSnvColumn;

    @FXML
    private TableColumn<VariantCountByGene, Integer> tierNIndelsColumn;

    @FXML
    private Button fusionButton;

    @FXML
    private Button popUpBtn;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        Panel panel = (Panel)getParamMap().get("panel");

        if(panel.getTarget() != null && "DNA".equals(panel.getTarget())) {
            popUpBtn.setText("SNP/INDELs");
        } else {
            popUpBtn.setText("Fusion");
        }

        fusionButton.setDisable(true);
        fusionButton.setVisible(false);

        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGeneSymbol()));
        tier1SnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTier1SnpCount()).asObject());
        tier1IndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTier1IndelCount()).asObject());
        tier2SnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTier2SnpCount()).asObject());
        tier2IndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTier2IndelCount()).asObject());
        tier3SnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTier3SnpCount()).asObject());
        tier3IndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTier3IndelCount()).asObject());
        tier4SnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTier4SnpCount()).asObject());
        tier4IndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTier4IndelCount()).asObject());

        showVariantCountByGeneData();

    }

    /**
     * SNP/Indels Dialog 창 출력
     */
    @FXML
    public void showSnpIndels() {
        Panel panel = (Panel)getParamMap().get("panel");
        if(panel.getTarget() != null && "DNA".equals(panel.getTarget())) {
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
        } else {
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
    }

    /**
     * Fusion Dialog 창 출력
     */
    @FXML
    public void showFusion() {
        try {
            // Load the fxml file and create a new stage for the popup dialog
            /*FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_FUSION);
            BorderPane page = loader.load();
            AnalysisDetailFusionController controller = loader.getController();
            controller.setParamMap(getParamMap());
            controller.setMainController(this.mainController);*/

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
        ObservableList<VariantCountByGene> VariantCountByGene = getVariantcountByGeneData(sample.getId(),
                    geneListTextField.getText());
        geneTable.setItems(VariantCountByGene);
    }

    @FXML
    public void cellSelectEvent(MouseEvent event){
        if(event.getClickCount() == 2) {
            String obj = geneTable.getSelectionModel().getSelectedCells().get(0).getTableColumn().getText();
            VariantCountByGene gene = geneTable.getSelectionModel().getSelectedItem();
            logger.debug(obj + " gene : " + gene.getGeneSymbol());
        }
    }


    private void showVariantCountByGeneData() {
        Sample sample = (Sample)getParamMap().get("sample");
        ObservableList<VariantCountByGene> VariantCountByGene = getVariantcountByGeneData(sample.getId(), null);
        geneTable.setItems(VariantCountByGene);
    }

    private ObservableList<VariantCountByGene> getVariantcountByGeneData(int sampleId, String geneList) {
        Map<String, Object> param = new HashMap<>();
        ObservableList<VariantCountByGene> variantCountByGenes = null;
        if (geneList != null && geneList.trim().length() > 0) {
            param.put("geneSymbols", geneList);
        }
        try {
            HttpClientResponse response = null;

            response = apiService.get("/analysisResults/variantCountByGeneForSomaticDNA/" + sampleId,
                    param, null, false);

            if (response != null) {
                variantCountByGenes = (ObservableList<VariantCountByGene>) response
                        .getMultiObjectBeforeConvertResponseToJSON(VariantCountByGene.class,
                                true);
            }
            if(variantCountByGenes == null) return null;
        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
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
