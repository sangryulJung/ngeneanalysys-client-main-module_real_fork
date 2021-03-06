package ngeneanalysys.controller;

import javafx.application.Platform;
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
import ngeneanalysys.code.enums.BrcaCNVCode;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedBrcaCNV;
import ngeneanalysys.model.paged.PagedBrcaCNVExon;
import ngeneanalysys.model.paged.PagedVariantAndInterpretationEvidence;
import ngeneanalysys.model.paged.PagedVirtualPanel;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.model.render.DatepickerConverter;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.ExcelConvertReportInformationService;
import ngeneanalysys.task.WordDownloadTask;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-09-04
 */
public class AnalysisDetailReportGermlineController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    private static final String CONFIRMATION_DIALOG = "Confirmation Dialog";

    /** api service */
    private APIService apiService;

    @FXML
    private Label geneCategoryLabel;

    @FXML
    private Label pathogenicityCountLabel;

    @FXML
    private Button excelTemplateBtn;

    @FXML
    private Button excelUploadBtn;

    @FXML
    private TextArea conclusionsTextArea;

    @FXML
    private TableView<VariantAndInterpretationEvidence> variantsTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> pathogenicityColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> chrColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> geneColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, Integer> positionColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> transcriptColumn;

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
    private FlowPane targetGenesFlowPane;
    @FXML
    private ComboBox<ComboBoxItem> virtualPanelComboBox;

    private SampleView sample = null;

    private Panel panel = null;

    private List<VariantAndInterpretationEvidence> pathogenicList = null;

    private List<VariantAndInterpretationEvidence> likelyPathgenicList = null;

    private List<VariantAndInterpretationEvidence> uncertainSignificanceList = null;

    private List<VariantAndInterpretationEvidence> likelyBenignList = null;

    private List<VariantAndInterpretationEvidence> benignList = null;

    private boolean reportData = false;

    private File excelFile = null;

    private Map<String, Object> variableList = null;

    private AnalysisDetailGermlineCNVReportController analysisDetailGermlineCNVReportController;

    private AnalysisDetailOverviewHeredAmcController analysisDetailOverviewHeredAmcController;

    @SuppressWarnings("unchecked")
    private void setTargetGenesList() {
        if(!targetGenesFlowPane.getChildren().isEmpty()) {
            mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() - targetGenesFlowPane.getPrefHeight());
            contentVBox.setPrefHeight(contentVBox.getPrefHeight() - targetGenesFlowPane.getPrefHeight());
            targetGenesFlowPane.getChildren().removeAll(targetGenesFlowPane.getChildren());
            targetGenesFlowPane.setPrefHeight(0);
        }
        try {
            HttpClientResponse response = apiService.get("/analysisResults/variantCountByGeneForGermlineDNA/" + sample.getId(),
                    null, null, false);
            if (response != null) {
                List<VariantCountByGeneForGermlineDNA> variantCountByGenes = (List<VariantCountByGeneForGermlineDNA>) response
                        .getMultiObjectBeforeConvertResponseToJSON(VariantCountByGeneForGermlineDNA.class,
                                false);

                variantCountByGenes = filteringGeneList(variantCountByGenes);

                if(variantCountByGenes != null && !variantCountByGenes.isEmpty()) {
                    targetGenesFlowPane.setPrefHeight(targetGenesFlowPane.getPrefHeight() + 30);
                    mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() + 30);
                    contentVBox.setPrefHeight(contentVBox.getPrefHeight() + 30);
                }

                variantCountByGenes = variantCountByGenes.stream().sorted(Comparator.comparing(VariantCountByGeneForGermlineDNA::getGeneSymbol)).collect(Collectors.toList());
                Set<String> allGeneList = null;
                Set<String> list = new HashSet<>();
                if(!virtualPanelComboBox.getItems().isEmpty() && !StringUtils.isEmpty(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue())) {
                    response = apiService.get("virtualPanels/" + virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue(),
                            null, null, false);

                    VirtualPanel virtualPanel = response.getObjectBeforeConvertResponseToJSON(VirtualPanel.class);

                    allGeneList = returnGeneList(virtualPanel.getEssentialGenes(), virtualPanel.getOptionalGenes());

                    String essentialGenes = virtualPanel.getEssentialGenes().replaceAll("\\p{Z}", "");

                    list.addAll(Arrays.stream(essentialGenes.split(",")).collect(Collectors.toSet()));

                }

                if(PipelineCode.isBRCAPipeline(panel.getCode()) && variantCountByGenes.size() == 1) {
                    if(variantCountByGenes.get(0).getGeneSymbol().equals("BRCA1")) {
                        variantCountByGenes.add(new VariantCountByGeneForGermlineDNA("BRCA2"));
                    } else {
                        variantCountByGenes.add(new VariantCountByGeneForGermlineDNA("BRCA1"));
                    }
                    variantCountByGenes = variantCountByGenes.stream()
                            .sorted(Comparator.comparing(VariantCountByGeneForGermlineDNA::getGeneSymbol))
                            .collect(Collectors.toList());
                }

                for(VariantCountByGeneForGermlineDNA gene : variantCountByGenes) {
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
        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
    }

    private boolean discriminationOfMutation(VariantCountByGeneForGermlineDNA gene) {
        return gene.getUncertainSignificanceInDelCount() > 0 || gene.getUncertainSignificanceSnpCount() > 0 ||
                gene.getPathogenicInDelCount() > 0 || gene.getPathogenicSnpCount() > 0 ||
                gene.getLikelyPathogenicInDelCount() > 0 || gene.getLikelyPathogenicSnpCount() > 0;
    }

    private Set<String> returnGeneList(String essentialGenes, String optionalGenes) {
        Set<String> list = new HashSet<>();

        essentialGenes = essentialGenes.replaceAll("\\p{Z}", "");

        list.addAll(Arrays.stream(essentialGenes.split(",")).collect(Collectors.toSet()));

        if(!StringUtils.isEmpty(optionalGenes)) {
            optionalGenes = optionalGenes.replaceAll("\\p{Z}", "");

            list.addAll(Arrays.stream(optionalGenes.split(",")).collect(Collectors.toSet()));
        }

        return list;
    }


    private List<VariantCountByGeneForGermlineDNA> filteringGeneList(List<VariantCountByGeneForGermlineDNA> list) {
        List<VariantCountByGeneForGermlineDNA> filteringList = new ArrayList<>();
        try {
            if(!StringUtils.isEmpty(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue())) {

                    HttpClientResponse response = apiService.get("virtualPanels/" + virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue(),
                            null, null, false);

                    VirtualPanel virtualPanel = response.getObjectBeforeConvertResponseToJSON(VirtualPanel.class);

                    Set<String> geneList = returnGeneList(virtualPanel.getEssentialGenes(), virtualPanel.getOptionalGenes());

                    for(VariantCountByGeneForGermlineDNA variantCountByGene : list) {
                        if(geneList.contains(variantCountByGene.getGeneSymbol())) {
                            filteringList.add(variantCountByGene);
                        }
                    }

            }
        } catch (Exception e) {
            return list;
        }

        if(filteringList.isEmpty()) {
            return list;
        }

        return filteringList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");

        tableCellUpdateFix(variantsTable);

        // API 서비스 클래스 init
        apiService = APIService.getInstance();

        apiService.setStage(getMainController().getPrimaryStage());

        loginSession = LoginSessionUtil.getCurrentLoginSession();

        customFieldGridPane.getChildren().clear();
        customFieldGridPane.setPrefHeight(0);

        sample = (SampleView)paramMap.get("sampleView");

        pathogenicityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSnpInDel().getExpertPathogenicity() != null ?
                        cellData.getValue().getSnpInDel().getExpertPathogenicity() :
                        cellData.getValue().getSnpInDel().getSwPathogenicity()));
        chrColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getChromosome()));
        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        positionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSnpInDel().getGenomicCoordinate().getStartPosition()));
        transcriptColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getTranscriptAccession()));
        ntChangeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        aaChangeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChange()));
        alleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()
                .toString() + "(" + cellData.getValue().getSnpInDel().getReadInfo().getAltReadNum() + "/" + cellData.getValue().getSnpInDel().getReadInfo().getReadDepth() + ")"));

        panel = sample.getPanel();

        if(PipelineCode.isBRCAPipeline(panel.getCode())) {
            geneCategoryLabel.setVisible(false);
            virtualPanelComboBox.setVisible(false);
        } else {
            setVirtualPanel();
        }
        Integer sampleSize = (Integer)paramMap.get("sampleSize");
        if(PipelineCode.isBRCACNVPipeline(panel.getCode()) && sampleSize > 5) {
            mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() + 170);
            contentVBox.setPrefHeight(contentVBox.getPrefHeight() + 170);

            try {
                FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_BRCA_CNV_REPORT);
                Node node = loader.load();
                AnalysisDetailGermlineCNVReportController controller = loader.getController();
                analysisDetailGermlineCNVReportController = controller;
                controller.setMainController(this.getMainController());
                controller.setParamMap(paramMap);
                controller.show((Parent) node);
                contentVBox.getChildren().add(2, node);
                VBox.setMargin(node, new Insets(10, 0, 0 ,0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(panel.getCode().equals(PipelineCode.HERED_ACCUTEST_AMC_CNV_DNA.getCode())) {
            mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() + 123);
            contentVBox.setPrefHeight(contentVBox.getPrefHeight() + 123);
            try {
                FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_HERED_AMC_OVERVIEW);
                Node node = loader.load();
                AnalysisDetailOverviewHeredAmcController controller = loader.getController();
                analysisDetailOverviewHeredAmcController = controller;
                controller.setMainController(this.getMainController());
                controller.setParamMap(paramMap);
                controller.show((Parent) node);
                contentVBox.getChildren().add(2, node);
                VBox.setMargin(node, new Insets(10, 0, 0 ,0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        HttpClientResponse response = null;
        try {
            if(panel.getReportTemplateId() != null) {
                response = apiService.get("reportTemplate/" + panel.getReportTemplateId(), null, null, false);

                ReportContents reportContents = response.getObjectBeforeConvertResponseToJSON(ReportContents.class);

                ReportTemplate template = reportContents.getReportTemplate();

                Map<String, Object> variableList = JsonUtil.fromJsonToMap(template.getCustomFields());

                if(variableList != null && !variableList.isEmpty()) {
                    this.variableList = variableList;
                    Set<String> keyList = variableList.keySet();

                    List<String> sortedKeyList = keyList.stream().sorted().collect(Collectors.toList());

                    if(keyList.contains("conclusions")) {
                        Map<String, String> item = (Map<String, String>) variableList.get("conclusions");
                        conclusions.setText(item.get("displayName"));
                        sortedKeyList.remove("conclusions");
                    }


                    int gridPaneRowSize = (int)Math.ceil(sortedKeyList.size() / 3.0);

                    for(int i = 0; i < gridPaneRowSize ; i++) {
                        customFieldGridPane.setPrefHeight(customFieldGridPane.getPrefHeight() + 30);
                        mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() + 30);
                        contentVBox.setPrefHeight(contentVBox.getPrefHeight() + 30);
                        RowConstraints con = new RowConstraints();
                        con.setPrefHeight(30);
                        customFieldGridPane.getRowConstraints().add(con);
                    }

                    int rowIndex = 0;
                    int colIndex = 0;

                    for(String key : sortedKeyList) {
                        Map<String, String> item = (Map<String, String>) variableList.get(key);
                        if(colIndex == 6) {
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
                        if(type.equalsIgnoreCase("Date")) {
                            DatePicker datePicker = new DatePicker();
                            datePicker.setStyle(datePicker.getStyle() + "-fx-text-inner-color: black; -fx-control-inner-background: white;");
                            datePicker.setPromptText(CommonConstants.DEFAULT_DAY_FORMAT);
                            datePicker.setConverter(DatepickerConverter.getConverter(CommonConstants.DEFAULT_DAY_FORMAT));
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
                            if(type.equalsIgnoreCase("Integer")) {
                                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                                    if(!newValue.matches(CommonConstants.NUMBER_PATTERN)) textField.setText(oldValue);
                                });
                            }

                            customFieldGridPane.add(textField, colIndex++, rowIndex);
                        }
                    }

                } else {
                    excelUploadBtn.setVisible(false);
                    excelTemplateBtn.setVisible(false);
                }
            } else {
                createdStandardBRCAColumn();
            }

            if(sample.getSampleStatus().getReportStartedAt() != null) {
                response = apiService.get("sampleReport/" + sample.getId(), null, null, false);

                if (response.getStatus() >= 200 && response.getStatus() <= 300) {
                    reportData = true;
                    SampleReport sampleReport = response.getObjectBeforeConvertResponseToJSON(SampleReport.class);
                    settingReportData(sampleReport.getContents());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        virtualPanelComboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            if(!t1.equals(t)) Platform.runLater(this::setVariantsList);
            if(!t1.equals(t)) Platform.runLater(this::setTargetGenesList);
        });

        Platform.runLater(this::setTargetGenesList);
    }

    private void createdStandardBRCAColumn() {
        int gridPaneRowSize = 4;

        for(int i = 0; i < gridPaneRowSize ; i++) {
            customFieldGridPane.setPrefHeight(customFieldGridPane.getPrefHeight() + 30);
            mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() + 30);
            contentVBox.setPrefHeight(contentVBox.getPrefHeight() + 30);
            RowConstraints con = new RowConstraints();
            con.setPrefHeight(30);
            customFieldGridPane.getRowConstraints().add(con);
        }

        int rowIndex = 0;
        int colIndex = 0;
        Map<String, Object> variableList = new HashMap<>();
        String[] displayNameList =  {"Test No", "Test Name", "Ordering Organization", "Ordering Physician", "Ordering Contact", "Patient Name",
                "Patient Birthday", "Patient Gender", "Patient ID", "Specimen Draw Date", "Specimen Received Date"};
        String[] variableNameList =  {"manageNo", "inspectionItem", "clientOrganization", "clientName", "clientContact", "name",
                "patientBirthday", "patientGender", "patientID", "DrawDate", "ReceivedDate"};

        for(int i = 0; i < displayNameList.length ;i++) {
            Map<String, String> item = new HashMap<>();
            item.put("displayName", displayNameList[i]);
            if(displayNameList[i].equalsIgnoreCase("Patient Birthday") || displayNameList[i].equalsIgnoreCase("Specimen Draw Date") ||
                    displayNameList[i].equalsIgnoreCase("Specimen Received Date")) {
                item.put("variableType", "Date");
            } else {
                item.put("variableType", "String");
            }

            variableList.put(variableNameList[i], item);
        }
        this.variableList = variableList;

        for(int i = 0; i < displayNameList.length ; i++) {
            String displayName = displayNameList[i];
            String variableName = variableNameList[i];

            if(colIndex == 6) {
                colIndex = 0;
                rowIndex++;
            }
            Label label = new Label(displayName);
            label.setStyle("-fx-text-fill : #595959;");
            customFieldGridPane.add(label, colIndex++, rowIndex);
            label.setMaxHeight(Double.MAX_VALUE);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);

            if(displayName.equalsIgnoreCase("Patient Birthday") || displayName.equalsIgnoreCase("Specimen Draw Date") ||
                    displayName.equalsIgnoreCase("Specimen Received Date")) {
                DatePicker datePicker = new DatePicker();
                datePicker.setStyle(datePicker.getStyle() + "-fx-text-inner-color: black; -fx-control-inner-background: white;");
                datePicker.setPromptText(CommonConstants.DEFAULT_DAY_FORMAT);
                datePicker.setConverter(DatepickerConverter.getConverter(CommonConstants.DEFAULT_DAY_FORMAT));
                datePicker.setId(variableName);
                customFieldGridPane.add(datePicker, colIndex++, rowIndex);
            } else {
                TextField textField = new TextField();
                textField.getStyleClass().add("txt_black");
                textField.setId(variableName);

                customFieldGridPane.add(textField, colIndex++, rowIndex);
            }
        }
    }

    private void setVirtualPanel() {

        virtualPanelComboBox.setConverter(new ComboBoxConverter());

        virtualPanelComboBox.getItems().add(new ComboBoxItem());

        virtualPanelComboBox.getSelectionModel().selectFirst();

        try {

            Map<String, Object> params = new HashMap<>();

            params.put("panelId", panel.getId());

            HttpClientResponse response = apiService.get("virtualPanels", params, null, false);

            PagedVirtualPanel pagedVirtualPanel = response.getObjectBeforeConvertResponseToJSON(PagedVirtualPanel.class);

            if(pagedVirtualPanel.getCount() > 0) {
                for(VirtualPanel virtualPanel : pagedVirtualPanel.getResult()) {
                    virtualPanelComboBox.getItems().add(new ComboBoxItem(virtualPanel.getId().toString(), virtualPanel.getName()));
                }
            }
        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
    }

    void setVariantsList() {
        if(analysisDetailOverviewHeredAmcController != null) {
            analysisDetailOverviewHeredAmcController.setContents("REPORT");
        }

        HttpClientResponse response = null;
        try {
            response = apiService.get("/analysisResults/sampleSnpInDels/" + sample.getId(), null,
                    null, false);

            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();

            PagedBrcaCNVExon pagedBrcaCNVExon = null;

            if(analysisDetailGermlineCNVReportController != null) {
                response = apiService.get("/analysisResults/brcaCnvExon/" + sample.getId(), null,
                        null, false);

                pagedBrcaCNVExon = response.getObjectBeforeConvertResponseToJSON(PagedBrcaCNVExon.class);
                analysisDetailGermlineCNVReportController.setBrcaCnvExonList(pagedBrcaCNVExon.getResult());
            }

            pathogenicList = settingPathogenicityList(list, "P");

            likelyPathgenicList = settingPathogenicityList(list, "LP");

            uncertainSignificanceList = settingPathogenicityList(list, "US");

            likelyBenignList = settingPathogenicityList(list, "LB");

            benignList = settingPathogenicityList(list, "B");

            long snvCount = list.stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport()) &&
                    variant.getSnpInDel().getSnpInDelExpression().getVariantType().matches("SNV|SNP|snv|snp")).count();
            long indelCount = list.stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport()) &&
                    variant.getSnpInDel().getSnpInDelExpression().getVariantType().matches("Ins|Del")).count();
            long pCount = pathogenicList != null ? pathogenicList.stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0;
            long lpCount = likelyPathgenicList != null ? likelyPathgenicList.stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0;
            long usCount = uncertainSignificanceList != null ? uncertainSignificanceList.stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0;
            long lbCount = likelyBenignList != null ? likelyBenignList.stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0;
            long bCount = benignList != null ? benignList.stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(Total : ").append((pCount + lpCount + usCount + lbCount + bCount));
            if(snvCount > 0) stringBuilder.append(", SNV : ").append(snvCount);
            if(indelCount > 0) stringBuilder.append(", Indel : ").append(indelCount);
            if((pCount + lpCount + usCount + lbCount + bCount) > 0) stringBuilder.append(" | ");
            if(pCount > 0) stringBuilder.append("P : ").append(pCount).append(", ");
            if(lpCount > 0) stringBuilder.append("LP : ").append(lpCount).append(", ");
            if(usCount > 0) stringBuilder.append("US : ").append(usCount).append(", ");
            if(lbCount > 0) stringBuilder.append("LB : ").append(lbCount).append(", ");
            if(bCount > 0) stringBuilder.append("B : ").append(bCount).append(", ");
            if((pCount + lpCount + usCount + lbCount + bCount) > 0) stringBuilder
                    .delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
            pathogenicityCountLabel.setText(stringBuilder.toString() + ")"
            );

            Collections.sort(list,
                    (a, b) -> a.getSnpInDel().getGenomicCoordinate().getGene().compareTo(b.getSnpInDel().getGenomicCoordinate().getGene()));

            if(variantsTable.getItems() != null && !variantsTable.getItems().isEmpty()) {
                variantsTable.getItems().removeAll(variantsTable.getItems());
            }

            List<VariantAndInterpretationEvidence> tableList = list.stream().filter(item -> item.getSnpInDel().getIncludedInReport().equals("Y"))
                    .collect(Collectors.toList());

            variantsTable.getItems().addAll(tableList);

        } catch (WebAPIException wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getContents(), this.getMainApp().getPrimaryStage(), true);
        }
    }

    private void tableCellUpdateFix(TableView<VariantAndInterpretationEvidence> tableView) {
        tableView.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            tableView.refresh();
            // close text box
            tableView.edit(-1, null);
        });
    }

    private List<VariantAndInterpretationEvidence> settingPathogenicityList(List<VariantAndInterpretationEvidence> allTierList,
                                                                           String pathogenicity) {
        if(!StringUtils.isEmpty(pathogenicity)) {
            return allTierList.stream().filter(item -> (pathogenicity.equalsIgnoreCase(item.getSnpInDel().getExpertPathogenicity()) ||
                    (StringUtils.isEmpty(item.getSnpInDel().getExpertPathogenicity())
                            && item.getSnpInDel().getSwPathogenicity().equalsIgnoreCase(pathogenicity))))
                    .collect(Collectors.toList());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
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
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DAY_FORMAT);
                    datePicker.setValue(LocalDate.parse((String)contentsMap.get(datePicker.getId()), formatter));
                }
            } else if(gridObject instanceof ComboBox) {
                ComboBox<String> comboBox = (ComboBox)gridObject;
                if(contentsMap.containsKey(comboBox.getId())) comboBox.getSelectionModel().select((String)contentsMap.get(comboBox.getId()));
            }
        }

    }

    /**
     * 입력 정보 저장
     * @return boolean
     */
    private boolean saveData() {

        String conclusionsText = conclusionsTextArea.getText();

        Map<String, Object> params = new HashMap<>();

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
            }
        }
        String contents = JsonUtil.toJson(contentsMap);
        params.put("contents", contents);

        HttpClientResponse response = null;
        try {
            if(reportData) {
                response = apiService.put("/sampleReport/" + sample.getId(), params, null, true);
            } else {
                response = apiService.post("/sampleReport/" + sample.getId(), params, null, true);
            }
            logger.debug("sample report status : " + response.getStatus());
        } catch (WebAPIException wae) {
            wae.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @FXML
    public void save() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        DialogUtil.setIcon(alert);
        alert.initOwner(getMainController().getPrimaryStage());
        alert.setTitle(CONFIRMATION_DIALOG);
        alert.setHeaderText("");
        alert.setContentText("Do you want to save?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            boolean dataSave = saveData();
            if(dataSave) {
                DialogUtil.alert("", "Data saving is completed.", getMainController().getPrimaryStage(), false);
            }
        } else {
            alert.close();
        }
    }

    @FXML
    public void createPDFAsDraft() {
        boolean dataSave = saveData();
        if(dataSave){
            if(createPDF(true)) {
                Platform.runLater(this::setVariantsList);
            }
        }
    }

    @FXML
    public void createPDFAsFinal() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        DialogUtil.setIcon(alert);
        alert.initOwner(getMainController().getPrimaryStage());
        alert.setTitle(CONFIRMATION_DIALOG);
        alert.setHeaderText("");
        alert.setContentText("Do you want to proceed?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            try {

                boolean dataSave = saveData();
                if (dataSave) {
                    // 최종 보고서 생성이 정상 처리된 경우 분석 샘플의 상태값 완료 처리.
                    if (createPDF(false)) {
                        Platform.runLater(this::setVariantsList);
                    }
                }
            } catch (Exception e) {
                logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
                DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), true);
            }
        } else {
            alert.close();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean createPDF(boolean isDraft) {
        boolean created = true;
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

            // Show save file dialog
            FileChooser fileChooser = new FileChooser();
            if(outputType != null && outputType.equalsIgnoreCase("MS_WORD_ZIP")) {
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("ZIP (*.zip)", "*.zip"));
            } else if(outputType != null && outputType.equalsIgnoreCase("MS_WORD")) {
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("WORD (*.docx)", "*.docx"));
            } else {
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
            }
            fileChooser.setInitialFileName(baseSaveName);
            File file = fileChooser.showSaveDialog(this.getMainApp().getPrimaryStage());


            if(file != null) {

                mainController.setMainMaskerPane(true);

                String draftImageStr = String.format("url('%s')", this.getClass().getClassLoader().getResource("layout/images/DRAFT.png"));
                String ngenebioLogo = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/ngenebio_logo_small.png"));
                SecureRandom random = new SecureRandom();
                Map<String,Object> contentsMap = new HashMap<>();
                contentsMap.put("isDraft", isDraft);
                contentsMap.put("sampleName", sample.getName());
                contentsMap.put("panelName", panel.getName());
                contentsMap.put("panelCode", panel.getCode());
                contentsMap.put("sampleSource", sample.getSampleSource());
                Integer sampleSize = (Integer)paramMap.get("sampleSize");
                contentsMap.put("sampleSize", sampleSize);
                //리포트를 생성할 때마다 고유 ID 부여 report ID + random Int
                contentsMap.put("reportID", String.format("%05d-%05d", sample.getId(), random.nextInt(99999)));

                List<VariantAndInterpretationEvidence> allVariant = new ArrayList<>();
                allVariant.addAll(pathogenicList);
                allVariant.addAll(likelyPathgenicList);
                allVariant.addAll(uncertainSignificanceList);
                allVariant.addAll(likelyBenignList);
                allVariant.addAll(benignList);

                List<VariantAndInterpretationEvidence> notReportedVariant = allVariant.stream()
                        .filter(item -> item.getSnpInDel().getIncludedInReport().equals("N"))
                        .collect(Collectors.toList());

                contentsMap.put("notReportedVariantList", notReportedVariant);

                contentsMap.put("inspectorOrganization", "");
                contentsMap.put("inspectorName", "");
                contentsMap.put("inspectorContact", "");
                contentsMap.put("reportingDate", "");
                if(!isDraft) {
                    HttpClientResponse response = apiService.get("/member", null, null,
                            false);
                    User user = response.getObjectBeforeConvertResponseToJSON(User.class);
                    if(!StringUtils.isEmpty(user.getOrganization()) && !StringUtils.isEmpty(user.getDepartment())) {
                        contentsMap.put("inspectorOrganization", user.getOrganization() + "/" + user.getDepartment());
                    } else if(!StringUtils.isEmpty(user.getOrganization())) {
                        contentsMap.put("inspectorOrganization", user.getOrganization());
                    } else if(!StringUtils.isEmpty(user.getDepartment())) {
                        contentsMap.put("inspectorOrganization", user.getDepartment());
                    } else {
                        contentsMap.put("inspectorOrganization", "");
                    }
                    contentsMap.put("inspectorName", user.getName());
                    contentsMap.put("inspectorContact", user.getPhone());
                    contentsMap.put("reportingDate", org.apache.commons.lang3.time.DateFormatUtils.format(new Date(), CommonConstants.DEFAULT_DAY_FORMAT));
                }
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

                List<VariantAndInterpretationEvidence> list = new ArrayList<>();
                if(pathogenicList != null && !pathogenicList.isEmpty()) list.addAll(pathogenicList);
                if(likelyPathgenicList != null && !likelyPathgenicList.isEmpty()) list.addAll(likelyPathgenicList);
                if(uncertainSignificanceList != null && !uncertainSignificanceList.isEmpty()) list.addAll(uncertainSignificanceList);
                if(likelyBenignList != null && !likelyBenignList.isEmpty()) list.addAll(likelyBenignList);
                if(benignList != null && !benignList.isEmpty()) list.addAll(benignList);

                for(VariantAndInterpretationEvidence variantAndInterpretationEvidence : list) {
                    variantAndInterpretationEvidence.getSnpInDel().setNtChangeBRCA();
                }

                if(list.stream().anyMatch(item -> "Y".equalsIgnoreCase(item.getSnpInDel().getIncludedInReport()))) contentsMap.put("isExistReportedVariant", "Y");

                if(list.stream().anyMatch(item -> "BRCA1".equalsIgnoreCase(item.getSnpInDel().getGenomicCoordinate().getGene()))) {
                    contentsMap.put("isExistBRCA1", "Y");
                }

                if(list.stream().anyMatch(item -> "BRCA2".equalsIgnoreCase(item.getSnpInDel().getGenomicCoordinate().getGene()))) {
                    contentsMap.put("isExistBRCA2", "Y");
                }

                if(!list.isEmpty()) {
                    for(VariantAndInterpretationEvidence variant : list){
                        variant.getSnpInDel().getSnpInDelExpression().setTranscript(ConvertUtil.insertTextAtFixedPosition(variant.getSnpInDel().getSnpInDelExpression().getTranscriptAccession(), 15, "\n"));
                        variant.getSnpInDel().getSnpInDelExpression().setNtChange(ConvertUtil.insertTextAtFixedPosition(variant.getSnpInDel().getSnpInDelExpression().getNtChange(), 15, "\n"));
                        variant.getSnpInDel().getSnpInDelExpression().setAaChange(ConvertUtil.insertTextAtFixedPosition(variant.getSnpInDel().getSnpInDelExpression().getAaChange(), 15, "\n"));
                    }
                }
                // 변이 목록 삽입
                contentsMap.put("variantList", list);
                // 소견 내용 라인별로 분리하여 리스트 객체로 변환하여 저장 : fop template에서 라인별로 출력하려는 경우 라인별로 <fo:block> 태그로 감싸주어야함.
                List<String> conclusionLineList = null;
                if(!StringUtils.isEmpty(conclusionsTextArea.getText())) {
                    conclusionLineList = new ArrayList<>();
                    String[] lines = conclusionsTextArea.getText().split("\n");
                    if(lines.length > 0) {
                        conclusionLineList.addAll(Arrays.asList(lines));
                    } else {
                        conclusionLineList.add(conclusionsTextArea.getText());
                    }
                }
                contentsMap.put("conclusion", conclusionLineList);

                if(analysisDetailGermlineCNVReportController != null) {
                    List<BrcaCnvExon> brcaCnvExons = analysisDetailGermlineCNVReportController.getBrcaCnvExonList();
                    contentsMap.put("brcaCnvExons", brcaCnvExons);
                    contentsMap.put("brcaCnvResults", analysisDetailGermlineCNVReportController.getBrcaCnvResultList());
                    if(brcaCnvExons != null && !brcaCnvExons.isEmpty()) {
                        contentsMap.put("brca1CnvList",
                                getBrcaResult(brcaCnvExons.stream().filter(item ->
                                        "BRCA1".equals(item.getGene())).collect(Collectors.toList())));
                        contentsMap.put("brca2CnvList",
                                getBrcaResult(brcaCnvExons.stream().filter(item ->
                                        "BRCA2".equals(item.getGene())).collect(Collectors.toList())));
                    }
                } else if(analysisDetailOverviewHeredAmcController != null) {
                    Map<String, Object> analysisFileMap = new HashMap<>();
                    analysisFileMap.put("sampleId", sample.getId());
                    HttpClientResponse response = apiService.get("/analysisFiles", analysisFileMap, null, false);
                    AnalysisFileList analysisFileList = response.getObjectBeforeConvertResponseToJSON(AnalysisFileList.class);

                    List<AnalysisFile> analysisFiles = analysisFileList.getResult();

                    Optional<AnalysisFile> optionalAnalysisFile = analysisFiles.stream()
                            .filter(item -> item.getName().contains("boxplot.png")).findFirst();

                     if(optionalAnalysisFile.isPresent()) contentsMap.put("cnvPlotImagePath", sample.getId() + "_" + optionalAnalysisFile.get().getName());

                    Optional<AnalysisFile> optionalVafFiles = analysisFiles.stream()
                            .filter(item -> item.getName().contains("vafplot.png")).findFirst();

                    if(optionalVafFiles.isPresent()) contentsMap.put("cnvVafImagePath", sample.getId() + "_" + optionalVafFiles.get().getName());


                    try {
                        response = apiService.get("/analysisResults/compositeCmtCnvResult/" + sample.getId(), null, null, null);
                        CompositeCmtCnvResult compositeCmtCnvResult = response.getObjectBeforeConvertResponseToJSON(CompositeCmtCnvResult.class);
                        contentsMap.put("compositeCmtCnvResult", compositeCmtCnvResult);

                    } catch (WebAPIException wae) {
                        logger.debug(wae.getMessage());
                    }
                }

                if(PipelineCode.isBRCACNVPipeline(panel.getCode())) {
                    Map<String, Object> analysisFileMap = new HashMap<>();
                    analysisFileMap.put("sampleId", sample.getId());
                    HttpClientResponse response = apiService.get("/analysisFiles", analysisFileMap, null, false);
                    AnalysisFileList analysisFileList = response.getObjectBeforeConvertResponseToJSON(AnalysisFileList.class);

                    List<AnalysisFile> analysisFiles = analysisFileList.getResult();

                    Optional<AnalysisFile> optionalAnalysisFile = analysisFiles.stream()
                            .filter(item -> item.getName().contains("cnv_plot.png")).findFirst();

                    if (optionalAnalysisFile.isPresent()) {
                        contentsMap.put("cnvImagePath", sample.getId() + "_" + optionalAnalysisFile.get().getName());
                    }

                    response = apiService.get("/analysisResults/brcaCnv/" + sample.getId(), null, null, null);
                    PagedBrcaCNV pagedCNV = response.getObjectBeforeConvertResponseToJSON(PagedBrcaCNV.class);
                    contentsMap.put("cnvList", pagedCNV.getResult());
                }

                Map<String,Object> model = new HashMap<>();
                model.put("isDraft", isDraft);
                model.put("qcResult", sample.getQcResult());
                model.put("draftImageURL", draftImageStr);
                model.put("ngenebioLogo", ngenebioLogo);
                model.put("contents", contentsMap);

                HttpClientResponse response = apiService.get("/analysisResults/sampleQCs/" + sample.getId(), null,
                        null, false);

                List<SampleQC> qcList = (List<SampleQC>) response.getMultiObjectBeforeConvertResponseToJSON(SampleQC.class, false);

                if(PipelineCode.isHeredPipeline(panel.getCode())) {
                    contentsMap.put("mappingQuality", findQCResult(qcList, "mapping_quality_60"));
                    contentsMap.put("uniformity", findQCResult(qcList, "uniformity_0.2"));
                    contentsMap.put("totalBase", findQCResult(qcList, "total_base"));
                    contentsMap.put("q30", findQCResult(qcList, "q30_trimmed_base"));
                    contentsMap.put("mappedBase", findQCResult(qcList, "mapped_base"));
                    contentsMap.put("onTarget", findQCResult(qcList, "on_target"));
                    contentsMap.put("onTargetCoverage", findQCResult(qcList, "on_target_coverage"));
                    contentsMap.put("duplicatedReads", findQCResult(qcList, "duplicated_reads"));
                    contentsMap.put("roiCoverage", findQCResult(qcList, "roi_coverage"));
                    contentsMap.put("onTargetRead", findQCResult(qcList, "on_target_read"));
                    contentsMap.put("targetCoverageAt30x", findQCResult(qcList, "target_coverage_at_30x"));
                } else if(PipelineCode.isBRCAPipeline(panel.getCode())){
                    contentsMap.put("uniformity", findQCResult(qcList, "coverage_uniformity"));
                    contentsMap.put("roiCoverage", findQCResult(qcList, "roi_coverage"));
                    contentsMap.put("meanThroughput", findQCResult(qcList, "mean_throughput"));
                }


                // 템플릿에 데이터 바인딩하여 pdf 생성 스크립트 생성
                if(panel.getReportTemplateId() == null) {
                    String jsonStr = JsonUtil.toJsonIncludeNullValue(model);
                    if(analysisDetailGermlineCNVReportController != null) {
                        Task<Void> task = new WordDownloadTask(this, null, jsonStr, file, "BRCA_CNV");
                        final Thread downloadThread = new Thread(task);
                        downloadThread.setDaemon(true);
                        downloadThread.start();
                    } else {
                        Task<Void> task = new WordDownloadTask(this, null, jsonStr, file, "BRCA");
                        final Thread downloadThread = new Thread(task);
                        downloadThread.setDaemon(true);
                        downloadThread.start();
                    }
                } else {
                    response = apiService.get("reportTemplate/" + panel.getReportTemplateId(), null, null, false);

                    ReportContents reportContents = response.getObjectBeforeConvertResponseToJSON(ReportContents.class);

                    if(reportContents.getReportTemplate().getOutputType() != null
                            && (reportContents.getReportTemplate().getOutputType().equalsIgnoreCase("MS_WORD") ||
                            reportContents.getReportTemplate().getOutputType().equalsIgnoreCase("MS_WORD_ZIP"))) {
                        List<ReportComponent> components = reportContents.getReportComponents();

                        if(components == null || components.isEmpty()) throw new Exception();
                        final Comparator<ReportComponent> comp = Comparator.comparingInt(ReportComponent::getId);
                        final ReportComponent component = components.stream().max(comp).get();

                        String jsonStr = JsonUtil.toJsonIncludeNullValue(contentsMap);

                        Task<Void> task = new WordDownloadTask(this, component, jsonStr, file);
                        final Thread downloadThread = new Thread(task);
                        downloadThread.setDaemon(true);
                        downloadThread.start();

                    } else {
                        String jsonStr = JsonUtil.toJsonIncludeNullValue(model);
                        Task<Void> task = new WordDownloadTask(this, reportContents.getReportTemplate(), jsonStr, file, "CUSTOM");
                        final Thread downloadThread = new Thread(task);
                        downloadThread.setDaemon(true);
                        downloadThread.start();
                    }
                }
            }
        } catch(FileNotFoundException fnfe){
            DialogUtil.error("Save Fail.", fnfe.getMessage(), getMainApp().getPrimaryStage(), false);
            mainController.setMainMaskerPane(false);
        } catch (Exception e) {
            DialogUtil.error("Save Fail.", "An error occurred during the creation of the report document.", getMainApp().getPrimaryStage(), false);
            e.printStackTrace();
            mainController.setMainMaskerPane(false);
            created = false;
        }


        return created;
    }

    public void createdCheck(boolean created, File file) {
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
        mainController.setMainMaskerPane(false);
    }

    private SampleQC findQCResult(List<SampleQC> qcList, String qc) {
        if(qcList != null && !qcList.isEmpty()) {
            Optional<SampleQC> findQC = qcList.stream().filter(sampleQC -> sampleQC.getQcType().equalsIgnoreCase(qc)).findFirst();
            if(findQC.isPresent()) {
                SampleQC qcData = findQC.get();
                String number = findQC.get().getQcValue().toString();
                double value = Double.parseDouble(number);
                if(qc.equalsIgnoreCase("total_base")) {
                    qcData.setQcUnit("Mb");
                    qcData.setQcValue(BigDecimal.valueOf(value / 1000 / 1000).setScale(1, BigDecimal.ROUND_FLOOR));
                    return qcData;
                }
                qcData.setQcValue(BigDecimal.valueOf(value).setScale(1, BigDecimal.ROUND_FLOOR));
                return qcData;
            }
        }
        return null;
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

    private List<BrcaCnvResult> getBrcaResult(List<BrcaCnvExon> brcaCnvExons) {
        String[] exons = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"
                , "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"
                , "22", "23", "24", "25", "26", "27"};
        List<BrcaCnvResult> results = new ArrayList<>();
        Arrays.stream(exons).forEach(exon -> {
            Optional<BrcaCnvExon> optionalBrcaCnvExon = brcaCnvExons.stream().filter(item ->
                    item.getExon().equals(exon)).findFirst();
            if(optionalBrcaCnvExon.isPresent()) {
                BrcaCnvExon brcaCnvExon = optionalBrcaCnvExon.get();
                String code = StringUtils.isNotEmpty(brcaCnvExon.getExpertCnv())
                        ? BrcaCNVCode.findInitial(brcaCnvExon.getExpertCnv()) : BrcaCNVCode.findInitial(brcaCnvExon.getSwCnv());
                BrcaCnvResult brcaCnvResult = new BrcaCnvResult(brcaCnvExon.getGene(), brcaCnvExon.getExon(), code);
                results.add(brcaCnvResult);
            } else {
                BrcaCnvResult brcaCnvResult = new BrcaCnvResult(brcaCnvExons.get(0).getGene(), exon, "");
                results.add(brcaCnvResult);
            }
        });

        return results;
    }
}
