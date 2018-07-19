package ngeneanalysys.controller.fragment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.SnpInDelTranscript;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
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
        showVariantIdentification();
    }
    /**
     * Variant Nomenclature 값 설정 및 화면 출력
     */
    @SuppressWarnings("unchecked")
    private void showVariantIdentification() {
        List<SnpInDelTranscript> transcriptDataList = (List<SnpInDelTranscript>) paramMap.get("snpInDelTranscripts");

        String ref = variant.getSnpInDel().getSnpInDelExpression().getRefSequence();
        String alt = variant.getSnpInDel().getSnpInDelExpression().getAltSequence();
        String left22Bp = variant.getSnpInDel().getSnpInDelExpression().getLeftSequence();
        String right22Bp = variant.getSnpInDel().getSnpInDelExpression().getRightSequence();
        String genePositionStart = String.valueOf(variant.getSnpInDel().getGenomicCoordinate().getStartPosition());
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

                        logger.debug(String.format("variant identification choose '%s' option idx [%s]", transcriptName, newIdx));
                        List<Integer> textLength = new ArrayList<>();
                        setTextField(geneSymbolTextField, geneSymbol, textLength);
                        setTextField(hgvspTextField, protein, textLength);
                        setTextField(hgvscTextField, codingDNA, textLength);
                        setTextField(grch37TextField, genomicDNA, textLength);
                        Optional<Integer> maxTextLengthOptional = textLength.stream().max(Integer::compare);
                        maxTextLengthOptional.ifPresent(value -> {
                            if(value > 29) {
                                transcriptDetailGrid.setPrefWidth(value * 9);
                                geneSymbolTextField.setMinWidth(value * 9);
                                hgvspTextField.setMinWidth(value * 9);
                                hgvscTextField.setMinWidth(value * 9);
                                grch37TextField.setMinWidth(value * 9);
                                transcriptDetailScrollBox.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                                transcriptDetailScrollBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                            }
                        });


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
        logger.debug("text length : " + textLength);

        if(textLength > 21) {
            gridBox.setPrefWidth(textLength * 12);
            sequenceCharsBox.setStyle("-fx-padding:-10 0 0 20;");
            scrollBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }

        transcriptAltLabel.setText(alt);
        transcriptAltTypeLabel.setText(transcriptAltType);
    }

    public void setTextField(TextField textField, String text, List<Integer> textLength) {
        textField.setText(text); //Gene Symbol
        if(!StringUtils.isEmpty(text)) {
            textField.setTooltip(new Tooltip(text));
            textLength.add(text.length());
        }
    }

    public int getTranscriptComboBoxSelectedIndex() {
        return transcriptComboBox.getSelectionModel().getSelectedIndex();
    }

}
