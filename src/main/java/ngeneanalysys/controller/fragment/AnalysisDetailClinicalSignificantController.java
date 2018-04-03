package ngeneanalysys.controller.fragment;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polyline;
import ngeneanalysys.animaition.ClinicalSignificantTimer;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.AnalysisDetailSNVController;
import ngeneanalysys.controller.ChangePathogenicityController;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.SnpInDelExtraInfo;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import ngeneanalysys.model.render.SNPsINDELsOverviewRadarGraph;
import ngeneanalysys.util.JsonUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jang
 * @since 2018-03-21
 */
public class AnalysisDetailClinicalSignificantController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private final int gaugeSpeed = 10;

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

    @FXML private Button pathogenic5;
    @FXML private Button pathogenic4;
    @FXML private Button pathogenic3;
    @FXML private Button pathogenic2;
    @FXML private Button pathogenic1;

    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;

    private AnalysisDetailSNVController controller;

    /**
     * @param controller
     */
    public void setController(AnalysisDetailSNVController controller) {
        this.controller = controller;
    }

    /**
     * @param selectedAnalysisResultVariant
     */
    public void setSelectedAnalysisResultVariant(VariantAndInterpretationEvidence selectedAnalysisResultVariant) {
        this.selectedAnalysisResultVariant = selectedAnalysisResultVariant;
    }

    @Override
    public void show(Parent root) throws IOException {
        showClinicalSignificantGraph();
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
                polyphenCanvas.getGraphicsContext2D(), polyphenValue, "MetaSVM", String.format("%7s", polyphenScore), this.gaugeSpeed);
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

    public Map<String, Object> returnResultsAfterSearch(String key) {
        List<SnpInDelExtraInfo> detail = (List<SnpInDelExtraInfo>)paramMap.get("detail");

        Optional<SnpInDelExtraInfo> populationFrequency = detail.stream().filter(item
                -> key.equalsIgnoreCase(item.key)).findFirst();

        if(populationFrequency.isPresent()) {
            return JsonUtil.fromJsonToMap(populationFrequency.get().value);
        }

        return null;
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

    @FXML
    public void setGermlineFlag(ActionEvent event) {
        Button actionButton = (Button) event.getSource();
        String value = null;
        if (actionButton == pathogenic5) {
            value = "P";
        } else if (actionButton == pathogenic4) {
            value = "LP";
        } else if (actionButton == pathogenic3) {
            value = "US";
        } else if (actionButton == pathogenic2) {
            value = "LB";
        } else if (actionButton == pathogenic1) {
            value = "B";
        }

        if((selectedAnalysisResultVariant.getSnpInDel().getExpertPathogenicity() == null && !selectedAnalysisResultVariant.getSnpInDel().getSwPathogenicity().equals(value))
                || (selectedAnalysisResultVariant.getSnpInDel().getExpertPathogenicity() != null &&!selectedAnalysisResultVariant.getSnpInDel().getExpertPathogenicity().equals(value))) {
            try {
                FXMLLoader loader = mainApp.load(FXMLConstants.CHANGE_PATHOGENICITY);
                Node root = loader.load();
                ChangePathogenicityController changePathogenicityController = loader.getController();
                changePathogenicityController.setMainController(this.getMainController());
                changePathogenicityController.settingTier(value, selectedAnalysisResultVariant);
                changePathogenicityController.show((Parent) root);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            controller.showVariantList(controller.getCurrentPageIndex() + 1,0);
        }
    }

}
