package ngeneanalysys.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import ngeneanalysys.code.enums.ExperimentTypeCode;
import ngeneanalysys.code.enums.LibraryTypeCode;
import ngeneanalysys.code.enums.SampleSourceCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.task.BedFileUploadTask;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    private Button roiFileSelectionButton;

    @FXML
    private Button panelSaveButton;

    @FXML
    private TableView<Panel> panelListTable;

    @FXML
    private TableColumn<Panel, Integer> panelIdTableColumn;

    @FXML
    private TableColumn<Panel, String> panelNameTableColumn;

    @FXML
    private TableColumn<Panel, String> panelCodeTableColumn;

    @FXML
    private TableColumn<Panel, String> panelTargetTableColumn;

    @FXML
    private TableColumn<Panel, String> analysisTypeTableColumn;

    @FXML
    private TableColumn<Panel, String> libraryTypeTableColumn;

    @FXML
    private TableColumn<Panel, String> sampleSourceTableColumn;

    @FXML
    private TableColumn<Panel, String> createdAtTableColumn;

    @FXML
    private TableColumn<Panel, String> updatedAtTableColumn;

    @FXML
    private TableColumn<Panel, String> deletedAtTableColumn;

    @FXML
    private TableColumn<Panel, String> deletedTableColumn;

    @FXML
    private TableColumn<Panel, Boolean> editPanelTableColumn;

    @FXML
    private Label titleLabel;

    @FXML
    private GridPane panelEditGridPane;

    private CheckComboBox<ComboBoxItem> groupCheckComboBox = null;

    private CheckComboBox<ComboBoxItem> diseaseCheckComboBox = null;

    File bedFile = null;

    private int panelId = 0;

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();

        panelSaveButton.setDisable(true);

        panelIdTableColumn.setCellValueFactory(item -> new SimpleIntegerProperty(item.getValue().getId()).asObject());
        panelNameTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getName()));
        panelCodeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getCode()));
        panelTargetTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getTarget()));
        analysisTypeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getAnalysisType()));
        sampleSourceTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getSampleSource()));
        libraryTypeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getLibraryType()));
        deletedTableColumn.setCellValueFactory(item -> new SimpleStringProperty((item.getValue().getDeleted() == 0) ? "N" : "Y"));

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

        HttpClientResponse response = null;

        try {
            response = apiService.get("/admin/panels", null, null, false);
            final PagedPanel panels = response.getObjectBeforeConvertResponseToJSON(PagedPanel.class);
            panelListTable.getItems().addAll(panels.getResult());

            response = apiService.get("/admin/memberGroups", null, null, false);
            logger.info(response.getContentString());
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


        setDisabledItem(true);
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
            params.put("target", targetComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("analysisType", analysisTypeComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("libraryType", libraryTypeComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("sampleSource", sampleSourceComboBox.getSelectionModel().getSelectedItem().getValue());

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
                        final HttpClientResponse response1 = apiService.get("/panels", null, null, false);
                        final PagedPanel panels = response1.getObjectBeforeConvertResponseToJSON(PagedPanel.class);
                        mainController.getBasicInformationMap().put("panels", panels.getResult());
                        final HttpClientResponse response2 = apiService.get("/admin/panels", null, null, false);
                        final PagedPanel tablePanels = response2.getObjectBeforeConvertResponseToJSON(PagedPanel.class);
                        //mainController.getBasicInformationMap().put("panels", panels.getResult());

                        panelListTable.getItems().clear();
                        panelListTable.getItems().addAll(tablePanels.getResult());
                        resetItem();
                        setDisabledItem(true);
                        panelSaveButton.setDisable(true);
                    } catch (Exception e) {
                        logger.error("panel list refresh fail.", e);
                    }

                    //List<Panel> panels = (List<Panel>) mainController.getBasicInformationMap().get("panels");
                    //panels.add(newPanel);

                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resetItem() {
        panelNameTextField.setText("");
        panelCodeTextField.setText("");
        sampleSourceComboBox.getSelectionModel().selectFirst();
        analysisTypeComboBox.getSelectionModel().selectFirst();
        targetComboBox.getSelectionModel().selectFirst();
        libraryTypeComboBox.getSelectionModel().selectFirst();
        bedFile = null;
        panelSaveButton.setDisable(true);
        groupCheckComboBox.getCheckModel().clearChecks();
        diseaseCheckComboBox.getCheckModel().clearChecks();
    }

    public void setDisabledItem(boolean condition) {
        panelNameTextField.setDisable(condition);
        panelCodeTextField.setDisable(condition);
        sampleSourceComboBox.setDisable(condition);
        analysisTypeComboBox.setDisable(condition);
        targetComboBox.setDisable(condition);
        libraryTypeComboBox.setDisable(condition);
        roiFileSelectionButton.setDisable(condition);
        groupCheckComboBox.setDisable(condition);
        diseaseCheckComboBox.setDisable(condition);
    }

    public void deletePanel(Integer panelId) {
        try {
            apiService.delete("admin/panels/" + panelId);

            HttpClientResponse response = apiService.get("admin/panels", null, null, false);
            final PagedPanel tablePanels = response.getObjectBeforeConvertResponseToJSON(PagedPanel.class);
            mainController.getBasicInformationMap().put("panels", tablePanels.getResult());

            panelListTable.getItems().clear();
            panelListTable.getItems().addAll(tablePanels.getResult());

        } catch (WebAPIException wae) {
            wae.printStackTrace();
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

    private class PanelModifyButton extends TableCell<Panel, Boolean> {
        HBox box = null;
        final ImageView img1 = new ImageView(resourceUtil.getImage("/layout/images/modify.png", 18, 18));
        final ImageView img2 = new ImageView(resourceUtil.getImage("/layout/images/delete.png", 18, 18));

        public PanelModifyButton() {

            img1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Panel panel = PanelModifyButton.this.getTableView().getItems().get(
                        PanelModifyButton.this.getIndex());

                Panel panelDetail = null;
                HttpClientResponse response = null;
                try {
                    response = apiService.get("admin/panels/" + panel.getId(), null, null, false);
                    panelDetail = response.getObjectBeforeConvertResponseToJSON(Panel.class);
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

            box.getChildren().add(img1);
            box.getChildren().add(img2);

            setGraphic(box);

        }
    }

}
