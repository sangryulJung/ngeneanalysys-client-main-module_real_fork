package ngeneanalysys.controller.fragment;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import ngeneanalysys.animaition.ClinicalSignificantTimer;
import ngeneanalysys.code.enums.AnalysisTypeCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.SnpInDelExtraInfo;
import ngeneanalysys.util.JsonUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jang
 * @since 2018-04-10
 */
public class InSilicoPredictionsController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private final int gaugeSpeed = 10;

    @FXML
    private Canvas polyphenCanvas;
    @FXML
    private Canvas siftCanvas;
    @FXML
    private Canvas mtCanvas;

    @Override
    public void show(Parent root) throws IOException {
        Panel panel = (Panel)paramMap.get("panel");
        if(panel != null && panel.getAnalysisType().equalsIgnoreCase(AnalysisTypeCode.SOMATIC.getDescription())) {
          polyphenCanvas.setVisible(false);
        }
        showInSilicoPredictions();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> returnResultsAfterSearch(String key) {
        List<SnpInDelExtraInfo> detail = (List<SnpInDelExtraInfo>)paramMap.get("detail");

        Optional<SnpInDelExtraInfo> populationFrequency = detail.stream().filter(item
                -> key.equalsIgnoreCase(item.key)).findFirst();

        return populationFrequency.map(snpInDelExtraInfo -> JsonUtil.fromJsonToMap(snpInDelExtraInfo.value)).orElse(null);

    }

    /**
     * Pathgenic Radar 차트 레벨에 따른 출력 퍼센트로 변환 반환
     * @param level String
     * @return double
     */
    private double convertRadarItemPercentageByLevelForPathogenic(String level) {
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

    private String checkType(Object obj) {
        if(obj instanceof Integer) {
            return String.valueOf(obj);
        }
        return (String) obj;
    }

    @SuppressWarnings("unchecked")
    private void showInSilicoPredictions () {
        Map<String,Object> inSilicoPredictionMap = returnResultsAfterSearch("in_silico_prediction");
        Map<String,Object> siftMap = (inSilicoPredictionMap != null && inSilicoPredictionMap.containsKey("SIFT")) ? (Map<String,Object>) inSilicoPredictionMap.get("SIFT") : null;
        Map<String,Object> polyphenMap = (inSilicoPredictionMap != null && inSilicoPredictionMap.containsKey("PolyPhen2")) ? (Map<String,Object>) inSilicoPredictionMap.get("PolyPhen2") : null;
        Map<String,Object> mtMap = (inSilicoPredictionMap != null && inSilicoPredictionMap.containsKey("mt")) ? (Map<String,Object>) inSilicoPredictionMap.get("mt") : null;
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

        // SIFT
        if (siftMap != null && !siftMap.isEmpty()) {
            if (siftMap.containsKey("score")) {
                siftScore = (String)siftMap.get("score");
                if (siftScore != null && !siftScore.trim().isEmpty() ) {
                    siftScore = siftScore.trim();
                    try {
                        siftValue = 1.0 - Double.valueOf(siftScore);
                    } catch (NumberFormatException e) {
                        logger.debug("sift score value is invalid " + siftScore);
                        siftValue = -1.0;
                    }
                } else {
                    logger.debug("sift score value is null");
                    siftValue = -1.0;
                }
            } else if (siftMap.containsKey("radar")) {
                siftValue = convertRadarItemPercentageByLevelForPathogenic(checkType(siftMap.get("radar"))) / 100.0;
            } else {
                logger.debug("sift score or radar value was not found.");
                siftValue = -1.0;
            }
            if (siftMap.containsKey("text") && siftMap.get("text") != null) {
                siftText = (String)siftMap.get("text");
            }
        }
        // metaSVM
        if (polyphenMap != null && !polyphenMap.isEmpty()) {
            if (polyphenMap.containsKey("score")) {
                polyphenScore = (String)polyphenMap.get("score");
                if (polyphenScore != null && !polyphenScore.trim().isEmpty() ) {
                    polyphenScore = polyphenScore.trim();
                    try {
                        polyphenValue = Double.valueOf(polyphenScore);
                    } catch (NumberFormatException e) {
                        logger.debug("metaSVM score value is invalid " + polyphenScore);
                        polyphenValue = -1.0;
                    }
                } else {
                    logger.debug("metaSVM value is null");
                    polyphenValue = -1.0;
                }
            } else if (polyphenMap.containsKey("radar")) {
                polyphenValue = convertRadarItemPercentageByLevelForPathogenic(checkType(polyphenMap.get("radar"))) / 100.0;
            } else {
                logger.debug("metaSVM score or radar value was not found.");
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
            } else {
                logger.debug("mt score or radar value was not found.");
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
    }
}
