package ngeneanalysys.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import ngeneanalysys.code.constants.CommonConstants;
import org.slf4j.Logger;
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

/**
 * @author Jang
 * @since 2018-03-15
 */
public class AnalysisDetailSNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    private CheckBox headerCheckBox;

    @FXML
    private Label reportedCountLabel;

    @FXML
    private CheckBox levelACheckBox;
    @FXML
    private CheckBox levelBCheckBox;
    @FXML
    private CheckBox levelCCheckBox;
    @FXML
    private CheckBox levelDCheckBox;
    @FXML
    private CheckBox levelECheckBox;

    @FXML
    private CheckBox reportCheckBox;
    @FXML
    private CheckBox showFalseVariantsCheckBox;
    @FXML
    private CheckBox commonVariantsCheckBox;

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
    private ComboBox<ComboBoxItem> filterComboBox;

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
    //private Map<String, String> sortMap = new HashMap<>();

    private AnalysisDetailVariantsController variantsController;

    private AnalysisDetailInterpretationController interpretationController;

    private AnalysisDetailVariantStatisticsController statisticsController;

    private AnalysisDetailClinicalSignificantController clinicalSignificantController;

    /** 현재 선택된 변이 정보 객체 */
    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;
    /** 현재 선택된 변이 리스트 객체의 index */

    private boolean rFlag = false;

    private AnalysisDetailSNPsINDELsMemoController memoController;

    private Map<String, List<Object>> filterList = new HashMap<>();

    private Map<String, TableColumn> columnMap = new HashMap<>();

    public void setrFlag(boolean rFlag) {
        this.rFlag = rFlag;
    }

    private final ListChangeListener<TableColumn<VariantAndInterpretationEvidence, ?>> tableColumnListChangeListener =
            c -> Platform.runLater(this::saveColumnInfoToServer);

    private ChangeListener<ComboBoxItem> filterComboBoxValuePropertyChangeListener = (ob, ov, nv) -> {
        if (!nv.equals(ov)) {
            Platform.runLater(() -> showVariantList(0));
        }

        viewAppliedFiltersLabel.setDisable(nv.getValue().equalsIgnoreCase("All"));
        if (nv.getValue().equalsIgnoreCase("All")) {
            viewAppliedFiltersLabel.setOpacity(0);
        } else {
            viewAppliedFiltersLabel.setOpacity(100);
        }
    };

    /**
     * @param variantsController AnalysisDetailVariantsController
     */
    void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    private void checkBoxCheck(CheckBox checkBox, List<Object> filterList, String value) {
        if(checkBox.isSelected() && !filterList.contains(value)) {
            filterList.add(value);
        }
    }

    private void defaultFilterAction(Map<String, List<Object>> list) {
        String key = "search";
        List<Object> filterList;
        if(list.containsKey(key)) {
            filterList = list.get(key);
        } else {
            filterList = new ArrayList<>();
            list.put(key, filterList);
        }
        if(PipelineCode.isBRCAPipeline(panel.getCode()) ||
                PipelineCode.isHeredPipeline(panel.getCode())) {
            checkBoxCheck(levelACheckBox, filterList, "pathogenicity P");
            checkBoxCheck(levelBCheckBox, filterList, "pathogenicity LP");
            checkBoxCheck(levelCCheckBox, filterList, "pathogenicity US");
            checkBoxCheck(levelDCheckBox, filterList, "pathogenicity LB");
            checkBoxCheck(levelECheckBox, filterList, "pathogenicity B");
        } else {
            checkBoxCheck(levelACheckBox, filterList, "tier T1");
            checkBoxCheck(levelBCheckBox, filterList, "tier T2");
            checkBoxCheck(levelCCheckBox, filterList, "tier T3");
            checkBoxCheck(levelDCheckBox, filterList, "tier T4");
        }
        checkBoxCheck(reportCheckBox, filterList, "includedInReport Y");
    }

    private void setStatisticsContents() {
        try {
            // comment tab 화면 출력
            if (statisticsTitledPane.getContent() == null) {
                showVariantStatistics();
            } else if(statisticsController != null) {
                statisticsController.showVariantStatistics();
            }
        } catch (Exception e) {
            logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
            DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    private void setInterpretationContents() {
        try {
            // comment tab 화면 출력
            if (interpretationTitledPane.getContent() == null) {
                showPredictionAndInterpretation();
            } else if(interpretationController != null) {
                interpretationController.contentRefresh();
            }
        } catch (Exception e) {
            logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
            DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    private void setClincialSignificant() {
        try {
            // comment tab 화면 출력
            if (clinicalSignificantTitledPane.getContent() == null) {
                showClinicalSignificant();
            } else if(clinicalSignificantController != null) {
                clinicalSignificantController.refresh();
            }
        } catch (Exception e) {
            logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
            DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    private void setAccordionContents() {
        Task task = new Task() {
            SnpInDelInterpretationLogsList memoList;
            @Override
            protected Object call() throws Exception {
                // Memo 데이터 API 요청
                //Map<String, Object> commentParamMap = new HashMap<>();
                HttpClientResponse responseMemo = apiService.get("/analysisResults/snpInDels/" + selectedAnalysisResultVariant.getSnpInDel().getId()  + "/snpInDelInterpretationLogs", null, null, false);

                // Flagging Comment 데이터 요청이 정상 요청된 경우 진행.
                memoList = responseMemo.getObjectBeforeConvertResponseToJSON(SnpInDelInterpretationLogsList.class);
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> {
                    // comment tab 화면 출력
                    if (interpretationLogsTitledPane.getContent() == null) {
                        showMemoTab(FXCollections.observableList(memoList.getResult()));
                    } else {
                        memoController.updateList(selectedAnalysisResultVariant.getSnpInDel().getId());
                    }
                    memoList = null;
                });
            }

            @Override
            protected void failed() {
                super.failed();
                DialogUtil.showWebApiException(getException(), getMainApp().getPrimaryStage());
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void setCheckBoxFilter() {
        levelACheckBox.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv != null) Platform.runLater(() -> showVariantList(0));
        });

        levelBCheckBox.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv != null) Platform.runLater(() -> showVariantList(0));
        });

        levelCCheckBox.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv != null) Platform.runLater(() ->showVariantList(0));
        });

        levelDCheckBox.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv != null) Platform.runLater(() -> showVariantList(0));
        });

        levelECheckBox.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv != null) Platform.runLater(() -> showVariantList(0));
        });

        reportCheckBox.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv != null) Platform.runLater(() -> showVariantList(0));
        });
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
        if(PipelineCode.isHemePipeline(panel.getCode())) {
            this.filterList = (Map<String, List<Object>>)mainController.getBasicInformationMap().get("hemeFilter");
        } else if(PipelineCode.isSolidPipeline(panel.getCode())) {
            this.filterList = (Map<String, List<Object>>)mainController.getBasicInformationMap().get("solidFilter");
        } else if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode())) {
            this.filterList = (Map<String, List<Object>>)mainController.getBasicInformationMap().get("tstDNAFilter");
        } else if(PipelineCode.isBRCAPipeline(panel.getCode())) {
            this.filterList = (Map<String, List<Object>>)mainController.getBasicInformationMap().get("brcaFilter");
        } else if(PipelineCode.isHeredPipeline(panel.getCode())) {
            this.filterList = (Map<String, List<Object>>)mainController.getBasicInformationMap().get("heredFilter");
        }

        if(!PipelineCode.isHeredPipeline(panel.getCode())) {
            commonVariantsCheckBox.setDisable(true);
            commonVariantsCheckBox.setVisible(false);
        }

        if(AnalysisTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            levelACheckBox.setText("T1");
            levelBCheckBox.setText("T2");
            levelCCheckBox.setText("T3");
            levelDCheckBox.setText("T4");
            levelECheckBox.setVisible(false);
            levelECheckBox.setDisable(true);
        } else {
            levelACheckBox.setText("P");
            levelBCheckBox.setText("LP");
            levelCCheckBox.setText("US");
            levelDCheckBox.setText("LB");
            levelECheckBox.setText("B");
        }
        eventRegistration();

        interpretationLogsTitledPane.expandedProperty().addListener((obs, ov, nv) -> {
            if(nv != null && nv) {
                setAccordionContents();
            }
        });
        interpretationTitledPane.expandedProperty().addListener((obs, ov, nv) -> {
            if(nv != null && nv) {
                setInterpretationContents();
            }
        });
        statisticsTitledPane.expandedProperty().addListener((obs, ov, nv) -> {
            if(nv != null && nv) {
                setStatisticsContents();
            }
        });
        clinicalSignificantTitledPane.expandedProperty().addListener((obs, ov, nv) -> {
            if(nv != null && nv) {
                setClincialSignificant();
            }
        });

        leftSizeButton.setOnMouseClicked(event -> {
            if(leftSizeButton.getStyleClass().contains("btn_fold")){
                foldLeft();
            } else if (leftSizeButton.getStyleClass().get(0) != null){
                expandLeft();
            }
        });

        setDefaultFilter();

        filterComboBox.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            filterComboBox.valueProperty().removeListener(filterComboBoxValuePropertyChangeListener);
            setFilterList();
            filterComboBox.valueProperty().addListener(filterComboBoxValuePropertyChangeListener);
        });

        rightSizeButton.setOnMouseClicked(event -> {
            if(rightSizeButton.getStyleClass().contains("right_btn_fold")){
                foldRight();
            } else if (rightSizeButton.getStyleClass().get(0) != null){
                expandRight();
            }
        });
        variantListTableView.setTableMenuButtonVisible(false);
        // 목록 클릭 시 변이 상세정보 출력 이벤트 바인딩
        variantListTableView.setRowFactory(tv -> {
            TableRow<VariantAndInterpretationEvidence> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                /*if(e.getClickCount() == 1) {
                    setSNVTabName();
                } else */if (e.getClickCount() <= 2) {
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
            }/* else if(keyEvent.getCode().equals(KeyCode.UP) || keyEvent.getCode().equals(KeyCode.DOWN)) {
                setSNVTabName();
            }*/
        });


        setTableViewColumn();
        //runColumnAction();
        setCheckBoxFilter();

        variantsController.getDetailContents().setCenter(root);

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
        Platform.runLater(this::initColumnOrderAndVariantList);
    }

    private List<VariantAndInterpretationEvidence> getSelectedItemList() {
        if(variantListTableView.getItems() == null) {
            return new ArrayList<>();
        }
        return variantListTableView.getItems().stream().filter(VariantAndInterpretationEvidence::getCheckItem)
                .collect(Collectors.toList());
    }

    private void viewCheckAlert() {
        DialogUtil.warning("", "Please select variants to change the value.", mainController.getPrimaryStage(), true);
    }

    private void eventRegistration() {
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
            } else {
                viewCheckAlert();
            }
        });

        if(AnalysisTypeCode.SOMATIC.getDescription().equalsIgnoreCase(panel.getAnalysisType())) {
            changeTierButton.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
                List<VariantAndInterpretationEvidence> selectList = getSelectedItemList();
                if (!selectList.isEmpty()) {
                    try {
                        FXMLLoader loader = getMainApp().load(FXMLConstants.BATCH_CHANGE_TIER);
                        Node node = loader.load();
                        BatchChangeTierDialogController controller = loader.getController();
                        controller.settingItem(sample.getId(), selectList, this,
                                "Modify Tier in multi-selection");
                        controller.setMainController(this.getMainController());
                        controller.setParamMap(getParamMap());
                        controller.show((Parent) node);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    viewCheckAlert();
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
                        controller.settingItem(sample.getId(), selectList, this,
                                "Modify Pathogenicity in multi-selection");
                        controller.setMainController(this.getMainController());
                        controller.setParamMap(getParamMap());
                        controller.show((Parent) node);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    viewCheckAlert();
                }
            });
        }
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
            } else {
                viewCheckAlert();
            }
        });
        showIGVButton.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            if(variantListTableView.getSelectionModel() != null
                    && variantListTableView.getSelectionModel().getSelectedItem() != null) {
                VariantAndInterpretationEvidence variant = variantListTableView.getSelectionModel().getSelectedItem();
                IGVService igvService = IGVService.getInstance();
                igvService.setMainController(getMainController());

                String sampleId = sample.getId().toString();
                String gene = variant.getSnpInDel().getGenomicCoordinate().getGene();
                String locus = String.format("%s:%,d-%,d",
                        variant.getSnpInDel().getGenomicCoordinate().getChromosome(),
                        variant.getSnpInDel().getGenomicCoordinate().getStartPosition(),
                        variant.getSnpInDel().getGenomicCoordinate().getStartPosition());
                String refGenome = variant.getSnpInDel().getGenomicCoordinate().getRefGenomeVer();
                String humanGenomeVersion = (refGenome.contains("hg19")) ? "hg19" : "hg18";

                try {
                    igvService.load(sampleId, sample.getName(), gene, locus , humanGenomeVersion);
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
        Platform.runLater(() -> showVariantList(0));
    }

    private void setDefaultFilter() {
        totalLabel.setText(sample.getAnalysisResultSummary().getAllVariantCount().toString());
        filterComboBox.setConverter(new ComboBoxConverter());
        filterComboBox.getItems().removeAll(filterComboBox.getItems());
        filterComboBox.getItems().add(new ComboBoxItem("All", "All"));
        filterComboBox.getSelectionModel().select(0);
        viewAppliedFiltersLabel.setDisable(true);
    }

    @SuppressWarnings("unchecked")
    private void setSomaticFilterList(String filterName) {
        Map<String, List<Object>> filter = (Map<String, List<Object>>)mainController.getBasicInformationMap().get(filterName);
        Set<String> keySet = filter.keySet();

        if(filterComboBox.getItems().size() > 1) {
            while(filterComboBox.getItems().size() > 1) {
                filterComboBox.getItems().remove(filterComboBox.getItems().size() - 1);
            }
        }

        for(String key : keySet) {
            filterComboBox.getItems().add(new ComboBoxItem(key, key));
        }
        filterList = filter;
    }

    @SuppressWarnings("unchecked")
    private void setGermlineFilterList(String filterName){
        Map<String, List<Object>> filter = (Map<String, List<Object>>)mainController.getBasicInformationMap().get(filterName);
        Set<String> keySet = filter.keySet();

        while(filterComboBox.getItems().size() > 1) {
            filterComboBox.getItems().remove(filterComboBox.getItems().size() - 1);
        }

        for(String key : keySet) {
            filterComboBox.getItems().add(new ComboBoxItem(key, key));
        }
        filterList = filter;
    }

    private void setFilterList() {
        filterComboBox.hide();
        String currentFilterName = filterComboBox.getSelectionModel().getSelectedItem().getText();
        if(PipelineCode.isHemePipeline(panel.getCode())) {
            setSomaticFilterList("hemeFilter");
        } else if (PipelineCode.isSolidPipeline(panel.getCode())) {
            setSomaticFilterList("solidFilter");
        } else if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode())) {
            setSomaticFilterList("tstDNAFilter");
        } else if(PipelineCode.isBRCAPipeline(panel.getCode())) {
            setGermlineFilterList("brcaFilter");
        } else if(PipelineCode.isHeredPipeline(panel.getCode())) {
            setGermlineFilterList("heredFilter");
        }

        Optional<ComboBoxItem> optionalComboBoxItem = filterComboBox.getItems().stream().filter(item
                -> item.getText().equalsIgnoreCase(currentFilterName)).findFirst();
        optionalComboBoxItem.ifPresent(comboBoxItem -> filterComboBox.getSelectionModel().select(comboBoxItem));
        filterComboBox.show();
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
        if(rFlag) {
            Platform.runLater(() -> showVariantList(0));
            rFlag = false;
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
            this.statisticsController = variantStatisticsController;
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
            this.interpretationController = controller;
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
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                paramMap.put("variant", analysisResultVariant);

                HttpClientResponse response = apiService.get(
                        "/analysisResults/snpInDels/" + analysisResultVariant.getSnpInDel().getId() + "/snpInDelExtraInfos", null, null, false);

                List<SnpInDelExtraInfo> item = (List<SnpInDelExtraInfo>)response.getMultiObjectBeforeConvertResponseToJSON(SnpInDelExtraInfo.class, false);
                paramMap.put("detail", item);
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> {
                    showDetailTab();
                    if(panel.getAnalysisType().equalsIgnoreCase(AnalysisTypeCode.SOMATIC.getDescription())) {
                        overviewAccordion.getPanes().remove(clinicalSignificantTitledPane);
                    } else {
                        overviewAccordion.getPanes().remove(interpretationTitledPane);
                    }
                    overviewAccordion.setExpandedPane(variantDetailTitledPane);
                });
            }

            @Override
            protected void failed() {
                super.failed();
                DialogUtil.showWebApiException(getException(), getMainApp().getPrimaryStage());
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void showClinicalSignificant() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_CLINICAL_SIGNIFICANT);
            Node node = loader.load();
            AnalysisDetailClinicalSignificantController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(getParamMap());
            controller.setController(this);
            controller.show((Parent) node);
            clinicalSignificantController = controller;
            clinicalSignificantTitledPane.setContent(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFilterItem(Map<String, List<Object>> list) {
        ComboBoxItem comboBoxItem = filterComboBox.getSelectionModel().getSelectedItem();
        if(comboBoxItem != null && filterList.containsKey(comboBoxItem.getValue())) {
            list.put("search", new ArrayList<>(filterList.get(comboBoxItem.getValue())));
        }
        defaultFilterAction(list);
        if(!showFalseVariantsCheckBox.isSelected()) {
            setIsFalseItemToN(list);
        }
        if(!commonVariantsCheckBox.isSelected()) {
            setCommonVariantsItemToN(list);
        }
    }

    private void setIsFalseItemToN(Map<String, List<Object>> list) {
        if(list.containsKey("search")) {
            list.get("search").add("isFalse " + "N");
        } else {
            List<Object> searchList = new ArrayList<>();
            searchList.add("isFalse " + "N");
            list.put("search", searchList);
        }
    }

    private void setCommonVariantsItemToN(Map<String, List<Object>> list) {
        if(list.containsKey("search")) {
            list.get("search").add("commonVariants N");
        } else {
            List<Object> searchList = new ArrayList<>();
            searchList.add("commonVariants N");
            list.put("search", searchList);
        }
    }

    public void showVariantList(int selectedIdx) {
        headerCheckBox.setSelected(false);
        // API 서버 조회
        Map<String, Object> params = new HashMap<>();
        Map<String, List<Object>> sortAndSearchItem = new HashMap<>();

        setFilterItem(sortAndSearchItem);
        Task<Void> task = new Task<Void>() {

            private PagedVariantAndInterpretationEvidence analysisResultVariantList;
            private List<VariantAndInterpretationEvidence> list;
            private int totalCount;
            @Override
            protected Void call() throws Exception {
                HttpClientResponse response1;
                HttpClientResponse response2;

                response1 = apiService.get("/analysisResults/sampleSnpInDels/"+ sample.getId(), params,
                        null, sortAndSearchItem);
                analysisResultVariantList =
                        response1.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

                list = analysisResultVariantList.getResult();
                totalCount = analysisResultVariantList.getCount();

                response2 = apiService.get("/analysisResults/sampleSummary/"+ sample.getId(), null, null, false);

                sample.setAnalysisResultSummary(response2.getObjectBeforeConvertResponseToJSON(AnalysisResultSummary.class));
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> {
                    mainController.setMainMaskerPane(false);
                    foldRight();
                    if (variantListTableView.getItems() != null) variantListTableView.getItems().clear();
                    searchCountLabel.setText(totalCount +"/");

                    ObservableList<VariantAndInterpretationEvidence> displayList = null;
                    reportedCountLabel.setText("(R : " + sample.getAnalysisResultSummary().getReportVariantCount() +")");

                    if (list != null && !list.isEmpty()) {
                        displayList = FXCollections.observableArrayList(list);
                    }
                    variantListTableView.setItems(displayList);

                    // 화면 출력
                    if (displayList != null && displayList.size() > 0) {
                        variantListTableView.getSelectionModel().select(selectedIdx);
                    }
                    //setSNVTabName();
                });
            }

            @Override
            protected void failed() {
                super.failed();
                mainController.setMainMaskerPane(false);
                try {
                    throw new Exception(getException());
                } catch (WebAPIException wae) {
                    variantListTableView.setItems(null);
                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                            getMainApp().getPrimaryStage(), true);
                    wae.printStackTrace();
                } catch (Exception e) {
                    logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
                    variantListTableView.setItems(null);
                    DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), true);
                }
            }
        };
        mainController.setMainMaskerPane(true);
        Thread thread = new Thread(task);
        thread.start();
    }

    void comboBoxSetAll() {
        filterComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void resetTableColumnOrder() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    apiService.delete("/member/memberOption/" + getColumnOrderType());
                } catch (WebAPIException wae) {
                    if (wae.getResponse() == null || wae.getResponse().getStatus() != 404) {
                        throw wae;
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                setDefaultTableColumnOrder(new ResourceUtil().getDefaultColumnOrderResourcePath(panel));
                mainController.setContentsMaskerPaneVisible(false);
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @FXML
    public void excelDownload() {
        fileDownload("EXCEL");
    }

    @FXML
    public void csvDownload() {
        fileDownload("CSV");
    }

    private void fileDownload(String fileType) {
        Map<String, Object> params = new HashMap<>();
        Map<String, List<Object>> filterList = new HashMap<>();
        setFilterItem(filterList);
        if(PipelineCode.isBRCAPipeline(panel.getCode())) {
            params.put("exportFields", "enigmaPathogenicity,acmg,acmgCriteria," +getExportFields());
        } else if(PipelineCode.isHeredPipeline(panel.getCode())) {
            params.put("exportFields", "acmg,acmgCriteria," + getExportFields());
        } else {
            params.put("exportFields", "evidence," + getExportFields());
        }
        WorksheetUtil worksheetUtil = new WorksheetUtil();
        worksheetUtil.exportSampleData(fileType, filterList, params, this.getMainApp(), sample);
    }

    private String getExportFields() {
        if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_CMC_DNA.getCode())) {
            return variantListTableView.getColumns().stream().filter(TableColumn::isVisible)
                    .filter(c -> c.getId() != null && c.getWidth() > 0)
                    .map(TableColumn::getId)
                    .map(v -> {
                        if(v.equals("ntChange")) {
                            return "ntChangeCMC";
                        }
                        return v;
                    })
                    .collect(Collectors.joining(","));
        }
        return variantListTableView.getColumns().stream().filter(TableColumn::isVisible)
                .filter(c -> c.getId() != null && c.getWidth() > 0)
                .map(TableColumn::getId).collect(Collectors.joining(","));
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
            variantFilterController.setPanel(panel);
            variantFilterController.show((Parent) node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void viewAppliedFilters() {
        viewAppliedFiltersLabel.setOpacity(100);
        ComboBoxItem comboBoxItem = filterComboBox.getSelectionModel().getSelectedItem();
        PopOverUtil.openFilterPopOver(viewAppliedFiltersLabel, filterList.get(comboBoxItem.getValue()));
    }

    private void   createTableHeader(TableColumn<VariantAndInterpretationEvidence, ?> column, String name, String tooltipName,
                                     Double size, String id) {
        Label label = new Label(name);
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getStyleClass().add("font_size_11");
        MenuItem moveToEndMenuItem = new MenuItem("Move to end");
        MenuItem moveToFrontMenuItem = new MenuItem("Move to front");
        contextMenu.getItems().add(moveToEndMenuItem);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().add(moveToFrontMenuItem);

        moveToEndMenuItem.setOnAction(e -> {
            variantListTableView.getColumns().remove(column);
            variantListTableView.getColumns().add(column);
        });
        moveToFrontMenuItem.setOnAction(e -> {
            variantListTableView.getColumns().remove(column);
            variantListTableView.getColumns().add(1, column);
        });


        label.setContextMenu(contextMenu);
        if(StringUtils.isNotEmpty(tooltipName)) label.setTooltip(new Tooltip(tooltipName));
        label.setPrefHeight(Double.MAX_VALUE);
        column.setGraphic(label);

        if(id != null) column.setId(id);
        //column.setMinWidth(50.0);
        column.widthProperty().addListener((ob, ov, nv) -> label.setMinWidth(column.getWidth()));

        if(size != null) column.setPrefWidth(size);
        columnMap.put(name, column);
    }

    private void createCheckBoxTableHeader(TableColumn<VariantAndInterpretationEvidence, ?> column) {
        HBox hBox = new HBox();
        hBox.setPrefHeight(Double.MAX_VALUE);
        hBox.setAlignment(Pos.CENTER);
        CheckBox box = new CheckBox();
        headerCheckBox = box;
        hBox.getChildren().add(box);
        column.setStyle("-fx-alignment : center");
        column.setSortable(false);
        column.setGraphic(box);

        box.selectedProperty().addListener((observable, ov, nv) -> {
            if(variantListTableView.getItems() != null) {
                variantListTableView.getItems().forEach(item -> item.setCheckItem(nv));
                variantListTableView.refresh();
            }
        });

        column.widthProperty().addListener((ob, ov, nv) -> hBox.setMinWidth(column.getWidth()));
        column.setResizable(false);

        column.setPrefWidth(30d);

        variantListTableView.getColumns().add(column);
    }

    private void setTableViewColumn() {
        variantListTableView.getColumns().removeListener(tableColumnListChangeListener);
        String centerStyleClass = "alignment_center";

        TableColumn<VariantAndInterpretationEvidence, Boolean> checkBoxColumn = new TableColumn<>("");
        createCheckBoxTableHeader(checkBoxColumn);
        //checkBoxColumn.impl_setReorderable(false); 컬럼 이동 방지 코드
        checkBoxColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue() != null ));
        checkBoxColumn.setCellFactory(param -> new BooleanCell());
        double predictionColumnSize = 30d;
        String columnName = SnvTableColumnCode.PATHOGENICITY.getName();
        if(panel.getAnalysisType().equals(AnalysisTypeCode.SOMATIC.getDescription())) {
            columnName = SnvTableColumnCode.TIER.getName();
        }
        TableColumn<VariantAndInterpretationEvidence, String> predictionColumn = new TableColumn<>(columnName);
        createTableHeader(predictionColumn, columnName, columnName, predictionColumnSize, columnName.toLowerCase());
        if (panel.getAnalysisType().equals(AnalysisTypeCode.SOMATIC.getDescription())) {
            predictionColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(
                            cellData.getValue().getSnpInDel().getExpertTier() != null
                                    ? cellData.getValue().getSnpInDel().getExpertTier()
                                    : cellData.getValue().getSnpInDel().getSwTier()
                    )
            );
        } else {
            predictionColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(
                            cellData.getValue().getSnpInDel().getExpertPathogenicity() != null
                                    ? cellData.getValue().getSnpInDel().getExpertPathogenicity()
                                    : cellData.getValue().getSnpInDel().getSwPathogenicity()
                    )
            );
        }
        predictionColumn.setComparator((o1, o2) -> {
            if (o1.equals(VariantLevelCode.TIER_ONE.getAlias())
                    || o1.equals(VariantLevelCode.TIER_TWO.getAlias())
                    || o1.equals(VariantLevelCode.TIER_THREE.getAlias())
                    || o1.equals(VariantLevelCode.TIER_FOUR.getAlias())) {
                return o1.compareTo(o2);
            } else {
                String o1Level = VariantLevelCode.getPathogenicityCodeFromAlias(o1);
                String o2Level  = VariantLevelCode.getPathogenicityCodeFromAlias(o2);
                return o1Level.compareTo(o2Level);
            }
        });
        predictionColumn.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
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
                            code = "tier_" + VariantLevelCode.getCodeFromAlias(value);
                        } else {
                            value = variant.getSnpInDel().getExpertTier();
                            code = "user_tier_" + VariantLevelCode.getCodeFromAlias(value);
                        }

                    } else {
                        if(StringUtils.isEmpty(variant.getSnpInDel().getExpertPathogenicity())) {
                            value = variant.getSnpInDel().getSwPathogenicity();
                            code = "prediction_" + PredictionTypeCode.getCodeFromAlias(value);
                        } else {
                            value = variant.getSnpInDel().getExpertPathogenicity();
                            code = "user_prediction_" + PredictionTypeCode.getCodeFromAlias(value);
                        }
                    }
                    Label label = null;
                    if(!"NONE".equals(code)) {
                        label = new Label(value);
                        label.getStyleClass().clear();
                        predictionColumn.getStyleClass().add(centerStyleClass);
                        label.getStyleClass().add(code);
                    }
                    setGraphic(label);
                }
            }
        });

        TableColumn<VariantAndInterpretationEvidence, String> warn = new TableColumn<>(SnvTableColumnCode.WARNING.getName());
        createTableHeader(warn, SnvTableColumnCode.WARNING.getName(), "Warning" , 30., SnvTableColumnCode.WARNING.getId());
        warn.getStyleClass().add(centerStyleClass);
        warn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getHasWarning()));
        warn.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                setGraphic((StringUtils.isNotEmpty(item)) ? SNPsINDELsList.getWarningReasonPopOver(item, panel) : null);
            }
        });

        TableColumn<VariantAndInterpretationEvidence, String> falsePositive = new TableColumn<>(SnvTableColumnCode.FALSE.getName());
        createTableHeader(falsePositive, SnvTableColumnCode.FALSE.getName(), "",55., SnvTableColumnCode.FALSE.getId());
        falsePositive.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getIsFalse()));
        falsePositive.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                Label label = null;
                getStyleClass().add(centerStyleClass);
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

        showFalseVariantsCheckBox.addEventFilter(MouseEvent.MOUSE_CLICKED, ev -> {

            if(showFalseVariantsCheckBox.isSelected()) {
                falsePositive.setMinWidth(40);
                falsePositive.setMaxWidth(40);
                falsePositive.setPrefWidth(40);
            } else {
                falsePositive.setMinWidth(0);
                falsePositive.setMaxWidth(0);
                falsePositive.setPrefWidth(0);
            }
            Platform.runLater(() -> showVariantList(0));
        });

        falsePositive.setPrefWidth(0);
        falsePositive.setMinWidth(0);
        falsePositive.setMaxWidth(0);

        TableColumn<VariantAndInterpretationEvidence, String> reportTest = new TableColumn<>(SnvTableColumnCode.REPORT.getName());
        createTableHeader(reportTest, SnvTableColumnCode.REPORT.getName(), "Report", 30., SnvTableColumnCode.REPORT.getId());
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
                Label label = new Label();
                label.getStyleClass().remove("label");
                if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
                    label.setText("R");
                    label.getStyleClass().add("report_check");
                } else {
                    label.getStyleClass().add("report_uncheck");
                }
                label.setCursor(Cursor.HAND);
                label.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
                    if (ev.getClickCount() == 1) {
                        String changeReportText = "Y".equals(item)
                                ? "Do you want to exclude the selected variant from the report?"
                                : "Do you want to include the selected variant in your report?";
                        Alert alert = DialogUtil.generalShow(Alert.AlertType.CONFIRMATION, "Report", changeReportText, getMainApp().getPrimaryStage(), true);
                        if(alert.getResult() == ButtonType.OK) {
                            Task<Void> task = new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
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
                                    return null;
                                }

                                @Override
                                protected void succeeded() {
                                    super.succeeded();
                                    refreshTable();
                                }

                                @Override
                                protected void failed() {
                                    super.failed();
                                    getMainController().setMainMaskerPane(false);
                                    DialogUtil.showWebApiException(getException(), getMainApp().getPrimaryStage());
                                }
                            };
                            getMainController().setMainMaskerPane(true);
                            Thread thread = new Thread(task);
                            thread.start();
                        }
                    }
                });
                setGraphic(label);
            }
        });

        TableColumn<VariantAndInterpretationEvidence, String> gene = new TableColumn<>(SnvTableColumnCode.GENE.getName());
        createTableHeader(gene, SnvTableColumnCode.GENE.getName(), "" ,null, SnvTableColumnCode.GENE.getId());
        gene.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        gene.setCellFactory(column ->
                new TableCell<VariantAndInterpretationEvidence, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item == null || empty) {
                            setText(null);
                        } else {
                            setText(item);
                            if(panel.getVariantFilter() != null
                                    && StringUtils.isNotEmpty(panel.getVariantFilter().getEssentialGenes())) {
                                if(Arrays.stream(panel.getVariantFilter().getEssentialGenes().split(",")).anyMatch(
                                        gene -> gene.equalsIgnoreCase(item))) {
                                    setTextFill(Color.RED);
                                } else {
                                    setTextFill(Color.BLACK);
                                }
                            }
                        }
                    }
                }
        );

        TableColumn<VariantAndInterpretationEvidence, String> transcriptAccession = new TableColumn<>(SnvTableColumnCode.TRANSCRIPT_ACCESSION.getName());
        createTableHeader(transcriptAccession, SnvTableColumnCode.TRANSCRIPT_ACCESSION.getName(), null ,null, SnvTableColumnCode.TRANSCRIPT_ACCESSION.getId());
        transcriptAccession.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getTranscriptAccession()));

        TableColumn<VariantAndInterpretationEvidence, String> proteinAccession = new TableColumn<>(SnvTableColumnCode.PROTEIN_ACCESSION.getName());
        createTableHeader(proteinAccession, SnvTableColumnCode.PROTEIN_ACCESSION.getName(), null ,null, SnvTableColumnCode.PROTEIN_ACCESSION.getId());
        proteinAccession.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getProteinAccession()));

        TableColumn<VariantAndInterpretationEvidence, String> type = new TableColumn<>(SnvTableColumnCode.TYPE.getName());
        createTableHeader(type, SnvTableColumnCode.TYPE.getName(), "" ,null, SnvTableColumnCode.TYPE.getId());
        type.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getVariantType()));

        TableColumn<VariantAndInterpretationEvidence, String> codCons = new TableColumn<>(SnvTableColumnCode.CONSEQUENCE.getName());
        createTableHeader(codCons, SnvTableColumnCode.CONSEQUENCE.getName(), null ,140., SnvTableColumnCode.CONSEQUENCE.getId());
        codCons.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getCodingConsequence()));

        TableColumn<VariantAndInterpretationEvidence, String> ntChange = new TableColumn<>(SnvTableColumnCode.NT_CHANGE.getName());
        createTableHeader(ntChange, SnvTableColumnCode.NT_CHANGE.getName(), null ,160., SnvTableColumnCode.NT_CHANGE.getId());
        if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_CMC_DNA.getCode())) {
            ntChange.setCellValueFactory(cellData -> {
                if(cellData.getValue().getSnpInDel().getGenomicCoordinate().getStrand().equals("-")) {
                    return new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange() + "(" + ntChangeReverse(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()) + ")");
                } else {
                    return new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange());
                }
            });
        } else {
            ntChange.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange());
            });
        }

        TableColumn<VariantAndInterpretationEvidence, String> ntChangeBIC = new TableColumn<>(SnvTableColumnCode.NT_CHANGE_BIC.getName());
        createTableHeader(ntChangeBIC, SnvTableColumnCode.NT_CHANGE_BIC.getName(), null ,140., SnvTableColumnCode.NT_CHANGE_BIC.getId());
        ntChangeBIC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().createNtChangeBRCA()));

        TableColumn<VariantAndInterpretationEvidence, String> aaChange = new TableColumn<>(SnvTableColumnCode.AA_CHANGE.getName());
        createTableHeader(aaChange, SnvTableColumnCode.AA_CHANGE.getName(), null ,140., SnvTableColumnCode.AA_CHANGE.getId());
        aaChange.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChange()));

        TableColumn<VariantAndInterpretationEvidence, String> aaChangeConversion = new TableColumn<>(SnvTableColumnCode.AA_CHANGE_SINGLE.getName());
        createTableHeader(aaChangeConversion, SnvTableColumnCode.AA_CHANGE_SINGLE.getName(), null ,140., SnvTableColumnCode.AA_CHANGE_SINGLE.getId());
        aaChangeConversion.setCellValueFactory(cellData -> cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChangeSingleLetter() == null ?
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChangeConversion()) :
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChangeSingleLetter()));

        TableColumn<VariantAndInterpretationEvidence, String> chr = new TableColumn<>(SnvTableColumnCode.CHROMOSOME.getName());
        createTableHeader(chr, SnvTableColumnCode.CHROMOSOME.getName(), "" ,null, SnvTableColumnCode.CHROMOSOME.getId());
        chr.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getChromosome()));

        TableColumn<VariantAndInterpretationEvidence, Integer> genomicCoordinate = new TableColumn<>(SnvTableColumnCode.START_POSITION.getName());
        genomicCoordinate.setStyle(genomicCoordinate.getStyle() + "-fx-alignment : baseline-right;");
        createTableHeader(genomicCoordinate, SnvTableColumnCode.START_POSITION.getName(), "" ,null, SnvTableColumnCode.START_POSITION.getId());
        genomicCoordinate.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getStartPosition()).asObject());
        genomicCoordinate.setCellFactory(column ->
                new TableCell<VariantAndInterpretationEvidence, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        setStyle(getStyle() + "; -fx-alignment : baseline-right;");
                        if(item == null || empty) {
                            setText(null);
                        } else {
                            VariantAndInterpretationEvidence evidence = this.getTableView().getItems().get(this.getIndex());
                            setText(item.toString());
                            if(evidence != null && StringUtils.isNotEmpty(evidence.getSnpInDel().getWarningReason())
                                    && evidence.getSnpInDel().getWarningReason().contains("consecutive_variants")) {
                                setTextFill(Color.RED);
                            } else {
                                setTextFill(Color.BLACK);
                            }
                        }
                    }
                }
        );

        TableColumn<VariantAndInterpretationEvidence, String> ref = new TableColumn<>(SnvTableColumnCode.REF.getName());
        createTableHeader(ref, SnvTableColumnCode.REF.getName(), null ,null, SnvTableColumnCode.REF.getId());
        ref.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getRefSequence()));

        TableColumn<VariantAndInterpretationEvidence, String> alt = new TableColumn<>(SnvTableColumnCode.ALT.getName());
        createTableHeader(alt, SnvTableColumnCode.ALT.getName(), null ,null, SnvTableColumnCode.ALT.getId());
        alt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAltSequence()));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> fraction = new TableColumn<>(SnvTableColumnCode.FRACTION.getName());
        createTableHeader(fraction, SnvTableColumnCode.FRACTION.getName(), "" ,null, SnvTableColumnCode.FRACTION.getId());
        fraction.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()));
        fraction.setCellFactory(column ->
                new TableCell<VariantAndInterpretationEvidence, BigDecimal>() {
                    @Override
                    protected void updateItem(BigDecimal item, boolean empty) {
                        super.updateItem(item, empty);
                        setStyle(getStyle() + "; -fx-alignment : baseline-right;");
                        if(item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.toString());
                            if(PipelineCode.isBRCAPipeline(panel.getCode()) || PipelineCode.isHeredPipeline(panel.getCode())) {
                                double doubleVal = item.doubleValue();
                                if ((doubleVal >= 5 && doubleVal <= 40) || (doubleVal >= 60 && doubleVal <= 95)) {
                                    setTextFill(Color.RED);
                                } else {
                                    setTextFill(Color.BLACK);
                                }
                            } else {
                                setTextFill(Color.BLACK);
                            }
                        }
                    }
                }
        );

        TableColumn<VariantAndInterpretationEvidence, Integer> depth = new TableColumn<>(SnvTableColumnCode.DEPTH.getName());
        depth.setStyle(depth.getStyle() + "-fx-alignment : baseline-right;");
        createTableHeader(depth, SnvTableColumnCode.DEPTH.getName(), "" ,null, SnvTableColumnCode.DEPTH.getId());
        depth.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getReadInfo().getReadDepth()).asObject());

        TableColumn<VariantAndInterpretationEvidence, Integer> refNum = new TableColumn<>(SnvTableColumnCode.REF_COUNT.getName());
        refNum.setStyle(refNum.getStyle() + "-fx-alignment : baseline-right;");
        createTableHeader(refNum, SnvTableColumnCode.REF_COUNT.getName(), "" ,null, SnvTableColumnCode.REF_COUNT.getId());
        refNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getReadInfo().getRefReadNum()).asObject());

        TableColumn<VariantAndInterpretationEvidence, Integer> altNum = new TableColumn<>(SnvTableColumnCode.ALT_COUNT.getName());
        createTableHeader(altNum, SnvTableColumnCode.ALT_COUNT.getName(), "" ,null, SnvTableColumnCode.ALT_COUNT.getId());
        altNum.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSnpInDel().getReadInfo().getAltReadNum()).asObject());
        altNum.setCellFactory(column ->
                new TableCell<VariantAndInterpretationEvidence, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        setStyle(getStyle() + "; -fx-alignment : baseline-right;");
                        if(item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.toString());
                            if(item <= 6) {
                                setTextFill(Color.RED);
                            } else {
                                setTextFill(Color.BLACK);
                            }
                        }
                    }
                }
        );

        TableColumn<VariantAndInterpretationEvidence, String> dbSnpRsId = new TableColumn<>(SnvTableColumnCode.DBSNP_ID.getName());
        createTableHeader(dbSnpRsId, SnvTableColumnCode.DBSNP_ID.getName(), null ,null, SnvTableColumnCode.DBSNP_ID.getId());
        dbSnpRsId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getDbSNP().getDbSnpRsId()));

        TableColumn<VariantAndInterpretationEvidence, String> exon = new TableColumn<>(SnvTableColumnCode.EXON.getName());
        createTableHeader(exon, SnvTableColumnCode.EXON.getName(), null ,null, SnvTableColumnCode.EXON.getId());
        exon.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getExonNum()));

        TableColumn<VariantAndInterpretationEvidence, String> cosmicIds = new TableColumn<>(SnvTableColumnCode.COSMIC_ID.getName());
        createTableHeader(cosmicIds, SnvTableColumnCode.COSMIC_ID.getName(), null ,null, SnvTableColumnCode.COSMIC_ID.getId());
        cosmicIds.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCosmic().getCosmicIds()));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarAcc = new TableColumn<>(SnvTableColumnCode.CLINVAR_SUBMITTED_ACCESSION.getName());
        createTableHeader(clinVarAcc, SnvTableColumnCode.CLINVAR_SUBMITTED_ACCESSION.getName(), null ,150d, SnvTableColumnCode.CLINVAR_SUBMITTED_ACCESSION.getId());
        clinVarAcc.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarAcc()));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarClass = new TableColumn<>(SnvTableColumnCode.CLINVAR_SUBMITTED_CLASS.getName());
        createTableHeader(clinVarClass, SnvTableColumnCode.CLINVAR_SUBMITTED_CLASS.getName(), null ,150d, SnvTableColumnCode.CLINVAR_SUBMITTED_CLASS.getId());
        clinVarClass.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarClass()));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarReviewStatus = new TableColumn<>(SnvTableColumnCode.CLINVAR_REVIEW_STATUS.getName());
        createTableHeader(clinVarReviewStatus, SnvTableColumnCode.CLINVAR_REVIEW_STATUS.getName(), null ,150d, SnvTableColumnCode.CLINVAR_REVIEW_STATUS.getId());
        clinVarReviewStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarReviewStatus()));
        clinVarReviewStatus.setCellFactory(cell -> new TableCell<VariantAndInterpretationEvidence, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null || empty) {
                    setText(null);
                    setTooltip(null);
                } else {
                    String[] text = item.split(":");
                    if(text.length >= 2) {
                        setText(text[0]);
                        setTooltip(new Tooltip(text[1]));
                    } else {
                        setText(item);
                        setTooltip(null);
                    }
                }
            }
        });

        TableColumn<VariantAndInterpretationEvidence, String> clinVarDisease = new TableColumn<>(SnvTableColumnCode.CLINVAR_SUBMITTED_DISEASE.getName());
        createTableHeader(clinVarDisease, SnvTableColumnCode.CLINVAR_SUBMITTED_DISEASE.getName(), null ,150d, SnvTableColumnCode.CLINVAR_SUBMITTED_DISEASE.getId());
        clinVarDisease.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarDisease()));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarDrugResponse = new TableColumn<>(SnvTableColumnCode.CLINVAR_CONDITION.getName());
        createTableHeader(clinVarDrugResponse, SnvTableColumnCode.CLINVAR_CONDITION.getName(), null ,150d, SnvTableColumnCode.CLINVAR_CONDITION.getId());
        clinVarDrugResponse.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarDrugResponse()));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarTraitOMIM = new TableColumn<>(SnvTableColumnCode.CLINVAR_SUBMITTED_OMIM.getName());
        createTableHeader(clinVarTraitOMIM, SnvTableColumnCode.CLINVAR_SUBMITTED_OMIM.getName(), null, null, SnvTableColumnCode.CLINVAR_SUBMITTED_OMIM.getId());
        clinVarTraitOMIM.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarTraitOMIM()));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> g1000All = new TableColumn<>(SnvTableColumnCode.KGP_ALL.getName());
        createTableHeader(g1000All, SnvTableColumnCode.KGP_ALL.getName(), "1000 genomes : All" ,null, SnvTableColumnCode.KGP_ALL.getId());
        g1000All.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getAll())));
        g1000All.setCellFactory(cell -> new PopTableCell("g1000.all"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> g1000African = new TableColumn<>(SnvTableColumnCode.KGP_AFR.getName());
        createTableHeader(g1000African, SnvTableColumnCode.KGP_AFR.getName(), "1000 genomes : African" ,null, SnvTableColumnCode.KGP_AFR.getId());
        g1000African.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getAfrican())));
        g1000African.setCellFactory(cell -> new PopTableCell("g1000.african"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> g1000American = new TableColumn<>(SnvTableColumnCode.KGP_AMR.getName());
        createTableHeader(g1000American, SnvTableColumnCode.KGP_AMR.getName(), "1000 genomes : American" ,null, SnvTableColumnCode.KGP_AMR.getId());
        g1000American.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getAmerican())));
        g1000American.setCellFactory(cell -> new PopTableCell("g1000.american"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> g1000EastAsian = new TableColumn<>(SnvTableColumnCode.KGP_EAS.getName());
        createTableHeader(g1000EastAsian, SnvTableColumnCode.KGP_EAS.getName(), "1000 genomes : East Asian" ,null, SnvTableColumnCode.KGP_EAS.getId());
        g1000EastAsian.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getEastAsian())));
        g1000EastAsian.setCellFactory(cell -> new PopTableCell("g1000.eastAsian"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> g1000European = new TableColumn<>(SnvTableColumnCode.KGP_EUR.getName());
        createTableHeader(g1000European, SnvTableColumnCode.KGP_EUR.getName(), "1000 genomes : European" ,null, SnvTableColumnCode.KGP_EUR.getId());
        g1000European.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getEuropean())));
        g1000European.setCellFactory(cell -> new PopTableCell("g1000.european"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> g1000SouthAsian = new TableColumn<>(SnvTableColumnCode.KGP_SAS.getName());
        createTableHeader(g1000SouthAsian, SnvTableColumnCode.KGP_SAS.getName(), "1000 genomes : South Asian" ,null, SnvTableColumnCode.KGP_SAS.getId());
        g1000SouthAsian.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getG1000().getSouthAsian())));
        g1000SouthAsian.setCellFactory(cell -> new PopTableCell("g1000.southAsian"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> espAll = new TableColumn<>(SnvTableColumnCode.ESP_ALL.getName());
        createTableHeader(espAll, SnvTableColumnCode.ESP_ALL.getName(), "Exome Sequencing Project : All" ,null, SnvTableColumnCode.ESP_ALL.getId());
        espAll.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getEsp6500().getAll())));
        espAll.setCellFactory(cell -> new PopTableCell("esp6500.all"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> espaa = new TableColumn<>(SnvTableColumnCode.ESP_AA.getName());
        createTableHeader(espaa, SnvTableColumnCode.ESP_AA.getName(), "ESP : African American" ,null, SnvTableColumnCode.ESP_AA.getId());
        espaa.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getEsp6500().getAa())));
        espaa.setCellFactory(cell -> new PopTableCell("esp6500.aa"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> espea = new TableColumn<>(SnvTableColumnCode.ESP_EA.getName());
        createTableHeader(espea, SnvTableColumnCode.ESP_EA.getName(), "ESP : European American" ,null, SnvTableColumnCode.ESP_EA.getId());
        espea.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getEsp6500().getEa())));
        espea.setCellFactory(cell -> new PopTableCell("esp6500.ea"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> exac = new TableColumn<>(SnvTableColumnCode.EXAC.getName());
        createTableHeader(exac, SnvTableColumnCode.EXAC.getName(), "Exome Aggregation Consortium : All" ,null, SnvTableColumnCode.EXAC.getId());
        exac.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getExac())));
        exac.setCellFactory(cell -> new PopTableCell("exac"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadAll = new TableColumn<>(SnvTableColumnCode.GNOMAD_ALL.getName());
        createTableHeader(gnomadAll, SnvTableColumnCode.GNOMAD_ALL.getName(), "gnomAD : All", null, SnvTableColumnCode.GNOMAD_ALL.getId());
        gnomadAll.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getAll())));
        gnomadAll.setCellFactory(cell -> new PopTableCell("gnomAD.all"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadAdmixedAmerican = new TableColumn<>(SnvTableColumnCode.GNOMAD_AMR.getName());
        createTableHeader(gnomadAdmixedAmerican, SnvTableColumnCode.GNOMAD_AMR.getName(), "gnomAD : Admixed American", null, SnvTableColumnCode.GNOMAD_AMR.getId());
        gnomadAdmixedAmerican.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getAdmixedAmerican())));
        gnomadAdmixedAmerican.setCellFactory(cell -> new PopTableCell("gnomAD.admixedAmerican"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadAfricanAfricanAmerican = new TableColumn<>(SnvTableColumnCode.GNOMAD_AFR.getName());
        createTableHeader(gnomadAfricanAfricanAmerican, SnvTableColumnCode.GNOMAD_AFR.getName(), "gnomAD : African, African American", null, SnvTableColumnCode.GNOMAD_AFR.getId());
        gnomadAfricanAfricanAmerican.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getAfricanAfricanAmerican())));
        gnomadAfricanAfricanAmerican.setCellFactory(cell -> new PopTableCell("gnomAD.africanAfricanAmerican"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadEastAsian = new TableColumn<>(SnvTableColumnCode.GNOMAD_EAS.getName());
        createTableHeader(gnomadEastAsian, SnvTableColumnCode.GNOMAD_EAS.getName(), "gnomAD : East Asian", null, SnvTableColumnCode.GNOMAD_EAS.getId());
        gnomadEastAsian.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getEastAsian())));
        gnomadEastAsian.setCellFactory(cell -> new PopTableCell("gnomAD.eastAsian"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadFinnish = new TableColumn<>(SnvTableColumnCode.GNOMAD_FIN.getName());
        createTableHeader(gnomadFinnish, SnvTableColumnCode.GNOMAD_FIN.getName(), "gnomAD : Finnish", null, SnvTableColumnCode.GNOMAD_FIN.getId());
        gnomadFinnish.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getFinnish())));
        gnomadFinnish.setCellFactory(cell -> new PopTableCell("gnomAD.finnish"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadNonFinnishEuropean = new TableColumn<>(SnvTableColumnCode.GNOMAD_NFE.getName());
        createTableHeader(gnomadNonFinnishEuropean, SnvTableColumnCode.GNOMAD_NFE.getName(), "gnomAD : Non-Finnsh European", null, SnvTableColumnCode.GNOMAD_NFE.getId());
        gnomadNonFinnishEuropean.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getNonFinnishEuropean())));
        gnomadNonFinnishEuropean.setCellFactory(cell -> new PopTableCell("gnomAD.nonFinnishEuropean"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadOthers = new TableColumn<>(SnvTableColumnCode.GNOMAD_OTH.getName());
        createTableHeader(gnomadOthers, SnvTableColumnCode.GNOMAD_OTH.getName(), "gnomAD : Other", null, SnvTableColumnCode.GNOMAD_OTH.getId());
        gnomadOthers.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getOthers())));
        gnomadOthers.setCellFactory(cell -> new PopTableCell("gnomAD.others"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> gnomadSouthAsian = new TableColumn<>(SnvTableColumnCode.GNOMAD_SAS.getName());
        createTableHeader(gnomadSouthAsian, SnvTableColumnCode.GNOMAD_SAS.getName(), "gnomAD : South Asian", null, SnvTableColumnCode.GNOMAD_SAS.getId());
        gnomadSouthAsian.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getGnomAD().getSouthAsian())));
        gnomadSouthAsian.setCellFactory(cell -> new PopTableCell("gnomAD.southAsian"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> koreanReferenceDatabase = new TableColumn<>(SnvTableColumnCode.KRGDB.getName());
        createTableHeader(koreanReferenceDatabase, SnvTableColumnCode.KRGDB.getName(), "Korean Reference Genome Database" ,null, SnvTableColumnCode.KRGDB.getId());
        koreanReferenceDatabase.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getKoreanReferenceGenomeDatabase())));
        koreanReferenceDatabase.setCellFactory(cell -> new PopTableCell("koreanReferenceGenomeDatabase"));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> koreanExomInformationDatabase = new TableColumn<>(SnvTableColumnCode.KOEXID.getName());
        createTableHeader(koreanExomInformationDatabase, SnvTableColumnCode.KOEXID.getName(), "Korean Exome Information Database" ,null, SnvTableColumnCode.KOEXID.getId());
        koreanExomInformationDatabase.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getKoreanExomInformationDatabase())));
        koreanExomInformationDatabase.setCellFactory(cell -> new PopTableCell("koreanExomInformationDatabase"));

        TableColumn<VariantAndInterpretationEvidence, String> dbSnpCommonId = new TableColumn<>(SnvTableColumnCode.DBSNP_COMMON_ID.getName());
        createTableHeader(dbSnpCommonId, SnvTableColumnCode.DBSNP_COMMON_ID.getName(), null ,null, SnvTableColumnCode.DBSNP_COMMON_ID.getId());
        dbSnpCommonId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getDbSNP().getDbSnpCommonId()));

        TableColumn<VariantAndInterpretationEvidence, String> strand = new TableColumn<>(SnvTableColumnCode.STRAND.getName());
        createTableHeader(strand, SnvTableColumnCode.STRAND.getName(), null ,55., SnvTableColumnCode.STRAND.getId());
        strand.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getStrand()));

        TableColumn<VariantAndInterpretationEvidence, String> typeExtension = new TableColumn<>(SnvTableColumnCode.TYPE_EXTENSION.getName());
        createTableHeader(typeExtension, SnvTableColumnCode.TYPE_EXTENSION.getName(), "variantTypeExtension", 70., SnvTableColumnCode.TYPE_EXTENSION.getId());
        typeExtension.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getVariantTypeExtension()));

        TableColumn<VariantAndInterpretationEvidence, String> exonBic = new TableColumn<>(SnvTableColumnCode.EXON_BIC.getName());
        createTableHeader(exonBic, SnvTableColumnCode.EXON_BIC.getName(), null ,null, SnvTableColumnCode.EXON_BIC.getId());
        exonBic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getExonNumBic()));

        TableColumn<VariantAndInterpretationEvidence, String> zigosity = new TableColumn<>(SnvTableColumnCode.ZYGOSITY.getName());
        createTableHeader(zigosity, SnvTableColumnCode.ZYGOSITY.getName(), null, null, SnvTableColumnCode.ZYGOSITY.getId());
        zigosity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getZygosity()));

        TableColumn<VariantAndInterpretationEvidence, String> siftPrediction = new TableColumn<>(SnvTableColumnCode.SIFT_PREDICTION.getName());
        createTableHeader(siftPrediction, SnvTableColumnCode.SIFT_PREDICTION.getName(), null,100., SnvTableColumnCode.SIFT_PREDICTION.getId());
        siftPrediction.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getDbNSFP().getSiftPrediction()));

        TableColumn<VariantAndInterpretationEvidence, String> mutationTasterPrediction = new TableColumn<>(SnvTableColumnCode.MUTATION_TASTER_PREDICTION.getName());
        createTableHeader(mutationTasterPrediction, SnvTableColumnCode.MUTATION_TASTER_PREDICTION.getName(), null,null, SnvTableColumnCode.MUTATION_TASTER_PREDICTION.getId());
        mutationTasterPrediction.getStyleClass().clear();
        mutationTasterPrediction.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getDbNSFP().getMutationTasterPrediction()));

        TableColumn<VariantAndInterpretationEvidence, String> gerpNrScore = new TableColumn<>(SnvTableColumnCode.GERP_NR.getName());
        createTableHeader(gerpNrScore, SnvTableColumnCode.GERP_NR.getName(), "Genomic Evolutionary Rate Profiling++ NR",null, SnvTableColumnCode.GERP_NR.getId());
        gerpNrScore.getStyleClass().clear();
        gerpNrScore.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getDbNSFP().getGerpNrScore()));
        gerpNrScore.setCellFactory(cell -> new GERPTableCell());

        TableColumn<VariantAndInterpretationEvidence, String> gerpRsScore = new TableColumn<>(SnvTableColumnCode.GERP_RS.getName());
        createTableHeader(gerpRsScore, SnvTableColumnCode.GERP_RS.getName(), "Genomic Evolutionary Rate Profiling++ RS",null, SnvTableColumnCode.GERP_RS.getId());
        gerpRsScore.getStyleClass().clear();
        gerpRsScore.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getDbNSFP().getGerpRsScore()));
        gerpRsScore.setCellFactory(cell -> new GERPTableCell());

        TableColumn<VariantAndInterpretationEvidence, String> fathmmPrediction = new TableColumn<>(SnvTableColumnCode.FATHMM_PREDICTION.getName());
        createTableHeader(fathmmPrediction, SnvTableColumnCode.FATHMM_PREDICTION.getName(), "Functional Analysis through Hidden Markov Models Prediction",null, SnvTableColumnCode.FATHMM_PREDICTION.getId());
        fathmmPrediction.getStyleClass().clear();
        fathmmPrediction.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getDbNSFP().getFathmmPrediction()));

        TableColumn<VariantAndInterpretationEvidence, String> lrtPrediction = new TableColumn<>(SnvTableColumnCode.LRT_PREDICTION.getName());
        createTableHeader(lrtPrediction, SnvTableColumnCode.LRT_PREDICTION.getName(), "Likelihood Ratio Test Prediction",null, SnvTableColumnCode.LRT_PREDICTION.getId());
        lrtPrediction.getStyleClass().clear();
        lrtPrediction.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getDbNSFP().getLrtPrediction()));

        TableColumn<VariantAndInterpretationEvidence, String> mutationAssessorPrediction = new TableColumn<>(SnvTableColumnCode.MUTATION_ASSESSOR_PREDICTION.getName());
        createTableHeader(mutationAssessorPrediction, SnvTableColumnCode.MUTATION_ASSESSOR_PREDICTION.getName(), null,null, SnvTableColumnCode.MUTATION_ASSESSOR_PREDICTION.getId());
        mutationAssessorPrediction.getStyleClass().clear();
        mutationAssessorPrediction.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getDbNSFP().getMutationAssessorPrediction()));

        TableColumn<VariantAndInterpretationEvidence, String> cosmicOccurrence = new TableColumn<>(SnvTableColumnCode.COSMIC_OCCURRENCE.getName());
        createTableHeader(cosmicOccurrence, SnvTableColumnCode.COSMIC_OCCURRENCE.getName(), "Catalogue Of Somatic Mutations In Cancer Occurrence" ,null, SnvTableColumnCode.COSMIC_OCCURRENCE.getId());
        cosmicOccurrence.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCosmic().getCosmicOccurrence()));

        TableColumn<VariantAndInterpretationEvidence, String> cosmicCount = new TableColumn<>(SnvTableColumnCode.COSMIC_COUNT.getName());
        createTableHeader(cosmicCount, SnvTableColumnCode.COSMIC_COUNT.getName(), "Catalogue Of Somatic Mutations In Cancer Count" ,null, SnvTableColumnCode.COSMIC_COUNT.getId());
        cosmicCount.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCosmic().getCosmicCount()));


        TableColumn<VariantAndInterpretationEvidence, String> bicCategory = new TableColumn<>(SnvTableColumnCode.BIC_CATEGORY.getName());
        createTableHeader(bicCategory, SnvTableColumnCode.BIC_CATEGORY.getName(), "Breast Cancer Information Core Category" ,null, SnvTableColumnCode.BIC_CATEGORY.getId());
        bicCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicCategory()));

        TableColumn<VariantAndInterpretationEvidence, String> bicClass = new TableColumn<>(SnvTableColumnCode.BIC_CLASS.getName());
        createTableHeader(bicClass, SnvTableColumnCode.BIC_CLASS.getName(), "Breast Cancer Information Core Class" ,null, SnvTableColumnCode.BIC_CLASS.getId());
        bicClass.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicClass()));

        TableColumn<VariantAndInterpretationEvidence, String> bicDesignation = new TableColumn<>(SnvTableColumnCode.BIC_DESIGNATION.getName());
        createTableHeader(bicDesignation, SnvTableColumnCode.BIC_DESIGNATION.getName(), "Breast Cancer Information Core Designation" ,null, SnvTableColumnCode.BIC_DESIGNATION.getId());
        bicDesignation.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicDesignation()));

        TableColumn<VariantAndInterpretationEvidence, String> bicImportance = new TableColumn<>(SnvTableColumnCode.BIC_IMPORTANCE.getName());
        createTableHeader(bicImportance, SnvTableColumnCode.BIC_IMPORTANCE.getName(), "Breast Cancer Information Core Importance" ,null, SnvTableColumnCode.BIC_IMPORTANCE.getId());
        bicImportance.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicImportance()));

        TableColumn<VariantAndInterpretationEvidence, String> bicNt = new TableColumn<>(SnvTableColumnCode.BIC_NT.getName());
        createTableHeader(bicNt, SnvTableColumnCode.BIC_NT.getName(), "Breast Cancer Information Core NT" ,null, SnvTableColumnCode.BIC_NT.getId());
        bicNt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBic().getBicNt()));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> kohbraFrequency = new TableColumn<>(SnvTableColumnCode.KOHBRA_FREQUENCY.getName());
        createTableHeader(kohbraFrequency, SnvTableColumnCode.KOHBRA_FREQUENCY.getName(), "The Korean Hereditary Breast Cancer Frequency", null, SnvTableColumnCode.KOHBRA_FREQUENCY.getId());
        kohbraFrequency.setCellValueFactory(cellData -> new SimpleObjectProperty<>(ConvertUtil.removeZero(cellData.getValue().getSnpInDel().getPopulationFrequency().getKohbraFreq())));

        TableColumn<VariantAndInterpretationEvidence, String> kohbraPatient = new TableColumn<>(SnvTableColumnCode.KOHBRA_PATIENT.getName());
        createTableHeader(kohbraPatient, SnvTableColumnCode.KOHBRA_PATIENT.getName(), "The Korean Hereditary Breast Cancer Patient", null, SnvTableColumnCode.KOHBRA_PATIENT.getId());
        kohbraPatient.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getKohbraPatient()));

        TableColumn<VariantAndInterpretationEvidence, String> beBicCategory = new TableColumn<>(SnvTableColumnCode.BE_BIC_CATEGORY.getName());
        createTableHeader(beBicCategory, SnvTableColumnCode.BE_BIC_CATEGORY.getName(), "BRCA Exchange BIC Category",null, SnvTableColumnCode.BE_BIC_CATEGORY.getId());
        beBicCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicCategory()));

        TableColumn<VariantAndInterpretationEvidence, String> beBicEthnic = new TableColumn<>(SnvTableColumnCode.BE_BIC_ETHNIC.getName());
        createTableHeader(beBicEthnic, SnvTableColumnCode.BE_BIC_ETHNIC.getName(), "BRCA Exchange BIC Ethnic" ,null, SnvTableColumnCode.BE_BIC_ETHNIC.getId());
        beBicEthnic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicNationality()));

        TableColumn<VariantAndInterpretationEvidence, String> beBicNationality = new TableColumn<>(SnvTableColumnCode.BE_BIC_NATIONALITY.getName());
        createTableHeader(beBicNationality, SnvTableColumnCode.BE_BIC_NATIONALITY.getName(), "BRCA Exchange BIC Nationality" ,null, SnvTableColumnCode.BE_BIC_NATIONALITY.getId());
        beBicNationality.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicNationality()));

        TableColumn<VariantAndInterpretationEvidence, String> beBicPathogenicity = new TableColumn<>(SnvTableColumnCode.BE_BIC_PATHOGENICITY.getName());
        createTableHeader(beBicPathogenicity, SnvTableColumnCode.BE_BIC_PATHOGENICITY.getName(), "BRCA Exchange BIC Pathogenicity" ,null, SnvTableColumnCode.BE_BIC_PATHOGENICITY.getId());
        beBicPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeBicPathogenicity()));

        TableColumn<VariantAndInterpretationEvidence, String> beClinVarMethod = new TableColumn<>(SnvTableColumnCode.BE_CLINVAR_METHOD.getName());
        createTableHeader(beClinVarMethod, SnvTableColumnCode.BE_CLINVAR_METHOD.getName(), "BRCA Exchange ClinVar Method" ,null, SnvTableColumnCode.BE_CLINVAR_METHOD.getId());
        beClinVarMethod.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarMethod()));

        TableColumn<VariantAndInterpretationEvidence, String> beClinVarOrigin = new TableColumn<>(SnvTableColumnCode.BE_CLINVAR_ORIGIN.getName());
        createTableHeader(beClinVarOrigin, SnvTableColumnCode.BE_CLINVAR_ORIGIN.getName(), "BRCA Exchange ClinVar Origin" ,null, SnvTableColumnCode.BE_CLINVAR_ORIGIN.getId());
        beClinVarOrigin.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarOrigin()));

        TableColumn<VariantAndInterpretationEvidence, String> beClinVarPathogenicity = new TableColumn<>(SnvTableColumnCode.BE_CLINVAR_PATHOGENICITY.getName());
        createTableHeader(beClinVarPathogenicity, SnvTableColumnCode.BE_CLINVAR_PATHOGENICITY.getName(), "BRCA Exchange ClinVar Pathogenicity" ,null, SnvTableColumnCode.BE_CLINVAR_PATHOGENICITY.getId());
        beClinVarPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarPathogenicity()));

        TableColumn<VariantAndInterpretationEvidence, String> beClinVarUpdate = new TableColumn<>(SnvTableColumnCode.BE_CLINVAR_UPDATE.getName());
        createTableHeader(beClinVarUpdate, SnvTableColumnCode.BE_CLINVAR_UPDATE.getName(), "BRCA Exchange ClinVar Update" ,null, SnvTableColumnCode.BE_CLINVAR_UPDATE.getId());
        beClinVarUpdate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeClinVarUpdate()));

        TableColumn<VariantAndInterpretationEvidence, String> beEnigmaCondition = new TableColumn<>(SnvTableColumnCode.BE_ENIGMA_CONDITION.getName());
        createTableHeader(beEnigmaCondition, SnvTableColumnCode.BE_ENIGMA_CONDITION.getName(), "BRCA Exchange ENIGMA Condition" ,null, SnvTableColumnCode.BE_ENIGMA_CONDITION.getId());
        beEnigmaCondition.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaCondition()));

        TableColumn<VariantAndInterpretationEvidence, String> beEnigmaPathogenicity = new TableColumn<>(SnvTableColumnCode.BE_ENIGMA_PATHOGENICITY.getName());
        createTableHeader(beEnigmaPathogenicity, SnvTableColumnCode.BE_ENIGMA_PATHOGENICITY.getName(), "BRCA Exchange ENIGMA Pathogenicity" ,null, SnvTableColumnCode.BE_ENIGMA_PATHOGENICITY.getId());
        beEnigmaPathogenicity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaPathogenicity()));

        TableColumn<VariantAndInterpretationEvidence, String> beEnigmaUpdate = new TableColumn<>(SnvTableColumnCode.BE_ENIGMA_UPDATE.getName());
        createTableHeader(beEnigmaUpdate, SnvTableColumnCode.BE_ENIGMA_UPDATE.getName(), "BRCA Exchange ENIGMA Update" ,null, SnvTableColumnCode.BE_ENIGMA_UPDATE.getId());
        beEnigmaUpdate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeEnigmaUpdate()));

        TableColumn<VariantAndInterpretationEvidence, String> beGene = new TableColumn<>(SnvTableColumnCode.BE_GENE.getName());
        createTableHeader(beGene, SnvTableColumnCode.BE_GENE.getName(), "BRCA Exchange Gene" ,null, SnvTableColumnCode.BE_GENE.getId());
        beGene.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeGene()));

        TableColumn<VariantAndInterpretationEvidence, String> beNt = new TableColumn<>(SnvTableColumnCode.BE_NT.getName());
        createTableHeader(beNt, SnvTableColumnCode.BE_NT.getName(), "BRCA Exchange NT",null, SnvTableColumnCode.BE_NT.getId());
        beNt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeNt()));

        TableColumn<VariantAndInterpretationEvidence, String> beTranscript = new TableColumn<>(SnvTableColumnCode.BE_TRANSCRIPT.getName());
        createTableHeader(beTranscript, SnvTableColumnCode.BE_TRANSCRIPT.getName(), "BRCA Exchange Transcript",null, SnvTableColumnCode.BE_TRANSCRIPT.getId());
        beTranscript.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getBe().getBeTranscript()));

        TableColumn<VariantAndInterpretationEvidence, String> enigma = new TableColumn<>(SnvTableColumnCode.ENIGMA.getName());
        createTableHeader(enigma, SnvTableColumnCode.ENIGMA.getName(), "Evidence-based Network for the Interpretation of Germline Mutant Alleles" ,null, SnvTableColumnCode.ENIGMA.getId());
        enigma.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getEnigma()));

        TableColumn<VariantAndInterpretationEvidence, Integer> clinVarVariationId = new TableColumn<>(SnvTableColumnCode.CLINVAR_VARIATION_ID.getName());
        createTableHeader(clinVarVariationId, SnvTableColumnCode.CLINVAR_VARIATION_ID.getName(), null ,null, SnvTableColumnCode.CLINVAR_VARIATION_ID.getId());
        clinVarVariationId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarVariationId()));

        TableColumn<VariantAndInterpretationEvidence, String> clinVarInterpretation = new TableColumn<>(SnvTableColumnCode.CLINVAR_INTERPRETATION.getName());
        createTableHeader(clinVarInterpretation, SnvTableColumnCode.CLINVAR_INTERPRETATION.getName(), null ,null, SnvTableColumnCode.CLINVAR_INTERPRETATION.getId());
        clinVarInterpretation.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getClinVar().getClinVarInterpretation()));

        TableColumn<VariantAndInterpretationEvidence, String> metaSvmPrediction = new TableColumn<>(SnvTableColumnCode.METASVM_PREDICTION.getName());
        createTableHeader(metaSvmPrediction, SnvTableColumnCode.METASVM_PREDICTION.getName(), null ,null, SnvTableColumnCode.METASVM_PREDICTION.getId());
        metaSvmPrediction.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getDbNSFP().getMetaSvmPrediction()));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> dbscSnvAdaScore = new TableColumn<>(SnvTableColumnCode.DBSCSNV_ADA_SCORE.getName());
        createTableHeader(dbscSnvAdaScore, SnvTableColumnCode.DBSCSNV_ADA_SCORE.getName(), null ,null, SnvTableColumnCode.DBSCSNV_ADA_SCORE.getId());
        dbscSnvAdaScore.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getClinicalDB().getDbNSFP().getDbscSnvAdaScore()));

        TableColumn<VariantAndInterpretationEvidence, BigDecimal> dbscSnvRfScore = new TableColumn<>(SnvTableColumnCode.DBSCSNV_RF_SCORE.getName());
        createTableHeader(dbscSnvRfScore, SnvTableColumnCode.DBSCSNV_RF_SCORE.getName(), null ,null, SnvTableColumnCode.DBSCSNV_RF_SCORE.getId());
        dbscSnvRfScore.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getClinicalDB().getDbNSFP().getDbscSnvRfScore()));

        TableColumn<VariantAndInterpretationEvidence, String> inheritance = new TableColumn<>(SnvTableColumnCode.INHERITANCE.getName());
        createTableHeader(inheritance, SnvTableColumnCode.INHERITANCE.getName(), null ,null, SnvTableColumnCode.INHERITANCE.getId());
        inheritance.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getInheritance()));

        TableColumn<VariantAndInterpretationEvidence, String> commonVariants = new TableColumn<>(SnvTableColumnCode.COMMON_VARIANTS.getName());
        createTableHeader(commonVariants, SnvTableColumnCode.COMMON_VARIANTS.getName(), null ,null, SnvTableColumnCode.COMMON_VARIANTS.getId());
        commonVariants.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getCommonVariants()));

        TableColumn<VariantAndInterpretationEvidence, String> customDatabase = new TableColumn<>(SnvTableColumnCode.CUSTOM_DATABASE.getName());
        createTableHeader(customDatabase, SnvTableColumnCode.CUSTOM_DATABASE.getName(), null ,null, SnvTableColumnCode.CUSTOM_DATABASE.getId());
        customDatabase.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getClinicalDB().getCustomDatabase()));
        customDatabase.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null || empty) {
                    setText(null);
                    setTooltip(null);
                    this.setOnMouseClicked(null);
                    this.getStyleClass().remove("cursor_hand");
                } else {
                    setText(Arrays.stream(item.split(";"))
                            .map(cdb -> cdb.substring(cdb.indexOf(": ") + 1))
                            .collect(Collectors.joining("; ")));
                    this.setOnMouseClicked(ev -> SNPsINDELsList.showItem(item, this));
                    this.getStyleClass().add("cursor_hand");

                    setTooltip(null);
                }
            }
        });

        commonVariantsCheckBox.addEventFilter(MouseEvent.MOUSE_CLICKED, ev -> {
            if(commonVariantsCheckBox.isSelected()) {
                commonVariants.setMinWidth(80);
                commonVariants.setMaxWidth(300);
                commonVariants.setPrefWidth(80);
            } else {
                commonVariants.setMinWidth(0);
                commonVariants.setMaxWidth(0);
                commonVariants.setPrefWidth(0);
            }
            Platform.runLater(() -> showVariantList(0));
        });

        commonVariants.setMinWidth(0);
        commonVariants.setMaxWidth(0);
        commonVariants.setPrefWidth(0);

        variantListTableView.getStyleClass().clear();
        variantListTableView.getStyleClass().add("table-view");

        variantListTableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) variantListTableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> {
                ObservableList columns = variantListTableView.getColumns();

                // If the first columns is not in the first index change it
                if (columns.indexOf(checkBoxColumn) != 0) {
                    columns.remove(checkBoxColumn);
                    columns.add(0, checkBoxColumn);
                }
            });
        });
    }

    private String getDefaultColumnOrderKey(Panel panel) {
        String key = null;
        if(PipelineCode.isHemePipeline(panel.getCode())) {
            key = "hemeColumnOrder";
        } else if(PipelineCode.isSolidPipeline(panel.getCode())) {
            key = "solidColumnOrder";
        } else if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode())) {
            key = "tstDNAColumnOrder";
        } else if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_CNV_DNA_V2_SNU.getCode())) {
            key = "brcaSnuColumnOrder";
        } else if(PipelineCode.isBRCAPipeline(panel.getCode())) {
            key = "brcaColumnOrder";
        } else if(PipelineCode.isHeredPipeline(panel.getCode())) {
            key = "heredColumnOrder";
        }
        return key;
    }

    private void initColumnOrderAndVariantList() {
        String path = new ResourceUtil().getDefaultColumnOrderResourcePath(panel);

        Task<Void> task = new Task<Void>() {
            String[] columnList;
            @Override
            protected Void call() throws Exception {
                try {
                    HttpClientResponse response;
                    String key = getDefaultColumnOrderKey(panel);
                    response = apiService.get("/member/memberOption/" + key, null, null, null);
                    if(response != null && response.getStatus() == 200) {
                        columnList = response.getContentString().split(",");
                    }
                } catch (WebAPIException wae) {
                    if (wae.getResponse().getStatus() != 404) throw wae;
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    if (columnList != null && columnList.length > 0) {
                        List<TableColumnInfo> tableColumnInfos = Arrays.stream(columnList)
                                .map(v -> v.split(":"))
                                .filter(v -> v.length == 3 && !v[1].equals("0"))
                                .map(v -> {
                                    TableColumnInfo tableColumnInfo = new TableColumnInfo();
                                    tableColumnInfo.setVisible(v[2].equals("Y"));
                                    tableColumnInfo.setColumnName(v[0]);
                                    tableColumnInfo.setOrder(Integer.parseInt(v[1]));
                                    return tableColumnInfo;
                                }).collect(Collectors.toList());
                        addAColumnToTable(tableColumnInfos);
                    } else {
                        setDefaultTableColumnOrder(path);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setDefaultTableColumnOrder(path);
                }
                variantListTableView.getColumns().addListener(tableColumnListChangeListener);
                Platform.runLater(() -> showVariantList(0));
            }

            @Override
            protected void failed() {
                super.failed();
                mainController.setMainMaskerPane(false);
                setDefaultTableColumnOrder(path);
                variantListTableView.getColumns().addListener(tableColumnListChangeListener);
                DialogUtil.showWebApiException(getException(), getMainController().getPrimaryStage());
                Platform.runLater(() -> showVariantList(0));
            }
        };
        mainController.setMainMaskerPane(true);
        Thread thread = new Thread(task);
        thread.start();
    }

    private void removeVariantTableColumns() {
        variantListTableView.getColumns().removeListener(tableColumnListChangeListener);
        int columnSize = variantListTableView.getColumns().size();

        if (columnSize > 1) {
            variantListTableView.getColumns().subList(1, columnSize).clear();
        }
    }


    private void saveColumnInfoToServer() {
        String columnString = variantListTableView.getColumns().stream()
                .filter(column -> StringUtils.isNotEmpty(column.getText())).map(column -> {
                    if (column.isVisible()) {
                        return column.getText() + ":" + variantListTableView.getColumns().indexOf(column) + ":Y";
                    } else {
                        return column.getText() + ":" + variantListTableView.getColumns().indexOf(column) + ":N";
                    }
                }).collect(Collectors.joining(","));
        logger.debug(columnString);
        Map<String, Object> map = new HashMap<>();
        map.put("value", columnString);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                apiService.put("/member/memberOption/" + getColumnOrderType(), map, null, true);
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                setMaskerPaneVisable(false);
            }

            @Override
            protected void failed() {
                super.failed();
                setMaskerPaneVisable(false);
            }
        };
        setMaskerPaneVisable(true);
        Thread thread = new Thread(task);
        thread.start();
    }

    private String getColumnOrderType() {
        String columnOrderType = "";
        if(PipelineCode.isHemePipeline(panel.getCode())) {
            columnOrderType = "hemeColumnOrder";
        } else if(PipelineCode.isSolidPipeline(panel.getCode())) {
            columnOrderType = "solidColumnOrder";
        } else if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode())) {
            columnOrderType = "tstDNAColumnOrder";
        } else if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_CNV_DNA_V2_SNU.getCode())) {
            columnOrderType = "brcaSnuColumnOrder";
        } else if(PipelineCode.isBRCAPipeline(panel.getCode())) {
            columnOrderType = "brcaColumnOrder";
        } else if(PipelineCode.isHeredPipeline(panel.getCode())) {
            columnOrderType = "heredColumnOrder";
        }

        return columnOrderType;
    }

    private void addAColumnToTable(List<TableColumnInfo> columnInfos) {
        List<TableColumnInfo> cols = columnInfos.stream()
                .filter(item -> columnMap.containsKey(item.getColumnName()))
                .sorted(Comparator.comparing(TableColumnInfo::getOrder)).collect(Collectors.toList());

        ArrayList visibleTableColumns = cols.stream().filter(tableColumnInfo -> tableColumnInfo.isVisible() ||
                tableColumnInfo.getColumnName().equals("False"))
                .map(item -> columnMap.get(item.getColumnName()))
                .collect(Collectors.toCollection(ArrayList::new));
        //컬럼 리셋을 했을 때 Visible 이 false인 컬럼을 뒤쪽으로 이동
        ArrayList invisibleTableColumns = cols.stream().filter(tableColumnInfo -> !tableColumnInfo.isVisible()
                && !tableColumnInfo.getColumnName().equals("False"))
                .map(item -> columnMap.get(item.getColumnName()))
                .collect(Collectors.toCollection(ArrayList::new));
        variantListTableView.getColumns().addAll(visibleTableColumns);
        variantListTableView.getColumns().addAll(invisibleTableColumns);
        logger.debug("Column Count = " + variantListTableView.getColumns().size());
    }

    @SuppressWarnings("unchecked")
    private void setDefaultTableColumnOrder(String path) {
        String str = PropertiesUtil.getJsonString(path);
        List<TableColumnInfo> columnInfos = (List<TableColumnInfo>)JsonUtil.getObjectList(str, TableColumnInfo.class);

        if (columnInfos != null) {
            removeVariantTableColumns();
            addAColumnToTable(columnInfos);
        }
    }

    class BooleanCell extends LockedTableCell<VariantAndInterpretationEvidence, Boolean> {
        private CheckBox checkBox = new CheckBox();
        private BooleanCell() {
            super(true);
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
            this.setStyle(this.getStyle() + "; -fx-background-color : white;");
            VariantAndInterpretationEvidence evidence = BooleanCell.this.getTableView().getItems().get(
                    BooleanCell.this.getIndex());
            checkBox.setSelected(evidence.getCheckItem());

            setGraphic(checkBox);
        }
    }

    public static class GERPTableCell extends TableCell<VariantAndInterpretationEvidence, String> {

        GERPTableCell() {   }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if(item == null || empty) {
                setText(null);
                setTooltip(null);
            } else if(StringUtils.isNotEmpty(item)){
                try {
                    DecimalFormat df = new DecimalFormat();
                    df.setMinimumFractionDigits(2);
                    df.setMaximumFractionDigits(2);
                    setText(df.format(new BigDecimal(item)));
                    setTooltip(new Tooltip(item));
                } catch (Exception e) {
                    setText(item);
                    setTooltip(null);
                }

            }
        }
    }

    public class PopTableCell extends TableCell<VariantAndInterpretationEvidence, BigDecimal> {
        String type;

        private PopTableCell(String type) {
            this.type = type;
        }

        @Override
        protected void updateItem(BigDecimal item, boolean empty) {
            super.updateItem(item, empty);
            setStyle(this.getStyle()+";-fx-alignment:baseline-right; -fx-padding: 0 10 0 0;");
            if(item == null || empty) {
                setText(null);
                setTooltip(null);
            } else {
                DecimalFormat df = new DecimalFormat();
                df.setMinimumFractionDigits(3);
                df.setMaximumFractionDigits(3);
                setText(df.format(item));
                setTooltip(new Tooltip(item.toString()));
                if(StringUtils.isNotEmpty(panel.getVariantFilter().getPopulationFrequencyDBs()) &&
                        panel.getVariantFilter().getPopulationFrequency() != null &&
                        Arrays.stream(panel.getVariantFilter().getPopulationFrequencyDBs().split(",")).anyMatch(db ->
                                db.equalsIgnoreCase(type)) && item.doubleValue() <
                        panel.getVariantFilter().getPopulationFrequency().doubleValue()) {
                    setTextFill(Color.RED);
                } else {
                    if(item.doubleValue() < 0.01) {
                        setTextFill(Color.RED);
                    } else {
                        setTextFill(Color.BLACK);
                    }
                }
            }
        }
    }

    private abstract class LockedTableCell<T, S> extends TableCell<T, S> {
        private ChangeListener<Number> getNumberChangeListener(Region headerNode) {
            return (ob, o, n) -> {
                double doubleValue = n.doubleValue();
                headerNode.setTranslateX(doubleValue);
                headerNode.toFront();
                this.setTranslateX(doubleValue);
                this.toFront();
            };
        }

        LockedTableCell(boolean lockEnabled) {
            if (lockEnabled) {
                Platform.runLater(() -> {
                    ScrollBar sc = (ScrollBar) getTableView().queryAccessibleAttribute(AccessibleAttribute.HORIZONTAL_SCROLLBAR);
                    TableHeaderRow thr = (TableHeaderRow) getTableView().queryAccessibleAttribute(AccessibleAttribute.HEADER);
                    Region headerNode = thr.getColumnHeaderFor(this.getTableColumn());
                    sc.valueProperty().addListener(getNumberChangeListener(headerNode));
                });
            }
        }
    }

    public static String ntChangeReverse(String ntChangeStr) {
        String result = "";
        try {
            String strconv = ntChangeStr.replace("c.", "0").replace("_", "0").replace("+", "0").replace("-", "0").replace("*", "0");
            char[] cha = strconv.toCharArray();
            int indexKey = 0;
            for (int i = 0; i < cha.length; i++) {
                if(cha[i] < 48 || cha[i] > 57) {
                    indexKey = i;
                    break;
                }
            }

            String frontStr = ntChangeStr.substring(0,indexKey+1);
            String afterStr = ntChangeStr.substring(indexKey+1, ntChangeStr.length());

            String[] seqStr = null;
            String splitKey = "";
            if(afterStr.indexOf("delins") == -1) {
                if(afterStr.indexOf(">") != -1) {
                    seqStr = afterStr.split(">");
                    splitKey = ">";
                } else if(afterStr.indexOf("del") != -1) {
                    seqStr = afterStr.split("del");
                    splitKey = "del";
                } else if(afterStr.indexOf("dup") != -1) {
                    seqStr = afterStr.split("dup");
                    splitKey = "dup";
                } else if(afterStr.indexOf("ins") != -1) {
                    seqStr = afterStr.split("ins");
                    splitKey = "ins";
                }
            } else {
                seqStr = afterStr.split("delins");
                splitKey = "delins";
            }

            try {
                result = frontStr + getReverseIndexSeq(seqStr[0]) + splitKey + getReverseIndexSeq(seqStr[1]);
            } catch (Exception e) {
                result = frontStr + splitKey;
            }

        } catch (Exception e) {
            result = "";
        }
        logger.debug(" ntChangeresultvalue = " + result);
        return result;
    }

    public static String getReverseIndexSeq(String midseq) throws Exception {
        String rcmidseq = new StringBuilder(midseq).reverse().toString();
        String result = "";
        for(int i = 0;i<rcmidseq.length();i++) {
            char k = rcmidseq.charAt(i);
            if(k == 'A')
                k = 'T';
            else if(k == 'T')
                k = 'A';
            else if(k == 'G')
                k = 'C';
            else if(k == 'C')
                k = 'G';
            result += k;
        }
        return result;
    }
}
