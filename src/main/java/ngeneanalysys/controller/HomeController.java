package ngeneanalysys.controller;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import ngeneanalysys.animaition.HddStatusTimer;
import ngeneanalysys.code.AnalysisJobStatusCode;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.controller.menu.SystemMenuPublicDatabasesController;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedNotice;
import ngeneanalysys.model.paged.PagedRun;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @author Jang
 * @since 2017-08-10
 */
public class HomeController extends SubPaneController{
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
	private Button buttonUpload;
    
    @FXML
    private Canvas hddCanvas;

    @FXML
    private Canvas availableCanvas;

    @FXML
    private GridPane homeWrapper;

    @FXML
    private HBox runListHBox;

    @FXML
    private HBox toggleGroupHBox;

    @FXML
    private Label noticeTitleLabel;

    @FXML
    private TextArea noticeContentsTextArea;

    @FXML
    private ToggleGroup newsTipGroup;

    @FXML
    private VBox databaseVersionVBox;

    /** API Service */
    private APIService apiService;
    /** Timer */
    Timeline autoRefreshTimeline;

    private List<RunStatusVBox> runList;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("HomeController show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        homeWrapper.add(maskerPane,0 ,0, 5, 6);
        setMaskerPanePrefWidth(homeWrapper.getPrefWidth());
        setMaskerPanePrefHeight(homeWrapper.getPrefHeight());
        setMaskerPaneVisable(false);

        newsTipGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            logger.debug("init");
            if(newValue == null) return;
            if(oldValue != null && oldValue != newValue)
                setNoticeArea(newsTipGroup.getToggles().indexOf(newValue));
        });

        this.mainController.getMainFrame().setCenter(root);

        initRunListLayout();
        showRunList();

        startAutoRefresh();
    }

    /**
     * 분석 요청 Dialog 창 출력
     */
    @FXML
    public void showUploadFASTQ() {
        runUploadFASTQ(null);
    }

    private void runUploadFASTQ(Run run) {
        getMainController().setMainMaskerPane(true);
        try {
            // Load the fxml file and create a new stage for the popup dialog
            FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_MAIN);
            Pane page = loader.load();
            SampleUploadController controller = loader.getController();
            controller.setMainController(this.mainController);
            controller.setHomeController(this);
            if(run != null) {
                controller.setRun(run);
            }
            controller.show(page);
            showRunList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getMainController().setMainMaskerPane(false);
    }

    @FXML
    public void newAnalysisMouseEnter() {
    	buttonUpload.setStyle("-fx-background-image:url('layout/images/renewal/plus-symbol-on.png');");
    }
    
    @FXML
    public void newAnalysisMouseExit() {
    	buttonUpload.setStyle("-fx-background-image:url('layout/images/renewal/plus-symbol.png');");
    }

    private void initRunListLayout() {
        final int maxRunNumberOfPage = 4;
        try {
            runList = new ArrayList<>();
            for (int i = 0; i < maxRunNumberOfPage; i++) {
                RunStatusVBox box = new RunStatusVBox(this);
                runList.add(box);
                runListHBox.setPrefWidth(runListHBox.getPrefWidth() + 235);
                runListHBox.getChildren().add(box);
                runListHBox.setSpacing(10);
            }
        } catch (Exception e) {
            logger.error("HOME -> initRunListLayout", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void setToolsAndDatabase() {
        databaseVersionVBox.getChildren().removeAll(databaseVersionVBox.getChildren());
        Task task = new Task() {
            List<PipelineVersionView> pipelineVersionViewList;
            @Override
            protected Object call() throws Exception {
                HttpClientResponse response = apiService.get("pipelineVersions/currentVersionGroupByPanel", null, null, null);

                pipelineVersionViewList = (List<PipelineVersionView>)response.getMultiObjectBeforeConvertResponseToJSON(PipelineVersionView.class, false);
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if(pipelineVersionViewList != null && !pipelineVersionViewList.isEmpty()) {
                    for (PipelineVersionView pipelineVersionView : pipelineVersionViewList) {
                        createPipelineVersionHBox(pipelineVersionView);
                    }
                } else {
                    createDefaultVersionHBox();
                }
            }

            @Override
            protected void failed() {
                super.failed();
                createDefaultVersionHBox();
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void createDefaultVersionHBox() {
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setPrefHeight(30);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setCursor(Cursor.HAND);
        Label iconLabel = new Label();
        iconLabel.getStyleClass().add("tools_icon");
        Label nameLabel = new Label("Analysis Tools");
        hBox.getChildren().addAll(iconLabel, nameLabel);
        databaseVersionVBox.getChildren().add(hBox);
        hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> toolsView());
        hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setPrefHeight(30);
        hBox.setCursor(Cursor.HAND);
        hBox.setAlignment(Pos.CENTER_LEFT);
        iconLabel = new Label();
        iconLabel.getStyleClass().add("tools_icon");
        nameLabel = new Label("Annotation Database");
        hBox.getChildren().addAll(iconLabel, nameLabel);
        databaseVersionVBox.getChildren().add(hBox);
        hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> databaseView());

    }

    private void createPipelineVersionHBox(final PipelineVersionView pipelineVersionView) {
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setPrefHeight(30);
        hBox.setCursor(Cursor.HAND);
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label();
        iconLabel.getStyleClass().add("tools_icon");
        Label nameLabel = new Label(pipelineVersionView.getPanelName() + " : " + pipelineVersionView.getVersion());
        hBox.getChildren().addAll(iconLabel, nameLabel);
        databaseVersionVBox.getChildren().add(hBox);
        hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            try {
                FXMLLoader loader = mainApp.load(FXMLConstants.SYSTEM_MENU_PUBLIC_DATABASES);
                Node root = loader.load();
                PublicDatabaseController publicDatabasesController = loader.getController();
                publicDatabasesController.setMainController(this.getMainController());
                publicDatabasesController.setPanelId(pipelineVersionView.getPanelId());
                publicDatabasesController.show((Parent) root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void setNoticeArea(int index) {
        Task<Void> task = new Task<Void>() {

            List<NoticeView> noticeList = null;
            @Override
            protected Void call() throws Exception {
                HttpClientResponse response;
                Map<String, Object> params = new HashMap<>();

                params.put("limit", 5);
                params.put("offset", 0);

                response = apiService.get("/notices", params, null, false);

                noticeList = response.getObjectBeforeConvertResponseToJSON(PagedNotice.class).getResult();
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if(toggleGroupHBox.getChildren() != null && !toggleGroupHBox.getChildren().isEmpty()) {
                    toggleGroupHBox.getChildren().clear();
                    newsTipGroup.getToggles().clear();
                }

                if(noticeList != null && !noticeList.isEmpty() && index >= 0 && index < noticeList.size()) {

                    for(int i = 0; i < noticeList.size(); i++) {
                        RadioButton radioButton = new RadioButton();
                        radioButton.setToggleGroup(newsTipGroup);
                        toggleGroupHBox.getChildren().add(radioButton);
                    }

                    noticeTitleLabel.setText(noticeList.get(index).getTitle());
                    noticeContentsTextArea.setText(noticeList.get(index).getContents());

                    newsTipGroup.selectToggle(newsTipGroup.getToggles().get(index));
                }
            }

            @Override
            protected void failed() {
                super.failed();
                getException().printStackTrace();
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void hddCheck() {
        Task<Void> task = new Task<Void>() {

            StorageUsage storageUsage;
            double value;
            int totalCount;
            double usageSample;
            String textLabel;
            String label;

            @Override
            protected Void call() throws Exception {
                HttpClientResponse response;
                response = apiService.get("/storageUsage", null, null, false);

                storageUsage = response.getObjectBeforeConvertResponseToJSON(StorageUsage.class);
                value = (double)(storageUsage.getTotalSpace() -  storageUsage.getFreeSpace())
                        / storageUsage.getTotalSpace();
                totalCount = (int)(storageUsage.getAvailableSampleCount() + storageUsage.getCurrentSampleCount());
                usageSample = (double)(storageUsage.getCurrentSampleCount()) / totalCount;
                textLabel = ConvertUtil.convertFileSizeFormat(storageUsage.getFreeSpace()) + " / " + ConvertUtil.convertFileSizeFormat(storageUsage.getTotalSpace());
                label = storageUsage.getAvailableSampleCount() + " / " + totalCount + " Samples";
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() ->{
                    AnimationTimer hddStatusTier = new HddStatusTimer(hddCanvas.getGraphicsContext2D(), value, "Free Space",
                            textLabel, 1);
                    hddStatusTier.start();
                    if(storageUsage!= null) {
                        AnimationTimer availableTier = new HddStatusTimer(availableCanvas.getGraphicsContext2D(), usageSample, "Available",
                                label, 1);
                        availableTier.start();
                    }
                });
            }

            @Override
            protected void failed() {
                super.failed();
                getException().printStackTrace();
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void showRunList() {
        getMainController().setContentsMaskerPaneVisible(true);
        if(autoRefreshTimeline != null)
            logger.debug("cycle time : " + autoRefreshTimeline.getCycleDuration());

        hddCheck();
        setNoticeArea(0);
        setToolsAndDatabase();

        Task task = new Task() {
            private PagedRun pagedRun;
            private final int maxRunNumberOfPage = 4;
            @Override
            protected Object call() throws Exception {
                Map<String, Object> params = new HashMap<>();
                params.put("limit", maxRunNumberOfPage);
                params.put("offset", 0);
                params.put("ordering", "DESC");
                HttpClientResponse response = apiService.get("/runs", params, null, false);
                pagedRun = response.getObjectBeforeConvertResponseToJSON(PagedRun.class);
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                getMainController().setContentsMaskerPaneVisible(false);
                int runCount = pagedRun.getResult().size();
                for(int i = 0; i < runCount; i++) {
                    Run run = pagedRun.getResult().get(i);
                    runList.get(i).setRunStatus(run);
                    runList.get(i).setVisible(true);
                }
                for(int i = runCount; i < maxRunNumberOfPage; i++){
                    runList.get(i).reset();
                    runList.get(i).setVisible(false);
                }
                pagedRun = null;
            }

            @Override
            protected void failed() {
                super.failed();
                pagedRun = null;
                getMainController().setContentsMaskerPaneVisible(false);
                logger.error("HOME -> SHOW RUN LIST", getException());
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    static class RunStatusVBox extends VBox {
        private Label runName;
        private Label panelLabel;
        private HBox panelHBox;
        private Label totalLabel;
        private HBox totalHBox;
        private Label statusLabel;
        private Label startDateLabel;
        private HBox startDateHBox;
        private Label finishDateLabel;
        private HBox finishDateHBox;
        private Label completeLabel;
        private HBox completeHBox;
        private Label queuedLabel;
        private HBox queuedHBox;
        private Label runningLabel;
        private Label failedLabel;
        private ProgressBar progressBar;
        private Label progressLabel;
        private HBox progressHBox;

        private VBox itemVBox;

        private Run run;

        private HomeController homeController;

        private EventHandler mouseEventEventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                homeController.runUploadFASTQ(run);
            }
        };

        private RunStatusVBox(HomeController homeController) {
            this.homeController = homeController;
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
            runName.getStyleClass().add("bold");
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
            finishDateLabel = new Label();
            finishDateHBox = createHBox(finishDateLabel, "Finish : ");

            completeLabel = new Label();
            failedLabel = new Label();
            completeHBox = createHBox(completeLabel, "Completed : ", failedLabel, "Failed : ");
            runningLabel = new Label();
            queuedLabel = new Label();
            queuedHBox = createHBox(runningLabel, "Running : ", queuedLabel, "Queued : ");

            progressBar = new ProgressBar();
            progressBar.getStyleClass().add("status_progress");
            progressBar.setPrefWidth(150);
            progressLabel = new Label();
            progressLabel.getStyleClass().add("font_gray");
            progressLabel.setPrefWidth(35);
            progressHBox = new HBox();
            progressHBox.setSpacing(5);
            progressHBox.setAlignment(Pos.CENTER_LEFT);
            progressHBox.setPrefHeight(20);
            progressHBox.getChildren().addAll(progressBar, progressLabel);

            backgroundVBox.getChildren().add(itemVBox);

            this.getChildren().add(backgroundVBox);
        }

        private HBox createHBox(Label label, String titleLabelString) {
            HBox box = new HBox();
            box.setPrefHeight(24);
            Label titleLabel = new Label(titleLabelString);
            titleLabel.setPrefWidth(75);
            titleLabel.getStyleClass().add("font_gray");
            box.getChildren().add(titleLabel);
            box.getChildren().add(label);
            label.setStyle("-fx-text-fill : gray;");
            label.setPrefWidth(120);
            return box;
        }

        private HBox createHBox(Label label1, String titleLabelString1
                ,Label label2, String titleLabelString2) {
            HBox box = new HBox();
            box.setPrefHeight(24);
            Label titleLabel = new Label(titleLabelString1);
            titleLabel.setPrefWidth(75);
            titleLabel.getStyleClass().add("font_gray");
            box.getChildren().addAll(titleLabel, label1);
            label1.setStyle("-fx-text-fill : gray;");
            label1.setPrefWidth(20);

            titleLabel = new Label(titleLabelString2);
            titleLabel.setPrefWidth(75);
            titleLabel.getStyleClass().add("font_gray");
            box.getChildren().addAll(titleLabel, label2);
            label2.setStyle("-fx-text-fill : gray;");
            label2.setPrefWidth(25);
            return box;
        }

        private void setRunStatus(Run run) {
            runName.setText(run.getName());
            /////////////run status 설정
            statusLabel.setText("");
            String commonLabelStyleClass = "label";
            statusLabel.getStyleClass().removeAll(statusLabel.getStyleClass());
            switch (run.getRunStatus().getStatus().toUpperCase()) {
                case AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_QUEUED:
                    statusLabel.getStyleClass().addAll(commonLabelStyleClass, "queued_icon");
                    statusLabel.setText("Q");
                    break;
                case AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_RUNNING:
                    statusLabel.getStyleClass().addAll(commonLabelStyleClass, "run_icon");
                    statusLabel.setText("R");
                    break;
                case AnalysisJobStatusCode.SAMPLE_ANALYSIS_STATUS_COMPLETE:
                    statusLabel.getStyleClass().addAll(commonLabelStyleClass, "complete_icon");
                    statusLabel.setText("C");
                    break;
                default:
                    statusLabel.getStyleClass().addAll(commonLabelStyleClass, "failed_icon");
                    statusLabel.setText("F");
                    break;
            }
            ///////////////////////
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            if(run.getCreatedAt() != null)
                startDateLabel.setText(format.format(run.getCreatedAt().toDate()));
            else
                startDateLabel.setText("");
            if(run.getCompletedAt() != null)
                finishDateLabel.setText(format.format(run.getCompletedAt().toDate()));
            else
                finishDateLabel.setText("");
            RunStatus runStatus = run.getRunStatus();

            panelLabel.setText(run.getPanelName());
            totalLabel.setText(String.valueOf(runStatus.getSampleCount()));
            completeLabel.setText(String.valueOf(runStatus.getCompleteCount()));
            runningLabel.setText(String.valueOf(runStatus.getRunningCount()));
            queuedLabel.setText(String.valueOf(runStatus.getQueuedCount()));
            failedLabel.setText(String.valueOf(runStatus.getFailedCount()));
            if(runStatus.getStatus().equals("FAIL")) {
                progressBar.setProgress(1);
                progressLabel.setText("100%");
            } else {
                progressBar.setProgress(run.getRunStatus().getProgressPercentage() / 100d);
                progressLabel.setText(run.getRunStatus().getProgressPercentage() + "%");
            }

            if(!itemVBox.getChildren().contains(panelHBox))
                itemVBox.getChildren().add(panelHBox);
            if(!itemVBox.getChildren().contains(totalHBox))
                itemVBox.getChildren().add(totalHBox);
            if(!itemVBox.getChildren().contains(startDateHBox))
                itemVBox.getChildren().add(startDateHBox);
            if(!itemVBox.getChildren().contains(finishDateHBox))
                itemVBox.getChildren().add(finishDateHBox);
            if(!itemVBox.getChildren().contains(completeHBox))
                itemVBox.getChildren().add(completeHBox);
            if(!itemVBox.getChildren().contains(queuedHBox))
                itemVBox.getChildren().add(queuedHBox);
            if(!itemVBox.getChildren().contains(progressHBox)) {
                itemVBox.getChildren().add(progressHBox);
            }
            this.run = run;
            this.setCursor(Cursor.HAND);
            this.setEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventEventHandler);
        }

        public void reset() {
            progressBar.setProgress(0);
            runName.setText("");
            statusLabel.getStyleClass().removeAll(startDateHBox.getStyleClass());
            startDateLabel.setText("");
            finishDateLabel.setText("");
            finishDateLabel.setText("");
            completeLabel.setText("");
            queuedLabel.setText("");
            runningLabel.setText("");
            failedLabel.setText("");
            run = null;
            this.setCursor(Cursor.DEFAULT);
            this.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventEventHandler);
        }

    }

    /**
     * 자동 새로고침 시작 처리
     */
    void startAutoRefresh() {
        boolean isAutoRefreshOn = "true".equals(config.getProperty("analysis.job.auto.refresh"));
        logger.debug(String.format("auto refresh on : %s", isAutoRefreshOn));

        if(isAutoRefreshOn) {
            // 갱신 시간 간격
            int refreshPeriodSecond = Integer.parseInt(config.getProperty("analysis.job.auto.refresh.period")) * 1000;
            logger.debug(String.format("auto refresh period second : %s millisecond", refreshPeriodSecond));

            if(autoRefreshTimeline == null) {
                autoRefreshTimeline = new Timeline(new KeyFrame(Duration.millis(refreshPeriodSecond),
                        ae -> showRunList()));
                autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
            } else {
                logger.debug(String.format("[%s] timeline restart", this.getClass().getName()));
                autoRefreshTimeline.stop();
                autoRefreshTimeline.getKeyFrames().removeAll(autoRefreshTimeline.getKeyFrames());
                autoRefreshTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(refreshPeriodSecond),
                        ae -> showRunList()));
            }

            autoRefreshTimeline.play();

        } else {
            if(autoRefreshTimeline != null) {
                autoRefreshTimeline.stop();
            }
        }
    }

    /**
     * 자동 새로고침 일시정지
     */
    void pauseAutoRefresh() {
        boolean isAutoRefreshOn = "true".equals(config.getProperty("analysis.job.auto.refresh"));
        // 기능 실행중인 상태인 경우 실행
        if(autoRefreshTimeline != null && isAutoRefreshOn) {
            logger.debug(String.format("[%s] timeline status : %s", this.getClass().getName(),
                    autoRefreshTimeline.getStatus()));
            // 일시정지
            if(autoRefreshTimeline.getStatus() == Animation.Status.RUNNING) {
                autoRefreshTimeline.pause();
                logger.debug(String.format("[%s] auto refresh pause", this.getClass().getName()));
            }
        }
    }

    /**
     * 자동 새로고침 시작
     */
    void resumeAutoRefresh() {
        boolean isAutoRefreshOn = "true".equals(config.getProperty("analysis.job.auto.refresh"));
        int refreshPeriodSecond = (Integer.parseInt(config.getProperty("analysis.job.auto.refresh.period")) * 1000) / 2;
        // 기능 실행중인 상태인 경우 실행
        if(autoRefreshTimeline != null && isAutoRefreshOn) {
            logger.debug(String.format("[%s] timeline status : %s", this.getClass().getName(),
                    autoRefreshTimeline.getStatus()));
            // 시작
            if(autoRefreshTimeline.getStatus() == Animation.Status.PAUSED) {
                autoRefreshTimeline.playFrom(Duration.millis(refreshPeriodSecond));
                autoRefreshTimeline.play();
                logger.debug(String.format("[%s] auto refresh resume", this.getClass().getName()));
            }
        }
    }


    private void databaseView() {
        try {
            FXMLLoader loader = mainApp.load(FXMLConstants.SYSTEM_MENU_DEFAULT_PUBLIC_DATABASE);
            Node root = loader.load();
            SystemMenuPublicDatabasesController publicDatabasesController = loader.getController();
            publicDatabasesController.setMainController(this.getMainController());
            publicDatabasesController.show((Parent) root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void toolsView() {
        try {
            FXMLLoader loader = mainApp.load(FXMLConstants.SYSTEM_MENU_TOOLS);
            Node root = loader.load();
            PublicToolsController publicToolsController = loader.getController();
            publicToolsController.setMainController(this.getMainController());
            publicToolsController.show((Parent) root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
