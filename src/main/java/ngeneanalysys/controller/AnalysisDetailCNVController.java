package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.CNV;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.model.paged.PagedCNV;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

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
    public void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    @Override
    public void show(Parent root) throws IOException {

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
