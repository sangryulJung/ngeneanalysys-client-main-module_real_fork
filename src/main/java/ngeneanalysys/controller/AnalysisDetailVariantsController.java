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
import ngeneanalysys.code.constants.CommonConstants;
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

    private AnalysisDetailTSTCNVController cnvController;

    private AnalysisDetailTSTFusionController tstFusionController;

    private AnalysisDetailTSTSpliceVariantController spliceVariantController;

    private AnalysisDetailTSTPublishedFusionController publishedFusionController;

    /** 이전 화면 정보 */
    private String currentShowFrameId;
    /** 현재 화면 출력중인 최상단 탭메뉴 인덱스 */
    private int selectedTopMenuIdx = 0;

    private Panel panel;

    private Label snvLabel;

    /**
     * @return detailContents
     */
    public BorderPane getDetailContents() {
        return detailContents;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("variants controller...");

        apiService = APIService.getInstance();

        panel = (Panel)paramMap.get("panel");

        setDefaultTab();
        refreshShowTopMenu(-1);
        showTopMenuContents(null, 0);

    }

    public void setSNVTabName(String text) {
        if(snvLabel != null) {
            if (!StringUtils.isEmpty(text)) {
                snvLabel.setText("SNV/Indel : " + text);
            } else {
                snvLabel.setText("SNV/Indel");
            }
        }
    }

    public void setDefaultTab() {
        if(panel.getTarget().equalsIgnoreCase("DNA")) {
            TopMenu menu;
            if(panel.getName().equals(CommonConstants.TST_170_DNA)) {
                topMenus = new TopMenu[2];
                topMenuContent = new Node[topMenus.length];
                menu = new TopMenu();
                menu.setMenuName("CNV");
                menu.setParamMap(getParamMap());
                menu.setFxmlPath(FXMLConstants.ANALYSIS_DETAIL_CNV);
                menu.setDisplayOrder(1);
                menu.setStaticMenu(true);
                topMenus[1] = menu;
            } else {
                topMenus = new TopMenu[1];
                topMenuContent = new Node[topMenus.length];
            }
            menu = new TopMenu();
            menu.setMenuName("SNV");
            menu.setParamMap(getParamMap());
            menu.setFxmlPath(FXMLConstants.ANALYSIS_DETAIL_VARIANTS_SNV);
            menu.setDisplayOrder(0);
            menu.setStaticMenu(true);
            topMenus[0] = menu;
        } else if(panel.getTarget().equalsIgnoreCase("RNA")) {
            if(panel.getName().equals(CommonConstants.TST_170_RNA)) {
                topMenus = new TopMenu[3];
                topMenuContent = new Node[topMenus.length];
                TopMenu menu = new TopMenu();
                menu.setMenuName("Fusion");
                menu.setParamMap(getParamMap());
                menu.setFxmlPath(FXMLConstants.ANALYSIS_DETAIL_TST_FUSION);
                menu.setDisplayOrder(0);
                menu.setStaticMenu(true);
                topMenus[0] = menu;

                menu = new TopMenu();
                menu.setMenuName("Splice Variant");
                menu.setParamMap(getParamMap());
                menu.setFxmlPath(FXMLConstants.ANALYSIS_DETAIL_TST_SPLICE_VARIANT);
                menu.setDisplayOrder(1);
                menu.setStaticMenu(true);
                topMenus[1] = menu;

                menu = new TopMenu();
                menu.setMenuName("Published Fusion");
                menu.setParamMap(getParamMap());
                menu.setFxmlPath(FXMLConstants.ANALYSIS_DETAIL_TST_PUBLISHED_FUSION);
                menu.setDisplayOrder(2);
                menu.setStaticMenu(true);
                topMenus[2] = menu;
            }
        }
    }

    /**
     * 상단 메뉴 새로 출력
     * @param selectIdx int
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
                Label menuName = new Label(topMenu.getMenuName());
                menuName.setLayoutX(0);

                region.getStyleClass().removeAll(region.getStyleClass());
                menuName.getStyleClass().removeAll(menuName.getStyleClass());
                if(topMenu.getMenuName().equals("SNV")) {
                    snvLabel = menuName;
                    menu.getStyleClass().add("group-big");
                    region.getStyleClass().add("region-big");
                    menuName.getStyleClass().add("label-big");
                } else {
                    menu.getStyleClass().add("group-small");
                    region.getStyleClass().add("region-small");
                    menuName.getStyleClass().add("label-small");
                }
                menu.getChildren().setAll(region, menuName);

                // 마우스 커서 타입 설정
                menu.setCursor(Cursor.HAND);
                menu.setOnMouseClicked(event -> showTopMenuContents(topMenu, selectIdx));

                topMenuGroups[idx] = menu;
                idx++;
            }
            tabMenuHBox.getChildren().setAll(topMenuGroups);

        }
    }

    /**
     * 선택 상단 메뉴 컨텐츠 출력
     * @param menu TopMenu
     * @param showIdx int
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
                logger.debug("mainFrame display fxmlPath : " + menu.getMenuName());

                if(topMenuContent[menu.getDisplayOrder()] == null) {
                    FXMLLoader loader = mainApp.load(menu.getFxmlPath());
                    Node node = loader.load();

                    switch (menu.getFxmlPath()) {
                        case FXMLConstants.ANALYSIS_DETAIL_CNV:
                            cnvController = loader.getController();
                            cnvController.setMainController(this.getMainController());
                            cnvController.setVariantsController(this);
                            cnvController.setParamMap(menu.getParamMap());
                            cnvController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_VARIANTS_SNV:
                            snvController = loader.getController();
                            snvController.setMainController(this.getMainController());
                            snvController.setVariantsController(this);
                            snvController.setParamMap(menu.getParamMap());
                            snvController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_TST_FUSION:
                            tstFusionController = loader.getController();
                            tstFusionController.setMainController(this.getMainController());
                            tstFusionController.setVariantsController(this);
                            tstFusionController.setParamMap(menu.getParamMap());
                            tstFusionController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_TST_SPLICE_VARIANT:
                            spliceVariantController = loader.getController();
                            spliceVariantController.setMainController(this.getMainController());
                            spliceVariantController.setVariantsController(this);
                            spliceVariantController.setParamMap(menu.getParamMap());
                            spliceVariantController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_TST_PUBLISHED_FUSION:
                            publishedFusionController = loader.getController();
                            publishedFusionController.setMainController(this.getMainController());
                            publishedFusionController.setVariantsController(this);
                            publishedFusionController.setParamMap(menu.getParamMap());
                            publishedFusionController.show((Parent) node);
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
