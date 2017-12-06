package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.AnalysisResultVariant;
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
public class ChangeTierDialogController extends SubPaneController {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService = null;

    private TableView<VariantAndInterpretationEvidence> table;

    private String tier;

    private VariantAndInterpretationEvidence selectedItem = null;

    private AnalysisDetailReportController analysisDetailReportController;

    @FXML
    private TextField commentTextField;

    @FXML
    private Label tierLabel;

    private Stage dialogStage;

    /**
     * @param analysisDetailReportController
     */
    public void setAnalysisDetailReportController(AnalysisDetailReportController analysisDetailReportController) {
        this.analysisDetailReportController = analysisDetailReportController;
    }

    public void settingItem(TableView<VariantAndInterpretationEvidence> table, String tier, VariantAndInterpretationEvidence selectedItem) {
        this.table =table;
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
        String comment = commentTextField.getText();
        if(!comment.isEmpty()) {
            logger.info(comment);
            Map<String, Object> params = new HashMap<>();
            try {
                params.put("tier", tier);
                params.put("comment", comment);

                apiService.put("analysisResults/variants/" + selectedItem.getVariant().getId() + "/updateExpertTier", params, null, true);
            } catch (WebAPIException wae) {
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
            }
            selectedItem.getVariant().setExpertTier(tier);
            analysisDetailReportController.resetData(table);
            //analysisDetailReportController.selectClear(getCurrentTier());
            dialogStage.close();
        }
    }

    @FXML
    public void cancel() {
        analysisDetailReportController.selectClear(getCurrentTier());
        selectedItem = null;
        dialogStage.close();
    }

    public String getCurrentTier() {
        String currentTier = null;
        if(!StringUtils.isEmpty(selectedItem.getVariant().getExpertTier())) {
            currentTier = selectedItem.getVariant().getExpertTier();
        } else {
            currentTier = selectedItem.getVariant().getSwTier();
        }
        return currentTier;
    }
}
