package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * @author Jang
 * @since 2017-09-25
 */
public class SystemManagerPanelController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

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
    private ComboBox<ComboBoxItem> sampleSourceComboBox;

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
    private TextField reportCutOffMAFTextField;

    @FXML
    private CheckBox reportCutOffMAFCheckBox;

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
    private TableColumn<PanelView, String> sampleSourceTableColumn;

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
    private Label titleLabel;

    @FXML
    private GridPane panelEditGridPane;

    @FXML
    private Pagination panelPagination;

    private CheckComboBox<ComboBoxItem> groupCheckComboBox = null;

    private CheckComboBox<ComboBoxItem> diseaseCheckComboBox = null;

    File bedFile = null;

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
        sampleSourceTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getSampleSource()));
        libraryTypeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getLibraryType()));
        defaultPanelTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getIsDefault() ? "Y" : "N"));
        deletedTableColumn.setCellValueFactory(item -> new SimpleStringProperty((item.getValue().getDeleted() == 0) ? "N" : "Y"));

        warningMAFTextField.setTextFormatter(returnFormatter());

        reportCutOffMAFTextField.setTextFormatter(returnFormatter());

        warningReadDepthTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[0-9]*")) warningReadDepthTextField.setText(oldValue);
        });

        warningMAFCheckBox.selectedProperty().addListener((observable, oldValue,  newValue) ->
            warningMAFTextField.setDisable(!newValue)
        );

        warningReadDepthCheckBox.selectedProperty().addListener((observable, oldValue,  newValue) ->
            warningReadDepthTextField.setDisable(!newValue)
        );

        reportCutOffMAFCheckBox.selectedProperty().addListener((observable, oldValue,  newValue) ->
                reportCutOffMAFTextField.setDisable(!newValue)
        );


        targetComboBox.setConverter(new ComboBoxConverter());
        targetComboBox.getItems().add(new ComboBoxItem("DNA", "DNA"));
        targetComboBox.getItems().add(new ComboBoxItem("RNA", "RNA"));
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
        sampleSourceComboBox.setConverter(new ComboBoxConverter());
        sampleSourceComboBox.getItems().add(new ComboBoxItem(SampleSourceCode.BLOOD.getDescription()
                , SampleSourceCode.BLOOD.getDescription()));
        sampleSourceComboBox.getItems().add(new ComboBoxItem(SampleSourceCode.FFPE.getDescription(), SampleSourceCode.FFPE.getDescription()));
        sampleSourceComboBox.getSelectionModel().selectFirst();

        createdAtTableColumn.setCellValueFactory(item -> new SimpleStringProperty(DateFormatUtils.format(
                item.getValue().getCreatedAt().toDate(), "yyyy-MM-dd")));
        updatedAtTableColumn.setCellValueFactory(item -> {
            if (item.getValue().getUpdatedAt() != null )
                return new SimpleStringProperty(DateFormatUtils.format(
                    item.getValue().getUpdatedAt().toDate(), "yyyy-MM-dd"));
            else
                return new SimpleStringProperty("");
        });
        deletedAtTableColumn.setCellValueFactory(item -> {
            if (item.getValue().getDeletedAt() != null )
                return new SimpleStringProperty(DateFormatUtils.format(
                        item.getValue().getDeletedAt().toDate(), "yyyy-MM-dd"));
            else
                return new SimpleStringProperty("");
        });

        editPanelTableColumn.setSortable(false);
        editPanelTableColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        editPanelTableColumn.setCellFactory(param -> new PanelModifyButton());

        virtualPanelColumn.setSortable(false);
        virtualPanelColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        virtualPanelColumn.setCellFactory(param -> new VirtualPanelButton());

        HttpClientResponse response = null;

        try {
            //response = apiService.get("/admin/panels", null, null, false);
            //final PagedPanel panels = response.getObjectBeforeConvertResponseToJSON(PagedPanel.class);
            //panelListTable.getItems().addAll(panels.getResult());

            response = apiService.get("/admin/memberGroups", null, null, false);

                if(response != null) {
                    SystemManagerUserGroupPaging systemManagerUserGroupPaging =
                            response.getObjectBeforeConvertResponseToJSON(SystemManagerUserGroupPaging.class);
                    List<UserGroup> groupList = systemManagerUserGroupPaging.getList();

                    List<ComboBoxItem> items = new ArrayList<>();

                    for(UserGroup userGroup :groupList) {
                        items.add(new ComboBoxItem(userGroup.getId().toString(), userGroup.getName()));
                    }

                    final CheckComboBox<ComboBoxItem> groupCheckComboBox = new CheckComboBox<>();
                    groupCheckComboBox.setConverter(new ComboBoxConverter());

                    groupCheckComboBox.getItems().addAll(items);

                    panelEditGridPane.add(groupCheckComboBox, 1, 6);

                    this.groupCheckComboBox = groupCheckComboBox;

                    groupCheckComboBox.setPrefWidth(150);
                    groupCheckComboBox.setMaxWidth(Double.MAX_VALUE);

                }

                List<Diseases> diseasesList = (List<Diseases>)mainController.getBasicInformationMap().get("diseases");

                if(diseasesList != null) {
                    List<ComboBoxItem> items = new ArrayList<>();

                    for(Diseases disease : diseasesList) {
                        items.add(new ComboBoxItem(disease.getId().toString(), disease.getName()));
                    }

                    final CheckComboBox<ComboBoxItem> diseaseCheckComboBox = new CheckComboBox<>();
                    diseaseCheckComboBox.setConverter(new ComboBoxConverter());

                    diseaseCheckComboBox.getItems().addAll(items);

                    panelEditGridPane.add(diseaseCheckComboBox, 1, 7);

                    diseaseCheckComboBox.setPrefWidth(150);
                    diseaseCheckComboBox.setMaxWidth(Double.MAX_VALUE);

                    this.diseaseCheckComboBox = diseaseCheckComboBox;
                }

            } catch (WebAPIException wae) {
            wae.printStackTrace();
        }

        panelPagination.setPageFactory(pageIndex -> {
            setPanelList(pageIndex + 1);
            return new VBox();
        });

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

                logger.info(String.format("total count : %s, page count : %s", totalCount, pageCount));

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
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
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

    @FXML
    public void savePanel() {
        String panelName = panelNameTextField.getText();
        String code = panelCodeTextField.getText();

        if(!StringUtils.isEmpty(panelName) &&  !StringUtils.isEmpty(code)) {
            if(bedFile == null && panelId == 0) return;

            Map<String,Object> params = new HashMap<>();
            params.put("name", panelName);
            params.put("code", code);
            if(warningReadDepthCheckBox.isSelected() && !StringUtils.isEmpty(warningReadDepthTextField.getText())) {
                params.put("warningReadDepth", Integer.parseInt(warningReadDepthTextField.getText()));
            }
            if(warningMAFCheckBox.isSelected() && !StringUtils.isEmpty(warningMAFTextField.getText())) {
                params.put("warningMAF", warningMAFTextField.getText());
            }
            if(reportCutOffMAFCheckBox.isSelected() && !StringUtils.isEmpty(reportCutOffMAFTextField.getText())) {
                params.put("reportCutoffMAF", reportCutOffMAFTextField.getText());
            }

            params.put("target", targetComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("analysisType", analysisTypeComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("libraryType", libraryTypeComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("sampleSource", sampleSourceComboBox.getSelectionModel().getSelectedItem().getValue());

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
                        /*final HttpClientResponse response1 = apiService.get("/panels", null, null, false);
                        final PagedPanel panels = response1.getObjectBeforeConvertResponseToJSON(PagedPanel.class);
                        mainController.getBasicInformationMap().put("panels", panels.getResult());*/

                        mainController.settingPanelAndDiseases();

                        //final HttpClientResponse response2 = apiService.get("/admin/panels", null, null, false);
                        //final PagedPanel tablePanels = response2.getObjectBeforeConvertResponseToJSON(PagedPanel.class);
                        //mainController.getBasicInformationMap().put("panels", panels.getResult());

                        //panelListTable.getItems().clear();
                        //panelListTable.getItems().addAll(tablePanels.getResult());
                        setPanelList(1);
                        resetItem();
                        setDisabledItem(true);
                        panelSaveButton.setDisable(true);
                    } catch (Exception e) {
                        logger.error("panel list refresh fail.", e);
                    }

                });

            } catch (WebAPIException wae) {
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
            } catch (IOException ioe) {
                DialogUtil.error(ioe.getMessage(), ioe.getMessage(), mainController.getPrimaryStage(), true);
            }
        }
    }

    public void resetItem() {
        reportTemplateComboBoxSetting();
        panelNameTextField.setText("");
        panelCodeTextField.setText("");
        warningMAFTextField.setText("");
        reportCutOffMAFTextField.setText("");
        warningReadDepthTextField.setText("");
        sampleSourceComboBox.getSelectionModel().selectFirst();
        analysisTypeComboBox.getSelectionModel().selectFirst();
        targetComboBox.getSelectionModel().selectFirst();
        libraryTypeComboBox.getSelectionModel().selectFirst();
        bedFile = null;
        panelSaveButton.setDisable(true);
        groupCheckComboBox.getCheckModel().clearChecks();
        diseaseCheckComboBox.getCheckModel().clearChecks();
        reportTemplateComboBox.getSelectionModel().selectFirst();
        warningReadDepthCheckBox.setSelected(false);
        warningMAFCheckBox.setSelected(false);
        reportCutOffMAFCheckBox.setSelected(false);
    }

    public void setDisabledItem(boolean condition) {
        warningReadDepthTextField.setDisable(true);
        warningMAFTextField.setDisable(true);
        reportCutOffMAFTextField.setDisable(true);
        warningReadDepthCheckBox.setDisable(condition);
        warningMAFCheckBox.setDisable(condition);
        reportCutOffMAFCheckBox.setDisable(condition);
        panelNameTextField.setDisable(condition);
        panelCodeTextField.setDisable(condition);
        sampleSourceComboBox.setDisable(condition);
        analysisTypeComboBox.setDisable(condition);
        targetComboBox.setDisable(condition);
        libraryTypeComboBox.setDisable(condition);
        roiFileSelectionButton.setDisable(condition);
        groupCheckComboBox.setDisable(condition);
        diseaseCheckComboBox.setDisable(condition);
        reportTemplateComboBox.setDisable(condition);
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
        titleLabel.setText("panel add");
        setDisabledItem(false);
        resetItem();
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

                titleLabel.setText("panel modify");

                setDisabledItem(false);
                resetItem();

                panelId = panel.getId();

                panelNameTextField.setText(panel.getName());
                panelCodeTextField.setText(panel.getCode());
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
                if(panel.getReportCutOffParams().getMinAlleleFrequency() != null) {
                    reportCutOffMAFCheckBox.setSelected(true);
                    reportCutOffMAFTextField.setDisable(false);
                    reportCutOffMAFTextField.setText(panel.getReportCutOffParams().getMinAlleleFrequency().toString());
                }

                Optional<ComboBoxItem> sampleSourceItem =
                        sampleSourceComboBox.getItems().stream().filter(item -> item.getValue().equalsIgnoreCase(panel.getSampleSource())).findFirst();
                if(sampleSourceItem.isPresent()) sampleSourceComboBox.getSelectionModel().select(sampleSourceItem.get());

                Optional<ComboBoxItem> analysisTypeItem =
                        analysisTypeComboBox.getItems().stream().filter(item -> item.getValue().equalsIgnoreCase(panel.getAnalysisType())).findFirst();
                if(analysisTypeItem.isPresent()) analysisTypeComboBox.getSelectionModel().select(analysisTypeItem.get());

                Optional<ComboBoxItem> targetItem = targetComboBox.getItems().stream().filter(item -> item.getValue().equalsIgnoreCase(panel.getTarget())).findFirst();
                if(targetItem.isPresent()) targetComboBox.getSelectionModel().select(targetItem.get());

                Optional<ComboBoxItem> libraryTypeItem = libraryTypeComboBox.getItems().stream().filter(item -> item.getValue().equalsIgnoreCase(panel.getLibraryType())).findFirst();
                if(libraryTypeItem.isPresent()) libraryTypeComboBox.getSelectionModel().select(libraryTypeItem.get());
                if(panel.getReportTemplateId() != null) {
                    Optional<ComboBoxItem> reportTemplate = reportTemplateComboBox.getItems().stream().filter(item -> item.getValue().equalsIgnoreCase(panel.getReportTemplateId().toString())).findFirst();
                    if (reportTemplate.isPresent())
                        reportTemplateComboBox.getSelectionModel().select(reportTemplate.get());
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
                }

                roiFileSelectionButton.setDisable(false);
                panelSaveButton.setDisable(false);

                if(panelDetail.getIsDefault()) {
                    setDisabledItem(true);
                    diseaseCheckComboBox.setDisable(false);
                    groupCheckComboBox.setDisable(false);
                }

                if(panelDetail.getName().contains("BRCA")) {
                    diseaseCheckComboBox.setDisable(true);
                    warningMAFCheckBox.setDisable(true);
                    warningReadDepthCheckBox.setDisable(true);
                }
            });

            img2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                String alertHeaderText = null;
                String alertContentText = "Are you sure to delete this panel?";

                alert.setTitle("Confirmation Dialog");
                Panel panel = PanelModifyButton.this.getTableView().getItems().get(
                        PanelModifyButton.this.getIndex());
                alert.setHeaderText(panel.getName());
                alert.setContentText(alertContentText);
                logger.info(panel.getId() + " : present id");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK) {
                    deletePanel(panel.getId());
                } else {
                    logger.info(result.get() + " : button select");
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
                    logger.info("try virtual Panel edit..A");
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
                            logger.info("try virtual Panel edit..");
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
