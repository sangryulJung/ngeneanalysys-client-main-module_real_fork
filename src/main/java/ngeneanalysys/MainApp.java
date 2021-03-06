package ngeneanalysys;
	
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.controller.MainController;
import ngeneanalysys.controller.ServerURLSettingController;
import ngeneanalysys.proxy.SparkHttpProxyServer;
import ngeneanalysys.service.CacheMemoryService;
import ngeneanalysys.service.PropertiesService;
import ngeneanalysys.util.*;
import org.slf4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.LoginController;
import javafx.scene.layout.GridPane;


public class MainApp extends Application {
	private static Logger logger = LoggerUtil.getLogger();
	
	// 어플리케이션 중복 구동 여부
	private boolean isAlreadyRunning = false;

	// IGV 연동을 위한 proxy 서버 모듈
	private SparkHttpProxyServer proxyServer;
	
	// Properties Config
	protected Properties config;
	
	// 메인 Stage
	private Stage primaryStage;

	/**
	 *
	 * @return SparkHttpProxyServer
	 */
	public SparkHttpProxyServer getProxyServer() {
		return proxyServer;
	}

	/**
	 * return property value
	 * @param name String
	 * @return String
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
			logger.debug("Pre Scene Close");
			primaryStage.close();
		}

		FXMLLoader loader = load(FXMLConstants.LOGIN);
		GridPane pane = loader.load();
		LoginController controller = loader.getController();
		controller.setMainApp(this);
		PropertiesService propertiesService = PropertiesService.getInstance();		
		controller.applyLoginTheme(propertiesService.getConfig().getProperty("window.theme"));
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
	 * @param fxmlPath String
	 * @return FXMLLoader
	 */
	public FXMLLoader load(String fxmlPath) {
		return FXMLLoadUtil.load(fxmlPath);
	}
	
	private boolean isProxyServerRunning() {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress("localhost", CommonConstants.HTTP_PROXY_SERVER_PORT), 500);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see javafx.application.Application#init()
	 */
	@Override
	public void init() {
		Locale.setDefault(Locale.ENGLISH);
		isAlreadyRunning = isProxyServerRunning();
		logger.debug(String.format("# already running application : %s", isAlreadyRunning));
		
		if(!isAlreadyRunning) {
			config = PropertiesService.getInstance().getConfig();
			logger.debug(String.format("application name : %s", getProperty("application.name")));
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception{
		if(isAlreadyRunning) {
			logger.warn("Requested applications are currently running and newly requested one will be shut down.");
			DialogUtil.warning("", "Requested applications are currently running and newly requested one will be shut down.", null, true);
			throw new RuntimeException();
		} else {
			// proxy 서버 기동
			startProxyServer();
		}

		this.primaryStage = primaryStage;
		this.primaryStage.initStyle(StageStyle.DECORATED);

		boolean isContainsServerURL = containsServerURL();
		logger.debug(String.format("server url is contains : %s", isContainsServerURL));
		
		if(!isContainsServerURL) {
			showServerURLSetting();
		} else {
			addProperty();
			logger.debug("show Login");
			showLogin();
		}
		
	}

	/**
	 * 서버 URL 설정 여부
	 * @return boolean
	 */
	private boolean containsServerURL() {
		File configFile = new File(CommonConstants.BASE_FULL_PATH, CommonConstants.CONFIG_PROPERTIES);

		if(!configFile.exists()) {
			return false;
		}

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))){
			Properties props = new Properties();
			props.load(reader);

			return (props.containsKey("default.server.host")
						&& StringUtils.isNotEmpty(props.getProperty("default.server.host")));

		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 로컬 설정 프로퍼티 설정 시스템 어플리케이션 프로퍼티에 추가 삽입
	 */
	public void addProperty() {
		File configFile = new File(CommonConstants.BASE_FULL_PATH, CommonConstants.CONFIG_PROPERTIES);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))) {
			Properties props = new Properties();
			props.load(reader);

			for(Map.Entry<Object, Object> entry : props.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();

				// 설정값이 존재하는 경우 추가
				if(StringUtils.isNotEmpty(value)) {
					logger.debug(String.format("add property [%s : %s]", key, value));
					config.setProperty(key, value);
				}
			}

		} catch(Exception e) {
			logger.debug(e.getMessage());
		}

	}

	public void showMain() throws Exception {
		//이전 stage (로그인화면) 종료
		if(primaryStage.getScene() != null) {
			logger.debug("Login Scene Close..");
			primaryStage.close();
		}

		// 세션 체크
		// 캐시 메모리 서비스 할당
		CacheMemoryService cacheMemoryService = CacheMemoryService.getInstance();

		if(cacheMemoryService.isEmpty(CommonConstants.SESSION_CACHE_SET_NAME, CommonConstants.SESSION_CACHE_KEY_NAME)) {
			logger.debug("empty login session!!!");
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
}
