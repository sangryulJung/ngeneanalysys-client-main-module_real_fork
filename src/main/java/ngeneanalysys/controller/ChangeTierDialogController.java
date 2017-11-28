package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.AnalysisResultVariant;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
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

    private TableView<AnalysisResultVariant> table;

    private String tier;

    private AnalysisResultVariant selectedItem = null;

    private AnalysisDetailReportController analysisDetailReportController;

    @FXML
    private TextField commentTextField;

    private Stage dialogStage;

    /**
     * @param analysisDetailReportController
     */
    public void setAnalysisDetailReportController(AnalysisDetailReportController analysisDetailReportController) {
        this.analysisDetailReportController = analysisDetailReportController;
    }

    public void settingItem(TableView<AnalysisResultVariant> table, String tier, AnalysisResultVariant selectedItem) {
        this.table =table;
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

                apiService.put("analysisResults/variants/" + selectedItem.getId() + "/updateExpertTier", params, null, true);
            } catch (WebAPIException wae) {
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
            }
            analysisDetailReportController.resetData(table);

            dialogStage.close();
        }
    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }
}
