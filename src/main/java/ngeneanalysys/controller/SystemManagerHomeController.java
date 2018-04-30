package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.SystemManagerTabMenuCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.render.SystemManagerTabItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.FXMLLoadUtil;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-09-04
 */
public class SystemManagerHomeController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private GridPane systemManagerHomeWrapper;

    /** api server 연동 서비스 */
    private APIService apiService;

    /** tab */
    @FXML
    private TabPane topTabPane;

    private SystemManagerAnalysisStatusController systemManagerAnalysisStatusController;

    private SystemManagerUserAccountController systemManagerUserAccountController;

    private SystemManagerSystemLogsController systemManagerSystemLogsController;

    private SystemManagerPanelController systemManagerPanelController ;

    private SystemManagerReportTemplateController systemManagerReportTemplateController;

    private SystemManagerInterpretationDatabaseController systemManagerInterpretationDatabaseController;

    private SystemManagerNewsAndTipsController systemManagerNewsAndTipsController;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("System Manager Home");

        apiService = APIService.getInstance();
        apiService.setStage(getMainApp().getPrimaryStage());

        int idx = 0;
        for(SystemManagerTabMenuCode code : SystemManagerTabMenuCode.values()) {
            SystemManagerTabItem item = code.getItem();
            Tab tab = new Tab();
            tab.setId(item.getNodeId());
            tab.setText(item.getTabName());
            tab.setClosable(false);
            if(idx == 0) {
                setTabContent(tab);
            }
            topTabPane.getTabs().add(tab);
            idx++;
        }

        topTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                setTabContent(newValue);
            }

        });


        this.mainController.getMainFrame().setCenter(root);

    }

    private void setTabContent(Tab tab) {
        if(tab.getContent() == null) {
            logger.debug(String.format("'%s' contents init..", tab.getId()));
            try {
                SystemManagerTabItem item = SystemManagerTabMenuCode.valueOf(tab.getId()).getItem();

                if(item != null) {
                    FXMLLoader loader = FXMLLoadUtil.load(item.getFxmlPath());
                    Node node = loader.load();

                    switch (item.getFxmlPath()) {
                        case FXMLConstants.SYSTEM_MANAGER_ANALYSIS_STATUS:
                            systemManagerAnalysisStatusController = loader.getController();
                            systemManagerAnalysisStatusController.setMainController(this.getMainController());
                            systemManagerAnalysisStatusController.show((Parent) node);
                            break;
                        case FXMLConstants.SYSTEM_MANAGER_USER_ACCOUNT:
                            systemManagerUserAccountController = loader.getController();
                            systemManagerUserAccountController.setMainController(this.getMainController());
                            systemManagerUserAccountController.show((Parent) node);
                            break;
                        case FXMLConstants.SYSTEM_MANAGER_LOG_LIST:
                            systemManagerSystemLogsController = loader.getController();
                            systemManagerSystemLogsController.setMainController(this.getMainController());
                            systemManagerSystemLogsController.show((Parent) node);
                            break;
                        case FXMLConstants.SYSTEM_MANAGER_PANEL:
                            systemManagerPanelController = loader.getController();
                            systemManagerPanelController.setMainController(this.getMainController());
                            systemManagerPanelController.show((Parent) node);
                            break;
                        case FXMLConstants.SYSTEM_MANAGER_REPORT_TEMPLATE :
                            systemManagerReportTemplateController = loader.getController();
                            systemManagerReportTemplateController.setMainController(this.getMainController());
                            systemManagerReportTemplateController.show((Parent) node);
                            break;
                        case FXMLConstants.SYSTEM_MANAGER_INTERPRETATION_DATABASE :
                            systemManagerInterpretationDatabaseController = loader.getController();
                            systemManagerInterpretationDatabaseController.setMainController(this.getMainController());
                            systemManagerInterpretationDatabaseController.show((Parent) node);
                            break;
                        case FXMLConstants.SYSTEM_MANAGER_NEWS_AND_TIPS :
                            systemManagerNewsAndTipsController = loader.getController();
                            systemManagerNewsAndTipsController.setMainController(this.getMainController());
                            systemManagerNewsAndTipsController.show((Parent) node);
                            break;
                        default:
                            break;
                    }
                    tab.setContent(node);
                } else {
                    logger.error("empty tab contents item..");
                }

            } catch (Exception e) {
                logger.error("tab contents loading fail." + e.getMessage());
                e.printStackTrace();
            }
        } else {
            refreshItem(tab.getId());
        }
    }

    public void refreshItem(String currentTabId) {
        if(currentTabId.equals(SystemManagerTabMenuCode.TAB_SYSTEM_MANAGER_REPORT_TEMPLATE
                .getItem().getNodeId())) {
            systemManagerReportTemplateController.setReportTableList(1);
            systemManagerReportTemplateController.setDisabledItem(true);
        } else if(currentTabId.equals(SystemManagerTabMenuCode.TAB_SYSTEM_MANAGER_INTERPRETATION_DATABASE
                .getItem().getNodeId())) {
            systemManagerInterpretationDatabaseController.setInterpretationList(1);
        } else if(currentTabId.equals(SystemManagerTabMenuCode.TAB_SYSTEM_MANAGER_NEWS_AND_TIPS
                .getItem().getNodeId())) {
            systemManagerNewsAndTipsController.setNewsAndTipsListTableList(1);
            systemManagerNewsAndTipsController.setDisabledItem(true);
        } else if(currentTabId.equals(SystemManagerTabMenuCode.TAB_SYSTEM_MANAGER_PANEL
                .getItem().getNodeId())) {
            systemManagerPanelController.setPanelList(1);
            systemManagerPanelController.setDisabledItem(true);
        } else if(currentTabId.equals(SystemManagerTabMenuCode.TAB_SYSTEM_MANAGER_ANALYSIS_STATUS
                .getItem().getNodeId())) {
            systemManagerAnalysisStatusController.setList(1);
        } else if(currentTabId.equals(SystemManagerTabMenuCode.TAB_SYSTEM_MANAGER_USER_ACCOUNT
                .getItem().getNodeId())) {
            systemManagerUserAccountController.setGroupList(1);
            systemManagerUserAccountController.setList(1);
        } else if(currentTabId.equals(SystemManagerTabMenuCode.TAB_SYSTEM_MANAGER_SYSTEM_LOGS
                .getItem().getNodeId())) {
            systemManagerSystemLogsController.setLogList(1);
        }
    }
}
