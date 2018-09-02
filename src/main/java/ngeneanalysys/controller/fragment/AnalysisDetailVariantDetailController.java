package ngeneanalysys.controller.fragment;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.util.*;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-03-21
 */
public class AnalysisDetailVariantDetailController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private GridPane detailWarpper;

    private AnalysisDetailVariantNomenclatureController analysisDetailVariantNomenclatureController;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("variant detail view");
        if(!detailWarpper.getChildren().isEmpty()) detailWarpper.getChildren().removeAll(detailWarpper.getChildren());
        showReadDepth();
        showVariantNomenclature();
        showDetailSub();
        showPopulationFrequency();
    }

    private void showDetailSub() {
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

    private void showReadDepth() {
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
    private void showVariantNomenclature() {
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
    private void showPopulationFrequency() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_POPULATION_FREQUENCIES);
            Node node = loader.load();
            AnalysisDetailPopulationFrequenciesController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.show((Parent) node);
            detailWarpper.add(node, 3, 0);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }
}
