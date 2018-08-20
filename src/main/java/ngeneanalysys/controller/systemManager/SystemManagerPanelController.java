package ngeneanalysys.controller.systemManager;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.code.enums.SampleSourceCode;
import ngeneanalysys.controller.VirtualPanelEditController;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedPanel;
import ngeneanalysys.model.paged.PagedPanelView;
import ngeneanalysys.model.paged.PagedReportTemplate;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.PanelTextFileSaveService;
import ngeneanalysys.task.BedFileUploadTask;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.FXMLLoadUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-09-25
 */
public class SystemManagerPanelController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @FXML
    private GridPane basicInformationGridPane;

    @FXML
    private GridPane snpPopulationFrequencyFilterGridPane;

    @FXML
    private TextField panelNameTextField;

    @FXML
    private ComboBox<ComboBoxItem> pipelineComboBox;

    @FXML
    private Label panelInfoLabel;

    @FXML
    private ComboBox<SampleSourceCode> defaultSampleSourceComboBox;

    @FXML
    private ComboBox<ComboBoxItem> defaultDiseaseComboBox;

    @FXML
    private ComboBox<ComboBoxItem> reportTemplateComboBox;

    @FXML
    private TextField warningReadDepthTextField;

    @FXML
    private TextField warningMAFTextField;

    @FXML
    private TextField lowConfidenceMinAlleleFractionTextField;

    @FXML
    private CheckBox warningReadDepthCheckBox;

    @FXML
    private CheckBox warningMAFCheckBox;

    @FXML
    private TextField indelMinAlleleFrequencyTextField;

    @FXML
    private TextField indelMinReadDepthTextField;

    @FXML
    private TextField snvMinReadDepthTextField;

    @FXML
    private TextField indelMinAlternateCountTextField;

    @FXML
    private TextField populationFrequencyTextField;

    @FXML
    private Button roiFileSelectionButton;

    @FXML
    private Button panelSaveButton;

    @FXML
    private TableView<PanelView> panelListTable;

    @FXML
    private TableColumn<PanelView, Integer> panelIdTableColumn;

    @FXML
    private TableColumn<PanelView, String> panelNameTableColumn;

    @FXML
    private TableColumn<PanelView, String> panelCodeTableColumn;

    @FXML
    private TableColumn<PanelView, String> panelTargetTableColumn;

    @FXML
    private TableColumn<PanelView, String> analysisTypeTableColumn;

    @FXML
    private TableColumn<PanelView, String> libraryTypeTableColumn;

    @FXML
    private TableColumn<PanelView, String> createdAtTableColumn;

    @FXML
    private TableColumn<PanelView, String> updatedAtTableColumn;

    @FXML
    private TableColumn<PanelView, String> deletedAtTableColumn;

    @FXML
    private TableColumn<PanelView, String> deletedTableColumn;

    @FXML
    private TableColumn<PanelView, Boolean> editPanelTableColumn;

    @FXML
    private TableColumn<PanelView, Boolean> virtualPanelColumn;

    @FXML
    private TextField essentialGenesTextField;

    @FXML
    private TextArea canonicalTranscriptTextArea;

    @FXML
    private Label titleLabel;

    @FXML
    private GridPane panelEditGridPane;

    @FXML
    private Pagination panelPagination;

    @FXML
    private TextField totalBasePairTextField;
    @FXML
    private TextField q30TrimmedBasePercentageTextField;
    @FXML
    private TextField mappedBasePercentageTextField;
    @FXML
    private TextField onTargetPercentageTextField;
    @FXML
    private TextField onTargetCoverageTextField;
    @FXML
    private TextField duplicatedReadsPercentageTextField;
    @FXML
    private TextField roiCoveragePercentageTextField;
    @FXML
    private ScrollPane panelContentsScrollPane;
    @FXML
    private TextField uniformity02PercentageTextField;

    @FXML
    private TextField mappingQuality60PercentageTextField;

    @FXML
    private Button saveTextFile;

    private CheckComboBox<ComboBoxItem> groupCheckComboBox = null;

    private CheckComboBox<ComboBoxItem> diseaseCheckComboBox = null;

    private CheckComboBox<String> lowConfidenceCheckComboBox = null;

    private CheckComboBox<ComboBoxItem> frequencyDBCheckComboBox = null;

    private File bedFile = null;

    private int panelId = 0;

    @Override
    public void show(Parent root) throws IOException {

        panelListTable.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            panelListTable.refresh();
            // close text box
            panelListTable.edit(-1, null);
        });

        apiService = APIService.getInstance();

        panelSaveButton.setDisable(true);

        createComboBox();

        panelIdTableColumn.setCellValueFactory(item -> new SimpleIntegerProperty(item.getValue().getId()).asObject());
        panelNameTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getName()));
        panelCodeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getCode()));
        panelTargetTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getTarget()));
        analysisTypeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getAnalysisType()));
        libraryTypeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getLibraryType()));
        deletedTableColumn.setCellValueFactory(item -> new SimpleStringProperty((item.getValue().getDeleted() == 0) ? "N" : "Y"));

        warningMAFTextField.setTextFormatter(returnFormatter());

        warningReadDepthTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[0-9]*")) warningReadDepthTextField.setText(oldValue);
        });

        warningMAFCheckBox.selectedProperty().addListener((observable, oldValue,  newValue) ->
            warningMAFTextField.setDisable(!newValue)
        );

        warningReadDepthCheckBox.selectedProperty().addListener((observable, oldValue,  newValue) ->
            warningReadDepthTextField.setDisable(!newValue)
        );

        pipelineComboBox.setConverter(new ComboBoxConverter());
        pipelineComboBox.getItems().add(new ComboBoxItem());
        for(PipelineCode pipelineCode : PipelineCode.values()) {
            pipelineComboBox.getItems().add(new ComboBoxItem(pipelineCode.getCode(),
                    pipelineCode.getDescription()));
        }
        pipelineComboBox.getSelectionModel().selectFirst();

        pipelineComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            defaultSampleSourceComboBox.getItems().removeAll(defaultSampleSourceComboBox.getItems());
            if(lowConfidenceCheckComboBox != null) {
                lowConfidenceCheckComboBox.getCheckModel().clearChecks();
                lowConfidenceCheckComboBox.getItems().removeAll(lowConfidenceCheckComboBox.getItems());
            }
            panelInfoLabel.setText("");
            if(newValue != null && StringUtils.isNotEmpty(newValue.getValue())) {

                PipelineCode pipelineCode = PipelineCode.getPipelineCode(newValue.getValue());
                if(pipelineCode != null) {
                    panelInfoLabel.setText("Analysis Type : " + pipelineCode.getAnalysisType() + ", Library Type : "
                            + pipelineCode.getLibraryType() + ", Analysis Target : " + pipelineCode.getAnalysisTarget());

                    if(pipelineCode.getCode().equals(PipelineCode.BRCA_ACCUTEST_DNA.getCode()) ||
                            pipelineCode.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_DNA.getCode())) {
                        setBRCADefault();
                    } else if(pipelineCode.getCode().equals(PipelineCode.HERED_ACCUTEST_DNA.getCode())) {
                        setHEREDDefault();
                    }

                    defaultSampleSourceComboBox.getItems().addAll(PipelineCode.getSampleSource(newValue.getValue()));
                    if(lowConfidenceCheckComboBox != null) {
                        lowConfidenceCheckComboBox.getItems().addAll(PipelineCode.getLowConfidences(newValue.getValue()));
                    }
                }
            }
        });

        createdAtTableColumn.setCellValueFactory(item -> new SimpleStringProperty(DateFormatUtils.format(
                item.getValue().getCreatedAt().toDate(), DATE_FORMAT)));
        updatedAtTableColumn.setCellValueFactory(item -> {
            if (item.getValue().getUpdatedAt() != null )
                return new SimpleStringProperty(DateFormatUtils.format(
                    item.getValue().getUpdatedAt().toDate(), DATE_FORMAT));
            else
                return new SimpleStringProperty("");
        });
        deletedAtTableColumn.setCellValueFactory(item -> {
            if (item.getValue().getDeletedAt() != null )
                return new SimpleStringProperty(DateFormatUtils.format(
                        item.getValue().getDeletedAt().toDate(), DATE_FORMAT));
            else
                return new SimpleStringProperty("");
        });

        editPanelTableColumn.setSortable(false);
        editPanelTableColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        editPanelTableColumn.setCellFactory(param -> new PanelModifyButton());

        virtualPanelColumn.setSortable(false);
        virtualPanelColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        virtualPanelColumn.setCellFactory(param -> new VirtualPanelButton());

        panelPagination.setPageFactory(pageIndex -> {
            setPanelList(pageIndex + 1);
            return new VBox();
        });

        setDisabledItem(true);
    }

    private void setHEREDDefault() {
        canonicalTranscriptTextArea.setText("");
        canonicalTranscriptTextArea.setDisable(true);

        warningMAFCheckBox.setSelected(false);
        warningMAFCheckBox.setDisable(true);

        essentialGenesTextField.setText("");
        essentialGenesTextField.setDisable(true);

        indelMinAlleleFrequencyTextField.setText("");
        indelMinAlleleFrequencyTextField.setDisable(true);
        indelMinReadDepthTextField.setText("");
        indelMinReadDepthTextField.setDisable(true);
        indelMinAlternateCountTextField.setText("");
        indelMinAlternateCountTextField.setDisable(true);
        snvMinReadDepthTextField.setText("");
        snvMinReadDepthTextField.setDisable(true);

        populationFrequencyTextField.setText("");
        populationFrequencyTextField.setDisable(true);

        frequencyDBCheckComboBox.getCheckModel().clearChecks();
        frequencyDBCheckComboBox.setDisable(true);

        roiCoveragePercentageTextField.setText("");
        roiCoveragePercentageTextField.setDisable(true);
    }

    private void setBRCADefault() {
        canonicalTranscriptTextArea.setText("");
        canonicalTranscriptTextArea.setDisable(true);

        warningReadDepthCheckBox.setSelected(false);
        warningReadDepthCheckBox.setDisable(true);
        warningReadDepthTextField.setText("");
        warningReadDepthTextField.setDisable(true);
        warningMAFCheckBox.setSelected(false);
        warningMAFCheckBox.setDisable(true);
        warningMAFTextField.setText("");
        warningMAFTextField.setDisable(true);

        essentialGenesTextField.setText("");
        essentialGenesTextField.setDisable(true);

        indelMinAlleleFrequencyTextField.setText("");
        indelMinAlleleFrequencyTextField.setDisable(true);
        indelMinReadDepthTextField.setText("");
        indelMinReadDepthTextField.setDisable(true);
        indelMinAlternateCountTextField.setText("");
        indelMinAlternateCountTextField.setDisable(true);
        snvMinReadDepthTextField.setText("");
        snvMinReadDepthTextField.setDisable(true);

        lowConfidenceCheckComboBox.getCheckModel().clearChecks();
        lowConfidenceCheckComboBox.setDisable(true);

        populationFrequencyTextField.setText("");
        populationFrequencyTextField.setDisable(true);

        frequencyDBCheckComboBox.getCheckModel().clearChecks();
        frequencyDBCheckComboBox.setDisable(true);

        totalBasePairTextField.setText("");
        totalBasePairTextField.setDisable(true);
        q30TrimmedBasePercentageTextField.setText("");
        q30TrimmedBasePercentageTextField.setDisable(true);
        mappedBasePercentageTextField.setText("");
        mappedBasePercentageTextField.setDisable(true);
        onTargetCoverageTextField.setText("");
        onTargetCoverageTextField.setDisable(true);
        onTargetPercentageTextField.setText("");
        onTargetPercentageTextField.setDisable(true);
        duplicatedReadsPercentageTextField.setText("");
        duplicatedReadsPercentageTextField.setDisable(true);
        roiCoveragePercentageTextField.setText("");
        roiCoveragePercentageTextField.setDisable(true);
        uniformity02PercentageTextField.setText("");
        uniformity02PercentageTextField.setDisable(true);
        mappingQuality60PercentageTextField.setText("");
        mappingQuality60PercentageTextField.setDisable(true);

    }

    private TextFormatter returnFormatter() {
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        return new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
                pattern.matcher(change.getControlNewText()).matches() ? change : null);
    }

    void setPanelList(int page) {

        int totalCount = 0;
        int limit = 11;
        int offset = (page - 1)  * limit;

        HttpClientResponse response = null;

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("limit", limit);
            param.put("offset", offset);

            response = apiService.get("/admin/panels", param, null, false);

            if(response != null) {
                PagedPanelView pagedPanel =
                        response.getObjectBeforeConvertResponseToJSON(PagedPanelView.class);
                List<PanelView> list = null;

                if(pagedPanel != null) {
                    totalCount = pagedPanel.getCount();
                    list = pagedPanel.getResult();
                }

                int pageCount = 0;

                if(totalCount > 0) {
                    pageCount = totalCount / limit;
                    panelPagination.setCurrentPageIndex(page - 1);
                    if(totalCount % limit > 0) {
                        pageCount++;
                    }
                }

                panelListTable.setItems((FXCollections.observableList(list)));

                logger.debug(String.format("total count : %s, page count : %s", totalCount, pageCount));

                if (pageCount > 0) {
                    panelPagination.setVisible(true);
                    panelPagination.setPageCount(pageCount);
                } else {
                    panelPagination.setVisible(false);
                }
            }

        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    private void createComboBoxItem() {
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("g1000.all", "1KGP All"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("g1000.african", "1KGP African"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("g1000.american", "1KGP American"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("g1000.eastAsian", "1KGP East Asian"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("g1000.european", "1KGP European"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("g1000.southAsian", "1KGP South Asian"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("esp6500.all", "ESP All"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("esp6500.aa", "ESP African American"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("esp6500.ea", "ESP European American"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("gnomAD.all", "gnomAD All"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("gnomAD.admixedAmerican", "gnomAD Admixed American"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("gnomAD.africanAfricanAmerican", "gnomAD African African American"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("gnomAD.eastAsian", "gnomAD East Asian"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("gnomAD.finnish", "gnomAD Finnish"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("gnomAD.nonFinnishEuropean", "gnomAD Non Finnish European"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("gnomAD.others", "gnomAD Others"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("gnomAD.southAsian", "gnomAD South Asian"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("koreanExomInformationDatabase", "KoEXID"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("koreanReferenceGenomeDatabase", "KRGDB"));
        frequencyDBCheckComboBox.getItems().add(new ComboBoxItem("exac", "ExAC"));
        /*lowConfidenceCheckComboBox.getItems().addAll("artifact_in_normal", "base_quality", "clustered_events",
                "contamination", "duplicate_evidence", "fragment_length", "germline_risk", "mapping_quality",
                "multiallelic", "orientation_bias", "panel_of_normals", "read_position", "str_contraction",
                "strand_artifact", "t_lod", "homopolymer", "repeat_sequence", "sequencing_error", "mapping_error",
                "snp_candidate");*/
    }

    private void createComboBox() {
        final CheckComboBox<ComboBoxItem> groupCheckComboBox = new CheckComboBox<>();
        groupCheckComboBox.setConverter(new ComboBoxConverter());

        groupCheckComboBox.setPrefWidth(150);
        groupCheckComboBox.setMaxWidth(Double.MAX_VALUE);

        this.groupCheckComboBox = groupCheckComboBox;

        basicInformationGridPane.add(groupCheckComboBox, 1, 2);
        groupCheckComboBox.setMaxWidth(Double.MAX_VALUE);
        final CheckComboBox<ComboBoxItem> diseaseCheckComboBox = new CheckComboBox<>();
        diseaseCheckComboBox.setConverter(new ComboBoxConverter());

        diseaseCheckComboBox.setPrefWidth(150);
        diseaseCheckComboBox.setMaxWidth(Double.MAX_VALUE);

        this.diseaseCheckComboBox = diseaseCheckComboBox;

        basicInformationGridPane.add(diseaseCheckComboBox, 1, 3);

        final CheckComboBox<String> lowConfidenceCheckComboBox = new CheckComboBox<>();

        lowConfidenceCheckComboBox.setPrefWidth(145);
        lowConfidenceCheckComboBox.setMaxWidth(Double.MAX_VALUE);

        this.lowConfidenceCheckComboBox = lowConfidenceCheckComboBox;

        panelEditGridPane.add(lowConfidenceCheckComboBox, 1, 0);

        final CheckComboBox<ComboBoxItem> frequencyDBCheckComboBox = new CheckComboBox<>();
        frequencyDBCheckComboBox.setConverter(new ComboBoxConverter());
        frequencyDBCheckComboBox.setPrefWidth(150);
        frequencyDBCheckComboBox.setMaxWidth(Double.MAX_VALUE);

        this.frequencyDBCheckComboBox = frequencyDBCheckComboBox;

        snpPopulationFrequencyFilterGridPane.add(frequencyDBCheckComboBox, 1, 0);

        createComboBoxItem();
    }

    @SuppressWarnings("unchecked")
    private void setPanelAndDisease() {
        HttpClientResponse response;

        groupCheckComboBox.getItems().removeAll(groupCheckComboBox.getItems());
        diseaseCheckComboBox.getItems().removeAll(diseaseCheckComboBox.getItems());

        try {
            response = apiService.get("/admin/memberGroups", null, null, false);

            if(response != null) {
                SystemManagerUserGroupPaging systemManagerUserGroupPaging =
                        response.getObjectBeforeConvertResponseToJSON(SystemManagerUserGroupPaging.class);
                List<UserGroup> groupList = systemManagerUserGroupPaging.getList();

                List<ComboBoxItem> items = new ArrayList<>();

                for(UserGroup userGroup :groupList) {
                    items.add(new ComboBoxItem(userGroup.getId().toString(), userGroup.getName()));
                }

                groupCheckComboBox.getItems().addAll(items);
            }

            response = apiService.get("/diseases", null, null, false);
            List<Diseases> diseasesList = (List<Diseases>)response.getMultiObjectBeforeConvertResponseToJSON(Diseases.class, false);

            if(diseasesList != null) {
                List<ComboBoxItem> items = new ArrayList<>();

                for(Diseases disease : diseasesList) {
                    items.add(new ComboBoxItem(disease.getId().toString(), disease.getName()));
                }

                diseaseCheckComboBox.getItems().addAll(items);

                diseaseCheckComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<ComboBoxItem>() {
                    @Override
                    public void onChanged(Change<? extends ComboBoxItem> c) {
                        List<ComboBoxItem> selectedItem = diseaseCheckComboBox.getCheckModel().getCheckedItems().stream().collect(Collectors.toList());
                        if(defaultDiseaseComboBox.getItems().isEmpty()) {
                            defaultDiseaseComboBox.getItems().addAll(selectedItem);
                        } else {
                            for(ComboBoxItem comboBoxItem : selectedItem) {
                                Optional<ComboBoxItem> combo  = defaultDiseaseComboBox.getItems().stream()
                                        .filter(item -> item.getValue().equalsIgnoreCase(comboBoxItem.getValue()))
                                        .findFirst();
                                if(!combo.isPresent()) {
                                    defaultDiseaseComboBox.getItems().add(comboBoxItem);
                                }
                            }
                            List<ComboBoxItem> removeList = new ArrayList<>();

                            for(ComboBoxItem comboBoxItem : defaultDiseaseComboBox.getItems()) {
                                Optional<ComboBoxItem> combo  = selectedItem.stream()
                                        .filter(item -> item.getValue().equalsIgnoreCase(comboBoxItem.getValue()))
                                        .findFirst();
                                if(!combo.isPresent()) {
                                    removeList.add(comboBoxItem);
                                }
                            }
                            defaultDiseaseComboBox.getItems().removeAll(removeList);
                        }
                    }
                });

            }

        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }

    }

    private void reportTemplateComboBoxSetting() {

        reportTemplateComboBox.getItems().clear();

        try {
            HttpClientResponse response = apiService.get("admin/reportTemplate", null, null, false);

            PagedReportTemplate pagedReportTemplate = response.getObjectBeforeConvertResponseToJSON(PagedReportTemplate.class);

            if(pagedReportTemplate != null &&pagedReportTemplate.getCount() != 0) {
                List<ReportTemplate> reportTemplates = pagedReportTemplate.getResult();
                reportTemplateComboBox.setConverter(new ComboBoxConverter());
                reportTemplateComboBox.getItems().add(new ComboBoxItem());
                for(ReportTemplate reportTemplate : reportTemplates) {
                    if(reportTemplate.getDeleted() == 0)
                        reportTemplateComboBox.getItems().add(new ComboBoxItem(reportTemplate.getId().toString(),
                                reportTemplate.getName()));
                }

            }

        } catch (WebAPIException wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getMessage(), mainController.getPrimaryStage(), true);
        }
    }

    private VariantFilter setVariantFilter() {
        VariantFilter variantFilter = new VariantFilter();

        try {
            BigDecimal indelMinAlleleFrequency = new BigDecimal(indelMinAlleleFrequencyTextField.getText());
            variantFilter.setInDelMinAlleleFraction(indelMinAlleleFrequency);
        } catch (Exception e) { }
        try {
            Integer indelMinReadDepth = Integer.parseInt(indelMinReadDepthTextField.getText());
            variantFilter.setInDelMinReadDepth(indelMinReadDepth);
        } catch (Exception e) { }
        try {
            Integer indelMinAlternateCount = Integer.parseInt(indelMinAlternateCountTextField.getText());
            variantFilter.setInDelMinAlternateCount(indelMinAlternateCount);
        } catch (Exception e) { }
        try {
            Integer snvMinAlternateCount = Integer.parseInt(snvMinReadDepthTextField.getText());
            variantFilter.setSnvMinReadDepth(snvMinAlternateCount);
        } catch (Exception e) { }

        if(!frequencyDBCheckComboBox.getCheckModel().getCheckedItems().isEmpty()) {
            final StringBuilder value = new StringBuilder();
            frequencyDBCheckComboBox.getCheckModel().getCheckedItems().forEach(item -> value.append(item.getValue() + ","));
            value.deleteCharAt(value.length() - 1);
            variantFilter.setPopulationFrequencyDBs(value.toString());
        }
        try {
            BigDecimal reportCutOffPopulationFrequency = new BigDecimal(populationFrequencyTextField.getText());
            variantFilter.setPopulationFrequency(reportCutOffPopulationFrequency);
        } catch (Exception e) { }
        try {
            BigDecimal lowConfidenceMinAlleleFraction = new BigDecimal(lowConfidenceMinAlleleFractionTextField.getText());
            variantFilter.setLowConfidenceMinAlleleFraction(lowConfidenceMinAlleleFraction);
        } catch (Exception e) { }

        variantFilter.setEssentialGenes(essentialGenesTextField.getText());

        if(!lowConfidenceCheckComboBox.getCheckModel().getCheckedItems().isEmpty()) {
            final StringBuilder value = new StringBuilder();
            lowConfidenceCheckComboBox.getCheckModel().getCheckedItems().forEach(item -> value.append(item + ","));
            value.deleteCharAt(value.length() - 1);
            variantFilter.setLowConfidenceFilter(value.toString());
        }

        return variantFilter;
    }

    private QCPassConfig setQCPassingConfig() {
        QCPassConfig qcPassConfig = new QCPassConfig();

        try {
            Integer totalBasePair = new Integer(totalBasePairTextField.getText());
            qcPassConfig.setTotalBasePair(totalBasePair);
        } catch (Exception e) { }
        try {
            Double q30 = new Double(q30TrimmedBasePercentageTextField.getText());
            qcPassConfig.setQ30TrimmedBasePercentage(q30);
        } catch (Exception e) { }
        try {
            Double mappedBase = new Double(mappedBasePercentageTextField.getText());
            qcPassConfig.setMappedBasePercentage(mappedBase);
        } catch (Exception e) { }
        try {
            Double onTarget = new Double(onTargetPercentageTextField.getText());
            qcPassConfig.setOnTargetPercentage(onTarget);
        } catch (Exception e) { }
        try {
            Double onTargetCoverage = new Double(onTargetCoverageTextField.getText());
            qcPassConfig.setOnTargetCoverage(onTargetCoverage);
        } catch (Exception e) { }
        try {
            Double duplicated = new Double(duplicatedReadsPercentageTextField.getText());
            qcPassConfig.setDuplicatedReadsPercentage(duplicated);
        } catch (Exception e) { }
        try {
            Double roiCoverage = new Double(roiCoveragePercentageTextField.getText());
            qcPassConfig.setRoiCoveragePercentage(roiCoverage);
        } catch (Exception e) { }
        try {
            Double uniformity = new Double(uniformity02PercentageTextField.getText());
            qcPassConfig.setUniformity02Percentage(uniformity);
        } catch (Exception e) { }
        try {
            Double mappingQuality = new Double(mappingQuality60PercentageTextField.getText());
            qcPassConfig.setMappingQuality60Percentage(mappingQuality);
        } catch (Exception e) { }

        return qcPassConfig;
    }


    @FXML
    public void showSaveTextFile() {
        if(panelId == 0) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("save Panel Information to txt file");
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Text File(*.txt)", "*.txt"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName(panelNameTextField.getText() + ".txt");
        File file = fileChooser.showSaveDialog(mainController.getPrimaryStage());

        if(file != null) {
            String panelName = panelNameTextField.getText();
            if(pipelineComboBox.getSelectionModel().getSelectedItem() == null) return;
            String code = pipelineComboBox.getSelectionModel().getSelectedItem().getValue();

            Map<String,Object> params = new HashMap<>();
            params.put("name", panelName);
            params.put("code", code);

            PipelineCode pipelineCode = PipelineCode.getPipelineCode(code);

            if(pipelineCode != null) {
                params.put("target", pipelineCode.getAnalysisTarget());
                params.put("analysisType", pipelineCode.getAnalysisType());
                params.put("libraryType", pipelineCode.getLibraryType());
            }

            if(warningReadDepthCheckBox.isSelected() && StringUtils.isNotEmpty(warningReadDepthTextField.getText())) {
                params.put("warningReadDepth", Integer.parseInt(warningReadDepthTextField.getText()));
            }
            if(warningMAFCheckBox.isSelected() && StringUtils.isNotEmpty(warningMAFTextField.getText())) {
                params.put("warningMAF", new BigDecimal(warningMAFTextField.getText()));
            }

            VariantFilter variantFilter = setVariantFilter();

            if(variantFilter.getInDelMinReadDepth() != null && variantFilter.getInDelMinReadDepth() < 20) {
                DialogUtil.warning("value error", "set min read depth >= 20", mainApp.getPrimaryStage(), true);
                return;
            }
            if(variantFilter.getInDelMinAlternateCount() != null && variantFilter.getInDelMinAlternateCount() < 6) {
                DialogUtil.warning("value error", "set min alternate count >= 6", mainApp.getPrimaryStage(), true);
                return;
            }

            params.put("canonicalTranscripts", canonicalTranscriptTextArea.getText());
            params.put("qcPassConfig", setQCPassingConfig());
            if(defaultDiseaseComboBox.getSelectionModel().getSelectedItem() != null) {
                params.put("defaultDiseaseId", defaultDiseaseComboBox.getSelectionModel().getSelectedItem().getText());
            }
            SampleSourceCode item = defaultSampleSourceComboBox.getSelectionModel().getSelectedItem();
            params.put("defaultSampleSource", (item != null) ? item.getDescription() : "");
            params.put("variantFilter", variantFilter);

            String reportId = null;
            if(!reportTemplateComboBox.getSelectionModel().isEmpty() &&
                    reportTemplateComboBox.getSelectionModel().getSelectedItem() != null) {
                reportId = reportTemplateComboBox.getSelectionModel().getSelectedItem().getText();
            }

            if(!StringUtils.isEmpty(reportId)) {
                params.put("reportTemplate", reportId);
            }

            StringBuilder groupString = new StringBuilder();
            ObservableList<ComboBoxItem> checkedGroupList =  groupCheckComboBox.getCheckModel().getCheckedItems();
            for(ComboBoxItem  group : checkedGroupList) {
                groupString.append(group.getText() + " ");
            }

            //panel에서 선택할 수 있는 질병을 지정함
            StringBuilder diseaseList = new StringBuilder();
            ObservableList<ComboBoxItem> checkedDiseaseList =  diseaseCheckComboBox.getCheckModel().getCheckedItems();
            for(ComboBoxItem  disease : checkedDiseaseList) {
                diseaseList.append(disease.getText() + " ");
            }

            params.put("memberGroupIds", groupString.toString());
            params.put("diseaseIds", diseaseList.toString());

            PanelTextFileSaveService panelTextFileSaveService = PanelTextFileSaveService.getInstance();
            panelTextFileSaveService.saveFile(file, params);
        }
    }

    @FXML
    public void savePanel() {
        String panelName = panelNameTextField.getText();
        if(StringUtils.isNotEmpty(panelName) && !panelName.matches("[a-zA-Z0-9_\\s]*")) {
            DialogUtil.warning("Incorrect panel name combination",
                    "Please enter at combination of English, whitespace, numbers and underscore.", mainApp.getPrimaryStage(), true);
            panelContentsScrollPane.setVvalue(0);
            panelNameTextField.setText("");
            panelNameTextField.requestFocus();
            return;
        }
        String code = pipelineComboBox.getSelectionModel().getSelectedItem().getValue();

        if(StringUtils.isNotEmpty(code)) {
            if(bedFile == null && panelId == 0) return;

            if(defaultSampleSourceComboBox.getSelectionModel().getSelectedItem() == null) {
                defaultSampleSourceComboBox.requestFocus();
                return;
            }

            Map<String,Object> params = new HashMap<>();
            params.put("name", panelName);
            params.put("code", code);

            PipelineCode pipelineCode = PipelineCode.getPipelineCode(code);

            if(pipelineCode != null) {
                params.put("target", pipelineCode.getAnalysisTarget());
                params.put("analysisType", pipelineCode.getAnalysisType());
                params.put("libraryType", pipelineCode.getLibraryType());
            }

            if(warningReadDepthCheckBox.isSelected() && StringUtils.isNotEmpty(warningReadDepthTextField.getText())) {
                params.put("warningReadDepth", Integer.parseInt(warningReadDepthTextField.getText()));
            }
            if(warningMAFCheckBox.isSelected() && StringUtils.isNotEmpty(warningMAFTextField.getText())) {
                params.put("warningMAF", new BigDecimal(warningMAFTextField.getText()));
            }

            VariantFilter variantFilter = setVariantFilter();

            if(variantFilter.getInDelMinReadDepth() != null && variantFilter.getInDelMinReadDepth() < 20) {
                DialogUtil.warning("value error", "set min read depth >= 20", mainApp.getPrimaryStage(), true);
                return;
            }
            if(variantFilter.getInDelMinAlternateCount() != null && variantFilter.getInDelMinAlternateCount() < 6) {
                DialogUtil.warning("value error", "set min alternate count >= 6", mainApp.getPrimaryStage(), true);
                return;
            }

            params.put("canonicalTranscripts", canonicalTranscriptTextArea.getText());
            params.put("qcPassConfig", setQCPassingConfig());
            if(defaultDiseaseComboBox.getSelectionModel().getSelectedItem() != null) {
                params.put("defaultDiseaseId", Integer.parseInt(defaultDiseaseComboBox.getSelectionModel().getSelectedItem().getValue()));
            }

            params.put("defaultSampleSource", defaultSampleSourceComboBox.getSelectionModel().getSelectedItem().getDescription());
            params.put("variantFilter", variantFilter);

            String reportId = null;
            if(!reportTemplateComboBox.getSelectionModel().isEmpty()) {
                reportId = reportTemplateComboBox.getSelectionModel().getSelectedItem().getValue();
            }

            if(!StringUtils.isEmpty(reportId)) {
                params.put("reportTemplateId", Integer.parseInt(reportId));
            }


            HttpClientResponse response = null;
            try {
                //panel을 사용할 수 있는 Group을 지정함
                List<Integer> groupIdList = new ArrayList<>();
                ObservableList<ComboBoxItem> checkedGroupList =  groupCheckComboBox.getCheckModel().getCheckedItems();
                for(ComboBoxItem  group : checkedGroupList) {
                    groupIdList.add(Integer.parseInt(group.getValue()));
                }

                //panel에서 선택할 수 있는 질병을 지정함
                List<Integer> diseaseIdList = new ArrayList<>();
                ObservableList<ComboBoxItem> checkedDiseaseList =  diseaseCheckComboBox.getCheckModel().getCheckedItems();
                for(ComboBoxItem  disease : checkedDiseaseList) {
                    diseaseIdList.add(Integer.parseInt(disease.getValue()));
                }

                if(groupIdList.isEmpty() || diseaseIdList.isEmpty()) {
                    return;
                }

                Panel panel = null;

                //패널 생성
                if(panelId == 0) {
                    //패널 생성
                    response = apiService.post("admin/panels", params, null, true);
                    panel = response.getObjectBeforeConvertResponseToJSON(Panel.class);

                    //패널 생성 후
                    params.put("memberGroupIds", groupIdList);
                    params.put("diseaseIds", diseaseIdList);
                    response = apiService.put("admin/panels/" + panel.getId(), params, null, true);
                    panel = response.getObjectBeforeConvertResponseToJSON(Panel.class);
                } else { //패널 수정
                    params.put("memberGroupIds", groupIdList);
                    params.put("diseaseIds", diseaseIdList);
                    response = apiService.put("admin/panels/" + panelId, params, null, true);
                    panel = response.getObjectBeforeConvertResponseToJSON(Panel.class);
                    panelId = 0; //패널을 수정했으므로 초기화
                }

                Task task = new BedFileUploadTask(panel.getId(), bedFile);

                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();

                task.setOnSucceeded(ev -> {
                    try {
                        setPanelList(1);
                        setDisabledItem(true);
                        panelSaveButton.setDisable(true);
                    } catch (Exception e) {
                        logger.error("panel list refresh fail.", e);
                    }

                });

            } catch (WebAPIException wae) {
                wae.printStackTrace();
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
            } catch (IOException ioe) {
                DialogUtil.error(ioe.getMessage(), ioe.getMessage(), mainController.getPrimaryStage(), true);
            }
        }
    }

    private void resetItem() {
        reportTemplateComboBoxSetting();
        setPanelAndDisease();
        panelNameTextField.setText("");
        //panelCodeTextField.setText("");
        if(pipelineComboBox.getSelectionModel().getSelectedItem() != null) {
            pipelineComboBox.getSelectionModel().clearSelection();
        }
        warningMAFTextField.setText("");
        warningReadDepthTextField.setText("");
        if(defaultSampleSourceComboBox.getSelectionModel().getSelectedItem() != null) {
            defaultSampleSourceComboBox.getSelectionModel().clearSelection();
        }
        bedFile = null;
        panelSaveButton.setDisable(true);
        groupCheckComboBox.getCheckModel().clearChecks();
        diseaseCheckComboBox.getCheckModel().clearChecks();
        defaultDiseaseComboBox.getSelectionModel().clearSelection();
        frequencyDBCheckComboBox.getCheckModel().clearChecks();
        lowConfidenceCheckComboBox.getCheckModel().clearChecks();
        defaultDiseaseComboBox.getItems().removeAll(defaultDiseaseComboBox.getItems());
        reportTemplateComboBox.getSelectionModel().selectFirst();
        warningReadDepthCheckBox.setSelected(false);
        warningMAFCheckBox.setSelected(false);
        indelMinAlleleFrequencyTextField.setText("");
        indelMinReadDepthTextField.setText("");
        snvMinReadDepthTextField.setText("");
        indelMinAlternateCountTextField.setText("");
        populationFrequencyTextField.setText("");
        essentialGenesTextField.setText("");
        canonicalTranscriptTextArea.setText("");
        totalBasePairTextField.setText("");
        q30TrimmedBasePercentageTextField.setText("");
        mappedBasePercentageTextField.setText("");
        onTargetPercentageTextField.setText("");
        onTargetCoverageTextField.setText("");
        lowConfidenceMinAlleleFractionTextField.setText("");
        duplicatedReadsPercentageTextField.setText("");
        roiCoveragePercentageTextField.setText("");
        mappingQuality60PercentageTextField.setText("");
        uniformity02PercentageTextField.setText("");
    }

    void setDisabledItem(boolean condition) {
        resetItem();
        saveTextFile.setDisable(condition);
        warningReadDepthTextField.setDisable(condition);
        warningMAFTextField.setDisable(condition);
        warningReadDepthCheckBox.setDisable(condition);
        warningMAFCheckBox.setDisable(condition);
        panelNameTextField.setDisable(condition);
        roiFileSelectionButton.setDisable(condition);
        groupCheckComboBox.setDisable(condition);
        diseaseCheckComboBox.setDisable(condition);
        reportTemplateComboBox.setDisable(condition);
        indelMinAlleleFrequencyTextField.setDisable(condition);
        indelMinReadDepthTextField.setDisable(condition);
        lowConfidenceMinAlleleFractionTextField.setDisable(condition);
        snvMinReadDepthTextField.setDisable(condition);
        indelMinAlternateCountTextField.setDisable(condition);
        frequencyDBCheckComboBox.setDisable(condition);
        lowConfidenceCheckComboBox.setDisable(condition);
        populationFrequencyTextField.setDisable(condition);
        essentialGenesTextField.setDisable(condition);
        pipelineComboBox.setDisable(condition);
        canonicalTranscriptTextArea.setDisable(condition);
        totalBasePairTextField.setDisable(condition);
        q30TrimmedBasePercentageTextField.setDisable(condition);
        mappedBasePercentageTextField.setDisable(condition);
        onTargetPercentageTextField.setDisable(condition);
        onTargetCoverageTextField.setDisable(condition);
        duplicatedReadsPercentageTextField.setDisable(condition);
        roiCoveragePercentageTextField.setDisable(condition);
        defaultDiseaseComboBox.setDisable(condition);
        defaultSampleSourceComboBox.setDisable(condition);
        mappingQuality60PercentageTextField.setDisable(condition);
        uniformity02PercentageTextField.setDisable(condition);
    }

    public void deletePanel(Integer panelId) {
        try {
            apiService.delete("admin/panels/" + panelId);

            HttpClientResponse response = apiService.get("admin/panels", null, null, false);
            final PagedPanel tablePanels = response.getObjectBeforeConvertResponseToJSON(PagedPanel.class);
            mainController.getBasicInformationMap().put("panels", tablePanels.getResult());

            //panelListTable.getItems().removeAll(panelListTable.getItems());
            //panelListTable.getItems().addAll(tablePanels.getResult());

            setPanelList(1);

        } catch (WebAPIException wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
        }
    }

    @FXML
    public void panelAdd() {
        titleLabel.setText("Panel Add");
        panelId = 0;
        setDisabledItem(false);
    }

    @FXML
    public void selectROIFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose ROI File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(mainController.getPrimaryStage());

        if(file != null) {
            this.bedFile = file;
            panelSaveButton.setDisable(false);
        }
    }

    private void selectPipelineComboBox(String code) {
        Optional<ComboBoxItem> comboBoxItem = pipelineComboBox.getItems().stream()
                .filter(item -> item.getValue().equals(code)).findFirst();

        comboBoxItem.ifPresent(item -> pipelineComboBox.getSelectionModel().select(item));
    }

    private class PanelModifyButton extends TableCell<PanelView, Boolean> {
        HBox box = null;
        final ImageView img1 = new ImageView(resourceUtil.getImage("/layout/images/modify.png", 18, 18));
        final ImageView img2 = new ImageView(resourceUtil.getImage("/layout/images/delete.png", 18, 18));

        public PanelModifyButton() {

            img1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Panel panel = PanelModifyButton.this.getTableView().getItems().get(
                        PanelModifyButton.this.getIndex());

                PanelView panelDetail = null;
                HttpClientResponse response = null;
                try {
                    response = apiService.get("admin/panels/" + panel.getId(), null, null, false);
                    panelDetail = response.getObjectBeforeConvertResponseToJSON(PanelView.class);
                } catch (WebAPIException wae) {
                    wae.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                titleLabel.setText("Panel Update");

                setDisabledItem(false);

                panelId = panel.getId();

                selectPipelineComboBox(panel.getCode());

                panelNameTextField.setText(panel.getName());
                //panelCodeTextField.setText(panel.getCode());
                if(panel.getWarningMAF() != null) {
                    warningMAFCheckBox.setSelected(true);
                    warningMAFTextField.setDisable(false);
                    warningMAFTextField.setText(panel.getWarningMAF().toString());
                }
                if(panel.getWarningReadDepth() != null) {
                    warningReadDepthCheckBox.setSelected(true);
                    warningReadDepthTextField.setDisable(false);
                    warningReadDepthTextField.setText(panel.getWarningReadDepth().toString());
                }

                VariantFilter params = panel.getVariantFilter();
                if(panel.getVariantFilter().getInDelMinAlleleFraction() != null) indelMinAlleleFrequencyTextField.setText(params.getInDelMinAlleleFraction().toString());
                if(panel.getVariantFilter().getInDelMinReadDepth() != null) indelMinReadDepthTextField.setText(params.getInDelMinReadDepth().toString());
                if(panel.getVariantFilter().getInDelMinAlternateCount() != null) indelMinAlternateCountTextField.setText(params.getInDelMinAlternateCount().toString());
                if(panel.getVariantFilter().getSnvMinReadDepth() != null) snvMinReadDepthTextField.setText(params.getSnvMinReadDepth().toString());
                if(panel.getVariantFilter().getLowConfidenceMinAlleleFraction() != null) lowConfidenceMinAlleleFractionTextField.setText(params.getLowConfidenceMinAlleleFraction().toString());
                if(panel.getVariantFilter().getPopulationFrequencyDBs() != null) {
                    String[] freqDBs = panel.getVariantFilter().getPopulationFrequencyDBs().split(",");
                    for(String freqDB : freqDBs) {
                        //frequencyDBCheckComboBox.getCheckModel().check(freqDB);
                        Optional<ComboBoxItem> comboBoxItem = frequencyDBCheckComboBox.getItems().stream().filter(item
                                -> item.getValue().equalsIgnoreCase(freqDB)).findFirst();
                        comboBoxItem.ifPresent(item -> frequencyDBCheckComboBox.getCheckModel().check(item));
                    }
                }
                if(panel.getVariantFilter().getLowConfidenceFilter() != null) {
                    String[] lowConfidences = panel.getVariantFilter().getLowConfidenceFilter().split(",");
                    for(String lowConfidence : lowConfidences) {
                        lowConfidenceCheckComboBox.getCheckModel().check(lowConfidence);
                    }
                }
                if(panel.getVariantFilter().getPopulationFrequency() != null) populationFrequencyTextField.setText(params.getPopulationFrequency().toString());

                if(panel.getQcPassConfig() != null) {
                    if(panel.getQcPassConfig().getTotalBasePair() != null) totalBasePairTextField.setText(panel.getQcPassConfig().getTotalBasePair().toString());
                    if(panel.getQcPassConfig().getDuplicatedReadsPercentage() != null) duplicatedReadsPercentageTextField.setText(panel.getQcPassConfig().getDuplicatedReadsPercentage().toString());
                    if(panel.getQcPassConfig().getMappedBasePercentage() != null) mappedBasePercentageTextField.setText(panel.getQcPassConfig().getMappedBasePercentage().toString());
                    if(panel.getQcPassConfig().getOnTargetCoverage() != null) onTargetCoverageTextField.setText(panel.getQcPassConfig().getOnTargetCoverage().toString());
                    if(panel.getQcPassConfig().getOnTargetPercentage() != null) onTargetPercentageTextField.setText(panel.getQcPassConfig().getOnTargetPercentage().toString());
                    if(panel.getQcPassConfig().getQ30TrimmedBasePercentage() != null) q30TrimmedBasePercentageTextField.setText(panel.getQcPassConfig().getQ30TrimmedBasePercentage().toString());
                    if(panel.getQcPassConfig().getRoiCoveragePercentage() != null) roiCoveragePercentageTextField.setText(panel.getQcPassConfig().getRoiCoveragePercentage().toString());
                    if(panel.getQcPassConfig().getUniformity02Percentage() != null) uniformity02PercentageTextField.setText(panel.getQcPassConfig().getUniformity02Percentage().toString());
                    if(panel.getQcPassConfig().getMappingQuality60Percentage() != null) mappingQuality60PercentageTextField.setText(panel.getQcPassConfig().getMappingQuality60Percentage().toString());
                }

                if(panel.getVariantFilter().getEssentialGenes() != null) essentialGenesTextField.setText(panel.getVariantFilter().getEssentialGenes());
                if(panel.getCanonicalTranscripts() != null) canonicalTranscriptTextArea.setText(panel.getCanonicalTranscripts());

                Optional<SampleSourceCode> defaultSampleSourceItem = defaultSampleSourceComboBox.getItems().stream().filter(item -> item.getDescription().equalsIgnoreCase(panel.getDefaultSampleSource())).findFirst();
                defaultSampleSourceItem.ifPresent(comboBoxItem -> defaultSampleSourceComboBox.getSelectionModel().select(comboBoxItem));

                if(panel.getReportTemplateId() != null) {
                    Optional<ComboBoxItem> reportTemplate = reportTemplateComboBox.getItems().stream().filter(item -> item.getValue().equalsIgnoreCase(panel.getReportTemplateId().toString())).findFirst();
                    reportTemplate.ifPresent(comboBoxItem -> reportTemplateComboBox.getSelectionModel().select(comboBoxItem));
                }

                if(StringUtils.isEmpty(panel.getDefaultSampleSource())) {
                    Optional<SampleSourceCode> sampleSource =
                                defaultSampleSourceComboBox.getItems().stream().filter(item -> item.getDescription().equalsIgnoreCase(panel.getDefaultSampleSource()))
                                .findFirst();
                    sampleSource.ifPresent(comboBoxItem -> defaultSampleSourceComboBox.getSelectionModel().select(comboBoxItem));
                }

                if(panelDetail != null) {
                    List<Integer> groupIds = panelDetail.getMemberGroupIds();
                    for (int i = 0; i < groupCheckComboBox.getItems().size(); i++) {
                        ComboBoxItem item = groupCheckComboBox.getCheckModel().getItem(i);
                        if (groupIds != null && !groupIds.isEmpty() &&
                                groupIds.stream().filter(id -> id.equals(Integer.parseInt(item.getValue()))).findFirst().isPresent())
                            groupCheckComboBox.getCheckModel().check(item);
                    }

                    List<Integer> diseaseIds = panelDetail.getDiseaseIds();
                    for (int i = 0; i < diseaseCheckComboBox.getItems().size(); i++) {
                        ComboBoxItem item = diseaseCheckComboBox.getCheckModel().getItem(i);
                        if (diseaseIds != null && !diseaseIds.isEmpty() &&
                                diseaseIds.stream().filter(id -> id.equals(Integer.parseInt(item.getValue()))).findFirst().isPresent())
                            diseaseCheckComboBox.getCheckModel().check(item);
                    }

                    if(panelDetail.getDefaultDiseaseId() != null && defaultDiseaseComboBox.getItems() != null &&
                            !defaultDiseaseComboBox.getItems().isEmpty()) {
                        final String id = String.valueOf(panelDetail.getDefaultDiseaseId());
                        Optional<ComboBoxItem> optionalComboBoxItem = defaultDiseaseComboBox.getItems().stream()
                                .filter(comboBoxItem -> comboBoxItem.getValue()
                                        .equals(id)).findFirst();
                        optionalComboBoxItem.ifPresent(comboBoxItem ->
                                defaultDiseaseComboBox.getSelectionModel().select(comboBoxItem));
                    }
                }

                roiFileSelectionButton.setDisable(false);
                panelSaveButton.setDisable(false);
            });

            img2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                DialogUtil.setIcon(alert);
                String alertHeaderText = "";
                String alertContentText = "Are you sure to delete this panel?";

                alert.setTitle("Confirmation Dialog");
                Panel panel = PanelModifyButton.this.getTableView().getItems().get(
                        PanelModifyButton.this.getIndex());
                alert.setHeaderText(panel.getName());
                alert.setContentText(alertContentText);
                logger.debug(panel.getId() + " : present id");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK) {
                    deletePanel(panel.getId());
                } else {
                    logger.debug(result.get() + " : button select");
                    alert.close();
                }
            });
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(item == null) {
                setGraphic(null);
                return;
            }

            Panel panel = PanelModifyButton.this.getTableView().getItems().get(
                    PanelModifyButton.this.getIndex());

            if(panel.getDeleted() != 0) {
                return;
            }

            box = new HBox();

            box.setAlignment(Pos.CENTER);

            box.setSpacing(10);
            img1.setStyle("-fx-cursor:hand;");
            img2.setStyle("-fx-cursor:hand;");
            box.getChildren().add(img1);
            box.getChildren().add(img2);

            setGraphic(box);

        }
    }

    private class VirtualPanelButton extends TableCell<PanelView, Boolean> {
        HBox box = null;
        ComboBox<ComboBoxItem> comboBox = new ComboBox<>();
        final ImageView img = new ImageView(resourceUtil.getImage("/layout/images/modify.png", 18, 18));

        public VirtualPanelButton() {

            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Panel panel = VirtualPanelButton.this.getTableView().getItems().get(
                        VirtualPanelButton.this.getIndex());
                try {
                    VirtualPanelEditController controller;
                    FXMLLoader loader = mainApp.load(FXMLConstants.VIRTUAL_PANEL_EDIT);
                    Node pane = loader.load();
                    logger.debug("try virtual Panel edit..A");
                    controller = loader.getController();
                    controller.setComboBox(this.comboBox);
                    controller.setPanelId(panel.getId());
                    controller.setMainController(mainController);
                    controller.show((Parent) pane);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

            comboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
                if(t1 != null && !StringUtils.isEmpty(t1.getValue())) {
                    Panel panel = VirtualPanelButton.this.getTableView().getItems().get(
                            VirtualPanelButton.this.getIndex());

                    Integer virtualPanelId = Integer.parseInt(t1.getValue());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("virtual panel setting");
                    alert.setHeaderText("What would you like to do?");
                    alert.setContentText("choose an action");

                    ButtonType buttonTypeEdit = new ButtonType("Edit");
                    ButtonType buttonTypeRemove = new ButtonType("Remove");
                    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(buttonTypeEdit, buttonTypeRemove, buttonTypeCancel);

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent()) {
                        if(result.get() == buttonTypeEdit) {
                            logger.debug("try virtual Panel edit..");
                            try {
                                VirtualPanelEditController controller;
                                FXMLLoader loader = FXMLLoadUtil.load(FXMLConstants.VIRTUAL_PANEL_EDIT);
                                AnchorPane pane = loader.load();
                                controller = loader.getController();
                                controller.setComboBox(this.comboBox);
                                controller.settingVirtualPanelContents(virtualPanelId);
                                controller.setPanelId(panel.getId());
                                controller.setMainController(mainController);
                                controller.show(pane);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if(result.get() == buttonTypeRemove) {
                            showRemoveDialog(t1);
                            alert.close();
                        } else {
                            Platform.runLater(() -> comboBox.getSelectionModel().selectFirst());
                        }
                    }
                }
            });

        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(item == null) {
                setGraphic(null);
                return;
            }

            PanelView panel = VirtualPanelButton.this.getTableView().getItems().get(
                    VirtualPanelButton.this.getIndex());

            if(panel.getDeleted() != 0) {
                return;
            }

            if(box == null) {
                comboBox.setPrefWidth(130);
                comboBox.setMinWidth(130);
                box = new HBox();

                comboBox.setConverter(new ComboBoxConverter());

                comboBox.getItems().add(new ComboBoxItem());
                comboBox.getSelectionModel().selectFirst();

                if(panel.getVirtualPanelSummaries() != null && !panel.getVirtualPanelSummaries().isEmpty()) {

                    for(VirtualPanelSummary virtualPanelSummary : panel.getVirtualPanelSummaries()) {
                        comboBox.getItems().add(new ComboBoxItem(virtualPanelSummary.getId().toString(), virtualPanelSummary.getName()));
                    }
                }

                box.setAlignment(Pos.CENTER);

                box.setSpacing(10);
                img.setStyle("-fx-cursor:hand;");
                box.getChildren().add(comboBox);
                box.getChildren().add(img);
            }
            setGraphic(box);

        }

        public void showRemoveDialog(ComboBoxItem item) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            DialogUtil.setIcon(alert);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Look, a Confirmation Dialog");
            alert.setContentText("Are you ok with this?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                try {
                    apiService.delete("admin/virtualPanels/" + item.getValue());

                    comboBox.getItems().remove(item);

                } catch (WebAPIException wae) {
                    wae.printStackTrace();
                }
                alert.close();
            }
        }

    }

}
