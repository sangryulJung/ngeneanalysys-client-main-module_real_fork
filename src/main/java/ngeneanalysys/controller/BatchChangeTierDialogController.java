package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-11-28
 */
public class BatchChangeTierDialogController extends SubPaneController {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService = null;

    private List<VariantAndInterpretationEvidence> variantList = null;

    private AnalysisDetailSNVController snvController;

    @FXML
    private RadioButton tierOneRadioButton;

    @FXML
    private RadioButton tierTwoRadioButton;

    @FXML
    private RadioButton tierThreeRadioButton;

    @FXML
    private RadioButton tierFourRadioButton;

    @FXML
    private ToggleGroup tierToggle;

    @FXML
    private Button submitButton;

    @FXML
    private TextField commentTextField;

    private Stage dialogStage;

    public void settingItem(List<VariantAndInterpretationEvidence> variantList, AnalysisDetailSNVController snvController) {
        this.variantList = variantList;
        this.snvController = snvController;
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

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    @FXML
    public void ok() {
        String comment = commentTextField.getText();
        if(tierToggle.getSelectedToggle().isSelected()) {
            if (!comment.isEmpty()) {
                try {
                    StringBuilder stringBuilder = new StringBuilder();

                    variantList.forEach(item -> stringBuilder.append(item + ","));
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);

                    Map<String, Object> params = new HashMap<>();
                    Map<String, Object> snpInDelTierBulkUpdateRequest = new HashMap<>();
                    snpInDelTierBulkUpdateRequest.put("comment", comment);
                    snpInDelTierBulkUpdateRequest.put("snpInDelIds", stringBuilder.toString());
                    snpInDelTierBulkUpdateRequest.put("tier", returnSelectTier());
                    params.put("snpInDelTierBulkUpdateRequest", snpInDelTierBulkUpdateRequest);
                    apiService.put("analysisResults/snpInDels/updateTier", params, null, true);
                    snvController.refreshTable();
                    dialogStage.close();
                } catch (WebAPIException wae) {
                    wae.printStackTrace();
                }

            } else {
                commentTextField.requestFocus();
            }
        } else {
            //DialogUtil.warning()
        }
    }

    public String returnSelectTier() {
        if(tierOneRadioButton.isSelected()) {
            return "T1";
        } else if(tierTwoRadioButton.isSelected()) {
            return "T2";
        } else if(tierThreeRadioButton.isSelected()) {
            return "T3";
        }
        return "T4";
    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }
}
