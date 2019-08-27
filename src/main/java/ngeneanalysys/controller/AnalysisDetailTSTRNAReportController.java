package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.enums.SequencerCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedVariantAndInterpretationEvidence;
import ngeneanalysys.model.paged.PagedVirtualPanel;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.model.render.DatepickerConverter;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.ExcelConvertReportInformationService;
import ngeneanalysys.service.PDFCreateService;
import ngeneanalysys.task.ImageFileDownloadTask;
import ngeneanalysys.task.WordDownloadTask;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-09-04
 */
public class AnalysisDetailTSTRNAReportController extends AnalysisDetailCommonController {
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
    private Label excelUploadBtn;

    @FXML
    private TextArea conclusionsTextArea;

    @FXML
    private TableView<VariantAndInterpretationEvidence> variantsTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> userTierColumn;

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

    @SuppressWarnings("unchecked")
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

        tierColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSwTier()));
        userTierColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getExpertTier()));
        chrColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getChromosome()));
        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        positionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSnpInDel().getGenomicCoordinate().getStartPosition()));
        refSeqColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getTranscriptAccession()));
        ntChangeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        aaChangeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChange()));
        alleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()
                .toString() + "(" + cellData.getValue().getSnpInDel().getReadInfo().getAltReadNum() + "/" + cellData.getValue().getSnpInDel().getReadInfo().getReadDepth() + ")"));

        panel = sample.getPanel();
        setVirtualPanel();

        HttpClientResponse response;

        try {
            if(panel.getReportTemplateId() != null) {
                response = apiService.get("reportTemplate/" + panel.getReportTemplateId(), null, null, false);

                ReportContents reportContents = response.getObjectBeforeConvertResponseToJSON(ReportContents.class);

                ReportTemplate template = reportContents.getReportTemplate();
                if (template.getContents() != null) {
                    extraFields.setVisible(true);
                    Map<String, Object> variableList = JsonUtil.fromJsonToMap(template.getCustomFields());

                    if (variableList != null && !variableList.isEmpty()) {
                        this.variableList = variableList;
                        Set<String> keyList = variableList.keySet();

                        List<String> sortedKeyList = keyList.stream().sorted().collect(Collectors.toList());

                        if (keyList.contains("conclusions")) {
                            Map<String, String> item = (Map<String, String>) variableList.get("conclusions");
                            conclusions.setText(item.get("displayName"));
                            sortedKeyList.remove("conclusions");
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
                                if (type.equalsIgnoreCase("Integer")) {
                                    textField.textProperty().addListener((observable, oldValue, newValue) -> {
                                        if (!newValue.matches(CommonConstants.NUMBER_PATTERN)) textField.setText(oldValue);
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
            if(sample.getSampleStatus().getReportStartedAt() != null) {
                response = apiService.get("sampleReport/" + sample.getId(), null, null, false);

                if (response.getStatus() == 200) {
                    reportData = true;
                    SampleReport sampleReport = response.getObjectBeforeConvertResponseToJSON(SampleReport.class);
                    if(sampleReport.getVirtualPanelId() != null) {
                        Optional<ComboBoxItem> item = virtualPanelComboBox.getItems().stream().filter(
                                comboBoxItem -> comboBoxItem.getValue().equals(sampleReport.getVirtualPanelId().toString())).findFirst();
                        item.ifPresent(comboBoxItem -> virtualPanelComboBox.getSelectionModel().select(comboBoxItem));
                    }
                    settingReportData(sampleReport.getContents());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        virtualPanelComboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            if(!t1.equals(t)) setVariantsList();
            if(!t1.equals(t)) setTargetGenesList();
        });

        setTargetGenesList();

    }

    @SuppressWarnings("unchecked")
    private void setTargetGenesList() {
        if(!targetGenesFlowPane.getChildren().isEmpty()) {
            mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() - targetGenesFlowPane.getPrefHeight());
            contentVBox.setPrefHeight(contentVBox.getPrefHeight() - targetGenesFlowPane.getPrefHeight());
            targetGenesFlowPane.getChildren().removeAll(targetGenesFlowPane.getChildren());
            targetGenesFlowPane.setPrefHeight(0);
        }
        try {
            HttpClientResponse response = apiService.get("/analysisResults/variantCountByGeneForSomaticRNA/" + sample.getId(),
                    null, null, false);
            if (response != null) {
                List<VariantCountByGene> variantCountByGenes = (List<VariantCountByGene>) response
                        .getMultiObjectBeforeConvertResponseToJSON(VariantCountByGene.class,
                                false);
                if (variantCountByGenes == null) {
                    variantCountByGenes = new ArrayList<>();
                }
                variantCountByGenes = filteringGeneList(variantCountByGenes);

                if(variantCountByGenes != null && !variantCountByGenes.isEmpty()) {
                    targetGenesFlowPane.setPrefHeight(targetGenesFlowPane.getPrefHeight() + 30);
                    mainContentsPane.setPrefHeight(mainContentsPane.getPrefHeight() + 30);
                    contentVBox.setPrefHeight(contentVBox.getPrefHeight() + 30);
                }
                variantCountByGenes = variantCountByGenes.stream().sorted(Comparator.comparing(VariantCountByGene::getGeneSymbol)).collect(Collectors.toList());
                Set<String> allGeneList = null;
                Set<String> list = new HashSet<>();
                if(!StringUtils.isEmpty(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue())) {
                    response = apiService.get("virtualPanels/" + virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue(),
                            null, null, false);

                    VirtualPanel virtualPanel = response.getObjectBeforeConvertResponseToJSON(VirtualPanel.class);

                    allGeneList = returnGeneList(virtualPanel.getEssentialGenes(), virtualPanel.getOptionalGenes());

                    String essentialGenes = virtualPanel.getEssentialGenes().replaceAll("\\p{Z}", "");

                    list.addAll(Arrays.stream(essentialGenes.split(",")).collect(Collectors.toSet()));

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
        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
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
        try {
            response = apiService.get("/analysisResults/sampleSnpInDels/" + sample.getId(), null,
                    null, false);

            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();

            list = filteringVariant(list);

            negativeList = list.stream().filter(item -> (
                    StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && "TN".equalsIgnoreCase(item.getSnpInDel().getSwTier())) ||
                    "TN".equalsIgnoreCase(item.getSnpInDel().getExpertTier())).collect(Collectors.toList());

            tierOne = settingTierList(list, "T1");

            tierTwo = settingTierList(list, "T2");

            tierThree = settingTierList(list, "T3");

            tierFour = settingTierList(list, "T4");

            tierCountLabel.setText("Tier 1 : " + ((tierOne != null) ? tierOne
                    .stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0) +
                    ", Tier 2 : " + ((tierTwo != null) ? tierTwo
                    .stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0) +
                    ", Tier 3 : " + ((tierThree != null) ? tierThree
                    .stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0) +
                    ", Tier 4 : " + ((tierFour != null) ? tierFour
                    .stream().filter(variant -> "Y".equals(variant.getSnpInDel().getIncludedInReport())).count() : 0));

            List<VariantAndInterpretationEvidence> tableList = new ArrayList<>();

            if(tierOne != null && !tierOne.isEmpty()) {
                tierOne.sort(Comparator.comparing(a -> a.getSnpInDel().getGenomicCoordinate().getGene()));
                tableList.addAll(tierOne);
            }

            if(tierTwo != null && !tierTwo.isEmpty()) {
                tierTwo.sort(Comparator.comparing(a -> a.getSnpInDel().getGenomicCoordinate().getGene()));
                tableList.addAll(tierTwo);
            }

            if(tierThree != null && !tierThree.isEmpty()) {
                tierThree.sort(Comparator.comparing(a -> a.getSnpInDel().getGenomicCoordinate().getGene()));

                tableList.addAll(tierThree);
            }

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

    private List<VariantAndInterpretationEvidence> settingTierList(List<VariantAndInterpretationEvidence> allTierList, String tier) {
        if(!StringUtils.isEmpty(tier)) {
            return allTierList.stream().filter(item -> ((tier.equalsIgnoreCase(item.getSnpInDel().getExpertTier()) ||
                    (StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && item.getSnpInDel().getSwTier().equalsIgnoreCase(tier)))))
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
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DAY_FORMAT);
                    datePicker.setValue(LocalDate.parse((String)contentsMap.get(datePicker.getId()), formatter));
                }
            } else if(gridObject instanceof ComboBox) {
                ComboBox comboBox = (ComboBox)gridObject;
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
        if(!StringUtils.isEmpty(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue())) {
            params.put("virtualPanelId", Integer.parseInt(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue()));
        }

        try {
            if(reportData) {
                apiService.put("/sampleReport/" + sample.getId(), params, null, true);
            } else {
                apiService.post("/sampleReport/" + sample.getId(), params, null, true);
            }
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
                DialogUtil.alert("Save Success", "Input data is successfully saved.", getMainController().getPrimaryStage(), false);
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
                setVariantsList();
            }
        }
    }

    @FXML
    public void createPDFAsFinal() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        DialogUtil.setIcon(alert);
        alert.initOwner(getMainController().getPrimaryStage());
        alert.setTitle(CONFIRMATION_DIALOG);
        alert.setHeaderText("Test conducting organization information");
        alert.setContentText(String.format("Test conducting organization information will be filled with current user [ %s ] information for final report generation.\n\nDo you want to proceed?", loginSession.getName()));

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            try {

                boolean dataSave = saveData();
                if (dataSave) {
                    // 최종 보고서 생성이 정상 처리된 경우 분석 샘플의 상태값 완료 처리.
                    if (createPDF(false)) {
                        setVariantsList();
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

    @FXML
    public void confirmPDFAsFinal() {
        createPDF(false);
    }

    /*public void convertPDFtoImage(File file, String baseFileName) {
        String path = file.getParentFile().getAbsolutePath();
        try {
        PDDocument document = PDDocument.load(file);
            PDFRenderer d = new PDFRenderer(document);
            int index = document.getNumberOfPages();

            for(int i = 0; i < index ;i++) {
                BufferedImage im  = d.renderImage(i, 2, ImageType.RGB);
                ImageIO.write(im, "jpg",  new File(path + "\\\\" + baseFileName + "_" + i+".jpg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

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
                    if(snpInDelEvidence.getEvidenceLevel().equals("A")) {
                        evidenceACount++;
                    } else if(snpInDelEvidence.getEvidenceLevel().equals("B")) {
                        evidenceBCount++;
                    } else if(snpInDelEvidence.getEvidenceLevel().equals("C")) {
                        evidenceCCount++;
                    } else if(snpInDelEvidence.getEvidenceLevel().equals("D")) {
                        evidenceDCount++;
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
                    if(snpInDelEvidence.getEvidenceLevel().equals("A")) {
                        evidenceACount++;
                    } else if(snpInDelEvidence.getEvidenceLevel().equals("B")) {
                        evidenceBCount++;
                    } else if(snpInDelEvidence.getEvidenceLevel().equals("C")) {
                        evidenceCCount++;
                    } else if(snpInDelEvidence.getEvidenceLevel().equals("D")) {
                        evidenceDCount++;
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

        /*HttpClientResponse response = apiService.get("/analysisResults/variantCountByGeneForSomaticRNA/" + sample.getId(),
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
            contentsMap.put("genesInPanelTableThree", Arrays.copyOfRange(genesInPanelTableOne, tableOneSize + tableTwoSize, variantCountByGenes.size()));*/
            HttpClientResponse response;
            if(!StringUtils.isEmpty(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue())) {
                response = apiService.get("virtualPanels/" + virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue(),
                        null, null, false);

                VirtualPanel virtualPanel = response.getObjectBeforeConvertResponseToJSON(VirtualPanel.class);

                Set<String> list = new HashSet<>();

                list.addAll(Arrays.stream(virtualPanel.getEssentialGenes().replaceAll("\\p{Z}", "")
                        .split(",")).collect(Collectors.toSet()));

                Set<String> allGeneList = returnGeneList(virtualPanel.getEssentialGenes(), virtualPanel.getOptionalGenes());

                contentsMap.put("essentialGenes", list);
                contentsMap.put("allGeneList", allGeneList);
                contentsMap.put("virtualPanelName", virtualPanel.getName());

            }

            String runSequencer = sample.getRun().getSequencingPlatform();

            if(runSequencer.equalsIgnoreCase("MISEQ")) {
                contentsMap.put("sequencer",SequencerCode.MISEQ.getDescription());
            } else {
                contentsMap.put("sequencer",SequencerCode.MISEQ_DX.getDescription());
            }

            response = apiService.get("/analysisResults/sampleQCs/" + sample.getId(), null,
                    null, false);

            List<SampleQC> qcList = (List<SampleQC>) response.getMultiObjectBeforeConvertResponseToJSON(SampleQC.class, false);

            contentsMap.put("readsPF",findQCResult(qcList, "Reads_PF"));
            contentsMap.put("medianInsertSize",findQCResult(qcList, "Median_Insert_Size"));
            contentsMap.put("medianCVCoverage",findQCResult(qcList, "Median_CV_Coverage_1000x"));
            contentsMap.put("q30ScoreRead1",findQCResult(qcList, "Q30_score_read1"));
            contentsMap.put("q30ScoreRead2",findQCResult(qcList, "Q30_score_read2"));

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

        //}

        return contentsMap;
    }

    private boolean createPDF(boolean isDraft) {
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
            } else {
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
            }
            fileChooser.setInitialFileName(baseSaveName);
            File file = fileChooser.showSaveDialog(this.getMainApp().getPrimaryStage());

            if(file != null) {

                mainController.setMainMaskerPane(true);

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
                    contents = velocityUtil.getContents("/layout/velocity/report_tst_rna.vm", CommonConstants.ENCODING_TYPE_UTF, model);
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

                        String test = JsonUtil.toJsonIncludeNullValue(contentsMap);

                        Task<Void> task = new WordDownloadTask(this, component, test, file);
                        final Thread downloadThread = new Thread(task);
                        downloadThread.setDaemon(true);
                        downloadThread.start();

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

                        final String contents1 = velocityUtil.getContents(reportContents.getReportTemplate().getId() + "/" + reportContents.getReportTemplate().getName() + ".vm", CommonConstants.ENCODING_TYPE_UTF, model);
                        task.setOnSucceeded(ev -> {
                            try {
                                final boolean created1 = pdfCreateService.createPDF(file, contents1);
                                createdCheck(created1, file);

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
        } finally {
            mainController.setMainMaskerPane(false);
        }
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
}
