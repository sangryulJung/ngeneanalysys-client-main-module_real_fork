package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
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
import java.util.Map;

/**
 * @author Jang
 * @since 2017-11-28
 */
public class ExcludeReportDialogController extends SubPaneController {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService = null;

    private String symbol;

    private VariantAndInterpretationEvidence selectedItem = null;

    private CheckBox checkBox;

    @FXML
    private TextField commentTextField;

    private Stage dialogStage;

    public void settingItem(String symbol, VariantAndInterpretationEvidence selectedItem, CheckBox checkBox) {
        this.symbol = symbol;
        this.selectedItem = selectedItem;
        this.checkBox = checkBox;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
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

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    @FXML
    public void ok() {
        String comment = commentTextField.getText();
        if(!comment.isEmpty()) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("comment", comment);
                params.put("includeInReport", symbol);
                selectedItem.getSnpInDel().setIncludedInReport(symbol);
                apiService.put("analysisResults/snpInDels/" + selectedItem.getSnpInDel().getId() + "/updateIncludeInReport", params, null, true);
            } catch (WebAPIException wae) {
                wae.printStackTrace();
            }
            selectedItem.getSnpInDel().setIncludedInReport(symbol);
            selectedItem.getSnpInDel().setComment(comment);
            dialogStage.close();
        } else {
            if(checkBox.isSelected()) {
                checkBox.setSelected(false);
            } else {
                checkBox.setSelected(true);
            }
        }

        selectedItem = null;
    }

    @FXML
    public void cancel() {
        if(checkBox.isSelected()) {
            checkBox.setSelected(false);
        } else {
            checkBox.setSelected(true);
        }
        dialogStage.close();
    }
}
