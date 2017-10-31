package ngeneanalysys.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import ngeneanalysys.code.enums.ExperimentTypeCode;
import ngeneanalysys.code.enums.LibraryTypeCode;
import ngeneanalysys.code.enums.SampleSourceCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.BedFileService;
import ngeneanalysys.task.BedFileUploadTask;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private Button bedFileSelectionButton;

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

    File file = null;

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

        List<Panel> panels = (List<Panel>) mainController.getBasicInformationMap().get("panels");
        panelListTable.getItems().addAll(panels);

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


        setDisabledItem(true);
    }

    @FXML
    public void savePanel() {
        String panelName = panelNameTextField.getText();
        String code = panelCodeTextField.getText();
        if(!StringUtils.isEmpty(panelName) &&  !StringUtils.isEmpty(code)) {
            if(file == null && panelId == 0) return;

            Map<String,Object> params = new HashMap<>();
            params.put("name", panelName);
            params.put("code", code);
            params.put("target", targetComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("analysisType", analysisTypeComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("libraryType", libraryTypeComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("sampleSource", sampleSourceComboBox.getSelectionModel().getSelectedItem().getValue());

            HttpClientResponse response = null;
            try {
                if(panelId == 0) {
                    response = apiService.post("admin/panels", params, null, true);
                    Panel newPanel = response.getObjectBeforeConvertResponseToJSON(Panel.class);

                    Task task = new BedFileUploadTask(newPanel.getId(), file);

                    Thread thread = new Thread(task);
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    response = apiService.put("admin/panels/" + panelId, params, null, true);
                    Panel modifiedPanel = response.getObjectBeforeConvertResponseToJSON(Panel.class);
                }

                response = apiService.get("/panels", null, null, false);
                final List<Panel> panels = (List<Panel>) response.getMultiObjectBeforeConvertResponseToJSON(Panel.class, false);
                mainController.getBasicInformationMap().put("panels", panels);
                //List<Panel> panels = (List<Panel>) mainController.getBasicInformationMap().get("panels");
                //panels.add(newPanel);

                panelListTable.getItems().clear();
                panelListTable.getItems().addAll(panels);
                resetItem();
                setDisabledItem(true);
                panelSaveButton.setDisable(true);
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
        file = null;
        panelSaveButton.setDisable(true);
    }

    public void setDisabledItem(boolean condition) {
        panelNameTextField.setDisable(condition);
        panelCodeTextField.setDisable(condition);
        sampleSourceComboBox.setDisable(condition);
        analysisTypeComboBox.setDisable(condition);
        targetComboBox.setDisable(condition);
        libraryTypeComboBox.setDisable(condition);
        bedFileSelectionButton.setDisable(condition);
    }

    public void deletePanel(Integer panelId) {
        try {
            apiService.delete("admin/panels/" + panelId);

            HttpClientResponse response = apiService.get("/panels", null, null, false);
            final List<Panel> panels = (List<Panel>) response.getMultiObjectBeforeConvertResponseToJSON(Panel.class, false);
            mainController.getBasicInformationMap().put("panels", panels);

            panelListTable.getItems().clear();
            panelListTable.getItems().addAll(panels);

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
    public void selectBedFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose FASTQ File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("bed", "*.bed"));
        File file = fileChooser.showOpenDialog(mainController.getPrimaryStage());

        if(file != null && file.getName().toLowerCase().endsWith(".bed")) {
            this.file = file;
            panelSaveButton.setDisable(false);
        }
    }

    private class PanelModifyButton extends TableCell<Panel, Boolean> {
        HBox box = null;
        final ImageView img1 = new ImageView(resourceUtil.getImage("/layout/images/icon_pathogenicity_1.png"));
        final ImageView img2 = new ImageView(resourceUtil.getImage("/layout/images/icon_pathogenicity_2.png"));

        public PanelModifyButton() {

            img1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Panel panel = PanelModifyButton.this.getTableView().getItems().get(
                        PanelModifyButton.this.getIndex());

                titleLabel.setText("panel modify");

                setDisabledItem(false);

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

                bedFileSelectionButton.setDisable(true);
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

            box = new HBox();

            box.setSpacing(10);

            box.getChildren().add(img1);
            box.getChildren().add(img2);

            setGraphic(box);

        }
    }

}
