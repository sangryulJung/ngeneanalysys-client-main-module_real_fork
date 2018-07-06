package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import ngeneanalysys.service.APIService;
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
public class BatchFalsePositiveDialogController extends SubPaneController {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService = null;

    private List<VariantAndInterpretationEvidence> variantList = null;

    private AnalysisDetailSNVController snvController;

    @FXML
    private CheckBox isFalseCheckBox;

    @FXML
    private Button submitButton;

    @FXML
    private ComboBox<String> commentComboBox;

    private Stage dialogStage;

    private int sampleId;

    public void settingItem(int sampleId, List<VariantAndInterpretationEvidence> variantList, AnalysisDetailSNVController snvController) {
        this.sampleId = sampleId;
        this.variantList = variantList;
        this.snvController = snvController;
    }

    public void comboBoxItemSetting() {
        commentComboBox.getItems().addAll("ETC");
        commentComboBox.getSelectionModel().selectFirst();
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");
        // Create the dialog Stage

        apiService = APIService.getInstance();

        comboBoxItemSetting();

        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Change False Positive");
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
        String comment = commentComboBox.getSelectionModel().getSelectedItem();
        if(!comment.isEmpty()) {
            try {
                StringBuilder stringBuilder = new StringBuilder();

                variantList.forEach(item -> stringBuilder.append(item.getSnpInDel().getId() + ","));
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);

                Map<String, Object> params = new HashMap<>();
                params.put("sampleId", sampleId);
                params.put("snpInDelIds", stringBuilder.toString());
                params.put("falseReason", comment);
                if(isFalseCheckBox.isSelected()) {
                    params.put("isFalse", "Y");
                } else {
                    params.put("isFalse", "N");
                }
                apiService.put("analysisResults/snpInDels/updateFalse", params, null, true);
                snvController.refreshTable();
                dialogStage.close();
            } catch (WebAPIException wae) {
                wae.printStackTrace();
            }
        }
    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }
}
