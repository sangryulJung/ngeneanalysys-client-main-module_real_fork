package ngeneanalysys.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import ngeneanalysys.code.enums.ExperimentTypeCode;
import ngeneanalysys.code.enums.SampleSourceCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.BedFileService;
import ngeneanalysys.task.BedFileUploadTask;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-09-25
 */
public class SystemManagerPanelController extends SubPaneController {

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
    private ComboBox<ComboBoxItem> sampleSourceComboBox;

    @FXML
    private Button fileSelectBtn;

    @FXML
    private Button panelAddBtn;

    @FXML
    private TableView<Panel> panelListTable;

    @FXML
    private TableColumn<Panel, Integer> panelId;

    @FXML
    private TableColumn<Panel, String> panelName;

    @FXML
    private TableColumn<Panel, String> panelCode;

    @FXML
    private TableColumn<Panel, String> panelTarget;

    @FXML
    private TableColumn<Panel, String> analysisType;

    @FXML
    private TableColumn<Panel, String> sampleSource;

    @FXML
    private TableColumn<Panel, String> createdAt;

    @FXML
    private TableColumn<Panel, String> deleted;

    @FXML
    private TableColumn<Panel, Boolean> modify;

    File file = null;

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();

        panelAddBtn.setDisable(true);

        panelId.setCellValueFactory(item -> new SimpleIntegerProperty(item.getValue().getId()).asObject());
        panelName.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getName()));
        panelCode.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getCode()));
        panelTarget.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getTarget()));
        analysisType.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getAnalysisType()));
        sampleSource.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getSampleSource()));

        List<Panel> panels = (List<Panel>) mainController.getBasicInformationMap().get("panels");
        panelListTable.getItems().addAll(panels);

        targetComboBox.setConverter(new ComboBoxConverter());
        targetComboBox.getItems().add(new ComboBoxItem("DNA", "DNA"));
        targetComboBox.getItems().add(new ComboBoxItem("RNA", "RNA"));
        targetComboBox.getSelectionModel().selectFirst();

        analysisTypeComboBox.setConverter(new ComboBoxConverter());
        analysisTypeComboBox.getItems().add(new ComboBoxItem(ExperimentTypeCode.SOMATIC.getDescription(), ExperimentTypeCode.SOMATIC.getDescription()));
        analysisTypeComboBox.getItems().add(new ComboBoxItem(ExperimentTypeCode.GERMLINE.getDescription(), ExperimentTypeCode.GERMLINE.getDescription()));
        analysisTypeComboBox.getSelectionModel().selectFirst();

        sampleSourceComboBox.setConverter(new ComboBoxConverter());
        sampleSourceComboBox.getItems().add(new ComboBoxItem(SampleSourceCode.BLOOD.getDescription(), SampleSourceCode.BLOOD.getDescription()));
        sampleSourceComboBox.getItems().add(new ComboBoxItem(SampleSourceCode.FFPE.getDescription(), SampleSourceCode.FFPE.getDescription()));
        sampleSourceComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void panelAdd() {
        String panelName = panelNameTextField.getText();
        String code = panelCodeTextField.getText();
        if(!StringUtils.isEmpty(panelName) &&  !StringUtils.isEmpty(code)) {
            if(file == null) return;

            Map<String,Object> params = new HashMap<>();
            params.put("name", panelName);
            params.put("code", code);
            params.put("target", targetComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("analysisType", analysisTypeComboBox.getSelectionModel().getSelectedItem().getValue());
            params.put("sampleSource", sampleSourceComboBox.getSelectionModel().getSelectedItem().getValue());

            HttpClientResponse response = null;
            try {
                response = apiService.post("admin/panels", params, null, true);
                Panel newPanel = response.getObjectBeforeConvertResponseToJSON(Panel.class);

                Task task = new BedFileUploadTask(newPanel.getId(), file);

                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();

                /*response = apiService.get("/panels", null, null, false);
                final List<Panel> panels = (List<Panel>) response.getMultiObjectBeforeConvertResponseToJSON(Panel.class, false);
                mainController.getBasicInformationMap().put("panels", panels);*/
                List<Panel> panels = (List<Panel>) mainController.getBasicInformationMap().get("panels");
                panels.add(newPanel);

                panelListTable.getItems().clear();
                panelListTable.getItems().addAll(panels);

                panelAddBtn.setDisable(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void fileSelect() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose FASTQ File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("bed", "*.bed"));
        File file = fileChooser.showOpenDialog(mainController.getPrimaryStage());

        if(file != null && file.getName().toLowerCase().endsWith(".bed")) {
            this.file = file;
            panelAddBtn.setDisable(false);
        }
    }
}
