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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ngeneanalysys.code.constants.FXMLConstants;
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
import ngeneanalysys.model.render.SNPsINDELsList;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

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
    private VBox filterArea;

    @FXML
    private Label totalVariantCountLabel;

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

    Sample sample = null;
    Panel panel = null;

    private AnalysisDetailVariantsController variantsController;
    //VariantList
    List<VariantAndInterpretationEvidence> list = null;

    /** 현재 선택된 변이 정보 객체 */
    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;
    /** 현재 선택된 변이 리스트 객체의 index */
    private int selectedVariantIndex;

    private final double leftFoldedWidth = 50;
    private final double leftExpandedWidth = 200;

    private final double rightFoldedWidth = 50;
    private final double rightStandardWidth = 890;
    private final double rightFullWidth = 1040;

    private final double centerFoldedWidth = 0;
    private final double centerStandardWidth = 890;
    private final double centerFullWidth = 1040;

    private final double minSize = 0;
    private final double standardAccordionSize = 870;
    private final double maxAccordionSize = 970;

    private final double standardTableSize = 830;
    private final double maxTableSize = 980;

    /**
     * @param variantsController
     */
    public void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.info("init snv controller");

        apiService = APIService.getInstance();

        sample = (Sample)paramMap.get("sample");
        panel = (Panel)paramMap.get("panel");

        leftSizeButton.setOnMouseClicked(event -> {
            if (leftSizeButton.getStyleClass().get(0) == null){
                return;
            } else if(leftSizeButton.getStyleClass().contains("btn_fold")){
                foldLeft();
            } else {
                expandLeft();
            }
        });

        rightSizeButton.setOnMouseClicked(event -> {
            if (rightSizeButton.getStyleClass().get(0) == null){
                return;
            } else if(rightSizeButton.getStyleClass().contains("right_btn_fold")){
                foldRight();
            } else {
                expandRight();
            }
        });

        // 목록 클릭 시 변이 상세정보 출력 이벤트 바인딩
        variantListTableView.setRowFactory(tv -> {
            TableRow<VariantAndInterpretationEvidence> row = new TableRow<>();
            row.setOnMouseClicked(e -> {

                if (e.getClickCount() <= 2) {
                    logger.info(e.getClickCount() + " Click count");
                    Platform.runLater(() -> showVariantDetail(variantListTableView.getSelectionModel().getSelectedItem()));
                   if(e.getClickCount() == 2) {
                       expandRight();
                   }
                }

                /*if (e.getClickCount() == 1 && (!row.isEmpty())) {
                    showVariantDetail(variantListTableView.getSelectionModel().getSelectedItem());
                } else if (e.getClickCount() == 2 && (!row.isEmpty())) {
                    showVariantDetail(variantListTableView.getSelectionModel().getSelectedItem());
                    expandRight();
                }*/
            });
            return row;
        });

        // 선택된 목록에서 엔터키 입력시 변이 상세정보 출력 이벤트 바인딩
        variantListTableView.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                showVariantDetail(variantListTableView.getSelectionModel().getSelectedItem());
                expandRight();
            }
        });


        setTableViewColumn();

        showVariantList(0);

        variantsController.getDetailContents().setCenter(root);
    }

    private void expandLeft() {
        snvWrapper.getColumnConstraints().get(0).setPrefWidth(this.leftExpandedWidth);
        if(snvWrapper.getColumnConstraints().get(1).getPrefWidth() == 0) {
            snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightStandardWidth);
            rightContentsHBox.setPrefWidth(this.rightStandardWidth);
            overviewAccordion.setPrefWidth(this.standardAccordionSize);
            variantListTableView.setPrefWidth(this.minSize);
        } else {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerStandardWidth);
            overviewAccordion.setPrefWidth(this.minSize);
            variantListTableView.setPrefWidth(this.standardTableSize);
        }
        filterArea.setPrefWidth(150);
        leftSizeButton.getStyleClass().clear();
        leftSizeButton.getStyleClass().add("btn_fold");
    }

    private void foldLeft(){
        snvWrapper.getColumnConstraints().get(0).setPrefWidth(this.leftFoldedWidth);
        rightContentsHBox.setPrefWidth(this.rightStandardWidth);
        if(snvWrapper.getColumnConstraints().get(1).getPrefWidth() == 0) {
            snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightFullWidth);
            overviewAccordion.setPrefWidth(this.maxAccordionSize);
            variantListTableView.setPrefWidth(this.minSize);
        } else {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerFullWidth);
            overviewAccordion.setPrefWidth(this.minSize);
            variantListTableView.setPrefWidth(this.maxTableSize);
        }
        filterArea.setPrefWidth(0);
        leftSizeButton.getStyleClass().clear();
        leftSizeButton.getStyleClass().add("btn_expand");
    }

    private void expandRight() {
        snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerFoldedWidth);
        if(snvWrapper.getColumnConstraints().get(0).getPrefWidth() == 200) {
            snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightStandardWidth);
            overviewAccordion.setPrefWidth(this.standardAccordionSize);
        } else {
            snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightFullWidth);
            overviewAccordion.setPrefWidth(this.maxAccordionSize);
        }
        variantListTableView.setPrefWidth(this.minSize);

        rightSizeButton.getStyleClass().clear();
        rightSizeButton.getStyleClass().add("right_btn_fold");
    }

    private void foldRight(){
        snvWrapper.getColumnConstraints().get(2).setPrefWidth(this.rightFoldedWidth);
        if(snvWrapper.getColumnConstraints().get(0).getPrefWidth() == 200) {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerStandardWidth);
            rightContentsHBox.setPrefWidth(minSize);
        } else {
            snvWrapper.getColumnConstraints().get(1).setPrefWidth(this.centerFullWidth);
        }

        rightSizeButton.getStyleClass().clear();
        rightSizeButton.getStyleClass().add("right_btn_expand");
    }

    public void showVariantStatistics() {
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

    public void showDetailTab() {
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
     * Memo 탭 화면 출력
     */
    public void showPredictionAndInterpretation(SnpInDelInterpretation interpretation) {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_INTERPRETATION);
            Node node = loader.load();
            AnalysisDetailInterpretationController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.show((Parent) node);
            controller.setLabel(interpretation);
            interpretationTitledPane.setContent(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Memo 탭 화면 출력
     */
    public void showMemoTab(ObservableList<SnpInDelInterpretationLogs> memoList) {
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
     * @param analysisResultVariant
     */
    @SuppressWarnings("unchecked")
    public void showVariantDetail(VariantAndInterpretationEvidence analysisResultVariant) {
        // 선택된 변이의 목록에서의 인덱스 정보 설정.
        selectedVariantIndex = variantListTableView.getItems().indexOf(analysisResultVariant);
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
            variantAndInterpretationEvidence.setInterpretationEvidence(selectedAnalysisResultVariant.getInterpretationEvidence());

            paramMap.put("variant", analysisResultVariant);

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

            showVariantStatistics();

            if(analysisResultVariant != null) {
                showDetailTab();
            }
            if(panel.getAnalysisType().equalsIgnoreCase(ExperimentTypeCode.SOMATIC.getDescription())) {
                showPredictionAndInterpretation(variantAndInterpretationEvidence.getInterpretationEvidence());
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
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

        try {
            // Memo 데이터 API 요청
            //Map<String, Object> commentParamMap = new HashMap<>();
            HttpClientResponse responseMemo = apiService.get("/analysisResults/snpInDelInterpretationLogs/" + selectedAnalysisResultVariant.getSnpInDel().getId() , null, null, false);

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
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

        // 첫번째 탭 선택 처리
        overviewAccordion.setExpandedPane(variantDetailTitledPane);
        //setDetailTabActivationToggle(true);
    }

    private void showClinicalSignificant() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_CLINICAL_SIGNIFICANT);
            Node node = loader.load();
            AnalysisDetailClinicalSignificantController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.show((Parent) node);
            clinicalSignificantTitledPane.setContent(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showVariantList(int selectedIdx) {

        try {
            // API 서버 조회
            HttpClientResponse response = apiService.get("/analysisResults/sampleSnpInDels/"+ sample.getId(), null,
                    null, false);
            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();
            this.list = list;

            ObservableList<VariantAndInterpretationEvidence> displayList = null;

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
                variantListTableView.getSelectionModel().select(selectedIdx);
                showVariantDetail(displayList.get(selectedIdx));
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

    private void setTableViewColumn() {
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
        aaChangeConversion.setCellValueFactory(cellData -> cellData.getValue().getSnpInDel().getSnpInDelExpression().getAachangeSingleLetter() == null ?
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChangeConversion()) :
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAachangeSingleLetter()));

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

        variantListTableView.getColumns().addAll(kohbraPatient, kohbraFrequency, variantNum, refGenomeVer, leftSequence, rightSequence
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

    }

    private String cutVariantTypeString(String variantType) {
        if(variantType.contains(":")) return variantType.substring(0, variantType.indexOf(':'));
        return variantType;
    }

}
