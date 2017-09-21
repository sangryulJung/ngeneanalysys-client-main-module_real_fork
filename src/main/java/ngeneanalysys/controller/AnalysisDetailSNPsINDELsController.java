package ngeneanalysys.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.*;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.service.ALAMUTService;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.IGVService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-08-28
 */
public class AnalysisDetailSNPsINDELsController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    /** IGV 연동 서비스 */
    private IGVService igvService;

    /** Alamut 연동 서비스 */
    private ALAMUTService alamutService;

    /** 현재 선택된 변이 정보 객체 */
    private AnalysisResultVariant selectedAnalysisResultVariant;

    /** 현재 선택된 Prediction Filter */
    private ACMGFilterCode selectedPredictionCodeFilter;

    /** ACMG Filter Box Area */
    @FXML
    private HBox filterList;

    /** 우측 상단 아이콘 출력 박스 */
    @FXML
    private HBox iconAreaHBox;

    /** 목록 정렬 컬럼 정보 출력 박스 */
    @FXML
    private HBox sortListBox;
    @FXML
    private GridPane mainGridPane;

    @FXML
    private TableView<AnalysisResultVariant> variantListTableView;
    /** Variant List Column > prediction */

    @FXML
    private Label filterTitle;

    /** Pathogenic Review > Pathogenic 박스 */
    @FXML
    public HBox pathogenicArea;

    @FXML
    private Button pathogenicNone;

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    /** 선택 변이 상세 정보 탭 영역 */
    @FXML
    private TabPane tabArea;

    /** Overview Tab */
    public Tab subTabOverview;
    /** Comments Tab */
    public Tab subTabMemo;
    /** Warnings Tab */
    public Tab subTabLowConfidence;

    /** 현재 선택된 변이 리스트 객체의 index */
    private int selectedVariantIndex;
    @FXML
    private VBox linkArea;								/** clinical > Link 박스 영역 */
    @FXML
    private Button overviewFoldButton;
    private AnalysisDetailSNPsINDELsOverviewController overviewController;
    private final double overviewFoldedHeight = 32;
    private final double overviewExpandedHeight = 304;

    Sample sample = null;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show SNPs-INDELs");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        sample = (Sample)paramMap.get("sample");
        /*if(sample.getAnalysisResultSummary() == null) {
            dummyDataCreated();
        }*/

        // igv service init
        igvService = IGVService.getInstance();
        igvService.setMainController(getMainController());

        // alamut service init
        alamutService = ALAMUTService.getInstance();
        alamutService.setMainController(getMainController());

        setTableViewColumn();

        // 목록 클릭 시 변이 상세정보 출력 이벤트 바인딩
        variantListTableView.setRowFactory(tv -> {
            TableRow<AnalysisResultVariant> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 1 && (!row.isEmpty())) {
                     showVariantDetail(variantListTableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });

        // 선택된 목록에서 엔터키 입력시 변이 상세정보 출력 이벤트 바인딩
        variantListTableView.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                showVariantDetail(variantListTableView.getSelectionModel().getSelectedItem());
            }
        });

        // 본 샘플의 FASTQC 결과가 "pass"가 아닌 경우 아이콘 출력함.
        String fastQC = "PASS";//sample.getQc();
        fastQC = "PASS";/*(StringUtils.isEmpty(fastQC) && sample.getAnalysisResultSummary() != null) ? sample.getAnalysisResultSummary().getQualityControl() : fastQC;*/
        fastQC = (!StringUtils.isEmpty(fastQC)) ? fastQC.toUpperCase() : "NONE";
        if(StringUtils.isEmpty(fastQC) || !"PASS".endsWith(fastQC.toUpperCase())) {
            ImageView imgView = new ImageView(resourceUtil.getImage("/layout/images/icon_warn_big.png"));
            iconAreaHBox.getChildren().add(imgView);
            iconAreaHBox.setMargin(imgView, new Insets(0, 5, 0, 0));
        }

        ImageView ruoImgView = new ImageView(resourceUtil.getImage("/layout/images/icon_ruo.png"));
        ruoImgView.setFitWidth(100);
        ruoImgView.setFitHeight(50);
        iconAreaHBox.getChildren().add(ruoImgView);


        // 목록 정렬 설정 트래킹
        variantListTableView.getSortOrder().addListener(new ListChangeListener<TableColumn<AnalysisResultVariant,?>>() {
            @Override
            public void onChanged(Change<? extends TableColumn<AnalysisResultVariant,?>> change) {
                while (change.next()) {
                    // 출력 박스 초기화
                    sortListBox.getChildren().removeAll(sortListBox.getChildren());

                    int size = variantListTableView.getSortOrder().size();
                    if(size > 0 ) {
                        int idx = 0;
                        for (TableColumn<AnalysisResultVariant, ?> column : variantListTableView.getSortOrder()) {
                            if(idx > 0) {
                                ImageView image = new ImageView(resourceUtil.getImage("/layout/images/icon-arrow_margin.png"));
                                sortListBox.getChildren().add(image);
                                sortListBox.setMargin(image, new Insets(0, 1, 0, 2));
                            }
                            sortListBox.getChildren().add(new Label(column.getText()));
                            idx++;
                        }
                    }
                }
            }
        });

        overviewFoldButton.setOnMouseClicked(event -> {
                if (overviewFoldButton.getStyleClass().get(0) == null){
                    return;
                } else if(overviewFoldButton.getStyleClass().get(0).equals("btn_fold")){
                    foldOverview();
                } else {
                    expandOverview();
                }
        });


        currentStage = new Stage();
        currentStage.setResizable(false);
        currentStage.initStyle(StageStyle.DECORATED);
        currentStage.initModality(Modality.APPLICATION_MODAL);
        currentStage.setTitle(CommonConstants.SYSTEM_NAME + " > New Analysis Request");
        // OS가 Window인 경우 아이콘 출력.
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            currentStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        currentStage.initOwner(getMainApp().getPrimaryStage());

        //탭생성
        tabArea.getTabs().removeAll(tabArea.getTabs());
        subTabOverview = new Tab(SNPsINDELsTabMenuCode.OVERVIEW.getMenuName());
        tabArea.getTabs().add(subTabOverview);
        subTabMemo = new Tab(SNPsINDELsTabMenuCode.MEMO.getMenuName());
        tabArea.getTabs().add(subTabMemo);
        subTabLowConfidence = new Tab(SNPsINDELsTabMenuCode.LOWCONFIDENCE.getMenuName());
        tabArea.getTabs().add(subTabLowConfidence);

        //필터 박스 출력
        if("GERMLINE".equals(sample.getAnalysisType())) {
            setFilterBox();
        } else {
            filterTitle.setText("Tier Filter");
            setTierFilterBox();
        }


        showVariantList(null, 0);

        // Scene Init
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.showAndWait();
    }

    public void setTierFilterBox() {
        AnalysisResultSummary summary = sample.getAnalysisResultSummary();

        VBox totalVariantBox = getFilterBox(ACMGFilterCode.TOTAL_VARIANT, summary.getAllVariantCount());
        filterList.getChildren().add(totalVariantBox);
        filterList.setMargin(totalVariantBox, new Insets(0, 0, 0, 5));

        // Tier I
        VBox predictionABox = getFilterBox(ACMGFilterCode.TIER_ONE, summary.getLevel1VariantCount());
        filterList.getChildren().add(predictionABox);
        filterList.setMargin(predictionABox, new Insets(0, 0, 0, 5));

        // Tier II
        VBox predictionBBox = getFilterBox(ACMGFilterCode.TIER_TWO, summary.getLevel2VariantCount());
        filterList.getChildren().add(predictionBBox);
        filterList.setMargin(predictionBBox, new Insets(0, 0, 0, 5));

        // Tier III
        VBox predictionCBox = getFilterBox(ACMGFilterCode.TIER_THREE, summary.getLevel3VariantCount());
        filterList.getChildren().add(predictionCBox);
        filterList.setMargin(predictionCBox, new Insets(0, 0, 0, 5));

        // Tier IV
        VBox predictionDBox = getFilterBox(ACMGFilterCode.TIER_FOUR, summary.getLevel4VariantCount());
        filterList.getChildren().add(predictionDBox);
        filterList.setMargin(predictionDBox, new Insets(0, 0, 0, 5));

    }

    /**
     * 필터 박스 설정
     */
    @SuppressWarnings("static-access")
    public void setFilterBox() {
        AnalysisResultSummary summary = sample.getAnalysisResultSummary();

        // total variant count
        VBox totalVariantBox = getFilterBox(ACMGFilterCode.TOTAL_VARIANT, summary.getAllVariantCount());
        filterList.getChildren().add(totalVariantBox);
        filterList.setMargin(totalVariantBox, new Insets(0, 0, 0, 5));

        // prediction A
        VBox predictionABox = getFilterBox(ACMGFilterCode.PREDICTION_A, summary.getLevel1VariantCount());
        filterList.getChildren().add(predictionABox);
        filterList.setMargin(predictionABox, new Insets(0, 0, 0, 5));

        // prediction B
        VBox predictionBBox = getFilterBox(ACMGFilterCode.PREDICTION_B, summary.getLevel2VariantCount());
        filterList.getChildren().add(predictionBBox);
        filterList.setMargin(predictionBBox, new Insets(0, 0, 0, 5));

        // prediction C
        VBox predictionCBox = getFilterBox(ACMGFilterCode.PREDICTION_C, summary.getLevel3VariantCount());
        filterList.getChildren().add(predictionCBox);
        filterList.setMargin(predictionCBox, new Insets(0, 0, 0, 5));

        // prediction D
        VBox predictionDBox = getFilterBox(ACMGFilterCode.PREDICTION_D, summary.getLevel4VariantCount());
        filterList.getChildren().add(predictionDBox);
        filterList.setMargin(predictionDBox, new Insets(0, 0, 0, 5));

        // prediction E
        VBox predictionEBox = getFilterBox(ACMGFilterCode.PREDICTION_E, summary.getLevel5VariantCount());
        filterList.getChildren().add(predictionEBox);
        filterList.setMargin(predictionEBox, new Insets(0, 0, 0, 5));

        // Low Confidence
        int lowConfidenceCount = summary.getWarningVariantCount();
        VBox lowConfidenceBox = getFilterBox(ACMGFilterCode.LOW_CONFIDENCE, lowConfidenceCount);
        filterList.getChildren().add(lowConfidenceBox);
        filterList.setMargin(lowConfidenceBox, new Insets(0, 0, 0, 5));
    }

    /**
     * 필터 박스 반환
     * @param acmgFilterCode
     * @param count
     * @return
     */
    public VBox getFilterBox(ACMGFilterCode acmgFilterCode, int count) {
        VBox box = new VBox();
        box.setId(acmgFilterCode.name());
        box.getStyleClass().add(acmgFilterCode.name());

        Label levelLabel = new Label(acmgFilterCode.getDetail());
        levelLabel.getStyleClass().add("level_" + acmgFilterCode.name());
        levelLabel.setTooltip(new Tooltip(acmgFilterCode.getDetail()));

        Label countLabel = new Label(String.valueOf(count));
        countLabel.getStyleClass().add("count_" + acmgFilterCode.name());
        if (acmgFilterCode.name().startsWith("PREDICTION_")) {
            Label alias = new Label(acmgFilterCode.getAlias());
            alias.getStyleClass().add("alias_ALL");
            alias.getStyleClass().add("alias_" + acmgFilterCode.name());
            HBox hbox = new HBox(alias, countLabel);
            box.getChildren().setAll(hbox, levelLabel);
        } else {
            box.getChildren().setAll(levelLabel, countLabel);
        }

        // 마우스 클릭 이벤트 바인딩
        box.setOnMouseClicked(event -> {
            showVariantList(acmgFilterCode, 0);
            setOnSelectedFilter(acmgFilterCode);
        });

        return box;
    }

    /**
     * 필터 선택 표시
     * @param predictionCode
     */
    public void setOnSelectedFilter(ACMGFilterCode predictionCode) {
        if(filterList.getChildren() != null && filterList.getChildren().size() > 0) {
            for (Node node : filterList.getChildren()) {
                VBox box = (VBox) node;
                // 선택 속성 클래스 삭제
                box.getStyleClass().remove("selected");
                if(box.getId().equals(predictionCode.name())) {
                    // 선택 속성 클래스 추가
                    box.getStyleClass().add("selected");
                }
            }
        }
    }

    /**
     * 변이 목록 화면 출력
     * @param acmgFilterCode
     */
    @SuppressWarnings("unchecked")
    public void showVariantList(ACMGFilterCode acmgFilterCode, int selectedIdx) {
        selectedPredictionCodeFilter = acmgFilterCode;

        /*Map<String,Object> paramMap = new HashMap<>();
        if(acmgFilterCode != null && acmgFilterCode != ACMGFilterCode.TOTAL_VARIANT) {
            if (acmgFilterCode == ACMGFilterCode.LOW_CONFIDENCE) {
                paramMap.put("warning", acmgFilterCode.getCode());
            } else {
                paramMap.put("prediction", acmgFilterCode.getCode());
            }
        }*/
        try {
            // API 서버 조회
            HttpClientResponse response = apiService.get("/analysisResults/"+ sample.getId()  + "/variants", null,
                    null, false);
            AnalysisResultVariantList analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(AnalysisResultVariantList.class);

            List<AnalysisResultVariant> list = analysisResultVariantList.getResult();
            //if(list == null || list.isEmpty()) list = dummyVariantList();
            ObservableList<AnalysisResultVariant> displayList = null;

            // 하단 탭 활성화 토글
            setDetailTabActivationToggle(true);

           if (list != null && list.size() > 0) {
                displayList = FXCollections.observableArrayList(list);
                logger.info(displayList.size() + "");
            }

            // 리스트 삽입
          if (variantListTableView.getItems() != null && variantListTableView.getItems().size() > 0) {
                variantListTableView.getItems().clear();
            }
            variantListTableView.setItems(displayList);

            int reportCount = 0;
            int falseCount = 0;

            // 화면 출력
            if (displayList != null && displayList.size() > 0) {
                // report & false variant 카운트 집계
                /*for (AnalysisResultVariant item : displayList) {
                    if (!StringUtils.isEmpty(item.getPathogenicReportYn())
                            && "Y".equals(item.getPathogenicReportYn())) {
                        reportCount++;
                    }
                    if (!StringUtils.isEmpty(item.getPathogenicFalseYn()) && "Y".equals(item.getPathogenicFalseYn())) {
                        falseCount++;
                    }
                }*/
                variantListTableView.getSelectionModel().select(selectedIdx);
                showVariantDetail(displayList.get(selectedIdx));
            } else {
                setDetailTabActivationToggle(false);
            }

        } catch (WebAPIException wae) {
            variantListTableView.setItems(null);
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            variantListTableView.setItems(null);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    /**
     * 선택된 변이 상세 정보 출력
     * @param analysisResultVariant
     */
    @SuppressWarnings("unchecked")
    public void showVariantDetail(AnalysisResultVariant analysisResultVariant) {
        expandOverview();
        // 선택된 변이의 목록에서의 인덱스 정보 설정.
        selectedVariantIndex = variantListTableView.getItems().indexOf(analysisResultVariant);
        // 선택된 변이 객체 정보 설정
        selectedAnalysisResultVariant = analysisResultVariant;
        // 탭 메뉴 활성화 토글
        setDetailTabActivationToggle(false);
        try {
            // Detail 데이터 API 요청
            /*HttpClientResponse responseDetail = apiService.get(
                    "/analysis_result/variant_details/" + selectedAnalysisResultVariant.getId(), null, null, false);*/
            // 상세 데이터 요청이 정상 요청된 경우 진행.
           /* String data = dummyVariantDetail(selectedAnalysisResultVariant.getId());
            Map<String, Object> detailMap = JsonUtil.fromJsonToMap(data);
            if (detailMap != null && !detailMap.isEmpty() && detailMap.size() > 0) {
                // 하단 Overview 탭 설정.
                if (subTabOverview != null){
                    showOverviewTab(detailMap);
                }
            }*/

           if(analysisResultVariant != null) {
               if (subTabOverview != null){
                   showOverviewTab(analysisResultVariant);
               }
           }
        } /*catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        }*/
        catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

        try {
            // Memo 데이터 API 요청
            //Map<String, Object> commentParamMap = new HashMap<>();
            HttpClientResponse responseMemo = apiService.get("/analysisResults/variantInterpretationLogs/" + selectedAnalysisResultVariant.getId() , null, null, false);

            // Flagging Comment 데이터 요청이 정상 요청된 경우 진행.
            VariantInterpretationLogsList memoList = responseMemo.getObjectBeforeConvertResponseToJSON(VariantInterpretationLogsList.class);

            if(memoList != null){
                //코드 값을 별칭으로 변경함.
                for(VariantInterpretationLogs memo : memoList.getResult()) {
                    /*if (memo.getCommentType().equals(PathogenicReviewFlagTypeCode.PATHOGENICITY.name())) {
                        memo.setPrevValue(PathogenicTypeCode.getAliasFromCode(memo.getPrevValue()));
                        memo.setValue(PathogenicTypeCode.getAliasFromCode(memo.getValue()));
                    }*/
                }
            }

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

        // warnings 탭 출력
        if(subTabLowConfidence != null) showLowConfidenceTab(selectedAnalysisResultVariant.getWarningReason());

        // 첫번째 탭 선택 처리
        tabArea.getSelectionModel().selectFirst();
        setDetailTabActivationToggle(true);
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

    }

    @FXML
    public void saveCSV() {

    }

    public void showOverviewTab(AnalysisResultVariant analysisResultVariant) {
        paramMap.put("variant", analysisResultVariant);

        try {

            HttpClientResponse response = apiService.get("/analysisResults/variantTranscripts/" + analysisResultVariant.getId(), null, null, false);
            List<VariantTranscript> variantTranscripts = (List<VariantTranscript>) response.getMultiObjectBeforeConvertResponseToJSON(VariantTranscript.class, false);
            paramMap.put("variantTranscripts", variantTranscripts);

            response = null;
            response = apiService.get("/analysisResults/variantStatistics/" + analysisResultVariant.getId(), null, null, false);
            VariantStatistics variantStatistics = response.getObjectBeforeConvertResponseToJSON(VariantStatistics.class);
            paramMap.put("variantStatistics", variantStatistics);

        } catch(WebAPIException e) {
            logger.info(e.getMessage());
        }


        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW);
            Node node = loader.load();
            overviewController = loader.getController();
            overviewController.setMainController(this.getMainController());
            overviewController.setAnalysisDetailSNPsINDELsController(this);
            overviewController.setParamMap(paramMap);
            overviewController.show((Parent) node);
            showLink();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Overview 탬 화면 출력
     * @param detailMap
     */
    @SuppressWarnings("unchecked")
    public void showOverviewTab(Map<String,Object> detailMap) {
        try {
            Map<String,Object> alleleMap = (detailMap.containsKey("allele")) ? (Map<String,Object>) detailMap.get("allele") : null;
            Map<String,Object> variantInformationMap = (detailMap.containsKey("variant_information")) ? (Map<String,Object>) detailMap.get("variant_information") : null;
            int sameVariantSampleCountInRun = (detailMap.containsKey("same_variant_sample_count_in_run")) ? (int) detailMap.get("same_variant_sample_count_in_run") : 0;
            int totalSampleCountInRun = (detailMap.containsKey("total_sample_count_in_run")) ? (int) detailMap.get("total_sample_count_in_run") : 0;
            int samePanelSameVariantSampleCountInUsergroup = (detailMap.containsKey("same_panel_same_variant_sample_count_in_usergroup")) ? (int) detailMap.get("same_panel_same_variant_sample_count_in_usergroup") : 0;
            int totalSamePanelSampleCountInUsergroup = (detailMap.containsKey("total_same_panel_sample_count_in_usergroup")) ? (int) detailMap.get("total_same_panel_sample_count_in_usergroup") : 0;
            int sameVariantSampleCountInUsergroup = (detailMap.containsKey("same_variant_sample_count_in_usergroup")) ? (int) detailMap.get("same_variant_sample_count_in_usergroup") : 0;
            int totalSampleCountInUsergroup = (detailMap.containsKey("total_sample_count_in_usergroup")) ? (int) detailMap.get("total_sample_count_in_usergroup") : 0;
            Map<String,Object> variantClassifierMap = (detailMap.containsKey("variant_classifier")) ? (Map<String,Object>) detailMap.get("variant_classifier") : null;
            Map<String,Object> clinicalMap = (detailMap.containsKey("clinical")) ? (Map<String,Object>) detailMap.get("clinical") : null;
            Map<String,Object> breastCancerInformationCoreMap = (detailMap.containsKey("breast_cancer_information_core")) ? (Map<String,Object>) detailMap.get("breast_cancer_information_core") : null;
            Map<String,Object> populationFrequencyMap = (detailMap.containsKey("population_frequency")) ? (Map<String,Object>) detailMap.get("population_frequency") : null;
            Map<String,Object> geneMap = (detailMap.containsKey("gene")) ? (Map<String,Object>) detailMap.get("gene") : null;
            Map<String,Object> inSilicoPredictionMap = (detailMap.containsKey("in_silico_prediction")) ? (Map<String,Object>) detailMap.get("in_silico_prediction") : null;
            Map<String,Object> enigmaMap = (detailMap.containsKey("ENIGMA")) ? (Map<String,Object>) detailMap.get("ENIGMA") : null;
            Map<String,Object> buildMap = (detailMap.containsKey("build")) ? (Map<String,Object>) detailMap.get("build") : null;
            Map<String,Object> genomicCoordinateMap = (detailMap.containsKey("genomic_coordinate")) ? (Map<String,Object>) detailMap.get("genomic_coordinate") : null;

            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("sample", sample);
            paramMap.put("analysisResultVariant", selectedAnalysisResultVariant);
            paramMap.put("allele", alleleMap);
            paramMap.put("variantInformation", variantInformationMap);
            paramMap.put("variantClassifier", variantClassifierMap);
            paramMap.put("clinical", clinicalMap);
            paramMap.put("breastCancerInformationCore", breastCancerInformationCoreMap);
            paramMap.put("populationFrequency", populationFrequencyMap);
            paramMap.put("gene", geneMap);
            paramMap.put("inSilicoPrediction", inSilicoPredictionMap);
            paramMap.put("sameVariantSampleCountInRun", sameVariantSampleCountInRun);
            paramMap.put("totalSampleCountInRun", totalSampleCountInRun);
            paramMap.put("samePanelSameVariantSampleCountInUsergroup", samePanelSameVariantSampleCountInUsergroup);
            paramMap.put("totalSamePanelSampleCountInUsergroup", totalSamePanelSampleCountInUsergroup);
            paramMap.put("sameVariantSampleCountInUsergroup", sameVariantSampleCountInUsergroup);
            paramMap.put("totalSampleCountInUsergroup", totalSampleCountInUsergroup);
            paramMap.put("enigma", enigmaMap);
            paramMap.put("build", buildMap);
            paramMap.put("genomicCoordinate", genomicCoordinateMap);

            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW);
            Node node = loader.load();
            overviewController = loader.getController();
            overviewController.setMainController(this.getMainController());
            overviewController.setAnalysisDetailSNPsINDELsController(this);
            overviewController.setParamMap(paramMap);
            overviewController.show((Parent) node);
            showLink();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Memo 탭 화면 출력
     */
    public void showMemoTab(ObservableList<VariantInterpretationLogs> memoList) {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_MEMO);
            Node node = loader.load();
            AnalysisDetailSNPsINDELsMemoController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setAnalysisDetailSNPsINDELsController(this);
            controller.show((Parent) node);
            controller.displayList(memoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Low Confidence 탭 화면 출력
     * @param warningReasonJsonStr
     */
    public void showLowConfidenceTab(String warningReasonJsonStr) {
        try {
            Map<String,Object> map = null;
            if(!StringUtils.isEmpty(warningReasonJsonStr)) {
                map = JsonUtil.fromJsonToMap(warningReasonJsonStr.replace("'", "\""));
            }

            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_LOW_CONFIDENCE);
            Node node = loader.load();
            AnalysisDetailSNPsINDELsLowConfidenceController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setAnalysisDetailSNPsINDELsController(this);
            controller.setParamMap(map);
            controller.show((Parent) node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void expandOverview() {
        mainGridPane.getRowConstraints().get(2).setPrefHeight(this.overviewExpandedHeight);
        tabArea.setPrefHeight(this.overviewExpandedHeight);
        overviewFoldButton.getStyleClass().clear();
        overviewFoldButton.getStyleClass().add("btn_fold");
    }
    private void foldOverview(){
        mainGridPane.getRowConstraints().get(2).setPrefHeight(this.overviewFoldedHeight);
        tabArea.setPrefHeight(this.overviewFoldedHeight);
        overviewFoldButton.getStyleClass().clear();
        overviewFoldButton.getStyleClass().add("btn_expand");
    }

    /**
     * IGV 실행 및 데이터 로드
     * @param sampleId
     * @param sampleName
     * @param variantId
     * @param gene
     * @param locus
     * @param genome
     */
    public void loadIGV(String sampleId, String sampleName, String variantId, String gene, String locus, String genome) throws Exception {
        igvService.load(sampleId, sampleName, variantId, gene, locus, genome);
    }

    /**
     * Alamut 연동
     * @param transcript
     * @param cDNA
     * @param sampleId
     * @param bamFileName
     */
    public void loadAlamut(String transcript, String cDNA, String sampleId, String bamFileName) {
        alamutService.call(transcript, cDNA, sampleId, bamFileName);
    }

    /**
     * Flagging 영역 비활성화 처리
     */
    public void disableFlagging(boolean disable) {
        pathogenicArea.setDisable(disable);
    }

    /**
     * 변이 상세 정보 새로고침
     */
    public void refreshVariantDetail() {
        showVariantList(selectedPredictionCodeFilter, selectedVariantIndex);
    }

    /*private void dummyDataCreated() {
        AnalysisResultSummary analysisResultSummary = new AnalysisResultSummary(sample.getId(),"warn",281,768,
                new BigDecimal("438"),2,5,0,0,0,0,0,5
                ,new BigDecimal(98.13662348939866),"pass",new BigDecimal(100.0),"pass",new BigDecimal(100),"pass",
                new BigDecimal(100.0),"pass");

        sample.setAnalysisResultSummary(analysisResultSummary);
    }*/

    @SuppressWarnings("unchecked")
    public void showLink() {
        Sample sample = (Sample) paramMap.get("sample");
        String analysisType = sample.getAnalysisType();
        FXMLLoader loader = null;
        Pane linkBox = null;

        try {
            // SOMATIC 인 경우
            if(analysisType.equals(ExperimentTypeCode.SOMATIC.getDescription())) {
                loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW_LINK_SOMATIC);
                linkBox = loader.load();
                linkArea.getChildren().removeAll(linkArea.getChildren());
                linkArea.getChildren().add(linkBox);
            }

            /*if(linkBox != null && kit.equals(ExperimentTypeCode.SOMATIC)) {
                Map<String,Object> variantInformationMap = (Map<String,Object>) paramMap.get("variantInformation");
                AnalysisResultVariant analysisResultVariant = (AnalysisResultVariant) paramMap.get("analysisResultVariant");

                //logger.info("init overview link button event binding..");
                String urlExAC = (variantInformationMap.containsKey("exac_url")) ? (String) variantInformationMap.get("exac_url") : null;
                String urlBRCAExchange = (variantInformationMap.containsKey("brca_exchange_url")) ? (String) variantInformationMap.get("brca_exchange_url") : null;
                String urlClinvar = (variantInformationMap.containsKey("clinvar_url")) ? (String) variantInformationMap.get("clinvar_url") : null;
                String urlNCBI = (variantInformationMap.containsKey("ncbi_url")) ? (String) variantInformationMap.get("ncbi_url") : null;
                String urlUCSC = (variantInformationMap.containsKey("ucsc_url")) ? (String) variantInformationMap.get("ucsc_url") : null;

                for(Node node : linkBox.getChildren()) {
                    if(node != null) {
                        String id = node.getId();
                        //logger.info(String.format("button id : %s", id));

                        // exACButton
                        if("exACButton".equals(id)) {
                            Button exACButton = (Button) node;
                            if(!StringUtils.isEmpty(urlExAC)) {
                                exACButton.setOnAction(event -> {
                                    logger.info(String.format("OPEN EXTERNAL LINK [%s][%s]", id, urlExAC));
                                    getMainApp().getHostServices().showDocument(urlExAC);
                                });
                                exACButton.setDisable(false);
                            }
                        }

                        // igvButton
                        if("igvButton".equals(id)) {
                            Button igvButton = (Button) node;

                            String sampleId = sample.getId().toString();
                            String variantId = analysisResultVariant.getVariantId();
                            String gene = analysisResultVariant.getGene();
                            String locus = String.format("%s:%,d-%,d", analysisResultVariant.getChromosome(), Integer.parseInt(analysisResultVariant.getGenomicPosition()), Integer.parseInt(analysisResultVariant.getGenomicEndPosition()));
                            String refGenome = analysisResultVariant.getReferenceGenome();
                            String humanGenomeVersion = (refGenome.contains("hg19")) ? "hg19" : "hg18";

                            igvButton.setOnAction(event -> {
                                try {
                                    loadIGV(sampleId, sample.getName(),	variantId, gene, locus, humanGenomeVersion);
                                } catch (WebAPIException wae) {
                                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                                            getMainApp().getPrimaryStage(), true);
                                } catch (Exception e) {
                                    DialogUtil.generalShow(AlertType.ERROR, "IGV launch fail", "IGV software doesn't launch.",
                                            getMainApp().getPrimaryStage(), true);
                                }
                            });

                            igvButton.setDisable(false);
                        }

                        // brcaExchangeButton
                        if("brcaExchangeButton".equals(id)) {
                            Button brcaExchangeButton = (Button) node;
                            if(!StringUtils.isEmpty(urlBRCAExchange)) {
                                brcaExchangeButton.setOnAction(event -> {
                                    logger.info(String.format("OPEN EXTERNAL LINK [%s][%s]", id, urlBRCAExchange));
                                    getMainApp().getHostServices().showDocument(urlBRCAExchange);
                                });
                                brcaExchangeButton.setDisable(false);
                            }
                        }

                        // clinvarButton
                        if("clinvarButton".equals(id)) {
                            Button clinvarButton = (Button) node;
                            if(!StringUtils.isEmpty(urlClinvar)) {
                                clinvarButton.setOnAction(event -> {
                                    logger.info(String.format("OPEN EXTERNAL LINK [%s][%s]", id, urlClinvar));
                                    getMainApp().getHostServices().showDocument(urlClinvar);
                                });
                                clinvarButton.setDisable(false);
                            }
                        }

                        // ncbiButton
                        if("ncbiButton".equals(id)) {
                            Button ncbiButton = (Button) node;
                            if(!StringUtils.isEmpty(urlNCBI)) {
                                ncbiButton.setOnAction(event -> {
                                    logger.info(String.format("OPEN EXTERNAL LINK [%s][%s]", id, urlNCBI));
                                    getMainApp().getHostServices().showDocument(urlNCBI);
                                });
                                ncbiButton.setDisable(false);
                            }
                        }

                        // ucscButton
                        if("ucscButton".equals(id)) {
                            Button ucscButton = (Button) node;
                            if(!StringUtils.isEmpty(urlUCSC)) {
                                ucscButton.setOnAction(event -> {
                                    logger.info(String.format("OPEN EXTERNAL LINK [%s][%s]", id, urlUCSC));
                                    getMainApp().getHostServices().showDocument(urlUCSC);
                                });
                                ucscButton.setDisable(false);
                            }
                        }

                        // alamutButton
                        if("alamutButton".equals(id)) {
                            Button alamutButton = (Button) node;

                            // variant identification transcript data map
                            Map<String,Object> geneMap = (Map<String,Object>) paramMap.get("gene");
                            if(geneMap != null && !geneMap.isEmpty() && geneMap.containsKey("transcript")) {
                                Map<String,Map<String,String>> transcriptDataMap = (Map<String,Map<String,String>>) geneMap.get("transcript");
                                if(!transcriptDataMap.isEmpty() && transcriptDataMap.size() > 0) {
                                    alamutButton.setOnAction(event -> {
                                        int selectedIdx = this.overviewController.getTranscriptComboBoxSelectedIndex();
                                        logger.info(String.format("selected transcript combobox idx : %s", selectedIdx));
                                        Map<String,String> map = transcriptDataMap.get(String.valueOf(selectedIdx));
                                        if(!map.isEmpty() && map.size() > 0) {
                                            String transcript = (String) map.get("transcript_name");
                                            String cDNA = (String) map.get("hgvs.c");
                                            String sampleId = sample.getId().toString();
                                            String bamFileName = sample.getName() + "_final.bam";
                                            loadAlamut(transcript, cDNA, sampleId, bamFileName);
                                        }
                                    });
                                    alamutButton.setDisable(false);
                                }
                            }
                        }
                    }
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTableViewColumn() {
        if("somatic".equalsIgnoreCase(sample.getAnalysisType())) {
            TableColumn<AnalysisResultVariant, String> swTier = new TableColumn<>("Tier");
            swTier.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSwTier()));

            TableColumn<AnalysisResultVariant, String> expertTier = new TableColumn<>("Tier(User)");
            expertTier.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExpertTier()));

            variantListTableView.getColumns().addAll(swTier, expertTier);
        } else {
            TableColumn<AnalysisResultVariant, String> swPathogenicityLevel = new TableColumn<>("Pathogenic");
            swPathogenicityLevel.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSwPathogenicityLevel()));
            swPathogenicityLevel.setPrefWidth(55);
            swPathogenicityLevel.setCellFactory(param -> {
                    TableCell<AnalysisResultVariant,String> cell = new TableCell<AnalysisResultVariant, String>() {
                        @Override
                        public void updateItem(String item, boolean empty) {
                            Label label = null;
                            if(item != null) {
                                String code = PredictionTypeCode.getCodeFromAlias(item);
                                if(code != null && !"NONE".equals(code)) {
                                    label = new Label(item);
                                    label.getStyleClass().clear();
                                    swPathogenicityLevel.getStyleClass().add("alignment_center");
                                    label.getStyleClass().add("prediction_" + code);
                                }
                            }
                            setGraphic(label);
                        }
                    };
                    return cell;
            });

            TableColumn<AnalysisResultVariant, String> expertPathogenicityLevel = new TableColumn<>("Pathogenic(User)");
            expertPathogenicityLevel.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExpertPathogenicityLevel()));
            expertPathogenicityLevel.setPrefWidth(80);
            variantListTableView.getColumns().addAll(swPathogenicityLevel, expertPathogenicityLevel);
        }

        TableColumn<AnalysisResultVariant, Integer> warn = new TableColumn<>("Warn");
        warn.setCellValueFactory(cellData -> new SimpleObjectProperty<Integer>(cellData.getValue().getHasWarning()));

        TableColumn<AnalysisResultVariant, Integer> report = new TableColumn<>("Report");
        report.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSkipReport()).asObject());

        TableColumn<AnalysisResultVariant, String> type = new TableColumn<>("Type");
        type.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantExpression().getVariantType()));

        TableColumn<AnalysisResultVariant, String> codCons = new TableColumn<>("Cod.Cons");
        codCons.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantExpression().getCodingConsequence()));

        TableColumn<AnalysisResultVariant, String> gene = new TableColumn<>("Gene");
        gene.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getGene()));

        TableColumn<AnalysisResultVariant, String> strand = new TableColumn<>("Strand");
        strand.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getStrand()));

        TableColumn<AnalysisResultVariant, String> transcript = new TableColumn<>("Transcript");
        transcript.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantExpression().getTranscript()));

        TableColumn<AnalysisResultVariant, String> ntChange = new TableColumn<>("NT change");
        ntChange.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantExpression().getNtChange()));

        TableColumn<AnalysisResultVariant, String> aaChange = new TableColumn<>("AA change");
        aaChange.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantExpression().getAaChange()));

        TableColumn<AnalysisResultVariant, String> ntChangeBIC = new TableColumn<>("NT change(BIC)");
        ntChangeBIC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantExpression().getNtChangeBic()));

        TableColumn<AnalysisResultVariant, String> chr = new TableColumn<>("Chr");
        chr.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getChromosome()));

        TableColumn<AnalysisResultVariant, String> ref = new TableColumn<>("Ref");
        ref.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getRefSequence()));

        TableColumn<AnalysisResultVariant, String> alt = new TableColumn<>("Alt");
        alt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getAltSequence()));

        TableColumn<AnalysisResultVariant, String> zigosity = new TableColumn<>("Zigosity");
        zigosity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantExpression().getZygosity()));

        TableColumn<AnalysisResultVariant, String> exon = new TableColumn<>("Exon");
        exon.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getExonNum()));

        TableColumn<AnalysisResultVariant, String> exonBic = new TableColumn<>("Exon(BIC)");
        exonBic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getExonNumBic()));

        TableColumn<AnalysisResultVariant, Integer> refNum = new TableColumn<>("ref.num");
        refNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getReadInfo().getRefReadNum()).asObject());

        TableColumn<AnalysisResultVariant, Integer> altNum = new TableColumn<>("alt.num");
        altNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getReadInfo().getAltReadNum()).asObject());

        TableColumn<AnalysisResultVariant, Integer> depth = new TableColumn<>("depth");
        depth.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getReadInfo().getReadDepth()).asObject());

        TableColumn<AnalysisResultVariant, String> fraction = new TableColumn<>("fraction");
        fraction.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getReadInfo().getAlleleFraction() != null ? String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f",cellData.getValue().getReadInfo().getAlleleFraction().toString(), ""))) : ""));

        TableColumn<AnalysisResultVariant, String> thousandGenomics = new TableColumn<>("1KG");
        thousandGenomics.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPopulationFrequency().getG1000() != null ? String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f",cellData.getValue().getPopulationFrequency().getG1000().toString(), ""))) : ""));

        TableColumn<AnalysisResultVariant, String> exac = new TableColumn<>("ExAC");
        exac.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPopulationFrequency().getExac() != null ? String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f",cellData.getValue().getPopulationFrequency().getExac().toString(), ""))) : ""));

        TableColumn<AnalysisResultVariant, String> esp = new TableColumn<>("Esp6500");
        esp.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPopulationFrequency().getEsp6500() != null ? String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f",cellData.getValue().getPopulationFrequency().getEsp6500().toString(), ""))) : ""));

        TableColumn<AnalysisResultVariant, String> korean = new TableColumn<>("Korean");
        korean.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPopulationFrequency().getKorean() != null ? String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f",cellData.getValue().getPopulationFrequency().getKorean().toString(), ""))) : ""));

        TableColumn<AnalysisResultVariant, String> clinVarAcc = new TableColumn<>("ClinVar.Acc");
        clinVarAcc.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getClinVar().getClinVarAcc()));

        TableColumn<AnalysisResultVariant, String> clinVarClass = new TableColumn<>("ClinVar.Class");
        clinVarClass.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getClinVar().getClinVarClass()));

        TableColumn<AnalysisResultVariant, String> bicClass = new TableColumn<>("BIC.Class");
        clinVarClass.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBic().getBicClass()));

        TableColumn<AnalysisResultVariant, String> bicDesignation = new TableColumn<>("BIC.Designation");
        bicDesignation.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBic().getBicDesignation()));

        TableColumn<AnalysisResultVariant, String> bicNt = new TableColumn<>("BIC.NT");
        bicNt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBic().getBicNt()));

        TableColumn<AnalysisResultVariant, String> kohbraPatient = new TableColumn<>("KOHBRA.patient");
        kohbraPatient.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getKohbraPatient()));

        TableColumn<AnalysisResultVariant, String> kohbraFrequency = new TableColumn<>("KOHBRA.frequency");
        kohbraFrequency.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPopulationFrequency().getKohbraFreq() != null ? String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f",cellData.getValue().getPopulationFrequency().getKohbraFreq().toString(), ""))) : ""));

        TableColumn<AnalysisResultVariant, String> polyphen2 = new TableColumn<>("polyphen2");
        polyphen2.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getClinicalSignificant().getPolyphen2() != null ? String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f",cellData.getValue().getClinicalSignificant().getPolyphen2().toString(), ""))) : ""));

        TableColumn<AnalysisResultVariant, String> sift = new TableColumn<>("sift");
        sift.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getClinicalSignificant().getSift() != null ? String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f",cellData.getValue().getClinicalSignificant().getSift().toString(), ""))) : ""));

        TableColumn<AnalysisResultVariant, String> mutationTaster = new TableColumn<>("mutationtaster");
        mutationTaster.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getMutationTaster()));

        TableColumn<AnalysisResultVariant, Integer> variantNum = new TableColumn<>("variantNum");
        variantNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getVariantNum()).asObject());
        variantNum.setVisible(false);

        TableColumn<AnalysisResultVariant, String> refGenomeVer = new TableColumn<>("refGenomeVer");
        refGenomeVer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getRefGenomeVer()));
        refGenomeVer.setVisible(false);

        TableColumn<AnalysisResultVariant, String> leftSequence = new TableColumn<>("leftSequence");
        leftSequence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getLeftSequence()));
        leftSequence.setVisible(false);

        TableColumn<AnalysisResultVariant, String> rightSequence = new TableColumn<>("rightSequence");
        rightSequence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getRightSequence()));
        rightSequence.setVisible(false);

        TableColumn<AnalysisResultVariant, Integer> genomicCoordinate = new TableColumn<>("genomicCoordinate");
        genomicCoordinate.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSequenceInfo().getGenomicCoordinate()).asObject());
        genomicCoordinate.setVisible(false);

        TableColumn<AnalysisResultVariant, String> dbSnpRsId = new TableColumn<>("snpRsId");
        dbSnpRsId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPopulationFrequency().getDbsnpRsId()));
        dbSnpRsId.setVisible(false);

        TableColumn<AnalysisResultVariant, String> clinVarDisease = new TableColumn<>("clinVar.Disease");
        clinVarDisease.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getClinVar().getClinVarDisease()));
        clinVarDisease.setVisible(false);

        TableColumn<AnalysisResultVariant, String> bicCategory = new TableColumn<>("BIC.Category");
        bicCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBic().getBicCategory()));
        bicCategory.setVisible(false);

        TableColumn<AnalysisResultVariant, String> bicImportance = new TableColumn<>("BIC.Importance");
        bicImportance.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBic().getBicImportance()));
        bicImportance.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beClinVarUpdate = new TableColumn<>("Be.ClinVar.Update");
        beClinVarUpdate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeClinVarUpdate()));
        beClinVarUpdate.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beClinVarOrigin = new TableColumn<>("Be.ClinVar.Origin");
        beClinVarOrigin.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeClinVarOrigin()));
        beClinVarOrigin.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beClinVarMethod = new TableColumn<>("Be.ClinVar.Method");
        beClinVarMethod.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeClinVarMethod()));
        beClinVarMethod.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beBicCategory = new TableColumn<>("Be.BIC.Category");
        beBicCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeBicCategory()));
        beBicCategory.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beBicNationality = new TableColumn<>("Be.BIC.Nationality");
        beBicNationality.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeBicNationality()));
        beBicNationality.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beBicEthnic = new TableColumn<>("Be.BIC.Ethnic");
        beBicEthnic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeBicNationality()));
        beBicEthnic.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beBicPathogenicity = new TableColumn<>("Be.BIC.Pathogenicity");
        beBicPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeBicPathogenicity()));
        beBicPathogenicity.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beTranscript = new TableColumn<>("Be.Transcript");
        beTranscript.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeTranscript()));
        beTranscript.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beNt = new TableColumn<>("Be.NT");
        beNt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeNt()));
        beNt.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beGene = new TableColumn<>("Be.Gene");
        beGene.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeGene()));
        beGene.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beEnigmaCondition = new TableColumn<>("Be.Enigma.Condition");
        beEnigmaCondition.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeEnigmaCondition()));
        beEnigmaCondition.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beEnigmaUpdate = new TableColumn<>("Be.Enigma.Update");
        beEnigmaUpdate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeEnigmaUpdate()));
        beEnigmaUpdate.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beClinVarPathogenicity = new TableColumn<>("Be.ClinVar.Pathogenicity");
        beClinVarPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeClinVarPathogenicity()));
        beClinVarPathogenicity.setVisible(false);

        TableColumn<AnalysisResultVariant, String> beEnigmaPathogenicity = new TableColumn<>("Be.Enigma.Pathogenicity");
        beEnigmaPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeEnigmaPathogenicity()));
        beEnigmaPathogenicity.setVisible(false);

        TableColumn<AnalysisResultVariant, String> enigma = new TableColumn<>("enigma");
        enigma.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getBe().getBeEnigmaPathogenicity()));
        enigma.setVisible(false);


        variantListTableView.getColumns().addAll(warn, report, type, codCons, gene, strand, transcript, ntChange, aaChange, ntChangeBIC, chr, ref
            ,alt, zigosity, exon, exonBic, fraction ,refNum, altNum, depth, thousandGenomics, exac, esp, korean, clinVarAcc, clinVarClass, bicClass, bicDesignation
            ,kohbraPatient, kohbraFrequency, polyphen2, sift, mutationTaster, variantNum, refGenomeVer, leftSequence, rightSequence
            ,genomicCoordinate, dbSnpRsId, clinVarDisease, bicCategory, bicImportance, beClinVarUpdate, beClinVarOrigin, beClinVarMethod
            ,beBicCategory, beBicNationality, beBicEthnic, beBicPathogenicity, beTranscript, beNt, beGene, beEnigmaCondition, beEnigmaUpdate
            ,beClinVarPathogenicity, beEnigmaPathogenicity, enigma);

    }
}
