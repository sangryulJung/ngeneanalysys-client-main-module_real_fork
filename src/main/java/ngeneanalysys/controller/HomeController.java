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
import javafx.scene.layout.*;
import javafx.util.Duration;
import ngeneanalysys.animaition.HddStatusTimer;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedRun;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
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

        homeWrapper.getChildren().add(maskerPane);
        maskerPane.setPrefWidth(getMainController().getMainFrame().getWidth());
        maskerPane.setPrefHeight(getMainController().getMainFrame().getHeight());
        maskerPane.setVisible(false);

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
        final int maxRunNumberOfPage = 10;
        try {
            runList = new ArrayList<>();
            for (int i = 0; i < maxRunNumberOfPage; i++) {
                RunStatusVBox box = new RunStatusVBox();
                runList.add(box);
                runListHBox.setPrefWidth(runListHBox.getPrefWidth() + 240);
                runListHBox.getChildren().add(box);
                runListHBox.setSpacing(5);
                AnimationTimer hddStatusTier = new HddStatusTimer(hddCanvas.getGraphicsContext2D(), 0.2, "Usage",
                "4.32/20 TB", 10);
                hddStatusTier.start();
            }
        } catch (Exception e) {
            logger.error("HOME -> initRunListLayout", e);
        }
    }

    private void showRunList() {
        final int maxRunNumberOfPage = 10;
        CompletableFuture<PagedRun> getPagedRun = new CompletableFuture<>();
        CompletableFuture.supplyAsync(() -> {
            HttpClientResponse response;
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
                runList.get(i).setRunStatus(run);

            }
            for(int i = runCount; i < maxRunNumberOfPage; i++){
                runList.get(i).reset();
            }
        } catch (Exception e) {
            logger.error("HOME -> SHOW RUN LIST", e);
        }
    }

    class RunStatusVBox extends VBox {
        private Label runName;
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
        private Label FailedLabel;
        private HBox FailedHBox;

        private VBox itemVBox;

        public RunStatusVBox() {
            this.setPrefSize(220, 220);
            this.setMinSize(220, 220);
            this.setStyle("-fx-effect: dropshadow(gaussian, rgba(0.0, 0.0, 0.0, 0.5), 0.5, 0.5, 0.0, 0.0);" +
                    "-fx-background-color: white;");
            HBox topHBox = new HBox();
            topHBox.setPrefHeight(25);
            runName = new Label();
            runName.setMinWidth(190);
            runName.setPrefWidth(190);
            statusLabel = new Label();
            statusLabel.setMinWidth(18);
            statusLabel.setPrefWidth(18);

            topHBox.getChildren().add(runName);
            topHBox.getChildren().add(statusLabel);
            Insets runNameInsets = new Insets(4,0,0,10);
            runName.setPadding(runNameInsets);
            Insets statusInsets = new Insets(4,0,0,0);
            statusLabel.setPadding(statusInsets);
            topHBox.setAlignment(Pos.CENTER_LEFT);
            this.getChildren().add(topHBox);

            VBox backgroundVBox = new VBox();
            Insets insets = new Insets(7,7,7,7);
            backgroundVBox.setPadding(insets);

            itemVBox = new VBox();
            itemVBox.setStyle("-fx-background-color : f0f0f0;");
            itemVBox.setPrefHeight(190);
            Insets itemInsets = new Insets(10,0,0,10);
            itemVBox.setPadding(itemInsets);
            startDateLabel = new Label();
            startDateHBox = createHBox(startDateLabel, "Start Date : ");
            FinishDateLabel = new Label();
            FinishDateHBox = createHBox(FinishDateLabel, "Finish Date: ");
            completeLabel = new Label();
            completeHBox = createHBox(completeLabel, "Complete : ");
            queuedLabel = new Label();
            queuedHBox = createHBox(queuedLabel, "Queued : ");
            runningLabel = new Label();
            runningHBox = createHBox(runningLabel, "Running : ");
            FailedLabel = new Label();
            FailedHBox = createHBox(FailedLabel, "Failed : ");

            backgroundVBox.getChildren().add(itemVBox);

            this.getChildren().add(backgroundVBox);


        }

        public HBox createHBox(Label label, String titleLabelString) {
            HBox box = new HBox();
            box.setPrefHeight(20);
            Label titleLabel = new Label(titleLabelString);
            titleLabel.setStyle("-fx-text-fill : gray; -fx-font-family : Noto Sans CJK KR Regular;");
            box.getChildren().add(titleLabel);
            box.getChildren().add(label);

            return box;
        }

        public void setRunStatus(Run run) {
            runName.setText(run.getName());
            /////////////run status 설정
            if(run.getStatus().toUpperCase().equals("QUEUED")) {
                statusLabel.getStyleClass().add("queued_icon");
            } else if(run.getStatus().toUpperCase().equals("RUNNING")) {
                statusLabel.getStyleClass().add("run_icon");
            } else if(run.getStatus().toUpperCase().equals("COMPLETE")) {
                statusLabel.getStyleClass().add("complete_icon");
            }
            ///////////////////////
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            if(run.getCreatedAt() != null)
                startDateLabel.setText(format.format(run.getCreatedAt().toDate()));
            if(run.getCompletedAt() != null)
                FinishDateLabel.setText(format.format(run.getCompletedAt().toDate()));
            if(!itemVBox.getChildren().contains(startDateHBox))
                itemVBox.getChildren().add(startDateHBox);
            if(!itemVBox.getChildren().contains(FinishDateHBox))
                itemVBox.getChildren().add(FinishDateHBox);

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
            FailedLabel.setText("");
            //itemVBox.getChildren().removeAll(itemVBox.getChildren());
        }

    }

}
