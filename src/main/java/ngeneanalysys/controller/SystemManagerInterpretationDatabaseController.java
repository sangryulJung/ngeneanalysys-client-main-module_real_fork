package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedGenomicCoordinateClinicalVariant;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.task.ClinicalFileUploadTask;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Jang
 * @since 2017-12-19
 */
public class SystemManagerInterpretationDatabaseController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    @FXML
    private TableView<GenomicCoordinateClinicalVariant> evidenceListTable;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, Integer> diseaseIdTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> tierTableColumn;

    @FXML
    private  TableColumn<GenomicCoordinateClinicalVariant, String> versionTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> chrTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> geneTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> positionTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> dbRefTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> dbAltTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> ngsRefTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> ngsAltTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> typeTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> evidenceATableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> evidenceBTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> evidenceCTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> evidenceDTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> evidenceNegativeTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> benignTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> createdAtTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> deletedTableColumn;

    @FXML
    private Pagination interpretationPagination;

    private Integer currentPageIndex = -1;

    private Set<Integer> modifiedList = new HashSet<>();

    @Override
    public void show(Parent root) throws IOException {

        apiService = APIService.getInstance();

        evidenceListTable.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            evidenceListTable.refresh();
            // close text box
            evidenceListTable.edit(-1, null);
        });

        diseaseIdTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getDiseaseId()));
        diseaseIdTableColumn.setCellFactory(item -> new DiseasesComboBoxCell());
        tierTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getTier()));
        tierTableColumn.setCellFactory(item -> new TierComboBoxCell());
        versionTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getClinicalVariantVersion()));
        versionTableColumn.setCellFactory(tableColumn -> new EditingCell());
        versionTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setClinicalVariantVersion(t.getNewValue());
        });

        chrTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGenomicCoordinateForCV().getChr()));
        chrTableColumn.setCellFactory(tableColumn -> new EditingCell());
        chrTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getGenomicCoordinateForCV().setChr(t.getNewValue());
        });
        geneTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGenomicCoordinateForCV().getGene()));
        geneTableColumn.setCellFactory(tableColumn -> new EditingCell());
        geneTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getGenomicCoordinateForCV().setGene(t.getNewValue());
        });
        positionTableColumn.setCellValueFactory(item -> new SimpleStringProperty((item.getValue().getGenomicCoordinateForCV().getPosition() != null) ?
                item.getValue().getGenomicCoordinateForCV().getPosition().toString() : null));
        positionTableColumn.setCellFactory(tableColumn -> new EditingCell());
        positionTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            if(!StringUtils.isEmpty(t.getNewValue()))
                (t.getTableView().getItems().get(t.getTablePosition().getRow())).getGenomicCoordinateForCV().setPosition(Integer.parseInt(t.getNewValue()));
        });
        dbRefTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGenomicCoordinateForCV().getDbRef()));
        dbRefTableColumn.setCellFactory(tableColumn -> new EditingCell());
        dbRefTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getGenomicCoordinateForCV().setDbRef(t.getNewValue());
        });
        dbAltTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGenomicCoordinateForCV().getDbAlt()));
        dbAltTableColumn.setCellFactory(tableColumn -> new EditingCell());
        dbAltTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getGenomicCoordinateForCV().setDbAlt(t.getNewValue());
        });

        ngsRefTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGenomicCoordinateForCV().getNgsRef()));
        ngsRefTableColumn.setCellFactory(tableColumn -> new EditingCell());
        ngsRefTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getGenomicCoordinateForCV().setNgsRef(t.getNewValue());
        });
        ngsAltTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGenomicCoordinateForCV().getNgsAlt()));
        ngsAltTableColumn.setCellFactory(tableColumn -> new EditingCell());
        ngsAltTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getGenomicCoordinateForCV().setNgsAlt(t.getNewValue());
        });

        typeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getClinicalVariantType()));
        typeTableColumn.setCellFactory(tableColumn -> new EditingCell());
        typeTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setClinicalVariantType(t.getNewValue());
        });

        evidenceATableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getEvidenceLevelA()));
        evidenceATableColumn.setCellFactory(tableColumn -> new PopUpTableCell("evidenceA"));

        evidenceBTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getEvidenceLevelB()));
        evidenceBTableColumn.setCellFactory(tableColumn -> new PopUpTableCell("evidenceB"));

        evidenceCTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getEvidenceLevelC()));
        evidenceCTableColumn.setCellFactory(tableColumn -> new PopUpTableCell("evidenceC"));

        evidenceDTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getEvidenceLevelD()));
        evidenceDTableColumn.setCellFactory(tableColumn -> new PopUpTableCell("evidenceD"));

        evidenceNegativeTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getEvidencePertinentNegative()));
        evidenceNegativeTableColumn.setCellFactory(tableColumn -> new PopUpTableCell("PertinentNegative"));

        benignTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getEvidenceLevelBenign()));
        benignTableColumn.setCellFactory(tableColumn -> new PopUpTableCell("evidenceBenign"));

        createdAtTableColumn.setCellValueFactory(item -> new SimpleStringProperty(DateFormatUtils.format(
                item.getValue().getCreatedAt().toDate(), "yyyy-MM-dd")));

        deletedTableColumn.setCellValueFactory(item -> new SimpleStringProperty((item.getValue().getDeleted() == 0) ? "N" : "Y"));

        interpretationPagination.setPageFactory(pageIndex -> {
            if(currentPageIndex != pageIndex) {
                setInterpretationList(pageIndex + 1);
                currentPageIndex = pageIndex;
            }
            return new VBox();
        });
    }

    @FXML
    public void addDataFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose ROI File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(mainController.getPrimaryStage());

        if(file != null) {
            Task task = new ClinicalFileUploadTask(file);

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

            task.setOnSucceeded(ev -> {
                try {
                    DialogUtil.alert("Clinical File Upload",
                            "Clinical File Upload Success!", this.getMainController().getPrimaryStage(), true);

                    setInterpretationList(1);

                } catch (Exception e) {
                    logger.error("panel list refresh fail.", e);
                }

            });
        }
    }

    public void setInterpretationList(int page) {
        int totalCount = 0;
        int limit = 17;
        int offset = (page - 1)  * limit;

        try {
            Map<String, Object> param = new HashMap<>();

            param.put("limit", limit);
            param.put("offset", offset);

            HttpClientResponse response = apiService.get("admin/clinicalVariant/genomicCoordinate", param, null, false);

            PagedGenomicCoordinateClinicalVariant pagedGenomicCoordinateClinicalVariant = response.getObjectBeforeConvertResponseToJSON(PagedGenomicCoordinateClinicalVariant.class);

            if(pagedGenomicCoordinateClinicalVariant != null) {
                totalCount = pagedGenomicCoordinateClinicalVariant.getCount();
                evidenceListTable.getItems().clear();
                evidenceListTable.setItems(FXCollections.observableArrayList(pagedGenomicCoordinateClinicalVariant.getResult()));
            }

            int pageCount = 0;

            if(totalCount > 0) {
                pageCount = totalCount / limit;
                interpretationPagination.setCurrentPageIndex(page - 1);
                if(totalCount % limit > 0) {
                    pageCount++;
                }
            }

            logger.info("total count : " + totalCount + ", page count : " + pageCount);

            if (pageCount > 0) {
                interpretationPagination.setVisible(true);
                interpretationPagination.setPageCount(pageCount);
            } else {
                interpretationPagination.setVisible(false);
            }
        } catch(WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), false);
        }
    }

    @FXML
    public void saveInterpretationDatabase() {
        List<GenomicCoordinateClinicalVariant> list = evidenceListTable.getItems();

        if(list != null && !list.isEmpty()) {
            for(GenomicCoordinateClinicalVariant item : list) {
                try {

                    if(item.getDiseaseId() == null || StringUtils.isEmpty(item.getTier()) || StringUtils.isEmpty(item.getClinicalVariantVersion())
                            || StringUtils.isEmpty(item.getGenomicCoordinateForCV().getChr()) || StringUtils.isEmpty(item.getGenomicCoordinateForCV().getGene())) {
                        break;
                    }

                    Map<String, Object> params = ConvertUtil.getMapToModel(item);
                    params.remove("createdAt");
                    params.remove("updatedAt");
                    params.remove("deletedAt");
                    params.remove("deleted");

                    if (item.getId() == null) {
                        params.remove("id");
                        apiService.post("admin/clinicalVariant/genomicCoordinate", params, null, true);
                    } else {
                        Integer id = (Integer)params.get("id");
                        if(modifiedList.contains(id)) {
                            params.remove("id");
                            apiService.put("admin/clinicalVariant/genomicCoordinate/" + id, params, null, true);
                        }
                    }
                } catch (WebAPIException wae) {
                    DialogUtil.error(wae.getHeaderText(), wae.getContents(), mainController.getPrimaryStage(), true);
                    wae.printStackTrace();
                } catch (IOException ioe) {
                    DialogUtil.error(ioe.getMessage(), ioe.getMessage(), mainController.getPrimaryStage(), true);
                    ioe.printStackTrace();
                }
            }
            modifiedList.clear();
        }
    }

    public void addModifiedList(GenomicCoordinateClinicalVariant variant) {
        if(variant.getId() != null && variant.getId() != 0) {
            modifiedList.add(variant.getId());
        }
    }

    @FXML
    private void interpretationAdd() {
        GenomicCoordinateClinicalVariant genomicCoordinateClinicalVariant = new GenomicCoordinateClinicalVariant();
        genomicCoordinateClinicalVariant.setDiseaseId(evidenceListTable.getItems().size());
        genomicCoordinateClinicalVariant.setGenomicCoordinateForCV(new GenomicCoordinateForCV());
        evidenceListTable.getItems().add(0, genomicCoordinateClinicalVariant);

        if(evidenceListTable.getItems().size() > 17) {
            while(evidenceListTable.getItems().size() > 17) {
                evidenceListTable.getItems().remove(17);
            }
        }
    }

    @FXML
    private void cancelInput() {
        setInterpretationList(currentPageIndex + 1);
    }

    class EditingCell extends TableCell<GenomicCoordinateClinicalVariant, String> {
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
            GenomicCoordinateClinicalVariant variant = this.getTableView().getItems().get(this.getTableRow().getIndex());
            textField.setOnKeyPressed(t -> {
                if(t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());
                    addModifiedList(variant);
                } else if (t.getCode() == KeyCode.TAB) {
                    commitEdit(textField.getText());
                    addModifiedList(variant);
                } else if(t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
            textField.focusedProperty().addListener((arg0, arg1, arg2) -> {
                if (!arg2) {
                    commitEdit(textField.getText());
                    addModifiedList(variant);
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

    private class TierComboBoxCell extends TableCell<GenomicCoordinateClinicalVariant, String> {
        private ComboBox<ComboBoxItem> comboBox = new ComboBox<>();
        private boolean setting = false;

        public TierComboBoxCell() {
            comboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
                GenomicCoordinateClinicalVariant genomicCoordinateClinicalVariant = TierComboBoxCell.this.getTableView().getItems().get(
                        TierComboBoxCell.this.getIndex());

                if(!StringUtils.isEmpty(t1.getValue())) {
                    genomicCoordinateClinicalVariant.setTier(t1.getValue());
                    if(setting) {
                        addModifiedList(genomicCoordinateClinicalVariant);
                    } else {
                        setting = true;
                    }
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

            GenomicCoordinateClinicalVariant genomicCoordinateClinicalVariant = TierComboBoxCell.this.getTableView().getItems().get(
                    TierComboBoxCell.this.getIndex());

            if(genomicCoordinateClinicalVariant.getDeleted() != null && genomicCoordinateClinicalVariant.getDeleted() != 0) {
                return;
            }

            if(comboBox.getItems().isEmpty()) {
                comboBox.setConverter(new ComboBoxConverter());

                comboBox.getItems().add(new ComboBoxItem());
                comboBox.getSelectionModel().selectFirst();

                comboBox.getItems().add(new ComboBoxItem("T1", "Tier I"));
                comboBox.getItems().add(new ComboBoxItem("T2", "Tier II"));
                comboBox.getItems().add(new ComboBoxItem("T3", "Tier III"));
                comboBox.getItems().add(new ComboBoxItem("T4", "Tier IV"));

                if(!StringUtils.isEmpty(genomicCoordinateClinicalVariant.getTier())) {
                    if("T1".equals(genomicCoordinateClinicalVariant.getTier())) {
                        comboBox.getSelectionModel().select(1);
                    } else if("T2".equals(genomicCoordinateClinicalVariant.getTier())) {
                        comboBox.getSelectionModel().select(2);
                    } else if("T3".equals(genomicCoordinateClinicalVariant.getTier())) {
                        comboBox.getSelectionModel().select(3);
                    } else if("T4".equals(genomicCoordinateClinicalVariant.getTier())) {
                        comboBox.getSelectionModel().select(4);
                    }
                }
            }
            setGraphic(comboBox);
        }

    }

    private class DiseasesComboBoxCell extends TableCell<GenomicCoordinateClinicalVariant, Integer> {
        private ComboBox<ComboBoxItem> comboBox = new ComboBox<>();
        boolean setting = false;

        public DiseasesComboBoxCell() {
            comboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
                GenomicCoordinateClinicalVariant genomicCoordinateClinicalVariant = DiseasesComboBoxCell.this.getTableView().getItems().get(
                        DiseasesComboBoxCell.this.getIndex());

                if(!StringUtils.isEmpty(t1.getValue())) {
                    genomicCoordinateClinicalVariant.setDiseaseId(Integer.parseInt(t1.getValue()));
                    if(setting) {
                        addModifiedList(genomicCoordinateClinicalVariant);
                    } else {
                        setting = true;
                    }
                }
            });
        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if(empty) {
                setGraphic(null);
                return;
            }

            GenomicCoordinateClinicalVariant genomicCoordinateClinicalVariant = DiseasesComboBoxCell.this.getTableView().getItems().get(
                    DiseasesComboBoxCell.this.getIndex());

            if(genomicCoordinateClinicalVariant.getDeleted() != null && genomicCoordinateClinicalVariant.getDeleted() != 0) {
                return;
            }

            if(comboBox.getItems().isEmpty()) {
                comboBox.setConverter(new ComboBoxConverter());

                comboBox.getSelectionModel().selectFirst();

                List<Diseases> diseases = (List<Diseases>)mainController.getBasicInformationMap().get("diseases");

                if(diseases != null && !diseases.isEmpty()) {

                    for(Diseases disease : diseases) {
                        ComboBoxItem comboBoxItem = new ComboBoxItem(disease.getId().toString(), disease.getName());
                        comboBox.getItems().add(comboBoxItem);

                        if(genomicCoordinateClinicalVariant.getDiseaseId() != null && genomicCoordinateClinicalVariant.getDiseaseId().equals(disease.getId())) {
                            comboBox.getSelectionModel().select(comboBoxItem);
                        }

                    }
                }
            }
            setGraphic(comboBox);

        }

    }

    private class PopUpTableCell extends TableCell<GenomicCoordinateClinicalVariant, String> {
        private TextField textField = null;
        private String item = "";

        public PopUpTableCell(String item) {
            this.item = item;
        }

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
            textField.setEditable(false);
            textField.setOnMouseClicked(t -> {
                GenomicCoordinateClinicalVariant variant = PopUpTableCell.this.getTableView().getItems().get(
                        PopUpTableCell.this.getIndex());

                TextInputDialog dialog = new TextInputDialog();
                if(item.equalsIgnoreCase("evidenceA") && !StringUtils.isEmpty(variant.getEvidenceLevelA())) {
                    dialog.getEditor().setText(variant.getEvidenceLevelA());
                } else if(item.equalsIgnoreCase("evidenceB") &&!StringUtils.isEmpty(variant.getEvidenceLevelB())) {
                    dialog.getEditor().setText(variant.getEvidenceLevelB());
                } else if(item.equalsIgnoreCase("evidenceC") &&!StringUtils.isEmpty(variant.getEvidenceLevelC())) {
                    dialog.getEditor().setText(variant.getEvidenceLevelC());
                } else if(item.equalsIgnoreCase("evidenceD") &&!StringUtils.isEmpty(variant.getEvidenceLevelD())) {
                    dialog.getEditor().setText(variant.getEvidenceLevelD());
                } else if(item.equalsIgnoreCase("PertinentNegative") && !StringUtils.isEmpty(variant.getEvidencePertinentNegative())) {
                    dialog.getEditor().setText(variant.getEvidencePertinentNegative());
                } else if(item.equalsIgnoreCase("evidenceBenign") && !StringUtils.isEmpty(variant.getEvidenceLevelBenign())) {
                    dialog.getEditor().setText(variant.getEvidenceLevelBenign());
                }
                dialog.getDialogPane().setMinWidth(450);
                dialog.setTitle("Text Input Dialog");
                dialog.setHeaderText("Please enter " + item +"");

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(text -> {
                    if(item.equalsIgnoreCase("evidenceA")) {
                        variant.setEvidenceLevelA(text);
                    } else if(item.equalsIgnoreCase("evidenceB")) {
                        variant.setEvidenceLevelB(text);
                    } else if(item.equalsIgnoreCase("evidenceC")) {
                        variant.setEvidenceLevelC(text);
                    } else if(item.equalsIgnoreCase("evidenceD")) {
                        variant.setEvidenceLevelD(text);
                    } else if(item.equalsIgnoreCase("PertinentNegative")) {
                        variant.setEvidencePertinentNegative(text);
                    } else if(item.equalsIgnoreCase("evidenceBenign")) {
                        variant.setEvidenceLevelBenign(text);
                    }
                    commitEdit(text);
                    addModifiedList(variant);
                });

                if(!result.isPresent()) {
                    cancelEdit();
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }

    }

}
