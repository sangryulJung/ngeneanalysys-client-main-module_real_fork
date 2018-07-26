package ngeneanalysys.controller;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.SystemMenuCode;
import ngeneanalysys.code.enums.UserTypeBit;
import ngeneanalysys.controller.extend.BaseStageController;
import ngeneanalysys.controller.systemManager.SystemManagerHomeController;
import ngeneanalysys.controller.systemMenu.SystemMenuEditController;
import ngeneanalysys.controller.systemMenu.SystemMenuLicenseController;
import ngeneanalysys.controller.systemMenu.SystemMenuSettingController;
import ngeneanalysys.controller.systemMenu.SystemMenuSupportController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedPanel;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.CacheMemoryService;
import ngeneanalysys.service.PropertiesService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.controlsfx.control.MaskerPane;
import org.slf4j.Logger;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.*;

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
    private TopMenu[] sampleMenu = new TopMenu[]{};
    /** 최상단 탭메뉴 화면 출력중인 컨텐츠 Scene 배열  */
    private Node[] sampleContent = new Node[]{};

    private String currentSampleId = null;

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

    private RawDataDownloadProgressTaskController rawDataDownloadProgressTaskController;

    /** 메인 레이아웃 화면 Stage */
    private Stage primaryStage;

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

    @FXML
    private Label managerBtn;

    @FXML
    private Label dashBoardBtn;

    @FXML
    private Label resultsBtn;

    @FXML
    private HBox topMenuArea1;

    @FXML
    private Label sampleListLabel;

    @FXML
    private StackPane sampleListStackPane;

    @FXML
    private GridPane mainGridPane;
    
    @FXML
    private VBox mainBackground;

    @FXML
    private HBox topTabArea;

    private ComboBox<ComboBoxItem> sampleList;

    private Map<String, Object> basicInformationMap = new HashMap<>();

    private Queue<Map<String, Object>> uploadListQueue = new LinkedList<>();

    private Queue<RawDataDownloadInfo> downloadListQueue = new LinkedList<>();

    private MaskerPane contentsMaskerPane = new MaskerPane();

    public void setContentsMaskerPaneVisible(boolean flag) {
        contentsMaskerPane.setVisible(flag);
    }

    /**
     * @return Map<String, Object>
     */
    public Map<String, Object> getBasicInformationMap() {
        return basicInformationMap;
    }

    /**
     * 메인 컨텐츠 프레임 Pane Node 객체 반환
     * @return BorderPane
     */
    public BorderPane getMainFrame() {
        return this.mainFrame;
    }

    /**
     * 메인 레이아웃의 Stage 객체 반환
     * @return Stage
     */
    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("main controller...");
        
        PropertiesService propertiesService = PropertiesService.getInstance();	
        String theme = propertiesService.getConfig().getProperty("window.theme");
        applyTheme(theme);
    	
        apiService = APIService.getInstance();
        cacheMemoryService = CacheMemoryService.getInstance();

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int usersMonitorWidth = gd.getDisplayMode().getWidth();
        int usersMonitorHeight = gd.getDisplayMode().getHeight();
        logger.debug(String.format("user's monitor size [width : %s, height : %s]", usersMonitorWidth, usersMonitorHeight));

        int setWindowHeight = CommonConstants.DEFAULT_MAIN_SCENE_HEIGHT;
        if (usersMonitorHeight < CommonConstants.DEFAULT_MAIN_SCENE_HEIGHT) {
            setWindowHeight = CommonConstants.MIN_MAIN_SCENE_HEIGHT;
        }

        primaryStage = this.mainApp.getPrimaryStage();
        primaryStage.setScene(new Scene(root));
        //primaryStage.setTitle(CommonConstants.SYSTEM_NAME);
        primaryStage.setTitle("NGeneAnalySys");
        // OS가 Window인 경우 아이콘 출력.
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            primaryStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }

        primaryStage.setMinHeight(setWindowHeight + 35);
        primaryStage.setHeight(setWindowHeight + 35);
        primaryStage.setMaxHeight(Double.MAX_VALUE);
        primaryStage.setMinWidth(1280);
        primaryStage.setWidth(1280);
        primaryStage.setMaxWidth(Double.MAX_VALUE);
        primaryStage.setResizable(true);

        primaryStage.centerOnScreen();
        primaryStage.show();
        logger.debug(String.format("start %s", primaryStage.getTitle()));

        mainGridPane.add(maskerPane,0 ,0, 4, 4);
        maskerPane.setPrefWidth(primaryStage.getWidth());
        maskerPane.setPrefHeight(primaryStage.getHeight());
        maskerPane.setVisible(false);

        mainGridPane.add(contentsMaskerPane,1 ,2, 2, 1);
        contentsMaskerPane.setPrefWidth(mainFrame.getPrefWidth());
        contentsMaskerPane.setPrefHeight(mainFrame.getPrefHeight());
        contentsMaskerPane.setVisible(false);

        primaryStage.setOnCloseRequest(event -> {
            /*if(!progressTaskContentArea.getChildren().isEmpty()) {
                String alertContentText = "The job is running. Are you sure you want to quit?";

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(this.primaryStage);
                alert.setTitle("Warning Dialog");
                alert.setContentText(alertContentText);

                Optional<ButtonType> result = alert.showAndWait();

                if(result.get() == ButtonType.CANCEL) {
                    event.consume();
                } else {
                    closeEvent(event);
                }
            } else {
                closeEvent(event);
            }*/
            closeEvent(event);
        });

        //로그인 사용자 세션
        LoginSession loginSession = LoginSessionUtil.getCurrentLoginSession();
        String role = loginSession.getRole();

        //우상단 로그인 사용자명 삽입
        loginUserName.setText(loginSession.getName());

        //상단 메뉴 설정
        initDefaultTopMenu(role);
        showTopMenuContents(0);

        //상단 사용자 시스템 메뉴 설정
        initTopUserMenu(role);

        // 하단 빌드 정보 출력
        String buildVersion = config.getProperty("application.version");
        String buildDate = config.getProperty("application.build.date");
        labelSystemBuild.setText(String.format("v %s (Build Date %s)", buildVersion, buildDate));
        logger.debug(String.format("v %s (Build Date %s)", buildVersion, buildDate));

        // 중단된 분석 요청 작업이 있는지 체크

        dashBoardBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> showTopMenuContents(0));
        resultsBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> showTopMenuContents(1));
        managerBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> showTopMenuContents(3));

        final ComboBox<ComboBoxItem> sampleList = new ComboBox<ComboBoxItem>() {
            @Override
            protected javafx.scene.control.Skin<?> createDefaultSkin() {
                return new ComboBoxListViewSkin<ComboBoxItem>( this ) {
                    @Override
                    protected boolean isHideOnClickEnabled() {
                        return false;
                    }
                };
            }
        };

        this.sampleList = sampleList;
        sampleList.getStyleClass().add("combo-box");
        sampleList.setDisable(true);
        sampleListLabel.setDisable(true);
        sampleList.getStyleClass().addAll("combo-box", "combo-box-base");
        sampleListStackPane.getChildren().add(0, sampleList);

        sampleListStackPane.setOnMouseClicked(ev -> {
            if(!sampleList.getItems().isEmpty()) sampleList.show();
        });

        sampleList.setConverter(new ComboBoxConverter());
        sampleList.setVisibleRowCount(15);
        sampleList.setCellFactory(lv ->
            new ListCell<ComboBoxItem>() {
                private HBox graphic;

                {
                    Label label = new Label();
                    label.getStyleClass().removeAll(label.getStyleClass());

                    label.textProperty().bind(Bindings.convert(itemProperty()));

                    label.setMinWidth(150);
                    label.setPrefWidth(150);
                    label.setMaxWidth(150);

                    label.setOnMouseClicked(event -> {
                        if(itemProperty() == null) return;
                        Optional<TopMenu> optionalTopMenu = Arrays.stream(sampleMenu).filter(menu ->
                                menu.getId().equalsIgnoreCase(itemProperty().getValue().getValue())).findFirst();
                        optionalTopMenu.ifPresent(topMenu -> showSampleDetail(topMenu));
                        clearComboBox();
                        sampleList.hide();
                    });

                    Button btn = new Button("X");
                    btn.getStyleClass().removeAll(btn.getStyleClass());
                    btn.getStyleClass().add("remove_btn");
                    /*btn.setPrefWidth(6);
                    btn.setPrefHeight(6);*/
                    btn.addEventHandler(MouseEvent.MOUSE_CLICKED, event-> {
                        ComboBoxItem item = getItem();

                        removeTopMenu(item.getValue());
                        sampleList.getItems().remove(item);
                        sampleList.hide();
                        Platform.runLater(() -> sampleList.getSelectionModel().select(null));
                    });

                    graphic = new HBox(label, btn);
                    graphic.setPrefWidth(170);
                    graphic.setAlignment(Pos.CENTER);
                    HBox.setHgrow(label, Priority.ALWAYS);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }

                @Override
                protected void updateItem(ComboBoxItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(graphic);
                    }
                }

            });

        settingPanelAndDiseases();
        //primaryStage.setResizable(false);
    }

    private void closeEvent(Event event) {
        boolean isClose = false;
        String alertContentText = "Do you want to exit the application?";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        DialogUtil.setIcon(alert);
        alert.initOwner(this.primaryStage);
        alert.setTitle("Confirmation Dialog");
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
    }

    private void clearComboBox() {
        Platform.runLater(() -> sampleList.getSelectionModel().clearSelection());
    }

    public void settingPanelAndDiseases() {
        // 기본 정보 로드
        HttpClientResponse response = null;

        LoginSession loginSession = LoginSessionUtil.getCurrentLoginSession();
        String path = System.getProperty("user.home");
        basicInformationMap.put("path", path);
        try {
            Map<String,Object> params = new HashMap<>();
            if(loginSession.getRole().equalsIgnoreCase("ADMIN")) {
                params.put("skipOtherGroup", "false");
            } else {
                params.put("skipOtherGroup", "true");
            }
            response = apiService.get("/panels", params, null, false);
            final PagedPanel panels = response.getObjectBeforeConvertResponseToJSON(PagedPanel.class);
            basicInformationMap.put("panels", panels.getResult());

            response = apiService.get("/diseases", null, null, false);
            List<Diseases> diseases = (List<Diseases>)response.getMultiObjectBeforeConvertResponseToJSON(Diseases.class, false);
            basicInformationMap.put("diseases", diseases);
            createFilter();

        } catch (WebAPIException e) {
            DialogUtil.error(e.getHeaderText(), e.getMessage(), getMainApp().getPrimaryStage(),
                    false);
        }
    }

    private void createFilter() {
        HttpClientResponse response;

        Map<String, List<Object>> somaticFilter = new HashMap<>();

        try {
            response = apiService.get("member/memberOption/somaticFilter", null, null, null);
            somaticFilter = JsonUtil.fromJsonToMap(response.getContentString());
        } catch (WebAPIException wae) {

        } finally {
            setDefaultSomaticFilter(somaticFilter);
        }

        Map<String, List<Object>> germlineFilter = new HashMap<>();

        try {
            response = apiService
                    .get("member/memberOption/germlineFilter", null, null, null);
            germlineFilter = JsonUtil.fromJsonToMap(response.getContentString());

        } catch (WebAPIException wae) {

        } finally {
            setDefaultGermlineFilter(germlineFilter);
        }
    }

    private void setDefaultGermlineFilter(Map<String, List<Object>> germlineFilter) {
        germlineFilter.put("Pathogenic", setStandardFilter("pathogenicity", "P"));
        germlineFilter.put("Likely Pathogenic", setStandardFilter("pathogenicity", "LP"));
        germlineFilter.put("Uncertain Significance", setStandardFilter("pathogenicity", "US"));
        germlineFilter.put("Likely Benign", setStandardFilter("pathogenicity", "LB"));
        germlineFilter.put("Benign", setStandardFilter("pathogenicity", "B"));
        basicInformationMap.put("germlineFilter", germlineFilter);
    }

    private void setDefaultSomaticFilter(Map<String, List<Object>> somaticFilter) {
        somaticFilter.put("Tier 1", setStandardFilter("tier", "T1"));
        somaticFilter.put("Tier 2", setStandardFilter("tier", "T2"));
        somaticFilter.put("Tier 3", setStandardFilter("tier", "T3"));
        somaticFilter.put("Tier 4", setStandardFilter("tier", "T4"));
        basicInformationMap.put("somaticFilter", somaticFilter);
    }

    private List<Object> setStandardFilter(String key, String value) {
        List<Object> list = new ArrayList<>();
        list.add(key + " " + value);
        return list;
    }

    /**
     * 상단 사용자 메뉴 설정 : 사용자 권한에 따른 메뉴 출력
     * @param role String
     */
    private void initTopUserMenu(String role) {
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
     * @param role String
     */
    public void initDefaultTopMenu(String role) {
        if(UserTypeBit.ADMIN.name().equalsIgnoreCase(role)) {
            managerBtn.setVisible(true);
            topMenus = new TopMenu[4];
            topMenuContent = new Node[topMenus.length];
            TopMenu menu = new TopMenu();
            menu.setMenuName("MANAGER");
            menu.setFxmlPath(FXMLConstants.SYSTEM_MANAGER_HOME);
            menu.setDisplayOrder(3);
            menu.setStaticMenu(true);
            topMenus[3] = menu;
        } else {
            managerBtn.setVisible(false);
            topMenus = new TopMenu[3];
            topMenuContent = new Node[topMenus.length];
        }
        TopMenu menu = new TopMenu();
        menu.setMenuName("DASH BOARD");
        menu.setFxmlPath(FXMLConstants.HOME);
        menu.setStaticMenu(true);
        menu.setDisplayOrder(0);
        topMenus[0] = menu;

        menu = new TopMenu();
        menu.setMenuName("RESULT");
        menu.setFxmlPath(FXMLConstants.PAST_RESULTS);
        menu.setStaticMenu(true);
        menu.setDisplayOrder(1);
        topMenus[1] = menu;

        menu = new TopMenu();
        menu.setMenuName("SAMPLE");
        menu.setFxmlPath(null);
        menu.setStaticMenu(true);
        menu.setDisplayOrder(2);
        topMenus[2] = menu;
    }

    /**
     * 상단 메뉴 추가
     * @param menu TopMenu
     * @param addPositionIdx int
     * @param isDisplay boolean
     */
    public void addTopMenu(TopMenu menu, int addPositionIdx, boolean isDisplay) {
        // 중복 체크
        boolean isAdded = false;
        int addedMenuIdx = 0;
        for(TopMenu topMenu : this.sampleMenu) {
            if(StringUtils.isNotEmpty(menu.getId()) && StringUtils.isNotEmpty(topMenu.getId()) &&
                    menu.getId().equals(topMenu.getId())) {
                    isAdded = true;
                    break;
            }
            addedMenuIdx++;
        }
        logger.debug(String.format("top menu added : %s, menu index : %s", isAdded, addedMenuIdx));

        if(isAdded) {
            // 이미 추가 되어있는 경우 해당 메뉴 화면으로 전환함.
            showSampleDetail(menu);
            return;
        }

        if(sampleMenu.length == 8) {
            DialogUtil.warning("", "The maximum number of tabs is eight.", this.getPrimaryStage(), true);
            return;
        }

        // 추가되어 있지 않은 경우 추가

        if(sampleList.isDisabled()) {
            sampleList.setDisable(false);
            sampleListLabel.setDisable(false);
        }
        ComboBoxItem item = new ComboBoxItem();
        item.setText(menu.getMenuName());
        item.setValue(menu.getId());
        sampleList.getItems().add(item);
        TopMenu[] newMenus = new TopMenu[this.sampleMenu.length + 1];
        menu.setDisplayOrder(newMenus.length - 1);
        Node[] newSubScenes = new Node[this.sampleMenu.length + 1];
        Node addNode = null;
        System.arraycopy(this.sampleMenu, 0, newMenus, 0, sampleMenu.length);
        System.arraycopy(this.sampleContent, 0, newSubScenes, 0, sampleContent.length);

        newMenus[newMenus.length - 1] = menu;
        newSubScenes[newSubScenes.length - 1] = addNode;

        sampleMenu = newMenus;
        sampleContent = newSubScenes;

        // 추가 대상 메뉴 컨텐츠 출력 설정인 경우
        if(isDisplay) {
            showSampleDetail(menu);
        }
    }

    /**
     * 지정 배열의 상단 메뉴 엘레멘트 삭제
     * @param id String
     */
    private void removeTopMenu(String id) {
        if(sampleMenu != null && sampleMenu.length > 0) {
            int removeIdx = -1;
            for(int i = 0; i < sampleMenu.length ; i++) {
                if(sampleMenu[i].getId().equalsIgnoreCase(id)) {
                    removeIdx = i;
                    break;
                }
            }

            int idx = 0;
            TopMenu[] newTopMenus = ArrayUtils.remove(sampleMenu, removeIdx);
            sampleContent = ArrayUtils.remove(sampleContent, removeIdx);
            for (TopMenu topMenu : newTopMenus) {
                topMenu.setDisplayOrder(idx);
                newTopMenus[idx] = topMenu;
                idx++;
            }
            sampleMenu = newTopMenus;

            if(currentSampleId == null) {

            } else if(sampleMenu.length == 0) {
                currentSampleId = null;
                showTopMenuContents(1);
            } else {
                if(removeIdx > 0) {
                    currentSampleId = sampleMenu[removeIdx - 1].getId();
                    showSampleDetail(sampleMenu[removeIdx - 1]);
                } else {
                    showSampleDetail(sampleMenu[removeIdx]);
                }
            }
            if(sampleMenu == null || sampleMenu.length == 0) {
                sampleList.setDisable(true);
                sampleListLabel.setDisable(true);
            }
        }
    }

    private void showSampleDetail(final TopMenu menu) {
        boolean isFirstShow = false;
        if(selectedTopMenuIdx != 2) {

            sampleListLabel.setId("selectedMenu");

            Node preSelectMenuGroup = topMenuArea1.getChildren().get(selectedTopMenuIdx);
            preSelectMenuGroup.setId(null);

            selectedTopMenuIdx = 2;
        }
        currentSampleId = menu.getId();

        if(sampleContent[menu.getDisplayOrder()] == null) {
            isFirstShow = true;
            try {
                FXMLLoader loader = mainApp.load(menu.getFxmlPath());
                Node node = loader.load();
                AnalysisDetailLayoutController analysisDetailLayoutController = loader.getController();
                analysisDetailLayoutController.setMainController(this);
                analysisDetailLayoutController.setParamMap(menu.getParamMap());
                analysisDetailLayoutController.show((Parent) node);
            } catch (Exception e) {

            }
            sampleContent[menu.getDisplayOrder()] = mainFrame.getCenter();
        } else {
            for(int i = 0; i < sampleMenu.length ; i++) {
                if(sampleMenu[i].getId().equalsIgnoreCase(menu.getId())) {
                    mainFrame.setCenter(sampleContent[i]);
                    break;
                }
            }
        }

        setAuto(isFirstShow);

    }

    /**
     * 선택 상단 메뉴 컨텐츠 출력
     * @param showIdx int
     */
    public void showTopMenuContents(int showIdx) {
        if(showIdx == 2) return;
        mainFrame.setCenter(null);
        TopMenu menu = topMenus[showIdx];

        currentSampleId = null;

        Node item = topMenuArea1.getChildren().get(showIdx);
        item.setId("selectedMenu");

        // 현재 선택된 메뉴와 컨텐츠 출력 대상 메뉴가 다른경우
        if(selectedTopMenuIdx != menu.getDisplayOrder()) {
            // 기존 선택 메뉴 아이디 제거
            Node preSelectMenuGroup = topMenuArea1.getChildren().get(selectedTopMenuIdx);
            if(selectedTopMenuIdx != 2) {
                preSelectMenuGroup.setId(null);
            } else {
                sampleListLabel.setId(null);
            }
        }

        selectedTopMenuIdx = menu.getDisplayOrder();

        // 최초 화면 출력 여부
        boolean isFirstShow = false;
        try {
            if (StringUtils.isNotEmpty(menu.getFxmlPath())) {
                logger.debug("mainFrame display fxmlPath : " + menu.getMenuName());

                if(topMenuContent[menu.getDisplayOrder()] == null) {
                    FXMLLoader loader = mainApp.load(menu.getFxmlPath());
                    Node node = loader.load();
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

                setAuto(isFirstShow);

            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void setAuto(boolean isFirstShow) {
        if("homeWrapper".equals(currentShowFrameId)) {	// 이전 화면이 분석자 HOME인 경우 자동 새로고침 토글
            homeController.pauseAutoRefresh();
        } else if("experimentPastResultsWrapper".equals(currentShowFrameId)) {	// 이전 화면이 분석자 Past Results인 경우 자동 새로고침 토글
            pastResultsController.pauseAutoRefresh();
        } else if ("systemManagerHomeWrapper".equals(currentShowFrameId)) {
            homeController.pauseAutoRefresh();
            pastResultsController.pauseAutoRefresh();
        }

        if("homeWrapper".equals(mainFrame.getCenter().getId())) {	// 현재 출력화면이 분석자 HOME 화면인 경우 다른 화면의 자동 새로고침 실행 토글 처리
            // 최초 화면 출력이 아닌 경우 분석자 HOME 화면 자동 새로고침 기능 시작
            if(!isFirstShow) homeController.resumeAutoRefresh();

        } else if("experimentPastResultsWrapper".equals(mainFrame.getCenter().getId())) {	// 현재 출력화면이 분석자 Past Results 화면인 경우 다른 화면의 자동 새로고침 실행 토글 처리
            // 최초 화면 출력이 아닌 경우 분석자 Past Results 화면 자동 새로고침 기능 시작
            if(!isFirstShow) pastResultsController.resumeAutoRefresh();
        }

        currentShowFrameId = mainFrame.getCenter().getId();
    }

    /**
     * 선택 시스템 메뉴 Dialog창 출력
     * @param actionEvent ActionEvent
     */
    private void openSystemMenu(ActionEvent actionEvent) {
        MenuItem menuItem = (MenuItem) actionEvent.getSource();

        try {
            String menuId = menuItem.getId();
            FXMLLoader loader = null;
            if(menuId.equals(SystemMenuCode.EDIT.name())) {
                loader = mainApp.load(FXMLConstants.SYSTEM_MENU_EDIT);
                Node root = loader.load();
                SystemMenuEditController editController = loader.getController();
                editController.setMainController(this);
                editController.show((Parent) root);
            } else if(menuId.equals(SystemMenuCode.SETTINGS.name())) {
                loader = mainApp.load(FXMLConstants.SYSTEM_MENU_SETTING);
                Node root = loader.load();
                SystemMenuSettingController settingsController = loader.getController();
                settingsController.setMainController(this);
                settingsController.show((Parent) root);
            } else if(menuId.equals(SystemMenuCode.SUPPORT.name())) {
                loader = mainApp.load(FXMLConstants.SYSTEM_MENU_SUPPORT);
                Node root = loader.load();
                SystemMenuSupportController supportController = loader.getController();
                supportController.setMainController(this);
                supportController.show((Parent) root);
            } else if(menuId.equals(SystemMenuCode.LICENSE.name())) {
                loader = mainApp.load(FXMLConstants.SYSTEM_MENU_LICENSE);
                Node root = loader.load();
                SystemMenuLicenseController licenseController = loader.getController();
                licenseController.setMainController(this);
                licenseController.show((Parent) root);
            } /*else if(menuId.equals(SystemMenuCode.PUBLIC_DATABASES.name())) {
                loader = mainApp.load(FXMLConstants.SYSTEM_MENU_PUBLIC_DATABASES);
                Node root = loader.load();
                SystemMenuPublicDatabasesController publicDatabasesController = loader.getController();
                publicDatabasesController.setMainController(this);
                publicDatabasesController.show((Parent) root);
            }*/
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
        DialogUtil.setIcon(alert);
        alert.initOwner(this.primaryStage);
        alert.setTitle("Log out");
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
                if(this.rawDataDownloadProgressTaskController != null) {
                    this.rawDataDownloadProgressTaskController.pauseUpload();
                    this.rawDataDownloadProgressTaskController.interruptForce();
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
                homeController.pauseAutoRefresh();
            }

            // 분석자 Past Results 자동 새로고침 기능 중지
            if(pastResultsController != null) {
                pastResultsController.pauseAutoRefresh();
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
        if (uploadFileData != null && !uploadFileData.isEmpty()) {
            Map<String, Object> param = new HashMap<>();
            param.put("fileMap", uploadFileData);
            param.put("fileList", fileList);
            param.put("run", run);
            uploadListQueue.add(param);
            if(this.analysisSampleUploadProgressTaskController == null) {
                runUpload();
            }
        }
    }

    public void runUpload() {
        if(!uploadListQueue.isEmpty()) {
            try {
                FXMLLoader loader = mainApp.load(FXMLConstants.ANALYSIS_SAMPLE_UPLOAD_PROGRESS_TASK);
                HBox box = loader.load();
                this.analysisSampleUploadProgressTaskController = loader.getController();
                this.analysisSampleUploadProgressTaskController.setMainController(this);
                Map<String, Object> param = uploadListQueue.poll();
            /*if (uploadFileData != null && !uploadFileData.isEmpty()) {
                Map<String, Object> param = new HashMap<>();
                param.put("fileMap", uploadFileData);
                param.put("fileList", fileList);
                param.put("run", run);
                this.analysisSampleUploadProgressTaskController.setParamMap(param);
            }*/
                this.analysisSampleUploadProgressTaskController.setParamMap(param);
                this.analysisSampleUploadProgressTaskController.show(box);
            } catch (IOException e) {
                DialogUtil.error("ERROR", e.getMessage(), getMainApp().getPrimaryStage(),
                        false);
                e.printStackTrace();
            }
        }
    }

    public void runningRawDataDownload(File folder, RunSampleView run, List<String> type) {
        if (folder != null && run != null) {
            RawDataDownloadInfo info = new RawDataDownloadInfo(folder, run, type);
            downloadListQueue.add(info);
            if(this.rawDataDownloadProgressTaskController == null) {
                runDownload();
            }
        }
    }

    public void runDownload() {
        if(!downloadListQueue.isEmpty()) {
            try {
                FXMLLoader loader = mainApp.load(FXMLConstants.RAW_DATA_DOWNLOAD_TASK);
                HBox box = loader.load();
                this.rawDataDownloadProgressTaskController = loader.getController();
                this.rawDataDownloadProgressTaskController.setMainController(this);
                RawDataDownloadInfo info = downloadListQueue.poll();
                this.rawDataDownloadProgressTaskController.setInfo(info);
                this.rawDataDownloadProgressTaskController.show(box);
            } catch (IOException e) {
                DialogUtil.error("ERROR", e.getMessage(), getMainApp().getPrimaryStage(),
                        false);
                e.printStackTrace();
            }
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
    public void clearProgressTaskArea(Node node) {
        //progressTaskContentArea.getChildren().removeAll(progressTaskContentArea.getChildren());
        progressTaskContentArea.getChildren().remove(node);
        if(analysisSampleUploadProgressTaskController != null) {
            this.analysisSampleUploadProgressTaskController = null;
            runUpload();
        } else {
            this.rawDataDownloadProgressTaskController = null;
            runDownload();
        }
    }

    /**
     * 지정 아이디에 해당하는 객체 삭제
     * @param id String
     */
    public void removeProgressTaskItemById(String id) {
        if(this.progressTaskContentArea.getChildren() != null && this.progressTaskContentArea.getChildren().size() > 0) {
            int idx = 0;
            for(Node node : this.progressTaskContentArea.getChildren()) {
                if(StringUtils.isNotEmpty(node.getId()) && id.equals(node.getId())) {
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
        if("homeWrapper".equals(currentShowFrameId)) {
            homeController.startAutoRefresh();
            if(pastResultsController != null) {
                pastResultsController.startAutoRefresh();
                pastResultsController.pauseAutoRefresh();
            }
        }

        // 현재 화면에 출력중인 화면이 분석자 Past Results 화면인 경우
        if("experimentPastResultsWrapper".equals(currentShowFrameId)) {
            if(pastResultsController != null) pastResultsController.startAutoRefresh();
            if(homeController != null) {
                homeController.startAutoRefresh();
                homeController.pauseAutoRefresh();
            }
        }
    }

    public void setMainMaskerPane(boolean status) {
        maskerPane.setVisible(status);
    }
    
    public void applyTheme(String theme) {
    	logger.debug("Main theme: " + theme);
    	
    	if(theme.equalsIgnoreCase("default")) {
    		mainBackground.setStyle("-fx-background-image:url('layout/images/renewal/main_background.png');");
    	}else if(theme.equalsIgnoreCase("dark")) {
    		mainBackground.setStyle("-fx-background-image:url('layout/images/renewal/main_background01.png');");
    	}else if(theme.equalsIgnoreCase("red")) {
    		mainBackground.setStyle("-fx-background-image:url('layout/images/renewal/main_background03.png');");
    	}else if(theme.equalsIgnoreCase("ice")) {
    		mainBackground.setStyle("-fx-background-image:url('layout/images/renewal/main_background02.png');");
    	}else if(theme.equalsIgnoreCase("mountain")) {
    		mainBackground.setStyle("-fx-background-image:url('layout/images/renewal/main_background10.png');");
    	}else if(theme.equalsIgnoreCase("dna")) {
    		mainBackground.setStyle("-fx-background-image:url('layout/images/renewal/main_background12.png');");
    	}
    }

    public void deleteSampleTab(final String id) {
        Optional<ComboBoxItem> optionalTab = sampleList.getItems().stream().filter(item -> item.getValue()
                .equals(id)).findFirst();
        optionalTab.ifPresent(tab -> {
            sampleList.getItems().remove(tab);
            removeTopMenu(id);
        });

    }

}
