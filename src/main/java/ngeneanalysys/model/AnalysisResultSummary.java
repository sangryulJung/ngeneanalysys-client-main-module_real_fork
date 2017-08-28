package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class AnalysisResultSummary implements Serializable {
    private static final long serialVersionUID = 8336098024552988388L;

    private Integer sampleId;
    private String qualityControl;
    private String depthMin;
    private String depthMax;
    private String depthMean;
    private String totalVariants;
    private String warning;
    private Integer levelACount;
    private Integer levelBCount;
    private Integer levelCCount;
    private Integer levelDCount;
    private Integer levelECount;
    private Integer genes;
    private String meanReadQualityPercentage;
    private String meanReadQualityMessage;
    private String retainedReadsPercentage;
    private String retainedReadsMessage;
    private String roiCoveragePercentage;
    private String roiCoverageMessage;
    private String coverageUniformityPercentage;
    private String coverageUniformityMessage;

    /**
     * @return the sampleId
     */
    public Integer getSampleId() {
        return sampleId;
    }
    /**
     * @param sampleId the sampleId to set
     */
    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }
    /**
     * @return the qualityControl
     */
    public String getQualityControl() {
        return qualityControl;
    }
    /**
     * @param qualityControl the qualityControl to set
     */
    public void setQualityControl(String qualityControl) {
        this.qualityControl = qualityControl;
    }
    /**
     * @return the depthMin
     */
    public String getDepthMin() {
        return depthMin;
    }
    /**
     * @param depthMin the depthMin to set
     */
    public void setDepthMin(String depthMin) {
        this.depthMin = depthMin;
    }
    /**
     * @return the depthMax
     */
    public String getDepthMax() {
        return depthMax;
    }
    /**
     * @param depthMax the depthMax to set
     */
    public void setDepthMax(String depthMax) {
        this.depthMax = depthMax;
    }
    /**
     * @return the depthMean
     */
    public String getDepthMean() {
        return depthMean;
    }
    /**
     * @param depthMean the depthMean to set
     */
    public void setDepthMean(String depthMean) {
        this.depthMean = depthMean;
    }
    /**
     * @return the totalVariants
     */
    public String getTotalVariants() {
        return totalVariants;
    }
    /**
     * @param totalVariants the totalVariants to set
     */
    public void setTotalVariants(String totalVariants) {
        this.totalVariants = totalVariants;
    }
    /**
     * @return the warning
     */
    public String getWarning() {
        return warning;
    }
    /**
     * @param warning the warning to set
     */
    public void setWarning(String warning) {
        this.warning = warning;
    }
    /**
     * @return the levelACount
     */
    public Integer getLevelACount() {
        return levelACount;
    }
    /**
     * @param levelACount the levelACount to set
     */
    public void setLevelACount(Integer levelACount) {
        this.levelACount = levelACount;
    }
    /**
     * @return the levelBCount
     */
    public Integer getLevelBCount() {
        return levelBCount;
    }
    /**
     * @param levelBCount the levelBCount to set
     */
    public void setLevelBCount(Integer levelBCount) {
        this.levelBCount = levelBCount;
    }
    /**
     * @return the levelCCount
     */
    public Integer getLevelCCount() {
        return levelCCount;
    }
    /**
     * @param levelCCount the levelCCount to set
     */
    public void setLevelCCount(Integer levelCCount) {
        this.levelCCount = levelCCount;
    }
    /**
     * @return the levelDCount
     */
    public Integer getLevelDCount() {
        return levelDCount;
    }
    /**
     * @param levelDCount the levelDCount to set
     */
    public void setLevelDCount(Integer levelDCount) {
        this.levelDCount = levelDCount;
    }
    /**
     * @return the levelECount
     */
    public Integer getLevelECount() {
        return levelECount;
    }
    /**
     * @param levelECount the levelECount to set
     */
    public void setLevelECount(Integer levelECount) {
        this.levelECount = levelECount;
    }
    /**
     * @return the meanReadQualityPercentage
     */
    public String getMeanReadQualityPercentage() {
        return meanReadQualityPercentage;
    }
    /**
     * @param meanReadQualityPercentage the meanReadQualityPercentage to set
     */
    public void setMeanReadQualityPercentage(String meanReadQualityPercentage) {
        this.meanReadQualityPercentage = meanReadQualityPercentage;
    }
    /**
     * @return the meanReadQualityMessage
     */
    public String getMeanReadQualityMessage() {
        return meanReadQualityMessage;
    }
    /**
     * @param meanReadQualityMessage the meanReadQualityMessage to set
     */
    public void setMeanReadQualityMessage(String meanReadQualityMessage) {
        this.meanReadQualityMessage = meanReadQualityMessage;
    }
    /**
     * @return the retainedReadsPercentage
     */
    public String getRetainedReadsPercentage() {
        return retainedReadsPercentage;
    }
    /**
     * @param retainedReadsPercentage the retainedReadsPercentage to set
     */
    public void setRetainedReadsPercentage(String retainedReadsPercentage) {
        this.retainedReadsPercentage = retainedReadsPercentage;
    }
    /**
     * @return the retainedReadsMessage
     */
    public String getRetainedReadsMessage() {
        return retainedReadsMessage;
    }
    /**
     * @param retainedReadsMessage the retainedReadsMessage to set
     */
    public void setRetainedReadsMessage(String retainedReadsMessage) {
        this.retainedReadsMessage = retainedReadsMessage;
    }
    /**
     * @return the genes
     */
    public Integer getGenes() {
        return genes;
    }
    /**
     * @param genes the genes to set
     */
    public void setGenes(Integer genes) {
        this.genes = genes;
    }
    /**
     * @return the roiCoveragePercentage
     */
    public String getRoiCoveragePercentage() {
        return roiCoveragePercentage;
    }
    /**
     * @param roiCoveragePercentage the roiCoveragePercentage to set
     */
    public void setRoiCoveragePercentage(String roiCoveragePercentage) {
        this.roiCoveragePercentage = roiCoveragePercentage;
    }
    /**
     * @return the roiCoverageMessage
     */
    public String getRoiCoverageMessage() {
        return roiCoverageMessage;
    }
    /**
     * @param roiCoverageMessage the roiCoverageMessage to set
     */
    public void setRoiCoverageMessage(String roiCoverageMessage) {
        this.roiCoverageMessage = roiCoverageMessage;
    }
    /**
     * @return the coverageUniformityPercentage
     */
    public String getCoverageUniformityPercentage() {
        return coverageUniformityPercentage;
    }
    /**
     * @param coverageUniformityPercentage the coverageUniformityPercentage to set
     */
    public void setCoverageUniformityPercentage(String coverageUniformityPercentage) {
        this.coverageUniformityPercentage = coverageUniformityPercentage;
    }
    /**
     * @return the coverageUniformityMessage
     */
    public String getCoverageUniformityMessage() {
        return coverageUniformityMessage;
    }
    /**
     * @param coverageUniformityMessage the coverageUniformityMessage to set
     */
    public void setCoverageUniformityMessage(String coverageUniformityMessage) {
        this.coverageUniformityMessage = coverageUniformityMessage;
    }

}