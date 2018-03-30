package ngeneanalysys.controller.fragment;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ngeneanalysys.controller.AnalysisDetailSNVController;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.ClinicalEvidence;
import ngeneanalysys.model.SnpInDel;
import ngeneanalysys.model.SnpInDelInterpretation;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jang
 * @since 2018-03-19
 */
public class AnalysisDetailInterpretationController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private Label swTierLabel;
    @FXML
    private Label userTierLabel;

    @FXML
    private ComboBox<ComboBoxItem> tierComboBox;

    @FXML
    private TextField commentTextField;

    @FXML
    private TextField TherapeuticALabel;

    @FXML
    private TextField TherapeuticBLabel;

    @FXML
    private TextField TherapeuticCLabel;

    @FXML
    private TextField TherapeuticDLabel;

    @FXML
    private TextField PrognosisALabel;

    @FXML
    private TextField PrognosisBLabel;

    @FXML
    private TextField PrognosisCLabel;

    @FXML
    private TextField PrognosisDLabel;

    @FXML
    private TextField DiagnosisALabel;

    @FXML
    private TextField DiagnosisBLabel;

    @FXML
    private TextField DiagnosisCLabel;

    @FXML
    private TextField DiagnosisDLabel;

    private VariantAndInterpretationEvidence variantAndInterpretationEvidence;

    private APIService apiService;

    private AnalysisDetailSNVController analysisDetailSNVController;

    /**
     * @param analysisDetailSNVController
     */
    public void setAnalysisDetailSNVController(AnalysisDetailSNVController analysisDetailSNVController) {
        this.analysisDetailSNVController = analysisDetailSNVController;
    }

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();

        tierComboBox.setConverter(new ComboBoxConverter());
        tierComboBox.getItems().add(new ComboBoxItem("T1", "T I"));
        tierComboBox.getItems().add(new ComboBoxItem("T2", "T II"));
        tierComboBox.getItems().add(new ComboBoxItem("T3", "T III"));
        tierComboBox.getItems().add(new ComboBoxItem("T4", "T IV"));
        tierComboBox.getSelectionModel().select(0);

    }

    public void returnTierClass(String tier, Label label) {
        label.setAlignment(Pos.CENTER);
        if(!StringUtils.isEmpty(tier)) {
            if (tier.equalsIgnoreCase("T1")) {
                label.setText("I");
                label.getStyleClass().add("tier_one");
            } else if (tier.equalsIgnoreCase("T2")) {
                label.setText("I");
                label.getStyleClass().add("tier_two");
            } else if (tier.equalsIgnoreCase("T3")) {
                label.setText("I");
                label.getStyleClass().add("tier_three");
            } else if (tier.equalsIgnoreCase("T4")) {
                label.setText("I");
                label.getStyleClass().add("tier_four");
            }
        }
    }
    @FXML
    public void saveInterpretation() {
        String comment = commentTextField.getText();
        String tier = tierComboBox.getSelectionModel().getSelectedItem().getValue();
        Map<String, Object> params = new HashMap<>();

        SnpInDelInterpretation snpInDelInterpretation = new SnpInDelInterpretation();

        snpInDelInterpretation.setSnpInDelId(variantAndInterpretationEvidence.getSnpInDel().getId());
        snpInDelInterpretation.setTherapeuticEvidence(new ClinicalEvidence());
        snpInDelInterpretation.getTherapeuticEvidence().setLevelA(TherapeuticALabel.getText());
        snpInDelInterpretation.getTherapeuticEvidence().setLevelB(TherapeuticBLabel.getText());
        snpInDelInterpretation.getTherapeuticEvidence().setLevelC(TherapeuticCLabel.getText());
        snpInDelInterpretation.getTherapeuticEvidence().setLevelD(TherapeuticDLabel.getText());

        snpInDelInterpretation.setDiagnosisEvidence(new ClinicalEvidence());
        snpInDelInterpretation.getDiagnosisEvidence().setLevelA(DiagnosisALabel.getText());
        snpInDelInterpretation.getDiagnosisEvidence().setLevelB(DiagnosisBLabel.getText());
        snpInDelInterpretation.getDiagnosisEvidence().setLevelC(DiagnosisCLabel.getText());
        snpInDelInterpretation.getDiagnosisEvidence().setLevelD(DiagnosisDLabel.getText());

        snpInDelInterpretation.setPrognosisEvidence(new ClinicalEvidence());
        snpInDelInterpretation.getPrognosisEvidence().setLevelA(PrognosisALabel.getText());
        snpInDelInterpretation.getPrognosisEvidence().setLevelB(PrognosisBLabel.getText());
        snpInDelInterpretation.getPrognosisEvidence().setLevelC(PrognosisCLabel.getText());
        snpInDelInterpretation.getPrognosisEvidence().setLevelD(PrognosisDLabel.getText());

        params.put("snpInDelInterpretation", snpInDelInterpretation);

        if(!StringUtils.isEmpty(comment)) {
            params.put("comment", comment);
            try {
                params.put("tier", tier);

                params.put("snpInDelId", variantAndInterpretationEvidence.getSnpInDel().getId());

                apiService.put("analysisResults/snpInDels/" + variantAndInterpretationEvidence.getSnpInDel().getId() + "/updateTier", params, null, true);

                analysisDetailSNVController.showVariantList(analysisDetailSNVController.getCurrentPageIndex() + 1, 0, null, null);
            } catch (WebAPIException wae) {
                wae.printStackTrace();
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
            }

        }
    }

    public void setTier(SnpInDel snpInDel) {
        returnTierClass(snpInDel.getSwTier(), swTierLabel);
        returnTierClass(snpInDel.getExpertTier(), userTierLabel);
    }

    public void setLabel(VariantAndInterpretationEvidence variantAndInterpretationEvidence) {
        this.variantAndInterpretationEvidence = variantAndInterpretationEvidence;
        setTier(variantAndInterpretationEvidence.getSnpInDel());

        setNull();

        SnpInDelInterpretation interpretation = variantAndInterpretationEvidence.getInterpretationEvidence();

        if(interpretation == null) return;

        if(interpretation.getTherapeuticEvidence() != null) {
            if (!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelA()))
                TherapeuticALabel.setText(interpretation.getTherapeuticEvidence().getLevelA());
            if (!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelB()))
                TherapeuticBLabel.setText(interpretation.getTherapeuticEvidence().getLevelB());
            if (!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelC()))
                TherapeuticCLabel.setText(interpretation.getTherapeuticEvidence().getLevelC());
            if (!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelD()))
                TherapeuticDLabel.setText(interpretation.getTherapeuticEvidence().getLevelD());
        }

        if(interpretation.getPrognosisEvidence() != null) {
            if (!StringUtils.isEmpty(interpretation.getPrognosisEvidence().getLevelA()))
                PrognosisALabel.setText(interpretation.getPrognosisEvidence().getLevelA());
            if (!StringUtils.isEmpty(interpretation.getPrognosisEvidence().getLevelB()))
                PrognosisBLabel.setText(interpretation.getPrognosisEvidence().getLevelB());
            if (!StringUtils.isEmpty(interpretation.getPrognosisEvidence().getLevelC()))
                PrognosisCLabel.setText(interpretation.getPrognosisEvidence().getLevelC());
            if (!StringUtils.isEmpty(interpretation.getPrognosisEvidence().getLevelD()))
                PrognosisDLabel.setText(interpretation.getPrognosisEvidence().getLevelD());
        }

        if(interpretation.getDiagnosisEvidence() != null) {
            if (!StringUtils.isEmpty(interpretation.getDiagnosisEvidence().getLevelA()))
                DiagnosisALabel.setText(interpretation.getDiagnosisEvidence().getLevelA());
            if (!StringUtils.isEmpty(interpretation.getDiagnosisEvidence().getLevelB()))
                DiagnosisBLabel.setText(interpretation.getDiagnosisEvidence().getLevelB());
            if (!StringUtils.isEmpty(interpretation.getDiagnosisEvidence().getLevelC()))
                DiagnosisCLabel.setText(interpretation.getDiagnosisEvidence().getLevelC());
            if (!StringUtils.isEmpty(interpretation.getDiagnosisEvidence().getLevelD()))
                DiagnosisDLabel.setText(interpretation.getDiagnosisEvidence().getLevelD());
        }

    }

    private void setNull() {
        TherapeuticALabel.setText("");
        TherapeuticBLabel.setText("");
        TherapeuticCLabel.setText("");
        TherapeuticDLabel.setText("");
        PrognosisALabel.setText("");
        PrognosisBLabel.setText("");
        PrognosisCLabel.setText("");
        PrognosisDLabel.setText("");
        DiagnosisALabel.setText("");
        DiagnosisBLabel.setText("");
        DiagnosisCLabel.setText("");
        DiagnosisDLabel.setText("");
    }
}
