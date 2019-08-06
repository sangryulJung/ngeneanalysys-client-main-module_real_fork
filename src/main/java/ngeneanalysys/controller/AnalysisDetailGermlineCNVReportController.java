package ngeneanalysys.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.code.constants.CommonConstants;
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
                .filter(item -> item.getExon().matches(CommonConstants.NUMBER_PATTERN))
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
        setCountLabel();
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

    private boolean checkCnv(BrcaCnvExon brcaCnvExon, String value) {
        return (StringUtils.isNotEmpty(brcaCnvExon.getExpertCnv()) && brcaCnvExon.getExpertCnv().equals(value))
                || (StringUtils.isEmpty(brcaCnvExon.getExpertCnv()) && brcaCnvExon.getSwCnv().equals(value));
    }

    private void setCountLabel() {
        long brca1DeletionCount = 0;
        long brca1AmplificationCount = 0;
        long brca2DeletionCount = 0;
        long brca2AmplificationCount = 0;

        if(brcaCnvExonList != null) {
            List<BrcaCnvExon> brcaCnvExons = brcaCnvExonList.stream()
                    .filter(item -> item.getIncludedInReport().equals("Y"))
                    .collect(Collectors.toList());

            brca1DeletionCount = brcaCnvExons.stream().filter(item ->
                            item.getGene().equals("BRCA1") &&
                            (checkCnv(item, BrcaCNVCode.COPY_LOSS.getCode()))).count();

            brca1AmplificationCount = brcaCnvExons.stream().filter(item ->
                            item.getGene().equals("BRCA1") &&
                                    (checkCnv(item, BrcaCNVCode.COPY_GAIN.getCode()))).count();

            brca2DeletionCount = brcaCnvExons.stream().filter(item ->
                            item.getGene().equals("BRCA2") &&
                                    (checkCnv(item, BrcaCNVCode.COPY_LOSS.getCode()))).count();

            brca2AmplificationCount = brcaCnvExons.stream().filter(item ->
                            item.getGene().equals("BRCA2") &&
                                    (checkCnv(item, BrcaCNVCode.COPY_GAIN.getCode()))).count();
        }

        if((brca1DeletionCount + brca1AmplificationCount) > 0
                || (brca2DeletionCount + brca2AmplificationCount) > 0) {
            String brca1Text = "";
            String brca2Text = "";

            if(brca1DeletionCount > 0 && brca1AmplificationCount > 0) {
                brca1Text = "BRCA1 Copy Loss : " + brca1DeletionCount + ", Copy Gain : " + brca1AmplificationCount;
            } else if(brca1DeletionCount > 0) {
                brca1Text = "BRCA1 Copy Loss : " + brca1DeletionCount;
            } else if(brca1AmplificationCount > 0) {
                brca1Text = "BRCA1 Copy Gain : " + brca1AmplificationCount;
            }

            if(brca2DeletionCount > 0 && brca2AmplificationCount > 0) {
                brca2Text = "BRCA2 Copy Loss : " + brca2DeletionCount + ", Copy Gain : " + brca2AmplificationCount;
            } else if(brca2DeletionCount > 0) {
                brca2Text = "BRCA2 Copy Loss : " + brca2DeletionCount;
            } else if(brca2AmplificationCount > 0) {
                brca2Text = "BRCA2 Copy Gain : " + brca2AmplificationCount;
            }

            countLabel.setText("(" + brca1Text +
                    ((StringUtils.isNotEmpty(brca1Text) && (StringUtils.isNotEmpty(brca2Text))) ? " | " : "")
                    + brca2Text + ")");
        } else {
            countLabel.setText("");
        }
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
    }
}
