package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import ngeneanalysys.code.enums.BrcaCNVCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.NormalizedCoverage;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.model.SnpVariantAlleleFraction;
import ngeneanalysys.model.paged.PagedNormalizedCoverage;
import ngeneanalysys.model.paged.PagedSnpVariantAlleleFraction;
import ngeneanalysys.model.render.ComboBoxItem;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.WorksheetUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Jang
 * @since 2018-11-15
 */
public class AnalysisDetailHeredCNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private HBox alleleFractionHBox;

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
    private TableColumn<SnpVariantAlleleFraction, BigDecimal> snpVafDepthColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, String> snpVafZygosityColumn;
    @FXML
    private TableColumn<SnpVariantAlleleFraction, String> snpVafPredictionColumn;

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
     * @param variantsController
     */
    public void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    @Override
    public void show(Parent root) throws IOException {
        apiService = APIService.getInstance();
        sample = (SampleView)paramMap.get("sampleView");

        snpVafNumberColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getNumber()));
        snpVafGeneColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGene()));
        snpVafDbSnpIdColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getDbSnpId()));
        snpVafReferenceRangeColumn.setCellValueFactory(item ->
                new SimpleStringProperty(
                        String.format("%.02f", item.getValue().getMinReferenceHeteroRange()) + " - " +
                                String.format("%.02f", item.getValue().getMaxReferenceHeteroRange())));
        snpVafSampleColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSample()));
        snpVafDepthColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getDepth()));
        snpVafZygosityColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getZygosity()));
        snpVafPredictionColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getPrediction()));

        coverageGeneColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGene()));
        coverageWarningColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getWarning()));
        coverageReferenceRangeColumn.setCellValueFactory(item -> new SimpleStringProperty(
                String.format("%.02f", item.getValue().getMinReferenceRange()) + " - " +
                String.format("%.02f", item.getValue().getMaxReferenceRange())));
        coverageRatioColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSampleRatio()));
        coverageDepthColumn.setCellValueFactory(item -> new SimpleObjectProperty<>(item.getValue().getSampleDepth()));
        coveragePredictionColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getPrediction()));

        variantsController.getDetailContents().setCenter(root);
    }

    public void setList() {
        maskerPane.setVisible(true);
        Task<Void> task = new Task<Void>() {

            List<SnpVariantAlleleFraction> snpVariantAlleleFractionList;
            List<NormalizedCoverage> normalizedCoverageList;

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

                snpVariantAlleleFractionList = pagedSnpVariantAlleleFraction.getResult();
                normalizedCoverageList = pagedNormalizedCoverage.getResult();

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    maskerPane.setVisible(false);
                    snpVariantAlleleFractionTable.getItems().addAll(snpVariantAlleleFractionList);
                    normalizedCoverageTable.getItems().addAll(normalizedCoverageList);
                    paintAlleleFraction(snpVariantAlleleFractionList);
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
        Thread thread = new Thread(task);
        thread.start();
    }

    @FXML
    public void excelDownload() {
        WorksheetUtil worksheetUtil = new WorksheetUtil();
        worksheetUtil.exportGermlineCnvData(this.getMainApp(), sample, true, false);
    }

    @FXML
    public void savePrediction() {

    }

    private void paintAlleleFraction(List<SnpVariantAlleleFraction> list) {
        alleleFractionHBox.getChildren().forEach(item -> {
            if(StringUtils.isNotEmpty(item.getId()) && item.getId().startsWith("exon_")) {
                Integer number = Integer.parseInt(item.getId());
                Optional<SnpVariantAlleleFraction> optionalSnpVariantAlleleFaction =
                        list.stream().filter(snpVaf -> snpVaf.getNumber() == number).findFirst();

                optionalSnpVariantAlleleFaction.ifPresent(snpVariantAlleleFraction -> {
                    item.getStyleClass().removeAll("fraction_normal", "fraction_duplication",
                            "fraction_homo");

                    if(StringUtils.isEmpty(snpVariantAlleleFraction.getPrediction())) {
                        item.getStyleClass().add("fraction_homo");
                    } else if(snpVariantAlleleFraction.getPrediction().equals(BrcaCNVCode.NORMAL.getCode())) {
                        item.getStyleClass().add("fraction_normal");
                    } else {
                        item.getStyleClass().add("fraction_duplication");
                    }
                });
            }
        });
    }
}
