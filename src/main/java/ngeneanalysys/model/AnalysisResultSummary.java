package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;

public class AnalysisResultSummary implements Serializable {
    private static final long serialVersionUID = 8336098024552988388L;

    private Integer sampleId;
    private String qualityControl;
    private String depthMin;
    private String depthMax;
    private String depthMean;
    private String totalVariantCount;
    private String warningVariantCount;
    private Integer level1VariantCount;
    private Integer level2VariantCount;
    private Integer level3VariantCount;
    private Integer level4VariantCount;
    private Integer level5VariantCount;
    private Integer geneCount;
    private BigDecimal meanReadQualityPercentage;
    private String meanReadQualityStatus;
    private BigDecimal retainedReadsPercentage;
    private String retainedReadsStatus;
    private BigDecimal roiCoveragePercentage;
    private String roiCoverageStatus;
    private BigDecimal coverageUniformityPercentage;
    private String coverageUniformityStatus;

    public Integer getSampleId() {
        return sampleId;
    }

    public String getQualityControl() {
        return qualityControl;
    }

    public String getDepthMin() {
        return depthMin;
    }

    public String getDepthMax() {
        return depthMax;
    }

    public String getDepthMean() {
        return depthMean;
    }

    public String getTotalVariantCount() {
        return totalVariantCount;
    }

    public String getWarningVariantCount() {
        return warningVariantCount;
    }

    public Integer getLevel1VariantCount() {
        return level1VariantCount;
    }

    public Integer getLevel2VariantCount() {
        return level2VariantCount;
    }

    public Integer getLevel3VariantCount() {
        return level3VariantCount;
    }

    public Integer getLevel4VariantCount() {
        return level4VariantCount;
    }

    public Integer getLevel5VariantCount() {
        return level5VariantCount;
    }

    public Integer getGeneCount() {
        return geneCount;
    }

    public BigDecimal getMeanReadQualityPercentage() {
        return meanReadQualityPercentage;
    }

    public String getMeanReadQualityStatus() {
        return meanReadQualityStatus;
    }

    public BigDecimal getRetainedReadsPercentage() {
        return retainedReadsPercentage;
    }

    public String getRetainedReadsStatus() {
        return retainedReadsStatus;
    }

    public BigDecimal getRoiCoveragePercentage() {
        return roiCoveragePercentage;
    }

    public String getRoiCoverageStatus() {
        return roiCoverageStatus;
    }

    public BigDecimal getCoverageUniformityPercentage() {
        return coverageUniformityPercentage;
    }

    public String getCoverageUniformityStatus() {
        return coverageUniformityStatus;
    }

    @Override
    public String toString() {
        return "AnalysisResultSummary{" +
                "sampleId=" + sampleId +
                ", qualityControl='" + qualityControl + '\'' +
                ", depthMin='" + depthMin + '\'' +
                ", depthMax='" + depthMax + '\'' +
                ", depthMean='" + depthMean + '\'' +
                ", totalVariantCount='" + totalVariantCount + '\'' +
                ", warningVariantCount='" + warningVariantCount + '\'' +
                ", level1VariantCount=" + level1VariantCount +
                ", level2VariantCount=" + level2VariantCount +
                ", level3VariantCount=" + level3VariantCount +
                ", level4VariantCount=" + level4VariantCount +
                ", level5VariantCount=" + level5VariantCount +
                ", geneCount=" + geneCount +
                ", meanReadQualityPercentage=" + meanReadQualityPercentage +
                ", meanReadQualityStatus='" + meanReadQualityStatus + '\'' +
                ", retainedReadsPercentage=" + retainedReadsPercentage +
                ", retainedReadsStatus='" + retainedReadsStatus + '\'' +
                ", roiCoveragePercentage=" + roiCoveragePercentage +
                ", roiCoverageStatus='" + roiCoverageStatus + '\'' +
                ", coverageUniformityPercentage=" + coverageUniformityPercentage +
                ", coverageUniformityStatus='" + coverageUniformityStatus + '\'' +
                '}';
    }
}