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
public class BatchExcludeReportDialogController extends SubPaneController {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService = null;

    private List<VariantAndInterpretationEvidence> variantList = null;

    private AnalysisDetailSNVController snvController;

    @FXML
    private CheckBox addToReportCheckBox;

    @FXML
    private TextField commentTextField;

    private Stage dialogStage;

    private int sampleId;

    void settingItem(int sampleId, List<VariantAndInterpretationEvidence> variantList, AnalysisDetailSNVController snvController) {
        this.sampleId = sampleId;
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
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Add to report");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());
        dialogStage.setResizable(false);

        // Scene Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    @FXML
    public void ok() {
        String comment = commentTextField.getText();
        try {
            StringBuilder stringBuilder = new StringBuilder();

            variantList.forEach(item -> stringBuilder.append(item.getSnpInDel().getId()).append(","));
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            String includeInReport = addToReportCheckBox.isSelected() ? "Y" : "N";
            Map<String, Object> params = new HashMap<>();
            params.put("sampleId", sampleId);
            params.put("snpInDelIds", stringBuilder.toString());
            params.put("comment", comment.isEmpty() ? "N/A" : comment);
            params.put("includeInReport", includeInReport);
            apiService.put("analysisResults/snpInDels/updateIncludeInReport", params, null, true);
            snvController.refreshTable();
            dialogStage.close();
        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }
}
