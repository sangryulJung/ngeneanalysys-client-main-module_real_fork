package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.PipelineAnnotationDatabase;
import ngeneanalysys.model.PipelineVersion;
import ngeneanalysys.model.PipelineVersionView;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2018-04-11
 */
public class PublicDatabaseController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private ComboBox<ComboBoxItem> versionComboBox;

    private Integer panelId;

    private APIService apiService;

    private Stage dialogStage;

    private List<PipelineVersionView> list;

    /**
     * @param panelId Integer
     */
    public void setPanelId(Integer panelId) {
        this.panelId = panelId;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");

        apiService = APIService.getInstance();

        // Create the dialog Stage
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Public Databases");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());
        dialogStage.resizableProperty().setValue(false);

        setVersionComboBox();

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void setList(String id) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("pipelineVersionId", Integer.valueOf(id));
            HttpClientResponse response = apiService.get("/pipelineVersions/annotationDatabases", params, null, null);

            List<PipelineAnnotationDatabase> list = (List<PipelineAnnotationDatabase>)response.getMultiObjectBeforeConvertResponseToJSON(PipelineAnnotationDatabase.class, false);

            if(list != null && !list.isEmpty()) {
                for(PipelineAnnotationDatabase pipelineAnnotationDatabase : list) {

                }
            }
        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(), dialogStage, true);
        }
    }

    private void createGridRow(PipelineAnnotationDatabase pipelineAnnotationDatabase) {

    }

    private void setVersionComboBox() {
        versionComboBox.setConverter(new ComboBoxConverter());
        try {
            HttpClientResponse response = apiService.get("/panels/" + this.panelId + "/pipelineVersions", null, null, null);

            List<PipelineVersionView> list = (List<PipelineVersionView>)response.getMultiObjectBeforeConvertResponseToJSON(PipelineVersionView.class, false);
            this.list = list;
            if(list != null && !list.isEmpty()) {
                for(PipelineVersionView pipelineVersionView : list) {
                    versionComboBox.getItems().add(new ComboBoxItem(pipelineVersionView.getId().toString(), pipelineVersionView.getVersion()));
                }
            }
        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(), dialogStage, true);
        }
    }

}
