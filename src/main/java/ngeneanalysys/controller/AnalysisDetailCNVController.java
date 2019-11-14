package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.code.enums.VariantLevelCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedCnv;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.RawDataDownloadService;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
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
 * @since 2018-05-24
 */
public class AnalysisDetailCNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    @FXML
    private ImageView cnvPlotImageView;

    @FXML
    private TableView<Cnv> cnvTableView;

    @FXML
    private ScrollPane imageScrollPane;

    @FXML
    private TableColumn<Cnv, String> geneTableColumn;

    @FXML
    private TableColumn<Cnv, BigDecimal> valueTableColumn;

    private AnalysisDetailVariantsController variantsController;

    /**
     * @param variantsController AnalysisDetailVariantsController
     */
    void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    @Override
    public void show(Parent root) throws IOException {

        Panel panel = (Panel)paramMap.get("panel");
        SampleView sample = (SampleView)paramMap.get("sampleView");

        if(panel.getCode().equals(PipelineCode.SOLID_ACCUTEST_CNV_DNA.getCode())) {
            TableColumn<Cnv, String> tierColumn = new TableColumn<>("Tier");
            tierColumn.setPrefWidth(60);
            tierColumn.setCellValueFactory(cellData -> new SimpleStringProperty(ConvertUtil.getTierInfo(cellData.getValue())));
            tierColumn.setCellFactory(param -> new TableCell<Cnv, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setGraphic(null);
                    } else {

                        Cnv cnv = getTableView().getItems().get(getIndex());

                        String value = "";
                        String code = "NONE";
                        if(StringUtils.isNotEmpty(ConvertUtil.getTierInfo(cnv))) {
                            value = ConvertUtil.getTierInfo(cnv);
                            code = "tier_" + VariantLevelCode.getCodeFromAlias(value);
                        }

                        Label label = null;
                        if(!"NONE".equals(code)) {
                            label = new Label(value);
                            label.getStyleClass().clear();
                            tierColumn.getStyleClass().add("alignment_center");
                            label.getStyleClass().add(code);
                        }

                        setGraphic(label);
                    }
                }
            });

            TableColumn<Cnv, String> reportColumn = new TableColumn<>("Report");
            reportColumn.getStyleClass().add("alignment_center");
            reportColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getIncludedInReport()));
            reportColumn.setCellFactory(param -> new TableCell<Cnv, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    if(StringUtils.isEmpty(item) || empty) {
                        setGraphic(null);
                        return;
                    }
                    Cnv variant = getTableView().getItems().get(getIndex());
                    Label label = new Label();
                    label.getStyleClass().remove("label");
                    if(StringUtils.isNotEmpty(item) && "Y".equals(item)) {
                        label.setText("R");
                        label.getStyleClass().add("report_check");
                    } else {
                        label.getStyleClass().add("report_uncheck");
                    }
                    /*label.setCursor(Cursor.HAND);
                    label.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
                        try {
                            Map<String, Object> params = new HashMap<>();
                            params.put("sampleId", sample.getId());
                            params.put("cnvIds", variant.getId().toString());
                            params.put("comment", "N/A");
                            if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
                                params.put("includeInReport", "N");
                            } else {
                                params.put("includeInReport", "Y");
                            }
                            apiService.put("analysisResults/cnv/updateReport", params, null, true);
                            cnvTableView.refresh();
                        } catch (WebAPIException wae) {
                            wae.printStackTrace();
                        }
                    });*/
                    setGraphic(label);
                }

            });

            cnvTableView.getColumns().addAll(tierColumn, reportColumn);
        }

        apiService = APIService.getInstance();

        geneTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGene()));

        valueTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCnvValue()));
        valueTableColumn.setSortType(TableColumn.SortType.DESCENDING);
        setList();
        cnvTableView.getSortOrder().add(valueTableColumn);
        variantsController.getDetailContents().setCenter(root);
    }

    public void setList() {

        SampleView sample = (SampleView)paramMap.get("sampleView");

        try {
            HttpClientResponse response = apiService.get("/analysisResults/cnv/" + sample.getId(), null, null, null);

            PagedCnv pagedCnv = response.getObjectBeforeConvertResponseToJSON(PagedCnv.class);

            if(pagedCnv.getResult() != null && !pagedCnv.getResult().isEmpty()) {
                List<Cnv> result = pagedCnv.getResult();
                cnvTableView.getItems().addAll(result);
            }

        } catch (WebAPIException wae) {
            logger.debug(wae.getMessage());
        }


        Task<Void> imageTask = new Task<Void>() {

            ByteArrayInputStream inputStream;

            @Override
            protected Void call() throws Exception {
                Map<String,Object> paramMap = new HashMap<>();
                paramMap.put("sampleId", sample.getId());
                HttpClientResponse response = apiService.get("/analysisFiles", paramMap, null, false);
                AnalysisFileList analysisFileList = response.getObjectBeforeConvertResponseToJSON(AnalysisFileList.class);
                Optional<AnalysisFile> optionalAnalysisFile = analysisFileList.getResult().stream()
                        .filter(item -> item.getName().contains("_cnv_plot.png")).findFirst();
                if(optionalAnalysisFile.isPresent()) {
                    inputStream = RawDataDownloadService.getInstance().getImageStream(optionalAnalysisFile.get());
                }

                return null;
            }

            @Override
            protected void succeeded() {
                if(inputStream != null) {
                    cnvPlotImageView.setImage(new Image(inputStream));
                    cnvPlotImageView.setFitHeight(imageScrollPane.getHeight() - 10);
                    imageScrollPane.heightProperty().addListener((observable, oldValue, newValue) ->
                            cnvPlotImageView.setFitHeight((Double) newValue - 10));
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
                    logger.error(CommonConstants.DEFAULT_WARNING_MGS, e);
                    DialogUtil.error(CommonConstants.DEFAULT_WARNING_MGS, e.getMessage(), getMainApp().getPrimaryStage(), false);
                    e.printStackTrace();
                }
            }
        };
        Thread imageThread = new Thread(imageTask);
        imageThread.start();
    }
}
