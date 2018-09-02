package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.NGeneAnalySysVersion;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-08-01
 */
public class SystemMenuSoftwareVersionController extends SubPaneController {
    private static final Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    @FXML
    private Label systemVersionLabel;

    @FXML
    private Label apiServerVersionLabel;

    @FXML
    private Label dockerVersionLabel;

    @FXML
    private Label guiClientVersionLabel;

    @FXML
    private Label brcaVersionLabel;

    @FXML
    private Label hemeVersionLabel;

    @FXML
    private Label solidVersionLabel;

    @FXML
    private Label heredVersionLabel;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");

        apiService = APIService.getInstance();

        setVersionInfo();

        // Create the dialog Stage
        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Software Version");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());

        // Scene Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }

    private void setVersionInfo() {
        try {
            HttpClientResponse response = apiService.get("", null, null, null);
            NGeneAnalySysVersion nGeneAnalySysVersion = response.getObjectBeforeConvertResponseToJSON(NGeneAnalySysVersion.class);
            systemVersionLabel.setText("v" + nGeneAnalySysVersion.getSystem());
            apiServerVersionLabel.setText("v" + nGeneAnalySysVersion.getApiServer());
            String buildVersion = config.getProperty("application.version");
            String buildDate = config.getProperty("application.build.date");
            guiClientVersionLabel.setText("v" + String.format("%s (Build Date %s)", buildVersion, buildDate));

            dockerVersionLabel.setText("v" + nGeneAnalySysVersion.getPipelineDocker());
            brcaVersionLabel.setText("v" + nGeneAnalySysVersion.getPipelines().getBrcaAccuTest());
            hemeVersionLabel.setText("v" + nGeneAnalySysVersion.getPipelines().getHemeAccuTest());
            solidVersionLabel.setText("v" + nGeneAnalySysVersion.getPipelines().getSolidAccuTest());
            heredVersionLabel.setText("v" + nGeneAnalySysVersion.getPipelines().getHeredAccuTest());
        } catch (WebAPIException wae) {
            logger.debug(wae.getMessage());
        }
    }
}
