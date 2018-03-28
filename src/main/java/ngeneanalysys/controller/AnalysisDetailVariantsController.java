package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.TopMenu;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-03-15
 */
public class AnalysisDetailVariantsController extends AnalysisDetailCommonController {
    private static final Logger logger = LoggerUtil.getLogger();

    /** api server 연동 서비스 */
    private APIService apiService;

    /** 최상단 탭메뉴 정보 배열  */
    private TopMenu[] topMenus = null;
    /** 최상단 탭메뉴 화면 출력중인 컨텐츠 Scene 배열  */
    private Node[] topMenuContent;

    @FXML
    private HBox tabMenuHBox;

    @FXML
    private BorderPane detailContents;

    private AnalysisDetailSNVController snvController;

    /** 이전 화면 정보 */
    private String currentShowFrameId;
    /** 현재 화면 출력중인 최상단 탭메뉴 인덱스 */
    private int selectedTopMenuIdx = 0;

    private Panel panel;

    /**
     * @return detailContents
     */
    public BorderPane getDetailContents() {
        return detailContents;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("variants controller...");

        apiService = APIService.getInstance();

        panel = (Panel)paramMap.get("panel");

        setDefaultTab();
        refreshShowTopMenu(-1);
        showTopMenuContents(null, 0);

    }

    public void setDefaultTab() {
        if(panel.getTarget().equalsIgnoreCase("DNA")) {
            topMenus = new TopMenu[1];
            topMenuContent = new Node[topMenus.length];
            TopMenu menu = new TopMenu();
            menu.setMenuName("SNV");
            menu.setParamMap(getParamMap());
            menu.setFxmlPath(FXMLConstants.ANALYSIS_DETAIL_VARIANTS_SNV);
            menu.setDisplayOrder(0);
            menu.setStaticMenu(true);
            topMenus[0] = menu;
        } else if(panel.getTarget().equalsIgnoreCase("RNA")) {
            topMenus = new TopMenu[2];
            topMenuContent = new Node[topMenus.length];
            TopMenu menu = new TopMenu();
            menu.setMenuName("CNV");
            menu.setParamMap(getParamMap());
            menu.setFxmlPath(FXMLConstants.ANALYSIS_DETAIL_VARIANTS_SNV);
            menu.setDisplayOrder(0);
            menu.setStaticMenu(true);
            topMenus[0] = menu;

            menu = new TopMenu();
            menu.setMenuName("TARGET");
            menu.setParamMap(getParamMap());
            menu.setFxmlPath(FXMLConstants.ANALYSIS_DETAIL_VARIANTS_SNV);
            menu.setDisplayOrder(1);
            menu.setStaticMenu(true);
            topMenus[1] = menu;
        }
    }

    /**
     * 상단 메뉴 새로 출력
     * @param selectIdx
     */
    public void refreshShowTopMenu(int selectIdx) {
        // 기존 메뉴 엘레멘트 제거
        tabMenuHBox.getChildren().removeAll();
        if(topMenus != null && topMenus.length > 0) {
            int idx = 0;
            Group[] topMenuGroups = new Group[topMenus.length];
            for (TopMenu topMenu : topMenus) {
                Group menu = new Group();

                if (selectIdx >= 0 && idx == selectIdx) {
                    menu.setId("selectedMenu");
                }

                Region region = new Region();
                //region.setLayoutX();

                Label menuName = new Label(topMenu.getMenuName());
                menuName.setLayoutX(0);

                menu.getChildren().setAll(region, menuName);

                // 마우스 커서 타입 설정
                menu.setCursor(Cursor.HAND);
                menu.setOnMouseClicked(event -> showTopMenuContents(topMenu, 0));

                topMenuGroups[idx] = menu;
                idx++;
            }
            tabMenuHBox.getChildren().setAll(topMenuGroups);

        }
    }

    /**
     * 선택 상단 메뉴 컨텐츠 출력
     * @param menu
     * @param showIdx
     */
    public void showTopMenuContents(TopMenu menu, int showIdx) {
        detailContents.setCenter(null);
        if(menu == null) menu = topMenus[showIdx];

        Group group = (Group) tabMenuHBox.getChildren().get(menu.getDisplayOrder());
        group.setId("selectedMenu");

        // 현재 선택된 메뉴와 컨텐츠 출력 대상 메뉴가 다른경우
        if(selectedTopMenuIdx != menu.getDisplayOrder()) {
            // 기존 선택 메뉴 아이디 제거
            Group preSelectMenuGroup = (Group) tabMenuHBox.getChildren().get(selectedTopMenuIdx);
            preSelectMenuGroup.setId(null);
        }

        selectedTopMenuIdx = menu.getDisplayOrder();

        try {
            if (!StringUtils.isEmpty(menu.getFxmlPath())) {
                logger.info("mainFrame display fxmlPath : " + menu.getMenuName());

                if(topMenuContent[menu.getDisplayOrder()] == null) {
                    FXMLLoader loader = mainApp.load(menu.getFxmlPath());
                    Node node = loader.load();

                    switch (menu.getFxmlPath()) {
                        case FXMLConstants.ANALYSIS_DETAIL_VARIANTS_SNV:
                            snvController = loader.getController();
                            snvController.setMainController(this.getMainController());
                            snvController.setVariantsController(this);
                            snvController.setParamMap(menu.getParamMap());
                            snvController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_FUSION_GENE:

                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_GENE_EXPRESSION:

                            break;
                        default:
                            break;
                    }
                    topMenuContent[menu.getDisplayOrder()] = detailContents.getCenter();
                } else {
                    detailContents.setCenter(topMenuContent[menu.getDisplayOrder()]);
                }

                currentShowFrameId = detailContents.getCenter().getId();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
