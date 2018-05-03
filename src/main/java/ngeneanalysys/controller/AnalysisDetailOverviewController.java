package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedVariantAndInterpretationEvidence;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-08-25
 */
public class AnalysisDetailOverviewController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private Label tierOneVariantsCountLabel;

    @FXML
    private Label tierOneGenesCountLabel;

    /*@FXML
    private Label tierOneTherapeuticLabel;*/

    @FXML
    private Label tierTwoVariantsCountLabel;

    @FXML
    private Label tierTwoGenesCountLabel;

    /*@FXML
    private Label tierTwoTherapeuticLabel;*/

    @FXML
    private Label tierThreeVariantsCountLabel;

    @FXML
    private Label tierThreeGenesCountLabel;

    @FXML
    private Label tierFourVariantsCountLabel;

    @FXML
    private Label tierFourGenesCountLabel;

    @FXML
    private TableView<VariantAndInterpretationEvidence> tierTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierColumn;

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
    private TableColumn<VariantAndInterpretationEvidence, Object> therapeuticColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, BigDecimal> alleleFrequencyColumn;

    @FXML
    private Label totalBaseLabel;

    @FXML
    private Label q30Label;

    @FXML
    private Label mappedLabel;

    @FXML
    private Label onTargetLabel;

    @FXML
    private Label onTargetCoverageLabel;

    @FXML
    private Label duplicatedReadsLabel;

    @FXML
    private Label roiCoverageLabel;

    /*@FXML
    private Tooltip totalBaseTooltip;

    @FXML
    private Tooltip q30Tooltip;

    @FXML
    private Tooltip mappedBaseTooltip;

    @FXML
    private Tooltip onTargetTooltip;

    @FXML
    private Tooltip onTargetCoverageTooltip;

    @FXML
    private Tooltip duplicatedReadsTooltip;

    @FXML
    private Tooltip roiCoverageTooltip;*/

    @FXML
    private Label totalBaseQCLabel;

    @FXML
    private Label q30QCLabel;

    @FXML
    private Label mappedBaseQCLabel;

    @FXML
    private Label onTargetQCLabel;

    @FXML
    private Label onTargetCoverageQCLabel;

    @FXML
    private Label dupicatedReadsQCLabel;

    @FXML
    private Label roiCoverageQCLabel;

    /** API 서버 통신 서비스 */
    private APIService apiService;

    @Override
    public void show(Parent root) throws IOException {
        logger.debug("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        //Tier Table Setting
        tierColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                !StringUtils.isEmpty(cellData.getValue().getSnpInDel().getExpertTier()) ?
                        ConvertUtil.tierConvert(cellData.getValue().getSnpInDel().getExpertTier()) :
                ConvertUtil.tierConvert(cellData.getValue().getSnpInDel().getSwTier())));
        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        variantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        alleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()));
        positionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSnpInDel().getGenomicCoordinate().getStartPosition()));
        transcriptColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getTranscript()));
        ntChangeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        aaChangeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChange()));
        therapeuticColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDelEvidences()));
        therapeuticColumn.setCellFactory(param -> new TableCell<VariantAndInterpretationEvidence, Object>() {
            @Override
            public void updateItem(Object item, boolean empty) {
                HBox hBox = new HBox();
                hBox.setSpacing(5);
                hBox.setAlignment(Pos.CENTER);
                if(item != null) {
                    List<SnpInDelEvidence> interpretation = (List<SnpInDelEvidence>) item;

                    Map<String, List<SnpInDelEvidence>> evidenceInterpretation = interpretation.stream().collect(
                            Collectors.groupingBy(evidence -> evidence.getEvidenceLevel()));

                    createEvidenceLabel(evidenceInterpretation.get("A"), hBox, "A");
                    createEvidenceLabel(evidenceInterpretation.get("B"), hBox, "B");
                    createEvidenceLabel(evidenceInterpretation.get("C"), hBox, "C");
                    createEvidenceLabel(evidenceInterpretation.get("D"), hBox, "D");
                }
                setGraphic(hBox);
            }
        });
        setDisplayItem();
    }

    public void createEvidenceLabel(List<SnpInDelEvidence> interpretation, HBox hBox, String evidenceLevel) {
        if(interpretation == null || interpretation.isEmpty()) {
            return;
        }
        Label label = new Label(evidenceLevel);
        label.getStyleClass().remove("label");
        label.getStyleClass().add("interpretation_" + evidenceLevel);
        Tooltip tooltip = new Tooltip();
        label.setTooltip(tooltip);
        for (SnpInDelEvidence evidence : interpretation) {
            tooltip.setText(tooltip.getText() +evidence.getEvidenceType() + " : " + evidence.getEvidence() + "\n");
        }
        hBox.getChildren().add(label);
    }


    private List<VariantAndInterpretationEvidence> settingTierList(List<VariantAndInterpretationEvidence> allTierList, String tier) {
        if(!StringUtils.isEmpty(tier)) {
            return allTierList.stream().filter(item -> ((tier.equalsIgnoreCase(item.getSnpInDel().getExpertTier()) ||
                    (StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && tier.equalsIgnoreCase(item.getSnpInDel().getSwTier())))))
                    .collect(Collectors.toList());
        }

        return null;
    }

    private long countTherapeutic (List<SnpInDelEvidence> snpInDelInterpretations) {
        return snpInDelInterpretations.stream().filter(item -> (item.getEvidence().equalsIgnoreCase("therapeutic"))).count();
    }

    public void setDisplayItem() {
        Sample sample = (Sample) getParamMap().get("sample");
        try {
            HttpClientResponse response = apiService.get("/analysisResults/sampleSnpInDels/" + sample.getId(), null,
                    null, false);

            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();

            List<VariantAndInterpretationEvidence> tierOne = settingTierList(list, "T1");

            List<VariantAndInterpretationEvidence> tierTwo = settingTierList(list, "T2");

            List<VariantAndInterpretationEvidence> tierThree = settingTierList(list, "T3");

            List<VariantAndInterpretationEvidence> tierFour = settingTierList(list, "T4");

            if(!tierTable.getItems().isEmpty()) tierTable.getItems().removeAll(tierTable.getItems());

            if(tierOne != null) {
                tierTable.getItems().addAll(FXCollections.observableArrayList(tierOne));
                tierOneVariantsCountLabel.setText(String.valueOf(tierOne.size()));
                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                tierOne.forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                tierOneGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelEvidence> snpInDelInterpretations = new ArrayList<>();
                tierOne.forEach(item -> {
                    SnpInDelEvidence snpInDelEvidence = ConvertUtil.findPrimaryEvidence(item.getSnpInDelEvidences());
                    if (snpInDelEvidence != null) {
                        snpInDelInterpretations.add(snpInDelEvidence);
                    }

                });

                //long count = snpInDelInterpretations.size();
                //long count = countTherapeutic(snpInDelInterpretations);
                //tierOneTherapeuticLabel.setText(String.valueOf(count));
            }

            if(tierTwo != null) {
                tierTable.getItems().addAll(FXCollections.observableArrayList(tierTwo));
                tierTwoVariantsCountLabel.setText(String.valueOf(tierTwo.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                tierTwo.forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                tierTwoGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelEvidence> snpInDelInterpretations = new ArrayList<>();
                tierTwo.forEach(item -> {
                    SnpInDelEvidence snpInDelEvidence = ConvertUtil.findPrimaryEvidence(item.getSnpInDelEvidences());
                    if (snpInDelEvidence != null) {
                        snpInDelInterpretations.add(snpInDelEvidence);
                    }

                });

                //long count = countTherapeutic(snpInDelInterpretations);
                //tierTwoTherapeuticLabel.setText(String.valueOf(count));
            }

            if(tierThree != null) {
                tierThreeVariantsCountLabel.setText(String.valueOf(tierThree.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                tierThree.forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                tierThreeGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");
            }

            if(tierFour != null) {
                tierFourVariantsCountLabel.setText(String.valueOf(tierFour.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                tierFour.stream().forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                tierFourGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //기본 초기화
        settingOverallQC(sample.getId());
    }

    public void setQCItem(final Label valueLabel, final Label QCLabel
            , final List<SampleQC> qcList, final String qcString) {
        valueLabel.setText(findQCResult(qcList, qcString).toUpperCase());
        valueLabel.setTooltip(new Tooltip(findQCResultString(qcList, qcString)));
        //totalBaseTooltip.setText(findQCTooltipString(qcList, "total_base"));
        final String value = findQCTooltipString(qcList, qcString);
        QCLabel.setOnMouseClicked(ev ->
                PopOverUtil.openQCPopOver(QCLabel, value));
    }

    public void settingOverallQC(int sampleId) {

        List<SampleQC> qcList = null;

        try {
            HttpClientResponse response = apiService.get("/analysisResults/sampleQCs/" + sampleId, null,
                    null, false);

            qcList = (List<SampleQC>) response.getMultiObjectBeforeConvertResponseToJSON(SampleQC.class, false);

            setQCItem(totalBaseLabel, totalBaseQCLabel, qcList, "total_base");
            setQCItem(q30Label, q30QCLabel, qcList, "q30_trimmed_base");
            setQCItem(mappedLabel, mappedBaseQCLabel, qcList, "mapped_base");
            setQCItem(onTargetLabel, onTargetQCLabel, qcList, "on_target");
            setQCItem(onTargetCoverageLabel, onTargetCoverageQCLabel, qcList, "on_target_coverage");
            setQCItem(duplicatedReadsLabel, dupicatedReadsQCLabel, qcList, "duplicated_reads");
            setQCItem(roiCoverageLabel, roiCoverageQCLabel, qcList, "roi_coverage");

        } catch(WebAPIException e) {
            DialogUtil.alert("QC ERROR", e.getMessage(), this.getMainApp().getPrimaryStage(), true);
        }
    }

    //qcList에서 해당 qc 결과를 반환
    private String findQCResult(List<SampleQC> qcList, String qc) {
        String result = "none";

        if(qcList != null && !qcList.isEmpty()) {
            Optional<SampleQC> findQC = qcList.stream().filter(sampleQC -> sampleQC.getQcType().equalsIgnoreCase(qc)).findFirst();
            if(findQC.isPresent()) {
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

    private String findQCResultString(List<SampleQC> qcList, String qc) {
        String result = "";

        if(qcList != null && !qcList.isEmpty()) {
            Optional<SampleQC> findQC = qcList.stream().filter(sampleQC -> sampleQC.getQcType().equalsIgnoreCase(qc)).findFirst();
            if(findQC.isPresent()) {
                result = findQC.get().getQcValue() + findQC.get().getQcUnit();
            }
        }

        return result;
    }

}
