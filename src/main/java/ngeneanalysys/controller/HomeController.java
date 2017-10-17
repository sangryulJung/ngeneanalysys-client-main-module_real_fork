package ngeneanalysys.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.*;
import ngeneanalysys.model.Panel;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.lang.Double.MAX_VALUE;
import static java.lang.Thread.sleep;
import static ngeneanalysys.code.AnalysisJobStatusCode.*;

/**
 * @author Jang
 * @since 2017-08-10
 */
public class HomeController extends SubPaneController{
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private Canvas cpuCanvas;

    @FXML
    private Canvas memoryCanvas;

    @FXML
    private Canvas hddCanvas;

    @FXML
    private GridPane homeWrapper;

    @FXML
    private GridPane runListGridPane;

    @FXML
    private GridPane sampleListGridPane;

    @FXML
    private Label runningSampleAnalysisJobCount;

    @FXML
    private Label queuedSampleAnalysisJobCount;

    @FXML
    private Label completedSampleAnalysisJobCount;

    @FXML
    private Label failedSampleAnalysisJobCount;

    @FXML
    private Button buttonUpload;

    @FXML
    private Pagination sampleListPagination;

    /** API Service */
    private APIService apiService;
    /** Timer */
    public Timeline autoRefreshTimeline;

    public Timeline sampleListAutoRefreshTimeline;

    private List<TextField> runNameFields;
    private List<RunAnalysisJobStatusBox> runStatusFields;
    private List<TextField> runDateFields;
    private List<TextField> sampleNameFields;
    private List<TextField> samplePanelFields;
    private List<SampleAnalysisJobStatusBox> sampleStatusFields;

    private int currentRunId = -1;
    private int currentPage = -1;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("ExperimenterHomeController show..");

        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        getMainController().getPrimaryStage().setMaxWidth(1000);
        this.mainController.getMainFrame().setCenter(root);
        //testAddRuns();
        initRunListLayout();
        initSampleListLayout();
        showRunList();
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.millis(10000),
                ae -> showRunList()));
        autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        autoRefreshTimeline.play();

        sampleListAutoRefreshTimeline = new Timeline(new KeyFrame(Duration.millis(3000),
                ae -> autoUpdateSampleList()));
        sampleListAutoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        sampleListAutoRefreshTimeline.play();
    }

    public void autoUpdateSampleList() {
        if(currentPage != -1 && currentRunId != -1) {
            showSampleList(currentRunId, currentPage);
        }
    }

    /**
     * 분석 요청 Dialog 창 출력
     */
    @FXML
    public void showUploadFASTQ() {
        try {
            // Load the fxml file and create a new stage for the popup dialog
            FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_MAIN);
            Pane page = loader.load();
            SampleUploadController controller = loader.getController();
            controller.setMainController(this.mainController);
            controller.setHomeController(this);
            controller.show(page);
            showRunList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showRunList() {
        final int maxRunNumberOfPage = runListGridPane.getRowConstraints().size();
        CompletableFuture<PagedRun> getPagedRun = new CompletableFuture<>();
        getPagedRun.supplyAsync(() -> {
            HttpClientResponse response = null;
            Map<String, Object> params = new HashMap<>();
            try {
                params.clear();
                params.put("limit", maxRunNumberOfPage);
                params.put("offset", 0);

                response = apiService.get("/runs", params, null, false);

                PagedRun pagedRun = response.getObjectBeforeConvertResponseToJSON(PagedRun.class);
                //logger.info(pagedRun.toString());
                getPagedRun.complete(pagedRun);
            } catch (Exception e) {
                getPagedRun.completeExceptionally(e);
            }
            return getPagedRun;
        });
        try {
            PagedRun pagedRun = getPagedRun.get();
            int runCount = pagedRun.getResult().size();
            for(int i = 0; i < runCount; i++) {
                Run run = pagedRun.getResult().get(i);
                runNameFields.get(i).setText(run.getName());
                runNameFields.get(i).setOnMouseClicked(e -> {
                    //showSampleList(run.getId(), 0);
                    if(e.getClickCount() == 1) {
                        sampleListPagination.setPageFactory((page) -> {
                            showSampleList(run.getId(), page);
                            currentRunId = run.getId();
                            currentPage = page;
                            return new VBox();
                        });
                    } else if(e.getClickCount() == 2) {
                        try {
                            // Load the fxml file and create a new stage for the popup dialog
                            FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_MAIN);
                            Pane page = loader.load();
                            SampleUploadController controller = loader.getController();
                            controller.setRunId(run.getId());
                            controller.setMainController(this.mainController);
                            controller.setHomeController(this);
                            controller.show(page);
                            showRunList();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                runStatusFields.get(i).setStatus(run.getStatus());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                runDateFields.get(i).setText(format.format(run.getCreatedAt().toDate()));
            }
            for(int i = runCount; i < maxRunNumberOfPage; i++){
                runNameFields.get(i).setText("");
                runNameFields.get(i).setOnMouseClicked(null);
                runDateFields.get(i).setText("");
                runStatusFields.get(i).setBorder(null);
                runStatusFields.get(i).setStatus(null);
            }

        } catch (Exception e) {
            logger.error("HOME -> SHOW RUN LIST", e);
        }
    }

    private void initRunListLayout() {
        final int maxRunNumberOfPage = runListGridPane.getRowConstraints().size();
        try {
            runNameFields = new ArrayList<>();
            runStatusFields = new ArrayList<>();
            runDateFields = new ArrayList<>();
            for (int i = 0; i < maxRunNumberOfPage; i++) {
                TextField runNameField = new TextField();
                runNameField.setEditable(false);
                TextField runDateField = new TextField();
                runDateField.setEditable(false);
                StringBuilder style = new StringBuilder("-fx-font-size:11;");
                style.append("-fx-border-width: 0 0 1 0;-fx-border-color:black;");
                style.append("-fx-border-radius:0;-fx-background-color:transparent;");
                style.append("-fx-max-height:30;");
                runNameField.setStyle(style.toString());
                runDateField.setStyle(style.toString());
                runNameFields.add(runNameField);
                runListGridPane.add(runNameFields.get(i), 0, i);
                runStatusFields.add(new RunAnalysisJobStatusBox());
                runStatusFields.get(i).setStyle(runStatusFields.get(i).getStyle() + "-fx-alignment:center;");
                runListGridPane.add(runStatusFields.get(i), 1, i);
                runDateField.setStyle(runDateField.getStyle() + "-fx-alignment:center;");
                runDateFields.add(runDateField);
                runListGridPane.add(runDateFields.get(i), 2, i);
            }
        } catch (Exception e) {
            logger.error("HOME -> initRunListLayout", e);
        }
    }

    private void initSampleListLayout() {
        final int maxItemNumberOfPage = sampleListGridPane.getRowConstraints().size() - 1;
        try {
            sampleNameFields = new ArrayList<>();
            samplePanelFields = new ArrayList<>();
            sampleStatusFields = new ArrayList<>();
            for (int i = 0; i < maxItemNumberOfPage; i++) {
                TextField sampleNameField = new TextField();
                sampleNameField.setEditable(false);
                sampleNameFields.add(sampleNameField);
                StringBuilder style = new StringBuilder("-fx-font-size:11;");
                style.append("-fx-border-width: 0 0 1 0;-fx-border-color:black;");
                style.append("-fx-border-radius:0;-fx-background-color:transparent;");
                style.append("-fx-max-height:30;");
                style.append("-fx-min-height:30;");
                sampleNameField.setStyle(style.toString());
                SampleAnalysisJobStatusBox sampleStatusField = new SampleAnalysisJobStatusBox();
                sampleStatusFields.add(sampleStatusField);
                sampleStatusField.setStyle("-fx-alignment:center");

                TextField samplePanelField = new TextField();
                samplePanelField.setEditable(false);
                samplePanelField.setStyle(style.toString());
                samplePanelFields.add(samplePanelField);

                sampleListGridPane.add(sampleNameField, 0, i);
                sampleListGridPane.add(sampleStatusField, 1, i);
                sampleListGridPane.add(samplePanelField, 2, i);
            }
            sampleListPagination.setVisible(false);
        } catch (Exception e) {
            logger.error("HOME -> initSampleListLayout", e);
        }
    }
    private void showSampleList(int runId, int pageIndex){
        final int maxItemNumberOfPage = sampleListGridPane.getRowConstraints().size() - 1;
        /*CompletableFuture<List<Panel>> getPanels = new CompletableFuture<>();
        getPanels.supplyAsync(() -> {
            HttpClientResponse response = null;
            try {
                response = apiService.get("/panels", null, null, false);
                List<Panel> panels = (List<Panel>)response.getMultiObjectBeforeConvertResponseToJSON(Panel.class, false);
                getPanels.complete(panels);
            } catch (Exception e) {
                getPanels.completeExceptionally(e);
            }
            return getPanels;
        });*/

        CompletableFuture<PagedSample> getPagedSample = new CompletableFuture<>();
        getPagedSample.supplyAsync(() -> {
            HttpClientResponse response = null;
            Map<String, Object> params = new HashMap<>();
            try {
                params.clear();
                params.put("runId", runId);
                params.put("limit", maxItemNumberOfPage);
                params.put("offset", maxItemNumberOfPage * pageIndex);
                response = apiService.get("/samples", params, null, false);

                PagedSample pagedSample = response.getObjectBeforeConvertResponseToJSON(PagedSample.class);
                //logger.info(pagedSample.toString());
                logger.info(pagedSample.getCount() + "");
                getPagedSample.complete(pagedSample);
            } catch (Exception e) {
                getPagedSample.completeExceptionally(e);
            }
            return getPagedSample;
        });
        try {
            List<Panel> panels = (List<Panel>) mainController.getBasicInformationMap().get("panels");//getPanels.get();
            Map<Integer, Panel> mapPanels = panels.stream().collect(Collectors.toMap(Panel::getId, p -> p));
            PagedSample pagedSample = getPagedSample.get();
            runningSampleAnalysisJobCount.setText(
                    pagedSample.getSampleAnalysisJobCount().getRunningSampleCount().toString());
            completedSampleAnalysisJobCount.setText(
                    pagedSample.getSampleAnalysisJobCount().getCompletedSampleCount().toString());
            queuedSampleAnalysisJobCount.setText(
                    pagedSample.getSampleAnalysisJobCount().getQueuedSampleCount().toString());
            failedSampleAnalysisJobCount.setText(
                    pagedSample.getSampleAnalysisJobCount().getFailedSampleCount().toString());
            sampleListPagination.setCurrentPageIndex(pageIndex);
            sampleListPagination.setMaxPageIndicatorCount(3);
            sampleListPagination.setPageCount((int)Math.ceil((double)pagedSample.getCount() / maxItemNumberOfPage));
            if (sampleListPagination.getPageCount() < 2)
                sampleListPagination.setVisible(false);
            else
                sampleListPagination.setVisible(true);
            int sampleCount = pagedSample.getResult().size();
            List<Sample> sortedSamples = pagedSample.getResult().stream().sorted((s1, s2) -> {
                if (s1.getId() > s2.getId()) {
                    return 0;
                }
                else {
                    return -1;
                }
            }).collect(Collectors.toList());
            for(int i = 0; i < sampleCount; i++) {
                Sample sample = sortedSamples.get(i);
                sampleNameFields.get(i).setText(sample.getName());
                sampleStatusFields.get(i).setStatus(sample.getSampleStatus());
                sampleStatusFields.get(i).setVisible(true);
                samplePanelFields.get(i).setText(mapPanels.get(sample.getPanelId()).getName());
            }
            for(int i = sampleCount; i < maxItemNumberOfPage; i++){
                sampleNameFields.get(i).setText("");
                sampleStatusFields.get(i).setVisible(false);
                samplePanelFields.get(i).setText("");
            }
        } catch (Exception e) {
            logger.error("HOME -> SHOW RUN LIST", e);
        }
    }
    private void testAddRuns() {
        Map<String, Object> params = new HashMap<>();
        HttpClientResponse response = null;
        for(int i = 0; i < 10; i ++) {
            try {
                params.put("name", "RUN NAME_" + i);
                params.put("sequencingPlatform", "MISEQ");
                response = apiService.post("/runs", params, null, true);
                Run run = response.getObjectBeforeConvertResponseToJSON(Run.class);
                logger.info(run.toString());
                testAddSamples(run.getId());
            } catch (Exception e) {

            }
        }
    }
    private void testAddSamples(int runId) {
        Map<String, Object> params = new HashMap<>();
        HttpClientResponse response = null;
        for(int i = 0; i < 10; i++) {
            try {
                params.put("runId", runId);
                params.put("name", "sample_" + i + "_" + "RUN_" + runId);
                params.put("patientId", "PAT_" + i + "_" + runId);
                int panelId = 3;//(runId % 3) + 1;
                params.put("panelId", panelId);
                params.put("diseaseId", 1);
                if(panelId == 1) {
                    params.put("analysisType", "GERMLINE");
                } else {
                    params.put("analysisType", "SOMATIC");
                }
                if (panelId == 2) {
                    params.put("sampleSource", "FFPE");
                } else {
                    params.put("sampleSource", "BLOOD");
                }
                params.put("inputFType", "FASTQ.GZ");
                Map<String, String> sampleSheet = new HashMap<>();
                sampleSheet.put("sampleId", "");
                sampleSheet.put("sampleName", "");
                sampleSheet.put("samplePlate", "");
                sampleSheet.put("sampleWell", "");
                sampleSheet.put("i7IndexId", "");
                sampleSheet.put("sampleIndex", "");
                sampleSheet.put("sampleProject", "");
                sampleSheet.put("description", "");
                params.put("sampleSheet", sampleSheet);
                Map<String, String> qcData = new HashMap<>();
                qcData.put("dnaQC", "");
                qcData.put("libraryQC", "");
                qcData.put("seqClusterDensity", "");
                qcData.put("seqQ30", "");
                qcData.put("seqClusterPF", "");
                qcData.put("seqIndexingPFCV", "");
                params.put("qcData", qcData);
                response = apiService.post("/samples", params, null, true);
                Sample sample = response.getObjectBeforeConvertResponseToJSON(Sample.class);
                if (i == 0) {
                    params.clear();
                    params.put("sampleId", sample.getId());
                    params.put("name", "1234.fastq.gz");

                    params.put("fileSize", 1234567890);
                    params.put("fileType", "FASTQ.GZ");
                    response = apiService.post("/analysisFiles", params, null, true);
                }
                logger.info(sample.toString());
            } catch (Exception e) {

            }
        }

    }
    class RunAnalysisJobStatusBox extends VBox {
        private HBox statusBox;
        private Label statusLabel;
        RunAnalysisJobStatusBox() {
            super();
            statusBox = new HBox();
            statusLabel = new Label();
            statusBox.getChildren().add(statusLabel);
            this.getChildren().add(statusBox);
            statusBox.setStyle("-fx-alignment:center");
        }
        protected void setStatus(String status) {
            statusLabel.setText(status);
            statusLabel.setId("jobStatus_" + status);
        }
    }

    class SampleAnalysisJobStatusBox extends VBox {
        private HBox statusBox;
        private Label statusLabelUpload;
        private Label statusLabelPipeline;
        private Label statusLabelComplete;
        private TextField statusMsgTextField;
        SampleAnalysisJobStatusBox() {
            super();
            setVisible(false);
            statusBox = new HBox();
            statusBox.setSpacing(5.0);
            statusLabelUpload = new Label();
            statusLabelPipeline = new Label();
            statusLabelComplete = new Label();
            statusMsgTextField = new TextField();
            StringBuilder style = new StringBuilder("-fx-font-size:9;");
            style.append("-fx-border-width: 0 0 0 0;-fx-border-color:black;");
            style.append("-fx-border-radius:0;-fx-background-color:transparent;");
            style.append("-fx-max-height:30;");
            style.append("-fx-min-height:30;");
            statusMsgTextField.setStyle(style.toString());
            statusBox.getChildren().add(statusLabelUpload);
            statusBox.getChildren().add(new ImageView(resourceUtil.getImage("/layout/images/icon-arrow_margin.png")));
            statusBox.getChildren().add(statusLabelPipeline);
            statusBox.getChildren().add(new ImageView(resourceUtil.getImage("/layout/images/icon-arrow_margin.png")));
            statusBox.getChildren().add(statusLabelComplete);
            statusBox.getChildren().add(statusMsgTextField);
            this.getChildren().add(statusBox);
            statusBox.setStyle("-fx-alignment:center;");
        }
        protected void setStatus(SampleStatus status) {
            if (status.getStep().equals(SAMPLE_ANALYSIS_STEP_UPLOAD)) {
                statusLabelUpload.setText(SAMPLE_ANALYSIS_STEP_UPLOAD);
                statusLabelUpload.setId("jobStatus_" + status.getStatus());
                statusLabelPipeline.setText(SAMPLE_ANALYSIS_STEP_PIPELINE);
                statusLabelPipeline.setId("jobStatus_" + SAMPLE_ANALYSIS_STATUS_NONE);
                statusLabelComplete.setText("COMPLETE");
                statusLabelComplete.setId("jobStatus_" + SAMPLE_ANALYSIS_STATUS_NONE);
            } else if (status.getStep().equals(SAMPLE_ANALYSIS_STEP_PIPELINE)) {
                statusLabelUpload.setText(SAMPLE_ANALYSIS_STEP_UPLOAD);
                statusLabelUpload.setId("jobStatus_" + SAMPLE_ANALYSIS_STATUS_COMPLETE);
                if (status.getStatus().equals(SAMPLE_ANALYSIS_STATUS_COMPLETE)) {
                    statusLabelPipeline.setText(SAMPLE_ANALYSIS_STEP_PIPELINE);
                    statusLabelPipeline.setId("jobStatus_" + SAMPLE_ANALYSIS_STATUS_COMPLETE);
                    statusLabelComplete.setText("COMPLETE");
                    statusLabelComplete.setId("jobStatus_" + SAMPLE_ANALYSIS_STATUS_COMPLETE);
                } else {
                    statusLabelPipeline.setText(SAMPLE_ANALYSIS_STEP_PIPELINE);
                    statusLabelPipeline.setId("jobStatus_" + status.getStatus());
                    statusLabelComplete.setText("COMPLETE");
                    statusLabelComplete.setId("jobStatus_" + SAMPLE_ANALYSIS_STATUS_NONE);
                }
            }
            statusMsgTextField.setText(status.getStatusMsg());
        }
    }
}
