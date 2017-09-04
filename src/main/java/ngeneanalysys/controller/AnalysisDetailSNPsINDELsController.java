package ngeneanalysys.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.ACMGFilterCode;
import ngeneanalysys.code.enums.SNPsINDELsTabMenuCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.model.AnalysisResultVariant;
import ngeneanalysys.model.Sample;
import ngeneanalysys.service.ALAMUTService;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.IGVService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.JsonUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    @FXML
    private Label filterTitle;

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

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    /** 선택 변이 상세 정보 탭 영역 */
    @FXML
    private TabPane tabArea;
    @FXML
    private CheckBox addToReportCheckBox;
    @FXML
    private CheckBox setToFalseCheckBox;

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

        // igv service init
        igvService = IGVService.getInstance();
        igvService.setMainController(getMainController());

        // alamut service init
        alamutService = ALAMUTService.getInstance();
        alamutService.setMainController(getMainController());


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
        VBox totalVariantBox = getFilterBox(ACMGFilterCode.TOTAL_VARIANT, 1);
        filterList.getChildren().add(totalVariantBox);
        filterList.setMargin(totalVariantBox, new Insets(0, 0, 0, 5));

        // prediction A
        VBox predictionABox = getFilterBox(ACMGFilterCode.TIER_ONE, 2);
        filterList.getChildren().add(predictionABox);
        filterList.setMargin(predictionABox, new Insets(0, 0, 0, 5));

        // prediction B
        VBox predictionBBox = getFilterBox(ACMGFilterCode.TIER_TWO, 3);
        filterList.getChildren().add(predictionBBox);
        filterList.setMargin(predictionBBox, new Insets(0, 0, 0, 5));

        // prediction C
        VBox predictionCBox = getFilterBox(ACMGFilterCode.TIER_THREE, 4);
        filterList.getChildren().add(predictionCBox);
        filterList.setMargin(predictionCBox, new Insets(0, 0, 0, 5));

        // prediction D
        VBox predictionDBox = getFilterBox(ACMGFilterCode.TIER_FOUR, 5);
        filterList.getChildren().add(predictionDBox);
        filterList.setMargin(predictionDBox, new Insets(0, 0, 0, 5));

    }

    /**
     * 필터 박스 설정
     */
    @SuppressWarnings("static-access")
    public void setFilterBox() {

        // total variant count
        VBox totalVariantBox = getFilterBox(ACMGFilterCode.TOTAL_VARIANT, 1);
        filterList.getChildren().add(totalVariantBox);
        filterList.setMargin(totalVariantBox, new Insets(0, 0, 0, 5));

        // prediction A
        VBox predictionABox = getFilterBox(ACMGFilterCode.PREDICTION_A, 2);
        filterList.getChildren().add(predictionABox);
        filterList.setMargin(predictionABox, new Insets(0, 0, 0, 5));

        // prediction B
        VBox predictionBBox = getFilterBox(ACMGFilterCode.PREDICTION_B, 3);
        filterList.getChildren().add(predictionBBox);
        filterList.setMargin(predictionBBox, new Insets(0, 0, 0, 5));

        // prediction C
        VBox predictionCBox = getFilterBox(ACMGFilterCode.PREDICTION_C, 4);
        filterList.getChildren().add(predictionCBox);
        filterList.setMargin(predictionCBox, new Insets(0, 0, 0, 5));

        // prediction D
        VBox predictionDBox = getFilterBox(ACMGFilterCode.PREDICTION_D, 5);
        filterList.getChildren().add(predictionDBox);
        filterList.setMargin(predictionDBox, new Insets(0, 0, 0, 5));

        // prediction E
        VBox predictionEBox = getFilterBox(ACMGFilterCode.PREDICTION_E, 6);
        filterList.getChildren().add(predictionEBox);
        filterList.setMargin(predictionEBox, new Insets(0, 0, 0, 5));

        // Low Confidence
        int lowConfidenceCount = 0;//Integer.parseInt(org.apache.commons.lang3.StringUtils.defaultIfEmpty(summary.getWarning(), "0"));
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
        //selectedPredictionCodeFilter = acmgFilterCode;

        Map<String,Object> paramMap = new HashMap<>();
        if(acmgFilterCode != null && acmgFilterCode != ACMGFilterCode.TOTAL_VARIANT) {
            if (acmgFilterCode == ACMGFilterCode.LOW_CONFIDENCE) {
                paramMap.put("warning", acmgFilterCode.getCode());
            } else {
                paramMap.put("prediction", acmgFilterCode.getCode());
            }
        }
        try {
            // API 서버 조회
            HttpClientResponse response = /*apiService.get("/analysis_result/variant_list/", paramMap,
                    null, false);*/null;

            //List<AnalysisResultVariant> list = (List<AnalysisResultVariant>) response
            //        .getMultiObjectBeforeConvertResponseToJSON(AnalysisResultVariant.class, false);
            ObservableList<AnalysisResultVariant> displayList = null;

            // 하단 탭 활성화 토글
            setDetailTabActivationToggle(true);

           /* if (list != null && list.size() > 0) {
                displayList = FXCollections.observableArrayList(list);
                logger.info(displayList.size() + "");
            }*/

            // 리스트 삽입
          /*  if (variantListTableView.getItems() != null && variantListTableView.getItems().size() > 0) {
                variantListTableView.getItems().clear();
            }
            variantListTableView.setItems(displayList);*/

            int reportCount = 0;
            int falseCount = 0;

            // 화면 출력
            if (/*displayList != null && displayList.size() > 0*/ true) {
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
                //variantListTableView.getSelectionModel().select(selectedIdx);
                showVariantDetail(/*displayList.get(selectedIdx)*/null);
            } else {
                setDetailTabActivationToggle(false);
            }

            //reportCountLabel.setText(String.valueOf(reportCount));
            //falseCountLabel.setText(String.valueOf(falseCount));
        } /*catch (WebAPIException wae) {
            //variantListTableView.setItems(null);
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        }*/ catch (Exception e) {
            //variantListTableView.setItems(null);
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
        /*selectedVariantIndex = variantListTableView.getItems().indexOf(analysisResultVariant);*/
        // 선택된 변이 객체 정보 설정
        /*selectedAnalysisResultVariant = analysisResultVariant;*/
        // 탭 메뉴 활성화 토글
        setDetailTabActivationToggle(false);
        try {
            // Detail 데이터 API 요청
            /*HttpClientResponse responseDetail = apiService.get(
                    "/analysis_result/variant_details/" + selectedAnalysisResultVariant.getId(), null, null, false);*/
            // 상세 데이터 요청이 정상 요청된 경우 진행.

            Map<String, Object> detailMap = /*JsonUtil.fromJsonToMap(responseDetail.getContentString());*/null;
            if (/*detailMap != null && !detailMap.isEmpty() && detailMap.size() > 0*/true) {
                // 하단 Overview 탭 설정.
                if (subTabOverview != null){
                    showOverviewTab(detailMap);
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
            // Comment 데이터 API 요청
            Map<String, Object> commentParamMap = new HashMap<String, Object>();
            //commentParamMap.put("variant", selectedAnalysisResultVariant.getId());
            //HttpClientResponse responseComment = apiService.get("/analysis_result/pathogenic_comment_list",
            //        commentParamMap, null, false);

            // Flagging Comment 데이터 요청이 정상 요청된 경우 진행.

            //ObservableList<AnalysisResultPathogenicComment> commentList = (ObservableList<AnalysisResultPathogenicComment>) responseComment
            //        .getMultiObjectBeforeConvertResponseToJSON(AnalysisResultPathogenicComment.class, true);
            /*if(commentList != null){
                //코드 값을 별칭으로 변경함.
                for(AnalysisResultPathogenicComment comment : commentList) {
                    if (comment.getCommentType().equals(PathogenicReviewFlagTypeCode.PATHOGENICITY.name())) {
                        comment.setPrevValue(PathogenicTypeCode.getAliasFromCode(comment.getPrevValue()));
                        comment.setValue(PathogenicTypeCode.getAliasFromCode(comment.getValue()));
                    }
                }
            }
            String reportYn = StringUtils.defaultIfEmpty(selectedAnalysisResultVariant.getPathogenicReportYn(), "N");
            String falseYn = StringUtils.defaultIfEmpty(selectedAnalysisResultVariant.getPathogenicFalseYn(), "N");

            // 우측 Pathogenic Review 화면 설정
            showPathogenicReview(selectedAnalysisResultVariant.getPrediction(),
                    selectedAnalysisResultVariant.getPathogenic(), reportYn, falseYn);*/
            // comment tab 화면 출력
            if (subTabMemo != null)
                showCommentTab();
        } /*catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } */catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

        // warnings 탭 출력
        if(subTabLowConfidence != null) showLowConfidenceTab(null);

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

    /**
     * Overview 탬 화면 출력
     * @param detailMap
     */
    @SuppressWarnings("unchecked")
    public void showOverviewTab(Map<String,Object> detailMap) {
        try {
            /*Map<String,Object> alleleMap = (detailMap.containsKey("allele")) ? (Map<String,Object>) detailMap.get("allele") : null;
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
            paramMap.put("genomicCoordinate", genomicCoordinateMap);*/

            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW);
            Node node = loader.load();
            overviewController = loader.getController();
            overviewController.setMainController(this.getMainController());
            overviewController.setAnalysisDetailSNPsINDELsController(this);
            overviewController.setParamMap(paramMap);
            overviewController.show((Parent) node);
            //showLink(paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Memo 탭 화면 출력
     */
    public void showCommentTab(/*ObservableList<AnalysisResultPathogenicComment> commentList*/) {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_MEMO);
            Node node = loader.load();
            AnalysisDetailSNPsINDELsMemoController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setAnalysisDetailSNPsINDELsController(this);
            controller.show((Parent) node);
            //controller.displayList(commentList);
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
}
