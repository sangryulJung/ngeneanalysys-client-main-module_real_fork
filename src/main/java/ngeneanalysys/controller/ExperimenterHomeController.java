package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private GridPane runListGridPane;

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

    private List<TextField> runNameFields;
    private List<VBox> runStatusFields;
    private List<TextField> runPanelFields;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("ExperimenterHomeController show..");

        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        this.mainController.getMainFrame().setCenter(root);
        initRunListLayout();
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
        CompletableFuture<PagedRun> getPagedRun = new CompletableFuture<>();
        getPagedRun.supplyAsync(() -> {
            HttpClientResponse response = null;
            try {
                Map<String, Object> params = new HashMap<>();
                try {
                    params.put("name", "blablabla1");
                    params.put("sequencingPlatform", "MISEQ");
                    response = apiService.post("/runs", params, null, true);
                    Run run1 = response.getObjectBeforeConvertResponseToJSON(Run.class);
                    logger.info(run1.toString());
                } catch (Exception e) {

                }
                try {
                    params.put("name", "blablabla2");
                    response = apiService.post("/runs", params, null, true);
                    Run run2 = response.getObjectBeforeConvertResponseToJSON(Run.class);
                    logger.info(run2.toString());
                } catch(Exception e) {

                }
                try {
                    params.put("name", "blablabla3");
                    response = apiService.post("/runs", params, null, true);
                    Run run2 = response.getObjectBeforeConvertResponseToJSON(Run.class);
                    logger.info(run2.toString());
                } catch(Exception e) {

                }
                try {
                    params.put("name", "blablabla4");
                    response = apiService.post("/runs", params, null, true);
                    Run run2 = response.getObjectBeforeConvertResponseToJSON(Run.class);
                    logger.info(run2.toString());
                } catch(Exception e) {

                }
                try {
                    params.put("name", "blablabla5");
                    response = apiService.post("/runs", params, null, true);
                    Run run2 = response.getObjectBeforeConvertResponseToJSON(Run.class);
                    logger.info(run2.toString());
                } catch(Exception e) {

                }
                try {
                    params.put("name", "blablabla6");
                    response = apiService.post("/runs", params, null, true);
                    Run run2 = response.getObjectBeforeConvertResponseToJSON(Run.class);
                    logger.info(run2.toString());
                } catch(Exception e) {

                }
                try {
                    params.put("name", "blablabla7");
                    response = apiService.post("/runs", params, null, true);
                    Run run2 = response.getObjectBeforeConvertResponseToJSON(Run.class);
                    logger.info(run2.toString());
                } catch(Exception e) {

                }
                params.clear();
                params.put("limit", runListGridPane.getRowConstraints().size());
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
            for(int i = 0; i < pagedRun.getCount(); i++) {
                runNameFields.get(i).setText(pagedRun.getResult().get(i).getName());
                runStatusFields.get(i).setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.DASHED, new CornerRadii(5), new BorderWidths(5))));
            }
            for(int i = pagedRun.getCount(); i < runListGridPane.getRowConstraints().size(); i++){
                runNameFields.get(i).setText("");
                runStatusFields.get(i).setBorder(null);
            }
        } catch (Exception e) {
            logger.error("HOME -> SHOW RUN LIST", e);
        }
    }

    private void initRunListLayout() {
        try {
            runNameFields = new ArrayList<>();
            runStatusFields = new ArrayList<>();
            for (int i = 0; i < runListGridPane.getRowConstraints().size(); i++) {
                runNameFields.add(new TextField());
                runListGridPane.add(runNameFields.get(i), 0, i);
                runStatusFields.add(new VBox());
                runListGridPane.add(runStatusFields.get(i), 1, i);
            }
        } catch (Exception e) {
            logger.error("HOME -> initRunListLayout", e);
        }
    }
}
