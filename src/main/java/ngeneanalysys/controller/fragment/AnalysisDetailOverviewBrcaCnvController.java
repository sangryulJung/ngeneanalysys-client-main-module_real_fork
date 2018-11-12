package ngeneanalysys.controller.fragment;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
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
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private HBox brca1Plot;

    @FXML
    private HBox brca2Plot;

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
        logger.info("test");
        cnvTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getCnv()));
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
                if(a.getExon().startsWith("exon") && b.getExon().startsWith("exon")) {
                    int intA = Integer.parseInt(a.getExon().replaceAll("exon", ""));
                    int intB = Integer.parseInt(b.getExon().replaceAll("exon", ""));
                    return Integer.compare(intA, intB);
                } else if(a.getExon().startsWith("UTR")) {
                    return 1;
                } else if(b.getExon().startsWith("UTR")) {
                    return -1;
                }
                return 1;
            }).collect(Collectors.toList());

            paintBrcaCnvTable("BRCA1");
            paintBrcaCnvTable("BRCA2");

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

    private void paintBrcaCnvTable(String gene) {
        HBox hBox;
        String text;
        if(gene.equals("BRCA1")) {
            hBox = brca1Plot;
            text = "brca1_";
        } else {
            hBox = brca2Plot;
            text = "brca2_";
        }
        List<BrcaCnvExon> brcaCnvExonList = this.brcaCnvExonList.stream().filter(item -> gene.equals(item.getGene()))
                .collect(Collectors.toList());

        hBox.getChildren().forEach(label -> {
            if(StringUtils.isNotEmpty(label.getId())) {
                if(label.getId().contains("utr")) {
                    Optional<BrcaCnvExon> optionalBrcaCnvExon = brcaCnvExonList.stream().filter(item ->
                            item.getExon().contains("UTR")).findFirst();
                    setLabelStyleClass(optionalBrcaCnvExon, label);
                } else {
                    Optional<BrcaCnvExon> optionalBrcaCnvExon = brcaCnvExonList.stream().filter(item ->
                            item.getExon().replaceAll("(exon)|(EXON)", "")
                                    .equals(label.getId().replaceAll(text, ""))).findFirst();
                    setLabelStyleClass(optionalBrcaCnvExon, label);
                }
            }
        });

    }

    private void setLabelStyleClass(Optional<BrcaCnvExon> optionalBrcaCnvExon, Node label) {
        if(optionalBrcaCnvExon.isPresent()) {
            BrcaCnvExon brcaCnvExon = optionalBrcaCnvExon.get();
            if((brcaCnvExon.getExpertCnv() != null &&
                    BrcaCNVCode.DUPLICATION.getCode().equals(brcaCnvExon.getExpertCnv())) ||
                    BrcaCNVCode.DUPLICATION.getCode().equals(brcaCnvExon.getSwCnv())) {
                label.getStyleClass().add("duplication_paint");
                ((Label)label).setText(BrcaCNVCode.DUPLICATION.getInitial());
            } else if((brcaCnvExon.getExpertCnv() != null &&
                    BrcaCNVCode.DELETION.getCode().equals(brcaCnvExon.getExpertCnv())) ||
                    BrcaCNVCode.DELETION.getCode().equals(brcaCnvExon.getSwCnv())) {
                label.getStyleClass().add("deletion_paint");
                ((Label)label).setText(BrcaCNVCode.DELETION.getInitial());
            } else {
                ((Label)label).setText(BrcaCNVCode.NORMAL.getInitial());
            }
        } else {
            label.getStyleClass().removeAll("deletion_paint", "duplication_paint");
            ((Label)label).setText("");
        }
    }

    private void addBrcaCnvTable(String gene) {
        List<BrcaCnvExon> brcaCnvExons = brcaCnvExonList.stream().filter(item -> gene.equals(item.getGene()))
                .collect(Collectors.toList());

        List<BrcaCnvExon> brcaCnvExonDeletionList = brcaCnvExons.stream().filter(brcaCnvExon ->
            (brcaCnvExon.getExpertCnv() != null &&
                    BrcaCNVCode.DELETION.getCode().equals(brcaCnvExon.getExpertCnv())) ||
                    BrcaCNVCode.DELETION.getCode().equals(brcaCnvExon.getSwCnv()))
                .collect(Collectors.toList());
        List<BrcaCnvExon> brcaCnvExonDuplicationList = brcaCnvExons.stream().filter(brcaCnvExon ->
                (brcaCnvExon.getExpertCnv() != null &&
                        BrcaCNVCode.DUPLICATION.getCode().equals(brcaCnvExon.getExpertCnv())) ||
                        BrcaCNVCode.DUPLICATION.getCode().equals(brcaCnvExon.getSwCnv()))
                .collect(Collectors.toList());

        if(!brcaCnvExonDeletionList.isEmpty()) {
            BrcaCnvResult brcaCnvResult = new BrcaCnvResult(gene, BrcaCNVCode.DELETION.getCode(),
                brcaCnvExonDeletionList.stream().map(item -> item.getExon().replaceAll("(exon)|(EXON)", ""))
                    .collect(Collectors.joining(", ")));
            brcaCnvResultList.add(brcaCnvResult);
        }

        if(!brcaCnvExonDuplicationList.isEmpty()) {
            BrcaCnvResult brcaCnvResult = new BrcaCnvResult(gene, BrcaCNVCode.DUPLICATION.getCode(),
                    brcaCnvExonDuplicationList.stream().map(item -> item.getExon().replaceAll("(exon)|(EXON)", ""))
                            .collect(Collectors.joining(", ")));
            brcaCnvResultList.add(brcaCnvResult);
        }
    }
}
