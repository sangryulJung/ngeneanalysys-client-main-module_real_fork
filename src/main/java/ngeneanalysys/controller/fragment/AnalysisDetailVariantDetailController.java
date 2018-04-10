package ngeneanalysys.controller.fragment;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.ExperimentTypeCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.service.ALAMUTService;
import ngeneanalysys.service.IGVService;
import ngeneanalysys.util.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jang
 * @since 2018-03-21
 */
public class AnalysisDetailVariantDetailController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private GridPane detailWarpper;

    private Panel panel;

    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;

    /** IGV 연동 서비스 */
    private IGVService igvService;

    /** Alamut 연동 서비스 */
    private ALAMUTService alamutService;

    private AnalysisDetailVariantNomenclatureController analysisDetailVariantNomenclatureController;

    @Override
    public void show(Parent root) throws IOException {
        panel = (Panel)paramMap.get("panel");

        selectedAnalysisResultVariant = (VariantAndInterpretationEvidence)paramMap.get("variant");

        igvService = IGVService.getInstance();
        igvService.setMainController(getMainController());

        // alamut service init
        alamutService = ALAMUTService.getInstance();
        alamutService.setMainController(getMainController());

        if(!detailWarpper.getChildren().isEmpty()) detailWarpper.getChildren().removeAll(detailWarpper.getChildren());
        showReadDepth();
        showVariantNomenclature();
        showDetailSub();

    }

    public void showDetailSub() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_DETAIL_SUB_INFO);
            Node node = loader.load();
            DetailSubInfoController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.setAnalysisDetailVariantNomenclatureController(analysisDetailVariantNomenclatureController);
            controller.show((Parent) node);
            detailWarpper.add(node, 2, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showReadDepth() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_READ_DEPTH);
            Node node = loader.load();
            AnalysisDetailReadDepthVariantFractionController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.show((Parent) node);
            detailWarpper.add(node, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showVariantNomenclature() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_VARIANT_NOMENCLATURE);
            Node node = loader.load();
            AnalysisDetailVariantNomenclatureController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.show((Parent) node);
            analysisDetailVariantNomenclatureController = controller;
            detailWarpper.add(node, 1, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
