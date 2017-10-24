package ngeneanalysys.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.User;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-08-10
 */
public class SystemMenuEditController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    /** 현재 비밀번호 입력란 */
    @FXML
    private PasswordField currentPasswordField;
    /** 새 비밀번호 입력란 */
    @FXML
    private PasswordField newPasswordField;
    /** 새 비밀번호 확인 입력란 */
    @FXML
    private PasswordField confirmPasswordField;
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

    private Stage dialogStage;

    /** 현재 사용중인 비밀번호 */
    private String currentPwd;
    /** 사용자 정보 */
    private User user;
    /** api 서비스 */
    private APIService apiService;

    /**
     * 화면 출력
     */
    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        // API Service Init
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        // 현재 로그인 세션 정보 조회
        loginSession = LoginSessionUtil.getCurrentLoginSession();
        //currentPwd = loginSession.getUser().getLoginPassword();

        // Create the dialog Stage
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Edit");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());

        // 현재 비밀번호 입력란 포커스 이동시 리스너 바인딩
        /*currentPasswordField.focusedProperty().addListener((ov, t, t1) -> {
                if(!t1) {	// focus out
                    if(!StringUtils.isEmpty(currentPasswordField.getText())) {
                        validCurrentPwdInput();
                    }
                }
        });*/

        // 새 비밀번호 입력란 포커스 이동시 리스너 바인딩
        newPasswordField.focusedProperty().addListener((ov, t,t1) -> {
                if(!t1) {	// focus out
                    if(!StringUtils.isEmpty(newPasswordField.getText())) {
                        validNewPwdInput();
                    }
                }
        });

        // 새 비밀번호 확인 입력란 포커스 이동시 리스너 바인딩
        confirmPasswordField.focusedProperty().addListener((ov, t, t1) -> {
                if(!t1) {	// focus out
                    if(!StringUtils.isEmpty(confirmPasswordField.getText()) && !StringUtils.isEmpty(newPasswordField.getText())) {
                        validConfirmPwdInput();
                    }
                }
        });
        try {
            // 사용자 정보 조회
            Map<String, Object> params = new HashMap<>();

            HttpClientResponse response = apiService.get("/member", getParamMap(), null,
                    false);

            user = response.getObjectBeforeConvertResponseToJSON(User.class);
            organizationTextField.setText(user.getOrganization());
            departmentTextField.setText(user.getDepartment());
            addressTextField.setText(user.getAddress());
            phoneTextField.setText(user.getPhone());
            emailTextField.setText(user.getEmail());
        } catch (WebAPIException wae) {
            // DialogUtil.error("Error Search User Information", "An error
            // occurred during search.", dialogStage, true);
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
    /**
     * 새 비밀번호 입력폼 유효성 체크
     * @return
     */
    public boolean validNewPwdInput() {
        if(ValidationUtil.text(newPasswordField.getText(), "password", 7, -1, "([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])|([!,@,#,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])", null, false, dialogStage) > 0) {
            DialogUtil.warning("Incorrect password combination.", "Please enter at least 8 characters with a combination of English, numbers and special characters.", dialogStage, true);
            // 입력 내용 삭제
            newPasswordField.setText(null);
            confirmPasswordField.setText(null);
            // 포커스 이동
            newPasswordField.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * 새 비밀번호 확인 입력폼 유효성 체크
     * @return
     */
    public boolean validConfirmPwdInput() {
        if(!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            DialogUtil.warning("New Password Mismatch.", "The password entered does not match the newly input password.", dialogStage, true);
            // 입력 내용 삭제
            confirmPasswordField.setText(null);
            // 포커스 이동
            confirmPasswordField.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * 저장
     * @param event
     */
    @FXML
    public void save(ActionEvent event) {
        if(user != null && user.getId() > 0) {
            Map<String,Object> params = null;
            // 개인정보 변경시 비밀번호는 반드시 입력 되어야함
            if(!StringUtils.isEmpty(currentPasswordField.getText())) {
                params = new HashMap<>();
                params.put("name", loginSession.getName());
                params.put("currentLoginPassword", currentPasswordField.getText());
                if (!StringUtils.isEmpty(newPasswordField.getText()) && validNewPwdInput() && validConfirmPwdInput())
                    params.put("newLoginPassword", newPasswordField.getText());

                    /*try {
                        apiService.patch("/users/password/" + user.getId(), params, null, true);

                        //loginSession.getUser().setLoginPassword(newPasswordField.getText());
                        // 캐시 메모리 서비스 할당
                        CacheMemoryService cacheMemoryService = CacheMemoryService.getInstance();
                        // 캐시 메모리내 세션 정보 삽입
                        cacheMemoryService.setCacheObject(CommonConstants.SESSION_CACHE_SET_NAME,
                                CommonConstants.SESSION_CACHE_KEY_NAME, loginSession);
                    } catch (WebAPIException wae) {
                        DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                                getMainApp().getPrimaryStage(), true);
                    } catch (Exception e) {
                        DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
                    }*/

                if (!StringUtils.isEmpty(organizationTextField.getText())) {
                    params.put("organization", organizationTextField.getText());
                }
                if (!StringUtils.isEmpty(departmentTextField.getText())) {
                    params.put("department", departmentTextField.getText());
                }
                if (!StringUtils.isEmpty(addressTextField.getText())) {
                    params.put("address", addressTextField.getText());
                }
                if (!StringUtils.isEmpty(phoneTextField.getText())) {
                    params.put("phone", phoneTextField.getText());
                }
                if (!StringUtils.isEmpty(emailTextField.getText())) {
                    params.put("email", emailTextField.getText());
                }

                try {
                    apiService.put("/member", params, null, true);

                    DialogUtil.alert("Update User Information Success", "Your Information has been updated.",
                            dialogStage, true);
                    dialogStage.close();
                } catch (WebAPIException wae) {
                    wae.printStackTrace();
                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                            getMainApp().getPrimaryStage(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
                }

                // 유효성 체크
                /*if (ValidationUtil.text(organizationTextField.getText(), "organization", -1, -1, null, null, true, dialogStage) > 0) {
                    organizationTextField.requestFocus();
                } else if (ValidationUtil.text(departmentTextField.getText(), "department", -1, -1, null, null, true, dialogStage) > 0) {
                    departmentTextField.requestFocus();
                } else if (ValidationUtil.text(addressTextField.getText(), "address", -1, -1, null, null, true, dialogStage) > 0) {
                    addressTextField.requestFocus();
                } else if (ValidationUtil.text(phoneTextField.getText(), "phone", -1, -1, null, null, true, dialogStage) > 0) {
                    phoneTextField.requestFocus();
                } else if (ValidationUtil.text(emailTextField.getText(), "email", -1, -1, null, null, true, dialogStage) > 0) {
                    emailTextField.requestFocus();
                } else {
                    // 사용자 정보 수정
                    params = new HashMap<>();
                    params.put("organization", organizationTextField.getText());
                    params.put("department", departmentTextField.getText());
                    params.put("address", addressTextField.getText());
                    params.put("phone", phoneTextField.getText());
                    params.put("email", emailTextField.getText());
                    try {
                        apiService.put("/member", params, null, true);

                        DialogUtil.alert("Update User Information Success", "Your Information has been updated.",
                                dialogStage, true);
                        dialogStage.close();
                    } catch (WebAPIException wae) {
                        DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                                getMainApp().getPrimaryStage(), true);
                    } catch (Exception e) {
                        DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
                    }
                }
            } else {
                DialogUtil.error(null, "Empty User Information", dialogStage, false);
            }*/
            }
        }
    }

    @FXML
    public void closeDialog() {
        dialogStage.close();
    }
}
