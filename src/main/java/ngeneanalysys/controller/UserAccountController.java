package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.User;
import ngeneanalysys.model.UserGroup;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.ValidationUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jang
 * @since 2017-09-05
 */
public class UserAccountController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private Stage dialogStage;

    private APIService apiService;

    /** 로그인 아이디 입력란 */
    @FXML
    private TextField loginIdTextField;
    /** 이름 입력란 */
    @FXML
    private TextField nameTextField;
    /** 비밀번호 */
    @FXML
    private PasswordField passwordField;
    /** 소속기관 입력란 */
    @FXML
    private TextField organizationTextField;
    /** 소속부서 입력란 */
    @FXML
    private TextField departmentTextField;
    /** 주소 입력란 */
    @FXML
    private TextField addressTextField;
    /** 전화번호 입력란 */
    @FXML
    private TextField phoneTextField;
    /** 이메일 입력란 */
    @FXML
    private TextField emailTextField;
    /** User Type 선택 */
    @FXML
    private ComboBox<ComboBoxItem> selectUserType;
    /** 그룹 선택 */
    @FXML
    private ComboBox<ComboBoxItem> selectUserGroup;
    /** 화면 제목 설정 */
    @FXML
    private Label titleLabel;
    /** 사용자 정보 변경시 필요한 사용자 정보 */
    private User user;
    /** 사용자 수정, 추가 구분 */
    private String type;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");

		/*
		apiService = (APIService) getApplicationContext().getBean("apiService");
		apiService.setStage(getMainController().getPrimaryStage());

		selectUserType.setConverter(new ComboBoxConverter());
		selectUserType.getItems().add(new ComboBoxItem());
		selectUserType.getItems().add(new ComboBoxItem(
				LogListStatusCode.USER_TYPE_ADMIN, LogListStatusCode.USER_TYPE_ADMIN));
		selectUserType.getItems().add(new ComboBoxItem(
				LogListStatusCode.USER_TYPE_DOCTOR, LogListStatusCode.USER_TYPE_DOCTOR));
		selectUserType.getItems().add(new ComboBoxItem(
				LogListStatusCode.USER_TYPE_EXPERIMENTER, LogListStatusCode.USER_TYPE_EXPERIMENTER));
		selectUserType.getItems().add(new ComboBoxItem(
				LogListStatusCode.USER_TYPE_RESEARCHER, LogListStatusCode.USER_TYPE_RESEARCHER));
		selectUserType.getSelectionModel().selectFirst();

		groupNameComboBoxCreate();
		*/
        // Create the dialog Stage
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > user account " + type);
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

    public void init(String type, User user) {
        this.type = type;
        this.user = user;

        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        /*selectUserType.setConverter(new ComboBoxConverter());
        selectUserType.getItems().add(new ComboBoxItem());
        selectUserType.getItems().add(new ComboBoxItem(
                LogListStatusCode.USER_TYPE_ADMIN, LogListStatusCode.USER_TYPE_ADMIN));
        selectUserType.getItems().add(new ComboBoxItem(
                LogListStatusCode.USER_TYPE_DOCTOR, LogListStatusCode.USER_TYPE_DOCTOR));
        selectUserType.getItems().add(new ComboBoxItem(
                LogListStatusCode.USER_TYPE_EXPERIMENTER, LogListStatusCode.USER_TYPE_EXPERIMENTER));
        selectUserType.getItems().add(new ComboBoxItem(
                LogListStatusCode.USER_TYPE_RESEARCHER, LogListStatusCode.USER_TYPE_RESEARCHER));
        selectUserType.getSelectionModel().selectFirst();*/

        groupNameComboBoxCreate();

        if("add".equalsIgnoreCase(type)) {
            titleLabel.setText("User Add");
        } else {
            titleLabel.setText("User Modify");
            loginIdTextField.setText(user.getLoginId());
            nameTextField.setText(user.getName());
            organizationTextField.setText(user.getOrganization());
            departmentTextField.setText(user.getDepartment());
            addressTextField.setText(user.getAddress());
            phoneTextField.setText(user.getPhone());
            emailTextField.setText(user.getEmail());
            /*final Optional<ComboBoxItem> userGroup = selectUserGroup.getItems().stream().filter(
                    item -> item.getValue().equals(user.getUserGroup().toString())).findFirst();*/
           /* if(userGroup.isPresent()) {
                selectUserGroup.setValue(userGroup.get());
            }

            final Optional<ComboBoxItem> userType = selectUserType.getItems().stream().filter(
                    item -> item.getText().equals(user.getUserType())).findFirst();
            if(userType.isPresent()) {
                selectUserType.setValue(userType.get());
            }*/
        }
    }

    @FXML
    public void save() {
        Map<String,Object> params = null;

        if(selectUserType.getSelectionModel().getSelectedIndex() == 0 || selectUserType.getValue() == null) {
            selectUserType.requestFocus();
        } else if(selectUserGroup.getSelectionModel().getSelectedIndex() == 0 || selectUserGroup.getValue() == null) {
            selectUserGroup.requestFocus();
        } else if(ValidationUtil.text(loginIdTextField.getText(), "login ID", 4, 8, null, null, true, dialogStage) > 0) {
            loginIdTextField.requestFocus();
        } else if(ValidationUtil.text(nameTextField.getText(), "name", 4, 8, null, null, true, dialogStage) > 0) {
            nameTextField.requestFocus();
        } else if(!StringUtils.isEmpty(organizationTextField.getText()) && ValidationUtil.text(organizationTextField.getText(), "organization", -1, -1, null, null, true, dialogStage) > 0) {
            organizationTextField.requestFocus();
        } else if(!StringUtils.isEmpty(departmentTextField.getText()) && ValidationUtil.text(departmentTextField.getText(), "department", -1, -1, null, null, true, dialogStage) > 0) {
            departmentTextField.requestFocus();
        } else if(!StringUtils.isEmpty(addressTextField.getText()) && ValidationUtil.text(addressTextField.getText(), "address", -1, -1, null, null, true, dialogStage) > 0) {
            addressTextField.requestFocus();
        } else if(!StringUtils.isEmpty(phoneTextField.getText()) && ValidationUtil.text(phoneTextField.getText(), "phone", -1, -1, null, null, true, dialogStage) > 0) {
            phoneTextField.requestFocus();
        } else if(!StringUtils.isEmpty(emailTextField.getText()) && ValidationUtil.text(emailTextField.getText(), "email", -1, -1, null, null, true, dialogStage) > 0) {
            emailTextField.requestFocus();
        } else if(validPwdInput()) {
            params = new HashMap<>();
            params.put("user_type", selectUserType.getSelectionModel().getSelectedItem().getValue());
            params.put("user_group", selectUserGroup.getSelectionModel().getSelectedItem().getValue());
            params.put("login_id", loginIdTextField.getText());
            params.put("login_password", passwordField.getText());
            params.put("name", nameTextField.getText());

            if(!StringUtils.isEmpty(organizationTextField.getText())) {
                params.put("organization", organizationTextField.getText());
            }
            if(!StringUtils.isEmpty(departmentTextField.getText())) {
                params.put("department", departmentTextField.getText());
            }
            if(!StringUtils.isEmpty(addressTextField.getText())) {
                params.put("address", addressTextField.getText());
            }
            if(!StringUtils.isEmpty(phoneTextField.getText())) {
                params.put("phone", phoneTextField.getText());
            }
            if(!StringUtils.isEmpty(emailTextField.getText())) {
                params.put("email", emailTextField.getText());
            }

            try {
                if("add".equalsIgnoreCase(type)) {
                    apiService.post("/users/create", params, null, true);
                    DialogUtil.alert("Create User Account Success", "A user account has been created.",
                            dialogStage, true);
                } else {
                    apiService.patch("/users/update/" + user.getId(), params, null, true);
                    DialogUtil.alert("Modify User Account Success", "A user account has been modified.",
                            dialogStage, true);
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

    /**
     * 비밀번호 입력폼 유효성 체크
     * @return
     */
    public boolean validPwdInput() {
        if(ValidationUtil.text(passwordField.getText(), "password", 7, -1, "([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])|([!,@,#,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])", null, false, dialogStage) > 0) {
            DialogUtil.warning("Incorrect password combination.", "Please enter at least 8 characters with a combination of English, numbers and special characters.", dialogStage, true);
            // 입력 내용 삭제
            passwordField.setText(null);
            // 포커스 이동
            passwordField.requestFocus();
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public void groupNameComboBoxCreate() {
        selectUserGroup.setConverter(new ComboBoxConverter());
        selectUserGroup.getItems().add(new ComboBoxItem());

        HttpClientResponse response = null;

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("format", "json");

            response = apiService.get("/users/group_name", param, null, false);
            logger.info(response.getContentString());
            if(response != null) {
                List<UserGroup> list = (List<UserGroup>) response.getMultiObjectBeforeConvertResponseToJSON(UserGroup.class, false);

                /*for(UserGroup group : list) {
                    selectUserGroup.getItems().add(new ComboBoxItem(
                            group.getId().toString(), group.getGroup_name()));
                }*/

                selectUserGroup.getSelectionModel().selectFirst();
            }

        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    public void modifySetting() {
        titleLabel.setText("User Modify");
        logger.info("in modify");
    }

}
