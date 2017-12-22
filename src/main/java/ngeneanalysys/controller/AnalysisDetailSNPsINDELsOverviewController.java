package ngeneanalysys.controller;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Polyline;
import ngeneanalysys.animaition.ClinicalSignificantTimer;
import ngeneanalysys.animaition.VariantStatisticsTimer;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.*;
import ngeneanalysys.model.render.SNPsINDELsOverviewRadarGraph;
import ngeneanalysys.util.JsonUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jang
 * @since 2017-08-31
 */
public class AnalysisDetailSNPsINDELsOverviewController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    /** 수직 막대 그래프 Height */
    private double verticalGraphHeight = 116;

    private boolean graphAnimationIconDisplay = true;

    private final int gaugeSpeed = 10;

    @FXML
    private Label depthMinLabel;						/** DEPTH > MIN 최소값 */
    @FXML
    private Label depthMaxLabel;						/** DEPTH > MAX 최대값 */
    @FXML
    private Label depthValueLabel;						/** DEPTH > value 실제값 */
    @FXML
    private VBox depthLegendBox;						/** DEPTH > Legend Box */
    @FXML
    private ImageView depthLegendImageView;				/** DEPTH > Legend Animation Image */
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
    private ImageView fractionLegendImageView;				/** FRACTION > Legend Animation Image */
    @FXML
    private Label fractionLegendLabel;					/** FRACTION > Legend 실제값 표시 라벨 */
    @FXML
    private Label fractionRef;							/** FRACTION > Reference */
    @FXML
    private Label fractionAlt;							/** FRACTION > Alternate */
    @FXML
    private VBox fractionLegendVBox;					/** FRACTION > Legend Box 실제값 표시 라벨 부모 박스 */

    @FXML
    private ComboBox<String> transcriptComboBox;		/** Variant Nomenclature > 콤보박스 */
    @FXML
    private VBox positionBox;							/** Variant Nomenclature > ref/alt 위치 표시 박스 */
    @FXML
    private ScrollPane scrollBox;						/** Variant Nomenclature > ref/alt 위치 표시 박스 > Scroll Box */
    @FXML
    private GridPane gridBox;							/** Variant Nomenclature > ref/alt 위치 표시 박스 > Grind Box */
    @FXML
    private HBox sequenceCharsBox;						/** Variant Nomenclature > 염기서열 문자열 박스 */
    @FXML
    private Label genePositionStartLabel;				/** Variant Nomenclature > 시작위치 */
    @FXML
    private Label left22BpLabel;						/** Variant Nomenclature > Reference 위치 이전 염기서열 문자열 */
    @FXML
    private Label transcriptRefLabel;					/** Variant Nomenclature > Reference 염기서열 문자열 */
    @FXML
    private Label deletionRefLabel;						/** Variant Nomenclature > Deletion Reference 염기서열 문자열 */
    @FXML
    private Label right22BpLabel;						/** Variant Nomenclature > Reference 위치 이후 염기서열 문자열 */
    @FXML
    private Label transcriptAltLabel;					/** Variant Nomenclature > Alternative 염기서열 문자열 */
    @FXML
    private Label transcriptAltTypeLabel;				/** Variant Nomenclature > Alternative 구분 */
    @FXML
    private TextField geneSymbolTextField;							/** Variant Nomenclature > transcript Gene Symbol */
    @FXML
    private TextField hgvscTextField;							/** Variant Nomenclature > transcript HGVS Nucleotide */
    @FXML
    private TextField hgvspTextField;							/** Variant Nomenclature > transcript HGVS Protein */
    @FXML
    private TextField grch37TextField;							/** Variant Nomenclature > transcript GRch37 */

    @FXML
    private GridPane populationFrequencyGraphGridPane; /** Population Frequency Graph Box */
    @FXML
    private Label variantFrequencyRunPercentLabel;		/** Variant Frequency > 전체 Run의 퍼센트 */
    @FXML
    private Arc variantFrequencyRunArc;					/** Variant Frequency > 전체 Run의 Arc(원호) 객체 */
    @FXML
    private HBox variantFrequencyRunGauge;				/** Variant Frequency > 전체 Run의 게이지바 객체 */
    @FXML
    private Label variantFrequencyPanelPercentLabel;	/** Variant Frequency > Panel 기준 퍼센트 */
    @FXML
    private Arc variantFrequencyPanelArc;				/** Variant Frequency > Panel 기준의 Arc(원호) 객체 */
    @FXML
    private HBox variantFrequencyPanelGauge;			/** Variant Frequency > Panel 기준의 게이지바 객체 */
    @FXML
    private Label variantFrequencyAccountPercentLabel;	/** Variant Frequency > Account별 퍼센트 */
    @FXML
    private Arc variantFrequencyAccountArc;				/** Variant Frequency > Account별 Arc(원호) 객체 */
    @FXML
    private HBox variantFrequencyAccountGauge;			/** Variant Frequency > Account별 게이지바 객체 */

    @FXML
    private VBox significantRadarGraphArea;				/** clinical > SIGNIFICANT > 그래프 박스 영역 */
    @FXML
    private Label clinicalSignificantPathogenicityPredictionLabel;					/** clinical > SIGNIFICANT > 그래프 라벨 0 */
    @FXML
    private Label clinicalSignificantPathogenicityBicLabel;					/** clinical > SIGNIFICANT > 그래프 라벨 1 */
    @FXML
    private Label clinicalSignificantPathogenicityClinVarLabel;					/** clinical > SIGNIFICANT > 그래프 라벨 2 */
    @FXML
    private Label clinicalSignificantPathogenicityEnigmaLabel;					/** clinical > SIGNIFICANT > 그래프 라벨 3 */
    @FXML
    private Label clinicalSignificantPathogenicityPolyphenLabel;					/** clinical > SIGNIFICANT > 그래프 라벨 4 */
    @FXML
    private Label clinicalSignificantPathogenicitySiftLabel;					/** clinical > SIGNIFICANT > 그래프 라벨 5 */
    @FXML
    private Polyline significantGraphPolyline;			/** clinical > SIGNIFICANT > 그래프 영역 */
    @FXML
    private VBox frequenciesRadarGraphArea;				/** clinical > FREQUENCIES > 그래프 박스 영역 */
    @FXML
    private Label frequencies0Label;					/** clinical > FREQUENCIES > 그래프 라벨 0 */
    @FXML
    private Label frequencies1Label;					/** clinical > FREQUENCIES > 그래프 라벨 1 */
    @FXML
    private Label frequencies2Label;					/** clinical > FREQUENCIES > 그래프 라벨 2 */
    @FXML
    private Label frequencies3Label;					/** clinical > FREQUENCIES > 그래프 라벨 3 */
    @FXML
    private Label frequencies4Label;					/** clinical > FREQUENCIES > 그래프 라벨 4 */
    @FXML
    private Label frequencies5Label;					/** clinical > FREQUENCIES > 그래프 라벨 5 */
    @FXML
    private Label frequencies0ValueLabel;
    @FXML
    private Label frequencies1ValueLabel;
    @FXML
    private Label frequencies2ValueLabel;
    @FXML
    private Label frequencies3ValueLabel;
    @FXML
    private Label frequencies4ValueLabel;
    @FXML
    private Label frequencies5ValueLabel;
    @FXML
    private Polyline frequenciesGraphPolyline;			/** clinical > FREQUENCIES > 그래프 영역 */

    @FXML
    private Canvas canvasVariantStatisticsRun;
    @FXML
    private Canvas canvasVariantStatisticsPanel;
    @FXML
    private Canvas canvasVariantStatisticsGroup;
    @FXML
    private HBox pathogenicityPredictionHBox;
    @FXML
    private HBox pathogenicityBicHBox;
    @FXML
    private HBox pathogenicityClinVarHBox;
    @FXML
    private HBox pathogenicityEnigmaHBox;
    @FXML
    private Canvas polyphenCanvas;
    @FXML
    private Canvas siftCanvas;
    @FXML
    private Canvas mtCanvas;
    @FXML
    private VBox variantInterpretation;
    @FXML
    private VBox clinicalSignificant;
    @FXML
    private HBox evidenceHBox;
    @FXML
    private HBox negativeHBox;
    @FXML
    private Label swTierLabel;
    @FXML
    private Label userTierLabel;
    @FXML
    private TextArea resultTextArea;
    @FXML
    private Pane nodePane;
    @FXML
    private Pane tierPane;
    @FXML
    private Pane clinicalPane;

    @FXML private VBox significantArea;

    @FXML
    private ScrollPane transcriptDetailScrollBox;

    @FXML
    private VBox leftVbox;

    @FXML
    private GridPane transcriptDetailGrid;

    private AnalysisDetailSNPsINDELsController analysisDetailSNPsINDELsController;

    private Sample sample;

    private Panel panel;

    private VariantAndInterpretationEvidence variant;

    /**
     * @return the analysisDetailSNPsINDELsController
     */
    public AnalysisDetailSNPsINDELsController getAnalysisDetailSNPsINDELsController() {
        return analysisDetailSNPsINDELsController;
    }
    /**
     * @param analysisDetailSNPsINDELsController the analysisDetailSNPsINDELsController to set
     */
    public void setAnalysisDetailSNPsINDELsController(
            AnalysisDetailSNPsINDELsController analysisDetailSNPsINDELsController) {
        this.analysisDetailSNPsINDELsController = analysisDetailSNPsINDELsController;
    }

    @Override
    public void show(Parent root) throws IOException {
        sample = (Sample) paramMap.get("sample");
        panel = (Panel) paramMap.get("panel");
        variant = (VariantAndInterpretationEvidence) paramMap.get("variant");

        // 그래프 애니메이션 아이콘 출력여부 체크
        this.graphAnimationIconDisplay = "true".equals(config.getProperty("graph.animation.icon.display"));

        // Depth 그래프 값 입력 및 화면 출력
        showDepth();

        // Fracion 그래프 값 입력 및 화면 출력
        showFraction();

        // Variant Nomenclature 값 설정 및 화면 출력
        showVariantIdentification();

        // 템플릿 차트 삭제
        populationFrequencyGraphGridPane.getChildren().removeAll(populationFrequencyGraphGridPane.getChildren());
        // 주요 기관 발현 빈도수(Population Frequencies) 그래프 화면 출력
        showPopulationFrequency();

        // 변이 발견 빈도수(Variant Frequency) 게이지 그래프 화면 출력
        showVariantStatistics();

        // 링크 목록 화면 출력
        //showLink();



        if(panel != null && "SOMATIC".equals(panel.getAnalysisType())) {
            variantInterpretation.setVisible(true);
            //clinicalSignificant.setVisible(false);
            nodePane.getChildren().remove(clinicalPane);
            showVariantInterpretation();
        } else {
            //variantInterpretation.setVisible(false);
            nodePane.getChildren().remove(tierPane);
            clinicalSignificant.setVisible(true);
            // SIGNIFICANT 레이더 차트 화면 출력
            showClinicalSignificantGraph();
        }

        analysisDetailSNPsINDELsController.subTabOverview.setContent(root);

        logger.info("SNP/Indels subTab Overview");
    }

    private void showVariantInterpretation() {

        renderTier(swTierLabel, variant.getInterpretationEvidence(), variant.getSnpInDel().getSwTier());
        renderTier(userTierLabel, variant.getInterpretationEvidence(), variant.getSnpInDel().getExpertTier());
        renderEvidence(evidenceHBox, variant.getInterpretationEvidence());
        renderNegative(negativeHBox, variant.getInterpretationEvidence());
    }

    //evidence 정보 표시
    private void renderEvidence(HBox node, Interpretation interpretation) {
        if (node == null || interpretation == null) return;
        ObservableList<Node> childs = node.getChildren();
        resultTextArea.setText("");
        if (childs != null) {
            for (Node child : childs) {
                child.getStyleClass().removeAll(child.getStyleClass());
                boolean flag = false;
                if(((Label)child).getText().equals("A") && !StringUtils.isEmpty(interpretation.getEvidenceLevelA())) {
                    child.getStyleClass().add("prediction_E");
                    //resultTextArea.setText(interpretation.getInterpretationEvidenceA());
                    child.setStyle("-fx-cursor:hand;");
                    child.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> resultTextArea.setText(interpretation.getEvidenceLevelA()));
                    flag = true;
                }
                if(((Label)child).getText().equals("B") && !StringUtils.isEmpty(interpretation.getEvidenceLevelB())) {
                    child.getStyleClass().add("prediction_E");
                    //resultTextArea.setText(interpretation.getInterpretationEvidenceB());
                    child.setStyle("-fx-cursor:hand;");
                    child.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> resultTextArea.setText(interpretation.getEvidenceLevelB()));
                    flag = true;
                }
                if(((Label)child).getText().equals("C") && !StringUtils.isEmpty(interpretation.getEvidenceLevelC())) {
                    child.getStyleClass().add("prediction_E");
                    //resultTextArea.setText(interpretation.getInterpretationEvidenceC());
                    child.setStyle("-fx-cursor:hand;");
                    child.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> resultTextArea.setText(interpretation.getEvidenceLevelC()));
                    flag = true;
                }
                if(((Label)child).getText().equals("D") && !StringUtils.isEmpty(interpretation.getEvidenceLevelD())) {
                    child.getStyleClass().add("prediction_E");
                    //resultTextArea.setText(interpretation.getInterpretationEvidenceD());
                    child.setStyle("-fx-cursor:hand;");
                    child.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> resultTextArea.setText(interpretation.getEvidenceLevelD()));
                    flag = true;
                }
                if(((Label)child).getText().equals("EVIDENCE")) {
                    child.getStyleClass().add("clinical_significant_pathogenicity_label");
                    flag = true;
                }
                if(!flag) {
                    child.getStyleClass().add("prediction_none");
                    if(resultTextArea.getText().equalsIgnoreCase("")) {
                        resultTextArea.setText("");
                    }
                }
            }
        }
    }

    //tier 정보 표시
    private void renderTier(Label node, Interpretation interpretation, String tier) {
        node.getStyleClass().removeAll(node.getStyleClass());
        if(tier == null) {
            node.getStyleClass().add("prediction_none");
            return;
        }
        String text = "";
        if(tier.equalsIgnoreCase("T1")) {
            text = "I";
        } else if(tier.equalsIgnoreCase("T2")) {
            text = "II";
        } else if(tier.equalsIgnoreCase("T3")) {
            text = "III";
        } else if(tier.equalsIgnoreCase("T4")) {
            text = "IV";
        }

        boolean flag = false;
        if(!text.equalsIgnoreCase("")) {
            node.setText(text);
            node.getStyleClass().add("prediction_C");
        } else {
            node.getStyleClass().add("prediction_none");
        }
    }

    //tier 정보 표시
    private void renderNegative(HBox node, Interpretation interpretation) {
        if (node == null || interpretation == null) return;
        ObservableList<Node> childs = node.getChildren();

        if (childs != null) {
            for (Node child : childs) {
                child.getStyleClass().removeAll(child.getStyleClass());
                if(((Label)child).getText().equals("NEGATIVE")) {
                    child.getStyleClass().add("clinical_significant_pathogenicity_label");
                    continue;
                }
                boolean flag = false;
                if(((Label)child).getText().equals("N") && !StringUtils.isEmpty(interpretation.getEvidencePersistentNegative())) {
                    child.getStyleClass().add("prediction_A");
                    //resultTextArea.setText(interpretation.getInterpretationNegativeTesult());
                    child.setStyle("-fx-cursor:hand;");
                    child.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> resultTextArea.setText(interpretation.getEvidencePersistentNegative()));
                    flag = true;
                }

                if(!flag) {
                    child.getStyleClass().add("prediction_none");
                    //resultTextArea.setText("");
                }
            }
        }
    }


    /**
     * Depth 그래프 값 입력 및 화면 출력
     */
    @SuppressWarnings("unchecked")
    public void showDepth() {
        AnalysisResultSummary summary =  sample.getAnalysisResultSummary();
        Map<String,Object> alleleMap = (Map<String,Object>) paramMap.get("allele");

        double depthMin = summary.getDepthMin();
        double depthMax = summary.getDepthMax();
        double depthMean = Double.parseDouble(summary.getDepthMean().toString() );
        double depth = 0;
        /*if(alleleMap != null && !alleleMap.isEmpty() && alleleMap.size() > 0) {
            depth = (alleleMap.containsKey("total_read_depth")) ? (int) alleleMap.get("total_read_depth") : 0;

        }*/
        depth = variant.getSnpInDel().getReadInfo().getReadDepth();
        depthLegendImageView.setVisible(this.graphAnimationIconDisplay);
        depthMinLabel.setText(String.valueOf(Math.round(depthMin)));
        depthMaxLabel.setText(String.valueOf(Math.round(depthMax)));
        depthLegendLabel.setText(String.valueOf(Math.round(depth)));
        depthMeanLabel.setText(String.valueOf(Math.round(depthMean)));
        double depthValueHeight = Math.round((verticalGraphHeight * ((depth - depthMin) / (depthMax - depthMin))));

        // 애니메이션 타이머 실행
        AnimationTimer depthGraphAnimationTimer = new AnimationTimer() {
            private double idx = 0;
            private double maxLeftMarginForImage = 26;
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
                    depthLegendImageView.setImage(resourceUtil.getImage("/layout/images/animation_stop.png"));
                    stop();
                }
                // 막대 높이 사이즈 1pixel씩 증가
                depthValueLabel.setMinHeight(idx);
                depthValueLabel.setPrefHeight(idx);
                depthValueLabel.setMaxHeight(idx);

                // 설명 라벨 위치 1pixel씩 증가
                double lableBottomMargin = idx - 2;
                double imageLeftMargin = Math.round(idx*(maxLeftMarginForImage/depthValueHeight));
                if(lableBottomMargin < -6) lableBottomMargin = -8;
                depthLegendBox.setMargin(depthLegendImageView, new Insets(0, 0, 0, imageLeftMargin));
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
    @SuppressWarnings("unchecked")
    public void showFraction() {
        /*Map<String,Object> alleleMap = (Map<String,Object>) paramMap.get("allele");
        Map<String,Object> variantInformationMap = (Map<String,Object>) paramMap.get("variantInformation");

        String ref = (String) variantInformationMap.get("ref");
        String alt = (String) variantInformationMap.get("alt");*/
        String ref = variant.getSnpInDel().getSequenceInfo().getRefSequence();
        String alt = variant.getSnpInDel().getSequenceInfo().getAltSequence();
        double alleleFraction = 0;

        /*if(alleleMap != null && !alleleMap.isEmpty() && alleleMap.size() > 0) {
            alleleFraction = (alleleMap.containsKey("allele_fraction")) ? (double) alleleMap.get("allele_fraction") : 0;
        }*/
        BigDecimal allele = variant.getSnpInDel().getReadInfo().getAlleleFraction();
        alleleFraction = (allele != null) ? allele.doubleValue() : 0;

        fractionLegendImageView.setVisible(this.graphAnimationIconDisplay);
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
            private double maxLeftMarginForImage = 26;
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
                    fractionLegendImageView.setImage(resourceUtil.getImage("/layout/images/animation_stop.png"));
                    stop();
                }
                // 막대 높이 사이즈 1pixel씩 증가
                fractionValueLabel.setMinHeight(idx);
                fractionValueLabel.setPrefHeight(idx);
                fractionValueLabel.setMaxHeight(idx);

                // 설명 라벨 위치 1pixel씩 증가
                double lableBottomMargin = idx - 2;
                double imageLeftMargin = Math.round(idx*(maxLeftMarginForImage/fractionValueHeight));
                if(lableBottomMargin < -6) lableBottomMargin = -8;
                fractionLegendBox.setMargin(fractionLegendImageView, new Insets(0, 0, 0, imageLeftMargin));
                fractionLegendVBox.setMargin(fractionLegendBox, new Insets(0, 0, lableBottomMargin, 0));
                idx += step;
                if (idx > fractionValueHeight) {
                    idx = fractionValueHeight;
                }
            }
        };
        fractionGraphAnimationTimer.start();
    }

    /**
     * Variant Nomenclature 값 설정 및 화면 출력
     */
    @SuppressWarnings("unchecked")
    public void showVariantIdentification() {
        List<SnpInDelTranscript> transcriptDataList = (List<SnpInDelTranscript>) paramMap.get("snpInDelTranscripts");

        String ref = variant.getSnpInDel().getSequenceInfo().getRefSequence();
        String alt = variant.getSnpInDel().getSequenceInfo().getAltSequence();
        String left22Bp = variant.getSnpInDel().getSequenceInfo().getLeftSequence();
        String right22Bp = variant.getSnpInDel().getSequenceInfo().getRightSequence();
        String genePositionStart = String.valueOf(variant.getSnpInDel().getSequenceInfo().getGenomicCoordinate());
        String transcriptAltType = variant.getSnpInDel().getSnpInDelExpression().getVariantType();
        String defaultTranscript = null;
        // transcript 콤보박스 설정
            // variant identification transcript data map

        if(transcriptDataList != null && !transcriptDataList.isEmpty()) {
            ObservableList<String> comboItemList = FXCollections.observableArrayList();
            // 콤보박스 아이템 목록 생성
            for(SnpInDelTranscript snpInDelTranscript : transcriptDataList) {
                comboItemList.add(snpInDelTranscript.getTranscriptId());
                if(snpInDelTranscript.getIsDefault()) {
                    defaultTranscript = snpInDelTranscript.getTranscriptId();
                }
            }

            // 콤보박스 아이템 삽입
            transcriptComboBox.setItems(comboItemList);

            // 콤보박스 Onchange 이벤트 바인딩
            transcriptComboBox.getSelectionModel().selectedIndexProperty().addListener((ov, oldIdx, newIdx) -> {
                if(oldIdx != newIdx) {
                    Optional<SnpInDelTranscript> transcriptOptional = transcriptDataList.stream().filter(item ->
                            item.getTranscriptId().equals(transcriptComboBox.getItems().get((int)newIdx))).findFirst();
                    if(transcriptOptional.isPresent()) {
                        SnpInDelTranscript transcript = transcriptOptional.get();
                        String geneSymbol = (!StringUtils.isEmpty(transcript.getGeneSymbol())) ? transcript.getGeneSymbol() : "N/A";
                        String transcriptName = (!StringUtils.isEmpty(transcript.getTranscriptId())) ? transcript.getTranscriptId() : "N/A";
                        String codingDNA = (!StringUtils.isEmpty(transcript.getCodingDna())) ? transcript.getCodingDna() : "N/A";
                        String protein = (!StringUtils.isEmpty(transcript.getProtein())) ? transcript.getProtein() : "N/A";
                        String genomicDNA = (!StringUtils.isEmpty(transcript.getGenomicDna())) ? transcript.getGenomicDna() : "N/A";


                        logger.info(String.format("variant identification choose '%s' option idx [%s]", transcriptName, newIdx));
                        List<Integer> textLength = new ArrayList<>();
                        geneSymbolTextField.setText(geneSymbol); //Gene Symbol
                        if(!StringUtils.isEmpty(geneSymbol)) {
                            geneSymbolTextField.setTooltip(new Tooltip(geneSymbol));
                            textLength.add(geneSymbol.length());
                        }
                        hgvspTextField.setText(protein); //Protein
                        if(!StringUtils.isEmpty(protein)) {
                            hgvspTextField.setTooltip(new Tooltip(protein));
                            textLength.add(protein.length());
                        }
                        hgvscTextField.setText(codingDNA); //Coding DNA
                        if(!StringUtils.isEmpty(codingDNA)) {
                            hgvscTextField.setTooltip(new Tooltip(codingDNA));
                            textLength.add(codingDNA.length());
                        }
                        grch37TextField.setText(genomicDNA); //Genomic DNA
                        if(!StringUtils.isEmpty(genomicDNA)) {
                            grch37TextField.setTooltip(new Tooltip(genomicDNA));
                            textLength.add(genomicDNA.length());
                        }
                        int maxTextLength = 0;
                        Optional<Integer> maxTextLengthOptional = textLength.stream().max(Integer::compare);
                        if(maxTextLengthOptional.isPresent()) {
                            maxTextLength = maxTextLengthOptional.get();
                        }
                        logger.info("max Length : " + maxTextLength);

                        if(maxTextLength > 29) {
                            transcriptDetailGrid.setPrefWidth(maxTextLength * 9);
                            geneSymbolTextField.setMinWidth(maxTextLength * 9);
                            hgvspTextField.setMinWidth(maxTextLength * 9);
                            hgvscTextField.setMinWidth(maxTextLength * 9);
                            grch37TextField.setMinWidth(maxTextLength * 9);
                            transcriptDetailScrollBox.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                            transcriptDetailScrollBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                        }
                    }
                }
            });

            // 첫번째 아이템 선택 처리
            transcriptComboBox.getSelectionModel().select(defaultTranscript);
        }

        // 레퍼런스 앞문자열 끝에서부터 9글자만 출력함.
        int displayLeft22Bplength = 9;
        String displayLeft22Bp = left22Bp;
        /*if(!StringUtils.isEmpty(left22Bp) && left22Bp.length() > displayLeft22Bplength) {
            for(int i = 0; i < left22Bp.length(); i++) {
                if( i >= (left22Bp.length() - displayLeft22Bplength)) {
                    displayLeft22Bp += left22Bp.substring(i, i + 1);
                }
            }
        }*/

        // 레퍼런스 뒷문자열 9글자만 출력 : 레퍼런스 문자열이 1보다 큰 경우 1보다 늘어난 숫자만큼 출력 문자열 수 가감함.
        int displayRight22BpLength = 9;
        String displayRight22Bp = right22Bp;
        // 처음부터 지정글자수까지 출력
        /*if(!StringUtils.isEmpty(right22Bp) && right22Bp.length() > displayRight22BpLength) {
            for(int i = 0; i < right22Bp.length(); i++) {
                if( i <= (displayRight22BpLength - 1)) {
                    displayRight22Bp += right22Bp.substring(i, i + 1);
                }
            }
        }*/

        // 변이 유형이 "deletion"인 경우 삭제된 염기서열 문자열 분리
        String notDeletionRef = "";
        String deletionRef = "";
        if("del".equals(transcriptAltType)) {
            notDeletionRef = alt;
            deletionRef = ref.substring(alt.length(), ref.length());
        } else if("complex".equals(transcriptAltType)) {
            deletionRef = ref;
        } else {
            notDeletionRef = ref;
        }

        if(!StringUtils.isEmpty(displayLeft22Bp)) {
            leftVbox.setPrefWidth(170);
        }

        // 값 화면 출력
        genePositionStartLabel.setText(genePositionStart);
        left22BpLabel.setText(displayLeft22Bp.toUpperCase());
        transcriptRefLabel.setText(notDeletionRef);
        deletionRefLabel.setText(deletionRef);
        right22BpLabel.setText(displayRight22Bp.toUpperCase());

        double textLength = (double)(displayLeft22Bp.length() + ref.length() + displayRight22Bp.length());
        logger.info("text length : " + textLength);

        if(textLength > 21) {
            gridBox.setPrefWidth(textLength * 12);
            sequenceCharsBox.setStyle("-fx-padding:-10 0 0 20;");
            scrollBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }

        transcriptAltLabel.setText(alt);
        transcriptAltTypeLabel.setText(transcriptAltType);
    }

    /**
     * 주요 기관 발현 빈도수(Population Frequencies) 그래프 화면 출력
     */
    public void showPopulationFrequency() {
        //double populationFrequencyESP6500 = getPopulationFrequencyByParam("ESP6500", "ALL");
        double populationFrequencyESP6500 = (variant.getSnpInDel().getPopulationFrequency().getEsp6500() != null) ? variant.getSnpInDel().getPopulationFrequency().getEsp6500().doubleValue() : -1d;
        addPopulationFrequencyGraph(0, 0, "ESP6500 ", populationFrequencyESP6500);

        //double populationFrequency1000Genomes = getPopulationFrequencyByParam("1000_genomes", "ALL");
        double populationFrequency1000Genomes = (variant.getSnpInDel().getPopulationFrequency().getG1000() != null) ?variant.getSnpInDel().getPopulationFrequency().getG1000().doubleValue() : -1d;
        addPopulationFrequencyGraph(0, 1, "1KG ", populationFrequency1000Genomes);

        //double populationFrequencyExAC = getPopulationFrequencyByParam("ExAC", "ALL");
        double populationFrequencyExAC = (variant.getSnpInDel().getPopulationFrequency().getExac() != null) ? variant.getSnpInDel().getPopulationFrequency().getExac().doubleValue() : -1d;
        addPopulationFrequencyGraph(1, 0, "ExAC ", populationFrequencyExAC);

        //double populationFrequencyKorean = getPopulationFrequencyByParam("Korean_exome", "ALL");
        double populationFrequencyKorean = (variant.getSnpInDel().getPopulationFrequency().getKoreanExomInformationDatabase() != null)
                ? variant.getSnpInDel().getPopulationFrequency().getKoreanExomInformationDatabase().doubleValue() : -1d;
        addPopulationFrequencyGraph(1, 1, "Korean ", populationFrequencyKorean);
    }

    /**
     * 주요 기관 발현 빈도수(Population Frequencies) 그래프 화면 추가
     * @param title
     * @param percentage
     */
    public void addPopulationFrequencyGraph(int row, int col, String title, double percentage) {
        double graphPercentage = percentage * 100;
        HBox hBox = new HBox();
        hBox.getStyleClass().add("population_frequency_graph");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("title");
        titleLabel.setTooltip(new Tooltip(title));

        HBox graphHBox = new HBox();
        graphHBox.getStyleClass().add("horizon_stick");
        Label graphLabel = new Label("");
        graphHBox.getChildren().add(graphLabel);
        Label percentLabel = new Label((percentage > -1) ? String.format("%.2f", percentage) : "");
        percentLabel.getStyleClass().add("percent");
        hBox.getChildren().setAll(titleLabel, graphHBox, percentLabel);
        // 퍼센트 데이터가 없는 경우(-1) disable 처리함.
        if(percentage < 0) {
            hBox.setDisable(true);
        }
        double graphHBoxWidth = 75.0;
        //populationFrequencyGraphVBox.getChildren().add(hBox);
        populationFrequencyGraphGridPane.add(hBox, col, row);
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

//		logger.info(String.format("run count : %s, total : %s", variantFrequencyRunCount, variantFrequencyRunTotalCount));
//		logger.info(String.format("panel count : %s, total : %s", variantFrequencyPanelCount, variantFrequencyPanelTotalCount));
//		logger.info(String.format("account count : %s, total : %s", variantFrequencyAccountCount, variantFrequencyAccountTotalCount));
//		logger.info(String.format("run : %s, panel : %s, account : %s", variantFrequencyRun, variantFrequencyPanel, variantFrequencyAccount));

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

    /**
     * Variant Frequency 게이지 그래프 마우스 over 이벤트
     */
    @FXML
    public void showFrequencyPercentage(MouseEvent event) {
        int count = 0;
        int totalCount = 0;
        double percent = 0.0;
        VBox box = (VBox) event.getSource();
        if("variantFrequencyRunVBox".equals(box.getId())) {
            count = (paramMap.containsKey("sameVariantSampleCountInRun")) ? (int) paramMap.get("sameVariantSampleCountInRun") : 0;
            totalCount = (paramMap.containsKey("totalSampleCountInRun")) ? (int) paramMap.get("totalSampleCountInRun") : 0;
            percent = ((double) count/(double) totalCount)*100;
            variantFrequencyRunPercentLabel.setText(String.format("%.1f", percent) + "%");
        } else if("variantFrequencyPanelVBox".equals(box.getId())) {
            count = (paramMap.containsKey("samePanelSameVariantSampleCountInUsergroup")) ? (int) paramMap.get("samePanelSameVariantSampleCountInUsergroup") : 0;
            totalCount = (paramMap.containsKey("totalSamePanelSampleCountInUsergroup")) ? (int) paramMap.get("totalSamePanelSampleCountInUsergroup") : 0;
            percent = ((double) count/(double) totalCount)*100;
            variantFrequencyPanelPercentLabel.setText(String.format("%.1f", percent) + "%");
        } else if("variantFrequencyAccountVBox".equals(box.getId())) {
            count = (paramMap.containsKey("sameVariantSampleCountInUsergroup")) ? (int) paramMap.get("sameVariantSampleCountInUsergroup") : 0;
            totalCount = (paramMap.containsKey("totalSampleCountInUsergroup")) ? (int) paramMap.get("totalSampleCountInUsergroup") : 0;
            percent = ((double) count/(double) totalCount)*100;
            variantFrequencyAccountPercentLabel.setText(String.format("%.1f", percent) + "%");
        }
    }

    /**
     * Variant Frequency 게이지 그래프 마우스 out 이벤트
     */
    @FXML
    public void showFrequencyCount(MouseEvent event) {
        int count = 0;
        int totalCount = 0;
        VBox box = (VBox) event.getSource();
        if("variantFrequencyRunVBox".equals(box.getId())) {
            count = (paramMap.containsKey("sameVariantSampleCountInRun")) ? (int) paramMap.get("sameVariantSampleCountInRun") : 0;
            totalCount = (paramMap.containsKey("totalSampleCountInRun")) ? (int) paramMap.get("totalSampleCountInRun") : 0;
            variantFrequencyRunPercentLabel.setText(count + "/" + totalCount);
        } else if("variantFrequencyPanelVBox".equals(box.getId())) {
            count = (paramMap.containsKey("samePanelSameVariantSampleCountInUsergroup")) ? (int) paramMap.get("samePanelSameVariantSampleCountInUsergroup") : 0;
            totalCount = (paramMap.containsKey("totalSamePanelSampleCountInUsergroup")) ? (int) paramMap.get("totalSamePanelSampleCountInUsergroup") : 0;
            variantFrequencyPanelPercentLabel.setText(count + "/" + totalCount);
        } else if("variantFrequencyAccountVBox".equals(box.getId())) {
            count = (paramMap.containsKey("sameVariantSampleCountInUsergroup")) ? (int) paramMap.get("sameVariantSampleCountInUsergroup") : 0;
            totalCount = (paramMap.containsKey("totalSampleCountInUsergroup")) ? (int) paramMap.get("totalSampleCountInUsergroup") : 0;
            variantFrequencyAccountPercentLabel.setText(count + "/" + totalCount);
        }
    }

    public Map<String, Object> returnResultsAfterSearch(String key) {
        List<SnpInDelExtraInfo> detail = (List<SnpInDelExtraInfo>)paramMap.get("detail");

        Optional<SnpInDelExtraInfo> populationFrequency = detail.stream().filter(item
                -> key.equalsIgnoreCase(item.key)).findFirst();

        if(populationFrequency.isPresent()) {
            return JsonUtil.fromJsonToMap(populationFrequency.get().value);
        }

        return null;
    }

    /**
     * SIGNIFICANT 레이더 차트 화면 출력
     */
    @SuppressWarnings("unchecked")
    public void showClinicalSignificantGraph() {
        Map<String,Object> inSilicoPredictionMap = returnResultsAfterSearch("in_silico_prediction");
        Map<String,Object> variantClassifierMap = returnResultsAfterSearch("variant_classifier");
        Map<String,Object> clinicalMap = returnResultsAfterSearch("clinical");
        Map<String,Object> breastCancerInformationCoreMap = returnResultsAfterSearch("breast_cancer_information_core");
        Map<String,Object> siftMap = (inSilicoPredictionMap != null && inSilicoPredictionMap.containsKey("SIFT")) ? (Map<String,Object>) inSilicoPredictionMap.get("SIFT") : null;
        Map<String,Object> polyphenMap = (inSilicoPredictionMap != null && inSilicoPredictionMap.containsKey("PolyPhen2")) ? (Map<String,Object>) inSilicoPredictionMap.get("PolyPhen2") : null;
        Map<String,Object> mtMap = (inSilicoPredictionMap != null && inSilicoPredictionMap.containsKey("mt")) ? (Map<String,Object>) inSilicoPredictionMap.get("mt") : null;
        Map<String,Object> enigmaMap = returnResultsAfterSearch("ENIGMA");

        // SIFT
        double siftValue = -1;
        // POLYPHEN2
        double polyphenValue = -1;
        double mtValue = -1;
        String siftText = "";
        String polyphenText = "";
        String mtText = "";
        String siftScore = null;
        String polyphenScore = null;

        // BIC
        if(breastCancerInformationCoreMap != null) {
            renderClinicalPathogenicityData(pathogenicityBicHBox, "BIC", (String) breastCancerInformationCoreMap.get("radar"));
        } else {
            renderClinicalPathogenicityData(pathogenicityBicHBox, "BIC", null);
        }
        // CLINVAR
        if(clinicalMap != null) {
            renderClinicalPathogenicityData(pathogenicityClinVarHBox, "CLINVAR", (String) clinicalMap.get("radar"));
        } else {
            renderClinicalPathogenicityData(pathogenicityClinVarHBox, "CLINVAR", null);
        }
        // ENIGMA
        if(enigmaMap != null) {
            renderClinicalPathogenicityData(pathogenicityEnigmaHBox, "ENIGMA", (String) enigmaMap.get("radar"));
        } else {
            renderClinicalPathogenicityData(pathogenicityEnigmaHBox, "ENIGMA", null);
        }
        // PREDICTION
        if(variantClassifierMap != null) {
            renderClinicalPathogenicityData(pathogenicityPredictionHBox, "PREDICTION", (String) variantClassifierMap.get("radar"));
        } else {
            renderClinicalPathogenicityData(pathogenicityPredictionHBox, "PREDICTION", null);
        }
        // SIFT
        if (siftMap != null && !siftMap.isEmpty()) {
            if (siftMap.containsKey("score")) {
                siftScore = (String)siftMap.get("score");
                if (siftScore != null && !siftScore.trim().isEmpty() ) {
                    siftScore = siftScore.trim();
                    try {
                        siftValue = 1.0 - Double.valueOf(siftScore);
                    } catch (NumberFormatException e) {
                        logger.warn("sift score value is invalid " + siftScore);
                        siftValue = -1.0;
                    }
                } else {
                    logger.warn("sift score value is null");
                    siftValue = -1.0;
                }
            } else if (siftMap.containsKey("radar")) {
                siftValue = convertRadarItemPercentageByLevelForPathogenic(checkType(siftMap.get("radar"))) / 100.0;
                // clinicalSignificantPathogenicitySiftLabel.setTooltip(new
                // Tooltip((String) siftMap.get("radar")));
            } else {
                logger.warn("sift score or radar value was not found.");
                siftValue = -1.0;
            }
            if (siftMap.containsKey("text") && siftMap.get("text") != null) {
                siftText = (String)siftMap.get("text");
            }
        }
        // POLYPHEN2
        if (polyphenMap != null && !polyphenMap.isEmpty()) {
            if (polyphenMap.containsKey("score")) {
                polyphenScore = (String)polyphenMap.get("score");
                if (polyphenScore != null && !polyphenScore.trim().isEmpty() ) {
                    polyphenScore = polyphenScore.trim();
                    try {
                        polyphenValue = Double.valueOf(polyphenScore);
                    } catch (NumberFormatException e) {
                        logger.warn("polyphen score value is invalid " + polyphenScore);
                        polyphenValue = -1.0;
                    }
                } else {
                    logger.warn("polyphen value is null");
                    polyphenValue = -1.0;
                }
            } else if (polyphenMap.containsKey("radar")) {
                polyphenValue = convertRadarItemPercentageByLevelForPathogenic(checkType(polyphenMap.get("radar"))) / 100.0;
                // clinicalSignificantPathogenicitySiftLabel.setTooltip(new
                // Tooltip((String) siftMap.get("radar")));
            } else {
                logger.warn("polyphen score or radar value was not found.");
                polyphenValue = -1.0;
            }
            if (polyphenMap.containsKey("text") && polyphenMap.get("text") != null) {
                polyphenText = (String)polyphenMap.get("text");
            }
        }
        // POLYPHEN2
        if (mtMap != null && !mtMap.isEmpty()) {
            if (mtMap.containsKey("radar")) {
                mtValue = convertRadarItemPercentageByLevelForPathogenic(checkType(mtMap.get("radar"))) / 100.0;
                // clinicalSignificantPathogenicitySiftLabel.setTooltip(new
                // Tooltip((String) siftMap.get("radar")));
            } else {
                logger.warn("mt score or radar value was not found.");
                mtValue = -1.0;
            }
            if (mtMap.containsKey("text") && mtMap.get("text") != null) {
                mtText = (String)mtMap.get("text");
            }
        }
        AnimationTimer siftTimer = new ClinicalSignificantTimer(
                siftCanvas.getGraphicsContext2D(), siftValue, "SIFT", siftText, this.gaugeSpeed);
        AnimationTimer polyphenTimer = new ClinicalSignificantTimer(
                polyphenCanvas.getGraphicsContext2D(), polyphenValue, "MetaSVM", polyphenText, this.gaugeSpeed);
        AnimationTimer mtTimer = new ClinicalSignificantTimer(
                mtCanvas.getGraphicsContext2D(), mtValue, "MUTATIONTASTER", mtText, this.gaugeSpeed);
        siftTimer.start();
        polyphenTimer.start();
        mtTimer.start();
        AnimationTimer siftTimer1 = new ClinicalSignificantTimer(
                siftCanvas.getGraphicsContext2D(), siftValue, "SIFT", String.format("%7s", siftScore), this.gaugeSpeed);
        AnimationTimer polyphenTimer1 = new ClinicalSignificantTimer(
                polyphenCanvas.getGraphicsContext2D(), polyphenValue, "POLYPHEN2", String.format("%7s", polyphenScore), this.gaugeSpeed);
        AnimationTimer mtTimer1 = new ClinicalSignificantTimer(
                mtCanvas.getGraphicsContext2D(), mtValue, "MUTATIONTASTER", mtText, this.gaugeSpeed);
        siftCanvas.setOnMouseEntered(event -> siftTimer1.start());
        siftCanvas.setOnMouseExited(event -> siftTimer.start());
        polyphenCanvas.setOnMouseEntered(event -> polyphenTimer1.start());
        polyphenCanvas.setOnMouseExited(event -> polyphenTimer.start());
        mtCanvas.setOnMouseEntered(event -> mtTimer1.start());
        mtCanvas.setOnMouseExited(event -> mtTimer.start());
//		SNPsINDELsOverviewRadarGraph significantRadarGraph = new SNPsINDELsOverviewRadarGraph(significantRadarGraphArea,
//				significantGraphPolyline, significantPercent0, significantPercent1, significantPercent2,
//				significantPercent3, significantPercent4, significantPercent5);
//		significantRadarGraph.display();
        double[] frequencyValue = new double[6];
        String[] frequencyValueText = new String[6];
        // 1KG SAS
        frequencyValue[0] = getPopulationFrequencyByParam("1000_genomes", "East_Asian");
        // 1KG EAS
        frequencyValue[1] = getPopulationFrequencyByParam("1000_genomes", "South_Asian");
        // ExAC NFE
        frequencyValue[2] = getPopulationFrequencyByParam("ExAC", "Europena(Non-Finnish)");
        // ExAC EAS
        frequencyValue[3] = getPopulationFrequencyByParam("ExAC", "East_Asian");
        // 1KG SAS
        frequencyValue[4] = getPopulationFrequencyByParam("ExAC", "South_Asian");
        // 1KG EUR
        frequencyValue[5] = getPopulationFrequencyByParam("1000_genomes", "European");
        for(int i = 0; i < frequencyValue.length; i++){
            if (frequencyValue[i] < 0) {
                frequencyValue[i] = 0;
                frequencyValueText[i] = "N/A";
            } else {
                frequencyValueText[i] = String.format("%.2f", frequencyValue[i]);
            }
        }
        frequencies0ValueLabel.setText(frequencyValueText[0]);
        frequencies1ValueLabel.setText(frequencyValueText[1]);
        frequencies2ValueLabel.setText(frequencyValueText[2]);
        frequencies3ValueLabel.setText(frequencyValueText[3]);
        frequencies4ValueLabel.setText(frequencyValueText[4]);
        frequencies5ValueLabel.setText(frequencyValueText[5]);
        //frequencies0Label.setTooltip(new Tooltip(String.format("%f", frequenciesPercent0) + "%"));
        //frequencies1Label.setTooltip(new Tooltip(String.format("%f", frequenciesPercent1) + "%"));
        //frequencies2Label.setTooltip(new Tooltip(String.format("%f", frequenciesValue2) + "%"));
        //frequencies3Label.setTooltip(new Tooltip(String.format("%f", frequenciesValue3) + "%"));
        //frequencies4Label.setTooltip(new Tooltip(String.format("%f", frequenciesValue4) + "%"));
        //frequencies5Label.setTooltip(new Tooltip(String.format("%f", frequenciesValue5) + "%"));
        SNPsINDELsOverviewRadarGraph frequenciesRadarGraph = new SNPsINDELsOverviewRadarGraph(
                frequenciesRadarGraphArea,
                frequenciesGraphPolyline,
                frequencyValue[0] * 100,
                frequencyValue[1] * 100,
                frequencyValue[2] * 100,
                frequencyValue[3] * 100,
                frequencyValue[4] * 100,
                frequencyValue[5] * 100);
        frequenciesRadarGraph.display();
    }

    private String checkType(Object obj) {
        if(obj instanceof Integer) {
            return String.valueOf(obj);
        }
        return (String) obj;
    }

    private void renderClinicalPathogenicityData(HBox node, String text, String value) {
        int level = 0;
        if (!StringUtils.isEmpty(value)) {
            try {
                level = Integer.valueOf(value);
            } catch(NumberFormatException nfe){
                nfe.printStackTrace();
            }
        }
        if (node == null) return;
        ObservableList<Node> childs = node.getChildren();
        if (childs != null) {
            for(Node child : childs){
                child.getStyleClass().removeAll(child.getStyleClass());
                if (((Label)child).getText().equals("P") && level  == 5) {
                    child.getStyleClass().add("prediction_A");
                    addClickEvent(text, child);
                } else if(((Label)child).getText().equals("LP") && level == 4) {
                    child.getStyleClass().add("prediction_B");
                    addClickEvent(text, child);
                } else if(((Label)child).getText().equals("US") && level == 3) {
                    child.getStyleClass().add("prediction_C");
                    addClickEvent(text, child);
                } else if(((Label)child).getText().equals("LB") && level == 2) {
                    child.getStyleClass().add("prediction_D");
                    addClickEvent(text, child);
                } else if(((Label)child).getText().equals("B") && level == 1) {
                    child.getStyleClass().add("prediction_E");
                    addClickEvent(text, child);
                } else if(((Label)child).getText().equals(text)) {
                    child.getStyleClass().add("clinical_significant_pathogenicity_label");
                } else {
                    child.getStyleClass().add("prediction_none");
                }
            }
        }
    }

    /**
     * 지정 기관의 지정 지역의 Population Frequency 정보 반환
     * @param orgKey
     * @param location
     * @return
     */
    @SuppressWarnings("unchecked")
    public double getPopulationFrequencyByParam(String orgKey, String location) {
        double percentage = -1d;
        Map<String,Object> populationFrequencyMap = returnResultsAfterSearch("population_frequency");
        if(!populationFrequencyMap.isEmpty() && populationFrequencyMap.containsKey(orgKey)) {
            Map<String,Object> orgMap = (Map<String,Object>) populationFrequencyMap.get(orgKey);
            if(!orgMap.isEmpty() && orgMap.containsKey(location)) {
                Map<String,Object> locationMap = (Map<String,Object>) orgMap.get(location);
                if(!locationMap.isEmpty() && locationMap.containsKey("allele_frequency")) {
                    if(!StringUtils.isEmpty(locationMap.get("allele_frequency").toString())) {
                        percentage = (double) locationMap.get("allele_frequency");
                    }
                }
            }
        }
        return percentage;
    }

    /**
     * Pathgenic Radar 차트 레벨에 따른 출력 퍼센트로 변환 반환
     * @param level
     * @return
     */
    public double convertRadarItemPercentageByLevelForPathogenic(String level) {
        if(StringUtils.isEmpty(level)) {
            return -1d;
        } else if("1".equals(level)) {
            return 20d;
        } else if("2".equals(level)) {
            return 40d;
        } else if("3".equals(level)) {
            return 60d;
        } else if("4".equals(level)) {
            return 80d;
        } else if("5".equals(level)) {
            return 100d;
        }
        return 4d;
    }

    public int getTranscriptComboBoxSelectedIndex() {
        return transcriptComboBox.getSelectionModel().getSelectedIndex();
    }

    @SuppressWarnings("unchecked")
    public void addClickEvent(String text, Node node) {
        if("PREDICTION".equals(text)) {
            Map<String, Object> acmg = returnResultsAfterSearch("acmg");
            if(acmg == null) return;
            node.setStyle("-fx-cursor:hand;");
            node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> openPopOver((Label) node, acmg));
        }
    }

    @SuppressWarnings("unchecked")
    public void openPopOver(Label label, Map<String, Object> acmg) {

        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        popOver.setHeaderAlwaysVisible(true);
        popOver.setAutoHide(true);
        popOver.setAutoFix(true);
        popOver.setDetachable(true);
        popOver.setArrowSize(15);
        popOver.setMaxSize(460, 150);
        popOver.setPrefWidth(460);
        popOver.setMinSize(460, 150);
        //popOver.setMinWidth(500);
        popOver.setTitle("List of evidence in 28 criteria");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setMaxSize(460, 200);

        VBox box = new VBox();
        //box.setMaxSize(400, 200);
        box.setMaxWidth(445);
        box.getStyleClass().add("acmg_content_box");

        scrollPane.setContent(box);

        String[] results = acmg.containsKey("rules") ? ((String)acmg.get("rules")).split(",") : null;
        String rulesText = acmg.containsKey("rules") ? "(" + acmg.get("rules") + ")" : null;

        Label reason = new Label();
        String pathogenicity = acmg.containsKey("pathogenicity") ? (String)acmg.get("pathogenicity") : null;
        reason.setText(pathogenicity + rulesText);
        if("benign".equalsIgnoreCase(pathogenicity)) {
            reason.getStyleClass().add("benign");
        } else if("likely benign".equalsIgnoreCase(pathogenicity)) {
            reason.getStyleClass().add("likely_benign");
        } else if("uncertain significance".equalsIgnoreCase(pathogenicity)) {
            reason.getStyleClass().add("uncertain_significance");
        } else if("likely pathogenic".equalsIgnoreCase(pathogenicity)) {
            reason.getStyleClass().add("likely_pathogenic");
        } else if("pathogenic".equalsIgnoreCase(pathogenicity)) {
            reason.getStyleClass().add("pathogenic");
        }
        box.getChildren().add(reason);

        for(String result : results) {
            Map<String, Object> role = (Map<String, Object>) acmg.get(result);

            Label roleLabel = new Label(result);
            roleLabel.setMaxWidth(50);
            roleLabel.setWrapText(true);
            roleLabel.getStyleClass().add("acmg_content_role_label");
            if(result.startsWith("PVS")) {
                roleLabel.getStyleClass().add("acmg_PVS");
            } else if(result.startsWith("PS")) {
                roleLabel.getStyleClass().add("acmg_PS");
            } else if(result.startsWith("PM")) {
                roleLabel.getStyleClass().add("acmg_PM");
            } else if(result.startsWith("PP")) {
                roleLabel.getStyleClass().add("acmg_PP");
            } else if(result.startsWith("BP")) {
                roleLabel.getStyleClass().add("acmg_BP");
            } else if(result.startsWith("BS")) {
                roleLabel.getStyleClass().add("acmg_BS");
            } else if(result.startsWith("BA")) {
                roleLabel.getStyleClass().add("acmg_BA");
            }
            box.getChildren().add(roleLabel);

            Label descLabel = new Label();
            String desc = role.containsKey("desc") ? (String)role.get("desc") : null;
            descLabel.setWrapText(true);
            descLabel.setText(desc);
            descLabel.getStyleClass().add("acmg_content_desc_label");
            descLabel.setMaxWidth(460);
            box.getChildren().add(descLabel);

            String massage = role.containsKey("message") ? (String)role.get("message") : null;
            if(!StringUtils.isEmpty(massage)) {
                Label msgLabel = new Label();
                msgLabel.setWrapText(true);
                msgLabel.setText("("+massage+")");
                msgLabel.getStyleClass().add("acmg_content_msg_label");
                msgLabel.setMaxWidth(460);
                box.getChildren().add(msgLabel);
            }
        }

        popOver.setContentNode(scrollPane);
        popOver.show(label, 10);
    }
}
