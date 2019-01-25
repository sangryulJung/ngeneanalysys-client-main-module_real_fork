package ngeneanalysys.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedVariantAndInterpretationEvidence;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-08-25
 */
public class AnalysisDetailTSTRNAOverviewController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private GridPane dataQCResultGridPane;

    @FXML
    private Label tierOneVariantsCountLabel;

    @FXML
    private Label tierOneGenesCountLabel;

    @FXML
    private Label tierTwoVariantsCountLabel;

    @FXML
    private Label tierTwoGenesCountLabel;

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
    private TableColumn<VariantAndInterpretationEvidence, String> alleleFrequencyColumn;

    /** API 서버 통신 서비스 */
    private APIService apiService;

    @SuppressWarnings("unchecked")
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
        alleleFrequencyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getReadInfo().getAlleleFraction()
                .toString() + "(" + cellData.getValue().getSnpInDel().getReadInfo().getAltReadNum() + "/" + cellData.getValue().getSnpInDel().getReadInfo().getReadDepth() + ")"));
        positionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSnpInDel().getGenomicCoordinate().getStartPosition()));
        transcriptColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getTranscriptAccession()));
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
                            Collectors.groupingBy(SnpInDelEvidence::getEvidenceLevel));

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

    private void createEvidenceLabel(List<SnpInDelEvidence> interpretation, HBox hBox, String evidenceLevel) {
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
            return allTierList.stream().filter(item -> (tier.equalsIgnoreCase(item.getSnpInDel().getExpertTier()) ||
                    (StringUtils.isEmpty(item.getSnpInDel().getExpertTier()) && tier.equalsIgnoreCase(item.getSnpInDel().getSwTier()))))
                    .collect(Collectors.toList());
        }

        return null;
    }

    void setDisplayItem() {
        SampleView sample = (SampleView) getParamMap().get("sampleView");

        //기본 초기화
        Platform.runLater(() -> settingOverallQC(sample.getId()));

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

    }

    private String returnQCTitle(String value) {
        if(value.equals("Median_BinCount_CNV_Targets")) {
            return "Median BinCount";
        } else if(value.equals("PCT_ExonBases_100X")) {
            return "PCT ExonBases";
        } else if(value.equals("Q30_score_read1")) {
            return "Q30+ Read2";
        } else if(value.equals("Q30_score_read2")) {
            return "Q30+ Read1";
        } else if(value.equals("Median_CV_Coverage_1000x")) {
            return "Median CV Coverage";
        }

        return value.replaceAll("_", " ");
    }

    private void addQCGrid(SampleQC sampleQC, int col) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        dataQCResultGridPane.getColumnConstraints().add(columnConstraints);
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setStyle(hBox.getStyle() + "-fx-background-color : #8f9fb9;");
        hBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hBox.setAlignment(Pos.CENTER);
        String title = returnQCTitle(sampleQC.getQcType());
        Label titleLabel = new Label(title);
        titleLabel.setStyle(titleLabel.getStyle() + "-fx-text-fill : #FFF;");
        Label descriptionLabel = new Label();

        descriptionLabel.getStyleClass().add("help_tooltip_white");
        descriptionLabel.setStyle(descriptionLabel.getStyle() + "-fx-cursor : hand;");
        String value = sampleQC.getQcDescription() + " " + sampleQC.getQcThreshold() + System.lineSeparator()
                + "Value : " + sampleQC.getQcValue().stripTrailingZeros().toPlainString() + sampleQC.getQcUnit();
        descriptionLabel.setOnMouseClicked(ev ->
                PopOverUtil.openQCPopOver(descriptionLabel, value));

        hBox.getChildren().addAll(titleLabel, descriptionLabel);

        dataQCResultGridPane.add(hBox, col, 0);

        Label qcResultLabel = new Label(sampleQC.getQcResult().toUpperCase());
        //qcResultLabel.setTooltip(new Tooltip(sampleQC.getQcValue() + sampleQC.getQcUnit()));

        dataQCResultGridPane.add(qcResultLabel, col, 1);
        GridPane.setValignment(qcResultLabel, VPos.CENTER);
        GridPane.setHalignment(qcResultLabel, HPos.CENTER);

    }

    @SuppressWarnings("unchecked")
    private void settingOverallQC(int sampleId) {
        if(dataQCResultGridPane.getChildren() != null && !dataQCResultGridPane.getChildren().isEmpty()) {
            dataQCResultGridPane.getChildren().removeAll(dataQCResultGridPane.getChildren());
            dataQCResultGridPane.getColumnConstraints().removeAll(dataQCResultGridPane.getColumnConstraints());
        }
        List<SampleQC> qcList = null;

        try {
            HttpClientResponse response = apiService.get("/analysisResults/sampleQCs/" + sampleId, null,
                    null, false);

            qcList = (List<SampleQC>) response.getMultiObjectBeforeConvertResponseToJSON(SampleQC.class, false);

            qcList.sort(Comparator.comparing(SampleQC::getQcType));
            int i = 0;
            for(SampleQC sampleQC : qcList) {
                if(!sampleQC.getQcThreshold().equals("N/A")) {
                    addQCGrid(sampleQC, i++);
                }
            }

        } catch(WebAPIException e) {
            DialogUtil.alert("QC Metrics data can not be loaded.", e.getMessage(), this.getMainApp().getPrimaryStage(), true);
        }
    }
}
