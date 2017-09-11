package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.UserGroup;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.ValidationUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-09-06
 */
public class GroupAddController extends SubPaneController{
    private static Logger logger = LoggerUtil.getLogger();

    private Stage dialogStage;

    private APIService apiService;

    @FXML
    private TextField groupNameTextField;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private Label titleText;

    private String type;

    private UserGroup group = null;

    public void init(String type, UserGroup group) {
        this.type = type;
        this.group = group;
        if("add".equalsIgnoreCase(type)) {
            titleText.setText("Group Add");
        } else {
            titleText.setText("Group Modify");
            groupNameTextField.setText(group.getName());
        }
    }

    @Override
    public void show(Parent root) throws IOException {

        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > group " + type);
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.showAndWait();
    }

    @FXML
    public void closeDialog() {
        dialogStage.close();
    }

    @FXML
    public void save() {
        Map<String,Object> params = null;

        if(ValidationUtil.text(groupNameTextField.getText(), "Group name", 4, 16, null, null, true, dialogStage) > 0) {
            groupNameTextField.requestFocus();
        } else {
            params = new HashMap<>();
            params.put("name", groupNameTextField.getText());

            try {
                if("add".equalsIgnoreCase(type)) {
                    apiService.post("/admin/member_groups", params, null, true);
                    DialogUtil.alert("Create User Group Success", "A user group has been created.",
                            dialogStage, true);
                } else {
                    /*if(group != null) {
                        apiService.patch("/users/group/" + group.getId(), params, null, true);
                        DialogUtil.alert("Modify User Group Success", "A user group has been modified.",
                                dialogStage, true);
                    }*/
                }
                dialogStage.close();
            } catch (WebAPIException wae) {
                DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                        getMainApp().getPrimaryStage(), true);
            } catch (Exception e) {
                DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
            }
        }
    }
}