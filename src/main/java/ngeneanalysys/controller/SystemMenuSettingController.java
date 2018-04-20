package ngeneanalysys.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.controller.fragment.InSilicoPredictionsController;
import ngeneanalysys.service.PropertiesService;
import ngeneanalysys.service.ServerURLManageService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.PropertiesUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;


/**
 * @author Jang
 * @since 2017-08-10
 */
public class SystemMenuSettingController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    /** 서버 URL 설정 관리 서비스 */
    private ServerURLManageService serverURLManageService;

    /** 서버 URL Text Field */
    @FXML
    private TextField serverURLTextField;
    /** 기본에 저장된 서버 URL */
    private String originServerURL;

    /** 서버 URL 확인 버튼 */
    @FXML
    private Button serverURLConfirmButton;

    /** 샘플 목록 조회 자동 새로고침 체크박스 */
    @FXML
    private CheckBox autoRefreshCheckBox;

    /** 샘플 목록 조회 자동 새로고침 시간간격 콤보박스 */
    @FXML
    private ComboBox<String> autoRefreshPeriodComboBox;
    
    /** 윈도우 테마 콤보박스 */
    @FXML
    private ComboBox<String> windowTheme;

    /** 설정 저장 버튼 */
    @FXML
    private Button saveButton;

    /** 창 닫기 버튼 */
    @FXML
    private Button closeButton;
    
    /** 로그인창 테마변경용 */
    
    /** 메인창 테마변경용 */
    

    /** this dialog window's stage object */
    private Stage dialogStage;
    
    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");
        
        windowTheme.valueProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				getMainController().applyTheme(windowTheme.getValue());
				//getLoginController().applyLoginTheme(windowTheme.getValue());
				
				
//				try {
//                    FXMLLoader loader = mainApp.load(FXMLConstants.MAIN);
//                    Node node = loader.load();
//                    MainController mainController = loader.getController();
//                    mainController.applyTheme();
//                   
//                    
//                   
//                } catch (IOException ioe) {
//                    ioe.printStackTrace();
//                }

			}
		});
        
        config = PropertiesUtil.getSystemDefaultProperties();
        serverURLManageService = ServerURLManageService.getInstance();
        
        // server url value
        originServerURL = config.getProperty("default.server.host");
        serverURLTextField.setText(config.getProperty("default.server.host"));

        // 어플리케이션이 "RUO" 모드인 경우 서버 URL 설정 비활성화 처리함.
        //serverURLTextField.setDisable(true);
        //serverURLConfirmButton.setDisable(true);

        // check auto refresh
        autoRefreshCheckBox.setSelected("true".equals(config.getProperty("analysis.job.auto.refresh")));

        // 자동 새로고침 콤보박스 아이템 삽입
        for (String second : CommonConstants.AUTO_REPRESH_SECOND_PERIOD) {
            autoRefreshPeriodComboBox.getItems().add(second + " second");
        }
        autoRefreshPeriodComboBox.setValue(config.getProperty("analysis.job.auto.refresh.period"));
        
        // 윈도우 테마 콤보박스 아이템 삽입
        for (String theme : CommonConstants.WINDOW_THEME) {
            windowTheme.getItems().add(theme);
        }
        windowTheme.setValue(config.getProperty("window.theme"));
        //System.out.println(windowTheme.getValue());

        // Create the dialog Stage
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(CommonConstants.SYSTEM_NAME + " > Settings");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            dialogStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        dialogStage.initOwner(getMainApp().getPrimaryStage());

        // Schen Init
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();

    }

    @FXML
    public void confirmServerURL() {
        if(StringUtils.isEmpty(serverURLTextField.getText())) {
            DialogUtil.warning("Empty server URL", "Please enter a valid server URL", this.mainApp.getPrimaryStage(), true);
        } else {
            int status = serverURLManageService.isValidURL(serverURLTextField.getText());
            if(status >= 200 && status < 300) {
                DialogUtil.alert("Connection OK", "It is connected to URL server.", this.mainApp.getPrimaryStage(), true);
            } else {
                serverURLTextField.setText(originServerURL);
                String msgHeader = "Incorrect URL.";
                String msgContents = "Please enter a valid server URL";
                if(status == -1) {
                    msgHeader = "Unable to connect to the input URL server.";
                } else if(status == 0) {
                    msgHeader = "Invalid URL format";
                }
                DialogUtil.warning(msgHeader, msgContents, this.mainApp.getPrimaryStage(), true);
            }
        }
    }

    @FXML
    public void save() {

        logger.debug("settings save start..");

        if(mainController.getProgressTaskContentArea().getChildren().size() != 0) {
            return;
        }

        boolean isContinue = true;
        if(!originServerURL.equals(serverURLTextField.getText())) {
            int status = serverURLManageService.isValidURL(serverURLTextField.getText());
            if(status < 200 || status >= 300) {
                serverURLTextField.setText(originServerURL);
                String msgHeader = "Incorrect URL.";
                String msgContents = "Please Enter the New Server URL Text";
                if(status == -1) {
                    msgHeader = "Unable to connect to the input URL server.";
                } else if(status == 0) {
                    msgContents = "Please Enter the valid URL Text";
                    msgHeader = "Invalid URL format";
                }
                DialogUtil.warning(msgHeader, msgContents, this.dialogStage, true);
                isContinue = false;
            }
        }

        if(isContinue) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(this.dialogStage);
            alert.setTitle("Save Settings");
			alert.setHeaderText("Save Settings");
			alert.setContentText("Do you want to save your changes?");

            Optional<ButtonType> result = alert.showAndWait();
            isContinue = result.isPresent() && result.get() == ButtonType.OK;
        }

        if(isContinue) {
            // 서버 정보 설정 파일에 입력 받은 내용을 기록
            String homeDir = System.getProperty("user.home");
            File configFile = new File(homeDir + File.separator + CommonConstants.BASE_PATH, CommonConstants.CONFIG_PROPERTIES);

            String pastURL = config.getProperty(CommonConstants.DEFAULT_SERVER_HOST_KEY);
            // 프로퍼티 파일에 저장
            Map<String,String> map = new HashMap<>();
            map.put("default.server.host", serverURLTextField.getText());
            map.put("analysis.job.auto.refresh", Boolean.toString(autoRefreshCheckBox.isSelected()));
            map.put("analysis.job.auto.refresh.period", autoRefreshPeriodComboBox.getValue().replaceAll(" second", ""));
            map.put("window.theme", windowTheme.getValue());
            PropertiesUtil.saveProperties(configFile, map);
           

            // 현재 설정된 프로퍼티 설정 갱신
            try {
                FileReader reader = new FileReader(configFile);
                Properties properties = new Properties();
                properties.load(reader);
                reader.close();
                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    String key = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    logger.debug(String.format("key : %s, value : %s", key, value));

                    // 설정값이 존재하는 경우 추가
                    if(!StringUtils.isEmpty(value)) {
                        config.setProperty(key, value);
                    }
                }
                DialogUtil.alert("Success", "The changes have been saved.", getMainApp().getPrimaryStage(), true);
                // 자동 새로고침 설정 적용

                String presentURL = serverURLTextField.getText();

                PropertiesService propertiesService = PropertiesService.getInstance();

                propertiesService.getConfig().setProperty("analysis.job.auto.refresh", Boolean.toString(autoRefreshCheckBox.isSelected()));
                propertiesService.getConfig().setProperty("analysis.job.auto.refresh.period", autoRefreshPeriodComboBox.getValue().replaceAll(" second", ""));
                propertiesService.getConfig().setProperty("window.theme", windowTheme.getValue());

                if(pastURL.equals(presentURL)) {
                    getMainController().applyAutoRefreshSettings();
                } else {
                    propertiesService.getConfig().setProperty(CommonConstants.DEFAULT_SERVER_HOST_KEY, presentURL);
                    mainController.logoutForce();
                }
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void close() {
        // dialog close
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
