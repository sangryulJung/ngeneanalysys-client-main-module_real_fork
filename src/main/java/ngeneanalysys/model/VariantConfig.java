package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-03-08
 */
public class VariantConfig {

    private Integer warningReadDepth;
    private BigDecimal warningMAF;
    private ReportCutOffParams reportCutOffParams;
    private String essentialGenes;
    private String geneRepresentativeTranscript;

    /**
     * @param warningReadDepth
     */
    public void setWarningReadDepth(Integer warningReadDepth) {
        this.warningReadDepth = warningReadDepth;
    }

    /**
     * @param warningMAF
     */
    public void setWarningMAF(BigDecimal warningMAF) {
        this.warningMAF = warningMAF;
    }

    /**
     * @param reportCutOffParams
     */
    public void setReportCutOffParams(ReportCutOffParams reportCutOffParams) {
        this.reportCutOffParams = reportCutOffParams;
    }

    /**
     * @param essentialGenes
     */
    public void setEssentialGenes(String essentialGenes) {
        this.essentialGenes = essentialGenes;
    }

    /**
     * @param geneRepresentativeTranscript
     */
    public void setGeneRepresentativeTranscript(String geneRepresentativeTranscript) {
        this.geneRepresentativeTranscript = geneRepresentativeTranscript;
    }

    /**
     * @return warningReadDepth
     */
    public Integer getWarningReadDepth() {
        return warningReadDepth;
    }

    /**
     * @return warningMAF
     */
    public BigDecimal getWarningMAF() {
        return warningMAF;
    }

    /**
     * @return reportCutOffParams
     */
    public ReportCutOffParams getReportCutOffParams() {
        return reportCutOffParams;
    }

    /**
     * @return essentialGenes
     */
    public String getEssentialGenes() {
        return essentialGenes;
    }

    /**
     * @return geneRepresentativeTranscript
     */
    public String getGeneRepresentativeTranscript() {
        return geneRepresentativeTranscript;
    }
}
