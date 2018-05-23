package ngeneanalysys.controller.fragment;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.Panel;
import ngeneanalysys.model.Sample;
import ngeneanalysys.model.SnpInDelExtraInfo;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
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
    private ComboBox<String> externalLinkComboBox;

    private Panel panel;

    private Sample sample;

    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;

    /** IGV 연동 서비스 */
    private IGVService igvService;

    /** Alamut 연동 서비스 */
    private ALAMUTService alamutService;

    private AnalysisDetailVariantNomenclatureController analysisDetailVariantNomenclatureController;

    /**
     * @param analysisDetailVariantNomenclatureController
     */
    public void setAnalysisDetailVariantNomenclatureController(AnalysisDetailVariantNomenclatureController analysisDetailVariantNomenclatureController) {
        this.analysisDetailVariantNomenclatureController = analysisDetailVariantNomenclatureController;
    }

    @Override
    public void show(Parent root) throws IOException {

        panel = (Panel)paramMap.get("panel");
        sample = (Sample) paramMap.get("sample");

        selectedAnalysisResultVariant = (VariantAndInterpretationEvidence)paramMap.get("variant");

        igvService = IGVService.getInstance();
        igvService.setMainController(getMainController());

        // alamut service init
        alamutService = ALAMUTService.getInstance();
        alamutService.setMainController(getMainController());

        setComboBox();
        showPopulationFrequency();

        if(panel.getAnalysisType().equalsIgnoreCase("GERMLINE")) {
            showInSilicoPredictions();
        }

    }

    public void showInSilicoPredictions() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_IN_SILICO_PREDICTIONS);
            Node node = loader.load();
            InSilicoPredictionsController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.show((Parent) node);
            mainVBox.getChildren().add(0, node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showLinkEvent(Event event) {
        ComboBox<String> ev = (ComboBox<String>)event.getSource();
        String item = ev.getSelectionModel().getSelectedItem();
        if(!StringUtils.isEmpty(item)) {
            showBrowser(item);
        }
    }

    public void showPopulationFrequency() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_POPULATION_FREQUENCIES);
            Node node = loader.load();
            AnalysisDetailPopulationFrequenciesController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.show((Parent) node);
            mainVBox.getChildren().add(0, node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setComboBox() {

        if(externalLinkComboBox.getItems() != null &&
                !externalLinkComboBox.getItems().isEmpty()) externalLinkComboBox.getItems().removeAll(externalLinkComboBox.getItems());

        // String[] germlineLink = {"exAC", "brcaExchange", "ncbi", "ucsc", "alamut"};
        // String[] somaticLink = {"dbSNP", "clinvar", "cosmic", "ncbi", "gnomes", "exAC", "gnomAD", "koEXID", "oncoKB", "ucsc"};
        Map<String, Object> variantInformationMap = returnResultsAfterSearch("variant_information");
        if(panel.getAnalysisType().equalsIgnoreCase("SOMATIC")) {
            String rsId = (variantInformationMap.containsKey("rs_id")) ? (String) variantInformationMap.get("rs_id") : null;
            String exacFormat = (variantInformationMap.containsKey("exac_url")) ? (String) variantInformationMap.get("exac_format") : null;
            String geneId = (variantInformationMap.containsKey("geneid")) ? (String) variantInformationMap.get("geneid") : null;
            Integer start = (variantInformationMap.containsKey("start")) ? (Integer) variantInformationMap.get("start") : null;
            Integer end = (variantInformationMap.containsKey("stop")) ? (Integer) variantInformationMap.get("stop") : null;

            if (!StringUtils.isEmpty(rsId)) {
                externalLinkComboBox.getItems().addAll("dbSNP", "ClinVar", "1000G", "KoEXID");
            }
            if (!StringUtils.isEmpty(geneId)) {
                externalLinkComboBox.getItems().addAll("NCBI");
            }
            if (!StringUtils.isEmpty(exacFormat)) {
                externalLinkComboBox.getItems().addAll("ExAC", "gnomAD");
            }
            if (selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getOncoKB() != null &&
                    !StringUtils.isEmpty(selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getOncoKB().getOncokbHgvsp())) {
                externalLinkComboBox.getItems().addAll("OncoKB");
            }
            if (!StringUtils.isEmpty(selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getCosmic().getCosmicIds())) {
                externalLinkComboBox.getItems().addAll("COSMIC");
            }
            if (start != null && end != null) {
                externalLinkComboBox.getItems().addAll("UCSC");
            }

        } else if(panel.getAnalysisType().equalsIgnoreCase("GERMLINE")) {
            String urlExAC = (variantInformationMap.containsKey("exac_url")) ? (String) variantInformationMap.get("exac_url") : null;
            String urlBRCAExchange = (variantInformationMap.containsKey("brca_exchange_url")) ? (String) variantInformationMap.get("brca_exchange_url") : null;
            String urlClinvar = (variantInformationMap.containsKey("clinvar_url")) ? (String) variantInformationMap.get("clinvar_url") : null;
            String urlNCBI = (variantInformationMap.containsKey("ncbi_url")) ? (String) variantInformationMap.get("ncbi_url") : null;
            String urlUCSC = (variantInformationMap.containsKey("ucsc_url")) ? (String) variantInformationMap.get("ucsc_url") : null;

            if (!StringUtils.isEmpty(urlExAC)) {
                externalLinkComboBox.getItems().add("ExAC");
            }
            if (!StringUtils.isEmpty(urlBRCAExchange)) {
                externalLinkComboBox.getItems().add("BRCA Exchange");
            }
            if (!StringUtils.isEmpty(urlClinvar)) {
                externalLinkComboBox.getItems().add("ClinVar");
            }
            if (!StringUtils.isEmpty(urlNCBI)) {
                externalLinkComboBox.getItems().add("NCBI");
            }
            if (!StringUtils.isEmpty(urlUCSC)) {
                externalLinkComboBox.getItems().add("UCSC");
            }
            Map<String, Object> geneMap = returnResultsAfterSearch("gene");
            if (geneMap != null && !geneMap.isEmpty() && geneMap.containsKey("transcript")) {
                Map<String, Map<String, String>> transcriptDataMap = (Map<String, Map<String, String>>) geneMap.get("transcript");
                if (!transcriptDataMap.isEmpty() && transcriptDataMap.size() > 0) {
                    externalLinkComboBox.getItems().add("ALAMUT");
                }
            }
        }

    }

    public void showBrowser(String item) {
        Map<String, Object> variantInformationMap = returnResultsAfterSearch("variant_information");
        if(panel.getAnalysisType().equalsIgnoreCase("SOMATIC")) {
            if("dbSNP".equalsIgnoreCase(item)) {
                String rsId = (variantInformationMap.containsKey("rs_id")) ? (String) variantInformationMap.get("rs_id") : null;
                String fullUrlDBsnp = "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + rsId.replaceAll("rs", "");
                openBrowser(fullUrlDBsnp);
            } else if("ClinVar".equalsIgnoreCase(item)) {
                String rsId = (variantInformationMap.containsKey("rs_id")) ? (String) variantInformationMap.get("rs_id") : null;
                String fullUrlClinvar = "http://www.ncbi.nlm.nih.gov/clinvar?term=" + rsId;
                openBrowser(fullUrlClinvar);
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
                String exacFormat = (variantInformationMap.containsKey("exac_url")) ? (String) variantInformationMap.get("exac_format") : null;
                String fullUrlExAC = "http://exac.broadinstitute.org/variant/"
                        + exacFormat;
                openBrowser(fullUrlExAC);
            } else if("gnomAD".equalsIgnoreCase(item)) {
                String exacFormat = (variantInformationMap.containsKey("exac_url")) ? (String) variantInformationMap.get("exac_format") : null;
                String fullUrlExAC = "http://gnomad.broadinstitute.org/variant/"
                        + exacFormat;
                openBrowser(fullUrlExAC);
            } else if("OncoKB".equalsIgnoreCase(item)) {
                String fullUrlOncoKB = "http://oncokb.org/#/gene/"
                        + selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getGene()
                        + "/variant/"
                        + selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getOncoKB().getOncokbHgvsp();
                openBrowser(fullUrlOncoKB);
            } else if("COSMIC".equalsIgnoreCase(item)) {
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
            } else if("UCSC".equalsIgnoreCase(item)) {
                Integer start = (variantInformationMap.containsKey("start")) ? (Integer) variantInformationMap.get("start") : null;
                Integer end = (variantInformationMap.containsKey("stop")) ? (Integer) variantInformationMap.get("stop") : null;
                StringBuilder insertStart = new StringBuilder(start.toString());
                StringBuilder insertEnd = new StringBuilder(end.toString());
                int startLength = insertStart.length();
                int endLength = insertEnd.length();
                for (int i = 1; i < startLength; i++) {
                    if (i % 3 == 0) insertStart.insert(startLength - i, ",");
                }
                for (int i = 1; i < endLength; i++) {
                    if (i % 3 == 0) insertEnd.insert(endLength - i, ",");
                }
                Integer startMinus = start - 30;
                Integer endPlus = end + 30;
                String fullUrlUCSC = "http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&highlight=hg19.{"
                        + selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome() + "%3A"
                        + insertStart + "-"
                        + insertEnd + "&position="
                        + selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome() + "%3A"
                        + startMinus + "-"
                        + endPlus;

                openBrowser(fullUrlUCSC);
            }
        } else if(panel.getAnalysisType().equalsIgnoreCase("GERMLINE")) {
            if("BRCA Exchange".equalsIgnoreCase(item)) {
                String urlBRCAExchange = (variantInformationMap.containsKey("brca_exchange_url")) ? (String) variantInformationMap.get("brca_exchange_url") : null;
                openBrowser(urlBRCAExchange);
            } else if("ClinVar".equalsIgnoreCase(item)) {
                String urlClinvar = (variantInformationMap.containsKey("clinvar_url")) ? (String) variantInformationMap.get("clinvar_url") : null;
                openBrowser(urlClinvar);
            } else if("NCBI".equalsIgnoreCase(item)) {
                String urlNCBI = (variantInformationMap.containsKey("ncbi_url")) ? (String) variantInformationMap.get("ncbi_url") : null;
                openBrowser(urlNCBI);
            } else if("UCSC".equalsIgnoreCase(item)) {
                String urlUCSC = (variantInformationMap.containsKey("ucsc_url")) ? (String) variantInformationMap.get("ucsc_url") : null;
                openBrowser(urlUCSC);
            } else if("ALAMUT".equalsIgnoreCase(item)) {
                Map<String, Object> geneMap = returnResultsAfterSearch("gene");
                Map<String, Map<String, String>> transcriptDataMap = (Map<String, Map<String, String>>) geneMap.get("transcript");
                if (!transcriptDataMap.isEmpty() && transcriptDataMap.size() > 0) {
                    int selectedIdx = this.analysisDetailVariantNomenclatureController.getTranscriptComboBoxSelectedIndex();
                    logger.debug(String.format("selected transcript combobox idx : %s", selectedIdx));
                    Map<String, String> map = transcriptDataMap.get(String.valueOf(selectedIdx));
                    if (!map.isEmpty() && map.size() > 0) {
                        String transcript = map.get("transcript_name");
                        String cDNA = map.get("hgvs.c");
                        String sampleId = sample.getId().toString();
                        String bamFileName = sample.getName() + "_final.bam";
                        loadAlamut(transcript, cDNA, sampleId, bamFileName);
                    }
                }
            } else if("ExAC".equalsIgnoreCase(item)) {
                String urlExAC = (variantInformationMap.containsKey("exac_url")) ? (String) variantInformationMap.get("exac_url") : null;
                openBrowser(urlExAC);
            }
        }
    }

    public void openBrowser(String url) {
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
        String variantId = selectedAnalysisResultVariant.getSnpInDel().getId().toString();
        String gene = selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getGene();
        String locus = String.format("%s:%,d-%,d",
                selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome(),
                selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getStartPosition(),
                selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getStartPosition());
        String refGenome = selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getRefGenomeVer();
        String humanGenomeVersion = (refGenome.contains("hg19")) ? "hg19" : "hg18";

        try {
            loadIGV(sampleId, sample.getName(), variantId, gene, locus, humanGenomeVersion);
        } catch (WebAPIException wae) {
            DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                    getMainApp().getPrimaryStage(), true);
        } catch (Exception e) {
            DialogUtil.generalShow(Alert.AlertType.ERROR, "IGV launch fail", "IGV software doesn't launch.",
                    getMainApp().getPrimaryStage(), true);
        }

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

    public Map<String, Object> returnResultsAfterSearch(String key) {
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
