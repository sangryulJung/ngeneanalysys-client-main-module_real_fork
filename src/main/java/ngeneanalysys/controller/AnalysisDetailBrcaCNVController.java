package ngeneanalysys.controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import ngeneanalysys.code.enums.BrcaAmpliconCopyNumberPredictionAlgorithmCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedBrcaCNV;
import ngeneanalysys.model.paged.PagedBrcaCNVExon;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.WorksheetUtil;
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
    private TableColumn<BrcaCnvExon, Integer> exonCopyNumberTotalAmpliconCountTableColumn;
    @FXML
    private TableView<BrcaCnvAmplicon> cnvAmpliconTableView;
    @FXML
    private TableColumn<BrcaCnvAmplicon, String> ampliconNameTableColumn;
    @FXML
    private TableColumn<BrcaCnvAmplicon, String> ampliconReferenceRatioTableColumn;
    @FXML
    private TableColumn<BrcaCnvAmplicon, Integer> ampliconReferenceMeanDepthTableColumn;
    @FXML
    private TableColumn<BrcaCnvAmplicon, Integer> ampliconReferenceMedianDepthTableColumn;
    @FXML
    private TableColumn<BrcaCnvAmplicon, BigDecimal> ampliconSampleRatioTableColumn;
    @FXML
    private TableColumn<BrcaCnvAmplicon, Integer> ampliconCopyNumberTableColumn;
    @FXML
    private TableColumn<BrcaCnvAmplicon, Integer> ampliconSampleDepthTableColumn;

    private APIService apiService;

    private AnalysisDetailVariantsController variantsController;

    private List<BrcaCnvAmplicon> brcaCnvAmpliconList;

    private List<BrcaCnvExon> brcaCnvExonList;

    private DecimalFormat decimalFormat = new DecimalFormat("0.###");
    private SampleView sample = null;
    private Panel panel = null;
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
        sample = (SampleView)paramMap.get("sampleView");
        panel = sample.getPanel();
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
                    setBrcaTableView(brcaExonCNV.getGene(), brcaExonCNV.getExon());
                }
            });
            return row;
        });

        ampliconNameTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getAmplicon()));
        ampliconReferenceRatioTableColumn.setCellValueFactory(item -> {
            if(BrcaAmpliconCopyNumberPredictionAlgorithmCode.DISTRIBUTION.getCode()
                    .equals(panel.getCnvConfigBRCAaccuTest().getAmpliconCopyNumberPredictionAlgorithm())) {
                return new SimpleStringProperty(
                        String.format("%.02f", item.getValue().getDistributionRangeMin()) + " - " +
                                String.format("%.02f", item.getValue().getDistributionRangeMax()));
            } else {
                BigDecimal del = panel.getCnvConfigBRCAaccuTest().getSimpleCutoffDeletionValue() != null ?
                        new BigDecimal(panel.getCnvConfigBRCAaccuTest().getSimpleCutoffDeletionValue()) : new BigDecimal(0);

                BigDecimal dup = panel.getCnvConfigBRCAaccuTest().getSimpleCutoffDulplicationValue() != null ?
                        new BigDecimal(panel.getCnvConfigBRCAaccuTest().getSimpleCutoffDulplicationValue()) : new BigDecimal(0);

                return new SimpleStringProperty(
                        String.format("%.02f", item.getValue().getRawRangeMin().subtract(del)) + " - " +
                                String.format("%.02f", item.getValue().getRawRangeMax().add(dup)));
            }
        });
        ampliconReferenceMeanDepthTableColumn.setCellValueFactory(item ->
                new SimpleObjectProperty<>(item.getValue().getReferenceMeanDepth()));
        ampliconReferenceMedianDepthTableColumn.setCellValueFactory(item ->
                new SimpleObjectProperty<>(item.getValue().getReferenceMedianDepth()));
        ampliconSampleRatioTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSampleRatio()));
        ampliconCopyNumberTableColumn.setText("Copy\nNumber");
        if (BrcaAmpliconCopyNumberPredictionAlgorithmCode.DISTRIBUTION.getCode()
                .equals(panel.getCnvConfigBRCAaccuTest().getAmpliconCopyNumberPredictionAlgorithm())) {
            ampliconCopyNumberTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getDistributionPrediction()));
        } else {
            ampliconCopyNumberTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getRawPrediction()));
        }
        ampliconSampleDepthTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSampleDepth()));


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
//        exonReportTableColumn.getStyleClass().add("alignment_center");
//        exonReportTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getIncludedInReport()));
//        exonReportTableColumn.setCellFactory(param -> new TableCell<BrcaCnvExon, String>() {
//            @Override
//            public void updateItem(String item, boolean empty) {
//                if(StringUtils.isEmpty(item) || empty) {
//                    setGraphic(null);
//                    return;
//                }
//                BrcaCnvExon brcaCNVExon = getTableView().getItems().get(getIndex());
//                Label label = new Label();
//                label.getStyleClass().remove("label");
//                if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
//                    label.setText("R");
//                    label.getStyleClass().add("report_check");
//                } else {
//                    label.getStyleClass().add("report_uncheck");
//                }
//                label.setCursor(Cursor.HAND);
//                label.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
//                    try {
//                        Map<String, Object> params = new HashMap<>();
//                        params.put("sampleId", brcaCNVExon.getId());
//                        params.put("snpInDelIds", brcaCNVExon.getId().toString());
//                        params.put("comment", "N/A");
//                        if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
//                            params.put("includeInReport", "N");
//                        } else {
//                            params.put("includeInReport", "Y");
//                        }
//                        apiService.put("analysisResults/brcaCnvExon/updateIncludeInReport", params, null, true);
//                        exonTableView.refresh();
//                    } catch (WebAPIException wae) {
//                        wae.printStackTrace();
//                    }
//                });
//                setGraphic(label);
//            }
//        });
        exonCopyNumberTableColumn.setText("Copy\nNumber");
        exonCopyNumberTableColumn.getStyleClass().add("alignment_center");
        exonCopyNumberTableColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCopyNumber()));
        exonCopyNumberTableColumn.setCellFactory(param -> new TableCell<BrcaCnvExon, Integer>() {
            @Override
            public void updateItem(Integer item, boolean empty) {
                if(item == null || empty) {
                    setGraphic(null);
                    return;
                }
                //BrcaCnvExon brcaCNVExon = getTableView().getItems().get(getIndex());
                Label label = new Label(item.toString());
                label.getStyleClass().remove("label");
                if (item == 2) {
                    label.getStyleClass().add("cnv_normal");
                } else if(item == 3) {
                    label.getStyleClass().add("cnv_duplication");
                } else if(item == 1) {
                    label.getStyleClass().add("cnv_deletion");
                } else {
                    setGraphic(null);
                    return;
                }
//                label.setCursor(Cursor.HAND);
//                label.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
//                    try {
//                        Map<String, Object> params = new HashMap<>();
//                        params.put("sampleId", brcaCNVExon.getId());
//                        params.put("snpInDelIds", brcaCNVExon.getId().toString());
//                        params.put("comment", "N/A");
//                        if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
//                            params.put("includeInReport", "N");
//                        } else {
//                            params.put("includeInReport", "Y");
//                        }
//                        apiService.put("analysisResults/brcaCnvExon/updateIncludeInReport", params, null, true);
//                        exonTableView.refresh();
//                    } catch (WebAPIException wae) {
//                        wae.printStackTrace();
//                    }
//                });
                setGraphic(label);
            }
        });
        exonExonTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExon()));
        exonDomainTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDomain()));
        exonCopyNumberOneAmpliconCountTableColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCopyNumberOneAmpliconCount()));
        exonCopyNumberTwoAmpliconCountTableColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCopyNumberTwoAmpliconCount()));
        exonCopyNumberThreeAmpliconCountTableColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCopyNumberThreeAmpliconCount()));
        exonCopyNumberTotalAmpliconCountTableColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getTotalAmpliconCount()));

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
            brcaCnvExonList = pagedBrcaCNVExon.getResult().stream().sorted((a, b) ->
            {
                if(a.getExon().startsWith("exon") && b.getExon().startsWith("exon")) {
                    int intA = Integer.parseInt(a.getExon().replaceAll("exon", ""));
                    int intB = Integer.parseInt(b.getExon().replaceAll("exon", ""));
                    return Integer.compare(intA, intB);
                } else if(a.getExon().startsWith("UTR")) {
                    return 1;
                } else if(b.getExon().startsWith("UTR")) {
                    return -1;
                }
                return 1;
            }).collect(Collectors.toList());

            Platform.runLater(() -> setBrcaCnvPlot("BRCA1"));
            Platform.runLater(() -> setBrcaCnvPlot("BRCA2"));
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
        cnvDetailLabel.setText("CNV DETAIL INFORMATION (" + exon.toUpperCase() + ")");
        if(cnvAmpliconTableView.getItems() != null) {
            cnvAmpliconTableView.getItems().removeAll(cnvAmpliconTableView.getItems());
            cnvAmpliconTableView.refresh();
        }

        List<BrcaCnvAmplicon> list = getBrcaCnvAmpliconsByExon(gene, exon);

        if(!list.isEmpty()) cnvAmpliconTableView.getItems().addAll(list);
    }

    private List<BrcaCnvExon> getBrcaCNVExon(final String gene) {
        if(brcaCnvExonList != null && !brcaCnvExonList.isEmpty()) {
            List<BrcaCnvExon> list = brcaCnvExonList.stream().filter(item -> gene.equals(item.getGene()))
                    .collect(Collectors.toList());

            if(!list.isEmpty()) return list;
        }
        return new ArrayList<>();
    }

    private List<BrcaCnvExon> getBrcaCnvExons(final String gene) {
        if(brcaCnvExonList != null && !brcaCnvExonList.isEmpty()) {
            List<BrcaCnvExon> list = brcaCnvExonList.stream().filter(item -> gene.equals(item.getGene()))
                    .collect(Collectors.toList());

            if(!list.isEmpty()) return list;
        }
        return new ArrayList<>();
    }

    private List<BrcaCnvAmplicon> getBrcaCnvAmpliconsByExon(final String gene, final String exon) {
        if(brcaCnvAmpliconList != null && !brcaCnvAmpliconList.isEmpty()) {
            List<BrcaCnvAmplicon> list = brcaCnvAmpliconList.stream().filter(item -> gene.equals(item.getGene()))
                    .filter(item -> exon.equalsIgnoreCase(item.getExon()))
                    .collect(Collectors.toList());

            if(!list.isEmpty()) return list;
        }
        return new ArrayList<>();
    }

    private void setBrcaCnvPlot(String gene) {
        List<BrcaCnvExon> brcaCnvExonList = getBrcaCnvExons(gene);
        if(!brcaCnvExonList.isEmpty()) {
            HBox box;
            String boxId = "brca2_";
            if(gene.equals("BRCA1")) {
                box = brca1ExonBox;
                boxId = "brca1_";
            } else {
                box = brca2ExonBox;
            }

            for (BrcaCnvExon exon : brcaCnvExonList) {
                String exonObjId = "5'UTR".equals(exon.getExon()) ? boxId + "utr5" : boxId + exon.getExon();
                Optional<Node> optionalNode = box.getChildren().stream()
                        .filter(obj -> obj.getId() != null && obj.getId().equals(exonObjId)).findFirst();

                optionalNode.ifPresent(node -> {
                    if(exon.getCopyNumber() == 1) {
                        node.getStyleClass().add("brca_cnv_1");
                    } else if(exon.getCopyNumber() == 3) {
                        node.getStyleClass().add("brca_cnv_3");
                    } else {
                        node.getStyleClass().removeAll("brca_cnv_3", "brca_cnv_1");
                    }
                    for(Node tempNode : ((HBox)node).getChildren()) {
                        if(tempNode instanceof Label) {
                            ((Label)tempNode).setText(exon.getCopyNumber().toString());
                        }
                    }
                });
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

    static class BooleanCell extends TableCell<BrcaCnvExon, Boolean> {
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

    private void changeCopyNumber(Integer value) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("sampleId", sample.getId());
            params.put("brcaCnvExonIds", getExportFields());
            params.put("comment", "N/A");
            params.put("cnv", value);
            apiService.put("analysisResults/brcaCnvExon/updateCnv", params, null, true);
            setList();
            String gene = exonTableView.getItems().get(0).getGene();
            String exon = exonTableView.getItems().get(0).getExon();
            setBrcaExonTableView(gene);
            setBrcaTableView(gene, exon);
        } catch (WebAPIException wae) {
            wae.printStackTrace();
        }
    }

    private List<BrcaCnvExon> getSelectedItemList() {
        if(exonTableView.getItems() == null) {
            return new ArrayList<>();
        }
        return exonTableView.getItems().stream().filter(BrcaCnvExon::getCheckItem)
                .collect(Collectors.toList());
    }

    private String getExportFields() {
        StringBuilder stringBuilder = new StringBuilder();

        getSelectedItemList().forEach(item -> stringBuilder.append(item.getId()).append(","));
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    @FXML
    public void doExonDeletion() {
        changeCopyNumber(1);
    }

    @FXML
    public void doExonDuplication() {
        changeCopyNumber(3);
    }

    @FXML
    public void doCnvReport() {
        changeCopyNumber(2);
    }

    @FXML
    public void exportExcel() {
        WorksheetUtil worksheetUtil = new WorksheetUtil();
        worksheetUtil.exportBrcaCnvData(this.getMainApp(), sample);
    }
}
