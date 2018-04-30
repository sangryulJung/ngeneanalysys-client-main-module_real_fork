package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.ClinicalEvidence;
import ngeneanalysys.model.SnpInDelInterpretation;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-11-28
 */
public class ChangeTierDialogController extends SubPaneController {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService = null;

    private TableView<VariantAndInterpretationEvidence> table;

    private String tier;

    private VariantAndInterpretationEvidence selectedItem = null;

    @FXML
    private ComboBox<String> clinicalVariantTypeComboBox;

    @FXML
    private TextField therapeuticEvidenceATextField;

    @FXML
    private TextField therapeuticEvidenceBTextField;

    @FXML
    private TextField therapeuticEvidenceCTextField;

    @FXML
    private TextField therapeuticEvidenceDTextField;

    @FXML
    private TextField diagnosisEvidenceATextField;

    @FXML
    private TextField diagnosisEvidenceBTextField;

    @FXML
    private TextField diagnosisEvidenceCTextField;

    @FXML
    private TextField diagnosisEvidenceDTextField;

    @FXML
    private TextField prognosisEvidenceATextField;

    @FXML
    private TextField prognosisEvidenceBTextField;

    @FXML
    private TextField prognosisEvidenceCTextField;

    @FXML
    private TextField prognosisEvidenceDTextField;

    @FXML
    private TextField commentTextField;

    @FXML
    private Label tierLabel;

    private Stage dialogStage;

    private TableRow<VariantAndInterpretationEvidence> rowItem;

    private boolean typeSomatic = true;

    public void settingItem(TableView<VariantAndInterpretationEvidence> table, String tier, VariantAndInterpretationEvidence selectedItem
            , TableRow<VariantAndInterpretationEvidence> rowItem) {
        this.table =table;
        this.tier = tier;
        this.selectedItem = selectedItem;
        this.rowItem = rowItem;
    }

    public void settingTier(String tier, VariantAndInterpretationEvidence selectedItem) {
        this.tier = tier;
        this.selectedItem = selectedItem;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");
        // Create the dialog Stage

        apiService = APIService.getInstance();

        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Change Tier");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());
        dialogStage.setResizable(false);

        String currentTier = getCurrentTier();

        tierLabel.setText(ConvertUtil.tierConvert(currentTier) + " > " + ConvertUtil.tierConvert(tier));

        clinicalVariantTypeComboBox.getItems().add("FLT3-ITD");
        clinicalVariantTypeComboBox.getItems().add("type A");
        clinicalVariantTypeComboBox.getItems().add("type B");
        clinicalVariantTypeComboBox.getItems().add("type D");
        clinicalVariantTypeComboBox.getItems().add("N-term");
        clinicalVariantTypeComboBox.getItems().add("C-term");
        clinicalVariantTypeComboBox.getItems().add("splicing");

        try {
            HttpClientResponse httpClientResponse = apiService
                    .get("analysisResults/snpInDelInterpretation/" + selectedItem.getSnpInDel().getId(), null, null, false);

            if(httpClientResponse.getStatus() == 200) {
                SnpInDelInterpretation interpretation = httpClientResponse.getObjectBeforeConvertResponseToJSON(SnpInDelInterpretation.class);
                if(!StringUtils.isEmpty(interpretation.getClinicalVariantType()))
                    clinicalVariantTypeComboBox.getSelectionModel().select(interpretation.getClinicalVariantType());
                //therapeutic
                if(!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelA()))
                    therapeuticEvidenceATextField.setText(interpretation.getTherapeuticEvidence().getLevelA());

                if(!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelB()))
                    therapeuticEvidenceBTextField.setText(interpretation.getTherapeuticEvidence().getLevelB());

                if(!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelC()))
                    therapeuticEvidenceCTextField.setText(interpretation.getTherapeuticEvidence().getLevelC());

                if(!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelD()))
                    therapeuticEvidenceDTextField.setText(interpretation.getTherapeuticEvidence().getLevelD());
                //diagnosis
                if(!StringUtils.isEmpty(interpretation.getDiagnosisEvidence().getLevelA()))
                    diagnosisEvidenceATextField.setText(interpretation.getTherapeuticEvidence().getLevelA());

                if(!StringUtils.isEmpty(interpretation.getDiagnosisEvidence().getLevelB()))
                    diagnosisEvidenceBTextField.setText(interpretation.getTherapeuticEvidence().getLevelB());

                if(!StringUtils.isEmpty(interpretation.getDiagnosisEvidence().getLevelC()))
                    diagnosisEvidenceCTextField.setText(interpretation.getTherapeuticEvidence().getLevelC());

                if(!StringUtils.isEmpty(interpretation.getDiagnosisEvidence().getLevelD()))
                    diagnosisEvidenceDTextField.setText(interpretation.getTherapeuticEvidence().getLevelD());
                //prognosis
                if(!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelA()))
                    prognosisEvidenceATextField.setText(interpretation.getPrognosisEvidence().getLevelA());

                if(!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelB()))
                    prognosisEvidenceBTextField.setText(interpretation.getPrognosisEvidence().getLevelB());

                if(!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelC()))
                    prognosisEvidenceCTextField.setText(interpretation.getPrognosisEvidence().getLevelC());

                if(!StringUtils.isEmpty(interpretation.getTherapeuticEvidence().getLevelD()))
                    prognosisEvidenceDTextField.setText(interpretation.getPrognosisEvidence().getLevelD());

            }
        } catch (WebAPIException wae) {
            wae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    @FXML
    public void ok() {
        String comment = commentTextField.getText();
        Map<String, Object> params = new HashMap<>();

        /*params.put("evidenceLevelA", evidenceATextField.getText());
        params.put("evidenceLevelB", evidenceBTextField.getText());
        params.put("evidenceLevelC", evidenceCTextField.getText());
        params.put("evidenceLevelD", evidenceDTextField.getText());

        if(clinicalVariantTypeComboBox.getSelectionModel().getSelectedItem() != null) {
            params.put("clinicalVariantType", clinicalVariantTypeComboBox.getSelectionModel().getSelectedItem());
        } else {
            params.put("clinicalVariantType", null);
        }
        params.put("dbRef", dbRefTextField.getText());
        params.put("dbAlt", dbAltTextField.getText());
        params.put("dbNtChange", dbNtChangeTextField.getText());
        params.put("dbTranscript", dbTranscriptTextField.getText());
*/
        SnpInDelInterpretation snpInDelInterpretation = new SnpInDelInterpretation();

        snpInDelInterpretation.setSnpInDelId(selectedItem.getSnpInDel().getId());
        snpInDelInterpretation.setTherapeuticEvidence(new ClinicalEvidence());
        snpInDelInterpretation.getTherapeuticEvidence().setLevelA(therapeuticEvidenceATextField.getText());
        snpInDelInterpretation.getTherapeuticEvidence().setLevelB(therapeuticEvidenceBTextField.getText());
        snpInDelInterpretation.getTherapeuticEvidence().setLevelC(therapeuticEvidenceCTextField.getText());
        snpInDelInterpretation.getTherapeuticEvidence().setLevelD(therapeuticEvidenceDTextField.getText());

        snpInDelInterpretation.setDiagnosisEvidence(new ClinicalEvidence());
        snpInDelInterpretation.getDiagnosisEvidence().setLevelA(diagnosisEvidenceATextField.getText());
        snpInDelInterpretation.getDiagnosisEvidence().setLevelB(diagnosisEvidenceBTextField.getText());
        snpInDelInterpretation.getDiagnosisEvidence().setLevelC(diagnosisEvidenceCTextField.getText());
        snpInDelInterpretation.getDiagnosisEvidence().setLevelD(diagnosisEvidenceDTextField.getText());

        snpInDelInterpretation.setPrognosisEvidence(new ClinicalEvidence());
        snpInDelInterpretation.getPrognosisEvidence().setLevelA(prognosisEvidenceATextField.getText());
        snpInDelInterpretation.getPrognosisEvidence().setLevelB(prognosisEvidenceBTextField.getText());
        snpInDelInterpretation.getPrognosisEvidence().setLevelC(prognosisEvidenceCTextField.getText());
        snpInDelInterpretation.getPrognosisEvidence().setLevelD(prognosisEvidenceDTextField.getText());
        snpInDelInterpretation.setClinicalVariantType(clinicalVariantTypeComboBox.getSelectionModel().getSelectedItem());

        params.put("snpInDelInterpretation", snpInDelInterpretation);

        if(!StringUtils.isEmpty(comment)) {
            params.put("comment", comment);
            try {
                if(typeSomatic) {
                    params.put("tier", tier);
                } else {
                    params.put("tier", tier);
                }
                //params.put("snpInDelId", selectedItem.getSnpInDel().getId());

                apiService.put("analysisResults/snpInDels/" + selectedItem.getSnpInDel().getId() + "/updateTier", params, null, true);
            } catch (WebAPIException wae) {
                wae.printStackTrace();
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
            }

            dialogStage.close();
        }
    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }

    public String getCurrentTier() {
        String currentTier = null;
        if(!StringUtils.isEmpty(selectedItem.getSnpInDel().getExpertTier())) {
            currentTier = selectedItem.getSnpInDel().getExpertTier();
        } else {
            currentTier = selectedItem.getSnpInDel().getSwTier();
        }
        return currentTier;
    }
}
