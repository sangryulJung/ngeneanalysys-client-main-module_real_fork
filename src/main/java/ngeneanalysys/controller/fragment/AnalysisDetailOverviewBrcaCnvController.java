package ngeneanalysys.controller.fragment;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.code.enums.BrcaCNVCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.BrcaCnvAmplicon;
import ngeneanalysys.model.BrcaCnvExon;
import ngeneanalysys.model.BrcaCnvResult;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.model.paged.PagedBrcaCNV;
import ngeneanalysys.model.paged.PagedBrcaCNVExon;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2018-11-09
 */
public class AnalysisDetailOverviewBrcaCnvController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    private List<BrcaCnvAmplicon> brcaCnvAmpliconList;

    private List<BrcaCnvExon> brcaCnvExonList;

    private List<BrcaCnvResult> brcaCnvResultList = new ArrayList<>();

    @FXML
    private TableView<BrcaCnvResult> cnvSummaryTable;

    @FXML
    private TableColumn<BrcaCnvResult, String> cnvTableColumn;

    @FXML
    private TableColumn<BrcaCnvResult, String> geneTableColumn;

    @FXML
    private TableColumn<BrcaCnvResult, String> exonTableColumn;

    @Override
    public void show(Parent root) throws IOException {
        cnvTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getCnv()));
        cnvTableColumn.setCellFactory(cell -> new TableCell<BrcaCnvResult, String>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                if(StringUtils.isNotEmpty(item)) {
                    BrcaCnvResult brcaCnvResult = this.getTableView().getItems().get(
                            this.getIndex());
                    long size = brcaCnvExonList.stream().filter(brcaCnvExon -> brcaCnvResult.getGene().equals(brcaCnvExon.getGene()) &&
                            ((StringUtils.isNotEmpty(brcaCnvExon.getExpertCnv())
                                    && brcaCnvResult.getCnv().equalsIgnoreCase(brcaCnvExon.getExpertCnv()))
                                    ||(StringUtils.isEmpty(brcaCnvExon.getExpertCnv())
                                    && brcaCnvResult.getCnv().equalsIgnoreCase(brcaCnvExon.getSwCnv())))).count();
                    setText(item + "(" + size + ")");
                } else {
                    setText(null);
                }
            }
        });
        geneTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getGene()));
        exonTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getResult()));
    }

    public void getBrcaCnvList() {
        if(cnvSummaryTable.getItems() != null) {
            cnvSummaryTable.getItems().removeAll(cnvSummaryTable.getItems());
            brcaCnvResultList.clear();
            cnvSummaryTable.refresh();
        }

        APIService apiService = APIService.getInstance();
        SampleView sample = (SampleView)paramMap.get("sampleView");
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

            addBrcaCnvTable("BRCA1");
            addBrcaCnvTable("BRCA2");
            if(!brcaCnvResultList.isEmpty()) {
                cnvSummaryTable.getItems().addAll(brcaCnvResultList);
                cnvSummaryTable.refresh();
            }
        } catch (WebAPIException wae) {
            DialogUtil.warning(wae.getHeaderText(), wae.getMessage(), mainApp.getPrimaryStage(), true);
        }
    }

    private void addBrcaCnvTable(String gene) {
        List<BrcaCnvExon> brcaCnvExons = brcaCnvExonList.stream().filter(item -> gene.equals(item.getGene()))
                .collect(Collectors.toList());

        List<BrcaCnvExon> brcaCnvExonDeletionList = brcaCnvExons.stream().filter(brcaCnvExon ->
                (StringUtils.isNotEmpty(brcaCnvExon.getExpertCnv()) &&
                        BrcaCNVCode.COPY_LOSS.getCode().equals(brcaCnvExon.getExpertCnv())) ||
                        (StringUtils.isEmpty(brcaCnvExon.getExpertCnv()) &&
                                BrcaCNVCode.COPY_LOSS.getCode().equals(brcaCnvExon.getSwCnv())))
                .collect(Collectors.toList());
        List<BrcaCnvExon> brcaCnvExonDuplicationList = brcaCnvExons.stream().filter(brcaCnvExon ->
                (StringUtils.isNotEmpty(brcaCnvExon.getExpertCnv()) &&
                        BrcaCNVCode.COPY_GAIN.getCode().equals(brcaCnvExon.getExpertCnv())) ||
                        (StringUtils.isEmpty(brcaCnvExon.getExpertCnv()) &&
                                BrcaCNVCode.COPY_GAIN.getCode().equals(brcaCnvExon.getSwCnv())))
                .collect(Collectors.toList());

        if(!brcaCnvExonDeletionList.isEmpty()) {
            BrcaCnvResult brcaCnvResult = new BrcaCnvResult(gene, WordUtils.capitalize(BrcaCNVCode.COPY_LOSS.getCode()),
                    ConvertUtil.convertBrcaCnvRegion(brcaCnvExonDeletionList.stream().map(BrcaCnvExon::getExon)
                            .collect(Collectors.toList())));
            brcaCnvResultList.add(brcaCnvResult);
        }

        if(!brcaCnvExonDuplicationList.isEmpty()) {
            BrcaCnvResult brcaCnvResult = new BrcaCnvResult(gene, WordUtils.capitalize(BrcaCNVCode.COPY_GAIN.getCode()),
                    ConvertUtil.convertBrcaCnvRegion(brcaCnvExonDuplicationList.stream().map(BrcaCnvExon::getExon)
                            .collect(Collectors.toList())));
            brcaCnvResultList.add(brcaCnvResult);
        }
    }
}
