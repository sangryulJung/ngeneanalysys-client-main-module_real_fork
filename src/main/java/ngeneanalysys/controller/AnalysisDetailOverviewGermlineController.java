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
    private Label diseasetextLabel;

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
            if(!diseaseName.equalsIgnoreCase("N/A")) {
                diseaseLabel.setText(diseaseName);
            } else {
                diseasetextLabel.setText("");
                diseaseLabel.setText("");
            }
        }

        //Tier Table Setting
        pathogenicityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSwPathogenicityLevel()));
        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSequenceInfo().getGene()));
        variantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));

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

            if(pathgenicList != null) {
                pathogenicityTable.getItems().addAll(FXCollections.observableArrayList(pathgenicList));
                pVariantsCountLabel.setText(String.valueOf(pathgenicList.size()));
                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                pathgenicList.stream().forEach(item -> {
                    if (item.getSnpInDel().getSequenceInfo() != null)
                        sequenceInfos.add(item.getSnpInDel().getSequenceInfo());
                });

                pGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                pathgenicList.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        interpretations.add(item.getInterpretationEvidence());
                });

            }

            //List<SnpInDel> tierTwo = variantTierMap.get("T2");

            if(likeylyPathogenic != null) {
                pathogenicityTable.getItems().addAll(FXCollections.observableArrayList(likeylyPathogenic));
                lpVariantsCountLabel.setText(String.valueOf(likeylyPathogenic.size()));

                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                likeylyPathogenic.stream().forEach(item -> {
                    if (item.getSnpInDel().getSequenceInfo() != null)
                        sequenceInfos.add(item.getSnpInDel().getSequenceInfo());
                });

                lpGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                likeylyPathogenic.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        interpretations.add(item.getInterpretationEvidence());
                });
            }

            //List<SnpInDel> tierThree  = variantTierMap.get("T3");

            if(uncertatinSignificance != null) {
                pathogenicityTable.getItems().addAll(FXCollections.observableArrayList(uncertatinSignificance));
                usVariantsCountLabel.setText(String.valueOf(uncertatinSignificance.size()));

                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                uncertatinSignificance.stream().forEach(item -> {
                    if (item.getSnpInDel().getSequenceInfo() != null)
                        sequenceInfos.add(item.getSnpInDel().getSequenceInfo());
                });

                usGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                uncertatinSignificance.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        interpretations.add(item.getInterpretationEvidence());
                });
            }

            //List<SnpInDel> tierFour  = variantTierMap.get("T4");

            if(likelyBenign != null) {
                lbVariantsCountLabel.setText(String.valueOf(likelyBenign.size()));

                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                likelyBenign.stream().forEach(item -> {
                    if (item.getSnpInDel().getSequenceInfo() != null)
                        sequenceInfos.add(item.getSnpInDel().getSequenceInfo());
                });

                lbGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                likelyBenign.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        interpretations.add(item.getInterpretationEvidence());
                });
            }

            if(benign != null) {
                bVariantsCountLabel.setText(String.valueOf(benign.size()));

                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                benign.stream().forEach(item -> {
                    if (item.getSnpInDel().getSequenceInfo() != null)
                        sequenceInfos.add(item.getSnpInDel().getSequenceInfo());
                });

                bGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                benign.stream().forEach(item -> {
                    if (item.getInterpretationEvidence() != null)
                        interpretations.add(item.getInterpretationEvidence());
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //기본 초기화
        settingOverallQC(sample.getId());

    }

    public List<VariantAndInterpretationEvidence> returnVariant(List<VariantAndInterpretationEvidence> list, String pathogenicity) {
        return list.stream().filter(item -> ((pathogenicity.equalsIgnoreCase(item.getSnpInDel().getExpertPathogenicityLevel()) ||
                (StringUtils.isEmpty(item.getSnpInDel().getExpertPathogenicityLevel()) && item.getSnpInDel().getSwPathogenicityLevel().equalsIgnoreCase(pathogenicity)))))
                .collect(Collectors.toList());
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
