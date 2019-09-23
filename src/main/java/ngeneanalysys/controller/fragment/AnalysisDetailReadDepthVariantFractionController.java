package ngeneanalysys.controller.fragment;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.AnalysisResultSummary;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.model.VariantAndInterpretationEvidence;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-03-21
 */
public class AnalysisDetailReadDepthVariantFractionController extends SubPaneController {
    private final int gaugeSpeed = 10;

    /** 수직 막대 그래프 Height */
    private double verticalGraphHeight = 116;

    @FXML
    private Label depthMinLabel;						/** DEPTH > MIN 최소값 */
    @FXML
    private Label depthMaxLabel;						/** DEPTH > MAX 최대값 */
    @FXML
    private Label depthValueLabel;						/** DEPTH > value 실제값 */
    @FXML
    private VBox depthLegendBox;						/** DEPTH > Legend Box */
    @FXML
    private Label depthLegendLabel;						/** DEPTH > Legend 실제값 표시 라벨 */
    @FXML
    private VBox depthLegendVBox;						/** DEPTH > Legend Box 실제값 표시 라벨 부모 박스 */
    @FXML
    private Label depthMeanLabel;						/** DEPTH > MEAN 평균값 */

    @FXML
    private Label fractionValueLabel;					/** FRACTION > Value 실제값 */
    @FXML
    private VBox fractionLegendBox;						/** FRACTION > Legend Box */
    @FXML
    private Label fractionLegendLabel;					/** FRACTION > Legend 실제값 표시 라벨 */
    @FXML
    private Label fractionRef;							/** FRACTION > Reference */
    @FXML
    private Label fractionAlt;							/** FRACTION > Alternate */
    @FXML
    private VBox fractionLegendVBox;					/** FRACTION > Legend Box 실제값 표시 라벨 부모 박스 */

    private SampleView sample;

    private VariantAndInterpretationEvidence variant;

    @Override
    public void show(Parent root) throws IOException {
        sample = (SampleView)paramMap.get("sampleView");
        variant = (VariantAndInterpretationEvidence)paramMap.get("variant");
        showReadDepth();
        showFraction();
    }

    private void showReadDepth() {
        AnalysisResultSummary summary =  sample.getAnalysisResultSummary();

        double depthMin = summary.getDepthMin();
        double depthMax = summary.getDepthMax();
        double depthMean = Double.parseDouble(summary.getDepthMean().toString() );
        double depth = 0;

        depth = variant.getSnpInDel().getReadInfo().getReadDepth();
        depthMinLabel.setText(String.valueOf(Math.round(depthMin)));
        depthMaxLabel.setText(String.valueOf(Math.round(depthMax)));
        depthLegendLabel.setText(String.valueOf(Math.round(depth)));
        depthMeanLabel.setText(String.valueOf(Math.round(depthMean)));
        double depthValueHeight = Math.round((verticalGraphHeight * ((depth - depthMin) / (depthMax - depthMin))));

        // 애니메이션 타이머 실행
        AnimationTimer depthGraphAnimationTimer = new AnimationTimer() {
            private double idx = 0;
            private double step = depthValueHeight / gaugeSpeed;
            {
                if (step < 1) {
                    step = 1;
                }
            }
            @SuppressWarnings("static-access")
            @Override
            public void handle(long l) {
                if(idx >= depthValueHeight) {
                    stop();
                }
                // 막대 높이 사이즈 1pixel씩 증가
                depthValueLabel.setMinHeight(idx);
                depthValueLabel.setPrefHeight(idx);
                depthValueLabel.setMaxHeight(idx);

                // 설명 라벨 위치 1pixel씩 증가
                double lableBottomMargin = idx - 2;

                if(lableBottomMargin < -6) lableBottomMargin = -8;
                depthLegendVBox.setMargin(depthLegendBox, new Insets(0, 0, lableBottomMargin, 0));
                idx += step;
                if (idx > depthValueHeight) {
                    idx = depthValueHeight;
                }
            }
        };
        depthGraphAnimationTimer.start();
    }

    /**
     * Fracion 그래프 값 입력 및 화면 출력
     */
    private void showFraction() {
        String ref = variant.getSnpInDel().getSnpInDelExpression().getRefSequence();
        String alt = variant.getSnpInDel().getSnpInDelExpression().getAltSequence();
        double alleleFraction = 0;

        BigDecimal allele = variant.getSnpInDel().getReadInfo().getAlleleFraction();
        alleleFraction = (allele != null) ? allele.doubleValue() : 0;

        fractionRef.setText(ref);
        fractionRef.setTooltip(new Tooltip(ref));
        fractionAlt.setText(alt);
        fractionAlt.setTooltip(new Tooltip(alt));
        if (alleleFraction < 100.0) {
            fractionLegendLabel.setText(String.format("%.2f", alleleFraction) + "%");
        } else {
            fractionLegendLabel.setText("100%");
        }
        double fractionValueHeight = Math.round((verticalGraphHeight * (alleleFraction / 100)));

        // 애니메이션 타이머 실행
        AnimationTimer fractionGraphAnimationTimer = new AnimationTimer() {
            private double idx = 0;
            private double step = fractionValueHeight / gaugeSpeed;
            {
                if (step < 1) {
                    step = 1;
                }
            }
            @SuppressWarnings("static-access")
            @Override
            public void handle(long l) {
                if(idx >= fractionValueHeight) {
                    stop();
                }
                // 막대 높이 사이즈 1pixel씩 증가
                fractionValueLabel.setMinHeight(idx);
                fractionValueLabel.setPrefHeight(idx);
                fractionValueLabel.setMaxHeight(idx);

                // 설명 라벨 위치 1pixel씩 증가
                double lableBottomMargin = idx - 2;
                if(lableBottomMargin < -6) lableBottomMargin = -8;
                VBox.setMargin(fractionLegendBox, new Insets(0, 0, lableBottomMargin, 0));
                idx += step;
                if (idx > fractionValueHeight) {
                    idx = fractionValueHeight;
                }
            }
        };
        fractionGraphAnimationTimer.start();
    }

}
