package ngeneanalysys.controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.BrcaAmpliconCopyNumberPredictionAlgorithmCode;
import ngeneanalysys.code.enums.BrcaCNVCode;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.paged.PagedBrcaCNV;
import ngeneanalysys.model.paged.PagedBrcaCNVExon;
import ngeneanalysys.model.paged.PagedBrcaCnvLog;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.model.render.SNPsINDELsList;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang.WordUtils;
import org.controlsfx.control.PopOver;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2018-10-15
 */
public class AnalysisDetailBrcaCNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    private static final double AMPLIFICATION_HEIGHT = 16;

    private static final double NORMAL_HEIGHT = 36;

    private static final double DELETION_HEIGHT = 56;

    private static final double LABEL_CENTER_FIX = 0.5;

    private CheckBox tableCheckBox;

    private final String[] brcaExonThinArea = new String[]{"BRCA1_1", "BRCA1_2", "BRCA1_24", "BRCA2_1", "BRCA2_2",
            "BRCA2_27"};

    private final String[] brcaCmcNoneTargetArea = new String[]{"BRCA1_24", "BRCA2_2", "BRCA2_27"};

    private final String[] brcaMlpaNoneTargetArea = new String[]{"BRCA1_24", "BRCA2_2", "BRCA2_27"};

    private final String[] brcaV2NoneTargetArea = new String[]{"BRCA1_24", "BRCA2_2", "BRCA2_27"};

    private final String[] brcaAndPlusNoneTargetArea = new String[]{"BRCA1_24", "BRCA2_2", "BRCA2_27"};

    @FXML
    private RadioButton bicNomenclatureRadioButton;
    @FXML
    private RadioButton hgvsNomenclatureRadioButton;

    @FXML
    private ComboBox<ComboBoxItem> geneComboBox;

    @FXML
    private StackPane mainStackPane;

    @FXML
    private ScrollPane brca1ScrollPane;
    @FXML
    private ScrollPane brca2ScrollPane;

    @FXML
    private GridPane brca1PlotGrid;
    @FXML
    private AnchorPane brca1PlotAnchorPane;
    @FXML
    private HBox brca1BoxPlotHBox;
    @FXML
    private HBox brca1ExonNumberHBox;
    @FXML
    private HBox brca1DomainHBox;

    @FXML
    private GridPane brca2PlotGrid;
    @FXML
    private AnchorPane brca2PlotAnchorPane;
    @FXML
    private HBox brca2BoxPlotHBox;
    @FXML
    private HBox brca2ExonNumberHBox;
    @FXML
    private HBox brca2DomainHBox;

    @FXML
    private TableView<BrcaCnvExon> brcaCnvTable;
    @FXML
    private TableColumn<BrcaCnvExon, String> geneTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> exonTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> cnvTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> warningTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> domainTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, Integer> totalTableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, Integer> copyNumber1TableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, Integer> copyNumber2TableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, Integer> copyNumber3TableColumn;
    @FXML
    private TableColumn<BrcaCnvExon, String> commentTableColumn;

    private APIService apiService;

    private AnalysisDetailVariantsController variantsController;

    private List<BrcaCnvAmplicon> brcaCnvAmpliconList;

    private List<BrcaCnvExon> brcaCnvExonList;

    private List<Label> brca1LabelList;

    private List<Label> brca2LabelList;

    private List<Line> brca1LineList;

    private List<Line> brca2LineList;

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

        brca1LabelList = new ArrayList<>();
        brca2LabelList = new ArrayList<>();
        brca1LineList = new ArrayList<>();
        brca2LineList = new ArrayList<>();

        geneComboBox.setConverter(new ComboBoxConverter());
        geneComboBox.getItems().add(new ComboBoxItem("BRCA1", "BRCA1 (NM_007294.3)"));
        geneComboBox.getItems().add(new ComboBoxItem("BRCA2", "BRCA2 (NM_000059.3)"));
        geneComboBox.getSelectionModel().selectFirst();
        geneComboBox.valueProperty().addListener((ob, ov, nv) -> {
            if(nv != null) {
                setTableItem(nv.getValue());
                if(nv.getValue().equals("BRCA1")) {
                    brca1ScrollPane.setVisible(true);
                    brca2ScrollPane.setVisible(false);
                } else {
                    brca1ScrollPane.setVisible(false);
                    brca2ScrollPane.setVisible(true);
                }
            }
        });
        initTable();
        setList();

        bicNomenclatureRadioButton.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv) {
                setNomenclature("BIC");
            }
        });

        hgvsNomenclatureRadioButton.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv) {
                setNomenclature("HGVS");
            }
        });
        mainStackPane.widthProperty().addListener((ob, ov, nv) -> resizeImage((double)nv));
        variantsController.getDetailContents().setCenter(root);
    }

    private void setNomenclature(String nomenclature) {
        List<BrcaCnvExon> list = getBrcaCnvExons("BRCA1");
        list = list.stream().filter(brcaCnvExon -> !brcaCnvExon.getExon().equalsIgnoreCase("Promoter"))
                .collect(Collectors.toList());
        int idx = 0;
        if(nomenclature.equals("HGVS")) {
            if(!brca1ExonNumberHBox.getChildren().isEmpty()) {
                for(Node node : brca1ExonNumberHBox.getChildren()) {
                    if(node instanceof Label) {
                        ((Label) node).setText(list.get(idx++).getExon());
                    }
                }
            }
        } else {
            if(!brca1ExonNumberHBox.getChildren().isEmpty()) {
                for(Node node : brca1ExonNumberHBox.getChildren()) {
                    if(node instanceof Label) {
                        String exon = list.get(idx++).getExon();
                        if(Integer.parseInt(exon) >= 4) {
                            exon = String.valueOf((Integer.parseInt(exon) + 1));
                        }
                        ((Label) node).setText(exon);
                    }
                }
            }
        }
        brcaCnvTable.refresh();
    }

    private void resizeImage(double value) {
        resizeGridPane(getTotalAmpliconCountInGene("BRCA1"), brca1PlotGrid, value);
        resizeGridPane(getTotalAmpliconCountInGene("BRCA2"), brca2PlotGrid, value);
        rePositionLabel("BRCA1", brca1LabelList, value);
        rePositionLabel("BRCA2", brca2LabelList, value);
        rePositionLine(brca1LabelList, brca1LineList);
        rePositionLine(brca2LabelList, brca2LineList);
        reSizeBoxPlot("BRCA1", brca1BoxPlotHBox, brca1ExonNumberHBox, value);
        reSizeBoxPlot("BRCA2", brca2BoxPlotHBox, brca2ExonNumberHBox, value);
        reSizeDomainArea("BRCA1", brca1DomainHBox, value);
        reSizeDomainArea("BRCA2", brca2DomainHBox, value);
    }

    private void reSizeBoxPlot(String gene, HBox box, HBox exonNumBox, double width) {
        List<Node> itemList = box.getChildren();
        double lineSize = calcOneLineSize(getTotalAmpliconCountInGene(gene), width);
        for(int idx = 0; idx < itemList.size() - 2; idx++) {
            Node item = itemList.get(idx);

            if(StringUtils.isNotEmpty(item.getId())) {
                String[] name = item.getId().split("_");
                List<BrcaCnvAmplicon> amplicons = getBrcaCnvAmpliconsByExon(gene, name[1]);
                double boxSize = calcOneBoxSize(getTotalAmpliconCountInGene(gene), width) * amplicons.size() - lineSize;
                reSizeNodeWidth(item, boxSize);
                reSizeNodeWidth(exonNumBox.getChildren().get(idx), boxSize);
                List<Node> labelList = ((HBox)item).getChildren();
                if(!labelList.isEmpty()) {
                    if (labelList.size() > 1) {
                        if (name[1].equals("24") || name[1].equals("27")) {
                            ((Label) labelList.get(0)).setMinWidth(boxSize - 4);
                        } else {
                            ((Label) labelList.get(1)).setMinWidth(boxSize - 4);
                        }
                    } else {
                        ((Label) labelList.get(0)).setMinWidth(boxSize);
                    }
                }
            } else {
                reSizeNodeWidth(item, lineSize);
                reSizeNodeWidth(exonNumBox.getChildren().get(idx), lineSize);
            }
        }
    }

    private void rePositionLabel(String gene, List<Label> list, double value) {
        double positionX = (calcOneBoxSize(getTotalAmpliconCountInGene(gene)) / 2) - 5;
        for(Label label: list) {
            AnchorPane.setLeftAnchor(label, positionX);
            positionX += calcOneBoxSize(getTotalAmpliconCountInGene(gene), value);
        }
    }

    private void rePositionLine(List<Label> labelList, List<Line> list) {
        int totalIdx = labelList.size() - 1;

        for(int idx = 0; idx < totalIdx; idx++) {
            Line line = list.get(idx);
            line.setStartX(AnchorPane.getLeftAnchor(labelList.get(idx)) + 3);
            line.setStartY(AnchorPane.getTopAnchor(labelList.get(idx)) + 3);

            line.setEndX(AnchorPane.getLeftAnchor(labelList.get(idx + 1)) + 3);
            line.setEndY(AnchorPane.getTopAnchor(labelList.get(idx + 1)) + 3);
        }
    }

    private void initTable() {
        brcaCnvTable.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) brcaCnvTable.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });

        TableColumn<BrcaCnvExon, Boolean> checkBoxColumn = new TableColumn<>("");
        createCheckBoxTableHeader(checkBoxColumn);
        checkBoxColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue() != null ));
        checkBoxColumn.setCellFactory(param -> new BooleanCell());

        geneTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGene()));
        cnvTableColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getExpertCnv() != null ?
                        cellData.getValue().getExpertCnv() : cellData.getValue().getSwCnv()));
        cnvTableColumn.setCellFactory(param -> new TableCell<BrcaCnvExon, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                if(item == null || empty) {
                    setGraphic(null);
                    return;
                }
                BrcaCnvExon brcaCnvExon = getTableView().getItems().get(getIndex());
                Label label = new Label(WordUtils.capitalize(item));
                Tooltip toolTip = new Tooltip(item);
                label.setTooltip(toolTip);
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

                label.setCursor(Cursor.HAND);
                label.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> popUp(Arrays.asList(brcaCnvExon), "Modify CNV in individual"));

                setGraphic(label);
            }
        });
        exonTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExon()));
        exonTableColumn.setCellFactory(column ->
                new TableCell<BrcaCnvExon, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setStyle(getStyle() + "; -fx-alignment : baseline-right;");
                        if(item == null || empty) {
                            setText(null);
                        } else {
                            BrcaCnvExon brcaCnvExon = getTableView().getItems().get(getIndex());
                            if(bicNomenclatureRadioButton.isSelected() && brcaCnvExon.getGene().equals("BRCA1")) {
                                if(brcaCnvExon.getExon().matches("[a-zA-Z]*")) {
                                    setText(item);
                                } else {
                                    int a = Integer.parseInt(item);
                                    if(a >= 4) {
                                        setText(String.valueOf(a + 1));
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
        exonTableColumn.setComparator((a,  b) -> {
            if(a.matches("[a-zA-Z]*")) {
                return -1;
            } else if(b.matches("[a-zA-Z]*")) {
                return 1;
            }else {
                int intA = Integer.parseInt(a);
                int intB = Integer.parseInt(b);
                return Integer.compare(intA, intB);
            }
        });
        domainTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDomain()));
        warningTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getWarning()));
        warningTableColumn.setCellFactory(param -> new TableCell<BrcaCnvExon, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                setGraphic((StringUtils.isNotEmpty(item)) ? SNPsINDELsList.getWarningReasonPopOver(item, panel) : null);
            }
        });
        totalTableColumn.setCellValueFactory(cellData -> {
            List<BrcaCnvAmplicon> amplicons = getBrcaCnvAmpliconsByExon(cellData.getValue().getGene(), cellData.getValue().getExon());
            return new SimpleObjectProperty<>(amplicons.size());
        });
        copyNumber1TableColumn.setCellValueFactory(cellData -> {
            List<BrcaCnvAmplicon> amplicons = getBrcaCnvAmpliconsByExon(cellData.getValue().getGene(), cellData.getValue().getExon());

            int size;
            if(BrcaAmpliconCopyNumberPredictionAlgorithmCode.DISTRIBUTION.getCode().equals(
                    panel.getCnvConfigBRCAaccuTest().getAmpliconCopyNumberPredictionAlgorithm())) {
                size = (int)(amplicons.stream().filter(item -> item.getDistributionPrediction() == 1).count());
            } else {
                size = (int)(amplicons.stream().filter(item -> item.getRawPrediction() == 1).count());
            }
            return new SimpleObjectProperty<>(size);
        });
        copyNumber2TableColumn.setCellValueFactory(cellData -> {
            List<BrcaCnvAmplicon> amplicons = getBrcaCnvAmpliconsByExon(cellData.getValue().getGene(), cellData.getValue().getExon());

            int size;
            if(BrcaAmpliconCopyNumberPredictionAlgorithmCode.DISTRIBUTION.getCode().equals(
                    panel.getCnvConfigBRCAaccuTest().getAmpliconCopyNumberPredictionAlgorithm())) {
                size = (int)(amplicons.stream().filter(item -> item.getDistributionPrediction() == 2).count());
            } else {
                size = (int)(amplicons.stream().filter(item -> item.getRawPrediction() == 2).count());
            }
            return new SimpleObjectProperty<>(size);
        });
        copyNumber3TableColumn.setCellValueFactory(cellData -> {
            List<BrcaCnvAmplicon> amplicons = getBrcaCnvAmpliconsByExon(cellData.getValue().getGene(), cellData.getValue().getExon());

            int size;
            if(BrcaAmpliconCopyNumberPredictionAlgorithmCode.DISTRIBUTION.getCode().equals(
                    panel.getCnvConfigBRCAaccuTest().getAmpliconCopyNumberPredictionAlgorithm())) {
                size = (int)(amplicons.stream().filter(item -> item.getDistributionPrediction() == 3).count());
            } else {
                size = (int)(amplicons.stream().filter(item -> item.getRawPrediction() == 3).count());
            }
            return new SimpleObjectProperty<>(size);
        });
        commentTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComment()));
        commentTableColumn.setCellFactory(param -> new TableCell<BrcaCnvExon, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                if(StringUtils.isNotEmpty(item)) {
                    setText(item);
                    this.setCursor(Cursor.HAND);
                    BrcaCnvExon brcaCnvExon = getTableView().getItems().get(getIndex());
                    this.setOnMouseClicked(ev -> logPopOver(brcaCnvExon.getId(), this));
                } else {
                    this.setCursor(Cursor.DEFAULT);
                    this.setOnMouseClicked(null);
                    setText(null);
                }
            }
        });
    }

    private void logPopOver(Integer id, TableCell<BrcaCnvExon, String> cell) {
        try {
            HttpClientResponse  response = apiService.get("/analysisResults/brcaCnvExonLog/" + id, null, null, null);
            PagedBrcaCnvLog pagedBrcaCnvLog = response.getObjectBeforeConvertResponseToJSON(PagedBrcaCnvLog.class);

            PopOver popOver = new PopOver();
            popOver.setMinWidth(650);
            popOver.setHeight(150);
            popOver.setMaxHeight(150);

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.setMinWidth(650);
            hBox.setMaxWidth(650);
            hBox.setMinHeight(150);
            hBox.setMaxHeight(150);

            TableView<BrcaCnvLog> tableView = new TableView<>();
            tableView.setMinWidth(600);
            tableView.setPrefWidth(600);
            tableView.setMaxWidth(600);
            tableView.setMinHeight(120);
            tableView.setPrefHeight(120);
            tableView.setMaxHeight(120);
            tableView.getStyleClass().add("cnvTable");
            TableColumn<BrcaCnvLog, String> oldValue = new TableColumn<>();
            oldValue.setText("Old Value");
            oldValue.setMinWidth(95);
            oldValue.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOldValue()));
            TableColumn<BrcaCnvLog, String> newValue = new TableColumn<>();
            newValue.setText("New Value");
            newValue.setMinWidth(95);
            newValue.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNewValue()));
            TableColumn<BrcaCnvLog, String> comment = new TableColumn<>();
            comment.setText("Comment");
            comment.setMinWidth(270);
            comment.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComment()));
            TableColumn<BrcaCnvLog, String> dateTime = new TableColumn<>();
            dateTime.setText("Created at");
            dateTime.setMinWidth(140);
            dateTime.setCellValueFactory(cellData -> new SimpleStringProperty(
                    timeConvertString(cellData.getValue().getCreatedAt())));
            tableView.getColumns().addAll(oldValue, newValue, comment, dateTime);

            tableView.getItems().addAll(pagedBrcaCnvLog.getResult());
            hBox.getChildren().add(tableView);
            popOver.getRoot().setAlignment(Pos.CENTER);
            popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_CENTER);
            //popOver.getRoot().setOpaqueInsets(new Insets(5, 5, 5, 5));
            popOver.setHeaderAlwaysVisible(true);
            popOver.setAutoHide(true);
            popOver.setAutoFix(true);
            popOver.setDetachable(true);
            popOver.setArrowSize(15);
            popOver.setArrowIndent(30);
            popOver.setTitle("");
            popOver.setContentNode(hBox);
            popOver.show(cell);

        } catch (WebAPIException  wae) {
            DialogUtil.error(wae.getHeaderText(), wae.getMessage(), mainApp.getPrimaryStage(), true);
        }
    }

    private String timeConvertString(DateTime date) {
        if(date == null)
            return "";

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return fmt.print(date);
    }

    private void popUp(List<BrcaCnvExon> changeList, String title) {
        if(changeList != null && !changeList.isEmpty()) {
            try {
                FXMLLoader loader = getMainApp().load(FXMLConstants.BATCH_BRCA_CNV);
                Node node = loader.load();
                BatchChangeBrcaCnvDialogController controller = loader.getController();
                controller.settingItem(sample.getId(), changeList, this, title);
                controller.setParamMap(getParamMap());
                controller.setMainController(mainController);
                controller.show((Parent) node);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setTableItem(String gene) {
        if(brcaCnvTable.getItems() != null) {
            brcaCnvTable.getItems().removeAll(brcaCnvTable.getItems());
        }

        List<BrcaCnvExon> list = brcaCnvExonList.stream().filter(item -> item.getExon().matches("[0-9]*"))
                .collect(Collectors.toList());

        if(!list.isEmpty()) {
            List<BrcaCnvExon> brca1List = list.stream().filter(item -> item.getGene().equals(gene)).collect(Collectors.toList());
            brcaCnvTable.getItems().addAll(brca1List);
        }
        brcaCnvTable.refresh();
        brcaCnvTable.scrollTo(0);
    }

    public void setList() {
        try {
            HttpClientResponse response = apiService.get("/analysisResults/brcaCnv/" + sample.getId(), null, null, false);
            PagedBrcaCNV pagedBrcaCNV = response.getObjectBeforeConvertResponseToJSON(PagedBrcaCNV.class);
            brcaCnvAmpliconList = pagedBrcaCNV.getResult();

            response = apiService.get("/analysisResults/brcaCnvExon/" + sample.getId(), null, null, false);
            PagedBrcaCNVExon pagedBrcaCNVExon = response.getObjectBeforeConvertResponseToJSON(PagedBrcaCNVExon.class);
            brcaCnvExonList = pagedBrcaCNVExon.getResult().stream().sorted((a, b) ->
            {
                if(a.getExon().matches("[a-zA-Z]*")) {
                    return -1;
                } else if(b.getExon().matches("[a-zA-Z]*")) {
                    return 1;
                } else {
                    int intA = Integer.parseInt(a.getExon());
                    int intB = Integer.parseInt(b.getExon());
                    return Integer.compare(intA, intB);
                }
            }).collect(Collectors.toList());

            setTableItem(geneComboBox.getSelectionModel().getSelectedItem().getValue());

            initImageArea();

        } catch (WebAPIException wae) {
            DialogUtil.warning(wae.getHeaderText(), wae.getMessage(), mainApp.getPrimaryStage(), true);
        }
    }

    private void initImageArea() {
        if(brca1LabelList.isEmpty()) {
            String brca1 = "BRCA1";
            resizeGridPane(getTotalAmpliconCountInGene(brca1), brca1PlotGrid);
            createLabelList(brca1, brca1LabelList, brca1PlotAnchorPane);
            drawConnectLabelLine(brca1LabelList, brca1PlotAnchorPane, brca1LineList);
            createBoxPlot(brca1, brca1BoxPlotHBox);
            createBoxPlotNumber(brca1, brca1ExonNumberHBox);
            drawDomainArea(brca1, brca1DomainHBox);
        } else {
            rePaintingPlotBox(brca1BoxPlotHBox);
        }
        if(brca2LabelList.isEmpty()) {
            String brca2 = "BRCA2";
            resizeGridPane(getTotalAmpliconCountInGene(brca2), brca2PlotGrid);
            createLabelList(brca2, brca2LabelList, brca2PlotAnchorPane);
            drawConnectLabelLine(brca2LabelList, brca2PlotAnchorPane, brca2LineList);
            createBoxPlot(brca2, brca2BoxPlotHBox);
            createBoxPlotNumber(brca2, brca2ExonNumberHBox);
            drawDomainArea(brca2, brca2DomainHBox);
        } else {
            rePaintingPlotBox(brca2BoxPlotHBox);
        }
    }

    private void rePaintingPlotBox(HBox box) {
        box.getChildren().forEach(item -> {
            if(item instanceof HBox && StringUtils.isNotEmpty(item.getId())) {
                String[] key = item.getId().split("_");
                Optional<BrcaCnvExon> optionalBrcaCnvExon = brcaCnvExonList.stream().filter(exon ->
                        exon.getExon().equals(key[1]) && exon.getGene().equals(key[0])).findFirst();
                optionalBrcaCnvExon.ifPresent(brcaCnvExon -> paintPlotBox((HBox)item, brcaCnvExon));
            }
        });
    }

    private void reSizeNodeWidth(Node node, double width) {
        if(node instanceof Label) {
            ((Label)node).setMinWidth(width);
            ((Label)node).setPrefWidth(width);
            ((Label)node).setMaxWidth(width);
        } else if(node instanceof HBox) {
            ((HBox)node).setMinWidth(width);
            ((HBox)node).setPrefWidth(width);
            ((HBox)node).setMaxWidth(width);
        }
    }

    private boolean nodeTextTest(Node node) {
        if(node instanceof Label) {
            return StringUtils.isEmpty(((Label)node).getText());
        } else if(node instanceof HBox) {
            if(!((HBox)node).getChildren().isEmpty()) {
                Label labelNode = (Label) (((HBox) node).getChildren().get(0));
                return StringUtils.isEmpty(labelNode.getText());
            }
        }
        return true;
    }

    private void reSizeDomainArea(String gene, HBox box, double width) {
        List<BrcaCnvExon> list = getBrcaCnvExons(gene);
        String domain = null;
        int size = 0;
        int idx = 0;
        double lineWidth = calcOneLineSize(getTotalAmpliconCountInGene(gene), width);
        double boxWidth = calcOneBoxSize(getTotalAmpliconCountInGene(gene), width);
        for(BrcaCnvExon brcaCnvExon : list) {
            List<BrcaCnvAmplicon> amplicons = getBrcaCnvAmpliconsByExon(gene, brcaCnvExon.getExon());
            if((StringUtils.isNotEmpty(brcaCnvExon.getDomain()) && StringUtils.isNotEmpty(domain)) ||
                    StringUtils.isEmpty(brcaCnvExon.getDomain()) && StringUtils.isNotEmpty(domain)) {
                if(!brcaCnvExon.getDomain().equals(domain)) {
                    if(nodeTextTest(box.getChildren().get(idx))) {
                        reSizeNodeWidth(box.getChildren().get(idx), lineWidth);
                        idx++;
                    }
                    reSizeNodeWidth(box.getChildren().get(idx), boxWidth * size - lineWidth);
                    domain = brcaCnvExon.getDomain();
                    idx++;
                    size = 0;
                }
                size += amplicons.size();
            } else if(StringUtils.isNotEmpty(brcaCnvExon.getDomain()) && StringUtils.isEmpty(domain)
                    && (list.indexOf(brcaCnvExon) != 0)) {
                if(nodeTextTest(box.getChildren().get(idx))) {
                    reSizeNodeWidth(box.getChildren().get(idx), lineWidth);
                    idx++;
                }
                domain = brcaCnvExon.getDomain();
                reSizeNodeWidth(box.getChildren().get(idx), boxWidth * size - lineWidth);
                idx++;
                size = 0;
                size += amplicons.size();
            } else if(StringUtils.isNotEmpty(brcaCnvExon.getDomain()) && StringUtils.isEmpty(domain)
                    && (list.indexOf(brcaCnvExon) == 0)) {
                size += amplicons.size();
                domain = brcaCnvExon.getDomain();
            } else {
                size += amplicons.size();
            }
        }
    }

    private void drawDomainArea(String gene, HBox box) {
        List<BrcaCnvExon> list = getBrcaCnvExons(gene);
        String domain = null;
        int size = 0;
        for(BrcaCnvExon brcaCnvExon : list) {
            List<BrcaCnvAmplicon> amplicons = getBrcaCnvAmpliconsByExon(gene, brcaCnvExon.getExon());
            if((StringUtils.isNotEmpty(brcaCnvExon.getDomain()) && StringUtils.isNotEmpty(domain)) ||
                    StringUtils.isEmpty(brcaCnvExon.getDomain()) && StringUtils.isNotEmpty(domain)) {
                if(!brcaCnvExon.getDomain().equals(domain)) {
                    if(!box.getChildren().isEmpty()) {
                        addLineInHBox(gene, box, "");
                    }
                    Label label = new Label(domain);
                    label.getStyleClass().add("domain_label");
                    label.setMinWidth((size * calcOneBoxSize(getTotalAmpliconCountInGene(gene))) - calcOneLineSize(getTotalAmpliconCountInGene(gene)));
                    box.getChildren().add(label);
                    domain = brcaCnvExon.getDomain();
                    size = 0;
                }
                size += amplicons.size();
            } else if(StringUtils.isNotEmpty(brcaCnvExon.getDomain()) && StringUtils.isEmpty(domain)
                    && (list.indexOf(brcaCnvExon) != 0)) {
                if(!box.getChildren().isEmpty()) {
                    addLineInHBox(gene, box, "");
                }
                domain = brcaCnvExon.getDomain();
                addChildrenInHBox(gene, box, size);
                size = 0;
                size += amplicons.size();
            } else if(StringUtils.isNotEmpty(brcaCnvExon.getDomain()) && StringUtils.isEmpty(domain)
                    && (list.indexOf(brcaCnvExon) == 0)) {
                size += amplicons.size();
                domain = brcaCnvExon.getDomain();
            } else {
                size += amplicons.size();
            }
        }
    }

    private void addChildrenInHBox(String gene, HBox parent, int size) {
        HBox line = new HBox();
        double boxSize = calcOneBoxSize(getTotalAmpliconCountInGene(gene)) * size - calcOneLineSize(getTotalAmpliconCountInGene(gene));
        line.setMinSize(boxSize, 0.1);
        line.setPrefSize(boxSize, 0.1);
        line.setMaxSize(boxSize, 0.1);
        parent.getChildren().add(line);
    }

    private void addLineInHBox(String gene, HBox parent, String css) {
        HBox line = new HBox();
        line.getStyleClass().add("line");
        double lineWidth = calcOneLineSize(getTotalAmpliconCountInGene(gene));
        line.setMinSize(lineWidth, 0.1);
        line.setPrefSize(lineWidth, 0.1);
        line.setMaxSize(lineWidth, 0.1);
        if(StringUtils.isNotEmpty(css)) {
            line.setStyle(css);
        }
        parent.getChildren().add(line);
    }

    private void drawConnectLabelLine(List<Label> labelList, AnchorPane pane, List<Line> lineList) {
        int totalIdx = labelList.size() - 1;

        for(int idx = 0; idx < totalIdx; idx++) {
            Line line = new Line();
            lineList.add(line);
            line.setStartX(AnchorPane.getLeftAnchor(labelList.get(idx)) + 3);
            line.setStartY(AnchorPane.getTopAnchor(labelList.get(idx)) + 3);

            line.setEndX(AnchorPane.getLeftAnchor(labelList.get(idx + 1)) + 3);
            line.setEndY(AnchorPane.getTopAnchor(labelList.get(idx + 1)) + 3);
            line.setFill(Color.BLACK);
            line.setStrokeWidth(0.5f);
            pane.getChildren().add(0, line);
        }
    }

    private void createBoxPlotNumber(String gene, HBox box) {
        List<BrcaCnvExon> list = getBrcaCnvExons(gene);
        double oneBoxSize = calcOneBoxSize(getTotalAmpliconCountInGene(gene));
        double oneLineSize = calcOneLineSize(getTotalAmpliconCountInGene(gene));
        boolean first = true;
        for(BrcaCnvExon brcaCnvExon : list) {
            if(first) {
                first = false;
            } else {
                addLineInHBox(gene, box, "");
            }
            List<BrcaCnvAmplicon> amplicons = getBrcaCnvAmpliconsByExon(gene, brcaCnvExon.getExon());
            if(brcaCnvExon.getExon().matches("[a-zA-Z]*")) {
                addChildrenInHBox(gene, box, amplicons.size());
            } else {
                Label label = new Label(brcaCnvExon.getExon());
                label.setAlignment(Pos.CENTER);
                label.getStyleClass().add("font_size_9");
                label.setMinWidth((amplicons.size() * oneBoxSize) - oneLineSize);
                box.getChildren().add(label);
            }
        }
    }

    private void createBoxPlot(String gene, HBox box) {
        List<BrcaCnvExon> list = getBrcaCnvExons(gene);
        String boxStyle = "-fx-border-width : 0.5 0 0 0; -fx-border-color : black";
        double oneBoxSize = calcOneBoxSize(getTotalAmpliconCountInGene(gene));
        double oneLineSize = calcOneLineSize(getTotalAmpliconCountInGene(gene));

        boolean first = true;
        for(BrcaCnvExon brcaCnvExon : list) {
            if(first) {
                first = false;
            } else {
                addLineInHBox(gene, box, boxStyle);
            }
            List<BrcaCnvAmplicon> amplicons = getBrcaCnvAmpliconsByExon(gene, brcaCnvExon.getExon());
            double boxSize = oneBoxSize * amplicons.size() - oneLineSize;
            if(brcaCnvExon.getExon().matches("[a-zA-Z]*")) {
                HBox line = new HBox();
                line.setId(gene + "_" + brcaCnvExon.getExon());
                line.setMinSize(boxSize, 0.1);
                line.setPrefSize(boxSize, 0.1);
                line.setMaxSize(boxSize, 0.1);
                line.setStyle(boxStyle);
                box.getChildren().add(line);
            } else {
                HBox boxPlot = initPlotBox(brcaCnvExon, amplicons.size(), oneLineSize);
                if(!brcaCnvExon.getExon().equals("Promoter")) {
                    boxPlot.setCursor(Cursor.HAND);
                    boxPlot.setOnMouseClicked(ev -> {
                        if(brcaCnvTable.getItems() != null) {
                            brcaCnvTable.getSelectionModel().select(brcaCnvExon);
                            brcaCnvTable.scrollTo(brcaCnvTable.getSelectionModel().getSelectedIndex());
                        }
                    });
                }
                box.getChildren().add(boxPlot);
            }
        }

        HBox line = new HBox();
        line.setMinSize(25, 0.1);
        line.setPrefSize(25, 0.1);
        line.setMaxSize(25, 0.1);
        line.setStyle(boxStyle);
        box.getChildren().add(line);
        Label label = new Label("3'");
        box.getChildren().add(label);
    }

    private String[] getNoneTargetArea() {
        if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_MLPA_DNA.getCode())) {
            return brcaMlpaNoneTargetArea;
        } else if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_CMC_DNA.getCode())) {
            return brcaCmcNoneTargetArea;
        } else if(panel.getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_DNA_V2.getCode())) {
            return brcaV2NoneTargetArea;
        }
        return brcaAndPlusNoneTargetArea;
    }

    private void paintPlotBox(HBox box, BrcaCnvExon brcaCnvExon) {
        box.getStyleClass().removeAll(box.getStyleClass());
        if(StringUtils.isNotEmpty(brcaCnvExon.getExpertCnv())) {
            if(brcaCnvExon.getExpertCnv().equals(BrcaCNVCode.AMPLIFICATION.getCode())) {
                box.getStyleClass().add("brca_cnv_3_expert");
            } else if(brcaCnvExon.getExpertCnv().equals(BrcaCNVCode.NORMAL.getCode())) {
                box.getStyleClass().add("brca_cnv_2_expert");
            } else {
                box.getStyleClass().add("brca_cnv_1_expert");
            }
        } else {
            if(brcaCnvExon.getSwCnv().equals(BrcaCNVCode.AMPLIFICATION.getCode())) {
                box.getStyleClass().add("brca_cnv_3");
            } else if(brcaCnvExon.getSwCnv().equals(BrcaCNVCode.NORMAL.getCode())) {
                box.getStyleClass().add("brca_cnv_2");
            } else {
                box.getStyleClass().add("brca_cnv_1");
            }
        }
    }

    private HBox initPlotBox(BrcaCnvExon brcaCnvExon, long size, double lineSize) {
        HBox box = new HBox();
        String boxId = brcaCnvExon.getGene() + "_" + brcaCnvExon.getExon();
        box.setId(boxId);
        double boxSize = (size * calcOneBoxSize(getTotalAmpliconCountInGene(brcaCnvExon.getGene()))) - lineSize;
        String[] noneTargetArea = getNoneTargetArea();
        if(Arrays.stream(brcaExonThinArea).anyMatch(item -> item.equals(boxId)) &&
                Arrays.stream(noneTargetArea).noneMatch(item -> item.equals(boxId))) {
            if(brcaCnvExon.getExon().equals("1")) {
                Label label = new Label();
                label.setMinWidth(boxSize);
                label.setMinHeight(6);
                label.setPrefHeight(6);
                label.setMaxHeight(6);
                box.getChildren().add(label);
            } else {
                Label label = new Label();
                label.setMinWidth(4);
                label.setMinHeight(6);
                label.setPrefHeight(6);
                label.setMaxHeight(6);
                Label label2 = new Label();
                label2.setMinWidth(boxSize - 4);
                label2.setMinHeight(12);
                label2.setPrefHeight(12);
                label2.setMaxHeight(12);
                if(brcaCnvExon.getExon().equals("24") || brcaCnvExon.getExon().equals("27")) {
                    box.getChildren().addAll(label2, label);
                } else {
                    box.getChildren().addAll(label, label2);
                }
            }
        } else {
            Label label = new Label();
            label.setMinWidth(boxSize);
            label.setMinHeight(12);
            label.setPrefHeight(12);
            label.setMaxHeight(12);
            box.getChildren().add(label);
        }
        box.setMinHeight(12);
        box.setPrefHeight(12);
        box.setMaxHeight(12);
        box.setAlignment(Pos.CENTER);
        paintPlotBox(box, brcaCnvExon);
        box.setMinWidth(boxSize);
        return box;
    }

    private void createLabelList(String gene, List<Label> labelList, AnchorPane anchorPane) {
        double positionX = (calcOneBoxSize(getTotalAmpliconCountInGene(gene)) / 2) - 5;
        List<BrcaCnvExon> list = getBrcaCnvExons(gene);

        for(BrcaCnvExon brcaCnvExon : list) {
            List<BrcaCnvAmplicon> amplicons = getBrcaCnvAmpliconsByExon(gene, brcaCnvExon.getExon());
            for(BrcaCnvAmplicon amplicon : amplicons) {
                Integer prediction = getAmpliconCnvLevel(amplicon);
                Label label = new Label();
                label.getStyleClass().add(getLabelStyleClass(prediction, amplicon));
                label.setOnMouseClicked(ev -> {
                    if(brcaCnvTable.getItems() != null) {
                        brcaCnvTable.getSelectionModel().select(brcaCnvExon);
                        brcaCnvTable.scrollTo(brcaCnvTable.getSelectionModel().getSelectedIndex());
                    }
                    sampleRatioPopOver(label, amplicon);

                });
                anchorPane.getChildren().add(label);
                AnchorPane.setTopAnchor(label, getLabelHeight(prediction) + LABEL_CENTER_FIX);
                AnchorPane.setLeftAnchor(label, positionX);
                positionX += calcOneBoxSize(getTotalAmpliconCountInGene(gene));
                labelList.add(label);
            }
        }
    }

    private void sampleRatioPopOver(Label label, BrcaCnvAmplicon amplicon) {
        PopOver popOver = new PopOver();
        popOver.setMinWidth(200);
        popOver.setHeight(50);
        popOver.setMaxHeight(50);
        VBox mainVBox = new VBox();
        mainVBox.setAlignment(Pos.CENTER);
        mainVBox.setMinWidth(200);
        mainVBox.setPrefWidth(200);
        mainVBox.setPrefHeight(50);
        Label titleLabel = new Label("Sample Ratio");
        titleLabel.getStyleClass().addAll("font_size_12", "bold");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPrefWidth(200);
        HBox legendBox = new HBox();
        legendBox.setAlignment(Pos.CENTER);
        legendBox.setMinWidth(200);
        legendBox.setPrefWidth(200);
        legendBox.setSpacing(60);
        VBox.setMargin(legendBox, new Insets(5, 0, 0, 0));
        Label deletionLabel = new Label("Del");
        deletionLabel.getStyleClass().add("font_size_10");
        Label normalLabel = new Label("Nor");
        normalLabel.setAlignment(Pos.CENTER);
        normalLabel.getStyleClass().add("font_size_10");
        Label amplificationLabel = new Label("Amp");
        amplificationLabel.getStyleClass().add("font_size_10");

        legendBox.getChildren().addAll(deletionLabel, normalLabel, amplificationLabel);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setMinSize(170, 30);
        anchorPane.setPrefSize(170, 30);
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setMinSize(170, 15);
        box.setPrefSize(170, 15);
        box.setStyle("-fx-border-color : gray; -fx-border-width : 0.5; -fx-border-radius : 15 15;");
        Label normalBox = new Label();
        normalBox.setMinSize(70, 15);
        normalBox.setPrefSize(70, 15);
        //normalBox.setStyle("-fx-background-color : #30a1c2; -fx-border-color : gray; -fx-border-width : 0 0.5 0 0.5;");
        normalBox.setStyle("-fx-background-color : linear-gradient(to right, white, #30a1c2, white); -fx-border-color : gray; -fx-border-width : 0.5 0 0.5 0;");
        box.getChildren().add(normalBox);
        Label position = new Label();
        position.getStyleClass().add("ratio_position");
        position.setMinWidth(16);

        Label warningLabel = new Label();
        if(StringUtils.isNotEmpty(amplicon.getWarning())) {
            warningLabel.setText(amplicon.getWarning().replaceAll("low_confidence_cnv : ", ""));
        }
        reSizeNodeWidth(warningLabel, 200);
        warningLabel.setAlignment(Pos.CENTER);
        warningLabel.getStyleClass().addAll("font_size_10","txt_red", "bold");

        anchorPane.getChildren().add(box);
        anchorPane.getChildren().add(position);
        AnchorPane.setTopAnchor(position, 7.5);
        double leftAnchorVal = (mainVBox.getMinWidth() - box.getMinWidth()) / 2;
        AnchorPane.setLeftAnchor(position, calcPosition(amplicon, box.getMinWidth(), normalBox.getMinWidth(),
                leftAnchorVal - (position.getMinWidth() / 2)));
        AnchorPane.setLeftAnchor(box, leftAnchorVal);
        mainVBox.getChildren().addAll(titleLabel, legendBox, anchorPane, warningLabel);
        popOver.getRoot().setAlignment(Pos.CENTER);
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popOver.getRoot().setOpaqueInsets(new Insets(5, 5, 5, 5));
        popOver.setHeaderAlwaysVisible(true);
        popOver.setAutoHide(true);
        popOver.setAutoFix(true);
        popOver.setDetachable(true);
        popOver.setArrowSize(15);
        popOver.setArrowIndent(30);
        popOver.setContentNode(mainVBox);
        popOver.setTitle("");
        popOver.show(label);
    }

    private double calcPosition(BrcaCnvAmplicon brcaCnvAmplicon, double boxWidth, double normalWidth, double margin) {
        double otherAreaWidth = (boxWidth - normalWidth) / 2;
        double maxValue = 2d;
        if(brcaCnvAmplicon.getSampleRatio().compareTo(BigDecimal.valueOf(maxValue)) > 0) {
            return boxWidth + margin;
        }
        if(panel.getCnvConfigBRCAaccuTest().getAmpliconCopyNumberPredictionAlgorithm()
                .equals(BrcaAmpliconCopyNumberPredictionAlgorithmCode.DISTRIBUTION.getCode())) {
            if(brcaCnvAmplicon.getDistributionPrediction() == 3) {
                BigDecimal max = BigDecimal.valueOf(maxValue);
                max = max.subtract(brcaCnvAmplicon.getDistributionRangeMax());
                BigDecimal correctionValue = brcaCnvAmplicon.getSampleRatio().subtract(brcaCnvAmplicon.getDistributionRangeMax());
                return correctionValue.divide(max, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(otherAreaWidth)).doubleValue() + normalWidth + otherAreaWidth + margin;
            } else if(brcaCnvAmplicon.getDistributionPrediction() == 1) {
                return brcaCnvAmplicon.getSampleRatio().divide(brcaCnvAmplicon.getDistributionRangeMin(), 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(otherAreaWidth)).doubleValue() + margin;
            } else {
                BigDecimal max = brcaCnvAmplicon.getDistributionRangeMax().subtract(brcaCnvAmplicon.getDistributionRangeMin());
                BigDecimal correctionValue = brcaCnvAmplicon.getSampleRatio().subtract(brcaCnvAmplicon.getDistributionRangeMin());
                return correctionValue.divide(max, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(normalWidth)).doubleValue() + otherAreaWidth + margin;
            }
        } else {
            if(brcaCnvAmplicon.getDistributionPrediction() == 3) {
                BigDecimal max = BigDecimal.valueOf(maxValue);
                max = max.subtract(brcaCnvAmplicon.getRawRangeMax());
                BigDecimal correctionValue = brcaCnvAmplicon.getSampleRatio().subtract(brcaCnvAmplicon.getRawRangeMax());
                return correctionValue.divide(max, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(otherAreaWidth)).doubleValue() + normalWidth + otherAreaWidth + margin;
            } else if(brcaCnvAmplicon.getRawPrediction() == 1) {
                return brcaCnvAmplicon.getSampleRatio().divide(brcaCnvAmplicon.getRawRangeMin(), 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(otherAreaWidth)).doubleValue() + margin;
            } else {
                BigDecimal max = brcaCnvAmplicon.getRawRangeMax().subtract(brcaCnvAmplicon.getRawRangeMin());
                BigDecimal correctionValue = brcaCnvAmplicon.getSampleRatio().subtract(brcaCnvAmplicon.getRawRangeMin());
                return correctionValue.divide(max, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(normalWidth)).doubleValue() + otherAreaWidth + margin;
            }
        }
    }

    private String getLabelStyleClass(Integer prediction, BrcaCnvAmplicon amplicon) {
        if(prediction == 1) {
            return "deletion_label";
        } else if(prediction == 2) {
            return getAmbiguousValue(amplicon);
        }
        return "amplification_label";
    }

    private Double getLabelHeight(Integer prediction) {
        if(prediction == 1) {
            return DELETION_HEIGHT;
        } else if(prediction == 2) {
            return NORMAL_HEIGHT;
        }
        return AMPLIFICATION_HEIGHT;
    }

    private Integer getAmpliconCnvLevel(BrcaCnvAmplicon brcaCnvAmplicon) {
        if(BrcaAmpliconCopyNumberPredictionAlgorithmCode.DISTRIBUTION.getCode().equals(
                panel.getCnvConfigBRCAaccuTest().getAmpliconCopyNumberPredictionAlgorithm())) {
            return brcaCnvAmplicon.getDistributionPrediction();
        }
        return brcaCnvAmplicon.getRawPrediction();
    }

    private void resizeGridPane(long size, GridPane gridPane) {
        gridPane.setPrefWidth(size * calcOneBoxSize(size));
    }

    private void resizeGridPane(long size, GridPane gridPane, double width) {
        gridPane.setPrefWidth(size * calcOneBoxSize(size, width));
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

    private String getAmbiguousValue(BrcaCnvAmplicon amplicon) {
        Double deletionGap = panel.getCnvConfigBRCAaccuTest().getLowConfidenceCnvDeletion();
        Double duplicationGap = panel.getCnvConfigBRCAaccuTest().getLowConfidenceCnvDuplication();
        if(BrcaAmpliconCopyNumberPredictionAlgorithmCode.DISTRIBUTION.getCode().equals(
                panel.getCnvConfigBRCAaccuTest().getAmpliconCopyNumberPredictionAlgorithm())) {
            if(deletionGap != null && amplicon.getSampleRatio()
                    .subtract(amplicon.getDistributionRangeMin())
                    .compareTo(new BigDecimal(deletionGap.toString())) < 0) {
                return "deletion_normal_label";
            } else if((duplicationGap != null && amplicon.getDistributionRangeMax()
                    .subtract(amplicon.getSampleRatio())
                    .compareTo(new BigDecimal(duplicationGap.toString())) < 0)) {
                return "amplification_normal_label";
            } else {
                return "normal_label";
            }
        } else {
            if(deletionGap != null && amplicon.getSampleRatio()
                    .subtract(amplicon.getRawRangeMin())
                    .compareTo(new BigDecimal(deletionGap.toString())) < 0) {
                return "deletion_normal_label";
            } else if((duplicationGap != null && amplicon.getRawRangeMax()
                    .subtract(amplicon.getSampleRatio())
                    .compareTo(new BigDecimal(duplicationGap.toString())) < 0)) {
                return "amplification_normal_label";
            } else {
                return "normal_label";
            }
        }
    }

    private String getExportFields() {
        StringBuilder stringBuilder = new StringBuilder();

        getSelectedItemList().forEach(item -> stringBuilder.append(item.getId()).append(","));
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public void exportExcel() {
        WorksheetUtil worksheetUtil = new WorksheetUtil();
        worksheetUtil.exportGermlineCnvData(this.getMainApp(), sample, true, false);
    }

    private List<BrcaCnvExon> getSelectedItemList() {
        if(brcaCnvTable.getItems() == null) {
            return new ArrayList<>();
        }
        return brcaCnvTable.getItems().stream().filter(BrcaCnvExon::getCheckItem)
                .collect(Collectors.toList());
    }

    private double calcOneBoxSize(long size) {
        double value = mainStackPane.widthProperty().getValue() == 0 ? 1060 : mainStackPane.widthProperty().getValue();
        double boxSize = Math.round(value / size);
        if(boxSize > 15) {
            if((value * size) + 40 > 1060) {
                return boxSize - 1;
            }
            return boxSize;
        }
        return 15;
    }

    private double calcOneLineSize(long size) {
        double val = Math.round(calcOneBoxSize(size) / 5);
        if(val > 5) {
            return val;
        }
        return 5;
    }

    private double calcOneBoxSize(long size, double width) {
        double value = width == 0 ? 1060 : width;
        double boxSize = Math.round(value / size);
        if(boxSize > 15) {
            if((value * size) + 40 > width) {
                return boxSize - 1;
            }
            return boxSize;
        }
        return 15;
    }

    private double calcOneLineSize(long size, double width) {
        double val = Math.round(calcOneBoxSize(size, width) / 5);
        if(val > 5) {
            return val;
        }
        return 5;
    }

    private long getTotalAmpliconCountInGene(String gene) {
        return brcaCnvAmpliconList.stream().filter(amplicon -> amplicon.getGene().equals(gene)).count();
    }

    @FXML
    public void doExonCnvChange() {
        if(getSelectedItemList().isEmpty()) {
            DialogUtil.warning("", "Please select Exon to change the value.", mainController.getPrimaryStage(), true);
        } else {
            popUp(getSelectedItemList(), "Modify CNV in multi-selection");
        }
    }

    @FXML
    private void showLegendTooltip(Event event) {
        PopOver popOver = new PopOver();
        popOver.setWidth(260);
        popOver.setHeight(200);
        popOver.setMaxHeight(160);
        VBox mainVBox = new VBox();
        mainVBox.setPrefWidth(260);
        mainVBox.setPrefHeight(160);
        HBox titleBox = createTitleBox();
        HBox deletion = createContentsBox("-fx-background-radius : 15 15; -fx-background-color : #cc3e4f;",
                "Deletion");
        HBox normal = createContentsBox("-fx-background-radius : 15 15; -fx-background-color : #97a2be;",
                "Normal");
        HBox amplification = createContentsBox("-fx-background-radius : 15 15; -fx-background-color : #e1b07b;",
                "Amplification");
        HBox userChange = createContentsBox("-fx-border-width : 0.5; -fx-border-color : #13aff7;",
                "CNV in Exon changed by User");
        HBox putativeDel = createContentsBox("-fx-background-color : #cc3e4f;", "Putative Deletion");
        HBox putativeAmp = createContentsBox("-fx-background-color : #e1b07b;", "Putative Amplification");

        mainVBox.getChildren().addAll(titleBox, deletion, amplification, normal, putativeDel, putativeAmp, userChange);
        popOver.getRoot().setAlignment(Pos.CENTER);
        popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_TOP);
        popOver.getRoot().setOpaqueInsets(new Insets(5, 5, 5, 5));
        popOver.setHeaderAlwaysVisible(true);
        popOver.setAutoHide(true);
        popOver.setAutoFix(true);
        popOver.setDetachable(true);
        popOver.setArrowSize(15);
        popOver.setArrowIndent(30);
        popOver.setContentNode(mainVBox);
        popOver.setTitle("");
        popOver.show((Node)event.getSource());
    }

    @FXML
    private void showNomenclatureTooltip(Event event) {
        PopOver popOver = new PopOver();
        popOver.setWidth(560);
        popOver.setHeight(80);
        popOver.setMaxHeight(80);
        VBox mainVBox = new VBox();
        mainVBox.setPrefWidth(560);
        mainVBox.setPrefHeight(80);
        mainVBox.getStyleClass().add("font_size_11");
        HBox hgvsNomencaltureBox = new HBox();
        Label hgvsTitleLabel = new Label(" HGVS Nomenclature: ");
        hgvsTitleLabel.getStyleClass().add("bold");
        Label hgvsContentsLabel = new Label("Nomenclature following HGVS recommendations");
        hgvsNomencaltureBox.getChildren().addAll(hgvsTitleLabel, hgvsContentsLabel);

        HBox bicNomencaltureBox = new HBox();
        Label bicTitleLabel = new Label(" BIC Nomenclature: ");
        bicTitleLabel.getStyleClass().add("bold");
        Label bicContentsLabel = new Label("Nomenclature following HGVS recommendations, but with the following exceptions:");
        bicNomencaltureBox.getChildren().addAll(bicTitleLabel, bicContentsLabel);

        Label contents1Label = new Label("· BRCA1 nucleotide is from GenBank U14680.1, this exon 4 is missing.");
        Label contents2Label = new Label("· BRCA2 nucleotide is from GenBank U43746.1");

        mainVBox.getChildren().addAll(hgvsNomencaltureBox, bicNomencaltureBox, contents1Label, contents2Label);
        VBox.setMargin(hgvsNomencaltureBox, new Insets(0, 0, 0, 10));
        VBox.setMargin(bicNomencaltureBox, new Insets(0, 0, 0, 10));
        VBox.setMargin(contents1Label, new Insets(0, 0, 0, 30));
        VBox.setMargin(contents2Label, new Insets(0, 0, 0, 30));
        popOver.getRoot().setAlignment(Pos.CENTER);
        popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_CENTER);
        popOver.setHeaderAlwaysVisible(true);
        popOver.setAutoHide(true);
        popOver.setAutoFix(true);
        popOver.setDetachable(true);
        popOver.setArrowSize(15);
        popOver.setArrowIndent(30);
        popOver.setContentNode(mainVBox);
        popOver.setTitle("");
        popOver.show((Node)event.getSource());
    }

    private HBox createTitleBox() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefHeight(25);
        box.setPrefWidth(260);

        Label icon = new Label("Icon");
        icon.getStyleClass().addAll("bold", "font_size_12");
        icon.setPrefWidth(45);
        icon.setMinHeight(15);
        icon.setPrefHeight(15);
        icon.setMaxHeight(15);
        icon.setAlignment(Pos.CENTER);

        Label contentsLabel = new Label("Description");
        contentsLabel.getStyleClass().addAll("bold", "font_size_12");
        contentsLabel.setPrefWidth(215);
        contentsLabel.setPadding(new Insets(0, 0, 0, 10));
        contentsLabel.setAlignment(Pos.CENTER);

        box.getChildren().addAll(icon, contentsLabel);
        box.setStyle("-fx-border-width : 0 0 0.5 0; -fx-border-color : black;");

        return box;
    }

    private HBox createContentsBox(String style, String contents) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefHeight(25);
        box.setPrefWidth(420);

        HBox iconBox = new HBox();
        iconBox.setPrefWidth(45);
        iconBox.setMinWidth(45);
        iconBox.setAlignment(Pos.CENTER);
        Label icon = new Label();
        icon.setStyle(style);
        icon.setPrefWidth(15);
        icon.setMinHeight(15);
        icon.setPrefHeight(15);
        icon.setMaxHeight(15);
        icon.setAlignment(Pos.CENTER);
        iconBox.getChildren().add(icon);

        Label contentsLabel = new Label(contents);
        contentsLabel.setPrefWidth(215);
        contentsLabel.setPadding(new Insets(0, 0, 0, 10));
        contentsLabel.setAlignment(Pos.CENTER_LEFT);
        contentsLabel.setFont(Font.font(12));

        box.getChildren().addAll(iconBox, contentsLabel);

        return box;
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
            if(brcaCnvTable.getItems() != null) {
                brcaCnvTable.getItems().forEach(item -> item.setCheckItem(nv));
                brcaCnvTable.refresh();
            }
        });

        column.widthProperty().addListener((ob, ov, nv) -> hBox.setMinWidth(column.getWidth()));
        column.setResizable(false);

        column.setPrefWidth(30d);

        brcaCnvTable.getColumns().add(0, column);
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
}
