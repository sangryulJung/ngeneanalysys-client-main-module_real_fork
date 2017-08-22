package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.Panel;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.lang.Double.MAX_VALUE;
import static java.lang.Thread.sleep;

/**
 * @author Jang
 * @since 2017-08-10
 */
public class ExperimenterHomeController extends SubPaneController{
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private Canvas cpuCanvas;

    @FXML
    private Canvas memoryCanvas;

    @FXML
    private Canvas hddCanvas;

    @FXML
    private GridPane runListGridPane;

    @FXML
    private GridPane sampleListGridPane;

    @FXML
    private Label runCount;

    @FXML
    private Label runQueuedCount;

    @FXML
    private Label sampleCount;

    @FXML
    private Label sampleQueuedCount;

    @FXML
    private Label sampleFailCount;

    @FXML
    private Button buttonUpload;

    @FXML
    private Pagination sampleListPagination;

    /** API Service */
    private APIService apiService;

    private List<TextField> runNameFields;
    private List<RunAnalysisJobStatusBox> runStatusFields;
    private List<TextField> sampleNameFields;
    private List<TextField> samplePanelFields;
    private List<SampleAnalysisJobStatusBox> sampleStatusFields;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("ExperimenterHomeController show..");

        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());
        getMainController().getPrimaryStage().setMaxWidth(1000);
        this.mainController.getMainFrame().setCenter(root);
        testAddRuns();
        initRunListLayout();
        initSampleListLayout();
        showRunList();
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

            controller.setExperimentHomeController(this);
            controller.show(page);
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
                logger.info(pagedRun.toString());
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
                    sampleListPagination.setPageFactory((page) -> {
                        showSampleList(run.getId(), page);
                        return new VBox();
                    });
                    sampleListPagination.setCurrentPageIndex(0);
                });

                runStatusFields.get(i).setStatus(run.getStatus());
            }
            for(int i = runCount; i < maxRunNumberOfPage; i++){
                runNameFields.get(i).setText("");
                runNameFields.get(i).setOnMouseClicked(null);
                runStatusFields.get(i).setBorder(null);
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
            for (int i = 0; i < maxRunNumberOfPage; i++) {
                TextField runNameField = new TextField();
                runNameField.setEditable(false);
                runNameField.setStyle("-fx-font-size:11;");
                runNameField.setStyle(runNameField.getStyle() + "-fx-border-width: 0 1 0 1;");
                runNameField.setStyle(runNameField.getStyle() + "-fx-border-color:blue;");
                runNameField.setStyle(runNameField.getStyle() + "-fx-border-radius:0;");
                runNameField.setStyle(runNameField.getStyle() + "-fx-background-color:transparent;");
                runNameField.setStyle(runNameField.getStyle() + "-fx-max-height:30;");
                runNameFields.add(runNameField);
                runListGridPane.add(runNameFields.get(i), 0, i);
                runStatusFields.add(new RunAnalysisJobStatusBox());
                runStatusFields.get(i).setStyle(runStatusFields.get(i).getStyle() + "-fx-alignment:center;");
                runListGridPane.add(runStatusFields.get(i), 1, i);
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
                SampleAnalysisJobStatusBox sampleStatusField = new SampleAnalysisJobStatusBox();
                sampleStatusFields.add(sampleStatusField);
                sampleStatusField.setStyle("-fx-alignment:center");

                TextField samplePanelField = new TextField();
                samplePanelField.setEditable(false);
                samplePanelFields.add(samplePanelField);

                sampleListGridPane.add(sampleNameField, 0, i);
                sampleListGridPane.add(sampleStatusField, 1, i);
                sampleListGridPane.add(samplePanelField, 2, i);
            }

        } catch (Exception e) {
            logger.error("HOME -> initSampleListLayout", e);
        }
    }
    private void showSampleList(int runId, int pageIndex){
        final int maxItemNumberOfPage = sampleListGridPane.getRowConstraints().size() - 1;
        CompletableFuture<List<Panel>> getPanels = new CompletableFuture<>();
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
        });

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
                logger.info(pagedSample.toString());
                getPagedSample.complete(pagedSample);
            } catch (Exception e) {
                getPagedSample.completeExceptionally(e);
            }
            return getPagedSample;
        });
        try {
            List<Panel> panels = getPanels.get();
            Map<Integer, Panel> mapPanels = panels.stream().collect(Collectors.toMap(Panel::getId, p -> p));
            PagedSample pagedSample = getPagedSample.get();
            sampleListPagination.setCurrentPageIndex(pageIndex);
            sampleListPagination.setMaxPageIndicatorCount(3);
            sampleListPagination.setPageCount(pagedSample.getCount() / maxItemNumberOfPage);
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
        for(int i = 0; i < 20; i++) {
            try {
                params.put("runId", runId);
                params.put("name", "sample_" + i + "_" + "RUN_" + runId);
                params.put("patientId", "PAT_" + i + "_" + runId);
                params.put("panelId", 1);
                params.put("diseaseId", 1);
                params.put("analysisType", "SOMATIC");
                params.put("sampleSource", "FFPE");
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
        private Label statusLabel;
        SampleAnalysisJobStatusBox() {
            super();
            statusBox = new HBox();
            statusLabel = new Label();
            statusBox.getChildren().add(statusLabel);
            this.getChildren().add(statusBox);
            statusBox.setStyle("-fx-alignment:center");
        }
        protected void setStatus(SampleStatus status) {
            statusLabel.setText(status.getStep());
            statusLabel.setId("jobStatus_" + status.getStatus());// + status.getUploadStatus());
        }
    }
}
