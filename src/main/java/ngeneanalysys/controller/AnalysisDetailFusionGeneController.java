package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.FusionGene;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class AnalysisDetailFusionGeneController extends SubPaneController {

    private AnalysisDetailFusionMainController analysisDetailFusionMainController;

    @FXML
    private TableView<FusionGene> fusionGeneTableView;

    @FXML
    private TableColumn<FusionGene, String> nameTableColumn;

    @FXML
    private TableColumn<FusionGene, String> tierTableColumn;

    @FXML
    private TableColumn<FusionGene, String> userTierTableColumn;

    @FXML
    private TableColumn<FusionGene, String> reportTableColumn;

    @FXML
    private TableColumn<FusionGene, String> junctionReadCountTableColumn;

    @FXML
    private TableColumn<FusionGene, String> spanninigFragCountTableColumn;

    @FXML
    private TableColumn<FusionGene, String> leftBreakPointTableColumn;

    @FXML
    private TableColumn<FusionGene, String> rightBreakPointTableColumn;

    @FXML
    private TableColumn<FusionGene, String> leftTxExonTableColumn;

    @FXML
    private TableColumn<FusionGene, String> rightTxExonTableColumn;

    @FXML
    private TableColumn<FusionGene, String> protFusionTypeTableColumn;

    @FXML
    private TableColumn<FusionGene, String> spliceTypeTableColumn;

    @FXML
    private TableColumn<FusionGene, String> largeAnchorSupportTableColumn;

    @FXML
    private TableColumn<FusionGene, String> leftBreakDincuTableColumn;

    @FXML
    private TableColumn<FusionGene, String> rightBreakDinucTableColumn;

    @FXML
    private TableColumn<FusionGene, String> annotsTableColumn;

    @FXML
    private TableColumn<FusionGene, String> cdsLeftIdTableColumn;

    @FXML
    private TableColumn<FusionGene, String> cdsleftRangeTableColumn;

    @FXML
    private TableColumn<FusionGene, String> cdsRightIdTableColumn;

    @FXML
    private TableColumn<FusionGene, String> cdsRightRangeTableColumn;

    @FXML
    private TableColumn<FusionGene, String> fusionModelTableColumn;

    @FXML
    private TableColumn<FusionGene, String> fusionCdsTableColumn;

    @FXML
    private TableColumn<FusionGene, String> fusionTranslTableColumn;

    @FXML
    private TableColumn<FusionGene, String> pfamLeftTableColumn;

    @FXML
    private TableColumn<FusionGene, String> pframRightTableColumn;



    /**
     * @param analysisDetailFusionMainController
     */
    public void setAnalysisDetailFusionMainController(AnalysisDetailFusionMainController analysisDetailFusionMainController) {
        this.analysisDetailFusionMainController = analysisDetailFusionMainController;
    }

    @Override
    public void show(Parent root) throws IOException {


        analysisDetailFusionMainController.subTabFusionGene.setContent(root);
    }
}
