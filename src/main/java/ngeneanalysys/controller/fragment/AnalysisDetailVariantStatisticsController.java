package ngeneanalysys.controller.fragment;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;
import ngeneanalysys.animaition.VariantStatisticsTimer;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import ngeneanalysys.model.VariantStatistics;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-03-21
 */
public class AnalysisDetailVariantStatisticsController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private Canvas canvasVariantStatisticsRun;

    @FXML
    private Canvas canvasVariantStatisticsPanel;

    @FXML
    private Canvas canvasVariantStatisticsGroup;

    @FXML
    private GridPane variantStatisticsGirdPane;

    private APIService apiService;

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();
        showVariantStatistics();
    }

    /**
     * 변이 발견 빈도수(Variant Frequency) 게이지 그래프 화면 출력
     */
    public void showVariantStatistics() {
        VariantAndInterpretationEvidence analysisResultVariant = (VariantAndInterpretationEvidence)paramMap.get("variant");

        if (analysisResultVariant != null) {
            Task<Void> task = new Task<Void>() {
                VariantStatistics variantStatistics;

                @Override
                protected Void call() throws Exception {
                    HttpClientResponse response = apiService.get(
                            "/analysisResults/snpInDels/" + analysisResultVariant.getSnpInDel().getId() + "/snpInDelStatistics",
                            null, null, false);
                    variantStatistics = response.getObjectBeforeConvertResponseToJSON(VariantStatistics.class);
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    if (variantStatistics != null) {
                        Platform.runLater(() -> {
                            int gaugeSpeed = 10;
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
                                    String.format("%.2f%%\n(%s)", variantFrequencyRun * 100.0, variantFrequencyRunCount + "/" + variantFrequencyRunTotalCount), gaugeSpeed);
                            AnimationTimer variantStatisticsPanelTimer = new VariantStatisticsTimer(
                                    canvasVariantStatisticsPanel.getGraphicsContext2D(), variantFrequencyPanel, "PANEL",
                                    String.format("%.2f%%\n(%s)", variantFrequencyPanel * 100.0, variantFrequencyPanelCount + "/" + variantFrequencyPanelTotalCount), gaugeSpeed);
                            AnimationTimer variantStatisticsGroupTimer = new VariantStatisticsTimer(
                                    canvasVariantStatisticsGroup.getGraphicsContext2D(), variantFrequencyAccount, "GROUP",
                                    String.format("%.2f%%\n(%s)", variantFrequencyAccount * 100.0, variantFrequencyAccountCount + "/" + variantFrequencyAccountTotalCount), gaugeSpeed);
                            variantStatisticsRunTimer.start();
                            variantStatisticsPanelTimer.start();
                            variantStatisticsGroupTimer.start();
                            variantStatistics = null;
                        });
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    variantStatistics = null;
                    DialogUtil.showWebApiException(getException(), getMainApp().getPrimaryStage());
                }
            };
            Thread thread = new Thread(task);
            thread.start();
        }
    }
}
