package ngeneanalysys.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.model.*;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.PDFCreateService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.VelocityUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-09-04
 */
public class AnalysisDetailReportController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    private static final String CONFIRMATION_DIALOG = "Confirmation Dialog";

    /** api service */
    private APIService apiService;

    private PDFCreateService pdfCreateService;

    /** Velocity Util */
    private VelocityUtil velocityUtil = new VelocityUtil();

    @FXML
    private Label diseaseLabel;

    @FXML
    private Label panelLabel;

    Sample sample = null;

    List<AnalysisResultVariant> tierOne = null;

    List<AnalysisResultVariant> tierTwo = null;

    List<AnalysisResultVariant> tierThree = null;

    List<AnalysisResultVariant> negativeVariant = null;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");

        // API 서비스 클래스 init
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        pdfCreateService = PDFCreateService.getInstance();

        sample = (Sample)paramMap.get("sample");

        logger.info(sample.toString());

        List<Diseases> diseases = (List<Diseases>) mainController.getBasicInformationMap().get("diseases");
        String diseaseName = diseases.stream().filter(disease -> disease.getId() == sample.getDiseaseId()).findFirst().get().getName();
        diseaseLabel.setText(diseaseName);

        List<Panel> panels = (List<Panel>) mainController.getBasicInformationMap().get("panels");
        String panelName = panels.stream().filter(panel -> panel.getId().equals(sample.getPanelId())).findFirst().get().getName();
        panelLabel.setText(panelName);

        try {
            HttpClientResponse response = apiService.get("/analysisResults/"+ sample.getId()  + "/variants", null,
                    null, false);

            AnalysisResultVariantList analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(AnalysisResultVariantList.class);

            List<AnalysisResultVariant> list = analysisResultVariantList.getResult();

            //negative list만 가져옴
            List<AnalysisResultVariant> negativeList = list.stream().filter(item -> !StringUtils.isEmpty(item.getInterpretation().getInterpretationNegativeTesult())).collect(Collectors.toList());

            Map<String, List<AnalysisResultVariant>> variantTierMap = list.stream().collect(Collectors.groupingBy(AnalysisResultVariant::getSwTier));

            tierOne = variantTierMap.get("T1");

            if(tierOne != null) {
                //tierTable.getItems().addAll(FXCollections.observableArrayList(tierOne));
                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                tierOne.stream().forEach(item -> {
                    if (item.getSequenceInfo() != null)
                        sequenceInfos.add(item.getSequenceInfo());
                });

                List<Interpretation> interpretations = new ArrayList<>();
                tierOne.stream().forEach(item -> {
                    if (item.getInterpretation() != null)
                        interpretations.add(item.getInterpretation());
                });

            }

            tierTwo = variantTierMap.get("T2");

            if(tierTwo != null) {
                //tierTable.getItems().addAll(FXCollections.observableArrayList(tierTwo));
                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                tierTwo.stream().forEach(item -> {
                    if (item.getSequenceInfo() != null)
                        sequenceInfos.add(item.getSequenceInfo());
                });

                List<Interpretation> interpretations = new ArrayList<>();
                tierTwo.stream().forEach(item -> {
                    if (item.getInterpretation() != null)
                        interpretations.add(item.getInterpretation());
                });

            }

            if(negativeList != null) {
                //pertinentNegativesTable.setItems(FXCollections.observableArrayList(negativeList));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
        createPDF(true, false);
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

        try {
            if(file != null) {
                String draftImageStr = String.format("url('%s')", this.getClass().getClassLoader().getResource("layout/images/DRAFT.png"));
                String ngenebioLogo = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/ngenebio_logo_small.png"));

                Map<String, Object> model = new HashMap<>();
                model.put("isDraft", isDraft);
                //model.put("qcResult", sample.getQc());
                model.put("draftImageURL", draftImageStr);
                model.put("ngenebioLogo", ngenebioLogo);
                //model.put("contents", contentsMap);

                String contents = velocityUtil.getContents("/layout/velocity/report.vm", "UTF-8", model);

                created = pdfCreateService.createPDF(file, contents);
                if (created) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.initOwner(getMainController().getPrimaryStage());
                    alert.setTitle(CONFIRMATION_DIALOG);
                    alert.setHeaderText("Creating the report document was completed.");
                    alert.setContentText("Do you want to check the report document?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        getMainApp().getHostServices().showDocument(file.toURI().toURL().toExternalForm());
                    } else {
                        alert.close();
                    }
                } else {
                    DialogUtil.error("Save Fail.", "An error occurred during the creation of the report document.",
                            getMainApp().getPrimaryStage(), false);
                }
            }
        } catch(FileNotFoundException fnfe){
            DialogUtil.error("Save Fail.", fnfe.getMessage(), getMainApp().getPrimaryStage(), false);
        } catch (Exception e) {
            DialogUtil.error("Save Fail.", "An error occurred during the creation of the report document.", getMainApp().getPrimaryStage(), false);
            e.printStackTrace();
            created = false;
        }

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
