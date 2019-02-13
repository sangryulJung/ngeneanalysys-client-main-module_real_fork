package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.code.enums.SequencerCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedCnv;
import ngeneanalysys.model.paged.PagedVariantAndInterpretationEvidence;
import ngeneanalysys.model.paged.PagedVirtualPanel;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.model.render.DatepickerConverter;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.ExcelConvertReportInformationService;
import ngeneanalysys.service.ImageService;
import ngeneanalysys.service.PDFCreateService;
import ngeneanalysys.task.ImageFileDownloadTask;
import ngeneanalysys.task.JarDownloadTask;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-09-04
 */
public class AnalysisDetailReportController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    private static final String CONFIRMATION_DIALOG = "Confirmation Dialog";

    /** api service */
    private APIService apiService;

    private PDFCreateService pdfCreateService;

    /** Velocity Util */
    private VelocityUtil velocityUtil = new VelocityUtil();

    @FXML
    private Label tierCountLabel;

    @FXML
    private Button excelTemplateBtn;

    @FXML
    private Button excelUploadBtn;

    @FXML
    private TextArea conclusionsTextArea;

    @FXML
    private TableView<VariantAndInterpretationEvidence> variantsTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> chrColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> geneColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, Integer> positionColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> refSeqColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> ntChangeColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> aaChangeColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> alleleFrequencyColumn;

    @FXML
    private GridPane customFieldGridPane;

    @FXML
    private VBox contentVBox;

    @FXML
    private Pane mainContentsPane;

    @FXML
    private Label conclusions;

    @FXML
    private Label extraFields;

    @FXML
    private ComboBox<ComboBoxItem> virtualPanelComboBox;

    @FXML
    private FlowPane targetGenesFlowPane;

    private SampleView sample = null;

    private Panel panel = null;

    private List<VariantAndInterpretationEvidence> tierOne = null;

    private List<VariantAndInterpretationEvidence> tierTwo = null;

    private List<VariantAndInterpretationEvidence> tierThree = null;

    private List<VariantAndInterpretationEvidence> tierFour = null;

    private List<VariantAndInterpretationEvidence> negativeList = null;

    private boolean reportData = false;

    private File excelFile = null;

    private Map<String, Object> variableList = null;

    private AnalysisDetailSolidCNVReportController analysisDetailSolidCNVReportController = null;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");

        tableCellUpdateFix(variantsTable);

        // API 서비스 클래스 init
        apiService = APIService.getInstance();

        apiService.setStage(getMainController().getPrimaryStage());

        loginSession = LoginSessionUtil.getCurrentLoginSession();

        pdfCreateService = PDFCreateService.getInstance();

        customFieldGridPane.getChildren().clear();
        customFieldGridPane.setPrefHeight(0);

        sample = (SampleView)paramMap.get("sampleView");

        tierColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSnpInDel().getExpertTier() != null ? cellData.getValue().getSnpInDel().getExpertTier() :
                        cellData.getValue().getSnpInDel().getSwTier()));
        chrColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getChromosome()));
        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        positionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSnpInDel().getGenomicCoordinate().getStartPosition()));
        refSeqColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getTranscriptAccession()));
        ntChangeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        aaChangeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChange()));
        alleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()
                .toString() + "(" + cellData.getValue().getSnpInDel().getReadInfo().getAltReadNum() + "/" + cellData.getValue().getSnpInDel().getReadInfo().getReadDepth() + ")"));
        panel = sample.getPanel();

        initialize(panel);

        if(panel.getCode().equals(PipelineCode.SOLID_ACCUTEST_CNV_DNA.getCode()) ||
                panel.getCode().equals(PipelineCode.HEME_ACCUTEST_CNV_DNA.getCode())) {
            mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() + 153);
            contentVBox.setPrefHeight(contentVBox.getPrefHeight() + 153);
            try {
                FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SOLID_AMC_CNV_REPORT);
                Node node = loader.load();
                AnalysisDetailSolidCNVReportController controller = loader.getController();
                analysisDetailSolidCNVReportController = controller;
                controller.setMainController(this.getMainController());
                controller.setParamMap(paramMap);
                controller.show((Parent) node);
                contentVBox.getChildren().add(3, node);
                VBox.setMargin(node, new Insets(10, 0, 0 ,0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setTargetGenesList() {
        if(!targetGenesFlowPane.getChildren().isEmpty()) {
            mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() - targetGenesFlowPane.getPrefHeight());
            contentVBox.setPrefHeight(contentVBox.getPrefHeight() - targetGenesFlowPane.getPrefHeight());
            targetGenesFlowPane.getChildren().removeAll(targetGenesFlowPane.getChildren());
            targetGenesFlowPane.setPrefHeight(0);
        }
        String virtualPanelId = virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue();
        Task<Void> task = new Task<Void>(){
            List<VariantCountByGene> variantCountByGenes;
            VirtualPanel virtualPanel;
            @Override
            protected Void call() throws Exception {
                HttpClientResponse response = apiService.get("/analysisResults/variantCountByGeneForSomaticDNA/" + sample.getId(),
                        null, null, false);
                if (response != null) {
                    variantCountByGenes = (List<VariantCountByGene>) response
                            .getMultiObjectBeforeConvertResponseToJSON(VariantCountByGene.class,
                                    false);
                    if (variantCountByGenes == null) {
                        variantCountByGenes = new ArrayList<>();
                    }
                    variantCountByGenes = filteringGeneList(variantCountByGenes);
                }
                if(!StringUtils.isEmpty(virtualPanelId)) {
                    response = apiService.get("virtualPanels/" + virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue(),
                            null, null, false);

                    virtualPanel = response.getObjectBeforeConvertResponseToJSON(VirtualPanel.class);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                getMainController().setMainMaskerPane(false);
                if(variantCountByGenes != null && !variantCountByGenes.isEmpty()) {
                    targetGenesFlowPane.setPrefHeight(targetGenesFlowPane.getPrefHeight() + 30);
                    mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() + 30);
                    contentVBox.setPrefHeight(contentVBox.getPrefHeight() + 30);
                }
                variantCountByGenes = variantCountByGenes.stream().sorted(Comparator.comparing(VariantCountByGene::getGeneSymbol))
                        .collect(Collectors.toList());
                Set<String> allGeneList = null;
                Set<String> list = new HashSet<>();
                if(virtualPanel != null){
                    allGeneList = returnGeneList(virtualPanel.getEssentialGenes(), virtualPanel.getOptionalGenes());

                    String essentialGenes = virtualPanel.getEssentialGenes().replaceAll("\\p{Z}", "");

                    list.addAll(Arrays.asList(essentialGenes.split(",")));

                }

                for(VariantCountByGene gene : variantCountByGenes) {
                    Label label = new Label(gene.getGeneSymbol());
                    if(discriminationOfMutation(gene)) {
                        label.getStyleClass().add("text_color_blue");
                    } else {
                        label.getStyleClass().add("text_color_white");
                    }

                    if(allGeneList == null || !list.contains(gene.getGeneSymbol())) {
                        label.getStyleClass().add("target_gene_variant_not");
                    } else {
                        label.getStyleClass().add("target_gene_variant");
                    }

                    targetGenesFlowPane.getChildren().add(label);
                    if(targetGenesFlowPane.getChildren().size() % 15 == 1) {
                        targetGenesFlowPane.setPrefHeight(targetGenesFlowPane.getPrefHeight() + 30);
                        mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() + 30);
                        contentVBox.setPrefHeight(contentVBox.getPrefHeight() + 30);
                    }
                }
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

    private void tableCellUpdateFix(TableView<VariantAndInterpretationEvidence> tableView) {
        tableView.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            tableView.refresh();
            // close text box
            tableView.edit(-1, null);
        });
    }

    private Set<String> returnGeneList(String essentialGenes, String optionalGenes) {
        Set<String> list = new HashSet<>();

        essentialGenes = essentialGenes.replaceAll("\\p{Z}", "");

        list.addAll(Arrays.asList(essentialGenes.split(",")));

        if(!StringUtils.isEmpty(optionalGenes)) {
            optionalGenes = optionalGenes.replaceAll("\\p{Z}", "");

            list.addAll(Arrays.asList(optionalGenes.split(",")));
        }

        return list;
    }

    private List<VariantAndInterpretationEvidence> filteringVariant(List<VariantAndInterpretationEvidence> list) {
        List<VariantAndInterpretationEvidence> filteringList = new ArrayList<>();
        if(!StringUtils.isEmpty(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue())) {
            try {
                HttpClientResponse response = apiService.get("virtualPanels/" + virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue(),
                        null, null, false);

                VirtualPanel virtualPanel = response.getObjectBeforeConvertResponseToJSON(VirtualPanel.class);

                Set<String> geneList = returnGeneList(virtualPanel.getEssentialGenes(), virtualPanel.getOptionalGenes());

                for(VariantAndInterpretationEvidence variantAndInterpretationEvidence : list) {
                    if(geneList.contains(variantAndInterpretationEvidence.getSnpInDel().getGenomicCoordinate().getGene())) {
                        filteringList.add(variantAndInterpretationEvidence);
                    }
                }
            } catch (WebAPIException wae) {

            }
        } else {
            return list;
        }

        if(filteringList.isEmpty()) return list;

        return filteringList;
    }

    private List<VariantCountByGene> filteringGeneList(List<VariantCountByGene> list) {
        List<VariantCountByGene> filteringList = new ArrayList<>();
        if(!StringUtils.isEmpty(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue())) {
            try {
                HttpClientResponse response = apiService.get("virtualPanels/" + virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue(),
                        null, null, false);

                VirtualPanel virtualPanel = response.getObjectBeforeConvertResponseToJSON(VirtualPanel.class);

                Set<String> geneList = returnGeneList(virtualPanel.getEssentialGenes(), virtualPanel.getOptionalGenes());

                for(VariantCountByGene variantCountByGene : list) {
                    if(geneList.contains(variantCountByGene.getGeneSymbol())) {
                        filteringList.add(variantCountByGene);
                    }
                }
            } catch (WebAPIException wae) {
                return list;
            }
        }

        if(filteringList.isEmpty()) {
            return list;
        }

        return filteringList;
    }

    void setVariantsList() {
        HttpClientResponse response = null;
        if(analysisDetailSolidCNVReportController != null) analysisDetailSolidCNVReportController.setContents();
        try {
            response = apiService.get("/analysisResults/sampleSnpInDels/" + sample.getId(), null,
                    null, false);

            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();

            list = filteringVariant(list);
            List<Cnv> cnvList = null;
            if(analysisDetailSolidCNVReportController != null) {
                response = apiService.get("/analysisResults/cnv/" + sample.getId(), null, null, null);
                PagedCnv pagedCnv = response.getObjectBeforeConvertResponseToJSON(PagedCnv.class);
                cnvList = pagedCnv.getResult().stream().filter(cnv -> cnv.getCnvValue().doubleValue() > 4.0)
                        .collect(Collectors.toList());
            }

            negativeList = list.stream().filter(item -> (
                    StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && "TN".equalsIgnoreCase(item.getSnpInDel().getSwTier())) ||
                    "TN".equalsIgnoreCase(item.getSnpInDel().getExpertTier())).collect(Collectors.toList());

            tierOne = settingTierList(list, "T1");

            tierTwo = settingTierList(list, "T2");

            tierThree = settingTierList(list, "T3");

            tierFour = settingTierList(list, "T4");
            long tier1Count = tierOne != null ? tierOne.stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0;
            long tier2Count = tierTwo != null ? tierTwo.stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0;
            long tier3Count = tierThree != null ? tierThree.stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0;
            long tier4Count = tierFour != null ? tierFour.stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0;
            tierCountLabel.setText("( SNV : " + (tier1Count + tier2Count + tier3Count + tier4Count)
                    + (tier1Count > 0 ? ", T1: " + tier1Count : "")
                    + (tier2Count > 0 ? ", T2: " + tier2Count : "")
                    + (tier3Count > 0 ? ", T3: " + tier3Count : "")
                    + (tier4Count > 0 ? ", T4: " + tier4Count : "")
                    + (cnvList != null && !cnvList.isEmpty() ?
                    " / CNV : " + cnvList.size() : "")
                    + " )"
            );

            List<VariantAndInterpretationEvidence> tableList = new ArrayList<>();

            if(tierOne != null && !tierOne.isEmpty()) {
                tierOne.sort(Comparator.comparing(variant -> variant.getSnpInDel().getGenomicCoordinate().getGene()));
                tableList.addAll(tierOne);
            }

            if(tierTwo != null && !tierTwo.isEmpty()) {
                tierTwo.sort(Comparator.comparing(variant -> variant.getSnpInDel().getGenomicCoordinate().getGene()));
                tableList.addAll(tierTwo);
            }

            if(tierThree != null && !tierThree.isEmpty()) {
                tierThree.sort(Comparator.comparing(variant -> variant.getSnpInDel().getGenomicCoordinate().getGene()));
                tableList.addAll(tierThree);
            }

            if(tierFour != null && !tierFour.isEmpty()) {
                tierFour.sort(Comparator.comparing(variant -> variant.getSnpInDel().getGenomicCoordinate().getGene()));
                tableList.addAll(tierFour);
            }

            tableList = tableList.stream().filter(item -> item.getSnpInDel().getIncludedInReport().equals("Y"))
                    .collect(Collectors.toList());

            if(variantsTable.getItems() != null && !variantsTable.getItems().isEmpty()) {
                variantsTable.getItems().removeAll(variantsTable.getItems());
            }

            Objects.requireNonNull(variantsTable.getItems()).addAll(tableList);

        } catch (WebAPIException wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getContents(), this.getMainApp().getPrimaryStage(), true);
        }
    }

    private boolean discriminationOfMutation(VariantCountByGene gene) {
        return (gene.getTier3IndelCount() > 0 || gene.getTier3SnpCount() > 0 ||
                gene.getTier1IndelCount() > 0 || gene.getTier1SnpCount() > 0 ||
                gene.getTier2IndelCount() > 0 || gene.getTier2SnpCount() > 0);
    }

    private void initialize(Panel panel) {
        virtualPanelComboBox.setConverter(new ComboBoxConverter());
        virtualPanelComboBox.getItems().add(new ComboBoxItem());
        virtualPanelComboBox.getSelectionModel().selectFirst();
        Task<Void> task = new Task<Void>() {
            PagedVirtualPanel pagedVirtualPanel;
            ReportContents reportContents;
            ReportTemplate template;
            SampleReport sampleReport;
            @Override
            protected Void call() throws Exception {
                Map<String, Object> params = new HashMap<>();
                params.put("panelId", panel.getId());
                HttpClientResponse response = apiService.get("virtualPanels", params, null, false);
                pagedVirtualPanel = response.getObjectBeforeConvertResponseToJSON(PagedVirtualPanel.class);

                if(panel.getReportTemplateId() != null) {
                    response = apiService.get("reportTemplate/" + panel.getReportTemplateId(), null, null, false);
                    reportContents = response.getObjectBeforeConvertResponseToJSON(ReportContents.class);
                    template = reportContents.getReportTemplate();
                }
                if(sample.getSampleStatus().getReportStartedAt() != null) {
                    response = apiService.get("sampleReport/" + sample.getId(), null, null, false);
                    if (response.getStatus() == 200) {
                        reportData = true;
                        sampleReport = response.getObjectBeforeConvertResponseToJSON(SampleReport.class);
                    }
                }

                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if(pagedVirtualPanel.getCount() > 0) {
                    for(VirtualPanel virtualPanel : pagedVirtualPanel.getResult()) {
                        virtualPanelComboBox.getItems().add(new ComboBoxItem(virtualPanel.getId().toString(), virtualPanel.getName()));
                    }
                }
                if(panel.getReportTemplateId() != null) {
                    if (template.getContents() != null) {
                        extraFields.setVisible(true);
                        Map<String, Object> localVariableList = JsonUtil.fromJsonToMap(template.getCustomFields());
                        if (localVariableList != null && !localVariableList.isEmpty()) {
                            variableList = localVariableList;
                            Set<String> keyList = localVariableList.keySet();

                            List<String> sortedKeyList = keyList.stream().sorted().collect(Collectors.toList());

                            if (keyList.contains("conclusions")) {
                                Map<String, String> item = (Map<String, String>) variableList.get("conclusions");
                                conclusions.setText(item.get("displayName"));
                                sortedKeyList.remove("conclusions");
                                //conclusions.setStyle("-fx-font-family: \"Noto Sans KR Bold\"");
                            }

                            int gridPaneRowSize = (int) Math.ceil(sortedKeyList.size() / 3.0);

                            for (int i = 0; i < gridPaneRowSize; i++) {
                                customFieldGridPane.setPrefHeight(customFieldGridPane.getPrefHeight() + 30);
                                mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() + 30);
                                contentVBox.setPrefHeight(contentVBox.getPrefHeight() + 30);
                                RowConstraints con = new RowConstraints();
                                con.setPrefHeight(30);
                                customFieldGridPane.getRowConstraints().add(con);
                            }

                            int rowIndex = 0;
                            int colIndex = 0;

                            for (String key : sortedKeyList) {
                                Map<String, String> item = (Map<String, String>) variableList.get(key);
                                if (colIndex == 6) {
                                    colIndex = 0;
                                    rowIndex++;
                                }
                                Label label = new Label(item.get("displayName"));
                                label.setStyle("-fx-text-fill : #595959;");
                                customFieldGridPane.add(label, colIndex++, rowIndex);
                                label.setMaxHeight(Double.MAX_VALUE);
                                label.setMaxWidth(Double.MAX_VALUE);
                                label.setAlignment(Pos.CENTER);

                                String type = item.get("variableType");
                                if (type.equalsIgnoreCase("Date")) {
                                    DatePicker datePicker = new DatePicker();
                                    datePicker.setStyle(datePicker.getStyle() + "-fx-text-inner-color: black; -fx-control-inner-background: white;");
                                    String dateType = "yyyy-MM-dd";
                                    datePicker.setPromptText(dateType);
                                    datePicker.setConverter(DatepickerConverter.getConverter(dateType));
                                    datePicker.setId(key);
                                    customFieldGridPane.add(datePicker, colIndex++, rowIndex);
                                } else if (type.equalsIgnoreCase("ComboBox")) {
                                    ComboBox<String> comboBox = new ComboBox<>();
                                    comboBox.getStyleClass().add("txt_black");
                                    comboBox.setId(key);
                                    String list = item.get("comboBoxItemList");
                                    String[] comboBoxItem = list.split("&\\^\\|");
                                    comboBox.getItems().addAll(comboBoxItem);
                                    comboBox.getSelectionModel().selectFirst();

                                    customFieldGridPane.add(comboBox, colIndex++, rowIndex);
                                } else {
                                    TextField textField = new TextField();
                                    textField.getStyleClass().add("txt_black");
                                    textField.setId(key);
                                    if (type.equalsIgnoreCase("Integer")) {
                                        textField.textProperty().addListener((observable, oldValue, newValue) -> {
                                            if (!newValue.matches("[0-9]*")) textField.setText(oldValue);
                                        });
                                    }
                                    customFieldGridPane.add(textField, colIndex++, rowIndex);
                                }
                            }
                        }
                    }
                } else {
                    excelUploadBtn.setVisible(false);
                    excelTemplateBtn.setVisible(false);
                }
                if(sample.getSampleStatus().getReportStartedAt() != null && sampleReport != null &&
                        sampleReport.getVirtualPanelId() != null) {
                    Optional<ComboBoxItem> item = virtualPanelComboBox.getItems().stream().filter(
                            comboBoxItem -> comboBoxItem.getValue().equals(sampleReport.getVirtualPanelId().toString())).findFirst();
                    item.ifPresent(comboBoxItem -> virtualPanelComboBox.getSelectionModel().select(comboBoxItem));
                }
                if(sampleReport != null) {
                    settingReportData(sampleReport.getContents());
                }
                virtualPanelComboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
                    if(!t1.equals(t)) setVariantsList();
                    if(!t1.equals(t)) setTargetGenesList();
                });
                setTargetGenesList();
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

    private List<VariantAndInterpretationEvidence> settingTierList(List<VariantAndInterpretationEvidence> allTierList, String tier) {
        if(!StringUtils.isEmpty(tier)) {
            return allTierList.stream().filter(item -> (tier.equalsIgnoreCase(item.getSnpInDel().getExpertTier()) ||
                    (StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && item.getSnpInDel().getSwTier().equalsIgnoreCase(tier))))
                    .collect(Collectors.toList());
        }

        return null;
    }

    private void settingReportData(String contents) {

        Map<String,Object> contentsMap = JsonUtil.fromJsonToMap(contents);

        if(contentsMap.containsKey("contents")) conclusionsTextArea.setText((String)contentsMap.get("contents"));

        for(int i = 0; i < customFieldGridPane.getChildren().size(); i++) {
            Object gridObject = customFieldGridPane.getChildren().get(i);

            if(gridObject instanceof TextField) {
                TextField textField = (TextField)gridObject;
                if(contentsMap.containsKey(textField.getId())) textField.setText((String)contentsMap.get(textField.getId()));
            } else if(gridObject instanceof DatePicker) {
                DatePicker datePicker = (DatePicker)gridObject;
                if(contentsMap.containsKey(datePicker.getId())) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    datePicker.setValue(LocalDate.parse((String)contentsMap.get(datePicker.getId()), formatter));
                }
            } else if(gridObject instanceof ComboBox) {
                ComboBox comboBox = (ComboBox)gridObject;
                if(contentsMap.containsKey(comboBox.getId())) comboBox.getSelectionModel().select(contentsMap.get(comboBox.getId()));
            }
        }

    }

    @SuppressWarnings("unchecked")
    /**
     * 입력 정보 저장
     * @return boolean
     */
    private Task<Boolean> getSaveDataTask(boolean showResultDialog) {

        String conclusionsText = conclusionsTextArea.getText();

        Map<String, Object> params = new HashMap<>();

        //params.put("sampleId", sample.getId());

        Map<String, Object> contentsMap = new HashMap<>();

        if(!StringUtils.isEmpty(conclusionsText)) contentsMap.put("contents", conclusionsText);

        for(int i = 0; i < customFieldGridPane.getChildren().size(); i++) {
            Object gridObject = customFieldGridPane.getChildren().get(i);

            if(gridObject instanceof TextField) {
                TextField textField = (TextField)gridObject;
                if(!StringUtils.isEmpty(textField.getText())) contentsMap.put(textField.getId(), textField.getText());
            } else if(gridObject instanceof DatePicker) {
                DatePicker datePicker = (DatePicker)gridObject;
                if(datePicker.getValue() != null) {
                    contentsMap.put(datePicker.getId(), datePicker.getValue().toString());
                }
            } else if(gridObject instanceof ComboBox) {
                ComboBox<String> comboBox = (ComboBox<String>) gridObject;
                String value = comboBox.getSelectionModel().getSelectedItem();
                if(!StringUtils.isEmpty(value)) contentsMap.put(comboBox.getId(), value);
            }
        }
        String contents = JsonUtil.toJson(contentsMap);
        params.put("contents", contents);
        if(!StringUtils.isEmpty(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue())) {
            params.put("virtualPanelId", Integer.parseInt(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue()));
        }
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if(reportData) {
                    apiService.put("/sampleReport/" + sample.getId(), params, null, true);
                } else {
                    apiService.post("/sampleReport/" + sample.getId(), params, null, true);
                }
                return true;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                getMainController().setMainMaskerPane(false);
                if(showResultDialog) {
                    DialogUtil.alert("", "Data saving is completed.", getMainController().getPrimaryStage(), false);
                }
            }

            @Override
            protected void failed() {
                super.failed();
                getMainController().setMainMaskerPane(false);
                if(showResultDialog) {
                    DialogUtil.showWebApiException(getException(), getMainApp().getPrimaryStage());
                }
            }
        };
    }

    @FXML
    public void save() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        DialogUtil.setIcon(alert);
        alert.initOwner(getMainController().getPrimaryStage());
        alert.setTitle(CONFIRMATION_DIALOG);
        alert.setHeaderText("Save Report Information");
        alert.setContentText("Do you want to save?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            Task<Boolean> task = getSaveDataTask(true);
            getMainController().setMainMaskerPane(true);
            Thread thread = new Thread(task);
            thread.start();
        } else {
            alert.close();
        }
    }

    @FXML
    public void createPDFAsDraft() {
        try {
            Task<Boolean> saveDataTask = getSaveDataTask(false);
            saveDataTask.run();
            Thread saveDataThread = new Thread(saveDataTask);
            saveDataThread.start();
            saveDataThread.join();
            if(saveDataTask.get() != null) {
                createPDF(true);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void createPDFAsFinal() {
        Optional<ButtonType> result;
        Alert alert = null;
        if(PipelineCode.isBRCAPipeline(panel.getCode())) {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            DialogUtil.setIcon(alert);
            alert.initOwner(getMainController().getPrimaryStage());
            alert.setTitle(CONFIRMATION_DIALOG);
            alert.setHeaderText("");
            alert.setContentText("Do you want to proceed?");
            result = alert.showAndWait();
        } else {
            result = Optional.of(ButtonType.OK);
        }

        if(!PipelineCode.isBRCAPipeline(panel.getCode()) || result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Task<Boolean> saveDataTask = getSaveDataTask(false);
                saveDataTask.run();
                Thread saveDataThread = new Thread(saveDataTask);
                saveDataThread.start();
                saveDataThread.join();
                if(saveDataTask.get() != null) {
                    createPDF(false);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            if (alert != null) {
                alert.close();
            }
        }
    }

    @FXML
    public void confirmPDFAsFinal() {
        createPDF(false);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> contents() throws WebAPIException {
        Map<String,Object> contentsMap = new HashMap<>();
        contentsMap.put("panelName", panel.getName());
        contentsMap.put("diseaseName", sample.getDiseaseName());
        contentsMap.put("sampleSource", sample.getSampleSource());
        contentsMap.put("panelCode", panel.getCode());
        contentsMap.put("sampleName", sample.getName());
        contentsMap.put("patientCode", "SS17-01182");
        SecureRandom random = new SecureRandom();
        contentsMap.put("reportID", String.format("%05d-%05d", sample.getId(), random.nextInt(99999)));

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM. dd yyyy");
        contentsMap.put("date", sdf.format(date));

        List<VariantAndInterpretationEvidence> variantList = new ArrayList<>();
        if(tierOne != null && !tierOne.isEmpty()) variantList.addAll(tierOne);
        if(tierTwo != null && !tierTwo.isEmpty()) variantList.addAll(tierTwo);

        List<VariantAndInterpretationEvidence> negativeResult = new ArrayList<>();
        //리포트에서 제외된 negative 정보를 제거
        if(negativeList != null && !negativeList.isEmpty()) {
            negativeResult.addAll(negativeList.stream().filter(item -> item.getSnpInDel().getIncludedInReport().equals("Y")).collect(Collectors.toList()));
        }
        //리포트에서 제외된 variant를 제거
        if(!variantList.isEmpty()) {
            variantList = variantList.stream().filter(item -> item.getSnpInDel().getIncludedInReport().equals("Y")).collect(Collectors.toList());
        }

        List<VariantAndInterpretationEvidence> clinicalVariantList = new ArrayList<>();

        clinicalVariantList.addAll(variantList);

        //if(tierThree != null && !tierThree.isEmpty()) variantList.addAll(tierThree);
        if(tierThree != null && !tierThree.isEmpty()) variantList.addAll(tierThree.stream().filter(tierThree ->
                tierThree.getSnpInDel().getIncludedInReport().equalsIgnoreCase("Y")).collect(Collectors.toList()));

        for(VariantAndInterpretationEvidence variant : variantList) {
            variant.getSnpInDel().getSnpInDelExpression().setTranscript(ConvertUtil.insertTextAtFixedPosition(variant.getSnpInDel().getSnpInDelExpression().getTranscriptAccession(), 15, "\n"));
            variant.getSnpInDel().getSnpInDelExpression().setNtChange(ConvertUtil.insertTextAtFixedPosition(variant.getSnpInDel().getSnpInDelExpression().getNtChange(), 15, "\n"));
            variant.getSnpInDel().getSnpInDelExpression().setAaChange(ConvertUtil.insertTextAtFixedPosition(variant.getSnpInDel().getSnpInDelExpression().getAaChange(), 15, "\n"));
        }

        Integer evidenceACount = 0;
        Integer evidenceBCount = 0;
        Integer evidenceCCount = 0;
        Integer evidenceDCount = 0;

        if(tierOne != null && !tierOne.isEmpty()) {
            for(VariantAndInterpretationEvidence variant : tierOne.stream().filter(tierOne ->
                    tierOne.getSnpInDel().getIncludedInReport().equalsIgnoreCase("Y")).collect(Collectors.toList())) {
                if("Y".equalsIgnoreCase(variant.getSnpInDel().getIncludedInReport()) && ConvertUtil.findPrimaryEvidence(variant.getSnpInDelEvidences()) != null) {
                    SnpInDelEvidence snpInDelEvidence = ConvertUtil.findPrimaryEvidence(variant.getSnpInDelEvidences());
                    switch (snpInDelEvidence.getEvidenceLevel()) {
                        case "A":
                            evidenceACount++;
                            break;
                        case "B":
                            evidenceBCount++;
                            break;
                        case "C":
                            evidenceCCount++;
                            break;
                        case "D":
                            evidenceDCount++;
                            break;
                        default:
                    }
                    /*if("T2".equals(variant.getSnpInDel().getSwTier())
                            && StringUtils.isEmpty(variant.getInterpretationEvidence().getTherapeuticEvidence().getLevelB())) evidenceBCount++;*/
                }
            }
        }

        if(tierTwo != null && !tierTwo.isEmpty()) {
            for(VariantAndInterpretationEvidence variant : tierTwo.stream().filter(tierTwo ->
                    tierTwo.getSnpInDel().getIncludedInReport().equalsIgnoreCase("Y")).collect(Collectors.toList())) {
                if("Y".equalsIgnoreCase(variant.getSnpInDel().getIncludedInReport()) && ConvertUtil.findPrimaryEvidence(variant.getSnpInDelEvidences()) != null) {
                    SnpInDelEvidence snpInDelEvidence = ConvertUtil.findPrimaryEvidence(variant.getSnpInDelEvidences());
                    switch (snpInDelEvidence.getEvidenceLevel()) {
                        case "A":
                            evidenceACount++;
                            break;
                        case "B":
                            evidenceBCount++;
                            break;
                        case "C":
                            evidenceCCount++;
                            break;
                        case "D":
                            evidenceDCount++;
                            break;
                        default:
                    }
                    /*if("T1".equals(variant.getSnpInDel().getSwTier())
                            && StringUtils.isEmpty(variant.getInterpretationEvidence().getTherapeuticEvidence().getLevelD())) evidenceDCount++;*/
                }
            }
        }

        contentsMap.put("clinicalVariantList", clinicalVariantList);
        contentsMap.put("variantList", variantList);
        contentsMap.put("tierThreeVariantList", tierThree);
        contentsMap.put("tierOneCount", (int)tierOne.stream().filter(item -> item.getSnpInDel().getIncludedInReport().equals("Y")).count());
        contentsMap.put("tierTwoCount", (int)tierTwo.stream().filter(item -> item.getSnpInDel().getIncludedInReport().equals("Y")).count());
        contentsMap.put("tierThreeCount", (int)tierThree.stream().filter(item -> item.getSnpInDel().getIncludedInReport().equals("Y")).count());
        contentsMap.put("tierFourCount", (tierFour != null) ? tierFour.size() : 0);
        contentsMap.put("evidenceACount", evidenceACount);
        contentsMap.put("evidenceBCount", evidenceBCount);
        contentsMap.put("evidenceCCount", evidenceCCount);
        contentsMap.put("evidenceDCount", evidenceDCount);
        contentsMap.put("negativeList", negativeResult);

        //Genes in panel

        HttpClientResponse response = apiService.get("/analysisResults/variantCountByGeneForSomaticDNA/" + sample.getId(),
                null, null, false);
        if (response != null) {
            List<VariantCountByGene> variantCountByGenes = (List<VariantCountByGene>) response
                    .getMultiObjectBeforeConvertResponseToJSON(VariantCountByGene.class,
                            false);

            variantCountByGenes = filteringGeneList(variantCountByGenes);

            variantCountByGenes = variantCountByGenes.stream().sorted(Comparator.comparing(VariantCountByGene::getGeneSymbol)).collect(Collectors.toList());

            contentsMap.put("variantCountByGenes", variantCountByGenes);
            int geneTableMaxRowCount = (int)Math.ceil(variantCountByGenes.size() / 7.0);
            int geneTableMaxRowCount4 = (int)Math.ceil(variantCountByGenes.size() / 4.0);
            contentsMap.put("geneTableCount", (7 * geneTableMaxRowCount) - 1);
            contentsMap.put("geneTableCount4", (4 * geneTableMaxRowCount4) - 1);

            int tableOneSize = (int)Math.ceil((double)variantCountByGenes.size() / 3);
            int tableTwoSize = (int)Math.ceil((double)(variantCountByGenes.size() - tableOneSize) / 2);

            Object[] genesInPanelTableOne = variantCountByGenes.toArray();
            //Gene List를 3등분함
            contentsMap.put("genesInPanelTableOne", Arrays.copyOfRange(genesInPanelTableOne, 0, tableOneSize));
            contentsMap.put("genesInPanelTableTwo", Arrays.copyOfRange(genesInPanelTableOne, tableOneSize, tableOneSize + tableTwoSize));
            contentsMap.put("genesInPanelTableThree", Arrays.copyOfRange(genesInPanelTableOne, tableOneSize + tableTwoSize, variantCountByGenes.size()));

            if(!StringUtils.isEmpty(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue())) {
                response = apiService.get("virtualPanels/" + virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue(),
                        null, null, false);

                VirtualPanel virtualPanel = response.getObjectBeforeConvertResponseToJSON(VirtualPanel.class);

                Set<String> list = new HashSet<>();

                list.addAll(Arrays.asList(virtualPanel.getEssentialGenes().replaceAll("\\p{Z}", "")
                        .split(",")));

                Set<String> allGeneList = returnGeneList(virtualPanel.getEssentialGenes(), virtualPanel.getOptionalGenes());

                contentsMap.put("essentialGenes", list);
                contentsMap.put("allGeneList", allGeneList);
                contentsMap.put("virtualPanelName", virtualPanel.getName());

            }

            String runSequencer = sample.getRun().getSequencingPlatform();

            if (runSequencer.equalsIgnoreCase("MISEQ")) {
                contentsMap.put("sequencer", SequencerCode.MISEQ.getDescription());
            } else {
                contentsMap.put("sequencer", SequencerCode.MISEQ_DX.getDescription());
            }

            response = apiService.get("/analysisResults/sampleQCs/" + sample.getId(), null,
                    null, false);

            List<SampleQC> qcList = (List<SampleQC>) response.getMultiObjectBeforeConvertResponseToJSON(SampleQC.class, false);

            if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode())) {
                contentsMap.put("q30ScoreRead1", findQCResult(qcList, "Q30_score_read1"));
                contentsMap.put("q30ScoreRead2", findQCResult(qcList, "Q30_score_read2"));
                contentsMap.put("coverageMAD", findQCResult(qcList, "Coverage_MAD"));
                contentsMap.put("medianBinCountCNVTargets", findQCResult(qcList, "Median_BinCount_CNV_Targets"));
                contentsMap.put("medianInsertSize", findQCResult(qcList, "Median_Insert_Size"));
                contentsMap.put("pctExonBases100X", findQCResult(qcList, "PCT_ExonBases_100X"));
                contentsMap.put("readsPF", findQCResult(qcList, "Reads_PF"));
            } else {
                contentsMap.put("mappingQuality", findQCResult(qcList, "mapping_quality_60"));
                contentsMap.put("uniformity", findQCResult(qcList, "uniformity_0.2"));
                contentsMap.put("uniformity05", findQCResult(qcList, "uniformity_0.5"));
                contentsMap.put("totalBase", findQCResult(qcList, "total_base"));
                contentsMap.put("q30", findQCResult(qcList, "q30_trimmed_base"));
                contentsMap.put("mappedBase", findQCResult(qcList, "mapped_base"));
                contentsMap.put("onTarget", findQCResult(qcList, "on_target"));
                contentsMap.put("onTargetCoverage", findQCResult(qcList, "on_target_coverage"));
                contentsMap.put("duplicatedReads", findQCResult(qcList, "duplicated_reads"));
                contentsMap.put("roiCoverage", findQCResult(qcList, "roi_coverage"));
            }

            if(sample.getPipelineVersion() != null) {
                //response = apiService.get("/pipelineVersions/" + sample.getPipelineVersionId(), null, null, false);
                //PipelineVersion pipelineVersion = response.getObjectBeforeConvertResponseToJSON(PipelineVersion.class);
                contentsMap.put("pipelineVersion", sample.getPipelineVersion());
            }

            List<String> conclusionLineList = null;
            if(!StringUtils.isEmpty(conclusionsTextArea.getText())) {
                conclusionLineList = new ArrayList<>();
                String[] lines = conclusionsTextArea.getText().split("\n");
                if(lines != null && lines.length > 0) {
                    conclusionLineList.addAll(Arrays.asList(lines));
                } else {
                    conclusionLineList.add(conclusionsTextArea.getText());
                }
            }
            contentsMap.put("conclusions", conclusionLineList);

            Map<String, Object> analysisFileMap = new HashMap<>();
            analysisFileMap.put("sampleId", sample.getId());
            response = apiService.get("/analysisFiles", analysisFileMap, null, false);
            AnalysisFileList analysisFileList = response.getObjectBeforeConvertResponseToJSON(AnalysisFileList.class);

            List<AnalysisFile> analysisFiles = analysisFileList.getResult();

            Optional<AnalysisFile> optionalAnalysisFile = analysisFiles.stream()
                    .filter(item -> item.getName().contains("cnv_plot.png")).findFirst();

            if(optionalAnalysisFile.isPresent()) {
                contentsMap.put("cnvImagePath", optionalAnalysisFile.get().getName());
                FileUtil.downloadCNVImage(optionalAnalysisFile.get());
            }
            if(analysisDetailSolidCNVReportController != null) {
                try {
                    response = apiService.get("/analysisResults/cnv/" + sample.getId(), null, null, null);
                    PagedCnv pagedCNV = response.getObjectBeforeConvertResponseToJSON(PagedCnv.class);
                    contentsMap.put("cnvList", pagedCNV.getResult());

                } catch (WebAPIException wae) {
                    logger.debug(wae.getMessage());
                }
            }
        }

        return contentsMap;
    }

    @FXML
    private void createExcelTemplate() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Microsoft Worksheet(*.xlsx)", "*.xlsx")
                ,new FileChooser.ExtensionFilter("Microsoft Worksheet(*.xls)", "*.xls"));
        fileChooser.setTitle("format file");
        fileChooser.setInitialFileName("SampleSheet");
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if(file != null) {
            ExcelConvertReportInformationService.createExcelTemplate(file, variableList, mainApp.getPrimaryStage());
        }
    }

    private boolean createPDF(boolean isDraft) {
        // TODO 쓰레드 적용
        boolean created = true;
        String reportCreationErrorMsg = "An error occurred during the creation of the report document.";

        try {
            String outputType = "PDF";
            if(panel.getReportTemplateId() != null) {
                HttpClientResponse response = apiService.get("reportTemplate/" + panel.getReportTemplateId(), null, null, false);
                ReportContents reportContents = response.getObjectBeforeConvertResponseToJSON(ReportContents.class);
                outputType = reportContents.getReportTemplate().getOutputType();
            }

            // 보고서 파일명 기본값
            String baseSaveName = String.format("FINAL_Report_%s", sample.getName());
            if(isDraft) {
                baseSaveName = String.format("DRAFT_Report_%s", sample.getName());
            }

            // Show save bedFile dialog
            FileChooser fileChooser = new FileChooser();
            if(outputType != null && outputType.equalsIgnoreCase("MS_WORD")) {
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF (*.docx)", "*.docx"));
            } else if(outputType != null && outputType.equalsIgnoreCase("PDF")){
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
            } else if(outputType != null && outputType.equalsIgnoreCase("IMG")){
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG (*.jpg)", "*.jpg"));
            }
            fileChooser.setInitialFileName(baseSaveName);
            File file = fileChooser.showSaveDialog(this.getMainApp().getPrimaryStage());

            if(file != null) {

                Map<String, Object> contentsMap = contents();
                contentsMap.put("isDraft", isDraft);

                String draftImageStr = String.format("url('%s')", this.getClass().getClassLoader().getResource("layout/images/DRAFT.png"));
                String ngenebioLogo = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/ngenebio_logo.png"));
                String testInformationText = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/test_information1.png"));
                String pathogenicMutationsDetectedText = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/pathogenic_mutations_detected1.png"));
                String pertinetNegativeText = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/pertinent_negative.png"));
                String variantDetailText = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/variant_detail.png"));
                String dataQcText = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/data_qc.png"));
                Map<String, Object> model = new HashMap<>();
                model.put("isDraft", isDraft);
                //model.put("qcResult", sample.getQc());
                model.put("draftImageURL", draftImageStr);
                model.put("ngenebioLogo", ngenebioLogo);
                model.put("testInformationText", testInformationText);
                model.put("pathogenicMutationsDetectedText", pathogenicMutationsDetectedText);
                model.put("pertinetNegativeText", pertinetNegativeText);
                model.put("variantDetailText", variantDetailText);
                model.put("dataQcText", dataQcText);
                model.put("contents", contentsMap);

                String contents = "";
                if(panel.getReportTemplateId() == null) {
                    if(panel.getCode().equals(PipelineCode.TST170_DNA.getCode())) {
                        contents = velocityUtil.getContents("/layout/velocity/report_tst.vm", "UTF-8", model);
                    } else {
                        contents = velocityUtil.getContents("/layout/velocity/report.vm", "UTF-8", model);
                    }
                    created = pdfCreateService.createPDF(file, contents);
                    createdCheck(created, file);
                    //convertPDFtoImage(file, sample.getName());
                } else {
                    for (int i = 0; i < customFieldGridPane.getChildren().size(); i++) {
                        Object gridObject = customFieldGridPane.getChildren().get(i);

                        if (gridObject instanceof TextField) {
                            TextField textField = (TextField) gridObject;
                            contentsMap.put(textField.getId(), textField.getText());
                        } else if (gridObject instanceof DatePicker) {
                            DatePicker datePicker = (DatePicker) gridObject;
                            if (datePicker.getValue() != null) {
                                contentsMap.put(datePicker.getId(), datePicker.getValue().toString());
                            } else {
                                contentsMap.put(datePicker.getId(), "");
                            }
                        } else if (gridObject instanceof ComboBox) {
                            ComboBox<String> comboBox = (ComboBox<String>) gridObject;
                            contentsMap.put(comboBox.getId(), comboBox.getSelectionModel().getSelectedItem());
                        }
                    }

                    HttpClientResponse response = apiService.get("reportTemplate/" + panel.getReportTemplateId(), null, null, false);

                    final ReportContents reportContents = response.getObjectBeforeConvertResponseToJSON(ReportContents.class);

                    if(reportContents.getReportTemplate().getOutputType() != null
                            && reportContents.getReportTemplate().getOutputType().equalsIgnoreCase("MS_WORD")) {
                        List<ReportComponent> components = reportContents.getReportComponents();

                        if(components == null || components.isEmpty()) throw new Exception();
                        final Comparator<ReportComponent> comp = (p1, p2) -> Integer.compare( p1.getId(), p2.getId());
                        final ReportComponent component = components.stream().max(comp).get();
                        final String filePath = CommonConstants.BASE_FULL_PATH + File.separator + "word" + File.separator + component.getId();
                        File jarFile = new File(filePath, component.getName());

                        components.remove(component);

                        if(!components.isEmpty()) {
                            for (ReportComponent cmp : components) {
                                File oldVersionFolder = new File(CommonConstants.BASE_FULL_PATH + File.separator + "word" + File.separator + cmp.getId());
                                if(oldVersionFolder.exists()) {
                                    FileUtils.deleteQuietly(oldVersionFolder);
                                }
                            }
                        }

                        if(!jarFile.exists()) {
                            File folder = new File(filePath);
                            if (!folder.exists()){
                                if(!folder.mkdirs()) {
                                    throw new Exception("Fail to make jarFile directory");
                                }
                            }

                            Task task = new JarDownloadTask(this, component);

                            Thread thread = new Thread(task);
                            thread.setDaemon(true);
                            thread.start();

                            task.setOnSucceeded(ev -> {
                                try {
                                    final File jarFile1 = new File(filePath, component.getName());
                                    URL[] jarUrls = new URL[]{jarFile1.toURI().toURL()};
                                    createWordFile(jarUrls, file, contentsMap, reportCreationErrorMsg);
                                } catch (MalformedURLException murle) {
                                    DialogUtil.error("Report Generation Fail", reportCreationErrorMsg + "\n" + murle.getMessage(), getMainApp().getPrimaryStage(), false);
                                    murle.printStackTrace();
                                }
                            });
                            task.exceptionProperty().addListener((observable, oldValue, newValue) ->
                                DialogUtil.error("Report Generation Fail",
                                        ((Exception)newValue).getMessage(), getMainApp().getPrimaryStage(), false));
                        } else {
                            URL[] jarUrls = new URL[]{jarFile.toURI().toURL()};
                            createWordFile(jarUrls, file, contentsMap, reportCreationErrorMsg);
                        }

                    } else {
                        List<ReportImage> images = reportContents.getReportImages();

                        for (ReportImage image : images) {
                            String path = "url('file:/" + CommonConstants.BASE_FULL_PATH + File.separator + "fop" + File.separator + image.getReportTemplateId()
                                    + File.separator + image.getName() + "')";
                            path = path.replaceAll("\\\\", "/");
                            String name = image.getName().substring(0, image.getName().lastIndexOf('.'));
                            logger.debug(name + " : " + path);
                            model.put(name, path);
                        }

                        FileUtil.saveVMFile(reportContents.getReportTemplate());

                        Task task = new ImageFileDownloadTask(this, reportContents.getReportImages());

                        Thread thread = new Thread(task);
                        thread.setDaemon(true);
                        thread.start();

                        final String contents1 = velocityUtil.getContents(reportContents.getReportTemplate().getId() + "/" + reportContents.getReportTemplate().getName() + ".vm", "UTF-8", model);
                        task.setOnSucceeded(ev -> {
                            try {
                                if(reportContents.getReportTemplate().getOutputType() != null
                                        && reportContents.getReportTemplate().getOutputType().equalsIgnoreCase("PDF")) {
                                    final boolean created1 = pdfCreateService.createPDF(file, contents1);
                                    createdCheck(created1, file);
                                } else {
                                    String path = CommonConstants.BASE_FULL_PATH + File.separator + "temp" + File.separator + "tempPDF.pdf";
                                    File tempPDFFile = new File(path);
                                    final boolean created1 = pdfCreateService.createPDF(tempPDFFile, contents1);
                                    createdCheck(created1, tempPDFFile);
                                    ImageService.convertPDFtoImage(tempPDFFile, sample.getName(), file);
                                }

                            } catch (Exception e) {
                                DialogUtil.error("Save Fail.", reportCreationErrorMsg + "\n" + e.getMessage(), getMainApp().getPrimaryStage(), false);
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }
        } catch(FileNotFoundException fnfe){
            DialogUtil.error("Save Fail.", reportCreationErrorMsg + "\n" + fnfe.getMessage(), getMainApp().getPrimaryStage(), false);
        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            DialogUtil.error("Save Fail.", reportCreationErrorMsg + "\n" + e.getMessage(), getMainApp().getPrimaryStage(), false);
            created = false;
        }

        return created;
    }

    @SuppressWarnings("unchecked")
    private void createWordFile(URL[] jarUrls, File file , Map<String, Object> contentsMap, String reportCreationErrorMsg) {

        try (URLClassLoader classLoader = new URLClassLoader(jarUrls, ClassLoader.getSystemClassLoader())) {
            Class classToLoad = Class.forName("word.create.App", true, classLoader);
            logger.debug("application init..");
            Method setParams = classToLoad.getMethod("setParams", Map.class);
            Method updateEmbeddedDoc = classToLoad.getMethod("updateEmbeddedDoc");
            Method updateWordFile = classToLoad.getDeclaredMethod("updateWordFile");
            Method setWriteFilePath = classToLoad.getDeclaredMethod("setWriteFilePath", String.class);
            Object application = classToLoad.newInstance();
            setParams.invoke(application, contentsMap);
            setWriteFilePath.invoke(application, file.getPath());
            updateEmbeddedDoc.invoke(application);
            updateWordFile.invoke(application);
            createdCheck(true, file);
        } catch (Exception e) {
            DialogUtil.error("Save Fail.", reportCreationErrorMsg + "\n" + e.getMessage(), getMainApp().getPrimaryStage(), false);
            e.printStackTrace();
        }
    }

    private void createdCheck(boolean created, File file) {
        try {
            if (created) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                DialogUtil.setIcon(alert);
                alert.initOwner(getMainController().getPrimaryStage());
                alert.setTitle(CONFIRMATION_DIALOG);
                alert.setHeaderText("");
                alert.setContentText("Creating the report document was completed. Do you want to check the report document?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    getMainApp().getHostServices().showDocument(file.toURI().toURL().toExternalForm());
                } else {
                    alert.close();
                }
            } else {
                DialogUtil.error("Save Fail.", "An error occurred during the creation of the report document.",
                        getMainApp().getPrimaryStage(), false);
            }
        } catch (Exception e) {
            DialogUtil.error("Save Fail.", "An error occurred during the creation of the report document.",
                    getMainApp().getPrimaryStage(), false);
        }
    }

    private SampleQC findQCResult(List<SampleQC> qcList, String qc) {
        if(qcList != null && !qcList.isEmpty()) {
            Optional<SampleQC> findQC = qcList.stream().filter(sampleQC -> sampleQC.getQcType().equalsIgnoreCase(qc)).findFirst();
            if(findQC.isPresent()) {
                    SampleQC qcData = findQC.get();
                    String number = findQC.get().getQcValue().toString();
                    Double value = Double.parseDouble(number);
                if(qc.equalsIgnoreCase("total_base")) {
                    qcData.setQcUnit("Mb");
                    qcData.setQcValue(BigDecimal.valueOf(value / 1000 / 1000).setScale(1, BigDecimal.ROUND_HALF_UP));
                    return qcData;
                }
                qcData.setQcValue(BigDecimal.valueOf(value).setScale(1, BigDecimal.ROUND_HALF_UP));
                return qcData;
            }
        }
        return null;
    }

    @FXML
    private void excelUpload() {
        FileChooser fileChooser = new FileChooser();

            fileChooser.getExtensionFilters()
                    .addAll(new FileChooser.ExtensionFilter("Microsoft Worksheet(*.xlsx, *.xls)", "*.xlsx", "*.xls"));
        fileChooser.setTitle("format file");
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if(file != null) {
            excelFile = file;
            if(variableList != null && !variableList.isEmpty()) {
                ExcelConvertReportInformationService.convertExcelData(sample.getName(),
                        excelFile, customFieldGridPane, variableList, mainController.getPrimaryStage());
            }
        }
    }
}
