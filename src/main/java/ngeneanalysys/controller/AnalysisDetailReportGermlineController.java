package ngeneanalysys.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
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
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.model.*;
import ngeneanalysys.model.render.DatepickerConverter;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.PDFCreateService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
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
    private TableView<VariantAndInterpretationEvidence> pathogenicVariantsTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> pathogenicGeneColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> pathogenicVariantsColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, Boolean> pathogenicExceptColumn;

    @FXML
    private TableView<VariantAndInterpretationEvidence> likelyPathogenicVariantsTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> likelyPathogenicGeneColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> likelyPathogenicVariantsColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, Boolean> likelyPathogenicExceptColumn;

    @FXML
    private TableView<VariantAndInterpretationEvidence> uncertainSignificanceVariantsTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> uncertainSignificanceGeneColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> uncertainSignificanceVariantsColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, Boolean> uncertainSignificanceExceptColumn;

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

    Sample sample = null;

    Panel panel = null;

    List<VariantAndInterpretationEvidence> pathgenicList = null;

    List<VariantAndInterpretationEvidence> likelyPathgenicList = null;

    List<VariantAndInterpretationEvidence> uncertainSignificanceList = null;

    List<VariantAndInterpretationEvidence> likelyBenignList = null;

    List<VariantAndInterpretationEvidence> benignList = null;

    private VariantAndInterpretationEvidence selectedItem = null;

    private TableView<VariantAndInterpretationEvidence> selectedTable = null;

    private TableRow<VariantAndInterpretationEvidence> rowItem;

    private boolean reportData = false;


    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");

        // API 서비스 클래스 init
        apiService = APIService.getInstance();

        apiService.setStage(getMainController().getPrimaryStage());

        loginSession = LoginSessionUtil.getCurrentLoginSession();

        pdfCreateService = PDFCreateService.getInstance();

        customFieldGridPane.getChildren().clear();
        customFieldGridPane.setPrefHeight(0);

        sample = (Sample)paramMap.get("sample");

        patientIdLabel.setText(sample.getPaitentId());

        settingTableViewDragAndDrop(pathogenicVariantsTable, "P");
        settingTableViewDragAndDrop(likelyPathogenicVariantsTable, "LP");
        settingTableViewDragAndDrop(uncertainSignificanceVariantsTable, "US");

        pathogenicGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSequenceInfo().getGene()));
        pathogenicVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        pathogenicExceptColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        //pathogenicExceptColumn.setCellFactory(param -> new ReportedCheckBox(this));

        likelyPathogenicGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSequenceInfo().getGene()));
        likelyPathogenicVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        likelyPathogenicExceptColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        //pathogenicExceptColumn.setCellFactory(param -> new ReportedCheckBox(this));

        uncertainSignificanceGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSequenceInfo().getGene()));
        uncertainSignificanceVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        uncertainSignificanceExceptColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        //pathogenicExceptColumn.setCellFactory(param -> new ReportedCheckBox(this));

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

        HttpClientResponse response = null;
        try {
            if(panel.getReportTemplateId() != null) {
                response = apiService.get("reportTemplate/" + panel.getReportTemplateId(), null, null, false);

                ReportContents reportContents = response.getObjectBeforeConvertResponseToJSON(ReportContents.class);

                ReportTemplate template = reportContents.getReportTemplate();

                Map<String, Object> variableList = JsonUtil.fromJsonToMap(template.getCustomFields());

                if(variableList != null && !variableList.isEmpty()) {

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
                        label.setStyle("-fx-text-fill : #C20E20;");
                        customFieldGridPane.add(label, colIndex++, rowIndex);
                        label.setMaxHeight(Double.MAX_VALUE);
                        label.setMaxWidth(Double.MAX_VALUE);
                        label.setAlignment(Pos.CENTER);

                        String type = item.get("variableType");
                        if(type.equalsIgnoreCase("Date")) {
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
                            if(type.equalsIgnoreCase("Integer")) {
                                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                                    if(!newValue.matches("[0-9]*")) textField.setText(oldValue);
                                });
                            }

                            customFieldGridPane.add(textField, colIndex++, rowIndex);
                        }
                    }

                }
            }

            response = apiService.get("/analysisResults/sampleSnpInDels/" + sample.getId(), null,
                    null, false);

            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();

            pathgenicList = settingPathogenicityList(list, "P");

            likelyPathgenicList = settingPathogenicityList(list, "LP");

            uncertainSignificanceList = settingPathogenicityList(list, "US");

            likelyBenignList = settingPathogenicityList(list, "LB");

            benignList = settingPathogenicityList(list, "B");

            orderingAndAddTableItem(pathogenicVariantsTable, pathgenicList);

            orderingAndAddTableItem(likelyPathogenicVariantsTable, likelyPathgenicList);

            orderingAndAddTableItem(uncertainSignificanceVariantsTable, uncertainSignificanceList);

            if(sample.getSampleStatus().getReportStartedAt() != null) {
                response = apiService.get("sampleReport/" + sample.getId(), null, null, false);

                if (response.getStatus() >= 200 && response.getStatus() <= 300) {
                    reportData = true;
                    SampleReport sampleReport = response.getObjectBeforeConvertResponseToJSON(SampleReport.class);
                    //settingReportData(sampleReport.getContents());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void settingTableViewDragAndDrop(TableView<VariantAndInterpretationEvidence> tableView, String pathogenicity) {
        if(!StringUtils.isEmpty(pathogenicity) && tableView != null) {
            tableView.setOnDragDetected(e -> onDragDetected(e, tableView));
            tableView.setOnDragDone(e -> onDragDone(e));
            tableView.setOnDragOver(e -> onDragOver(e, tableView));
            tableView.setOnDragExited(e -> onDragExited(e, tableView));
            tableView.setOnDragDropped(e -> onDragDropped(e, tableView, pathogenicity));

            tableView.setRowFactory(tv -> {
                TableRow<VariantAndInterpretationEvidence> rowData = new TableRow<>();
                rowData.setOnDragDetected(e -> rowItem = rowData);
                return rowData;
            });
        }
    }

    public void orderingAndAddTableItem(TableView<VariantAndInterpretationEvidence> tableView,
                                        List<VariantAndInterpretationEvidence> pathogenicityList) {
        if(pathogenicityList != null && !pathogenicityList.isEmpty()) {
            Collections.sort(pathogenicityList,
                    (a, b) -> a.getSnpInDel().getSequenceInfo().getGene().compareTo(b.getSnpInDel().getSequenceInfo().getGene()));
            tableView.getItems().addAll(pathogenicityList);
        }
    }

    public List<VariantAndInterpretationEvidence> settingPathogenicityList(List<VariantAndInterpretationEvidence> allTierList,
                                                                           String pathogenicity) {
        if(!StringUtils.isEmpty(pathogenicity)) {
            return allTierList.stream().filter(item -> ((pathogenicity.equalsIgnoreCase(item.getSnpInDel().getExpertPathogenicityLevel()) ||
                    (StringUtils.isEmpty(item.getSnpInDel().getExpertPathogenicityLevel())
                            && item.getSnpInDel().getSwPathogenicityLevel().equalsIgnoreCase(pathogenicity)))))
                    .collect(Collectors.toList());
        }

        return null;
    }

    @FXML
    public void save() {

    }

    @FXML
    public void createPDFAsDraft() {

    }

    @FXML
    public void createPDFAsFinal() {

    }

    @FXML
    public void confirmPDFAsFinal() {

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
        System.out.println("change Pathogenicity");
        if(selectedItem != null && selectedTable != table) {
            /*try {
                FXMLLoader loader = mainApp.load(FXMLConstants.CHANGE_TIER);
                Node root = loader.load();
                ChangeTierDialogController changeTierDialogController = loader.getController();
                changeTierDialogController.setMainController(this.getMainController());
                changeTierDialogController.setAnalysisDetailReportController(this);
                changeTierDialogController.settingItem(table, tier, selectedItem, rowItem);
                changeTierDialogController.show((Parent) root);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }*/

        }
        t.setDropCompleted(true);
    }

    public void resetData(TableView<VariantAndInterpretationEvidence> table) {
        selectedTable.getItems().remove(selectedItem);
        table.getItems().add(selectedItem);
        selectedItem = null;
        selectedTable = null;
        rowItem = null;
    }

    public void selectClear(String pathogenicity) {
        if(pathogenicity.equals("P")) {
            pathogenicVariantsTable.getSelectionModel().clearSelection();
        } else if(pathogenicity.equals("LP")) {
            likelyPathogenicVariantsTable.getSelectionModel().clearSelection();
        } else if(pathogenicity.equals("US")) {
            uncertainSignificanceVariantsTable.getSelectionModel().clearSelection();
        }
    }


}
