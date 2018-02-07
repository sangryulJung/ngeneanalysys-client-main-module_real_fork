package ngeneanalysys.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
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
import ngeneanalysys.model.paged.PagedVariantAndInterpretationEvidence;
import ngeneanalysys.model.render.SNPsINDELsList;
import ngeneanalysys.service.ALAMUTService;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.IGVService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
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
    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;

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
    private TableView<VariantAndInterpretationEvidence> variantListTableView;
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
    Panel panel = null;

    @FXML private Button tierOne;
    @FXML private Button tierTwo;
    @FXML private Button tierThree;
    @FXML private Button tierFour;

    @FXML private HBox swTierArea;
    @FXML private HBox userTierArea;

    @FXML private VBox tierChangeVBox;

    @FXML private CheckBox addToReportCheckBox;

    @FXML private Button pathogenic5;
    @FXML private Button pathogenic4;
    @FXML private Button pathogenic3;
    @FXML private Button pathogenic2;
    @FXML private Button pathogenic1;

    @FXML private HBox predictionArea;
    @FXML private HBox pathogenicityArea;

    @FXML private VBox pathogenicityVBox;

    @FXML private CheckBox addToGermlineReportCheckBox;

    private List<Label> countLabels = new ArrayList<>();


    //VariantList
    List<VariantAndInterpretationEvidence> list = null;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show SNPs-INDELs");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        sample = (Sample)paramMap.get("sample");
        panel = (Panel)paramMap.get("panel");
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
            TableRow<VariantAndInterpretationEvidence> row = new TableRow<>();
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
        String fastQC = sample.getQcResult();
        if(StringUtils.isEmpty(fastQC) || !"PASS".endsWith(fastQC.toUpperCase())) {
            ImageView imgView = new ImageView(resourceUtil.getImage("/layout/images/icon_warn_big.png"));
            iconAreaHBox.getChildren().add(imgView);
            iconAreaHBox.setMargin(imgView, new Insets(0, 5, 0, 0));
        }

        ImageView ruoImgView = new ImageView(resourceUtil.getImage("/layout/images/icon_ruo.png"));
        ruoImgView.setFitWidth(100);
        ruoImgView.setFitHeight(50);
        iconAreaHBox.getChildren().add(ruoImgView);

        addToReportCheckBox.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> addToReportBtn(addToReportCheckBox ));

        addToGermlineReportCheckBox.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> addToReportBtn(addToGermlineReportCheckBox));

        // 목록 정렬 설정 트래킹
        variantListTableView.getSortOrder().addListener(new ListChangeListener<TableColumn<VariantAndInterpretationEvidence,?>>() {
            @Override
            public void onChanged(Change<? extends TableColumn<VariantAndInterpretationEvidence,?>> change) {
                while (change.next()) {
                    // 출력 박스 초기화
                    sortListBox.getChildren().removeAll(sortListBox.getChildren());

                    int size = variantListTableView.getSortOrder().size();
                    if(size > 0 ) {
                        int idx = 0;
                        for (TableColumn<VariantAndInterpretationEvidence, ?> column : variantListTableView.getSortOrder()) {
                            if(idx > 0) {
                                ImageView image = new ImageView(resourceUtil.getImage("/layout/images/icon-arrow_margin.png"));
                                sortListBox.getChildren().add(image);
                                sortListBox.setMargin(image, new Insets(0, 1, 0, 2));
                            }
                            Label label = new Label(column.getText());
                            label.setStyle("-fx-font-size : 16; -fx-text-fill : #C30D23;");
                            sortListBox.getChildren().add(label);
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

        if("SOMATIC".equalsIgnoreCase(panel.getAnalysisType())) {
            tierChangeVBox.setVisible(true);
            pathogenicityVBox.setVisible(false);
        } else {
            tierChangeVBox.setVisible(false);
            pathogenicityVBox.setVisible(true);
        }

        currentStage = new Stage();
        currentStage.setResizable(false);
        currentStage.initStyle(StageStyle.DECORATED);
        currentStage.initModality(Modality.APPLICATION_MODAL);
        currentStage.setTitle(CommonConstants.SYSTEM_NAME + " > SNP-InDels");
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

        showVariantList(null, 0);

        //필터 박스 출력
        if(panel != null &&"GERMLINE".equals(panel.getAnalysisType())) {
            tierChangeVBox.setVisible(false);
            setFilterBox();
        } else {
            filterTitle.setText("Tier Filter");
            setTierFilterBox();
        }

        // Scene Init
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.showAndWait();
    }

    public void setTierFilterBox() {
        VBox totalVariantBox = getFilterBox(ACMGFilterCode.TOTAL_VARIANT, list.size());
        filterList.getChildren().add(totalVariantBox);
        filterList.setMargin(totalVariantBox, new Insets(0, 0, 0, 5));

        // Tier I
        //VBox predictionABox = getFilterBox(ACMGFilterCode.TIER_ONE, (count.get("T1") != null ? count.get("T1").intValue() : 0));
        VBox predictionABox = getFilterBox(ACMGFilterCode.TIER_ONE, returnTierVariantCount("T1"));
        filterList.getChildren().add(predictionABox);
        filterList.setMargin(predictionABox, new Insets(0, 0, 0, 5));

        // Tier II
        VBox predictionBBox = getFilterBox(ACMGFilterCode.TIER_TWO, returnTierVariantCount("T2"));
        filterList.getChildren().add(predictionBBox);
        filterList.setMargin(predictionBBox, new Insets(0, 0, 0, 5));

        // Tier III
        VBox predictionCBox = getFilterBox(ACMGFilterCode.TIER_THREE, returnTierVariantCount("T3"));
        filterList.getChildren().add(predictionCBox);
        filterList.setMargin(predictionCBox, new Insets(0, 0, 0, 5));

        // Tier IV
        VBox predictionDBox = getFilterBox(ACMGFilterCode.TIER_FOUR, returnTierVariantCount("T4"));
        filterList.getChildren().add(predictionDBox);
        filterList.setMargin(predictionDBox, new Insets(0, 0, 0, 5));

    }

    public int returnTierVariantCount(String tier) {
        return (int)list.stream().filter(item -> ((tier.equalsIgnoreCase(item.getSnpInDel().getExpertTier()) ||
                (StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && item.getSnpInDel().getSwTier().equalsIgnoreCase(tier)))))
                .count();
    }

    public int returnPathogenicityVariantCount(String pathogenicity) {
        return (int)list.stream().filter(item -> ((pathogenicity.equalsIgnoreCase(item.getSnpInDel().getExpertPathogenicity()) ||
                (StringUtils.isEmpty(item.getSnpInDel().getExpertPathogenicity()) && item.getSnpInDel().getSwPathogenicity().equalsIgnoreCase(pathogenicity)))))
                .count();
    }

    public void addToReportBtn(CheckBox checkBox) {
        if(selectedAnalysisResultVariant != null) {
            String oldSymbol = selectedAnalysisResultVariant.getSnpInDel().getIncludedInReport();
            if (checkBox.isSelected()) {
                try {
                    FXMLLoader loader = mainApp.load(FXMLConstants.EXCLUDE_REPORT);
                    Node node = loader.load();
                    ExcludeReportDialogController excludeReportDialogController = loader.getController();
                    excludeReportDialogController.setMainController(mainController);
                    excludeReportDialogController.settingItem("Y", selectedAnalysisResultVariant, checkBox);
                    excludeReportDialogController.show((Parent) node);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                if(!oldSymbol.equals(selectedAnalysisResultVariant.getSnpInDel().getIncludedInReport()))
                    showVariantList(null, selectedVariantIndex);
            } else {
                try {
                    FXMLLoader loader = mainApp.load(FXMLConstants.EXCLUDE_REPORT);
                    Node node = loader.load();
                    ExcludeReportDialogController excludeReportDialogController = loader.getController();
                    excludeReportDialogController.setMainController(mainController);
                    excludeReportDialogController.settingItem("N", selectedAnalysisResultVariant, checkBox);
                    excludeReportDialogController.show((Parent) node);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            if(!oldSymbol.equals(selectedAnalysisResultVariant.getSnpInDel().getIncludedInReport()))
                showVariantList(null, selectedVariantIndex);
        }
    }

    /**
     * 필터 박스 설정
     */
    @SuppressWarnings("static-access")
    public void setFilterBox() {
        AnalysisResultSummary summary = sample.getAnalysisResultSummary();

        // total variant count
        VBox totalVariantBox = getFilterBox(ACMGFilterCode.TOTAL_VARIANT, list.size());
        filterList.getChildren().add(totalVariantBox);
        filterList.setMargin(totalVariantBox, new Insets(0, 0, 0, 5));

        // prediction A
        VBox predictionABox = getFilterBox(ACMGFilterCode.PREDICTION_A, returnPathogenicityVariantCount("P"));
        filterList.getChildren().add(predictionABox);
        filterList.setMargin(predictionABox, new Insets(0, 0, 0, 5));

        // prediction B
        VBox predictionBBox = getFilterBox(ACMGFilterCode.PREDICTION_B, returnPathogenicityVariantCount("LP"));
        filterList.getChildren().add(predictionBBox);
        filterList.setMargin(predictionBBox, new Insets(0, 0, 0, 5));

        // prediction C
        VBox predictionCBox = getFilterBox(ACMGFilterCode.PREDICTION_C, returnPathogenicityVariantCount("US"));
        filterList.getChildren().add(predictionCBox);
        filterList.setMargin(predictionCBox, new Insets(0, 0, 0, 5));

        // prediction D
        VBox predictionDBox = getFilterBox(ACMGFilterCode.PREDICTION_D, returnPathogenicityVariantCount("LB"));
        filterList.getChildren().add(predictionDBox);
        filterList.setMargin(predictionDBox, new Insets(0, 0, 0, 5));

        // prediction E
        VBox predictionEBox = getFilterBox(ACMGFilterCode.PREDICTION_E, returnPathogenicityVariantCount("B"));
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
        if (acmgFilterCode.name().startsWith("PREDICTION_") || acmgFilterCode.name().startsWith("TIER_")) {
            Label alias = new Label(acmgFilterCode.getAlias());
            alias.getStyleClass().add("alias_ALL");
            alias.getStyleClass().add("alias_" + acmgFilterCode.name());
            HBox hbox = new HBox(alias, countLabel);
            box.getChildren().setAll(hbox, levelLabel);
        }  else {
            box.getChildren().setAll(levelLabel, countLabel);
        }
        countLabels.add(countLabel);
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
        if(filterList.getChildren() != null && !filterList.getChildren().isEmpty()) {
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
            HttpClientResponse response = apiService.get("/analysisResults/sampleSnpInDels/"+ sample.getId(), null,
                    null, false);
            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();
            this.list = list;

            ObservableList<VariantAndInterpretationEvidence> displayList = null;

            if(acmgFilterCode != null &&  acmgFilterCode.getCode() != null) {
                if (acmgFilterCode.getAlias() != null && panel != null && ExperimentTypeCode.GERMLINE.getDescription().equals(panel.getAnalysisType())) {
                    list = list.stream().filter(variant -> (!StringUtils.isEmpty(variant.getSnpInDel().getExpertPathogenicity()) && variant.getSnpInDel().getExpertPathogenicity().equals(acmgFilterCode.getAlias())) ||
                            (StringUtils.isEmpty(variant.getSnpInDel().getExpertPathogenicity()) && variant.getSnpInDel().getSwPathogenicity().equals(acmgFilterCode.getAlias()))).collect(Collectors.toList());
                } else if (acmgFilterCode.getAlias() != null && panel != null && ExperimentTypeCode.SOMATIC.getDescription().equals(panel.getAnalysisType())) {
                    list = list.stream().filter(variant ->
                            (!StringUtils.isEmpty(variant.getSnpInDel().getExpertTier()) && variant.getSnpInDel().getExpertTier().equals(acmgFilterCode.getAlias())) ||
                                    (StringUtils.isEmpty(variant.getSnpInDel().getExpertTier()) && variant.getSnpInDel().getSwTier().equals(acmgFilterCode.getAlias()))).collect(Collectors.toList());
                } else if (acmgFilterCode.getAlias() == null && panel != null && ExperimentTypeCode.GERMLINE.getDescription().equals(panel.getAnalysisType())) {
                    list = list.stream().filter(variant ->
                            variant.getSnpInDel().getHasWarning() != null).collect(Collectors.toList());
                }
            }

            // 하단 탭 활성화 토글
            setDetailTabActivationToggle(true);

            if (list != null && !list.isEmpty()) {
                displayList = FXCollections.observableArrayList(list);
                logger.info(displayList.size() + "");
            }

            // 리스트 삽입
            if (variantListTableView.getItems() != null && variantListTableView.getItems().size() > 0) {
                variantListTableView.getItems().clear();
            }
            variantListTableView.setItems(displayList);

            // 화면 출력
            if (displayList != null && displayList.size() > 0) {
                // report & false variant 카운트 집계
                /*for (SnpInDel item : displayList) {
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
    public void showVariantDetail(VariantAndInterpretationEvidence analysisResultVariant) {
        expandOverview();
        // 선택된 변이의 목록에서의 인덱스 정보 설정.
        selectedVariantIndex = variantListTableView.getItems().indexOf(analysisResultVariant);
        // 선택된 변이 객체 정보 설정
        selectedAnalysisResultVariant = analysisResultVariant;
        // 탭 메뉴 활성화 토글
        setDetailTabActivationToggle(false);
        try {
            // Detail 데이터 API 요청
            HttpClientResponse responseDetail = apiService.get(
                    "/analysisResults/snpInDels/" + selectedAnalysisResultVariant.getSnpInDel().getId(), null, null, false);
            // 상세 데이터 요청이 정상 요청된 경우 진행.
            SnpInDel snpInDel
                    = responseDetail.getObjectBeforeConvertResponseToJSON(SnpInDel.class);
            VariantAndInterpretationEvidence variantAndInterpretationEvidence = new VariantAndInterpretationEvidence();

            variantAndInterpretationEvidence.setSnpInDel(snpInDel);
            variantAndInterpretationEvidence.setInterpretationEvidence(selectedAnalysisResultVariant.getInterpretationEvidence());

            if(analysisResultVariant != null) {
                if (subTabOverview != null){
                    showOverviewTab(variantAndInterpretationEvidence);
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
            HttpClientResponse responseMemo = apiService.get("/analysisResults/snpInDelInterpretationLogs/" + selectedAnalysisResultVariant.getSnpInDel().getId() , null, null, false);

            // Flagging Comment 데이터 요청이 정상 요청된 경우 진행.
            SnpInDelInterpretationLogsList memoList = responseMemo.getObjectBeforeConvertResponseToJSON(SnpInDelInterpretationLogsList.class);

            if("SOMATIC".equalsIgnoreCase(panel.getAnalysisType())) {
                settingTierArea();
                checkBoxSetting(addToReportCheckBox, analysisResultVariant.getSnpInDel().getIncludedInReport());
            } else {
                settingGermlineArea();
                checkBoxSetting(addToGermlineReportCheckBox, analysisResultVariant.getSnpInDel().getIncludedInReport());
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
        if(subTabLowConfidence != null) showLowConfidenceTab(selectedAnalysisResultVariant.getSnpInDel().getWarningReason());

        // 첫번째 탭 선택 처리
        tabArea.getSelectionModel().selectFirst();
        setDetailTabActivationToggle(true);
    }

    public void checkBoxSetting(CheckBox checkBox, String Symbol) {
        if("Y".equals(Symbol)) {
            checkBox.setSelected(true);
        } else {
            checkBox.setSelected(false);
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

    public void showOverviewTab(VariantAndInterpretationEvidence analysisResultVariant) {
        paramMap.put("variant", analysisResultVariant);

        try {
            HttpClientResponse response = apiService.get("/analysisResults/snpInDelTranscripts/" + analysisResultVariant.getSnpInDel().getId(), null, null, false);
            List<SnpInDelTranscript> snpInDelTranscripts = (List<SnpInDelTranscript>) response.getMultiObjectBeforeConvertResponseToJSON(SnpInDelTranscript.class, false);
            paramMap.put("snpInDelTranscripts", snpInDelTranscripts);

            response = apiService.get("/analysisResults/snpInDelStatistics/" + analysisResultVariant.getSnpInDel().getId(), null, null, false);
            VariantStatistics variantStatistics = response.getObjectBeforeConvertResponseToJSON(VariantStatistics.class);
            paramMap.put("variantStatistics", variantStatistics);

            response = apiService.get(
                    "/analysisResults/snpInDelExtraInfos/" + analysisResultVariant.getSnpInDel().getId(), null, null, false);

            List<SnpInDelExtraInfo> item = (List<SnpInDelExtraInfo>)response.getMultiObjectBeforeConvertResponseToJSON(SnpInDelExtraInfo.class, false);
            paramMap.put("detail", item);

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
     * Memo 탭 화면 출력
     */
    public void showMemoTab(ObservableList<SnpInDelInterpretationLogs> memoList) {
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

    public Map<String, Object> returnResultsAfterSearch(String key) {
        List<SnpInDelExtraInfo> detail = (List<SnpInDelExtraInfo>)paramMap.get("detail");

        if(detail != null && !detail.isEmpty()) {
            Optional<SnpInDelExtraInfo> populationFrequency = detail.stream().filter(item
                    -> key.equalsIgnoreCase(item.key)).findFirst();

            if (populationFrequency.isPresent()) {
                return JsonUtil.fromJsonToMap(populationFrequency.get().value);
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public void showLink() {
        Sample sample = (Sample) paramMap.get("sample");
        String analysisType = (panel != null) ? panel.getAnalysisType() : "";
        FXMLLoader loader = null;
        ScrollPane somaticLinkBox = null;
        Pane linkBox = null;
        try {
            // SOMATIC 인 경우
            if(analysisType.equals(ExperimentTypeCode.SOMATIC.getDescription())) {
                loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW_LINK_SOMATIC);
                somaticLinkBox = loader.load();
                linkArea.getChildren().removeAll(linkArea.getChildren());
                linkArea.getChildren().add(somaticLinkBox);
            } else {
                loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW_LINK_BRCA);
                linkBox = loader.load();
                linkArea.getChildren().removeAll(linkArea.getChildren());
                linkArea.getChildren().add(linkBox);
            }

            if(somaticLinkBox != null && analysisType.equals(ExperimentTypeCode.SOMATIC.getDescription())) {
                Map<String,Object> variantInformationMap = returnResultsAfterSearch("variant_information");
                String rsId = (variantInformationMap.containsKey("rs_id")) ? (String) variantInformationMap.get("rs_id") : null;
                String exacFormat = (variantInformationMap.containsKey("exac_url")) ? (String) variantInformationMap.get("exac_format") : null;
                String geneId = (variantInformationMap.containsKey("geneid")) ? (String) variantInformationMap.get("geneid") : null;
                Integer start = (variantInformationMap.containsKey("start")) ? (Integer) variantInformationMap.get("start") : null;
                Integer end = (variantInformationMap.containsKey("stop")) ? (Integer) variantInformationMap.get("stop") : null;
                GridPane gridPane = (GridPane) somaticLinkBox.getContent();
                for(Node node : gridPane.getChildren()) {
                    if (node != null) {
                        String id = node.getId();
                        if ("igvButton".equals(id)) {
                            Button igvButton = (Button) node;

                            String sampleId = sample.getId().toString();
                            String variantId = selectedAnalysisResultVariant.getSnpInDel().getId().toString();
                            String gene = selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getGene();
                            String locus = String.format("%s:%,d-%,d",
                                    selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome(),
                                    selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getStartPosition(),
                                    selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getStartPosition());
                            String refGenome = selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getRefGenomeVer();
                            String humanGenomeVersion = (refGenome.contains("hg19")) ? "hg19" : "hg18";

                            igvButton.setOnAction(event -> {
                                try {
                                    loadIGV(sampleId, sample.getName(), variantId, gene, locus, humanGenomeVersion);
                                } catch (WebAPIException wae) {
                                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                                            getMainApp().getPrimaryStage(), true);
                                } catch (Exception e) {
                                    DialogUtil.generalShow(Alert.AlertType.ERROR, "IGV launch fail", "IGV software doesn't launch.",
                                            getMainApp().getPrimaryStage(), true);
                                }
                            });

                            igvButton.setDisable(false);
                        }

                        if ("dbSNPButton".equals(id)) {
                            Button dbSNPButton = (Button) node;

                            if(!StringUtils.isEmpty(geneId)) {
                                String fullUrlDBsnp = "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + rsId.replaceAll("rs", "");
                                dbSNPButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlDBsnp));
                                dbSNPButton.setDisable(false);
                            }
                        }

                        if ("clinvarButton".equals(id)) {
                            Button clinvarButton = (Button) node;
                            if(!StringUtils.isEmpty(rsId)) {
                                String fullUrlClinvar = "http://www.ncbi.nlm.nih.gov/clinvar?term=" + rsId;
                                clinvarButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlClinvar));
                                clinvarButton.setDisable(false);
                            }
                        }

                        if ("cosmicButton".equals(id)) {
                            Button cosmicButton = (Button) node;
                            if(!StringUtils.isEmpty(selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getCosmic().getCosmicIds())) {
                                String cosmicId = selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getCosmic().getCosmicIds().replaceAll("COSM", "");
                                if(cosmicId.contains("|")) {
                                    String[] ids = cosmicId.split("\\|");

                                    cosmicButton.setOnAction(event -> {
                                        boolean first = true;
                                        for(String cosmic : ids) {
                                            String fullUrlCOSMIC = "http://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=37&id=" + cosmic;
                                            getMainApp().getHostServices().showDocument(fullUrlCOSMIC);
                                            try {
                                                if(first) {
                                                    Thread.sleep(1200);
                                                    first = false;
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                } else {
                                    String fullUrlCOSMIC = "http://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=37&id=" + cosmicId;
                                    cosmicButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlCOSMIC));
                                }
                                cosmicButton.setDisable(false);
                            }
                        }

                        if ("ncbiButton".equals(id)) {
                            Button ncbiButton = (Button) node;
                            if(!StringUtils.isEmpty(geneId)) {
                                String fullUrlNCBI = "http://www.ncbi.nlm.nih.gov/gene/" + geneId;
                                ncbiButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlNCBI));
                                ncbiButton.setDisable(false);
                            }
                        }

                        if ("gnomesButton".equals(id)) {
                            Button gnomesButton = (Button) node;
                            if(!StringUtils.isEmpty(rsId)) {
                                String fullUrl1000G = "http://grch37.ensembl.org/Homo_sapiens/Variation/Population?db=core;v="
                                        + rsId + ";vdb=variation";
                                gnomesButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrl1000G));
                                gnomesButton.setDisable(false);
                            }
                        }

                        if ("exACButton".equals(id)) {
                            Button exACButton = (Button) node;
                            if(!StringUtils.isEmpty(exacFormat)) {
                                String fullUrlExAC = "http://exac.broadinstitute.org/variant/"
                                        + exacFormat;
                                exACButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlExAC));
                                exACButton.setDisable(false);
                            }
                        }

                        if ("gnomADButton".equals(id)) {
                            Button gnomADButton = (Button) node;
                            if(!StringUtils.isEmpty(exacFormat)) {
                                String fullUrlExAC = "http://gnomad.broadinstitute.org/variant/"
                                        + exacFormat;
                                gnomADButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlExAC));
                                gnomADButton.setDisable(false);
                            }
                        }

                        if ("koEXIDButton".equals(id)) {
                            Button koEXIDButton = (Button) node;
                            if(!StringUtils.isEmpty(rsId)) {
                                String fullUrlKoKEXID = "http://koex.snu.ac.kr/koex_main.php?section=search&db_code=15&keyword_class=varid&search_keyword="
                                        + rsId;
                                koEXIDButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlKoKEXID));
                                koEXIDButton.setDisable(false);
                            }
                        }

                        if ("oncoKBButton".equals(id)) {
                            Button oncoKBButton = (Button) node;
                            if(selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getOncoKB() != null &&
                                    !StringUtils.isEmpty(selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getOncoKB().getOncokbHgvsp())) {
                                String fullUrlOncoKB = "http://oncokb.org/#/gene/"
                                        + selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getGene()
                                        + "/variant/"
                                        + selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getOncoKB().getOncokbHgvsp();
                                oncoKBButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlOncoKB));
                                oncoKBButton.setDisable(false);
                            }
                        }

                        if ("ucscButton".equals(id)) {
                            Button ucscButton = (Button) node;
                            if(start != null && end != null) {
                                StringBuilder insertStart = new StringBuilder(start.toString());
                                StringBuilder insertEnd = new StringBuilder(end.toString());
                                int startLength = insertStart.length();
                                int endLength = insertEnd.length();
                                for(int i = 1; i < startLength; i++) {
                                    if(i % 3 == 0) insertStart.insert(startLength - i, ",");
                                }
                                for(int i = 1; i < endLength; i++) {
                                    if(i % 3 == 0) insertEnd.insert(endLength - i, ",");
                                }
                                Integer startMinus = start - 30;
                                Integer endPlus = end + 30;
                                String fullUrlUCSC = "http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&highlight=hg19.{"
                                        + selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome() + "%3A"
                                        + insertStart + "-"
                                        + insertEnd + "&position="
                                        + selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome() + "%3A"
                                        + startMinus +"-"
                                        + endPlus;

                                ucscButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlUCSC));
                                ucscButton.setDisable(false);
                            }
                        }
                    }
                }
            } else {
                Map<String,Object> variantInformationMap = returnResultsAfterSearch("variant_information");
                SnpInDel snpInDel = (SnpInDel) paramMap.get("snpInDel");
                logger.info("init overview link button event binding..");
                String urlExAC = (variantInformationMap.containsKey("exac_url")) ? (String) variantInformationMap.get("exac_url") : null;
                String urlBRCAExchange = (variantInformationMap.containsKey("brca_exchange_url")) ? (String) variantInformationMap.get("brca_exchange_url") : null;
                String urlClinvar = (variantInformationMap.containsKey("clinvar_url")) ? (String) variantInformationMap.get("clinvar_url") : null;
                String urlNCBI = (variantInformationMap.containsKey("ncbi_url")) ? (String) variantInformationMap.get("ncbi_url") : null;
                String urlUCSC = (variantInformationMap.containsKey("ucsc_url")) ? (String) variantInformationMap.get("ucsc_url") : null;
                for(Node node : linkBox.getChildren()) {
                    if (node != null) {
                        String id = node.getId();
                        logger.info(String.format("button id : %s", id));
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
                        if ("igvButton".equals(id)) {
                            Button igvButton = (Button) node;

                            String sampleId = sample.getId().toString();
                            String variantId = selectedAnalysisResultVariant.getSnpInDel().getId().toString();
                            String gene = selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getGene();
                            String locus = String.format("%s:%,d-%,d",
                                    selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome(),
                                    selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getStartPosition(),
                                    selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getStartPosition());
                            String refGenome = selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getRefGenomeVer();
                            String humanGenomeVersion = (refGenome.contains("hg19")) ? "hg19" : "hg18";

                            igvButton.setOnAction(event -> {
                                try {
                                    loadIGV(sampleId, sample.getName(), variantId, gene, locus, humanGenomeVersion);
                                } catch (WebAPIException wae) {
                                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                                            getMainApp().getPrimaryStage(), true);
                                } catch (Exception e) {
                                    DialogUtil.generalShow(Alert.AlertType.ERROR, "IGV launch fail", "IGV software doesn't launch.",
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
                            Map<String,Object> geneMap = returnResultsAfterSearch("gene");
                            if(geneMap != null && !geneMap.isEmpty() && geneMap.containsKey("transcript")) {
                                Map<String,Map<String,String>> transcriptDataMap = (Map<String,Map<String,String>>) geneMap.get("transcript");
                                if(!transcriptDataMap.isEmpty() && transcriptDataMap.size() > 0) {
                                    alamutButton.setOnAction(event -> {
                                        int selectedIdx = this.overviewController.getTranscriptComboBoxSelectedIndex();
                                        logger.info(String.format("selected transcript combobox idx : %s", selectedIdx));
                                        Map<String,String> map = transcriptDataMap.get(String.valueOf(selectedIdx));
                                        if(!map.isEmpty() && map.size() > 0) {
                                            String transcript = map.get("transcript_name");
                                            String cDNA = map.get("hgvs.c");
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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTableViewColumn() {
        if(panel != null && ExperimentTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> swTier = new TableColumn<>("Tier");
            swTier.setCellValueFactory(cellData -> new SimpleStringProperty(ConvertUtil.tierConvert(cellData.getValue().getSnpInDel().getSwTier())));

            TableColumn<VariantAndInterpretationEvidence, String> expertTier = new TableColumn<>("Tier(User)");
            expertTier.setCellValueFactory(cellData -> new SimpleStringProperty(ConvertUtil.tierConvert(cellData.getValue().getSnpInDel().getExpertTier())));

            variantListTableView.getColumns().addAll(swTier, expertTier);
        } else {
            TableColumn<VariantAndInterpretationEvidence, String> swPathogenicityLevel = new TableColumn<>("Prediction");
            swPathogenicityLevel.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSwPathogenicity()));
            swPathogenicityLevel.setPrefWidth(55);
            swPathogenicityLevel.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
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
                });

            TableColumn<VariantAndInterpretationEvidence, String> expertPathogenicityLevel = new TableColumn<>("Pathogenicity");
            expertPathogenicityLevel.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getExpertPathogenicity()));
            expertPathogenicityLevel.setPrefWidth(70);
            expertPathogenicityLevel.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    Label label = null;
                    if(item != null) {
                        String code = PredictionTypeCode.getCodeFromAlias(item);
                        if(code != null && !"NONE".equals(code)) {
                            label = new Label(item);
                            label.getStyleClass().clear();
                            expertPathogenicityLevel.getStyleClass().add("alignment_center");
                            label.getStyleClass().add("prediction_" + code);
                        }
                    }
                    setGraphic(label);
                }
            });
            variantListTableView.getColumns().addAll(swPathogenicityLevel, expertPathogenicityLevel);
        }

        TableColumn<VariantAndInterpretationEvidence, String> warn = new TableColumn<>("Warn");
        warn.getStyleClass().add("alignment_center");
        warn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getHasWarning()));
        warn.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        setGraphic((!StringUtils.isEmpty(item)) ? SNPsINDELsList.getWarningReasonPopOver(item) : null);
                    }
                });

        TableColumn<VariantAndInterpretationEvidence, String> report = new TableColumn<>("Report");
        report.getStyleClass().add("alignment_center");
        report.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getIncludedInReport()));
        report.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
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

        TableColumn<VariantAndInterpretationEvidence, String> type = new TableColumn<>("Type");
        type.getStyleClass().clear();
        type.setCellValueFactory(cellData -> new SimpleStringProperty(cutVariantTypeString(cellData.getValue().getSnpInDel().getSnpInDelExpression().getVariantType())));

        TableColumn<VariantAndInterpretationEvidence, String> codCons = new TableColumn<>("Cod.Cons");
        codCons.getStyleClass().clear();
        codCons.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getCodingConsequence()));

        TableColumn<VariantAndInterpretationEvidence, String> gene = new TableColumn<>("Gene");
        gene.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));

        TableColumn<VariantAndInterpretationEvidence, String> strand = new TableColumn<>("Strand");
        strand.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getStrand()));

        TableColumn<VariantAndInterpretationEvidence, String> transcript = new TableColumn<>("Transcript");
        transcript.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getTranscript()));

        TableColumn<VariantAndInterpretationEvidence, String> ntChange = new TableColumn<>("NT change");
        ntChange.setPrefWidth(90);
        ntChange.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));

        TableColumn<VariantAndInterpretationEvidence, String> aaChange = new TableColumn<>("AA change");
        aaChange.setPrefWidth(90);
        aaChange.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChange()));

        TableColumn<VariantAndInterpretationEvidence, String> aaChangeConversion = new TableColumn<>("AA change(Single)");
        aaChangeConversion.setPrefWidth(90);
        aaChangeConversion.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChangeConversion()));

        variantListTableView.getColumns().addAll(warn, report, type, codCons, gene, strand, transcript, ntChange, aaChange, aaChangeConversion);

        if(panel != null && ExperimentTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> ntChangeBIC = new TableColumn<>("NT change(BIC)");
            ntChangeBIC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getNtChangeBRCA()));
            variantListTableView.getColumns().addAll(ntChangeBIC);
        }

        TableColumn<VariantAndInterpretationEvidence, String> chr = new TableColumn<>("Chr");
        chr.setComparator((s1, s2) -> {
            String value1 = s1.replaceAll("chr", "");
            String value2 = s2.replace("chr", "");

            if(!value1.equalsIgnoreCase("x") && !value1.equalsIgnoreCase("y") &&
                    value1.length() == 1) {
                value1 = "0" + value1;
            }

            if(!value2.equalsIgnoreCase("x") && !value2.equalsIgnoreCase("y") &&
                    value2.length() == 1) {
                value2 = "0" + value2;
            }
            return value1.compareToIgnoreCase(value2);
        });
        chr.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getChromosome()));

        TableColumn<VariantAndInterpretationEvidence, String> ref = new TableColumn<>("Ref");
        ref.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getRefSequence()));

        TableColumn<VariantAndInterpretationEvidence, String> alt = new TableColumn<>("Alt");
        alt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getAltSequence()));

        TableColumn<VariantAndInterpretationEvidence, String> zigosity = new TableColumn<>("Zigosity");
        zigosity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getZygosity()));


        TableColumn<VariantAndInterpretationEvidence, String> exon = new TableColumn<>("Exon");
        exon.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getExonNum()));

        variantListTableView.getColumns().addAll(chr, ref, alt, zigosity, exon);

        if(panel != null && ExperimentTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> exonBic = new TableColumn<>("Exon(BIC)");
            exonBic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getExonNumBic()));
            variantListTableView.getColumns().addAll(exonBic);
        }

        TableColumn<VariantAndInterpretationEvidence, Integer> refNum = new TableColumn<>("Ref.num");
        refNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getReadInfo().getRefReadNum()).asObject());

        TableColumn<VariantAndInterpretationEvidence, Integer> altNum = new TableColumn<>("Alt.num");
        altNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getReadInfo().getAltReadNum()).asObject());

        TableColumn<VariantAndInterpretationEvidence, Integer> depth = new TableColumn<>("Depth");
        depth.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getReadInfo().getReadDepth()).asObject());

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> fraction = new TableColumn<>("Fraction");
        fraction.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> thousandGenomics = new TableColumn<>("1KG");
        thousandGenomics.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getAll())));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> exac = new TableColumn<>("ExAC");
        exac.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getExac())));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> esp = new TableColumn<>("Esp6500");
        esp.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getEsp6500().getAll())));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> korean = new TableColumn<>("Korean");
        korean.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getKoreanExomInformationDatabase())));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarAcc = new TableColumn<>("ClinVar.Acc");
        clinVarAcc.setPrefWidth(150);
        clinVarAcc.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarAcc()));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarClass = new TableColumn<>("ClinVar.Class");
        clinVarClass.setPrefWidth(150);
        clinVarClass.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarClass()));

        TableColumn<VariantAndInterpretationEvidence, String> cosmicIds = new TableColumn<>("COSMIC");
        cosmicIds.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCosmic().getCosmicIds()));

        TableColumn<VariantAndInterpretationEvidence, String> cosmicCount = new TableColumn<>("COSMIC COUNT");
        cosmicCount.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCosmic().getCosmicCount()));
        cosmicCount.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, String> cosmicOccurrence = new TableColumn<>("COSMIC OCCURRENCE");
        cosmicOccurrence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCosmic().getCosmicOccurrence()));
        cosmicOccurrence.setVisible(false);

        variantListTableView.getColumns().addAll(fraction ,refNum, altNum, depth, thousandGenomics, exac, esp, korean, clinVarAcc, clinVarClass, cosmicIds, cosmicCount, cosmicOccurrence);

        if(panel != null && ExperimentTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> bicClass = new TableColumn<>("BIC.Class");
            clinVarClass.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicClass()));

            TableColumn<VariantAndInterpretationEvidence, String> bicDesignation = new TableColumn<>("BIC.Designation");
            bicDesignation.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicDesignation()));

            TableColumn<VariantAndInterpretationEvidence, String> bicNt = new TableColumn<>("BIC.NT");
            bicNt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicNt()));

            variantListTableView.getColumns().addAll(bicClass, bicDesignation, bicNt);
        }

        TableColumn<VariantAndInterpretationEvidence, String> kohbraPatient = new TableColumn<>("KOHBRA.patient");
        kohbraPatient.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getKohbraPatient()));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> kohbraFrequency = new TableColumn<>("KOHBRA.frequency");
        kohbraFrequency.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getKohbraFreq())));

        /*TableColumn<SnpInDel, String> polyphen2 = new TableColumn<>("polyphen2");
        polyphen2.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getClinicalSignificant().getPolyphen2() != null ? String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f",cellData.getValue().getClinicalSignificant().getPolyphen2().toString(), ""))) : ""));
        TableColumn<SnpInDel, String> sift = new TableColumn<>("sift");
        sift.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getClinicalSignificant().getSift() != null ? String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f",cellData.getValue().getClinicalSignificant().getSift().toString(), ""))) : ""));
        TableColumn<SnpInDel, String> mutationTaster = new TableColumn<>("mutationtaster");
        mutationTaster.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClinicalSignificant().getMutationTaster()));*/

        TableColumn<VariantAndInterpretationEvidence, Integer> variantNum = new TableColumn<>("VariantNum");
        variantNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getVariantNum()).asObject());
        variantNum.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, String> refGenomeVer = new TableColumn<>("RefGenomeVer");
        refGenomeVer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getRefGenomeVer()));
        refGenomeVer.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, String> leftSequence = new TableColumn<>("LeftSequence");
        leftSequence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getLeftSequence()));
        leftSequence.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, String> rightSequence = new TableColumn<>("RightSequence");
        rightSequence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getRightSequence()));
        rightSequence.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, Integer> genomicCoordinate = new TableColumn<>("StartPosition");
        genomicCoordinate.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getStartPosition()).asObject());
        genomicCoordinate.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, String> dbSnpRsId = new TableColumn<>("SnpRsId");
        dbSnpRsId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getPopulationFrequency().getDbsnpRsId()));
        dbSnpRsId.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, String> clinVarDisease = new TableColumn<>("ClinVar.Disease");
        clinVarDisease.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarDisease()));
        clinVarDisease.setVisible(false);

        variantListTableView.getColumns().addAll(kohbraPatient, kohbraFrequency,/* polyphen2, sift, mutationTaster,*/ variantNum, refGenomeVer, leftSequence, rightSequence
                ,genomicCoordinate, dbSnpRsId, clinVarDisease);

        if(panel != null && ExperimentTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> bicCategory = new TableColumn<>("BIC.Category");
            bicCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicCategory()));
            bicCategory.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> bicImportance = new TableColumn<>("BIC.Importance");
            bicImportance.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicImportance()));
            bicImportance.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beClinVarUpdate = new TableColumn<>("Be.ClinVar.Update");
            beClinVarUpdate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarUpdate()));
            beClinVarUpdate.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beClinVarOrigin = new TableColumn<>("Be.ClinVar.Origin");
            beClinVarOrigin.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarOrigin()));
            beClinVarOrigin.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beClinVarMethod = new TableColumn<>("Be.ClinVar.Method");
            beClinVarMethod.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarMethod()));
            beClinVarMethod.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beBicCategory = new TableColumn<>("Be.BIC.Category");
            beBicCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicCategory()));
            beBicCategory.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beBicNationality = new TableColumn<>("Be.BIC.Nationality");
            beBicNationality.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicNationality()));
            beBicNationality.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beBicEthnic = new TableColumn<>("Be.BIC.Ethnic");
            beBicEthnic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicNationality()));
            beBicEthnic.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beBicPathogenicity = new TableColumn<>("Be.BIC.Pathogenicity");
            beBicPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicPathogenicity()));
            beBicPathogenicity.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beTranscript = new TableColumn<>("Be.Transcript");
            beTranscript.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeTranscript()));
            beTranscript.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beNt = new TableColumn<>("Be.NT");
            beNt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeNt()));
            beNt.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beGene = new TableColumn<>("Be.Gene");
            beGene.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeGene()));
            beGene.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beEnigmaCondition = new TableColumn<>("Be.Enigma.Condition");
            beEnigmaCondition.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaCondition()));
            beEnigmaCondition.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beEnigmaUpdate = new TableColumn<>("Be.Enigma.Update");
            beEnigmaUpdate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaUpdate()));
            beEnigmaUpdate.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beClinVarPathogenicity = new TableColumn<>("Be.ClinVar.Pathogenicity");
            beClinVarPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarPathogenicity()));
            beClinVarPathogenicity.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beEnigmaPathogenicity = new TableColumn<>("Be.Enigma.Pathogenicity");
            beEnigmaPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaPathogenicity()));
            beEnigmaPathogenicity.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> enigma = new TableColumn<>("Enigma");
            enigma.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaPathogenicity()));
            enigma.setVisible(false);

            variantListTableView.getColumns().addAll(bicCategory, bicImportance, beClinVarUpdate, beClinVarOrigin, beClinVarMethod
                    ,beBicCategory, beBicNationality, beBicEthnic, beBicPathogenicity, beTranscript, beNt, beGene, beEnigmaCondition, beEnigmaUpdate
                    ,beClinVarPathogenicity, beEnigmaPathogenicity, enigma);

        }

        variantListTableView.getStyleClass().clear();
        variantListTableView.getStyleClass().add("table-view");

        /*variantListTableView.getColumns().addAll(warn, report, type, codCons, gene, strand, transcript, ntChange, aaChange, ntChangeBIC, chr, ref
            ,alt, zigosity, exon, exonBic, fraction ,refNum, altNum, depth, thousandGenomics, exac, esp, korean, clinVarAcc, clinVarClass, bicClass, bicDesignation
            ,kohbraPatient, kohbraFrequency, polyphen2, sift, mutationTaster, variantNum, refGenomeVer, leftSequence, rightSequence
            ,genomicCoordinate, dbSnpRsId, clinVarDisease, bicCategory, bicImportance, beClinVarUpdate, beClinVarOrigin, beClinVarMethod
            ,beBicCategory, beBicNationality, beBicEthnic, beBicPathogenicity, beTranscript, beNt, beGene, beEnigmaCondition, beEnigmaUpdate
            ,beClinVarPathogenicity, beEnigmaPathogenicity, enigma);*/

    }

    public String cutVariantTypeString(String variantType) {
        if(variantType.contains(":")) return variantType.substring(0, variantType.indexOf(':'));
        return variantType;
    }

    public void settingTierArea() {
        if(selectedAnalysisResultVariant.getSnpInDel().getSwTier() != null) {
            for(Node node : swTierArea.getChildren()) {
                Label label = (Label) node;
                label.getStyleClass().removeAll(label.getStyleClass());
                if(label.getId().equals(selectedAnalysisResultVariant.getSnpInDel().getSwTier())) {

                    if(selectedAnalysisResultVariant.getSnpInDel().getSwTier().equals("T1")) {
                        label.getStyleClass().add("tier_one");
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getSwTier().equals("T2")) {
                        label.getStyleClass().add("tier_two");
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getSwTier().equals("T3")) {
                        label.getStyleClass().add("tier_three");
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getSwTier().equals("T4")) {
                        label.getStyleClass().add("tier_four");
                    }
                } else {
                    label.getStyleClass().add("no_selected_sw_tier");
                }
            }
        }
        String userTier = selectedAnalysisResultVariant.getSnpInDel().getExpertTier();
        userTier = ConvertUtil.convertButtonId(userTier);
        for(Node node : userTierArea.getChildren()) {
            Button button = (Button)node;
            button.getStyleClass().removeAll(button.getStyleClass());
            button.getStyleClass().add("button");
            if(!StringUtils.isEmpty(userTier)) {
                if(userTier.equals(button.getId())) {
                    if(selectedAnalysisResultVariant.getSnpInDel().getExpertTier().equals("T1")) {
                        button.getStyleClass().add("tier_one");
                        button.setDisable(true);
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getExpertTier().equals("T2")) {
                        button.getStyleClass().add("tier_two");
                        button.setDisable(true);
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getExpertTier().equals("T3")) {
                        button.getStyleClass().add("tier_three");
                        button.setDisable(true);
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getExpertTier().equals("T4")) {
                        button.getStyleClass().add("tier_four");
                        button.setDisable(true);
                    }
                } else {
                    button.getStyleClass().add("no_selected_user_tier");
                    button.setDisable(false);
                }
            } else {
                button.getStyleClass().add("no_selected_user_tier");
                button.setDisable(false);
            }

        }
    }

    public void settingGermlineArea() {
        if(selectedAnalysisResultVariant.getSnpInDel().getSwPathogenicity() != null) {
            for(Node node : predictionArea.getChildren()) {
                Label label = (Label) node;
                label.getStyleClass().removeAll(label.getStyleClass());
                if(label.getId().equals(selectedAnalysisResultVariant.getSnpInDel().getSwPathogenicity())) {
                    if(selectedAnalysisResultVariant.getSnpInDel().getSwPathogenicity().equals("P")) {
                        label.getStyleClass().add("prediction_A");
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getSwPathogenicity().equals("LP")) {
                        label.getStyleClass().add("prediction_B");
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getSwPathogenicity().equals("US")) {
                        label.getStyleClass().add("prediction_C");
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getSwPathogenicity().equals("LB")) {
                        label.getStyleClass().add("prediction_D");
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getSwPathogenicity().equals("B")) {
                        label.getStyleClass().add("prediction_E");
                    }
                } else {
                    label.getStyleClass().add("prediction_none");
                }
            }
        }
        String pathogenicity = selectedAnalysisResultVariant.getSnpInDel().getExpertPathogenicity();
        for(Node node : pathogenicityArea.getChildren()) {
            Button button = (Button)node;
            button.getStyleClass().removeAll(button.getStyleClass());
            button.getStyleClass().add("button");
            if(!StringUtils.isEmpty(pathogenicity)) {
                if(pathogenicity.equals(button.getText())) {
                    if(selectedAnalysisResultVariant.getSnpInDel().getExpertPathogenicity().equals("P")) {
                        button.getStyleClass().add("prediction_A_Selected");
                        button.setDisable(true);
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getExpertPathogenicity().equals("LP")) {
                        button.getStyleClass().add("prediction_B_Selected");
                        button.setDisable(true);
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getExpertPathogenicity().equals("US")) {
                        button.getStyleClass().add("prediction_C_Selected");
                        button.setDisable(true);
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getExpertPathogenicity().equals("LB")) {
                        button.getStyleClass().add("prediction_D_Selected");
                        button.setDisable(true);
                    } else if(selectedAnalysisResultVariant.getSnpInDel().getExpertPathogenicity().equals("B")) {
                        button.getStyleClass().add("prediction_E_Selected");
                        button.setDisable(true);
                    }
                } else {
                    button.getStyleClass().add("no_selected_user_tier");
                    button.setDisable(false);
                }
            } else {
                button.getStyleClass().add("no_selected_user_tier");
                button.setDisable(false);
            }

        }
    }

    @FXML
    public void setFlag(ActionEvent event) {
        Button actionButton = (Button) event.getSource();
        String value = null;
        if (actionButton == tierOne) {
            value = "T1";
        } else if (actionButton == tierTwo) {
            value = "T2";
        } else if (actionButton == tierThree) {
            value = "T3";
        } else if (actionButton == tierFour) {
            value = "T4";
        }

        if((selectedAnalysisResultVariant.getSnpInDel().getExpertTier() == null && !selectedAnalysisResultVariant.getSnpInDel().getSwTier().equals(value))
                    || (selectedAnalysisResultVariant.getSnpInDel().getExpertTier() != null &&!selectedAnalysisResultVariant.getSnpInDel().getExpertTier().equals(value))) {
            try {
                FXMLLoader loader = mainApp.load(FXMLConstants.CHANGE_TIER);
                Node root = loader.load();
                ChangeTierDialogController changeTierDialogController = loader.getController();
                changeTierDialogController.setMainController(this.getMainController());
                changeTierDialogController.settingTier(value, selectedAnalysisResultVariant);
                changeTierDialogController.show((Parent) root);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            //setTierVariantCount();
            showVariantList(null, 0);
        }
    }

    @FXML
    public void setGermlineFlag(ActionEvent event) {
        Button actionButton = (Button) event.getSource();
        String value = null;
        if (actionButton == pathogenic5) {
            value = "P";
        } else if (actionButton == pathogenic4) {
            value = "LP";
        } else if (actionButton == pathogenic3) {
            value = "US";
        } else if (actionButton == pathogenic2) {
            value = "LB";
        } else if (actionButton == pathogenic1) {
            value = "B";
        }

        if((selectedAnalysisResultVariant.getSnpInDel().getExpertPathogenicity() == null && !selectedAnalysisResultVariant.getSnpInDel().getSwPathogenicity().equals(value))
                || (selectedAnalysisResultVariant.getSnpInDel().getExpertPathogenicity() != null &&!selectedAnalysisResultVariant.getSnpInDel().getExpertPathogenicity().equals(value))) {
            try {
                FXMLLoader loader = mainApp.load(FXMLConstants.CHANGE_PATHOGENICITY);
                Node root = loader.load();
                ChangePathogenicityController changePathogenicityController = loader.getController();
                changePathogenicityController.setMainController(this.getMainController());
                changePathogenicityController.settingTier(value, selectedAnalysisResultVariant);
                changePathogenicityController.show((Parent) root);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            //setPathogenicityVariantCount();
            showVariantList(null, 0);
        }
    }

    public void setPathogenicityVariantCount() {
        if(!countLabels.isEmpty()) {
            /*countLabels.get(1).setText(String.valueOf(returnPathogenicityVariantCount("P")));
            countLabels.get(2).setText(String.valueOf(returnPathogenicityVariantCount("LP")));
            countLabels.get(3).setText(String.valueOf(returnPathogenicityVariantCount("US")));
            countLabels.get(4).setText(String.valueOf(returnPathogenicityVariantCount("LB")));
            countLabels.get(5).setText(String.valueOf(returnPathogenicityVariantCount("B")));*/
        }
    }

    public void setTierVariantCount() {
        if(!countLabels.isEmpty()) {
            /*countLabels.get(1).setText(String.valueOf(returnTierVariantCount("T1")));
            countLabels.get(2).setText(String.valueOf(returnPathogenicityVariantCount("T2")));
            countLabels.get(3).setText(String.valueOf(returnPathogenicityVariantCount("T3")));
            countLabels.get(4).setText(String.valueOf(returnPathogenicityVariantCount("T4")));*/
        }
    }
}