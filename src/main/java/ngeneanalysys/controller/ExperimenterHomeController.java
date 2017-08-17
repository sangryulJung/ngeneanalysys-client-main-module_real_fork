package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.PagedRun;
import ngeneanalysys.model.Run;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

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
    private GridPane runGrid;

    @FXML
    private GridPane sampleGrid;

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

    /** API Service */
    private APIService apiService;


    @Override
    public void show(Parent root) throws IOException {
        logger.info("ExperimenterHomeController show..");

        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        this.mainController.getMainFrame().setCenter(root);


    }

    /**
     * 분석 요청 Dialog 창 출력
     */
    @FXML
    public void showUploadFASTQ() {
        try {
            // Load the fxml file and create a new stage for the popup dialog
            FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_FIRST);
            Pane page = loader.load();
            SampleUploadScreenFirst controller = loader.getController();
            controller.setMainController(this.mainController);
            controller.setExperimentHomeController(this);
            CompletableFuture<PagedRun> getPagedRun = new CompletableFuture<>();
            getPagedRun.supplyAsync(() -> {
                HttpClientResponse response = null;
                try {
                    Map<String, Object> params = new HashMap<>();
                    //            params.put("name", "blablabla1");
                    //            params.put("sequencingPlatform", "MISEQ");
                    //            response = apiService.post("/runs", params, null, true);
                    //            Run run1 = response.getObjectBeforeConvertResponseToJSON(Run.class);
                    //            logger.info(run1.toString());
                    //            params.put("name", "blablabla2");
                    //            response = apiService.post("/runs", params, null, true);
                    //            Run run2 = response.getObjectBeforeConvertResponseToJSON(Run.class);
                    //            logger.info(run2.toString());
                    //            params.clear();
                    params.put("limit", 5);
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
                PagedRun p = getPagedRun.get();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            controller.show(page);
                        } catch (IOException e) {
                            logger.error("error", e);
                        }
                    }
                });
            } catch (Exception e){

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
