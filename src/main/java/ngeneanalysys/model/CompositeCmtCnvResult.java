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
