package ngeneanalysys.controller;

import javafx.fxml.FXML;
import java.awt.Toolkit;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.LoginSession;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.CacheMemoryService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jang
 * @since 2017. 8. 1.
 *
 */


public class LoginController extends BaseStageController {
	
	public void initialize(){
		showCapLock();	
	}
	
	 
	private static final Logger logger = LoggerUtil.getLogger();

	@FXML
	private Label CapsLock;
	
	@FXML
	private GridPane contentswrapper;
	
	@FXML
	private Button loginBtn;
	
	/** 아이디 input 객체 */
	@FXML
	private TextField inputLoginID;

	/** 로그인 실행 버튼 객체 */
	@FXML
	private Label labelLoginID;

	/** 비밀번호 input 객체 */
	@FXML
	private PasswordField inputPassword;

	/** 로그인 실행 버튼 객체 */
	@FXML
	private Label labelPassword;

	/** 서버 URL 변경 창 출력 버튼 */
	@FXML
	private Button settingURLButton;

	/** 처리진행중 표시 객체 */
	@FXML
	private ProgressIndicator progress;

	@FXML
	public void checkValidateLoginID(KeyEvent ke) {
		boolean vaild = validateLoginID();
		if(ke.getCode().equals(KeyCode.ENTER) && vaild) {
			runLogin();
		}
	}
	
	@FXML
	public void changeIdIcon(KeyEvent ke) {
		if(inputLoginID.getText().isEmpty()) {
			inputLoginID.setStyle("-fx-background-image:url('layout/images/renewal/login_user_icon.png')");
		}else {
			inputLoginID.setStyle("-fx-background-image:url('layout/images/renewal/login_user_icon_on.png')");
		}		
	}
	
	@FXML
	public void changePwIcon(KeyEvent ke) {
		if (Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK) ) {
			CapsLock.setStyle("-fx-background-image:url('layout/images/renewal/upper_case_icon.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
		}else {
			CapsLock.setStyle("");
		}
		if(inputPassword.getText().isEmpty()) {
			inputPassword.setStyle("-fx-background-image:url('layout/images/renewal/login_password_icon.png')");
		}else {
			inputPassword.setStyle("-fx-background-image:url('layout/images/renewal/login_password_icon_on.png')");
		}		
	}
	
	@FXML
	public void checkValidatePassword(KeyEvent ke) {
		boolean valid = validatePassword();
		if(ke.getCode().equals(KeyCode.ENTER) && valid && validateLoginID()) {
			runLogin();
		}
	}

	/**
	 *  로그인 실행
	 */
	@FXML
	public void runLogin() {
		try {
			if(validateLoginID() && validatePassword()) {
				ProgressBar progressBar = new ProgressBar();
				progress.setProgress(progressBar.getProgress());
				progress.setVisible(true);

				LoginSession loginSession = null;

				APIService apiService = APIService.getInstance();
				apiService.setStage(getMainApp().getPrimaryStage());
				Map<String, Object> params = new HashMap<>();
				params.put("id", inputLoginID.getText());
				params.put("password", inputPassword.getText());

				HttpClientResponse response = null;
				try {
					response = apiService.postNotContainToken("/auth/token", params, null, true);
					loginSession = response.getObjectBeforeConvertResponseToJSON(LoginSession.class);

					loginSession.setLoginId(inputLoginID.getText());

					// 캐시 메모리 서비스 할당
					CacheMemoryService cacheMemoryService = CacheMemoryService.getInstance();
					// 캐시 메모리내 세션 정보 삽입
					cacheMemoryService.setCacheObject(CommonConstants.SESSION_CACHE_SET_NAME, CommonConstants.SESSION_CACHE_KEY_NAME, loginSession);

					if(mainApp.getProxyServer() == null) {
						mainApp.startProxyServer();
					}
					mainApp.getProxyServer().setApiServerHost(this.mainApp.getProperty(CommonConstants.DEFAULT_SERVER_HOST_KEY));
					mainApp.getProxyServer().setAuthToken(loginSession.getToken());

					mainApp.showMain();
				} catch (WebAPIException wae){
					if (wae.getResponse() != null && wae.getResponse().getStatus() == HttpStatus.BAD_REQUEST_400) {
						DialogUtil.warning(null, "The Username and Password do not match.", getMainApp().getPrimaryStage(), true);
					} else {
						DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(), getMainApp().getPrimaryStage(), true);
					}
				} catch (Exception e){
					logger.error("Unknown Error", e);
					DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
				} finally {
					progress.setVisible(false);
				}
			}
		} catch (Exception e) {
			logger.error("Unknown Error", e);
			DialogUtil.error(null, "error!!!", mainApp.getPrimaryStage(), true);
			logger.error(e.getMessage(), e);
		}

	}
	
	@FXML
	public void openSettingURLDialog() {
		try {
			getMainApp().showServerURLSetting();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showCapLock() {
		//System.out.println("hi");
		if (Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK) ) {
			CapsLock.setStyle("-fx-background-image:url('layout/images/renewal/upper_case_icon.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
		}else {
			CapsLock.setStyle("");
		}
	}
	
	@Override
	public void show(Parent root) throws IOException {
		Scene scene = new Scene(root);
		scene.addEventFilter(KeyEvent.KEY_PRESSED,
                event -> showCapLock());
		scene.addEventFilter(MouseEvent.ANY,
                event -> showCapLock());
	    
		scene.getFocusOwner();
		scene.setFill(Color.TRANSPARENT);
		
		Stage primaryStage = this.mainApp.getPrimaryStage();

		inputLoginID.focusedProperty().addListener((ov, t, t1) -> {
			if(t1) validateLoginID();
		});

		inputPassword.focusedProperty().addListener((ov, t, t1) -> {
			if(t1) {
				validatePassword();
			}
		});

		settingURLButton.setVisible(true);

		primaryStage.setScene(scene);
		
		primaryStage.setTitle(CommonConstants.SYSTEM_NAME + " Login");

		if(System.getProperty("os.name").toLowerCase().contains("window")) {
			primaryStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
		}
		primaryStage.setMaximized(false);
		primaryStage.setMinWidth(430);
		primaryStage.setWidth(430);
		primaryStage.setMaxWidth(430);
		primaryStage.setMinHeight(410);
		primaryStage.setHeight(410);
		primaryStage.setMaxHeight(410);
		primaryStage.setResizable(false);
		primaryStage.centerOnScreen();
		primaryStage.show();
		logger.debug(String.format("start %s", primaryStage.getTitle()));

		// 창 닫기 이벤트 바인딩
		primaryStage.setOnCloseRequest(event -> {
			//프록시 서버가 기동중인 경우 중지 처리
			if(mainApp.getProxyServer() != null) {
				mainApp.getProxyServer().stopServer();
			}
			primaryStage.close();
		});

	}

	public boolean validateLoginID() {
		if (StringUtils.isEmpty(inputLoginID.getText())) {
			labelLoginID.setText("Please enter the username");
			labelLoginID.setVisible(true);
			return false;
		} else {
			labelLoginID.setText("");
			labelLoginID.setVisible(false);
		}
		return true;
	}
	public boolean validatePassword() {
		if (StringUtils.isEmpty(inputPassword.getText())) {
			labelPassword.setText("Please enter the password");
			labelPassword.setVisible(true);
			return false;
		} else {
			labelPassword.setText("");
			labelPassword.setVisible(false);
		}
		return true;
	}
	
	//테마 로그인창 
    public void applyLoginTheme(String theme) {
    	logger.debug("Login theme:" + theme);
    	
    	if(theme.equalsIgnoreCase("default")) {
    		contentswrapper.setStyle("-fx-background-image:url('layout/images/renewal/main_background.png');");
    	}else if(theme.equalsIgnoreCase("dark")) {
    		contentswrapper.setStyle("-fx-background-image:url('layout/images/renewal/main_background01.png');");
    	}else if(theme.equalsIgnoreCase("red")) {
    		contentswrapper.setStyle("-fx-background-image:url('layout/images/renewal/main_background06.png');");
    	}else if(theme.equalsIgnoreCase("ice")) {
    		contentswrapper.setStyle("-fx-background-image:url('layout/images/renewal/main_background02.png');");
    	}else if(theme.equalsIgnoreCase("mountain")) {
    		contentswrapper.setStyle("-fx-background-image:url('layout/images/renewal/main_background10.png');");
    	}else if(theme.equalsIgnoreCase("dna")) {
    		contentswrapper.setStyle("-fx-background-image:url('layout/images/renewal/main_background12.png');");
    	}
    	
    	//System.out.println((String)contentswrapper.getStyle());

    }
}
