package ngeneanalysys.controller;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.util.Duration;
import ngeneanalysys.animaition.HddStatusTimer;
import ngeneanalysys.code.AnalysisJobStatusCode;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedNotice;
import ngeneanalysys.model.paged.PagedRun;
import ngeneanalysys.model.paged.PagedRunSampleView;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Jang
 * @since 2017-08-10
 */
public class HomeController extends SubPaneController{
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private Canvas hddCanvas;

    @FXML
    private Canvas availableCanvas;

    @FXML
    private GridPane homeWrapper;

    @FXML
    private HBox runListHBox;

    /*@FXML
    private Label noticeTitleLabel;*/

    @FXML
    private Label dateLabel;

    @FXML
    private Label noticeContentsLabel;

    @FXML
    private ToggleGroup newsTipGroup;

    private List<NoticeView> noticeList = null;

    /** API Service */
    private APIService apiService;
    /** Timer */
    public Timeline autoRefreshTimeline;

    private List<RunStatusVBox> runList;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("HomeController show..");

        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        homeWrapper.add(maskerPane,0 ,0, 5, 6);
        maskerPane.setPrefWidth(getMainController().getMainFrame().getWidth());
        maskerPane.setPrefHeight(getMainController().getMainFrame().getHeight());
        maskerPane.setVisible(false);

        newsTipGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            logger.info("init");
            if(newValue == null) return;
            if(!noticeLabelSetting(newsTipGroup.getToggles().indexOf(newValue))) {
                newsTipGroup.selectToggle(oldValue);
            } else {
                newsTipGroup.selectToggle(newValue);
            }
        });

        getMainController().getPrimaryStage().setMaxWidth(1000);
        this.mainController.getMainFrame().setCenter(root);

        initRunListLayout();
        showRunList();
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.millis(Integer.parseInt(config.getProperty("analysis.job.auto.refresh.period")) * 1000),
                ae -> showRunList()));
        autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        autoRefreshTimeline.play();

    }

    /**
     * 분석 요청 Dialog 창 출력
     */
    @FXML
    public void showUploadFASTQ() {
        maskerPane.setVisible(true);
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
        maskerPane.setVisible(false);
    }

    private void initRunListLayout() {
        final int maxRunNumberOfPage = 3;
        try {
            runList = new ArrayList<>();
            for (int i = 0; i < maxRunNumberOfPage; i++) {
                RunStatusVBox box = new RunStatusVBox();
                runList.add(box);
                runListHBox.setPrefWidth(runListHBox.getPrefWidth() + 247);
                runListHBox.getChildren().add(box);
                runListHBox.setSpacing(37);
            }
            hddCheck();
        } catch (Exception e) {
            logger.error("HOME -> initRunListLayout", e);
        }
    }

    private void setNoticeArea() {
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("limit", 5);
            params.put("offset", 0);

            HttpClientResponse response = apiService.get("/notices", params, null, false);

             noticeList =response.getObjectBeforeConvertResponseToJSON(PagedNotice.class).getResult();

             if(noticeList != null && !noticeList.isEmpty()) {
                    noticeLabelSetting(0);
                    newsTipGroup.selectToggle(newsTipGroup.getToggles().get(0));
             }

        } catch (WebAPIException wae) {

        }

    }

    public boolean noticeLabelSetting(int index) {
        if(noticeList == null || index > noticeList.size() -1) return false;
        NoticeView noticeView = noticeList.get(index);
        //noticeTitleLabel.setText(noticeView.getTitle());

        dateLabel.setText(DateFormatUtils.format(
                noticeView.getCreatedAt().toDate(), "yyyy-MM-dd"));
        noticeContentsLabel.setText(noticeView.getContents());
        return true;
    }

    private void hddCheck() {
        try {
            HttpClientResponse response = apiService.get("/storageUsage", null, null, false);

            StorageUsage storageUsage = response.getObjectBeforeConvertResponseToJSON(StorageUsage.class);
            double value = (double)storageUsage.getFreeSpace() / storageUsage.getTotalSpace();
            String textLabel = ConvertUtil.convertFileSizeFormat(storageUsage.getFreeSpace()) + " / " + ConvertUtil.convertFileSizeFormat(storageUsage.getTotalSpace());
            AnimationTimer hddStatusTier = new HddStatusTimer(hddCanvas.getGraphicsContext2D(), value, "Free Space",
                    textLabel, 10);
            hddStatusTier.start();

            int totalCount = (int)(storageUsage.getTotalSpace() / 10737418240L);
            double available = (double)storageUsage.getAvailableSampleCount() / totalCount;
            String label = storageUsage.getAvailableSampleCount() + " / " + totalCount + " Samples";
            AnimationTimer availableTier = new HddStatusTimer(availableCanvas.getGraphicsContext2D(), available, "Available",
                    label, 10);
            availableTier.start();

        } catch (WebAPIException wae) {

        }
    }

    private void showRunList() {
        setNoticeArea();
        hddCheck();
        final int maxRunNumberOfPage = 3;
        CompletableFuture<PagedRunSampleView> getPagedRun = new CompletableFuture<>();
        CompletableFuture.supplyAsync(() -> {
            HttpClientResponse response;
            Map<String, Object> params = new HashMap<>();
            try {
                params.clear();
                params.put("limit", maxRunNumberOfPage);
                params.put("offset", 0);
                params.put("ordering", "DESC");

                response = apiService.get("/searchSamples", params, null, false);

                PagedRunSampleView searchedSamples = response
                        .getObjectBeforeConvertResponseToJSON(PagedRunSampleView.class);
                //logger.info(pagedRun.toString());
                getPagedRun.complete(searchedSamples);
            } catch (Exception e) {
                getPagedRun.completeExceptionally(e);
            }
            return getPagedRun;
        });
        try {
            PagedRunSampleView pagedRun = getPagedRun.get();
            int runCount = pagedRun.getCount();
            for(int i = 0; i < runCount; i++) {
                RunSampleView run = pagedRun.getResult().get(i);
                runList.get(i).setRunStatus(run);
                runList.get(i).setVisible(true);

            }
            for(int i = runCount; i < maxRunNumberOfPage; i++){
                runList.get(i).reset();
                runList.get(i).setVisible(false);
            }
        } catch (Exception e) {
            logger.error("HOME -> SHOW RUN LIST", e);
        }
    }

    class RunStatusVBox extends VBox {
        private Label runName;
        private Label panelLabel;
        private HBox panelHBox;
        private Label totalLabel;
        private HBox totalHBox;
        private Label statusLabel;
        private Label startDateLabel;
        private HBox startDateHBox;
        private Label FinishDateLabel;
        private HBox FinishDateHBox;
        private Label completeLabel;
        private HBox completeHBox;
        private Label queuedLabel;
        private HBox queuedHBox;
        private Label runningLabel;
        private HBox runningHBox;
        private Label failedLabel;
        private HBox failedHBox;

        private VBox itemVBox;

        public RunStatusVBox() {
            this.setPrefSize(220, 220);
            this.setMinSize(220, 220);
            this.getStyleClass().add("run_box");
            HBox topHBox = new HBox();
            topHBox.setPrefHeight(25);
            runName = new Label();
            runName.setAlignment(Pos.CENTER_LEFT);
            runName.setMinHeight(16);
            runName.setPrefHeight(16);
            runName.setMinWidth(190);
            runName.setPrefWidth(190);
            statusLabel = new Label();
            statusLabel.setMinWidth(17);
            statusLabel.setPrefWidth(17);

            topHBox.getChildren().add(runName);
            topHBox.getChildren().add(statusLabel);
            Insets runNameInsets = new Insets(0,0,0,10);
            runName.setPadding(runNameInsets);
            topHBox.setAlignment(Pos.BOTTOM_LEFT);
            this.getChildren().add(topHBox);

            VBox backgroundVBox = new VBox();
            Insets insets = new Insets(7,7,7,7);
            backgroundVBox.setPadding(insets);

            itemVBox = new VBox();
            itemVBox.setStyle("-fx-background-color : f0f0f0;");
            itemVBox.setPrefHeight(185);
            Insets itemInsets = new Insets(10,0,0,10);
            itemVBox.setPadding(itemInsets);
            panelLabel = new Label();
            panelHBox = createHBox(panelLabel, "Panel : ");
            totalLabel = new Label();
            totalHBox = createHBox(totalLabel, "Samples : ");
            startDateLabel = new Label();
            startDateHBox = createHBox(startDateLabel, "Start : ");
            FinishDateLabel = new Label();
            FinishDateHBox = createHBox(FinishDateLabel, "Finished : ");
            completeLabel = new Label();
            completeHBox = createHBox(completeLabel, "Complete : ");
            queuedLabel = new Label();
            queuedHBox = createHBox(queuedLabel, "Queued : ");
            runningLabel = new Label();
            runningHBox = createHBox(runningLabel, "Running : ");
            failedLabel = new Label();
            failedHBox = createHBox(failedLabel, "Failed : ");

            backgroundVBox.getChildren().add(itemVBox);

            this.getChildren().add(backgroundVBox);
        }

        public HBox createHBox(Label label, String titleLabelString) {
            HBox box = new HBox();
            box.setPrefHeight(20);
            Label titleLabel = new Label(titleLabelString);
            titleLabel.setPrefWidth(75);
            titleLabel.getStyleClass().add("font_gray");
            box.getChildren().add(titleLabel);
            box.getChildren().add(label);
            label.setStyle("-fx-text-fill : gray; -fx-font-family : Noto Sans CJK KR Regular;");
            label.setPrefWidth(120);
            return box;
        }

        public void setRunStatus(RunSampleView run) {
            runName.setText(run.getRun().getName());
            /////////////run status 설정
            statusLabel.setText("");
            statusLabel.getStyleClass().removeAll(statusLabel.getStyleClass());
            if(run.getRun().getStatus().toUpperCase().equals("QUEUED")) {
                statusLabel.getStyleClass().addAll("label", "queued_icon");
                statusLabel.setText("Q");
            } else if(run.getRun().getStatus().toUpperCase().equals("RUNNING")) {
                statusLabel.getStyleClass().addAll("label","run_icon");
                statusLabel.setText("R");
            } else if(run.getRun().getStatus().toUpperCase().equals("COMPLETE")) {
                statusLabel.getStyleClass().addAll("label","complete_icon");
                statusLabel.setText("C");
            } else {
                statusLabel.getStyleClass().addAll("label", "failed_icon");
                statusLabel.setText("F");
            }
            ///////////////////////
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            if(run.getRun().getCreatedAt() != null)
                startDateLabel.setText(format.format(run.getRun().getCreatedAt().toDate()));
            if(run.getRun().getCompletedAt() != null)
                FinishDateLabel.setText(format.format(run.getRun().getCompletedAt().toDate()));
            List<SampleView> sampleViews = run.getSampleViews();

            panelLabel.setText(sampleViews.get(0).getPanelName());
            int totalCount = sampleViews.size();
            totalLabel.setText(String.valueOf(totalCount));
            long completeCount = sampleViews.stream().filter(item -> item.getSampleStatus().getStatus().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_COMPLETE)).count();
            completeLabel.setText(String.valueOf(completeCount));
            long runningCount = sampleViews.stream().filter(item -> item.getSampleStatus().getStatus().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_RUNNING)).count();
            runningLabel.setText(String.valueOf(runningCount));
            long queuedCount = sampleViews.stream().filter(item -> item.getSampleStatus().getStatus().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_QUEUED)).count();
            queuedLabel.setText(String.valueOf(queuedCount));
            long failCount = sampleViews.stream().filter(item -> item.getSampleStatus().getStatus().equals(AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_FAIL)).count();
            failedLabel.setText(String.valueOf(failCount));

            if(!itemVBox.getChildren().contains(panelHBox))
                itemVBox.getChildren().add(panelHBox);
            if(!itemVBox.getChildren().contains(totalHBox))
                itemVBox.getChildren().add(totalHBox);
            if(!itemVBox.getChildren().contains(startDateHBox))
                itemVBox.getChildren().add(startDateHBox);
            if(!itemVBox.getChildren().contains(FinishDateHBox))
                itemVBox.getChildren().add(FinishDateHBox);
            if(!itemVBox.getChildren().contains(completeHBox))
                itemVBox.getChildren().add(completeHBox);
            if(!itemVBox.getChildren().contains(runningHBox))
                itemVBox.getChildren().add(runningHBox);
            if(!itemVBox.getChildren().contains(queuedHBox))
                itemVBox.getChildren().add(queuedHBox);
            if(!itemVBox.getChildren().contains(failedHBox))
                itemVBox.getChildren().add(failedHBox);

        }

        public void reset() {
            runName.setText("");
            statusLabel.getStyleClass().removeAll(startDateHBox.getStyleClass());
            startDateLabel.setText("");
            FinishDateLabel.setText("");
            FinishDateLabel.setText("");
            completeLabel.setText("");
            queuedLabel.setText("");
            runningLabel.setText("");
            failedLabel.setText("");
            //itemVBox.getChildren().removeAll(itemVBox.getChildren());
        }

    }

}
