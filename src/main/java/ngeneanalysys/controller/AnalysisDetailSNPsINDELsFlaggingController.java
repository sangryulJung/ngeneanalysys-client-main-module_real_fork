package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.enums.PathogenicReviewFlagTypeCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
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
 * @since 2017-09-05
 */
public class AnalysisDetailSNPsINDELsFlaggingController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    /** SNPs-INDELs 본화면 컨트롤러 */
    private AnalysisDetailSNPsINDELsController analysisDetailSNPsINDELsController;

    /** 작업 내역 정보 */
    @FXML
    private Label workInformationLabel;

    /** 코멘트 입력폼 */
    @FXML
    private TextField commentTextField;

    /** OK 전송 버튼 */
    @FXML
    private Button submitButton;

    /** 취소 버튼 */
    @FXML
    private Button cancelButton;

    /** 다이얼로그 창 Stage Object */
    private Stage dialogStage;

    /** 작업 취소 여부 */
    private boolean isCancel = true;

    /**
     * @return the analysisDetailSNPsINDELsController
     */
    public AnalysisDetailSNPsINDELsController getAnalysisDetailSNPsINDELsController() {
        return analysisDetailSNPsINDELsController;
    }

    /**
     * @param analysisDetailSNPsINDELsController the analysisDetailSNPsINDELsController to set
     */
    public void setAnalysisDetailSNPsINDELsController(AnalysisDetailSNPsINDELsController analysisDetailSNPsINDELsController) {
        this.analysisDetailSNPsINDELsController = analysisDetailSNPsINDELsController;
    }

    /**
     * 화면 출력
     */
    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        workInformationLabel.setText((String) getParamMap().get("workInformation"));

        commentTextField.setOnKeyReleased(keyEvent -> {
            submitButton.setDisable(StringUtils.isEmpty(commentTextField.getText()));
        });

        // Create the dialog Stage
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle((String) getParamMap().get("dialogTitle"));
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());

        // Scene Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    @FXML
    public void save() {
        if(StringUtils.isEmpty(commentTextField.getText())) {
            DialogUtil.warning("Empty Comment", "Please enter the comment.", dialogStage, true);
        } else {
            Integer variantId = (Integer) getParamMap().get("id");
            String value = (String) getParamMap().get("value");
            PathogenicReviewFlagTypeCode flagType = (PathogenicReviewFlagTypeCode) getParamMap().get("commentType");

            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("value", value);
            paramMap.put("comment_type", flagType.name());
            paramMap.put("comment", commentTextField.getText());
            try {
                apiService.post("/analysis_result/pathogenic_comment_save/" + variantId.intValue(), paramMap, null,
                        true);

                // DialogUtil.alert("Pathogenic Review Save Success",
                // String.format("[%s] This setting operation has been saved.",
                // flagType.name()), dialogStage, true);
                // 부모창 상세정보 새로고침
                analysisDetailSNPsINDELsController.refreshVariantDetail();
                isCancel = false;
                // dialog close
                dialogStage.close();
            } catch (WebAPIException wae) {
                // DialogUtil.error("Pathogenic Review Save Fail",
                // String.format("An error occurred during [%s] save work.",
                // flagType.name()), dialogStage, true);
                DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                        getMainApp().getPrimaryStage(), true);
            } catch (Exception e) {
                logger.error("Unknown Error", e);
                DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
            }
        }
    }

    @FXML
    public void closeFlaggingDialog() {
        dialogStage.close();
    }

}
