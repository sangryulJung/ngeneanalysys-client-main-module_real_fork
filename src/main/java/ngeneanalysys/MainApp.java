package ngeneanalysys;
	
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javafx.scene.Parent;
import javafx.scene.control.*;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.MainController;
import ngeneanalysys.controller.ServerURLSettingController;
import ngeneanalysys.proxy.SparkHttpProxyServer;
import ngeneanalysys.service.CacheMemoryService;
import ngeneanalysys.service.PropertiesService;
import ngeneanalysys.util.*;
import org.slf4j.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.LoginController;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;


public class MainApp extends Application {
	private static Logger logger = LoggerUtil.getLogger();
	
	/** 어플리케이션 중복 구동 여부 */
	private boolean isAlreadyRunning = false;

	/** IGV 연동을 위한 proxy 서버 모듈 */
	//public Spark
	private SparkHttpProxyServer proxyServer;
	
	/** Properties Config */
	protected Properties config;
	
	/** Resource Util */
	protected ResourceUtil resourceUtil = new ResourceUtil();
	
	/** 메인 Stage */
	protected Stage primaryStage;


	/**
	 *
	 * @return
	 */
	public SparkHttpProxyServer getProxyServer() {
		return proxyServer;
	}

	/**
	 *
	 * @param proxyServer
	 */
	public void setProxyServer(SparkHttpProxyServer proxyServer) {
		this.proxyServer = proxyServer;
	}

	/**
	 * return property value
	 * @param name
	 * @return
	 */
	public String getProperty(String name) {
		return config.getProperty(name);
	}
	
	/**
	 * @return the primaryStage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void startProxyServer() {
		if(proxyServer == null) {
			proxyServer = new SparkHttpProxyServer();
			proxyServer.start();
		}
	}
	
	public void showLogin() throws Exception {
		if(primaryStage.getScene() != null) {
			logger.info("Pre Scene Close");
			primaryStage.close();
		}

		FXMLLoader loader = load(FXMLConstants.LOGIN);
		AnchorPane pane = loader.load();
		LoginController controller = loader.getController();
		controller.setMainApp(this);
		controller.show(pane);
		
	}

	public void showServerURLSetting() throws Exception {
		FXMLLoader loader =load(FXMLConstants.SERVER_URL_SETTING);
		GridPane pane = loader.load();
		ServerURLSettingController controller = loader.getController();
		controller.setMainApp(this);
		controller.show(pane);
	}
	
	/**
	 * 
	 * @param fxmlPath
	 * @return
	 */
	public FXMLLoader load(String fxmlPath) {
		return FXMLLoadUtil.load(fxmlPath);
	}

	
	public boolean checkExistsDatabasePathAndCreate() {
		return false;
	}
	
	public boolean isProxyServerRunning() {
		try (Socket socket = new Socket()){
			socket.connect(new InetSocketAddress("localhost", CommonConstants.HTTP_PROXY_SERVER_PORT), 500);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 예외상황 메시지 출력
	 * @param t
	 * @param e
	 */
	@SuppressWarnings("unused")
	private static void showError(Thread t, Throwable e) {
		logger.error("***Default exception handler***");
		if (Platform.isFxApplicationThread()) {
			showErrorDialog(e);
		} else {
			logger.error("An unexpected error occurred in " + t);
		}
	}
	
	/**
	 * 예외상황 메시지 Dialog 출력
	 * @param e
	 */
	private static void showErrorDialog(Throwable e) {
		StringWriter errorMsg = new StringWriter();
		e.printStackTrace(new PrintWriter(errorMsg));
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception Dialog");
		alert.setHeaderText("Look, an Exception Dialog");
		
		// Create expandable Exception.
		String exceptionText = errorMsg.toString();

		Label label = new Label("The exception stacktrace was:");
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		alert.showAndWait();
	}
	
	/* (non-Javadoc)
	 * @see javafx.application.Application#init()
	 */
	@Override
	public void init() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		isAlreadyRunning = isProxyServerRunning();
		logger.info(String.format("# already running application : %s", isAlreadyRunning));
		
		if(!isAlreadyRunning) {
			boolean checkDB = checkExistsDatabasePathAndCreate();
			logger.info("check local db : " + checkDB);

			config = PropertiesService.getInstance().getConfig();
			logger.info(String.format("application name : %s", getProperty("application.name")));
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception{

		if(isAlreadyRunning) {
			logger.warn("Requested applications are currently running and newly requested one will be shut down.");
			DialogUtil.warning("Requested application is running already.", "Requested applications are currently running and newly requested one will be shut down.", null, true);
			System.exit(0);
		} else {
			// proxy 서버 기동
			startProxyServer();
		}
		
		this.primaryStage = primaryStage;
		this.primaryStage.initStyle(StageStyle.DECORATED);
		
		boolean isContainsServerURL = containsServerURL();
		logger.info(String.format("server url is contains : %s", isContainsServerURL));
		
		if(!isContainsServerURL) {
			showServerURLSetting();
		} else {
			addProperty();
			logger.info("show Login");
			showLogin();
		}
		
	}

	/**
	 * 서버 URL 설정 여부
	 * @return
	 */
	public boolean containsServerURL() {
		File configFile = new File(CommonConstants.BASE_FULL_PATH, CommonConstants.CONFIG_PROPERTIES);

		if(!configFile.exists()) {
			return false;
		}

		try (FileReader reader = new FileReader(configFile)){
			Properties props = new Properties();
			props.load(reader);

			return (props.containsKey("default.server.host")
						&& !StringUtils.isEmpty(props.getProperty("default.server.host")));

		} catch (FileNotFoundException ex) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 로컬 설정 프로퍼티 설정 시스템 어플리케이션 프로퍼티에 추가 삽입
	 */
	public void addProperty() {
		File configFile = new File(CommonConstants.BASE_FULL_PATH, CommonConstants.CONFIG_PROPERTIES);

		try (FileReader reader = new FileReader(configFile)) {
			Properties props = new Properties();
			props.load(reader);

			for(Map.Entry<Object, Object> entry : props.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();

				// 설정값이 존재하는 경우 추가
				if(!StringUtils.isEmpty(value)) {
					logger.info(String.format("add property [%s : %s]", key, value));
					config.setProperty(key, value);
				}
			}

		} catch(Exception e) {
			logger.info(e.getMessage());
		}

	}

	/**
	 * 메인 화면 출력
	 * @throws Exception
	 */
	public void showMain() throws Exception {
		//이전 stage (로그인화면) 종료
		if(primaryStage.getScene() != null) {
			logger.info("Login Scene Close..");
			primaryStage.close();
		}

		// 세션 체크
		// 캐시 메모리 서비스 할당
		CacheMemoryService cacheMemoryService = CacheMemoryService.getInstance();

		if(cacheMemoryService.isEmpty(CommonConstants.SESSION_CACHE_SET_NAME, CommonConstants.SESSION_CACHE_KEY_NAME)) {
			logger.info("empty login session!!!");
			DialogUtil.warning("Empty Login Session", "Please Login", this.primaryStage, true);
			showLogin();
		} else {
			FXMLLoader loader = load(FXMLConstants.MAIN);
			GridPane mainPane = loader.load();
			MainController controller = loader.getController();
			controller.setMainApp(this);
			controller.show(mainPane);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
