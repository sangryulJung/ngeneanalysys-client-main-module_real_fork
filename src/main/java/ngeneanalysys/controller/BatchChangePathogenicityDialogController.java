package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-11-28
 */
public class BatchChangePathogenicityDialogController extends SubPaneController {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService = null;

    private List<VariantAndInterpretationEvidence> variantList = null;

    private AnalysisDetailSNVController snvController;

    @FXML
    private RadioButton lvARadioButton;

    @FXML
    private RadioButton lvBRadioButton;

    @FXML
    private RadioButton lvCRadioButton;

    @FXML
    private RadioButton lvDRadioButton;

    @FXML
    private RadioButton lvERadioButton;

    @FXML
    private ToggleGroup tierToggle;

    @FXML
    private TextField commentTextField;

    private Stage dialogStage;

    private int sampleId;

    private String title;

    void settingItem(int sampleId, List<VariantAndInterpretationEvidence> variantList, AnalysisDetailSNVController snvController,
                     String title) {
        this.sampleId = sampleId;
        this.variantList = variantList;
        this.snvController = snvController;
        this.title = title;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");
        // Create the dialog Stage

        apiService = APIService.getInstance();

        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > " + title);
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());
        dialogStage.setResizable(false);

        if(variantList.stream().allMatch(v -> (StringUtils.isNotEmpty(v.getSnpInDel().getExpertPathogenicity())
                && v.getSnpInDel().getExpertPathogenicity().equals("P")) || (StringUtils.isEmpty(v.getSnpInDel().getExpertPathogenicity())
                && v.getSnpInDel().getSwPathogenicity().equals("P")))) {
            lvARadioButton.setSelected(true);
        }else if(variantList.stream().allMatch(v -> (StringUtils.isNotEmpty(v.getSnpInDel().getExpertPathogenicity())
                && v.getSnpInDel().getExpertPathogenicity().equals("LP")) || (StringUtils.isEmpty(v.getSnpInDel().getExpertPathogenicity())
                && v.getSnpInDel().getSwPathogenicity().equals("LP")))) {
            lvBRadioButton.setSelected(true);
        }else if(variantList.stream().allMatch(v -> (StringUtils.isNotEmpty(v.getSnpInDel().getExpertPathogenicity())
                && v.getSnpInDel().getExpertPathogenicity().equals("US")) || (StringUtils.isEmpty(v.getSnpInDel().getExpertPathogenicity())
                && v.getSnpInDel().getSwPathogenicity().equals("US")))) {
            lvCRadioButton.setSelected(true);
        }else if(variantList.stream().allMatch(v -> (StringUtils.isNotEmpty(v.getSnpInDel().getExpertPathogenicity())
                && v.getSnpInDel().getExpertPathogenicity().equals("LB")) || (StringUtils.isEmpty(v.getSnpInDel().getExpertPathogenicity())
                && v.getSnpInDel().getSwPathogenicity().equals("LB")))) {
            lvDRadioButton.setSelected(true);
        }else {
            lvERadioButton.setSelected(true);
        }

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

            Map<String, Object> params = new HashMap<>();
            params.put("sampleId", sampleId);
            params.put("snpInDelIds", stringBuilder.toString());
            params.put("pathogenicity", returnSelectPathogenicity());
            params.put("comment", comment.isEmpty() ? "Not applicable" : comment);
            apiService.put("analysisResults/snpInDels/updatePathogenicity", params, null, true);
            snvController.refreshTable();
            dialogStage.close();
        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
    }

    private String returnSelectPathogenicity() {
        if(lvARadioButton.isSelected()) {
            return "P";
        } else if(lvBRadioButton.isSelected()) {
            return "LP";
        } else if(lvCRadioButton.isSelected()) {
            return "US";
        } else if(lvDRadioButton.isSelected()) {
            return "LB";
        }
        return "B";
    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }
}
