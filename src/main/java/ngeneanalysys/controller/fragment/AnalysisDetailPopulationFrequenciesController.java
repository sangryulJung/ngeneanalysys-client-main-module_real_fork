package ngeneanalysys.controller.fragment;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.VariantAndInterpretationEvidence;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-03-21
 */
public class AnalysisDetailPopulationFrequenciesController extends SubPaneController {

    private final int gaugeSpeed = 10;

    @FXML
    private GridPane populationFrequencyGraphGridPane; /** Population Frequency Graph Box */

    private VariantAndInterpretationEvidence variant;

    @Override
    public void show(Parent root) throws IOException {
        variant = (VariantAndInterpretationEvidence)paramMap.get("variant");
        populationFrequencyGraphGridPane.getChildren().removeAll(populationFrequencyGraphGridPane.getChildren());
        showPopulationFrequency();
    }

    /**
     * 주요 기관 발현 빈도수(Population Frequencies) 그래프 화면 출력
     */
    public void showPopulationFrequency() {
        double populationFrequencyESP6500 = (variant.getSnpInDel().getPopulationFrequency().getEsp6500() != null &&
                variant.getSnpInDel().getPopulationFrequency().getEsp6500().getAll() != null) ? variant.getSnpInDel().getPopulationFrequency().getEsp6500().getAll().doubleValue() : -1d;
        addPopulationFrequencyGraph(0, 0, "ESP6500 ", populationFrequencyESP6500);

        double populationFrequency1000Genomes = (variant.getSnpInDel().getPopulationFrequency().getG1000() != null &&
                variant.getSnpInDel().getPopulationFrequency().getG1000().getAll() != null) ? variant.getSnpInDel().getPopulationFrequency().getG1000().getAll().doubleValue() : -1d;
        addPopulationFrequencyGraph(0, 1, "1KG ", populationFrequency1000Genomes);

        double populationFrequencyExAC = (variant.getSnpInDel().getPopulationFrequency().getExac() != null) ? variant.getSnpInDel().getPopulationFrequency().getExac().doubleValue() : -1d;
        addPopulationFrequencyGraph(1, 0, "ExAC ", populationFrequencyExAC);

        double populationFrequencyKorean = (variant.getSnpInDel().getPopulationFrequency().getKoreanExomInformationDatabase() != null)
                ? variant.getSnpInDel().getPopulationFrequency().getKoreanExomInformationDatabase().doubleValue() : -1d;
        addPopulationFrequencyGraph(1, 1, "Korean ", populationFrequencyKorean);
    }

    /**
     * 주요 기관 발현 빈도수(Population Frequencies) 그래프 화면 추가
     * @param row int
     * @param col int
     * @param title String
     * @param percentage double
     */
    private void addPopulationFrequencyGraph(int row, int col, String title, double percentage) {
        double graphPercentage = percentage * 100;
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        HBox hBox = new HBox();
        hBox.getStyleClass().add("population_frequency_graph");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("title");
        titleLabel.setTooltip(new Tooltip(title));

        HBox graphHBox = new HBox();
        graphHBox.getStyleClass().add("horizon_stick");
        Label graphLabel = new Label("");
        graphHBox.getChildren().add(graphLabel);
        Label percentLabel = new Label((percentage > -1) ? BigDecimal.valueOf(percentage).stripTrailingZeros().toString() : "");
        percentLabel.getStyleClass().add("percent");
        percentLabel.setPrefWidth(130);
        percentLabel.setMinWidth(130);
        percentLabel.setPrefHeight(10);
        percentLabel.setMinHeight(10);
        percentLabel.setAlignment(Pos.TOP_RIGHT);
        percentLabel.getStyleClass().add("font_size_8");
        hBox.getChildren().setAll(titleLabel, graphHBox);
        vbox.getChildren().setAll(percentLabel, hBox);
        // 퍼센트 데이터가 없는 경우(-1) disable 처리함.
        if(percentage < 0) {
            hBox.setDisable(true);
        }
        double graphHBoxWidth = 74.0;
        //populationFrequencyGraphVBox.getChildren().add(hBox);
        populationFrequencyGraphGridPane.add(vbox, col, row);
        if(graphPercentage > -1) {
            double widthByPercent = Math.round(graphHBoxWidth * (graphPercentage/100));
            // 애니메이션 타이머 실행
            AnimationTimer timer = new AnimationTimer() {
                private double idx = 0;
                private double step = widthByPercent / gaugeSpeed;
                {
                    if(step < 1){
                        step = 1.0;
                    }
                }
                @Override
                public void handle(long l) {
                    if(idx >= widthByPercent) {
                        stop();
                    }
                    // 막대 너비 사이즈 1pixel씩 증가
                    graphLabel.setMinWidth(idx);
                    graphLabel.setPrefWidth(idx);
                    graphLabel.setMaxWidth(idx);
                    idx += step;
                    if(idx > widthByPercent) {
                        idx = widthByPercent;
                    }
                }
            };
            timer.start();
        }
    }
}
