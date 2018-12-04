package ngeneanalysys.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.code.enums.BrcaCNVCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.BrcaCnvExon;
import ngeneanalysys.model.BrcaCnvResult;
import ngeneanalysys.util.ConvertUtil;
import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2018-11-13
 */
public class AnalysisDetailGermlineCNVReportController extends SubPaneController {

    private List<BrcaCnvExon> brcaCnvExonList;

    private List<BrcaCnvResult> brcaCnvResultList = new ArrayList<>();

    @FXML
    private TableView<BrcaCnvResult> brcaCnvResultTable;
    @FXML
    private TableColumn<BrcaCnvResult, String> geneTableColumn;
    @FXML
    private TableColumn<BrcaCnvResult, String> cnvTableColumn;
    @FXML
    private TableColumn<BrcaCnvResult, String> exonTableColumn;

    /**
     * @param brcaCnvExonList
     */
    public void setBrcaCnvExonList(List<BrcaCnvExon> brcaCnvExonList) {
        if(!brcaCnvResultTable.getItems().isEmpty()) {
            brcaCnvResultTable.getItems().removeAll(brcaCnvResultTable.getItems());
        }
        this.brcaCnvExonList = brcaCnvExonList.stream().sorted((a, b) ->
        {
            if(a.getExon().contains("UTR")) {
                return -1;
            } else if(b.getExon().contains("UTR")) {
                return 1;
            } else {
                int intA = Integer.parseInt(a.getExon());
                int intB = Integer.parseInt(b.getExon());
                return Integer.compare(intA, intB);
            }
        }).collect(Collectors.toList());
        brcaCnvResultList.clear();
        addBrcaCnvTable("BRCA1");
        addBrcaCnvTable("BRCA2");
        if(!brcaCnvExonList.isEmpty()) {
            brcaCnvResultTable.getItems().addAll(brcaCnvResultList);
        }
        brcaCnvResultTable.refresh();
    }

    /**
     * @return brcaCnvExonList
     */
    public List<BrcaCnvExon> getBrcaCnvExonList() {
        return brcaCnvExonList;
    }

    /**
     * @return brcaCnvResultList
     */
    public List<BrcaCnvResult> getBrcaCnvResultList() {
        return brcaCnvResultList;
    }

    @Override
    public void show(Parent root) throws IOException {
        geneTableColumn.setCellValueFactory(item ->  new SimpleStringProperty(item.getValue().getGene()));
        cnvTableColumn.setCellValueFactory(item ->  new SimpleStringProperty(item.getValue().getCnv()));
        exonTableColumn.setCellValueFactory(item ->  new SimpleStringProperty(item.getValue().getResult()));
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
                        BrcaCNVCode.AMPLIFICATION.getCode().equals(brcaCnvExon.getExpertCnv())) ||
                        BrcaCNVCode.AMPLIFICATION.getCode().equals(brcaCnvExon.getSwCnv()))
                .collect(Collectors.toList());

        if(!brcaCnvExonDeletionList.isEmpty()) {
            BrcaCnvResult brcaCnvResult = new BrcaCnvResult(gene, WordUtils.capitalize(BrcaCNVCode.DELETION.getCode()),
                    ConvertUtil.convertBrcaCnvRegion(brcaCnvExonDeletionList.stream().map(BrcaCnvExon::getExon)
                            .collect(Collectors.toList()), gene));
            brcaCnvResultList.add(brcaCnvResult);
        }

        if(!brcaCnvExonDuplicationList.isEmpty()) {
            BrcaCnvResult brcaCnvResult = new BrcaCnvResult(gene, WordUtils.capitalize(BrcaCNVCode.AMPLIFICATION.getCode()),
                    ConvertUtil.convertBrcaCnvRegion(brcaCnvExonDuplicationList.stream().map(BrcaCnvExon::getExon)
                            .collect(Collectors.toList()), gene));
            brcaCnvResultList.add(brcaCnvResult);
        }
    }
}
