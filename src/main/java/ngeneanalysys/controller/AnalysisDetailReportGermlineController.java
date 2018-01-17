package ngeneanalysys.controller;

import javafx.beans.property.SimpleBooleanProperty;
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
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedVariantAndInterpretationEvidence;
import ngeneanalysys.model.render.DatepickerConverter;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.PDFCreateService;
import ngeneanalysys.task.ImageFileDownloadTask;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    List<VariantAndInterpretationEvidence> pathogenicList = null;

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

        tableCellUpdateFix(pathogenicVariantsTable);

        tableCellUpdateFix(likelyPathogenicVariantsTable);

        tableCellUpdateFix(uncertainSignificanceVariantsTable);

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

        pathogenicGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        pathogenicVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        pathogenicExceptColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        pathogenicExceptColumn.setCellFactory(param -> new ReportedCheckBox(this));

        likelyPathogenicGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        likelyPathogenicVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        likelyPathogenicExceptColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        likelyPathogenicExceptColumn.setCellFactory(param -> new ReportedCheckBox(this));

        uncertainSignificanceGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        uncertainSignificanceVariantsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        uncertainSignificanceExceptColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        uncertainSignificanceExceptColumn.setCellFactory(param -> new ReportedCheckBox(this));

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

    }

    public void createdStandardBRCAColumn() {
        int gridPaneRowSize = 3;

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

        String[] displayNameList =  {"Test No", "Test Name", "Organization", "Ordering Physician", "Contact", "Name", "Birthday", "Gender"};
        String[] variableNameList =  {"manageNo", "inspectionItem", "clientOrganization", "clientName", "clientContact", "name",
                "patientBirthday", "patientGender"};

        for(int i = 0; i < displayNameList.length ; i++) {
            String displayName = displayNameList[i];
            String variableName = variableNameList[i];

            if(colIndex == 6) {
                colIndex = 0;
                rowIndex++;
            }
            Label label = new Label(displayName);
            label.setStyle("-fx-text-fill : #C20E20;");
            customFieldGridPane.add(label, colIndex++, rowIndex);
            label.setMaxHeight(Double.MAX_VALUE);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);

            if(displayName.equalsIgnoreCase("Birthday")) {
                DatePicker datePicker = new DatePicker();
                datePicker.setStyle(datePicker.getStyle() + "-fx-text-inner-color: black; -fx-control-inner-background: white;");
                String dateType = "yyyy-MM-dd";
                datePicker.setPromptText(dateType);
                datePicker.setConverter(DatepickerConverter.getConverter(dateType));
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

    public void setVariantsList() {
        HttpClientResponse response = null;
        try {
            response = apiService.get("/analysisResults/sampleSnpInDels/" + sample.getId(), null,
                    null, false);

            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();

            pathogenicList = settingPathogenicityList(list, "P");

            likelyPathgenicList = settingPathogenicityList(list, "LP");

            uncertainSignificanceList = settingPathogenicityList(list, "US");

            likelyBenignList = settingPathogenicityList(list, "LB");

            benignList = settingPathogenicityList(list, "B");

            orderingAndAddTableItem(pathogenicVariantsTable, pathogenicList);

            orderingAndAddTableItem(likelyPathogenicVariantsTable, likelyPathgenicList);

            orderingAndAddTableItem(uncertainSignificanceVariantsTable, uncertainSignificanceList);
        } catch (WebAPIException wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getContents(), this.getMainApp().getPrimaryStage(), true);
        }
    }

    public void tableCellUpdateFix(TableView<VariantAndInterpretationEvidence> tableView) {
        tableView.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            tableView.refresh();
            // close text box
            tableView.edit(-1, null);
        });
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

            if(tableView.getItems() != null && !tableView.getItems().isEmpty()) {
                tableView.getItems().removeAll(tableView.getItems());
            }

            Collections.sort(pathogenicityList,
                    (a, b) -> a.getSnpInDel().getGenomicCoordinate().getGene().compareTo(b.getSnpInDel().getGenomicCoordinate().getGene()));
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
    /**
     * 보고서 작업 완료 처리
     */
    public void setComplete() {
    }

    @FXML
    public void confirmPDFAsFinal() {

    }

    public boolean createPDF(boolean isDraft) {
        boolean created = true;

        // 보고서 파일명 기본값
        String baseSaveName = String.format("FINAL_Report_%s", sample.getName());
        if(isDraft) {
            baseSaveName = String.format("DRAFT_Report_%s", sample.getName());
        }

        // Show save file dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName(baseSaveName);
        File file = fileChooser.showSaveDialog(this.getMainApp().getPrimaryStage());

        try {
            if(file != null) {
                String draftImageStr = String.format("url('%s')", this.getClass().getClassLoader().getResource("layout/images/DRAFT.png"));
                String ngenebioLogo = String.format("%s", this.getClass().getClassLoader().getResource("layout/images/ngenebio_logo_small.png"));
                SecureRandom random = new SecureRandom();
                Map<String,Object> contentsMap = new HashMap<>();
                contentsMap.put("panelName", panel.getName());
                contentsMap.put("panelCode", panel.getCode());
                contentsMap.put("patientID", sample.getPaitentId());
                //리포트를 생성할 때마다 고유 ID 부여 report ID + random Int
                contentsMap.put("reportingDate" , new Date().toString());
                contentsMap.put("reportID", String.format("%05d-%05d", sample.getId(), random.nextInt(99999)));

                contentsMap.put("inspectorOrganization", "");
                contentsMap.put("inspectorName", "");
                contentsMap.put("inspectorContact", "");
                contentsMap.put("reportingDate", "");
                if(!isDraft) {
                    HttpClientResponse response = apiService.get("/members/" + loginSession.getId(), null, null,
                            false);
                    User user = response.getObjectBeforeConvertResponseToJSON(User.class);
                    contentsMap.put("inspectorOrganization", user.getOrganization() + "/" + user.getDepartment());
                    contentsMap.put("inspectorName", user.getName());
                    contentsMap.put("inspectorContact", user.getPhone());
                    if((sample.getSampleStatus() != null) && sample.getSampleStatus().getReportFinishedAt() != null) {
                        contentsMap.put("reportingDate", sample.getSampleStatus().getReportFinishedAt());
                    }
                }

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

                List<VariantAndInterpretationEvidence> list = new ArrayList<>();
                if(pathogenicVariantsTable.getItems() != null
                        && !pathogenicVariantsTable.getItems().isEmpty()) list.addAll(pathogenicVariantsTable.getItems());
                if(likelyPathogenicVariantsTable.getItems() != null
                        && !likelyPathogenicVariantsTable.getItems().isEmpty()) list.addAll(likelyPathogenicVariantsTable.getItems());
                if(uncertainSignificanceVariantsTable.getItems() != null
                        && !uncertainSignificanceVariantsTable.getItems().isEmpty()) list.addAll(uncertainSignificanceVariantsTable.getItems());
                if(likelyBenignList != null && !likelyBenignList.isEmpty()) list.addAll(likelyBenignList);
                if(benignList != null && !benignList.isEmpty()) list.addAll(benignList);

                /*if(!list.isEmpty())
                    list = list.stream().filter(item -> item.getSnpInDel().getIncludedInReport().equalsIgnoreCase("Y")).collect(Collectors.toList());*/

                if(list.stream().anyMatch(item -> "Y".equalsIgnoreCase(item.getSnpInDel().getIncludedInReport()))) contentsMap.put("isExistReportedVariant", "Y");

                if(list.stream().anyMatch(item -> "BRCA1".equalsIgnoreCase(item.getSnpInDel().getGenomicCoordinate().getGene()))) {
                    contentsMap.put("isExistBRCA1", "Y");
                }

                if(list.stream().anyMatch(item -> "BRCA2".equalsIgnoreCase(item.getSnpInDel().getGenomicCoordinate().getGene()))) {
                    contentsMap.put("isExistBRCA2", "Y");
                }

                if(list != null && !list.isEmpty()) {
                    for(VariantAndInterpretationEvidence variant : list){
                        variant.getSnpInDel().getSnpInDelExpression().setTranscript(ConvertUtil.insertTextAtFixedPosition(variant.getSnpInDel().getSnpInDelExpression().getTranscript(), 15, "\n"));
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
                    if(lines != null && lines.length > 0) {
                        for (String line : lines) {
                            conclusionLineList.add(line);
                        }
                    } else {
                        conclusionLineList.add(conclusionsTextArea.getText());
                    }
                }
                contentsMap.put("conclusion", conclusionLineList);

                Map<String,Object> model = new HashMap<>();
                model.put("isDraft", isDraft);
                model.put("qcResult", sample.getQcResult());
                model.put("draftImageURL", draftImageStr);
                model.put("ngenebioLogo", ngenebioLogo);
                model.put("contents", contentsMap);

                // 템플릿에 데이터 바인딩하여 pdf 생성 스크립트 생성
                String contents = null;
                if(panel.getReportTemplateId() == null) {
                    contents = velocityUtil.getContents("/layout/velocity/report_brca.vm", "UTF-8", model);
                    created = pdfCreateService.createPDF(file, contents);
                    createdCheck(created, file);
                } else {
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
                            final boolean created1 = pdfCreateService.createPDF(file, contents1);
                            createdCheck(created1, file);
                        } catch (Exception e) {
                            DialogUtil.error("Save Fail.", "An error occurred during the creation of the report document.", getMainApp().getPrimaryStage(), false);
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch(FileNotFoundException fnfe){
            DialogUtil.error("Save Fail.", fnfe.getMessage(), getMainApp().getPrimaryStage(), false);
        } catch (Exception e) {
            DialogUtil.error("Save Fail.", "An error occurred during the creation of the report document.", getMainApp().getPrimaryStage(), false);
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

    private class ReportedCheckBox extends TableCell<VariantAndInterpretationEvidence, Boolean> {
        HBox box = null;
        final CheckBox checkBox = new CheckBox();

        ReportedCheckBox(AnalysisDetailReportGermlineController analysisDetailReportGermlineController) {
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
        System.out.println("change Pathogenicity " + tier);
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

}
