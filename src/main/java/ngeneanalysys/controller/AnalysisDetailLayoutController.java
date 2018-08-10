package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.AnalysisDetailTabMenuCode;
import ngeneanalysys.code.enums.AnalysisTypeCode;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.render.AnalysisDetailTabItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.FXMLLoadUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-08-25
 */
public class AnalysisDetailLayoutController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    /** 상단 샘플 요약 정보 > 사용 패널키트 */
    @FXML
    private Label panelLabel;
    @FXML
    private Tooltip panelNameTooltip;
    /** 상단 샘플 요약 정보 > 샘플명 */
    @FXML
    private Label sampleNameLabel;
    @FXML
    private Tooltip sampleNameTooltip;
    @FXML
    private Label runNameLabel;
    @FXML
    private Tooltip runNameTooltip;
    @FXML
    private Label diseaseLabel;
    @FXML
    private Tooltip diseaseTooltip;
    @FXML
    private Label sequencerLabel;
    @FXML
    private Tooltip sequencerTooltip;

    /** 상단 탭메뉴 영역 */
    @FXML
    private TabPane topTabPane;

    /** 현재 샘플의 고유 아아디 */
    private Integer sampleId;

    private SampleView sampleView;

    private Panel panel;

    private AnalysisDetailOverviewController analysisDetailOverviewController;

    private AnalysisDetailReportController analysisDetailReportController;

    private AnalysisDetailOverviewGermlineController analysisDetailOverviewGermlineController;

    private AnalysisDetailReportGermlineController analysisDetailReportGermlineController;

    private AnalysisDetailVariantsController analysisDetailVariantsController;

    private AnalysisDetailTSTRNAReportController tstrnaReportController;

    private AnalysisDetailTSTRNAOverviewController tstrnaOverviewController;

    /** API 서버 통신 서비스 */
    private APIService apiService;

    @FXML
    private Button rawDataDownload;

    @Override
    public void show(Parent root) throws IOException {
        mainController.setContentsMaskerPaneVisible(true);
        logger.debug("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        sampleId = (int) getParamMap().get("id");
        Platform.runLater(() -> {
        try {
            HttpClientResponse response = apiService.get("samples/" + sampleId, null, null, true);

            sampleView = response.getObjectBeforeConvertResponseToJSON(SampleView.class);

            getParamMap().put("sampleView", sampleView);

            setPaneAndDisease();

            runNameLabel.setText(sampleView.getRun().getName());
            runNameTooltip.setText(sampleView.getRun().getName());
            sequencerLabel.setText(WordUtils.capitalize(sampleView.getRun().getSequencingPlatform()));
            sequencerTooltip.setText(WordUtils.capitalize(sampleView.getRun().getSequencingPlatform()));

        } catch (WebAPIException e) {
            e.printStackTrace();
        }
        topTabPane.getTabs().clear();
        // 권한별 탭메뉴 추가
        int idx = 0;
        if(panel != null) {
            for (AnalysisDetailTabMenuCode code : AnalysisDetailTabMenuCode.values()) {
                AnalysisDetailTabItem item = code.getItem();

                if(item.getNodeId().equals("TAB_VARIANTS") || (item.getNodeId().contains("TST_RNA") &&
                        panel.getCode().equals(PipelineCode.TST170_RNA.getCode()))) {
                    addTab(item, idx);
                    idx++;
                } else if(!panel.getCode().equals(PipelineCode.TST170_RNA.getCode())) {
                    if (item.getNodeId().contains(AnalysisTypeCode.GERMLINE.getDescription()) &&
                            (panel.getAnalysisType() != null && AnalysisTypeCode.GERMLINE.getDescription().equals(panel.getAnalysisType()))) {
                        addTab(item, idx);
                        idx++;
                    } else if (item.getNodeId().contains(AnalysisTypeCode.SOMATIC.getDescription()) &&
                            (panel.getAnalysisType() != null && AnalysisTypeCode.SOMATIC.getDescription().equals(panel.getAnalysisType()))) {
                        addTab(item, idx);
                        idx++;
                    }
                }
            }
        } else {
            DialogUtil.warning("Panel error", "Panel not found.", mainApp.getPrimaryStage(), true);
        }

        // 탭메뉴 변경 리스너 설정 : 해당 탭 최초 선택 시 내용 삽입 처리.
        topTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                setTabContent(newValue);
            }
            // 탭 화면 선택시 마다 갱신해야하는 부분이 있는 경우 갱신 실행을 위한 메소드 실행.
            if (newValue.getContent() != null) {
                executeReloadByTab(newValue);
            }
        });
        mainController.setContentsMaskerPaneVisible(false);
        });
        this.mainController.getMainFrame().setCenter(root);

    }

    private void setPaneAndDisease() {
        if(sampleView.getPanel() != null) {
            this.panel = sampleView.getPanel();
            getParamMap().put("panel", panel);
            panelLabel.setText(panel.getName());
            panelNameTooltip.setText(panel.getName());
            sampleNameLabel.setText(sampleView.getName());
            sampleNameTooltip.setText(sampleView.getName());
            diseaseLabel.setText(sampleView.getDiseaseName());
            diseaseTooltip.setText(sampleView.getDiseaseName());
        }
    }

    private void addTab(AnalysisDetailTabItem item, int idx) {
        Tab tab = new Tab();
        tab.setId(item.getNodeId());
        tab.setText(item.getTabName());
        tab.setClosable(false);
        // 첫번째 탭 컨텐츠 삽입.
        if (idx == 0)
            setTabContent(tab);
        topTabPane.getTabs().add(tab);
    }

    /**
     * 탭 컨텐츠 삽입
     * @param tab Tab
     */
    private void setTabContent(Tab tab) {
        // 화면 내용이 없는 경우 셋팅.
        if(tab.getContent() == null) {
            logger.debug(String.format("'%s' contents init..", tab.getId()));
            try {
                AnalysisDetailTabItem item = AnalysisDetailTabMenuCode.valueOf(tab.getId()).getItem();

                if(item != null) {
                    FXMLLoader loader = FXMLLoadUtil.load(item.getFxmlPath());
                    Node node = loader.load();

                    switch (item.getFxmlPath()) {
                        case FXMLConstants.ANALYSIS_DETAIL_OVERVIEW:
                            analysisDetailOverviewController = loader.getController();
                            analysisDetailOverviewController.setAnalysisDetailLayoutController(this);
                            analysisDetailOverviewController.setParamMap(getParamMap());
                            analysisDetailOverviewController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_REPORT :
                            analysisDetailReportController = loader.getController();
                            analysisDetailReportController.setAnalysisDetailLayoutController(this);
                            analysisDetailReportController.setParamMap(getParamMap());
                            analysisDetailReportController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_OVERVIEW_GERMLINE:
                            analysisDetailOverviewGermlineController = loader.getController();
                            analysisDetailOverviewGermlineController.setAnalysisDetailLayoutController(this);
                            analysisDetailOverviewGermlineController.setParamMap(getParamMap());
                            analysisDetailOverviewGermlineController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_REPORT_GERMLINE :
                            analysisDetailReportGermlineController = loader.getController();
                            analysisDetailReportGermlineController.setAnalysisDetailLayoutController(this);
                            analysisDetailReportGermlineController.setParamMap(getParamMap());
                            analysisDetailReportGermlineController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_VARIANTS:
                            analysisDetailVariantsController = loader.getController();
                            analysisDetailVariantsController.setParamMap(getParamMap());
                            analysisDetailVariantsController.setMainController(this.mainController);
                            analysisDetailVariantsController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_TST_RNA_REPORT:
                            tstrnaReportController = loader.getController();
                            tstrnaReportController.setParamMap(getParamMap());
                            tstrnaReportController.setMainController(this.mainController);
                            tstrnaReportController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_OVERVIEW_TST_RNA:
                            tstrnaOverviewController = loader.getController();
                            tstrnaOverviewController.setAnalysisDetailLayoutController(this);
                            tstrnaOverviewController.setParamMap(getParamMap());
                            tstrnaOverviewController.show((Parent) node);
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
        }
    }

    /**
     * 지정탭이 선택시 마다 새로 갱신해야하는 부분이 있는 경우 실행함.
     * @param tab Tab
     */
    private void executeReloadByTab(Tab tab) {
        // 보고서 탭인 경우 reported variant list 갱신함.

        if(tab.getId().equals(AnalysisDetailTabMenuCode.TAB_OVERVIEW_SOMATIC.name())) {
            analysisDetailOverviewController.setDisplayItem();
        } else if (tab.getId().equals(AnalysisDetailTabMenuCode.TAB_OVERVIEW_GERMLINE.name())) {
            analysisDetailOverviewGermlineController.setDisplayItem();
        } else if(tab.getId().equals(AnalysisDetailTabMenuCode.TAB_REPORT_SOMATIC.name())) {
            logger.debug("report tab reported variant list reload...");
            analysisDetailReportController.setVariantsList();
        } else if(tab.getId().equals(AnalysisDetailTabMenuCode.TAB_REPORT_GERMLINE.name())) {
            logger.debug("germline report tab reported variant list reload...");
            analysisDetailReportGermlineController.setVariantsList();
        } else if(tab.getId().equals(AnalysisDetailTabMenuCode.TAB_REPORT_TST_RNA.name())) {
            tstrnaReportController.setVariantsList();
        } else if(tab.getId().equals(AnalysisDetailTabMenuCode.TAB_OVERVIEW_TST_RNA.name())) {
            tstrnaOverviewController.setDisplayItem();
        }
    }

    @FXML
    private void rawDataDownloadButton() {
        try {
            // Load the fxml bedFile and create a new stage for the popup dialog
            FXMLLoader loader = this.mainController.getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_RAW_DATA);
            BorderPane page = loader.load();
            AnalysisDetailRawDataController controller = loader.getController();
            controller.setParamMap(getParamMap());
            controller.setMainController(this.mainController);

            controller.show(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
