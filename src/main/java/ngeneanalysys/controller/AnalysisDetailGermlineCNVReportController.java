package ngeneanalysys.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.code.enums.BrcaCNVCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.BrcaCnvExon;
import ngeneanalysys.model.BrcaCnvResult;
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.StringUtils;
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

    @FXML
    private Label countLabel;

    /**
     * @param brcaCnvExonList List<BrcaCnvExon>
     */
    void setBrcaCnvExonList(List<BrcaCnvExon> brcaCnvExonList) {
        if(!brcaCnvResultTable.getItems().isEmpty()) {
            brcaCnvResultTable.getItems().removeAll(brcaCnvResultTable.getItems());
        }
        this.brcaCnvExonList = brcaCnvExonList.stream().sorted((a, b) ->
        {
            if(a.getExon().equals("Promoter")) {
                return -1;
            } else if(b.getExon().equals("Promoter")) {
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
    List<BrcaCnvExon> getBrcaCnvExonList() {
        return brcaCnvExonList;
    }

    /**
     * @return brcaCnvResultList
     */
    List<BrcaCnvResult> getBrcaCnvResultList() {
        return brcaCnvResultList;
    }

    @Override
    public void show(Parent root) throws IOException {
        geneTableColumn.setCellValueFactory(item ->  new SimpleStringProperty(item.getValue().getGene()));
        cnvTableColumn.setCellValueFactory(item ->  new SimpleStringProperty(item.getValue().getCnv()));
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

        long deletionCount = 0;
        long amplificationCount = 0;
        long totalCount = 0;

        if(brcaCnvExonList != null) {
            deletionCount = brcaCnvExonList.stream().filter(item ->
                    (item.getExpertCnv() != null && item.getExpertCnv().equals(BrcaCNVCode.DELETION.getCode()))
                            || item.getSwCnv().equals(BrcaCNVCode.DELETION.getCode())
            ).count();

            amplificationCount = brcaCnvExonList.stream().filter(item ->
                    (item.getExpertCnv() != null && item.getExpertCnv().equals(BrcaCNVCode.AMPLIFICATION.getCode()))
                            || item.getSwCnv().equals(BrcaCNVCode.AMPLIFICATION.getCode())
            ).count();
        }

        totalCount = deletionCount + amplificationCount;

        if(totalCount > 0) {
            countLabel.setText("(Total : " + totalCount
                    + (deletionCount > 0 ? ", Deletion: " + deletionCount : "")
                    + (amplificationCount > 0 ? ", Amplification: " + amplificationCount : "") + ")");
        }
    }
}
