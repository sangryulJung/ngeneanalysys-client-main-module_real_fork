package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.code.enums.AnalysisTypeCode;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.code.enums.PredictionTypeCode;
import ngeneanalysys.code.enums.VariantLevelCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.CNV;
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import ngeneanalysys.model.paged.PagedCNV;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2018-05-24
 */
public class AnalysisDetailCNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    private APIService apiService;

    @FXML
    private TableView<CNV> cnvTableView;

    @FXML
    private TableColumn<CNV, String> geneTableColumn;

    @FXML
    private TableColumn<CNV, BigDecimal> valueTableColumn;

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
            TableColumn<CNV, String> tierColumn = new TableColumn<>("Tier");
            tierColumn.setPrefWidth(60);
            tierColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTier()));
            tierColumn.setCellFactory(param -> new TableCell<CNV, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setGraphic(null);
                    } else {

                        CNV variant = getTableView().getItems().get(getIndex());

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
                            tierColumn.getStyleClass().add("alignment_center");
                            label.getStyleClass().add(code);
                        }
                        setGraphic(label);
                    }
                }
            });

            TableColumn<CNV, String> reportColumn = new TableColumn<>("Report");
            reportColumn.getStyleClass().add("alignment_center");
            reportColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getIncludedInReport()));
            reportColumn.setCellFactory(param -> new TableCell<CNV, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    if(StringUtils.isEmpty(item) || empty) {
                        setGraphic(null);
                        return;
                    }
                    CNV variant = getTableView().getItems().get(getIndex());
                    Label label = new Label();
                    label.getStyleClass().remove("label");
                    if(StringUtils.isNotEmpty(item) && "Y".equals(item)) {
                        label.setText("R");
                        label.getStyleClass().add("report_check");
                    } else {
                        label.getStyleClass().add("report_uncheck");
                    }
                    //label.setCursor(Cursor.HAND);
                    /*label.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
                        try {
                            Map<String, Object> params = new HashMap<>();
                            params.put("sampleId", sample.getId());
                            params.put("snpInDelIds", variant.getSnpInDel().getId().toString());
                            params.put("comment", "N/A");
                            if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
                                params.put("includeInReport", "N");
                            } else {
                                params.put("includeInReport", "Y");
                            }
                            apiService.put("analysisResults/snpInDels/updateIncludeInReport", params, null, true);
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

            PagedCNV pagedCNV = response.getObjectBeforeConvertResponseToJSON(PagedCNV.class);

            if(pagedCNV.getResult() != null && !pagedCNV.getResult().isEmpty()) {
                List<CNV> result = pagedCNV.getResult();
                cnvTableView.getItems().addAll(result);
            }

        } catch (WebAPIException wae) {
            logger.debug(wae.getMessage());
        }
    }
}
