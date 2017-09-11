package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-09-08
 */
public class Variant {
    private Integer id;
    private Integer sampleId;
    private String swPathogenicityLevel;
    private String expertPathogenicityLevel;
    private String swTier;
    private String expertTier;
    private Integer skipReport;
    private Integer hasWarning;
    private String warningReason;
    private Integer variantNum;
    private String comment;

    private VariantExpression variantExpression;

    private ClinicalSignificant clinicalSignificant;

    private SequenceInfo sequenceInfo;

    private ReadInfo readInfo;

    private PopulationFrequency populationFrequency;

    /**
     * @return sequenceInfo
     */
    public SequenceInfo getSequenceInfo() {
        return sequenceInfo;
    }

    /**
     * @param sequenceInfo
     */
    public void setSequenceInfo(SequenceInfo sequenceInfo) {
        this.sequenceInfo = sequenceInfo;
    }

    /**
     * @return readInfo
     */
    public ReadInfo getReadInfo() {
        return readInfo;
    }

    /**
     * @param readInfo
     */
    public void setReadInfo(ReadInfo readInfo) {
        this.readInfo = readInfo;
    }

    /**
     * @return populationFrequency
     */
    public PopulationFrequency getPopulationFrequency() {
        return populationFrequency;
    }

    /**
     * @param populationFrequency
     */
    public void setPopulationFrequency(PopulationFrequency populationFrequency) {
        this.populationFrequency = populationFrequency;
    }

    /**
     * @return clinicalSignificant
     */
    public ClinicalSignificant getClinicalSignificant() {
        return clinicalSignificant;
    }

    /**
     * @param clinicalSignificant
     */
    public void setClinicalSignificant(ClinicalSignificant clinicalSignificant) {
        this.clinicalSignificant = clinicalSignificant;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * @param sampleId
     */
    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    /**
     * @return swPathogenicityLevel
     */
    public String getSwPathogenicityLevel() {
        return swPathogenicityLevel;
    }

    /**
     * @param swPathogenicityLevel
     */
    public void setSwPathogenicityLevel(String swPathogenicityLevel) {
        this.swPathogenicityLevel = swPathogenicityLevel;
    }

    /**
     * @return expertPathogenicityLevel
     */
    public String getExpertPathogenicityLevel() {
        return expertPathogenicityLevel;
    }

    /**
     * @param expertPathogenicityLevel
     */
    public void setExpertPathogenicityLevel(String expertPathogenicityLevel) {
        this.expertPathogenicityLevel = expertPathogenicityLevel;
    }

    /**
     * @return swTier
     */
    public String getSwTier() {
        return swTier;
    }

    /**
     * @param swTier
     */
    public void setSwTier(String swTier) {
        this.swTier = swTier;
    }

    /**
     * @return expertTier
     */
    public String getExpertTier() {
        return expertTier;
    }

    /**
     * @param expertTier
     */
    public void setExpertTier(String expertTier) {
        this.expertTier = expertTier;
    }

    /**
     * @return skipReport
     */
    public Integer getSkipReport() {
        return skipReport;
    }

    /**
     * @param skipReport
     */
    public void setSkipReport(Integer skipReport) {
        this.skipReport = skipReport;
    }

    /**
     * @return hasWarning
     */
    public Integer getHasWarning() {
        return hasWarning;
    }

    /**
     * @param hasWarning
     */
    public void setHasWarning(Integer hasWarning) {
        this.hasWarning = hasWarning;
    }

    /**
     * @return warningReason
     */
    public String getWarningReason() {
        return warningReason;
    }

    /**
     * @param warningReason
     */
    public void setWarningReason(String warningReason) {
        this.warningReason = warningReason;
    }

    /**
     * @return variantNum
     */
    public Integer getVariantNum() {
        return variantNum;
    }

    /**
     * @param variantNum
     */
    public void setVariantNum(Integer variantNum) {
        this.variantNum = variantNum;
    }

    /**
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return variantExpression
     */
    public VariantExpression getVariantExpression() {
        return variantExpression;
    }

    /**
     * @param variantExpression
     */
    public void setVariantExpression(VariantExpression variantExpression) {
        this.variantExpression = variantExpression;
    }
}
