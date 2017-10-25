package ngeneanalysys.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import ngeneanalysys.code.enums.SequencerCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
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

    @FXML
    private TextArea conclusionsTextArea;

    @FXML
    private TableView<AnalysisResultVariant> tierOneVariantsTable;

    @FXML
    private TableColumn<AnalysisResultVariant, String> tierOneGeneColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> tierOneVariantsColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> tierOneTherapeuticColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> tierOneDrugColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, Boolean> tierOneExceptColumn;

    @FXML
    private TableView<AnalysisResultVariant> tierTwoVariantsTable;

    @FXML
    private TableColumn<AnalysisResultVariant, String> tierTwoGeneColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> tierTwoVariantsColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> tierTwoTherapeuticColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> tierTwoDrugColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, Boolean> tierTwoExceptColumn;

    @FXML
    private TableView<AnalysisResultVariant> negativeVariantsTable;

    @FXML
    private TableColumn<AnalysisResultVariant, String> negativeGeneColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> negativeVariantsColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> negativeCauseColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, Boolean> negativeExceptColumn;

    Sample sample = null;

    Panel panel = null;

    List<AnalysisResultVariant> tierOne = null;

    List<AnalysisResultVariant> tierTwo = null;

    List<AnalysisResultVariant> tierThree = null;

    List<AnalysisResultVariant> negativeVariant = null;

    List<AnalysisResultVariant> negativeList = null;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");

        // API 서비스 클래스 init
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        pdfCreateService = PDFCreateService.getInstance();

        sample = (Sample)paramMap.get("sample");

        tierOneGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getGene()));
        tierOneVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantExpression().getNtChange()));
        tierOneTherapeuticColumn.setCellValueFactory(cellData -> {
            Interpretation interpretation = cellData.getValue().getInterpretation();
            String text = "";

            if(!StringUtils.isEmpty(interpretation.getInterpretationEvidenceA()))
                text += interpretation.getInterpretationEvidenceA() + ", ";
            if(!StringUtils.isEmpty(interpretation.getInterpretationEvidenceB()))
                text += interpretation.getInterpretationEvidenceB() + ", ";
            if(!StringUtils.isEmpty(interpretation.getInterpretationEvidenceC()))
                text += interpretation.getInterpretationEvidenceC() + ", ";
            if(!StringUtils.isEmpty(interpretation.getInterpretationEvidenceD()))
                text += interpretation.getInterpretationEvidenceD() + ", ";

            if(!"".equals(text)) {
                text = text.substring(0, text.length() - 2);
            }

            return new SimpleStringProperty(text);
        });

        tierTwoGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getGene()));
        tierTwoVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantExpression().getNtChange()));
        tierTwoTherapeuticColumn.setCellValueFactory(cellData -> {
            Interpretation interpretation = cellData.getValue().getInterpretation();
            String text = "";

            if(!StringUtils.isEmpty(interpretation.getInterpretationEvidenceA()))
                text += interpretation.getInterpretationEvidenceA() + ", ";
            if(!StringUtils.isEmpty(interpretation.getInterpretationEvidenceB()))
                text += interpretation.getInterpretationEvidenceB() + ", ";
            if(!StringUtils.isEmpty(interpretation.getInterpretationEvidenceC()))
                text += interpretation.getInterpretationEvidenceC() + ", ";
            if(!StringUtils.isEmpty(interpretation.getInterpretationEvidenceD()))
                text += interpretation.getInterpretationEvidenceD() + ", ";

            if(!"".equals(text)) {
                text = text.substring(0, text.length() - 2);
            }

            return new SimpleStringProperty(text);
        });

        negativeGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getGene()));
        negativeVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantExpression().getNtChange()));
        negativeCauseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInterpretation().getInterpretationNegativeTesult()));

        logger.info(sample.toString());

        List<Diseases> diseases = (List<Diseases>) mainController.getBasicInformationMap().get("diseases");
        String diseaseName = diseases.stream().filter(disease -> disease.getId() == sample.getDiseaseId()).findFirst().get().getName();
        diseaseLabel.setText(diseaseName);

        List<Panel> panels = (List<Panel>) mainController.getBasicInformationMap().get("panels");
        panel = panels.stream().filter(panel -> panel.getId().equals(sample.getPanelId())).findFirst().get();
        String panelName = panel.getName();
        panelLabel.setText(panelName);

        try {
            HttpClientResponse response = apiService.get("/analysisResults/" + sample.getId()  + "/variants", null,
                    null, false);

            AnalysisResultVariantList analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(AnalysisResultVariantList.class);

            List<AnalysisResultVariant> list = analysisResultVariantList.getResult();

            //negative list만 가져옴
            negativeList = list.stream().filter(item -> !StringUtils.isEmpty(item.getInterpretation().getInterpretationNegativeTesult())).collect(Collectors.toList());

            Map<String, List<AnalysisResultVariant>> variantTierMap = list.stream().collect(Collectors.groupingBy(AnalysisResultVariant::getSwTier));

            tierOne = variantTierMap.get("T1");

            if(tierOne != null) {
                tierOneVariantsTable.getItems().addAll(FXCollections.observableArrayList(tierOne));

            }

            tierTwo = variantTierMap.get("T2");

            if(tierTwo != null) {
                tierTwoVariantsTable.getItems().addAll(FXCollections.observableArrayList(tierTwo));

            }

            tierThree = variantTierMap.get("T3");

            if(negativeList != null) {
                negativeVariantsTable.setItems(FXCollections.observableArrayList(negativeList));
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
                Map<String,Object> contentsMap = new HashMap<>();
                contentsMap.put("panelName", panel.getName());
                contentsMap.put("diseaseName", diseaseLabel.getText());
                contentsMap.put("sampleSource", panel.getSampleSource());
                contentsMap.put("panelCode", panel.getCode());

                List<AnalysisResultVariant> variantList = new ArrayList<>();
                if(tierOne != null && !tierOne.isEmpty()) variantList.addAll(tierOne);
                if(tierTwo != null && !tierTwo.isEmpty()) variantList.addAll(tierTwo);

                List<AnalysisResultVariant> negativeResult = new ArrayList<>();
                //리포트에서 제외된 negative 정보를 제거
                if(negativeList != null && !negativeList.isEmpty()) {
                    negativeResult.addAll(negativeList.stream().filter(item -> item.getIncludedInReport().equals("Y")).collect(Collectors.toList()));
                }
                //리포트에서 제외된 variant를 제거
                if(!variantList.isEmpty()) {
                    variantList = variantList.stream().filter(item -> item.getIncludedInReport().equals("Y")).collect(Collectors.toList());
                }

                Long evidenceACount = variantList.stream().filter(item -> !StringUtils.isEmpty(item.getInterpretation().getInterpretationEvidenceA())).count();
                Long evidenceBCount = variantList.stream().filter(item -> !StringUtils.isEmpty(item.getInterpretation().getInterpretationEvidenceB())).count();
                Long evidenceCCount = variantList.stream().filter(item -> !StringUtils.isEmpty(item.getInterpretation().getInterpretationEvidenceC())).count();
                Long evidenceDCount = variantList.stream().filter(item -> !StringUtils.isEmpty(item.getInterpretation().getInterpretationEvidenceD())).count();

                contentsMap.put("variantList", variantList);
                contentsMap.put("tierThreeVariantList", tierThree);
                contentsMap.put("evidenceACount", evidenceACount);
                contentsMap.put("evidenceBCount", evidenceBCount);
                contentsMap.put("evidenceCCount", evidenceCCount);
                contentsMap.put("evidenceDCount", evidenceDCount);
                contentsMap.put("negativeList", negativeResult);
                contentsMap.put("qcData", sample.getQcData());

                //Genes in panel
                try {
                    HttpClientResponse response = apiService.get("/analysisResults/variantCountByGene/" + sample.getId(),
                            null, null, false);
                    logger.info(response.toString());
                    if (response != null) {
                        List<VariantCountByGene> variantCountByGenes = (List<VariantCountByGene>) response
                                .getMultiObjectBeforeConvertResponseToJSON(VariantCountByGene.class,
                                        false);

                        int tableOneSize = (int)Math.ceil((double)variantCountByGenes.size() / 3);
                        int tableTwoSize = (int)Math.ceil((double)(variantCountByGenes.size() - tableOneSize) / 2);

                        Object[] genesInPanelTableOne = variantCountByGenes.toArray();
                        //Gene List를 3등분함
                        contentsMap.put("genesInPanelTableOne", Arrays.copyOfRange(genesInPanelTableOne, 0, tableOneSize));
                        contentsMap.put("genesInPanelTableTwo", Arrays.copyOfRange(genesInPanelTableOne, tableOneSize, tableOneSize + tableTwoSize));
                        contentsMap.put("genesInPanelTableThree", Arrays.copyOfRange(genesInPanelTableOne, tableOneSize + tableTwoSize, variantCountByGenes.size()));

                        response = apiService.get("/runs/" + sample.getRunId(), null,
                                null, false);

                        String runSequencer = response.getContentString().substring(response.getContentString().indexOf("MISEQ"));

                        runSequencer = runSequencer.substring(0, runSequencer.indexOf("\""));

                        if(runSequencer.equalsIgnoreCase("MISEQ")) {
                            contentsMap.put("sequencer",SequencerCode.MISEQ.getDescription());
                        } else {
                            contentsMap.put("sequencer",SequencerCode.MISEQ_DX.getDescription());
                        }

                        response = apiService.get("/analysisResults/"+ sample.getId() + "/sampleQCs", null,
                                null, false);

                        List<SampleQC> qcList = (List<SampleQC>) response.getMultiObjectBeforeConvertResponseToJSON(SampleQC.class, false);

                        contentsMap.put("totalBase",findQCResult(qcList, "total_base"));
                        contentsMap.put("q30",findQCResult(qcList, "q30_trimmed_base"));
                        contentsMap.put("mappedBase",findQCResult(qcList, "mapped_base"));
                        contentsMap.put("onTarget",findQCResult(qcList, "on_target"));
                        contentsMap.put("onTargetCoverage",findQCResult(qcList, "on_target_coverage"));
                        contentsMap.put("duplicatedReads",findQCResult(qcList, "duplicated_reads"));
                        contentsMap.put("roiCoverage",findQCResult(qcList, "roi_coverage"));
                    }
                } catch (WebAPIException wae) {
                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                            getMainApp().getPrimaryStage(), true);
                }

                Map<String, Object> model = new HashMap<>();
                model.put("isDraft", isDraft);
                //model.put("qcResult", sample.getQc());
                model.put("draftImageURL", draftImageStr);
                model.put("ngenebioLogo", ngenebioLogo);
                model.put("contents", contentsMap);

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

    private SampleQC findQCResult(List<SampleQC> qcList, String qc) {
        if(qcList != null && !qcList.isEmpty()) {
            Optional<SampleQC> findQC = qcList.stream().filter(sampleQC -> sampleQC.getQcType().equalsIgnoreCase(qc)).findFirst();
            if(findQC.isPresent()) {
                return findQC.get();
            }
        }
        return null;
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
