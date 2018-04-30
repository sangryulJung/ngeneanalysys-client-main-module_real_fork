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
import ngeneanalysys.code.enums.ExperimentTypeCode;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    private Sample sample;

    private Panel panel;

    private AnalysisDetailOverviewController analysisDetailOverviewController;

    /** target Tab Controller */
    private AnalysisDetailTargetController targetController;

    private AnalysisDetailReportController analysisDetailReportController;

    private AnalysisDetailOverviewGermlineController analysisDetailOverviewGermlineController;

    /** target Tab Controller */
    private AnalysisDetailTargetGermlineController analysisDetailTargetGermlineController;

    private AnalysisDetailReportGermlineController analysisDetailReportGermlineController;

    private AnalysisDetailSNPsINDELsController analysisDetailSNPsINDELsController;

    private AnalysisDetailVariantsController analysisDetailVariantsController;

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

            sample = response.getObjectBeforeConvertResponseToJSON(Sample.class);

            response = apiService.get("analysisResults/sampleSummary/" + sampleId , null, null, true);

            sample.setAnalysisResultSummary(response.getObjectBeforeConvertResponseToJSON(AnalysisResultSummary.class));

            getParamMap().put("sample", sample);

            response = apiService.get("runs/" + sample.getRunId() , null, null, true);
            RunWithSamples run = response.getObjectBeforeConvertResponseToJSON(RunWithSamples.class);

            setPaneAndDisease();

            runNameLabel.setText(run.getRun().getName());
            runNameTooltip.setText(run.getRun().getName());
            sequencerLabel.setText(WordUtils.capitalize(run.getRun().getSequencingPlatform()));
            sequencerTooltip.setText(WordUtils.capitalize(run.getRun().getSequencingPlatform()));

        } catch (WebAPIException e) {
            e.printStackTrace();
        }
        topTabPane.getTabs().clear();
        // 권한별 탭메뉴 추가
        int idx = 0;
        if(panel != null) {
            for (AnalysisDetailTabMenuCode code : AnalysisDetailTabMenuCode.values()) {
                AnalysisDetailTabItem item = code.getItem();

                if (item.getNodeId().contains(ExperimentTypeCode.GERMLINE.getDescription()) &&
                        (panel.getAnalysisType() != null && ExperimentTypeCode.GERMLINE.getDescription().equals(panel.getAnalysisType()))) {
                    addTab(item, idx);
                    idx++;
                } else if (!item.getNodeId().contains(ExperimentTypeCode.GERMLINE.getDescription()) &&
                        (panel.getAnalysisType() != null && ExperimentTypeCode.SOMATIC.getDescription().equals(panel.getAnalysisType()))) {
                    addTab(item, idx);
                    idx++;
                } else if (item.getNodeId().equalsIgnoreCase("TAB_VARIANTS")) {
                    addTab(item, idx);
                    idx++;
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

    public void setPaneAndDisease() {
        List<Panel> panels = (List<Panel>) mainController.getBasicInformationMap().get("panels");
        if(panels != null && !panels.isEmpty()) {
            Optional<Panel> optionalPanel = panels.stream().filter(panel -> panel.getId().equals(sample.getPanelId())).findFirst();
            optionalPanel.ifPresent(panel1 -> {
                this.panel = panel1;
                getParamMap().put("panel", panel);
                panelLabel.setText(panel1.getName());
                panelNameTooltip.setText(panel1.getName());
            });
        }

        sampleNameLabel.setText(sample.getName());
        sampleNameTooltip.setText(sample.getName());
        List<Diseases> diseases = (List<Diseases>) mainController.getBasicInformationMap().get("diseases");
        Optional<Diseases> diseasesOptional = diseases.stream().filter(disease -> Objects.equals(disease.getId(), sample.getDiseaseId())).findFirst();
        diseasesOptional.ifPresent(diseases1 -> {
            String diseaseName = diseases1.getName();
            diseaseLabel.setText(diseaseName);
            diseaseTooltip.setText(diseaseName);
        });
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
                        case FXMLConstants.ANALYSIS_DETAIL_TARGET:
                            targetController = loader.getController();
                            targetController.setAnalysisDetailLayoutController(this);
                            targetController.setParamMap(getParamMap());
                            targetController.show((Parent) node);
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
                        case FXMLConstants.ANALYSIS_DETAIL_TARGET_GERMLINE:
                            analysisDetailTargetGermlineController = loader.getController();
                            analysisDetailTargetGermlineController.setAnalysisDetailLayoutController(this);
                            analysisDetailTargetGermlineController.setParamMap(getParamMap());
                            analysisDetailTargetGermlineController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_REPORT_GERMLINE :
                            analysisDetailReportGermlineController = loader.getController();
                            analysisDetailReportGermlineController.setAnalysisDetailLayoutController(this);
                            analysisDetailReportGermlineController.setParamMap(getParamMap());
                            analysisDetailReportGermlineController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_LAYOUT:
                            analysisDetailSNPsINDELsController = loader.getController();
                            analysisDetailSNPsINDELsController.setParamMap(getParamMap());
                            analysisDetailSNPsINDELsController.setMainController(this.mainController);
                            analysisDetailSNPsINDELsController.show((Parent) node);
                        break;
                        case FXMLConstants.ANALYSIS_DETAIL_VARIANTS:
                            analysisDetailVariantsController = loader.getController();
                            analysisDetailVariantsController.setParamMap(getParamMap());
                            analysisDetailVariantsController.setMainController(this.mainController);
                            analysisDetailVariantsController.show((Parent) node);
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

        if(tab.getId().equals(AnalysisDetailTabMenuCode.TAB_OVERVIEW.name())) {
            analysisDetailOverviewController.setDisplayItem();
        } else if (tab.getId().equals(AnalysisDetailTabMenuCode.TAB_OVERVIEW_GERMLINE.name())) {
            analysisDetailOverviewGermlineController.setDisplayItem();
        } else if(tab.getId().equals(AnalysisDetailTabMenuCode.TAB_REPORT.name())) {
            logger.debug("report tab reported variant list reload...");
            analysisDetailReportController.setVariantsList();
        } else if(tab.getId().equals(AnalysisDetailTabMenuCode.TAB_REPORT_GERMLINE.name())) {
            logger.debug("germline report tab reported variant list reload...");
            analysisDetailReportGermlineController.setVariantsList();
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
