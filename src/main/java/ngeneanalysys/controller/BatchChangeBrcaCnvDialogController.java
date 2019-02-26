package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.enums.BrcaCNVCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.BrcaCnvExon;
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
public class BatchChangeBrcaCnvDialogController extends SubPaneController {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService = null;

    private List<BrcaCnvExon> brcaCnvExonList = null;

    private AnalysisDetailBrcaCNVController cnvController;

    @FXML
    private RadioButton deletionRadioButton;

    @FXML
    private RadioButton normalRadioButton;

    @FXML
    private RadioButton amplificationRadioButton;

    @FXML
    private TextField commentTextField;

    private Stage dialogStage;

    private int sampleId;

    private String title;

    public void settingItem(int sampleId, List<BrcaCnvExon> brcaCnvExonList, AnalysisDetailBrcaCNVController cnvController,
                            String title) {
        this.sampleId = sampleId;
        this.brcaCnvExonList = brcaCnvExonList;
        this.cnvController = cnvController;
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

        if(brcaCnvExonList.stream().allMatch(v -> (StringUtils.isNotEmpty(v.getExpertCnv())
                && v.getExpertCnv().equals(BrcaCNVCode.AMPLIFICATION.getCode())) ||
                (StringUtils.isEmpty(v.getExpertCnv()) && v.getSwCnv().equals(BrcaCNVCode.AMPLIFICATION.getCode())))) {
            amplificationRadioButton.setSelected(true);
        }else if(brcaCnvExonList.stream().allMatch(v -> (StringUtils.isNotEmpty(v.getExpertCnv())
                && v.getExpertCnv().equals(BrcaCNVCode.NORMAL.getCode())) ||
                (StringUtils.isEmpty(v.getExpertCnv()) && v.getSwCnv().equals(BrcaCNVCode.NORMAL.getCode())))) {
            normalRadioButton.setSelected(true);
        }else if(brcaCnvExonList.stream().allMatch(v -> (StringUtils.isNotEmpty(v.getExpertCnv())
                && v.getExpertCnv().equals(BrcaCNVCode.DELETION.getCode())) ||
                (StringUtils.isEmpty(v.getExpertCnv()) && v.getSwCnv().equals(BrcaCNVCode.DELETION.getCode())))) {
            deletionRadioButton.setSelected(true);
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

            brcaCnvExonList.forEach(item -> stringBuilder.append(item.getId()).append(","));
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            Map<String, Object> params = new HashMap<>();
            params.put("sampleId", sampleId);
            params.put("brcaCnvExonIds", stringBuilder.toString());
            params.put("cnv", returnSelectCnv());
            params.put("comment", comment.isEmpty() ? "N/A" : comment);
            apiService.put("analysisResults/brcaCnvExon/updateCnv", params, null, true);
            cnvController.setList();
            dialogStage.close();
        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
    }

    private String returnSelectCnv() {
        if(deletionRadioButton.isSelected()) {
            return BrcaCNVCode.DELETION.getCode();
        } else if(normalRadioButton.isSelected()) {
            return BrcaCNVCode.NORMAL.getCode();
        } else if(amplificationRadioButton.isSelected()) {
            return BrcaCNVCode.AMPLIFICATION.getCode();
        } else {
            return "";
        }
    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }
}
