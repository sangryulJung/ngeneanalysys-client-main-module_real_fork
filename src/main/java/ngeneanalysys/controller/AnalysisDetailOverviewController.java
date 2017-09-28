package ngeneanalysys.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ngeneanalysys.code.enums.SequencerCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.model.*;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private TableView<AnalysisResultVariant> tierTable;

    @FXML
    private TableColumn<AnalysisResultVariant, String> tierColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> geneColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> variantColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> therapeuticColumn;

    @FXML
    private TableView<AnalysisResultVariant> pertinentNegativesTable;

    @FXML
    private TableColumn<AnalysisResultVariant, String> negativeGeneColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> negativeVariantColumn;

    @FXML
    private TableColumn<AnalysisResultVariant, String> negativeCauseColumn;

    @FXML
    private Label sampleQCResultLabel;

    @FXML
    private Label libraryQCResultLabel;

    @FXML
    private Label sequencingQCResultLabel;

    @FXML
    private Label analysisQCResultLabel;

    /** API 서버 통신 서비스 */
    private APIService apiService;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show..");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        Sample sample = (Sample) getParamMap().get("sample");

        //Tier Table Setting
        tierColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSwTier()));
        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getGene()));
        variantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantExpression().getNtChange()));
        therapeuticColumn.setCellValueFactory(cellData -> {
            Interpretation interpretation = cellData.getValue().getInterpretation();
            String text = "";

            if(!StringUtils.isEmpty(interpretation.getInterpretationEvidenceA()))
                text += interpretation.getInterpretationEvidenceA() + ", ";
            if(!StringUtils.isEmpty(interpretation.getInterpretationEvidenceB()))
                text += interpretation.getInterpretationEvidenceB() + ", ";
            if(!StringUtils.isEmpty(interpretation.getInterpretationEvidenceC()))
                text += interpretation.getInterpretationEvidenceC() + ", ";
            if(!StringUtils.isEmpty(interpretation.getInterpretationEvidenceD()))
                text += interpretation.getInterpretationEvidenceD() + ", ";

            if(!"".equals(text)) {
                text = text.substring(0, text.length() - 2);
            }

            return new SimpleStringProperty(text);
            });

        //Negative Table Setting
        negativeGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSequenceInfo().getGene()));
        negativeVariantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantExpression().getNtChange()));
        negativeCauseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInterpretation().getInterpretationNegativeTesult()));

        try {
            HttpClientResponse response = apiService.get("/analysisResults/"+ sample.getId()  + "/variants", null,
                    null, false);

            AnalysisResultVariantList analysisResultVariantList = response.getObjectBeforeConvertResponseToJSON(AnalysisResultVariantList.class);

            List<AnalysisResultVariant> list = analysisResultVariantList.getResult();

            //negative list만 가져옴
            List<AnalysisResultVariant> negativeList = list.stream().filter(item -> !StringUtils.isEmpty(item.getInterpretation().getInterpretationNegativeTesult())).collect(Collectors.toList());

            //그 이후 list에서 negative list를 제거
            //list.removeAll(negativeList);
            //list = list.stream().filter(item -> item.getInterpretation().getInterpretationNegativeTesult() == null).collect(Collectors.toList());

            Map<String, List<AnalysisResultVariant>> variantTierMap = list.stream().collect(Collectors.groupingBy(AnalysisResultVariant::getSwTier));

            List<AnalysisResultVariant> tierOne = variantTierMap.get("T1");


            if(tierOne != null) {
                tierTable.getItems().addAll(FXCollections.observableArrayList(tierOne));
                tierOneVariantsCountLabel.setText(String.valueOf(tierOne.size()));
                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                tierOne.stream().forEach(item -> {
                    if (item.getSequenceInfo() != null)
                        sequenceInfos.add(item.getSequenceInfo());
                });

                tierOneGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                tierOne.stream().forEach(item -> {
                    if (item.getInterpretation() != null)
                        interpretations.add(item.getInterpretation());
                });

                long count = interpretations.stream().filter(item -> (!StringUtils.isEmpty(item.getInterpretationEvidenceA()) ||
                        !StringUtils.isEmpty(item.getInterpretationEvidenceB()) ||
                        !StringUtils.isEmpty(item.getInterpretationEvidenceC()) ||
                        !StringUtils.isEmpty(item.getInterpretationEvidenceD()))).count();
                tierOneTherapeuticLabel.setText(String.valueOf(count));
            }

            List<AnalysisResultVariant> tierTwo = variantTierMap.get("T2");

            if(tierTwo != null) {
                tierTable.getItems().addAll(FXCollections.observableArrayList(tierTwo));
                tierTwoVariantsCountLabel.setText(String.valueOf(tierTwo.size()));

                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                tierTwo.stream().forEach(item -> {
                    if (item.getSequenceInfo() != null)
                        sequenceInfos.add(item.getSequenceInfo());
                });

                tierTwoGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                tierTwo.stream().forEach(item -> {
                    if (item.getInterpretation() != null)
                        interpretations.add(item.getInterpretation());
                });

                long count = interpretations.stream().filter(item -> (!StringUtils.isEmpty(item.getInterpretationEvidenceA()) ||
                        !StringUtils.isEmpty(item.getInterpretationEvidenceB()) ||
                        !StringUtils.isEmpty(item.getInterpretationEvidenceC()) ||
                        !StringUtils.isEmpty(item.getInterpretationEvidenceD()))).count();
                tierTwoTherapeuticLabel.setText(String.valueOf(count));
            }

            List<AnalysisResultVariant> tierThree  = variantTierMap.get("T3");

            if(tierThree != null) {
                tierThreeVariantsCountLabel.setText(String.valueOf(tierThree.size()));

                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                tierThree.stream().forEach(item -> {
                    if (item.getSequenceInfo() != null)
                        sequenceInfos.add(item.getSequenceInfo());
                });

                tierThreeGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                tierThree.stream().forEach(item -> {
                    if (item.getInterpretation() != null)
                        interpretations.add(item.getInterpretation());
                });

                long count = interpretations.stream().filter(item -> (!StringUtils.isEmpty(item.getInterpretationEvidenceA()) ||
                        !StringUtils.isEmpty(item.getInterpretationEvidenceB()) ||
                        !StringUtils.isEmpty(item.getInterpretationEvidenceC()) ||
                        !StringUtils.isEmpty(item.getInterpretationEvidenceD()))).count();
                tierThreeTherapeuticLabel.setText(String.valueOf(count));
            }

            List<AnalysisResultVariant> tierFour  = variantTierMap.get("T4");

            if(tierFour != null) {
                tierFourVariantsCountLabel.setText(String.valueOf(tierFour.size()));

                List<SequenceInfo> sequenceInfos = new ArrayList<>();
                tierFour.stream().forEach(item -> {
                    if (item.getSequenceInfo() != null)
                        sequenceInfos.add(item.getSequenceInfo());
                });

                tierFourGenesCountLabel.setText(sequenceInfos.stream().collect(Collectors.groupingBy(SequenceInfo::getGene)).size() + "");

                List<Interpretation> interpretations = new ArrayList<>();
                tierFour.stream().forEach(item -> {
                    if (item.getInterpretation() != null)
                        interpretations.add(item.getInterpretation());
                });

                long count = interpretations.stream().filter(item -> (!StringUtils.isEmpty(item.getInterpretationEvidenceA()) ||
                        !StringUtils.isEmpty(item.getInterpretationEvidenceB()) ||
                        !StringUtils.isEmpty(item.getInterpretationEvidenceC()) ||
                        !StringUtils.isEmpty(item.getInterpretationEvidenceD()))).count();
                tierFourTherapeuticLabel.setText(String.valueOf(count));
            }

            if(negativeList != null) {
                pertinentNegativesTable.setItems(FXCollections.observableArrayList(negativeList));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //기본 초기화
        //settingDetectedVariantsSummary();
        settingOverallQC();
        //sampleTableViewAdd();

    }

    public void settingDetectedVariantsSummary() {
        tierOneVariantsCountLabel.setText("2");
        tierOneGenesCountLabel.setText("4");
        tierOneTherapeuticLabel.setText("5");
        tierTwoVariantsCountLabel.setText("3");
        tierTwoGenesCountLabel.setText("1");
        tierTwoTherapeuticLabel.setText("2");
        tierThreeVariantsCountLabel.setText("1");
        tierThreeGenesCountLabel.setText("1");
        tierThreeTherapeuticLabel.setText("3");
        tierFourVariantsCountLabel.setText("3");
        tierFourGenesCountLabel.setText("3");
        tierFourTherapeuticLabel.setText("3");
    }

    public void settingOverallQC() {
        sampleQCResultLabel.setText("pass");
        libraryQCResultLabel.setText("fail");
        sequencingQCResultLabel.setText("pass");
        analysisQCResultLabel.setText("pass");

    }

    /*public void sampleTableViewAdd() {

        ObservableList<VariantInformation> variantInformation = FXCollections.observableArrayList(
                new VariantInformation(1, "IDH2", "c.516G>A (R172K)", "Enasidenib (IDHIFA)"),
                new VariantInformation(1, "NPM1", "c.863_864insTCTG (W288fs*12)", "Gemtuzumab ozogamicin"),
                new VariantInformation(2, "DNMT3A", "c.2645G>A (R882H)", ""),
                new VariantInformation(2, "SRSF2", "c.284C>T (P95L)", "")
        );

        tierColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTier()));
        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGene()));
        variantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariant()));
        therapeuticColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTherapeutic()));

        tierTable.setItems(variantInformation);


        ObservableList<PertinentNegatives> pertinentNegatives = FXCollections.observableArrayList(
                new PertinentNegatives("FLT-ITD", "c.1803_1804insGATTTCAGAGAATATGAATATGATCTC (p.K602delinsDFREYEYDLK)", "Ins", ""),
                new PertinentNegatives("c-KIT", "c.2447A>T (D816V)", "SNV", ""));

        negativeGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGene()));
        negativeVariantColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariant()));
        negativeVariantTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariantType()));
        negativeCauseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCause()));

        pertinentNegativesTable.setItems(pertinentNegatives);
    }*/
}
