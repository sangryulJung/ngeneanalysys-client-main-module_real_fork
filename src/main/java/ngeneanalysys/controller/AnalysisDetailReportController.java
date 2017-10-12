package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.model.Diseases;
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.User;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Jang
 * @since 2017-09-04
 */
public class AnalysisDetailReportController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    /** api service */
    private APIService apiService;

    @FXML
    private Label diseaseLabel;

    @FXML
    private Label panelLabel;

    Sample sample = null;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");

        // API 서비스 클래스 init
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        sample = (Sample)paramMap.get("sample");

        logger.info(sample.toString());

        List<Diseases> diseases = (List<Diseases>) mainController.getBasicInformationMap().get("diseases");
        String diseaseName = diseases.stream().filter(disease -> disease.getId() == sample.getDiseaseId()).findFirst().get().getName();
        diseaseLabel.setText(diseaseName);

        List<Panel> panels = (List<Panel>) mainController.getBasicInformationMap().get("panels");
        String panelName = panels.stream().filter(panel -> panel.getId().equals(sample.getPanelId())).findFirst().get().getName();
        panelLabel.setText(panelName);

    }

    /**
     * 입력 정보 저장
     * @param user
     * @return
     */
    public boolean saveData(User user) {

        return true;
    }

    @FXML
    public void save() {

    }

    @FXML
    public void createPDFAsDraft() {

    }

    @FXML
    public void createPDFAsFinal() {

    }

    @FXML
    public void confirmPDFAsFinal() {

    }

    public boolean createPDF(boolean isDraft, boolean printInspectionEnforcement) {
        boolean created = true;

        // 보고서 파일명 기본값
        String baseSaveName = String.format("FINAL_Report_%s", sample.getName());
        if(isDraft) {
            baseSaveName = String.format("DRAFT_Report_%s", sample.getName());
        }
        // Show save file dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName(baseSaveName);
        File file = fileChooser.showSaveDialog(this.getMainApp().getPrimaryStage());

        return created;
    }

    /**
     * 보고서 작업 완료 처리
     */
    public void setComplete() {
        /*try {
            // step_report 상태값 완료 처리.
            int jobStatusId = sample.getJobStatus().getId();
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("step_report", AnalysisJobStatusCode.SAMPLE_JOB_STATUS_COMPLETE);
            param.put("step msg", "Complete Review and Reporting");
            apiService.patch("/analysis_progress_state/jobstatus_update/" + jobStatusId, param, null, true);
            // 상태값 변경이 정상 처리된 경우 화면 상태 변경 처리.
            // 보고서 작업화면 비활성화
            setActive(false);
            // 상위 레이아웃 컨트롤러의 완료 처리 메소드 실행
            getAnalysisDetailLayoutController().setReviewComplete();
        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }*/
    }
}
