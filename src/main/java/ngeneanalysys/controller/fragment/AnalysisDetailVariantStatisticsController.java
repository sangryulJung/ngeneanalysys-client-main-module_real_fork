package ngeneanalysys.controller.fragment;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;
import ngeneanalysys.animaition.VariantStatisticsTimer;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.VariantStatistics;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-03-21
 */
public class AnalysisDetailVariantStatisticsController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private final int gaugeSpeed = 10;

    @FXML
    private Canvas canvasVariantStatisticsRun;

    @FXML
    private Canvas canvasVariantStatisticsPanel;

    @FXML
    private Canvas canvasVariantStatisticsGroup;

    @FXML
    private GridPane variantStatisticsGirdPane;

    @Override
    public void show(Parent root) throws IOException {
        showVariantStatistics();
        showPopulationFrequency();
    }

    /**
     * 변이 발견 빈도수(Variant Frequency) 게이지 그래프 화면 출력
     */
    public void showVariantStatistics() {
        VariantStatistics variantStatistics = (VariantStatistics) paramMap.get("variantStatistics");
        if(variantStatistics != null) {
            int variantFrequencyRunCount = variantStatistics.getSameVariantSampleCountInRun();
            int variantFrequencyRunTotalCount = variantStatistics.getTotalSampleCountInRun();
            int variantFrequencyPanelCount = variantStatistics.getSamePanelSameVariantSampleCountInMemberGroup();
            int variantFrequencyPanelTotalCount = variantStatistics.getTotalSamePanelSampleCountInMemberGroup();
            int variantFrequencyAccountCount = variantStatistics.getSameVariantSampleCountInMemberGroup();
            int variantFrequencyAccountTotalCount = variantStatistics.getTotalSampleCountInMemberGroup();

            double variantFrequencyRun = (double) variantFrequencyRunCount / (double) variantFrequencyRunTotalCount;
            double variantFrequencyPanel = (double) variantFrequencyPanelCount / (double) variantFrequencyPanelTotalCount;
            double variantFrequencyAccount = (double) variantFrequencyAccountCount / (double) variantFrequencyAccountTotalCount;

            AnimationTimer variantStatisticsRunTimer = new VariantStatisticsTimer(
                    canvasVariantStatisticsRun.getGraphicsContext2D(), variantFrequencyRun, "RUN",
                    String.format("%.2f%%", variantFrequencyRun * 100.0), this.gaugeSpeed);
            AnimationTimer variantStatisticsPanelTimer = new VariantStatisticsTimer(
                    canvasVariantStatisticsPanel.getGraphicsContext2D(), variantFrequencyPanel, "PANEL",
                    String.format("%.2f%%", variantFrequencyPanel * 100.0), this.gaugeSpeed);
            AnimationTimer variantStatisticsGroupTimer = new VariantStatisticsTimer(
                    canvasVariantStatisticsGroup.getGraphicsContext2D(), variantFrequencyAccount, "GROUP",
                    String.format("%.2f%%", variantFrequencyAccount * 100.0), this.gaugeSpeed);
            variantStatisticsRunTimer.start();
            variantStatisticsPanelTimer.start();
            variantStatisticsGroupTimer.start();
            AnimationTimer variantStatisticsRunTimer1 = new VariantStatisticsTimer(
                    canvasVariantStatisticsRun.getGraphicsContext2D(), variantFrequencyRun, "RUN",
                    variantFrequencyRunCount + "/" + variantFrequencyRunTotalCount, gaugeSpeed);
            AnimationTimer variantStatisticsPanelTimer1 = new VariantStatisticsTimer(
                    canvasVariantStatisticsPanel.getGraphicsContext2D(), variantFrequencyPanel, "PANEL",
                    variantFrequencyPanelCount + "/" + variantFrequencyPanelTotalCount, gaugeSpeed);
            AnimationTimer variantStatisticsGroupTimer1 = new VariantStatisticsTimer(
                    canvasVariantStatisticsGroup.getGraphicsContext2D(), variantFrequencyAccount, "GROUP",
                    variantFrequencyAccountCount + "/" + variantFrequencyAccountTotalCount, gaugeSpeed);
            canvasVariantStatisticsRun.setOnMouseEntered(event ->
                    variantStatisticsRunTimer1.start());
            canvasVariantStatisticsRun.setOnMouseExited(event ->
                    variantStatisticsRunTimer.start());
            canvasVariantStatisticsPanel.setOnMouseEntered(event ->
                    variantStatisticsPanelTimer1.start());
            canvasVariantStatisticsPanel.setOnMouseExited(event ->
                    variantStatisticsPanelTimer.start());
            canvasVariantStatisticsGroup.setOnMouseEntered(event ->
                    variantStatisticsGroupTimer1.start());
            canvasVariantStatisticsGroup.setOnMouseExited(event ->
                    variantStatisticsGroupTimer.start());
        }
    }

    public void showPopulationFrequency() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_POPULATION_FREQUENCIES);
            Node node = loader.load();
            AnalysisDetailPopulationFrequenciesController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.show((Parent) node);
            variantStatisticsGirdPane.add(node, 1, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
