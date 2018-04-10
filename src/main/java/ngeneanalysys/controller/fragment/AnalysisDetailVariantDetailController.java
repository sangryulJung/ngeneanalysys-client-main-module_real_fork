package ngeneanalysys.controller.fragment;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import ngeneanalysys.code.constants.FXMLConstants;
import ngeneanalysys.code.enums.ExperimentTypeCode;
import ngeneanalysys.controller.extend.SubPaneController;
import ngeneanalysys.exceptions.WebAPIException;
import ngeneanalysys.model.*;
import ngeneanalysys.service.ALAMUTService;
import ngeneanalysys.service.IGVService;
import ngeneanalysys.util.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jang
 * @since 2018-03-21
 */
public class AnalysisDetailVariantDetailController extends SubPaneController {
    private static Logger logger = LoggerUtil.getLogger();

    @FXML
    private GridPane detailWarpper;

    private Panel panel;

    private VariantAndInterpretationEvidence selectedAnalysisResultVariant;

    /** IGV 연동 서비스 */
    private IGVService igvService;

    /** Alamut 연동 서비스 */
    private ALAMUTService alamutService;

    private AnalysisDetailVariantNomenclatureController analysisDetailVariantNomenclatureController;


    @Override
    public void show(Parent root) throws IOException {
        panel = (Panel)paramMap.get("panel");

        selectedAnalysisResultVariant = (VariantAndInterpretationEvidence)paramMap.get("variant");

        igvService = IGVService.getInstance();
        igvService.setMainController(getMainController());

        // alamut service init
        alamutService = ALAMUTService.getInstance();
        alamutService.setMainController(getMainController());

        if(!detailWarpper.getChildren().isEmpty()) detailWarpper.getChildren().removeAll(detailWarpper.getChildren());
        showReadDepth();
        showVariantNomenclature();
        showDetailSub();
        //showLink();

    }

    public void showDetailSub() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_DETAIL_SUB_INFO);
            Node node = loader.load();
            DetailSubInfoController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.show((Parent) node);
            detailWarpper.add(node, 2, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showReadDepth() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_READ_DEPTH);
            Node node = loader.load();
            AnalysisDetailReadDepthVariantFractionController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.show((Parent) node);
            detailWarpper.add(node, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showVariantNomenclature() {
        try {
            FXMLLoader loader = getMainApp().load(FXMLConstants.ANALYSIS_DETAIL_VARIANT_NOMENCLATURE);
            Node node = loader.load();
            AnalysisDetailVariantNomenclatureController controller = loader.getController();
            controller.setMainController(this.getMainController());
            controller.setParamMap(paramMap);
            controller.show((Parent) node);
            analysisDetailVariantNomenclatureController = controller;
            detailWarpper.add(node, 1, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    public void showLink() {
        Sample sample = (Sample) paramMap.get("sample");
        String analysisType = (panel != null) ? panel.getAnalysisType() : "";
        FXMLLoader loader = null;
        ScrollPane somaticLinkBox = null;
        Pane linkBox = null;
        try {
            // SOMATIC 인 경우
            if (analysisType.equals(ExperimentTypeCode.SOMATIC.getDescription())) {
                loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW_LINK_SOMATIC);
                somaticLinkBox = loader.load();
                detailWarpper.add(somaticLinkBox, 2, 0);
            } else {
                loader = FXMLLoadUtil.load(FXMLConstants.ANALYSIS_DETAIL_SNPS_INDELS_OVERVIEW_LINK_BRCA);
                linkBox = loader.load();
                detailWarpper.add(linkBox, 2, 0);
            }

            if (somaticLinkBox != null && analysisType.equals(ExperimentTypeCode.SOMATIC.getDescription())) {
                Map<String, Object> variantInformationMap = returnResultsAfterSearch("variant_information");
                String rsId = (variantInformationMap.containsKey("rs_id")) ? (String) variantInformationMap.get("rs_id") : null;
                String exacFormat = (variantInformationMap.containsKey("exac_url")) ? (String) variantInformationMap.get("exac_format") : null;
                String geneId = (variantInformationMap.containsKey("geneid")) ? (String) variantInformationMap.get("geneid") : null;
                Integer start = (variantInformationMap.containsKey("start")) ? (Integer) variantInformationMap.get("start") : null;
                Integer end = (variantInformationMap.containsKey("stop")) ? (Integer) variantInformationMap.get("stop") : null;
                GridPane gridPane = (GridPane) somaticLinkBox.getContent();
                for (Node node : gridPane.getChildren()) {
                    if (node != null) {
                        String id = node.getId();
                        if ("igvButton".equals(id)) {
                            Button igvButton = (Button) node;

                            String sampleId = sample.getId().toString();
                            String variantId = selectedAnalysisResultVariant.getSnpInDel().getId().toString();
                            String gene = selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getGene();
                            String locus = String.format("%s:%,d-%,d",
                                    selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome(),
                                    selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getStartPosition(),
                                    selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getStartPosition());
                            String refGenome = selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getRefGenomeVer();
                            String humanGenomeVersion = (refGenome.contains("hg19")) ? "hg19" : "hg18";

                            igvButton.setOnAction(event -> {
                                try {
                                    loadIGV(sampleId, sample.getName(), variantId, gene, locus, humanGenomeVersion);
                                } catch (WebAPIException wae) {
                                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                                            getMainApp().getPrimaryStage(), true);
                                } catch (Exception e) {
                                    DialogUtil.generalShow(Alert.AlertType.ERROR, "IGV launch fail", "IGV software doesn't launch.",
                                            getMainApp().getPrimaryStage(), true);
                                }
                            });

                            igvButton.setDisable(false);
                        }

                        if ("dbSNPButton".equals(id)) {
                            Button dbSNPButton = (Button) node;

                            if (!StringUtils.isEmpty(rsId)) {
                                String fullUrlDBsnp = "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + rsId.replaceAll("rs", "");
                                dbSNPButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlDBsnp));
                                dbSNPButton.setDisable(false);
                            }
                        }

                        if ("clinvarButton".equals(id)) {
                            Button clinvarButton = (Button) node;
                            if (!StringUtils.isEmpty(rsId)) {
                                String fullUrlClinvar = "http://www.ncbi.nlm.nih.gov/clinvar?term=" + rsId;
                                clinvarButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlClinvar));
                                clinvarButton.setDisable(false);
                            }
                        }

                        if ("cosmicButton".equals(id)) {
                            Button cosmicButton = (Button) node;
                            if (!StringUtils.isEmpty(selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getCosmic().getCosmicIds())) {
                                String cosmicId = selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getCosmic().getCosmicIds().replaceAll("COSM", "");
                                if (cosmicId.contains("|")) {
                                    String[] ids = cosmicId.split("\\|");

                                    cosmicButton.setOnAction(event -> {
                                        boolean first = true;
                                        for (String cosmic : ids) {
                                            String fullUrlCOSMIC = "http://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=37&id=" + cosmic;
                                            getMainApp().getHostServices().showDocument(fullUrlCOSMIC);
                                            try {
                                                if (first) {
                                                    Thread.sleep(1200);
                                                    first = false;
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                } else {
                                    String fullUrlCOSMIC = "http://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=37&id=" + cosmicId;
                                    cosmicButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlCOSMIC));
                                }
                                cosmicButton.setDisable(false);
                            }
                        }

                        if ("ncbiButton".equals(id)) {
                            Button ncbiButton = (Button) node;
                            if (!StringUtils.isEmpty(geneId)) {
                                String fullUrlNCBI = "http://www.ncbi.nlm.nih.gov/gene/" + geneId;
                                ncbiButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlNCBI));
                                ncbiButton.setDisable(false);
                            }
                        }

                        if ("gnomesButton".equals(id)) {
                            Button gnomesButton = (Button) node;
                            if (!StringUtils.isEmpty(rsId)) {
                                String fullUrl1000G = "http://grch37.ensembl.org/Homo_sapiens/Variation/Population?db=core;v="
                                        + rsId + ";vdb=variation";
                                gnomesButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrl1000G));
                                gnomesButton.setDisable(false);
                            }
                        }

                        if ("exACButton".equals(id)) {
                            Button exACButton = (Button) node;
                            if (!StringUtils.isEmpty(exacFormat)) {
                                String fullUrlExAC = "http://exac.broadinstitute.org/variant/"
                                        + exacFormat;
                                exACButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlExAC));
                                exACButton.setDisable(false);
                            }
                        }

                        if ("gnomADButton".equals(id)) {
                            Button gnomADButton = (Button) node;
                            if (!StringUtils.isEmpty(exacFormat)) {
                                String fullUrlExAC = "http://gnomad.broadinstitute.org/variant/"
                                        + exacFormat;
                                gnomADButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlExAC));
                                gnomADButton.setDisable(false);
                            }
                        }

                        if ("koEXIDButton".equals(id)) {
                            Button koEXIDButton = (Button) node;
                            if (!StringUtils.isEmpty(rsId)) {
                                String fullUrlKoKEXID = "http://koex.snu.ac.kr/koex_main.php?section=search&db_code=15&keyword_class=varid&search_keyword="
                                        + rsId;
                                koEXIDButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlKoKEXID));
                                koEXIDButton.setDisable(false);
                            }
                        }

                        if ("oncoKBButton".equals(id)) {
                            Button oncoKBButton = (Button) node;
                            if (selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getOncoKB() != null &&
                                    !StringUtils.isEmpty(selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getOncoKB().getOncokbHgvsp())) {
                                String fullUrlOncoKB = "http://oncokb.org/#/gene/"
                                        + selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getGene()
                                        + "/variant/"
                                        + selectedAnalysisResultVariant.getSnpInDel().getClinicalDB().getOncoKB().getOncokbHgvsp();
                                oncoKBButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlOncoKB));
                                oncoKBButton.setDisable(false);
                            }
                        }

                        if ("ucscButton".equals(id)) {
                            Button ucscButton = (Button) node;
                            if (start != null && end != null) {
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

                                ucscButton.setOnAction(event -> getMainApp().getHostServices().showDocument(fullUrlUCSC));
                                ucscButton.setDisable(false);
                            }
                        }
                    }
                }
            } else {
                Map<String, Object> variantInformationMap = returnResultsAfterSearch("variant_information");
                SnpInDel snpInDel = (SnpInDel) paramMap.get("snpInDel");
                logger.info("init overview link button event binding..");
                String urlExAC = (variantInformationMap.containsKey("exac_url")) ? (String) variantInformationMap.get("exac_url") : null;
                String urlBRCAExchange = (variantInformationMap.containsKey("brca_exchange_url")) ? (String) variantInformationMap.get("brca_exchange_url") : null;
                String urlClinvar = (variantInformationMap.containsKey("clinvar_url")) ? (String) variantInformationMap.get("clinvar_url") : null;
                String urlNCBI = (variantInformationMap.containsKey("ncbi_url")) ? (String) variantInformationMap.get("ncbi_url") : null;
                String urlUCSC = (variantInformationMap.containsKey("ucsc_url")) ? (String) variantInformationMap.get("ucsc_url") : null;
                for (Node node : linkBox.getChildren()) {
                    if (node != null) {
                        String id = node.getId();
                        logger.info(String.format("button id : %s", id));
                        // exACButton
                        if ("exACButton".equals(id)) {
                            Button exACButton = (Button) node;
                            if (!StringUtils.isEmpty(urlExAC)) {
                                exACButton.setOnAction(event -> {
                                    logger.info(String.format("OPEN EXTERNAL LINK [%s][%s]", id, urlExAC));
                                    getMainApp().getHostServices().showDocument(urlExAC);
                                });
                                exACButton.setDisable(false);
                            }
                        }
                        // igvButton
                        if ("igvButton".equals(id)) {
                            Button igvButton = (Button) node;

                            String sampleId = sample.getId().toString();
                            String variantId = selectedAnalysisResultVariant.getSnpInDel().getId().toString();
                            String gene = selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getGene();
                            String locus = String.format("%s:%,d-%,d",
                                    selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getChromosome(),
                                    selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getStartPosition(),
                                    selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getStartPosition());
                            String refGenome = selectedAnalysisResultVariant.getSnpInDel().getGenomicCoordinate().getRefGenomeVer();
                            String humanGenomeVersion = (refGenome.contains("hg19")) ? "hg19" : "hg18";

                            igvButton.setOnAction(event -> {
                                try {
                                    loadIGV(sampleId, sample.getName(), variantId, gene, locus, humanGenomeVersion);
                                } catch (WebAPIException wae) {
                                    DialogUtil.generalShow(wae.getAlertType(), wae.getHeaderText(), wae.getContents(),
                                            getMainApp().getPrimaryStage(), true);
                                } catch (Exception e) {
                                    DialogUtil.generalShow(Alert.AlertType.ERROR, "IGV launch fail", "IGV software doesn't launch.",
                                            getMainApp().getPrimaryStage(), true);
                                }
                            });

                            igvButton.setDisable(false);
                        }
                        // brcaExchangeButton
                        if ("brcaExchangeButton".equals(id)) {
                            Button brcaExchangeButton = (Button) node;
                            if (!StringUtils.isEmpty(urlBRCAExchange)) {
                                brcaExchangeButton.setOnAction(event -> {
                                    logger.info(String.format("OPEN EXTERNAL LINK [%s][%s]", id, urlBRCAExchange));
                                    getMainApp().getHostServices().showDocument(urlBRCAExchange);
                                });
                                brcaExchangeButton.setDisable(false);
                            }
                        }

                        // clinvarButton
                        if ("clinvarButton".equals(id)) {
                            Button clinvarButton = (Button) node;
                            if (!StringUtils.isEmpty(urlClinvar)) {
                                clinvarButton.setOnAction(event -> {
                                    logger.info(String.format("OPEN EXTERNAL LINK [%s][%s]", id, urlClinvar));
                                    getMainApp().getHostServices().showDocument(urlClinvar);
                                });
                                clinvarButton.setDisable(false);
                            }
                        }

                        // ncbiButton
                        if ("ncbiButton".equals(id)) {
                            Button ncbiButton = (Button) node;
                            if (!StringUtils.isEmpty(urlNCBI)) {
                                ncbiButton.setOnAction(event -> {
                                    logger.info(String.format("OPEN EXTERNAL LINK [%s][%s]", id, urlNCBI));
                                    getMainApp().getHostServices().showDocument(urlNCBI);
                                });
                                ncbiButton.setDisable(false);
                            }
                        }

                        // ucscButton
                        if ("ucscButton".equals(id)) {
                            Button ucscButton = (Button) node;
                            if (!StringUtils.isEmpty(urlUCSC)) {
                                ucscButton.setOnAction(event -> {
                                    logger.info(String.format("OPEN EXTERNAL LINK [%s][%s]", id, urlUCSC));
                                    getMainApp().getHostServices().showDocument(urlUCSC);
                                });
                                ucscButton.setDisable(false);
                            }
                        }

                        // alamutButton
                        if ("alamutButton".equals(id)) {
                            Button alamutButton = (Button) node;

                            // variant identification transcript data map
                            Map<String, Object> geneMap = returnResultsAfterSearch("gene");
                            if (geneMap != null && !geneMap.isEmpty() && geneMap.containsKey("transcript")) {
                                Map<String, Map<String, String>> transcriptDataMap = (Map<String, Map<String, String>>) geneMap.get("transcript");
                                if (!transcriptDataMap.isEmpty() && transcriptDataMap.size() > 0) {
                                    alamutButton.setOnAction(event -> {
                                        int selectedIdx = this.analysisDetailVariantNomenclatureController.getTranscriptComboBoxSelectedIndex();
                                        logger.info(String.format("selected transcript combobox idx : %s", selectedIdx));
                                        Map<String, String> map = transcriptDataMap.get(String.valueOf(selectedIdx));
                                        if (!map.isEmpty() && map.size() > 0) {
                                            String transcript = map.get("transcript_name");
                                            String cDNA = map.get("hgvs.c");
                                            String sampleId = sample.getId().toString();
                                            String bamFileName = sample.getName() + "_final.bam";
                                            loadAlamut(transcript, cDNA, sampleId, bamFileName);
                                        }
                                    });
                                    alamutButton.setDisable(false);
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

}
