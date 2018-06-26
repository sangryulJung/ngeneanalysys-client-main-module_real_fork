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
import ngeneanalysys.code.enums.ExperimentTypeCode;
import ngeneanalysys.code.enums.LibraryTypeCode;
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
    private TextField panelNameTextField;

    @FXML
    private TextField panelCodeTextField;

    @FXML
    private ComboBox<ComboBoxItem> targetComboBox;

    @FXML
    private ComboBox<ComboBoxItem> analysisTypeComboBox;

    @FXML
    private ComboBox<ComboBoxItem> libraryTypeComboBox;

    @FXML
    private ComboBox<ComboBoxItem> defaultSampleSourceComboBox;

    @FXML
    private ComboBox<ComboBoxItem> defaultDiseaseComboBox;

    @FXML
    private ComboBox<ComboBoxItem> reportTemplateComboBox;

    @FXML
    private TextField warningReadDepthTextField;

    @FXML
    private TextField warningMAFTextField;

    @FXML
    private CheckBox warningReadDepthCheckBox;

    @FXML
    private CheckBox warningMAFCheckBox;

    @FXML
    private TextField minAlleleFrequencyTextField;

    @FXML
    private TextField minReadDepthTextField;

    @FXML
    private TextField minAlternateCountTextField;

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
    private TableColumn<PanelView, String> defaultPanelTableColumn;

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

    private CheckComboBox<ComboBoxItem> groupCheckComboBox = null;

    private CheckComboBox<ComboBoxItem> diseaseCheckComboBox = null;

    private CheckComboBox<String> lowConfidenceCheckComboBox = null;

    private CheckComboBox<String> frequencyDBCheckComboBox = null;

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

        panelIdTableColumn.setCellValueFactory(item -> new SimpleIntegerProperty(item.getValue().getId()).asObject());
        panelNameTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getName()));
        panelCodeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getCode()));
        panelTargetTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getTarget()));
        analysisTypeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getAnalysisType()));
        libraryTypeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getLibraryType()));
        defaultPanelTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getIsDefault() ? "Y" : "N"));
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

        targetComboBox.setConverter(new ComboBoxConverter());
        targetComboBox.getItems().add(new ComboBoxItem("DNA", "DNA"));
        targetComboBox.getItems().add(new ComboBoxItem("RNA", "RNA"));
        targetComboBox.getSelectionModel().selectFirst();

        targetComboBox.getSelectionModel().selectFirst();

        analysisTypeComboBox.setConverter(new ComboBoxConverter());
        analysisTypeComboBox.getItems().add(new ComboBoxItem(ExperimentTypeCode.SOMATIC.getDescription(),
                ExperimentTypeCode.SOMATIC.getDescription()));
        analysisTypeComboBox.getItems().add(new ComboBoxItem(ExperimentTypeCode.GERMLINE.getDescription(),
                ExperimentTypeCode.GERMLINE.getDescription()));
        analysisTypeComboBox.getItems().add(new ComboBoxItem(ExperimentTypeCode.SOMATIC_AND_GERMLINE.getDescription(),
                ExperimentTypeCode.SOMATIC_AND_GERMLINE.getDescription()));
        analysisTypeComboBox.getSelectionModel().selectFirst();
        libraryTypeComboBox.setConverter(new ComboBoxConverter());
        libraryTypeComboBox.getItems().add(new ComboBoxItem(LibraryTypeCode.HYBRIDIZATION_CAPTURE.getDescription(),
                LibraryTypeCode.HYBRIDIZATION_CAPTURE.getDescription()));
        libraryTypeComboBox.getItems().add(new ComboBoxItem(LibraryTypeCode.AMPLICON_BASED.getDescription(),
                LibraryTypeCode.AMPLICON_BASED.getDescription()));
        libraryTypeComboBox.getSelectionModel().selectFirst();
        defaultSampleSourceComboBox.setConverter(new ComboBoxConverter());
        defaultSampleSourceComboBox.getItems().add(new ComboBoxItem(SampleSourceCode.ETC.getDescription(),
                SampleSourceCode.ETC.getDescription()));
        defaultSampleSourceComboBox.getItems().add(new ComboBoxItem(SampleSourceCode.DNA.getDescription(),
                SampleSourceCode.DNA.getDescription()));
        defaultSampleSourceComboBox.getItems().add(new ComboBoxItem(SampleSourceCode.FFPE.getDescription(),
                SampleSourceCode.FFPE.getDescription()));
        defaultSampleSourceComboBox.getItems().add(new ComboBoxItem(SampleSourceCode.BONEMARROW.getDescription(),
                SampleSourceCode.BONEMARROW.getDescription()));
        defaultSampleSourceComboBox.getItems().add(new ComboBoxItem(SampleSourceCode.BONEMARROWCRYO.getDescription(),
                SampleSourceCode.BONEMARROWCRYO.getDescription()));
        defaultSampleSourceComboBox.getItems().add(new ComboBoxItem(SampleSourceCode.BLOOD.getDescription(),
                SampleSourceCode.BLOOD.getDescription()));
        defaultSampleSourceComboBox.getItems().add(new ComboBoxItem(SampleSourceCode.BLOODCRYO.getDescription(),
                SampleSourceCode.BLOODCRYO.getDescription()));
        defaultSampleSourceComboBox.getSelectionModel().selectFirst();
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

        createComboBox();

        setDisabledItem(true);
    }

    public TextFormatter returnFormatter() {
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        return new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
                pattern.matcher(change.getControlNewText()).matches() ? change : null);
    }

    public void setPanelList(int page) {

        int totalCount = 0;
        int limit = 15;
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

    public void createComboBoxItem() {
        frequencyDBCheckComboBox.getItems().addAll("g1000.all", "g1000.african", "g1000.american",
                "g1000.eastAsian", "g1000.european", "g1000.southAsian", "esp6500.all", "esp6500.aa",
                "esp6500.ea", "gnomAD.all", "gnomAD.admixedAmerican", "gnomAD.africanAfricanAmerican",
                "gnomAD.eastAsian", "gnomAD.finnish", "gnomAD.nonFinnishEuropean", "gnomAD.others", "gnomAD.southAsian",
                "koreanExomInformationDatabase", "koreanReferenceGenomeDatabase", "exac");
        lowConfidenceCheckComboBox.getItems().addAll("artifact_in_normal", "base_quality", "clustered_events",
                "contamination", "duplicate_evidence", "fragment_length", "germline_risk", "mapping_quality",
                "multiallelic", "orientation_bias", "panel_of_normals", "read_position", "str_contraction",
                "strand_artifact", "t_lod", "homopolymer", "repeat_sequence", "sequencing_error", "mapping_error",
                "snp_candidate");
    }

    public void createComboBox() {
        final CheckComboBox<ComboBoxItem> groupCheckComboBox = new CheckComboBox<>();
        groupCheckComboBox.setConverter(new ComboBoxConverter());

        groupCheckComboBox.setPrefWidth(150);
        groupCheckComboBox.setMaxWidth(Double.MAX_VALUE);

        this.groupCheckComboBox = groupCheckComboBox;

        panelEditGridPane.add(groupCheckComboBox, 1, 5);

        final CheckComboBox<ComboBoxItem> diseaseCheckComboBox = new CheckComboBox<>();
        diseaseCheckComboBox.setConverter(new ComboBoxConverter());

        diseaseCheckComboBox.setPrefWidth(150);
        diseaseCheckComboBox.setMaxWidth(Double.MAX_VALUE);

        this.diseaseCheckComboBox = diseaseCheckComboBox;

        panelEditGridPane.add(diseaseCheckComboBox, 1, 6);

        final CheckComboBox<String> lowConfidenceCheckComboBox = new CheckComboBox<>();

        lowConfidenceCheckComboBox.setPrefWidth(145);
        lowConfidenceCheckComboBox.setMaxWidth(Double.MAX_VALUE);

        this.lowConfidenceCheckComboBox = lowConfidenceCheckComboBox;

        panelEditGridPane.add(lowConfidenceCheckComboBox, 1, 13);

        final CheckComboBox<String> frequencyDBCheckComboBox = new CheckComboBox<>();

        frequencyDBCheckComboBox.setPrefWidth(150);
        frequencyDBCheckComboBox.setMaxWidth(Double.MAX_VALUE);

        this.frequencyDBCheckComboBox = frequencyDBCheckComboBox;

        panelEditGridPane.add(frequencyDBCheckComboBox, 1, 18);

        createComboBoxItem();
    }

    public void setPanelAndDisease() {
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

            List<Diseases> diseasesList = (List<Diseases>)mainController.getBasicInformationMap().get("diseases");

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

    public void reportTemplateComboBoxSetting() {

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

    public ReportCutOffParams setReportCutOffParams() {
        ReportCutOffParams reportCutOffParams = new ReportCutOffParams();

        try {
            BigDecimal reportCutOffMinAlleleFrequency = new BigDecimal(minAlleleFrequencyTextField.getText());
            reportCutOffParams.setMinAlleleFrequency(reportCutOffMinAlleleFrequency);
        } catch (Exception e) { }
        try {
            Integer reportCutoffMinReadDepth = Integer.parseInt(minReadDepthTextField.getText());
            reportCutOffParams.setMinReadDepth(reportCutoffMinReadDepth);
        } catch (Exception e) { }
        try {
            Integer reportCutOffMinAlternateCount = Integer.parseInt(minAlternateCountTextField.getText());
            reportCutOffParams.setMinAlternateCount(reportCutOffMinAlternateCount);
        } catch (Exception e) { }
        //reportCutOffParams.setPopulationFrequencyDBs(populationFrequencyDBsTextField.getText());
        if(!frequencyDBCheckComboBox.getCheckModel().getCheckedItems().isEmpty()) {
            final StringBuilder value = new StringBuilder();
            frequencyDBCheckComboBox.getCheckModel().getCheckedItems().forEach(item -> value.append(item + ","));
            value.deleteCharAt(value.length() - 1);
            reportCutOffParams.setPopulationFrequencyDBs(value.toString());
        }
        try {
            BigDecimal reportCutOffPopulationFrequency = new BigDecimal(populationFrequencyTextField.getText());
            reportCutOffParams.setPopulationFrequency(reportCutOffPopulationFrequency);
        } catch (Exception e) { }

        return reportCutOffParams;
    }

    public QCPassConfig setQCPassingConfig() {
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
            qcPassConfig.setOnTargetCoverage(onTarget);
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

        return qcPassConfig;
    }

    @FXML
    public void savePanel() {
        String panelName = panelNameTextField.getText();
        String code = panelCodeTextField.getText();

        if(!StringUtils.isEmpty(panelName) &&  !StringUtils.isEmpty(code)) {
            if(bedFile == null && panelId == 0) return;

            Map<String,Object> params = new HashMap<>();
            params.put("name", panelName);
            params.put("code", code);
            VariantConfig variantConfig = new VariantConfig();
            if(warningReadDepthCheckBox.isSelected() && !StringUtils.isEmpty(warningReadDepthTextField.getText())) {
                variantConfig.setWarningReadDepth(Integer.parseInt(warningReadDepthTextField.getText()));
            }
            if(warningMAFCheckBox.isSelected() && !StringUtils.isEmpty(warningMAFTextField.getText())) {
                variantConfig.setWarningMAF(new BigDecimal(warningMAFTextField.getText()));
            }
            if(!lowConfidenceCheckComboBox.getCheckModel().getCheckedItems().isEmpty()) {
                final StringBuilder value = new StringBuilder();
                lowConfidenceCheckComboBox.getCheckModel().getCheckedItems().forEach(item -> value.append(item + ","));
                value.deleteCharAt(value.length() - 1);
                variantConfig.setLowConfidenceFilter(value.toString());
            }

            variantConfig.setReportCutOffParams(setReportCutOffParams());

            variantConfig.setEssentialGenes(essentialGenesTextField.getText());
            variantConfig.setCanonicalTranscripts(canonicalTranscriptTextArea.getText());
            params.put("qcPassConfig", setQCPassingConfig());
            params.put("target", targetComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("analysisType", analysisTypeComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("libraryType", libraryTypeComboBox.getSelectionModel().getSelectedItem().getValue());
            if(defaultDiseaseComboBox.getSelectionModel().getSelectedItem() != null) {
                params.put("defaultDiseaseId", Integer.parseInt(defaultDiseaseComboBox.getSelectionModel().getSelectedItem().getValue()));
            }
            params.put("defaultSampleSource", defaultSampleSourceComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("variantConfig", variantConfig);

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
                        mainController.settingPanelAndDiseases();

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
        panelCodeTextField.setText("");
        warningMAFTextField.setText("");
        warningReadDepthTextField.setText("");
        analysisTypeComboBox.getSelectionModel().selectFirst();
        targetComboBox.getSelectionModel().selectFirst();
        libraryTypeComboBox.getSelectionModel().selectFirst();
        defaultSampleSourceComboBox.getSelectionModel().selectFirst();
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
        minAlleleFrequencyTextField.setText("");
        minReadDepthTextField.setText("");
        minAlternateCountTextField.setText("");
        populationFrequencyTextField.setText("");
        essentialGenesTextField.setText("");
        canonicalTranscriptTextArea.setText("");
        totalBasePairTextField.setText("");
        q30TrimmedBasePercentageTextField.setText("");
        mappedBasePercentageTextField.setText("");
        onTargetPercentageTextField.setText("");
        onTargetCoverageTextField.setText("");
        duplicatedReadsPercentageTextField.setText("");
        roiCoveragePercentageTextField.setText("");
    }

    public void setDisabledItem(boolean condition) {
        resetItem();
        warningReadDepthTextField.setDisable(condition);
        warningMAFTextField.setDisable(condition);
        warningReadDepthCheckBox.setDisable(condition);
        warningMAFCheckBox.setDisable(condition);
        panelNameTextField.setDisable(condition);
        panelCodeTextField.setDisable(condition);
        analysisTypeComboBox.setDisable(condition);
        targetComboBox.setDisable(condition);
        libraryTypeComboBox.setDisable(condition);
        roiFileSelectionButton.setDisable(condition);
        groupCheckComboBox.setDisable(condition);
        diseaseCheckComboBox.setDisable(condition);
        reportTemplateComboBox.setDisable(condition);
        minAlleleFrequencyTextField.setDisable(condition);
        minReadDepthTextField.setDisable(condition);
        minAlternateCountTextField.setDisable(condition);
        frequencyDBCheckComboBox.setDisable(condition);
        lowConfidenceCheckComboBox.setDisable(condition);
        populationFrequencyTextField.setDisable(condition);
        essentialGenesTextField.setDisable(condition);
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

                panelNameTextField.setText(panel.getName());
                panelCodeTextField.setText(panel.getCode());
                if(panel.getVariantConfig().getWarningMAF() != null) {
                    warningMAFCheckBox.setSelected(true);
                    warningMAFTextField.setDisable(false);
                    warningMAFTextField.setText(panel.getVariantConfig().getWarningMAF().toString());
                }
                if(panel.getVariantConfig().getWarningReadDepth() != null) {
                    warningReadDepthCheckBox.setSelected(true);
                    warningReadDepthTextField.setDisable(false);
                    warningReadDepthTextField.setText(panel.getVariantConfig().getWarningReadDepth().toString());
                }

                ReportCutOffParams params = panel.getVariantConfig().getReportCutOffParams();
                if(panel.getVariantConfig().getReportCutOffParams().getMinAlleleFrequency() != null) minAlleleFrequencyTextField.setText(params.getMinAlleleFrequency().toString());
                if(panel.getVariantConfig().getReportCutOffParams().getMinReadDepth() != null) minReadDepthTextField.setText(params.getMinReadDepth().toString());
                if(panel.getVariantConfig().getReportCutOffParams().getMinAlternateCount() != null) minAlternateCountTextField.setText(params.getMinAlternateCount().toString());
                if(panel.getVariantConfig().getReportCutOffParams().getPopulationFrequencyDBs() != null) {
                    String[] freqDBs = panel.getVariantConfig().getReportCutOffParams().getPopulationFrequencyDBs().split(",");
                    for(String freqDB : freqDBs) {
                        frequencyDBCheckComboBox.getCheckModel().check(freqDB);
                    }
                }
                if(panel.getVariantConfig().getLowConfidenceFilter() != null) {
                    String[] lowConfidences = panel.getVariantConfig().getLowConfidenceFilter().split(",");
                    for(String lowConfidence : lowConfidences) {
                        lowConfidenceCheckComboBox.getCheckModel().check(lowConfidence);
                    }
                }
                if(panel.getVariantConfig().getReportCutOffParams().getPopulationFrequency() != null) populationFrequencyTextField.setText(params.getPopulationFrequency().toString());

                if(panel.getQcPassConfig() != null) {
                    if(panel.getQcPassConfig().getTotalBasePair() != null) totalBasePairTextField.setText(panel.getQcPassConfig().getTotalBasePair().toString());
                    if(panel.getQcPassConfig().getDuplicatedReadsPercentage() != null) duplicatedReadsPercentageTextField.setText(panel.getQcPassConfig().getDuplicatedReadsPercentage().toString());
                    if(panel.getQcPassConfig().getMappedBasePercentage() != null) mappedBasePercentageTextField.setText(panel.getQcPassConfig().getMappedBasePercentage().toString());
                    if(panel.getQcPassConfig().getOnTargetCoverage() != null) onTargetCoverageTextField.setText(panel.getQcPassConfig().getOnTargetCoverage().toString());
                    if(panel.getQcPassConfig().getOnTargetPercentage() != null) onTargetPercentageTextField.setText(panel.getQcPassConfig().getOnTargetPercentage().toString());
                    if(panel.getQcPassConfig().getQ30TrimmedBasePercentage() != null) q30TrimmedBasePercentageTextField.setText(panel.getQcPassConfig().getQ30TrimmedBasePercentage().toString());
                    if(panel.getQcPassConfig().getRoiCoveragePercentage() != null) roiCoveragePercentageTextField.setText(panel.getQcPassConfig().getRoiCoveragePercentage().toString());
                }

                if(panel.getVariantConfig().getEssentialGenes() != null) essentialGenesTextField.setText(panel.getVariantConfig().getEssentialGenes());
                if(panel.getVariantConfig().getCanonicalTranscripts() != null) canonicalTranscriptTextArea.setText(panel.getVariantConfig().getCanonicalTranscripts());

                Optional<ComboBoxItem> analysisTypeItem =
                        analysisTypeComboBox.getItems().stream().filter(item -> item.getValue().equalsIgnoreCase(panel.getAnalysisType())).findFirst();
                analysisTypeItem.ifPresent(comboBoxItem -> analysisTypeComboBox.getSelectionModel().select(comboBoxItem));

                Optional<ComboBoxItem> targetItem = targetComboBox.getItems().stream().filter(item -> item.getValue().equalsIgnoreCase(panel.getTarget())).findFirst();
                targetItem.ifPresent(comboBoxItem -> targetComboBox.getSelectionModel().select(comboBoxItem));

                Optional<ComboBoxItem> libraryTypeItem = libraryTypeComboBox.getItems().stream().filter(item -> item.getValue().equalsIgnoreCase(panel.getLibraryType())).findFirst();
                libraryTypeItem.ifPresent(comboBoxItem -> libraryTypeComboBox.getSelectionModel().select(comboBoxItem));

                Optional<ComboBoxItem> defaultSampleSourceItem = defaultSampleSourceComboBox.getItems().stream().filter(item -> item.getValue().equalsIgnoreCase(panel.getDefaultSampleSource())).findFirst();
                defaultSampleSourceItem.ifPresent(comboBoxItem -> defaultSampleSourceComboBox.getSelectionModel().select(comboBoxItem));

                if(panel.getReportTemplateId() != null) {
                    Optional<ComboBoxItem> reportTemplate = reportTemplateComboBox.getItems().stream().filter(item -> item.getValue().equalsIgnoreCase(panel.getReportTemplateId().toString())).findFirst();
                    reportTemplate.ifPresent(comboBoxItem -> reportTemplateComboBox.getSelectionModel().select(comboBoxItem));
                }

                if(StringUtils.isEmpty(panel.getDefaultSampleSource())) {
                    Optional<ComboBoxItem> sampleSource =
                                defaultSampleSourceComboBox.getItems().stream().filter(item -> item.getValue().equalsIgnoreCase(panel.getDefaultSampleSource()))
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

                /*if(panelDetail.getIsDefault()) {
                    setDisabledItem(true);
                    diseaseCheckComboBox.setDisable(false);
                    groupCheckComboBox.setDisable(false);
                }*/

                if(panelDetail.getName().contains("BRCA")) {
                    //diseaseCheckComboBox.setDisable(true);
                    warningReadDepthTextField.setDisable(true);
                    essentialGenesTextField.setDisable(true);
                    canonicalTranscriptTextArea.setDisable(true);
                    warningMAFCheckBox.setDisable(true);
                    warningMAFTextField.setDisable(true);
                    warningReadDepthCheckBox.setDisable(true);
                    minAlleleFrequencyTextField.setDisable(true);
                    minReadDepthTextField.setDisable(true);
                    minAlternateCountTextField.setDisable(true);
                    lowConfidenceCheckComboBox.setDisable(true);
                    frequencyDBCheckComboBox.setDisable(true);
                    populationFrequencyTextField.setDisable(true);
                    essentialGenesTextField.setDisable(true);
                    canonicalTranscriptTextArea.setDisable(true);
                    totalBasePairTextField.setDisable(true);
                    q30TrimmedBasePercentageTextField.setDisable(true);
                    mappedBasePercentageTextField.setDisable(true);
                    onTargetPercentageTextField.setDisable(true);
                    onTargetCoverageTextField.setDisable(true);
                    duplicatedReadsPercentageTextField.setDisable(true);
                    roiCoveragePercentageTextField.setDisable(true);
                }
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
