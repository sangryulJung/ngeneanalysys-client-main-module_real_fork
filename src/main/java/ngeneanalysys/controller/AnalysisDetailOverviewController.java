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
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.model.PertinentNegatives;
import ngeneanalysys.model.VariantInformation;
import ngeneanalysys.service.APIService;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;

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
    private TableView<VariantInformation> tierTable;

    @FXML
    private TableColumn<VariantInformation, Number> tierColumn;

    @FXML
    private TableColumn<VariantInformation, String> geneColumn;

    @FXML
    private TableColumn<VariantInformation, String> variantColumn;

    @FXML
    private TableColumn<VariantInformation, String> therapeuticColumn;

    @FXML
    private TableView<PertinentNegatives> pertinentNegativesTable;

    @FXML
    private TableColumn<PertinentNegatives, String> negativeGeneColumn;

    @FXML
    private TableColumn<PertinentNegatives, String> negativeVariantColumn;

    @FXML
    private TableColumn<PertinentNegatives, String> negativeVariantTypeColumn;

    @FXML
    private TableColumn<PertinentNegatives, String> negativeCauseColumn;

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

        //기본 초기화
        settingDetectedVariantsSummary();
        settingOverallQC();
        sampleTableViewAdd();

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

    public void sampleTableViewAdd() {

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
    }
}
