package ngeneanalysys.controller.systemManager;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.CustomDatabase;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jang
 * @since 2019-06-20
 */
public class SystemManagerCustomDatabaseController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private TextArea contentsTextArea;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    private APIService apiService;

    private Stage dialogStage;

    private Integer panelId;

    private Integer customDatabaseId = 0;

    /**
     * @param panelId
     */
    public void setPanelId(Integer panelId) {
        this.panelId = panelId;
    }

    public void setCustomDatabase(CustomDatabase customDatabase) {
        this.panelId = customDatabase.getPanelId();
        titleTextField.setText(customDatabase.getTitle());
        descriptionTextField.setText(customDatabase.getDescription());
        contentsTextArea.setText(customDatabase.getContents());
        customDatabaseId = customDatabase.getId();
    }

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();

        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Custom Database");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());
        dialogStage.setResizable(false);

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private boolean stringCheck(String[] str) {
        return str.length == 5 && str[1].matches("[0-9]*") && str[2].matches("[A-Za-z]*")
                && str[3].matches("[A-Za-z]*") && str[4].matches("[^;:=]*");
    }

    @FXML
    private void save() {
        String title = titleTextField.getText();
        String description = descriptionTextField.getText();
        String contents = contentsTextArea.getText();

        if(!contents.split("\t")[0].equalsIgnoreCase("Chr")) {
            contents = "Chr\tStart\tRef\tAlt\tAnnotaion\n" + contents;
        }

        String[] lineSplit = contents.split("\n");
        String[] testLine = Arrays.copyOfRange(lineSplit, 1, lineSplit.length);

        if(StringUtils.isNotEmpty(title) &&
                Arrays.stream(testLine).map(t -> t.split("\t")).allMatch(this::stringCheck)) {
            logger.info("success");
            Map<String, Object> params = new HashMap<>();
            params.put("panelId", panelId);
            params.put("title", title);
            params.put("description", description);
            params.put("contents", contents);
            if(customDatabaseId > 0) {
                try {
                    params.put("id", customDatabaseId);
                    apiService.put("/admin/panels/customDatabase/" + panelId + "/" + customDatabaseId, params, null, true);
                    dialogStage.close();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            } else {
                try {
                    apiService.post("/admin/panels/customDatabase/" + panelId, params, null, true);
                    dialogStage.close();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        } else {
            logger.info("failed");
        }
    }

    @FXML
    private void cancel() {
        dialogStage.close();
    }
}
