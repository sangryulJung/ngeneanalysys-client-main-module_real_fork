package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.VirtualPanel;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jang
 * @since 2018-01-05
 */
public class VirtualPanelEditController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private TextField essentialGenesTextField;

    @FXML
    private TextField optionalGenesTextField;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;

    private Stage dialogStage;

    private Integer virtualPanelId = null;

    private Integer panelId = null;

    private ComboBox<ComboBoxItem> comboBox;

    /**
     * @param comboBox
     */
    public void setComboBox(ComboBox<ComboBoxItem> comboBox) {
        this.comboBox = comboBox;
    }

    /**
     * @param panelId
     */
    public void setPanelId(Integer panelId) {
        this.panelId = panelId;
    }

    public void settingVirtualPanelContents(Integer virtualPanelId) {
        if(apiService == null) apiService = APIService.getInstance();

        try {
            HttpClientResponse response = apiService.get("admin/virtualPanels/" + virtualPanelId, null, null, false);

            this.virtualPanelId = virtualPanelId;

            VirtualPanel virtualPanel = response.getObjectBeforeConvertResponseToJSON(VirtualPanel.class);

            nameTextField.setText(virtualPanel.getName());
            descriptionTextField.setText(virtualPanel.getDescription());
            essentialGenesTextField.setText(virtualPanel.getEssentialGenes());
            optionalGenesTextField.setText(virtualPanel.getOptionalGenes());

        } catch (WebAPIException wae) {

        }
    }

    @Override
    public void show(Parent root) throws IOException {

        logger.info("show virtual Panel Edit");
        // Create the dialog Stage

        apiService = APIService.getInstance();

        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Virtual Panel Edit");
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

    @FXML
    public void submit() {
        try {

            Map<String, Object> params = new HashMap<>();

            if(StringUtils.isEmpty(nameTextField.getText())) {
                return;
            } else if(StringUtils.isEmpty(essentialGenesTextField.getText())) {
                return;
            }

            params.put("panelId", panelId);
            params.put("name", nameTextField.getText());
            params.put("description", descriptionTextField.getText());
            params.put("essentialGenes", essentialGenesTextField.getText());
            params.put("optionalGenes", optionalGenesTextField.getText());

            HttpClientResponse response;

            if(virtualPanelId == null) {
                response = apiService.post("/admin/virtualPanels", params, null, true);
            } else {
                response = apiService.put("/admin/virtualPanels/"  + virtualPanelId, params, null, true);
            }

            VirtualPanel virtualPanel = response.getObjectBeforeConvertResponseToJSON(VirtualPanel.class);

            if(virtualPanelId != null) {
                Optional<ComboBoxItem> item = comboBox.getItems().stream().filter(comboBoxItem ->
                    comboBoxItem.getValue().equals(virtualPanel.getId().toString())).findFirst();

                if(item.isPresent()) {
                    item.get().setText(virtualPanel.getName());
                }
            } else {
                comboBox.getItems().add(new ComboBoxItem(virtualPanel.getId().toString(), virtualPanel.getName()));
            }

            dialogStage.close();
        } catch (WebAPIException wae) {
            wae.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }
}
