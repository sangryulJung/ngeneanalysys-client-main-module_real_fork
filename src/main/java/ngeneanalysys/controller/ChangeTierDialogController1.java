package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-11-28
 */
public class ChangeTierDialogController1 extends SubPaneController {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService = null;

    private TableView<VariantAndInterpretationEvidence> table;

    private String tier;

    private VariantAndInterpretationEvidence selectedItem = null;

    private AnalysisDetailReportController analysisDetailReportController;

    private AnalysisDetailReportGermlineController analysisDetailReportGermlineController;

    @FXML
    private TextField clinicalVariantVersionTextField;

    @FXML
    private TextField clinicalVariantTypeTextField;

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
    private Label tierLabel;

    private Stage dialogStage;

    private TableRow<VariantAndInterpretationEvidence> rowItem;

    private boolean typeSomatic = true;

    /**
     * @param analysisDetailReportGermlineController
     */
    public void setAnalysisDetailReportGermlineController(AnalysisDetailReportGermlineController analysisDetailReportGermlineController) {
        this.analysisDetailReportGermlineController = analysisDetailReportGermlineController;
    }

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

        String currentTier = getCurrentTier();

        tierLabel.setText(ConvertUtil.tierConvert(currentTier) + " > " + ConvertUtil.tierConvert(tier));

        if(tier.equalsIgnoreCase("T1")) {
            evidenceCTextField.setDisable(true);
            evidenceDTextField.setDisable(true);
            evidenceBenignTextField.setDisable(true);
        } else if(tier.equalsIgnoreCase("T2")) {
            evidenceATextField.setDisable(true);
            evidenceBTextField.setDisable(true);
            evidenceBenignTextField.setDisable(true);
        } else if(tier.equalsIgnoreCase("T3")) {
            evidenceATextField.setDisable(true);
            evidenceBTextField.setDisable(true);
            evidenceCTextField.setDisable(true);
            evidenceDTextField.setDisable(true);
            evidenceBenignTextField.setDisable(true);
        } else if(tier.equalsIgnoreCase("T4")) {
            evidenceATextField.setDisable(true);
            evidenceBTextField.setDisable(true);
            evidenceCTextField.setDisable(true);
            evidenceDTextField.setDisable(true);
        }
    }

    public void settingTier(String tier, VariantAndInterpretationEvidence selectedItem) {
        this.tier = tier;
        this.selectedItem = selectedItem;
        String currentTier = getCurrentTier();

        tierLabel.setText(ConvertUtil.tierConvert(currentTier) + " > " + ConvertUtil.tierConvert(tier));
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

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    @FXML
    public void ok() {
        boolean textInput = false;
        Map<String, Object> params = new HashMap<>();

        if(tier.equalsIgnoreCase("T1")) {
            if(!StringUtils.isEmpty(evidenceATextField.getText())) {
                textInput = true;
                params.put("evidenceLevelA", evidenceATextField.getText());
            }
            if(!StringUtils.isEmpty(evidenceBTextField.getText())) {
                textInput = true;
                params.put("evidenceLevelB", evidenceBTextField.getText());
            }
        } else if(tier.equalsIgnoreCase("T2")) {
            if(!StringUtils.isEmpty(evidenceCTextField.getText())) {
                textInput = true;
                params.put("evidenceLevelC", evidenceCTextField.getText());
            }
            if(!StringUtils.isEmpty(evidenceDTextField.getText())) {
                textInput = true;
                params.put("evidenceLevelD", evidenceDTextField.getText());
            }
        } else if(tier.equalsIgnoreCase("T4")) {
            if(!StringUtils.isEmpty(evidenceBenignTextField.getText())) {
                textInput = true;
                params.put("evidenceBenign", evidenceBenignTextField.getText());
            }
        } else if(tier.equalsIgnoreCase("T3")) {
            textInput = true;
        }

        if(textInput) {
            try {
                /*if(typeSomatic) {
                    params.put("tier", tier);
                } else {
                    params.put("tier", tier);
                }*/
                params.put("snpInDelId", selectedItem.getSnpInDel().getId());

                apiService.put("analysisResults/snpInDels/" + selectedItem.getSnpInDel().getId() + "/updateExpertTier", params, null, true);
            } catch (WebAPIException wae) {
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
            }
            if(analysisDetailReportController != null) {
                selectedItem.getSnpInDel().setExpertTier(tier);
                analysisDetailReportController.resetData(table);
            }
            if(analysisDetailReportGermlineController != null) {
                selectedItem.getSnpInDel().setExpertPathogenicityLevel(tier);
                analysisDetailReportGermlineController.resetData(table);
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
