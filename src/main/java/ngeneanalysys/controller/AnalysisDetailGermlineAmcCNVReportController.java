package ngeneanalysys.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.model.BrcaCnvExon;
import ngeneanalysys.model.BrcaCnvResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jang
 * @since 2018-11-13
 */
public class AnalysisDetailGermlineAmcCNVReportController extends SubPaneController {

    private List<BrcaCnvExon> brcaCnvExonList;

    private List<BrcaCnvResult> brcaCnvResultList = new ArrayList<>();

    @FXML
    private TableView<BrcaCnvResult> heredAmcCnvResultTable;
    @FXML
    private TableColumn<BrcaCnvResult, String> geneTableColumn;
    @FXML
    private TableColumn<BrcaCnvResult, String> variantTableColumn;
    @FXML
    private TableColumn<BrcaCnvResult, String> classificationTableColumn;
    @FXML
    private TableColumn<BrcaCnvResult, String> inheritanceTableColumn;
    @FXML
    private TableColumn<BrcaCnvResult, String> parentalOriginTableColumn;

    @Override
    public void show(Parent root) throws IOException {

    }

}
