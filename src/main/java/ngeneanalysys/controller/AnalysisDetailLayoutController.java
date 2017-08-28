package ngeneanalysys.controller;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.AnalysisDetailTabMenuCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.render.AnalysisDetailTabItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.FXMLLoadUtil;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2017-08-25
 */
public class AnalysisDetailLayoutController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private HBox summaryBgHBox;

    @FXML
    private HBox summaryHBox;

    /** 상단 샘플 요약 정보 > 샘플 아이디 */
    @FXML
    private Label sampleIdLabel;

    /** 상단 샘플 요약 정보 > 사용 패널키트 */
    @FXML
    private Label kitLabel;

    /** 상단 샘플 요약 정보 > 실험방법 */
    @FXML
    private Label experimentLabel;

    /** 상단 샘플 요약 정보 > 샘플명 */
    @FXML
    private Label sampleNameLabel;

    /** 상단 샘플 요약 정보 > FASTQC 레벨 */
    @FXML
    private Label qcLabel;

    /** 상단 샘플 요약 정보 > FASTQC 레벨 아이콘 이미지 */
    @FXML
    private ImageView qcImageView;

    /** 상단 탭메뉴 영역 */
    @FXML
    private TabPane topTabPane;

    /** 현재 샘플의 고유 아아디 */
    private Integer sampleId;

    private AnalysisDetailOverviewController analysisDetailOverviewController;

    /** SNPs-INDELs Tab Controller */
    private AnalysisDetailSNPsINDELsController sNPsINDELsController;

    /** SNPs-INDELs Tab Controller */
    private AnalysisDetailVariantsController variantsController;

    /** API 서버 통신 서비스 */
    private APIService apiService;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        // 권한별 탭메뉴 추가
        int idx = 0;
        for (AnalysisDetailTabMenuCode code : AnalysisDetailTabMenuCode.values()) {
            AnalysisDetailTabItem item = (AnalysisDetailTabItem) code.getItem();
            Tab tab = new Tab();
            tab.setId(item.getNodeId());
            tab.setText(item.getTabName());
            tab.setClosable(false);
            // 첫번째 탭 컨텐츠 삽입.
            if (idx == 0)
                setTabContent(tab);
            topTabPane.getTabs().add(tab);
            idx++;
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

        this.mainController.getMainFrame().setCenter(root);
    }

    /**
     * 탭 컨텐츠 삽입
     * @param tab
     */
    public void setTabContent(Tab tab) {
        // 화면 내용이 없는 경우 셋팅.
        if(tab.getContent() == null) {
            logger.info(String.format("'%s' contents init..", tab.getId()));
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
                        case FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_LAYOUT:
                            sNPsINDELsController = loader.getController();
                            sNPsINDELsController.setAnalysisDetailLayoutController(this);
                            sNPsINDELsController.setParamMap(getParamMap());
                            sNPsINDELsController.show((Parent) node);
                            break;
                        case FXMLConstants.ANALYSIS_DETAIL_VARIANTS:
                            variantsController = loader.getController();
                            variantsController.setAnalysisDetailLayoutController(this);
                            variantsController.setParamMap(getParamMap());
                            variantsController.show((Parent) node);
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
     * @param tab
     */
    public void executeReloadByTab(Tab tab) {
        // 보고서 탭인 경우 reported variant list 갱신함.
        if(tab.getId().equals(AnalysisDetailTabMenuCode.TAB_REPORT.name())) {
            logger.info("report tab reported variant list reload...");
            //reportController.setVariantList();
        }
    }
}