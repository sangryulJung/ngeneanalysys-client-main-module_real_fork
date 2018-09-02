package ngeneanalysys.model;

import java.math.BigDecimal;

public class VariantFilter {
    private String essentialGenes;
    private String lowConfidenceFilter;
    private BigDecimal inDelMinAlleleFraction;
    private Integer inDelMinReadDepth;
    private Integer inDelMinAlternateCount;
    private BigDecimal snvMinAlleleFraction;
    private Integer snvMinAlternateCount;
    private Integer snvMinReadDepth;
    private BigDecimal lowConfidenceMinAlleleFraction;
    private String populationFrequencyDBs;
    private BigDecimal populationFrequency;
    private Boolean clinVarDrugResponseCheck = false;
    /**
     * @param lowConfidenceMinAlleleFraction
     */
    public void setLowConfidenceMinAlleleFraction(BigDecimal lowConfidenceMinAlleleFraction) {
        this.lowConfidenceMinAlleleFraction = lowConfidenceMinAlleleFraction;
    }

    /**
     * @return lowConfidenceMinAlleleFraction
     */
    public BigDecimal getLowConfidenceMinAlleleFraction() {
        return lowConfidenceMinAlleleFraction;
    }

    /**
     * @return essentialGenes
     */
    public String getEssentialGenes() {
        return essentialGenes;
    }

    /**
     * @param essentialGenes
     */
    public void setEssentialGenes(String essentialGenes) {
        this.essentialGenes = essentialGenes;
    }

    /**
     * @return lowConfidenceFilter
     */
    public String getLowConfidenceFilter() {
        return lowConfidenceFilter;
    }

    /**
     * @param lowConfidenceFilter
     */
    public void setLowConfidenceFilter(String lowConfidenceFilter) {
        this.lowConfidenceFilter = lowConfidenceFilter;
    }

    /**
     * @return inDelMinAlleleFraction
     */
    public BigDecimal getInDelMinAlleleFraction() {
        return inDelMinAlleleFraction;
    }

    /**
     * @param inDelMinAlleleFraction
     */
    public void setInDelMinAlleleFraction(BigDecimal inDelMinAlleleFraction) {
        this.inDelMinAlleleFraction = inDelMinAlleleFraction;
    }

    /**
     * @return inDelMinReadDepth
     */
    public Integer getInDelMinReadDepth() {
        return inDelMinReadDepth;
    }

    /**
     * @param inDelMinReadDepth
     */
    public void setInDelMinReadDepth(Integer inDelMinReadDepth) {
        this.inDelMinReadDepth = inDelMinReadDepth;
    }

    /**
     * @return inDelMinAlternateCount
     */
    public Integer getInDelMinAlternateCount() {
        return inDelMinAlternateCount;
    }

    /**
     * @param inDelMinAlternateCount
     */
    public void setInDelMinAlternateCount(Integer inDelMinAlternateCount) {
        this.inDelMinAlternateCount = inDelMinAlternateCount;
    }
    /**
     * @return snvMinAlleleFraction
     */
    public BigDecimal getSnvMinAlleleFraction() {
        return snvMinAlleleFraction;
    }
    /**
     * @param snvMinAlleleFraction
     */
    public void setSnvMinAlleleFraction(BigDecimal snvMinAlleleFraction) {
        this.snvMinAlleleFraction = snvMinAlleleFraction;
    }
    /**
     * @return snvMinAlternateCount
     */
    public Integer getSnvMinAlternateCount() {
        return snvMinAlternateCount;
    }
    /**
     * @param snvMinAlternateCount
     */
    public void setSnvMinAlternateCount(Integer snvMinAlternateCount) {
        this.snvMinAlternateCount = snvMinAlternateCount;
    }

    /**
     * @return snvMinReadDepth
     */
    public Integer getSnvMinReadDepth() {
        return snvMinReadDepth;
    }

    /**
     * @param snvMinReadDepth
     */
    public void setSnvMinReadDepth(Integer snvMinReadDepth) {
        this.snvMinReadDepth = snvMinReadDepth;
    }

    /**
     * @return populationFrequencyDBs
     */
    public String getPopulationFrequencyDBs() {
        return populationFrequencyDBs;
    }

    /**
     * @param populationFrequencyDBs
     */
    public void setPopulationFrequencyDBs(String populationFrequencyDBs) {
        this.populationFrequencyDBs = populationFrequencyDBs;
    }

    /**
     * @return populationFrequency
     */
    public BigDecimal getPopulationFrequency() {
        return populationFrequency;
    }

    /**
     * @param populationFrequency
     */
    public void setPopulationFrequency(BigDecimal populationFrequency) {
        this.populationFrequency = populationFrequency;
    }
    /**
     * @return clinVarDrugResponseCheck
     */
    public Boolean getClinVarDrugResponseCheck() {
        return clinVarDrugResponseCheck;
    }
    /**
     * @param clinVarDrugResponseCheck
     */
    public void setClinVarDrugResponseCheck(Boolean clinVarDrugResponseCheck) {
        this.clinVarDrugResponseCheck = clinVarDrugResponseCheck;
    }

    @Override
    public String toString() {
        return "VariantFilter{" +
                "essentialGenes='" + essentialGenes + '\'' +
                ", lowConfidenceFilter='" + lowConfidenceFilter + '\'' +
                ", inDelMinAlleleFraction=" + inDelMinAlleleFraction +
                ", inDelMinReadDepth=" + inDelMinReadDepth +
                ", inDelMinAlternateCount=" + inDelMinAlternateCount +
                ", snvMinAlleleFraction=" + snvMinAlleleFraction +
                ", snvMinAlternateCount=" + snvMinAlternateCount +
                ", snvMinReadDepth=" + snvMinReadDepth +
                ", populationFrequencyDBs='" + populationFrequencyDBs + '\'' +
                ", populationFrequency=" + populationFrequency +
                ", clinVarDrugResponseCheck=" + clinVarDrugResponseCheck +
                '}';
    }
}
