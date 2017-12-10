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
        tierColumn.setCellValueFactory(cellData -> new SimpleStringProperty(ConvertUtil.tierConvert(cellData.getValue().getSnpInDel().getSwTier())));
        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSequenceInfo().getGene()));
        variantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        alleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()));
        therapeuticColumn.setCellValueFactory(cellData -> {
            Interpretation interpretation = cellData.getValue().getInterpretationEvidence();
            String text = "";
            if(interpretation != null) {
                if (!StringUtils.isEmpty(interpretation.getEvidenceLevelA()))
                    text += interpretation.getEvidenceLevelA() + ", ";
                if (!StringUtils.isEmpty(interpretation.getEvidenceLevelB()))
                    text += interpretation.getEvidenceLevelB() + ", ";
                if (!StringUtils.isEmpty(interpretation.getEvidenceLevelC()))
                    text += interpretation.getEvidenceLevelC() + ", ";
                if (!StringUtils.isEmpty(interpretation.getEvidenceLevelD()))
                    text += interpretation.getEvidenceLevelD() + ", ";
            }
            if(!"".equals(text)) {
                text = text.substring(0, text.length() - 2);
            }

            return new SimpleStringProperty(text);
            });

        //Negative Table Setting
        negativeGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSequenceInfo().getGene()));
        negativeVariantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        negativeCauseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInterpretationEvidence() != null
                ? cellData.getValue().getInterpretationEvidence().getEvidencePersistentNegative() : ""));
        negativeAlleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()));

        try {
            HttpClientResponse response = apiService.get("/analysisResults/sampleSnpInDels/" + sample.getId(), null,
                    null, false);

            PagedVariantAndInterpretationEvidence analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(PagedVariantAndInterpretationEvidence.class);

            List<VariantAndInterpretationEvidence> list = analysisResultVariantList.getResult();

            //negative list만 가져옴
            List<VariantAndInterpretationEvidence> negativeList = list.stream().filter(item -> (item.getInterpretationEvidence() != null &&
                    !StringUtils.isEmpty(item.getInterpretationEvidence().getEvidencePersistentNegative())
                    || (!StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && "TN".equalsIgnoreCase(item.getSnpInDel().getExpertTier()))))
                    .collect(Collectors.toList());

            //그 이후 list에서 negative list를 제거
            //list.removeAll(negativeList);
            //list = list.stream().filter(item -> item.getInterpretation().getInterpretationNegativeTesult() == null).collect(Collectors.toList());

            //Map<String, List<SnpInDel>> variantTierMap = list.stream().collect(Collectors.groupingBy(SnpInDel::getSwTier));

            //List<SnpInDel> tierOne = variantTierMap.get("T1");

            List<VariantAndInterpretationEvidence> tierOne = list.stream().filter(item -> (("T1".equalsIgnoreCase(item.getSnpInDel().getExpertTier()) ||
                    (StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && item.getSnpInDel().getSwTier().equalsIgnoreCase("T1")))))
                    .collect(Collectors.toList());

            List<VariantAndInterpretationEvidence> tierTwo = list.stream().filter(item -> (("T2".equalsIgnoreCase(item.getSnpInDel().getExpertTier()) ||
                    (StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && item.getSnpInDel().getSwTier().equalsIgnoreCase("T2")))))
                    .collect(Collectors.toList());

            List<VariantAndInterpretationEvidence> tierThree = list.stream().filter(item -> (("T3".equalsIgnoreCase(item.getSnpInDel().getExpertTier()) ||
                    (StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && item.getSnpInDel().getSwTier().equalsIgnoreCase("T3")))))
                    .collect(Collectors.toList());

            List<VariantAndInterpretationEvidence> tierFour = list.stream().filter(item -> (("T4".equalsIgnoreCase(item.getSnpInDel().getExpertTier()) ||
                    (StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && item.getSnpInDel().getSwTier().equalsIgnoreCase("T4")))))
                    .collect(Collectors.toList());

            if(tierOne != null) {
                tierTable.getItems().addAll(FXCollections.observableArrayList(tierOne));
                tierOneVariantsCountLabel.setText(String.valueOf(tierOne.size()));
                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                tierOne.stream().forEach(item -> {
                    if (item.getSnpInDel().getSequenceInfo() != null)
                        sequenceInfos.add(item.getSnpInDel().getSequenceInfo());
                });

                tierOneGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                tierOne.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        interpretations.add(item.getInterpretationEvidence());
                });

                long count = interpretations.stream().filter(item -> (!StringUtils.isEmpty(item.getEvidenceLevelA()) ||
                        !StringUtils.isEmpty(item.getEvidenceLevelB()) ||
                        !StringUtils.isEmpty(item.getEvidenceLevelC()) ||
                        !StringUtils.isEmpty(item.getEvidenceLevelD()))).count();
                tierOneTherapeuticLabel.setText(String.valueOf(count));
            }

            //List<SnpInDel> tierTwo = variantTierMap.get("T2");

            if(tierTwo != null) {
                tierTable.getItems().addAll(FXCollections.observableArrayList(tierTwo));
                tierTwoVariantsCountLabel.setText(String.valueOf(tierTwo.size()));

                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                tierTwo.stream().forEach(item -> {
                    if (item.getSnpInDel().getSequenceInfo() != null)
                        sequenceInfos.add(item.getSnpInDel().getSequenceInfo());
                });

                tierTwoGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                tierTwo.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        interpretations.add(item.getInterpretationEvidence());
                });

                long count = interpretations.stream().filter(item -> (!StringUtils.isEmpty(item.getEvidenceLevelA()) ||
                        !StringUtils.isEmpty(item.getEvidenceLevelB()) ||
                        !StringUtils.isEmpty(item.getEvidenceLevelC()) ||
                        !StringUtils.isEmpty(item.getEvidenceLevelD()))).count();
                tierTwoTherapeuticLabel.setText(String.valueOf(count));
            }

            //List<SnpInDel> tierThree  = variantTierMap.get("T3");

            if(tierThree != null) {
                tierThreeVariantsCountLabel.setText(String.valueOf(tierThree.size()));

                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                tierThree.stream().forEach(item -> {
                    if (item.getSnpInDel().getSequenceInfo() != null)
                        sequenceInfos.add(item.getSnpInDel().getSequenceInfo());
                });

                tierThreeGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                tierThree.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        interpretations.add(item.getInterpretationEvidence());
                });

                long count = interpretations.stream().filter(item -> (!StringUtils.isEmpty(item.getEvidenceLevelA()) ||
                        !StringUtils.isEmpty(item.getEvidenceLevelB()) ||
                        !StringUtils.isEmpty(item.getEvidenceLevelC()) ||
                        !StringUtils.isEmpty(item.getEvidenceLevelD()))).count();
                tierThreeTherapeuticLabel.setText(String.valueOf(count));
            }

            //List<SnpInDel> tierFour  = variantTierMap.get("T4");

            if(tierFour != null) {
                tierFourVariantsCountLabel.setText(String.valueOf(tierFour.size()));

                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                tierFour.stream().forEach(item -> {
                    if (item.getSnpInDel().getSequenceInfo() != null)
                        sequenceInfos.add(item.getSnpInDel().getSequenceInfo());
                });

                tierFourGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                tierFour.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        interpretations.add(item.getInterpretationEvidence());
                });

                long count = interpretations.stream().filter(item -> (!StringUtils.isEmpty(item.getEvidenceLevelA()) ||
                        !StringUtils.isEmpty(item.getEvidenceLevelB()) ||
                        !StringUtils.isEmpty(item.getEvidenceLevelC()) ||
                        !StringUtils.isEmpty(item.getEvidenceLevelD()))).count();
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
