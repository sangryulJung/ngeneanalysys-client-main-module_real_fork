package ngeneanalysys.service;

import ngeneanalysys.model.QCPassConfig;
import ngeneanalysys.model.VariantFilter;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author Jang
 * @since 2018-08-17
 */
public class PanelTextFileSaveService {
    private static final Logger logger = LoggerUtil.getLogger();

    private String[] viewList = new String[]{"name", "code", "target", "analysisType", "libraryType", "memberGroupIds",
            "diseaseIds", "defaultDiseaseId", "defaultSampleSource", "reportTemplateId", "canonicalTranscripts",
            "warningReadDepth", "warningMAF", "variantFilter", "qcPassConfig"};

    private String[] saveList = new String[]{"Panel name", "pipeline", "Target", "Analysis Type", "Library Type", "Group Ids",
            "Disease Ids", "defaultDiseaseId", "Default Sample Source", "Report Template Id", "Canonical Transcripts",
            "Warning Read Depth", "Warning MAF"};

    /**
     * 인스턴스 생성 제한
     */
    private PanelTextFileSaveService() {}

    /**
     * 싱글톤 인스턴스 생성 내부 클래스
     */
    private static class PanelTextFileSaveHelper{
        private PanelTextFileSaveHelper() {}
        private static final PanelTextFileSaveService INSTANCE = new PanelTextFileSaveService();
    }

    public static PanelTextFileSaveService getInstance() { return PanelTextFileSaveHelper.INSTANCE; }

    public void saveFile(File file, Map<String, Object> panelMap) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {

            for(int i = 0; i < viewList.length ; i++) {
                if(viewList[i].equals("variantFilter")) {
                    VariantFilter variantFilter = (VariantFilter)panelMap.get(viewList[i]);
                    out.write("Essential Genes" + " : " + variantFilter.getEssentialGenes());
                    out.newLine();
                    out.write("InDel MRD" + " : " + variantFilter.getInDelMinReadDepth());
                    out.newLine();
                    out.write("InDel MAF" + " : " + variantFilter.getInDelMinAlleleFraction());
                    out.newLine();
                    out.write("InDel MAC" + " : " + variantFilter.getInDelMinAlternateCount());
                    out.newLine();
                    out.write("SNV MRD" + " : " + variantFilter.getSnvMinReadDepth());
                    out.newLine();
                    out.write("Low Confidence" + " : " + variantFilter.getLowConfidenceFilter());
                    out.newLine();
                    out.write("Population Frequency DBs" + " : " + variantFilter.getPopulationFrequencyDBs());
                    out.newLine();
                    out.write("Frequency Threshold" + " : " + variantFilter.getPopulationFrequency());
                    out.newLine();
                } else if(viewList[i].equals("qcPassConfig")) {
                    QCPassConfig qcPassConfig = (QCPassConfig)panelMap.get(viewList[i]);
                    out.write("Total Base Pair : " + qcPassConfig.getTotalBasePair());
                    out.newLine();
                    out.write("Q30 Trimmed Base : " + qcPassConfig.getQ30TrimmedBasePercentage());
                    out.newLine();
                    out.write("Mapped Base : " + qcPassConfig.getMappedBasePercentage());
                    out.newLine();
                    out.write("On Target : " + qcPassConfig.getOnTargetPercentage());
                    out.newLine();
                    out.write("On Target Coverage : " + qcPassConfig.getOnTargetCoverage());
                    out.newLine();
                    out.write("Duplicated Reads : " + qcPassConfig.getDuplicatedReadsPercentage());
                    out.newLine();
                    out.write("ROI Coverage : " + qcPassConfig.getRoiCoveragePercentage());
                    out.newLine();
                    out.write("Uniformity : " + qcPassConfig.getUniformity02Percentage());
                    out.newLine();
                    out.write("Mapping Quality : " + qcPassConfig.getMappingQuality60Percentage());
                    out.newLine();
                } else {
                    out.write(saveList[i] + " : " + panelMap.get(viewList[i]));
                    out.newLine();
                }
            }
            out.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
