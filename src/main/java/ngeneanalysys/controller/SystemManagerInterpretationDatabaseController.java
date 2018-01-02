package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.DbGenomicCoordinateClinicalVariant;
import ngeneanalysys.model.GenomicCoordinateClinicalVariant;
import ngeneanalysys.model.NgsGenomicCoordinateClinicalVariant;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private  TableColumn<GenomicCoordinateClinicalVariant, String> versionTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> dbChrTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> dbGeneTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> dbPositionTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> dbRefTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> dbAltTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> ngsChrTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> ngsGeneTableColumn;

    @FXML
    private TableColumn<GenomicCoordinateClinicalVariant, String> ngsPositionTableColumn;

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
    private Pagination interpretationPagination;

    @Override
    public void show(Parent root) throws IOException {

        apiService = APIService.getInstance();

        evidenceListTable.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            evidenceListTable.refresh();
            // close text box
            evidenceListTable.edit(-1, null);
        });

        diseaseIdTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getDiseaseId()));
        versionTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getClinicalVariantVersion()));
        versionTableColumn.setCellFactory(tableColumn -> new EditingCell());
        versionTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setClinicalVariantVersion(t.getNewValue());
        });

        dbChrTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getDb().getDbChr()));
        dbChrTableColumn.setCellFactory(tableColumn -> new EditingCell());
        dbChrTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getDb().setDbChr(t.getNewValue());
        });
        dbGeneTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getDb().getDbGene()));
        dbGeneTableColumn.setCellFactory(tableColumn -> new EditingCell());
        dbGeneTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getDb().setDbChr(t.getNewValue());
        });
        dbPositionTableColumn.setCellValueFactory(item -> new SimpleStringProperty((item.getValue().getDb().getDbPosition() != null) ?
                item.getValue().getDb().getDbPosition().toString() : null));
        dbPositionTableColumn.setCellFactory(tableColumn -> new EditingCell());
        dbPositionTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getDb().setDbPosition(Integer.parseInt(t.getNewValue()));
        });
        dbRefTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getDb().getDbRef()));
        dbRefTableColumn.setCellFactory(tableColumn -> new EditingCell());
        dbRefTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getDb().setDbRef(t.getNewValue());
        });
        dbAltTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getDb().getDbAlt()));
        dbAltTableColumn.setCellFactory(tableColumn -> new EditingCell());
        dbAltTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getDb().setDbAlt(t.getNewValue());
        });

        ngsChrTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getNgs().getNgsChr()));
        ngsChrTableColumn.setCellFactory(tableColumn -> new EditingCell());
        ngsChrTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getNgs().setNgsChr(t.getNewValue());
        });
        ngsGeneTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getNgs().getNgsGene()));
        ngsGeneTableColumn.setCellFactory(tableColumn -> new EditingCell());
        ngsGeneTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getNgs().setNgsGene(t.getNewValue());
        });
        ngsPositionTableColumn.setCellValueFactory(item -> new SimpleStringProperty((item.getValue().getNgs().getNgsPosition() != null) ?
                item.getValue().getNgs().getNgsPosition().toString() : null));
        ngsPositionTableColumn.setCellFactory(tableColumn -> new EditingCell());
        ngsPositionTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getNgs().setNgsPosition(Integer.parseInt(t.getNewValue()));
        });
        ngsRefTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getNgs().getNgsRef()));
        ngsRefTableColumn.setCellFactory(tableColumn -> new EditingCell());
        ngsRefTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getNgs().setNgsRef(t.getNewValue());
        });
        ngsAltTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getNgs().getNgsAlt()));
        ngsAltTableColumn.setCellFactory(tableColumn -> new EditingCell());
        ngsAltTableColumn.setOnEditCommit((TableColumn.CellEditEvent<GenomicCoordinateClinicalVariant, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).getNgs().setNgsAlt(t.getNewValue());
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

        interpretationPagination.setPageFactory(pageIndex -> {
            setInterpretationList(pageIndex + 1);
            return new VBox();
        });
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

            /*PagedReportTemplate pagedReportTemplate = response.getObjectBeforeConvertResponseToJSON(PagedReportTemplate.class);

            if(pagedReportTemplate != null) {
                totalCount = pagedReportTemplate.getCount();
                reportTemplateListTable.getItems().clear();
                reportTemplateListTable.setItems(FXCollections.observableArrayList(pagedReportTemplate.getResult()));
            }

            int pageCount = 0;

            if(totalCount > 0) {
                pageCount = totalCount / limit;
                reportTemplatePagination.setCurrentPageIndex(page - 1);
                if(totalCount % limit > 0) {
                    pageCount++;
                }
            }

            logger.info("total count : " + totalCount + ", page count : " + pageCount);

            if (pageCount > 0) {
                reportTemplatePagination.setVisible(true);
                reportTemplatePagination.setPageCount(pageCount);
            } else {
                reportTemplatePagination.setVisible(false);
            }*/

        } catch(WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        }
    }

    @FXML
    public void saveInterpretationDatabase() {
        List<GenomicCoordinateClinicalVariant> list = evidenceListTable.getItems();

        if(list != null && !list.isEmpty()) {
            for(GenomicCoordinateClinicalVariant item : list) {
                if(item.getId() == null) {

                } else {

                }
            }
        }
    }

    @FXML
    private void interpretationAdd() {
        GenomicCoordinateClinicalVariant genomicCoordinateClinicalVariant = new GenomicCoordinateClinicalVariant();
        genomicCoordinateClinicalVariant.setDiseaseId(evidenceListTable.getItems().size());
        genomicCoordinateClinicalVariant.setDb(new DbGenomicCoordinateClinicalVariant());
        genomicCoordinateClinicalVariant.setNgs(new NgsGenomicCoordinateClinicalVariant());
        evidenceListTable.getItems().add(0, genomicCoordinateClinicalVariant);

        if(evidenceListTable.getItems().size() > 17) {
            while(evidenceListTable.getItems().size() > 17) {
                evidenceListTable.getItems().remove(17);
            }
        }
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
            return getItem() == null ? "" : getItem().toString();
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
