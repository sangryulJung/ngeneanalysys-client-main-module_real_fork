package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.SNPsINDELsTabMenuCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-12-04
 */
public class AnalysisDetailFusionGeneController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    private AnalysisDetailFusionMainController analysisDetailFusionMainController;

    private AnalysisDetailSNPsINDELsOverviewController overviewController;

    @FXML
    private TableView<FusionGene> fusionGeneTableView;

    @FXML
    private TableColumn<FusionGene, String> nameTableColumn;

    @FXML
    private TableColumn<FusionGene, String> tierTableColumn;

    @FXML
    private TableColumn<FusionGene, String> userTierTableColumn;

    @FXML
    private TableColumn<FusionGene, String> reportTableColumn;

    @FXML
    private TableColumn<FusionGene, Integer> junctionReadCountTableColumn;

    @FXML
    private TableColumn<FusionGene, Integer> spanninigFragCountTableColumn;

    @FXML
    private TableColumn<FusionGene, String> leftBreakPointTableColumn;

    @FXML
    private TableColumn<FusionGene, String> rightBreakPointTableColumn;

    @FXML
    private TableColumn<FusionGene, String> leftTxExonTableColumn;

    @FXML
    private TableColumn<FusionGene, String> rightTxExonTableColumn;

    @FXML
    private TableColumn<FusionGene, String> protFusionTypeTableColumn;

    @FXML
    private TableColumn<FusionGene, String> spliceTypeTableColumn;

    @FXML
    private TableColumn<FusionGene, String> largeAnchorSupportTableColumn;

    @FXML
    private TableColumn<FusionGene, String> leftBreakDinucTableColumn;

    @FXML
    private TableColumn<FusionGene, String> rightBreakDinucTableColumn;

    @FXML
    private TableColumn<FusionGene, String> annotsTableColumn;

    @FXML
    private TableColumn<FusionGene, String> cdsLeftIdTableColumn;

    @FXML
    private TableColumn<FusionGene, String> cdsleftRangeTableColumn;

    @FXML
    private TableColumn<FusionGene, String> cdsRightIdTableColumn;

    @FXML
    private TableColumn<FusionGene, String> cdsRightRangeTableColumn;

    @FXML
    private TableColumn<FusionGene, String> fusionModelTableColumn;

    @FXML
    private TableColumn<FusionGene, String> fusionCdsTableColumn;

    @FXML
    private TableColumn<FusionGene, String> fusionTranslTableColumn;

    @FXML
    private TableColumn<FusionGene, String> pfamLeftTableColumn;

    @FXML
    private TableColumn<FusionGene, String> pfamRightTableColumn;

    @FXML
    private TabPane tabArea;

    /** 현재 샘플 정보 객체 */
    private Sample sample;

    /** 현재 선택된 변이 리스트 객체의 index */
    private int selectedVariantIndex;

    private FusionGene selectedFusionGene;

    /** Overview Tab */
    public Tab subTabOverview;
    /** Comments Tab */
    public Tab subTabMemo;

    /**
     * @param analysisDetailFusionMainController
     */
    public void setAnalysisDetailFusionMainController(AnalysisDetailFusionMainController analysisDetailFusionMainController) {
        this.analysisDetailFusionMainController = analysisDetailFusionMainController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show fusion gene tab");

        apiService = APIService.getInstance();

        sample = (Sample)paramMap.get("sample");

        nameTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getName()));
        tierTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(ConvertUtil.tierConvert(cellData.getValue().getSwTier())));
        userTierTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(ConvertUtil.tierConvert(cellData.getValue().getExpertTier())));
        reportTableColumn.getStyleClass().add("alignment_center");
        reportTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getIncludedInReport()));
        reportTableColumn.setCellFactory(param -> new TableCell<FusionGene, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                Label label = null;
                if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
                    label = new Label("R");
                    label.getStyleClass().remove("label");
                    label.getStyleClass().add("prediction_A");
                }
                setGraphic(label);
            }
        });

        junctionReadCountTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getJunctionReadCount()));
        spanninigFragCountTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSpanningFragCount()));
        leftBreakPointTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionLeft().getLeftBreakPoint()));
        rightBreakPointTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionRight().getRightBreakPoint()));
        leftTxExonTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionLeft().getLeftTxExon()));
        rightTxExonTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionRight().getRightTxExon()));
        protFusionTypeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getProtFusionType()));
        spliceTypeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getSpliceType()));
        largeAnchorSupportTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getLargeAnchorSupport()));
        leftBreakDinucTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionLeft().getLeftBreakDinuc()));
        rightBreakDinucTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionRight().getRightBreakDinuc()));
        annotsTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getAnnots()));
        cdsLeftIdTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionLeft().getCdsLeftId()));
        cdsleftRangeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionLeft().getCdsLeftRange()));
        cdsRightIdTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionRight().getCdsRightId()));
        cdsRightRangeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionRight().getCdsRightRange()));
        fusionModelTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionModel()));
        fusionCdsTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionCds()));
        fusionTranslTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionTransl()));
        pfamLeftTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionLeft().getPfamLeft()));
        pfamRightTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFusionRight().getPfamRight()));


        tabArea.getTabs().removeAll(tabArea.getTabs());
        subTabOverview = new Tab(SNPsINDELsTabMenuCode.OVERVIEW.getMenuName());
        tabArea.getTabs().add(subTabOverview);
        subTabMemo = new Tab(SNPsINDELsTabMenuCode.MEMO.getMenuName());
        tabArea.getTabs().add(subTabMemo);

        showVariantList(0);

        analysisDetailFusionMainController.subTabFusionGene.setContent(root);
    }

    public void showVariantList(int index) {
        HttpClientResponse response = null;
        try {
            response = apiService.get("analysisResults/sampleFusionGene/" + sample.getId(), null, null, false);

            PagedFusionGene fusionGene = response.getObjectBeforeConvertResponseToJSON(PagedFusionGene.class);
            List<FusionGene> list = fusionGene.getResult();
            ObservableList<FusionGene> displayList = null;

            if(list != null && !list.isEmpty()) {
                displayList = FXCollections.observableArrayList(list);
                logger.info(displayList.size() + "");
            }

            if (fusionGeneTableView.getItems() != null && !fusionGeneTableView.getItems().isEmpty()) {
                fusionGeneTableView.getItems().clear();
            }
            fusionGeneTableView.setItems(displayList);

            fusionGeneTableView.getSelectionModel().select(index);
            showVariantDetail(displayList.get(index));

        } catch (WebAPIException wae) {
            fusionGeneTableView.setItems(null);
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            fusionGeneTableView.setItems(null);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    /**
     * 선택된 변이 상세 정보 출력
     * @param fusionGene
     */
    @SuppressWarnings("unchecked")
    public void showVariantDetail(FusionGene fusionGene) {
        //expandOverview();
        // 선택된 변이의 목록에서의 인덱스 정보 설정.
        selectedVariantIndex = fusionGeneTableView.getItems().indexOf(fusionGene);
        // 선택된 변이 객체 정보 설정
        selectedFusionGene = fusionGene;
        // 탭 메뉴 활성화 토글
        setDetailTabActivationToggle(false);
        try {
            // Detail 데이터 API 요청
            HttpClientResponse responseDetail = apiService.get(
                    "/analysisResults/fusionGene/" + selectedFusionGene.getId(), null, null, false);
            // 상세 데이터 요청이 정상 요청된 경우 진행.
            FusionGene fusionGeneDetail
                    = responseDetail.getObjectBeforeConvertResponseToJSON(FusionGene.class);

            if(fusionGeneDetail != null) {
                if (subTabOverview != null){
                    showOverviewTab(fusionGeneDetail);
                }
            }
        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        }
        catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

        try {
            // Memo 데이터 API 요청
            //Map<String, Object> commentParamMap = new HashMap<>();
            HttpClientResponse responseMemo = apiService.get("/analysisResults/fusionGeneInterpretationLogs/" + selectedFusionGene.getId() , null, null, false);

            // Flagging Comment 데이터 요청이 정상 요청된 경우 진행.
            PagedFusionGeneInterpretationLogView memoList = responseMemo.getObjectBeforeConvertResponseToJSON(PagedFusionGeneInterpretationLogView.class);

            // 우측 Pathogenic Review 화면 설정
            // comment tab 화면 출력
            if (subTabMemo != null)
                showMemoTab(FXCollections.observableList(memoList.getResult()));
        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

        // 첫번째 탭 선택 처리
        tabArea.getSelectionModel().selectFirst();
        setDetailTabActivationToggle(true);
    }

    public void showOverviewTab(FusionGene fusionGene) {
        paramMap.put("fusionGene", fusionGene);

        /*try {
            HttpClientResponse response = apiService.get("/analysisResults/snpInDelTranscripts/" + fusionGene.getId(), null, null, false);
            List<SnpInDelTranscript> snpInDelTranscripts = (List<SnpInDelTranscript>) response.getMultiObjectBeforeConvertResponseToJSON(SnpInDelTranscript.class, false);
            paramMap.put("snpInDelTranscripts", snpInDelTranscripts);

            response = apiService.get("/analysisResults/snpInDelStatistics/" + fusionGene.getId(), null, null, false);
            VariantStatistics variantStatistics = response.getObjectBeforeConvertResponseToJSON(VariantStatistics.class);
            paramMap.put("variantStatistics", variantStatistics);

            response = apiService.get(
                    "/analysisResults/snpInDelExtraInfos/" + fusionGene.getId(), null, null, false);

            List<SnpInDelExtraInfo> item = (List<SnpInDelExtraInfo>)response.getMultiObjectBeforeConvertResponseToJSON(SnpInDelExtraInfo.class, false);
            paramMap.put("detail", item);

        } catch(WebAPIException e) {
            logger.info(e.getMessage());
        }*/

        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW);
            Node node = loader.load();
            overviewController = loader.getController();
            overviewController.setMainController(this.getMainController());
            overviewController.setParamMap(paramMap);
            overviewController.show((Parent) node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Memo 탭 화면 출력
     */
    public void showMemoTab(ObservableList<FusionGeneInterpretationLogView> memoList) {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_MEMO);
            Node node = loader.load();
            AnalysisDetailSNPsINDELsMemoController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.show((Parent) node);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 하단 탭 활성봐/비활성화 토글
     * @param flag
     */
    public void setDetailTabActivationToggle(boolean flag) {
        for(Tab tab : tabArea.getTabs()) {
            if(!flag) tab.setContent(null);
            tab.setDisable(!flag);
        }
    }

    @FXML
    public void saveExcel() {
        Map<String, Object> params = new HashMap<>();
        params.put("sampleId", sample.getId());
        WorksheetUtil worksheetUtil = new WorksheetUtil();
        worksheetUtil.exportVariantData("EXCEL", params, this.getMainApp());
    }

    @FXML
    public void saveCSV() {
        Map<String, Object> params = new HashMap<>();
        params.put("sampleId", sample.getId());
        WorksheetUtil worksheetUtil = new WorksheetUtil();
        worksheetUtil.exportVariantData("CSV", params, this.getMainApp());
    }
}
