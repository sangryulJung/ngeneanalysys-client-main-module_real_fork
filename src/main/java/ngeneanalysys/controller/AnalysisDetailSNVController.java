package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.ACMGFilterCode;
import ngeneanalysys.code.enums.ExperimentTypeCode;
import ngeneanalysys.code.enums.PredictionTypeCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.controller.fragment.AnalysisDetailClinicalSignificantController;
import ngeneanalysys.controller.fragment.AnalysisDetailInterpretationController;
import ngeneanalysys.controller.fragment.AnalysisDetailVariantDetailController;
import ngeneanalysys.controller.fragment.AnalysisDetailVariantStatisticsController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedVariantAndInterpretationEvidence;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.model.render.LowConfidenceList;
import ngeneanalysys.model.render.SNPsINDELsList;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Jang
 * @since 2018-03-15
 */
public class AnalysisDetailSNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    @FXML
    private GridPane snvWrapper;

    @FXML
    private Button leftSizeButton;

    @FXML
    private Button rightSizeButton;

    @FXML
    private Accordion overviewAccordion;

    @FXML
    private TableView<VariantAndInterpretationEvidence> variantListTableView;

    @FXML
    private HBox rightContentsHBox;

    @FXML
    private VBox tableVBox;

    @FXML
    private VBox filterArea;

    @FXML
    private TitledPane variantDetailTitledPane;

    @FXML
    private TitledPane interpretationTitledPane;

    @FXML
    private TitledPane clinicalSignificantTitledPane;

    @FXML
    private TitledPane statisticsTitledPane;

    @FXML
    private TitledPane interpretationLogsTitledPane;

    @FXML
    private Pagination variantPagination;

    @FXML
    private ComboBox<ComboBoxItem> filterComboBox;

    @FXML
    private Button filterAddBtn;

    @FXML
    private Label viewAppliedFiltersLabel;

    @FXML
    private Label totalLabel;
    @FXML
    private Label searchCountLabel;

    private Sample sample = null;
    private Panel panel = null;
    private Map<String, String> sortMap = new HashMap<>();

    private AnalysisDetailVariantsController variantsController;
    //VariantList
    List<VariantAndInterpretationEvidence> list = null;

    /** 현재 선택된 변이 정보 객체 */
    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;
    /** 현재 선택된 변이 리스트 객체의 index */

    private final double rightStandardWidth = 890;
    private final double rightFullWidth = 1040;

    private final double centerStandardWidth = 890;
    private final double centerFullWidth = 1040;

    private final double minSize = 0;
    private final double standardAccordionSize = 870;
    private final double maxAccordionSize = 1020;

    private final double standardTableSize = 830;
    private final double maxTableSize = 980;

    private Integer currentPageIndex = -1;

    private AnalysisDetailInterpretationController interpretationController;

    private Map<String, List<Object>> filterList = new HashMap<>();

    /**
     * @return currentPageIndex
     */
    public Integer getCurrentPageIndex() {
        return currentPageIndex;
    }

    /**
     * @param variantsController AnalysisDetailVariantsController
     */
    void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    private void setAccordionContents() {

        try {
            // Memo 데이터 API 요청
            //Map<String, Object> commentParamMap = new HashMap<>();
            HttpClientResponse responseMemo = apiService.get("/analysisResults/snpInDels/" + selectedAnalysisResultVariant.getSnpInDel().getId()  + "/snpInDelInterpretationLogs", null, null, false);

            // Flagging Comment 데이터 요청이 정상 요청된 경우 진행.
            SnpInDelInterpretationLogsList memoList = responseMemo.getObjectBeforeConvertResponseToJSON(SnpInDelInterpretationLogsList.class);

            // comment tab 화면 출력
            if (interpretationLogsTitledPane.getContent() == null)
                showMemoTab(FXCollections.observableList(memoList.getResult()));
        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("init snv controller");

        apiService = APIService.getInstance();

        //filterAddBtn.setDisable(true);
        //viewAppliedFiltersLabel.setDisable(true);

        sample = (Sample)paramMap.get("sample");
        panel = (Panel)paramMap.get("panel");

        interpretationLogsTitledPane.setOnMouseClicked(ev -> setAccordionContents());

        leftSizeButton.setOnMouseClicked(event -> {
            if(leftSizeButton.getStyleClass().contains("btn_fold")){
                foldLeft();
            } else if (leftSizeButton.getStyleClass().get(0) != null){
                expandLeft();
            }
        });

        setDefaultFilter();
        viewAppliedFiltersLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, ev -> {
            ComboBoxItem comboBoxItem = filterComboBox.getSelectionModel().getSelectedItem();
            PopOverUtil.openFilterPopOver(viewAppliedFiltersLabel, filterList.get(comboBoxItem.getValue()));
        });

        rightSizeButton.setOnMouseClicked(event -> {
            if(rightSizeButton.getStyleClass().contains("right_btn_fold")){
                foldRight();
            } else if (rightSizeButton.getStyleClass().get(0) != null){
                expandRight();
            }
        });

        // 목록 클릭 시 변이 상세정보 출력 이벤트 바인딩
        variantListTableView.setRowFactory(tv -> {
            TableRow<VariantAndInterpretationEvidence> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if(e.getClickCount() == 1) {
                    setSNVTabName();
                } else if (e.getClickCount() <= 2) {
                    logger.debug(e.getClickCount() + " Click count");
                   if(e.getClickCount() == 2) {
                       expandRight();
                   }
                }
            });
            return row;
        });

        // 선택된 목록에서 엔터키 입력시 변이 상세정보 출력 이벤트 바인딩
        variantListTableView.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                expandRight();
            } else if(keyEvent.getCode().equals(KeyCode.UP) || keyEvent.getCode().equals(KeyCode.DOWN)) {
                setSNVTabName();
            }
        });


        setTableViewColumn();

        foldLeft();
        foldRight();

        variantsController.getDetailContents().setCenter(root);

        variantPagination.setPageFactory(pageIndex -> {
            if(!Objects.equals(currentPageIndex, pageIndex)) {
                showVariantList(pageIndex + 1, 0);
                currentPageIndex = pageIndex;
            }
            return new VBox();
        });

        filterComboBox.valueProperty().addListener((ob, ov, nv) -> {
            showVariantList(1 ,0);
            if(!nv.getValue().startsWith("C")) {
                viewAppliedFiltersLabel.setDisable(true);
            } else {
                viewAppliedFiltersLabel.setDisable(false);
            }
        });
    }

    private void setSNVTabName() {
        if(variantListTableView.getItems() != null) {
            VariantAndInterpretationEvidence variant = variantListTableView.getSelectionModel()
                    .getSelectedItem();
            String title;
            if (panel.getAnalysisType().equalsIgnoreCase("SOMATIC")) {
                title = variant.getSnpInDel().getGenomicCoordinate().getGene() + " "
                        + variant.getSnpInDel().getSnpInDelExpression().getCodingConsequence().split(";")[0] + " "
                        + variant.getSnpInDel().getSnpInDelExpression().getTranscript() + " "
                        + variant.getSnpInDel().getSnpInDelExpression().getNtChange() + " "
                        + variant.getSnpInDel().getSnpInDelExpression().getAaChangeConversion();
            } else {
                String[] ntChange = variant.getSnpInDel().getSnpInDelExpression().getNtChange().split(":");
                String[] aaChangeConversion = variant.getSnpInDel().getSnpInDelExpression().getAaChangeConversion().split(":");
                title = variant.getSnpInDel().getGenomicCoordinate().getGene() + " "
                        + variant.getSnpInDel().getSnpInDelExpression().getCodingConsequence().split(";")[0] + " "
                        + variant.getSnpInDel().getSnpInDelExpression().getTranscript() + " "
                        + ntChange[ntChange.length - 1] + " "
                        + aaChangeConversion[aaChangeConversion.length - 1];
            }
            variantsController.setSNVTabName(title);
        } else {
            variantsController.setSNVTabName(null);
        }
    }

    private void setStandardFilter(String sortName, String key, String value) {
        List<Object> list = new ArrayList<>();
        list.add(key + " " + value);
        filterList.put(sortName, list);
    }

    private void setDefaultFilter() {
        filterComboBox.setConverter(new ComboBoxConverter());
        filterComboBox.getItems().removeAll(filterComboBox.getItems());
        totalLabel.setText("Showing " + sample.getAnalysisResultSummary().getAllVariantCount());
        filterComboBox.getItems().add(new ComboBoxItem("All", "All"));
        if(panel.getAnalysisType().equalsIgnoreCase("SOMATIC")) {
            filterComboBox.getItems().add(new ComboBoxItem("Tier 1", "Tier I"));
            setStandardFilter("Tier 1", "tier", "T1");
            filterComboBox.getItems().add(new ComboBoxItem("Tier 2", "Tier II"));
            setStandardFilter("Tier 2", "tier", "T2");
            filterComboBox.getItems().add(new ComboBoxItem("Tier 3", "Tier III"));
            setStandardFilter("Tier 3", "tier", "T3");
            filterComboBox.getItems().add(new ComboBoxItem("Tier 4", "Tier IV"));
            setStandardFilter("Tier 4", "tier", "T4");
        } else if(panel.getAnalysisType().equalsIgnoreCase("GERMLINE")) {
            filterComboBox.getItems().add(new ComboBoxItem("Pathogenic", "Pathogenic"));
            setStandardFilter("Pathogenic", "pathogenicity", "P");
            filterComboBox.getItems().add(new ComboBoxItem("Likely Pathogenic", "Likely Pathogenic"));
            setStandardFilter("Likely Pathogenic", "pathogenicity", "LP");
            filterComboBox.getItems().add(new ComboBoxItem("Uncertain Significance", "Uncertain Significance"));
            setStandardFilter("Uncertain Significance", "pathogenicity", "US");
            filterComboBox.getItems().add(new ComboBoxItem("Likely Benign", "Likely Benign"));
            setStandardFilter("Likely Benign", "pathogenicity", "LB");
            filterComboBox.getItems().add(new ComboBoxItem("Benign", "Benign"));
            setStandardFilter("Benign", "pathogenicity", "B");
        }
        filterComboBox.getSelectionModel().select(0);
        viewAppliedFiltersLabel.setDisable(true);
    }

    private void expandLeft() {
        double leftExpandedWidth = 200;
        snvWrapper.getColumnConstraints().get(0).setPrefWidth(leftExpandedWidth);
        if(snvWrapper.getColumnConstraints().get(1).getPrefWidth() == 0) {
            snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightStandardWidth);
            rightContentsHBox.setPrefWidth(this.rightStandardWidth);
            if(interpretationController !=null) interpretationController.setGridPaneWidth(this.standardAccordionSize - 10);
            overviewAccordion.setPrefWidth(this.standardAccordionSize);
            tableVBox.setVisible(false);
        } else {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerStandardWidth);
            overviewAccordion.setVisible(false);
            tableVBox.setPrefWidth(this.centerStandardWidth);
            variantListTableView.setPrefWidth(this.standardTableSize);
        }
        filterArea.setVisible(true);
        filterArea.setPrefWidth(150);
        leftSizeButton.getStyleClass().clear();
        leftSizeButton.getStyleClass().add("btn_fold");
    }

    private void foldLeft(){
        double leftFoldedWidth = 50;
        snvWrapper.getColumnConstraints().get(0).setPrefWidth(leftFoldedWidth);
        rightContentsHBox.setPrefWidth(this.rightStandardWidth);
        if(snvWrapper.getColumnConstraints().get(1).getPrefWidth() == 0) {
            snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightFullWidth);
            overviewAccordion.setPrefWidth(this.maxAccordionSize);
            if(interpretationController !=null) interpretationController.setGridPaneWidth(this.maxAccordionSize - 10);
            overviewAccordion.setVisible(true);
            tableVBox.setVisible(false);
        } else {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerFullWidth);
            overviewAccordion.setVisible(false);
            tableVBox.setPrefWidth(this.centerFullWidth);
            variantListTableView.setPrefWidth(this.maxTableSize);
            tableVBox.setVisible(true);
        }
        filterArea.setVisible(false);
        filterArea.setPrefWidth(0);
        leftSizeButton.getStyleClass().clear();
        leftSizeButton.getStyleClass().add("btn_expand");
    }

    private void expandRight() {
        if(variantListTableView.getItems() != null && !variantListTableView.getItems().isEmpty()) {
            showVariantDetail(variantListTableView.getSelectionModel().getSelectedItem());
            double centerFoldedWidth = 0;
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(centerFoldedWidth);
            if (snvWrapper.getColumnConstraints().get(0).getPrefWidth() == 200) {
                snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightStandardWidth);
                overviewAccordion.setPrefWidth(this.standardAccordionSize);
                if (interpretationController != null)
                    interpretationController.setGridPaneWidth(this.standardAccordionSize - 10);
            } else {
                snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightFullWidth);
                overviewAccordion.setPrefWidth(this.maxAccordionSize);
                if (interpretationController != null)
                    interpretationController.setGridPaneWidth(this.maxAccordionSize - 10);
            }
            overviewAccordion.setVisible(true);
            tableVBox.setPrefWidth(this.minSize);
            tableVBox.setVisible(false);
            rightSizeButton.getStyleClass().clear();
            rightSizeButton.getStyleClass().add("right_btn_fold");
        }
    }

    private void foldRight(){
        if(currentPageIndex != -1) {
            showVariantList(currentPageIndex + 1, 0);
        } else {
            showVariantList(1, 0);
        }
        double rightFoldedWidth = 50;
        snvWrapper.getColumnConstraints().get(2).setPrefWidth(rightFoldedWidth);
        if(snvWrapper.getColumnConstraints().get(0).getPrefWidth() == 200) {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerStandardWidth);
            tableVBox.setPrefWidth(this.centerStandardWidth);
            variantListTableView.setPrefWidth(this.standardTableSize);
        } else {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerFullWidth);
            tableVBox.setPrefWidth(this.centerFullWidth);
            variantListTableView.setPrefWidth(this.maxTableSize);
        }
        rightContentsHBox.setPrefWidth(minSize);
        overviewAccordion.setVisible(false);
        tableVBox.setVisible(true);
        rightSizeButton.getStyleClass().clear();
        rightSizeButton.getStyleClass().add("right_btn_expand");
    }

    private void showVariantStatistics() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_VARIANT_STATISTICS);
            Node node = loader.load();
            AnalysisDetailVariantStatisticsController variantStatisticsController = loader.getController();
            variantStatisticsController.setMainController(this.getMainController());
            variantStatisticsController.setParamMap(paramMap);
            variantStatisticsController.show((Parent) node);
            statisticsTitledPane.setContent(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDetailTab() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_VARIANT_DETAIL);
            Node node = loader.load();
            AnalysisDetailVariantDetailController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.show((Parent) node);
            variantDetailTitledPane.setContent(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Interpretation 탭 화면 출력
     */
    private void showPredictionAndInterpretation() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_INTERPRETATION);
            Node node = loader.load();
            AnalysisDetailInterpretationController controller = loader.getController();
            interpretationController = controller;
            controller.setMainController(this.getMainController());
            controller.setAnalysisDetailSNVController(this);
            controller.setParamMap(getParamMap());
            controller.show((Parent) node);
            interpretationTitledPane.setContent(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Memo 탭 화면 출력
     */
    private void showMemoTab(ObservableList<SnpInDelInterpretationLogs> memoList) {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_INTERPRETATION_LOGS);
            Node node = loader.load();
            AnalysisDetailSNPsINDELsMemoController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setAnalysisDetailSNVController(this);
            controller.show((Parent) node);
            controller.displayList(memoList);
            interpretationLogsTitledPane.setContent(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 선택된 변이 상세 정보 출력
     * @param analysisResultVariant VariantAndInterpretationEvidence
     */
    @SuppressWarnings("unchecked")
    private void showVariantDetail(VariantAndInterpretationEvidence analysisResultVariant) {
        // 선택된 변이 객체 정보 설정
        selectedAnalysisResultVariant = analysisResultVariant;

        // 탭 메뉴 활성화 토글
        //setDetailTabActivationToggle(false);
        try {
            // Detail 데이터 API 요청
            HttpClientResponse responseDetail = apiService.get(
                    "/analysisResults/snpInDels/" + selectedAnalysisResultVariant.getSnpInDel().getId(), null, null, false);
            // 상세 데이터 요청이 정상 요청된 경우 진행.
            SnpInDel snpInDel
                    = responseDetail.getObjectBeforeConvertResponseToJSON(SnpInDel.class);
            VariantAndInterpretationEvidence variantAndInterpretationEvidence = new VariantAndInterpretationEvidence();

            variantAndInterpretationEvidence.setSnpInDel(snpInDel);
            variantAndInterpretationEvidence.setSnpInDelEvidences(selectedAnalysisResultVariant.getSnpInDelEvidences());

            paramMap.put("variant", analysisResultVariant);

            Platform.runLater(() -> {
                try {
                    HttpClientResponse response = apiService.get("/analysisResults/snpInDels/" + analysisResultVariant.getSnpInDel().getId() + "/snpInDelStatistics", null, null, false);
                    VariantStatistics variantStatistics = response.getObjectBeforeConvertResponseToJSON(VariantStatistics.class);
                    paramMap.put("variantStatistics", variantStatistics);

                    showVariantStatistics();
                } catch (WebAPIException wae) {
                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                            getMainApp().getPrimaryStage(), true);
                }
            });


            HttpClientResponse response = apiService.get("/analysisResults/snpInDels/" + analysisResultVariant.getSnpInDel().getId() + "/snpInDelTranscripts", null, null, false);
            List<SnpInDelTranscript> snpInDelTranscripts = (List<SnpInDelTranscript>) response.getMultiObjectBeforeConvertResponseToJSON(SnpInDelTranscript.class, false);
            paramMap.put("snpInDelTranscripts", snpInDelTranscripts);

            response = apiService.get(
                    "/analysisResults/snpInDels/" + analysisResultVariant.getSnpInDel().getId() + "/snpInDelExtraInfos", null, null, false);

            List<SnpInDelExtraInfo> item = (List<SnpInDelExtraInfo>)response.getMultiObjectBeforeConvertResponseToJSON(SnpInDelExtraInfo.class, false);
            paramMap.put("detail", item);

            showDetailTab();
            if(panel.getAnalysisType().equalsIgnoreCase(ExperimentTypeCode.SOMATIC.getDescription())) {
                showPredictionAndInterpretation();
                overviewAccordion.getPanes().remove(clinicalSignificantTitledPane);
            } else {
                overviewAccordion.getPanes().remove(interpretationTitledPane);
                showClinicalSignificant();
            }

        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        }
        catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

        // 첫번째 탭 선택 처리
        if(panel.getAnalysisType().equals("SOMATIC")) {
            overviewAccordion.setExpandedPane(interpretationTitledPane);
        } else {
            overviewAccordion.setExpandedPane(clinicalSignificantTitledPane);
        }
        //setDetailTabActivationToggle(true);
    }

    private void showClinicalSignificant() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_CLINICAL_SIGNIFICANT);
            Node node = loader.load();
            AnalysisDetailClinicalSignificantController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(getParamMap());
            controller.setSelectedAnalysisResultVariant(selectedAnalysisResultVariant);
            controller.setController(this);
            controller.show((Parent) node);
            clinicalSignificantTitledPane.setContent(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSortItem(Map<String, List<Object>> list) {
        Set<String> keySets = sortMap.keySet();
        List<Object> sortList = new ArrayList<>();
        for(String key : keySets) {
            sortList.add(key + " " + sortMap.get(key));
        }
        if(sortList.isEmpty()) {
            if(panel.getAnalysisType().equalsIgnoreCase("SOMATIC")) {
                sortList.add("swTier ASC");
            } else {
                sortList.add("swPathogenicity DESC");
            }
        }
        if(!sortList.isEmpty()) list.put("sort", sortList);
    }

    private void setFilterItem(Map<String, List<Object>> list) {
        ComboBoxItem comboBoxItem = filterComboBox.getSelectionModel().getSelectedItem();
        if(filterList.containsKey(comboBoxItem.getValue())) {
            list.put("search", filterList.get(comboBoxItem.getValue()));
        }
    }

    public void showVariantList(int pageIndex, int selectedIdx) {
        int totalCount;
        int limit = 100;
        int offset = (pageIndex - 1)  * limit;

        try {
            // API 서버 조회
            Map<String, Object> params = new HashMap<>();
            params.put("offset", offset);
            params.put("limit", limit);

            Map<String, List<Object>> sortItem = new HashMap<>();

            setSortItem(sortItem);
            setFilterItem(sortItem);

            HttpClientResponse response = apiService.get("/analysisResults/sampleSnpInDels/"+ sample.getId(), params,
                    null, sortItem);
            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();
            totalCount = analysisResultVariantList.getCount();
            this.list = list;

            searchCountLabel.setText("of " + totalCount + " variants");

            //totalVariantCountLabel.setText(sample.getAnalysisResultSummary().getAllVariantCount().toString());
            ObservableList<VariantAndInterpretationEvidence> displayList = null;

            response = apiService.get("/analysisResults/sampleSummary/"+ sample.getId(), null, null, false);

            sample.setAnalysisResultSummary(response.getObjectBeforeConvertResponseToJSON(AnalysisResultSummary.class));

            if (list != null && !list.isEmpty()) {
                displayList = FXCollections.observableArrayList(list);
            }

            // 리스트 삽입
            if (variantListTableView.getItems() != null && variantListTableView.getItems().size() > 0) {
                variantListTableView.getItems().clear();
            }
            variantListTableView.setItems(displayList);

            // 화면 출력
            if (displayList != null && displayList.size() > 0) {
                variantListTableView.getSelectionModel().select(selectedIdx);
                //showVariantDetail(displayList.get(selectedIdx));
            }

            int pageCount = 0;

            if(totalCount > 0) {
                pageCount = totalCount / limit;
                variantPagination.setCurrentPageIndex(pageIndex - 1);
                if(totalCount % limit > 0) {
                    pageCount++;
                }
            }

            logger.debug("total count : " + totalCount + ", page count : " + pageCount);

            if (pageCount > 0) {
                variantPagination.setVisible(true);
                variantPagination.setPageCount(pageCount);
            } else {
                variantPagination.setVisible(false);
            }
            setSNVTabName();
        } catch (WebAPIException wae) {
            variantListTableView.setItems(null);
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
            wae.printStackTrace();
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            variantListTableView.setItems(null);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

    }

    private void sortTable(String column) {
        if(sortMap.size() == 1 && sortMap.containsKey(column)) {
            if(sortMap.get(column).equalsIgnoreCase("ASC")) {
                sortMap.put(column, "DESC");
            } else {
                sortMap.remove(column);
            }
        } else if(sortMap.isEmpty()){
            sortMap.put(column, "ASC");
        } else if(sortMap.size() == 1) {
            sortMap.clear();
            sortMap.put(column, "ASC");
         }
        showVariantList(currentPageIndex + 1, 0);
    }

    @FXML
    public void excelDownload() {
        Map<String, Object> params = new HashMap<>();
        WorksheetUtil worksheetUtil = new WorksheetUtil();
        worksheetUtil.exportVariantData("EXCEL", params, this.getMainApp(), sample.getId());
    }

    @FXML
    public void csvDownload() {
        Map<String, Object> params = new HashMap<>();
        WorksheetUtil worksheetUtil = new WorksheetUtil();
        worksheetUtil.exportVariantData("CSV", params, this.getMainApp(), sample.getId());
    }

    private void createTableHeader(TableColumn<VariantAndInterpretationEvidence, ?> column, String name, String sortName, Double size) {
        Label label = new Label(name);
        column.setSortable(false);
        if(!StringUtils.isEmpty(sortName)) label.setOnMouseClicked(e -> sortTable(sortName));
        column.setGraphic(label);
        if(size != null) column.setPrefWidth(size);
        variantListTableView.getColumns().add(column);
    }

    @FXML
    public void showFilter() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_VARIANT_FILTER);
            Node node = loader.load();
            VariantFilterController variantFilterController = loader.getController();
            variantFilterController.setMainController(this.getMainController());
            if(filterComboBox.getSelectionModel().getSelectedItem().getValue().startsWith("C")) {
                variantFilterController.setCurrentFilerName(filterComboBox.getSelectionModel().getSelectedItem().getValue());
                variantFilterController.setCurrentFilter(filterList.get(filterComboBox.getSelectionModel().getSelectedItem().getValue()));
            }
            variantFilterController.setParamMap(paramMap);
            variantFilterController.setAnalysisType(panel.getAnalysisType());
            variantFilterController.setAnalysisDetailSNVController(this);
            variantFilterController.show((Parent) node);
            statisticsTitledPane.setContent(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void saveFilter(List<Object> list, String filterName) {
        if(StringUtils.isEmpty(filterName)) {
            ComboBoxItem comboBoxItem = new ComboBoxItem("C" + filterList.size(), "C" + filterList.size());
            filterComboBox.getItems().add(comboBoxItem);
            filterList.put("C" + filterList.size(), list);
            filterComboBox.getSelectionModel().select(comboBoxItem);
        } else {
            filterList.remove(filterName);
            filterList.put(filterName, list);
            showVariantList(currentPageIndex + 1, 0);
        }

    }

    @FXML
    public void viewAppliedFilters() {
        if(filterComboBox.getSelectionModel().getSelectedItem().getValue().startsWith("C")) {
            List<Object> list = filterList.get(filterComboBox.getSelectionModel().getSelectedItem().getValue());
            String currentKey = "";
            for(Object obj : list) {
                if(obj.toString().contains(" ")) {
                    String key = obj.toString().substring(0, obj.toString().indexOf(" "));
                    if (!key.equalsIgnoreCase(currentKey)) {
                        currentKey = key;
                    }
                    String value = obj.toString().substring(obj.toString().indexOf(" ") + 1);
                    logger.debug(currentKey + " " + value);
                }
            }

        }
    }


    private void setTableViewColumn() {
        String centerStyleClass = "alignment_center";
        if(panel != null && ExperimentTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> swTier = new TableColumn<>("Prediction");
            createTableHeader(swTier, "Prediction", "swTier" ,70d);
            swTier.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSwTier()));
            swTier.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    Label label = null;
                    if(item != null) {
                        String code = ACMGFilterCode.getCodeFromAlias(item);
                        if(code != null && !"NONE".equals(code)) {
                            label = new Label(item);
                            label.getStyleClass().clear();
                            swTier.getStyleClass().add(centerStyleClass);
                            label.getStyleClass().add("tier_" + code);
                        }
                    }
                    setGraphic(label);
                }
            });
            TableColumn<VariantAndInterpretationEvidence, String> expertTier = new TableColumn<>("Tier");
            createTableHeader(expertTier, "Tier", "expertTier" ,null);
            expertTier.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getExpertTier()));
            expertTier.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    Label label = null;
                    if(item != null) {
                        String code = ACMGFilterCode.getCodeFromAlias(item);
                        if(code != null && !"NONE".equals(code)) {
                            label = new Label(item);
                            label.getStyleClass().clear();
                            expertTier.getStyleClass().add(centerStyleClass);
                            label.getStyleClass().add("tier_" + code);
                        }
                    }
                    setGraphic(label);
                }
            });
        } else {
            TableColumn<VariantAndInterpretationEvidence, String> swPathogenicityLevel = new TableColumn<>("Prediction");
            createTableHeader(swPathogenicityLevel, "Prediction", "swPathogenicity" ,70.);
            swPathogenicityLevel.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSwPathogenicity()));
            swPathogenicityLevel.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    Label label = null;
                    if(item != null) {
                        String code = PredictionTypeCode.getCodeFromAlias(item);
                        if(code != null && !"NONE".equals(code)) {
                            label = new Label(item);
                            label.getStyleClass().clear();
                            swPathogenicityLevel.getStyleClass().add(centerStyleClass);
                            label.getStyleClass().add("prediction_" + code);
                        }
                    }
                    setGraphic(label);
                }
            });

            TableColumn<VariantAndInterpretationEvidence, String> expertPathogenicityLevel = new TableColumn<>("Pathogenicity");
            createTableHeader(expertPathogenicityLevel, "Pathogenicity", "expertPathogenicity" ,90.);
            expertPathogenicityLevel.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getExpertPathogenicity()));
            expertPathogenicityLevel.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    Label label = null;
                    if(item != null) {
                        String code = PredictionTypeCode.getCodeFromAlias(item);
                        if(code != null && !"NONE".equals(code)) {
                            label = new Label(item);
                            label.getStyleClass().clear();
                            expertPathogenicityLevel.getStyleClass().add(centerStyleClass);
                            label.getStyleClass().add("prediction_" + code);
                        }
                    }
                    setGraphic(label);
                }
            });
        }

        TableColumn<VariantAndInterpretationEvidence, String> gene = new TableColumn<>("Gene");
        createTableHeader(gene, "Gene", "gene" ,null);
        gene.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));

        TableColumn<VariantAndInterpretationEvidence, String> warn = new TableColumn<>("Warn");
        createTableHeader(warn, "Warn", null ,55.);
        warn.getStyleClass().add(centerStyleClass);
        warn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getHasWarning()));
        warn.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                setGraphic((!StringUtils.isEmpty(item)) ? SNPsINDELsList.getWarningReasonPopOver(item) : null);
            }
        });
        if(panel != null && ExperimentTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> lowConfidence = new TableColumn<>("Low confidence");
            createTableHeader(lowConfidence, "Low confidence", "lowConfidence", 70.);
            lowConfidence.getStyleClass().add(centerStyleClass);
            lowConfidence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getLowConfidence()));
            lowConfidence.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    setGraphic((!StringUtils.isEmpty(item)) ? LowConfidenceList.getLowConfidencePopOver(item) : null);
                }
            });
        }

        TableColumn<VariantAndInterpretationEvidence, String> report = new TableColumn<>("Report");
        createTableHeader(report, "Report", null ,55.);
        report.getStyleClass().add(centerStyleClass);
        report.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getIncludedInReport()));
        report.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                Label label = null;
                if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
                    label = new Label("R");
                    label.getStyleClass().remove("label");
                    label.getStyleClass().add("report_check");
                }
                setGraphic(label);
            }
        });

        TableColumn<VariantAndInterpretationEvidence, String> type = new TableColumn<>("Type");
        createTableHeader(type, "Type", null ,null);
        type.getStyleClass().clear();
        type.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getVariantType()));
        if(panel != null && ExperimentTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> typeExtension = new TableColumn<>("Type Extension");
            createTableHeader(typeExtension, "Type Extension", null, 70.);
            typeExtension.getStyleClass().clear();
            typeExtension.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getVariantTypeExtension()));
        }

        TableColumn<VariantAndInterpretationEvidence, String> codCons = new TableColumn<>("Cod.Cons");
        createTableHeader(codCons, "Cod.Cons", null ,70.);
        codCons.getStyleClass().clear();
        codCons.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getCodingConsequence()));

        TableColumn<VariantAndInterpretationEvidence, Integer> genomicCoordinate = new TableColumn<>("StartPosition");
        createTableHeader(genomicCoordinate, "StartPosition", null ,null);
        genomicCoordinate.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getStartPosition()).asObject());

        TableColumn<VariantAndInterpretationEvidence, String> strand = new TableColumn<>("Strand");
        createTableHeader(strand, "Strand", null ,55.);
        strand.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getStrand()));
        strand.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, String> transcript = new TableColumn<>("Transcript");
        createTableHeader(transcript, "Transcript", null ,null);
        transcript.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getTranscript()));

        TableColumn<VariantAndInterpretationEvidence, String> ntChange = new TableColumn<>("NT change");
        createTableHeader(ntChange, "NT change", null ,90.);
        ntChange.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));

        TableColumn<VariantAndInterpretationEvidence, String> aaChangeConversion = new TableColumn<>("AA change(Single)");
        createTableHeader(aaChangeConversion, "AA change(Single)", null ,90.);
        aaChangeConversion.setCellValueFactory(cellData -> cellData.getValue().getSnpInDel().getSnpInDelExpression().getAachangeSingleLetter() == null ?
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChangeConversion()) :
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAachangeSingleLetter()));

        TableColumn<VariantAndInterpretationEvidence, String> aaChange = new TableColumn<>("AA change");
        createTableHeader(aaChange, "AA change", null ,90.);
        aaChange.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChange()));
        aaChange.setVisible(false);

        if(panel != null && ExperimentTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> ntChangeBIC = new TableColumn<>("NT change(BIC)");
            createTableHeader(ntChangeBIC, "NT change(BIC)", null ,90.);
            ntChangeBIC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().createNtChangeBRCA()));
        }

        TableColumn<VariantAndInterpretationEvidence, String> chr = new TableColumn<>("Chr");
        createTableHeader(chr, "Chr", "chromosome" ,null);
        chr.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getChromosome()));

        TableColumn<VariantAndInterpretationEvidence, String> ref = new TableColumn<>("Ref");
        createTableHeader(ref, "Ref", null ,null);
        ref.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getRefSequence()));

        TableColumn<VariantAndInterpretationEvidence, String> alt = new TableColumn<>("Alt");
        createTableHeader(alt, "Alt", null ,null);
        alt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getAltSequence()));
        if(panel != null && ExperimentTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> zigosity = new TableColumn<>("Zigosity");
            createTableHeader(zigosity, "Zigosity", null, null);
            zigosity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getZygosity()));
            if (panel.getCode().equalsIgnoreCase("447") && sample.getSampleSource().equalsIgnoreCase("FFPE")) {
                zigosity.setVisible(false);
            }
        }
        TableColumn<VariantAndInterpretationEvidence, String> exon = new TableColumn<>("Exon");
        createTableHeader(exon, "Exon", null ,null);
        exon.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getExonNum()));
        exon.setVisible(false);
        if(panel != null && ExperimentTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> exonBic = new TableColumn<>("Exon(BIC)");
            createTableHeader(exonBic, "Exon(BIC)", null ,null);
            exonBic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getExonNumBic()));
        }

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> fraction = new TableColumn<>("Fraction");
        createTableHeader(fraction, "Fraction", "alleleFraction" ,null);
        fraction.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()));

        TableColumn<VariantAndInterpretationEvidence, Integer> refNum = new TableColumn<>("Ref.num");
        createTableHeader(refNum, "Ref.num", null ,null);
        refNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getReadInfo().getRefReadNum()).asObject());

        TableColumn<VariantAndInterpretationEvidence, Integer> altNum = new TableColumn<>("Alt.num");
        createTableHeader(altNum, "Alt.num", null ,null);
        altNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getReadInfo().getAltReadNum()).asObject());

        TableColumn<VariantAndInterpretationEvidence, Integer> depth = new TableColumn<>("Depth");
        createTableHeader(depth, "Depth", null ,null);
        depth.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getReadInfo().getReadDepth()).asObject());

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> thousandGenomics = new TableColumn<>("1KG");
        createTableHeader(thousandGenomics, "1KG", null ,null);
        thousandGenomics.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getAll())));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> exac = new TableColumn<>("ExAC");
        createTableHeader(exac, "ExAC", null ,null);
        exac.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getExac())));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> esp = new TableColumn<>("Esp6500");
        createTableHeader(esp, "Esp6500", null ,null);
        esp.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getEsp6500().getAll())));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> korean = new TableColumn<>("Korean");
        createTableHeader(korean, "Korean", null ,null);
        korean.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getKoreanExomInformationDatabase())));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarAcc = new TableColumn<>("ClinVar.Acc");
        createTableHeader(clinVarAcc, "ClinVar.Acc", null ,150d);
        clinVarAcc.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarAcc()));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarClass = new TableColumn<>("ClinVar.Class");
        createTableHeader(clinVarClass, "ClinVar.Class", null ,150d);
        clinVarClass.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarClass()));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarDisease = new TableColumn<>("ClinVar.Disease");
        createTableHeader(clinVarDisease, "ClinVar.Disease", null ,null);
        clinVarDisease.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarDisease()));
        if(panel != null && ExperimentTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> clinVarTraitOMIM = new TableColumn<>("ClinVar.Trait.OMIM");
            createTableHeader(clinVarTraitOMIM, "ClinVar.Trait.OMIM", null, null);
            clinVarTraitOMIM.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarTraitOMIM()));
        }
        TableColumn<VariantAndInterpretationEvidence, String> cosmicIds = new TableColumn<>("COSMIC");
        createTableHeader(cosmicIds, "COSMIC", null ,null);
        cosmicIds.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCosmic().getCosmicIds()));

        TableColumn<VariantAndInterpretationEvidence, String> cosmicCount = new TableColumn<>("COSMIC Count");
        createTableHeader(cosmicCount, "COSMIC Count", null ,null);
        cosmicCount.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCosmic().getCosmicCount()));

        TableColumn<VariantAndInterpretationEvidence, String> cosmicOccurrence = new TableColumn<>("COSMIC Occurrence");
        createTableHeader(cosmicOccurrence, "COSMIC Occurrence", null ,null);
        cosmicOccurrence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCosmic().getCosmicOccurrence()));
        if(panel != null && ExperimentTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            cosmicCount.setVisible(false);
            cosmicOccurrence.setVisible(false);
        }
        if(panel != null && ExperimentTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> oncokbActionCancer = new TableColumn<>("OncoKB Action Cancer");
            createTableHeader(oncokbActionCancer, "OncoKB Action Cancer", null ,null);
            oncokbActionCancer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getOncoKB().getOncokbActionCancer()));

            TableColumn<VariantAndInterpretationEvidence, String> oncokbActionDrugs = new TableColumn<>("OncoKB Action Drugs");
            createTableHeader(oncokbActionDrugs, "OncoKB Action Drugs", null ,null);
            oncokbActionDrugs.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getOncoKB().getOncokbActionDrugs()));

            TableColumn<VariantAndInterpretationEvidence, String> oncokbActionLevel = new TableColumn<>("OncoKB Action Level");
            createTableHeader(oncokbActionLevel, "OncoKB Action Level", null ,null);
            oncokbActionLevel.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getOncoKB().getOncokbActionLevel()));

            TableColumn<VariantAndInterpretationEvidence, String> oncokbAlteration = new TableColumn<>("OncoKB Alteration");
            createTableHeader(oncokbAlteration, "OncoKB Alteration", null ,null);
            oncokbAlteration.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getOncoKB().getOncokbAlteration()));

            TableColumn<VariantAndInterpretationEvidence, String> oncokbPmids = new TableColumn<>("OncoKB PMIDs");
            createTableHeader(oncokbPmids, "OncoKB PMIDs", null ,null);
            oncokbPmids.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getOncoKB().getOncokbPmids()));

            TableColumn<VariantAndInterpretationEvidence, String> oncokbActionPmid = new TableColumn<>("OncoKB Action PMID");
            createTableHeader(oncokbActionPmid, "OncoKB Action PMID", null ,null);
            oncokbActionPmid.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getOncoKB().getOncokbActionPmid()));

            TableColumn<VariantAndInterpretationEvidence, String> oncokbOncogenicity = new TableColumn<>("OncoKB Oncogenicity");
            createTableHeader(oncokbOncogenicity, "OncoKB Oncogenicity", null ,null);
            oncokbOncogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getOncoKB().getOncokbOncogenicity()));

            TableColumn<VariantAndInterpretationEvidence, String> oncokbHgvsp = new TableColumn<>("OncoKB HGVS.p");
            createTableHeader(oncokbHgvsp, "OncoKB HGVS.p", null ,null);
            oncokbHgvsp.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getOncoKB().getOncokbHgvsp()));

            TableColumn<VariantAndInterpretationEvidence, String> oncokbTx = new TableColumn<>("OncoKB TX");
            createTableHeader(oncokbTx, "OncoKB TX", null ,null);
            oncokbTx.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getOncoKB().getOncokbTx()));
        }
        if(panel != null && ExperimentTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> bicClass = new TableColumn<>("BIC.Class");
            createTableHeader(bicClass, "BIC.Class", null ,null);
            bicClass.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicClass()));

            TableColumn<VariantAndInterpretationEvidence, String> bicDesignation = new TableColumn<>("BIC.Designation");
            createTableHeader(bicDesignation, "BIC.Designation", null ,null);
            bicDesignation.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicDesignation()));

            TableColumn<VariantAndInterpretationEvidence, String> bicNt = new TableColumn<>("BIC.NT");
            createTableHeader(bicNt, "BIC.NT", null ,null);
            bicNt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicNt()));

            TableColumn<VariantAndInterpretationEvidence, String> kohbraPatient = new TableColumn<>("KOHBRA.patient");
            createTableHeader(kohbraPatient, "KOHBRA.patient", null, null);
            kohbraPatient.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getKohbraPatient()));

            TableColumn<VariantAndInterpretationEvidence, BigDecimal> kohbraFrequency = new TableColumn<>("KOHBRA.frequency");
            createTableHeader(kohbraFrequency, "KOHBRA.frequency", null, null);
            kohbraFrequency.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getKohbraFreq())));
        }
        TableColumn<VariantAndInterpretationEvidence, Integer> variantNum = new TableColumn<>("VariantNum");
        createTableHeader(variantNum, "VariantNum", null ,null);
        variantNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getVariantNum()).asObject());
        variantNum.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, String> refGenomeVer = new TableColumn<>("RefGenomeVer");
        createTableHeader(refGenomeVer, "RefGenomeVer", null ,null);
        refGenomeVer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getRefGenomeVer()));
        refGenomeVer.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, String> leftSequence = new TableColumn<>("LeftSequence");
        createTableHeader(leftSequence, "LeftSequence", null ,null);
        leftSequence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getLeftSequence()));
        leftSequence.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, String> rightSequence = new TableColumn<>("RightSequence");
        createTableHeader(rightSequence, "RightSequence", null ,null);
        rightSequence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getRightSequence()));
        rightSequence.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, String> dbSnpRsId = new TableColumn<>("SnpRsId");
        createTableHeader(dbSnpRsId, "SnpRsId", null ,null);
        dbSnpRsId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getPopulationFrequency().getDbsnpRsId()));
        dbSnpRsId.setVisible(false);

        if(panel != null && ExperimentTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> bicCategory = new TableColumn<>("BIC.Category");
            createTableHeader(bicCategory, "BIC.Category", null ,null);
            bicCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicCategory()));
            bicCategory.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> bicImportance = new TableColumn<>("BIC.Importance");
            createTableHeader(bicImportance, "BIC.Importance", null ,null);
            bicImportance.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicImportance()));
            bicImportance.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beClinVarUpdate = new TableColumn<>("Be.ClinVar.Update");
            createTableHeader(beClinVarUpdate, "Be.ClinVar.Update", null ,null);
            beClinVarUpdate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarUpdate()));
            beClinVarUpdate.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beClinVarOrigin = new TableColumn<>("Be.ClinVar.Origin");
            createTableHeader(beClinVarOrigin, "Be.ClinVar.Origin", null ,null);
            beClinVarOrigin.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarOrigin()));
            beClinVarOrigin.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beClinVarMethod = new TableColumn<>("Be.ClinVar.Method");
            createTableHeader(beClinVarMethod, "Be.ClinVar.Method", null ,null);
            beClinVarMethod.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarMethod()));
            beClinVarMethod.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beBicCategory = new TableColumn<>("Be.BIC.Category");
            createTableHeader(beBicCategory, "Be.BIC.Category", null ,null);
            beBicCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicCategory()));
            beBicCategory.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beBicNationality = new TableColumn<>("Be.BIC.Nationality");
            createTableHeader(beBicNationality, "Be.BIC.Nationality", null ,null);
            beBicNationality.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicNationality()));
            beBicNationality.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beBicEthnic = new TableColumn<>("Be.BIC.Ethnic");
            createTableHeader(beBicEthnic, "Be.BIC.Ethnic", null ,null);
            beBicEthnic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicNationality()));
            beBicEthnic.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beBicPathogenicity = new TableColumn<>("Be.BIC.Pathogenicity");
            createTableHeader(beBicPathogenicity, "Be.BIC.Pathogenicity", null ,null);
            beBicPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicPathogenicity()));
            beBicPathogenicity.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beTranscript = new TableColumn<>("Be.Transcript");
            createTableHeader(beTranscript, "Be.Transcript", null ,null);
            beTranscript.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeTranscript()));
            beTranscript.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beNt = new TableColumn<>("Be.NT");
            createTableHeader(beNt, "Be.NT", null ,null);
            beNt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeNt()));
            beNt.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beGene = new TableColumn<>("Be.Gene");
            createTableHeader(beGene, "Be.Gene", null ,null);
            beGene.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeGene()));
            beGene.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beEnigmaCondition = new TableColumn<>("Be.Enigma.Condition");
            createTableHeader(beEnigmaCondition, "Be.Enigma.Condition", null ,null);
            beEnigmaCondition.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaCondition()));
            beEnigmaCondition.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beEnigmaUpdate = new TableColumn<>("Be.Enigma.Update");
            createTableHeader(beEnigmaUpdate, "Be.Enigma.Update", null ,null);
            beEnigmaUpdate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaUpdate()));
            beEnigmaUpdate.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beClinVarPathogenicity = new TableColumn<>("Be.ClinVar.Pathogenicity");
            createTableHeader(beClinVarPathogenicity, "Be.ClinVar.Pathogenicity", null ,null);
            beClinVarPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarPathogenicity()));
            beClinVarPathogenicity.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beEnigmaPathogenicity = new TableColumn<>("Be.Enigma.Pathogenicity");
            createTableHeader(beEnigmaPathogenicity, "Be.Enigma.Pathogenicity", null ,null);
            beEnigmaPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaPathogenicity()));
            beEnigmaPathogenicity.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> enigma = new TableColumn<>("Enigma");
            createTableHeader(enigma, "Enigma", null ,null);
            enigma.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaPathogenicity()));
            enigma.setVisible(false);
        }

        variantListTableView.getStyleClass().clear();
        variantListTableView.getStyleClass().add("table-view");

    }
}
