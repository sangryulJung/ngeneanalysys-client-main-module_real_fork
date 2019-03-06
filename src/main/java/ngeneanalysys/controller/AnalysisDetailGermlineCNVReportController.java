package ngeneanalysys.controller;

import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.LinkedList;
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
    private TableColumn<BrcaCnvResult, Boolean> coverageTableColumn;

    @FXML
    private Label countLabel;

    /**
     * @param brcaCnvExonList List<BrcaCnvExon>
     */
    void setBrcaCnvExonList(List<BrcaCnvExon> brcaCnvExonList) {
        if(!brcaCnvResultTable.getItems().isEmpty()) {
            brcaCnvResultTable.getItems().removeAll(brcaCnvResultTable.getItems());
        }
        this.brcaCnvExonList = brcaCnvExonList.stream()
                .filter(item -> item.getExon().matches("[0-9]*"))
                .sorted((a, b) -> {
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
        coverageTableColumn.setCellValueFactory(item ->  new SimpleBooleanProperty(item == null));
        coverageTableColumn.setCellFactory(cell -> new TableCell<BrcaCnvResult, Boolean>(){
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                if(item == null || item || empty) {
                    setText(null);
                } else {
                    BrcaCnvResult brcaCnvResult = this.getTableView().getItems().get(
                            this.getIndex());
                    LinkedList<BrcaCnvExon> list = brcaCnvExonList.stream()
                            .filter(brcaCnvExon -> brcaCnvResult.getGene().equals(brcaCnvExon.getGene()))
                            .collect(Collectors.toCollection(LinkedList::new));
                    setText(list.getFirst().getExon() + " ~ " + list.getLast().getExon());
                }
            }
        });
    }

    private void addBrcaCnvTable(String gene) {
        List<BrcaCnvExon> brcaCnvExons = brcaCnvExonList.stream().filter(item -> gene.equals(item.getGene())
                && item.getIncludedInReport().equals("Y"))
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
                            .collect(Collectors.toList()), gene));
            brcaCnvResultList.add(brcaCnvResult);
        }

        if(!brcaCnvExonDuplicationList.isEmpty()) {
            BrcaCnvResult brcaCnvResult = new BrcaCnvResult(gene, WordUtils.capitalize(BrcaCNVCode.COPY_GAIN.getCode()),
                    ConvertUtil.convertBrcaCnvRegion(brcaCnvExonDuplicationList.stream().map(BrcaCnvExon::getExon)
                            .collect(Collectors.toList()), gene));
            brcaCnvResultList.add(brcaCnvResult);
        }

        long deletionCount = 0;
        long amplificationCount = 0;
        long totalCount = 0;

        if(brcaCnvExonList != null) {
            deletionCount = brcaCnvExonList.stream().filter(item ->
                    item.getIncludedInReport().equals("Y") &&
                    (StringUtils.isNotEmpty(item.getExpertCnv()) &&
                            BrcaCNVCode.COPY_LOSS.getCode().equals(item.getExpertCnv())) ||
                            (StringUtils.isEmpty(item.getExpertCnv()) &&
                                    BrcaCNVCode.COPY_LOSS.getCode().equals(item.getSwCnv()))).count();

            amplificationCount = brcaCnvExonList.stream().filter(item ->
                    item.getIncludedInReport().equals("Y") &&
                    (StringUtils.isNotEmpty(item.getExpertCnv()) &&
                            BrcaCNVCode.COPY_GAIN.getCode().equals(item.getExpertCnv())) ||
                            (StringUtils.isEmpty(item.getExpertCnv()) &&
                                    BrcaCNVCode.COPY_GAIN.getCode().equals(item.getSwCnv()))).count();
        }

        totalCount = deletionCount + amplificationCount;

        if(totalCount > 0) {
            countLabel.setText("(Total : " + totalCount
                    + (deletionCount > 0 ? ", Copy loss: " + deletionCount : "")
                    + (amplificationCount > 0 ? ", Copy Gain: " + amplificationCount : "") + ")");
        } else {
            countLabel.setText("(Total : " + totalCount + ")");
        }
    }
}
