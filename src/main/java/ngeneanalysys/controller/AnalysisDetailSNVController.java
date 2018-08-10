package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.*;
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
import ngeneanalysys.model.render.SNPsINDELsList;
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
 * @since 2018-03-15
 */
public class AnalysisDetailSNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    @FXML
    private CheckBox showFalseVariantsCheckBox;

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

    @FXML
    private Button changeTierButton;

    @FXML
    private Button falsePositiveButton;

    @FXML
    private Button addToReportButton;

    @FXML
    private Button showIGVButton;

    private SampleView sample = null;
    private Panel panel = null;
    private Map<String, String> sortMap = new HashMap<>();

    private AnalysisDetailVariantsController variantsController;
    //VariantList
    private List<VariantAndInterpretationEvidence> list = null;

    /** 현재 선택된 변이 정보 객체 */
    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;
    /** 현재 선택된 변이 리스트 객체의 index */

    private Integer currentPageIndex = -1;

    private AnalysisDetailInterpretationController interpretationController;

    private AnalysisDetailSNPsINDELsMemoController memoController;

    private Map<String, List<Object>> filterList = new HashMap<>();

    private Map<String, TableColumn> columnMap = new HashMap<>();

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
            if (interpretationLogsTitledPane.getContent() == null) {
                showMemoTab(FXCollections.observableList(memoList.getResult()));
            } else {
                memoController.updateList(selectedAnalysisResultVariant.getSnpInDel().getId());
            }
        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void show(Parent root) throws IOException {
        logger.debug("init snv controller");

        apiService = APIService.getInstance();

        /*variantListTableView.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            variantListTableView.refresh();
            // close text box
            variantListTableView.edit(-1, null);
        });*/

        //filterAddBtn.setDisable(true);
        //viewAppliedFiltersLabel.setDisable(true);

        sample = (SampleView)paramMap.get("sampleView");
        panel = (Panel)paramMap.get("panel");
        if(panel.getAnalysisType().equalsIgnoreCase("SOMATIC")) {
            this.filterList = (Map<String, List<Object>>)mainController.getBasicInformationMap().get("somaticFilter");
        } else if(panel.getAnalysisType().equalsIgnoreCase("GERMLINE")) {
            this.filterList = (Map<String, List<Object>>)mainController.getBasicInformationMap().get("germlineFilter");
        }

        eventRegistration();

        interpretationLogsTitledPane.setOnMouseClicked(ev -> setAccordionContents());

        leftSizeButton.setOnMouseClicked(event -> {
            if(leftSizeButton.getStyleClass().contains("btn_fold")){
                foldLeft();
            } else if (leftSizeButton.getStyleClass().get(0) != null){
                expandLeft();
            }
        });

        setDefaultFilter();

        filterComboBox.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            setFilterList();
        });

        //setFilterList();

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

        //foldLeft();
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
            String[] defaultFilterName = {"Tier I", "Tier II", "Tier III", "Tier IV", "Pathogenic", "Likely Pathogenic",
                    "Uncertain Significance", "Likely Benign", "Benign", "Tier 1", "Tier 2", "Tier 3", "Tier 4", "All"};
            viewAppliedFiltersLabel.setDisable(Arrays.stream(defaultFilterName).anyMatch(item -> item.equals(nv.getValue())));

        });

//        showFalseVariantsCheckBox.addEventFilter(MouseEvent.MOUSE_CLICKED, ev ->
//            showVariantList(currentPageIndex + 1, 0));

        snvWrapper.widthProperty().addListener((ob, ov, nv) -> {
            double wrapperWidth = (Double)nv;
            int filterWidth = (int)snvWrapper.getColumnConstraints().get(0).getPrefWidth();

            if(snvWrapper.getColumnConstraints().get(1).getPrefWidth() == 0) {
                snvWrapper.getColumnConstraints().get(2).setPrefWidth(wrapperWidth - filterWidth);
                rightContentsHBox.setPrefWidth(wrapperWidth - filterWidth);
                overviewAccordion.setPrefWidth(wrapperWidth - filterWidth - 70);
            } else {
                snvWrapper.getColumnConstraints().get(1).setPrefWidth(wrapperWidth - filterWidth - 50);
                tableVBox.setPrefWidth(wrapperWidth - filterWidth - 50);
                variantListTableView.setPrefWidth(wrapperWidth - filterWidth - 110);
            }
        });

        snvWrapper.heightProperty().addListener((ob, ov, nv) -> {
            double wrapperHeight = (Double)nv;

            if(wrapperHeight > 450) {
                changeTitledPaneTextSize("font_size_13", "font_size_18");
            } else {
                changeTitledPaneTextSize("font_size_18", "font_size_13");
            }
        });

    }

    private void changeTitledPaneTextSize(String currentStyle, String changeStyle) {
        if(interpretationTitledPane != null) {
            interpretationTitledPane.getStyleClass().remove(currentStyle);
            interpretationTitledPane.getStyleClass().add(changeStyle);
        } else {
            clinicalSignificantTitledPane.getStyleClass().remove(currentStyle);
            clinicalSignificantTitledPane.getStyleClass().add(changeStyle);
        }
        variantDetailTitledPane.getStyleClass().remove(currentStyle);
        variantDetailTitledPane.getStyleClass().add(changeStyle);
        statisticsTitledPane.getStyleClass().remove(currentStyle);
        statisticsTitledPane.getStyleClass().add(changeStyle);
        interpretationLogsTitledPane.getStyleClass().remove(currentStyle);
        interpretationLogsTitledPane.getStyleClass().add(changeStyle);
    }

    private List<VariantAndInterpretationEvidence> getSelectedItemList() {
        if(variantListTableView.getItems() == null) {
            return new ArrayList<>();
        }
        return variantListTableView.getItems().stream().filter(VariantAndInterpretationEvidence::getCheckItem)
                .collect(Collectors.toList());
    }

    private void eventRegistration() {
        addToReportButton.setCursor(Cursor.HAND);
        addToReportButton.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            List<VariantAndInterpretationEvidence> selectList = getSelectedItemList();
            if(!selectList.isEmpty()) {
                try {
                    FXMLLoader loader = getMainApp().load(FXMLConstants.BATCH_EXCLUDE_REPORT);
                    Node node = loader.load();
                    BatchExcludeReportDialogController controller = loader.getController();
                    controller.settingItem(sample.getId(), selectList, this);
                    controller.setMainController(this.getMainController());
                    controller.setParamMap(getParamMap());
                    controller.show((Parent) node);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if("SOMATIC".equalsIgnoreCase(panel.getAnalysisType())) {
            changeTierButton.setCursor(Cursor.HAND);
            changeTierButton.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
                List<VariantAndInterpretationEvidence> selectList = getSelectedItemList();
                if (!selectList.isEmpty()) {
                    try {
                        FXMLLoader loader = getMainApp().load(FXMLConstants.BATCH_CHANGE_TIER);
                        Node node = loader.load();
                        BatchChangeTierDialogController controller = loader.getController();
                        controller.settingItem(sample.getId(), selectList, this);
                        controller.setMainController(this.getMainController());
                        controller.setParamMap(getParamMap());
                        controller.show((Parent) node);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            changeTierButton.setText("Pathogenicity");
            changeTierButton.setCursor(Cursor.HAND);
            changeTierButton.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
                List<VariantAndInterpretationEvidence> selectList = getSelectedItemList();
                if (!selectList.isEmpty()) {
                    try {
                        FXMLLoader loader = getMainApp().load(FXMLConstants.BATCH_CHANGE_PATHOGENICITY);
                        Node node = loader.load();
                        BatchChangePathogenicityDialogController controller = loader.getController();
                        controller.settingItem(sample.getId(), selectList, this);
                        controller.setMainController(this.getMainController());
                        controller.setParamMap(getParamMap());
                        controller.show((Parent) node);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        falsePositiveButton.setCursor(Cursor.HAND);
        falsePositiveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            List<VariantAndInterpretationEvidence> selectList = getSelectedItemList();
            if(!selectList.isEmpty()) {
                try {
                    FXMLLoader loader = getMainApp().load(FXMLConstants.BATCH_FALSE_POSITIVE);
                    Node node = loader.load();
                    BatchFalsePositiveDialogController controller = loader.getController();
                    controller.settingItem(sample.getId(), selectList, this);
                    controller.setMainController(this.getMainController());
                    controller.setParamMap(getParamMap());
                    controller.show((Parent) node);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        showIGVButton.setCursor(Cursor.HAND);
        showIGVButton.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            if(variantListTableView.getSelectionModel() != null
                    && variantListTableView.getSelectionModel().getSelectedItem() != null) {
                VariantAndInterpretationEvidence variant = variantListTableView.getSelectionModel().getSelectedItem();
                IGVService igvService = IGVService.getInstance();
                igvService.setMainController(getMainController());

                String sampleId = sample.getId().toString();
                String variantId = variant.getSnpInDel().getId().toString();
                String gene = variant.getSnpInDel().getGenomicCoordinate().getGene();
                String locus = String.format("%s:%,d-%,d",
                        variant.getSnpInDel().getGenomicCoordinate().getChromosome(),
                        variant.getSnpInDel().getGenomicCoordinate().getStartPosition(),
                        variant.getSnpInDel().getGenomicCoordinate().getStartPosition());
                String refGenome = variant.getSnpInDel().getGenomicCoordinate().getRefGenomeVer();
                String humanGenomeVersion = (refGenome.contains("hg19")) ? "hg19" : "hg18";

                try {
                    igvService.load(sampleId, sample.getName(), variantId, gene, locus , humanGenomeVersion);
                } catch (WebAPIException wae) {
                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                            getMainApp().getPrimaryStage(), true);
                } catch (Exception e) {
                    DialogUtil.generalShow(Alert.AlertType.ERROR, "IGV launch fail", "IGV software doesn't launch.",
                            getMainApp().getPrimaryStage(), true);
                }
            }
        });
    }

    void refreshTable() {
        showVariantList(currentPageIndex + 1, 0);
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
                        + variant.getSnpInDel().getSnpInDelExpression().getAaChangeSingleLetter();
            } else {
                String[] ntChange = variant.getSnpInDel().getSnpInDelExpression().getNtChange().split(":");
                String[] aaChangeConversion = variant.getSnpInDel().getSnpInDelExpression().getAaChangeSingleLetter().split(":");
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

    private void setDefaultFilter() {
        totalLabel.setText("of " + sample.getAnalysisResultSummary().getAllVariantCount() + " variants");
        filterComboBox.setConverter(new ComboBoxConverter());
        filterComboBox.getItems().removeAll(filterComboBox.getItems());
        filterComboBox.getItems().add(new ComboBoxItem("All", "All"));
        if(panel.getAnalysisType().equalsIgnoreCase("SOMATIC")) {
            filterComboBox.getItems().add(new ComboBoxItem("Tier 1", "Tier I"));
            filterComboBox.getItems().add(new ComboBoxItem("Tier 2", "Tier II"));
            filterComboBox.getItems().add(new ComboBoxItem("Tier 3", "Tier III"));
            filterComboBox.getItems().add(new ComboBoxItem("Tier 4", "Tier IV"));
        } else if(panel.getAnalysisType().equalsIgnoreCase("GERMLINE")) {
            filterComboBox.getItems().add(new ComboBoxItem("Pathogenic", "Pathogenic"));
            filterComboBox.getItems().add(new ComboBoxItem("Likely Pathogenic", "Likely Pathogenic"));
            filterComboBox.getItems().add(new ComboBoxItem("Uncertain Significance", "Uncertain Significance"));
            filterComboBox.getItems().add(new ComboBoxItem("Likely Benign", "Likely Benign"));
            filterComboBox.getItems().add(new ComboBoxItem("Benign", "Benign"));
        }
        filterComboBox.getSelectionModel().select(0);
        viewAppliedFiltersLabel.setDisable(true);
    }

    @SuppressWarnings("unchecked")
    private void setFilterList() {
        String currentFilterName = filterComboBox.getSelectionModel().getSelectedItem().getText();
        filterComboBox.hide();
        if(panel.getAnalysisType().equalsIgnoreCase("SOMATIC")) {

            Map<String, List<Object>> filter = (Map<String, List<Object>>)mainController.getBasicInformationMap().get("somaticFilter");
            Set<String> keySet = filter.keySet();

            if(filterComboBox.getItems().size() > 5) {
                while(filterComboBox.getItems().size() > 5) {
                    filterComboBox.getItems().remove(filterComboBox.getItems().size() - 1);
                }
            }

            for(String key : keySet) {
                if(!(key.equals("Tier 1") || key.equals("Tier 2") ||key.equals("Tier 3") ||key.equals("Tier 4"))) {
                    filterComboBox.getItems().add(new ComboBoxItem(key, key));
                }
            }
            filterList = filter;
        } else if(panel.getAnalysisType().equalsIgnoreCase("GERMLINE")) {

            Map<String, List<Object>> filter = (Map<String, List<Object>>)mainController.getBasicInformationMap().get("germlineFilter");
            Set<String> keySet = filter.keySet();

            while(filterComboBox.getItems().size() > 6) {
                filterComboBox.getItems().remove(filterComboBox.getItems().size() - 1);
            }

            for(String key : keySet) {
                if(!(key.equals("Pathogenic") || key.equals("Likely Pathogenic") ||key.equals("Uncertain Significance")
                        || key.equals("Likely Benign") || key.equals("Benign"))) {
                    filterComboBox.getItems().add(new ComboBoxItem(key, key));
                }
                filterList = filter;
            }
        }
        filterComboBox.show();
        Optional<ComboBoxItem> optionalComboBoxItem = filterComboBox.getItems().stream().filter(item
                -> item.getText().equalsIgnoreCase(currentFilterName)).findFirst();
        optionalComboBoxItem.ifPresent(comboBoxItem -> filterComboBox.getSelectionModel().select(comboBoxItem));
    }

    private void expandLeft() {
        double leftExpandedWidth = 200;
        double snvWrapperWidth = snvWrapper.getWidth();
        snvWrapper.getColumnConstraints().get(0).setPrefWidth(leftExpandedWidth);
        if(snvWrapper.getColumnConstraints().get(1).getPrefWidth() == 0) {
            snvWrapper.getColumnConstraints().get(2).setPrefWidth(snvWrapperWidth - leftExpandedWidth);
            rightContentsHBox.setPrefWidth(snvWrapperWidth - leftExpandedWidth);
            overviewAccordion.setPrefWidth(snvWrapperWidth - leftExpandedWidth - 70);
            tableVBox.setVisible(false);
        } else {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(snvWrapperWidth - leftExpandedWidth - 50);
            tableVBox.setPrefWidth(snvWrapperWidth - leftExpandedWidth - 50);
            variantListTableView.setPrefWidth(snvWrapperWidth - leftExpandedWidth - 110);
            overviewAccordion.setVisible(false);
        }

        filterArea.setVisible(true);
        filterArea.setPrefWidth(150);
        leftSizeButton.getStyleClass().clear();
        leftSizeButton.getStyleClass().add("btn_fold");
    }

    private void foldLeft(){
        double leftFoldedWidth = 50;
        double snvWrapperWidth = snvWrapper.getWidth();
        snvWrapper.getColumnConstraints().get(0).setPrefWidth(leftFoldedWidth);
        rightContentsHBox.setPrefWidth(snvWrapperWidth - leftFoldedWidth);
        if(snvWrapper.getColumnConstraints().get(1).getPrefWidth() == 0) {
            snvWrapper.getColumnConstraints().get(2).setPrefWidth(snvWrapperWidth - leftFoldedWidth);
            overviewAccordion.setPrefWidth(snvWrapperWidth - leftFoldedWidth - 70);
            overviewAccordion.setVisible(true);
            tableVBox.setVisible(false);
        } else {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(snvWrapperWidth - leftFoldedWidth - 50);
            tableVBox.setPrefWidth(snvWrapperWidth - leftFoldedWidth - 50);
            variantListTableView.setPrefWidth(snvWrapperWidth - leftFoldedWidth - 110);
            overviewAccordion.setVisible(false);
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
            double snvWrapperWidth = snvWrapper.getWidth();
            double filterAreaWidth = snvWrapper.getColumnConstraints().get(0).getPrefWidth();
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(centerFoldedWidth);

            snvWrapper.getColumnConstraints().get(2).setPrefWidth(snvWrapperWidth - filterAreaWidth);
            overviewAccordion.setPrefWidth(snvWrapperWidth - filterAreaWidth - 70);

            overviewAccordion.setVisible(true);
            tableVBox.setPrefWidth(0);
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
        double snvWrapperWidth = snvWrapper.getWidth();
        double filterAreaWidth = snvWrapper.getColumnConstraints().get(0).getPrefWidth();
        snvWrapper.getColumnConstraints().get(2).setPrefWidth(rightFoldedWidth);

        snvWrapper.getColumnConstraints().get(1).setPrefWidth(snvWrapperWidth - filterAreaWidth - 50);
        tableVBox.setPrefWidth(snvWrapperWidth - filterAreaWidth - 50);
        variantListTableView.setPrefWidth(snvWrapperWidth - filterAreaWidth - 110);

        rightContentsHBox.setPrefWidth(0);
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
            memoController = loader.getController();
            memoController.setMainController(this.getMainController());
            memoController.setAnalysisDetailSNVController(this);
            memoController.show((Parent) node);
            memoController.displayList(memoList);
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
            // VariantAndInterpretationEvidence variantAndInterpretationEvidence = new VariantAndInterpretationEvidence();

            // variantAndInterpretationEvidence.setSnpInDel(snpInDel);
            // variantAndInterpretationEvidence.setSnpInDelEvidences(selectedAnalysisResultVariant.getSnpInDelEvidences());

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
            if(panel.getAnalysisType().equalsIgnoreCase(AnalysisTypeCode.SOMATIC.getDescription())) {
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
        Set<Map.Entry<String, String>> entrySet = sortMap.entrySet();
        List<Object> sortList = new ArrayList<>();
        for(Map.Entry<String, String> entry : entrySet) {
            sortList.add(entry.getKey() + " " + entry.getValue());
        }
        if(!sortList.isEmpty()) list.put("sort", sortList);
    }

    private void setFilterItem(Map<String, List<Object>> list) {
        ComboBoxItem comboBoxItem = filterComboBox.getSelectionModel().getSelectedItem();
        if(comboBoxItem != null && filterList.containsKey(comboBoxItem.getValue())) {
            list.put("search", filterList.get(comboBoxItem.getValue()));
        }
        if(!showFalseVariantsCheckBox.isSelected()) {
            setIsFalseItem("N", list);
        }
    }

    public void setIsFalseItem(String flag, Map<String, List<Object>> list) {
        if(list.containsKey("search")) {
            list.get("search").add("isFalse " + flag);
        } else {
            List<Object> searchList = new ArrayList<>();
            searchList.add("isFalse " + flag);
            list.put("search", searchList);
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

            searchCountLabel.setText("Showing " + totalCount);

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

    void comboBoxSetAll() {
        filterComboBox.getSelectionModel().selectFirst();
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
        worksheetUtil.exportVariantData("EXCEL", params, this.getMainApp(), sample);
    }

    @FXML
    public void csvDownload() {
        Map<String, Object> params = new HashMap<>();
        WorksheetUtil worksheetUtil = new WorksheetUtil();
        worksheetUtil.exportVariantData("CSV", params, this.getMainApp(), sample);
    }

    @FXML
    public void showFilter() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_VARIANT_FILTER);
            Node node = loader.load();
            VariantFilterController variantFilterController = loader.getController();
            variantFilterController.setMainController(this.getMainController());
            variantFilterController.setFilter(filterList);
            variantFilterController.setParamMap(paramMap);
            variantFilterController.setSnvController(this);
            variantFilterController.setAnalysisType(panel.getAnalysisType());
            variantFilterController.show((Parent) node);
            statisticsTitledPane.setContent(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void viewAppliedFilters() {
        ComboBoxItem comboBoxItem = filterComboBox.getSelectionModel().getSelectedItem();
        PopOverUtil.openFilterPopOver(viewAppliedFiltersLabel, filterList.get(comboBoxItem.getValue()));
    }

    private void createTableHeader(TableColumn<VariantAndInterpretationEvidence, ?> column, String name, String sortName, Double size) {
        Label label = new Label(name);
        label.setPrefHeight(Double.MAX_VALUE);
        column.setSortable(false);
        if(StringUtils.isNotEmpty(sortName)) {
            //column.getStyleClass().add("sort_icon");
            label.setOnMouseClicked(e -> sortTable(sortName));
        }
        column.setGraphic(label);

        column.widthProperty().addListener((ob, ov, nv) -> {
            label.setMinWidth(column.getWidth());
        });

        if(size != null) column.setPrefWidth(size);

        variantListTableView.getColumns().add(column);
        columnMap.put(name, column);
    }

    /*private void createTableHeader(TableColumn<VariantAndInterpretationEvidence, ?> column, String name, String sortName, Double size) {
        column.setText(name);


        if(StringUtils.isEmpty(sortName)) {
            column.setSortable(false);
        } else {
            column.setComparator((v1, v2) -> 0);
            column.sortTypeProperty().addListener((ob, ov, nv) -> {
                sortMap.clear();
                if(nv.name().equals("ASCENDING")) {
                    sortMap.put(sortName, "ASC");
                } else if(nv.name().equals("DESCENDING")) {
                    sortMap.put(sortName, "DESC");
                }
                showVariantList(currentPageIndex + 1, 0);
            });
            //label.setOnMouseClicked(e -> sortTable(sortName));

        }

        if(size != null) column.setPrefWidth(size);

        variantListTableView.getColumns().add(column);
    }*/

    private void createTableHeader(TableColumn<VariantAndInterpretationEvidence, ?> column, Double size) {
        HBox hBox = new HBox();
        hBox.setPrefHeight(Double.MAX_VALUE);
        hBox.setAlignment(Pos.CENTER);
        CheckBox box = new CheckBox();
        hBox.getChildren().add(box);
        column.setStyle("-fx-alignment : center");
        column.setSortable(false);
        column.setGraphic(box);

        box.selectedProperty().addListener((list, ov, nv) -> {
            if(variantListTableView.getItems() != null) {
                variantListTableView.getItems().stream().forEach(item -> item.setCheckItem(nv));
                variantListTableView.refresh();
            }
        });

        column.widthProperty().addListener((ob, ov, nv) -> {
            hBox.setMinWidth(column.getWidth());
        });

        if(size != null) column.setPrefWidth(size);

        variantListTableView.getColumns().add(column);
    }

    private void setTableViewColumn() {
        String centerStyleClass = "alignment_center";

        TableColumn<VariantAndInterpretationEvidence, Boolean> checkBoxColumn = new TableColumn<>("");
        createTableHeader(checkBoxColumn, 50d);
        checkBoxColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue() != null ));
        checkBoxColumn.setCellFactory(param -> new BooleanCell());

        TableColumn<VariantAndInterpretationEvidence, Boolean> testColumn = new TableColumn<>("testColumn");
        createTableHeader(testColumn, "testColumn", "test" ,70d);
        testColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue() != null));
        testColumn.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if(empty) {
                  setGraphic(null);
                } else {

                    VariantAndInterpretationEvidence variant = getTableView().getItems().get(getIndex());

                    String value;
                    String code;
                    if (panel != null && AnalysisTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
                        if(StringUtils.isEmpty(variant.getSnpInDel().getExpertTier())) {
                            value = variant.getSnpInDel().getSwTier();
                            code = "tier_" + ACMGFilterCode.getCodeFromAlias(value);
                        } else {
                            value = variant.getSnpInDel().getExpertTier();
                            code = "user_tier_" + ACMGFilterCode.getCodeFromAlias(value);
                        }

                    } else {
                        value = StringUtils.isEmpty(variant.getSnpInDel().getExpertPathogenicity()) ? variant.getSnpInDel().getSwPathogenicity()
                                : variant.getSnpInDel().getExpertPathogenicity();
                        code = "prediction_" + PredictionTypeCode.getCodeFromAlias(value);
                    }
                    Label label = null;
                    if (variant != null) {
                        if(code != null && !"NONE".equals(code)) {
                            label = new Label(value);
                            label.getStyleClass().clear();
                            testColumn.getStyleClass().add(centerStyleClass);
                            label.getStyleClass().add(code);
                        }
                    }
                    setGraphic(label);
                }
            }
        });

        TableColumn<VariantAndInterpretationEvidence, String> warn = new TableColumn<>("Warning");
        createTableHeader(warn, "Warning", "hasWarning" ,55.);
        warn.getStyleClass().add(centerStyleClass);
        warn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getHasWarning()));
        warn.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                setGraphic((StringUtils.isNotEmpty(item)) ? SNPsINDELsList.getWarningReasonPopOver(item, panel) : null);
            }
        });
        if(panel != null && (AnalysisTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) ||
                (AnalysisTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType()) &&
                        LibraryTypeCode.HYBRIDIZATION_CAPTURE.getDescription().equalsIgnoreCase(panel.getLibraryType()))) {
            TableColumn<VariantAndInterpretationEvidence, String> falsePositive = new TableColumn<>("False");
            createTableHeader(falsePositive, "False", "isFalse",55.);
            falsePositive.setStyle(falsePositive.getStyle() + "-fx-alignment : center;");
            falsePositive.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getIsFalse()));
            falsePositive.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    Label label = null;
                    if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
                        VariantAndInterpretationEvidence variant = getTableView().getItems().get(getIndex());
                        label = new Label("F");
                        label.getStyleClass().remove("label");
                        label.getStyleClass().add("tier_FP");
                        label.setCursor(Cursor.HAND);
                        PopOverUtil.openFalsePopOver(label, variant.getSnpInDel().getFalseReason());
                    }
                    setGraphic(label);
                }
            });
            falsePositive.setVisible(false);
            showFalseVariantsCheckBox.addEventFilter(MouseEvent.MOUSE_CLICKED, ev -> {
                falsePositive.setVisible(showFalseVariantsCheckBox.isSelected());
                showVariantList(currentPageIndex + 1, 0);
            });
        } else {
            showFalseVariantsCheckBox.setVisible(false);
            falsePositiveButton.setVisible(false);
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

        TableColumn<VariantAndInterpretationEvidence, String> reportTest = new TableColumn<>("Report");
        createTableHeader(reportTest, "Report", null ,55.);
        reportTest.getStyleClass().add(centerStyleClass);
        reportTest.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getIncludedInReport()));
        reportTest.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                if(StringUtils.isEmpty(item) || empty) {
                    setGraphic(null);
                    return;
                }
                VariantAndInterpretationEvidence variant = getTableView().getItems().get(getIndex());
                Label label = new Label("R");
                label.getStyleClass().remove("label");
                if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
                    label.getStyleClass().add("report_check");
                } else {
                    label.getStyleClass().add("report_uncheck");
                }
                label.setCursor(Cursor.HAND);
                label.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
                    try {
                        Map<String, Object> params = new HashMap<>();
                        params.put("sampleId", sample.getId());
                        params.put("snpInDelIds", variant.getSnpInDel().getId().toString());
                        params.put("comment", "N/A");
                        if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
                            params.put("includeInReport", "N");
                        } else {
                            params.put("includeInReport", "Y");
                        }
                        apiService.put("analysisResults/snpInDels/updateIncludeInReport", params, null, true);
                    } catch (WebAPIException wae) {
                        wae.printStackTrace();
                    }
                });
                setGraphic(label);
            }
        });

        TableColumn<VariantAndInterpretationEvidence, String> gene = new TableColumn<>("Gene");
        createTableHeader(gene, "Gene", "gene" ,null);
        gene.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));

        TableColumn<VariantAndInterpretationEvidence, String> codCons = new TableColumn<>("Consequence");
        createTableHeader(codCons, "Consequence", null ,140.);
        codCons.getStyleClass().clear();
        codCons.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getCodingConsequence()));

        TableColumn<VariantAndInterpretationEvidence, String> ntChange = new TableColumn<>("NT Change");
        createTableHeader(ntChange, "NT Change", null ,160.);
        ntChange.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));

        if(panel != null && AnalysisTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> ntChangeBIC = new TableColumn<>("NT Change (BIC)");
            createTableHeader(ntChangeBIC, "NT Change (BIC)", null ,140.);
            ntChangeBIC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().createNtChangeBRCA()));
        }

        TableColumn<VariantAndInterpretationEvidence, String> aaChange = new TableColumn<>("AA Change");
        createTableHeader(aaChange, "AA Change", null ,140.);
        aaChange.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChange()));
        aaChange.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, String> aaChangeConversion = new TableColumn<>("AA Change (Single)");
        createTableHeader(aaChangeConversion, "AA Change (Single)", null ,140.);
        aaChangeConversion.setCellValueFactory(cellData -> cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChangeSingleLetter() == null ?
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChangeConversion()) :
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChangeSingleLetter()));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarClass = new TableColumn<>("ClinVar Class");
        createTableHeader(clinVarClass, "ClinVar Class", null ,150d);
        clinVarClass.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarClass()));

        TableColumn<VariantAndInterpretationEvidence, String> chr = new TableColumn<>("Chr");
        createTableHeader(chr, "Chr", "chromosome" ,null);
        chr.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getChromosome()));

        TableColumn<VariantAndInterpretationEvidence, Integer> genomicCoordinate = new TableColumn<>("Start Position");
        createTableHeader(genomicCoordinate, "Start Position", "startPosition" ,null);
        genomicCoordinate.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getStartPosition()).asObject());

        TableColumn<VariantAndInterpretationEvidence, String> exon = new TableColumn<>("Exon");
        createTableHeader(exon, "Exon", null ,null);
        exon.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getExonNum()));
        exon.setVisible(true);

        TableColumn<VariantAndInterpretationEvidence, String> ref = new TableColumn<>("Ref");
        createTableHeader(ref, "Ref", null ,null);
        ref.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getRefSequence()));

        TableColumn<VariantAndInterpretationEvidence, String> alt = new TableColumn<>("Alt");
        createTableHeader(alt, "Alt", null ,null);
        alt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAltSequence()));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> fraction = new TableColumn<>("Fraction");
        createTableHeader(fraction, "Fraction", "alleleFraction" ,null);
        fraction.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()));

        TableColumn<VariantAndInterpretationEvidence, Integer> depth = new TableColumn<>("Depth");
        createTableHeader(depth, "Depth", "readDepth" ,null);
        depth.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getReadInfo().getReadDepth()).asObject());

        TableColumn<VariantAndInterpretationEvidence, Integer> refNum = new TableColumn<>("Ref Count");
        createTableHeader(refNum, "Ref Count", "refReadNum" ,null);
        refNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getReadInfo().getRefReadNum()).asObject());

        TableColumn<VariantAndInterpretationEvidence, Integer> altNum = new TableColumn<>("Alt Count");
        createTableHeader(altNum, "Alt Count", "altReadNum" ,null);
        altNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getReadInfo().getAltReadNum()).asObject());

        TableColumn<VariantAndInterpretationEvidence, String> transcript = new TableColumn<>("Transcript");
        createTableHeader(transcript, "Transcript", null ,null);
        transcript.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getTranscript()));

        TableColumn<VariantAndInterpretationEvidence, String> type = new TableColumn<>("Type");
        createTableHeader(type, "Type", "variantType" ,null);
        type.getStyleClass().clear();
        type.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getVariantType()));

        if(panel != null && AnalysisTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> cosmicIds = new TableColumn<>("COSMIC ID");
            createTableHeader(cosmicIds, "COSMIC ID", null ,null);
            cosmicIds.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCosmic().getCosmicIds()));
            cosmicIds.setVisible(true);
        }
        TableColumn<VariantAndInterpretationEvidence, String> dbSnpRsId = new TableColumn<>("dbSNP ID");
        createTableHeader(dbSnpRsId, "dbSNP ID", null ,null);
        dbSnpRsId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getDbSNP().getDbSnpRsId()));
        dbSnpRsId.setVisible(true);

        TableColumn<VariantAndInterpretationEvidence, String> clinVarAcc = new TableColumn<>("ClinVar Accession\n");
        createTableHeader(clinVarAcc, "ClinVar Accession\n", null ,150d);
        clinVarAcc.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarAcc()));

        TableColumn<VariantAndInterpretationEvidence, String> strand = new TableColumn<>("Strand");
        createTableHeader(strand, "Strand", null ,55.);
        strand.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getStrand()));
        strand.setVisible(false);

        if(panel != null && AnalysisTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> typeExtension = new TableColumn<>("Type Extension");
            createTableHeader(typeExtension, "Type Extension", "variantTypeExtension", 70.);
            typeExtension.getStyleClass().clear();
            typeExtension.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getVariantTypeExtension()));
            typeExtension.setVisible(false);
        }

        TableColumn<VariantAndInterpretationEvidence, String> dbSnpCommonId = new TableColumn<>("dbSNP Common ID");
        createTableHeader(dbSnpCommonId, "dbSNP Common ID", null ,null);
        dbSnpCommonId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getDbSNP().getDbSnpCommonId()));
        dbSnpCommonId.setVisible(false);

        if(panel != null && AnalysisTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> exonBic = new TableColumn<>("Exon (BIC)");
            createTableHeader(exonBic, "Exon (BIC)", null ,null);
            exonBic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getExonNumBic()));
        }

        if ((panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_DNA.getCode()) || (panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_DNA.getCode())))
                && !sample.getSampleSource().equalsIgnoreCase("FFPE")) {
            TableColumn<VariantAndInterpretationEvidence, String> zigosity = new TableColumn<>("Zigosity");
            createTableHeader(zigosity, "Zigosity", null, null);
            zigosity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getZygosity()));
        }

        TableColumn<VariantAndInterpretationEvidence, String> clinVarDisease = new TableColumn<>("ClinVar Disease");
        createTableHeader(clinVarDisease, "ClinVar Disease", null ,null);
        clinVarDisease.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarDisease()));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarTraitOMIM = new TableColumn<>("ClinVar Trait OMIM");
        createTableHeader(clinVarTraitOMIM, "ClinVar Trait OMIM", null, null);
        clinVarTraitOMIM.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarTraitOMIM()));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> g1000All = new TableColumn<>("1KGP All");
        createTableHeader(g1000All, "1KGP All", null ,null);
        g1000All.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getAll())));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> g1000African = new TableColumn<>("1KGP African");
        createTableHeader(g1000African, "1KGP African", null ,null);
        g1000African.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getAfrican())));
        g1000African.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> g1000American = new TableColumn<>("1KGP American");
        createTableHeader(g1000American, "1KGP American", null ,null);
        g1000American.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getAmerican())));
        g1000American.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> g1000EastAsian = new TableColumn<>("1KGP East Asian");
        createTableHeader(g1000EastAsian, "1KGP East Asian", null ,null);
        g1000EastAsian.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getEastAsian())));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> g1000European = new TableColumn<>("1KGP European");
        createTableHeader(g1000European, "1KGP European", null ,null);
        g1000European.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getEuropean())));
        g1000European.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> g1000SouthAsian = new TableColumn<>("1KGP South Asian");
        createTableHeader(g1000SouthAsian, "1KGP South Asian", null ,null);
        g1000SouthAsian.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getSouthAsian())));
        g1000SouthAsian.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> espAll = new TableColumn<>("ESP All");
        createTableHeader(espAll, "ESP All", null ,null);
        espAll.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getEsp6500().getAll())));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> espaa = new TableColumn<>("ESP African American");
        createTableHeader(espaa, "ESP African American", null ,null);
        espaa.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getEsp6500().getAa())));
        espaa.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> espea = new TableColumn<>("ESP European American");
        createTableHeader(espea, "ESP  European American", null ,null);
        espea.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getEsp6500().getEa())));
        espea.setVisible(false);

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> exac = new TableColumn<>("ExAC");
        createTableHeader(exac, "ExAC", null ,null);
        exac.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getExac())));
        if(panel != null && AnalysisTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadAll = new TableColumn<>("gnomAD All");
            createTableHeader(gnomadAll, "gnomAD All", null, null);
            gnomadAll.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getAll())));

            TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadAdmixedAmerican = new TableColumn<>("gnomAD Admixed American");
            createTableHeader(gnomadAdmixedAmerican, "gnomAD Admixed American", null, null);
            gnomadAdmixedAmerican.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getAdmixedAmerican())));
            gnomadAdmixedAmerican.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadAfricanAfricanAmerican = new TableColumn<>("gnomAD African African American");
            createTableHeader(gnomadAfricanAfricanAmerican, "gnomAD African African American", null, null);
            gnomadAfricanAfricanAmerican.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getAfricanAfricanAmerican())));
            gnomadAfricanAfricanAmerican.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadEastAsian = new TableColumn<>("gnomAD East Asian");
            createTableHeader(gnomadEastAsian, "gnomAD EastAsian", null, null);
            gnomadEastAsian.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getAdmixedAmerican())));

            TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadFinnish = new TableColumn<>("gnomAD Finnish");
            createTableHeader(gnomadFinnish, "gnomAD Finnish", null, null);
            gnomadFinnish.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getFinnish())));
            gnomadFinnish.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadNonFinnishEuropean = new TableColumn<>("gnomAD Non Finnish European");
            createTableHeader(gnomadNonFinnishEuropean, "gnomAD Non Finnish European", null, null);
            gnomadNonFinnishEuropean.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getNonFinnishEuropean())));
            gnomadNonFinnishEuropean.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadOthers = new TableColumn<>("gnomAD Others");
            createTableHeader(gnomadOthers, "gnomAD Others", null, null);
            gnomadOthers.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getOthers())));
            gnomadOthers.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadSouthAsian = new TableColumn<>("gnomAD South Asian");
            createTableHeader(gnomadSouthAsian, "gnomAD South Asian", null, null);
            gnomadSouthAsian.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getSouthAsian())));
            gnomadSouthAsian.setVisible(false);
        }
        TableColumn<VariantAndInterpretationEvidence, BigDecimal> koreanReferenceDatabase = new TableColumn<>("Korean Reference Genome Database");
        createTableHeader(koreanReferenceDatabase, "KRGDB", null ,null);
        koreanReferenceDatabase.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getKoreanReferenceGenomeDatabase())));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> koreanExomInformationDatabase = new TableColumn<>("Korean Exom Information Database");
        createTableHeader(koreanExomInformationDatabase, "KoEXID", null ,null);
        koreanExomInformationDatabase.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getKoreanExomInformationDatabase())));
        koreanExomInformationDatabase.setVisible(false);

        if(panel != null && AnalysisTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            TableColumn<VariantAndInterpretationEvidence, String> cosmicOccurrence = new TableColumn<>("COSMIC Occurrence");
            createTableHeader(cosmicOccurrence, "COSMIC Occurrence", null ,null);
            cosmicOccurrence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCosmic().getCosmicOccurrence()));
            cosmicOccurrence.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> cosmicCount = new TableColumn<>("COSMIC Count");
            createTableHeader(cosmicCount, "COSMIC Count", null ,null);
            cosmicCount.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCosmic().getCosmicCount()));
            cosmicCount.setVisible(false);
        }

        if(panel != null && AnalysisTypeCode.GERMLINE.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {

            TableColumn<VariantAndInterpretationEvidence, String> bicCategory = new TableColumn<>("BIC Category");
            createTableHeader(bicCategory, "BIC Category", null ,null);
            bicCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicCategory()));
            bicCategory.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> bicClass = new TableColumn<>("BIC Class");
            createTableHeader(bicClass, "BIC Class", null ,null);
            bicClass.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicClass()));

            TableColumn<VariantAndInterpretationEvidence, String> bicDesignation = new TableColumn<>("BIC Designation");
            createTableHeader(bicDesignation, "BIC Designation", null ,null);
            bicDesignation.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicDesignation()));

            TableColumn<VariantAndInterpretationEvidence, String> bicImportance = new TableColumn<>("BIC Importance");
            createTableHeader(bicImportance, "BIC Importance", null ,null);
            bicImportance.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicImportance()));
            bicImportance.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> bicNt = new TableColumn<>("BIC NT");
            createTableHeader(bicNt, "BIC NT", null ,null);
            bicNt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicNt()));

            TableColumn<VariantAndInterpretationEvidence, BigDecimal> kohbraFrequency = new TableColumn<>("KOHBRA Frequency");
            createTableHeader(kohbraFrequency, "KOHBRA Frequency", null, null);
            kohbraFrequency.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getKohbraFreq())));

            TableColumn<VariantAndInterpretationEvidence, String> kohbraPatient = new TableColumn<>("KOHBRA Patient");
            createTableHeader(kohbraPatient, "KOHBRA Patient", null, null);
            kohbraPatient.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getKohbraPatient()));

            TableColumn<VariantAndInterpretationEvidence, String> beBicCategory = new TableColumn<>("Be BIC Category");
            createTableHeader(beBicCategory, "Be BIC Category", null ,null);
            beBicCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicCategory()));
            beBicCategory.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beBicEthnic = new TableColumn<>("Be BIC Ethnic");
            createTableHeader(beBicEthnic, "Be BIC Ethnic", null ,null);
            beBicEthnic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicNationality()));
            beBicEthnic.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beBicNationality = new TableColumn<>("Be BIC Nationality");
            createTableHeader(beBicNationality, "Be BIC Nationality", null ,null);
            beBicNationality.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicNationality()));
            beBicNationality.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beBicPathogenicity = new TableColumn<>("Be BIC Pathogenicity");
            createTableHeader(beBicPathogenicity, "Be BIC Pathogenicity", null ,null);
            beBicPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicPathogenicity()));
            beBicPathogenicity.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beClinVarMethod = new TableColumn<>("Be ClinVar Method");
            createTableHeader(beClinVarMethod, "Be ClinVar Method", null ,null);
            beClinVarMethod.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarMethod()));
            beClinVarMethod.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beClinVarOrigin = new TableColumn<>("Be ClinVar Origin");
            createTableHeader(beClinVarOrigin, "Be ClinVar Origin", null ,null);
            beClinVarOrigin.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarOrigin()));
            beClinVarOrigin.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beClinVarPathogenicity = new TableColumn<>("Be ClinVar Pathogenicity");
            createTableHeader(beClinVarPathogenicity, "Be ClinVar Pathogenicity", null ,null);
            beClinVarPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarPathogenicity()));
            beClinVarPathogenicity.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beClinVarUpdate = new TableColumn<>("Be ClinVar Update");
            createTableHeader(beClinVarUpdate, "Be ClinVar Update", null ,null);
            beClinVarUpdate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarUpdate()));
            beClinVarUpdate.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beEnigmaCondition = new TableColumn<>("Be ENIGMA Condition");
            createTableHeader(beEnigmaCondition, "Be ENIGMA Condition", null ,null);
            beEnigmaCondition.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaCondition()));
            beEnigmaCondition.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beEnigmaPathogenicity = new TableColumn<>("Be ENIGMA Pathogenicity");
            createTableHeader(beEnigmaPathogenicity, "Be ENIGMA Pathogenicity", null ,null);
            beEnigmaPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaPathogenicity()));
            beEnigmaPathogenicity.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beEnigmaUpdate = new TableColumn<>("Be ENIGMA Update");
            createTableHeader(beEnigmaUpdate, "Be ENIGMA Update", null ,null);
            beEnigmaUpdate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaUpdate()));
            beEnigmaUpdate.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beGene = new TableColumn<>("Be Gene");
            createTableHeader(beGene, "Be Gene", null ,null);
            beGene.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeGene()));
            beGene.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beNt = new TableColumn<>("Be NT");
            createTableHeader(beNt, "Be NT", null ,null);
            beNt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeNt()));
            beNt.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> beTranscript = new TableColumn<>("Be Transcript");
            createTableHeader(beTranscript, "Be Transcript", null ,null);
            beTranscript.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeTranscript()));
            beTranscript.setVisible(false);

            TableColumn<VariantAndInterpretationEvidence, String> enigma = new TableColumn<>("ENIGMA");
            createTableHeader(enigma, "ENIGMA", null ,null);
            enigma.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaPathogenicity()));
            enigma.setVisible(false);

        }

//        TableColumn<VariantAndInterpretationEvidence, String> refGenomeVer = new TableColumn<>("RefGenomeVer");
//        createTableHeader(refGenomeVer, "RefGenomeVer", null ,null);
//        refGenomeVer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getRefGenomeVer()));
//        refGenomeVer.setVisible(false);

//        TableColumn<VariantAndInterpretationEvidence, String> leftSequence = new TableColumn<>("LeftSequence");
//        createTableHeader(leftSequence, "LeftSequence", null ,null);
//        leftSequence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getLeftSequence()));
//        leftSequence.setVisible(false);
//
//        TableColumn<VariantAndInterpretationEvidence, String> rightSequence = new TableColumn<>("RightSequence");
//        createTableHeader(rightSequence, "RightSequence", null ,null);
//        rightSequence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getRightSequence()));
//        rightSequence.setVisible(false);


        /*TableColumn<VariantAndInterpretationEvidence, Integer> variantNum = new TableColumn<>("VariantNum");
        createTableHeader(variantNum, "VariantNum", null ,null);
        variantNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getVariantNum()).asObject());
        variantNum.setVisible(false);*/

        variantListTableView.getStyleClass().clear();
        variantListTableView.getStyleClass().add("table-view");

    }

    class BooleanCell extends TableCell<VariantAndInterpretationEvidence, Boolean> {
        private CheckBox checkBox = new CheckBox();
        private BooleanCell() {
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                VariantAndInterpretationEvidence evidence = BooleanCell.this.getTableView().getItems().get(
                        BooleanCell.this.getIndex());
                evidence.setCheckItem(newValue);
                checkBox.setSelected(newValue);

            });
        }

        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(empty) {
                setGraphic(null);
                return;
            }

            VariantAndInterpretationEvidence evidence = BooleanCell.this.getTableView().getItems().get(
                    BooleanCell.this.getIndex());
            checkBox.setSelected(evidence.getCheckItem());

            setGraphic(checkBox);
        }
    }
}
