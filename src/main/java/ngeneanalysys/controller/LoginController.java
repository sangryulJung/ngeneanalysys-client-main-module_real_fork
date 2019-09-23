package ngeneanalysys.controller;

import javafx.concurrent.Task;
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
import ngeneanalysys.model.NGeneAnalySysVersion;
import ngeneanalysys.service.PropertiesService;
import ngeneanalysys.util.PropertiesUtil;
import ngeneanalysys.util.httpclient.HttpClientUtil;
import org.apache.http.HttpStatus;
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
	private Label versionLabel;

	@FXML
	private Label capsLock;
	
	@FXML
	private GridPane contentsWrapper;
	
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

	/** 처리진행중 표시 객체 */
	@FXML
	private ProgressIndicator progress;

	@FXML
	private CheckBox loginIdSaveCheckBox;

	@FXML
	public void checkValidateLoginID(KeyEvent ke) {
		boolean valid = validateLoginID();
		if(ke.getCode().equals(KeyCode.ENTER) && valid) {
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

					if(loginIdSaveCheckBox.isSelected()) {
						PropertiesService.getInstance().getConfig().setProperty("login.id.save", inputLoginID.getText());
						PropertiesUtil.saveLoginId(inputLoginID.getText());
					} else {
						PropertiesService.getInstance().getConfig().remove("login.id.save");
						PropertiesUtil.saveLoginId(null);
					}

					mainApp.showMain();
				} catch (WebAPIException wae){
					if (wae.getResponse() != null && wae.getResponse().getStatus() == HttpStatus.SC_BAD_REQUEST) {
						DialogUtil.warning(null, "The Username and Password do not match.", getMainApp().getPrimaryStage(), true);
					} else {
						DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(), getMainApp().getPrimaryStage(), true);
					}
				} catch (Exception e){
					logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
					DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), true);
				} finally {
					progress.setVisible(false);
				}
			}
		} catch (Exception e) {
			logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
			DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), mainApp.getPrimaryStage(), true);
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

	private void showCapLock() {
		if (Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK)) {
			capsLock.setStyle("-fx-background-image:url('layout/images/renewal/upper_case_icon.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
		}else {
			capsLock.setStyle("");
		}
	}
	
	@Override
	public void show(Parent root) throws IOException {
		Scene scene = new Scene(root);
		scene.addEventFilter(KeyEvent.KEY_PRESSED,
                event -> showCapLock());
		scene.addEventFilter(MouseEvent.ANY,
                event -> showCapLock());

		if(loginIdSaveCheckBox.getTooltip() == null) {
			loginIdSaveCheckBox.setTooltip(new Tooltip("If checked, the login ID is saved."));
		}

		PropertiesService propertiesService = PropertiesService.getInstance();
		String loginId = propertiesService.getConfig().getProperty("login.id.save");
		if(StringUtils.isNotEmpty(loginId)) {
			loginIdSaveCheckBox.selectedProperty().setValue(true);
			inputLoginID.setText(loginId);
			inputPassword.requestFocus();
		}
	    
		scene.getFocusOwner();
		scene.setFill(Color.TRANSPARENT);
		
		Stage primaryStage = this.mainApp.getPrimaryStage();

		inputLoginID.focusedProperty().addListener((ov, t, t1) -> {
			if(Boolean.TRUE.equals(t1)) validateLoginID();
		});

		inputPassword.focusedProperty().addListener((ov, t, t1) -> {
			if(Boolean.TRUE.equals(t1)) {
				validatePassword();
			}
		});

		primaryStage.setScene(scene);
		
		primaryStage.setTitle(CommonConstants.SYSTEM_NAME + " Login");
		int otherAreas = 0;
		if(System.getProperty("os.name").toLowerCase().contains("window")) {
			primaryStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
			otherAreas = 20;
		}
		primaryStage.setMaximized(false);
		primaryStage.setMinWidth(430);
		primaryStage.setWidth(430);
		primaryStage.setMaxWidth(430);
		primaryStage.setMinHeight(410. + otherAreas);
		primaryStage.setHeight(410. + otherAreas);
		primaryStage.setMaxHeight(410. + otherAreas);
		primaryStage.setResizable(false);
		primaryStage.centerOnScreen();
		primaryStage.show();
		logger.debug(String.format("start %s", primaryStage.getTitle()));

		getSoftwareVersionInfo();

		// 창 닫기 이벤트 바인딩
		primaryStage.setOnCloseRequest(event -> {
			//프록시 서버가 기동중인 경우 중지 처리
			if(mainApp.getProxyServer() != null) {
				mainApp.getProxyServer().stopServer();
			}
			primaryStage.close();
		});

	}

	private void getSoftwareVersionInfo() {
		Task task = new Task() {
			NGeneAnalySysVersion nGeneAnalySysVersion;

			@Override
			protected Object call() throws Exception {
				APIService apiService = APIService.getInstance();
				HttpClientResponse response = HttpClientUtil
						.get(apiService.getConvertConnectURL(""), null, null, false);
				nGeneAnalySysVersion = response.getObjectBeforeConvertResponseToJSON(NGeneAnalySysVersion.class);
				return null;
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				versionLabel.setText("System version : " + nGeneAnalySysVersion.getSystem());
			}

			@Override
			protected void failed() {
				super.failed();
				getException().printStackTrace();
			}
		};
		Thread thread = new Thread(task);
		thread.start();
	}

	private boolean validateLoginID() {
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

	private boolean validatePassword() {
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
    		contentsWrapper.setStyle("-fx-background-image:url('layout/images/renewal/main_background.png');");
    	}else if(theme.equalsIgnoreCase("dark")) {
    		contentsWrapper.setStyle("-fx-background-image:url('layout/images/renewal/main_background01.png');");
    	}else if(theme.equalsIgnoreCase("red")) {
    		contentsWrapper.setStyle("-fx-background-image:url('layout/images/renewal/main_background06.png');");
    	}else if(theme.equalsIgnoreCase("ice")) {
    		contentsWrapper.setStyle("-fx-background-image:url('layout/images/renewal/main_background02.png');");
    	}else if(theme.equalsIgnoreCase("mountain")) {
    		contentsWrapper.setStyle("-fx-background-image:url('layout/images/renewal/main_background10.png');");
    	}else if(theme.equalsIgnoreCase("dna")) {
    		contentsWrapper.setStyle("-fx-background-image:url('layout/images/renewal/main_background12.png');");
    	}
    }
}
