package ngeneanalysys.controller.fragment;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ngeneanalysys.animaition.VariantStatisticsTimer;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import ngeneanalysys.model.VariantStatistics;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.PopOverUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.controlsfx.control.PopOver;
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
    private Label runSampleCountLabel;
    @FXML
    private Label runSamplePercentageLabel;
    @FXML
    private Label panelSampleCountLabel;
    @FXML
    private Label panelSamplePercentageLabel;
    @FXML
    private Label groupSampleCountLabel;
    @FXML
    private Label groupSamplePercentageLabel;
    @FXML
    private Label runSampleHelpLabel;
    @FXML
    private Label panelSampleHelpLabel;
    @FXML
    private Label groupSampleHelpLabel;
    private APIService apiService;

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();
        showVariantStatistics();
        runSampleHelpLabel.setOnMouseClicked(e -> {
            openPopOver(runSampleHelpLabel, "The proportion of the samples with corresponding variant within the same run.");
        });
        panelSampleHelpLabel.setOnMouseClicked(e -> {
            openPopOver(panelSampleHelpLabel, "Describes the use of the same panel, and the proportion of the samples having the same variant as the corresponding variant.");
        });
        groupSampleHelpLabel.setOnMouseClicked(e -> {
            openPopOver(groupSampleHelpLabel, "The proportion of the samples having the same variant as the corresponding variant within the user group.");
        });
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
                                    canvasVariantStatisticsRun.getGraphicsContext2D(), variantFrequencyRun, "RUN", gaugeSpeed);
                            runSampleCountLabel.setText(variantFrequencyRunCount + "/" + variantFrequencyRunTotalCount);
                            runSamplePercentageLabel.setText(String.format("%.2f %%", variantFrequencyRun * 100.0));
                            AnimationTimer variantStatisticsPanelTimer = new VariantStatisticsTimer(
                                    canvasVariantStatisticsPanel.getGraphicsContext2D(), variantFrequencyPanel, "PANEL", gaugeSpeed);
                            panelSampleCountLabel.setText(variantFrequencyPanelCount + "/" + variantFrequencyPanelTotalCount);
                            panelSamplePercentageLabel.setText(String.format("%.2f %%", variantFrequencyPanel * 100.0));
                            AnimationTimer variantStatisticsGroupTimer = new VariantStatisticsTimer(
                                    canvasVariantStatisticsGroup.getGraphicsContext2D(), variantFrequencyAccount, "GROUP", gaugeSpeed);
                            groupSampleCountLabel.setText(variantFrequencyAccountCount + "/" + variantFrequencyAccountTotalCount);
                            groupSamplePercentageLabel.setText(String.format("%.2f %%", variantFrequencyAccount * 100.0));
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

    private void openPopOver(Label label, String value) {
        if(StringUtils.isEmpty(value)) return;
        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        popOver.setHeaderAlwaysVisible(true);
        popOver.setAutoHide(true);
        popOver.setAutoFix(true);
        popOver.setDetachable(true);
        popOver.setArrowSize(15);
        popOver.setMaxSize(220, 90);
        popOver.setPrefSize(220, 90);
        popOver.setMinSize(220, 90);

        VBox box = new VBox();
        box.setMinWidth(220);
        box.setPrefWidth(220);
        box.setMaxWidth(220);

        Label contents = new Label();
        contents.getStyleClass().add("font_size_11");
        contents.setPadding(new Insets(0, 0, 0, 10));
        contents.setAlignment(Pos.TOP_LEFT);
        contents.setPrefHeight(80);
        contents.setPrefWidth(200);
        contents.setWrapText(true);

        contents.setText(value);

        box.getChildren().add(contents);

        popOver.setContentNode(box);
        popOver.show(label, 0);
    }
}
