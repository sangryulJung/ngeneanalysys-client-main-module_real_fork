package ngeneanalysys.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.controller.fragment.AnalysisDetailOverviewBrcaCnvController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.model.paged.PagedVariantAndInterpretationEvidence;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jang
 * @since 2017-08-25
 */
public class AnalysisDetailOverviewGermlineController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private GridPane dataQCResultGridPane;

    @FXML
    private GridPane overviewMainGridPane;

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

    /** API 서버 통신 서비스 */
    private APIService apiService;

    private AnalysisDetailOverviewBrcaCnvController analysisDetailOverviewBrcaCnvController;

    private AnalysisDetailOverviewHeredAmcController analysisDetailOverviewHeredAmcController;

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
        transcriptColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getTranscriptAccession()));
        ntChangeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getNtChange()));
        aaChangeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnpInDel().getSnpInDelExpression().getAaChange()));

        setBrcaCnvOverview();

        //Platform.runLater(this::setDisplayItem);
    }

    private void setBrcaCnvOverview() {
        Panel panel = (Panel)paramMap.get("panel");
        if(PipelineCode.BRCA_ACCUTEST_PLUS_CMC_DNA.getCode().equals(panel.getCode()) ||
                PipelineCode.BRCA_ACCUTEST_PLUS_MLPA_DNA.getCode().equals(panel.getCode())) {
            overviewMainGridPane.setPrefHeight(overviewMainGridPane.getPrefHeight() + 265);
            overviewMainGridPane.getRowConstraints().get(4).setPrefHeight(265);
            overviewMainGridPane.getRowConstraints().get(4).setMaxHeight(265);

            try {
                FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_BRCA_CNV_OVERVIEW);
                Node node = loader.load();
                AnalysisDetailOverviewBrcaCnvController controller = loader.getController();
                analysisDetailOverviewBrcaCnvController = controller;
                controller.setMainController(this.getMainController());
                controller.setParamMap(paramMap);
                controller.show((Parent) node);
                overviewMainGridPane.add(node, 0, 4);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(PipelineCode.HERED_ACCUTEST_AMC_CNV_DNA.getCode().equals(panel.getCode())) {
            overviewMainGridPane.setPrefHeight(overviewMainGridPane.getPrefHeight() + 110);
            overviewMainGridPane.getRowConstraints().get(4).setPrefHeight(110);
            overviewMainGridPane.getRowConstraints().get(4).setMaxHeight(110);

            try {
                FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_HERED_AMC_OVERVIEW);
                Node node = loader.load();
                AnalysisDetailOverviewHeredAmcController controller = loader.getController();
                analysisDetailOverviewHeredAmcController = controller;
                controller.setMainController(this.getMainController());
                controller.setParamMap(paramMap);
                controller.show((Parent) node);
                overviewMainGridPane.add(node, 0, 4);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setDisplayItem() {
        if(analysisDetailOverviewBrcaCnvController != null) {
            analysisDetailOverviewBrcaCnvController.getBrcaCnvList();
        } else if(analysisDetailOverviewHeredAmcController != null) {
            analysisDetailOverviewHeredAmcController.setContents();
        }

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
            }

            if(likelyBenign != null) {
                lbVariantsCountLabel.setText(String.valueOf(likelyBenign.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                likelyBenign.forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                lbGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");
            }

            if(benign != null) {
                bVariantsCountLabel.setText(String.valueOf(benign.size()));

                List<GenomicCoordinate> genomicCoordinates = new ArrayList<>();
                benign.forEach(item -> {
                    if (item.getSnpInDel().getGenomicCoordinate() != null)
                        genomicCoordinates.add(item.getSnpInDel().getGenomicCoordinate());
                });

                bGenesCountLabel.setText(genomicCoordinates.stream().collect(Collectors.groupingBy(GenomicCoordinate::getGene)).size() + "");
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

    private void addQCGrid(SampleQC sampleQC, int col) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        dataQCResultGridPane.getColumnConstraints().add(columnConstraints);
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setStyle(hBox.getStyle() + "-fx-background-color : #8f9fb9;");
        hBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hBox.setAlignment(Pos.CENTER);
        String title = ConvertUtil.returnQCTitle(sampleQC.getQcType());
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

        SampleView sample = (SampleView) getParamMap().get("sampleView");
        if(sample.getPanel().getCode().equals(PipelineCode.BRCA_ACCUTEST_DNA.getCode()) ||
                sample.getPanel().getCode().equals(PipelineCode.BRCA_ACCUTEST_PLUS_DNA.getCode())) {
            dataQCResultGridPane.setPrefWidth(570);
            dataQCResultGridPane.setMaxWidth(570);
        }

        try {
            HttpClientResponse response = apiService.get("/analysisResults/sampleQCs/" + sampleId, null,
                    null, false);

            List<SampleQC> qcList = (List<SampleQC>) response.getMultiObjectBeforeConvertResponseToJSON(SampleQC.class, false);

            qcList.sort(Comparator.comparing(SampleQC::getQcType));
            int i = 0;
            for(SampleQC sampleQC : qcList) {
                if(!sampleQC.getQcThreshold().equals("N/A")) {
                    addQCGrid(sampleQC, i++);
                }
            }

        } catch(WebAPIException e) {
            DialogUtil.alert("QC ERROR", e.getMessage(), this.getMainApp().getPrimaryStage(), true);
        }
    }
}
