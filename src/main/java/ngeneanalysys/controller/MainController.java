package ngeneanalysys.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.SystemMenuCode;
import ngeneanalysys.code.enums.UserTypeBit;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.CacheMemoryService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.LoginSessionUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jang
 * @since 2017-08-07
 */
public class MainController extends BaseStageController {
    private static final Logger logger = LoggerUtil.getLogger();

    /** api server 연동 서비스 */
    private APIService apiService;
    /** cache memory 관리 서비스 */
    private CacheMemoryService cacheMemoryService;

    /** 최상단 탭메뉴 정보 배열  */
    private TopMenu[] topMenus = null;
    /** 최상단 탭메뉴 화면 출력중인 컨텐츠 Scene 배열  */
    private Node[] topMenuContent;
    /** 현재 화면 출력중인 최상단 탭메뉴 인덱스 */
    private int selectedTopMenuIdx = 0;
    /** 이전 화면 정보 */
    private String currentShowFrameId;
    /** 분석자 HOME 컨트롤러 */
    private HomeController homeController;
    /** 분석자 Past Results 컨트롤러*/
    private PastResultsController pastResultsController;

    private SystemManagerHomeController systemManagerHomeController;

    /** 분석 요청 작업 Task 관리 컨트롤러 */
    private AnalysisSampleUploadProgressTaskController analysisSampleUploadProgressTaskController;

    /** 메인 레이아웃 화면 Stage */
    private Stage primaryStage;

    /** 상단 탭 메뉴 > 스크롤 왼쪽으로 이동 버튼 */
    @FXML
    private Button topMenuScrollLeftButton;

    /** 상단 탭 메뉴 > 스크롤 오른쪽으로 이동 버튼 */
    @FXML
    private Button topMenuScrollRightButton;

    /** 상단 탭 메뉴 Area */
    @FXML
    private ScrollPane topMenuScrollPane;

    /** 상단 탭 메뉴 Area */
    @FXML
    private HBox topMenuArea;

    /** 상단 로그인 사용자명 */
    @FXML
    private Label loginUserName;

    /** 상단 사용자 메뉴 */
    @FXML
    private Menu userSystemMenu;

    /** 메인 컨텐츠 프레임 Pane Node */
    @FXML
    private BorderPane mainFrame;

    /** progress task contenst area */
    @FXML
    private HBox progressTaskContentArea;

    /** 클라이언트 빌드 정보 라벨 */
    @FXML
    private Label labelSystemBuild;

    private Map<String, Object> basicInformationMap = new HashMap<>();

    /**
     * @return basicInformationMap
     */
    public Map<String, Object> getBasicInformationMap() {
        return basicInformationMap;
    }

    /**
     * 메인 컨텐츠 프레임 Pane Node 객체 반환
     * @return
     */
    public BorderPane getMainFrame() {
        return this.mainFrame;
    }

    /**
     * 메인 레이아웃의 Stage 객체 반환
     * @return
     */
    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("main controller...");

        apiService = APIService.getInstance();
        cacheMemoryService = CacheMemoryService.getInstance();

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int usersMonitorWidth = gd.getDisplayMode().getWidth();
        int usersMonitorHeight = gd.getDisplayMode().getHeight();
        logger.info(String.format("user's monitor size [width : %s, height : %s]", usersMonitorWidth, usersMonitorHeight));

        int setWindowHeight = CommonConstants.DEFAULT_MAIN_SCENE_HEIGHT;
        if (usersMonitorHeight < CommonConstants.DEFAULT_MAIN_SCENE_HEIGHT) {
            setWindowHeight = CommonConstants.MIN_MAIN_SCENE_HEIGHT;
        }

        primaryStage = this.mainApp.getPrimaryStage();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(CommonConstants.SYSTEM_NAME);
        // OS가 Window인 경우 아이콘 출력.
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            primaryStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        primaryStage.setMinHeight(setWindowHeight + 35);
        primaryStage.setHeight(setWindowHeight + 35);
        primaryStage.setMinWidth(1290);
        primaryStage.setWidth(1290);
        primaryStage.centerOnScreen();
        primaryStage.show();
        logger.info(String.format("start %s", primaryStage.getTitle()));


        primaryStage.setOnCloseRequest(event -> {
            boolean isClose = false;
            String alertHeaderText = null;
            String alertContentText = "Do you want to exit the application?";

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner((Window) this.primaryStage);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(alertHeaderText);
            alert.setContentText(alertContentText);

            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.OK) {
                isClose = true;
            }

            if(isClose) {
                if (mainApp.getProxyServer() != null) {
                    mainApp.getProxyServer().stopServer();
                }
                primaryStage.close();
            } else {
                event.consume();
            }
        });

        //로그인 사용자 세션
        LoginSession loginSession = LoginSessionUtil.getCurrentLoginSession();
        String role = loginSession.getRole();
        //String loginId = loginSession.getLoginId();

        //우상단 로그인 사용자명 삽입
        loginUserName.setText(loginSession.getName());

        //상단 탭메뉴 스크롤 영역 설정
        topMenuScrollPane.widthProperty().addListener((ov, oldWidth, newWidth) -> {
            if (oldWidth != newWidth) {
                logger.debug(String.format("current top tab menu scroll pane area width :%s, topMenuArea width : %s ", newWidth, topMenuArea.getWidth()));
                if (newWidth.doubleValue() < topMenuArea.getWidth()) {
                    topMenuScrollLeftButton.setVisible(true);
                    topMenuScrollRightButton.setVisible(true);
                } else {
                    topMenuScrollLeftButton.setVisible(false);
                    topMenuScrollRightButton.setVisible(false);
                }
            }
        });

        //상단 탭메뉴 영역 가로사이즈 리스너 바인딩
        topMenuArea.widthProperty().addListener((ov, oldWidth, newWidth) -> {
            if(oldWidth != newWidth) {
                logger.debug(String.format("current top tab menu area width :%s, scroll pane area width : %s ", newWidth, topMenuScrollPane.getWidth()));
                if(newWidth.doubleValue() > topMenuScrollPane.getWidth()) {
                    topMenuScrollLeftButton.setVisible(true);
                    topMenuScrollRightButton.setVisible(true);
                } else {
                    topMenuScrollLeftButton.setVisible(false);
                    topMenuScrollRightButton.setVisible(false);
                }
            }
        });

        //상단 메뉴 설정
        initDefaultTopMenu(role);
        refreshShowTopMenu(-1);
        showTopMenuContents(null, 0);

        //상단 사용자 시스템 메뉴 설정
        initTopUserMenu(role);

        // 하단 빌드 정보 출력
        String buildVersion = config.getProperty("application.version");
        String buildDate = config.getProperty("application.build.date");
        labelSystemBuild.setText(String.format("v %s (Build Date %s)", buildVersion, buildDate));
        logger.info(String.format("v %s (Build Date %s)", buildVersion, buildDate));

        // 중단된 분석 요청 작업이 있는지 체크

        // 기본 정보 로드
        HttpClientResponse response = null;
        try {
            response = apiService.get("/panels", null, null, false);
            final List<Panel> panels = (List<Panel>) response.getMultiObjectBeforeConvertResponseToJSON(Panel.class, false);
            basicInformationMap.put("panels", panels);

            response = apiService.get("/diseases", null, null, false);
            List<Diseases> diseases = (List<Diseases>)response.getMultiObjectBeforeConvertResponseToJSON(Diseases.class, false);
            basicInformationMap.put("diseases", diseases);

        } catch (WebAPIException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * 상단 사용자 메뉴 설정 : 사용자 권한에 따른 메뉴 출력
     * @param role
     */
    public void initTopUserMenu(String role) {
        int userAccessBit = UserTypeBit.valueOf(role).getAuth();

        int idx = 0;
        for(SystemMenuCode code : SystemMenuCode.values()) {
            // 현재 로그인한 사용자가 해당 메뉴 접근 권한이 있는 경우 추가
            if((userAccessBit & code.getAuthBit()) > 0) {
                if(idx > 0) {
                    userSystemMenu.getItems().add(new SeparatorMenuItem());
                }

                MenuItem menuItem = new MenuItem(code.getMenuName());
                menuItem.setId(code.name());
                menuItem.setOnAction(actionEvent -> {
                    if(code == SystemMenuCode.LOGOUT) {
                        logout();
                    } else {
                        openSystemMenu(actionEvent);
                    }
                });
                userSystemMenu.getItems().add(menuItem);
            }
            idx++;
        }

    }

    /**
     * 상단메뉴 초기 설정
     * @param role
     */
    public void initDefaultTopMenu(String role) {
        if(UserTypeBit.ADMIN.name().equalsIgnoreCase(role)) {
            topMenus = new TopMenu[3];
            topMenuContent = new Node[topMenus.length];
            TopMenu menu = new TopMenu();
            menu.setMenuName("System Manager");
            menu.setFxmlPath(FXMLConstants.SYSTEM_MANAGER_HOME);
            menu.setDisplayOrder(2);
            menu.setStaticMenu(true);
            topMenus[2] = menu;
        } else {
            topMenus = new TopMenu[2];
            topMenuContent = new Node[topMenus.length];
        }
        TopMenu menu = new TopMenu();
        menu.setMenuName("Home");
        menu.setFxmlPath(FXMLConstants.HOME);
        menu.setDisplayOrder(0);
        menu.setStaticMenu(true);
        topMenus[0] = menu;

        menu = new TopMenu();
        menu.setMenuName("Past Result");
        menu.setFxmlPath(FXMLConstants.PAST_RESULTS);
        menu.setDisplayOrder(1);
        menu.setStaticMenu(true);
        topMenus[1] = menu;
    }

    /**
     * 상단 메뉴 새로 출력
     * @param selectIdx
     */
    public void refreshShowTopMenu(int selectIdx) {
        // 기존 메뉴 엘레멘트 제거
        topMenuArea.getChildren().removeAll();
        if(topMenus != null && topMenus.length > 0) {
            int idx = 0;
            Group[] topMenuGroups = new Group[topMenus.length];
            for (TopMenu topMenu : topMenus) {
                Group menu = new Group();

                if (selectIdx >= 0 && idx == selectIdx) {
                    menu.setId("selectedMenu");
                }

                Region region = new Region();
                region.setLayoutX(5);

                Label menuName = new Label(topMenu.getMenuName());
                menuName.setLayoutX(0);
                menuName.setLayoutY(10);

                if (!topMenu.isStaticMenu()) {
                    region.setId("addMenu");
                    menuName.setId("addMenuLabel");

                    // 닫기 버튼 삽입
                    Button closeButton = new Button("");
                    closeButton.getStyleClass().add("close_btn");
                    closeButton.setLayoutX(158);
                    closeButton.setLayoutY(10);

                    //메뉴 삭제 이벤트 바인딩
                    closeButton.setOnMouseClicked(event -> {
                        logger.info("remove top menu idx : " + topMenu.getDisplayOrder());
                        // 해당 메뉴 객체 삭제
                        removeTopMenu(topMenu.getDisplayOrder());
                        // 다른 메뉴 출력
                        if(selectedTopMenuIdx == topMenu.getDisplayOrder()) {
                            logger.info("현재 보고 있는 메뉴 삭제");
                            selectedTopMenuIdx -= 1;
                            // 상단 메뉴 출력 새로고침
                            refreshShowTopMenu(-1);
                            // 현재 선택된 메뉴를 삭제하는 경우 바로 좌측 메뉴 출력
                            showTopMenuContents(topMenus[selectedTopMenuIdx], 0);
                        } else if(selectedTopMenuIdx < topMenu.getDisplayOrder()) {
                            logger.info("현재 보고 있는 메뉴 다음 메뉴 삭제");
                            refreshShowTopMenu(selectedTopMenuIdx);
                        } else {
                            logger.info("현재 보고 있는 메뉴 이전 메뉴 삭제");
                            selectedTopMenuIdx = selectedTopMenuIdx - 1;
                            refreshShowTopMenu(selectedTopMenuIdx);
                        }
                    });
                    menu.getChildren().setAll(region, menuName, closeButton);
                } else {
                    menu.getChildren().setAll(region, menuName);
                }

                // 마우스 커서 타입 설정
                menu.setCursor(Cursor.HAND);
                menu.setOnMouseClicked(event -> showTopMenuContents(topMenu, 0));

                topMenuGroups[idx] = menu;
                idx++;
            }
            topMenuArea.getChildren().setAll(topMenuGroups);

        }
    }

    /**
     * 상단 메뉴 추가
     * @param menu
     * @param addPositionIdx
     * @param isDisplay
     */
    public void addTopMenu(TopMenu menu, int addPositionIdx, boolean isDisplay) {
        // 중복 체크
        boolean isAdded = false;
        int addedMenuIdx = 0;
        for(TopMenu topMenu : this.topMenus) {
            if(!StringUtils.isEmpty(menu.getId()) && !StringUtils.isEmpty(topMenu.getId()) &&
                    menu.getId().equals(topMenu.getId())) {
                    isAdded = true;
                    break;
            }
            addedMenuIdx++;
        }
        logger.info(String.format("top menu added : %s, menu index : %s", isAdded, addedMenuIdx));

        // 추가되어 있지 않은 경우 추가
        if(!isAdded) {
            TopMenu[] newMenus = new TopMenu[topMenus.length + 1];
            TopMenu addMenu = null;
            Node[] newSubScenes = new Node[topMenus.length + 1];
            Node addNode = null;
            for (int i = 0; i < newMenus.length; i++) {
                if(i == addPositionIdx) {
                    addMenu = menu;
                    addNode = null;
                } else if(i < addPositionIdx) {
                    addMenu = topMenus[i];
                    addNode = topMenuContent[i];
                } else {
                    addMenu = topMenus[i-1];
                    addNode = topMenuContent[i-1];
                }
                // 메뉴 출력 순서 업데이트
                addMenu.setDisplayOrder(i);
                newMenus[i] = addMenu;
                newSubScenes[i] = addNode;
            }
            topMenus = newMenus;
            topMenuContent = newSubScenes;

            // 추가 대상 메뉴 컨텐츠 출력 설정인 경우
            if(isDisplay) {
                refreshShowTopMenu(-1);
                selectedTopMenuIdx = addPositionIdx;
                showTopMenuContents(menu, 0);
            } else {
                // 기존 메뉴 선택 상태 처리
                if(selectedTopMenuIdx >= addPositionIdx) {
                    selectedTopMenuIdx++;
                }
                refreshShowTopMenu(selectedTopMenuIdx);
            }
        } else {
            // 이미 추가 되어있는 경우 해당 메뉴 화면으로 전환함.
            selectedTopMenuIdx = addedMenuIdx;
            refreshShowTopMenu(selectedTopMenuIdx);
            showTopMenuContents(null, addedMenuIdx);
        }

    }

    /**
     * 지정 배열의 상단 메뉴 엘레멘트 삭제
     * @param removeIdx
     */
    private void removeTopMenu(int removeIdx) {
        if(topMenus != null && topMenus.length > 0) {
            int idx = 0;
            TopMenu[] newTopMenus = ArrayUtils.remove(topMenus, removeIdx);
            topMenuContent = ArrayUtils.remove(topMenuContent, removeIdx);
            for (TopMenu topMenu : newTopMenus) {
                topMenu.setDisplayOrder(idx);
                newTopMenus[idx] = topMenu;
                idx++;
            }
            topMenus = newTopMenus;
        }

    }

    /**
     * 선택 상단 메뉴 컨텐츠 출력
     * @param menu
     * @param showIdx
     */
    public void showTopMenuContents(TopMenu menu, int showIdx) {
        mainFrame.setCenter(null);
        if(menu == null) menu = topMenus[showIdx];

        Group group = (Group) topMenuArea.getChildren().get(menu.getDisplayOrder());
        group.setId("selectedMenu");

        // 현재 선택된 메뉴와 컨텐츠 출력 대상 메뉴가 다른경우
        if(selectedTopMenuIdx != menu.getDisplayOrder()) {
            // 기존 선택 메뉴 아이디 제거
            Group preSelectMenuGroup = (Group) topMenuArea.getChildren().get(selectedTopMenuIdx);
            preSelectMenuGroup.setId(null);
        }

        selectedTopMenuIdx = menu.getDisplayOrder();

        // 최초 화면 출력 여부
        boolean isFirstShow = false;
        try {
            if (!StringUtils.isEmpty(menu.getFxmlPath())) {
                logger.info("mainFrame display fxmlPath : " + menu.getMenuName());

                if(topMenuContent[menu.getDisplayOrder()] == null) {
                    FXMLLoader loader = mainApp.load(menu.getFxmlPath());
                    Node node = (Node) loader.load();
                    isFirstShow = true;

                    switch (menu.getFxmlPath()) {
                        case FXMLConstants.HOME:
                            homeController = loader.getController();
                            homeController.setMainController(this);
                            homeController.setParamMap(menu.getParamMap());
                            homeController.show((Parent) node);
                            break;
                        case FXMLConstants.PAST_RESULTS:    // 분석자 Past Results
                            pastResultsController = loader.getController();
                            pastResultsController.setMainController(this);
                            pastResultsController.setParamMap(menu.getParamMap());
                            pastResultsController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_LAYOUT:
                            AnalysisDetailLayoutController analysisDetailLayoutController = loader.getController();
                            analysisDetailLayoutController.setMainController(this);
                            analysisDetailLayoutController.setParamMap(menu.getParamMap());
                            analysisDetailLayoutController.show((Parent) node);
                            break;
                        case FXMLConstants.SYSTEM_MANAGER_HOME:
                            systemManagerHomeController= loader.getController();
                            systemManagerHomeController.setMainController(this);
                            systemManagerHomeController.setParamMap(menu.getParamMap());
                            systemManagerHomeController.show((Parent) node);
                            break;
                        default:
                            break;
                    }
                    topMenuContent[menu.getDisplayOrder()] = mainFrame.getCenter();
                } else {
                    mainFrame.setCenter(topMenuContent[menu.getDisplayOrder()]);
                }

                if("homeWrapper".equals(currentShowFrameId)) {	// 이전 화면이 분석자 HOME인 경우 자동 새로고침 토글
                    homeController.autoRefreshTimeline.stop();
                    homeController.sampleListAutoRefreshTimeline.stop();
                } else if("experimentPastResultsWrapper".equals(currentShowFrameId)) {	// 이전 화면이 분석자 Past Results인 경우 자동 새로고침 토글
                    pastResultsController.pauseAutoRefresh();
                } else if ("systemManagerHomeWrapper".equals(currentShowFrameId)) {
                    homeController.autoRefreshTimeline.stop();
                    homeController.sampleListAutoRefreshTimeline.stop();
                    pastResultsController.pauseAutoRefresh();
                }

                if("homeWrapper".equals(mainFrame.getCenter().getId())) {	// 현재 출력화면이 분석자 HOME 화면인 경우 다른 화면의 자동 새로고침 실행 토글 처리
                    // 최초 화면 출력이 아닌 경우 분석자 HOME 화면 자동 새로고침 기능 시작
                    if(!isFirstShow) {
                        homeController.autoRefreshTimeline.play();
                        homeController.sampleListAutoRefreshTimeline.play();
                    }
                } else if("experimentPastResultsWrapper".equals(mainFrame.getCenter().getId())) {	// 현재 출력화면이 분석자 Past Results 화면인 경우 다른 화면의 자동 새로고침 실행 토글 처리
                    // 최초 화면 출력이 아닌 경우 분석자 Past Results 화면 자동 새로고침 기능 시작
                    if(!isFirstShow) pastResultsController.resumeAutoRefresh();
                }

                currentShowFrameId = mainFrame.getCenter().getId();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 선택 시스템 메뉴 Dialog창 출력
     * @param actionEvent
     */
    public void openSystemMenu(ActionEvent actionEvent) {
        MenuItem menuItem = (MenuItem) actionEvent.getSource();

        try {
            String menuId = menuItem.getId();
            FXMLLoader loader = null;
            if(menuId.equals(SystemMenuCode.EDIT.name())) {
                loader = mainApp.load(FXMLConstants.SYSTEM_MENU_EDIT);
                Node root = (Node) loader.load();
                SystemMenuEditController editController = loader.getController();
                editController.setMainController(this);
                editController.show((Parent) root);
            } else if(menuId.equals(SystemMenuCode.SETTINGS.name())) {
                loader = mainApp.load(FXMLConstants.SYSTEM_MENU_SETTING);
                Node root = (Node) loader.load();
                SystemMenuSettingController settingsController = loader.getController();
                settingsController.setMainController(this);
                settingsController.show((Parent) root);
            } else if(menuId.equals(SystemMenuCode.SUPPORT.name())) {
                loader = mainApp.load(FXMLConstants.SYSTEM_MENU_SUPPORT);
                Node root = (Node) loader.load();
                SystemMenuSupportController supportController = loader.getController();
                supportController.setMainController(this);
                supportController.show((Parent) root);
            } else if(menuId.equals(SystemMenuCode.LICENSE.name())) {
                loader = mainApp.load(FXMLConstants.SYSTEM_MENU_LICENSE);
                Node root = (Node) loader.load();
                SystemMenuLicenseController licenseController = loader.getController();
                licenseController.setMainController(this);
                licenseController.show((Parent) root);
            } else if(menuId.equals(SystemMenuCode.PUBLIC_DATABASES.name())) {
                loader = mainApp.load(FXMLConstants.SYSTEM_MENU_PUBLIC_DATABASES);
                Node root = (Node) loader.load();
                SystemMenuPublicDatabasesController publicDatabasesController = loader.getController();
                publicDatabasesController.setMainController(this);
                publicDatabasesController.show((Parent) root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void logout() {
        boolean isLogoutContinue = false;
        String alertHeaderText = null;
        String alertContentText = "Do you want to log out?";

        // 진행중인 분석 요청건이 있는지 확인
        if(progressTaskContentArea.getChildren() != null && progressTaskContentArea.getChildren().size() > 0) {
            alertHeaderText = "There is a work of analysis request in progress.";
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner((Window) this.primaryStage);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(alertHeaderText);
        alert.setContentText(alertContentText);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() &&result.get() == ButtonType.OK){
            // 진행중인 분석 요청건이 있는 경우 정지 처리
            if(progressTaskContentArea.getChildren() != null && progressTaskContentArea.getChildren().size() > 0) {
                if(this.analysisSampleUploadProgressTaskController != null) {
                    this.analysisSampleUploadProgressTaskController.pauseUpload();
                    this.analysisSampleUploadProgressTaskController.interruptForce();
                }
            }
            isLogoutContinue = true;
        } else {
            alert.close();
        }

        if(isLogoutContinue) {
            logoutForce();
        }

    }

    public void logoutForce() {
        try {

            // 세션 캐쉬 메모리 삭제
            if(!cacheMemoryService.isEmpty(CommonConstants.SESSION_CACHE_SET_NAME, CommonConstants.SESSION_CACHE_KEY_NAME)) {
                cacheMemoryService.removeCacheKey(CommonConstants.SESSION_CACHE_SET_NAME, CommonConstants.SESSION_CACHE_KEY_NAME);
            }

            // 분석자 HOME 자동 새로고침 기능 중지
            if(homeController != null && homeController.autoRefreshTimeline != null) {
                homeController.autoRefreshTimeline.stop();
                homeController.sampleListAutoRefreshTimeline.stop();
            }

            // 분석자 Past Results 자동 새로고침 기능 중지
            if(pastResultsController != null) {
                pastResultsController.autoRefreshTimeline.stop();
            }

            // 로그인 화면으로 전환
            this.mainApp.showLogin();
        } catch (Exception e) {
            logger.error("logout fail.." + e.getMessage());
            DialogUtil.error(null, "Sorry, Logout Failed.", this.mainApp.getPrimaryStage(), false);
            e.printStackTrace();
        }

    }


    /**
     * 분석 요청 업로드 작업 실행
     */
    public void runningAnalysisRequestUpload(List<AnalysisFile> uploadFileData, List<File> fileList, Run run) {
        try {
            FXMLLoader loader = mainApp.load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_PROGRESS_TASK);
            HBox box = loader.load();
            this.analysisSampleUploadProgressTaskController = loader.getController();
            this.analysisSampleUploadProgressTaskController.setMainController(this);
            if(uploadFileData != null && !uploadFileData.isEmpty()) {
                Map<String,Object> param = new HashMap<>();
                param.put("fileMap", uploadFileData);
                param.put("fileList", fileList);
                param.put("run", run);
                this.analysisSampleUploadProgressTaskController.setParamMap(param);
            }
            this.analysisSampleUploadProgressTaskController.show(box);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the progressTaskContentArea
     */
    public HBox getProgressTaskContentArea() {
        return progressTaskContentArea;
    }

    /**
     * @return the analysisSampleUploadProgressTaskController
     */
    public AnalysisSampleUploadProgressTaskController getAnalysisSampleUploadProgressTaskController() {
        return analysisSampleUploadProgressTaskController;
    }

    /**
     * 진행 상태 출력 영역 초기화
     */
    public void clearProgressTaskArea() {
        this.analysisSampleUploadProgressTaskController = null;
        progressTaskContentArea.getChildren().removeAll(progressTaskContentArea.getChildren());
    }

    /**
     * 상단 탭 메뉴 스크롤 왼족으로 이동
     */
    @FXML
    public void moveScrollLeft() {
        double moveLength = topMenuScrollPane.getWidth()/topMenuArea.getWidth();
        logger.info(String.format("scroll move [left] H-value : %s, move length : %s", topMenuScrollPane.getHvalue(), moveLength));
        topMenuScrollPane.setHvalue(topMenuScrollPane.getHvalue() - moveLength);
    }

    /**
     * 상단 탭 메뉴 스크롤 오른족으로 이동
     */
    @FXML
    public void moveScrollRight() {
        double moveLength = topMenuScrollPane.getWidth()/topMenuArea.getWidth();
        logger.info(String.format("scroll move [right] H-value : %s, move length : %s", topMenuScrollPane.getHvalue(), moveLength));
        topMenuScrollPane.setHvalue(topMenuScrollPane.getHvalue() + moveLength);
    }

    /**
     * 지정 아이디에 해당하는 객체 삭제
     * @param id
     */
    public void removeProgressTaskItemById(String id) {
        if(this.progressTaskContentArea.getChildren() != null && this.progressTaskContentArea.getChildren().size() > 0) {
            int idx = 0;
            for(Node node : this.progressTaskContentArea.getChildren()) {
                if(!StringUtils.isEmpty(node.getId()) && id.equals(node.getId())) {
                    break;
                }
                idx++;
            }
            this.progressTaskContentArea.getChildren().remove(idx);
        }
    }

    /**
     * 자동 새로고침 설정 저장 내용 적용
     */
    public void applyAutoRefreshSettings() {
        // 현재 화면에 출력중인 화면이 분석자 HOME 화면인 경우
        if("HomeWrapper".equals(currentShowFrameId)) {
            homeController.autoRefreshTimeline.play();
            if(pastResultsController != null) {
                pastResultsController.startAutoRefresh();
            }
        }

        // 현재 화면에 출력중인 화면이 분석자 Past Results 화면인 경우
        if("experimentPastResultsWrapper".equals(currentShowFrameId)) {
            pastResultsController.startAutoRefresh();
            if(homeController != null) {
                homeController.autoRefreshTimeline.play();
            }
        }
    }

}
