package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.controller.fragment.AnalysisDetailClinicalSignificantController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
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
 * @since 2018-01-24
 */
public class ChangePathogenicityController extends SubPaneController {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService = null;

    private String pathogenicity;

    private VariantAndInterpretationEvidence selectedItem = null;

    @FXML
    private TextField commentTextField;

    @FXML
    private Label pathogenicityLabel;

    private Stage dialogStage;

    private AnalysisDetailClinicalSignificantController clinicalSignificantController;

    /**
     * @param clinicalSignificantController AnalysisDetailClinicalSignificantController
     */
    public void setClinicalSignificantController(AnalysisDetailClinicalSignificantController clinicalSignificantController) {
        this.clinicalSignificantController = clinicalSignificantController;
    }

    public void settingTier(String pathogenicity, VariantAndInterpretationEvidence selectedItem) {
        this.pathogenicity = pathogenicity;
        this.selectedItem = selectedItem;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show pathogenicity change");
        // Create the dialog Stage

        apiService = APIService.getInstance();

        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Pathogenicity");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());
        dialogStage.setResizable(false);

        pathogenicityLabel.setText(getCurrentPathogenicity() + " > " + pathogenicity);
        commentTextField.setOnKeyPressed(ev -> commentTextField.setPromptText(""));
        // Scene Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();

    }

    @FXML
    public void ok() {
        SampleView sampleView = (SampleView) paramMap.get("sampleView");
        String comment = commentTextField.getText();
        Map<String, Object> params = new HashMap<>();

        params.put("comment", StringUtils.isEmpty(comment) ? "N/A" : comment);

        try {
            params.put("pathogenicity", pathogenicity);
            params.put("sampleId", sampleView.getId());
            params.put("snpInDelIds", selectedItem.getSnpInDel().getId().toString());
            apiService.put("analysisResults/snpInDels/updatePathogenicity", params, null, true);
        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
        }
        clinicalSignificantController.setVariantPathogenicity(pathogenicity);
        clinicalSignificantController.setPathogenicityArea(pathogenicity);
        dialogStage.close();

    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }

    private String getCurrentPathogenicity() {
        String currentPathogenicity = null;
        if(!StringUtils.isEmpty(selectedItem.getSnpInDel().getExpertPathogenicity())) {
            currentPathogenicity = selectedItem.getSnpInDel().getExpertPathogenicity();
        } else {
            currentPathogenicity = selectedItem.getSnpInDel().getSwPathogenicity();
        }
        return currentPathogenicity;
    }


}
