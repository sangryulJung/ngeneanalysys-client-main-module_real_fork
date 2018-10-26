package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-09-18
 */
public class AnalysisSampleUploadProgressDetailController extends BaseStageController {
    private static Logger logger = LoggerUtil.getLogger();

    /** 메인 화면 컨트롤러 클래스 */
    private MainController mainController;

    /** 파라미터 */
    private Map<String,Object> paramMap;

    /** 분석 요청 업로드 작업 컨트롤러 객체 */
    private AnalysisSampleUploadProgressTaskController taskController;

    /** 현재 업로드중인 분석 요청 그롭 ID */
    private Integer currentUploadGroupId;

    /** 현재 업로드중인 분석 요청 그룹명 */
    private String currentUploadGroupRefName;

    /** 분석 요청 작업 그룹명 라벨 */
    @FXML
    private Label refName;

    /** 샘플 파일 목록 리스트뷰*/
    @FXML
    private ListView<HBox> listViewSampleFile;

    /** 일시정지/시작 버튼 */
    @FXML
    private Button buttonPause;

    /** 분석 요청 창 닫기 버튼 */
    @FXML
    private Button buttonClose;

    /**
     * @return the mainController
     */
    public MainController getMainController() {
        return mainController;
    }
    /**
     * Set Parameter Map
     * @param param Map
     */
    public void setParam(Map<String,Object> param) {
        this.paramMap = param;
    }

    /**
     * @param taskController the taskController to set
     */
    public void setTaskController(AnalysisSampleUploadProgressTaskController taskController) {
        this.taskController = taskController;
        this.mainController = this.taskController.getMainController();
    }

    /**
     * 다이얼로그 화면 출력
     */

    @Override
    public void show(Parent root) throws IOException {
        this.currentUploadGroupId = (Integer) this.paramMap.get("currentUploadGroupId");
        this.currentUploadGroupRefName = (String) this.paramMap.get("currentUploadGroupRefName");
        this.refName.setText(this.currentUploadGroupRefName);


        // Create the dialog Stage
        Stage dialogStage = new Stage();
        dialogStage.setTitle("New Analysis Request");
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > New Analysis Request");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(this.mainController.getMainApp().getPrimaryStage());

        // Scene Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    /**
     * 분석 요청 취소 처리
     */
    @FXML
    public void cancelRequest() {
        this.taskController.cancelUpload();
    }

    /**
     * 업로드 정지
     */
    @FXML
    public void pause() {
        if(this.taskController.isPause) {
            logger.debug("Resume from detail..");
            this.taskController.startUpload();
            buttonPause.setText("Pause");
        } else {
            logger.debug("pause from detail..");
            this.taskController.pauseUpload();
            buttonPause.setText("Resume");
        }
    }

    /**
     * 창 닫기
     */
    @FXML
    public void windowDialogClose() {
        Stage stage = (Stage) this.buttonClose.getScene().getWindow();
        stage.close();
    }

    /**
     * 지정 샘플의 진행률 정보 갱신
     * @param sampleFileId int
     * @param progressPercent double
     */
    public void update(int sampleFileId, double progressPercent) {
        logger.debug(String.format("update progress [bedFile id : %s, percent : %s]", sampleFileId, progressPercent));
        if(listViewSampleFile.getItems() != null && listViewSampleFile.getItems().size() > 0) {
            String selector = "#sampleFile_" + sampleFileId;
            HBox hbox = (HBox) listViewSampleFile.lookup(selector);
            Label statusLabel = (Label) hbox.lookup("#statusLabel");

            // 100퍼센트가 되지 않은 경우에는 진행
            if(progressPercent < 100) {
                ProgressBar progressBar = (ProgressBar) hbox.lookup("#progressBar");
                if(progressBar != null) {
                    progressBar.setProgress(progressPercent/100);
                }
                statusLabel.setText("[" + progressPercent + "%]");
            } else {
                statusLabel.setText("[Completed]");
                // 완료된 경우 진행바 제거
                hbox.getChildren().remove(1);
            }
        }
    }
}
