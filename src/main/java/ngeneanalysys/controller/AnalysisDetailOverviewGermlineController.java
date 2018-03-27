package ngeneanalysys.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedVariantAndInterpretationEvidence;
import ngeneanalysys.service.APIService;
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
 * @since 2017-08-25
 */
public class AnalysisDetailOverviewGermlineController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private Label pVariantsCountLabel;

    @FXML
    private Label pGenesCountLabel;

    @FXML
    private Label lpVariantsCountLabel;

    @FXML
    private Label lpGenesCountLabel;

    @FXML
    private Label usVariantsCountLabel;

    @FXML
    private Label usGenesCountLabel;

    @FXML
    private Label lbVariantsCountLabel;

    @FXML
    private Label lbGenesCountLabel;

    @FXML
    private Label bVariantsCountLabel;

    @FXML
    private Label bGenesCountLabel;

    @FXML
    private TableView<VariantAndInterpretationEvidence> pathogenicityTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> pathogenicityColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> geneColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> variantColumn;

    @FXML
    private Tooltip roiCoverageTooltip;

    @FXML
    private Label roiCoverageLabel;

    @FXML
    private Tooltip meanReadTooltip;

    @FXML
    private Label meanReadLabel;

    @FXML
    private Tooltip retainedReadTooltip;

    @FXML
    private Label retainedReadLabel;

    @FXML
    private Tooltip coverageUniformityTooltip;

    @FXML
    private Label coverageUniformityLabel;



    /** API 서버 통신 서비스 */
    private APIService apiService;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        Sample sample = (Sample) getParamMap().get("sample");

        //Tier Table Setting
        pathogenicityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                !StringUtils.isEmpty(cellData.getValue().getSnpInDel().getExpertPathogenicity()) ?
                        cellData.getValue().getSnpInDel().getExpertPathogenicity() :
                        cellData.getValue().getSnpInDel().getSwPathogenicity()));
        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        variantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));

        setDisplayItem();
    }

    public void setDisplayItem() {
        Sample sample = (Sample) getParamMap().get("sample");
        try {
            HttpClientResponse response = apiService.get("/analysisResults/sampleSnpInDels/" + sample.getId(), null,
                    null, false);

            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();

            List<VariantAndInterpretationEvidence> pathgenicList = returnVariant(list, "P");

            List<VariantAndInterpretationEvidence> likeylyPathogenic = returnVariant(list, "LP");

            List<VariantAndInterpretationEvidence> uncertatinSignificance = returnVariant(list, "US");

            List<VariantAndInterpretationEvidence> likelyBenign = returnVariant(list, "LB");

            List<VariantAndInterpretationEvidence> benign = returnVariant(list, "B");

            if(!pathogenicityTable.getItems().isEmpty()) pathogenicityTable.getItems().removeAll(pathogenicityTable.getItems());

            if(pathgenicList != null) {
                pathogenicityTable.getItems().addAll(FXCollections.observableArrayList(pathgenicList));
                pVariantsCountLabel.setText(String.valueOf(pathgenicList.size()));
                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                pathgenicList.stream().forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                pGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelInterpretation> snpInDelInterpretations = new ArrayList<>();
                pathgenicList.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        snpInDelInterpretations.add(item.getInterpretationEvidence());
                });

            }

            if(likeylyPathogenic != null) {
                pathogenicityTable.getItems().addAll(FXCollections.observableArrayList(likeylyPathogenic));
                lpVariantsCountLabel.setText(String.valueOf(likeylyPathogenic.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                likeylyPathogenic.stream().forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                lpGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelInterpretation> snpInDelInterpretations = new ArrayList<>();
                likeylyPathogenic.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        snpInDelInterpretations.add(item.getInterpretationEvidence());
                });
            }

            if(uncertatinSignificance != null) {
                pathogenicityTable.getItems().addAll(FXCollections.observableArrayList(uncertatinSignificance));
                usVariantsCountLabel.setText(String.valueOf(uncertatinSignificance.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                uncertatinSignificance.stream().forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                usGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelInterpretation> snpInDelInterpretations = new ArrayList<>();
                uncertatinSignificance.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        snpInDelInterpretations.add(item.getInterpretationEvidence());
                });
            }

            if(likelyBenign != null) {
                lbVariantsCountLabel.setText(String.valueOf(likelyBenign.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                likelyBenign.stream().forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                lbGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelInterpretation> snpInDelInterpretations = new ArrayList<>();
                likelyBenign.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        snpInDelInterpretations.add(item.getInterpretationEvidence());
                });
            }

            if(benign != null) {
                bVariantsCountLabel.setText(String.valueOf(benign.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                benign.stream().forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                bGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelInterpretation> snpInDelInterpretations = new ArrayList<>();
                benign.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        snpInDelInterpretations.add(item.getInterpretationEvidence());
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //기본 초기화
        settingOverallQC(sample.getId());
    }

    public List<VariantAndInterpretationEvidence> returnVariant(List<VariantAndInterpretationEvidence> list, String pathogenicity) {
        return list.stream().filter(item -> ((pathogenicity.equalsIgnoreCase(item.getSnpInDel().getExpertPathogenicity()) ||
                (StringUtils.isEmpty(item.getSnpInDel().getExpertPathogenicity()) && item.getSnpInDel().getSwPathogenicity().equalsIgnoreCase(pathogenicity)))))
                .collect(Collectors.toList());
    }

    public void settingOverallQC(int sampleId) {

        List<SampleQC> qcList = null;

        try {
            HttpClientResponse response = apiService.get("/analysisResults/sampleQCs/" + sampleId, null,
                    null, false);

            qcList = (List<SampleQC>) response.getMultiObjectBeforeConvertResponseToJSON(SampleQC.class, false);

            /*roiCoverageLabel.setText(findQCResult(qcList, "roi_coverage"));
            roiCoverageTooltip.setText(findQCTooltipString(qcList, "roi_coverage"));

            meanReadLabel.setText(findQCResult(qcList, "mean_read_quality"));
            meanReadTooltip.setText(findQCTooltipString(qcList, "mean_read_quality"));

            retainedReadLabel.setText(findQCResult(qcList, "retained_reads"));
            retainedReadTooltip.setText(findQCTooltipString(qcList, "retained_reads"));

            coverageUniformityLabel.setText(findQCResult(qcList, "coverage_uniformity"));
            coverageUniformityTooltip.setText(findQCTooltipString(qcList, "coverage_uniformity"));*/

        } catch(WebAPIException e) {
            e.printStackTrace();
        }
    }

    //qcList에서 해당 qc 결과를 반환
    private String findQCResult(List<SampleQC> qcList, String qc) {
        String result = "none";

        if(qcList != null && !qcList.isEmpty()) {
            Optional<SampleQC> findQC = qcList.stream().filter(sampleQC -> sampleQC.getQcType().equalsIgnoreCase(qc)).findFirst();
            if(findQC.isPresent() && !StringUtils.isEmpty(findQC.get().getQcResult())) {
                result = findQC.get().getQcResult();
            }
        }

        return result;
    }

    //qcList에서 해당 qc 결과를 반환
    private String findQCTooltipString(List<SampleQC> qcList, String qc) {
        String result = "";

        if(qcList != null && !qcList.isEmpty()) {
            Optional<SampleQC> findQC = qcList.stream().filter(sampleQC -> sampleQC.getQcType().equalsIgnoreCase(qc)).findFirst();
            if(findQC.isPresent()) {
                result = findQC.get().getQcDescription() + " " + findQC.get().getQcThreshold();
            }
        }

        return result;
    }

}
