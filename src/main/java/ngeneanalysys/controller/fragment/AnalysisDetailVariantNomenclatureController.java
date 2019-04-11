package ngeneanalysys.controller.fragment;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.SnpInDelTranscript;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Jang
 * @since 2018-03-21
 */
public class AnalysisDetailVariantNomenclatureController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

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
    private ScrollPane transcriptDetailScrollBox;
    @FXML
    private GridPane transcriptDetailGrid;
    @FXML
    private VBox leftVbox;

    private VariantAndInterpretationEvidence variant;

    @Override
    public void show(Parent root) throws IOException {
        variant = (VariantAndInterpretationEvidence)paramMap.get("variant");
        Platform.runLater(this::showVariantIdentification);
    }

    @SuppressWarnings("unchecked")
    private List<SnpInDelTranscript> getTranscript() {
        try {
            APIService apiService = APIService.getInstance();
            HttpClientResponse response = apiService.get("/analysisResults/snpInDels/" + variant.getSnpInDel().getId() + "/snpInDelTranscripts", null, null, false);
            return (List<SnpInDelTranscript>) response.getMultiObjectBeforeConvertResponseToJSON(SnpInDelTranscript.class, false);
        } catch (WebAPIException wae) {
            return Collections.emptyList();
        }
    }
    /**
     * Variant Nomenclature 값 설정 및 화면 출력
     */
    private void showVariantIdentification() {
        List<SnpInDelTranscript> transcriptDataList = getTranscript();

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
                if(!oldIdx.equals(newIdx)) {
                    Optional<SnpInDelTranscript> transcriptOptional = transcriptDataList.stream().filter(item ->
                            item.getTranscriptId().equals(transcriptComboBox.getItems().get((int)newIdx))).findFirst();
                    if(transcriptOptional.isPresent()) {
                        SnpInDelTranscript transcript = transcriptOptional.get();
                        String geneSymbol = (!StringUtils.isEmpty(transcript.getGeneSymbol())) ? transcript.getGeneSymbol() : "N/A";
                        String transcriptName = (!StringUtils.isEmpty(transcript.getTranscriptId())) ? transcript.getTranscriptId() : "N/A";
                        String codingDNA = (!StringUtils.isEmpty(transcript.getCodingDna())) ? transcript.getCodingDna() : "N/A";
                        String protein = (!StringUtils.isEmpty(transcript.getProtein())) ? transcript.getProtein() : "N/A";
                        String genomicDNA = (!StringUtils.isEmpty(transcript.getGenomicDna())) ? transcript.getGenomicDna() : "N/A";
                        initNomenclature(transcript);
                        logger.debug(String.format("variant identification choose '%s' option idx [%s]", transcriptName, newIdx));
                        List<Integer> textLength = new ArrayList<>();
                        setTextField(geneSymbolTextField, geneSymbol, textLength);
                        setTextField(hgvspTextField, protein, textLength);
                        setTextField(hgvscTextField, codingDNA, textLength);
                        setTextField(grch37TextField, genomicDNA, textLength);
                        Optional<Integer> maxTextLengthOptional = textLength.stream().max(Integer::compare);
                        maxTextLengthOptional.ifPresent(value -> {
                            if(value > 24) {
                                transcriptDetailGrid.setPrefWidth(95 + (8 + value * 7.4));
                                transcriptDetailScrollBox.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                                transcriptDetailScrollBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                            }
                        });
                    } else {
                        initNomenclature(null);
                    }
                }
            });

            // 첫번째 아이템 선택 처리
            transcriptComboBox.getSelectionModel().select(defaultTranscript);
        } else {
            initNomenclature(null);
        }
    }

    private void initNomenclature(SnpInDelTranscript snpInDelTranscript) {
        String ref;
        String alt;
        String left22Bp;
        String right22Bp;
        if(snpInDelTranscript == null || snpInDelTranscript.getLeftSequence() == null) {
            ref = variant.getSnpInDel().getSnpInDelExpression().getRefSequence();
            alt = variant.getSnpInDel().getSnpInDelExpression().getAltSequence();
            left22Bp = variant.getSnpInDel().getSnpInDelExpression().getLeftSequence();
            right22Bp = variant.getSnpInDel().getSnpInDelExpression().getRightSequence();
        } else {
            ref = snpInDelTranscript.getRefSequence();
            alt = snpInDelTranscript.getAltSequence();
            left22Bp = snpInDelTranscript.getLeftSequence();
            right22Bp = snpInDelTranscript.getRightSequence();
        }

        String genePositionStart = String.valueOf(variant.getSnpInDel().getGenomicCoordinate().getStartPosition());
        String transcriptAltType = variant.getSnpInDel().getSnpInDelExpression().getVariantType();

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

        if(!StringUtils.isEmpty(left22Bp)) {
            leftVbox.setPrefWidth(170);
        }

        // 값 화면 출력
        genePositionStartLabel.setText(genePositionStart);
        left22BpLabel.setText(left22Bp);
        transcriptRefLabel.setText(notDeletionRef);
        deletionRefLabel.setText(deletionRef);
        right22BpLabel.setText(right22Bp);

        double textLength = (double)(left22Bp.length() + ref.length() + right22Bp.length());
        logger.debug("text length : " + textLength);

        if(alt.length() > 21 && (textLength - left22Bp.length()) < alt.length()){
            setScrollBoxSize(alt.length() - 21);
        } else if(textLength > 31) {
            setScrollBoxSize(textLength - 31);
        }

        transcriptAltLabel.setText(alt);
        transcriptAltTypeLabel.setText(transcriptAltType);
    }

    private void setScrollBoxSize(double size) {
        gridBox.setPrefWidth(275 + size * 8);
        sequenceCharsBox.setStyle("-fx-padding:-10 0 0 20;");
        scrollBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    private void setTextField(TextField textField, String text, List<Integer> textLength) {
        textField.setText(text); //Gene Symbol
        if(!StringUtils.isEmpty(text)) {
            textField.setTooltip(new Tooltip(text));
            textLength.add(text.length());
        }
    }

    int getTranscriptComboBoxSelectedIndex() {
        return transcriptComboBox.getSelectionModel().getSelectedIndex();
    }

}
