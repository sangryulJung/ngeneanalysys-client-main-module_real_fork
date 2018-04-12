package ngeneanalysys.controller.fragment;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.AnalysisDetailSNVController;
import ngeneanalysys.controller.ExcludeReportDialogController;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedSameVariantInterpretation;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2018-03-19
 */
public class AnalysisDetailInterpretationController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private Label swTierLabel;
    @FXML
    private Label userTierLabel;

    @FXML
    private Label arrow;

    @FXML
    private CheckBox addToReportCheckBox;

    @FXML
    private TableView<SnpInDelEvidence> evidenceTableView;

    @FXML
    private TableColumn<SnpInDelEvidence, String> evidenceTypeColumn;

    @FXML
    private TableColumn<SnpInDelEvidence, String> evidenceColumn;

    @FXML
    private TableColumn<SnpInDelEvidence, String> evidenceCommentColumn;

    @FXML
    private TableColumn<SnpInDelEvidence, Boolean> evidencePrimaryColumn;

    @FXML
    private TableColumn<SnpInDelEvidence, Boolean> evidenceSaveColumn;

    @FXML
    private TableColumn<SnpInDelEvidence, Boolean> evidenceDeleteColumn;

    @FXML
    private TableView<SameVariantInterpretation> pastCasesTableView;

    @FXML
    private TableColumn<SameVariantInterpretation, String> pastCasesSampleColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> pastCasesTypeColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> pastCasesEvidenceColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> pastCasesInterpretationColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> pastCasesEvidenceCommentColumn;

    @FXML
    private TableColumn<SameVariantInterpretation, String> pastCasesDateColumn;

    @FXML
    private TableView<SnpInDelPrimaryEvidenceLog> interpretationTableView;

    @FXML
    private TableColumn<SnpInDelPrimaryEvidenceLog, String> interpretationTypeColumn;

    @FXML
    private TableColumn<SnpInDelPrimaryEvidenceLog, String> interpretationEvidenceColumn;

    @FXML
    private TableColumn<SnpInDelPrimaryEvidenceLog, String> interpretationEvidenceCommentColumn;

    @FXML
    private TableColumn<SnpInDelPrimaryEvidenceLog, String> interpretationDateColumn;

    @FXML
    private GridPane interpretationGridPane;

    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;

    private APIService apiService;

    private AnalysisDetailSNVController analysisDetailSNVController;

    private Sample sample;

    private Panel panel;

    /**
     * @param analysisDetailSNVController
     */
    public void setAnalysisDetailSNVController(AnalysisDetailSNVController analysisDetailSNVController) {
        this.analysisDetailSNVController = analysisDetailSNVController;
    }

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();
        sample = (Sample)getParamMap().get("sample");
        panel = (Panel)getParamMap().get("panel");
        selectedAnalysisResultVariant = (VariantAndInterpretationEvidence)paramMap.get("variant");

        if(StringUtils.isEmpty(selectedAnalysisResultVariant.getSnpInDel().getExpertTier())) arrow.setVisible(false);
        addToReportCheckBox.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> addToReportBtn(addToReportCheckBox ));
        checkBoxSetting(addToReportCheckBox, selectedAnalysisResultVariant.getSnpInDel().getIncludedInReport());

        evidenceTableView.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            evidenceTableView.refresh();
            // close text box
            evidenceTableView.edit(-1, null);
        });

        ///////////////////////////////////////////////////
        evidenceTypeColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getEvidenceType()));
        evidenceTypeColumn.setCellFactory(item -> new TypeComboBoxCell());

        evidenceColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getEvidenceLevel()));
        evidenceColumn.setCellFactory(item -> new EvidenceLevelComboBoxCell());

        evidencePrimaryColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getPrimaryEvidence()));
        evidencePrimaryColumn.setCellFactory(item -> new PrimaryRadioButtonCell());

        evidenceCommentColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getEvidence()));
        evidenceCommentColumn.setCellFactory(tableColumn -> new EditingCell());
        evidenceCommentColumn.setOnEditCommit((TableColumn.CellEditEvent<SnpInDelEvidence, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setEvidence(t.getNewValue());
        });

        evidenceSaveColumn.setSortable(false);
        evidenceSaveColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        evidenceSaveColumn.setCellFactory(param -> new SaveButtonCreate());

        evidenceDeleteColumn.setSortable(false);
        evidenceDeleteColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        evidenceDeleteColumn.setCellFactory(param -> new DeleteButtonCreate());

        //////////////////////////////////////////////////

        interpretationTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvidenceType()));
        interpretationEvidenceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvidenceLevel()));
        interpretationEvidenceCommentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvidence()));
        interpretationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DateFormatUtils.format(cellData.getValue().getCreatedAt().toDate(), "yyyy-MM-dd HH:mm:ss")));

        pastCasesSampleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSampleName()));
        pastCasesTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDelEvidence() != null ? cellData.getValue().getSnpInDelEvidence().getEvidenceType() : ""));
        pastCasesEvidenceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDelEvidence() != null ? cellData.getValue().getSnpInDelEvidence().getEvidenceLevel() : ""));
        pastCasesInterpretationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTier()));
        pastCasesEvidenceCommentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDelEvidence() != null ? cellData.getValue().getSnpInDelEvidence().getEvidence() : ""));
        pastCasesDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDelEvidence() != null ? DateFormatUtils.format(cellData.getValue().getSnpInDelEvidence().getCreatedAt().toDate(), "yyyy-MM-dd hh:mm:ss") : ""));

        setEvidenceTable();
        setInterpretationTable();
        setPastCases();
        setTier(selectedAnalysisResultVariant.getSnpInDel());

    }

    private class SaveButtonCreate extends TableCell<SnpInDelEvidence, Boolean> {
        HBox box = null;
        final ImageView img = new ImageView(resourceUtil.getImage("/layout/images/renewal/save_icon.png", 16, 16));

        public SaveButtonCreate() {
            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                /*Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                String alertHeaderText = null;
                String alertContentText = "Are you sure to restart this run?";

                alert.setTitle("Confirmation Dialog");
                SnpInDelEvidence run = SaveButtonCreate.this.getTableView().getItems().get(
                        SaveButtonCreate.this.getIndex());
                alert.setHeaderText(run.getName());
                alert.setContentText(alertContentText);
                logger.info(run.getId() + " : present id");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK) {
                    restartRun(run.getId());
                } else {
                    logger.info(result.get() + " : button select");
                    alert.close();
                }*/

                SnpInDelEvidence snpInDelEvidence = SaveButtonCreate.this.getTableView().getItems().get(
                        SaveButtonCreate.this.getIndex());

                save(snpInDelEvidence);
            });
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);

            if(item == null) {
                setGraphic(null);
                return;
            }
            img.setStyle("-fx-cursor:hand;");
            box = new HBox();
            box.setAlignment(Pos.CENTER);
            box.getChildren().add(img);

            setGraphic(box);
        }
    }

    class EditingCell extends TableCell<SnpInDelEvidence, String> {
        private TextField textField = null;

        public EditingCell() {}

        @Override
        public void startEdit() {
            if(!isEmpty()) {
                super.startEdit();
                createTextField();
                setGraphic(textField);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(getItem());
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if(empty) {
                setText(null);
                setGraphic(null);
            } else {
                if(isEditing()) {
                    if(textField != null) {
                        textField.setText(getString());
                    }
                    setGraphic(textField);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                } else {
                    setText(getString());
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }

        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.getStyleClass().add("txt_black");
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            SnpInDelEvidence variant = this.getTableView().getItems().get(this.getTableRow().getIndex());
            textField.setOnKeyPressed(t -> {
                if(t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());
                    //addModifiedList(variant);
                } else if (t.getCode() == KeyCode.TAB) {
                    commitEdit(textField.getText());
                    //addModifiedList(variant);
                } else if(t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
            textField.focusedProperty().addListener((arg0, arg1, arg2) -> {
                if (!arg2) {
                    commitEdit(textField.getText());
                    //addModifiedList(variant);
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

    private class TypeComboBoxCell extends TableCell<SnpInDelEvidence, String> {
        private ComboBox<String> comboBox = new ComboBox<>();
        boolean setting = false;

        public TypeComboBoxCell() {
            comboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
                SnpInDelEvidence snpInDelEvidence = TypeComboBoxCell.this.getTableView().getItems().get(
                        TypeComboBoxCell.this.getIndex());

                if(!StringUtils.isEmpty(t1)) {
                    snpInDelEvidence.setEvidenceType(t1);
                }
            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if(empty) {
                setGraphic(null);
                return;
            }

            if(comboBox.getItems().isEmpty()) {

                if(panel.getAnalysisType().equals("SOMATIC")) {
                    comboBox.getItems().addAll("therapeutic", "prognosis", "diagnosis");
                }
                comboBox.getSelectionModel().selectFirst();

                SnpInDelEvidence evidence = TypeComboBoxCell.this.getTableView().getItems().get(
                        TypeComboBoxCell.this.getIndex());

                if(evidence != null && !StringUtils.isEmpty(evidence.getEvidenceType())) {
                    comboBox.getSelectionModel().select(evidence.getEvidenceType());
                }
            }
            setGraphic(comboBox);

        }

    }

    private class EvidenceLevelComboBoxCell extends TableCell<SnpInDelEvidence, String> {
        private ComboBox<String> comboBox = new ComboBox<>();
        boolean setting = false;

        public EvidenceLevelComboBoxCell() {
            comboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
                SnpInDelEvidence snpInDelEvidence = EvidenceLevelComboBoxCell.this.getTableView().getItems().get(
                        EvidenceLevelComboBoxCell.this.getIndex());

                if(!StringUtils.isEmpty(t1)) {
                    snpInDelEvidence.setEvidenceLevel(t1);
                }
            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if(empty) {
                setGraphic(null);
                return;
            }

            if(comboBox.getItems().isEmpty()) {

                if(panel.getAnalysisType().equals("SOMATIC")) {
                    comboBox.getItems().addAll("A", "B", "C", "D");
                }
                comboBox.getSelectionModel().selectFirst();

                SnpInDelEvidence evidence = EvidenceLevelComboBoxCell.this.getTableView().getItems().get(
                        EvidenceLevelComboBoxCell.this.getIndex());

                if(evidence != null && !StringUtils.isEmpty(evidence.getEvidenceLevel())) {
                    comboBox.getSelectionModel().select(evidence.getEvidenceLevel());
                }

            }
            setGraphic(comboBox);

        }

    }

    private class PrimaryRadioButtonCell extends TableCell<SnpInDelEvidence, Boolean> {
        private RadioButton radioButton = new RadioButton();
        boolean setting = false;

        public PrimaryRadioButtonCell() {
            radioButton.selectedProperty().addListener((ob, ov, nv) -> {
                if(PrimaryRadioButtonCell.this.getIndex() != -1 && PrimaryRadioButtonCell.this.getIndex() < evidenceTableView.getItems().size()) {
                    SnpInDelEvidence snpInDelEvidence = PrimaryRadioButtonCell.this.getTableView().getItems().get(
                            PrimaryRadioButtonCell.this.getIndex());
                    snpInDelEvidence.setPrimaryEvidence(nv);
                }
            });

            radioButton.setToggleGroup(toggleGroup);
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(empty) {
                setGraphic(null);
                return;
            }

            SnpInDelEvidence evidence = PrimaryRadioButtonCell.this.getTableView().getItems().get(
                    PrimaryRadioButtonCell.this.getIndex());

            if(evidence != null && evidence.getPrimaryEvidence() != null && evidence.getPrimaryEvidence() &&
                    !radioButton.isSelected()) {
                toggleGroup.selectToggle(radioButton);
            }

            setGraphic(radioButton);
        }
    }

    /** 삭제 버튼 생성 */
    private class DeleteButtonCreate extends TableCell<SnpInDelEvidence, Boolean> {
        HBox box = null;
        final ImageView img = new ImageView(resourceUtil.getImage("/layout/images/renewal/delete_icon.png", 16, 16));

        public DeleteButtonCreate() {
            img.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                /*Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                String alertHeaderText = null;
                String alertContentText = "Are you sure to delete this run?";

                alert.setTitle("Confirmation Dialog");
                Run run = DeleteButtonCreate.this.getTableView().getItems().get(
                        DeleteButtonCreate.this.getIndex());
                alert.setHeaderText(run.getName());
                alert.setContentText(alertContentText);
                logger.info(run.getId() + " : present id");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK) {
                    deleteRun(run.getId());
                } else {
                    logger.info(result.get() + " : button select");
                    alert.close();
                }*/
                SnpInDelEvidence evidence = DeleteButtonCreate.this.getTableView().getItems().get(
                        DeleteButtonCreate.this.getIndex());

                delete(evidence);
            });
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);

            if(item == null) {
                setGraphic(null);
                return;
            }
            img.setStyle("-fx-cursor:hand;");
            box = new HBox();
            box.setAlignment(Pos.CENTER);
            box.getChildren().add(img);

            setGraphic(box);
        }
    }


    public void setGridPaneWidth(double size) {
        interpretationGridPane.setPrefWidth(size);
    }

    public void setEvidenceTable() {
        if(evidenceTableView.getItems() != null) evidenceTableView.getItems().removeAll(evidenceTableView.getItems());
        try {

            // Memo 데이터 API 요청
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("snpInDelId", selectedAnalysisResultVariant.getSnpInDel().getId());
            HttpClientResponse response = apiService.get("/analysisResults/evidences", paramMap, null, false);

            // Flagging Comment 데이터 요청이 정상 요청된 경우 진행.
            List<SnpInDelEvidence> list = (List<SnpInDelEvidence>)response.getMultiObjectBeforeConvertResponseToJSON(SnpInDelEvidence.class, false);
            if(list != null && !list.isEmpty()) evidenceTableView.getItems().addAll(FXCollections.observableArrayList(list));

        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    public void setInterpretationTable() {
        if(interpretationTableView.getItems() != null) interpretationTableView.getItems().removeAll(interpretationTableView.getItems());
        try {

            // Memo 데이터 API 요청
            //Map<String, Object> commentParamMap = new HashMap<>();
            HttpClientResponse responseMemo = apiService.get("/analysisResults/evidenceLog/" + selectedAnalysisResultVariant.getSnpInDel().getId()
                    , null, null, false);

            // Flagging Comment 데이터 요청이 정상 요청된 경우 진행.
            List<SnpInDelPrimaryEvidenceLog> memoList = (List<SnpInDelPrimaryEvidenceLog>)responseMemo.getMultiObjectBeforeConvertResponseToJSON(SnpInDelPrimaryEvidenceLog.class, false);
            if(memoList != null && !memoList.isEmpty()) interpretationTableView.getItems().addAll(FXCollections.observableArrayList(memoList));

        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    public void setPastCases() {
        if(pastCasesTableView.getItems() != null) pastCasesTableView.getItems().removeAll(pastCasesTableView.getItems());
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("chromosome", selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome());
            params.put("gene", selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getGene());
            params.put("ntChange", selectedAnalysisResultVariant.getSnpInDel().getSnpInDelExpression().getNtChange());

            HttpClientResponse response = apiService.get("/analysisResults/sameVariantInterpretations/" + selectedAnalysisResultVariant.getSnpInDel().getSampleId()
                    , params, null, false);

            List<SameVariantInterpretation> sameList = (List<SameVariantInterpretation>)response.getMultiObjectBeforeConvertResponseToJSON(SameVariantInterpretation.class, false);

            if( sameList != null && !sameList.isEmpty()) {
                pastCasesTableView.getItems().addAll(FXCollections.observableArrayList(sameList));
            }

        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    public void addToReportBtn(CheckBox checkBox) {
        if(selectedAnalysisResultVariant != null) {
            String oldSymbol = selectedAnalysisResultVariant.getSnpInDel().getIncludedInReport();
            if (checkBox.isSelected()) {
                try {
                    FXMLLoader loader = mainApp.load(FXMLConstants.EXCLUDE_REPORT);
                    Node node = loader.load();
                    ExcludeReportDialogController excludeReportDialogController = loader.getController();
                    excludeReportDialogController.setMainController(mainController);
                    excludeReportDialogController.settingItem("Y", selectedAnalysisResultVariant, checkBox);
                    excludeReportDialogController.show((Parent) node);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                if(!oldSymbol.equals(selectedAnalysisResultVariant.getSnpInDel().getIncludedInReport()))
                    analysisDetailSNVController.showVariantList(analysisDetailSNVController.getCurrentPageIndex() + 1, 0);
            } else {
                try {
                    FXMLLoader loader = mainApp.load(FXMLConstants.EXCLUDE_REPORT);
                    Node node = loader.load();
                    ExcludeReportDialogController excludeReportDialogController = loader.getController();
                    excludeReportDialogController.setMainController(mainController);
                    excludeReportDialogController.settingItem("N", selectedAnalysisResultVariant, checkBox);
                    excludeReportDialogController.show((Parent) node);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            if(!oldSymbol.equals(selectedAnalysisResultVariant.getSnpInDel().getIncludedInReport()))
                analysisDetailSNVController.showVariantList(analysisDetailSNVController.getCurrentPageIndex() + 1, 0);
        }
    }

    public void checkBoxSetting(CheckBox checkBox, String Symbol) {
        if("Y".equals(Symbol)) {
            checkBox.setSelected(true);
        } else {
            checkBox.setSelected(false);
        }
    }

    public void returnTierClass(String tier, Label label, Integer usertier) {
        label.setAlignment(Pos.CENTER);
        logger.info("+++++++++++++++");
        logger.info(userTierLabel.getText()+" "+tier+" "+usertier);
        if(usertier == 2 && tier != null) {arrow.setVisible(true);}
        //else {arrow.setVisible(true);}
        //if(label == userTierLabel) arrow.setVisible(true);

        label.getStyleClass().removeAll(label.getStyleClass());
        if(!StringUtils.isEmpty(tier)) {
            if (tier.equalsIgnoreCase("T1")) {
                label.setText("Tier 1");
                label.getStyleClass().add("tier_full");
                //label.getStyleClass().add("tier_one");
            } else if (tier.equalsIgnoreCase("T2")) {
                label.setText("Tier 2");
                label.getStyleClass().add("tier_full");
                //label.getStyleClass().add("tier_two");
            } else if (tier.equalsIgnoreCase("T3")) {
                label.setText("Tier 3");
                label.getStyleClass().add("tier_full");
                //label.getStyleClass().add("tier_three");
            } else if (tier.equalsIgnoreCase("T4")) {
                label.setText("Tier 4");
                label.getStyleClass().add("tier_full");
                //label.getStyleClass().add("tier_four");
            }
        }
    }



    @FXML
    public void saveInterpretation() {
        SnpInDelEvidence snpInDelEvidence = new SnpInDelEvidence();
        evidenceTableView.getItems().add(snpInDelEvidence);
       /*
        params.put("snpInDelInterpretation", snpInDelInterpretation);

        if(!StringUtils.isEmpty(comment)) {
            params.put("comment", comment);
            try {
                params.put("tier", tier);

                params.put("snpInDelId", variantAndInterpretationEvidence.getSnpInDel().getId());

                apiService.put("analysisResults/snpInDels/" + variantAndInterpretationEvidence.getSnpInDel().getId() + "/updateTier", params, null, true);
                returnTierClass(tier, userTierLabel);
                analysisDetailSNVController.showVariantList(analysisDetailSNVController.getCurrentPageIndex() + 1, 0);
            } catch (WebAPIException wae) {
                wae.printStackTrace();
                DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
            }

        } else {
                DialogUtil.warning("The comment field is empty", " ", mainController.getPrimaryStage(), true);
            commentTextField.requestFocus();
        }*/
    }

    public void setTier(SnpInDel snpInDel) {
        returnTierClass(snpInDel.getSwTier(), swTierLabel,1);
        returnTierClass(snpInDel.getExpertTier(), userTierLabel,2);
    }

    public void save(SnpInDelEvidence snpInDelEvidence) {
        Map<String, Object> params = new HashMap<>();
        params.put("snpInDelId", selectedAnalysisResultVariant.getSnpInDel().getId());
        params.put("id", snpInDelEvidence.getId() == null ? 0 : snpInDelEvidence.getId());
        params.put("evidence", snpInDelEvidence.getEvidence());
        params.put("evidenceType", snpInDelEvidence.getEvidenceType());
        params.put("evidenceLevel", snpInDelEvidence.getEvidenceLevel());
        params.put("evidence", snpInDelEvidence.getEvidence());
        params.put("primaryEvidence", snpInDelEvidence.getPrimaryEvidence() == null ? false : snpInDelEvidence.getPrimaryEvidence());
        params.put("createdAt", ConvertUtil.convertLocalTimeToUTC("00-00-00 00:00:00", "yyyy-MM-dd HH:mm:ss", null));

        try {
            if(snpInDelEvidence.getId() == null) {
                apiService.post("analysisResults/evidences", params, null, true);
            } else {
                params.put("id", snpInDelEvidence.getId());
                apiService.put("analysisResults/evidences", params, null, true);
            }
            setInterpretationTable();
            setPastCases();
            setEvidenceTable();
            analysisDetailSNVController.showVariantList(analysisDetailSNVController.getCurrentPageIndex() + 1, 0);
        } catch (WebAPIException wae) {
            wae.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void delete(SnpInDelEvidence snpInDelEvidence) {
        try {

            if(snpInDelEvidence.getId() != null) {
                logger.info("server");
                apiService.delete("analysisResults/evidences/" + snpInDelEvidence.getId());
                setInterpretationTable();
                setPastCases();
                setEvidenceTable();
            } else {
                evidenceTableView.getItems().remove(snpInDelEvidence);
            }
        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
    }


}
