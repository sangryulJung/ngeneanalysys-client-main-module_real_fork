package ngeneanalysys.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class AnalysisResultSummary implements Serializable {
    private static final long serialVersionUID = 8336098024552988388L;

    private Integer sampleId;
    private Integer depthMin;
    private Integer depthMax;
    private BigDecimal depthMean;
    private Integer geneCount;
    private Integer allVariantCount;
    private Integer falseVariantCount;
    private Integer warningVariantCount;
    private Integer level1VariantCount;
    private Integer level2VariantCount;
    private Integer level3VariantCount;
    private Integer level4VariantCount;
    private Integer level5VariantCount;
    private Integer reportVariantCount;

    /**
     * @return reportVariantCount
     */
    public Integer getReportVariantCount() {
        return reportVariantCount;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public Integer getDepthMin() {
        return depthMin;
    }

    public Integer getDepthMax() {
        return depthMax;
    }

    public BigDecimal getDepthMean() {
        return depthMean;
    }

    public Integer getAllVariantCount() {
        return allVariantCount;
    }

    public Integer getWarningVariantCount() {
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

    public Integer getFalseVariantCount() { return falseVariantCount; }

    public Integer getGeneCount() {
        return geneCount;
    }

    @Override
    public String toString() {
        return "AnalysisResultSummary{" +
                "sampleId=" + sampleId +
                ", depthMin=" + depthMin +
                ", depthMax=" + depthMax +
                ", depthMean=" + depthMean +
                ", geneCount=" + geneCount +
                ", allVariantCount=" + allVariantCount +
                ", warningVariantCount=" + warningVariantCount +
                ", falseVariantCount=" + falseVariantCount +
                ", level1VariantCount=" + level1VariantCount +
                ", level2VariantCount=" + level2VariantCount +
                ", level3VariantCount=" + level3VariantCount +
                ", level4VariantCount=" + level4VariantCount +
                ", level5VariantCount=" + level5VariantCount +
                '}';
    }
}