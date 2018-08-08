package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
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
import ngeneanalysys.util.*;
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
    private TableColumn<VariantAndInterpretationEvidence, Integer> positionColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> transcriptColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> ntChangeColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> aaChangeColumn;

    @FXML
    private Label roiCoverageLabel;

    @FXML
    private Label coverageUniformityLabel;

    @FXML
    private Label roiCoverageQCLabel;

    @FXML
    private Label coverageUniQCLabel;


    /** API 서버 통신 서비스 */
    private APIService apiService;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        //Tier Table Setting
        pathogenicityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                !StringUtils.isEmpty(cellData.getValue().getSnpInDel().getExpertPathogenicity()) ?
                        cellData.getValue().getSnpInDel().getExpertPathogenicity() :
                        cellData.getValue().getSnpInDel().getSwPathogenicity()));
        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        variantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        positionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSnpInDel().getGenomicCoordinate().getStartPosition()));
        transcriptColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getTranscript()));
        ntChangeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        aaChangeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChange()));

        setDisplayItem();
    }

    void setDisplayItem() {
        SampleView sample = (SampleView) getParamMap().get("sampleView");
        try {
            HttpClientResponse response = apiService.get("/analysisResults/sampleSnpInDels/" + sample.getId(), null,
                    null, false);

            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();

            if(list == null) {
                list = new ArrayList<>();
            }

            list = list.stream().filter(item -> item.getSnpInDel().getIsFalse().equalsIgnoreCase("N"))
                    .collect(Collectors.toList());

            List<VariantAndInterpretationEvidence> pathogenicList = returnVariant(list, "P");

            List<VariantAndInterpretationEvidence> likelyPathogenic = returnVariant(list, "LP");

            List<VariantAndInterpretationEvidence> uncertainSignificance = returnVariant(list, "US");

            List<VariantAndInterpretationEvidence> likelyBenign = returnVariant(list, "LB");

            List<VariantAndInterpretationEvidence> benign = returnVariant(list, "B");

            if(!pathogenicityTable.getItems().isEmpty()) pathogenicityTable.getItems().removeAll(pathogenicityTable.getItems());

            if(pathogenicList != null) {
                pathogenicityTable.getItems().addAll(FXCollections.observableArrayList(pathogenicList));
                pVariantsCountLabel.setText(String.valueOf(pathogenicList.size()));
                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                pathogenicList.forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                pGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelEvidence> snpInDelInterpretations = new ArrayList<>();
                pathogenicList.forEach(item -> {
                    if (ConvertUtil.findPrimaryEvidence(item.getSnpInDelEvidences()) != null)
                        snpInDelInterpretations.add(ConvertUtil.findPrimaryEvidence(item.getSnpInDelEvidences()));
                });

            }

            if(likelyPathogenic != null) {
                pathogenicityTable.getItems().addAll(FXCollections.observableArrayList(likelyPathogenic));
                lpVariantsCountLabel.setText(String.valueOf(likelyPathogenic.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                likelyPathogenic.forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                lpGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelEvidence> snpInDelInterpretations = new ArrayList<>();
                likelyPathogenic.forEach(item -> {
                    if (ConvertUtil.findPrimaryEvidence(item.getSnpInDelEvidences()) != null)
                        snpInDelInterpretations.add(ConvertUtil.findPrimaryEvidence(item.getSnpInDelEvidences()));
                });
            }

            if(uncertainSignificance != null) {
                pathogenicityTable.getItems().addAll(FXCollections.observableArrayList(uncertainSignificance));
                usVariantsCountLabel.setText(String.valueOf(uncertainSignificance.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                uncertainSignificance.forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                usGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelEvidence> snpInDelInterpretations = new ArrayList<>();
                uncertainSignificance.forEach(item -> {
                    if (ConvertUtil.findPrimaryEvidence(item.getSnpInDelEvidences()) != null)
                        snpInDelInterpretations.add(ConvertUtil.findPrimaryEvidence(item.getSnpInDelEvidences()));
                });
            }

            if(likelyBenign != null) {
                lbVariantsCountLabel.setText(String.valueOf(likelyBenign.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                likelyBenign.forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                lbGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelEvidence> snpInDelInterpretations = new ArrayList<>();
                likelyBenign.forEach(item -> {
                    if (ConvertUtil.findPrimaryEvidence(item.getSnpInDelEvidences()) != null)
                        snpInDelInterpretations.add(ConvertUtil.findPrimaryEvidence(item.getSnpInDelEvidences()));
                });
            }

            if(benign != null) {
                bVariantsCountLabel.setText(String.valueOf(benign.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                benign.forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                bGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelEvidence> snpInDelInterpretations = new ArrayList<>();
                benign.forEach(item -> {
                    if (ConvertUtil.findPrimaryEvidence(item.getSnpInDelEvidences()) != null)
                        snpInDelInterpretations.add(ConvertUtil.findPrimaryEvidence(item.getSnpInDelEvidences()));
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //기본 초기화
        settingOverallQC(sample.getId());
    }

    private List<VariantAndInterpretationEvidence> returnVariant(List<VariantAndInterpretationEvidence> list, String pathogenicity) {
        return list.stream().filter(item -> (pathogenicity.equalsIgnoreCase(item.getSnpInDel().getExpertPathogenicity()) ||
                (StringUtils.isEmpty(item.getSnpInDel().getExpertPathogenicity()) && item.getSnpInDel().getSwPathogenicity().equalsIgnoreCase(pathogenicity))))
                .collect(Collectors.toList());
    }

    private void setQCItem(final Label valueLabel, final Label qcLabel
            , final List<SampleQC> qcList, final String qcString) {
        valueLabel.setText(findQCResult(qcList, qcString).toUpperCase());
        //valueLabel.setTooltip(new Tooltip(findQCResultString(qcList, qcString)));
        //totalBaseTooltip.setText(findQCTooltipString(qcList, "total_base"));
        final String value = findQCTooltipString(qcList, qcString);
        qcLabel.setOnMouseClicked(ev ->
                PopOverUtil.openQCPopOver(qcLabel, value));
    }

    @SuppressWarnings("unchecked")
    private void settingOverallQC(int sampleId) {

        try {
            HttpClientResponse response = apiService.get("/analysisResults/sampleQCs/" + sampleId, null,
                    null, false);

            List<SampleQC> qcList = (List<SampleQC>) response.getMultiObjectBeforeConvertResponseToJSON(SampleQC.class, false);

            setQCItem(roiCoverageLabel, roiCoverageQCLabel, qcList, "roi_coverage");
            setQCItem(coverageUniformityLabel, coverageUniQCLabel, qcList, "coverage_uniformity");

        } catch(WebAPIException e) {
            DialogUtil.alert("QC ERROR", e.getMessage(), this.getMainApp().getPrimaryStage(), true);
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
                result = findQC.get().getQcDescription() + " " + findQC.get().getQcThreshold() + System.lineSeparator()
                        + "Value : " + findQC.get().getQcValue().stripTrailingZeros().toPlainString() + findQC.get().getQcUnit();
            }
        }

        return result;
    }

}
