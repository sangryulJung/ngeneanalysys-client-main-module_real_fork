package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.code.enums.PredictionTypeCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.CompositeCmtCnvResult;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author Jang
 * @since 2018-11-13
 */
public class AnalysisDetailGermlineAmcCNVReportController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private TableView<CompositeCmtCnvResult> heredAmcCnvResultTable;
    @FXML
    private TableColumn<CompositeCmtCnvResult, String> geneTableColumn;
    @FXML
    private TableColumn<CompositeCmtCnvResult, String> variantTableColumn;
    @FXML
    private TableColumn<CompositeCmtCnvResult, String> classificationTableColumn;
    @FXML
    private TableColumn<CompositeCmtCnvResult, String> inheritanceTableColumn;
    @FXML
    private TableColumn<CompositeCmtCnvResult, String> parentalOriginTableColumn;

    void setCompositeCmtCnvResults(String type, Label titleLabel) {
        if(heredAmcCnvResultTable.getItems() != null) {
            heredAmcCnvResultTable.getItems().removeAll(heredAmcCnvResultTable.getItems());
            heredAmcCnvResultTable.refresh();
        }

        APIService apiService = APIService.getInstance();

        SampleView sample = (SampleView) getParamMap().get("sampleView");

        Task<Void> task = new Task<Void>() {

            CompositeCmtCnvResult compositeCmtCnvResult;

            @Override
            protected Void call() throws Exception {

                HttpClientResponse response;
                response = apiService.get("/analysisResults/compositeCmtCnvResult/" + sample.getId(), null, null, null);
                CompositeCmtCnvResult compositeCmtCnvResult = response.getObjectBeforeConvertResponseToJSON(CompositeCmtCnvResult.class);

                this.compositeCmtCnvResult = compositeCmtCnvResult;

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if((type.equals("OVERVIEW") && StringUtils.isNotEmpty(compositeCmtCnvResult.getPrediction()))
                            || "Y".equals(compositeCmtCnvResult.getIncludedInReport())) {
                        heredAmcCnvResultTable.getItems().add(compositeCmtCnvResult);
                        titleLabel.setText("DETECTED CNV SUMMARY");
                    } else {
                        titleLabel.setText("REPORTED CNV VARIANT" + (compositeCmtCnvResult != null && compositeCmtCnvResult.getIncludedInReport().equals("Y") ?
                                "(" + compositeCmtCnvResult.getGene() + " : " + WordUtils.capitalize(compositeCmtCnvResult.getPrediction()) + ")" : ""));
                    }
                });
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
    }

    public CompositeCmtCnvResult getCompositeCmtCnvResult() {
        if(heredAmcCnvResultTable.getItems() != null && heredAmcCnvResultTable.getItems().size() > 0) {
            return heredAmcCnvResultTable.getItems().get(0);
        } else {
            return null;
        }
    }

    @Override
    public void show(Parent root) throws IOException {
        geneTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGene()));
        variantTableColumn.setCellValueFactory(item -> new SimpleStringProperty(WordUtils.capitalize(item.getValue().getPrediction())));
        classificationTableColumn.setCellValueFactory(item -> new SimpleStringProperty(
                PredictionTypeCode.getNameFromAlias(item.getValue().getClassification())));
        inheritanceTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getInheritance()));
        parentalOriginTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getParentalOrigin()));
    }
}