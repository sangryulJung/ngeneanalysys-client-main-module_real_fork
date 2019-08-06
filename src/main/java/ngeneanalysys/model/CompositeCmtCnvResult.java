package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-11-20
 */
public class CompositeCmtCnvResult {
    private Integer sampleId;
    private String gene;
    private String prediction;
    private String includedInReport;
    private String classification;
    private String inheritance;
    private String parentalOrigin;

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @return gene
     */
    public String getGene() {
        return gene;
    }

    /**
     * @return prediction
     */
    public String getPrediction() {
        return prediction;
    }

    /**
     * @return includedInReport
     */
    public String getIncludedInReport() {
        return includedInReport;
    }

    /**
     * @return classification
     */
    public String getClassification() {
        return classification;
    }

    /**
     * @return inheritance
     */
    public String getInheritance() {
        return inheritance;
    }

    /**
     * @return parentalOrigin
     */
    public String getParentalOrigin() {
        return parentalOrigin;
    }

    /**
     * @param sampleId
     */
    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    /**
     * @param gene
     */
    public void setGene(String gene) {
        this.gene = gene;
    }

    /**
     * @param prediction
     */
    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    /**
     * @param includedInReport
     */
    public void setIncludedInReport(String includedInReport) {
        this.includedInReport = includedInReport;
    }

    /**
     * @param classification
     */
    public void setClassification(String classification) {
        this.classification = classification;
    }

    /**
     * @param inheritance
     */
    public void setInheritance(String inheritance) {
        this.inheritance = inheritance;
    }

    /**
     * @param parentalOrigin
     */
    public void setParentalOrigin(String parentalOrigin) {
        this.parentalOrigin = parentalOrigin;
    }
}
