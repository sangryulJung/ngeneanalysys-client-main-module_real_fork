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
import ngeneanalysys.util.ConvertUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @FXML
    private Label tierOneTherapeuticLabel;

    @FXML
    private Label tierTwoVariantsCountLabel;

    @FXML
    private Label tierTwoGenesCountLabel;

    @FXML
    private Label tierTwoTherapeuticLabel;

    @FXML
    private Label tierThreeVariantsCountLabel;

    @FXML
    private Label tierThreeGenesCountLabel;

    @FXML
    private Label tierThreeTherapeuticLabel;

    @FXML
    private Label tierFourVariantsCountLabel;

    @FXML
    private Label tierFourGenesCountLabel;

    @FXML
    private Label tierFourTherapeuticLabel;

    @FXML
    private TableView<VariantAndInterpretationEvidence> tierTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> tierColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> geneColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> variantColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> therapeuticColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, BigDecimal> alleleFrequencyColumn;

    @FXML
    private TableView<VariantAndInterpretationEvidence> pertinentNegativesTable;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> negativeGeneColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> negativeVariantColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, String> negativeCauseColumn;

    @FXML
    private TableColumn<VariantAndInterpretationEvidence, BigDecimal> negativeAlleleFrequencyColumn;

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

    @FXML
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
    private Tooltip roiCoverageTooltip;

    @FXML
    private Label diseaseLabel;

    /** API 서버 통신 서비스 */
    private APIService apiService;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        Sample sample = (Sample) getParamMap().get("sample");

        List<Diseases> diseases = (List<Diseases>) mainController.getBasicInformationMap().get("diseases");
        Optional<Diseases> diseasesOptional = diseases.stream().filter(disease -> disease.getId() == sample.getDiseaseId()).findFirst();
        if(diseasesOptional.isPresent()) {
            String diseaseName = diseasesOptional.get().getName();
            diseaseLabel.setText(diseaseName);
        }

        //Tier Table Setting
        tierColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                !StringUtils.isEmpty(cellData.getValue().getSnpInDel().getExpertTier()) ?
                        ConvertUtil.tierConvert(cellData.getValue().getSnpInDel().getExpertTier()) :
                ConvertUtil.tierConvert(cellData.getValue().getSnpInDel().getSwTier())));
        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        variantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        alleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()));
        therapeuticColumn.setCellValueFactory(cellData -> {
            SnpInDelInterpretation snpInDelInterpretation = cellData.getValue().getInterpretationEvidence();
            String text = "";
            if(snpInDelInterpretation != null) {
                if (!StringUtils.isEmpty(snpInDelInterpretation.getTherapeuticEvidence().getLevelA()))
                    text += snpInDelInterpretation.getTherapeuticEvidence().getLevelA() + ", ";
                if (!StringUtils.isEmpty(snpInDelInterpretation.getTherapeuticEvidence().getLevelB()))
                    text += snpInDelInterpretation.getTherapeuticEvidence().getLevelB() + ", ";
                if (!StringUtils.isEmpty(snpInDelInterpretation.getTherapeuticEvidence().getLevelC()))
                    text += snpInDelInterpretation.getTherapeuticEvidence().getLevelC() + ", ";
                if (!StringUtils.isEmpty(snpInDelInterpretation.getTherapeuticEvidence().getLevelD()))
                    text += snpInDelInterpretation.getTherapeuticEvidence().getLevelD() + ", ";
            }
            if(!"".equals(text)) {
                text = text.substring(0, text.length() - 2);
            }

            return new SimpleStringProperty(text);
            });

        //Negative Table Setting
        negativeGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getGenomicCoordinate().getGene()));
        negativeVariantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        //negativeCauseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInterpretationEvidence() != null
        //        ? cellData.getValue().getInterpretationEvidence().getEvidencePersistentNegative() : ""));
        negativeAlleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()));

        setDisplayItem();
    }

    public List<VariantAndInterpretationEvidence> settingTierList(List<VariantAndInterpretationEvidence> allTierList, String tier) {
        if(!StringUtils.isEmpty(tier)) {
            return allTierList.stream().filter(item -> ((tier.equalsIgnoreCase(item.getSnpInDel().getExpertTier()) ||
                    (StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && tier.equalsIgnoreCase(item.getSnpInDel().getSwTier())))))
                    .collect(Collectors.toList());
        }

        return null;
    }

    public long countTherapeutic (List<SnpInDelInterpretation> snpInDelInterpretations) {
        return snpInDelInterpretations.stream().filter(item -> (!StringUtils.isEmpty(item.getTherapeuticEvidence().getLevelA()) ||
                !StringUtils.isEmpty(item.getTherapeuticEvidence().getLevelB()) ||
                !StringUtils.isEmpty(item.getTherapeuticEvidence().getLevelC()) ||
                !StringUtils.isEmpty(item.getTherapeuticEvidence().getLevelD()))).count();
    }

    public void setDisplayItem() {
        Sample sample = (Sample) getParamMap().get("sample");
        try {
            HttpClientResponse response = apiService.get("/analysisResults/sampleSnpInDels/" + sample.getId(), null,
                    null, false);

            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();

            List<VariantAndInterpretationEvidence> negativeList = list.stream().filter(item ->
                    ((!StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && "TN".equalsIgnoreCase(item.getSnpInDel().getExpertTier()))) ||
                            (StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && "TN".equalsIgnoreCase(item.getSnpInDel().getSwTier())))
                    .collect(Collectors.toList());

            List<VariantAndInterpretationEvidence> tierOne = settingTierList(list, "T1");

            List<VariantAndInterpretationEvidence> tierTwo = settingTierList(list, "T2");

            List<VariantAndInterpretationEvidence> tierThree = settingTierList(list, "T3");

            List<VariantAndInterpretationEvidence> tierFour = settingTierList(list, "T4");

            if(!tierTable.getItems().isEmpty()) tierTable.getItems().removeAll(tierTable.getItems());
            if(!pertinentNegativesTable.getItems().isEmpty()) pertinentNegativesTable.getItems().removeAll(pertinentNegativesTable.getItems());

            if(tierOne != null) {
                tierTable.getItems().addAll(FXCollections.observableArrayList(tierOne));
                tierOneVariantsCountLabel.setText(String.valueOf(tierOne.size()));
                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                tierOne.stream().forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                tierOneGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelInterpretation> snpInDelInterpretations = new ArrayList<>();
                tierOne.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        snpInDelInterpretations.add(item.getInterpretationEvidence());
                });

                long count = snpInDelInterpretations.stream().filter(item -> (!StringUtils.isEmpty(item.getTherapeuticEvidence().getLevelA()) ||
                        !StringUtils.isEmpty(item.getTherapeuticEvidence().getLevelB()) ||
                        !StringUtils.isEmpty(item.getTherapeuticEvidence().getLevelC()) ||
                        !StringUtils.isEmpty(item.getTherapeuticEvidence().getLevelD()))).count();
                tierOneTherapeuticLabel.setText(String.valueOf(count));
            }

            if(tierTwo != null) {
                tierTable.getItems().addAll(FXCollections.observableArrayList(tierTwo));
                tierTwoVariantsCountLabel.setText(String.valueOf(tierTwo.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                tierTwo.stream().forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                tierTwoGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelInterpretation> snpInDelInterpretations = new ArrayList<>();
                tierTwo.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        snpInDelInterpretations.add(item.getInterpretationEvidence());
                });

                long count = countTherapeutic(snpInDelInterpretations);
                tierTwoTherapeuticLabel.setText(String.valueOf(count));
            }

            if(tierThree != null) {
                tierThreeVariantsCountLabel.setText(String.valueOf(tierThree.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                tierThree.stream().forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                tierThreeGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelInterpretation> snpInDelInterpretations = new ArrayList<>();
                tierThree.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        snpInDelInterpretations.add(item.getInterpretationEvidence());
                });

                long count = countTherapeutic(snpInDelInterpretations);
                tierThreeTherapeuticLabel.setText(String.valueOf(count));
            }

            if(tierFour != null) {
                tierFourVariantsCountLabel.setText(String.valueOf(tierFour.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                tierFour.stream().forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                tierFourGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");

                List<SnpInDelInterpretation> snpInDelInterpretations = new ArrayList<>();
                tierFour.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        snpInDelInterpretations.add(item.getInterpretationEvidence());
                });

                long count = countTherapeutic(snpInDelInterpretations);
                tierFourTherapeuticLabel.setText(String.valueOf(count));
            }

            if(negativeList != null) {
                pertinentNegativesTable.setItems(FXCollections.observableArrayList(negativeList));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //기본 초기화
        settingOverallQC(sample.getId());
    }

    public void settingOverallQC(int sampleId) {

        List<SampleQC> qcList = null;

        try {
            HttpClientResponse response = apiService.get("/analysisResults/sampleQCs/" + sampleId, null,
                    null, false);

            qcList = (List<SampleQC>) response.getMultiObjectBeforeConvertResponseToJSON(SampleQC.class, false);

            totalBaseLabel.setText(findQCResult(qcList, "total_base"));
            totalBaseTooltip.setText(findQCTooltipString(qcList, "total_base"));
            q30Label.setText(findQCResult(qcList, "q30_trimmed_base"));
            q30Tooltip.setText(findQCTooltipString(qcList, "q30_trimmed_base"));
            mappedLabel.setText(findQCResult(qcList, "mapped_base"));
            mappedBaseTooltip.setText(findQCTooltipString(qcList, "mapped_base"));
            onTargetLabel.setText(findQCResult(qcList, "on_target"));
            onTargetTooltip.setText(findQCTooltipString(qcList, "on_target"));
            onTargetCoverageLabel.setText(findQCResult(qcList, "on_target_coverage"));
            onTargetCoverageTooltip.setText(findQCTooltipString(qcList, "on_target_coverage"));
            duplicatedReadsLabel.setText(findQCResult(qcList, "duplicated_reads"));
            duplicatedReadsTooltip.setText(findQCTooltipString(qcList, "duplicated_reads"));
            roiCoverageLabel.setText(findQCResult(qcList, "roi_coverage"));
            roiCoverageTooltip.setText(findQCTooltipString(qcList, "roi_coverage"));

        } catch(WebAPIException e) {
            e.printStackTrace();
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

}
