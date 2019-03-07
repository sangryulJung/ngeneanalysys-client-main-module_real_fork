package ngeneanalysys.controller.fragment;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.AnalysisTypeCode;
import ngeneanalysys.code.enums.PipelineCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.service.ALAMUTService;
import ngeneanalysys.service.IGVService;
import ngeneanalysys.util.DialogUtil;
import ngeneanalysys.util.JsonUtil;
import ngeneanalysys.util.LoggerUtil;
import ngeneanalysys.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jang
 * @since 2018-04-10
 */
public class DetailSubInfoController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();
    @FXML
    private VBox mainVBox;

    @FXML
    private GridPane dbLinkGridPane;

    private Panel panel;

    private SampleView sample;

    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;

    /** IGV 연동 서비스 */
    private IGVService igvService;

    /** Alamut 연동 서비스 */
    private ALAMUTService alamutService;

    private AnalysisDetailVariantNomenclatureController analysisDetailVariantNomenclatureController;

    /**
     * @param analysisDetailVariantNomenclatureController AnalysisDetailVariantNomenclatureController
     */
    void setAnalysisDetailVariantNomenclatureController(AnalysisDetailVariantNomenclatureController analysisDetailVariantNomenclatureController) {
        this.analysisDetailVariantNomenclatureController = analysisDetailVariantNomenclatureController;
    }

    @Override
    public void show(Parent root) throws IOException {

        panel = (Panel)paramMap.get("panel");
        sample = (SampleView) paramMap.get("sampleView");

        selectedAnalysisResultVariant = (VariantAndInterpretationEvidence)paramMap.get("variant");

        igvService = IGVService.getInstance();
        igvService.setMainController(getMainController());

        // alamut service init
        alamutService = ALAMUTService.getInstance();
        alamutService.setMainController(getMainController());

        showDbLinkLisk();
        showPopulationFrequency();
    }

    @SuppressWarnings("unchecked")
    @FXML
    public void showLinkEvent(Event event) {
        ComboBox<String> ev = (ComboBox<String>)event.getSource();
        String item = ev.getSelectionModel().getSelectedItem();
        if(!StringUtils.isEmpty(item)) {
            showBrowser(item);
        }
    }

    private void showPopulationFrequency() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_POPULATION_FREQUENCIES);
            Node node = loader.load();
            AnalysisDetailPopulationFrequenciesController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.show((Parent) node);
            mainVBox.getChildren().add(0, node);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void showDbLinkLisk() {
        if(dbLinkGridPane.getChildren() != null && !dbLinkGridPane.getChildren().isEmpty() && dbLinkGridPane.getChildren().size() > 1) {
            while(dbLinkGridPane.getChildren().size() > 2) {
                dbLinkGridPane.getChildren().remove(2);
            }
        }
        double rowHeight = 20.0;
        Map<String, Object> variantInformationMap = returnResultsAfterSearch("variant_information");
        Map<String, Object> genomicCoordinateMap = returnResultsAfterSearch("genomic_coordinate");

        String rsId = selectedAnalysisResultVariant.getSnpInDel().getDbSNP().getDbSnpRsId(); // (variantInformationMap.containsKey("rs_id")) ? (String) variantInformationMap.get("rs_id") : null;
        Integer variationId = selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getClinVar().getClinVarVariationId();
        String exacFormat = (variantInformationMap.containsKey("exac_format")) ? (String) variantInformationMap.get("exac_format") : null;
        String geneId = (variantInformationMap.containsKey("geneid")) ? (String) variantInformationMap.get("geneid") : null;
        Integer start = (variantInformationMap.containsKey("start")) ? (Integer) variantInformationMap.get("start") : null;
        Integer end = (variantInformationMap.containsKey("stop")) ? (Integer) variantInformationMap.get("stop") : null;
        String chromosome = (genomicCoordinateMap != null && genomicCoordinateMap.containsKey("chromosome"))
                ? (String) genomicCoordinateMap.get("chromosome") : null;
        Integer gPos = (genomicCoordinateMap != null && genomicCoordinateMap.containsKey("g.pos"))
                ? (Integer) genomicCoordinateMap.get("g.pos") : null;

        String cursorHand = "cursor_hand";
        String titleCss = "title2";

        if (variationId != null) {
            dbLinkGridPane.getRowConstraints().add(new RowConstraints(rowHeight,rowHeight, rowHeight));
            Label dbContentLabel = createLinkLabel("ClinVar(" + variationId + ")", "ClinVar");
            dbContentLabel.getStyleClass().addAll(titleCss, cursorHand);
            dbLinkGridPane.add(dbContentLabel, 0, dbLinkGridPane.getRowConstraints().size() - 1, 1, 1);
        }

        if (!StringUtils.isEmpty(rsId)) {
            dbLinkGridPane.getRowConstraints().add(new RowConstraints(rowHeight,rowHeight, rowHeight));
            Label dbContentLabel = createLinkLabel("dbSNP(" + rsId + ")", "dbSNP");
            dbContentLabel.getStyleClass().addAll(titleCss, cursorHand);
            dbLinkGridPane.add(dbContentLabel, 0, dbLinkGridPane.getRowConstraints().size() - 1, 1, 1);
        }

        if (selectedAnalysisResultVariant.getSnpInDel().getPopulationFrequency().getG1000().getAll() != null &&
                !StringUtils.isEmpty(rsId)) {
            dbLinkGridPane.getRowConstraints().add(new RowConstraints(rowHeight,rowHeight, rowHeight));
            Label dbContentLabel = createLinkLabel("1000G(" + rsId + ")", "1000G");
            dbContentLabel.getStyleClass().addAll(titleCss, cursorHand);
            dbLinkGridPane.add(dbContentLabel, 0, dbLinkGridPane.getRowConstraints().size() - 1, 1, 1);
        }

        if (selectedAnalysisResultVariant.getSnpInDel().getPopulationFrequency().getKoreanExomInformationDatabase() != null &&
                !StringUtils.isEmpty(rsId)) {
            dbLinkGridPane.getRowConstraints().add(new RowConstraints(rowHeight,rowHeight, rowHeight));
            Label dbContentLabel = createLinkLabel("KoEXID(" + rsId + ")", "KoEXID");
            dbContentLabel.getStyleClass().addAll(titleCss, cursorHand);
            dbLinkGridPane.add(dbContentLabel, 0, dbLinkGridPane.getRowConstraints().size() - 1, 1, 1);
        }

        if (!StringUtils.isEmpty(geneId)) {
            dbLinkGridPane.getRowConstraints().add(new RowConstraints(rowHeight,rowHeight, rowHeight));
            Label dbContentLabel = createLinkLabel("NCBI(" + geneId + ")", "NCBI");
            dbContentLabel.getStyleClass().addAll(titleCss, cursorHand);
            dbLinkGridPane.add(dbContentLabel, 0, dbLinkGridPane.getRowConstraints().size() - 1, 1, 1);
        }
        if (selectedAnalysisResultVariant.getSnpInDel().getPopulationFrequency().getExac() != null &&
                !StringUtils.isEmpty(exacFormat)) {
            dbLinkGridPane.getRowConstraints().add(new RowConstraints(rowHeight,rowHeight, rowHeight));
            Label dbContentLabel = createLinkLabel("ExAC(" + exacFormat + ")", "ExAC");
            dbContentLabel.getStyleClass().addAll(titleCss, cursorHand);
            dbLinkGridPane.add(dbContentLabel, 0, dbLinkGridPane.getRowConstraints().size() - 1, 1, 1);
        }
        if (selectedAnalysisResultVariant.getSnpInDel().getPopulationFrequency().getGnomAD().getAll() != null &&
                !StringUtils.isEmpty(exacFormat)) {
            dbLinkGridPane.getRowConstraints().add(new RowConstraints(rowHeight,rowHeight, rowHeight));
            Label dbContentLabel = createLinkLabel("gnomAD(" + exacFormat + ")", "gnomAD");
            dbContentLabel.getStyleClass().addAll(titleCss, cursorHand);
            dbLinkGridPane.add(dbContentLabel, 0, dbLinkGridPane.getRowConstraints().size() - 1, 1, 1);
        }

        if (start != null && end != null) {
            dbLinkGridPane.getRowConstraints().add(new RowConstraints(rowHeight,rowHeight, rowHeight));
            Label dbContentLabel = createLinkLabel("UCSC(" + start + "-" + end + ")", "UCSC");
            dbContentLabel.getStyleClass().addAll(titleCss, cursorHand);
            dbLinkGridPane.add(dbContentLabel, 0, dbLinkGridPane.getRowConstraints().size() - 1, 1, 1);
        }

        if(AnalysisTypeCode.SOMATIC.getDescription().equals(panel.getAnalysisType())) {
            if (!StringUtils.isEmpty(selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getCosmic().getCosmicIds())) {
                dbLinkGridPane.getRowConstraints().add(new RowConstraints(rowHeight,rowHeight, rowHeight));
                Label dbContentLabel = createLinkLabel("COSMIC", "COSMIC");
                dbContentLabel.getStyleClass().addAll(titleCss, cursorHand);
                dbLinkGridPane.add(dbContentLabel, 0, dbLinkGridPane.getRowConstraints().size() - 1, 1, 1);
            }

        } else if(panel.getAnalysisType().equalsIgnoreCase("GERMLINE")) {
            if (!StringUtils.isEmpty(chromosome) && gPos != null
                    && !PipelineCode.isHeredPipeline(panel.getCode())) {
                dbLinkGridPane.getRowConstraints().add(new RowConstraints(rowHeight,rowHeight, rowHeight));
                Label dbContentLabel = createLinkLabel("BRCA Exchange", "BRCA Exchange");
                dbContentLabel.getStyleClass().addAll(titleCss, cursorHand);
                dbLinkGridPane.add(dbContentLabel, 0, dbLinkGridPane.getRowConstraints().size() - 1, 1, 1);
            }
            Map<String, Object> geneMap = returnResultsAfterSearch("gene");
            if (geneMap != null && !geneMap.isEmpty() && geneMap.containsKey("transcript")) {
                Map<String, Map<String, String>> transcriptDataMap = (Map<String, Map<String, String>>) geneMap.get("transcript");
                if (!transcriptDataMap.isEmpty()) {
                    dbLinkGridPane.getRowConstraints().add(new RowConstraints(rowHeight,rowHeight, rowHeight));
                    Label dbContentLabel = createLinkLabel("ALAMUT", "ALAMUT");
                    dbContentLabel.getStyleClass().addAll(titleCss, cursorHand);
                    dbLinkGridPane.add(dbContentLabel, 0, dbLinkGridPane.getRowConstraints().size() - 1, 1, 1);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void showBrowser(String item) {
        Map<String, Object> variantInformationMap = returnResultsAfterSearch("variant_information");
        Map<String, Object> genomicCoordinateMap = returnResultsAfterSearch("genomic_coordinate");
        if(variantInformationMap == null || genomicCoordinateMap == null) return;
        if("dbSNP".equalsIgnoreCase(item)) {
            String rsId = (variantInformationMap.containsKey("rs_id")) ? (String) variantInformationMap.get("rs_id") : null;
            if(StringUtils.isNotEmpty(rsId)) {
                String fullUrlDBsnp = "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + rsId.replaceAll("rs", "");
                openBrowser(fullUrlDBsnp);
            }
        } else if("ClinVar".equalsIgnoreCase(item)) {
            String variationId = (variantInformationMap.containsKey("variation_id")) ? (String) variantInformationMap.get("variation_id") : null;
            String fullUrlClinVar = "https://www.ncbi.nlm.nih.gov/clinvar/variation/" + variationId + "/";
            openBrowser(fullUrlClinVar);
        } else if("1000G".equalsIgnoreCase(item)) {
            String rsId = (variantInformationMap.containsKey("rs_id")) ? (String) variantInformationMap.get("rs_id") : null;
            String fullUrl1000G = "http://grch37.ensembl.org/Homo_sapiens/Variation/Population?db=core;v="
                    + rsId + ";vdb=variation";
            openBrowser(fullUrl1000G);
        } else if("KoEXID".equalsIgnoreCase(item)) {
            String rsId = (variantInformationMap.containsKey("rs_id")) ? (String) variantInformationMap.get("rs_id") : null;
            String fullUrlKoKEXID = "http://koex.snu.ac.kr/koex_main.php?section=search&db_code=15&keyword_class=varid&search_keyword="
                    + rsId;
            openBrowser(fullUrlKoKEXID);
        } else if("NCBI".equalsIgnoreCase(item)) {
            String geneId = (variantInformationMap.containsKey("geneid")) ? (String) variantInformationMap.get("geneid") : null;
            String fullUrlNCBI = "http://www.ncbi.nlm.nih.gov/gene/" + geneId;
            openBrowser(fullUrlNCBI);
        } else if("ExAC".equalsIgnoreCase(item)) {
            String exacFormat = (variantInformationMap.containsKey("exac_format")) ? (String) variantInformationMap.get("exac_format") : null;
            String fullUrlExAC = "http://exac.broadinstitute.org/variant/" + exacFormat;
            openBrowser(fullUrlExAC);
        } else if("gnomAD".equalsIgnoreCase(item)) {
            String exacFormat = (variantInformationMap.containsKey("exac_format")) ? (String) variantInformationMap.get("exac_format") : null;
            String fullUrlExAC = "http://gnomad.broadinstitute.org/variant/"
                    + exacFormat;
            openBrowser(fullUrlExAC);
        } else if("UCSC".equalsIgnoreCase(item)) {
            Integer start = (variantInformationMap.containsKey("start")) ? (Integer) variantInformationMap.get("start") : null;
            Integer end = (variantInformationMap.containsKey("stop")) ? (Integer) variantInformationMap.get("stop") : null;
            StringBuilder insertStart = new StringBuilder(start != null ? start.toString() : "");
            StringBuilder insertEnd = new StringBuilder(end != null ? end.toString() : "");
            int startLength = insertStart.length();
            int endLength = insertEnd.length();
            for (int i = 1; i < startLength; i++) {
                if (i % 3 == 0) insertStart.insert(startLength - i, ",");
            }
            for (int i = 1; i < endLength; i++) {
                if (i % 3 == 0) insertEnd.insert(endLength - i, ",");
            }
            if(start == null) start = 0;
            if(end == null) end = 0;
            Integer startMinus = start - 30;
            Integer endPlus = end + 30;
            String fullUrlUCSC = "http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&highlight=hg19."
                    + selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome() + "%3A"
                    + insertStart + "-"
                    + insertEnd + "&position="
                    + selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome() + "%3A"
                    + startMinus + "-"
                    + endPlus;

            openBrowser(fullUrlUCSC);
        } else if(AnalysisTypeCode.SOMATIC.getDescription().equals(panel.getAnalysisType())) {
            if("COSMIC".equalsIgnoreCase(item)) {
                String cosmicId = selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getCosmic().getCosmicIds().replaceAll("COSM", "");
                if (cosmicId.contains("|")) {
                    String[] ids = cosmicId.split("\\|");

                    boolean first = true;
                    for (String cosmic : ids) {
                        String fullUrlCOSMIC = "http://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=37&id=" + cosmic;
                        openBrowser(fullUrlCOSMIC);
                        try {
                            if (first) {
                                Thread.sleep(1200);
                                first = false;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    String fullUrlCOSMIC = "http://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=37&id=" + cosmicId;
                    openBrowser(fullUrlCOSMIC);
                }
            }
        } else if(panel.getAnalysisType().equalsIgnoreCase("GERMLINE")) {
            if("BRCA Exchange".equalsIgnoreCase(item)) {
                String chromosome = (genomicCoordinateMap.containsKey("chromosome"))
                        ? (String) genomicCoordinateMap.get("chromosome") : null;
                Integer gPos = (genomicCoordinateMap.containsKey("g.pos"))
                        ? (Integer) genomicCoordinateMap.get("g.pos") : null;

                String urlBRCAExchange = "http://brcaexchange.org/variants?search=" + chromosome + ":g." + gPos;

                openBrowser(urlBRCAExchange);
            } else if("ALAMUT".equalsIgnoreCase(item)) {
                Map<String, Object> geneMap = returnResultsAfterSearch("gene");
                Map<String, Map<String, String>> transcriptDataMap = (Map<String, Map<String, String>>) geneMap.get("transcript");
                if (transcriptDataMap != null && !transcriptDataMap.isEmpty()) {
                    int selectedIdx = this.analysisDetailVariantNomenclatureController.getTranscriptComboBoxSelectedIndex();
                    logger.debug(String.format("selected transcript combobox idx : %s", selectedIdx));
                    Map<String, String> map = transcriptDataMap.get(String.valueOf(selectedIdx));
                    if (!map.isEmpty()) {
                        String transcript = map.get("transcript_name");
                        String cDNA = map.get("hgvs.c");
                        String sampleId = sample.getId().toString();
                        String bamFileName = sample.getName() + "_final.bam";
                        loadAlamut(transcript, cDNA, sampleId, bamFileName);
                    }
                }
            }
        }
    }

    private void openBrowser(String url) {
        if (System.getProperty("os.name").toLowerCase().contains("window")) {
            getMainApp().getHostServices().showDocument(url);
        } else {
            String value = "open " + url;
            try {
                Process child = Runtime.getRuntime().exec(value);
            } catch (IOException e) {
                logger.debug(e.getMessage());
            }
        }
    }

    @FXML
    public void showIGV() {
        String sampleId = sample.getId().toString();
        String gene = selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getGene();
        String locus = String.format("%s:%,d-%,d",
                selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome(),
                selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getStartPosition(),
                selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getStartPosition());
        String refGenome = selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getRefGenomeVer();
        String humanGenomeVersion = (refGenome.contains("hg19")) ? "hg19" : "hg18";

        try {
            loadIGV(sampleId, sample.getName(), gene, locus, humanGenomeVersion);
        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            DialogUtil.generalShow(Alert.AlertType.ERROR, "IGV software doesn't launch.", e.getMessage(),
                    getMainApp().getPrimaryStage(), true);
        }

    }

    private Label createLinkLabel(final String title, final String link) {
        Label label = new Label();
        label.setText(title);
        label.getStyleClass().add("title1");
        label.setCursor(Cursor.HAND);
        label.setOnMouseClicked(event -> showBrowser(link));

        return label;
    }


    /**
     * IGV 실행 및 데이터 로드
     * @param sampleId String
     * @param sampleName String
     * @param gene String
     * @param locus String
     * @param genome String
     */
    private void loadIGV(String sampleId, String sampleName, String gene, String locus, String genome) throws Exception {
        igvService.load(sampleId, sampleName, gene, locus, genome);
    }

    /**
     * Alamut 연동
     * @param transcript String
     * @param cDNA String
     * @param sampleId String
     * @param bamFileName String
     */
    private void loadAlamut(String transcript, String cDNA, String sampleId, String bamFileName) {
        alamutService.call(transcript, cDNA, sampleId, bamFileName);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> returnResultsAfterSearch(String key) {
        List<SnpInDelExtraInfo> detail = (List<SnpInDelExtraInfo>)paramMap.get("detail");

        if(detail != null && !detail.isEmpty()) {
            Optional<SnpInDelExtraInfo> populationFrequency = detail.stream().filter(item
                    -> key.equalsIgnoreCase(item.key)).findFirst();

            if (populationFrequency.isPresent()) {
                return JsonUtil.fromJsonToMap(populationFrequency.get().value);
            }
        }

        return null;
    }

}