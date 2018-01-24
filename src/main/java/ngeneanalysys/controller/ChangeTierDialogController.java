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

    private AnalysisDetailReportController analysisDetailReportController;

    @FXML
    private ComboBox<String> clinicalVariantTypeComboBox;

    @FXML
    private TextField dbRefTextField;

    @FXML
    private TextField dbAltTextField;

    @FXML
    private TextField dbNtChangeTextField;

    @FXML
    private TextField dbTranscriptTextField;

    @FXML
    private TextField evidenceATextField;

    @FXML
    private TextField evidenceBTextField;

    @FXML
    private TextField evidenceCTextField;

    @FXML
    private TextField evidenceDTextField;

    @FXML
    private TextField evidenceBenignTextField;

    @FXML
    private TextField commentTextField;

    @FXML
    private Label tierLabel;

    private Stage dialogStage;

    private TableRow<VariantAndInterpretationEvidence> rowItem;

    private boolean typeSomatic = true;

    /**
     * @param analysisDetailReportController
     */
    public void setAnalysisDetailReportController(AnalysisDetailReportController analysisDetailReportController) {
        this.analysisDetailReportController = analysisDetailReportController;
    }

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
        logger.info("show..");
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

        try {
            HttpClientResponse httpClientResponse = apiService
                    .get("analysisResults/snpInDelInterpretation/" + selectedItem.getSnpInDel().getId(), null, null, false);

            if(httpClientResponse.getStatus() == 200) {
                SnpInDelInterpretation interpretation = httpClientResponse.getObjectBeforeConvertResponseToJSON(SnpInDelInterpretation.class);
                if(!StringUtils.isEmpty(interpretation.getClinicalVariantType()))
                    clinicalVariantTypeComboBox.getSelectionModel().select(interpretation.getClinicalVariantType());

                if(!StringUtils.isEmpty(interpretation.getDbAlt()))
                    dbAltTextField.setText(interpretation.getDbAlt());

                if(!StringUtils.isEmpty(interpretation.getDbNtChange()))
                    dbNtChangeTextField.setText(interpretation.getDbNtChange());

                if(!StringUtils.isEmpty(interpretation.getDbRef()))
                    dbRefTextField.setText(interpretation.getDbRef());

                if(!StringUtils.isEmpty(interpretation.getDbTranscript()))
                    dbTranscriptTextField.setText(interpretation.getDbTranscript());

                if(!StringUtils.isEmpty(interpretation.getEvidenceLevelA()))
                    evidenceATextField.setText(interpretation.getEvidenceLevelA());

                if(!StringUtils.isEmpty(interpretation.getEvidenceLevelB()))
                    evidenceBTextField.setText(interpretation.getEvidenceLevelB());

                if(!StringUtils.isEmpty(interpretation.getEvidenceLevelC()))
                    evidenceCTextField.setText(interpretation.getEvidenceLevelC());

                if(!StringUtils.isEmpty(interpretation.getEvidenceLevelD()))
                    evidenceDTextField.setText(interpretation.getEvidenceLevelD());

                if(!StringUtils.isEmpty(interpretation.getEvidenceBenign()))
                    evidenceBenignTextField.setText(interpretation.getEvidenceBenign());

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
        snpInDelInterpretation.setEvidenceLevelA(evidenceATextField.getText());
        snpInDelInterpretation.setEvidenceLevelB(evidenceBTextField.getText());
        snpInDelInterpretation.setEvidenceLevelC(evidenceCTextField.getText());
        snpInDelInterpretation.setEvidenceLevelD(evidenceDTextField.getText());
        snpInDelInterpretation.setEvidenceBenign(evidenceBenignTextField.getText());
        snpInDelInterpretation.setDbRef(dbRefTextField.getText());
        snpInDelInterpretation.setDbAlt(dbAltTextField.getText());
        snpInDelInterpretation.setDbNtChange(dbNtChangeTextField.getText());
        snpInDelInterpretation.setDbTranscript(dbTranscriptTextField.getText());
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
            if(analysisDetailReportController != null) {
                /*selectedItem.getSnpInDel().setExpertTier(tier);*/
                analysisDetailReportController.resetData(table);
                analysisDetailReportController.setVariantsList();
            }

            dialogStage.close();
        }
    }

    @FXML
    public void cancel() {
        if(analysisDetailReportController != null) {
            analysisDetailReportController.selectClear(getCurrentTier());
            selectedItem = null;
            rowItem = null;
        }
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
