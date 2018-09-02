package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.service.ServerURLManageService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-08-03
 */
public class ServerURLSettingController extends BaseStageController{
    private static final Logger logger = LoggerUtil.getLogger();

    private ServerURLManageService serverURLManageService = ServerURLManageService.getInstance();

    @FXML
    private TextField inputURL;

    @FXML
    private Button btnSave;

    @FXML
    public void confirm() {
        if(StringUtils.isEmpty(inputURL.getText())) {
            DialogUtil.warning("Empty Server URL", "Please Enter the Server URL Text", this.mainApp.getPrimaryStage(), true);
        } else {
            int status = serverURLManageService.isValidURL(inputURL.getText());
            if(status >= 200 && status < 300) {
                btnSave.setDisable(false);
                DialogUtil.alert("Connection OK", "It is connected to URL server.", this.mainApp.getPrimaryStage(), true);
            } else {
                inputURL.setText(null);
                btnSave.setDisable(true);
                String msgHeader = "Incorrect URL.";
                String msgContents = "Please Enter the New Server URL Text";
                if(status == -1) {
                    msgHeader = "Unable to connect to the input URL server.";
                } else if(status == 0) {
                    msgContents = "Please Enter the valid URL Text";
                    msgHeader = "Invalid URL format";
                }
                DialogUtil.warning(msgHeader, msgContents, this.mainApp.getPrimaryStage(), true);
            }
        }
    }

    @FXML
    public void save() throws Exception{
        if(StringUtils.isEmpty(inputURL.getText())) {
            DialogUtil.warning("Empty Server URL", "Please Enter the Server URL Text", this.mainApp.getPrimaryStage(), true);
        } else {
            int status = serverURLManageService.isValidURL(inputURL.getText());
            if(status < 200 || status >= 300) {
                String msgHeader = "Incorrect URL.";
                String msgContents = "Please Enter the New Server URL Text";
                if(status == -1) {
                    msgHeader = "Unable to connect to the input URL server.";
                } else if(status == 0) {
                    msgHeader = "Invalid URL format";
                }
                DialogUtil.warning(msgHeader, msgContents, this.mainApp.getPrimaryStage(), true);
            } else {
                // 저장
                serverURLManageService.save(inputURL.getText());
                // 기존 프로퍼티 설정에 추가
                mainApp.addProperty();
                // 로그인 화면 출력
                mainApp.showLogin();
            }
        }

    }

    @Override
    public void show(Parent root) throws IOException {
        Stage primaryStage = this.mainApp.getPrimaryStage();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(CommonConstants.SYSTEM_NAME + " > API Server URL Setting");
        // OS가 Window인 경우 아이콘 출력.
        if(System.getProperty("os.name").toLowerCase().contains("window")) {
            primaryStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            try {
                mainApp.showLogin();
            } catch (Exception e){
                logger.error("Show Login Fail", e);
            }
        });
        primaryStage.setMinWidth(610);
        primaryStage.setWidth(610);
        primaryStage.setMaxWidth(610);
        primaryStage.setMinHeight(205);
        primaryStage.setHeight(205);
        primaryStage.setMaxHeight(205);
        primaryStage.centerOnScreen();
        primaryStage.show();

        String serverURL = config.getProperty("default.server.host");

        if(!StringUtils.isEmpty(serverURL)) inputURL.setText(serverURL);

        logger.debug(String.format("start %s", primaryStage.getTitle()));
    }
}
