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
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2018-03-19
 */
public class AnalysisDetailInterpretationController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private Label swTierLabel;
    @FXML
    private Label userTierLabel;
    @FXML
    private Label arrow;

    @FXML
    private Button saveBtn;

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
    private TableView<SnpInDelEvidence> interpretationTableView;

    @FXML
    private TableColumn<SnpInDelEvidence, String> interpretationTypeColumn;

    @FXML
    private TableColumn<SnpInDelEvidence, String> interpretationStatusColumn;

    @FXML
    private TableColumn<SnpInDelEvidence, String> interpretationEvidenceColumn;

    @FXML
    private TableColumn<SnpInDelEvidence, String> interpretationEvidenceCommentColumn;

    @FXML
    private TableColumn<SnpInDelEvidence, String> interpretationDateColumn;

    @FXML
    private GridPane interpretationGridPane;

    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;

    private APIService apiService;

    private AnalysisDetailSNVController analysisDetailSNVController;

    private Panel panel;

    /**
     * @param analysisDetailSNVController AnalysisDetailSNVController
     */
    public void setAnalysisDetailSNVController(AnalysisDetailSNVController analysisDetailSNVController) {
        this.analysisDetailSNVController = analysisDetailSNVController;
    }

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();
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
        evidenceCommentColumn.setOnEditCommit((TableColumn.CellEditEvent<SnpInDelEvidence, String> t) ->
                (t.getTableView().getItems().get(t.getTablePosition().getRow())).setEvidence(t.getNewValue()));

        evidenceDeleteColumn.setSortable(false);
        evidenceDeleteColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
        evidenceDeleteColumn.setCellFactory(param -> new DeleteButtonCreate());

        //////////////////////////////////////////////////

        interpretationTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvidenceType()));
        interpretationEvidenceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvidenceLevel()));
        interpretationEvidenceCommentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvidence()));
        interpretationStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        interpretationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(DateFormatUtils
                .format(cellData.getValue().getCreatedAt().toDate(), "yyyy-MM-dd HH:mm:ss")));

        pastCasesSampleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSampleName()));
        pastCasesTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDelEvidence()
                != null ? cellData.getValue().getSnpInDelEvidence().getEvidenceType() : ""));
        pastCasesEvidenceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDelEvidence()
                != null ? cellData.getValue().getSnpInDelEvidence().getEvidenceLevel() : ""));
        pastCasesInterpretationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTier()));
        pastCasesEvidenceCommentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .getSnpInDelEvidence() != null ? cellData.getValue().getSnpInDelEvidence().getEvidence() : ""));
        pastCasesDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDelEvidence()
                != null ? DateFormatUtils.format(cellData.getValue().getSnpInDelEvidence().getCreatedAt().toDate(), "yyyy-MM-dd hh:mm:ss") : ""));

        setEvidenceTable();
        setPastCases();
        setTier(selectedAnalysisResultVariant.getSnpInDel());
    }

    class EditingCell extends TableCell<SnpInDelEvidence, String> {
        private TextField textField = null;

        private EditingCell() {}

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
            textField.setOnKeyPressed(t -> {
                if(t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());

                } else if (t.getCode() == KeyCode.TAB) {
                    commitEdit(textField.getText());

                } else if(t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
            textField.focusedProperty().addListener((arg0, arg1, arg2) -> {
                if (!arg2) {
                    commitEdit(textField.getText());
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }

    private class TypeComboBoxCell extends TableCell<SnpInDelEvidence, String> {
        private ComboBox<String> comboBox = new ComboBox<>();

        private TypeComboBoxCell() {
            comboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
                SnpInDelEvidence snpInDelEvidence = TypeComboBoxCell.this.getTableView().getItems().get(
                        TypeComboBoxCell.this.getIndex());

                if(StringUtils.isNotEmpty(t1)) {
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
                    comboBox.getItems().addAll("therapeutic", "prognosis", "diagnosis", "N/A");
                }

                SnpInDelEvidence evidence = TypeComboBoxCell.this.getTableView().getItems().get(
                        TypeComboBoxCell.this.getIndex());

                if(evidence != null && StringUtils.isNotEmpty(evidence.getEvidenceType())) {
                    comboBox.getSelectionModel().select(evidence.getEvidenceType());
                } else {
                    comboBox.getSelectionModel().selectFirst();
                }
            }
            setGraphic(comboBox);

        }
    }

    private class EvidenceLevelComboBoxCell extends TableCell<SnpInDelEvidence, String> {
        private ComboBox<String> comboBox = new ComboBox<>();

        private EvidenceLevelComboBoxCell() {
            comboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
                SnpInDelEvidence snpInDelEvidence = EvidenceLevelComboBoxCell.this.getTableView().getItems().get(
                        EvidenceLevelComboBoxCell.this.getIndex());

                if(StringUtils.isNotEmpty(t1)) {
                    snpInDelEvidence.setEvidenceLevel(t1);
                    if(t1.equalsIgnoreCase("T3") || t1.equalsIgnoreCase("T4")) {
                        snpInDelEvidence.setEvidenceType("N/A");
                    } else if(snpInDelEvidence.getEvidenceType().equals("N/A")){
                        snpInDelEvidence.setEvidenceType("therapeutic");
                    }
                    if(StringUtils.isNotEmpty(t) && !t.equals(t1)) {
                        evidenceTableView.refresh();
                    }
                    //evidenceTableView.refresh();
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
                    comboBox.getItems().addAll("A", "B", "C", "D", "T3", "T4");
                }

                SnpInDelEvidence evidence = EvidenceLevelComboBoxCell.this.getTableView().getItems().get(
                        EvidenceLevelComboBoxCell.this.getIndex());

                if(evidence != null && StringUtils.isNotEmpty(evidence.getEvidenceLevel())) {
                    comboBox.getSelectionModel().select(evidence.getEvidenceLevel());
                } else {
                    comboBox.getSelectionModel().selectFirst();
                }

            }
            setGraphic(comboBox);

        }

    }

    private class PrimaryRadioButtonCell extends TableCell<SnpInDelEvidence, Boolean> {
        private RadioButton radioButton = new RadioButton();

        private PrimaryRadioButtonCell() {
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

    @SuppressWarnings("unchecked")
    private void setEvidenceTable() {
        if(evidenceTableView.getItems() != null && !evidenceTableView.getItems().isEmpty()) {
            evidenceTableView.getItems().removeAll(evidenceTableView.getItems());
            evidenceTableView.refresh();
        }
        if(interpretationTableView.getItems() != null && !interpretationTableView.getItems().isEmpty()) {
            interpretationTableView.getItems().removeAll(interpretationTableView.getItems());
            interpretationTableView.refresh();
        }
        try {
            HttpClientResponse response = apiService.get("/analysisResults/snpInDels/"+
                    selectedAnalysisResultVariant.getSnpInDel().getId() + "/evidences", null, null, false);

            // Flagging Comment 데이터 요청이 정상 요청된 경우 진행.
            List<SnpInDelEvidence> list = (List<SnpInDelEvidence>)response.getMultiObjectBeforeConvertResponseToJSON(SnpInDelEvidence.class, false);
            if(list != null && !list.isEmpty()) {
                //list.sort(Comparator.comparing(SnpInDelEvidence::getId));
                Collections.sort(list, Collections.reverseOrder(Comparator.comparing(SnpInDelEvidence::getId)));

                List<SnpInDelEvidence> interpretationList = new ArrayList<>();
                interpretationList.addAll(list.stream().filter(item -> "Active".equalsIgnoreCase(item.getStatus())).collect(Collectors.toList()));

                Optional<SnpInDelEvidence> snpInDelEvidenceOptional
                        = interpretationList.stream().filter(item -> item.getPrimaryEvidence()).findFirst();

                snpInDelEvidenceOptional.ifPresent(snpInDelEvidence -> {
                    String tier = "";
                    if(snpInDelEvidence.getEvidenceLevel().equalsIgnoreCase("A")
                            || snpInDelEvidence.getEvidenceLevel().equalsIgnoreCase("B")) {
                        tier = "T1";
                    } else if(snpInDelEvidence.getEvidenceLevel().equalsIgnoreCase("C")
                            || snpInDelEvidence.getEvidenceLevel().equalsIgnoreCase("D")) {
                        tier = "T2";
                    } else if(snpInDelEvidence.getEvidenceLevel().equalsIgnoreCase("T3")) {
                        tier = "T3";
                    } else if(snpInDelEvidence.getEvidenceLevel().equalsIgnoreCase("T4")) {
                        tier = "T4";
                    }

                    returnTierClass(tier, userTierLabel ,2);
                });

                if(!interpretationList.isEmpty())
                    evidenceTableView.getItems().addAll(FXCollections.observableArrayList(interpretationList));

                interpretationTableView.getItems().addAll(FXCollections.observableArrayList(list));

            }

        } catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    @SuppressWarnings("unchecked")
    private void setPastCases() {
        if(pastCasesTableView.getItems() != null && !pastCasesTableView.getItems().isEmpty()) {
            pastCasesTableView.getItems().removeAll(pastCasesTableView.getItems());
        }
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
            logger.error("Unknown Error", e);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    private void addToReportBtn(CheckBox checkBox) {
        if(selectedAnalysisResultVariant != null) {
            String oldSymbol = selectedAnalysisResultVariant.getSnpInDel().getIncludedInReport();
            String symbol = "N";
            if (checkBox.isSelected()) {
                symbol = "Y";
            }

            try {
                FXMLLoader loader = mainApp.load(FXMLConstants.EXCLUDE_REPORT);
                Node node = loader.load();
                ExcludeReportDialogController excludeReportDialogController = loader.getController();
                excludeReportDialogController.setMainController(mainController);
                excludeReportDialogController.settingItem(symbol, selectedAnalysisResultVariant, checkBox);
                excludeReportDialogController.show((Parent) node);
            } catch (IOException ioe) {
                ioe.printStackTrace();
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

    public void returnTierClass(String tier, Label label, Integer userTier) {
        label.setAlignment(Pos.CENTER);
        logger.debug("+++++++++++++++");
        logger.debug(userTierLabel.getText()+" "+tier+" "+userTier);
        if(userTier == 2 && tier != null) {arrow.setVisible(true);}
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
    public void addInterpretation() {
        SnpInDelEvidence snpInDelEvidence = new SnpInDelEvidence();
        evidenceTableView.getItems().add(snpInDelEvidence);
        saveBtn.setDisable(false);
    }

    private boolean checkPrimary() {
        return evidenceTableView.getItems().stream().anyMatch(item -> (item.getPrimaryEvidence() != null) ? item.getPrimaryEvidence() : false);
    }

    @FXML
    public void saveInterpretation() {
        if(evidenceTableView.getItems() != null && !evidenceTableView.getItems().isEmpty()) {

            if (checkPrimary()) {
                HttpClientResponse response;
                try {
                    Map<String, Object> params = new HashMap<>();
                    params.put("snpInDelEvidenceCreateRequests", returnEvidenceMap());
                    response = apiService.post("/analysisResults/snpInDels/"
                            + selectedAnalysisResultVariant.getSnpInDel().getId() + "/evidences", params, null, true);
                    logger.debug(response.getContentString());
                    setEvidenceTable();
                    setPastCases();
                } catch (WebAPIException wae) {
                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                            getMainApp().getPrimaryStage(), true);
                    wae.printStackTrace();
                } catch (IOException e) {
                    logger.error("Unknown Error", e);
                    DialogUtil.error("Unknown Error", e.getMessage(),
                            getMainApp().getPrimaryStage(), true);
                }

            } else {
                DialogUtil.warning("Primary check error", "Check primary radio button", getMainApp().getPrimaryStage(), true);
            }
        }
    }

    public void setTier(SnpInDel snpInDel) {
        returnTierClass(snpInDel.getSwTier(), swTierLabel,1);
        //returnTierClass(snpInDel.getExpertTier(), userTierLabel,2);
    }

    public List<Map<String, Object>> returnEvidenceMap() {
        List<Map<String, Object>> list = new ArrayList<>();
        for(SnpInDelEvidence snpInDelEvidence : evidenceTableView.getItems()) {
            Map<String, Object> params = new HashMap<>();

            params.put("provider", StringUtils.isEmpty(snpInDelEvidence.getProvider()) ? "Clinician" : snpInDelEvidence.getProvider());
            params.put("evidenceType", snpInDelEvidence.getEvidenceType());
            params.put("evidenceLevel", snpInDelEvidence.getEvidenceLevel());
            params.put("primaryEvidence", snpInDelEvidence.getPrimaryEvidence() != null ? snpInDelEvidence.getPrimaryEvidence() : false);
            params.put("evidence", snpInDelEvidence.getEvidence());
            list.add(params);
        }
        return list;
    }

    public void delete(SnpInDelEvidence snpInDelEvidence) {
        evidenceTableView.getItems().remove(snpInDelEvidence);
        if(evidenceTableView.getItems().isEmpty()) saveBtn.setDisable(true);
    }


}
