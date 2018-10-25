package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.BrcaCNV;
import ngeneanalysys.model.SampleView;
import ngeneanalysys.model.paged.PagedBrcaCNV;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2018-10-15
 */
public class AnalysisDetailBrcaCNVController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private HBox brca1ExonBox;
    @FXML
    private HBox brca2ExonBox;

    @FXML
    private RadioButton brca1RadioButton;

    @FXML
    private RadioButton brca2RadioButton;

    @FXML
    private TableView<BrcaCNV> cnvTableView;
    @FXML
    private TableColumn<BrcaCNV, String> exonTableColumn;
    @FXML
    private TableColumn<BrcaCNV, String> ampliconTableColumn;
    @FXML
    private TableColumn<BrcaCNV, String> referenceRatioTableColumn;
    @FXML
    private TableColumn<BrcaCNV, String> cnvValueTableColumn;
    @FXML
    private TableColumn<BrcaCNV, String> predictionTableColumn;

    private APIService apiService;

    private AnalysisDetailVariantsController variantsController;

    private List<BrcaCNV> brcaCNVList;

    /**
     * @param variantsController AnalysisDetailVariantsController
     */
    void setVariantsController(AnalysisDetailVariantsController variantsController) {
        this.variantsController = variantsController;
    }

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("BRCA cnv view...");
        apiService = APIService.getInstance();

        brca1RadioButton.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv) setBrcaTableView("BRCA1");
        });

        brca2RadioButton.selectedProperty().addListener((ob, ov, nv) -> {
            if(nv) setBrcaTableView("BRCA2");
        });

        exonTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getExon()));
        ampliconTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getAmplicon()));
        referenceRatioTableColumn.setCellValueFactory(item -> new SimpleStringProperty(
                cutBigDecimal(item.getValue().getNormalRangeMin()) + " - " +
                        cutBigDecimal(item.getValue().getNormalRangeMax())));
        cnvValueTableColumn.setCellValueFactory(item -> new SimpleStringProperty(
                cutBigDecimal(item.getValue().getCnvValue())));
        predictionTableColumn.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getPrediction()));

        setList();
        brca1RadioButton.selectedProperty().setValue(true);
        variantsController.getDetailContents().setCenter(root);
    }

    private String cutBigDecimal(BigDecimal bigDecimal) {
        if(!bigDecimal.toString().contains(".")) return bigDecimal.toString();
        return bigDecimal.toString().substring(0, bigDecimal.toString().indexOf('.') + 4);
    }

    public void setList() {
        SampleView sample = (SampleView)paramMap.get("sampleView");

        try {
            HttpClientResponse response = apiService.get("/analysisResults/brcaCnv/" + sample.getId(), null, null, false);
            PagedBrcaCNV pagedBrcaCNV = response.getObjectBeforeConvertResponseToJSON(PagedBrcaCNV.class);
            brcaCNVList = pagedBrcaCNV.getResult();
            Platform.runLater(() -> setBrcaCNVPlot("BRCA1"));
            Platform.runLater(() -> setBrcaCNVPlot("BRCA2"));
        } catch (WebAPIException wae) {
            DialogUtil.warning(wae.getHeaderText(), wae.getMessage(), mainApp.getPrimaryStage(), true);
        }
    }

    private void setBrcaTableView(final String gene) {
        if(cnvTableView.getItems() != null) {
            cnvTableView.getItems().removeAll(cnvTableView.getItems());
            cnvTableView.refresh();
        }

        List<BrcaCNV> list = getBrcaCNV(gene);

        if(!list.isEmpty()) cnvTableView.getItems().addAll(list);
    }

    private List<BrcaCNV> getBrcaCNV(final String gene) {
        if(brcaCNVList != null && !brcaCNVList.isEmpty()) {
            List<BrcaCNV> list = brcaCNVList.stream().filter(item -> gene.equals(item.getGene()))
                    .collect(Collectors.toList());

            if(!list.isEmpty()) return list;
        }
        return new ArrayList<>();
    }

    private Map<String, List<BrcaCNV>> groupingExon(List<BrcaCNV> list) {
        return list.stream().collect(Collectors.groupingBy(BrcaCNV::getExon));
    }

    private boolean compareCount(long numerator, long denominator) {
        return ((double)numerator / denominator) >= 0.8;
    }

    private void calcExonPlot(List<BrcaCNV> cnvs, String key, HBox box, final String boxId) {
        long duplicationCount = cnvs.stream().filter(cnv -> cnv.getPrediction().equals("Duplication")).count();
        long deletionCount = cnvs.stream().filter(cnv -> cnv.getPrediction().equals("Deletion")).count();
        long totalCount = cnvs.size();

        if(compareCount(deletionCount, totalCount)) {
            setColorHBox(box, "brca_cnv_deletion",boxId + key);
        } else if(compareCount(duplicationCount, totalCount)) {
            setColorHBox(box, "brca_cnv_duplication",boxId + key);
        }
    }

    private void setColorHBox(HBox box, final String styleClass, final String key) {
        Optional<Node> optionalNode = box.getChildren().stream()
                .filter(obj -> obj.getId() != null && obj.getId().equals(key)).findFirst();

        optionalNode.ifPresent(node -> node.getStyleClass().add(styleClass));
    }

    private void setBrcaCNVPlot(String gene) {
        List<BrcaCNV> brcaCNVList = getBrcaCNV(gene);
        if(!brcaCNVList.isEmpty()) {
            Map<String, List<BrcaCNV>> cnvGroup = groupingExon(brcaCNVList);

            Set<String> cnvKey = cnvGroup.keySet();
            HBox box;
            String boxId = "brca2_";
            if(gene.equals("BRCA1")) {
                box = brca1ExonBox;
                boxId = "brca1_";
            } else {
                box = brca2ExonBox;
            }

            for (String s : cnvKey) {
                if(s.contains("UTR")) {
                    calcExonPlot(cnvGroup.get(s), "utr", box, boxId);
                } else {
                    calcExonPlot(cnvGroup.get(s), s, box, boxId);
                }
            }
        }
    }

}
