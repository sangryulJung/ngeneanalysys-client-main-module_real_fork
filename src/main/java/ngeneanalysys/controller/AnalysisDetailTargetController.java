package ngeneanalysys.controller;

import javafx.beans.property.SimpleBooleanProperty;
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
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.model.VariantPerGene;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

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
    private TextField geneNameTextField;

    @FXML
    private TableView<VariantPerGene> geneTable;

    @FXML
    private TableColumn<VariantPerGene, String> geneColumn;

    @FXML
    private TableColumn<VariantPerGene, Integer> tierOneSnvColumn;

    @FXML
    private TableColumn<VariantPerGene, Integer> tierOneIndelsColumn;

    @FXML
    private TableColumn<VariantPerGene, Integer> tierOneFusionColumn;

    @FXML
    private TableColumn<VariantPerGene, Integer> tierTwoSnvColumn;

    @FXML
    private TableColumn<VariantPerGene, Integer> tierTwoIndelsColumn;

    @FXML
    private TableColumn<VariantPerGene, Integer> tierTwoFusionColumn;

    @FXML
    private TableColumn<VariantPerGene, Integer> tierThreeSnvColumn;

    @FXML
    private TableColumn<VariantPerGene, Integer> tierThreeIndelsColumn;

    @FXML
    private TableColumn<VariantPerGene, Integer> tierThreeFusionColumn;

    @FXML
    private TableColumn<VariantPerGene, Integer> tierFourSnvColumn;

    @FXML
    private TableColumn<VariantPerGene, Integer> tierFourIndelsColumn;

    @FXML
    private TableColumn<VariantPerGene, Integer> tierFourFusionColumn;

    @FXML
    private TableColumn<VariantPerGene, Boolean> openDetailsColumn;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGene()));
        tierOneSnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getT1SnpCount()).asObject());
        tierOneIndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getT1SnpCount()).asObject());
        tierOneFusionColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getT1FusionCount()).asObject());
        tierTwoSnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getT2SnpCount()).asObject());
        tierTwoIndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getT2SnpCount()).asObject());
        tierTwoFusionColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getT2FusionCount()).asObject());
        tierThreeSnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getT3SnpCount()).asObject());
        tierThreeIndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getT3SnpCount()).asObject());
        tierThreeFusionColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getT3FusionCount()).asObject());
        tierFourSnvColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getT4SnpCount()).asObject());
        tierFourIndelsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getT4SnpCount()).asObject());
        tierFourFusionColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getT4FusionCount()).asObject());
        openDetailsColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue() != null));
        openDetailsColumn.setCellFactory(cellData -> new OpenDetailButton());

        addDummyData();

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
            // Load the fxml file and create a new stage for the popup dialog
            FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_FUSION);
            BorderPane page = loader.load();
            AnalysisDetailFusionController controller = loader.getController();
            controller.setParamMap(getParamMap());
            controller.setMainController(this.mainController);

            controller.show(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class OpenDetailButton extends TableCell<VariantPerGene, Boolean> {
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

    }

    @FXML
    public void cellSelectEvent(MouseEvent event){
        if(event.getClickCount() == 2) {
            String obj = geneTable.getSelectionModel().getSelectedCells().get(0).getTableColumn().getText();
            VariantPerGene gene = geneTable.getSelectionModel().getSelectedItem();
            logger.info(obj.toString() + " gene : " + gene.getGene());
        }
    }


    public void addDummyData() {

        ObservableList<VariantPerGene> variantPerGene = FXCollections.observableArrayList(
                new VariantPerGene(1 , (Integer)getParamMap().get("sampleId"),"NO", "sample Gene 1",2,0,2,0,2,2,2,0,0,0,2,0,2,1,0,2),
                new VariantPerGene(2 , (Integer)getParamMap().get("sampleId"),"NO", "sample Gene 2",0,2,2,0,2,0,0,2,0,2,0,2,0,1,2,0),
                new VariantPerGene(3 , (Integer)getParamMap().get("sampleId"),"NO", "sample Gene 3",0,0,2,2,0,0,2,0,0,0,2,2,0,1,2,0),
                new VariantPerGene(4 , (Integer)getParamMap().get("sampleId"),"NO", "sample Gene 4",2,0,2,2,0,2,0,2,0,2,0,0,2,1,0,2)
        );

        geneTable.setItems(variantPerGene);

    }
}
