package ngeneanalysys.controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ngeneanalysys.code.enums.BrcaCNVCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.BrcaCnvAmplicon;
import ngeneanalysys.model.BrcaCnvExon;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.model.paged.PagedBrcaCNV;
import ngeneanalysys.model.paged.PagedBrcaCNVExon;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2018-10-15
 */
public class AnalysisDetailBrcaCNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    private CheckBox headerCheckBox;

    @FXML
    private HBox brca1ExonBox;
    @FXML
    private HBox brca2ExonBox;

    @FXML
    private RadioButton brca1RadioButton;
    @FXML
    private RadioButton brca2RadioButton;

    @FXML
    private Label cnvExonLabel;
    @FXML
    private Label cnvDetailLabel;

    @FXML
    private TableView<BrcaCnvExon> exonTableView;
    @FXML
    private TableColumn<BrcaCnvExon, String> exonCNVTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> exonReportTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> exonExonTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> exonDomainTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, Integer> exonCopyNumberTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, Integer> exonCopyNumberOneAmpliconCountTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, Integer> exonCopyNumberTwoAmpliconCountTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, Integer> exonCopyNumberThreeAmpliconCountTableColumn;
    @FXML
    private TableView<BrcaCnvAmplicon> cnvTableView;
    @FXML
    private TableColumn<BrcaCnvAmplicon, String> ampliconTableColumn;
    @FXML
    private TableColumn<BrcaCnvAmplicon, String> referenceRatioTableColumn;
    @FXML
    private TableColumn<BrcaCnvAmplicon, Integer> referenceDepthTableColumn;
    @FXML
    private TableColumn<BrcaCnvAmplicon, BigDecimal> sampleRatioTableColumn;
    @FXML
    private TableColumn<BrcaCnvAmplicon, Integer> copyNumberTableColumn;

    private APIService apiService;

    private AnalysisDetailVariantsController variantsController;

    private List<BrcaCnvAmplicon> brcaCnvAmpliconList;

    private List<BrcaCnvExon> brcaCnvExonList;

    private DecimalFormat decimalFormat = new DecimalFormat("0.###");

    /**
     * @param variantsController AnalysisDetailVariantsController
     */
    void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("BRCA cnv view...");
        apiService = APIService.getInstance();

        brca1RadioButton.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv) {
                cnvExonLabel.setText("CNV EXON INFORMATION (BRCA1)");
                setBrcaExonTableView("BRCA1");
            }
        });

        brca2RadioButton.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv) {
                cnvExonLabel.setText("CNV EXON INFORMATION (BRCA2)");
                setBrcaExonTableView("BRCA2");
            }
        });

        exonTableView.setRowFactory(tv -> {
            TableRow<BrcaCnvExon> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if(e.getClickCount() == 1) {
                    BrcaCnvExon brcaExonCNV = exonTableView.getSelectionModel().getSelectedItem();
                    cnvDetailLabel.setText("CNV DETAIL INFORMATION (" + brcaExonCNV.getExon().toUpperCase() + ")");
                    setBrcaTableView(brcaExonCNV.getGene(), brcaExonCNV.getExon());
                }
            });
            return row;
        });

        ampliconTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getAmplicon()));
        referenceRatioTableColumn.setCellValueFactory(item -> new SimpleStringProperty(
                String.format("%.02f", item.getValue().getDistributionRangeMin()) + " - " +
                        String.format("%.02f", item.getValue().getDistributionRangeMax())));
        referenceDepthTableColumn.setCellValueFactory(item ->
                new SimpleObjectProperty<>(item.getValue().getReferenceMeanDepth()));
        sampleRatioTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSampleRatio()));
        copyNumberTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getDistributionPrediction()));

        brcaExonTableInit();

        setList();
        brca1RadioButton.selectedProperty().setValue(true);
        exonTableView.getSelectionModel().select(0);
        setBrcaTableView("BRCA1", "5'UTR");
        variantsController.getDetailContents().setCenter(root);

    }

    private void brcaExonTableInit() {
        TableColumn<BrcaCnvExon, Boolean> checkBoxColumn = new TableColumn<>("");
        createCheckBoxTableHeader(checkBoxColumn);
        //checkBoxColumn.impl_setReorderable(false); 컬럼 이동 방지 코드
        checkBoxColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue() != null ));
        checkBoxColumn.setCellFactory(param -> new BooleanCell());

        exonCNVTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCnv()));
        exonCNVTableColumn.setCellFactory(param -> new TableCell<BrcaCnvExon, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(empty) {
                    setGraphic(null);
                } else {
                    BrcaCnvExon brcaCNVExon = getTableView().getItems().get(getIndex());

                    String value = BrcaCNVCode.findInitial(brcaCNVExon.getCnv());
                    String code = "cnv_" + brcaCNVExon.getCnv();

                    Label label = null;
                    if(!"NONE".equals(code)) {
                        label = new Label(value);
                        label.getStyleClass().clear();
                        exonCNVTableColumn.getStyleClass().add("alignment_center");
                        label.getStyleClass().add(code);
                    }
                    setGraphic(label);
                }
            }
        });

        exonReportTableColumn.getStyleClass().add("alignment_center");
        exonReportTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getIncludedInReport()));
        exonReportTableColumn.setCellFactory(param -> new TableCell<BrcaCnvExon, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                if(StringUtils.isEmpty(item) || empty) {
                    setGraphic(null);
                    return;
                }
                BrcaCnvExon brcaCNVExon = getTableView().getItems().get(getIndex());
                Label label = new Label();
                label.getStyleClass().remove("label");
                if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
                    label.setText("R");
                    label.getStyleClass().add("report_check");
                } else {
                    label.getStyleClass().add("report_uncheck");
                }
                label.setCursor(Cursor.HAND);
                label.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
                    try {
                        Map<String, Object> params = new HashMap<>();
                        params.put("sampleId", brcaCNVExon.getId());
                        params.put("snpInDelIds", brcaCNVExon.getId().toString());
                        params.put("comment", "N/A");
                        if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
                            params.put("includeInReport", "N");
                        } else {
                            params.put("includeInReport", "Y");
                        }
                        apiService.put("analysisResults/brcaCnvExon/updateIncludeInReport", params, null, true);
                        exonTableView.refresh();
                    } catch (WebAPIException wae) {
                        wae.printStackTrace();
                    }
                });
                setGraphic(label);
            }
        });
        exonExonTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExon()));
        exonDomainTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDomain()));
        exonCopyNumberTableColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCopyNumber()));
        exonCopyNumberOneAmpliconCountTableColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCopyNumberOneAmpliconCount()));
        exonCopyNumberTwoAmpliconCountTableColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCopyNumberTwoAmpliconCount()));
        exonCopyNumberThreeAmpliconCountTableColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCopyNumberThreeAmpliconCount()));

        exonTableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) exonTableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> {
                ObservableList columns = exonTableView.getColumns();

                // If the first columns is not in the first index change it
                if (columns.indexOf(checkBoxColumn) != 0) {
                    columns.remove(checkBoxColumn);
                    columns.add(0, checkBoxColumn);
                }
            });
        });
    }
    public void setList() {
        SampleView sample = (SampleView)paramMap.get("sampleView");

        try {
            HttpClientResponse response = apiService.get("/analysisResults/brcaCnv/" + sample.getId(), null, null, false);
            PagedBrcaCNV pagedBrcaCNV = response.getObjectBeforeConvertResponseToJSON(PagedBrcaCNV.class);
            brcaCnvAmpliconList = pagedBrcaCNV.getResult();

            response = apiService.get("/analysisResults/brcaCnvExon/" + sample.getId(), null, null, false);
            PagedBrcaCNVExon pagedBrcaCNVExon = response.getObjectBeforeConvertResponseToJSON(PagedBrcaCNVExon.class);
            brcaCnvExonList = pagedBrcaCNVExon.getResult();

            Platform.runLater(() -> setBrcaCNVPlot("BRCA1"));
            Platform.runLater(() -> setBrcaCNVPlot("BRCA2"));
        } catch (WebAPIException wae) {
            DialogUtil.warning(wae.getHeaderText(), wae.getMessage(), mainApp.getPrimaryStage(), true);
        }
    }

    private void setBrcaExonTableView(final String gene) {
        if(exonTableView.getItems() != null) {
            exonTableView.getItems().removeAll(exonTableView.getItems());
        }

        List<BrcaCnvExon> list = getBrcaCNVExon(gene);

        if(!list.isEmpty()) exonTableView.getItems().addAll(list);
        exonTableView.refresh();
    }

    private void setBrcaTableView(final String gene, final String exon) {
        if(cnvTableView.getItems() != null) {
            cnvTableView.getItems().removeAll(cnvTableView.getItems());
            cnvTableView.refresh();
        }

        List<BrcaCnvAmplicon> list = getBrcaCNV(gene, exon);

        if(!list.isEmpty()) cnvTableView.getItems().addAll(list);
    }

    private List<BrcaCnvExon> getBrcaCNVExon(final String gene) {
        if(brcaCnvExonList != null && !brcaCnvExonList.isEmpty()) {
            List<BrcaCnvExon> list = brcaCnvExonList.stream().filter(item -> gene.equals(item.getGene()))
                    .collect(Collectors.toList());

            if(!list.isEmpty()) return list;
        }
        return new ArrayList<>();
    }

    private List<BrcaCnvAmplicon> getBrcaCNV(final String gene) {
        if(brcaCnvAmpliconList != null && !brcaCnvAmpliconList.isEmpty()) {
            List<BrcaCnvAmplicon> list = brcaCnvAmpliconList.stream().filter(item -> gene.equals(item.getGene()))
                    .collect(Collectors.toList());

            if(!list.isEmpty()) return list;
        }
        return new ArrayList<>();
    }

    private List<BrcaCnvAmplicon> getBrcaCNV(final String gene, final String exon) {
        if(brcaCnvAmpliconList != null && !brcaCnvAmpliconList.isEmpty()) {
            List<BrcaCnvAmplicon> list = brcaCnvAmpliconList.stream().filter(item -> gene.equals(item.getGene()))
                    .filter(item -> exon.equalsIgnoreCase(item.getExon()))
                    .collect(Collectors.toList());

            if(!list.isEmpty()) return list;
        }
        return new ArrayList<>();
    }

    private Map<String, List<BrcaCnvAmplicon>> groupingExon(List<BrcaCnvAmplicon> list) {
        return list.stream().collect(Collectors.groupingBy(BrcaCnvAmplicon::getExon));
    }

    private boolean compareCount(long numerator, long denominator) {
        return ((double)numerator / denominator) >= 0.8;
    }

    private void calcExonPlot(List<BrcaCnvAmplicon> cnvs, String key, HBox box, final String boxId) {
        long duplicationCount = cnvs.stream().filter(cnv -> cnv.getDistributionPrediction().equals(3)).count();
        long deletionCount = cnvs.stream().filter(cnv -> cnv.getDistributionPrediction().equals(1)).count();
        long totalCount = cnvs.size();

        if(compareCount(deletionCount, totalCount)) {
            setColorHBox(box, "brca_cnv_deletion",boxId + key);
        } else if(compareCount(duplicationCount, totalCount)) {
            setColorHBox(box, "brca_cnv_duplication",boxId + key);
        }
    }

    private void setColorHBox(HBox box, final String styleClass, final String key) {
        Optional<Node> optionalNode = box.getChildren().stream()
                .filter(obj -> obj.getId() != null && obj.getId().equals(key)).findFirst();

        optionalNode.ifPresent(node -> node.getStyleClass().add(styleClass));
    }

    private void setBrcaCNVPlot(String gene) {
        List<BrcaCnvAmplicon> brcaCnvAmpliconList = getBrcaCNV(gene);
        if(!brcaCnvAmpliconList.isEmpty()) {
            Map<String, List<BrcaCnvAmplicon>> cnvGroup = groupingExon(brcaCnvAmpliconList);

            Set<String> cnvKey = cnvGroup.keySet();
            HBox box;
            String boxId = "brca2_";
            if(gene.equals("BRCA1")) {
                box = brca1ExonBox;
                boxId = "brca1_";
            } else {
                box = brca2ExonBox;
            }

            for (String s : cnvKey) {
                if(s.contains("UTR")) {
                    calcExonPlot(cnvGroup.get(s), "utr", box, boxId);
                } else {
                    calcExonPlot(cnvGroup.get(s), s, box, boxId);
                }
            }
        }
    }

    private void createCheckBoxTableHeader(TableColumn<BrcaCnvExon, ?> column) {
        HBox hBox = new HBox();
        hBox.setPrefHeight(Double.MAX_VALUE);
        hBox.setAlignment(Pos.CENTER);
        CheckBox box = new CheckBox();
        headerCheckBox = box;
        hBox.getChildren().add(box);
        column.setStyle("-fx-alignment : center");
        column.setSortable(false);
        column.setGraphic(box);

        box.selectedProperty().addListener((observable, ov, nv) -> {
            if(exonTableView.getItems() != null) {
                exonTableView.getItems().forEach(item -> item.setCheckItem(nv));
                exonTableView.refresh();
            }
        });

        column.widthProperty().addListener((ob, ov, nv) -> hBox.setMinWidth(column.getWidth()));
        column.setResizable(false);

        column.setPrefWidth(50d);

        exonTableView.getColumns().add(0, column);
    }

    class BooleanCell extends TableCell<BrcaCnvExon, Boolean> {
        private CheckBox checkBox = new CheckBox();
        private BooleanCell() {
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                BrcaCnvExon brcaCNVExon = BooleanCell.this.getTableView().getItems().get(
                        BooleanCell.this.getIndex());
                brcaCNVExon.setCheckItem(newValue);
                checkBox.setSelected(newValue);

            });
        }

        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(empty) {
                setGraphic(null);
                return;
            }
            this.setStyle(this.getStyle() + "; -fx-background-color : white;");
            BrcaCnvExon brcaCNVExon = BooleanCell.this.getTableView().getItems().get(
                    BooleanCell.this.getIndex());
            checkBox.setSelected(brcaCNVExon.getCheckItem());

            setGraphic(checkBox);
        }
    }

    @FXML
    public void doExonDeletion() {
        //TODO
    }

    @FXML
    public void doExonDuplication() {
        //TODO
    }

    @FXML
    public void doCnvReport() {
        //TODO
    }
}
