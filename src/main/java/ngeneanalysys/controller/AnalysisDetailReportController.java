package ngeneanalysys.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
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
import ngeneanalysys.service.PDFCreateService;
import ngeneanalysys.task.ImageFileDownloadTask;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
public class AnalysisDetailReportController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    private static final String CONFIRMATION_DIALOG = "Confirmation Dialog";

    /** api service */
    private APIService apiService;

    private PDFCreateService pdfCreateService;

    /** Velocity Util */
    private VelocityUtil velocityUtil = new VelocityUtil();

    @FXML
    private Label diseaseLabel;

    @FXML
    private Label panelLabel;

    @FXML
    private TextArea conclusionsTextArea;

    @FXML
    private TableView<VariantAndInterpretationEvidence> tierOneVariantsTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierOneGeneColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierOneVariantsColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierOneTherapeuticColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, BigDecimal> tierOneAlleleFrequencyColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierOneDrugColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, Boolean> tierOneExceptColumn;

    @FXML
    private TableView<VariantAndInterpretationEvidence> tierTwoVariantsTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierTwoGeneColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierTwoVariantsColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierTwoTherapeuticColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, BigDecimal> tierTwoAlleleFrequencyColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierTwoDrugColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, Boolean> tierTwoExceptColumn;

    @FXML
    private TableView<VariantAndInterpretationEvidence> tierThreeVariantsTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierThreeGeneColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierThreeVariantsColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierThreeTherapeuticColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, BigDecimal> tierThreeAlleleFrequencyColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, Boolean> tierThreeExceptColumn;

    @FXML
    private TableView<VariantAndInterpretationEvidence> negativeVariantsTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> negativeGeneColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> negativeVariantsColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> negativeCauseColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> negativeAlleleFrequencyColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, Boolean> negativeExceptColumn;

    @FXML
    private GridPane customFieldGridPane;

    @FXML
    private VBox contentVBox;

    @FXML
    private Pane mainContentsPane;

    @FXML
    private Label conclusions;

    @FXML
    private Label patientIdLabel;

    @FXML
    private ComboBox<ComboBoxItem> virtualPanelComboBox;

    Sample sample = null;

    Panel panel = null;

    List<VariantAndInterpretationEvidence> tierOne = null;

    List<VariantAndInterpretationEvidence> tierTwo = null;

    List<VariantAndInterpretationEvidence> tierThree = null;

    List<VariantAndInterpretationEvidence> tierFour = null;

    List<VariantAndInterpretationEvidence> negativeList = null;

    private VariantAndInterpretationEvidence selectedItem = null;

    private TableView<VariantAndInterpretationEvidence> selectedTable = null;

    private TableRow<VariantAndInterpretationEvidence> rowItem;

    private boolean reportData = false;

    @FXML
    private GridPane mainGridPane;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");

        tableCellUpdateFix(tierOneVariantsTable);
        tableCellUpdateFix(tierTwoVariantsTable);
        tableCellUpdateFix(tierThreeVariantsTable);
        tableCellUpdateFix(negativeVariantsTable);

        //Drag & Drop 으로 Variant의 Tier를 변경

        settingTableViewDragAndDrop(tierOneVariantsTable, "T1");
        settingTableViewDragAndDrop(tierTwoVariantsTable, "T2");
        settingTableViewDragAndDrop(tierThreeVariantsTable, "T3");

        // API 서비스 클래스 init
        apiService = APIService.getInstance();

        apiService.setStage(getMainController().getPrimaryStage());

        loginSession = LoginSessionUtil.getCurrentLoginSession();

        pdfCreateService = PDFCreateService.getInstance();

        customFieldGridPane.getChildren().clear();
        customFieldGridPane.setPrefHeight(0);

        sample = (Sample)paramMap.get("sample");

        patientIdLabel.setText(sample.getPaitentId());

        tierOneGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        tierOneVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        tierOneAlleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()));
        tierOneExceptColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        tierOneExceptColumn.setCellFactory(param -> new ReportedCheckBox(this));
        tierOneTherapeuticColumn.setCellValueFactory(cellData -> returnTherapeutic(cellData.getValue().getInterpretationEvidence()));

        tierTwoGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        tierTwoVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        tierTwoAlleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()));
        tierTwoExceptColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        tierTwoExceptColumn.setCellFactory(param -> new ReportedCheckBox(this));
        tierTwoTherapeuticColumn.setCellValueFactory(cellData -> returnTherapeutic(cellData.getValue().getInterpretationEvidence()));

        tierThreeGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        tierThreeVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        tierThreeAlleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()));
        tierThreeExceptColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        tierThreeExceptColumn.setCellFactory(param -> new ReportedCheckBox(this));
        tierThreeTherapeuticColumn.setCellValueFactory(cellData -> returnTherapeutic(cellData.getValue().getInterpretationEvidence()));

        negativeGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        negativeVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        //negativeCauseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInterpretationEvidence().getEvidencePersistentNegative()));
        negativeAlleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction().toString()));
        negativeExceptColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        negativeExceptColumn.setCellFactory(param -> new ReportedCheckBox(this));

        List<Diseases> diseases = (List<Diseases>) mainController.getBasicInformationMap().get("diseases");
        Optional<Diseases> diseasesOptional = diseases.stream().filter(disease -> disease.getId() == sample.getDiseaseId()).findFirst();
        if(diseasesOptional.isPresent()) {
            String diseaseName = diseasesOptional.get().getName();
            diseaseLabel.setText(diseaseName);
        }

        List<Panel> panels = (List<Panel>) mainController.getBasicInformationMap().get("panels");
        Optional<Panel> panelOptional = panels.stream().filter(panelItem -> panelItem.getId().equals(sample.getPanelId())).findFirst();
        if(panelOptional.isPresent()) {
            panel = panelOptional.get();
            String panelName = panel.getName();
            panelLabel.setText(panelName);
        }

        setVirtualPanel();

        HttpClientResponse response = null;

        try {
            if(panel.getReportTemplateId() != null) {
                response = apiService.get("reportTemplate/" + panel.getReportTemplateId(), null, null, false);

                ReportContents reportContents = response.getObjectBeforeConvertResponseToJSON(ReportContents.class);

                ReportTemplate template = reportContents.getReportTemplate();
                if (template.getContents() != null) {
                    Map<String, Object> variableList = JsonUtil.fromJsonToMap(template.getCustomFields());

                    if (variableList != null && !variableList.isEmpty()) {

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
                            label.setStyle("-fx-text-fill : #C20E20;");
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
            }
            if(sample.getSampleStatus().getReportStartedAt() != null) {
                response = apiService.get("sampleReport/" + sample.getId(), null, null, false);

                if (response.getStatus() == 200) {
                    reportData = true;
                    SampleReport sampleReport = response.getObjectBeforeConvertResponseToJSON(SampleReport.class);
                    if(sampleReport.getVirtualPanelId() != null) {
                        Optional<ComboBoxItem> item = virtualPanelComboBox.getItems().stream().filter(
                                comboBoxItem -> comboBoxItem.getValue().equals(sampleReport.getVirtualPanelId().toString())).findFirst();
                        if(item.isPresent()) {
                            virtualPanelComboBox.getSelectionModel().select(item.get());
                        }
                    }
                    settingReportData(sampleReport.getContents());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        virtualPanelComboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            if(!t1.equals(t)) setVariantsList();
        });

    }

    public void tableCellUpdateFix(TableView<VariantAndInterpretationEvidence> tableView) {
        tableView.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            tableView.refresh();
            // close text box
            tableView.edit(-1, null);
        });
    }

    public Set<String> returnGeneList(String essentialGenes, String optionalGenes) {
        Set<String> list = new HashSet<>();

        essentialGenes = essentialGenes.replaceAll("\\p{Z}", "");

        list.addAll(Arrays.stream(essentialGenes.split(",")).collect(Collectors.toSet()));

        if(!StringUtils.isEmpty(optionalGenes)) {
            optionalGenes = optionalGenes.replaceAll("\\p{Z}", "");

            list.addAll(Arrays.stream(optionalGenes.split(",")).collect(Collectors.toSet()));
        }

        return list;
    }

    public List<VariantAndInterpretationEvidence> filteringVariant(List<VariantAndInterpretationEvidence> list) {
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

    public List<VariantCountByGene> filteringGeneList(List<VariantCountByGene> list) {
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

            }
        } else {
            return list;
        }

        if(filteringList.isEmpty()) {
            return list;
        }

        return filteringList;
    }

    public void setVariantsList() {
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

            orderingAndAddTableItem(tierOneVariantsTable, tierOne);

            orderingAndAddTableItem(tierTwoVariantsTable, tierTwo);

            orderingAndAddTableItem(tierThreeVariantsTable, tierThree);

            orderingAndAddTableItem(negativeVariantsTable, negativeList);

        } catch (WebAPIException wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getContents(), this.getMainApp().getPrimaryStage(), true);
        }
    }

    public void setVirtualPanel() {

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

    public void settingTableViewDragAndDrop(TableView<VariantAndInterpretationEvidence> tableView, String tier) {
        if(!StringUtils.isEmpty(tier) && tableView != null) {
            tableView.setOnDragDetected(e -> onDragDetected(e, tableView));
            tableView.setOnDragDone(e -> onDragDone(e));
            tableView.setOnDragOver(e -> onDragOver(e, tableView));
            tableView.setOnDragExited(e -> onDragExited(e, tableView));
            tableView.setOnDragDropped(e -> onDragDropped(e, tableView, tier));
            tableView.setOnMouseClicked(e -> onClicked(e, tableView , tier));

            tableView.setRowFactory(tv -> {
                TableRow<VariantAndInterpretationEvidence> rowData = new TableRow<>();
                rowData.setOnDragDetected(e -> rowItem = rowData);
                return rowData;
            });
        }
    }

    public void orderingAndAddTableItem(TableView<VariantAndInterpretationEvidence> tableView,
                                        List<VariantAndInterpretationEvidence> tierList) {
        if(tierList != null && !tierList.isEmpty()) {

            if(tableView.getItems() != null && !tableView.getItems().isEmpty()) {
                tableView.getItems().removeAll(tableView.getItems());
            }

            Collections.sort(tierList,
                    (a, b) -> a.getSnpInDel().getGenomicCoordinate().getGene().compareTo(b.getSnpInDel().getGenomicCoordinate().getGene()));
            tableView.getItems().addAll(tierList);
        }
    }

    public List<VariantAndInterpretationEvidence> settingTierList(List<VariantAndInterpretationEvidence> allTierList, String tier) {
        if(!StringUtils.isEmpty(tier)) {
            return allTierList.stream().filter(item -> ((tier.equalsIgnoreCase(item.getSnpInDel().getExpertTier()) ||
                    (StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && item.getSnpInDel().getSwTier().equalsIgnoreCase(tier)))))
                    .collect(Collectors.toList());
        }

        return null;
    }

    public SimpleStringProperty returnTherapeutic(SnpInDelInterpretation snpInDelInterpretation) {
        String text = "";
        if(snpInDelInterpretation != null) {
            if (!StringUtils.isEmpty(snpInDelInterpretation.getEvidenceLevelA()))
                text += snpInDelInterpretation.getEvidenceLevelA() + ", ";
            if (!StringUtils.isEmpty(snpInDelInterpretation.getEvidenceLevelB()))
                text += snpInDelInterpretation.getEvidenceLevelB() + ", ";
            if (!StringUtils.isEmpty(snpInDelInterpretation.getEvidenceLevelC()))
                text += snpInDelInterpretation.getEvidenceLevelC() + ", ";
            if (!StringUtils.isEmpty(snpInDelInterpretation.getEvidenceLevelD()))
                text += snpInDelInterpretation.getEvidenceLevelD() + ", ";
        }
        if(!"".equals(text)) {
            text = text.substring(0, text.length() - 2);
        }

        return new SimpleStringProperty(text);
    }

    public void settingReportData(String contents) {

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
            }
        }

    }

    /**
     * 입력 정보 저장
     * @param user
     * @return
     */
    public boolean saveData(User user) {

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
            }
        }
        String contents = JsonUtil.toJson(contentsMap);
        params.put("contents", contents);
        if(!StringUtils.isEmpty(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue())) {
            params.put("virtualPanelId", Integer.parseInt(virtualPanelComboBox.getSelectionModel().getSelectedItem().getValue()));
        }

        HttpClientResponse response = null;
        try {
            if(reportData) {
                response = apiService.put("/sampleReport/" + sample.getId(), params, null, true);
            } else {
                response = apiService.post("/sampleReport/" + sample.getId(), params, null, true);
            }
        } catch (WebAPIException wae) {
            wae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @FXML
    public void save() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(getMainController().getPrimaryStage());
        alert.setTitle(CONFIRMATION_DIALOG);
        alert.setHeaderText("Save Report Information");
        alert.setContentText("Do you want to save?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            boolean dataSave = saveData(null);
            if(dataSave) {
                DialogUtil.alert("Save Success", "Input data is successfully saved.", getMainController().getPrimaryStage(), false);
            }
        } else {
            alert.close();
        }
    }

    @FXML
    public void createPDFAsDraft() {
        boolean dataSave = saveData(null);
        if(dataSave){
            createPDF(true);
        }
    }

    @FXML
    public void createPDFAsFinal() {
        User user;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(getMainController().getPrimaryStage());
        alert.setTitle(CONFIRMATION_DIALOG);
        alert.setHeaderText("Test conducting organization information");
        alert.setContentText(String.format("Test conducting organization information will be filled with current user [ %s ] information for final report generation.\n\nDo you want to proceed?", loginSession.getName()));

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            try {
                HttpClientResponse response = apiService.get("/members", null,
                        null, false);
                user = response.getObjectBeforeConvertResponseToJSON(User.class);
                // 소속기관, 연락처 정보 존재 확인
                if (!StringUtils.isEmpty(user.getOrganization()) && !StringUtils.isEmpty(user.getPhone())) {
                    boolean dataSave = saveData(user);
                    if (dataSave) {
                        // 최종 보고서 생성이 정상 처리된 경우 분석 샘플의 상태값 완료 처리.
                        if (createPDF(false)) {
                            setComplete();
                        }
                    }
                } else {
                    DialogUtil.warning("Empty Reviewer Information",
                            "Please Input a Reviewer Information. [Menu > Edit]", getMainApp().getPrimaryStage(), true);
                }

            }  catch (WebAPIException wae) {
                logger.error("web api exception", wae);
                DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                        getMainApp().getPrimaryStage(), true);
            } catch (Exception e) {
                logger.error("exception", e);
                DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
            }
        } else {
            alert.close();
        }
    }

    @FXML
    public void confirmPDFAsFinal() {
        createPDF(false);
    }

    public void convertPDFtoImage(File file, String baseFileName) {
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
    }

    public boolean createPDF(boolean isDraft) {
        boolean created = true;
        String reportCreationErrorMsg = "An error occurred during the creation of the report document.";

        // 보고서 파일명 기본값
        String baseSaveName = String.format("FINAL_Report_%s", sample.getName());
        if(isDraft) {
            baseSaveName = String.format("DRAFT_Report_%s", sample.getName());
        }
        // Show save bedFile dialog
        FileChooser fileChooser = new FileChooser();
        if(panel.getName().equalsIgnoreCase("HEMEaccuTest CNUHH 123 v1")) {
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF (*.docx)", "*.docx"));
        } else {
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
        }
        fileChooser.setInitialFileName(baseSaveName);
        File file = fileChooser.showSaveDialog(this.getMainApp().getPrimaryStage());

        try {
            if(file != null) {
                String draftImageStr = String.format("url('%s')", this.getClass().getClassLoader().getResource("layout/images/DRAFT.png"));
                String ngenebioLogo = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/ngenebio_logo.png"));
                String testInformationText = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/test_information1.png"));
                String pathogenicMutationsDetectedText = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/pathogenic_mutations_detected1.png"));
                String pertinetNegativeText = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/pertinent_negative.png"));
                String variantDetailText = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/variant_detail.png"));
                String dataQcText = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/data_qc.png"));
                Map<String,Object> contentsMap = new HashMap<>();
                contentsMap.put("panelName", panel.getName());
                contentsMap.put("diseaseName", diseaseLabel.getText());
                contentsMap.put("sampleSource", panel.getSampleSource());
                contentsMap.put("panelCode", panel.getCode());
                contentsMap.put("sampleName", sample.getName());
                contentsMap.put("patientCode", "SS17-01182");
                SecureRandom random = new SecureRandom();
                contentsMap.put("reportID", String.format("%05d-%05d", sample.getId(), random.nextInt(99999)));

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM. dd yyyy");
                contentsMap.put("date", sdf.format(date));

                List<VariantAndInterpretationEvidence> variantList = new ArrayList<>();
                if(!tierOneVariantsTable.getItems().isEmpty()) variantList.addAll(tierOneVariantsTable.getItems().stream().collect(Collectors.toList()));
                if(!tierTwoVariantsTable.getItems().isEmpty()) variantList.addAll(tierTwoVariantsTable.getItems().stream().collect(Collectors.toList()));
                //if(tierOne != null && !tierOne.isEmpty()) variantList.addAll(tierOne);
                //if(tierTwo != null && !tierTwo.isEmpty()) variantList.addAll(tierTwo);

                List<VariantAndInterpretationEvidence> negativeResult = new ArrayList<>();
                //리포트에서 제외된 negative 정보를 제거
                if(negativeList != null && !negativeList.isEmpty()) {
                    negativeResult.addAll(negativeVariantsTable.getItems().stream().filter(item -> item.getSnpInDel().getIncludedInReport().equals("Y")).collect(Collectors.toList()));
                }
                //리포트에서 제외된 variant를 제거
                if(!variantList.isEmpty()) {
                    variantList = variantList.stream().filter(item -> item.getSnpInDel().getIncludedInReport().equals("Y")).collect(Collectors.toList());
                }

                List<VariantAndInterpretationEvidence> clinicalVariantList = new ArrayList<>();

                clinicalVariantList.addAll(variantList);

                //if(tierThree != null && !tierThree.isEmpty()) variantList.addAll(tierThree);
                if(!tierThreeVariantsTable.getItems().isEmpty()) variantList.addAll(tierThreeVariantsTable.getItems().stream().filter(tierThree ->
                tierThree.getSnpInDel().getIncludedInReport().equalsIgnoreCase("Y")).collect(Collectors.toList()));

                for(VariantAndInterpretationEvidence variant : variantList) {
                    variant.getSnpInDel().getSnpInDelExpression().setTranscript(ConvertUtil.insertTextAtFixedPosition(variant.getSnpInDel().getSnpInDelExpression().getTranscript(), 15, "\n"));
                    variant.getSnpInDel().getSnpInDelExpression().setNtChange(ConvertUtil.insertTextAtFixedPosition(variant.getSnpInDel().getSnpInDelExpression().getNtChange(), 15, "\n"));
                    variant.getSnpInDel().getSnpInDelExpression().setAaChange(ConvertUtil.insertTextAtFixedPosition(variant.getSnpInDel().getSnpInDelExpression().getAaChange(), 15, "\n"));
                }

                Integer evidenceACount = 0;
                Integer evidenceBCount = 0;
                Integer evidenceCCount = 0;
                Integer evidenceDCount = 0;

                if(!tierOneVariantsTable.getItems().isEmpty()) {
                    for(VariantAndInterpretationEvidence variant : tierOneVariantsTable.getItems().stream().filter(tierOne ->
                    tierOne.getSnpInDel().getIncludedInReport().equalsIgnoreCase("Y")).collect(Collectors.toList())) {
                        if("Y".equalsIgnoreCase(variant.getSnpInDel().getIncludedInReport()) && variant.getInterpretationEvidence() != null) {
                            if(!StringUtils.isEmpty(variant.getInterpretationEvidence().getEvidenceLevelA())) evidenceACount++;
                            if(!StringUtils.isEmpty(variant.getInterpretationEvidence().getEvidenceLevelB())) evidenceBCount++;
                            if(!StringUtils.isEmpty(variant.getInterpretationEvidence().getEvidenceLevelC())) evidenceCCount++;
                            if(!StringUtils.isEmpty(variant.getInterpretationEvidence().getEvidenceLevelD())) evidenceDCount++;
                            if("T2".equals(variant.getSnpInDel().getSwTier())
                                    && StringUtils.isEmpty(variant.getInterpretationEvidence().getEvidenceLevelB())) evidenceBCount++;
                        }
                    }
                }

                if(!tierTwoVariantsTable.getItems().isEmpty()) {
                    for(VariantAndInterpretationEvidence variant : tierTwoVariantsTable.getItems().stream().filter(tierTwo ->
                            tierTwo.getSnpInDel().getIncludedInReport().equalsIgnoreCase("Y")).collect(Collectors.toList())) {
                        if("Y".equalsIgnoreCase(variant.getSnpInDel().getIncludedInReport()) && variant.getInterpretationEvidence() != null) {
                            if(!StringUtils.isEmpty(variant.getInterpretationEvidence().getEvidenceLevelA())) evidenceACount++;
                            if(!StringUtils.isEmpty(variant.getInterpretationEvidence().getEvidenceLevelB())) evidenceBCount++;
                            if(!StringUtils.isEmpty(variant.getInterpretationEvidence().getEvidenceLevelC())) evidenceCCount++;
                            if(!StringUtils.isEmpty(variant.getInterpretationEvidence().getEvidenceLevelD())) evidenceDCount++;
                            if("T1".equals(variant.getSnpInDel().getSwTier())
                                    && StringUtils.isEmpty(variant.getInterpretationEvidence().getEvidenceLevelD())) evidenceDCount++;
                        }
                    }
                }

                contentsMap.put("clinicalVariantList", clinicalVariantList);
                contentsMap.put("variantList", variantList);
                contentsMap.put("tierThreeVariantList", tierThree);
                contentsMap.put("tierOneCount", tierOneVariantsTable.getItems().filtered(item -> item.getSnpInDel().getIncludedInReport().equals("Y")).size());
                contentsMap.put("tierTwoCount", tierTwoVariantsTable.getItems().filtered(item -> item.getSnpInDel().getIncludedInReport().equals("Y")).size());
                contentsMap.put("tierThreeCount", tierThreeVariantsTable.getItems().filtered(item -> item.getSnpInDel().getIncludedInReport().equals("Y")).size());
                contentsMap.put("tierFourCount", (tierFour != null) ? tierFour.size() : 0);
                contentsMap.put("evidenceACount", evidenceACount);
                contentsMap.put("evidenceBCount", evidenceBCount);
                contentsMap.put("evidenceCCount", evidenceCCount);
                contentsMap.put("evidenceDCount", evidenceDCount);
                contentsMap.put("negativeList", negativeResult);
                contentsMap.put("qcData", sample.getQcData());

                //Genes in panel
                try {
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

                            list.addAll(Arrays.stream(virtualPanel.getEssentialGenes().replaceAll("\\p{Z}", "")
                                    .split(",")).collect(Collectors.toSet()));

                            Set<String> allGeneList = returnGeneList(virtualPanel.getEssentialGenes(), virtualPanel.getOptionalGenes());

                            contentsMap.put("essentialGenes", list);
                            contentsMap.put("allGeneList", allGeneList);
                            contentsMap.put("virtualPanelName", virtualPanel.getName());

                        }

                        response = apiService.get("/runs/" + sample.getRunId(), null,
                                null, false);

                        RunWithSamples runWithSamples = response.getObjectBeforeConvertResponseToJSON(RunWithSamples.class);
                        String runSequencer = runWithSamples.getRun().getSequencingPlatform();

                        if(runSequencer.equalsIgnoreCase("MISEQ")) {
                            contentsMap.put("sequencer",SequencerCode.MISEQ.getDescription());
                        } else {
                            contentsMap.put("sequencer",SequencerCode.MISEQ_DX.getDescription());
                        }

                        response = apiService.get("/analysisResults/sampleQCs/" + sample.getId(), null,
                                null, false);

                        List<SampleQC> qcList = (List<SampleQC>) response.getMultiObjectBeforeConvertResponseToJSON(SampleQC.class, false);

                        contentsMap.put("totalBase",findQCResult(qcList, "total_base"));
                        contentsMap.put("q30",findQCResult(qcList, "q30_trimmed_base"));
                        contentsMap.put("mappedBase",findQCResult(qcList, "mapped_base"));
                        contentsMap.put("onTarget",findQCResult(qcList, "on_target"));
                        contentsMap.put("onTargetCoverage",findQCResult(qcList, "on_target_coverage"));
                        contentsMap.put("duplicatedReads",findQCResult(qcList, "duplicated_reads"));
                        contentsMap.put("roiCoverage",findQCResult(qcList, "roi_coverage"));

                        List<String> conclusionLineList = null;
                        if(!StringUtils.isEmpty(conclusionsTextArea.getText())) {
                            conclusionLineList = new ArrayList<>();
                            String[] lines = conclusionsTextArea.getText().split("\n");
                            if(lines != null && lines.length > 0) {
                                for (String line : lines) {
                                    conclusionLineList.add(line);
                                }
                            } else {
                                conclusionLineList.add(conclusionsTextArea.getText());
                            }
                        }
                        contentsMap.put("conclusions", conclusionLineList);

                    }
                } catch (WebAPIException wae) {
                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                            getMainApp().getPrimaryStage(), true);
                }

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
                    contents = velocityUtil.getContents("/layout/velocity/report.vm", "UTF-8", model);
                    created = pdfCreateService.createPDF(file, contents);
                    createdCheck(created, file);
                    //convertPDFtoImage(file, sample.getName());
                } else {
                    for(int i = 0; i < customFieldGridPane.getChildren().size(); i++) {
                        Object gridObject = customFieldGridPane.getChildren().get(i);

                        if(gridObject instanceof TextField) {
                            TextField textField = (TextField)gridObject;
                            contentsMap.put(textField.getId(), textField.getText());
                        } else if(gridObject instanceof DatePicker) {
                            DatePicker datePicker = (DatePicker)gridObject;
                            if(datePicker.getValue() != null) {
                                contentsMap.put(datePicker.getId(), datePicker.getValue().toString());
                            } else {
                                contentsMap.put(datePicker.getId(), "");
                            }
                        }
                    }

                    HttpClientResponse response = apiService.get("reportTemplate/" + panel.getReportTemplateId(), null, null, false);

                    ReportContents reportContents = response.getObjectBeforeConvertResponseToJSON(ReportContents.class);

                    List<ReportImage> images = reportContents.getReportImages();

                    for(ReportImage image : images) {
                        String path = "url('file:/" + CommonConstants.BASE_FULL_PATH  + File.separator + "fop" + File.separator + image.getReportTemplateId()
                                + File.separator + image.getName() + "')";
                        path = path.replaceAll("\\\\", "/");
                        String name = image.getName().substring(0, image.getName().lastIndexOf('.'));
                        logger.info(name + " : " + path);
                        model.put(name, path);
                    }

                    FileUtil.saveVMFile(reportContents.getReportTemplate());

                    Task task = new ImageFileDownloadTask(this, reportContents.getReportImages());

                    Thread thread = new Thread(task);
                    thread.setDaemon(true);
                    thread.start();

                    final String contents1 = velocityUtil.getContents( reportContents.getReportTemplate().getId()+ "/" + reportContents.getReportTemplate().getName() + ".vm", "UTF-8", model);

                    task.setOnSucceeded(ev -> {
                        try {
                            //이미지파일이 모두 다운로드 되었다면 PDF 파일을 생성함
                            if(panel.getName().equalsIgnoreCase("HEMEaccuTest CNUHH 123 v1")) {
                                /*URL url = resourceUtil.getResourceURL("word-creater.jar");
                                File gocrygo = new File(CommonConstants.BASE_FULL_PATH, "word-creater.jar");
                                URL[] jarUrls = new URL[]{gocrygo.toURI().toURL()};
                                URLClassLoader classLoader = new URLClassLoader(jarUrls, ClassLoader.getSystemClassLoader());

                                Thread.currentThread().setContextClassLoader(classLoader);
                                @SuppressWarnings("unchecked")
                                Class classToLoad = Class.forName("word.create.app.App", true, classLoader);
                                logger.info("application init..");
                                Method[] methods = classToLoad.getMethods();
                                Method method = classToLoad.getDeclaredMethod("init");
                                Method setParams = classToLoad.getMethod("setParams");
                                Method updateEmbeddedDoc = classToLoad.getMethod("updateEmbeddedDoc");
                                Method updateWordFile = classToLoad.getDeclaredMethod("updateWordFile");
                                Object application = classToLoad.newInstance();
                                Object result = method.invoke(application);
                                result = setParams.invoke(application, contentsMap);
                                result = updateEmbeddedDoc.invoke(application);
                                result = updateWordFile.invoke(application);
                                System.out.print("test");*/
                                WordCreator wc = WordCreator.getInstance();
                                wc.setWriteFilePath(file.getPath());
                                wc.setParams(contentsMap);
                                //wc.updateEmbeddedDoc(resourceUtil.getResourceURL("/config/test.docx").getPath());
                                wc.updateEmbeddedDoc(resourceUtil.getResourceAsStream("/layout/word/test.docx"));
                                wc.updateWordFile();
                                createdCheck(true, file);
                            } else {
                                final boolean created1 = pdfCreateService.createPDF(file, contents1);
                                createdCheck(created1, file);
                            }
                            //convertPDFtoImage(file, sample.getName());
                        } catch (Exception e) {
                            DialogUtil.error("Save Fail.", reportCreationErrorMsg + "\n" + e.getMessage(), getMainApp().getPrimaryStage(), false);
                            e.printStackTrace();
                        }
                    });

                }

            }
        } catch(FileNotFoundException fnfe){
            DialogUtil.error("Save Fail.", reportCreationErrorMsg + "\n" + fnfe.getMessage(), getMainApp().getPrimaryStage(), false);
        } catch (Exception e) {
            DialogUtil.error("Save Fail.", reportCreationErrorMsg + "\n" + e.getMessage(), getMainApp().getPrimaryStage(), false);
            e.printStackTrace();
            created = false;
        }

        return created;
    }

    public void createdCheck(boolean created, File file) {
        try {
            if (created) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(getMainController().getPrimaryStage());
                alert.setTitle(CONFIRMATION_DIALOG);
                alert.setHeaderText("Creating the report document was completed.");
                alert.setContentText("Do you want to check the report document?");

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
            e.printStackTrace();
        }
    }

    private SampleQC findQCResult(List<SampleQC> qcList, String qc) {
        if(qcList != null && !qcList.isEmpty()) {
            Optional<SampleQC> findQC = qcList.stream().filter(sampleQC -> sampleQC.getQcType().equalsIgnoreCase(qc)).findFirst();
            if(findQC.isPresent()) {
                    SampleQC qcData = findQC.get();
                    String number = findQC.get().getQcValue().toString();
                    Long value = Math.round(Double.parseDouble(number));
                if(qc.equalsIgnoreCase("total_base")) {
                    qcData.setQcUnit("Mb");
                    qcData.setQcValue(BigDecimal.valueOf(value / 1024 / 1024));
                    return qcData;
                }
                qcData.setQcValue(BigDecimal.valueOf(value));
                return qcData;
            }
        }
        return null;
    }

    /**
     * 보고서 작업 완료 처리
     */
    public void setComplete() {
        /*try {
            // step_report 상태값 완료 처리.
            int jobStatusId = sample.getJobStatus().getId();
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("step_report", AnalysisJobStatusCode.SAMPLE_JOB_STATUS_COMPLETE);
            param.put("step msg", "Complete Review and Reporting");
            apiService.patch("/analysis_progress_state/jobstatus_update/" + jobStatusId, param, null, true);
            // 상태값 변경이 정상 처리된 경우 화면 상태 변경 처리.
            // 보고서 작업화면 비활성화
            setActive(false);
            // 상위 레이아웃 컨트롤러의 완료 처리 메소드 실행
            getAnalysisDetailLayoutController().setReviewComplete();
        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }*/
    }


    private class ReportedCheckBox extends TableCell<VariantAndInterpretationEvidence, Boolean> {
        HBox box = null;
        final CheckBox checkBox = new CheckBox();

        ReportedCheckBox(AnalysisDetailReportController analysisDetailReportController) {
            checkBox.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                VariantAndInterpretationEvidence analysisResultVariant = ReportedCheckBox.this.getTableView().getItems().get(
                        ReportedCheckBox.this.getIndex());

                selectedItem = analysisResultVariant;

                TextInputDialog dialog = new TextInputDialog();
                dialog.setContentText("cause:");
                dialog.setTitle("Reported variant");

                if (!checkBox.isSelected()) {
                    try {
                        FXMLLoader loader = mainApp.load(FXMLConstants.EXCLUDE_REPORT);
                        Node root = loader.load();
                        ExcludeReportDialogController excludeReportDialogController = loader.getController();
                        excludeReportDialogController.setMainController(mainController);
                        //excludeReportDialogController.setAnalysisDetailReportController(analysisDetailReportController);
                        excludeReportDialogController.settingItem("Y", selectedItem, checkBox);
                        excludeReportDialogController.show((Parent) root);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                } else {
                    try {
                        FXMLLoader loader = mainApp.load(FXMLConstants.EXCLUDE_REPORT);
                        Node root = loader.load();
                        ExcludeReportDialogController excludeReportDialogController = loader.getController();
                        excludeReportDialogController.setMainController(mainController);
                        excludeReportDialogController.settingItem("N", selectedItem, checkBox);
                        excludeReportDialogController.show((Parent) root);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            });
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(isEmpty()) {
                setGraphic(null);
                return;
            }
            if(box == null) {
                VariantAndInterpretationEvidence analysisResultVariant = ReportedCheckBox.this.getTableView().getItems().get(
                        ReportedCheckBox.this.getIndex());

                if (analysisResultVariant.getSnpInDel().getIncludedInReport().equalsIgnoreCase("N")) {
                    checkBox.setSelected(true);
                }

                box = new HBox();

                box.setAlignment(Pos.CENTER);

                box.setSpacing(10);
                checkBox.setStyle("-fx-cursor:hand;");
                box.getChildren().add(checkBox);
            }

            setGraphic(box);

        }

    }

    ////////////////////////////////////////////////////////////////////////////////////

    /*
    TableView 간의 Drag and Drop 기능 구현
     */

    /**
     *
     * @param e
     * @param table
     */
    public void onDragDetected(MouseEvent e, TableView<VariantAndInterpretationEvidence> table) {
        VariantAndInterpretationEvidence selectedVariant = table.getSelectionModel().getSelectedItem();
        if(selectedVariant == null) return;

        selectedTable = table;

        selectedItem = selectedVariant;

        Dragboard db = table.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        //저장된 row를 기준으로 스냅샷 생성
        SnapshotParameters sp = new SnapshotParameters();
        sp.setTransform(Transform.scale(2, 2));

        db.setDragView(rowItem.snapshot(sp, null));

        content.putString(selectedVariant.toString());
        db.setContent(content);
        e.consume();
    }

    public void onDragDone(DragEvent e) {
        e.consume();
    }

    public void onDragOver(DragEvent t, TableView<VariantAndInterpretationEvidence> table) {
        if(selectedTable != table) {
            t.acceptTransferModes(TransferMode.ANY);
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(5.0);
            dropShadow.setOffsetX(3.0);
            dropShadow.setOffsetY(3.0);
            dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
            table.setEffect(dropShadow);
        }
    }

    public void onDragExited(DragEvent t, TableView<VariantAndInterpretationEvidence> table) {
        t.acceptTransferModes(TransferMode.ANY);
        table.setEffect(null);
        t.consume();
    }

    public void onDragDropped(DragEvent t, TableView<VariantAndInterpretationEvidence> table, String tier) {
        if(selectedItem != null && selectedTable != table) {
            try {
                FXMLLoader loader = mainApp.load(FXMLConstants.CHANGE_TIER);
                Node root = loader.load();
                ChangeTierDialogController changeTierDialogController = loader.getController();
                changeTierDialogController.setMainController(this.getMainController());
                changeTierDialogController.setAnalysisDetailReportController(this);
                changeTierDialogController.settingItem(table, tier, selectedItem, rowItem);
                changeTierDialogController.show((Parent) root);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
        t.setDropCompleted(true);
    }

    public void onClicked(MouseEvent e,TableView<VariantAndInterpretationEvidence> table, String tier) {

        if(e.getClickCount() == 2) {
            VariantAndInterpretationEvidence variantAndInterpretationEvidence = table.getSelectionModel().getSelectedItem();
            try {
                FXMLLoader loader = mainApp.load(FXMLConstants.CHANGE_TIER);
                Node root = loader.load();
                ChangeTierDialogController changeTierDialogController = loader.getController();
                changeTierDialogController.setMainController(this.getMainController());
                changeTierDialogController.setAnalysisDetailReportController(this);
                changeTierDialogController.settingTier(tier, variantAndInterpretationEvidence);
                changeTierDialogController.show((Parent) root);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void resetData(TableView<VariantAndInterpretationEvidence> table) {
        if(selectedTable != null && table != null) {
            selectedTable.getItems().remove(selectedItem);
            table.getItems().add(selectedItem);
        }
        selectedItem = null;
        selectedTable = null;
        rowItem = null;
    }

    public void selectClear(String tier) {
        if(tier.equals("T1")) {
            tierOneVariantsTable.getSelectionModel().clearSelection();
        } else if(tier.equals("T2")) {
            tierTwoVariantsTable.getSelectionModel().clearSelection();
        } else if(tier.equals("T3")) {
            tierThreeVariantsTable.getSelectionModel().clearSelection();
        } else if(tier.equals("TN")) {
            negativeVariantsTable.getSelectionModel().clearSelection();
        }
    }
}
