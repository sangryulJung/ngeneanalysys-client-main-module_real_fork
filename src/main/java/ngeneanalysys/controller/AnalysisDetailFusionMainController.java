package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.FusionTabMenuCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class AnalysisDetailFusionMainController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    /** api service */
    private APIService apiService;

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    /** Overview Tab */
    public Tab subTabFusionGene;
    /** Comments Tab */
    public Tab subTabFusionExpression;
    /** Warnings Tab */
    public Tab subTabExonSkipping;

    private AnalysisDetailFusionExpressionController analysisDetailFusionExpressionController;

    private AnalysisDetailFusionGeneController analysisDetailFusionGeneController;

    private AnalysisDetailExonSkippingController analysisDetailExonSkippingController;

    @FXML
    private TabPane tabArea;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("fusion main init");

        currentStage = new Stage();
        currentStage.setResizable(false);
        currentStage.initStyle(StageStyle.DECORATED);
        currentStage.initModality(Modality.APPLICATION_MODAL);
        currentStage.setTitle(CommonConstants.SYSTEM_NAME + " > New Analysis Request");
        // OS가 Window인 경우 아이콘 출력.
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            currentStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        currentStage.initOwner(getMainApp().getPrimaryStage());

        //탭생성
        tabArea.getTabs().removeAll(tabArea.getTabs());
        subTabFusionGene = new Tab(FusionTabMenuCode.FUSIONGENE.getMenuName());
        tabArea.getTabs().add(subTabFusionGene);
        subTabFusionExpression = new Tab(FusionTabMenuCode.GENEEXPRESSION.getMenuName());
        tabArea.getTabs().add(subTabFusionExpression);
        subTabExonSkipping = new Tab(FusionTabMenuCode.EXONSKIPPING.getMenuName());
        tabArea.getTabs().add(subTabExonSkipping);

        if (subTabFusionGene != null){
            showFusionGeneTab();
        }

        if (subTabFusionExpression != null){
            showFusionExpressionTab();
        }

        if (subTabExonSkipping != null){
            showExonSkippingTab();
        }
        // Scene Init
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.showAndWait();
    }

    public void showFusionExpressionTab() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_GENE_EXPRESSION);
            Node node = loader.load();
            analysisDetailFusionExpressionController = loader.getController();
            analysisDetailFusionExpressionController.setMainController(this.getMainController());
            analysisDetailFusionExpressionController.setAnalysisDetailFusionMainController(this);
            analysisDetailFusionExpressionController.setParamMap(paramMap);
            analysisDetailFusionExpressionController.show((Parent) node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showFusionGeneTab() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_FUSION_GENE);
            Node node = loader.load();
            analysisDetailFusionGeneController = loader.getController();
            analysisDetailFusionGeneController.setMainController(this.getMainController());
            analysisDetailFusionGeneController.setAnalysisDetailFusionMainController(this);
            analysisDetailFusionGeneController.setParamMap(paramMap);
            analysisDetailFusionGeneController.show((Parent) node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showExonSkippingTab() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_EXON_SKIPPING);
            Node node = loader.load();
            analysisDetailExonSkippingController = loader.getController();
            analysisDetailExonSkippingController.setMainController(this.getMainController());
            analysisDetailExonSkippingController.setAnalysisDetailFusionMainController(this);
            analysisDetailExonSkippingController.setParamMap(paramMap);
            analysisDetailExonSkippingController.show((Parent) node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
