package ngeneanalysys.controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import ngeneanalysys.code.enums.BrcaCNVCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedNormalizedCoverage;
import ngeneanalysys.model.paged.PagedSnpVariantAlleleFraction;
import ngeneanalysys.model.render.ComboBoxConverter;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.model.render.SNPsINDELsList;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.RawDataDownloadService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.WorksheetUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jang
 * @since 2018-11-15
 */
public class AnalysisDetailHeredCNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private GridPane heredCnvWrapper;

    @FXML
    private HBox imageHBox;
    @FXML
    private ImageView plotImageView;

    @FXML
    private HBox pmpHBox;

    @FXML
    private HBox kektHBox;

    @FXML
    private CheckBox reportCheckBox;

    @FXML
    private ComboBox<ComboBoxItem> predictionComboBox;

    @FXML
    private TableView<SnpVariantAlleleFraction> snpVariantAlleleFractionTable;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, Integer> snpVafNumberColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, String> snpVafGeneColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, String> snpVafDbSnpIdColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, String> snpVafReferenceRangeColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, BigDecimal> snpVafSampleColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, BigDecimal> snpVafSampleRatioColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, BigDecimal> snpVafControlRatioColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, String> snpVafZygosityColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, String> snpVafPredictionColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, BigDecimal> snpVaf1kpgEasColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, BigDecimal> snpVafgnomadEasColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, BigDecimal> snpVafkrgdbColumn;

    @FXML
    private TableView<NormalizedCoverage> normalizedCoverageTable;
    @FXML
    private TableColumn<NormalizedCoverage, String> coverageGeneColumn;
    @FXML
    private TableColumn<NormalizedCoverage, String> coverageWarningColumn;
    @FXML
    private TableColumn<NormalizedCoverage, String> coverageReferenceRangeColumn;
    @FXML
    private TableColumn<NormalizedCoverage, BigDecimal> coverageRatioColumn;
    @FXML
    private TableColumn<NormalizedCoverage, BigDecimal> coverageDepthColumn;
    @FXML
    private TableColumn<NormalizedCoverage, String> coveragePredictionColumn;

    private APIService apiService;

    private AnalysisDetailVariantsController variantsController;

    private SampleView sample = null;

    /**
     * @param variantsController AnalysisDetailVariantsController
     */
    public void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    private void cnvTest() {
        predictionComboBox.setConverter(new ComboBoxConverter());
        predictionComboBox.getItems().add(new ComboBoxItem(BrcaCNVCode.DUPLICATION.getCode(), "Duplication"));
        predictionComboBox.getItems().add(new ComboBoxItem(BrcaCNVCode.DELETION.getCode(), "Deletion"));

        reportCheckBox.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            Map<String, Object> params = new HashMap<>();
            if(reportCheckBox.isSelected()) {
                params.put("value", "Y");
            } else {
                params.put("value", "N");
            }
            try {
                apiService.put("/analysisResults/updateCmtCnvIncludedInReport/" + sample.getId(), params, null, true);
            } catch (WebAPIException wae) {
                reportCheckBox.setSelected(!reportCheckBox.isSelected());
            }
        });
    }

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();
        sample = (SampleView)paramMap.get("sampleView");

        snpVariantAlleleFractionTable.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) snpVariantAlleleFractionTable.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });

        normalizedCoverageTable.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) normalizedCoverageTable.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });

        snpVafNumberColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getNumber()));
        snpVafGeneColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGene()));
        snpVafDbSnpIdColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getDbSnpId()));
        snpVafReferenceRangeColumn.setCellValueFactory(item ->
                new SimpleStringProperty(
                        String.format("%.03f", item.getValue().getMinReferenceHeteroRange()) + " - " +
                                String.format("%.03f", item.getValue().getMaxReferenceHeteroRange())));
        snpVafSampleColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getVaf()));
        snpVafSampleRatioColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSampleRatio()));
        snpVafControlRatioColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getControlRatio()));
        snpVafZygosityColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getZygosity()));
        snpVafPredictionColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getPrediction()));
        snpVaf1kpgEasColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getG1000EastAsian()));
        snpVafgnomadEasColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getGnomADeastAsian()));
        snpVafkrgdbColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getKoreanReferenceGenomeDatabase()));

        coverageGeneColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGene()));
        coverageWarningColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getWarning()));
        coverageWarningColumn.setCellFactory(param -> new TableCell<NormalizedCoverage, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                setGraphic((StringUtils.isNotEmpty(item)) ?
                        SNPsINDELsList.getWarningReasonPopOver(item, sample.getPanel()) : null);
            }
        });
        coverageReferenceRangeColumn.setCellValueFactory(item -> new SimpleStringProperty(
                String.format("%.03f", item.getValue().getMinReferenceRange()) + " - " +
                String.format("%.03f", item.getValue().getMaxReferenceRange())));
        coverageRatioColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSampleRatio()));
        coverageDepthColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSampleDepth()));
        coveragePredictionColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getPrediction()));

        setList();
        cnvTest();
        variantsController.getDetailContents().setCenter(root);
    }

    public void setList() {
        maskerPane.setVisible(true);
        Task<Void> task = new Task<Void>() {

            List<SnpVariantAlleleFraction> snpVariantAlleleFractionList;
            List<NormalizedCoverage> normalizedCoverageList;
            CompositeCmtCnvResult compositeCmtCnvResult;

            @Override
            protected Void call() throws Exception {
                if(snpVariantAlleleFractionTable.getItems() != null) {
                    snpVariantAlleleFractionTable.getItems().removeAll(snpVariantAlleleFractionTable.getItems());
                    normalizedCoverageTable.getItems().removeAll(normalizedCoverageTable.getItems());
                }

                HttpClientResponse response;
                response = apiService.get("/analysisResults/snpVariantAlleleFraction/" + sample.getId(), null, null, null);
                PagedSnpVariantAlleleFraction pagedSnpVariantAlleleFraction = response.getObjectBeforeConvertResponseToJSON(PagedSnpVariantAlleleFraction.class);

                response = apiService.get("/analysisResults/normalizedCoverage/" + sample.getId(), null, null, null);
                PagedNormalizedCoverage pagedNormalizedCoverage = response.getObjectBeforeConvertResponseToJSON(PagedNormalizedCoverage.class);

                response = apiService.get("/analysisResults/compositeCmtCnvResult/" + sample.getId(), null, null, null);
                CompositeCmtCnvResult compositeCmtCnvResult = response.getObjectBeforeConvertResponseToJSON(CompositeCmtCnvResult.class);

                snpVariantAlleleFractionList = pagedSnpVariantAlleleFraction.getResult();
                normalizedCoverageList = pagedNormalizedCoverage.getResult();
                this.compositeCmtCnvResult = compositeCmtCnvResult;

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    maskerPane.setVisible(false);
                    snpVariantAlleleFractionTable.getItems().addAll(snpVariantAlleleFractionList);
                    normalizedCoverageTable.getItems().addAll(normalizedCoverageList);
                    paintAlleleFraction(snpVariantAlleleFractionList);
                    setPredictionArea(compositeCmtCnvResult);
                });
            }

            @Override
            protected void failed() {
                super.failed();
                maskerPane.setVisible(false);
                Exception e = new Exception(getException());
                if (e instanceof WebAPIException) {
                    DialogUtil.generalShow(((WebAPIException)e).getAlertType(), ((WebAPIException)e).getHeaderText(),
                            ((WebAPIException)e).getContents(),	getMainApp().getPrimaryStage(), false);
                } else {
                    logger.error("Unknown Error", e);
                    DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), false);
                    e.printStackTrace();
                }
            }
        };
        Task<Void> imageTask = new Task<Void>() {

            ByteArrayInputStream inputStream;

            @Override
            protected Void call() throws Exception {
                Map<String,Object> paramMap = new HashMap<>();
                paramMap.put("sampleId", sample.getId());
                HttpClientResponse response = apiService.get("/analysisFiles", paramMap, null, false);
                AnalysisFileList analysisFileList = response.getObjectBeforeConvertResponseToJSON(AnalysisFileList.class);
                Optional<AnalysisFile> optionalAnalysisFile = analysisFileList.getResult().stream()
                        .filter(item -> item.getName().contains(".cnv_boxplot.png")).findFirst();
                if(optionalAnalysisFile.isPresent()) {
                    inputStream = RawDataDownloadService.getInstance().getImageStream(optionalAnalysisFile.get());
                }

                return null;
            }

            @Override
            protected void succeeded() {
                if(inputStream != null) {
                    plotImageView.setImage(new Image(inputStream));
                    //plotImageView.fitWidthProperty().bind(imageHBox.widthProperty());
                    plotImageView.setFitWidth(imageHBox.getWidth());
                    imageHBox.widthProperty().addListener((observable, oldValue, newValue) ->
                            plotImageView.setFitWidth((Double) newValue));
                }
            }

            @Override
            protected void failed() {
                super.failed();
                Exception e = new Exception(getException());
                if (e instanceof WebAPIException) {
                    DialogUtil.generalShow(((WebAPIException)e).getAlertType(), ((WebAPIException)e).getHeaderText(),
                            ((WebAPIException)e).getContents(),	getMainApp().getPrimaryStage(), false);
                } else {
                    logger.error("Unknown Error", e);
                    DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), false);
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
        Thread imageThread = new Thread(imageTask);
        imageThread.start();
    }

    @FXML
    public void excelDownload() {
        WorksheetUtil worksheetUtil = new WorksheetUtil();
        worksheetUtil.exportGermlineCnvData(this.getMainApp(), sample, false, true);
    }

    @FXML
    public void savePrediction() {
        ComboBoxItem item = predictionComboBox.getSelectionModel().getSelectedItem();
        if(item != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("value", item.getValue());
            try {
                apiService.put("/analysisResults/updateCmtCnvPrediction/" + sample.getId(), params, null, true);
                reportCheckBox.setSelected(true);
            } catch (WebAPIException wae) {
                reportCheckBox.setSelected(false);
            }
        }
    }

    @FXML
    private void autoTableSelectEvent(MouseEvent mouseEvent) {
        String id = ((Node)mouseEvent.getSource()).getParent().getId();
        if(StringUtils.isNotEmpty(id)) {
            Integer number = Integer.parseInt(id.replaceAll("exon_", ""));
            if(snpVariantAlleleFractionTable.getItems() != null) {
                snpVariantAlleleFractionTable.getSelectionModel().select(number - 1);
                snpVariantAlleleFractionTable.scrollTo(number - 1);
            }
        }
    }

    private void paintAlleleFraction(List<SnpVariantAlleleFraction> list) {
        pmpHBox.getChildren().forEach(item -> setHBoxColor(item, list));
        kektHBox.getChildren().forEach(item -> setHBoxColor(item, list));
    }

    private void setHBoxColor(Node item, List<SnpVariantAlleleFraction> list) {
        if(StringUtils.isNotEmpty(item.getId()) && item.getId().startsWith("exon_")) {
            Integer number = Integer.parseInt(item.getId().replaceAll("exon_", ""));
            Optional<SnpVariantAlleleFraction> optionalSnpVariantAlleleFaction =
                    list.stream().filter(snpVaf -> snpVaf.getNumber() == number).findFirst();

            optionalSnpVariantAlleleFaction.ifPresent(snpVariantAlleleFraction -> {
                HBox hBox = (HBox)((VBox)item).getChildren().get(1);
                if(!hBox.getChildren().isEmpty()) {
                    hBox.getChildren().removeAll(hBox.getChildren());
                }
                Canvas canvas = new Canvas(18, 18);
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setFill(Color.rgb(45, 112, 232));
                if(snpVariantAlleleFraction.getVaf().compareTo(new BigDecimal("0.9")) == 1) {
                    gc.fillArc(0, 0, 18, 18, 45, 360, ArcType.CHORD);
                } else if(snpVariantAlleleFraction.getVaf().compareTo(new BigDecimal("0.1")) == -1) {
                    gc.fillArc(0, 0, 18, 18, 0, 0, ArcType.CHORD);
                } else if(snpVariantAlleleFraction.getVaf().compareTo(snpVariantAlleleFraction.getMaxReferenceHeteroRange()) == 1) {
                    gc.fillArc(0, 0, 18, 18, 225, 270, ArcType.CHORD);
                } else if(snpVariantAlleleFraction.getVaf().compareTo(snpVariantAlleleFraction.getMinReferenceHeteroRange()) == -1) {
                    gc.fillArc(0, 0, 18, 18, 315, 90, ArcType.CHORD);
                } else {
                    gc.fillArc(0, 0, 18, 18, 270, 180, ArcType.CHORD);
                }
                gc.setFill(Color.rgb(0, 0 ,0));
                gc.fillRect(9, 0, 1, 18);
                hBox.getChildren().add(canvas);
            });
        }
    }

    private void setPredictionArea(CompositeCmtCnvResult compositeCmtCnvResult) {
        if(compositeCmtCnvResult != null) {
            if(compositeCmtCnvResult.getIncludedInReport().equals("Y")) {
                reportCheckBox.setSelected(true);
            } else {
                reportCheckBox.setSelected(false);
            }
            Optional<ComboBoxItem> optionalComboBoxItem = predictionComboBox.getItems().stream()
                    .filter(item -> item.getValue().equals(compositeCmtCnvResult.getPrediction())).findFirst();
            optionalComboBoxItem.ifPresent(item -> predictionComboBox.getSelectionModel().select(item));
        }
    }

    @FXML
    private void showHelpScreen(Event event) {
        PopOver popOver = new PopOver();
        popOver.setWidth(420);
        popOver.setHeight(300);
        popOver.setMaxHeight(140);
        VBox mainVBox = new VBox();
        mainVBox.setPrefWidth(400);
        mainVBox.setPrefHeight(140);
        //HBox titleBox = createTitleBox("icon", "explain");
        HBox homoRefBox = createContentsBox(0, 0, "Homozygous Reference");
        HBox homoVaBox = createContentsBox(45, 360, "Homozygous Variant");
        HBox heteroVaBox = createContentsBox(270, 180, "Heterozygous Variant");
        HBox heteroIncRefBox = createContentsBox(225, 270, "Heterozygous Variant (increased reference fraction)");
        HBox heteroIncVarBox = createContentsBox(315, 90, "Heterozygous Variant (increased variant fraction)");
        mainVBox.getChildren().addAll(homoRefBox, homoVaBox, heteroVaBox, heteroIncRefBox, heteroIncVarBox);
        popOver.getRoot().setAlignment(Pos.CENTER);
        popOver.getRoot().setOpaqueInsets(new Insets(5, 5, 5, 5));
        popOver.setHeaderAlwaysVisible(true);
        popOver.setAutoHide(true);
        popOver.setAutoFix(true);
        popOver.setDetachable(true);
        popOver.setArrowSize(15);
        popOver.setArrowIndent(30);
        popOver.setContentNode(mainVBox);
        popOver.setTitle("genotype");
        popOver.show((Node)event.getSource());
    }

    private HBox createTitleBox(String title, String contents) {
        HBox box = new HBox();
        box.setPrefWidth(400);
        Label titleLabel = new Label(title);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPrefWidth(60);
        titleLabel.setStyle("-fx-border-color : black; -fx-border-width : 0.5;");
        Label contentsLabel = new Label(contents);
        contentsLabel.setPrefWidth(340);
        contentsLabel.setAlignment(Pos.CENTER);
        contentsLabel.setStyle("-fx-border-color : black; -fx-border-width : 0.5;");

        box.getChildren().addAll(titleLabel, contentsLabel);

        return box;
    }

    private HBox createContentsBox(double startAngle, double arcExtent, String contents) {
        HBox box = new HBox();
        box.setPrefWidth(400);
        VBox titleBox = new VBox();
        titleBox.setPrefWidth(60);
        titleBox.setStyle("-fx-border-color : black; -fx-border-width : 0;");
        titleBox.setAlignment(Pos.CENTER);
        HBox imageBox = new HBox();
        imageBox.setAlignment(Pos.TOP_LEFT);
        imageBox.setMinSize(18, 18);
        imageBox.setPrefSize(18, 18);
        imageBox.setMaxSize(18, 18);
        Canvas canvas = new Canvas(18, 18);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.rgb(45, 112, 232));
        gc.fillArc(0, 0, 16, 16, startAngle, arcExtent, ArcType.CHORD);
        gc.setFill(Color.rgb(0, 0 ,0));
        gc.fillRect(7.5, 0, 1, 17);
        imageBox.getChildren().add(canvas);
        imageBox.setStyle("-fx-border-color : black; -fx-border-width : 1; -fx-border-radius : 15 15;");
        titleBox.getChildren().add(imageBox);
        Label contentsLabel = new Label(contents);
        contentsLabel.setPrefWidth(340);
        contentsLabel.setStyle("-fx-border-color : black; -fx-border-width : 0;");

        box.getChildren().addAll(titleBox, contentsLabel);

        return box;
    }
}
