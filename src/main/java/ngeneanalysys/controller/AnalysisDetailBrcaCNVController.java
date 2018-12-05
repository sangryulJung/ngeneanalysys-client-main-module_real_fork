package ngeneanalysys.controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import ngeneanalysys.code.enums.BrcaAmpliconCopyNumberPredictionAlgorithmCode;
import ngeneanalysys.code.enums.BrcaCNVCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedBrcaCNV;
import ngeneanalysys.model.paged.PagedBrcaCNVExon;
import ngeneanalysys.model.render.SNPsINDELsList;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
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

    private CheckBox tableCheckBox;

    @FXML
    private Label brca1NMLabel;
    @FXML
    private Label brca2NMLabel;

    @FXML
    private HBox br4Body;
    @FXML
    private VBox br4Line;

    @FXML
    private HBox brLastBody;
    @FXML
    private VBox brLastLine;

    @FXML
    private RadioButton bicRadio;
    @FXML
    private RadioButton hgvsRadio;

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
    private TableColumn<BrcaCnvExon, String> exonExonTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> exonDomainTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> exonCopyNumberTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> exonWarningTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> exonCopyNumberOneAmpliconCountTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> exonCopyNumberTwoAmpliconCountTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> exonCopyNumberThreeAmpliconCountTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, Boolean> exonSeqTableColumn;
    @FXML
    private TableView<BrcaCnvAmplicon> cnvAmpliconTableView;
    @FXML
    private TableColumn<BrcaCnvAmplicon, String> ampliconNameTableColumn;
    @FXML
    private TableColumn<BrcaCnvAmplicon, String> ampliconWarningTableColumn;
    @FXML
    private TableColumn<BrcaCnvAmplicon, String> ampliconReferenceRatioTableColumn;
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

    private SampleView sample = null;
    private Panel panel = null;
    /**
     * @param variantsController AnalysisDetailVariantsController
     */
    void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    private void setNomenclature(String nomenclature) {

        if(nomenclature.equals("BIC")) {
            brca1NMLabel.setText("(NM_007294.3)");
            brca2NMLabel.setText("(NM_000059.3)");
            br4Line.setPrefWidth(5);
            br4Body.setPrefWidth(32);
            brLastLine.setPrefWidth(0);
            brLastBody.setPrefWidth(2);
            HBox.setHgrow(br4Line, Priority.ALWAYS);
            HBox.setHgrow(brLastLine, Priority.NEVER);
        } else {
            brca1NMLabel.setText("(NM_007294.3)");
            brca2NMLabel.setText("(NM_000059.3)");
            br4Line.setPrefWidth(0);
            br4Body.setPrefWidth(2);
            brLastLine.setPrefWidth(5);
            brLastBody.setPrefWidth(32);
            HBox.setHgrow(brLastLine, Priority.ALWAYS);
            HBox.setHgrow(br4Line, Priority.NEVER);
        }
        exonTableView.refresh();
        if(exonTableView.getSelectionModel().getSelectedItem() == null) exonTableView.getSelectionModel().selectFirst();
        BrcaCnvExon brcaCnvExon = exonTableView.getSelectionModel().getSelectedItem();
        setBrcaTableView(brcaCnvExon.getGene(), brcaCnvExon.getExon(), brcaCnvExon.getCopyNumber());
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("BRCA cnv view...");
        apiService = APIService.getInstance();
        sample = (SampleView)paramMap.get("sampleView");
        panel = sample.getPanel();

        exonTableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) exonTableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });

        cnvAmpliconTableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) cnvAmpliconTableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });

        exonTableView.setStyle(exonTableView.getStyle() + "-fx-selection-bar-non-focused : skyblue;");
        cnvAmpliconTableView.setStyle(exonTableView.getStyle() + "-fx-selection-bar-non-focused : skyblue;");

        bicRadio.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv) {
                setNomenclature("BIC");
            }
        });

        hgvsRadio.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv) {
                setNomenclature("HGVS");
            }
        });

        brca1RadioButton.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv) {
                setBrcaExonTableView("BRCA1");
            }
        });

        brca2RadioButton.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv) {
                setBrcaExonTableView("BRCA2");
            }
        });

        exonTableView.setRowFactory(tv -> {
            TableRow<BrcaCnvExon> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if(e.getClickCount() == 1) {
                    BrcaCnvExon brcaExonCNV = exonTableView.getSelectionModel().getSelectedItem();
                    setBrcaTableView(brcaExonCNV.getGene(), brcaExonCNV.getExon(), brcaExonCNV.getCopyNumber());
                }
            });
            return row;
        });

        ampliconNameTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getAmplicon()));
        ampliconReferenceRatioTableColumn.setCellValueFactory(item -> {
            if(BrcaAmpliconCopyNumberPredictionAlgorithmCode.DISTRIBUTION.getCode()
                    .equals(panel.getCnvConfigBRCAaccuTest().getAmpliconCopyNumberPredictionAlgorithm())) {
                return new SimpleStringProperty(
                        String.format("%.03f", item.getValue().getDistributionRangeMin()) + " - " +
                                String.format("%.03f", item.getValue().getDistributionRangeMax()));
            } else {
                return new SimpleStringProperty(
                        String.format("%.03f", item.getValue().getRawRangeMin()) + " - " +
                                String.format("%.03f", item.getValue().getRawRangeMax()));
            }
        });
        ampliconWarningTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getWarning()));
        ampliconWarningTableColumn.setCellFactory(param -> new TableCell<BrcaCnvAmplicon, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                setGraphic((StringUtils.isNotEmpty(item)) ? SNPsINDELsList.getWarningReasonPopOver(item, panel) : null);
            }
        });
        ampliconReferenceMedianDepthTableColumn.setCellValueFactory(item ->
                new SimpleObjectProperty<>(item.getValue().getReferenceMedianDepth()));
        ampliconSampleRatioTableColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSampleRatio()));
        ampliconSampleRatioTableColumn.setCellFactory(column ->
                new TableCell<BrcaCnvAmplicon, BigDecimal>() {
                    @Override
                    protected void updateItem(BigDecimal item, boolean empty) {
                        super.updateItem(item, empty);
                        setStyle(getStyle() + "; -fx-alignment : baseline-center;");
                        if(item == null || empty) {
                            setText(null);
                        } else {
                            BrcaCnvAmplicon amplicon = this.getTableView().getItems().get(this.getIndex());
                            setText(String.format("%.03f", item));
                            setTextFill(Color.BLACK);
                            if(amplicon != null) {
                                Double deletionGap = panel.getCnvConfigBRCAaccuTest().getLowConfidenceCnvDeletion();
                                Double duplicationGap = panel.getCnvConfigBRCAaccuTest().getLowConfidenceCnvDuplication();
                                if(BrcaAmpliconCopyNumberPredictionAlgorithmCode.DISTRIBUTION.getCode().
                                        equalsIgnoreCase(panel.getCnvConfigBRCAaccuTest().getAmpliconCopyNumberPredictionAlgorithm())) {
                                    if(amplicon.getDistributionPrediction().equals(2)) {
                                        if(duplicationGap != null && amplicon.getDistributionRangeMax()
                                                .subtract(amplicon.getSampleRatio())
                                                .compareTo(new BigDecimal(duplicationGap.toString())) < 0) {
                                            setTextFill(Color.rgb(168, 200, 232));
                                        } else if(deletionGap != null && amplicon.getSampleRatio()
                                                .subtract(amplicon.getDistributionRangeMin())
                                                .compareTo(new BigDecimal(deletionGap.toString())) < 0) {
                                            setTextFill(Color.rgb(240, 161, 181));
                                        }
                                    }
                                } else {
                                    if(amplicon.getRawPrediction().equals(2)) {
                                        if(duplicationGap != null && amplicon.getRawRangeMax()
                                                .subtract(amplicon.getSampleRatio())
                                                .compareTo(new BigDecimal(duplicationGap.toString())) < 0) {
                                            setTextFill(Color.rgb(168, 200, 232));
                                        } else if(deletionGap != null && amplicon.getSampleRatio()
                                                .subtract(amplicon.getRawRangeMin())
                                                .compareTo(new BigDecimal(deletionGap.toString())) < 0) {
                                            setTextFill(Color.rgb(240, 161, 181));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        );
        //ampliconCopyNumberTableColumn.setText("Copy\nNumber");
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
        BrcaCnvExon brcaCnvExon = exonTableView.getItems().get(0);
        if(brcaCnvExon != null) {
            setBrcaTableView(brcaCnvExon.getGene(), brcaCnvExon.getExon(), brcaCnvExon.getCopyNumber());
        }

        bicRadio.selectedProperty().setValue(true);

        variantsController.getDetailContents().setCenter(root);
    }

    private void brcaExonTableInit() {
        TableColumn<BrcaCnvExon, Boolean> checkBoxColumn = new TableColumn<>("");
        createCheckBoxTableHeader(checkBoxColumn);
        //checkBoxColumn.impl_setReorderable(false); 컬럼 이동 방지 코드
        checkBoxColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue() != null ));
        checkBoxColumn.setCellFactory(param -> new BooleanCell());

        exonCopyNumberTableColumn.getStyleClass().add("alignment_center");
        exonCopyNumberTableColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getExpertCnv() != null ?
                        cellData.getValue().getExpertCnv() : cellData.getValue().getSwCnv()));
        exonCopyNumberTableColumn.setCellFactory(param -> new TableCell<BrcaCnvExon, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                if(item == null || empty) {
                    setGraphic(null);
                    return;
                }
                BrcaCnvExon brcaCnvExon = getTableView().getItems().get(getIndex());
                Label label = new Label(BrcaCNVCode.findInitial(item));
                label.getStyleClass().remove("label");
                if(brcaCnvExon.getExpertCnv() != null) {
                    if (item.equalsIgnoreCase(BrcaCNVCode.NORMAL.getCode())) {
                        label.getStyleClass().add("expert_cnv_normal");
                    } else if (item.equalsIgnoreCase(BrcaCNVCode.AMPLIFICATION.getCode())) {
                        label.getStyleClass().add("expert_cnv_duplication");
                    } else if (item.equalsIgnoreCase(BrcaCNVCode.DELETION.getCode())) {
                        label.getStyleClass().add("expert_cnv_deletion");
                    } else {
                        setGraphic(null);
                        return;
                    }
                } else {

                    if (item.equalsIgnoreCase(BrcaCNVCode.NORMAL.getCode())) {
                        label.getStyleClass().add("cnv_normal");
                    } else if (item.equalsIgnoreCase(BrcaCNVCode.AMPLIFICATION.getCode())) {
                        label.getStyleClass().add("cnv_duplication");
                    } else if (item.equalsIgnoreCase(BrcaCNVCode.DELETION.getCode())) {
                        label.getStyleClass().add("cnv_deletion");
                    } else {
                        setGraphic(null);
                        return;
                    }
                }
                setGraphic(label);
            }
        });
        exonExonTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExon()));
        exonExonTableColumn.setCellFactory(column ->
                new TableCell<BrcaCnvExon, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setStyle(getStyle() + "; -fx-alignment : baseline-right;");
                        if(item == null || empty) {
                            setText(null);
                        } else {
                            BrcaCnvExon brcaCnvExon = getTableView().getItems().get(getIndex());
                            if(hgvsRadio.isSelected() && brcaCnvExon.getGene().equals("BRCA1")) {
                                if(brcaCnvExon.getExon().contains("UTR")) {
                                    setText(item);
                                } else {
                                    int a = Integer.parseInt(item);
                                    if(a >= 5) {
                                        setText(String.valueOf(a - 1));
                                    } else {
                                        setText(item);
                                    }
                                }
                            } else {
                                setText(item);
                            }
                        }
                    }
                }
        );
        exonExonTableColumn.setComparator((a,  b) -> {
            if(a.contains("UTR")) {
                return -1;
            } else if(b.contains("UTR")) {
                return 1;
            }else {
                int intA = Integer.parseInt(a);
                int intB = Integer.parseInt(b);
                return Integer.compare(intA, intB);
            }
        });
        exonDomainTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDomain()));
        exonCopyNumberOneAmpliconCountTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty((cellData.getValue().getCopyNumberOneAmpliconPercentage() * 100) + "%"));
        exonCopyNumberTwoAmpliconCountTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty((cellData.getValue().getCopyNumberTwoAmpliconPercentage() * 100) + "%"));
        exonCopyNumberThreeAmpliconCountTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty((cellData.getValue().getCopyNumberThreeAmpliconPercentage() * 100 ) + "%"));
        exonWarningTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getWarning()));
        exonWarningTableColumn.setCellFactory(param -> new TableCell<BrcaCnvExon, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                setGraphic((StringUtils.isNotEmpty(item)) ? SNPsINDELsList.getWarningReasonPopOver(item, panel) : null);
            }
        });
        exonSeqTableColumn.setCellValueFactory(param -> new SimpleBooleanProperty(brcaCnvAmpliconList.stream()
                .anyMatch(item -> item.getExon().equalsIgnoreCase(param.getValue().getExon()))));
        exonSeqTableColumn.setCellFactory(param -> new TableCell<BrcaCnvExon, Boolean>(){
            @Override
            public void updateItem(Boolean item, boolean empty) {
                if(!empty && item) {
                    BrcaCnvExon brcaCnvExon = getTableView().getItems().get(getIndex());
                    List<BrcaCnvAmplicon> brcaCnvAmplicons = brcaCnvAmpliconList.stream().filter(brcaCnvAmplicon ->
                            brcaCnvExon.getGene().equalsIgnoreCase(brcaCnvAmplicon.getGene())
                                    && brcaCnvExon.getExon().equalsIgnoreCase(brcaCnvAmplicon.getExon()))
                            .collect(Collectors.toList());
                    int maxWidth = 110;
                    int maxHeight = 12;
                    Canvas canvas = new Canvas(maxWidth, maxHeight);
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    double size = (double)maxWidth / brcaCnvAmplicons.size();
                    for(int idx = 0; idx < brcaCnvAmplicons.size(); idx++) {
                        BrcaCnvAmplicon amplicon = brcaCnvAmplicons.get(idx);
                        Double deletionGap = panel.getCnvConfigBRCAaccuTest().getLowConfidenceCnvDeletion();
                        Double duplicationGap = panel.getCnvConfigBRCAaccuTest().getLowConfidenceCnvDuplication();
                        if(BrcaAmpliconCopyNumberPredictionAlgorithmCode.DISTRIBUTION.getCode().
                                equalsIgnoreCase(panel.getCnvConfigBRCAaccuTest().getAmpliconCopyNumberPredictionAlgorithm())) {
                            if(amplicon.getDistributionPrediction().equals(1)) {
                                gc.setFill(Color.rgb(240, 73, 120));
                            } else if(amplicon.getDistributionPrediction().equals(2)) {
                                if(duplicationGap != null && amplicon.getDistributionRangeMax()
                                        .subtract(amplicon.getSampleRatio())
                                        .compareTo(new BigDecimal(duplicationGap.toString())) < 0) {
                                    gc.setFill(Color.rgb(168, 200, 232));
                                } else if(deletionGap != null && amplicon.getSampleRatio()
                                        .subtract(amplicon.getDistributionRangeMin())
                                        .compareTo(new BigDecimal(deletionGap.toString())) < 0) {
                                    gc.setFill(Color.rgb(240, 161, 181));
                                } else {
                                    gc.setFill(Color.LIGHTGRAY);
                                }
                            } else {
                                gc.setFill(Color.rgb(45, 112, 232));
                            }
                        } else {
                            if(amplicon.getRawPrediction().equals(1)) {
                                gc.setFill(Color.rgb(240, 73, 120));
                            } else if(amplicon.getRawPrediction().equals(2)) {
                                if(duplicationGap != null && amplicon.getRawRangeMax()
                                        .subtract(amplicon.getSampleRatio())
                                        .compareTo(new BigDecimal(duplicationGap.toString())) < 0) {
                                    gc.setFill(Color.rgb(168, 200, 232));
                                } else if(deletionGap != null && amplicon.getSampleRatio()
                                        .subtract(amplicon.getRawRangeMin())
                                        .compareTo(new BigDecimal(deletionGap.toString())) < 0) {
                                    gc.setFill(Color.rgb(240, 161, 181));
                                } else {
                                    gc.setFill(Color.LIGHTGRAY);
                                }
                            } else {
                                gc.setFill(Color.rgb(45, 112, 232));
                            }
                        }

                        double xPos = idx * size;
                        double wPos = xPos + size;
                        gc.fillRect(xPos, 0, wPos, maxHeight);

                        if(wPos < maxWidth) {
                            gc.setFill(Color.WHITE);
                            gc.fillRect(wPos - 0.4, 0, wPos, maxHeight);
                        }
                    }
                    this.setAlignment(Pos.CENTER);
                    setGraphic(canvas);
                }
            }
        });

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
                if(a.getExon().contains("UTR")) {
                    return -1;
                } else if(b.getExon().contains("UTR")) {
                    return 1;
                }else {
                    int intA = Integer.parseInt(a.getExon());
                    int intB = Integer.parseInt(b.getExon());
                    return Integer.compare(intA, intB);
                }
            }).collect(Collectors.toList());

            Platform.runLater(() -> setBrcaCnvPlot("BRCA1"));
            Platform.runLater(() -> setBrcaCnvPlot("BRCA2"));
        } catch (WebAPIException wae) {
            DialogUtil.warning(wae.getHeaderText(), wae.getMessage(), mainApp.getPrimaryStage(), true);
        }
    }

    private void setBrcaExonTableView(final String gene) {
        tableCheckBox.setSelected(false);
        List<BrcaCnvExon> list = getBrcaCNVExon(gene);

        long delCount = list.stream().filter(item -> (item.getExpertCnv() != null
                && item.getExpertCnv().equals(BrcaCNVCode.DELETION.getCode()))
                || item.getSwCnv().equals(BrcaCNVCode.DELETION.getCode())).count();

        long dupCount = list.stream().filter(item -> (item.getExpertCnv() != null
                && item.getExpertCnv().equals(BrcaCNVCode.AMPLIFICATION.getCode()))
                || item.getSwCnv().equals(BrcaCNVCode.AMPLIFICATION.getCode())).count();

        cnvExonLabel.setText("CNV EXON INFORMATION (" + gene.toUpperCase() + " - Del: " + delCount + ", Dup: " + dupCount + ")");
        if(exonTableView.getItems() != null) {
            exonTableView.getItems().removeAll(exonTableView.getItems());
        }

        if(!list.isEmpty()) exonTableView.getItems().addAll(list);
        exonTableView.refresh();
        if(exonTableView.getSelectionModel().getSelectedItem() == null) exonTableView.getSelectionModel().selectFirst();
        BrcaCnvExon brcaCnvExon = exonTableView.getSelectionModel().getSelectedItem();
        setBrcaTableView(brcaCnvExon.getGene(), brcaCnvExon.getExon(), brcaCnvExon.getCopyNumber());
    }

    private void setBrcaTableView(final String gene, final String exon, int copyNumber) {
        String exonName;
        if(exon.contains("UTR")) {
            exonName = exon;
        } else if(gene.equals("BRCA2") || bicRadio.isSelected()){
            exonName = "EXON " + exon;
        } else {
            Integer val = Integer.parseInt(exon);
            if(val > 4) {
                exonName = "EXON " + (val - 1);
            } else {
                exonName = "EXON " + exon;
            }
        }
        cnvDetailLabel.setText("CNV DETAIL INFORMATION (" + exonName + " - Copy Number: " + copyNumber + ")");
        if(cnvAmpliconTableView.getItems() != null) {
            cnvAmpliconTableView.getItems().removeAll(cnvAmpliconTableView.getItems());
            cnvAmpliconTableView.refresh();
        }

        List<BrcaCnvAmplicon> list = getBrcaCnvAmpliconsByExon(gene, exon);
        ampliconNameTableColumn.setText("Amplicon (" + list.size() + ")");
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
                String exonObjId = "5'UTR".equals(exon.getExon()) ? boxId + "utr5" : boxId + "exon" + exon.getExon();
                Optional<Node> optionalNode = box.getChildren().stream()
                        .filter(obj -> obj.getId() != null && obj.getId().equals(exonObjId)).findFirst();

                optionalNode.ifPresent(node -> {
                    node.getStyleClass().removeAll("brca_cnv_3", "brca_cnv_1",
                            "brca_cnv_3_expert", "brca_cnv_1_expert");
                    if(exon.getExpertCnv() != null &&
                            BrcaCNVCode.DELETION.getCode().equalsIgnoreCase(exon.getExpertCnv())) {
                        node.getStyleClass().add("brca_cnv_1_expert");
                    } else if(BrcaCNVCode.DELETION.getCode().equalsIgnoreCase(exon.getSwCnv())) {
                        node.getStyleClass().add("brca_cnv_1");
                    } else if(exon.getExpertCnv() != null &&
                            BrcaCNVCode.AMPLIFICATION.getCode().equalsIgnoreCase(exon.getExpertCnv())) {
                        node.getStyleClass().add("brca_cnv_3_expert");
                    } else if(BrcaCNVCode.AMPLIFICATION.getCode().equalsIgnoreCase(exon.getSwCnv())) {
                        node.getStyleClass().add("brca_cnv_3");
                    }
                    for(Node tempNode : ((HBox)node).getChildren()) {
                        if(tempNode instanceof Label) {
                            ((Label)tempNode).setText(exon.getExpertCnv() != null ?
                                    BrcaCNVCode.findInitial(exon.getExpertCnv()) :
                                    BrcaCNVCode.findInitial(exon.getSwCnv()));
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
        tableCheckBox = box;
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

        column.setPrefWidth(30d);

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

    private void changeCopyNumber(String value) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("sampleId", sample.getId());
            params.put("brcaCnvExonIds", getExportFields());
            params.put("comment", "N/A");
            params.put("cnv", value);
            apiService.put("analysisResults/brcaCnvExon/updateCnv", params, null, true);
            setList();
            String gene = exonTableView.getItems().get(0).getGene();
            setBrcaExonTableView(gene);
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
        changeCopyNumber(BrcaCNVCode.DELETION.getCode());
    }

    @FXML
    public void doExonDuplication() {
        changeCopyNumber(BrcaCNVCode.AMPLIFICATION.getCode());
    }

    @FXML
    public void doCnvReport() {
        changeCopyNumber(BrcaCNVCode.NORMAL.getCode());
    }

    @FXML
    public void exportExcel() {
        WorksheetUtil worksheetUtil = new WorksheetUtil();
        worksheetUtil.exportGermlineCnvData(this.getMainApp(), sample, true, false);
    }

    private void setTableView(final String gene, final String exon) {
        if(gene.equalsIgnoreCase("BRCA1")) {
            brca1RadioButton.selectedProperty().setValue(true);
        } else {
            brca2RadioButton.selectedProperty().setValue(true);
        }

        Optional<BrcaCnvExon> optional = exonTableView.getItems().stream()
                .filter(item -> item.getExon().equalsIgnoreCase(exon)).findFirst();
        optional.ifPresent(brcaCnvExon -> {
            exonTableView.getSelectionModel().select(brcaCnvExon);
            exonTableView.scrollTo(brcaCnvExon);
            setBrcaTableView(gene, exon, brcaCnvExon.getCopyNumber());
        });
    }

    @FXML
    public void exon_plot_click(Event e) {
        Node obj = (Node)e.getSource();
        String gene = obj.getId().split("_")[0].toUpperCase();
        String exon = getExonName(obj.getId().split("_")[1].replaceAll("exon", ""));
        setTableView(gene, exon);
    }

    private String getExonName(String split) {
        if(StringUtils.isEmpty(split)) {
            return "";
        }
        return split.contains("utr") ? "5'UTR" : split.toUpperCase();
    }
}
