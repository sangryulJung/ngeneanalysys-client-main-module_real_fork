package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import ngeneanalysys.code.enums.VariantLevelCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.Cnv;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.model.paged.PagedCnv;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2018-11-27
 */
public class AnalysisDetailOverviewSolidCNVController extends AnalysisDetailCommonController {

    @FXML
    private GridPane mainGridPane;

    @FXML
    private TableView<Cnv> cnvTableView;

    @FXML
    private TableColumn<Cnv, String> geneTableColumn;
    @FXML
    private TableColumn<Cnv, BigDecimal> valueTableColumn;
    @FXML
    private TableColumn<Cnv, String> tierTableColumn;

    void setContents() {
        Task<Void> task = new Task<Void>() {

            List<Cnv> list;

            @Override
            protected Void call() throws Exception {
                APIService apiService = APIService.getInstance();
                SampleView sample = (SampleView)paramMap.get("sampleView");
                HttpClientResponse response = apiService.get("/analysisResults/cnv/" + sample.getId(), null, null, null);

                PagedCnv pagedCnv = response.getObjectBeforeConvertResponseToJSON(PagedCnv.class);
                list = pagedCnv.getResult();
                return null;
            }

            @Override
            protected void succeeded() {
                if(list != null&& !list.isEmpty()) {
                    if(!cnvTableView.getItems().isEmpty()) {
                        cnvTableView.getItems().removeAll(cnvTableView.getItems());
                    }
                    list = list.stream().filter(item -> item.getCnvValue().compareTo(new BigDecimal("4.0")) > 0)
                            .collect(Collectors.toList());
                    cnvTableView.getItems().addAll(list);
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
                    DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), false);
                    e.printStackTrace();
                }
            }
        };
        Thread imageThread = new Thread(task);
        imageThread.start();
    }

    @Override
    public void show(Parent root) throws IOException {
        geneTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGene()));
        valueTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCnvValue()));
        tierTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTier()));
        tierTableColumn.setCellFactory(param -> new TableCell<Cnv, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(empty) {
                    setGraphic(null);
                } else {

                    Cnv variant = getTableView().getItems().get(getIndex());

                    String value = "";
                    String code = "NONE";
                    if(StringUtils.isNotEmpty(variant.getTier())) {
                        value = variant.getTier();
                        code = "tier_" + VariantLevelCode.getCodeFromAlias(value);
                    }

                    Label label = null;
                    if(!"NONE".equals(code)) {
                        label = new Label(value);
                        label.getStyleClass().clear();
                        tierTableColumn.getStyleClass().add("alignment_center");
                        label.getStyleClass().add(code);
                    }
                    setGraphic(label);
                }
            }
        });
    }
}
