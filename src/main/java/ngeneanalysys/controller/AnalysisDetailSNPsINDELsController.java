package ngeneanalysys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.*;
import ngeneanalysys.controller.extend.AnalysisDetailCommonController;
import ngeneanalysys.model.AnalysisResultSummary;
import ngeneanalysys.model.AnalysisResultVariant;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.render.SNPsINDELsList;
import ngeneanalysys.service.ALAMUTService;
import ngeneanalysys.service.APIService;
import ngeneanalysys.service.IGVService;
import ngeneanalysys.util.*;
import ngeneanalysys.util.httpclient.HttpClientResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jang
 * @since 2017-08-28
 */
public class AnalysisDetailSNPsINDELsController extends AnalysisDetailCommonController {
    private static Logger logger = LoggerUtil.getLogger();

    /** API 서버 통신 서비스 */
    private APIService apiService;

    /** IGV 연동 서비스 */
    private IGVService igvService;

    /** Alamut 연동 서비스 */
    private ALAMUTService alamutService;

    /** 현재 선택된 변이 정보 객체 */
    private AnalysisResultVariant selectedAnalysisResultVariant;

    /** 현재 선택된 Prediction Filter */
    private ACMGFilterCode selectedPredictionCodeFilter;

    /** ACMG Filter Box Area */
    @FXML
    private HBox filterList;

    /** 우측 상단 아이콘 출력 박스 */
    @FXML
    private HBox iconAreaHBox;

    /** 목록 정렬 컬럼 정보 출력 박스 */
    @FXML
    private HBox sortListBox;
    @FXML
    private GridPane mainGridPane;

    @FXML
    private TableView<AnalysisResultVariant> variantListTableView;
    /** Variant List Column > prediction */
    @FXML
    private TableColumn<AnalysisResultVariant,String> predictionColumn;
    /** Variant List Column > pathogenic */
    @FXML
    private TableColumn<AnalysisResultVariant,String> pathgenicColumn;
    /** Variant List Column > add to report flag */
    @FXML
    private TableColumn<AnalysisResultVariant,String> reportColumn;
    /** Variant List Column > set to false flag */
    @FXML
    private TableColumn<AnalysisResultVariant,String> falseColumn;
    /** Variant List Column > pre filtered */
    @FXML
    private TableColumn<AnalysisResultVariant,String> filteredColumn;
    /** Variant List Column > variant id */
    @FXML
    private TableColumn<AnalysisResultVariant,Number> variantIdColumn;
    /** Variant List Column > type */
    @FXML
    private TableColumn<AnalysisResultVariant,String> typeColumn;
    /** Variant List Column > coding consequence */
    @FXML
    private TableColumn<AnalysisResultVariant,String> codingConsequenceColumn;
    /** Variant List Column > gene */
    @FXML
    private TableColumn<AnalysisResultVariant,String> geneColumn;
    /** Variant List Column > refSeqId */
    @FXML
    private TableColumn<AnalysisResultVariant,String> refSeqIdColumn;
    /** Variant List Column > c.DNA */
    @FXML
    private TableColumn<AnalysisResultVariant,String> cDNAColumn;
    /** Variant List Column > c.DNA(BIC) */
    @FXML
    private TableColumn<AnalysisResultVariant,String> cDNAbicColumn;
    /** Variant List Column > protein */
    @FXML
    private TableColumn<AnalysisResultVariant,String> proteinColumn;
    /** Variant List Column > zygosity */
    @FXML
    private TableColumn<AnalysisResultVariant,String> zygosityColumn;
    /** Variant List Column > ref */
    @FXML
    private TableColumn<AnalysisResultVariant,String> refColumn;
    /** Variant List Column > alt */
    @FXML
    private TableColumn<AnalysisResultVariant,String> altColumn;
    /** Variant List Column > left seq */
    @FXML
    private TableColumn<AnalysisResultVariant,String> leftSeqColumn;
    /** Variant List Column > right seq */
    @FXML
    private TableColumn<AnalysisResultVariant,String> rightSeqColumn;
    /** Variant List Column > chromosome */
    @FXML
    private TableColumn<AnalysisResultVariant,String> chromosomeColumn;
    /** Variant List Column > snp */
    @FXML
    private TableColumn<AnalysisResultVariant,String> snpColumn;
    /** Variant List Column > exon id */
    @FXML
    private TableColumn<AnalysisResultVariant,String> exonIdColumn;
    /** Variant List Column > exon id */
    @FXML
    private TableColumn<AnalysisResultVariant,String> exonIdBICColumn;
    /** Variant List Column > variant depth */
    @FXML
    private TableColumn<AnalysisResultVariant,Number> variantDepthColumn;
    /** Variant List Column > allele fraction */
    @FXML
    private TableColumn<AnalysisResultVariant,Number> alleleFractionColumn;
    /** Variant List Column > reference number */
    @FXML
    private TableColumn<AnalysisResultVariant,Number> referenceNumberColumn;
    /** Variant List Column > alternate number */
    @FXML
    private TableColumn<AnalysisResultVariant,Number> alternateNumberColumn;
    /** Variant List Column > genomic position */
    @FXML
    private TableColumn<AnalysisResultVariant,Number> genomicPositionColumn;
    /** Variant List Column > genomic end position */
    @FXML
    private TableColumn<AnalysisResultVariant,Number> genomicEndPositionColumn;
    /** Variant List Column > reference genome */
    @FXML
    private TableColumn<AnalysisResultVariant,String> referenceGenomeColumn;
    /** Variant List Column > gene strand */
    @FXML
    private TableColumn<AnalysisResultVariant,String> geneStrandColumn;
    /** Variant List Column > warning */
    @FXML
    private TableColumn<AnalysisResultVariant,String> warningColumn;
    /** Variant List Column > sift */
    @FXML
    private TableColumn<AnalysisResultVariant,String> siftColumn;
    /** Variant List Column > polyphen2 */
    @FXML
    private TableColumn<AnalysisResultVariant,String> polyphen2Column;
    /** Variant List Column > mutationtaster */
    @FXML
    private TableColumn<AnalysisResultVariant,String> mutationtasterColumn;
    /** Variant List Column > esp5400 */
    @FXML
    private TableColumn<AnalysisResultVariant,String> esp6500Column;
    /** Variant List Column > exac */
    @FXML
    private TableColumn<AnalysisResultVariant,String> exacColumn;
    /** Variant List Column > 1000 genomes  */
    @FXML
    private TableColumn<AnalysisResultVariant,String> thousandGenomicsColumn;
    /** Variant List Column > Korean Exome */
    @FXML
    private TableColumn<AnalysisResultVariant,String> koreaExomeColumn;
    /** Variant List Column > clinvar accession */
    @FXML
    private TableColumn<AnalysisResultVariant,String> clinvarAccessionColumn;
    /** Variant List Column > clinvar disease */
    @FXML
    private TableColumn<AnalysisResultVariant,String> clinvarDiseaseColumn;
    /** Variant List Column > clinvar class */
    @FXML
    private TableColumn<AnalysisResultVariant,String> clinvarClassColumn;
    /** Variant List Column > bic category */
    @FXML
    private TableColumn<AnalysisResultVariant,String> bicCategoryColumn;
    /** Variant List Column > bic importance */
    @FXML
    private TableColumn<AnalysisResultVariant,String> bicImportanceColumn;
    /** Variant List Column > bic classification */
    @FXML
    private TableColumn<AnalysisResultVariant,String> bicClassificationColumn;
    /** Variant List Column > bic designation */
    @FXML
    private TableColumn<AnalysisResultVariant,String> bicDesignationColumn;
    /** Variant List Column > bic NT */
    @FXML
    private TableColumn<AnalysisResultVariant,String> bicNTColumn;
    /** Variant List Column > kohbra patient */
    @FXML
    private TableColumn<AnalysisResultVariant,String> kohbraPatientColumn;
    /** Variant List Column > kohbra frequency */
    @FXML
    private TableColumn<AnalysisResultVariant,String> kohbraFrequencyColumn;
    /** Variant List Column > Experiment Type */
    @FXML
    private TableColumn<AnalysisResultVariant,String> experimentTypeColumn;
    /** Variant List Column > enigma */
    @FXML
    private TableColumn<AnalysisResultVariant,String> enigmaColumn;
    /** Variant List Column > be.clin.update */
    @FXML
    private TableColumn<AnalysisResultVariant, String> beClinUpdateColumn;
    /** Variant List Column > be.clin.origin */
    @FXML
    private TableColumn<AnalysisResultVariant, String> beClinOriginColumn;
    /** Variant List Column > be.clin.meth */
    @FXML
    private TableColumn<AnalysisResultVariant, String> beClinMethColumn;
    /** Variant List Column > be.bic.cate */
    @FXML
    private TableColumn<AnalysisResultVariant, String> beBicCateColumn;
    /** Variant List Column > be.bic.eth */
    @FXML
    private TableColumn<AnalysisResultVariant, String> beBicEthColumn;
    /** Variant List Column > be.bic.nat */
    @FXML
    private TableColumn<AnalysisResultVariant, String> beBicNatColumn;
    /** Variant List Column > be.ref */
    @FXML
    private TableColumn<AnalysisResultVariant, String> beRefColumn;
    /** Variant List Column > be.nuc */
    @FXML
    private TableColumn<AnalysisResultVariant, String> beNucColumn;
    /** Variant List Column > be.gene */
    @FXML
    private TableColumn<AnalysisResultVariant, String> beGeneColumn;
    /** Variant List Column > be.eni_cond */
    @FXML
    private TableColumn<AnalysisResultVariant, String> beEniCondColumn;
    /** Variant List Column > be.eni.update */
    @FXML
    private TableColumn<AnalysisResultVariant, String> beEniUpdateColumn;
    /** Variant List Column > be.eni.comm */
    @FXML
    private TableColumn<AnalysisResultVariant, String> beEniCommColumn;
    /** Variant List Column > be.path.clin */
    @FXML
    private TableColumn<AnalysisResultVariant, String> bePathClinColumn;
    /** Variant List Column > be.path.eni */
    @FXML
    private TableColumn<AnalysisResultVariant, String> bePathEniColumn;
    /** Variant List Column > be.path.bic */
    @FXML
    private TableColumn<AnalysisResultVariant, String> bePathBicColumn;

    @FXML
    private Label filterTitle;

    /** Pathogenic Review > Pathogenic 박스 */
    @FXML
    public HBox pathogenicArea;

    @FXML
    private Button pathogenicNone;

    /** 작업 Dialog Window Stage Object */
    private Stage currentStage;

    /** 선택 변이 상세 정보 탭 영역 */
    @FXML
    private TabPane tabArea;

    /** Overview Tab */
    public Tab subTabOverview;
    /** Comments Tab */
    public Tab subTabMemo;
    /** Warnings Tab */
    public Tab subTabLowConfidence;

    /** 현재 선택된 변이 리스트 객체의 index */
    private int selectedVariantIndex;
    @FXML
    private VBox linkArea;								/** clinical > Link 박스 영역 */
    @FXML
    private Button overviewFoldButton;
    private AnalysisDetailSNPsINDELsOverviewController overviewController;
    private final double overviewFoldedHeight = 32;
    private final double overviewExpandedHeight = 304;

    Sample sample = null;

    AnalysisResultSummary analysisResultSummary = null;

    @Override
    public void show(Parent root) throws IOException {
        logger.info("show SNPs-INDELs");
        apiService = APIService.getInstance();
        apiService.setStage(getMainController().getPrimaryStage());

        sample = (Sample)paramMap.get("sample");
        dummyDataCreated();

        // igv service init
        igvService = IGVService.getInstance();
        igvService.setMainController(getMainController());

        // alamut service init
        alamutService = ALAMUTService.getInstance();
        alamutService.setMainController(getMainController());

        // 목록 클릭 시 변이 상세정보 출력 이벤트 바인딩
        variantListTableView.setRowFactory(tv -> {
            TableRow<AnalysisResultVariant> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 1 && (!row.isEmpty())) {
                     showVariantDetail(variantListTableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });

        // 선택된 목록에서 엔터키 입력시 변이 상세정보 출력 이벤트 바인딩
        variantListTableView.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                showVariantDetail(variantListTableView.getSelectionModel().getSelectedItem());
            }
        });

        // 본 샘플의 FASTQC 결과가 "pass"가 아닌 경우 아이콘 출력함.
        String fastQC = "PASS";//sample.getQc();
        fastQC = "PASS";/*(StringUtils.isEmpty(fastQC) && sample.getAnalysisResultSummary() != null) ? sample.getAnalysisResultSummary().getQualityControl() : fastQC;*/
        fastQC = (!StringUtils.isEmpty(fastQC)) ? fastQC.toUpperCase() : "NONE";
        if(StringUtils.isEmpty(fastQC) || !"PASS".endsWith(fastQC.toUpperCase())) {
            ImageView imgView = new ImageView(resourceUtil.getImage("/layout/images/icon_warn_big.png"));
            iconAreaHBox.getChildren().add(imgView);
            iconAreaHBox.setMargin(imgView, new Insets(0, 5, 0, 0));
        }

        ImageView ruoImgView = new ImageView(resourceUtil.getImage("/layout/images/icon_ruo.png"));
        ruoImgView.setFitWidth(100);
        ruoImgView.setFitHeight(50);
        iconAreaHBox.getChildren().add(ruoImgView);

        // 변이 목록 TableView Column Value Setting...
        predictionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrediction()));
        predictionColumn.setCellFactory(param -> {
                TableCell<AnalysisResultVariant,String> cell = new TableCell<AnalysisResultVariant, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        Label label = null;
                        if(item != null) {
                            //PredictionTypeCode code = PredictionTypeCode.getByCode(item);
                            String alias = PredictionTypeCode.getAliasFromCode(item);
                            if(alias != null && !"NONE".equals(alias)) {
                                label = new Label(alias);
                                label.getStyleClass().add("prediction_" + item);
                            }
                        }
                        setGraphic(label);
                    }
                };
                return cell;
        });
        pathgenicColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPathogenic()));
        pathgenicColumn.setCellFactory(param -> {
                TableCell<AnalysisResultVariant,String> cell = new TableCell<AnalysisResultVariant, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        Label label = null;
                        if(item != null && !"NONE".equals(item) && !"-1".equals(item)) {
                            String alias = PathogenicTypeCode.getAliasFromCode(item);
                            label = new Label(alias);
                            label.getStyleClass().add("pathogenic_" + item);
                        }
                        setGraphic(label);
                    }
                };
                return cell;
        });
        reportColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPathogenicReportYn()));
        reportColumn.setCellFactory(param -> {
                TableCell<AnalysisResultVariant,String> cell = new TableCell<AnalysisResultVariant, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        Label label = null;
                        if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
                            label = new Label("R");
                            label.getStyleClass().add("prediction_E");
                        }
                        setGraphic(label);
                    }
                };
                return cell;
        });
        falseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPathogenicFalseYn()));
        falseColumn.setCellFactory(param -> {
                TableCell<AnalysisResultVariant,String> cell = new TableCell<AnalysisResultVariant, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        Label label = null;
                        if(!StringUtils.isEmpty(item) && "Y".equals(item)) {
                            label = new Label("F");
                            label.getStyleClass().addAll("bullet_red", "brd_radius_2");
                        }
                        setGraphic(label);
                    }
                };
                return cell;
        });
        warningColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getWarningReasonIfWarningIsYes()));
        warningColumn.setCellFactory(param -> {
                TableCell<AnalysisResultVariant,String> cell = new TableCell<AnalysisResultVariant, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        setGraphic((!StringUtils.isEmpty(item)) ? SNPsINDELsList.getWarningReasonPopOver(item) : null);
                    }
                };
                return cell;
        });
        variantIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Integer.parseInt(cellData.getValue().getVariantId())));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        codingConsequenceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodingConsequence()));
        geneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGene()));
        refSeqIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRefSeqId()));
        cDNAColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getcDNA()));
        cDNAbicColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getcDNAbic()));
        proteinColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProtein()));
        zygosityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getZygosity()));
        refColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRef()));
        altColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlt()));
        chromosomeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChromosome()));
        snpColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSnp()));
        exonIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExonId()));
        exonIdBICColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExonIdBIC()));
        variantDepthColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Integer.parseInt(cellData.getValue().getVariantDepth())));
        alleleFractionColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f", cellData.getValue().getAlleleFraction(), "0"))));
        referenceNumberColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Integer.parseInt(cellData.getValue().getReferenceNumber())));
        alternateNumberColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Integer.parseInt(cellData.getValue().getAlternateNumber())));
        genomicPositionColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Integer.parseInt(cellData.getValue().getGenomicPosition())));
        genomicEndPositionColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Integer.parseInt(cellData.getValue().getGenomicEndPosition())));
        referenceGenomeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReferenceGenome()));
        geneStrandColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGeneStrand()));
        siftColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSift()));
        polyphen2Column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPolyphen2()));
        mutationtasterColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMutationtaster()));
        esp6500Column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEsp6500() == null || cellData.getValue().getEsp6500().isEmpty() ? "" : String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f", cellData.getValue().getEsp6500(), "")))));
        exacColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExac() == null || cellData.getValue().getExac().isEmpty() ? "" : String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f", cellData.getValue().getExac(), "")))));
        thousandGenomicsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getThousandGenomics() == null || cellData.getValue().getThousandGenomics().isEmpty() ? "" : String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f", cellData.getValue().getThousandGenomics(), "")))));
        koreaExomeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKoreanExome() == null || cellData.getValue().getKoreanExome().isEmpty() ? "" : String.valueOf(Double.parseDouble(ConvertUtil.convertFormatNumber("%.2f", cellData.getValue().getKoreanExome(), "")))));
        clinvarAccessionColumn.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().getVariantClinical() != null) ? cellData.getValue().getVariantClinical().getClinvarAccession() : null));
        clinvarDiseaseColumn.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().getVariantClinical() != null) ? cellData.getValue().getVariantClinical().getVariantDiseaseDbName() : null));
        clinvarClassColumn.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().getVariantClinical() != null) ? cellData.getValue().getVariantClinical().getClassification() : null));
        bicCategoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBicCategory()));
        bicImportanceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBicImportance()));
        bicClassificationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBicClassification()));
        bicDesignationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBicDesignation()));
        bicNTColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBicNT()));
        kohbraPatientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKohbraPatient()));
        kohbraFrequencyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKohbraFrequency()));
        experimentTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExperimentType()));
        leftSeqColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLeftSeq()));
        rightSeqColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRightSeq()));
        enigmaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnigma()));
        beClinUpdateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBeClinUpdate()));
        beClinOriginColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBeClinOrigin()));
        beClinMethColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBeClinMeth()));
        beBicCateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBeBicCate()));
        beBicEthColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBeBicEth()));
        beBicNatColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBeBicNat()));
        beRefColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBeRef()));
        beNucColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBeNuc()));
        beGeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBeGene()));
        beEniCondColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBeEniCond()));
        beEniUpdateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBeEniUpdate()));
        beEniCommColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBeEniComm()));
        bePathClinColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBePathClin()));
        bePathEniColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBePathEni()));
        bePathBicColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBePathBic()));

        // 목록 정렬 설정 트래킹
        variantListTableView.getSortOrder().addListener(new ListChangeListener<TableColumn<AnalysisResultVariant,?>>() {
            @Override
            public void onChanged(Change<? extends TableColumn<AnalysisResultVariant,?>> change) {
                while (change.next()) {
                    // 출력 박스 초기화
                    sortListBox.getChildren().removeAll(sortListBox.getChildren());

                    int size = variantListTableView.getSortOrder().size();
                    if(size > 0 ) {
                        int idx = 0;
                        for (TableColumn<AnalysisResultVariant, ?> column : variantListTableView.getSortOrder()) {
                            if(idx > 0) {
                                ImageView image = new ImageView(resourceUtil.getImage("/layout/images/icon-arrow_margin.png"));
                                sortListBox.getChildren().add(image);
                                sortListBox.setMargin(image, new Insets(0, 1, 0, 2));
                            }
                            sortListBox.getChildren().add(new Label(column.getText()));
                            idx++;
                        }
                    }
                }
            }
        });

        overviewFoldButton.setOnMouseClicked(event -> {
                if (overviewFoldButton.getStyleClass().get(0) == null){
                    return;
                } else if(overviewFoldButton.getStyleClass().get(0).equals("btn_fold")){
                    foldOverview();
                } else {
                    expandOverview();
                }
        });


        currentStage = new Stage();
        currentStage.setResizable(false);
        currentStage.initStyle(StageStyle.DECORATED);
        currentStage.initModality(Modality.APPLICATION_MODAL);
        currentStage.setTitle(CommonConstants.SYSTEM_NAME + " > New Analysis Request");
        // OS가 Window인 경우 아이콘 출력.
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            currentStage.getIcons().add(resourceUtil.getImage(CommonConstants.SYSTEM_FAVICON_PATH));
        }
        currentStage.initOwner(getMainApp().getPrimaryStage());

        //탭생성
        tabArea.getTabs().removeAll(tabArea.getTabs());
        subTabOverview = new Tab(SNPsINDELsTabMenuCode.OVERVIEW.getMenuName());
        tabArea.getTabs().add(subTabOverview);
        subTabMemo = new Tab(SNPsINDELsTabMenuCode.MEMO.getMenuName());
        tabArea.getTabs().add(subTabMemo);
        subTabLowConfidence = new Tab(SNPsINDELsTabMenuCode.LOWCONFIDENCE.getMenuName());
        tabArea.getTabs().add(subTabLowConfidence);

        //필터 박스 출력
        if("GERMLINE".equals(sample.getAnalysisType())) {
            setFilterBox();
        } else {
            filterTitle.setText("Tier Filter");
            setTierFilterBox();
        }


        showVariantList(null, 0);

        // Scene Init
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.showAndWait();
    }

    public void setTierFilterBox() {
        VBox totalVariantBox = getFilterBox(ACMGFilterCode.TOTAL_VARIANT, 1);
        filterList.getChildren().add(totalVariantBox);
        filterList.setMargin(totalVariantBox, new Insets(0, 0, 0, 5));

        // prediction A
        VBox predictionABox = getFilterBox(ACMGFilterCode.TIER_ONE, 2);
        filterList.getChildren().add(predictionABox);
        filterList.setMargin(predictionABox, new Insets(0, 0, 0, 5));

        // prediction B
        VBox predictionBBox = getFilterBox(ACMGFilterCode.TIER_TWO, 3);
        filterList.getChildren().add(predictionBBox);
        filterList.setMargin(predictionBBox, new Insets(0, 0, 0, 5));

        // prediction C
        VBox predictionCBox = getFilterBox(ACMGFilterCode.TIER_THREE, 4);
        filterList.getChildren().add(predictionCBox);
        filterList.setMargin(predictionCBox, new Insets(0, 0, 0, 5));

        // prediction D
        VBox predictionDBox = getFilterBox(ACMGFilterCode.TIER_FOUR, 5);
        filterList.getChildren().add(predictionDBox);
        filterList.setMargin(predictionDBox, new Insets(0, 0, 0, 5));

    }

    /**
     * 필터 박스 설정
     */
    @SuppressWarnings("static-access")
    public void setFilterBox() {
        AnalysisResultSummary summary = analysisResultSummary;

        // total variant count
        VBox totalVariantBox = getFilterBox(ACMGFilterCode.TOTAL_VARIANT, summary.getAllVariantCount());
        filterList.getChildren().add(totalVariantBox);
        filterList.setMargin(totalVariantBox, new Insets(0, 0, 0, 5));

        // prediction A
        VBox predictionABox = getFilterBox(ACMGFilterCode.PREDICTION_A, summary.getLevel1VariantCount());
        filterList.getChildren().add(predictionABox);
        filterList.setMargin(predictionABox, new Insets(0, 0, 0, 5));

        // prediction B
        VBox predictionBBox = getFilterBox(ACMGFilterCode.PREDICTION_B, summary.getLevel2VariantCount());
        filterList.getChildren().add(predictionBBox);
        filterList.setMargin(predictionBBox, new Insets(0, 0, 0, 5));

        // prediction C
        VBox predictionCBox = getFilterBox(ACMGFilterCode.PREDICTION_C, summary.getLevel3VariantCount());
        filterList.getChildren().add(predictionCBox);
        filterList.setMargin(predictionCBox, new Insets(0, 0, 0, 5));

        // prediction D
        VBox predictionDBox = getFilterBox(ACMGFilterCode.PREDICTION_D, summary.getLevel4VariantCount());
        filterList.getChildren().add(predictionDBox);
        filterList.setMargin(predictionDBox, new Insets(0, 0, 0, 5));

        // prediction E
        VBox predictionEBox = getFilterBox(ACMGFilterCode.PREDICTION_E, summary.getLevel5VariantCount());
        filterList.getChildren().add(predictionEBox);
        filterList.setMargin(predictionEBox, new Insets(0, 0, 0, 5));

        // Low Confidence
        int lowConfidenceCount = summary.getWarningVariantCount();
        VBox lowConfidenceBox = getFilterBox(ACMGFilterCode.LOW_CONFIDENCE, lowConfidenceCount);
        filterList.getChildren().add(lowConfidenceBox);
        filterList.setMargin(lowConfidenceBox, new Insets(0, 0, 0, 5));
    }

    /**
     * 필터 박스 반환
     * @param acmgFilterCode
     * @param count
     * @return
     */
    public VBox getFilterBox(ACMGFilterCode acmgFilterCode, int count) {
        VBox box = new VBox();
        box.setId(acmgFilterCode.name());
        box.getStyleClass().add(acmgFilterCode.name());

        Label levelLabel = new Label(acmgFilterCode.getDetail());
        levelLabel.getStyleClass().add("level_" + acmgFilterCode.name());
        levelLabel.setTooltip(new Tooltip(acmgFilterCode.getDetail()));

        Label countLabel = new Label(String.valueOf(count));
        countLabel.getStyleClass().add("count_" + acmgFilterCode.name());
        if (acmgFilterCode.name().startsWith("PREDICTION_")) {
            Label alias = new Label(acmgFilterCode.getAlias());
            alias.getStyleClass().add("alias_ALL");
            alias.getStyleClass().add("alias_" + acmgFilterCode.name());
            HBox hbox = new HBox(alias, countLabel);
            box.getChildren().setAll(hbox, levelLabel);
        } else {
            box.getChildren().setAll(levelLabel, countLabel);
        }

        // 마우스 클릭 이벤트 바인딩
        box.setOnMouseClicked(event -> {
            showVariantList(acmgFilterCode, 0);
            setOnSelectedFilter(acmgFilterCode);
        });

        return box;
    }

    /**
     * 필터 선택 표시
     * @param predictionCode
     */
    public void setOnSelectedFilter(ACMGFilterCode predictionCode) {
        if(filterList.getChildren() != null && filterList.getChildren().size() > 0) {
            for (Node node : filterList.getChildren()) {
                VBox box = (VBox) node;
                // 선택 속성 클래스 삭제
                box.getStyleClass().remove("selected");
                if(box.getId().equals(predictionCode.name())) {
                    // 선택 속성 클래스 추가
                    box.getStyleClass().add("selected");
                }
            }
        }
    }

    /**
     * 변이 목록 화면 출력
     * @param acmgFilterCode
     */
    @SuppressWarnings("unchecked")
    public void showVariantList(ACMGFilterCode acmgFilterCode, int selectedIdx) {
        selectedPredictionCodeFilter = acmgFilterCode;

        Map<String,Object> paramMap = new HashMap<>();
        if(acmgFilterCode != null && acmgFilterCode != ACMGFilterCode.TOTAL_VARIANT) {
            if (acmgFilterCode == ACMGFilterCode.LOW_CONFIDENCE) {
                paramMap.put("warning", acmgFilterCode.getCode());
            } else {
                paramMap.put("prediction", acmgFilterCode.getCode());
            }
        }
        try {
            // API 서버 조회
            HttpClientResponse response = /*apiService.get("/analysis_result/variant_list/", paramMap,
                    null, false);*/null;

            //List<AnalysisResultVariant> list = (List<AnalysisResultVariant>) response
            //        .getMultiObjectBeforeConvertResponseToJSON(AnalysisResultVariant.class, false);
            List<AnalysisResultVariant> list = dummyVariantList();
            ObservableList<AnalysisResultVariant> displayList = null;

            // 하단 탭 활성화 토글
            setDetailTabActivationToggle(true);

           if (list != null && list.size() > 0) {
                displayList = FXCollections.observableArrayList(list);
                logger.info(displayList.size() + "");
            }

            // 리스트 삽입
          if (variantListTableView.getItems() != null && variantListTableView.getItems().size() > 0) {
                variantListTableView.getItems().clear();
            }
            variantListTableView.setItems(displayList);

            int reportCount = 0;
            int falseCount = 0;

            // 화면 출력
            if (displayList != null && displayList.size() > 0) {
                // report & false variant 카운트 집계
                for (AnalysisResultVariant item : displayList) {
                    if (!StringUtils.isEmpty(item.getPathogenicReportYn())
                            && "Y".equals(item.getPathogenicReportYn())) {
                        reportCount++;
                    }
                    if (!StringUtils.isEmpty(item.getPathogenicFalseYn()) && "Y".equals(item.getPathogenicFalseYn())) {
                        falseCount++;
                    }
                }
                variantListTableView.getSelectionModel().select(selectedIdx);
                showVariantDetail(displayList.get(selectedIdx));
            } else {
                setDetailTabActivationToggle(false);
            }

        } /*catch (WebAPIException wae) {
            //variantListTableView.setItems(null);
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        }*/ catch (Exception e) {
            variantListTableView.setItems(null);
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }
    }

    /**
     * 선택된 변이 상세 정보 출력
     * @param analysisResultVariant
     */
    @SuppressWarnings("unchecked")
    public void showVariantDetail(AnalysisResultVariant analysisResultVariant) {
        expandOverview();
        // 선택된 변이의 목록에서의 인덱스 정보 설정.
        selectedVariantIndex = variantListTableView.getItems().indexOf(analysisResultVariant);
        // 선택된 변이 객체 정보 설정
        selectedAnalysisResultVariant = analysisResultVariant;
        // 탭 메뉴 활성화 토글
        setDetailTabActivationToggle(false);
        try {
            // Detail 데이터 API 요청
            /*HttpClientResponse responseDetail = apiService.get(
                    "/analysis_result/variant_details/" + selectedAnalysisResultVariant.getId(), null, null, false);*/
            // 상세 데이터 요청이 정상 요청된 경우 진행.
            String data = dummyVariantDetail(selectedAnalysisResultVariant.getId());
            Map<String, Object> detailMap = JsonUtil.fromJsonToMap(data);
            if (detailMap != null && !detailMap.isEmpty() && detailMap.size() > 0) {
                // 하단 Overview 탭 설정.
                if (subTabOverview != null){
                    showOverviewTab(detailMap);
                }
            }
        } /*catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        }*/
        catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

        try {
            // Comment 데이터 API 요청
            Map<String, Object> commentParamMap = new HashMap<>();
            commentParamMap.put("variant", selectedAnalysisResultVariant.getId());
            //HttpClientResponse responseMemo = apiService.get("/analysis_result/pathogenic_comment_list", commentParamMap, null, false);

            // Flagging Comment 데이터 요청이 정상 요청된 경우 진행.

            //ObservableList<AnalysisResultPathogenicComment> commentList = (ObservableList<AnalysisResultPathogenicComment>) responseComment
            //        .getMultiObjectBeforeConvertResponseToJSON(AnalysisResultPathogenicComment.class, true);
            /*if(commentList != null){
                //코드 값을 별칭으로 변경함.
                for(AnalysisResultPathogenicComment comment : commentList) {
                    if (comment.getCommentType().equals(PathogenicReviewFlagTypeCode.PATHOGENICITY.name())) {
                        comment.setPrevValue(PathogenicTypeCode.getAliasFromCode(comment.getPrevValue()));
                        comment.setValue(PathogenicTypeCode.getAliasFromCode(comment.getValue()));
                    }
                }
            }
            String reportYn = StringUtils.defaultIfEmpty(selectedAnalysisResultVariant.getPathogenicReportYn(), "N");
            String falseYn = StringUtils.defaultIfEmpty(selectedAnalysisResultVariant.getPathogenicFalseYn(), "N");

            // 우측 Pathogenic Review 화면 설정
            showPathogenicReview(selectedAnalysisResultVariant.getPrediction(),
                    selectedAnalysisResultVariant.getPathogenic(), reportYn, falseYn);*/
            // comment tab 화면 출력
            if (subTabMemo != null)
                showCommentTab();
        } /*catch (WebAPIException wae) {
            wae.printStackTrace();
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } */catch (Exception e) {
            e.printStackTrace();
            DialogUtil.error("Unknown Error", e.getMessage(), getMainApp().getPrimaryStage(), true);
        }

        // warnings 탭 출력
        if(subTabLowConfidence != null) showLowConfidenceTab(selectedAnalysisResultVariant.getWarningReason());

        // 첫번째 탭 선택 처리
        tabArea.getSelectionModel().selectFirst();
        setDetailTabActivationToggle(true);
    }

    /**
     * 하단 탭 활성봐/비활성화 토글
     * @param flag
     */
    public void setDetailTabActivationToggle(boolean flag) {
        for(Tab tab : tabArea.getTabs()) {
            if(!flag) tab.setContent(null);
            tab.setDisable(!flag);
        }
    }

    @FXML
    public void saveExcel() {

    }

    @FXML
    public void saveCSV() {

    }

    /**
     * Overview 탬 화면 출력
     * @param detailMap
     */
    @SuppressWarnings("unchecked")
    public void showOverviewTab(Map<String,Object> detailMap) {
        try {
            Map<String,Object> alleleMap = (detailMap.containsKey("allele")) ? (Map<String,Object>) detailMap.get("allele") : null;
            Map<String,Object> variantInformationMap = (detailMap.containsKey("variant_information")) ? (Map<String,Object>) detailMap.get("variant_information") : null;
            int sameVariantSampleCountInRun = (detailMap.containsKey("same_variant_sample_count_in_run")) ? (int) detailMap.get("same_variant_sample_count_in_run") : 0;
            int totalSampleCountInRun = (detailMap.containsKey("total_sample_count_in_run")) ? (int) detailMap.get("total_sample_count_in_run") : 0;
            int samePanelSameVariantSampleCountInUsergroup = (detailMap.containsKey("same_panel_same_variant_sample_count_in_usergroup")) ? (int) detailMap.get("same_panel_same_variant_sample_count_in_usergroup") : 0;
            int totalSamePanelSampleCountInUsergroup = (detailMap.containsKey("total_same_panel_sample_count_in_usergroup")) ? (int) detailMap.get("total_same_panel_sample_count_in_usergroup") : 0;
            int sameVariantSampleCountInUsergroup = (detailMap.containsKey("same_variant_sample_count_in_usergroup")) ? (int) detailMap.get("same_variant_sample_count_in_usergroup") : 0;
            int totalSampleCountInUsergroup = (detailMap.containsKey("total_sample_count_in_usergroup")) ? (int) detailMap.get("total_sample_count_in_usergroup") : 0;
            Map<String,Object> variantClassifierMap = (detailMap.containsKey("variant_classifier")) ? (Map<String,Object>) detailMap.get("variant_classifier") : null;
            Map<String,Object> clinicalMap = (detailMap.containsKey("clinical")) ? (Map<String,Object>) detailMap.get("clinical") : null;
            Map<String,Object> breastCancerInformationCoreMap = (detailMap.containsKey("breast_cancer_information_core")) ? (Map<String,Object>) detailMap.get("breast_cancer_information_core") : null;
            Map<String,Object> populationFrequencyMap = (detailMap.containsKey("population_frequency")) ? (Map<String,Object>) detailMap.get("population_frequency") : null;
            Map<String,Object> geneMap = (detailMap.containsKey("gene")) ? (Map<String,Object>) detailMap.get("gene") : null;
            Map<String,Object> inSilicoPredictionMap = (detailMap.containsKey("in_silico_prediction")) ? (Map<String,Object>) detailMap.get("in_silico_prediction") : null;
            Map<String,Object> enigmaMap = (detailMap.containsKey("ENIGMA")) ? (Map<String,Object>) detailMap.get("ENIGMA") : null;
            Map<String,Object> buildMap = (detailMap.containsKey("build")) ? (Map<String,Object>) detailMap.get("build") : null;
            Map<String,Object> genomicCoordinateMap = (detailMap.containsKey("genomic_coordinate")) ? (Map<String,Object>) detailMap.get("genomic_coordinate") : null;

            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("sample", sample);
            paramMap.put("analysisResultVariant", selectedAnalysisResultVariant);
            paramMap.put("allele", alleleMap);
            paramMap.put("variantInformation", variantInformationMap);
            paramMap.put("variantClassifier", variantClassifierMap);
            paramMap.put("clinical", clinicalMap);
            paramMap.put("breastCancerInformationCore", breastCancerInformationCoreMap);
            paramMap.put("populationFrequency", populationFrequencyMap);
            paramMap.put("gene", geneMap);
            paramMap.put("inSilicoPrediction", inSilicoPredictionMap);
            paramMap.put("sameVariantSampleCountInRun", sameVariantSampleCountInRun);
            paramMap.put("totalSampleCountInRun", totalSampleCountInRun);
            paramMap.put("samePanelSameVariantSampleCountInUsergroup", samePanelSameVariantSampleCountInUsergroup);
            paramMap.put("totalSamePanelSampleCountInUsergroup", totalSamePanelSampleCountInUsergroup);
            paramMap.put("sameVariantSampleCountInUsergroup", sameVariantSampleCountInUsergroup);
            paramMap.put("totalSampleCountInUsergroup", totalSampleCountInUsergroup);
            paramMap.put("enigma", enigmaMap);
            paramMap.put("build", buildMap);
            paramMap.put("genomicCoordinate", genomicCoordinateMap);
            paramMap.put("analysisResultSummary", analysisResultSummary);

            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW);
            Node node = loader.load();
            overviewController = loader.getController();
            overviewController.setMainController(this.getMainController());
            overviewController.setAnalysisDetailSNPsINDELsController(this);
            overviewController.setParamMap(paramMap);
            overviewController.show((Parent) node);
            //showLink(paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Memo 탭 화면 출력
     */
    public void showCommentTab(/*ObservableList<AnalysisResultPathogenicComment> commentList*/) {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_MEMO);
            Node node = loader.load();
            AnalysisDetailSNPsINDELsMemoController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setAnalysisDetailSNPsINDELsController(this);
            controller.show((Parent) node);
            //controller.displayList(commentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Low Confidence 탭 화면 출력
     * @param warningReasonJsonStr
     */
    public void showLowConfidenceTab(String warningReasonJsonStr) {
        try {
            Map<String,Object> map = null;
            if(!StringUtils.isEmpty(warningReasonJsonStr)) {
                map = JsonUtil.fromJsonToMap(warningReasonJsonStr.replace("'", "\""));
            }

            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_LOW_CONFIDENCE);
            Node node = loader.load();
            AnalysisDetailSNPsINDELsLowConfidenceController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setAnalysisDetailSNPsINDELsController(this);
            controller.setParamMap(map);
            controller.show((Parent) node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void expandOverview() {
        mainGridPane.getRowConstraints().get(2).setPrefHeight(this.overviewExpandedHeight);
        tabArea.setPrefHeight(this.overviewExpandedHeight);
        overviewFoldButton.getStyleClass().clear();
        overviewFoldButton.getStyleClass().add("btn_fold");
    }
    private void foldOverview(){
        mainGridPane.getRowConstraints().get(2).setPrefHeight(this.overviewFoldedHeight);
        tabArea.setPrefHeight(this.overviewFoldedHeight);
        overviewFoldButton.getStyleClass().clear();
        overviewFoldButton.getStyleClass().add("btn_expand");
    }

    /**
     * IGV 실행 및 데이터 로드
     * @param sampleId
     * @param sampleName
     * @param variantId
     * @param gene
     * @param locus
     * @param genome
     */
    public void loadIGV(String sampleId, String sampleName, String variantId, String gene, String locus, String genome) throws Exception {
        igvService.load(sampleId, sampleName, variantId, gene, locus, genome);
    }

    /**
     * Alamut 연동
     * @param transcript
     * @param cDNA
     * @param sampleId
     * @param bamFileName
     */
    public void loadAlamut(String transcript, String cDNA, String sampleId, String bamFileName) {
        alamutService.call(transcript, cDNA, sampleId, bamFileName);
    }

    /**
     * Flagging 영역 비활성화 처리
     */
    public void disableFlagging(boolean disable) {
        pathogenicArea.setDisable(disable);
    }

    /**
     * 변이 상세 정보 새로고침
     */
    public void refreshVariantDetail() {
        showVariantList(selectedPredictionCodeFilter, selectedVariantIndex);
    }

    private void dummyDataCreated() {
        AnalysisResultSummary analysisResultSummary = new AnalysisResultSummary(sample.getId(),"warn",281,768,
                new BigDecimal("438"),2,5,0,0,0,0,0,5
        ,new BigDecimal(98.13662348939866),"pass",new BigDecimal(100.0),"pass",new BigDecimal(100),"pass",
                new BigDecimal(100.0),"pass");
        this.analysisResultSummary = analysisResultSummary;
        paramMap.put("analysisResultSummary", analysisResultSummary);

    }

    @SuppressWarnings("unchecked")
    private List<AnalysisResultVariant> dummyVariantList() {
        List<AnalysisResultVariant> variantsList = null;

        String str = "[{\"id\":25924,\"sample\":1448,\"prediction\":\"E\",\"variant_id\":1,\"type\":\"snp\",\"zygosity\":\"homozygote\",\"coding_consequence\":\"synonymous\",\"refseqid\":\"NM_000059.3\",\"c_dna\":\"c.3396A>G\",\"protein\":\"p.(=)\",\"exon_id\":\"11/27\",\"gene_strand\":\"+\",\"exon_id_bic\":\"11\",\"gene\":\"BRCA2\",\"ref\":\"A\",\"alt\":\"G\",\"left_seq\":\"TTGAATTTACTCAGTTTAGAAA\",\"right_seq\":\"CCAAGCTACATATTGCAGAAGA\",\"chromosome\":\"chr13\",\"snp\":\"rs1801406\",\"variant_depth\":\"436\",\"allele_fraction\":\"99.77064220183486\",\"reference_number\":\"1\",\"alternate_number\":\"435\",\"genomic_position\":\"32911888\",\"genomic_end_position\":\"32911888\",\"reference_genome\":\"GRCh37/hg19\",\"warning\":\"no\",\"sift\":\"\",\"polyphen2\":\"\",\"mutationtaster\":\"\",\"esp_6500\":\"0.27983697323900336\",\"exac\":\"0.29338121437749154\",\"korean_exome\":\"0.38648\",\"g1000_genomes\":\"0.266773\",\"clinical\":\"{'hgvs': 'NC_000013.10:g.32911888A>G', 'allele_orign': {'0': 'Both'}, 'classification': 'Benign, Benign|Benign|Benign|Benign', 'alternate_allele': '2', 'variant_disease_db_name': 'Breast-ovarian cancer,familial 2|Familial cancer of breast|Hereditary cancer-predisposing syndrome|not specified', 'clinical_channel': {'channel': 'Breast_Cancer_Information_Core_(BRCA2)', 'channel_id': '3624&base_change=A_to_G'}, 'total_rcv': 2, 'database_source': {'database': 'GeneReviews:MedGen:OMIM:Orphanet|GeneReviews:MedGen:OMIM:SNOMED_CT|MedGen:SNOMED_CT|MedGen', 'database_id': 'NBK1247:C2675520:612555:ORPHA145|NBK1247:C0346153:114480:254843006|C0027672:699346009|CN169374'}, 'clinvar_accession': 'RCV000113169.1, RCV000113170.4|RCV000114982.2|RCV000130987.2|RCV000152873.3', 'review_status': 'Reviewed by expert panel|Criteria provided single submitter|Criteria provided single submitter|Criteria provided single submitter', 'radar': '1', 'gene_info': {'0': 'BRCA2:675'}}\",\"bic_category\":\"Synonymous(Syn)\",\"bic_importance\":\"no\",\"bic_classification\":\"Likely not pathogenic/little clinical significance\",\"bic_designation\":\"3624A>G\",\"bic_nt\":\"3624\",\"kohbra_patient\":\"\",\"kohbra_frequency\":\"\",\"serial_number\":\"GRCh37-chr13-32911888-32911888-G\",\"warning_reason\":\"{'roi_region': 'yes', 'low_variant_coverage_depth': 'no', 'low_coverage': 'no', 'primer_deletion': 'no', 'warning': 'no', 'low_confidence': 'no', 'pre_filter': 'no', 'homopolymer_region': 'no', 'low_variant_fraction': 'no', 'is_control': 'no', 'is_primer': 'no', 'soft_clipped_amplicon': 'no'}\",\"experiment_type\":\"germline\",\"pat\":null,\"pathogenic_report_yn\":\"N\",\"pathogenic_false_yn\":\"N\",\"enigma\":\"Benign(ENIGMA)\",\"be_clin_update\":\"2004-02-20_2014-01-02_2014-10-30_2011-03-17_2015-01-12_2014-11-07_2013-09-04\",\"be_clin_origin\":\"germline,unknown\",\"be_clin_meth\":\"curation,clinical,testing,literature,only\",\"be_bic_cate\":\"Syn\",\"be_bic_eth\":\"Global,-,Causian,Sinhalese,Polish,Caucasian\",\"be_bic_nat\":\"USA German Spanish - Austria Sri Lankan Polish\",\"be_ref\":\"NM_000059.3\",\"be_nuc\":\"NM_000059.3:c.3396A>G\",\"be_gene\":\"BRCA2\",\"be_eni_cond\":\"OMIM\",\"be_eni_update\":\"1/12/15\",\"be_eni_comm\":\"Class 1 not pathogenic based on frequency >1% in an outbred sampleset. Frequency 0.3846 (Asian)  0.1646 (African)  0.2942 (European)  derived from 1000 genomes (2012-04-30).\",\"be_path_clin\":\"Benign (ClinVar)\",\"be_path_eni\":\"Benign(ENIGMA)\",\"be_path_bic\":\"Class 1 (BIC)\"},{\"id\":25925,\"sample\":1448,\"prediction\":\"E\",\"variant_id\":2,\"type\":\"snp\",\"zygosity\":\"homozygote\",\"coding_consequence\":\"synonymous\",\"refseqid\":\"NM_000059.3\",\"c_dna\":\"c.4563A>G\",\"protein\":\"p.(=)\",\"exon_id\":\"11/27\",\"gene_strand\":\"+\",\"exon_id_bic\":null,\"gene\":\"BRCA2\",\"ref\":\"A\",\"alt\":\"G\",\"left_seq\":\"AAAAGATCAAAGAACCTACTCT\",\"right_seq\":\"TTGGGTTTTCATACAGCTAGCG\",\"chromosome\":\"chr13\",\"snp\":\"rs206075\",\"variant_depth\":\"281\",\"allele_fraction\":\"100.0\",\"reference_number\":\"0\",\"alternate_number\":\"281\",\"genomic_position\":\"32913055\",\"genomic_end_position\":\"32913055\",\"reference_genome\":\"GRCh37/hg19\",\"warning\":\"no\",\"sift\":\"\",\"polyphen2\":\"\",\"mutationtaster\":\"\",\"esp_6500\":\"0.9757729580064606\",\"exac\":\"0.9930558987792221\",\"korean_exome\":\"1.0\",\"g1000_genomes\":\"0.974042\",\"clinical\":\"{'hgvs': 'NC_000013.10:g.32913055A>G', 'allele_orign': {'0': 'Germline'}, 'classification': 'Benign, Benign|Likely benign|Likely benign, Benign|Benign|Benign|Benign', 'alternate_allele': '2', 'variant_disease_db_name': 'Breast-ovarian cancer,familial 2|Familial cancer of breast|Hereditary cancer-predisposing syndrome|not specified', 'clinical_channel': {'channel': 'None', 'channel_id': 'None'}, 'total_rcv': 3, 'database_source': {'database': 'GeneReviews:MedGen:OMIM:Orphanet|GeneReviews:MedGen:OMIM:SNOMED_CT|MedGen:SNOMED_CT|MedGen', 'database_id': 'NBK1247:C2675520:612555:ORPHA145|NBK1247:C0346153:114480:254843006|C0027672:699346009|CN169374'}, 'clinvar_accession': 'RCV000113321.1, RCV000160224.1|RCV000164515.1|RCV000198429.1, RCV000119245.2|RCV000123972.2|RCV000132170.2|RCV000152875.4', 'review_status': 'Reviewed by expert panel|Criteria provided single submitter|Criteria provided single submitter|Criteria provided single submitter', 'radar': '2', 'gene_info': {'0': 'BRCA2:675'}}\",\"bic_category\":\"\",\"bic_importance\":\"\",\"bic_classification\":\"\",\"bic_designation\":null,\"bic_nt\":null,\"kohbra_patient\":\"\",\"kohbra_frequency\":\"\",\"serial_number\":\"GRCh37-chr13-32913055-32913055-G\",\"warning_reason\":\"{'roi_region': 'yes', 'low_variant_coverage_depth': 'no', 'low_coverage': 'no', 'primer_deletion': 'no', 'warning': 'no', 'low_confidence': 'no', 'pre_filter': 'yes', 'homopolymer_region': 'no', 'low_variant_fraction': 'no', 'is_control': 'no', 'is_primer': 'no', 'soft_clipped_amplicon': 'no'}\",\"experiment_type\":\"germline\",\"pat\":null,\"pathogenic_report_yn\":\"N\",\"pathogenic_false_yn\":\"N\",\"enigma\":\"Benign(ENIGMA)\",\"be_clin_update\":\"None_2014-01-02_2014-11-19_2013-11-13_2014-10-15_2015-01-12\",\"be_clin_origin\":\"germline,unknown\",\"be_clin_meth\":\"curation,clinical,testing,literature,only\",\"be_bic_cate\":\"-\",\"be_bic_eth\":\"-\",\"be_bic_nat\":\"-\",\"be_ref\":\"NM_000059.3\",\"be_nuc\":\"NM_000059.3:c.4563A>G\",\"be_gene\":\"BRCA2\",\"be_eni_cond\":\"OMIM\",\"be_eni_update\":\"1/12/15\",\"be_eni_comm\":\"Class 1 not pathogenic based on frequency >1% in an outbred sampleset. Frequency 0.8902 (African)  derived from 1000 genomes (2012-04-30).\",\"be_path_clin\":\"Benign (ClinVar)\",\"be_path_eni\":\"Benign(ENIGMA)\",\"be_path_bic\":null},{\"id\":25926,\"sample\":1448,\"prediction\":\"E\",\"variant_id\":3,\"type\":\"snp\",\"zygosity\":\"homozygote\",\"coding_consequence\":\"synonymous\",\"refseqid\":\"NM_000059.3\",\"c_dna\":\"c.6513G>C\",\"protein\":\"p.(=)\",\"exon_id\":\"11/27\",\"gene_strand\":\"+\",\"exon_id_bic\":null,\"gene\":\"BRCA2\",\"ref\":\"G\",\"alt\":\"C\",\"left_seq\":\"AGTTGGTATTAGGAACCAAAGT\",\"right_seq\":\"TCACTTGTTGAGAACATTCATG\",\"chromosome\":\"chr13\",\"snp\":\"rs206076\",\"variant_depth\":\"419\",\"allele_fraction\":\"100.0\",\"reference_number\":\"0\",\"alternate_number\":\"419\",\"genomic_position\":\"32915005\",\"genomic_end_position\":\"32915005\",\"reference_genome\":\"GRCh37/hg19\",\"warning\":\"no\",\"sift\":\"\",\"polyphen2\":\"\",\"mutationtaster\":\"\",\"esp_6500\":\"0.975472858680609\",\"exac\":\"0.9930139062808937\",\"korean_exome\":\"1.0\",\"g1000_genomes\":\"0.973642\",\"clinical\":\"{'hgvs': 'NC_000013.10:g.32915005G>T', 'allele_orign': {'0': 'Germline'}, 'classification': 'not provided, Benign|Benign|Benign|Benign, Benign|Likely benign|Uncertain significance|Uncertain significance|Likely benign', 'alternate_allele': '2', 'variant_disease_db_name': 'Familial cancer of breast|Hereditary cancer-predisposing syndrome|not specified|not provided|BRCA1 and BRCA2 Hereditary Breast and Ovarian Cancer', 'clinical_channel': {'channel': 'None', 'channel_id': 'None'}, 'total_rcv': 3, 'database_source': {'database': 'GeneReviews:MedGen:OMIM:SNOMED_CT|MedGen:SNOMED_CT|MedGen|MedGen|GeneReviews:MedGen', 'database_id': 'NBK1247:C0346153:114480:254843006|C0027672:699346009|CN169374|CN221809|NBK1247:C0677776'}, 'clinvar_accession': 'RCV000113602.1, RCV000119246.2|RCV000123985.2|RCV000152878.4|RCV000162377.1, RCV000160234.1|RCV000163332.1|RCV000168590.1|RCV000173959.1|RCV000195987.1', 'review_status': 'Criteria provided single submitter|Criteria provided single submitter|No assertion criteria provided|Criteria provided single submitter|Criteria provided single submitter', 'radar': '3', 'gene_info': {'0': 'BRCA2:675'}}\",\"bic_category\":\"\",\"bic_importance\":\"\",\"bic_classification\":\"\",\"bic_designation\":null,\"bic_nt\":null,\"kohbra_patient\":\"\",\"kohbra_frequency\":\"\",\"serial_number\":\"GRCh37-chr13-32915005-32915005-C\",\"warning_reason\":\"{'roi_region': 'yes', 'low_variant_coverage_depth': 'no', 'low_coverage': 'no', 'primer_deletion': 'no', 'warning': 'no', 'low_confidence': 'no', 'pre_filter': 'yes', 'homopolymer_region': 'no', 'low_variant_fraction': 'no', 'is_control': 'no', 'is_primer': 'no', 'soft_clipped_amplicon': 'no'}\",\"experiment_type\":\"germline\",\"pat\":null,\"pathogenic_report_yn\":\"N\",\"pathogenic_false_yn\":\"N\",\"enigma\":\"Benign(ENIGMA)\",\"be_clin_update\":\"None_2014-01-02_2014-06-23_2014-11-19_2014-10-15_2015-01-12\",\"be_clin_origin\":\"germline,unknown\",\"be_clin_meth\":\"curation,clinical,testing,literature,only\",\"be_bic_cate\":\"-\",\"be_bic_eth\":\"-\",\"be_bic_nat\":\"-\",\"be_ref\":\"NM_000059.3\",\"be_nuc\":\"NM_000059.3:c.6513G>C\",\"be_gene\":\"BRCA2\",\"be_eni_cond\":\"OMIM\",\"be_eni_update\":\"1/12/15\",\"be_eni_comm\":\"Class 1 not pathogenic based on frequency >1% in an outbred sampleset. Frequency 0.8902 (African)  derived from 1000 genomes (2012-04-30).\",\"be_path_clin\":\"Benign (ClinVar)\",\"be_path_eni\":\"Benign(ENIGMA)\",\"be_path_bic\":null},{\"id\":25928,\"sample\":1448,\"prediction\":\"E\",\"variant_id\":4,\"type\":\"snp\",\"zygosity\":\"homozygote\",\"coding_consequence\":\"synonymous\",\"refseqid\":\"NM_000059.3\",\"c_dna\":\"c.7242A>G\",\"protein\":\"p.(=)\",\"exon_id\":\"14/27\",\"gene_strand\":\"+\",\"exon_id_bic\":\"14\",\"gene\":\"BRCA2\",\"ref\":\"A\",\"alt\":\"G\",\"left_seq\":\"TTCCACCTTTTAAAACTAAATC\",\"right_seq\":\"CATTTTCACAGAGTTGAACAGT\",\"chromosome\":\"chr13\",\"snp\":\"rs1799955\",\"variant_depth\":\"768\",\"allele_fraction\":\"100.0\",\"reference_number\":\"0\",\"alternate_number\":\"768\",\"genomic_position\":\"32929232\",\"genomic_end_position\":\"32929232\",\"reference_genome\":\"GRCh37/hg19\",\"warning\":\"no\",\"sift\":\"\",\"polyphen2\":\"\",\"mutationtaster\":\"\",\"esp_6500\":\"0.21136398585268337\",\"exac\":\"0.22430976541908276\",\"korean_exome\":\"0.38499\",\"g1000_genomes\":\"0.232628\",\"clinical\":\"{'hgvs': 'NC_000013.10:g.32929232A>G', 'allele_orign': {'0': 'Germline'}, 'classification': 'Benign|Benign|Benign|Benign', 'alternate_allele': '1', 'variant_disease_db_name': 'Breast-ovarian cancer,familial 2|Familial cancer of breast|Hereditary cancer-predisposing syndrome|not specified', 'clinical_channel': {'channel': 'Breast_Cancer_Information_Core_(BRCA2)', 'channel_id': '7470&base_change=A_to_G'}, 'total_rcv': 1, 'database_source': {'database': 'GeneReviews:MedGen:OMIM:Orphanet|GeneReviews:MedGen:OMIM:SNOMED_CT|MedGen:SNOMED_CT|MedGen', 'database_id': 'NBK1247:C2675520:612555:ORPHA145|NBK1247:C0346153:114480:254843006|C0027672:699346009|CN169374'}, 'clinvar_accession': 'RCV000113739.4|RCV000123992.2|RCV000130994.2|RCV000152882.3', 'review_status': 'Reviewed by expert panel|Criteria provided single submitter|Criteria provided single submitter|Criteria provided single submitter', 'radar': '1', 'gene_info': {'0': 'BRCA2:675'}}\",\"bic_category\":\"Synonymous(Syn)\",\"bic_importance\":\"no\",\"bic_classification\":\"Likely not pathogenic/little clinical significance\",\"bic_designation\":\"7470A>G\",\"bic_nt\":\"7470\",\"kohbra_patient\":\"\",\"kohbra_frequency\":\"\",\"serial_number\":\"GRCh37-chr13-32929232-32929232-G\",\"warning_reason\":\"{'roi_region': 'yes', 'low_variant_coverage_depth': 'no', 'low_coverage': 'no', 'primer_deletion': 'no', 'warning': 'no', 'low_confidence': 'no', 'pre_filter': 'no', 'homopolymer_region': 'no', 'low_variant_fraction': 'no', 'is_control': 'no', 'is_primer': 'no', 'soft_clipped_amplicon': 'no'}\",\"experiment_type\":\"germline\",\"pat\":null,\"pathogenic_report_yn\":\"N\",\"pathogenic_false_yn\":\"N\",\"enigma\":\"Benign(ENIGMA)\",\"be_clin_update\":\"2014-12-08_2014-01-02_2014-11-19_2011-03-17_2002-05-29_2015-01-12_2013-09-04\",\"be_clin_origin\":\"germline,unknown\",\"be_clin_meth\":\"curation,clinical,testing,literature,only\",\"be_bic_cate\":\"Syn\",\"be_bic_eth\":\"Global,-,Arab,Sinhalese,Polish,Caucasian\",\"be_bic_nat\":\"USA German Sri Lankan - Austria Spanish Polish Omanis Belgian\",\"be_ref\":\"NM_000059.3\",\"be_nuc\":\"NM_000059.3:c.7242A>G\",\"be_gene\":\"BRCA2\",\"be_eni_cond\":\"OMIM\",\"be_eni_update\":\"1/12/15\",\"be_eni_comm\":\"Class 1 not pathogenic based on frequency >1% in an outbred sampleset. Frequency 0.3864 (Asian)  0.1585 (African)  0.219 (European)  derived from 1000 genomes (2012-04-30).\",\"be_path_clin\":\"Benign (ClinVar)\",\"be_path_eni\":\"Benign(ENIGMA)\",\"be_path_bic\":\"Class 1 (BIC)\"},{\"id\":25927,\"sample\":1448,\"prediction\":\"E\",\"variant_id\":5,\"type\":\"snp\",\"zygosity\":\"homozygote\",\"coding_consequence\":\"missense\",\"refseqid\":\"NM_000059.3\",\"c_dna\":\"c.7397T>C\",\"protein\":\"p.(=)\",\"exon_id\":\"14/27\",\"gene_strand\":\"+\",\"exon_id_bic\":null,\"gene\":\"BRCA2\",\"ref\":\"T\",\"alt\":\"C\",\"left_seq\":\"AAAAACAACTCCAATCAAGCAG\",\"right_seq\":\"AGCTGTAACTTTCACAAAGTGT\",\"chromosome\":\"chr13\",\"snp\":\"rs169547\",\"variant_depth\":\"286\",\"allele_fraction\":\"100.0\",\"reference_number\":\"0\",\"alternate_number\":\"286\",\"genomic_position\":\"32929387\",\"genomic_end_position\":\"32929387\",\"reference_genome\":\"GRCh37/hg19\",\"warning\":\"no\",\"sift\":\"1.0\",\"polyphen2\":\"\",\"mutationtaster\":\"P:polymorphism automatic\",\"esp_6500\":\"0.9776957391170589\",\"exac\":\"0.9937231255869124\",\"korean_exome\":\"1.0\",\"g1000_genomes\":\"0.975839\",\"clinical\":\"{'hgvs': 'NC_000013.10:g.32929387T>C', 'allele_orign': {'0': 'Germline'}, 'classification': 'Benign|Benign|Benign, Benign', 'alternate_allele': '1', 'variant_disease_db_name': 'not specified', 'clinical_channel': {'channel': 'HGMD', 'channel_id': 'CM960194'}, 'total_rcv': 2, 'database_source': {'database': 'MedGen', 'database_id': 'CN169374'}, 'clinvar_accession': 'RCV000045197.3|RCV000113751.2|RCV000168597.1, RCV000120357.4', 'review_status': 'Criteria provided single submitter', 'radar': '1', 'gene_info': {'0': 'BRCA2:675'}}\",\"bic_category\":\"\",\"bic_importance\":\"\",\"bic_classification\":\"\",\"bic_designation\":null,\"bic_nt\":null,\"kohbra_patient\":\"\",\"kohbra_frequency\":\"\",\"serial_number\":\"GRCh37-chr13-32929387-32929387-C\",\"warning_reason\":\"{'roi_region': 'yes', 'low_variant_coverage_depth': 'no', 'low_coverage': 'no', 'primer_deletion': 'no', 'warning': 'no', 'low_confidence': 'no', 'pre_filter': 'yes', 'homopolymer_region': 'no', 'low_variant_fraction': 'no', 'is_control': 'no', 'is_primer': 'no', 'soft_clipped_amplicon': 'no'}\",\"experiment_type\":\"germline\",\"pat\":null,\"pathogenic_report_yn\":\"N\",\"pathogenic_false_yn\":\"N\",\"enigma\":null,\"be_clin_update\":\"2014-10-15\",\"be_clin_origin\":\"germline\",\"be_clin_meth\":\"clinical,testing\",\"be_bic_cate\":\"-\",\"be_bic_eth\":\"-\",\"be_bic_nat\":\"-\",\"be_ref\":\"NM_000059.3\",\"be_nuc\":\"NM_000059.3:c.7397T>C\",\"be_gene\":\"BRCA2\",\"be_eni_cond\":\"-\",\"be_eni_update\":\"-\",\"be_eni_comm\":\"-\",\"be_path_clin\":\"Benign (ClinVar)\",\"be_path_eni\":null,\"be_path_bic\":null}]";

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            List<AnalysisResultVariant> list = mapper.readValue(str, mapper.getTypeFactory().constructCollectionType(List.class, AnalysisResultVariant.class));
            if(list != null && !list.isEmpty()) {
                return list;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String dummyVariantDetail(int id) {
        String data = null;
        if(id == 25924) {
            data = "{\"experiment\":{\"sequencer\":\"Illumina MiSeq\",\"specimen_type\":\"blood\",\"experiment_user\":\"Changbum Hong\",\"experiment_type\":\"germline\"},\"same_variant_sample_count_in_usergroup\":8,\"total_sample_count_in_usergroup\":8,\"variant_classifier\":{\"radar\":\"1\",\"grade\":\"E\",\"result\":\"benign\"},\"variant_information\":{\"type\":\"snp\",\"alt\":\"G\",\"start\":32911888,\"vcf_format\":\"chr13 32911888.A G.\",\"stop\":32911888,\"ucsc_format\":\"chr13:32,911,888-32,911,888\",\"1kg_url\":\"\",\"brca_exchange_url\":\"http://brcaexchange.org/variants?search=chr13:g.32911888\",\"rs_id\":\"rs1801406\",\"size\":\"1bp\",\"esp_url\":\"\",\"ucsc_url\":\"http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&highlight=hg19.chr13%3A32,911,888-32,911,888&position=chr13%3A32911858-32911918\",\"filter\":\"None\",\"ncbi_url\":\"http://www.ncbi.nlm.nih.gov/gene/675\",\"exac_format\":\"13-32911888-A-G\",\"ref\":\"A\",\"right_22_bp\":\"CCAAGCTACATATTGCAGAAGA\",\"exac_url\":\"http://exac.broadinstitute.org/variant/13-32911888-A-G\",\"clinvar_url\":\"http://www.ncbi.nlm.nih.gov/clinvar?term=rs1801406\",\"left_22_bp\":\"TTGAATTTACTCAGTTTAGAAA\"},\"assay\":{\"amplicon\":\"\",\"pipeline\":\"444\",\"company\":\"NGeneBio\",\"assay_name\":\"NGB_BRCAccuTest\"},\"korean_hereditary_breast_cancer_study\":{\"high_risk_breast_cancer_patient_in_kohbra\":\"\",\"high_risk_breast_cancer_frequency_in_kohbra\":\"\"},\"breast_cancer_information_core\":{\"exon\":\"11\",\"nt\":\"3624\",\"mutation_category_bic\":\"Synonymous(Syn)\",\"clinical_importance\":\"no\",\"clinical_classification\":\"Likely not pathogenic/little clinical significance\",\"bic_designation\":\"3624A>G\",\"radar\":\"2\"},\"build\":{\"dbsnp_build\":\"dbSNP 147\",\"ESP6500\":\"ESP6500SI-V2-SSA137\",\"ExAc\":\"0.31\",\"1000_genomes\":\"20150218 phase3\",\"ref_genome\":\"GRCh37/hg19\",\"ESP5400\":\"0.3\",\"clinvar_build\":\"20160502\"},\"in_silico_prediction\":{\"SIFT\":{\"text\":\"\",\"type\":\"Protein effect\",\"test\":\"SIFT\",\"reference range\":\"0-1\",\"score\":\"\",\"radar\":\"\",\"desc\":\"dbNSFP annotation, SIFT score (SIFTori)\"},\"mt\":{\"text\":\"\",\"score\":\"\",\"radar\":\"\"},\"PolyPhen2\":{\"text\":\"\",\"type\":\"Protein effect\",\"test\":\"PolyPhen2\",\"reference range\":\"0-1\",\"score\":\"\",\"radar\":\"\",\"desc\":\"dbNSFP annotation, Polyphen2 score based on HumVar\"}},\"gene\":{\"default_transcript\":\"NM_000059.3\",\"strand\":\"+\",\"entrez_gene_summary\":\"Inherited mutations in BRCA1 and this gene, BRCA2, confer increased lifetime risk of developing breast or ovarian cancer. Both BRCA1 and BRCA2 are involved in maintenance of genome stability, specifically the homolog     ous recombination pathway for double-strand DNA repair. The BRCA2 protein contains several copies of a 70 aa motif called the BRC motif, and these motifs mediate binding to the RAD51 recombinase which functions in DNA repair. BRCA2 is considered a tumor suppre     ssor gene, as tumors with BRCA2 mutations generally exhibit loss of heterozygosity (LOH) of the wild-type allele. [provided by RefSeq, Dec 2008]\",\"gene_symbol\":\"BRCA2\",\"gene_name\":\"BRCA2\",\"transcript\":{\"0\":{\"transcript_name\":\"NM_000059.3\",\"exon_number/total\":\"11/27\",\"hgvs.c\":\"c.3396A>G\",\"strand\":\"+\",\"aa_abbreviation\":\"\",\"genome_37\":\"chr13:g.32911888:A>G\",\"coding_consequence\":\"synonymous\",\"solvebio_format\":\"NM_000059.3(BRCA2):c.3396A>G(p.(=))\",\"hgvs.p\":\"p.(=)\",\"gene_symbol\":\"BRCA2\"}}},\"BRCA_Exchange\":{\"Condition_ID_Value_ENIGMA\":\"BREAST-OVARIAN CANCER  FAMILIAL  SUSCEPTIBILITY TO  2  BROVCA2 (612555)\",\"Functional_analysis_result_LOVD\":\"predicted_neutral_-_?\",\"Patient_nationality_BIC\":\"USA German Spanish - Austria Sri Lankan Polish\",\"Comment_on_Clinical_significance_ENIGMA\":\"Class 1 not pathogenic based on frequency >1% in an outbred sampleset. Frequency 0.3846 (Asian)  0.1646 (African)  0.2942 (European)  derived from 1000 genomes (2012-04-30).\",\"Has_Discordant_Evidence\":\"Concordant\",\"Allele_Origin_ClinVar\":\"germline,unknown\",\"Date_last_evaluated_ENIGMA\":\"1/12/15\",\"Condition_ID_Type_ENIGMA\":\"OMIM\",\"Gene_Symbol\":\"BRCA2\",\"Allele_Origin_BIC\":\"G\",\"Mutation_category_BIC\":\"Syn\",\"Protein\":\"NP_000050.2:p.(eq)\",\"Nucleotide\":\"NM_000059.3:c.3396A>G\",\"ID\":\"1087\",\"Date_last_updated_ClinVar\":\"2004-02-20_2014-01-02_2014-10-30_2011-03-17_2015-01-12_2014-11-07_2013-09-04\",\"Ethnicity_BIC\":\"Global,-,Causian,Sinhalese,Polish,Caucasian\",\"Pathogenicity\":{\"BIC\":\"Class 1 (BIC)\",\"ENIGMA\":\"Benign(ENIGMA)\",\"ClinVar\":\"Benign (ClinVar)\"},\"Analysis_Method_ClinVar\":\"curation,clinical,testing,literature,only\",\"Reference_cDNA_Sequence\":\"NM_000059.3\"},\"genomic_coordinate\":{\"chromosome\":\"chr13\",\"g.pos\":32911888,\"build\":\"GRCh37\"},\"clinical\":{\"gene_info\":{\"0\":\"BRCA2:675\"},\"alternate_allele\":\"2\",\"hgvs\":\"NC_000013.10:g.32911888A>G\",\"clinical_channel\":{\"channel\":\"Breast_Cancer_Information_Core_(BRCA2)\",\"channel_id\":\"3624&base_change=A_to_G\"},\"clinvar_accession\":\"RCV000113169.1, RCV000113170.4|RCV000114982.2|RCV000130987.2|RCV000152873.3\",\"radar\":\"1\",\"database_source\":{\"database\":\"GeneReviews:MedGen:OMIM:Orphanet|GeneReviews:MedGen:OMIM:SNOMED_CT|MedGen:SNOMED_CT|MedGen\",\"database_id\":\"NBK1247:C2675520:612555:ORPHA145|NBK1247:C0346153:114480:254843006|C0027672:699346009|CN169374\"},\"classification\":\"Benign, Benign|Benign|Benign|Benign\",\"allele_orign\":{\"0\":\"Both\"},\"total_rcv\":2,\"review_status\":\"Reviewed by expert panel|Criteria provided single submitter|Criteria provided single submitter|Criteria provided single submitter\",\"variant_disease_db_name\":\"Breast-ovarian cancer,familial 2|Familial cancer of breast|Hereditary cancer-predisposing syndrome|not specified\"},\"allele\":{\"type_of_allele\":\"snp\",\"zygosity_dp\":\"0\",\"total_read_depth\":436,\"alternate\":\"G\",\"alternate_allele_observation\":435,\"reference_allele_observation\":\"1\",\"allele_fraction\":99.77064220183486,\"zygosity\":\"homozygote\",\"reference\":\"A\"},\"same_panel_same_variant_sample_count_in_usergroup\":0,\"same_variant_sample_count_in_run\":1,\"id\":1,\"total_sample_count_in_run\":1,\"ENIGMA\":{\"has\":\"yes\",\"pathogenic\":\"Benign(ENIGMA)\",\"radar\":\"1\"},\"population_frequency\":{\"Korean_exome\":{\"ALL\":{\"allele_frequency\":0.38648,\"allele_count\":2678,\"allele_number\":1035,\"type\":\"Common\",\"population\":\"All Korean Genomes\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"ALL\"}},\"1000_genomes\":{\"European\":{\"allele_frequency\":0.2753,\"allele_count\":\"\",\"allele_number\":\"\",\"type\":\"Common\",\"population\":\"European\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"EUR\"},\"American\":{\"allele_frequency\":0.2378,\"allele_count\":\"\",\"allele_number\":\"\",\"type\":\"Common\",\"population\":\"American\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"AMR\"},\"African\":{\"allele_frequency\":0.2095,\"allele_count\":\"\",\"allele_number\":\"\",\"type\":\"Common\",\"population\":\"African\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"AFR\"},\"South_Asian\":{\"allele_frequency\":0.2515,\"allele_count\":\"\",\"allele_number\":\"\",\"type\":\"Common\",\"population\":\"South Asian\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"SAS\"},\"ALL\":{\"allele_frequency\":0.266773,\"allele_count\":\"\",\"allele_number\":\"\",\"type\":\"Common\",\"population\":\"All 1000 Genomes\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"ALL\"},\"East_Asian\":{\"allele_frequency\":0.3681,\"allele_count\":\"\",\"allele_number\":\"\",\"type\":\"Common\",\"population\":\"East Asian\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"EAS\"}},\"ESP6500\":{\"AA\":{\"allele_frequency\":0.22605537902859738,\"allele_count\":\"996\",\"allele_number\":4406,\"type\":\"Common\",\"population\":\"African American\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"AA\"},\"EA\":{\"allele_frequency\":0.3073970690858339,\"allele_count\":\"2643\",\"allele_number\":8598,\"type\":\"Common\",\"population\":\"European American\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"EA\"},\"ALL\":{\"allele_frequency\":0.27983697323900336,\"allele_count\":\"3639\",\"allele_number\":13004,\"type\":\"Common\",\"population\":\"ALL\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"All ESP6500\"}},\"ExAC\":{\"Latino\":{\"allele_frequency\":0.20869489760499826,\"allele_count\":\"2405\",\"allele_number\":\"11524\",\"type\":\"Common\",\"population\":\"Latino\",\"homozygote_count\":\"256\",\"hemizygote_count\":\"\",\"code\":\"AMR\"},\"African\":{\"allele_frequency\":0.22917897223862965,\"allele_count\":\"2328\",\"allele_number\":\"10158\",\"type\":\"Common\",\"population\":\"African\",\"homozygote_count\":\"273\",\"hemizygote_count\":\"\",\"code\":\"AFR\"},\"South_Asian\":{\"allele_frequency\":0.29242074577918137,\"allele_count\":\"4815\",\"allele_number\":\"16466\",\"type\":\"Common\",\"population\":\"South Asian\",\"homozygote_count\":\"718\",\"hemizygote_count\":\"\",\"code\":\"SAS\"},\"Europena(Non-Finnish)\":{\"allele_frequency\":0.30639968708629195,\"allele_count\":\"20367\",\"allele_number\":\"66472\",\"type\":\"Common\",\"population\":\"Europena(Non-Finnish)\",\"homozygote_count\":\"3115\",\"hemizygote_count\":\"\",\"code\":\"NFE\"},\"Europena(Finnish)\":{\"allele_frequency\":0.30898366606170596,\"allele_count\":\"2043\",\"allele_number\":\"6612\",\"type\":\"Common\",\"population\":\"European(Finnish)\",\"homozygote_count\":\"324\",\"hemizygote_count\":\"\",\"code\":\"FIN\"},\"ALL\":{\"allele_frequency\":0.29338121437749154,\"population\":\"All ExAC\",\"allele_number\":\"121412\",\"type\":\"Common\",\"allele_count\":\"35620\",\"code\":\"ALL\"},\"East_Asian\":{\"allele_frequency\":0.3847846225104215,\"allele_count\":\"3323\",\"allele_number\":\"8636\",\"type\":\"Common\",\"population\":\"East Asian\",\"homozygote_count\":\"663\",\"hemizygote_count\":\"\",\"code\":\"EAS\"},\"Other\":{\"allele_frequency\":0.3148558758314856,\"allele_count\":\"284\",\"allele_number\":\"902\",\"type\":\"Common\",\"population\":\"Other\",\"homozygote_count\":\"42\",\"hemizygote_count\":\"\",\"code\":\"OTH\"}}},\"total_same_panel_sample_count_in_usergroup\":0,\"flag\":{\"soft_clipped_amplicon\":\"no\",\"roi_region\":\"yes\",\"low_coverage\":\"no\",\"low_variant_coverage_depth\":\"no\",\"primer_deletion\":\"no\",\"is_primer\":\"no\",\"low_confidence\":\"no\",\"homopolymer_region\":\"no\",\"warning\":\"no\",\"pre_filter\":\"no\",\"is_control\":\"no\",\"low_variant_fraction\":\"no\"}}";
        } else if(id == 25925) {
            data = "{\"assay\":{\"amplicon\":\"\",\"assay_name\":\"NGB_BRCAccuTest\",\"company\":\"NGeneBio\",\"pipeline\":\"444\"},\"variant_classifier\":{\"result\":\"benign\",\"grade\":\"E\",\"radar\":\"1\"},\"total_sample_count_in_run\":1,\"genomic_coordinate\":{\"chromosome\":\"chr13\",\"build\":\"GRCh37\",\"g.pos\":32913055},\"same_variant_sample_count_in_run\":1,\"same_variant_sample_count_in_usergroup\":8,\"population_frequency\":{\"Korean_exome\":{\"ALL\":{\"population\":\"All Korean Genomes\",\"type\":\"Common\",\"allele_number\":2678,\"code\":\"ALL\",\"allele_count\":2678,\"allele_frequency\":1.0,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"}},\"1000_genomes\":{\"South_Asian\":{\"population\":\"South Asian\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"SAS\",\"allele_count\":\"\",\"allele_frequency\":1.0,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"African\":{\"population\":\"African\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"AFR\",\"allele_count\":\"\",\"allele_frequency\":0.9039,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"European\":{\"population\":\"European\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"EUR\",\"allele_count\":\"\",\"allele_frequency\":0.999,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"American\":{\"population\":\"American\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"AMR\",\"allele_count\":\"\",\"allele_frequency\":0.9971,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"ALL\":{\"population\":\"All 1000 Genomes\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"ALL\",\"allele_count\":\"\",\"allele_frequency\":0.974042,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"East_Asian\":{\"population\":\"East Asian\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"EAS\",\"allele_count\":\"\",\"allele_frequency\":1.0,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"}},\"ExAC\":{\"Europena(Finnish)\":{\"population\":\"European(Finnish)\",\"type\":\"Common\",\"allele_number\":\"6612\",\"code\":\"FIN\",\"allele_count\":\"6612\",\"allele_frequency\":1.0,\"hemizygote_count\":\"\",\"homozygote_count\":\"3306\"},\"Europena(Non-Finnish)\":{\"population\":\"Europena(Non-Finnish)\",\"type\":\"Common\",\"allele_number\":\"66426\",\"code\":\"NFE\",\"allele_count\":\"66395\",\"allele_frequency\":0.999533315268118,\"hemizygote_count\":\"\",\"homozygote_count\":\"33182\"},\"Other\":{\"population\":\"Other\",\"type\":\"Common\",\"allele_number\":\"904\",\"code\":\"OTH\",\"allele_count\":\"903\",\"allele_frequency\":0.9988938053097345,\"hemizygote_count\":\"\",\"homozygote_count\":\"451\"},\"South_Asian\":{\"population\":\"South Asian\",\"type\":\"Common\",\"allele_number\":\"16496\",\"code\":\"SAS\",\"allele_count\":\"16495\",\"allele_frequency\":0.999939379243453,\"hemizygote_count\":\"\",\"homozygote_count\":\"8247\"},\"African\":{\"population\":\"African\",\"type\":\"Common\",\"allele_number\":\"10300\",\"code\":\"AFR\",\"allele_count\":\"9542\",\"allele_frequency\":0.9264077669902913,\"hemizygote_count\":\"\",\"homozygote_count\":\"4417\"},\"Latino\":{\"population\":\"Latino\",\"type\":\"Common\",\"allele_number\":\"11564\",\"code\":\"AMR\",\"allele_count\":\"11526\",\"allele_frequency\":0.9967139398132134,\"hemizygote_count\":\"\",\"homozygote_count\":\"5744\"},\"ALL\":{\"population\":\"All ExAC\",\"type\":\"Common\",\"allele_number\":\"121398\",\"code\":\"ALL\",\"allele_count\":\"120555\",\"allele_frequency\":0.9930558987792221},\"East_Asian\":{\"population\":\"East Asian\",\"type\":\"Common\",\"allele_number\":\"8652\",\"code\":\"EAS\",\"allele_count\":\"8651\",\"allele_frequency\":0.9998844197873324,\"hemizygote_count\":\"\",\"homozygote_count\":\"4325\"}},\"ESP6500\":{\"AA\":{\"population\":\"African American\",\"type\":\"Common\",\"allele_number\":4404,\"code\":\"AA\",\"allele_count\":\"4094\",\"allele_frequency\":0.9296094459582198,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"EA\":{\"population\":\"European American\",\"type\":\"Common\",\"allele_number\":8598,\"code\":\"EA\",\"allele_count\":\"8593\",\"allele_frequency\":0.999418469411491,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"ALL\":{\"population\":\"ALL\",\"type\":\"Common\",\"allele_number\":13002,\"code\":\"All ESP6500\",\"allele_count\":\"12687\",\"allele_frequency\":0.9757729580064606,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"}}},\"breast_cancer_information_core\":{\"clinical_classification\":\"\",\"clinical_importance\":\"\",\"mutation_category_bic\":\"\",\"radar\":\"0\"},\"clinical\":{\"allele_orign\":{\"0\":\"Germline\"},\"total_rcv\":3,\"radar\":\"2\",\"hgvs\":\"NC_000013.10:g.32913055A>G\",\"variant_disease_db_name\":\"Breast-ovarian cancer,familial 2|Familial cancer of breast|Hereditary cancer-predisposing syndrome|not specified\",\"alternate_allele\":\"2\",\"database_source\":{\"database_id\":\"NBK1247:C2675520:612555:ORPHA145|NBK1247:C0346153:114480:254843006|C0027672:699346009|CN169374\",\"database\":\"GeneReviews:MedGen:OMIM:Orphanet|GeneReviews:MedGen:OMIM:SNOMED_CT|MedGen:SNOMED_CT|MedGen\"},\"review_status\":\"Reviewed by expert panel|Criteria provided single submitter|Criteria provided single submitter|Criteria provided single submitter\",\"clinical_channel\":{\"channel_id\":\"None\",\"channel\":\"None\"},\"gene_info\":{\"0\":\"BRCA2:675\"},\"classification\":\"Benign, Benign|Likely benign|Likely benign, Benign|Benign|Benign|Benign\",\"clinvar_accession\":\"RCV000113321.1, RCV000160224.1|RCV000164515.1|RCV000198429.1, RCV000119245.2|RCV000123972.2|RCV000132170.2|RCV000152875.4\"},\"BRCA_Exchange\":{\"Pathogenicity\":{\"ENIGMA\":\"Benign(ENIGMA)\",\"ClinVar\":\"Benign (ClinVar)\"},\"Patient_nationality_BIC\":\"-\",\"Gene_Symbol\":\"BRCA2\",\"ID\":\"11886\",\"Protein\":\"NP_000050.2:p.(eq)\",\"Allele_Origin_BIC\":\"-\",\"Nucleotide\":\"NM_000059.3:c.4563A>G\",\"Allele_Origin_ClinVar\":\"germline,unknown\",\"Reference_cDNA_Sequence\":\"NM_000059.3\",\"Analysis_Method_ClinVar\":\"curation,clinical,testing,literature,only\",\"Date_last_evaluated_ENIGMA\":\"1/12/15\",\"Condition_ID_Type_ENIGMA\":\"OMIM\",\"Date_last_updated_ClinVar\":\"None_2014-01-02_2014-11-19_2013-11-13_2014-10-15_2015-01-12\",\"Condition_ID_Value_ENIGMA\":\"BREAST-OVARIAN CANCER  FAMILIAL  SUSCEPTIBILITY TO  2  BROVCA2 (612555)\",\"Functional_analysis_result_LOVD\":\"-\",\"Comment_on_Clinical_significance_ENIGMA\":\"Class 1 not pathogenic based on frequency >1% in an outbred sampleset. Frequency 0.8902 (African)  derived from 1000 genomes (2012-04-30).\",\"Ethnicity_BIC\":\"-\",\"Has_Discordant_Evidence\":\"Concordant\",\"Mutation_category_BIC\":\"-\"},\"id\":2,\"experiment\":{\"experiment_user\":\"Changbum Hong\",\"specimen_type\":\"blood\",\"experiment_type\":\"germline\",\"sequencer\":\"Illumina MiSeq\"},\"total_same_panel_sample_count_in_usergroup\":0,\"in_silico_prediction\":{\"mt\":{\"text\":\"\",\"score\":\"\",\"radar\":\"\"},\"PolyPhen2\":{\"text\":\"\",\"test\":\"PolyPhen2\",\"desc\":\"dbNSFP annotation, Polyphen2 score based on HumVar\",\"radar\":\"\",\"reference range\":\"0-1\",\"type\":\"Protein effect\",\"score\":\"\"},\"SIFT\":{\"text\":\"\",\"test\":\"SIFT\",\"desc\":\"dbNSFP annotation, SIFT score (SIFTori)\",\"radar\":\"\",\"reference range\":\"0-1\",\"type\":\"Protein effect\",\"score\":\"\"}},\"korean_hereditary_breast_cancer_study\":{\"high_risk_breast_cancer_patient_in_kohbra\":\"\",\"high_risk_breast_cancer_frequency_in_kohbra\":\"\"},\"flag\":{\"is_primer\":\"no\",\"warning\":\"no\",\"primer_deletion\":\"no\",\"low_variant_fraction\":\"no\",\"homopolymer_region\":\"no\",\"is_control\":\"no\",\"roi_region\":\"yes\",\"low_coverage\":\"no\",\"low_variant_coverage_depth\":\"no\",\"low_confidence\":\"no\",\"pre_filter\":\"yes\",\"soft_clipped_amplicon\":\"no\"},\"allele\":{\"reference\":\"A\",\"type_of_allele\":\"snp\",\"alternate_allele_observation\":281,\"zygosity\":\"homozygote\",\"alternate\":\"G\",\"reference_allele_observation\":\"0\",\"allele_fraction\":100.0,\"zygosity_dp\":\"0\",\"total_read_depth\":281},\"same_panel_same_variant_sample_count_in_usergroup\":0,\"gene\":{\"entrez_gene_summary\":\"Inherited mutations in BRCA1 and this gene, BRCA2, confer increased lifetime risk of developing breast or ovarian cancer. Both BRCA1 and BRCA2 are involved in maintenance of genome stability, specifically the homolog     ous recombination pathway for double-strand DNA repair. The BRCA2 protein contains several copies of a 70 aa motif called the BRC motif, and these motifs mediate binding to the RAD51 recombinase which functions in DNA repair. BRCA2 is considered a tumor suppre     ssor gene, as tumors with BRCA2 mutations generally exhibit loss of heterozygosity (LOH) of the wild-type allele. [provided by RefSeq, Dec 2008]\",\"transcript\":{\"0\":{\"hgvs.p\":\"p.(=)\",\"transcript_name\":\"NM_000059.3\",\"coding_consequence\":\"synonymous\",\"gene_symbol\":\"BRCA2\",\"genome_37\":\"chr13:g.32913055:A>G\",\"hgvs.c\":\"c.4563A>G\",\"strand\":\"+\",\"solvebio_format\":\"NM_000059.3(BRCA2):c.4563A>G(p.(=))\",\"exon_number/total\":\"11/27\",\"aa_abbreviation\":\"\"}},\"gene_symbol\":\"BRCA2\",\"gene_name\":\"BRCA2\",\"default_transcript\":\"NM_000059.3\",\"strand\":\"+\"},\"build\":{\"ref_genome\":\"GRCh37/hg19\",\"1000_genomes\":\"20150218 phase3\",\"ESP6500\":\"ESP6500SI-V2-SSA137\",\"dbsnp_build\":\"dbSNP 147\",\"clinvar_build\":\"20160502\",\"ESP5400\":\"0.3\",\"ExAc\":\"0.31\"},\"total_sample_count_in_usergroup\":8,\"ENIGMA\":{\"has\":\"yes\",\"pathogenic\":\"Benign(ENIGMA)\",\"radar\":\"1\"},\"variant_information\":{\"exac_url\":\"http://exac.broadinstitute.org/variant/13-32913055-A-G\",\"rs_id\":\"rs206075\",\"start\":32913055,\"alt\":\"G\",\"exac_format\":\"13-32913055-A-G\",\"left_22_bp\":\"AAAAGATCAAAGAACCTACTCT\",\"vcf_format\":\"chr13 32913055.A G.\",\"ucsc_format\":\"chr13:32,913,055-32,913,055\",\"type\":\"snp\",\"right_22_bp\":\"TTGGGTTTTCATACAGCTAGCG\",\"size\":\"1bp\",\"clinvar_url\":\"http://www.ncbi.nlm.nih.gov/clinvar?term=rs206075\",\"esp_url\":\"\",\"1kg_url\":\"\",\"filter\":\"None\",\"stop\":32913055,\"brca_exchange_url\":\"http://brcaexchange.org/variants?search=chr13:g.32913055\",\"ncbi_url\":\"http://www.ncbi.nlm.nih.gov/gene/675\",\"ucsc_url\":\"http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&highlight=hg19.chr13%3A32,913,055-32,913,055&position=chr13%3A32913025-32913085\",\"ref\":\"A\"}}";
        } else if(id == 25926) {
            data = "{\"experiment\":{\"sequencer\":\"Illumina MiSeq\",\"specimen_type\":\"blood\",\"experiment_user\":\"Changbum Hong\",\"experiment_type\":\"germline\"},\"same_variant_sample_count_in_usergroup\":8,\"total_sample_count_in_usergroup\":8,\"variant_classifier\":{\"radar\":\"1\",\"grade\":\"E\",\"result\":\"benign\"},\"variant_information\":{\"type\":\"snp\",\"alt\":\"C\",\"start\":32915005,\"vcf_format\":\"chr13 32915005.G C.\",\"stop\":32915005,\"ucsc_format\":\"chr13:32,915,005-32,915,005\",\"1kg_url\":\"\",\"brca_exchange_url\":\"http://brcaexchange.org/variants?search=chr13:g.32915005\",\"rs_id\":\"rs206076\",\"size\":\"1bp\",\"esp_url\":\"\",\"ucsc_url\":\"http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&highlight=hg19.chr13%3A32,915,005-32,915,005&position=chr13%3A32914975-32915035\",\"filter\":\"None\",\"ncbi_url\":\"http://www.ncbi.nlm.nih.gov/gene/675\",\"exac_format\":\"13-32915005-G-C\",\"ref\":\"G\",\"right_22_bp\":\"TCACTTGTTGAGAACATTCATG\",\"exac_url\":\"http://exac.broadinstitute.org/variant/13-32915005-G-C\",\"clinvar_url\":\"http://www.ncbi.nlm.nih.gov/clinvar?term=rs206076\",\"left_22_bp\":\"AGTTGGTATTAGGAACCAAAGT\"},\"assay\":{\"amplicon\":\"\",\"pipeline\":\"444\",\"company\":\"NGeneBio\",\"assay_name\":\"NGB_BRCAccuTest\"},\"korean_hereditary_breast_cancer_study\":{\"high_risk_breast_cancer_patient_in_kohbra\":\"\",\"high_risk_breast_cancer_frequency_in_kohbra\":\"\"},\"breast_cancer_information_core\":{\"clinical_importance\":\"\",\"clinical_classification\":\"\",\"mutation_category_bic\":\"\",\"radar\":\"0\"},\"build\":{\"dbsnp_build\":\"dbSNP 147\",\"ESP6500\":\"ESP6500SI-V2-SSA137\",\"ExAc\":\"0.31\",\"1000_genomes\":\"20150218 phase3\",\"ref_genome\":\"GRCh37/hg19\",\"ESP5400\":\"0.3\",\"clinvar_build\":\"20160502\"},\"in_silico_prediction\":{\"SIFT\":{\"text\":\"\",\"type\":\"Protein effect\",\"test\":\"SIFT\",\"reference range\":\"0-1\",\"score\":\"\",\"radar\":\"\",\"desc\":\"dbNSFP annotation, SIFT score (SIFTori)\"},\"mt\":{\"text\":\"\",\"score\":\"\",\"radar\":\"\"},\"PolyPhen2\":{\"text\":\"\",\"type\":\"Protein effect\",\"test\":\"PolyPhen2\",\"reference range\":\"0-1\",\"score\":\"\",\"radar\":\"\",\"desc\":\"dbNSFP annotation, Polyphen2 score based on HumVar\"}},\"gene\":{\"default_transcript\":\"NM_000059.3\",\"strand\":\"+\",\"entrez_gene_summary\":\"Inherited mutations in BRCA1 and this gene, BRCA2, confer increased lifetime risk of developing breast or ovarian cancer. Both BRCA1 and BRCA2 are involved in maintenance of genome stability, specifically the homolog     ous recombination pathway for double-strand DNA repair. The BRCA2 protein contains several copies of a 70 aa motif called the BRC motif, and these motifs mediate binding to the RAD51 recombinase which functions in DNA repair. BRCA2 is considered a tumor suppre     ssor gene, as tumors with BRCA2 mutations generally exhibit loss of heterozygosity (LOH) of the wild-type allele. [provided by RefSeq, Dec 2008]\",\"gene_symbol\":\"BRCA2\",\"gene_name\":\"BRCA2\",\"transcript\":{\"0\":{\"transcript_name\":\"NM_000059.3\",\"exon_number/total\":\"11/27\",\"hgvs.c\":\"c.6513G>C\",\"strand\":\"+\",\"aa_abbreviation\":\"\",\"genome_37\":\"chr13:g.32915005:G>C\",\"coding_consequence\":\"synonymous\",\"solvebio_format\":\"NM_000059.3(BRCA2):c.6513G>C(p.(=))\",\"hgvs.p\":\"p.(=)\",\"gene_symbol\":\"BRCA2\"}}},\"BRCA_Exchange\":{\"Condition_ID_Value_ENIGMA\":\"BREAST-OVARIAN CANCER  FAMILIAL  SUSCEPTIBILITY TO  2  BROVCA2 (612555)\",\"Functional_analysis_result_LOVD\":\"-\",\"Patient_nationality_BIC\":\"-\",\"Comment_on_Clinical_significance_ENIGMA\":\"Class 1 not pathogenic based on frequency >1% in an outbred sampleset. Frequency 0.8902 (African)  derived from 1000 genomes (2012-04-30).\",\"Has_Discordant_Evidence\":\"Concordant\",\"Allele_Origin_ClinVar\":\"germline,unknown\",\"Date_last_evaluated_ENIGMA\":\"1/12/15\",\"Condition_ID_Type_ENIGMA\":\"OMIM\",\"Gene_Symbol\":\"BRCA2\",\"Allele_Origin_BIC\":\"-\",\"Mutation_category_BIC\":\"-\",\"Protein\":\"NP_000050.2:p.(eq)\",\"Nucleotide\":\"NM_000059.3:c.6513G>C\",\"ID\":\"11205\",\"Date_last_updated_ClinVar\":\"None_2014-01-02_2014-06-23_2014-11-19_2014-10-15_2015-01-12\",\"Ethnicity_BIC\":\"-\",\"Pathogenicity\":{\"ENIGMA\":\"Benign(ENIGMA)\",\"ClinVar\":\"Benign (ClinVar)\"},\"Analysis_Method_ClinVar\":\"curation,clinical,testing,literature,only\",\"Reference_cDNA_Sequence\":\"NM_000059.3\"},\"genomic_coordinate\":{\"chromosome\":\"chr13\",\"g.pos\":32915005,\"build\":\"GRCh37\"},\"clinical\":{\"gene_info\":{\"0\":\"BRCA2:675\"},\"alternate_allele\":\"2\",\"hgvs\":\"NC_000013.10:g.32915005G>T\",\"clinical_channel\":{\"channel\":\"None\",\"channel_id\":\"None\"},\"clinvar_accession\":\"RCV000113602.1, RCV000119246.2|RCV000123985.2|RCV000152878.4|RCV000162377.1, RCV000160234.1|RCV000163332.1|RCV000168590.1|RCV000173959.1|RCV000195987.1\",\"radar\":\"3\",\"database_source\":{\"database\":\"GeneReviews:MedGen:OMIM:SNOMED_CT|MedGen:SNOMED_CT|MedGen|MedGen|GeneReviews:MedGen\",\"database_id\":\"NBK1247:C0346153:114480:254843006|C0027672:699346009|CN169374|CN221809|NBK1247:C0677776\"},\"classification\":\"not provided, Benign|Benign|Benign|Benign, Benign|Likely benign|Uncertain significance|Uncertain significance|Likely benign\",\"allele_orign\":{\"0\":\"Germline\"},\"total_rcv\":3,\"review_status\":\"Criteria provided single submitter|Criteria provided single submitter|No assertion criteria provided|Criteria provided single submitter|Criteria provided single submitter\",\"variant_disease_db_name\":\"Familial cancer of breast|Hereditary cancer-predisposing syndrome|not specified|not provided|BRCA1 and BRCA2 Hereditary Breast and Ovarian Cancer\"},\"allele\":{\"type_of_allele\":\"snp\",\"zygosity_dp\":\"0\",\"total_read_depth\":419,\"alternate\":\"C\",\"alternate_allele_observation\":419,\"reference_allele_observation\":\"0\",\"allele_fraction\":100.0,\"zygosity\":\"homozygote\",\"reference\":\"G\"},\"same_panel_same_variant_sample_count_in_usergroup\":0,\"same_variant_sample_count_in_run\":1,\"id\":3,\"total_sample_count_in_run\":1,\"ENIGMA\":{\"has\":\"yes\",\"pathogenic\":\"Benign(ENIGMA)\",\"radar\":\"1\"},\"population_frequency\":{\"Korean_exome\":{\"ALL\":{\"allele_frequency\":1.0,\"allele_count\":2678,\"allele_number\":2678,\"type\":\"Common\",\"population\":\"All Korean Genomes\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"ALL\"}},\"1000_genomes\":{\"European\":{\"allele_frequency\":0.999,\"allele_count\":\"\",\"allele_number\":\"\",\"type\":\"Common\",\"population\":\"European\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"EUR\"},\"American\":{\"allele_frequency\":0.9971,\"allele_count\":\"\",\"allele_number\":\"\",\"type\":\"Common\",\"population\":\"American\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"AMR\"},\"African\":{\"allele_frequency\":0.9024,\"allele_count\":\"\",\"allele_number\":\"\",\"type\":\"Common\",\"population\":\"African\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"AFR\"},\"South_Asian\":{\"allele_frequency\":1.0,\"allele_count\":\"\",\"allele_number\":\"\",\"type\":\"Common\",\"population\":\"South Asian\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"SAS\"},\"ALL\":{\"allele_frequency\":0.973642,\"allele_count\":\"\",\"allele_number\":\"\",\"type\":\"Common\",\"population\":\"All 1000 Genomes\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"ALL\"},\"East_Asian\":{\"allele_frequency\":1.0,\"allele_count\":\"\",\"allele_number\":\"\",\"type\":\"Common\",\"population\":\"East Asian\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"EAS\"}},\"ESP6500\":{\"AA\":{\"allele_frequency\":0.9287335451656832,\"allele_count\":\"4092\",\"allele_number\":4406,\"type\":\"Common\",\"population\":\"African American\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"AA\"},\"EA\":{\"allele_frequency\":0.9994186046511628,\"allele_count\":\"8595\",\"allele_number\":8600,\"type\":\"Common\",\"population\":\"European American\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"EA\"},\"ALL\":{\"allele_frequency\":0.975472858680609,\"allele_count\":\"12687\",\"allele_number\":13006,\"type\":\"Common\",\"population\":\"ALL\",\"homozygote_count\":\"\",\"hemizygote_count\":\"\",\"code\":\"All ESP6500\"}},\"ExAC\":{\"Latino\":{\"allele_frequency\":0.9967128027681661,\"allele_count\":\"11522\",\"allele_number\":\"11560\",\"type\":\"Common\",\"population\":\"Latino\",\"homozygote_count\":\"5742\",\"hemizygote_count\":\"\",\"code\":\"AMR\"},\"African\":{\"allele_frequency\":0.9252772913018097,\"allele_count\":\"9510\",\"allele_number\":\"10278\",\"type\":\"Common\",\"population\":\"African\",\"homozygote_count\":\"4398\",\"hemizygote_count\":\"\",\"code\":\"AFR\"},\"South_Asian\":{\"allele_frequency\":0.9998168721767794,\"allele_count\":\"16379\",\"allele_number\":\"16382\",\"type\":\"Common\",\"population\":\"South Asian\",\"homozygote_count\":\"8188\",\"hemizygote_count\":\"\",\"code\":\"SAS\"},\"Europena(Non-Finnish)\":{\"allele_frequency\":0.9995195772279606,\"allele_count\":\"66576\",\"allele_number\":\"66608\",\"type\":\"Common\",\"population\":\"Europena(Non-Finnish)\",\"homozygote_count\":\"33272\",\"hemizygote_count\":\"\",\"code\":\"NFE\"},\"Europena(Finnish)\":{\"allele_frequency\":1.0,\"allele_count\":\"6610\",\"allele_number\":\"6610\",\"type\":\"Common\",\"population\":\"European(Finnish)\",\"homozygote_count\":\"3305\",\"hemizygote_count\":\"\",\"code\":\"FIN\"},\"ALL\":{\"allele_frequency\":0.9930139062808937,\"population\":\"All ExAC\",\"allele_number\":\"121384\",\"type\":\"Common\",\"allele_count\":\"120536\",\"code\":\"ALL\"},\"East_Asian\":{\"allele_frequency\":0.999884286044897,\"allele_count\":\"8641\",\"allele_number\":\"8642\",\"type\":\"Common\",\"population\":\"East Asian\",\"homozygote_count\":\"4320\",\"hemizygote_count\":\"\",\"code\":\"EAS\"},\"Other\":{\"allele_frequency\":0.9988962472406181,\"allele_count\":\"905\",\"allele_number\":\"906\",\"type\":\"Common\",\"population\":\"Other\",\"homozygote_count\":\"452\",\"hemizygote_count\":\"\",\"code\":\"OTH\"}}},\"total_same_panel_sample_count_in_usergroup\":0,\"flag\":{\"soft_clipped_amplicon\":\"no\",\"roi_region\":\"yes\",\"low_coverage\":\"no\",\"low_variant_coverage_depth\":\"no\",\"primer_deletion\":\"no\",\"is_primer\":\"no\",\"low_confidence\":\"no\",\"homopolymer_region\":\"no\",\"warning\":\"no\",\"pre_filter\":\"yes\",\"is_control\":\"no\",\"low_variant_fraction\":\"no\"}}";
        } else if (id == 25927) {
            data = "{\"flag\":{\"low_confidence\":\"no\",\"low_coverage\":\"no\",\"low_variant_coverage_depth\":\"no\",\"warning\":\"no\",\"is_control\":\"no\",\"is_primer\":\"no\",\"primer_deletion\":\"no\",\"soft_clipped_amplicon\":\"no\",\"homopolymer_region\":\"no\",\"low_variant_fraction\":\"no\",\"roi_region\":\"yes\",\"pre_filter\":\"yes\"},\"total_sample_count_in_run\":1,\"breast_cancer_information_core\":{\"clinical_classification\":\"\",\"radar\":\"0\",\"clinical_importance\":\"\",\"mutation_category_bic\":\"\"},\"same_variant_sample_count_in_run\":1,\"clinical\":{\"alternate_allele\":\"1\",\"database_source\":{\"database\":\"MedGen\",\"database_id\":\"CN169374\"},\"classification\":\"Benign|Benign|Benign, Benign\",\"radar\":\"1\",\"review_status\":\"Criteria provided single submitter\",\"variant_disease_db_name\":\"not specified\",\"allele_orign\":{\"0\":\"Germline\"},\"hgvs\":\"NC_000013.10:g.32929387T>C\",\"clinical_channel\":{\"channel\":\"HGMD\",\"channel_id\":\"CM960194\"},\"total_rcv\":2,\"gene_info\":{\"0\":\"BRCA2:675\"},\"clinvar_accession\":\"RCV000045197.3|RCV000113751.2|RCV000168597.1, RCV000120357.4\"},\"experiment\":{\"experiment_type\":\"germline\",\"specimen_type\":\"blood\",\"sequencer\":\"Illumina MiSeq\",\"experiment_user\":\"Changbum Hong\"},\"total_same_panel_sample_count_in_usergroup\":0,\"id\":5,\"population_frequency\":{\"ExAC\":{\"Europena(Finnish)\":{\"allele_count\":\"6614\",\"population\":\"European(Finnish)\",\"type\":\"Common\",\"allele_number\":\"6614\",\"code\":\"FIN\",\"allele_frequency\":1.0,\"homozygote_count\":\"3307\",\"hemizygote_count\":\"\"},\"South_Asian\":{\"allele_count\":\"16505\",\"population\":\"South Asian\",\"type\":\"Common\",\"allele_number\":\"16508\",\"code\":\"SAS\",\"allele_frequency\":0.999818269929731,\"homozygote_count\":\"8251\",\"hemizygote_count\":\"\"},\"East_Asian\":{\"allele_count\":\"8649\",\"population\":\"East Asian\",\"type\":\"Common\",\"allele_number\":\"8650\",\"code\":\"EAS\",\"allele_frequency\":0.9998843930635838,\"homozygote_count\":\"4324\",\"hemizygote_count\":\"\"},\"Latino\":{\"allele_count\":\"11537\",\"population\":\"Latino\",\"type\":\"Common\",\"allele_number\":\"11572\",\"code\":\"AMR\",\"allele_frequency\":0.996975458002074,\"homozygote_count\":\"5751\",\"hemizygote_count\":\"\"},\"Other\":{\"allele_count\":\"907\",\"population\":\"Other\",\"type\":\"Common\",\"allele_number\":\"908\",\"code\":\"OTH\",\"allele_frequency\":0.998898678414097,\"homozygote_count\":\"453\",\"hemizygote_count\":\"\"},\"Europena(Non-Finnish)\":{\"allele_count\":\"66634\",\"population\":\"Europena(Non-Finnish)\",\"type\":\"Common\",\"allele_number\":\"66660\",\"code\":\"NFE\",\"allele_frequency\":0.9996099609960996,\"homozygote_count\":\"33304\",\"hemizygote_count\":\"\"},\"ALL\":{\"allele_count\":\"120636\",\"population\":\"All ExAC\",\"type\":\"Common\",\"code\":\"ALL\",\"allele_frequency\":0.9937231255869124,\"allele_number\":\"121398\"},\"African\":{\"allele_count\":\"9696\",\"population\":\"African\",\"type\":\"Common\",\"allele_number\":\"10392\",\"code\":\"AFR\",\"allele_frequency\":0.9330254041570438,\"homozygote_count\":\"4522\",\"hemizygote_count\":\"\"}},\"Korean_exome\":{\"ALL\":{\"allele_count\":2678,\"population\":\"All Korean Genomes\",\"type\":\"Common\",\"allele_number\":2678,\"code\":\"ALL\",\"allele_frequency\":1.0,\"homozygote_count\":\"\",\"hemizygote_count\":\"\"}},\"ESP6500\":{\"AA\":{\"allele_count\":\"4119\",\"population\":\"African American\",\"type\":\"Common\",\"allele_number\":4404,\"code\":\"AA\",\"allele_frequency\":0.9352861035422343,\"homozygote_count\":\"\",\"hemizygote_count\":\"\"},\"EA\":{\"allele_count\":\"8593\",\"population\":\"European American\",\"type\":\"Common\",\"allele_number\":8598,\"code\":\"EA\",\"allele_frequency\":0.999418469411491,\"homozygote_count\":\"\",\"hemizygote_count\":\"\"},\"ALL\":{\"allele_count\":\"12712\",\"population\":\"ALL\",\"type\":\"Common\",\"allele_number\":13002,\"code\":\"All ESP6500\",\"allele_frequency\":0.9776957391170589,\"homozygote_count\":\"\",\"hemizygote_count\":\"\"}},\"1000_genomes\":{\"European\":{\"allele_count\":\"\",\"population\":\"European\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"EUR\",\"allele_frequency\":0.999,\"homozygote_count\":\"\",\"hemizygote_count\":\"\"},\"South_Asian\":{\"allele_count\":\"\",\"population\":\"South Asian\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"SAS\",\"allele_frequency\":1.0,\"homozygote_count\":\"\",\"hemizygote_count\":\"\"},\"American\":{\"allele_count\":\"\",\"population\":\"American\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"AMR\",\"allele_frequency\":0.9971,\"homozygote_count\":\"\",\"hemizygote_count\":\"\"},\"East_Asian\":{\"allele_count\":\"\",\"population\":\"East Asian\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"EAS\",\"allele_frequency\":1.0,\"homozygote_count\":\"\",\"hemizygote_count\":\"\"},\"ALL\":{\"allele_count\":\"\",\"population\":\"All 1000 Genomes\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"ALL\",\"allele_frequency\":0.975839,\"homozygote_count\":\"\",\"hemizygote_count\":\"\"},\"African\":{\"allele_count\":\"\",\"population\":\"African\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"AFR\",\"allele_frequency\":0.9107,\"homozygote_count\":\"\",\"hemizygote_count\":\"\"}}},\"variant_classifier\":{\"result\":\"benign\",\"grade\":\"E\",\"radar\":\"1\"},\"ENIGMA\":{},\"korean_hereditary_breast_cancer_study\":{\"high_risk_breast_cancer_frequency_in_kohbra\":\"\",\"high_risk_breast_cancer_patient_in_kohbra\":\"\"},\"same_panel_same_variant_sample_count_in_usergroup\":0,\"assay\":{\"amplicon\":\"\",\"pipeline\":\"444\",\"company\":\"NGeneBio\",\"assay_name\":\"NGB_BRCAccuTest\"},\"variant_information\":{\"left_22_bp\":\"AAAAACAACTCCAATCAAGCAG\",\"esp_url\":\"\",\"brca_exchange_url\":\"http://brcaexchange.org/variants?search=chr13:g.32929387\",\"ucsc_format\":\"chr13:32,929,387-32,929,387\",\"clinvar_url\":\"http://www.ncbi.nlm.nih.gov/clinvar?term=rs169547\",\"exac_url\":\"http://exac.broadinstitute.org/variant/13-32929387-T-C\",\"alt\":\"C\",\"start\":32929387,\"ncbi_url\":\"http://www.ncbi.nlm.nih.gov/gene/675\",\"ref\":\"T\",\"vcf_format\":\"chr13 32929387.T C.\",\"filter\":\"None\",\"type\":\"snp\",\"stop\":32929387,\"ucsc_url\":\"http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&highlight=hg19.chr13%3A32,929,387-32,929,387&position=chr13%3A32929357-32929417\",\"exac_format\":\"13-32929387-T-C\",\"size\":\"1bp\",\"right_22_bp\":\"AGCTGTAACTTTCACAAAGTGT\",\"1kg_url\":\"\",\"rs_id\":\"rs169547\"},\"gene\":{\"default_transcript\":\"NM_000059.3\",\"gene_symbol\":\"BRCA2\",\"entrez_gene_summary\":\"Inherited mutations in BRCA1 and this gene, BRCA2, confer increased lifetime risk of developing breast or ovarian cancer. Both BRCA1 and BRCA2 are involved in maintenance of genome stability, specifically the homolog     ous recombination pathway for double-strand DNA repair. The BRCA2 protein contains several copies of a 70 aa motif called the BRC motif, and these motifs mediate binding to the RAD51 recombinase which functions in DNA repair. BRCA2 is considered a tumor suppre     ssor gene, as tumors with BRCA2 mutations generally exhibit loss of heterozygosity (LOH) of the wild-type allele. [provided by RefSeq, Dec 2008]\",\"strand\":\"+\",\"transcript\":{\"0\":{\"aa_abbreviation\":\"\",\"gene_symbol\":\"BRCA2\",\"genome_37\":\"chr13:g.32929387:T>C\",\"hgvs.c\":\"c.7397T>C\",\"hgvs.p\":\"p.(=)\",\"coding_consequence\":\"missense\",\"exon_number/total\":\"14/27\",\"transcript_name\":\"NM_000059.3\",\"strand\":\"+\",\"solvebio_format\":\"NM_000059.3(BRCA2):c.7397T>C(p.(=))\"}},\"gene_name\":\"BRCA2\"},\"same_variant_sample_count_in_usergroup\":8,\"build\":{\"dbsnp_build\":\"dbSNP 147\",\"ref_genome\":\"GRCh37/hg19\",\"1000_genomes\":\"20150218 phase3\",\"ESP5400\":\"0.3\",\"ESP6500\":\"ESP6500SI-V2-SSA137\",\"ExAc\":\"0.31\",\"clinvar_build\":\"20160502\"},\"in_silico_prediction\":{\"mt\":{\"score\":\"1.0\",\"text\":\"P:polymorphism automatic\",\"radar\":\"2\"},\"PolyPhen2\":{\"score\":\"\",\"test\":\"PolyPhen2\",\"radar\":\"\",\"reference range\":\"0-1\",\"text\":\"\",\"type\":\"Protein effect\",\"desc\":\"dbNSFP annotation, Polyphen2 score based on HumVar\"},\"SIFT\":{\"score\":\"1.0\",\"test\":\"SIFT\",\"radar\":\"1\",\"reference range\":\"0-1\",\"text\":\"tolerated\",\"type\":\"Protein effect\",\"desc\":\"dbNSFP annotation, SIFT score (SIFTori)\"}},\"allele\":{\"type_of_allele\":\"snp\",\"total_read_depth\":286,\"zygosity\":\"homozygote\",\"alternate\":\"C\",\"alternate_allele_observation\":286,\"reference_allele_observation\":\"0\",\"zygosity_dp\":\"0\",\"reference\":\"T\",\"allele_fraction\":100.0},\"BRCA_Exchange\":{\"ID\":\"9742\",\"Condition_ID_Type_ENIGMA\":\"-\",\"Date_last_updated_ClinVar\":\"2014-10-15\",\"Condition_ID_Value_ENIGMA\":\"-\",\"Analysis_Method_ClinVar\":\"clinical,testing\",\"Pathogenicity\":{\"ClinVar\":\"Benign (ClinVar)\"},\"Comment_on_Clinical_significance_ENIGMA\":\"-\",\"Functional_analysis_result_LOVD\":\"-\",\"Allele_Origin_ClinVar\":\"germline\",\"Nucleotide\":\"NM_000059.3:c.7397T>C\",\"Gene_Symbol\":\"BRCA2\",\"Mutation_category_BIC\":\"-\",\"Allele_Origin_BIC\":\"-\",\"Patient_nationality_BIC\":\"-\",\"Protein\":\"NP_000050.2:p.(eq)\",\"Ethnicity_BIC\":\"-\",\"Reference_cDNA_Sequence\":\"NM_000059.3\",\"Has_Discordant_Evidence\":\"Concordant\",\"Date_last_evaluated_ENIGMA\":\"-\"},\"genomic_coordinate\":{\"build\":\"GRCh37\",\"g.pos\":32929387,\"chromosome\":\"chr13\"},\"total_sample_count_in_usergroup\":8}";
        } else if (id == 25928) {
            data = "{\"assay\":{\"amplicon\":\"\",\"assay_name\":\"NGB_BRCAccuTest\",\"company\":\"NGeneBio\",\"pipeline\":\"444\"},\"variant_classifier\":{\"result\":\"benign\",\"grade\":\"E\",\"radar\":\"1\"},\"total_sample_count_in_run\":1,\"genomic_coordinate\":{\"chromosome\":\"chr13\",\"build\":\"GRCh37\",\"g.pos\":32929232},\"same_variant_sample_count_in_run\":1,\"same_variant_sample_count_in_usergroup\":2,\"population_frequency\":{\"Korean_exome\":{\"ALL\":{\"population\":\"All Korean Genomes\",\"type\":\"Common\",\"allele_number\":1031,\"code\":\"ALL\",\"allele_count\":2678,\"allele_frequency\":0.38499,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"}},\"1000_genomes\":{\"South_Asian\":{\"population\":\"South Asian\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"SAS\",\"allele_count\":\"\",\"allele_frequency\":0.1748,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"African\":{\"population\":\"African\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"AFR\",\"allele_count\":\"\",\"allele_frequency\":0.2073,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"European\":{\"population\":\"European\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"EUR\",\"allele_count\":\"\",\"allele_frequency\":0.2087,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"American\":{\"population\":\"American\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"AMR\",\"allele_count\":\"\",\"allele_frequency\":0.1988,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"ALL\":{\"population\":\"All 1000 Genomes\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"ALL\",\"allele_count\":\"\",\"allele_frequency\":0.232628,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"East_Asian\":{\"population\":\"East Asian\",\"type\":\"Common\",\"allele_number\":\"\",\"code\":\"EAS\",\"allele_count\":\"\",\"allele_frequency\":0.369,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"}},\"ExAC\":{\"Europena(Finnish)\":{\"population\":\"European(Finnish)\",\"type\":\"Common\",\"allele_number\":\"6612\",\"code\":\"FIN\",\"allele_count\":\"1562\",\"allele_frequency\":0.23623714458560194,\"hemizygote_count\":\"\",\"homozygote_count\":\"178\"},\"Europena(Non-Finnish)\":{\"population\":\"Europena(Non-Finnish)\",\"type\":\"Common\",\"allele_number\":\"66678\",\"code\":\"NFE\",\"allele_count\":\"14287\",\"allele_frequency\":0.2142685743423618,\"hemizygote_count\":\"\",\"homozygote_count\":\"1556\"},\"Other\":{\"population\":\"Other\",\"type\":\"Common\",\"allele_number\":\"908\",\"code\":\"OTH\",\"allele_count\":\"207\",\"allele_frequency\":0.22797356828193832,\"hemizygote_count\":\"\",\"homozygote_count\":\"22\"},\"South_Asian\":{\"population\":\"South Asian\",\"type\":\"Common\",\"allele_number\":\"16504\",\"code\":\"SAS\",\"allele_count\":\"3566\",\"allele_frequency\":0.2160688317983519,\"hemizygote_count\":\"\",\"homozygote_count\":\"388\"},\"African\":{\"population\":\"African\",\"type\":\"Common\",\"allele_number\":\"10392\",\"code\":\"AFR\",\"allele_count\":\"2199\",\"allele_frequency\":0.21160508083140878,\"hemizygote_count\":\"\",\"homozygote_count\":\"231\"},\"Latino\":{\"population\":\"Latino\",\"type\":\"Common\",\"allele_number\":\"11556\",\"code\":\"AMR\",\"allele_count\":\"2075\",\"allele_frequency\":0.17956040152301836,\"hemizygote_count\":\"\",\"homozygote_count\":\"188\"},\"ALL\":{\"population\":\"All ExAC\",\"type\":\"Common\",\"allele_number\":\"121408\",\"code\":\"ALL\",\"allele_count\":\"27233\",\"allele_frequency\":0.22430976541908276},\"East_Asian\":{\"population\":\"East Asian\",\"type\":\"Common\",\"allele_number\":\"8638\",\"code\":\"EAS\",\"allele_count\":\"3323\",\"allele_frequency\":0.384695531373003,\"hemizygote_count\":\"\",\"homozygote_count\":\"665\"}},\"ESP6500\":{\"AA\":{\"population\":\"African American\",\"type\":\"Common\",\"allele_number\":4406,\"code\":\"AA\",\"allele_count\":\"915\",\"allele_frequency\":0.20767135724012709,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"EA\":{\"population\":\"European American\",\"type\":\"Common\",\"allele_number\":8600,\"code\":\"EA\",\"allele_count\":\"1834\",\"allele_frequency\":0.21325581395348836,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"},\"ALL\":{\"population\":\"ALL\",\"type\":\"Common\",\"allele_number\":13006,\"code\":\"All ESP6500\",\"allele_count\":\"2749\",\"allele_frequency\":0.21136398585268337,\"hemizygote_count\":\"\",\"homozygote_count\":\"\"}}},\"breast_cancer_information_core\":{\"clinical_classification\":\"Likely not pathogenic/little clinical significance\",\"bic_designation\":\"7470A>G\",\"nt\":\"7470\",\"mutation_category_bic\":\"Synonymous(Syn)\",\"clinical_importance\":\"no\",\"radar\":\"2\",\"exon\":\"14\"},\"clinical\":{\"allele_orign\":{\"0\":\"Germline\"},\"total_rcv\":1,\"radar\":\"1\",\"hgvs\":\"NC_000013.10:g.32929232A>G\",\"variant_disease_db_name\":\"Breast-ovarian cancer,familial 2|Familial cancer of breast|Hereditary cancer-predisposing syndrome|not specified\",\"alternate_allele\":\"1\",\"database_source\":{\"database_id\":\"NBK1247:C2675520:612555:ORPHA145|NBK1247:C0346153:114480:254843006|C0027672:699346009|CN169374\",\"database\":\"GeneReviews:MedGen:OMIM:Orphanet|GeneReviews:MedGen:OMIM:SNOMED_CT|MedGen:SNOMED_CT|MedGen\"},\"review_status\":\"Reviewed by expert panel|Criteria provided single submitter|Criteria provided single submitter|Criteria provided single submitter\",\"clinical_channel\":{\"channel_id\":\"7470&base_change=A_to_G\",\"channel\":\"Breast_Cancer_Information_Core_(BRCA2)\"},\"gene_info\":{\"0\":\"BRCA2:675\"},\"classification\":\"Benign|Benign|Benign|Benign\",\"clinvar_accession\":\"RCV000113739.4|RCV000123992.2|RCV000130994.2|RCV000152882.3\"},\"BRCA_Exchange\":{\"Pathogenicity\":{\"BIC\":\"Class 1 (BIC)\",\"ClinVar\":\"Benign (ClinVar)\",\"ENIGMA\":\"Benign(ENIGMA)\"},\"Patient_nationality_BIC\":\"USA German Sri Lankan - Austria Spanish Polish Omanis Belgian\",\"Gene_Symbol\":\"BRCA2\",\"ID\":\"5758\",\"Protein\":\"NP_000050.2:p.(eq)\",\"Allele_Origin_BIC\":\"G\",\"Nucleotide\":\"NM_000059.3:c.7242A>G\",\"Allele_Origin_ClinVar\":\"germline,unknown\",\"Reference_cDNA_Sequence\":\"NM_000059.3\",\"Analysis_Method_ClinVar\":\"curation,clinical,testing,literature,only\",\"Date_last_evaluated_ENIGMA\":\"1/12/15\",\"Condition_ID_Type_ENIGMA\":\"OMIM\",\"Date_last_updated_ClinVar\":\"2014-12-08_2014-01-02_2014-11-19_2011-03-17_2002-05-29_2015-01-12_2013-09-04\",\"Condition_ID_Value_ENIGMA\":\"BREAST-OVARIAN CANCER  FAMILIAL  SUSCEPTIBILITY TO  2  BROVCA2 (612555)\",\"Functional_analysis_result_LOVD\":\"predicted_neutral_-_?\",\"Comment_on_Clinical_significance_ENIGMA\":\"Class 1 not pathogenic based on frequency >1% in an outbred sampleset. Frequency 0.3864 (Asian)  0.1585 (African)  0.219 (European)  derived from 1000 genomes (2012-04-30).\",\"Ethnicity_BIC\":\"Global,-,Arab,Sinhalese,Polish,Caucasian\",\"Has_Discordant_Evidence\":\"Concordant\",\"Mutation_category_BIC\":\"Syn\"},\"id\":4,\"experiment\":{\"experiment_user\":\"Changbum Hong\",\"specimen_type\":\"blood\",\"experiment_type\":\"germline\",\"sequencer\":\"Illumina MiSeq\"},\"total_same_panel_sample_count_in_usergroup\":0,\"in_silico_prediction\":{\"mt\":{\"text\":\"\",\"score\":\"\",\"radar\":\"\"},\"PolyPhen2\":{\"text\":\"\",\"test\":\"PolyPhen2\",\"desc\":\"dbNSFP annotation, Polyphen2 score based on HumVar\",\"radar\":\"\",\"reference range\":\"0-1\",\"type\":\"Protein effect\",\"score\":\"\"},\"SIFT\":{\"text\":\"\",\"test\":\"SIFT\",\"desc\":\"dbNSFP annotation, SIFT score (SIFTori)\",\"radar\":\"\",\"reference range\":\"0-1\",\"type\":\"Protein effect\",\"score\":\"\"}},\"korean_hereditary_breast_cancer_study\":{\"high_risk_breast_cancer_patient_in_kohbra\":\"\",\"high_risk_breast_cancer_frequency_in_kohbra\":\"\"},\"flag\":{\"is_primer\":\"no\",\"warning\":\"no\",\"primer_deletion\":\"no\",\"low_variant_fraction\":\"no\",\"homopolymer_region\":\"no\",\"is_control\":\"no\",\"roi_region\":\"yes\",\"low_coverage\":\"no\",\"low_variant_coverage_depth\":\"no\",\"low_confidence\":\"no\",\"pre_filter\":\"no\",\"soft_clipped_amplicon\":\"no\"},\"allele\":{\"reference\":\"A\",\"type_of_allele\":\"snp\",\"alternate_allele_observation\":768,\"zygosity\":\"homozygote\",\"alternate\":\"G\",\"reference_allele_observation\":\"0\",\"allele_fraction\":100.0,\"zygosity_dp\":\"0\",\"total_read_depth\":768},\"same_panel_same_variant_sample_count_in_usergroup\":0,\"gene\":{\"entrez_gene_summary\":\"Inherited mutations in BRCA1 and this gene, BRCA2, confer increased lifetime risk of developing breast or ovarian cancer. Both BRCA1 and BRCA2 are involved in maintenance of genome stability, specifically the homolog     ous recombination pathway for double-strand DNA repair. The BRCA2 protein contains several copies of a 70 aa motif called the BRC motif, and these motifs mediate binding to the RAD51 recombinase which functions in DNA repair. BRCA2 is considered a tumor suppre     ssor gene, as tumors with BRCA2 mutations generally exhibit loss of heterozygosity (LOH) of the wild-type allele. [provided by RefSeq, Dec 2008]\",\"transcript\":{\"0\":{\"hgvs.p\":\"p.(=)\",\"transcript_name\":\"NM_000059.3\",\"coding_consequence\":\"synonymous\",\"gene_symbol\":\"BRCA2\",\"genome_37\":\"chr13:g.32929232:A>G\",\"hgvs.c\":\"c.7242A>G\",\"strand\":\"+\",\"solvebio_format\":\"NM_000059.3(BRCA2):c.7242A>G(p.(=))\",\"exon_number/total\":\"14/27\",\"aa_abbreviation\":\"\"}},\"gene_symbol\":\"BRCA2\",\"gene_name\":\"BRCA2\",\"default_transcript\":\"NM_000059.3\",\"strand\":\"+\"},\"build\":{\"ref_genome\":\"GRCh37/hg19\",\"1000_genomes\":\"20150218 phase3\",\"ESP6500\":\"ESP6500SI-V2-SSA137\",\"dbsnp_build\":\"dbSNP 147\",\"clinvar_build\":\"20160502\",\"ESP5400\":\"0.3\",\"ExAc\":\"0.31\"},\"total_sample_count_in_usergroup\":8,\"ENIGMA\":{\"has\":\"yes\",\"pathogenic\":\"Benign(ENIGMA)\",\"radar\":\"1\"},\"variant_information\":{\"exac_url\":\"http://exac.broadinstitute.org/variant/13-32929232-A-G\",\"rs_id\":\"rs1799955\",\"start\":32929232,\"alt\":\"G\",\"exac_format\":\"13-32929232-A-G\",\"left_22_bp\":\"TTCCACCTTTTAAAACTAAATC\",\"vcf_format\":\"chr13 32929232.A G.\",\"ucsc_format\":\"chr13:32,929,232-32,929,232\",\"type\":\"snp\",\"right_22_bp\":\"CATTTTCACAGAGTTGAACAGT\",\"size\":\"1bp\",\"clinvar_url\":\"http://www.ncbi.nlm.nih.gov/clinvar?term=rs1799955\",\"esp_url\":\"\",\"1kg_url\":\"\",\"filter\":\"None\",\"stop\":32929232,\"brca_exchange_url\":\"http://brcaexchange.org/variants?search=chr13:g.32929232\",\"ncbi_url\":\"http://www.ncbi.nlm.nih.gov/gene/675\",\"ucsc_url\":\"http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&highlight=hg19.chr13%3A32,929,232-32,929,232&position=chr13%3A32929202-32929262\",\"ref\":\"A\"}}";
        }
        return data;
    }

}
